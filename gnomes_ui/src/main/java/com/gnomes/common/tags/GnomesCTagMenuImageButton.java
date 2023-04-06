package com.gnomes.common.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.entity.MstrScreenTransition;

/**
 * メニューイメージボタン カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	I.Shibasaka          初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagMenuImageButton extends GnomesCTagBaseBookmark {

    /** 辞書：メニューボタンリソースID */
    private static final String INFO_MENU_RESOUCE_ID = "menu_resource_id";

    /** 辞書：画像パス */
    private static final String INFO_IMAGE = "image";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：プルダウン項目定義Bean */
    private static final String INFO_ITEM_BEAN = "menu_button_item_bean";

    /** 辞書：メニュー項目name */
    private static final String INFO_BUTTON_ITEM_NAME = "item_name";

    /** 辞書：メニュー項目リソースID */
    private static final String INFO_BUTTON_ITEM_RESOURCE_ID = "item_resource_id";

    /** 辞書：メニュー項目コマンドID */
    private static final String INFO_BUTTON_ITEM_COMMAND_ID = "item_command_id";

    /** 辞書：メニュー項目コマンドID */
    private static final String INFO_BUTTON_ITEM_ONCLICK = "item_onclick";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 辞書：メニュー項目 画面ID */
    private static final String INFO_BUTTON_ITEM_SCREEN_ID = "item_screen_id";

    /** ブックマークのname属性フォーマット */
    private static final String INFO_BUTTON_NAME_BOOKMARK_FORMAT = "bookmark_";

    /** ブックマークのname属性フォーマット */
    private static final String INFO_BUTTON_DICT_BOOKMARK = "bookmark";

    /**  ボタンID */
    private static final String INFO_BUTTON_BUTTON_ID = "button_id";

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
     * メニューイメージボタン タグ出力
     */
    @SuppressWarnings("unchecked")
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

            // メニューボタン辞書取得
            Map<String,Object> menuButtonInfo = dict.getMenuButtonInfo(this.dictId);
            // メニューボタン項目辞書取得
            Map<String,Object> menuButtonItemInfo = dict.getMenuButtonItemInfo(this.dictId);

             String strLabel = "";
            // メニューボタンリソースID取得
            String labelRourceId = (String)menuButtonInfo.get(INFO_MENU_RESOUCE_ID);
            if (labelRourceId != null && !labelRourceId.equals("")) {
                strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // 画像パス取得
            String imagePath = (String)menuButtonInfo.get(INFO_IMAGE);

            // スタイルシート追加クラス取得
            String addStyle = (String)menuButtonInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(addStyle)){
                 addStyle = "";
            }

            // メニュー項目定義Bean取得
            String menuButtonItemBeanName = (String)menuButtonInfo.get(INFO_ITEM_BEAN);

            //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
            String disabledClass = judgeAndSetDisabledClass(menuButtonInfo);

            // ボタン部処理
            out.print("<div class=\"common-menu-box" + disabledClass);
            if (addStyle.length() >= 0) {
                out.print(" " + addStyle);
            }
            out.print("\">\n");
            out.print("  <img src=\""+ imagePath +"\" alt=\"\" class=\"common-menu-box-img\">\n");
            out.print("  <div class=\"common-menu-box-text\">"+ strLabel +"</div>\n");
            out.print("</div>\n");

            // プルダウン部処理
            if(!StringUtil.isNullOrEmpty(menuButtonItemBeanName)){

                // 出力元データのクラス
                Class<?> clsBean = bean.getClass();

                // 出力元データのクラス
                List<MenuItemInfoBean> menuButtonItemInfoBeanList = (List<MenuItemInfoBean>)getData(clsBean, bean, menuButtonItemBeanName);

                // メニュー項目定義Beanを取得した場合
                if (menuButtonItemInfoBeanList != null && menuButtonItemInfoBeanList.size() > 0) {
                    outMenuButtonItem(out, menuButtonItemInfoBeanList, userLocale);
                }
            } else {

                if (this.dictId.equals(INFO_BUTTON_DICT_BOOKMARK)) {
                    outMenuButtonItemBookMark(out, userLocale);
                } else {
                    outMenuButtonItem(out, menuButtonItemInfo, userLocale);
                }
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
     * メニューボタン出力（メニュー項目情報）
     * @param out
     * @param menuButtonItemInfoBeanList
     * @param locale
     * @throws Exception
     */
    private void outMenuButtonItem(JspWriter out,
            List<MenuItemInfoBean> menuButtonItemInfoBeanList,
            Locale locale) throws Exception {

        out.print("<div class=\"common-menu-list\">\n");
        out.print("    <ul>\n");

        for(MenuItemInfoBean menuItemInfoBean: menuButtonItemInfoBeanList){

            // メニュー項目name名
            String itemName= menuItemInfoBean.getItemName();

            // メニュー項目リソースID
            String strLabel = "";
            String itemResourceId= menuItemInfoBean.getItemResourceId();
            if (!StringUtil.isNullOrEmpty(itemResourceId)) {
                strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(itemResourceId, locale));
            }


            String onclick = "";
            // コマンドID取得
            if(!StringUtil.isNullOrEmpty(menuItemInfoBean.getItemCommandId())) {
                onclick = "commandSubmit('" + menuItemInfoBean.getItemCommandId() + "');";
            }
            // onclick取得
            else if(!StringUtil.isNullOrEmpty(menuItemInfoBean.getItemOnclick())){
                onclick = menuItemInfoBean.getItemOnclick();

            }

            // スタイルシート追加クラス取得
            String addStyle = menuItemInfoBean.getAddStyle();
            if(StringUtil.isNullOrEmpty(addStyle)){
                 addStyle = "";
            }

            // メニューボタン項目接続領域設定
            Map<String, Object> menuItemInfoDetail = new HashMap<>();
            menuItemInfoDetail.put("operation_connection_area", menuItemInfoBean.getOperationConnectionArea());

            //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
            String disabledClass = judgeAndSetDisabledClass(menuItemInfoDetail);

            if (!disabledClass.equals(" gnomes_display_none ")) {
                out.print("      <li><a name=\"" + itemName + "\" onclick=\"" + onclick + "\" class=\"" + addStyle + "\">" + strLabel + "</a></li>\n");
            }
        }

        out.print("    </ul>\n");
        out.print("</div>\n");


    }

    /**
     * メニューボタン出力（アイテム定義またはDBから）
     * @param out
     * @param menuButtonItemInfo
     * @param locale
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void outMenuButtonItem(JspWriter out,
            Map<String, Object> menuButtonItemInfo, Locale locale)
            throws Exception {

        out.print("<div class=\"common-menu-list\">\n");
        out.print("  <ul>\n");
        
        // パーツ権限情報Listを取得
        List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);

        for(int i=1 ; i <= menuButtonItemInfo.size(); i++){
            
            // メニュー項目取得
            Map<String, Object> menuItemInfoDetail = (Map<String, Object>)menuButtonItemInfo.get(ConverterUtils.numberToString(false, locale.toString(), i, null));

            // パーツ権限を取得
            PartsPrivilegeResultInfo partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(menuItemInfoDetail.get(
                    INFO_BUTTON_BUTTON_ID).toString(), stmPartsPrivilegeResultInfo);

            String itemName = "";
            String onclick = "";
            String strLabel = "";
            String itemResId = "";
            String addStyle = "";
            String disabledClass = "";

            // 画面ID取得
            String screenId = (String) menuItemInfoDetail.get(INFO_BUTTON_ITEM_SCREEN_ID);
            // データベースから
            if (screenId != null) {

                // 画面遷移情報マスタを取得
                MstrScreenTransition mst = this.getMstrScreenTransition(screenId);

                // メニューボタン項目名取得
                itemName = (String)menuItemInfoDetail.get(INFO_BUTTON_ITEM_NAME);

                // メニューボタン項目リソースID
                itemResId = mst.getMenu_resource_id();
                if (!StringUtil.isNullOrEmpty(itemResId)) {
                    strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(itemResId, locale));
                }

                // コマンドID取得
                onclick = "commandSubmit('" + mst.getCommand_id() + "');";

                // スタイルシート追加クラス取得
                addStyle = (String)menuItemInfoDetail.get(INFO_ADD_STYLE );
                if(StringUtil.isNullOrEmpty(addStyle)){
                     addStyle = "";
                }

                //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
                disabledClass = judgeAndSetDisabledClass(menuItemInfoDetail);
            }
            // アイテム定義から
            else {
                // メニューボタン項目名取得
                itemName = (String)menuItemInfoDetail.get(INFO_BUTTON_ITEM_NAME);

                // メニューボタン項目リソースID
                itemResId = (String) menuItemInfoDetail.get(INFO_BUTTON_ITEM_RESOURCE_ID);
                if (!StringUtil.isNullOrEmpty(itemResId)) {
                    strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(itemResId, locale));
                }

                // コマンドID取得
                if(!StringUtil.isNullOrEmpty((String) menuItemInfoDetail.get(INFO_BUTTON_ITEM_COMMAND_ID))) {
                    onclick = "commandSubmit('" + (String) menuItemInfoDetail.get(INFO_BUTTON_ITEM_COMMAND_ID) + "');";
                }
                // onclick取得
                else if(!StringUtil.isNullOrEmpty((String) menuItemInfoDetail.get(INFO_BUTTON_ITEM_ONCLICK))){
                    onclick = (String) menuItemInfoDetail.get(INFO_BUTTON_ITEM_ONCLICK);

                }
                // スタイルシート追加クラス取得
                addStyle = (String)menuItemInfoDetail.get(INFO_ADD_STYLE );
                if(StringUtil.isNullOrEmpty(addStyle)){
                     addStyle = "";
                }
                //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
                disabledClass = judgeAndSetDisabledClass(menuItemInfoDetail);

            }

            // 権限無しのClass名
            String nonePrivilegeClassName = "";

            // 権限があるかをチェック
            if (partsPrivilegeResultInfo != null && !partsPrivilegeResultInfo.isPrivilege()) {
                // 権限無しの場合、ボタン表示区分を取得
                DisplayDivision displayDivision = partsPrivilegeResultInfo.getDisplayDiv();

                if (displayDivision == DisplayDivision.DISABLE) {
                    // ボタン表示区分が非活性である場合
                    nonePrivilegeClassName = " common-menu-image-button-disabled";
                }
                else if (displayDivision == DisplayDivision.HIDE) {
                    // ボタン表示区分が非表示である場合
                    nonePrivilegeClassName = " common-menu-image-button-hide";
                }
                else {
                    nonePrivilegeClassName = "";
                }
            }

            if (!disabledClass.equals(" gnomes_display_none ")) {
                if (!StringUtil.isNullOrEmpty(nonePrivilegeClassName)) {
                    // 権限無しのClass名が空白ではない場
                    out.print(
                            "      <li><a name=\"" + itemName + "\" class=\"" + addStyle + disabledClass + nonePrivilegeClassName + "\">" + strLabel + "</a></li>\n");
                }
                else {
                    // 権限ありの場合、または、ボタン表示区分が「エラーを表示（従来の動き）」である場合
                    out.print(
                            "      <li><a name=\"" + itemName + "\" onclick=\"" + onclick + "\" class=\"" + addStyle + disabledClass + "\">" + strLabel + "</a></li>\n");
                }
            }
        }

        out.print("  </ul>\n");
        out.print("</div>\n");

    }

    /**
     * メニューボタン出力（ブックマーク）
     * @param out
     * @param locale
     * @throws Exception
     */
    private void outMenuButtonItemBookMark(JspWriter out, Locale locale)
            throws Exception {

        out.print("<div class=\"common-menu-list\">\n");
        out.print("  <ul>\n");

        // ブックマーク取得
        List<MstrScreenTransition> lstBookmark = this.getBookmarkScreenTransitionList();

        for(int i=1 ; i <= lstBookmark.size(); i++){
            // マスター
            MstrScreenTransition mst = lstBookmark.get(i - 1);

            // メニューボタン項目名取得
            String itemName = INFO_BUTTON_NAME_BOOKMARK_FORMAT.concat(String.valueOf(i));

            // メニューボタン項目リソースID
            String strLabel = "";
            String itemResId = mst.getMenu_resource_id();
            if (!StringUtil.isNullOrEmpty(itemResId)) {
                strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(itemResId, locale));
            }

            String onclick = "";
            // コマンドID取得
            onclick = "commandSubmit('" + mst.getCommand_id() + "');";

            // 10196 Y.Kitazakzi
//            out.print("      <li><a href=\"#\" name=\"" + itemName + "\" onclick=\"" + onclick + "\" >" + strLabel + "</a></li>\n");
            out.print("      <li><a name=\"" + itemName + "\" onclick=\"" + onclick + "\" >" + strLabel + "</a></li>\n");

        }

        out.print("  </ul>\n");
        out.print("</div>\n");

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