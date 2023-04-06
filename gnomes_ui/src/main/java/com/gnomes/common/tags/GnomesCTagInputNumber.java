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
 * 数値入力カスタムタグ
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
public class GnomesCTagInputNumber extends GnomesCTagBase {
    /** 辞書：ラベルリソースID **/
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：数値Bean (必須) **/
    private static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：小数点桁数Bean **/
    private static final String INFO_DECIMAL_POINT = "decimal_point_bean";

    /** 辞書：単位種別Bean **/
    private static final String INFO_UNIT_NAME = "unit_name";

    /** 辞書：INPUT タイプ **/
    private static final String INFO_INPUT_TYPE = "input_type";

    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

    /** 辞書：数値入力名称 **/
    private static final String INFO_INPUT_NUMBER_NAME = "input_number_name";

    /** 辞書：最大入力文字数 **/
    private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";

    /** 辞書：最大入力文字数(ドメイン定義) **/
    private static final String INFO_INPUT_MAX_LENGTH_DOMAIN = "input_max_length_domain";

    /** 辞書：整数入力桁数 **/
    private static final String INFO_INPUT_INTEGER_DIGITS = "input_integer_digits";

    /** 辞書：整数入力桁数Bean **/
    private static final String INFO_INPUT_INTEGER_DIGITS_BEAN = "input_integer_digits_bean";

    /** 辞書：整数入力桁数(ドメイン定義) **/
    private static final String INFO_INPUT_INTEGER_DIGITS_DOMAIN = "input_integer_digits_domain";

    /** 辞書：小数点入力桁数 **/
    private static final String INFO_INPUT_DECIMAL_DIGITS = "input_decimal_digits";

    /** 辞書：小数点入力桁数Bean **/
    private static final String INFO_INPUT_DECIMAL_DIGITS_BEAN = "input_decimal_digits_bean";

    /** 辞書：小数点入力桁数(ドメイン定義) **/
    private static final String INFO_INPUT_DECIMAL_DIGITS_DOMAIN = "input_decimal_digits_domain";

    /** 辞書：カンマ区切りフォーマットを行うか否か **/
    private static final String INFO_INPUT_IS_COMMA_FORMAT = "input_is_comma_format";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";

    /** 辞書：INPUT差し替えクラス */
    private static final String INFO_INPUT_CHANGE_CLASS = "input_change_class";

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
     * 数値入力タグ出力
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
            Map<String,Object> mapInfo = dict.getNumberInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();
            // 右側ラベル出力

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))){
            	isPanecon = true;
            }

            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);
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
            }

            //decimalPoint:小数点桁数
            Integer decimalPoint = 0;
            String labelDecimalPoint = (String)mapInfo.get(INFO_DECIMAL_POINT);
            if ( labelDecimalPoint != null) {
            	decimalPoint = (Integer) getData(clsBean, bean, labelDecimalPoint);
            }

            // inputNumberName:input name="inputNumberName"
            String strInputNumberName = (String)mapInfo.get(INFO_INPUT_NUMBER_NAME);
            if(StringUtil.isNullOrEmpty(strInputNumberName)){
            	strInputNumberName = "";
            } else {
            	strInputNumberName =  " name=\"" + strInputNumberName + "\"";
            }


            //unitName:単位名称
            String strUnitName = "";
            String labelUnitName = (String)mapInfo.get(INFO_UNIT_NAME);
            if ( labelUnitName != null) {
                strUnitName = (String) getData(clsBean, bean, labelUnitName);
                if(StringUtil.isNullOrEmpty(strUnitName)){
                    strUnitName ="";
                } else {
                	strUnitName = "&nbsp;" + this.getStringEscapeHtml(strUnitName);
                }
            }


            // typeName:タイプ指定 ( input/readonly/disabled)
            String strInputType = (String)mapInfo.get(INFO_INPUT_TYPE);
            if(StringUtil.isNullOrEmpty(strInputType)){
                strInputType = "";
            }

            // 最大入力文字数の設定
            String strMaxLength = "";
            // ドメインID(最大入力文字数)
            String inputDomainIdMaxLength = (String)mapInfo.get(INFO_INPUT_MAX_LENGTH_DOMAIN);

            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_MAX_LENGTH))){
            	strMaxLength = " maxlength=\"" + (String)mapInfo.get(INFO_INPUT_MAX_LENGTH) + "\"";
            }
            else if(!StringUtil.isNullOrEmpty(inputDomainIdMaxLength)){
            	try{
            		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainIdMaxLength);
	            	String inputMaxLength = inputDomain.getCustomTagData().getInputMaxLength();
	            	if (!StringUtil.isNullOrEmpty(inputMaxLength)){
	            		strMaxLength = " maxlength=\"" + inputMaxLength + "\"";
	            	}
            	}
            	catch (Exception e) {
            		// ME01.0215:「指定されたドメインIDは定義されていません。（タグ名：{0}、{1}）」
            		String errorMessage = MessagesHandler.getString(GnomesMessagesConstants.ME01_0215, userLocale, this.dictId, inputDomainIdMaxLength);
            		logger.severe(errorMessage);
                    GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                    throw ex;
            	}

            }

            String strIntegerDigits = "";
            String strDecimalDigits = "";
            /*[2019/09/13 浜本記載]
             * 元々工程端末用の仕掛けだが、管理端末にも適用するため端末制限を外す
            if (isPanecon) { */

          	// ドメインID(整数入力桁数)
            String inputDomainIdIntegerDigits = (String)mapInfo.get(INFO_INPUT_INTEGER_DIGITS_DOMAIN);

            // 整数入力桁数の設定!
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_INTEGER_DIGITS))){
            	strIntegerDigits = " data-INTEGER-DIGITS=\"" + (String)mapInfo.get(INFO_INPUT_INTEGER_DIGITS) + "\"";
            }
            else if (!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_INTEGER_DIGITS_BEAN))) {
            	Integer intDigits = (Integer)getData(clsBean, bean, (String)mapInfo.get(INFO_INPUT_INTEGER_DIGITS_BEAN));
            	if (!Objects.isNull(intDigits)) {
            		strIntegerDigits = " data-INTEGER-DIGITS=\"" + intDigits + "\"";
            	}
            }
            else if(!StringUtil.isNullOrEmpty(inputDomainIdIntegerDigits)){
            	try{
            		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainIdIntegerDigits);
	            	String inputIntegerDigits = inputDomain.getCustomTagData().getInputIntegerDigits();
	            	if (!StringUtil.isNullOrEmpty(inputIntegerDigits)){
	            		strIntegerDigits = " data-INTEGER-DIGITS=\"" + inputIntegerDigits + "\"";
	            	}
            	}
            	catch (Exception e) {
            		// ME01.0215:「指定されたドメインIDは定義されていません。（タグ名：{0}、{1}）」
            		String errorMessage = MessagesHandler.getString(GnomesMessagesConstants.ME01_0215, userLocale, this.dictId, inputDomainIdIntegerDigits);
            		logger.severe(errorMessage);
                    GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                    throw ex;
            	}

            }

            // ドメインID(小数点入力桁数)
            String inputDomainIdDecimalDigits = (String)mapInfo.get(INFO_INPUT_DECIMAL_DIGITS_DOMAIN);

            // 小数点入力桁数の設定!
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_DECIMAL_DIGITS))){
            	strDecimalDigits = " data-DECIMAL-DIGITS=\"" + (String)mapInfo.get(INFO_INPUT_DECIMAL_DIGITS) + "\"";
            }
            else if (!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_DECIMAL_DIGITS_BEAN))) {
            	Integer decimalDigits = (Integer)getData(clsBean, bean, (String)mapInfo.get(INFO_INPUT_DECIMAL_DIGITS_BEAN));
            	if (!Objects.isNull(decimalDigits)) {
            		strDecimalDigits = " data-DECIMAL-DIGITS=\"" + decimalDigits + "\"";
            	}
            }
            else if(!StringUtil.isNullOrEmpty(inputDomainIdDecimalDigits)){
            	try{
            		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainIdDecimalDigits);
	            	String inputDecimalDigits = inputDomain.getCustomTagData().getInputDecimalDigits();
	            	if (!StringUtil.isNullOrEmpty(inputDecimalDigits)){
	            		strDecimalDigits = " data-DECIMAL-DIGITS=\"" + inputDecimalDigits + "\"";
	            	}
            	}
            	catch (Exception e) {
            		// ME01.0215:「指定されたドメインIDは定義されていません。（タグ名：{0}、{1}）」
            		String errorMessage = MessagesHandler.getString(GnomesMessagesConstants.ME01_0215, userLocale, this.dictId, inputDomainIdDecimalDigits);
            		logger.severe(errorMessage);
                    GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                    throw ex;
            	}

            }

            //} ←端末制限をもとに戻す場合はこちらを戻す

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
              	  strInputType = "" ;
                }
            }


            String tabIndex = "";
            // readonlyの場合、タブ移動によるフォーカスを当てさせない
            if (strInputType.equals(INPUT_READONLY)) {
            	tabIndex = " tabindex=\"-1\"";
            }

            // 追加クラスの設定
            String addClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(addClass)){
                addClass = "";
            } else {
            	addClass = " " + addClass;
            }

            String strInputClass = (String)mapInfo.get(INFO_INPUT_CHANGE_CLASS);
            // INPUT差し替えクラスが設定されていない場合
            if(StringUtil.isNullOrEmpty(strInputClass)){
            	// 共通クラスを使用
            	strInputClass = "common-text-number gnomes-number";
            }
            // 工程端末の場合
            if ( isPanecon ){
            	// 数値入力ダイアログ表示
            	strInputClass = strInputClass + " common-keyboard-input-num";
            }

            // INPUT追加クラスの設定
            String strInputAddStyle = (String)mapInfo.get(INFO_INPUT_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(strInputAddStyle)){
            	strInputAddStyle = "";
            } else {
            	strInputAddStyle = " " + strInputAddStyle;
            }

            // 桁数設定がある場合
            if(!StringUtil.isNullOrEmpty(strIntegerDigits) || !StringUtil.isNullOrEmpty(strDecimalDigits)){
            	// フォーカスアウト時に桁数をチェック。入力桁数を超えた場合は削除される。
            	strInputClass = strInputClass + " number-digit-check";
            }

            // カンマ区切りを行う場合
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_IS_COMMA_FORMAT))){
            	strInputClass = strInputClass + " gnomes-number-format";
            }

            // value
            String strInputValue = null;

            // Beanからの取得
            //INFO_PARAM_NAME:入力する数値のBean
            String labelNumberValue = (String)mapInfo.get(INFO_PARAM_NAME);
//            Object valueObj  = getData(clsBean, bean, labelNumberValue);
            Object valueObj = responseContext.getResponseFormBean(bean, labelNumberValue, (String)mapInfo.get(INFO_INPUT_NUMBER_NAME));

            if (valueObj instanceof String) {
                strInputValue = (String)valueObj;
            } else {
                strInputValue = getStringNumber(dictId, labelNumberValue, valueObj, decimalPoint);
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
            }
            else {
           	  onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
           }

            out.print( "<div class=\"common-header-col-data common-input common-text-number" + addClass + "\">");

            out.print("<input type=\"text\"" + strInputNumberName + strInputType + tabIndex
                    + strMaxLength + strIntegerDigits + strDecimalDigits
                    + " value=\"" + this.getStringEscapeHtmlValue(strInputValue)
                    + "\" class=\"" + strInputClass + strInputAddStyle + "\""+ onchange + ">"
                    + strUnitName + "</div>\n");

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