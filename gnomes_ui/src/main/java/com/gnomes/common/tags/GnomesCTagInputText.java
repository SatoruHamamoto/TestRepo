package com.gnomes.common.tags;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * 文字入力カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/2/27  YJP/S.Michiura            初版
 * R0.01.02 2018/11/09 YJP/A.Oomori              readonly時、tabIndex=-1を追加出力
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagInputText extends GnomesCTagBase {
    /** 辞書：ラベルリソースID **/
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：テキスト入力用のBean (必須) **/
    private static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：INPUTタイプ **/
    private static final String INFO_INPUT_TYPE = "input_type";

    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

    /** 辞書：テキスト入力参照用名称 **/
    private static final String INFO_INPUT_TEXT_NAME = "input_text_name";

    /** 辞書：最大入力文字数 **/
    private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";

    /** 辞書：最大入力文字数(ドメイン定義) **/
    private static final String INFO_INPUT_MAX_LENGTH_DOMAIN = "input_max_length_domain";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：INPUTの追加クラス */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";

    /** 辞書：入力値変更後の処理 スクリプト */
    private static final String INFO_ON_CHANGE_EVENT = "on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_ON_CHANGE_COMMAND_ID = "on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_ON_CHANGE_BEAN_COMMAND_ID = "on_change_bean_command_id";

    /** INPUTタイプ：ファイル */
    protected static final String INPUT_FILE = "file";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;

    /**
     * 辞書IDを取得
     * @return dictId
     */
    public String getDictId() {
        return dictId;
    }

    /**
     * 辞書IDを設定
     * @param dictId 辞書ID
     */
    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    /**
     * Bean取得
     * @return bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Bean設定
     * @param bean Bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * 文字入力タグ出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {
        	//Beanの指定が間違ってnullになっているのをチェック
        	if ( this.bean == null){
        		logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
        		throw new GnomesAppException(NO_BEAN_ERR_MES);
        	}

            Locale userLocale = ((IScreenFormBean)this.bean).getUserLocale();
            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // ラベル辞書取得
            Map<String,Object> mapInfo = dict.getTextInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();
            // 右側ラベル出力

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))){
            	isPanecon = true;
            }


            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);
            String dataClassName = "common-header-col-data-titleless";
            if ( labelRourceId != null) {
                // 出力ラベル名
                String strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
                if(StringUtil.isNullOrEmpty(strLabel)){
                	strLabel = "";
                }

                // 追加クラスの設定
                String strLabelClassName = (String)mapInfo.get(INFO_LABEL_CLASS_NAME);
                if(StringUtil.isNullOrEmpty(strLabelClassName)){
                	strLabelClassName = "";
                } else {    // 追加がある場合はスペースを加える
                	strLabelClassName = " " + strLabelClassName;
                }

                // 左ラベルの出力
                out.print("  <div class=\"common-header-col-title" + strLabelClassName + "\">" + strLabel + "</div>");

                // データ部のCSSクラス
                dataClassName = "common-header-col-data";
            }


            // Beanからの取得
            //INFO_PARAM_NAME:テキスト入力用のBean
            String labelInputText = (String)mapInfo.get(INFO_PARAM_NAME);
            String strInputText = (String) responseContext.getResponseFormBean(
                    bean, labelInputText,
                    (String) mapInfo.get(INFO_INPUT_TEXT_NAME));

            if ( strInputText == null) {
                    strInputText ="";
            }

            // 左側ラベル出力
            // inputTextName:input name="inputTextName"
            String strInputTextName = (String)mapInfo.get(INFO_INPUT_TEXT_NAME);
            if(StringUtil.isNullOrEmpty(strInputTextName)){
                strInputTextName = "";
            } else {
                strInputTextName = " name=\"" + strInputTextName + "\" ";
            }

            // 最大入力文字数の設定
            String strMaxLength = "";
            // ドメインID
            String inputDomainId = (String)mapInfo.get(INFO_INPUT_MAX_LENGTH_DOMAIN);

            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_MAX_LENGTH))){
            	strMaxLength = " maxlength=\"" + (String)mapInfo.get(INFO_INPUT_MAX_LENGTH) + "\"";
            }
            else if(!StringUtil.isNullOrEmpty(inputDomainId)){
            	try{
            		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);
	            	String inputMaxLength = inputDomain.getCustomTagData().getInputMaxLength();
	            	if (!StringUtil.isNullOrEmpty(inputMaxLength)){
	            		strMaxLength = " maxlength=\"" + inputMaxLength + "\"";
	            	}
            	}
            	catch (Exception e) {
            		// ME01.0215:「指定されたドメインIDは定義されていません。（タグ名：{0}、{1}）」
            		String errorMessage = MessagesHandler.getString(GnomesMessagesConstants.ME01_0215, userLocale, this.dictId, inputDomainId);
            		logger.severe(errorMessage);
                    GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                    throw ex;
            	}

            }

            String addClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(addClass)){
                addClass = "";
            } else {
                addClass = " " + addClass;
            }

            String strClass= "";
            if (isPanecon  ){  // 工程端末
                strClass = " common-input";
            }

            // typeName:タイプ指定 ( input/readonly/disabled/file)
            String strInputType = (String)mapInfo.get(INFO_INPUT_TYPE);
            if(StringUtil.isNullOrEmpty(strInputType)){
                strInputType = "";
            }

            String type = "text";
            if (strInputType.equals(INPUT_FILE)) {
            	type = INPUT_FILE;
            }

            // 非活性の設定
            //inputActivity:入力活性非活性Bean
            boolean inputActivity = true;
            String labelInputActivity = (String)mapInfo.get(INFO_INPUT_ACTIVITY);
            if ( labelInputActivity != null) {
            	Object inputActivityData = getData(clsBean, bean, labelInputActivity);
            	if (inputActivityData instanceof Boolean) {
            		inputActivity = (Boolean)inputActivityData;
            	}
            }

            if ( !strInputType.equals(INPUT_DISABLED) && !strInputType.equals(INPUT_READONLY) ){
                if ( !inputActivity ){
              	  strInputType = INPUT_READONLY;
                } else {
              	  strInputType = "";
                }
            }

            String tabIndex = "";
            // readonlyの場合、タブ移動によるフォーカスを当てさせない
            if (strInputType.equals(INPUT_READONLY)) {
            	tabIndex = " tabindex=\"-1\"";
            }

            String strInputClass = "";
            if ( isPanecon ){  // 工程端末 キーボードダイアログ
                strInputClass = "common-keyboard-input-char";
            } else {
            	strInputClass = "common-table-input-size-max";
            }

            // INPUT追加クラスの設定
            String strInputAddStyle = (String)mapInfo.get(INFO_INPUT_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(strInputAddStyle)){
            	strInputAddStyle = "";
            } else {
            	strInputAddStyle = " " + strInputAddStyle;
            }

            // 入力値変更後の処理
            String onchange = "";
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_EVENT))){
            	onchange = " onchange=\"" + SET_WARMING_FLAG + (String)mapInfo.get(INFO_ON_CHANGE_EVENT) + "\"";
            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_COMMAND_ID))){
            	onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf((String)mapInfo.get(INFO_ON_CHANGE_COMMAND_ID))) + "\"";

            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID))){
            	Object commandId = getData(clsBean, bean, (String)mapInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID));
            	if (commandId != null) {
            		onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(commandId)) + "\"";
            	} else {
            		onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            	}
            } else {
            	 onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            }

            out.print( "<div class=\"" + dataClassName +  strClass + addClass + "\">");

            out.print("<input type=\"" + type + "\"" + strInputTextName + strInputType + tabIndex
                    + strMaxLength + " value=\""
                    + this.getStringEscapeHtmlValue(strInputText) +
                    "\" class=\"" + strInputClass + strInputAddStyle
                    + "\""+ onchange + "></div>\n");

        } catch(Exception e) {
            if (!Objects.isNull(out)) {
                try {
                    out.print(this.getCTagErrorToMenu());
                    out.flush();
                } catch (Exception e1) {
                    throw new JspTagException(e1);
                }
            }

            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    /**
     * 終了処理
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * 解放処理
     */
    public void release() {}



}