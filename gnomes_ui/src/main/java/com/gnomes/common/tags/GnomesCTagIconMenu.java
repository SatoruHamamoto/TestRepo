package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * アイコンメニューカスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/2/27 YJP/S.Michiura             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagIconMenu extends GnomesCTagBasePrivilege {

    /** 辞書：アイコンメニューボタンリソースID */
    private static final String INFO_ICON_MENU_RESOUCE_ID = "icon_menu_resource_id";

    /** 辞書：画像パス */
    private static final String INFO_IMAGE = "image";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** イメージクラス名追加指定 **/
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：コマンドID */
    private static final String INFO_COMMAND_ID = "command_id";

    /** 辞書：BeanからコマンドIDを取得 */
    private static final String INFO_BEAN_COMMAND_ID = "bean_command_id";

    /** 辞書：オンクリック */
    private static final String INFO_ONCLICK = "onclick";

    /** ボタンID関係 **/
    /** 辞書：アイコンメニュー名 */
    private static final String INFO_ICON_MENU_NAME = "icon_menu_name";

    /** 辞書：OKボタンのリソースID */
    private static final String INFO_OK_RESOURCE_ID = "ok_resource_id";

    /** 辞書：ボタンID */
    private static final String INFO_BUTTON_ID = "button_id";

    /**  辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

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
     * アイコンメニュータグ出力
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
            // IconMenu辞書取得
            Map<String,Object> mapInfo = dict.getIconMenuInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // パーツ権限情報Listを取得
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);

            // ボタン名取得
            String IconMenuName = (String)mapInfo.get(INFO_ICON_MENU_NAME);
            if(StringUtil.isNullOrEmpty(IconMenuName)){
            	IconMenuName = "";
            }


            String strLabel = "";
            // アイコンメニューボタンリソースID取得
            String labelRourceId = (String)mapInfo.get(INFO_ICON_MENU_RESOUCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // onclick
            String onclick = "";
            String okScript = "";
            // コマンドID取得
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_COMMAND_ID))){
                String commandId = (String)mapInfo.get(INFO_COMMAND_ID);
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
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ONCLICK))){
                onclick = (String)mapInfo.get(INFO_ONCLICK);
            }

            // スタイルシート追加クラス取得
            String strAddStyle = (String)mapInfo.get(INFO_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(strAddStyle)){
                strAddStyle = "";
            }
            else{
            	strAddStyle = " " + strAddStyle;

            }

            // イメージクラス名追加指定取得
            String strAddClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(strAddClass)){
                strAddClass = "";
            } else {
                strAddClass = " " + strAddClass;
            }

            // 画像パス取得
            String StrImagePath = (String)mapInfo.get(INFO_IMAGE);
            if(StringUtil.isNullOrEmpty(StrImagePath)){
                StrImagePath = "";
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

            // 権限無しのClass名
            String nonePrivilegeClassName = "";

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

                // 権限があるかをチェック
                if (partsPrivilegeResultInfo != null && !partsPrivilegeResultInfo.isPrivilege()) {
                    // 権限無しの場合、ボタン表示区分を取得
                    DisplayDivision displayDivision = partsPrivilegeResultInfo.getDisplayDiv();

                    if (displayDivision == DisplayDivision.DISABLE) {
                        // ボタン表示区分が非活性である場合
                        nonePrivilegeClassName = " common-icon-menu-disabled";
                    }
                    else if (displayDivision == DisplayDivision.HIDE) {
                        // ボタン表示区分が非表示である場合
                        nonePrivilegeClassName = " common-icon-menu-hide";
                    }
                    else {
                        nonePrivilegeClassName = "";
                    }
                }
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
            
            // BaseFormBeanのカスタムタグの非表示区分よりstyleを取得する
            String strStyle = this.getStyleHiddenKindWithAttribute(this.bean, this.dictId);

            if (!StringUtil.isNullOrEmpty(nonePrivilegeClassName)) {
                // 権限無しのClass名が空白ではない場
                tabindex = "-1";
                out.print(
                        "<div class=\"common-panecon-menu-box" + strAddStyle + disabledClass + nonePrivilegeClassName + "\"" + strStyle + " tabindex=\"" + tabindex + "\">\n");
                out.print(
                        "    <img src=\"" + StrImagePath + "\" alt=\"\" class=\"common-panecon-menu-box-img \" >\n");
                out.print(
                        "    <div class=\"common-panecon-menu-box-text" + strAddClass + "\">" + strLabel + "</div>\n");
                out.print("</div>\n");
            }
            else {
                // 権限ありの場合、または、ボタン表示区分が「エラーを表示（従来の動き）」である場合
                //OUT:<div class="common-koutei-menu-box" onclick="document.main.command.value='P02001C005';document.main.submit(); return false;" >
                out.print("<div class=\"common-panecon-menu-box" + strAddStyle + disabledClass + "\"" + strStyle + " onclick=\"" + onclick + " return false;\" tabindex=\"" + tabindex + "\">\n");

                //OUT:<img src="assets/img/icon/icon-koutei-menu-2.png" alt="更新" class="common-koutei-menu-box-img" >
                out.print("    <img src=\"" + StrImagePath + "\" alt=\"\" class=\"common-panecon-menu-box-img\" >\n");

                //OUT: <div class="common-koutei-menu-box-text">SUB MENU</div>
                out.print("    <div class=\"common-panecon-menu-box-text" + strAddClass + "\">" + strLabel + "</div>\n");

                //OUT: </div>
                out.print("</div>\n");
            }

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