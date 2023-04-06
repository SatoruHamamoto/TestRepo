package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController.OrderType;
import com.gnomes.common.search.data.MstOrdering;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.OrderingInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IScreenFormBean;

/**
 * ソートダイアログ カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	A.Oomori               初版
 * R0.01.01 2019/05/27 YJP/S.Hamamoto            indexを廃止
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagSortDialog extends GnomesCTagBaseSearch {

    /** 辞書：ダイアログヘッダーリソースID */
    private static final String INFO_DIALOG_HEADER_RESOURCE_ID = "dialog_header_resource_id";

    /** 辞書：ソート対象テーブルID */
    private static final String INFO_SORT_TABLE_ID = "sort_table_id";

    /** 辞書：ソートダイアログID */
    private static final String INFO_SORT_DIALOG_ID = "sort_dialog_id";

    /** 辞書：ソートボタンコマンドID */
    private static final String INFO_SORT_BUTTON_COMMAND_ID = "sort_button_command_id";

    /** 辞書：ソートボタンOnclick */
    private static final String INFO_SORT_BUTTON_ONCLICK = "sort_button_onclick";

    /** 辞書：ソート条件保存ボタン */
    private static final String INFO_SAVE_CONDITION_BUTTON = "save_condition_button";

    /** 辞書：ソート条件初期化ボタン */
    private static final String INFO_INIT_CONDIITON_BUTTON = "init_condition_button";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 固定列数 */
    public static final int COLUMN_FIXED_NUMBER = 10;


    /**  辞書：工程端末表示か否か */
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
     * ソートダイアログタグ出力
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
            // ソートダイアログ辞書取得
            Map<String,Object> sortDialogInfo = dict.getSortDialogInfo(this.dictId);


            // ソート対象テーブルID取得
            String sortTableId = (String)sortDialogInfo.get(INFO_SORT_TABLE_ID);


            // 検索マスター
            MstSearchInfo sortMasterInfo = this.bean.getMstSearchInfoMap().get(sortTableId);

            // 検索設定
            SearchSetting sortInfo;
            if (responseContext.isLogicError()) {
                // リクエスト情報より取得する
                sortInfo = requestContext.getRequestSearchSetting(sortTableId);
            } else {
                sortInfo = this.bean.getSearchSettingMap().get(sortTableId);
            }

            String strLabel = "";
            // ソートダイアログタイトルリソースID取得
            String labelRourceId = (String)sortDialogInfo.get(INFO_DIALOG_HEADER_RESOURCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // ソートダイアログID取得
            String sortDialogId = (String)sortDialogInfo.get(INFO_SORT_DIALOG_ID);
            if (StringUtil.isNullOrEmpty(sortDialogId)) {
                throw new Exception();
            }


            // スタイルシート追加クラス取得
            String addStyle = (String)sortDialogInfo.get(INFO_ADD_STYLE);
            if (StringUtil.isNullOrEmpty(addStyle)) {
                 addStyle = "";
            }

            // フィルタテーブル部タグID
            String searchTableTagId = sortDialogId + "_FilterTable";

            // ソートテーブル部タグID
            String sortTableTagId = sortDialogId + "_SortTable";

            // ソートタグID
            String sortTagId = sortDialogId + "_Sort";

            // ソート条件保存ボタン出力有無取得
            String saveButton = (String)sortDialogInfo.get(INFO_SAVE_CONDITION_BUTTON);

            //保存ボタン出力が有りでも、保管領域が指定と食い違っていたら非表示にする
            if(StringUtil.isNotNull(saveButton)){
                if(! judgeDisplayFromConnectionArea(sortDialogInfo)){
                    saveButton = null;
                }
            }

            // ソート条件初期化ボタン出力有無取得
            String initButton = (String)sortDialogInfo.get(INFO_INIT_CONDIITON_BUTTON);

            String onclick = "";
            // コマンドID取得
            if (!StringUtil.isNullOrEmpty((String)sortDialogInfo.get(INFO_SORT_BUTTON_COMMAND_ID))) {
                onclick = "sortSubmit('" + (String)sortDialogInfo.get(INFO_SORT_BUTTON_COMMAND_ID) + "','" + sortTableId + "', '" + searchTableTagId + "', '" + sortTableTagId + "');";
            }
            // onclick取得
            else if (!StringUtil.isNullOrEmpty((String)sortDialogInfo.get(INFO_SORT_BUTTON_ONCLICK))) {
                onclick = (String)sortDialogInfo.get(INFO_SORT_BUTTON_ONCLICK);

            }

            // 列固定取得
            int fixedColNum = sortInfo.getFixedColNum();

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil.isNullOrEmpty((String)sortDialogInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            // tabindex取得
            String tabindex = "";
            if(isPanecon){
            	tabindex = "-1";
            }

            // ヘッダー
            out.print("<div class=\"modal\" id=\"" + sortTagId + "\" data-keyboard=\"false\" data-backdrop=\"static\">");
            out.print("  <div class=\"modal-sort-dialog\">");
            out.print("    <div class=\"common-dialog-content sort-dialog-area "+ addStyle +"\">\n");
            out.print("      <div class=\"common-dialog-header-title clearfix\">" + strLabel + "&nbsp;" + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0084, userLocale)) + "</div>\n");
            out.print("      <div class=\"common-dialog-header-wrapper clearfix\">\n");

            // ソート部
            out.print("        <div class=\"sort-dialog-body-column sort-dialog-table-area common-table-ttl-fix\">\n");

            // ソートダイアログ項目の出力
            outSortDialogItem(out, sortTableTagId, sortMasterInfo, sortInfo, userLocale);

            out.print("        </div>\n");
            out.print("      </div>\n");

            out.print("      <div class=\"sort-dialog-footer-area\">\n");
            // 保存ボタン、ソート条件初期化ボタン、列固定プルダウンの出力
            outSortDialogFooter(out, sortTableId, searchTableTagId, sortTableTagId, sortMasterInfo, saveButton, initButton, onclick, fixedColNum, userLocale);
            out.print("      </div>\n");

            // フッター
            out.print("      <div class=\"common-dialog-footer clearfix\">\n");
            out.print("        <div class=\"common-dialog-footer-button-left\"><a href=\"#\" class=\"modal-focus\" name=\"common-dialog-cancel\" onclick=\"$('#" + sortTagId + "').modal('hide'); return false;\" tabindex=\"" + tabindex + "\"><span class=\"common-button common-dialog-footer-button\">" +
                    this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0057, userLocale)) + "</span></a></div>\n");
            out.print("        <div class=\"common-dialog-footer-button-right\"><a href=\"#\" class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\"" + onclick + " return false;\" tabindex=\"" + tabindex + "\">" +
                                "<span class=\"common-button common-dialog-footer-button\">" +
                    this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0019, userLocale)) + "</span></a></div>\n");

            out.print("      </div>\n");
            out.print("    </div>\n");
            out.print("  </div>\n");
            out.print("</div>\n");
            // テーブルのヘッダー部をダブルクリックすることでソートダイアログを表示
            out.print("<script>\n");
            out.print("$(document).ready(function () {\n");

            out.print("  window.addEventListener(\"load\", function() {\n");
            out.print("    $( \"[name=" + sortTagId + "_tr]\" ).dblclick( function(e){\n");
            out.print("      setSortDblClick(e);\n");
            out.print("    });\n");
            out.print("  }, false);\n");

            out.print("});\n");

            out.print("$(window).resize(function () {\n");
            out.print("  $( \"[name=" + sortTagId + "_tr]\" ).unbind('dblclick');\n");
            out.print("  $( \"[name=" + sortTagId + "_tr]\" ).dblclick( function(e){\n");
            out.print("    setSortDblClick(e);\n");
            out.print("  });\n");
            out.print("});\n");
            out.print("</script>\n");


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
     * ソートダイアログ項目表示出力
     * @param out 出力先
     * @param searchTableTagId ソート部タグID
     * @param searchMasterInfo 検索マスター
     * @param searchInfo 検索設定
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    protected void outSortDialogItem(JspWriter out, String sortTableTagId, MstSearchInfo searchMasterInfo, SearchSetting searchInfo,  Locale userLocale) throws Exception {

        List<MstOrdering> mstOrderings = searchMasterInfo.getMstOrdering();

        List<OrderingInfo> orderingInfos = searchInfo.getOrderingInfos();

        // ソート対象の総計
        Integer mstOrderingsSize = 0;
        for (MstOrdering mstOrdering: mstOrderings) {
            if (!StringUtil.isNullOrEmpty(mstOrdering.getColumnName())) {
                mstOrderingsSize++;
            }
        }

        // ソート部ヘッダー
        // 固定有り無し
        String rowNum = "1";
        String colNum = "1";
        out.print("        <table class=\"common-table\" _fixedhead=\"rows:"+rowNum+" cols:"+colNum+" div-full-mode=no;\">\n");
        out.print("          <thead class=\"common-header-table\">\n");
        out.print("            <tr><th></th><th></th><th>" + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0222, userLocale)) + "</th><th>" +
                this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0223, userLocale)) + "</th><th>" +
                this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0224, userLocale)) + "</th><th>" +
                this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0225, userLocale)) + "</th></tr>\n");
        out.print("          </thead>\n");

        out.print("          <tbody id=\"" + sortTableTagId + "\">\n");


        // ソート部ボディ
        for (OrderingInfo orderingInfo: orderingInfos) {

        	String indexLabel = orderingInfo.getColumnId();


            // 表示対象カラム
            String dispColumn = indexLabel;

            // カラム名
            String columnName = sortTableTagId + "_" + indexLabel;

            // カラムID
            String columnId = orderingInfo.getColumnId();
            if(StringUtil.isNullOrEmpty(columnId)){
            	columnId="";
            }
            // 表示カラム名
            MstOrdering sortMaster = searchMasterInfo.getMsterOrderingInfo(StringUtil.isNullOrEmpty(indexLabel)? null:indexLabel);
			//古い情報があった場合は表示を無視する
            //古い検索条件が保存されていた場合はマスターヒットしないので、設定を無視する
            if(sortMaster == null){
            	continue;
            }

            String text = this.getStringEscapeHtml(sortMaster.getText());

            // テーブル内 表示有無
            boolean isHiddenColumn = orderingInfo.isHiddenTable();

            // ソート方式  -1:ソート無し 0:昇順 1:降順
            OrderType orderType = orderingInfo.getOrderType();

            // ソート順序
            Integer orderNum = orderingInfo.getOrderNum();

            // ソート必須
            boolean sortRequired = orderingInfo.getSortRequired();

            out.print("            <tr class=\"tr\" id=\""+ columnName + "\" columnId=\"" + columnId + "\" sortRequired=" + sortRequired + ">\n");

            // 表示順列
            out.print("              <td class=\"\"><a class=\"modal-focus\" href=\"#\" name=\"moveUp\" onclick=\"moveUp('"+ columnName + "','#" + sortTableTagId + "'); return false;\">" +
                                        "<img src=\"./images/gnomes/icons/up-chevron-button.png\"></a></td>");
            out.print("              <td class=\"\"><a href=\"#\" name=\"moveDown\" onclick=\"moveDown('"+ columnName + "','#" + sortTableTagId + "'); return false;\">" +
                                        "<img src=\"./images/gnomes/icons/up-chevron-button.png\" class=\"common-icon-roteto180\"></a></td>");

            // 表示有無チェックボックス列
            if(!isHiddenColumn){
                out.print("              <td class=\"\"><input type=\"checkbox\" name=\"" + columnName + "_disp\" value=\"1\" checked></td>");
            }
            else{
                out.print("              <td class=\"\"><input type=\"checkbox\" name=\"" + columnName + "_disp\" value=\"1\"></td>");
            }


            // カラム名列
            out.print("              <td class=\"\">" + text + "</td>");

            // ソート順序設定可能項目の場合（表示カラム名が設定されている場合）
            if (!StringUtil.isNullOrEmpty(dispColumn) && orderType.getValue() > OrderType.NONE.getValue() ) {
                // ソート順序プルダウンを表示
                if(Objects.nonNull(sortRequired) && sortRequired == false){
                    out.print("              <td class=\"\">\n");
                }
                else {
                    out.print("              <td class=\"orderingInfo_sortrequired\">\n");
                }
                out.print("                <select name=\"" + columnName + "_orderNum\">\n");

                //ソート必須列は先頭の空白を指定できないようにする
                if(Objects.nonNull(sortRequired) && sortRequired == false){
                    out.print("                  <option></option>\n");
                }
                for (int i = 1; i <= mstOrderingsSize; i++) {
                    String iLabel = this.getStringEscapeHtmlValue(getStringNumber(i, false, null));
                    if (orderNum != null && i == orderNum) {
                        out.print("                  <option value=\"" + iLabel + "\" selected=\"selected\">" + iLabel + "</option>\n");
                    }
                    else {
                            out.print("                  <option value=\"" + iLabel + "\">" + iLabel + "</option>\n");
                    }
                }
                out.print("                </select>\n");
                out.print("              </td>\n");

                // ソート方式ラジオボタンを表示
                out.print("              <td class=\"\">\n");
                if (orderType.getValue() == 0) {
                    out.print("                <input type=\"radio\" name=\"" + columnName  + "_orderType\" value=\"0\" checked>"
                    + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0226, userLocale)) + "\n");
                    out.print("                <input type=\"radio\" name=\"" + columnName  + "_orderType\" value=\"1\">"
                    + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0227, userLocale)) + "\n");
                }
                else {
                    out.print("                <input type=\"radio\" name=\"" + columnName  + "_orderType\" value=\"0\">"
                    + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0226, userLocale)) + "\n");
                    out.print("                <input type=\"radio\" name=\"" + columnName  + "_orderType\" value=\"1\" checked>"
                    + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0227, userLocale)) + "\n");
                }

                out.print("              </td>\n");

            }
            // ソート順序設定可能項目の場合
            else {
                out.print("              <td class=\"\">\n");
                out.print("              </td>\n");
                out.print("              <td class=\"\">\n");
                out.print("              </td>\n");
            }

            out.print("            </tr>\n");

        }
        out.print("          </tbody>\n");
        out.print("        </table>\n");

    }



    /**
     * ソートダイアログ項目表示出力(Bean定義)
     * @param out 出力先
     * @param sortTableId ソートテーブルID
     * @param searchTableTagId 検索タグID
     * @param sortTableTagId ソートタグID
     * @param searchMasterInfo 検索マスター
     * @param saveButton ソート条件保存ボタン出力有無
     * @param initButton ソート条件初期化ボタン出力有無
     * @param onclick onclickコマンド
     * @param fixedColNum 列固定
     * @param userLocale ユーザーロケール
     * @throws Exception
     */
    private void outSortDialogFooter(JspWriter out,
                                     String sortTableId,
                                     String searchTableTagId,
                                     String sortTableTagId,
                                     MstSearchInfo searchMasterInfo,
                                     String saveButton,
                                     String initButton,
                                     String onclick,
                                     int fixedColNum,
                                     Locale userLocale) throws Exception {


    	// 辞書取得
        GnomesCTagDictionary dict = getCTagDictionary();
        // ソートダイアログ辞書取得
        Map<String,Object> sortDialogInfo = dict.getSortDialogInfo(this.dictId);

        // 工程端末か否か（デフォルトは管理端末表示）
        boolean isPanecon = false;
        if (!StringUtil.isNullOrEmpty((String)sortDialogInfo.get(INFO_IS_PANECON))) {
            isPanecon = true;
        }

        // tabindex取得
        String tabindex = "";
        if(isPanecon){
        	tabindex = "-1";
        }

        // 保存ボタンの出力
        if (!StringUtil.isNullOrEmpty(saveButton)) {
            String buttonName = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0085, userLocale));
            out.print("      <a href=\"#\" onclick=\"save_table_search_setting("
                                            + "'" + CommandIdConstants.COMMAND_ID_Y99004C001 + "',"
                                            + "'" + sortTableId + "',"
                                            + CommonEnums.TableSearchSettingType.DISPLAY.getValue() + ","
                                            + "'" + searchTableTagId + "',"
                                            + "'" + sortTableTagId + "'"
                                            + "); return false;\" tabindex=\"" + tabindex + "\">\n");
            out.print("        <span class=\"common-button\">" + buttonName + "</span>\n");
            out.print("      </a>\n");
        }
        // ソート条件初期化ボタンの出力
        if (!StringUtil.isNullOrEmpty(initButton)) {
            String buttonName = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0138, userLocale));
            out.print("      <a href=\"#\" onclick=\"init_table_search_setting("
                                            + "'" + CommandIdConstants.COMMAND_ID_Y99004C002 + "',"
                                            + "'" + sortTableId + "',"
                                            + CommonEnums.TableSearchSettingType.DISPLAY.getValue() + ","
                                            + "'" + searchTableTagId + "',"
                                            + "'" + sortTableTagId + "'"
                                            + "); return false;\" tabindex=\"" + tabindex + "\">\n");
            out.print("        <span class=\"common-button\">" + buttonName + "</span>\n");
            out.print("      </a>\n");

        }

        List<MstOrdering> mstOrderings = searchMasterInfo.getMstOrdering();
        // ソート対象の総計
        Integer mstOrderingsSize = 0;
        for (MstOrdering mstOrdering: mstOrderings) {
            if (!StringUtil.isNullOrEmpty(mstOrdering.getColumnName())) {
                mstOrderingsSize++;
            }
        }

        // 列固定
        out.print("      <span class=\"fixed-col-list\">"
        + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0228, userLocale)) + ":\n");
        out.print("        <select name=\"fixed-col-list\" id=\""+ sortTableTagId + "_fixedColList\">\n");
        out.print("          <option value=\"0\">0</option>\n");
        for (int i = 1; i <= COLUMN_FIXED_NUMBER; i++) {
            String iLabel = this.getStringEscapeHtmlValue(getStringNumber(i, false, null));
            if (i == fixedColNum) {
                out.print("          <option value=\"" + iLabel + "\" selected=\"selected\">" + iLabel + "</option>\n");
            }
            else {
                out.print("          <option value=\"" + iLabel + "\">" + iLabel + "</option>\n");
            }
        }

        out.print("        </select>\n");
        out.print("      </span>\n");

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