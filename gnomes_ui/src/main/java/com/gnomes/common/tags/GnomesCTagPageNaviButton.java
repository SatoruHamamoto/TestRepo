package com.gnomes.common.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.model.PagingBaseModel.PagingCommand;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;

/**
 * ページ選択用リストボタン カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/18 YJP/	I.Shibasaka          初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagPageNaviButton extends GnomesCTagBase {

    /** 辞書：ボタンリソースID */
    private static final String INFO_PREVIOUS_BUTTON_RESOUCE_ID = "prev_button_resource_id";

    /** 辞書：ボタンリソースID */
    private static final String INFO_NEXT_BUTTON_RESOUCE_ID = "next_button_resource_id";

    /** 辞書：画像パス */
    private static final String INFO_PREV_IMAGE = "prev_image";

    /** 辞書：画像パス */
    private static final String INFO_NEXT_IMAGE = "next_image";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_PREV_ADD_STYLE = "prev_add_style";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_NEXT_ADD_STYLE = "next_add_style";

    /** 辞書：コマンドID */
    private static final String INFO_COMMAND_ID = "command_id";


    /** 辞書ID */
    private String dictId;

    /** bean */
    private BaseFormBean bean;

    @Inject
    SearchInfoController searchInfoController;

    /** 表示件数プルダウン */
    private  static final String INFO_PAGE_PULLDOWN_LIST = "pagePulldownList";

    /** プルダウン候補名 */
    private static final String PULLDOWN_NAME = "name";

    /** プルダウン候補値 */
    private static final String PULLDOWN_VALUE = "value";

    /** 辞書：テーブルID */
    public static final String INFO_TABLE_ID = "table_id";

    /** ページングフォーマット */
    private static final String FORMAT_FUNC_PAGING= "funcPaging('%s','%s', %s); return false;";

    /** 1ページ目 */
    private static final int CURRENT_PAGE_ONE = 1;

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
     * ページ選択用リストボタンタグ出力
     */
    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {
            // Beanの指定が間違ってnullになっているのをチェック
            if (this.bean == null) {
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }

            Locale userLocale = ((IScreenFormBean)this.bean).getUserLocale();

            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();

            // ラベル辞書取得
            Map<String,Object> mapInfo = dict.getPagingButtonInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            String tableId = (String)mapInfo.get(INFO_TABLE_ID);
            String commandId = (String)mapInfo.get(INFO_COMMAND_ID);

            SearchSetting searchSetting = this.bean.getSearchSettingMap()
                    .get(tableId);

            // 現在表示のページ取得
            int currentPage = searchSetting.getNowPage() ;

            // 最大ページ数
            int pageSize = 1;
            if (searchSetting.getAllDataCount() > 0) {
                pageSize = searchSetting.getAllDataCount() / searchSetting.getOnePageDispCount();
                if ((searchSetting.getAllDataCount() % searchSetting.getOnePageDispCount()) != 0) {
                    pageSize ++;
                }
            }
            // pageプルダウン情報取得
            String fieldNamePullDown = (String)mapInfo.get(INFO_PAGE_PULLDOWN_LIST);
            List<Object> lstValue = (List<Object>) getData(clsBean, bean, fieldNamePullDown);

            // 前へボタン表示
            outPrevPageButton(mapInfo, currentPage, out, userLocale, commandId);

            // ページボタン表示
            outPageButton(pageSize, currentPage, out, commandId);

            // 次へボタン表示
            outNextPageButton(mapInfo, currentPage, pageSize, out, userLocale, commandId);

            // pageプルダウンの表示
            outPagePullDown(searchSetting.getAllDataCount(), searchSetting.getOnePageDispCount(), lstValue, out, tableId, commandId, userLocale);

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
     * 次へボタンの表示
     * @param mapInfo 辞書情報
     * @param currentPage 現在表示ページ
     * @param pageSize ページサイズ
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param commandId コマンドID
     * @throws Exception 例外
     */
    private void outNextPageButton(Map<String, Object> mapInfo,
            int currentPage,
            int pageSize,
            JspWriter out,
            Locale userLocale,
            String commandId) throws Exception {

        // 次へボタンの追加スタイル取得
        String nextAddStyle = (String)mapInfo.get(INFO_NEXT_ADD_STYLE);
        if (StringUtil.isNullOrEmpty(nextAddStyle)) {
            nextAddStyle = "";
        }

        // 次へボタンのラベル取得
        String strNextLabel = "";
        // ボタンリソースID取得
        String nextLabelRourceId = (String)mapInfo.get(INFO_NEXT_BUTTON_RESOUCE_ID);
        if (nextLabelRourceId != null) {
            strNextLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(nextLabelRourceId, userLocale));
        }

        // 画像パス取得
        String nextImagePath = (String)mapInfo.get(INFO_NEXT_IMAGE);

        // 次へボタンの生成
        String onClick = String.format(FORMAT_FUNC_PAGING, commandId,
                PagingCommand.PagingCommand_Next.getValue(), 0);

        out.print("<div class=\"common-button common-pageNavi-next");
        if (currentPage == pageSize) {
            out.print(" common-page-navi-button-disable");
        }
        if (nextAddStyle != null) {
            out.print(" " + nextAddStyle);
        }
        out.print("\"");
        if (currentPage != pageSize) {
            out.print(" onclick=\"" + onClick + "\"");
        }
        out.print(">" + strNextLabel + "\n");

        if (!StringUtil.isNullOrEmpty(nextImagePath)) {
            out.print("    <img alt=\"\" src=\"" + nextImagePath + "\">\n");
        }
        out.print("</div>\n");

    }

    /**
     * 前へボタンの表示
     * @param mapInfo 辞書情報
     * @param currentPage 現在表示ページ
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param commandId コマンドID
     * @throws Exception 例外
     */
    private void outPrevPageButton(Map<String, Object> mapInfo,
            int currentPage,
            JspWriter out,
            Locale userLocale,
            String commandId) throws Exception {

        // スタイルシート追加クラス取得
        String prevAddStyle = (String)mapInfo.get(INFO_PREV_ADD_STYLE);
        if (StringUtil.isNullOrEmpty(prevAddStyle)) {
            prevAddStyle = "";
        }

        String strLabel = "";
        // ボタンリソースID取得
        String prevLabelRourceId = (String)mapInfo.get(INFO_PREVIOUS_BUTTON_RESOUCE_ID);
        if (prevLabelRourceId != null) {
            strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(prevLabelRourceId, userLocale));
        }

        // 前へ画像パス取得
        String prevImagePath = (String)mapInfo.get(INFO_PREV_IMAGE);

        String onClick = String.format(FORMAT_FUNC_PAGING, commandId,
                PagingCommand.PagingCommand_Prev.getValue(), 0);
        out.print("<div class=\"common-button common-pageNavi-preview");
        if (currentPage == CURRENT_PAGE_ONE) {
            out.print(" common-page-navi-button-disable");
        }
        if (prevAddStyle != null) {
            out.print(" " + prevAddStyle);
        }
        out.print("\"");
        if (currentPage != CURRENT_PAGE_ONE) {
            out.print(" onclick=\"" + onClick + "\"");
        }
        out.print(">\n");
        if (!StringUtil.isNullOrEmpty(prevImagePath)) {
            out.print("    <img alt=\"\" src=\"" + prevImagePath + "\">" + strLabel + "\n");
        }
        out.print("</div>\n");

    }

    /**
     * 表示件数プルダウンの表示
     * @param listCount リスト件数
     * @param currentPageTable 1ページ当たりの表示件数
     * @param lstValue ページプルダウン情報
     * @param out 出力先
     * @param tableId テーブルID
     * @param commandId コマンドID
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    private void outPagePullDown(int listCount, int currentPageTable, List<Object> lstValue,
            JspWriter out, String tableId, String commandId, Locale userLocale) throws Exception {

        // pageプルダウン
        out.print("<div class=\"common-pageNavi-totalPage\"><div class=\"common-pageNavi-flex\"><div class=\"common-pageNavi-label\">"
        + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0128, userLocale))
        + ":" + listCount + "/\n");
        out.print("</div><div data-command=\"" + commandId + "\" data-tableid=\""+ tableId + "\" style=\"display: inline-block\" class=\"common-pageNavi-pulldown\">\n");
        out.print("<select onchange=\"selectPageNum(this);\">\n");

        if (lstValue != null && lstValue.size() > 0) {
            Class<?> lstClass = lstValue.get(0).getClass();
            for (int i=0; i<lstValue.size(); i++) {
                Object item = lstValue.get(i);

                Object key = getData(lstClass, item, PULLDOWN_NAME);
                Object value = getData(lstClass, item, PULLDOWN_VALUE);

                if (value.equals(String.valueOf(currentPageTable))) {
                    out.print(
                            "<option class=\"common-pageNum-option common-pageNum-option-selected\" value=\""
                                    + key + "\" selected>" + this.getStringEscapeHtmlValue((String)value) + "</option>\n");

                } else {
                    out.print("<option class=\"common-pageNum-option\" value=\""
                            + key + "\">" + this.getStringEscapeHtmlValue((String)value) + "</option>\n");

                }
            }
        }

        out.print("</select>");
        out.print("</div>\n");
        out.print("</div>\n");
        out.print("</div>\n");
    }

    /**
     * ページボタン表示
     * @param pageSize ページサイズ
     * @param currentPage 現在ページ
     * @param out 出力先
     * @param commandId コマンドID
     * @throws IOException 例外
     */
    private void outPageButton(int pageSize, int currentPage, JspWriter out, String commandId) throws IOException {

        // 5ページ以下の場合
        if (pageSize <= 5) {
            for (int i = 1; i <= pageSize; i++) {
                if (i == currentPage) {
                    out.print("<div class=\"common-button common-pageNavi-page common-page-navi-button-disable\">"+i+"</div>\n");
                } else {
                    String onlick = String.format(FORMAT_FUNC_PAGING, commandId,
                            PagingCommand.PagingCommand_ChangeNowPage.getValue(),
                            String.valueOf(i));
                    out.print(
                            "<div class=\"common-button common-pageNavi-page\" onclick=\""
                                    + onlick + "\">" + i + "</div>\n");
                }
            }
        } else {
            // 先に表示するページ数を検索
            List<Integer> pageList = new ArrayList<Integer>();
            for (int i = 1; i <= pageSize; i++) {
                // 最初のページ
                if (i == 1) {
                    pageList.add(i);
                }
                // 最後のページ
                else if (i == pageSize) {
                    pageList.add(i);
                }
                else {
                    if (i >= currentPage - 2 &&  i <= currentPage + 2) {
                        pageList.add(i);
                    } else {
                        // 表示しない
                    }
                }
            }

            boolean bOmmit = true;
            // ボタン生成
            for (int j = 1; j <= pageSize; j++) {
                for (int i = 0; i < pageList.size(); i++) {
                    if (pageList.get(i) == j) {
                        int pageNum = pageList.get(i);

                        if (pageList.get(i) == currentPage) {
                            out.print(
                                    "<div class=\"common-button common-pageNavi-page common-page-navi-button-disable\">"
                                            + pageNum + "</div>\n");
                        } else {
                            String onlick = String.format(FORMAT_FUNC_PAGING,
                                    commandId,
                                    PagingCommand.PagingCommand_ChangeNowPage.getValue(),
                                    String.valueOf(pageNum));
                            out.print(
                                    "<div class=\"common-button common-pageNavi-page\" onclick=\""
                                            + onlick + "\">" + pageNum
                                            + "</div>\n");
                        }
                        bOmmit = false;
                    }
                    else {
                        if (!bOmmit && !pageList.contains(j)) {
                            out.print("<div class=\"common-button common-pageNavi-page common-page-navi-button-disable\">"+"..."+"</div>\n");
                            bOmmit = true;
                        } else {
                            // 何もしない
                        }
                    }
                }
            }
        }
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