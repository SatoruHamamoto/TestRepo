package com.gnomes.common.tags;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayDiscardChangeFlag;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * ボタン カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagButton extends GnomesCTagBasePrivilege {

    /** 辞書：ボタン名 */
    private static final String INFO_BUTTON_NAME = "button_name";

    /** 辞書：ボタンリソースID */
    private static final String INFO_BUTTON_RESOUCE_ID = "button_resource_id";

    /** 辞書：コマンドID */
    private static final String INFO_COMMAND_ID = "command_id";

    /** 辞書：BeanからコマンドIDを取得 */
    private static final String INFO_BEAN_COMMAND_ID = "bean_command_id";

    /** 辞書：オンクリック */
    private static final String INFO_ONCLICK = "onclick";

    /** 辞書：画像パス */
    private static final String INFO_IMAGE = "image";

    /** 辞書：OKボタンのリソースID */
    private static final String INFO_OK_RESOURCE_ID = "ok_resource_id";

    /** 辞書：スタイルシート指定クラス */
    private static final String INFO_STYLE_CLASS = "style_class";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 辞書：ボタンID */
    private static final String INFO_BUTTON_ID = "button_id";

    /**  辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 警告チェック処理 */
    protected static final String WARMING_ALERT_PROCESS = "warmingAlertProcess(\'%s\',\'%s\');";

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
     * ボタンタグ出力
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
            // ボタン辞書取得
            Map<String,Object> mapInfo = dict.getButtonInfo(this.dictId);

            // パーツ権限情報Listを取得
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();
            // ボタン名取得
            String buttonName = getStringEscapeHtml((String)mapInfo.get(INFO_BUTTON_NAME));
            if(StringUtil.isNullOrEmpty(buttonName)){
            	buttonName = "";
            }

            String strLabel = "";
            // ボタンリソースID取得
            String labelRourceId = (String)mapInfo.get(INFO_BUTTON_RESOUCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // onclick
            String onclick = "";
            String okScript = "";
            // コマンドID取得
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_COMMAND_ID))){
            	onclick = String.format(COMMAND_SCRIPT_FORMAT, (String)mapInfo.get(INFO_COMMAND_ID));
            	okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, (String)mapInfo.get(INFO_COMMAND_ID));
            }
            // BeanからコマンドID取得
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_BEAN_COMMAND_ID))){
            	Object valueObj = getData(clsBean, bean, (String)mapInfo.get(INFO_BEAN_COMMAND_ID));
            	if (valueObj != null) {
            		onclick = String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(valueObj));
            		okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, String.valueOf(valueObj));
                }
            }
            // onclick取得
            else if((String)mapInfo.get(INFO_ONCLICK) != null){
            	onclick = (String)mapInfo.get(INFO_ONCLICK);

            }

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            // tabindex取得
            String tabindex = "";
            if(isPanecon){
            	tabindex = "-1";
            }

            // 画像パス取得
            String imagePath = (String)mapInfo.get(INFO_IMAGE);

            // スタイルシート指定クラス取得
            String styleClass = (String)mapInfo.get(INFO_STYLE_CLASS);
            // 指定がない場合は共通ボタンクラスを使用
            if(StringUtil.isNullOrEmpty(styleClass)){
            	styleClass = "common-button";

            }
            // スタイルシート追加クラス取得
            String addStyle = (String)mapInfo.get(INFO_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(addStyle)){
            	 addStyle = "";
            }

            // OKボタンのリソースID取得
            String okResourceLabel = "";
            String okResourceId = (String)mapInfo.get(INFO_OK_RESOURCE_ID);
            if (!StringUtil.isNullOrEmpty(okResourceId)) {
            	okResourceLabel = ResourcesHandler.getString(okResourceId, userLocale);
            }

            // ボタンID取得
            String buttonId = (String)mapInfo.get(INFO_BUTTON_ID);

            PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

            //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
            String disabledClass = judgeAndSetDisabledClass(mapInfo);

            if (!StringUtil.isNullOrEmpty(buttonId)) {
                // パーツ権限を取得
                partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(buttonId, stmPartsPrivilegeResultInfo);
                // 権限からボタンのhtml属性取得
                Map<String, String> mapAttribute =
                		getButtonAttribute(partsPrivilegeResultInfo, strLabel, okResourceLabel, okScript, (String)mapInfo.get(INFO_ONCLICK), userLocale);

                // onclickスクリプト
                onclick = mapAttribute.get(MAP_KEY_ONCLICK);
                // disableが空でない（権限無し）場合
                if(!StringUtil.isNullOrEmpty(mapAttribute.get(MAP_KEY_DISABLE))){
                	// 非活性表示クラス＊業務側の処理によりで対応すること
                	// disabledClass = " common-disabled";
                }

                if (partsPrivilegeResultInfo != null) {
                    String discardFlag ="";
                    if (partsPrivilegeResultInfo.getDisplayDiscardChangeFlag()
                            == PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_ON) {
                        discardFlag = "1";
                    } else {
                        discardFlag = "0";
                    }

                    String uncOnClick = URLEncoder.encode(onclick, "UTF-8");

                    String warmingAlertProcess = String.format(WARMING_ALERT_PROCESS, discardFlag, uncOnClick);
                    onclick = "var success = " + warmingAlertProcess + "if (success) { " + onclick + "}";
                }
            }

            // BaseFormBeanのカスタムタグの非表示区分よりstyleを取得する
            String strStyle = this.getStyleHiddenKindWithAttribute(this.bean, this.dictId);

            // 権限無しのClass名
            String nonePrivilegeClassName = "";

            // 権限があるかをチェック
            if (partsPrivilegeResultInfo != null && !partsPrivilegeResultInfo.isPrivilege()) {
                // 権限無しの場合、ボタン表示区分を取得
                DisplayDivision displayDivision = partsPrivilegeResultInfo.getDisplayDiv();

                if (displayDivision == DisplayDivision.DISABLE) {
                    // ボタン表示区分が非活性である場合
                    nonePrivilegeClassName = " common-button-disabled";
                }
                else if (displayDivision == DisplayDivision.HIDE) {
                    // ボタン表示区分が非表示である場合
                    nonePrivilegeClassName = " common-button-hide";
                }
                else {
                    nonePrivilegeClassName = "";
                }
            }

            if (!StringUtil.isNullOrEmpty(nonePrivilegeClassName)) {
                // 権限無しのClass名が空白ではない場
                // 画像パスを取得した場合、画像ボタン表示
                if (!StringUtil.isNullOrEmpty(imagePath)) {
                    // *共通ボタンクラスは使用しない（追加スタイルの指定は可能）
                    out.print(
                            "<div name=\"" + buttonName + "\" class=\"" + addStyle + disabledClass + nonePrivilegeClassName + "\"" + strStyle + " tabindex=\"" + tabindex + "\">\n");
                    out.print("    <img alt=\"\" src=\"" + imagePath + "\">\n");
                }
                // ラベルボタン表示
                else {
                    out.print(
                            "<div name=\"" + buttonName + "\" class=\"" + styleClass + " " + addStyle + disabledClass + nonePrivilegeClassName + "\"" + strStyle + " tabindex=\"" + tabindex + "\">" + strLabel);
                }

            }
            else {
                // 権限ありの場合、または、ボタン表示区分が「エラーを表示（従来の動き）」である場合
                // 画像パスを取得した場合、画像ボタン表示
                if (!StringUtil.isNullOrEmpty(imagePath)) {
                    // *共通ボタンクラスは使用しない（追加スタイルの指定は可能）
                    out.print(
                            "<div name=\"" + buttonName + "\" class=\"" + addStyle + disabledClass + "\"" + strStyle + " onclick=\"" + onclick + "\" tabindex=\"" + tabindex + "\">\n");
                    out.print("    <img alt=\"\" src=\"" + imagePath + "\">\n");
                }
                // ラベルボタン表示
                else {
                    out.print(
                            "<div name=\"" + buttonName + "\" class=\"" + styleClass + " " + addStyle + disabledClass + "\"" + strStyle + " onclick=\"" + onclick + "\" tabindex=\"" + tabindex + "\">" + strLabel);
                }
            }

            out.print("</div>\n");

        } catch(Exception e) {
            if (out != null) {
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