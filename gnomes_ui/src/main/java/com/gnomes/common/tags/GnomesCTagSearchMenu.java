package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;

/**
 * 検索メニュー（右ペイン） カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	A.Oomori               初版
 * R0.01.02 2019/05/27 YJP/S.Hamamoto              indexを廃止
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagSearchMenu extends GnomesCTagBaseSearch {

    /** 辞書：検索メニュータイトルリソースID */
    private static final String INFO_SEARCH_MENU_TITLE_RESOURCE_ID = "search_menu_title_resource_id";

    /** 辞書：検索対象テーブルID */
    private static final String INFO_SEARCH_TABLE_ID = "search_table_id";

    /** 辞書：詳細検索ID */
    private static final String INFO_SEARCH_DETAIL_LINK = "search_detail_link";

    /** 辞書：詳細検索リンクの有無 */
    private static final String INFO_IS_SEARCH_DETAIL_LINK = "is_search_detail_link";

    /** 辞書：タイトルスタイルシート追加クラス */
    private static final String INFO_TITLE_ADD_STYLE = "add_style";

    /** 辞書：メニューボックススタイルクラス追加 */
    private static final String INFO_MENU_BOX_ADD_CLASS = "menu_box_add_style";

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
     * 検索メニュータグ出力
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
            // 検索メニュー辞書取得
            Map<String,Object> searchMenuInfo = dict.getSearchMenuInfo(this.dictId);

            // 検索対象テーブルID取得
            String searchTableId = (String)searchMenuInfo.get(INFO_SEARCH_TABLE_ID);

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

            // 詳細検索ID
            String searchDetailLink = (String)searchMenuInfo.get(INFO_SEARCH_DETAIL_LINK);

            // フィルター部タグID
            String searchTableTagId = searchDetailLink + "_FilterTableMenu";

            String strLabel = "";
            // 検索メニュータイトルリソースID取得
            String labelRourceId = (String)searchMenuInfo.get(INFO_SEARCH_MENU_TITLE_RESOURCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // タイトルスタイルシート追加クラス取得
            String titleAddStyle = (String)searchMenuInfo.get(INFO_TITLE_ADD_STYLE);
            if (StringUtil.isNullOrEmpty(titleAddStyle)) {
                titleAddStyle = "";
            }

            // メニューボックススタイルシート追加クラス取得
            String menuBoxAddStyle = (String)searchMenuInfo.get(INFO_MENU_BOX_ADD_CLASS);
            if (StringUtil.isNullOrEmpty(menuBoxAddStyle)) {
                menuBoxAddStyle = "";
           }
            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil.isNullOrEmpty((String)searchMenuInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            //R1.02追加（接続領域ごと操作可否で領域が違うと非活性になる)
            String inactive = judgeAndSetActivityInClass(searchMenuInfo);

            // 工程端末表示
            if (isPanecon) {
                out.print("  <div " + inactive + " class=\"common-flexMenu-search-header panecon-search-menu-header clearfix\">\n");
            }
            // 管理端末表示
            else {
                out.print("  <div " + inactive + " class=\"common-flexMenu-search-header clearfix\">\n");
            }

            out.print("    <span class=\"common-flexMenu-search-header-title " + titleAddStyle + "\">" + strLabel+ "\n");
            out.print("      <span class=\"common-flexMenu-search-header-icon\"><img alt=\"\" src=\"./images/gnomes/icons/icon-arrow-7.png\"></span>\n");
            out.print("    </span>\n");
            out.print("  </div>\n");

            // data-indexs
            String dataIndexs = getDataIndexType(searchMasterInfo, searchInfo);

            // 工程端末表示
            if (isPanecon) {
                out.print("  <div id=\"" + searchTableTagId
                        + "\" data-tableid=\"" + searchTableId
                        + "\" data-indexs='" + dataIndexs
                        + "' class=\"common-flexMenu-search-box common-flexMenu-size " + menuBoxAddStyle + " common-data-area-wrapper-t\">\n");
                out.print("    <div class=\"common-data-area-t\">\n");
                // 検索メニュー項目の出力
                outSearchMenuItem(out, searchTableTagId, searchMasterInfo, searchInfo, isPanecon, userLocale);
                out.print("    </div>\n");
                out.print("  </div>\n");

            }
            // 管理端末表示
            else {
                out.print("  <div id=\"" + searchTableTagId
                        + "\" data-tableid=\"" + searchTableId
                        + "\" data-indexs='" + dataIndexs
                        + "' class=\"common-flexMenu-search-box common-flexMenu-size " + menuBoxAddStyle + "\">\n");
                // 検索メニュー項目の出力
                outSearchMenuItem(out, searchTableTagId, searchMasterInfo, searchInfo, isPanecon, userLocale);
                out.print("  </div>\n");
            }

            // tabindex取得
            String tabindex = "";
            if(isPanecon){
            	tabindex = "-1";
            }

            // 詳細検索リンクを表示する場合
            if (!StringUtil.isNullOrEmpty((String)searchMenuInfo.get(INFO_IS_SEARCH_DETAIL_LINK))) {
                out.print("    <a href=\"#\" class=\"common-link search-detail-link\" onclick=\"$('#"+ searchDetailLink + "_Search').on('shown.bs.modal').modal(); return false;\" tabindex=\"" + tabindex + "\">" +
                        this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI91_0220, userLocale)) + "</a>\n");
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
     * data-indexsのデータを取得
     * @param searchMasterInfo 検索マスタ
     * @param searchInfo 検索情報
     * @return data-indexsのデータ
     */
    private String getDataIndexType(MstSearchInfo searchMasterInfo,
            SearchSetting searchInfo) {

        StringBuilder sb = new StringBuilder();
        List<MstCondition> mstConditions = searchMasterInfo.getMstConditions();
        List<ConditionInfo> conditionInfos = searchInfo.getConditionInfos();

        // 表示順
        int count = 0;
        for (ConditionInfo conditionInfo : conditionInfos) {
        	//検索条件項目は表示場合、
        	if(!conditionInfo.isHiddenItem()) {
	        	// 条件マスター情報Index
	        	String columnId = conditionInfo.getColumnId();
	            // 検索条件タイプ
	            ConditionType type = searchMasterInfo.getMstCondition(conditionInfo.getColumnId()).getType();

	            if (sb.length() > 0) {
	                sb.append(",");
	            }

	            // 表示順.条件マスター情報のカラムID : 検索条件タイプ
	            sb.append(String.format("\"%d.%s\":\"%d\"", count, columnId, type.getValue()));
	            count++;
        	}
        }

        return String.format("{ %s }", sb.toString());
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