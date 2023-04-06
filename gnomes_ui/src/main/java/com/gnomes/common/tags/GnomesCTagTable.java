package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;

/**
 * 管理端末テーブルカスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/16 YJP/I.Shibasaka           初版
 * R0.01.02 2018/11/09 YJP/A.Oomori              readonly時、tabIndex=-1を追加出力
 * R0.01.03 2018/11/20 YJP/A.Oomori              チェックボックスをクリックでチェック状態にした場合onclickを実行するよう修正
 * R0.01.04 2018/11/27 KCC/K.Fujiwara            形式をパラメータから決定する対応
 * R0.01.05 2019/01/28 YJP/A.Oomori              プルダウンの選択名を任意のパラメータ値に差し替えできるよう修正
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagTable extends GnomesCTagBase {

    /** タグタイプ：チェックボックス */
    public static final String TAG_TYPE_CHECKBOX = "checkbox";

    /** タグタイプ：非表示 */
    public static final String TAG_TYPE_HIDDEN = "hidden";

    /** タグタイプ：ボタン */
    public static final String TAG_TYPE_BUTTON = "button";

    /** タグタイプ：画像パターン */
    public static final String TAG_TYPE_IMG_PATTERN = "imgPattern";

    /** タグタイプ：テキストエリアパターン */
    public static final String TAG_TYPE_TEXTAREA = "textarea";

    /** タグタイプ：テキスト入力エリアパターン */
    private static final String TAG_TYPE_INPUT_TEXT = "input_text";

    /** タグタイプ：数値入力テキストエリアパターン */
    private static final String TAG_TYPE_INPUT_NUMBER = "input_number";

    /** タグタイプ：日付入力エリアパターン */
    private static final String TAG_TYPE_INPUT_DATE = "input_date";

    /** タグタイプ：日時入力パターン */
    private static final String TAG_TYPE_INPUT_ZONEDDATETIME = "input_timezone";

    /** タグタイプ：入力形式をパラメータから決定 */
    private static final String TAG_TYPE_INPUT_DATA_TYPE= "input_data_type";

    /** タグタイプ：表示形式をパラメータから決定 */
    private static final String TAG_TYPE_OUTPUT_DATA_TYPE= "output_data_type";


    /** タグタイプ：テキストエリア入力パターン */
    /* 現状想定されていないため、コメントアウト
    private static final String TAG_TYPE_INPUT_TEXTAREA = "input_textarea";
    */

    /** 辞書：タグタイプ */
    public static final String INFO_TAG_TYPE = "type";

    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";

    /** 辞書：パラメータ名 */
    public static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：スタイル */
    private static final String INFO_STYLE = "style";

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";

    /** 辞書：ボタンクラス */
    private static final String INFO_BUTTON_CLASS = "buttonClass";

    /** 辞書：オンクリック */
    private static final String INFO_ON_CLICK = "onclick";

    /** 辞書：プルダウン（コード等定数）パターン */
    public static final String TAG_TYPE_PULLDOWN_CODE = "pulldown_code";

    /** 辞書：プルダウン（コード等定数）パターン 先頭空白無し */
    public static final String TAG_TYPE_PULLDOWN_CODE_NO_SPACE = "pulldown_code_no_space";

    /** 辞書：プルダウン（データ項目）パターン */
    public static final String TAG_TYPE_PULLDOWN_DATA = "pulldown_data";

    /** 辞書：プルダウン（データ項目）パターン 先頭空白無し */
    public static final String TAG_TYPE_PULLDOWN_DATA_NO_SPACE = "pulldown_data_no_space";

    /** 辞書：画像ソース */
    private static final String INFO_IMG_SRC = "imgsrc";

    /** 辞書：タイトル */
    private static final String INFO_TITLE = "title";

    /** 辞書：パターン定義ID */
    public static final String INFO_PATTERN_ID = "pattern_id";

    /** 辞書：小数点桁数パラメータ名 */
    public static final String INFO_DECIMAL_POINT_PARAM_NAME = "decimal_point_name";

    /** 辞書：小数点桁数（固定値） */
    public static final String INFO_DECIMAL_POINT_VALUE = "decimal_point_value";

    /** 辞書：フォーマットリソースID */
    public static final String INFO_FORMAT_RESOURCE_ID = "format_resource_id";

    /** 辞書：非表示 */
    public static final String INFO_HIDDEN = "hidden";

    /** 辞書：プルダウン候補パラメータ名 */
    private static final String INFO_LIST_PARAM_NAME = "list_param_name";

    /** 辞書：プルダウン選択パラメータ名 */
    private static final String INFO_SELECT_PARAM_NAME = "select_param_name";

    /** 辞書：最大桁数 */
    private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";

    /** 辞書：整数入力桁数 */
    private static final String INFO_INPUT_INTEGER_DIGITS = "input_integer_digits";

    /** 辞書：小数点入力桁数 */
    private static final String INFO_INPUT_DECIMAL_DIGITS = "input_decimal_digits";

    /** 辞書：カンマ区切りフォーマットを行うか否か */
    private static final String INFO_INPUT_IS_COMMA_FORMAT = "input_is_comma_format";

    /** プルダウンリストID */
    //private static final String INFO_LIST_ID = "list_id";

    /** オートコンプリート */
    private static final String INFO_LIST_AUTOCOMPLETE = "list_autocomplete";

    /** テキストエリア行数 */
    /** 現状想定されていないため、コメントアウト
    private static final String INFO_ROWS = "rows";
    */

    /** 固定列数数 */
    public static final String INFO_COLS = "cols";

    /** 辞書：テーブル追加クラス */
    private static final String INFO_TABLE_ADD_CLASS = "table_add_class";

    /** 辞書：ソートダイアログID */
    private static final String INFO_TABLE_SORTDIALOG_ID = "table_sortdialog_id";

    /** 辞書：入力値変更後の処理 スクリプト */
    private static final String INFO_ON_CHANGE_EVENT = "on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_ON_CHANGE_COMMAND_ID = "on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_ON_CHANGE_BEAN_COMMAND_ID = "on_change_bean_command_id";

    @Inject
    GnomesCTagTableCommon gnomesCTagTableCommon;

    @Inject
    SearchInfoController searchInfoController;

    @Inject
    GnomesCTagButtonCommon gnomesCTagButtonCommon;

    /** 辞書ID */
    private String dictId;

    /** bean */
    private BaseFormBean bean;

    /** 一覧データ */
    private List<Object> lstObject;

    /** 固定行（ヘッダー） */
    private static final String headerPosition = "1";

    /** 固定列 */
    private static final String defaultColPosition = "0";

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
     * 一覧データを取得
     * @return lstObject
     */
    public List<Object> getLstObject() {
        return lstObject;
    }

    /**
     * 一覧データを設定
     * @param lstObject 一覧データ
     */
    public void setLstObject(List<Object> lstObject) {
        this.lstObject = lstObject;
    }

    /**
     * 検索情報を取得
     * @return 検索情報
     * @throws Exception 例外
     */
    private SearchInfoPack getSearchInfoPack() throws Exception {

        // 検索ダイアログ情報の取得
        SearchInfoPack searchInfoPack = new SearchInfoPack();
        SearchSetting searchSetting = null;
        MstSearchInfo mstSearchInfo = null;

        try {
            // 検索情報マスターの取得
            mstSearchInfo = this.bean.getMstSearchInfoMap()
                    .get(this.dictId);
        }
        catch(NullPointerException ex) {
            // 検索情報マスターがとれない
            logger.severe("MstSearchInfoMap is not found Table CustomTagID = " + this.dictId);
            throw ex;
        }
        try {
            // 検索設定の取得
            searchSetting = this.bean.getSearchSettingMap().get(this.dictId);
        }
        catch (NullPointerException ex)
        {
            // 検索条件の取得でNULLの場合、システム定数テーブルにMAX_LIST_DISPLAY_COUNTが定義
            // されていない可能性がある。
            logger.severe("MAX_LIST_DISPLAY_COUNT is not defined in table [mstr_system_define] Table CustomTagID = " + this.dictId);
            throw ex;
        }

        searchInfoPack.setMstConditions(mstSearchInfo.getMstConditions());
        searchInfoPack.setMstOrdering(mstSearchInfo.getMstOrdering());
        searchInfoPack.setSearchSetting(searchSetting);

        return searchInfoPack;
    }

    /**
     * テーブル出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {
            // Beanの指定が間違ってnullになっているのをチェック
            if ( this.bean == null){
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }

            Locale userLocale = ((IScreenFormBean) this.bean).getUserLocale();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();

            // 検索ダイアログ情報取得
            SearchInfoPack searchInfoPack = getSearchInfoPack();

            // 項目情報
            List<Map<String, Object>> lstTableInfo = gnomesCTagTableCommon
                    .getTableColumnInfo(searchInfoPack, this.dictId);

            // テーブル辞書取得
            Map<String, Object> mapTableInfo = gnomesCTagDictionary
                    .getTableInfo(dictId);

            out = pageContext.getOut();

            // リソースにするまたは辞書に追加する
            // tableタグ
            String table_add_class = (String) mapTableInfo.get(INFO_TABLE_ADD_CLASS);

            out.print("<table class=\"common-table");
            if (table_add_class != null) {
                out.print(" " + table_add_class);
            }

            // 固定有り無し
            if (searchInfoPack.getSearchSetting().getFixedColNum() != 0) {
                // 列固定あり
                out.print("\" _fixedhead=\"rows:" + headerPosition + "; cols:"
                        + searchInfoPack.getSearchSetting().getFixedColNum()
                        + "; div-full-mode=no;\"");
            } else {
                // 列固定なし
                out.print("\" _fixedhead=\"rows:" + headerPosition + "; cols:"
                        + defaultColPosition + "; div-full-mode=no;\"");
            }

            // tableタグに idとnameを差し込む
            // <Table id="xxx" name="xxx"
            outTagNameToIdName(out,dictId);

            out.print(">\n");

            // ソートダイアログIDの取得
            String tableSortdialogId = "";
            if (!StringUtil.isNullOrEmpty((String) mapTableInfo.get(INFO_TABLE_SORTDIALOG_ID))) {
                tableSortdialogId = (String) mapTableInfo.get(INFO_TABLE_SORTDIALOG_ID) + "_Sort";
            }
            // theadタグ
            int lastDispIndex = outHeader(out, userLocale, lstTableInfo, tableSortdialogId);

            // bodyタグ
            outBody(out, userLocale, dict, lstTableInfo, this.lstObject,
                    lastDispIndex);

            out.print("</table>\n");

        } catch (Exception e) {
            if (out != null) {
                try {
                    out.print("</table>\n");
                    out.print(this.getCTagErrorToMenu());
                    out.flush();
                } catch (Exception e1) {
                    throw new JspTagException(e1);
                }
            } else {
                try {
                    out = pageContext.getOut();
                    out.print(this.getCTagErrorToMenu());
                    out.flush();
                } catch (Exception e1) {
                    throw new JspTagException(e1);
                }
            }
            throw new JspTagException(e);
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
    public void release() {
    }

    /**
     * theadタグの出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param info テーブル辞書情報
     * @param tableSortdialogId ソートダイアログID
     * @return 最終表示項目Index
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private int outHeader(JspWriter out, Locale userLocale,
            List<Map<String, Object>> info, String tableSortdialogId) throws Exception {

        int lastDispIndex = -1;

        out.print("<thead>\n");
        if (StringUtil.isNullOrEmpty(tableSortdialogId)) {
            out.print("<tr>\n");
        } else {
            out.print("<tr id=\""+ tableSortdialogId +"_tr\" name=\""+ tableSortdialogId +"_tr\">\n");
        }

        for (int i = 0; i < info.size(); i++) {

            Map<String, Object> tr = info.get(i);
            Map<String, Object> mapColInfo = (Map<String, Object>) tr
                    .get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);
            Map<String, String> headInfo = (Map<String, String>) tr
                    .get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);

            String label = "";

            // headInfoのタイプ取得
            String type = headInfo.get(INFO_TAG_TYPE);
            if (type == null) {
                out.print("<th></th>");
                continue;
            }

            // 表示項目の最終項目判定
            // 非表示項目は表示項目以降に設定することが前提
            if (!TAG_TYPE_HIDDEN.equals(type)) {
                // 最後判定
                if (i == info.size() - 1) {
                    lastDispIndex = i;
                } else {
                    // 次の項目が非表示が判定
                    Map<String, Object> trNext = info.get(i + 1);
                    Map<String, String> headInfoNext = (Map<String, String>) trNext
                            .get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);
                    String typeNext = headInfoNext.get(INFO_TAG_TYPE);
                    if (TAG_TYPE_HIDDEN.equals(typeNext)) {
                        lastDispIndex = i;
                    }
                }
            }

            // ヘッダースタイルの取得
            String style = headInfo.get(INFO_STYLE);
            switch (type) {
            // チェックボックス
            case TAG_TYPE_CHECKBOX:
                out.print("<th class=\"common-table-checkbox\"");
                if (!StringUtil.isNullOrEmpty(style)) {
                    out.print(" style=\""+style + "\"");
                }
                out.print(">");

                // タグ名
                String tagName = (String) mapColInfo.get(INFO_TAG_NAME);
                // 全チェックonclick
                String onclickAllCheck = "checkAll('" + tagName + "', this.checked, true);";

                out.print(
                        "<input type=\"checkbox\" onclick=\"" + onclickAllCheck + "\">");
                out.print("</th>\n");
                break;
            // ボタン
            case TAG_TYPE_BUTTON:
                out.print("<th");
                if (!StringUtil.isNullOrEmpty(style)) {
                    out.print(" style=\""+style + "\"");
                }
                out.print(">");
                out.print("</th>\n");
                break;
             // イメージパターン
            case TAG_TYPE_IMG_PATTERN:
                out.print("<th");
                if (!StringUtil.isNullOrEmpty(style)) {
                    out.print(" style=\""+style + "\"");
                }
                out.print(">");
				out.print(this.getDispColHeader(dictId, headInfo, mapColInfo, userLocale));
                out.print("</th>\n");
                break;
            // 非表示項目
            case TAG_TYPE_HIDDEN:
                out.print("<th style=\"display:none\">");
                out.print("</th>\n");
                break;
            default:
                // 折り返すため、nowrapを追加
                String styleClass = headInfo.get(INFO_CLASS);

                if (!StringUtil.isNullOrEmpty(styleClass)) {
                    out.print("<th class=\"" + styleClass + "\"");
                    if (!StringUtil.isNullOrEmpty(style)) {
                        out.print("\" style=\"" + style);
                    }
                    out.print(">");
                } else if (type.equals("date")) {
                    out.print("<th");
                    if (!StringUtil.isNullOrEmpty(style)) {
                        out.print(" style=\"" + style + "\"");
                    }
                    out.print(">");
                } else {
                    out.print("<th");
                    if (!StringUtil.isNullOrEmpty(style)) {
                        out.print(" style=\"" + style + "\"");
                    }
                    out.print(">");
                }

				out.print(this.getDispColHeader(dictId, headInfo, mapColInfo, userLocale));
                out.print("</th>\n");
                break;
            }
        }
        out.print("</tr>\n");
        out.print("</thead>\n");

        return lastDispIndex;

    }

	/**
	 * テーブルタイトルを返却する
	 *
	 * @param tableId    テーブルID
	 * @param headInfo   タイトル情報
	 * @param mapColInfo カラム情報
	 * @param userLocale ユーザーロケール
	 * @return テーブルタイトル
	 * @throws Exception エラー
	 */
	private String getDispColHeader(String tableId, Map<String, String> headInfo, Map<String, Object> mapColInfo,
			Locale userLocale) throws Exception {
		String colName = (String) mapColInfo.get(INFO_TAG_NAME);
		String customHeader = Optional.ofNullable(getBean()).map(BaseFormBean::getTableDispHeaders)
				.map(m -> m.get(tableId)).map(m -> m.get(colName)).orElse(null);
		if (StringUtils.isBlank(customHeader)) {
			if (StringUtils.isNotBlank(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID))) {
				String header = ResourcesHandler.getString(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID),
						userLocale);
				return getStringEscapeHtml(header);
			}
			return StringUtils.EMPTY;
		}
		return getStringEscapeHtml(customHeader);

	}

    /**
     * tbodyタグの出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param dict 辞書
     * @param info テーブ辞書情報
     * @param lstData 出力元データ
     * @param lastDispIndex 最終表示項目Index
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private void outBody(
            JspWriter out,
            Locale userLocale,
            GnomesCTagDictionary dict,
            List<Map<String, Object>> info,
            List<Object> lstData,
            int lastDispIndex) throws Exception {
        // 出力元データのクラス
        Class<?> clsRowData = null;

        out.print("<tbody>\n");

        if (lstData != null) {
            if (lstData.size() > 0) {
                clsRowData = lstData.get(0).getClass();
            }

            // テーブルデータを1行分取得
            for (int i = 0; i < lstData.size(); i++) {

                out.print("<tr>\n");

                Object objRowData = lstData.get(i);

                // 1列ずつ処理
                for (int j = 0; j < info.size(); j++) {
                    boolean isLastDisp;
                    Map<String, Object> mapCol = info.get(j);
                    if (j == lastDispIndex) {
                        isLastDisp = true;
                    } else {
                        isLastDisp = false;
                    }

                    Map<String, Object> mapColInfo = (Map<String, Object>) mapCol
                            .get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

                    Map<String, Object> headInfo = (Map<String, Object>) mapCol
                            .get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);

                    String type = (String) mapColInfo.get(INFO_TAG_TYPE);
                    String colStyle = (String) mapColInfo.get(INFO_STYLE);  // (追加)Columnのスタイル
                    String style = (String) headInfo.get(INFO_STYLE);

                    switch (type) {
                    // チェックボックス
                    case TAG_TYPE_CHECKBOX:
                        outCheckBox(out, style, colStyle, mapColInfo, clsRowData, objRowData, i,
                                isLastDisp);
                        break;
                    // ボタン
                    case TAG_TYPE_BUTTON:
                        outButton(out, userLocale, style, colStyle, mapColInfo, clsRowData,
                                objRowData, i, isLastDisp);
                        break;
                    // イメージパターン
                    case TAG_TYPE_IMG_PATTERN:
                        outImgPattern(out, userLocale, style, colStyle, mapColInfo, clsRowData,
                                objRowData, i, dict, isLastDisp);
                        break;
                    // ラベル（テキスト）
                    case TAG_TYPE_TEXT:
                        outText(out, dict, style, colStyle, mapColInfo, clsRowData, objRowData, i,
                                isLastDisp);
                        break;
                    // ラベル（数値）
                    case TAG_TYPE_NUMBER:
                        outNumber(out, dict, style, colStyle, mapColInfo, clsRowData, objRowData, i,
                                isLastDisp);
                        break;
                    // ラベル（日付）
                    case TAG_TYPE_DATE:
                    case TAG_TYPE_ZONEDDATETIME:
                        outDate(out, userLocale, style, colStyle, mapColInfo, clsRowData,
                                objRowData);
                        break;
                    // プルダウン（コード等定数）
                    case TAG_TYPE_PULLDOWN_CODE:
                        outPullDownConstant(out, style, colStyle, mapColInfo, clsRowData, objRowData, i, true);
                        break;
                    // プルダウン（コード等定数） 先頭空白無し
                    case TAG_TYPE_PULLDOWN_CODE_NO_SPACE:
                        outPullDownConstant(out, style, colStyle, mapColInfo, clsRowData, objRowData, i, false);
                        break;
                    // プルダウン（データ項目）
                    case TAG_TYPE_PULLDOWN_DATA:
                        outPullDownData(out, style, colStyle, mapColInfo, clsRowData, objRowData, i, true);
                        break;
                    // プルダウン（データ項目） 先頭空白無し
                    case TAG_TYPE_PULLDOWN_DATA_NO_SPACE:
                        outPullDownData(out, style, colStyle, mapColInfo, clsRowData, objRowData, i, false);
                        break;
                        /*
                    case TAG_TYPE_TEXTAREA:
                        outTextArea(out, dict, style, colStyle, mapColInfo, clsRowData, objRowData);
                        break;
                        */
                    // 非表示項目
                    case TAG_TYPE_HIDDEN:
                        outHidden(out, style, colStyle, mapColInfo, clsRowData, objRowData, i);
                        break;
                    // テキスト入力
                    case TAG_TYPE_INPUT_TEXT:
                        inputText(out, style, colStyle, mapColInfo, clsRowData, objRowData, i,
                                isLastDisp);
                        break;
                    // 数値入力
                    case TAG_TYPE_INPUT_NUMBER:
                        inputNumber(out, style, colStyle, mapColInfo, clsRowData, objRowData, i,
                                isLastDisp);
                        break;
                    // 日付入力
                    case TAG_TYPE_INPUT_DATE:
                    case TAG_TYPE_INPUT_ZONEDDATETIME:
                        inputDate(out, userLocale, style, colStyle, mapColInfo, clsRowData,
                                objRowData, i, isLastDisp);
                        break;
                        /*
                    case TAG_TYPE_INPUT_TEXTAREA:
                        inputTextArea(out, resBundle, dict, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp);
                        break;
                         */
                    case TAG_TYPE_INPUT_DATA_TYPE:
                        inputDataType(out, style, colStyle, mapColInfo, clsRowData,
                                objRowData, i, isLastDisp);
                        break;
                    case TAG_TYPE_OUTPUT_DATA_TYPE:
                        outputDataType(out, dict, style, colStyle, mapColInfo, clsRowData,
                                objRowData, i, isLastDisp);
                        break;
                    case TAG_TYPE_PROGRESS:
                        outputProgressBar(out,style, colStyle,mapColInfo, clsRowData, objRowData,i);
                    default:
                        break;

                    }
                }

                out.print("</tr>\n");

            }
        }
        out.print("</tbody>\n");
    }

// 現状想定されていないため、コメントアウト
//    /**
//     * テキストエリア入力
//     * @param out 出力先
//     * @param style
//     * @param mapColInfo 項目辞書
//     * @param clsData Beanクラス
//     * @param data Beanデータ
//     * @param rowCnt 行数
//     * @throws Exception 例外
//     */
//    private void inputTextArea(JspWriter out, ResourceBundle resBundle, GnomesCTagDictionary dict,
//            String style,String colStyle,//追加 Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData, int i, boolean isLastDisp) throws Exception {
//        String styleClass = (String) mapColInfo.get(INFO_CLASS);
//        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
//        String rows = (String) mapColInfo.get(INFO_ROWS);
//
//        String name = null;
//        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))){
//            name = (String) mapColInfo.get(INFO_TAG_NAME);
//        }
//
//        String strVal = "";
//        Object valueObj = responseContext.getResponseFormBean(objRowData,
//                paramName, i, name);
//
//
//        if (valueObj != null) {
//            strVal = valueObj.toString();
//        }
//
//        boolean hidden = false;
//        if (mapColInfo.containsKey(INFO_HIDDEN)) {
//            hidden = true;
//        }
//
//        if (!hidden) {
//            out.print("<td");
//            if (style != null) {
//                out.print(" style=\"" + style + "\"");
//            }
//            if (styleClass != null) {
//                out.print(" class=\"" + styleClass + "\"");
//            }
//            out.print(">\n");
//        } else {            out.print("<td style=\"display:none\">\n");
//        }
//
//        out.print("<textarea name=\"" + name + "\" rows=\"" + rows + "\" class=\"common-table-input-size-max");
//        if (styleClass != null) {
//            out.print(" " + styleClass + "\"");
//        }
//        out.print("\">\n");
//        out.print(strVal);
//        out.print("</textarea>\n");
//        out.print("</td>\n");
//
//    }



    /**
     * タグタイプ：表示形式をパラメータから決定
     * @param out 出力先
     * @param dict 辞書
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void outputDataType(JspWriter out,
            GnomesCTagDictionary dict,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i,
            boolean isLastDisp) throws Exception {

        // パラメータ名
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        String paramNames[] = infoParamName.split(",");

        // データタイプ取得
        Integer dataType = (Integer) getData(clsRowData, objRowData, paramNames[0]);

        String beanParamName;
        if (dataType == PARAM_DATA_TYPE_DIV_NUMBER || dataType == PARAM_DATA_TYPE_DIV_PULLDOWN || dataType == PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE) {
            beanParamName = paramNames[2];
        } else {
            beanParamName = paramNames[1];
        }

        // 日付時刻の場合
        if (isDataTypeDateTime(dataType)) {
            // データフォーマット
            String datePattern = gnomesCTagTableCommon.getDataTypeDatePattern(dataType);
            outDateBase(out, beanParamName, datePattern, style, colStyle, mapColInfo, clsRowData,
                    objRowData);
            return;
        }

        switch (dataType) {
            // 数値入力の場合
            case PARAM_DATA_TYPE_DIV_NUMBER:
                outNumberBase(out, beanParamName, dict, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp);
                return;
            // 二値(プルダウン)
            case PARAM_DATA_TYPE_DIV_PULLDOWN:
            case PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE:
                outputSelect(out, beanParamName, dict, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp);
                return;
            // 上記以外の場合
            default:
                // 文字入力の場合
                outTextBase(out, beanParamName, dict, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp);
                return;
        }

    }

    @SuppressWarnings("unchecked")
    private void outputSelect(JspWriter out,
            String beanParamName,
            GnomesCTagDictionary dict,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i,
            boolean isLastDisp) throws Exception {

        // プルダウン候補パラメータ名
        String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
        Class<?> clsBean = objRowData.getClass();

        // プルダウン候補パラメータ名からリストデータを取得
        List<Object> listValue = (List<Object>) getData(clsBean, objRowData, listParamName);

        // 選択値
        Object selValue = getData(clsBean, objRowData, beanParamName);

        // 表示文字
        String strVal = "";
        String[] outSel = new String[2];

        if (getSelect(listValue, selValue, outSel)) {
            strVal = outSel[0];
            if (outSel[1].length() > 0) {
                strVal = outSel[0] + " " + outSel[1];
            }
        }

/* getSelectにまとめ
        String compValue = null;
        if (selValue != null) {
            if (selValue instanceof BigDecimal) {
                compValue = String.valueOf(((BigDecimal)(selValue)).intValue());
            } else {
                compValue = selValue.toString();
            }
        }

        // プルダウン内のデータ生成
        if (listValue != null && listValue.size() > 0) {
            Class<?> lstClass = listValue.get(0).getClass();
            for (int l=0; l<listValue.size(); l++) {
                Object item = listValue.get(l);

                Object key = getData(lstClass, item, INFO_PULLDOWN_NAME);
                Object value = getData(lstClass, item, INFO_PULLDOWN_VALUE);

                if (compValue != null && compValue.compareTo(key.toString()) == 0) {
                    strVal = compValue + " " + value.toString();
                    break;
                }
            }
        }
*/
        outTextCommon(out, dict, strVal, style, colStyle,mapColInfo);
    }

    /**
     * タグタイプ：入力形式をパラメータから決定
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private void inputDataType(JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i, boolean isLastDisp) throws Exception {

        // パラメータ名
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        String paramNames[] = infoParamName.split(",");

        // データタイプ取得
        Integer dataType = (Integer) getData(clsRowData, objRowData, paramNames[0]);

        String beanParamName;
        if (dataType == PARAM_DATA_TYPE_DIV_NUMBER || dataType == PARAM_DATA_TYPE_DIV_PULLDOWN || dataType == PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE) {
            beanParamName = paramNames[2];
        } else {
            beanParamName = paramNames[1];
        }

        // 日付時刻の場合
        if (isDataTypeDateTime(dataType)) {
            // データフォーマット
            String datePattern = gnomesCTagTableCommon.getDataTypeDatePattern(dataType);
            inputDateBase(out, beanParamName, datePattern, style, colStyle, mapColInfo, clsRowData,
                    objRowData, i, isLastDisp);
            return;
        }

        switch (dataType) {
            // 数値入力の場合
            case PARAM_DATA_TYPE_DIV_NUMBER:
                inputNumberBase(out, beanParamName, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp,true);
                return;
            // 二値(プルダウン)
            case PARAM_DATA_TYPE_DIV_PULLDOWN:

                // 行データよりプルダウン候補リストを取得
                // プルダウン候補パラメータ名
                String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
                // プルダウン候補パラメータ名のデータをBeanから取得
                List<Object> listValue = (List<Object>) getData(clsRowData, objRowData, listParamName);

                outPullDownDataBase(
                        out,
                        listValue,
                        beanParamName,
                        beanParamName,
                        style,
                        colStyle,
                        mapColInfo,
                        clsRowData,
                        objRowData,
                        i,
                        true);
                return;
            // 二値(プルダウン) 先頭空白無し
            case PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE:

                // 行データよりプルダウン候補リストを取得
                // プルダウン候補パラメータ名
                String listParamName2 = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
                // プルダウン候補パラメータ名のデータをBeanから取得
                List<Object> listValue2 = (List<Object>) getData(clsRowData, objRowData, listParamName2);

                outPullDownDataBase(
                        out,
                        listValue2,
                        beanParamName,
                        beanParamName,
                        style,
                        colStyle,
                        mapColInfo,
                        clsRowData,
                        objRowData,
                        i,
                        false);
                return;

            // 上記以外の場合
            default:
                // 文字入力の場合
                inputTextBase(out, beanParamName, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp,true);
                return;
        }
    }

    /**
     * 日付入力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void inputDate(JspWriter out, Locale userLocale, String style,String colStyle, Map<String, Object> mapColInfo, Class<?> clsRowData,
            Object objRowData, int i, boolean isLastDisp) throws Exception {


        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // フォーマットリソースID
        String formatResourceId = (String) mapColInfo
                .get(INFO_FORMAT_RESOURCE_ID);
        // 日付パターン
        String datePattern = ResourcesHandler.getString(formatResourceId, userLocale);

        inputDateBase(out, paramName, datePattern, style, colStyle, mapColInfo, clsRowData,
                objRowData, i,isLastDisp);
    }

    /**
     * 日付入力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void inputDateBase(JspWriter out, String paramName, String datePattern, String style,String colStyle,Map<String, Object> mapColInfo, Class<?> clsRowData,
            Object objRowData, int i, boolean isLastDisp) throws Exception {
        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);
        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 表示データ
        String strDate = null;
        Object valueObj = responseContext.getResponseFormBean(objRowData,
                paramName, i, name);
        if (valueObj instanceof String) {
            strDate = (String) valueObj;
        } else {
            strDate = getStringDate(this.dictId, paramName, valueObj, datePattern);
        }
        if(strDate == null){
            strDate ="";
        }

        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        // 入力可能か否か
        String strReadOnly = "";
        String tabIndex = "";
        if (isInputReadOnlyParam(clsRowData, objRowData, paramName)) {
            strReadOnly = " readonly";
            tabIndex = " tabindex=\"-1\"";
        }

        // 入力値変更後の処理
        String onchange = getOnChange(mapColInfo);

        if (!hidden) {
            out.print("<td");
            if (!StringUtil.isNullOrEmpty(style)) {
                out.print(" style=\"" + style + "\"");
            }
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // 入力部分のhtml出力
        out.print("<div\n");
        if (!StringUtil.isNullOrEmpty(style)) {
            out.print(" style=\"" + style + "\"");
        }
        out.print(">\n");
        out.print("<input type=\"text\" class=\"datetime \"");
        if (name != null) {
            out.print(" name=\"" + name + "\"");
        }
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        // データフォーマットの取得
        String inputDatePattern = getStringDateFormatHtml(datePattern);
        out.print(" data-date-format=\"" + inputDatePattern + "\"");
        out.print(" value=\"" + getStringEscapeHtmlValue(strDate) + "\"" + strReadOnly + tabIndex + onchange);
        out.print(">\n");
        out.print("</div>\n");

        out.print("</td>\n");


    }



    /**
     * 数値入力
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void inputNumber(JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i, boolean isLastDisp) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
        inputNumberBase(out, paramName, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp,false);
    }

    /**
     * 数値入力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void inputNumberBase(JspWriter out,
            String paramName,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i, boolean isLastDisp,
            boolean isMultiType) throws Exception {

        // 表示データ
        String strNum = "";
        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);
        // 小数点桁数パラメータ名
        String decimalPointName = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_PARAM_NAME);
        // 小数点桁数（固定値）
        String decimalPointValue = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_VALUE);
        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }
        // 最大桁数
        String inputMaxLength = (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH);

        // 可変タイプ対応
        // 最大件数がカンマで区切られたら数値用と文字用に分ける
        // （カンマがなかったらそのままOUTとする)
        if(isMultiType){
            inputMaxLength = getMaxLengthMultiDigits(inputMaxLength);
        }

        // 最大入力文字数の設定
        String strMaxLength = "";
        if (!StringUtil.isNullOrEmpty(inputMaxLength)) {
            strMaxLength = " maxlength=\"" + inputMaxLength + "\"";
        }

         // 数値入力クラス
        String strInputClass = "";

         // 整数入力桁数
        String inputIntegerDigits = (String) mapColInfo.get(INFO_INPUT_INTEGER_DIGITS);
        // 小数点入力桁数
        String inputDecimalDigits = (String) mapColInfo.get(INFO_INPUT_DECIMAL_DIGITS);
        // カンマ区切りフォーマットを行うか否か
        String inputIsCommaFormat = (String) mapColInfo.get(INFO_INPUT_IS_COMMA_FORMAT);

        // 表示データを取得
        if (!StringUtil.isNullOrEmpty(paramName)
                && !StringUtil.isNullOrEmpty(name)) {

            Integer intObj = null;
            // 小数点桁数パラメータ名 の指定がある場合、Beanより小数点桁数を取得
            if (decimalPointName != null) {
                intObj = (Integer) (getData(clsRowData, objRowData,
                        decimalPointName));
            }
            // 小数点桁数の指定がある場合、小数点桁数を取得
            else if (decimalPointValue != null){
                intObj = Integer.valueOf(decimalPointValue);
            }
            // 小数点入力桁数の指定がある場合、小数点入力桁数を小数点桁数とする
            else if (!StringUtil.isNullOrEmpty(inputDecimalDigits)) {
                intObj = Integer.valueOf(inputDecimalDigits);
            }

            // 小数点桁数の設定がある場合
            if (intObj != null) {
            	inputDecimalDigits = Integer.toString(intObj);
            }

            Object valueObj = responseContext.getResponseFormBean(objRowData,
                    paramName, i, name);
            if (valueObj instanceof String) {
                strNum = (String) valueObj;
            } else {
                strNum = getStringNumber(this.dictId, paramName, valueObj, intObj);
            }
        }

        // 桁数設定がある場合
        if (!StringUtil.isNullOrEmpty(inputIntegerDigits) || !StringUtil.isNullOrEmpty(inputDecimalDigits)) {
            // フォーカスアウト時に桁数をチェック。入力桁数を超えた場合は削除される。
            strInputClass = strInputClass + " number-digit-check";
        }

        // 整数入力桁数の設定
        String strIntegerDigits = "";
        if (!StringUtil.isNullOrEmpty(inputIntegerDigits)) {
            strIntegerDigits = " data-INTEGER-DIGITS=\"" + inputIntegerDigits + "\"";
        }

        // 小数点入力桁数の設定
        String strDecimalDigits = "";
        if (!StringUtil.isNullOrEmpty(inputDecimalDigits)) {
            strDecimalDigits = " data-DECIMAL-DIGITS=\"" + inputDecimalDigits + "\"";
        }

        // カンマ区切りを行う場合
        if (!StringUtil.isNullOrEmpty(inputIsCommaFormat)) {
            strInputClass = strInputClass + " gnomes-number-format";
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        // 入力可能か否か
        String strReadOnly = "";
        String tabIndex = "";
        if (isInputReadOnlyParam(clsRowData, objRowData, paramName)) {
            // キーボードを出力しない
            strInputClass = "";
            strReadOnly = " readonly";
            tabIndex = " tabindex=\"-1\"";
        }

        if (!hidden) {
            out.print("<td");
            if (!StringUtil.isNullOrEmpty(style)) {
                out.print(" style=\"" + style + "\"");
            }
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // 入力値変更後の処理
        String onchange = getOnChange(mapColInfo);

        // 入力部分のhtml出力
        out.print("<div\n");
        if (!StringUtil.isNullOrEmpty(style)) {
            out.print(" style=\"" + style + "\"");
        }
        out.print(">\n");
        out.print("<input class=\"common-text-number " + strInputClass + "\"" + strMaxLength + strIntegerDigits + strDecimalDigits);
        if (name != null) {
            out.print(" name=\"" + name + "\"");
            out.print(" id=\"" + dictId + "_" +  name + "_" + i + "\"");
        }
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        out.print(" value=\"" + getStringEscapeHtmlValue(strNum) + "\"" + strReadOnly + tabIndex + onchange + ">\n");
        out.print("</div>\n");
        out.print("</td>\n");

    }

    /**
     * テキスト入力
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void inputText(JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i,
            boolean isLastDisp) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        inputTextBase(out, paramName, style, colStyle, mapColInfo, clsRowData, objRowData, i, isLastDisp,false);
    }

    /**
     * テキスト入力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @param isLastDisp 最終表示項目判定
     * @throws Exception 例外
     */
    private void inputTextBase(JspWriter out,
            String paramName,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            int i,
            boolean isLastDisp,
            boolean isMultiType) throws Exception {


        // スタイルクラス(テキストのみ","でtd と input に分ける
        String[] styleClasses = getColumnClasses((String) mapColInfo.get(INFO_CLASS));

        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 表示データ
        String strVal = "";
        Object valueObj = responseContext.getResponseFormBean(objRowData,
                paramName, i, name);

        // 最大桁数
        String inputMaxLength = (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH);

        //可変型データタイプの対応
        // 最大件数がカンマで区切られたら数値用と文字用に分け、文字用として切り出す
        // （カンマがなかったらそのままOUTとする)
        if(isMultiType){
            inputMaxLength = getMaxLengthMultiString(inputMaxLength);
        }

        // 最大入力文字数の設定
        String strMaxLength = "";
        if (!StringUtil.isNullOrEmpty(inputMaxLength)) {
            strMaxLength = " maxlength=\"" + inputMaxLength + "\"";
        }

        if (valueObj != null) {
            strVal = valueObj.toString();
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td");
            if (!StringUtil.isNullOrEmpty(style)) {
                out.print(" style=\"" + style + "\"");
            }
            this.outColumnClasses(out, styleClasses[COLUMN_CLASS_INDEX_TD]);
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // 入力可能か否か
        String strReadOnly = "";
        String tabIndex = "";
        if (isInputReadOnlyParam(clsRowData, objRowData, paramName)) {
            strReadOnly = " readonly";
            tabIndex = " tabindex=\"-1\"";
        }

        // 入力値変更後の処理
        String onchange = getOnChange(mapColInfo);

        // 入力部分のhtml出力
        out.print("<div\n");
        if (!StringUtil.isNullOrEmpty(style)) {
            out.print(" style=\"" + style + "\"");
        }
        out.print(">\n");
        out.print("<input type=\"text\"");
        if (name != null) {
            out.print(" name=\"" + name + "\"");
        }
        //個別クラスの出力
        this.outColumnClasses(out, styleClasses[COLUMN_CLASS_INDEX_INNER]);
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        out.print(strMaxLength + " value=\""+getStringEscapeHtmlValue(strVal)+"\"" + strReadOnly + tabIndex + onchange + ">\n");
        out.print("</div>\n");
        out.print("</td>\n");

    }

    /**
    *
    * プログレスバーの出力
    *
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @throws Exception 例外
    */
   @SuppressWarnings("unchecked")
   private void outputProgressBar(
           JspWriter out,
           String style,String colStyle,//追加
           Map<String, Object> mapColInfo,
           Class<?> clsData,
           Object data,
           int rowCnt) throws Exception
   {

       // パラメータ名(１つ以上の","で区切られた値が格納されているパラメータ名)
       String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
       // スタイルクラス
       String styleClass = (String) mapColInfo.get(INFO_CLASS);

       if (StringUtil.isNullOrEmpty(infoParamName)) {
           infoParamName = "";
       }
       String paramNames[] = infoParamName.split(",");

       // html出力
       out.print("<td><div");

       if (styleClass != null) {
           out.print(" class=\"" + styleClass + "\"");
       }
       //個別スタイルの出力
       this.outColumnStyles(out, style, colStyle);

       out.print(">\n");


       //パラメータのカンマで区切った値を順番に出力する
       for(String paramName:paramNames){

           // データ（非表示項目）
           Object value = getData(clsData, data, paramName);
           String objStr = value.toString();
           String[] intStr = objStr.split("\\.");
           Integer intValue;

           try {
               intValue = Integer.parseInt(intStr[0]);
           }
           catch(NumberFormatException ex){
               throw ex;
           }
           catch(Exception ex){
               throw ex;
           }

           if(intValue >= 0){
               //どの型でも％として整数にする（マイナス値はないとする）
               String formatedValue = String.format("%3d", intValue);
               out.print(formatedValue + "%");
           }
           else {
               //マイナス値は"-"にする
               out.print("&nbsp;&nbsp;&nbsp;-");
           }

           out.print("<progress value=\"" + intValue + "\" max=\"100\"");
           out.print("></progress><br />");

       }
       out.print("</div></td>\n");
   }
    /**
     * 非表示出力
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outHidden(
            JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt) throws Exception {
        // タグ名
        String name = (String) mapColInfo.get(INFO_TAG_NAME);
        // パラメータ名
        String valueName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // データ（非表示項目）
        Object value = getData(clsData, data, valueName);

        // html出力
        out.print("<td style=\"display:none\">\n");
        out.print("    <input type=\"hidden\" name=\"" + name
                + "\" value=\"" + getStringEscapeHtmlValue(String.valueOf(value))
                + "\"/>");
        out.print("</td>\n");

    }

    /**
     * チェックボックス出力
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outCheckBox(
            JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean isLastDispCol) throws Exception {

        // チェック状態
        String strChecked = "";
        // パラメータ名
        String valueName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // タグ名
        String name = null;
        // onclick
        String onclick = (String) mapColInfo.get(INFO_ON_CLICK);
        // チェックボックスクラス
        String checkboxclass = (String) mapColInfo.get(INFO_CLASS);

        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 表示データ
        if (!StringUtil.isNullOrEmpty(valueName)
                && !StringUtil.isNullOrEmpty(name)) {
            Object valueObj = responseContext.getResponseCheckBoxFormBean(data,
                    valueName, rowCnt, name);

            if (valueObj != null) {
                if (valueObj instanceof Integer) {
                    if (((Integer) valueObj)
                            .intValue() == CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE) {
                        strChecked = "checked";
                    }
                }
            }
        }

        String strOnclick = "";
        // onclickが設定されている場合
        if (!StringUtil.isNullOrEmpty(onclick)) {
            String onclicks[] = onclick.split(",");
            if (onclicks.length >= 1 && !StringUtil.isNullOrEmpty(onclicks[0])) {
                strOnclick += "if(this.checked){ " + onclicks[0] + " };";
            }
            if (onclicks.length == 2 && !StringUtil.isNullOrEmpty(onclicks[1])) {
                strOnclick += "if(!this.checked){ " + onclicks[1] + " };";
            }
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td class=\"common-text-center");
            // チェックボックスクラスの設定
            if (!StringUtil.isNullOrEmpty(checkboxclass)) {
                out.append(" " + checkboxclass);
            }
            out.print("\"");

            if (style != null) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td class=\"common-text-center\" style=\"display:none\">\n");
        }
        // チェックボックスのhtml出力
        out.print("<input type=\"checkbox\" name=\"" + name + "\"");
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        out.print(" value=\"" + String.valueOf(rowCnt) + "\" onclick=\"" + strOnclick + "\" " + strChecked + ">\n");
        out.print("</td>\n");
    }

    /**
     * ボタン出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outButton(
            JspWriter out,
            Locale userLocale,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean isLastDispCol) throws Exception {
        // ボタンクラス
        String btnclass = (String) mapColInfo.get(INFO_CLASS);
        // onclick
        String onclick = (String) mapColInfo.get(INFO_ON_CLICK);
        // イメージソース
        String imgsrc = (String) mapColInfo.get(INFO_IMG_SRC);
        // タイトルリソースID
        String titleResouceId = (String) mapColInfo.get(INFO_TITLE);
        // スタイル指定
        String dataStyle = (String) mapColInfo.get(INFO_STYLE);
        // タイトル
        String title = "";
        String titleResouce = "";

        // タイトルリソースIDからタイトルを取得
        if (titleResouceId != null) {
            titleResouce = this.getStringEscapeHtml(ResourcesHandler.getString(titleResouceId, userLocale));
            title = " title=\"" + titleResouce + "\"";
        }

        // ボタンクラスの設定
        if (StringUtil.isNullOrEmpty(btnclass)) {
            btnclass = "";
        }

        // スタイルの設定
        if (StringUtil.isNullOrEmpty(dataStyle)) {
            dataStyle = "";
        }

        // パラメータ名(表示非表示データを格納されたパラメータ名, 押したボタンの行数を格納するパラメータ名)
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        if (StringUtil.isNullOrEmpty(infoParamName)) {
            infoParamName = "";
        }
        String paramNames[] = infoParamName.split(",");

        // ボタン場合、ボタンようなonclick event
        String dicId = this.dictId;
        String prefix = null;

        // eg..OPC000_PH.orderList --> OPC000_PH のように、"."の左を得る
        // 配列数2であることが前提。違う場合はそのままの値にする
        // 画面IDにドット.が無いことが前提
        String[] prefixs = dicId.split("\\.");

        if(prefixs.length >= 2) {
            prefix = prefixs[0];
        }
        else {
            prefix = dicId;
        }

        String tagName = (String) mapColInfo.get(INFO_TAG_NAME);
        // 項目がある場合
        if(tagName != null) {
        	 // ボタン辞書Id
        	String buttonDicId = prefix + "." + tagName;
        	// ボタンの値を取得
        	onclick = gnomesCTagButtonCommon.getOnclickButtonAttribute(this.bean,buttonDicId);
        }

        // 列すべて非表示項目か否か
        boolean allHidden = false;

        // 非表示項目か否か
        Boolean hidden = false;
        String hiddenStyle = "";
        // 非表示項目か否かが設定されている場合、列すべて非表示
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            allHidden = true;
        }
        // パラメータ名より表示非表示の切替
        else if (!StringUtil.isNullOrEmpty(paramNames[0])) {
            hidden = ConverterUtils.IntTobool((Integer)getData(clsData, data, paramNames[0]));
            // その項目が非表示の場合
            if (hidden) {
                hiddenStyle = " display: none;";
            }
        }

        // onclickに何行目が押されたかどうかをparam_nameに設定されたパラメータに設定
        if (paramNames.length >= 2 && !StringUtil.isNullOrEmpty(paramNames[1])) {
            rowCnt = rowCnt + 1;
            onclick = "setInputValue('" + paramNames[1] + "', "+ rowCnt + ");" + onclick;
        }

        //-------------------------------------------------------
        // 2022/06/14 追加　
        //  ボタンに表示する文字を取得して出力する
        //  paramNameの3番目の値に入っているBean変数名を使用する
        //  設定が無い場合は通常通りイメージが出力される
        //  例）<param_name>inputNumber,inputNumber,itemButton</param_name>
        //   DTOのitemButtonの値(String限定）で使用される
        //-------------------------------------------------------
        String buttonLabelString=null;
        if(paramNames.length == 3 && !StringUtil.isNullOrEmpty(paramNames[2])) {
            Object value = getData(clsData, data, paramNames[2]);
            if (value != null) {
                buttonLabelString = value.toString();
            }
        }


        if (!allHidden) {
            out.print("<td class=\"common-text-center\"");
            if (style != null) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td class=\"common-text-center\" style=\"display:none\">\n");
        }


        // ボタン部分のhtml出力
        out.print("        <div class=\"" + btnclass + "\" style=\"" + dataStyle + hiddenStyle
                + "\"" + title + " onclick=\"" + onclick
                + "\">\n");

        // ボタンにラベルが指定されている場合は、イメージは出力せず
        // 指定したラベルが出力される
        if(StringUtils.isEmpty(buttonLabelString)) {
            out.print("<img alt=\"\" src=\"" + imgsrc + "\">\n");
        }
        else {
            out.print(buttonLabelString);
        }
        out.print("</div>\n");
        out.print("</td>\n");
    }

    /**
     * 画像パターン出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param dict 辞書
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outImgPattern(
            JspWriter out,
            Locale userLocale,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            GnomesCTagDictionary dict,
            boolean isLastDispCol) throws Exception {

        // パターンID
        String patternId = (String) mapColInfo.get(INFO_PATTERN_ID);
        // イメージクラス
        String imgclass = (String) mapColInfo.get(INFO_BUTTON_CLASS);

        // パラメータ名(パターン値が格納されたパラメータ名, 押したボタンの行数を格納するパラメータ名)
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        if (StringUtil.isNullOrEmpty(infoParamName)) {
            infoParamName = "";
        }

        String paramNames[] = infoParamName.split(",");

        Object value = getData(clsData, data, paramNames[0]);
        // アイコンパス
        String iconPath = getPatternValue(dict, patternId, value.toString());

        // onclick
        String onclick = (String) mapColInfo.get(INFO_ON_CLICK);
        if (StringUtil.isNullOrEmpty(onclick)) {
            onclick = "";
        }

        // onclickに何行目が押されたかどうかをparam_nameに設定されたパラメータに設定
        if (paramNames.length == 2 && !StringUtil.isNullOrEmpty(paramNames[1])) {
            rowCnt = rowCnt + 1;
            onclick = "setInputValue('" + paramNames[1] + "', "+ rowCnt + ");" + onclick;
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td class=\"common-text-center\"");
            if (!StringUtil.isNullOrEmpty(style)) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td class=\"common-text-center\" style=\"display:none\">\n");
        }

        String buttonTag = "";
        // onclickが設定されている場合、ボタン要素を追加
        if (!StringUtil.isNullOrEmpty(onclick)) {
            buttonTag = " onclick=\"" + onclick + "\" style=\"cursor: pointer;\"";
        }
        //String altResId =
        if (!StringUtil.isNullOrEmpty(iconPath)) {
            // イメージ部分のhtml出力
            out.print("<img src=\""+ iconPath + "\" alt=\"\"" + buttonTag);
            if (imgclass != null) {
                out.print(" class=\""+imgclass+"\"");
            }
            //個別スタイルの出力
            this.outColumnStyles(out, style, colStyle);
            out.print(">\n");
        }
        out.print("</td>\n");
    }

    /**
     * テキスト出力
     * @param out 出力先
     * @param dict 辞書
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outText(
            JspWriter out,
            GnomesCTagDictionary dict,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean isLastDispCol) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        outTextBase(out, paramName, dict, style, colStyle, mapColInfo, clsData, data, rowCnt, isLastDispCol);

    }

    /**
     * テキスト出力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param dict 辞書
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outTextBase(
            JspWriter out,
            String paramName,
            GnomesCTagDictionary dict,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean isLastDispCol) throws Exception {

        // 値の取得
        String strVal = "";
        Object value = getData(clsData, data, paramName);
        if (value != null) {
            strVal = value.toString();
        }

        outTextCommon(out, dict, strVal, style,colStyle, mapColInfo);

    }

    /**
     * テキスト出力
     * @param out 出力先
     * @param dict 辞書
     * @param strVal 表示データ
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @throws Exception
     */
    private void outTextCommon(
            JspWriter out,
            GnomesCTagDictionary dict,
            String strVal,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo) throws Exception {

        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);

        // パターンID
        String patternId = (String) mapColInfo.get(INFO_PATTERN_ID);
        String patternClass = "";
        // パターンIDが指定されている場合、表示値をキーにスタイルクラスを取得
        if(!StringUtil.isNullOrEmpty(patternId)) {
            patternClass = getPatternValue(dict, patternId, strVal);
        }

        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td");
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            if (style != null) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // テキスト部分のhtml出力
        out.print("<div class=\"" + patternClass + "\"");
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        if (name != null) {
            out.print(" name=\"" + name + "\"");
        }
        out.print(">"+ getStringEscapeHtml(strVal) + "</div>");
        out.print("</td>\n");
    }


    /**
     * 数値出力
     * @param out 出力先
     * @param dict 辞書
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outNumber(
            JspWriter out,
            GnomesCTagDictionary dict,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean isLastDispCol) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
        outNumberBase(out, paramName, dict, style, colStyle, mapColInfo, clsData, data, rowCnt, isLastDispCol);
    }


    /**
     * 数値出力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param dict 辞書
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param isLastDispCol 最終表示項目判定
     * @throws Exception 例外
     */
    private void outNumberBase(
            JspWriter out,
            String paramName,
            GnomesCTagDictionary dict,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean isLastDispCol) throws Exception {
        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);
        // 小数点桁数パラメータ名
        String decimalPointName = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_PARAM_NAME);
        // 小数点桁数（固定値）
        String decimalPointValue = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_VALUE);
        Object valueObj = getData(clsData, data, paramName);
        // 表示データ
        String strNum = "";
        Integer intObj = null;

        // 小数点桁数パラメータ名の指定がある場合にBeanより桁数を取得する
        if (decimalPointName != null) {
            intObj = (Integer) (getData(clsData, data, decimalPointName));
        }
        // 小数点桁数（固定値）の指定がある場合に桁数を取得する
        else if (decimalPointValue != null) {
            intObj = Integer.valueOf(decimalPointValue);
        }
        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 表示データを取得
        strNum = this.getStringEscapeHtmlValue(getStringNumber(this.dictId, paramName, valueObj, intObj));

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        // パターンID
        String patternId = (String) mapColInfo.get(INFO_PATTERN_ID);
        String patternClass = "";
        // パターンIDが指定されている場合、表示値をキーにスタイルクラスを取得
        if(!StringUtil.isNullOrEmpty(patternId)) {
            patternClass = getPatternValue(dict, patternId, getStringNumber(this.dictId, paramName, valueObj, null));
        }

        if (!hidden) {
            out.print("<td");
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            if (style != null) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // 数値部分のhtml出力
        out.print("<div class=\"common-text-number " + patternClass + "\"");
        if (name != null) {
            out.print(" name=\""+ name + "\"");
        }
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        out.print(">"+ strNum + "</div>");
        out.print("</td>\n");

    }

    /**
     * 日付出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @throws Exception 例外
     */
    private void outDate(
            JspWriter out,
            Locale userLocale,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data) throws Exception {


        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // フォーマットリソースID
        String formatResourceId = (String) mapColInfo
                .get(INFO_FORMAT_RESOURCE_ID);
        // 日付パターン
        String datePattern = ResourcesHandler.getString(formatResourceId, userLocale);

        outDateBase(
                out,
                paramName,
                datePattern,
                style,
                colStyle,
                mapColInfo,
                clsData,
                data);
    }

    /**
     * 日付出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @throws Exception 例外
     */
    private void outDateBase(
            JspWriter out,
            String paramName,
            String datePattern,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data) throws Exception {
        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);
        Object valueObj = getData(clsData, data, paramName);

        // 表示データの取得
        String strDate = this.getStringEscapeHtmlValue(getStringDate(this.dictId, paramName, valueObj, datePattern));

        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td");
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            if (style != null) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // 日付のhtml出力
        out.print("<div");
        if (name != null) {
            out.print(" name=\"" + name + "\"");
        }
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        out.print(">" + strDate + "</div>\n");
        out.print("</td>\n");
    }

    /**
     * プルダウン出力（コード等定数）
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param defaultSpace 先頭空白有無
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private void outPullDownConstant(
            JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean defaultSpace) throws Exception {

        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);
        Class<?> clsBean = bean.getClass();

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // FormBeanから取得するBean名取得
        String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);

        // FormBeanからプルダウンを取得
        List<Object> listValue = (List<Object>) getData(clsBean, this.bean, listParamName);

        // FormBeanからプルダウン選択状態を取得
        String selectParamName = (String)mapColInfo.get(INFO_SELECT_PARAM_NAME);
        if (StringUtil.isNullOrEmpty(selectParamName)) {
            selectParamName = "";
        }
        // 選択値パラメータ名と差し替え用選択名パラメータ名を分割
        String selectParamNames[] = selectParamName.split(",");

        Object valueObj = null;
        // プルダウン選択項目値を取得
        if (selectParamNames.length >= 1 && !StringUtil.isNullOrEmpty(selectParamNames[0])) {
            valueObj = responseContext.getResponseFormBean(data,
                    selectParamNames[0], rowCnt, name);
        }

        // プルダウン選択項目値
        String selValue = "";

        String[] outSel = new String[2];
        // プルダウン選択項目値が候補リストに存在する場合、選択値を設定
        if (getSelect(listValue, valueObj, outSel)) {
            selValue = outSel[0];
        }
        // プルダウン選択項目名
        String selName = "";

        // 差し替え用選択名パラメータ名が設定されている場合、差し替え用プルダウン選択項目名を取得
        if (selectParamNames.length == 2 && !StringUtil.isNullOrEmpty(selectParamNames[1])) {
            Object nameObj = responseContext.getResponseFormBean(data,
                    selectParamNames[1], rowCnt, name);
            if (nameObj != null && !StringUtil.isNullOrEmpty(selValue)) {
                selName = nameObj.toString();
            }
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td");
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            if (!StringUtil.isNullOrEmpty(style)) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // readonlyの場合、選択データを隠し項目として出力し、プルダウン部はdisabledとして表示
        // 入力可能か否か
        String strDisabled = "";
        if (isInputReadOnlyParam(clsData, data, paramName)) {
            out.print("<input type=\"hidden\" name=\""+ name + "\" value=\"" + selValue + "\">\n");
            strDisabled = " " + INPUT_DISABLED;
            name = name + "_pulldown";// リネーム
        }

        // 入力値変更後の処理
        String onchange = getOnChange(mapColInfo);

        // プルダウン部分のhtml出力
        out.print("<select class=\"common-data-input-size-item-code");
        if (styleClass != null) {
            out.print(" " + styleClass);
        }
        if(name != null) {
            out.print("\" name=\"" + name + "\"");
        }
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        out.print(strDisabled + onchange + ">\n");

        // 先頭空白有りの場合
        if (defaultSpace) {
            out.print("<option value=\"\"></option>\n");
        }
        // プルダウン内のデータ生成
        outSelectOption(out, listValue, valueObj, selName);

        out.print("</select>\n");
        out.print("</td>\n");

    }

    /**
     * プルダウン出力（データ項目）
     * @param out 出力先
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param defaultSpace 先頭空白有無
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private void outPullDownData(
            JspWriter out,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean defaultSpace) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // FormBeanからプルダウン選択状態を取得
        String selectParamName = (String)mapColInfo.get(INFO_SELECT_PARAM_NAME);

        // プルダウン候補パラメータ名
        String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
        Class<?> clsBean = bean.getClass();

        // プルダウン候補パラメータ名からリストデータを取得
        List<Object> listValue = (List<Object>) getData(clsBean, this.bean, listParamName);

        outPullDownDataBase(
                out,
                listValue,
                selectParamName,
                paramName,
                style,
                colStyle,
                mapColInfo,
                clsData,
                data,
                rowCnt,
                defaultSpace);

    }

    /**
     * プルダウン出力（データ項目）
     * @param out 出力先
     * @param listValue プルダウン候補
     * @param selectParamName 選択値パラメータ名
     * @param paramName 読取フラグ確認用パラメータ名
     * @param style スタイル
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param rowCnt 行数
     * @param defaultSpace 先頭空白有無
     * @throws Exception 例外
     */
    private void outPullDownDataBase(
            JspWriter out,
            List<Object> listValue,
            String selectParamName,
            String paramName,
            String style,String colStyle,//追加
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            int rowCnt,
            boolean defaultSpace) throws Exception {

        // スタイルクラス
        String styleClass = (String) mapColInfo.get(INFO_CLASS);
        // プルダウンリストID
        //String listId = String.format("%s_%d", (String)mapColInfo.get(INFO_LIST_ID), rowCnt);
        // オートコンプリート
        String autoComplite = (String)mapColInfo.get(INFO_LIST_AUTOCOMPLETE);

        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        if (StringUtil.isNullOrEmpty(selectParamName)) {
            selectParamName = "";
        }

        // 選択値パラメータ名と差し替え用選択名パラメータ名を分割
        String selectParamNames[] = selectParamName.split(",");
        Object valueObj = null;
        // プルダウン選択項目値を取得
        if (selectParamNames.length >= 1 && !StringUtil.isNullOrEmpty(selectParamNames[0])) {
            valueObj = responseContext.getResponseFormBean(data,
                    selectParamNames[0], rowCnt, name);
        }

        // プルダウン選択項目値を取得
        String selValue = "";

        String[] outSel = new String[2];
        // プルダウン選択項目値が候補リストに存在する場合、選択値を設定
        if (getSelect(listValue, valueObj, outSel)) {
            selValue = outSel[0];
        }

        // プルダウン選択項目名
        String selName = "";

        // 差し替え用選択名パラメータ名が設定されている場合、差し替え用プルダウン選択項目名を取得
        if (selectParamNames.length == 2 && !StringUtil.isNullOrEmpty(selectParamNames[1])) {
            Object nameObj = responseContext.getResponseFormBean(data,
                    selectParamNames[1], rowCnt, name);
            if (nameObj != null && !StringUtil.isNullOrEmpty(selValue)) {
                selName = nameObj.toString();
            }
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td");
            if (styleClass != null) {
                out.print(" class=\"" + styleClass + "\"");
            }
            if (!StringUtil.isNullOrEmpty(style)) {
                out.print(" style=\"" + style + "\"");
            }
            out.print(">\n");
        } else {
            out.print("<td style=\"display:none\">\n");
        }

        // readonlyの場合、選択データを隠し項目として出力し、プルダウン部はdisabledとして表示
        // 入力可能か否か
        String strDisabled = "";
        if (isInputReadOnlyParam(clsData, data, paramName)) {
            out.print("<input type=\"hidden\" name=\""+ name + "\" value=\"" + selValue + "\">\n");
            strDisabled = " " + INPUT_DISABLED;
            name = name + "_pulldown";// リネーム
        }

        String mode = null;
        if ( !StringUtil.isNullOrEmpty(autoComplite) ){

            mode = autoComplite;
            if (!mode.equals(AUTO_MODE_VALUE) && !mode.equals(AUTO_MODE_TEXT)) {
                mode = AUTO_MODE_VALUE_TEXT;
            }
        }

        // 入力値変更後の処理
        String strOnchange = getOnChange(mapColInfo);

        // プルダウン部分のhtml出力
        out.print("<select class=\"common-data-input-size-item-code");
        if (styleClass != null) {
            out.print(" " + styleClass);
        }
        if (mode != null) {
            out.print(" " + CLASS_AUTO_COMBO);
        }
        if(name != null) {
            out.print("\" name=\"" + name + "\"");
        }
        //個別スタイルの出力
        this.outColumnStyles(out, style, colStyle);

        if (mode != null) {
            out.print(" data-mode=\"" + mode + "\" ");
        }
        out.print(strDisabled + strOnchange + ">\n");

        // 先頭空白有りの場合
        if (defaultSpace) {
            out.print("<option value=\"\"></option>\n");
        }

        // プルダウン内のデータ生成
        outSelectOption(out, listValue, valueObj, selName);

        out.print("</select>\n");
        out.print("</td>\n");

    }

    /**
     * スタイルの個別設定
     *  テーブルのカラムに内包される個々のコンポーネントに対し、ヘッダスタイル(hedaerStyle)を
     *  適用するか、カラムスタイル（colStyle)を適用するか判断し出力する
     *  カラムスタイル ＞ヘッダスタイル＞設定しない　の優先順位
     * @param headerStyle ヘッダのスタイル
     * @param colStyle カラムのスタイル
     */
    private void outColumnStyles(JspWriter out,String headerStyle,String colStyle) throws Exception
    {
        if (!StringUtil.isNullOrEmpty(colStyle)) {
            out.print(" style=\"" + colStyle + "\"");
            return;
        }
        if (!StringUtil.isNullOrEmpty(headerStyle)) {
            out.print(" style=\"" + headerStyle + "\"");
            return;
        }
    }
    /**
     * Classの個別設定
     * 　テーブルのカラムに設定しているクラス定義を出力する
     *
     * @param out ライター
     * @param colClass カラムクラスの設定値(nullまたは""の場合は出力しない)
     * @throws Exception
     */
    private void outColumnClasses(JspWriter out,String colClass) throws Exception
    {
        if (!StringUtil.isNullOrEmpty(colClass)) {
            out.print(" class=\"" + colClass + "\"");
            return;
        }
    }
    /**
     * カラムクラスをカンマで区切った時にそれぞれのclass文字に変更する
     * @param colClasses カンマで区切られたクラスまたは、nullまたは""
     * @return 必ず2つの配列が返る。colClassesがnull or "" の場合は2つの配列のnullが返る
     */
    private static final int COLUMN_CLASS_MAXNUM = 2;
    private static final int COLUMN_CLASS_INDEX_TD = 0;
    private static final int COLUMN_CLASS_INDEX_INNER = 1;
    private String[] getColumnClasses(String colClasses)
    {
        String[] returnVal = null;
        if(! StringUtil.isNullOrEmpty(colClasses)){
            String[] classes = colClasses.split(",");
            if(classes.length == COLUMN_CLASS_MAXNUM){
                returnVal = classes;
            }
            else {
                returnVal  = new String[COLUMN_CLASS_MAXNUM];
                for(int i=0;i<COLUMN_CLASS_MAXNUM;i++) {
                    if(i < classes.length){
                        returnVal[i] = classes[i];
                    }
                    else {
                        returnVal[i] = null;
                    }
                }
            }
        }
        else {
            returnVal = new String[COLUMN_CLASS_MAXNUM];
            for(int i=0;i<COLUMN_CLASS_MAXNUM;i++) {
                returnVal[i] = null;
            }
        }
        return returnVal;
    }

    /**
     * onChange時の処理を生成する
     * @param mapColInfo 項目辞書
     * @return onChange処理
     */
    private String getOnChange(Map<String, Object> mapColInfo) throws Exception {
        // 入力値変更後の処理
        // 出力元データのクラス
        Class<?> clsBean = bean.getClass();
        String onchange = "";
        if(!StringUtil.isNullOrEmpty((String)mapColInfo.get(INFO_ON_CHANGE_EVENT))){
            onchange = " onchange=\"" + SET_WARMING_FLAG + (String)mapColInfo.get(INFO_ON_CHANGE_EVENT) + "\"";
        }
        else if(!StringUtil.isNullOrEmpty((String)mapColInfo.get(INFO_ON_CHANGE_COMMAND_ID))){
            onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf((String)mapColInfo.get(INFO_ON_CHANGE_COMMAND_ID))) + "\"";

        }
        else if(!StringUtil.isNullOrEmpty((String)mapColInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID))){
            Object commandId = getData(clsBean, bean, (String)mapColInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID));
            if (commandId != null) {
                onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(commandId)) + "\"";
            } else {
                onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            }
        } else {
             onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
        }
        return onchange;
    }

    /**
     * MaxLengthが"40,20"などカンマで区切られた場合、2番目の20の所をDigit系のMaxLength値とする
     * カンマがなかったら数値型Lengthを指定していないとしnullを返す
     *
     * @param maxLengthString   カンマで区切られたMaxLength値
     * @return カンマの左側の値（文字）
     */
    private String getMaxLengthMultiDigits(String maxLengthString)
    {
        //nullチェック nullか空白はチェックしない
        if(StringUtil.isNullOrEmpty(maxLengthString)){
            return maxLengthString;
        }
        // ","で区切り
        String[] maxLengths = maxLengthString.split(",");
        // ","がない。つまり区切られていない場合は固定列の対応とし
        // maxLengthStringの値をそのまま返す
        if(maxLengths.length == 1){
            return null;
        }
        //カンマがたくさんあっても2番目の数値を対応する
        if(maxLengths.length > 1){
            return maxLengths[1];
        }
        else {
            // MaxLength指定のフォーマットがおかしい
            String errmsg = String.format("maxLengthString is invalid format param value = [%s]", maxLengthString);
            logger.severe(errmsg);
            throw new IllegalArgumentException(errmsg);
        }
    }
    /**
     * MaxLengthが"40,20"などカンマで区切られた場合、1番目の40の所をText系のMaxLength値とする
     * カンマがなかったらそのままの値を返す
     *
     * @param maxLengthString   カンマで区切られたMaxLength値
     * @return カンマの最初の値または、値そのもの（文字）
     */
    private String getMaxLengthMultiString(String maxLengthString)
    {
        //nullチェック
        if(StringUtil.isNullOrEmpty(maxLengthString)){
            return maxLengthString;
        }
        // ","で区切り
        String[] maxLengths = maxLengthString.split(",");
        // ","がない。つまり区切られていない場合は文字用長さとみなしそのままリターンする
        if(maxLengths.length == 1){
            return maxLengthString;
        }
        //カンマがたくさんあっても一応先頭を対応する
        if(maxLengths.length > 1){
            return maxLengths[0];
        }
        else {
            // MaxLength指定のフォーマットがおかしい
            String errmsg = String.format("maxLengthString is invalid format param value = [%s]", maxLengthString);
            logger.severe(errmsg);
            throw new IllegalArgumentException(errmsg);
        }
    }
}
