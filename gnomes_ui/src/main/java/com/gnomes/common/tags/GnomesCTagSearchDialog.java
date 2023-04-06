package com.gnomes.common.tags;

import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IScreenFormBean;

/**
 * 検索ダイアログ カスタムタグ
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
public class GnomesCTagSearchDialog extends GnomesCTagBaseSearch {

    /** 辞書：ダイアログヘッダーリソースID */
    private static final String INFO_DIALOG_HEADER_RESOURCE_ID = "dialog_header_resource_id";

    /** 辞書：検索対象テーブルID */
    private static final String INFO_SEARCH_TABLE_ID = "search_table_id";

    /** 辞書：検索ダイアログID */
    private static final String INFO_SEARCH_DIALOG_ID = "search_dialog_id";

    /** 辞書：検索ボタンコマンドID */
    private static final String INFO_SEARCH_BUTTON_COMMAND_ID = "search_button_command_id";

    /** 辞書：検索ボタンOnclick */
    private static final String INFO_SEARCH_BUTTON_ONCLICK = "search_button_onclick";

    /** 辞書：検索条件保存ボタン */
    private static final String INFO_SAVE_CONDITION_BUTTON = "save_condition_button";

    /** 辞書：検索条件初期化ボタン */
    private static final String INFO_INIT_CONDIITON_BUTTON = "init_condition_button";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private BaseFormBean bean;

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
    public BaseFormBean getBean() {
        return bean;
    }

    /**
     * Bean設定
     * @param bean Bean
     */
    public void setBean(BaseFormBean bean) {
        this.bean = bean;
    }

    /**
     * 検索ダイアログタグ出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {
            //Beanの指定が間違ってnullになっているのをチェック
            if ( this.bean == null) {
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }

            Locale userLocale = ((IScreenFormBean)this.bean).getUserLocale();

            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // 検索ダイアログ辞書取得
            Map<String,Object> searchDialogInfo = dict.getSearchDialogInfo(this.dictId);

            // 検索対象テーブルID取得
            String searchTableId = (String)searchDialogInfo.get(INFO_SEARCH_TABLE_ID);

            // 検索マスター
            MstSearchInfo searchMasterInfo = this.bean.getMstSearchInfoMap().get(searchTableId);

            // 検索設定
            SearchSetting searchInfo;
            if (responseContext.isLogicError()) {
                // リクエスト情報より取得する
                searchInfo = requestContext.getRequestSearchSetting(searchTableId);
            } else {
                searchInfo = this.bean.getSearchSettingMap().get(searchTableId);
            }

            String strLabel = "";
            // 検索ダイアログタイトルリソースID取得
            String labelRourceId = (String)searchDialogInfo.get(INFO_DIALOG_HEADER_RESOURCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // 検索ダイアログID取得
            String searchDialogId = (String)searchDialogInfo.get(INFO_SEARCH_DIALOG_ID);
            if (StringUtil.isNullOrEmpty(searchDialogId)) {
                throw new Exception();
            }


            // スタイルシート追加クラス取得
            String addStyle = (String)searchDialogInfo.get(INFO_ADD_STYLE);
            if (StringUtil.isNullOrEmpty(addStyle)) {
                 addStyle = "";
            }

            // フィルター部タグID
            String searchTableTagId = searchDialogId + "_FilterTable";

            // ソート部タグID
            String sortTableTagId = searchDialogId + "_SortTable";

            searchDialogId = searchDialogId + "_Search";

            // 検索条件保存ボタン出力有無取得
            String saveButton = (String)searchDialogInfo.get(INFO_SAVE_CONDITION_BUTTON);

            //保存ボタン出力が有りでも、保管領域が指定と食い違っていたら非表示にする
            if(StringUtil.isNotNull(saveButton)){
                if(! judgeDisplayFromConnectionArea(searchDialogInfo)){
                    saveButton = null;
                }
            }

            // 検索条件初期化ボタン出力有無取得
            String initButton = (String)searchDialogInfo.get(INFO_INIT_CONDIITON_BUTTON);

            String onclick = "";
            // 検索ボタンコマンドID取得
            if(!StringUtil.isNullOrEmpty((String)searchDialogInfo.get(INFO_SEARCH_BUTTON_COMMAND_ID))){
                onclick = "searchSubmit('" + (String)searchDialogInfo.get(INFO_SEARCH_BUTTON_COMMAND_ID) + "','" +
                        searchTableId + "', '" + searchTableTagId + "', '" + sortTableTagId + "');";
            }
            // onclick取得
            else if (!StringUtil.isNullOrEmpty((String)searchDialogInfo.get(INFO_SEARCH_BUTTON_ONCLICK))) {
                onclick = (String)searchDialogInfo.get(INFO_SEARCH_BUTTON_ONCLICK);

            }

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil.isNullOrEmpty((String)searchDialogInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            out.print("<div class=\"modal\" id=\"" + searchDialogId + "\" data-keyboard=\"false\" data-backdrop=\"static\">\n");

            // 工程端末表示
            if (isPanecon) {
                out.print("  <div class=\"modal-search-dialog\">\n");
                out.print("    <div class=\"common-dialog-content search-dialog-area-panecon "+ addStyle + "\">\n");

            }
            // 管理端末表示
            else {
                out.print("  <div class=\"modal-search-dialog\">\n");
                out.print("    <div class=\"common-dialog-content search-dialog-area " + addStyle + "\">\n");
            }

            out.print("      <div class=\"common-dialog-header-title clearfix\">" + strLabel +
                    "&nbsp;" + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0220, userLocale)) + "</div>\n");
            out.print("      <div class=\"common-dialog-header-wrapper clearfix\">\n");

            // フィルター部
            out.print("        <div class=\"common-dialog-body-column\">\n");

            // 検索ダイアログ項目の出力
            outSearchDialogItem(out, searchTableId, searchTableTagId, searchMasterInfo, searchInfo, isPanecon, userLocale);

            out.print("        </div>\n");
            out.print("        <div class=\"search-dialog-separation\"></div>\n");

            // tabindex取得
            String tabindex = "";
            if(isPanecon){
            	tabindex = "-1";
            }

            // 保存ボタンの出力
            if (!StringUtil.isNullOrEmpty(saveButton)) {
                String buttonName = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0085, userLocale));
                out.print("        <a href=\"#\" onclick=\"save_table_search_setting("
                        + "'" + CommandIdConstants.COMMAND_ID_Y99004C001 + "',"
                        + "'" + searchTableId + "',"
                        + CommonEnums.TableSearchSettingType.SEARCH.getValue() + ","
                        + "'" + searchTableTagId + "',"
                        + "'" + sortTableTagId + "'"
                        + "); return false;\" tabindex=\"" + tabindex + "\">\n");
                out.print("          <span class=\"common-button\">" + buttonName + "</span>\n");
                out.print("        </a>\n");

            }
            // 検索条件初期化ボタンの出力
            if (!StringUtil.isNullOrEmpty(initButton)) {
                String buttonName = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0086, userLocale));
                out.print("        <a href=\"#\" name=\"searchInitializeButton\" id=\"searchInitializeButton\" onclick=\"init_table_search_setting("
                        + "'" + CommandIdConstants.COMMAND_ID_Y99004C002 + "',"
                        + "'" + searchTableId + "',"
                        + CommonEnums.TableSearchSettingType.SEARCH.getValue() + ","
                        + "'" + searchTableTagId + "',"
                        + "'" + sortTableTagId + "',"
                        + String.valueOf(isPanecon) + "); return false;\" tabindex=\"" + tabindex + "\">\n");
                out.print("          <span class=\"common-button\">" + buttonName + "</span>\n");
                out.print("        </a>\n");

            }

            out.print("      </div>\n");

            // フッター
            out.print("      <div class=\"common-dialog-footer clearfix\">\n");
            out.print("        <div class=\"common-dialog-footer-button-left\"><a href=\"#\" class=\"modal-focus\" name=\"common-dialog-cancel\" onclick=\"originalSearchModal('" + searchTableId + "', '" + searchTableTagId + "');$('#" + searchDialogId + "').modal('hide'); return false;\" tabindex=\"" + tabindex + "\"><span class=\"common-button common-dialog-footer-button\">" +
                    this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0057, userLocale)) + "</span></a></div>\n");
            out.print("        <div class=\"common-dialog-footer-button-right\"><a href=\"#\" class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\"" + onclick + " return false;\" tabindex=\"" + tabindex + "\"><span class=\"common-button common-dialog-footer-button\">" +
                    this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0087, userLocale)) + "</span></a></div>\n");

            out.print("      </div>\n");
            out.print("    </div>\n");
            out.print("  </div>\n");
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