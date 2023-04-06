package com.gnomes.common.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.data.ProcessTableBean;
import com.gnomes.common.data.ProcessTableInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * 工程端末テーブル カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/14 YJP/I.Shibasaka          初版
 * R0.01.02 2018/09/28 YJP/S.Hamamoto            現場端末でImgPatternの時のヘッダタイトルが出ない不具合を修正
 * R0.01.03 2018/10/23 YJP/S.Hamamoto            現場端末で1行内の列数    表示列    表示行    1行内の表示順序を定義しないとNullPointerになるので、詳細に表記するように変更
 * R0.01.04 2018/11/09 YJP/A.Oomori             readonly時、tabIndex=-1を追加出力
 * R0.01.05 2018/11/20 YJP/A.Oomori             チェックボックスをクリックでチェック状態にした場合onclickを実行するよう修正
 * R0.01.06 2018/12/03 KCC/K.Fujiwara           形式をパラメータから決定する対応
 * R0.01.07 2018/12/27 YJP/A.Oomori             同一列内に２列以上配置できない問題、スタイルクラス指定しても反映されない問題、hidden項目がソース出力されない問題を修正
 * R0.01.08 2019/01/22 YJP/S.Hamamoto           ヘッダを固定する対応
 * R0.01.09 2019/01/28 YJP/A.Oomori              プルダウンの選択名を任意のパラメータ値に差し替えできるよう修正
 * R0.01.10 2022/04/11 YJP/kei                  テーブルタイトルを動的に変更できるように修正
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagPaneconTable extends GnomesCTagBase {

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

    /** タグタイプ：空白 */
    public static final String TAG_TYPE_BLANK = "blank";

    /** タグタイプ：テキストエリア入力パターン */
    /*
     * 現状想定されていないため、コメントアウト
    private static final String TAG_TYPE_INPUT_TEXTAREA = "input_textarea";
    */

    /** 辞書：タグタイプ */
    public static final String INFO_TAG_TYPE = "type";

    /** 辞書：パラメータ名 */
    public static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：スタイル */
    private static final String INFO_STYLE = "style";

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";

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

    /** プルダウンリストID */
    /*
     * baseにまとめ
    private static final String INFO_LIST_ID = "list_id";
     */

    /** オートコンプリート */
    /*
     * baseにまとめ
    private static final String INFO_LIST_AUTOCOMPLETE = "list_autocomplete";
     */

    /** テキストエリア行数 */
    /*
     * 現状想定されていないため、コメントアウト
    private static final String INFO_ROWS = "rows";
    */

    /** 辞書：列位置 */
    public static final String INFO_COL_POSITION = "col_pos";

    /** 辞書：行位置 */
    public static final String INFO_ROW_POSITION = "row_pos";

    /** 辞書：行順序 */
    public static final String INFO_ROW_NUMBER = "row_num";

    /** 辞書：最大桁数 */
    private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";

    /** 辞書：整数入力桁数 */
    private static final String INFO_INPUT_INTEGER_DIGITS = "input_integer_digits";

    /** 辞書：小数点入力桁数 */
    private static final String INFO_INPUT_DECIMAL_DIGITS = "input_decimal_digits";

    /** 辞書：カンマ区切りフォーマットを行うか否か */
    private static final String INFO_INPUT_IS_COMMA_FORMAT = "input_is_comma_format";

    /** 先頭位置 */
    private static final String DEFAULT_POS_ROW_COL = "1";

    /** 行位置 */
    private static final int DEFAULT_ROW_NUMBER = 1;

    /** 半角スペース */
    private static final String CSS_HALF_SPACE = "&nbsp;";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private BaseFormBean bean;

    /** 一覧データ */
    private List<Object> lstObject;

    @Inject
    GnomesCTagTableCommon gnomesCTagTableCommon;

    @Inject
    SearchInfoController searchInfoController;

    @Inject
    GnomesCTagButtonCommon gnomesCTagButtonCommon;

    /** 辞書：行順序 */
    private static final String INFO_ROW_LINE_NUM = "row_line_num";

    /** テーブルの列数 */
    private Object INFO_MAX_TABLE_COL = "table_col_num";

    /** 1列毎の行内順序最大 */
    private static final String INFO_MAX_LINE_ROW_NUM = "max_line_row";

    /** 1列毎の最大行数 */
    private static final String INFO_MAX_ROW = "max_row";

    /** 辞書：表示行 */
    private static final String INFO_HEADER_ROW = "row";

    /** 辞書：表示列 */
    private static final String INFO_HEADER_COL = "col";

    /** 固定行（ヘッダー） */
    private static final String headerPosition = "1";

    /** 辞書：入力値変更後の処理 スクリプト */
    private static final String INFO_ON_CHANGE_EVENT = "on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_ON_CHANGE_COMMAND_ID = "on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_ON_CHANGE_BEAN_COMMAND_ID = "on_change_bean_command_id";

    /** メッセージ定義 Dao */
    @Inject
    protected MstrMessageDefineDao mstrMessageDefineDao;

    /**  ボタンID */
    private static final String INFO_BUTTON_BUTTON_ID = "button_id";

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
        SearchInfoPack searchInfoPack = new SearchInfoPack();

        MstSearchInfo mstSearchInfo = this.bean.getMstSearchInfoMap()
                .get(this.dictId);
        SearchSetting searchSetting = this.bean.getSearchSettingMap()
                .get(this.dictId);

        searchInfoPack.setMstConditions(mstSearchInfo.getMstConditions());
        searchInfoPack.setMstOrdering(mstSearchInfo.getMstOrdering());
        searchInfoPack.setSearchSetting(searchSetting);

        return searchInfoPack;
    }

    /**
     * 工程端末一覧出力
     */
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = null;

        try {
            //Beanの指定が間違ってnullになっているのをチェック
            if ( this.bean == null){
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }
            // テーブル辞書情報取得
            Locale userLocale = ((IScreenFormBean) this.bean).getUserLocale();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();

            // 検索ダイアログ情報取得
            SearchInfoPack searchInfoPack = getSearchInfoPack();

            // 項目情報
            List<Map<String, Object>> lstTableInfo = gnomesCTagTableCommon.getProcessTableColumnInfo(searchInfoPack, this.dictId);

            // テーブル辞書取得
            Map<String, Object> mapTableInfo = gnomesCTagDictionary.getTableInfo(dictId);

            // テーブルの列数
            String maxTableCol = (String) mapTableInfo.get(INFO_MAX_TABLE_COL);
            if (StringUtil.isNullOrEmpty(maxTableCol)) {
                maxTableCol = DEFAULT_POS_ROW_COL;
            }

            // 1列毎の最大行数取得
            String maxRow = (String) mapTableInfo.get(INFO_MAX_ROW);
            if (StringUtil.isNullOrEmpty(maxRow)) {
                maxRow = DEFAULT_POS_ROW_COL;
            }

            String maxLineRowNum =  (String) mapTableInfo.get(INFO_MAX_LINE_ROW_NUM);
            if (StringUtil.isNullOrEmpty(maxLineRowNum)) {
                maxLineRowNum = DEFAULT_POS_ROW_COL;
            }

            out = pageContext.getOut();

            // tableタグ
            String table_add_class = (String) mapTableInfo.get("table_add_class");

            out.print("<table class=\"common-table common-data-table-t");
            if (table_add_class != null) {
                out.print(" " + table_add_class);
            }

            out.print("\"");

            // tableタグに idとnameを差し込む
            // <Table id="xxx" name="xxx"
            outTagNameToIdName(out,dictId);


            // 表示位置・順の検索
            ProcessTableInfo processTableInfo = getProcessTableBeanList(maxTableCol, maxRow, maxLineRowNum, lstTableInfo);

            out.print(" _fixedhead=\"rows:" + headerPosition + "; cols:"
                    + processTableInfo.getProcessTableBeanList().size()
                    + "; div-full-mode=no;\"");

            out.print(">\n");

            // ヘッダー部の出力
            outHeader(out, maxRow, maxLineRowNum, userLocale, processTableInfo);

            // ボディ生成
            outBody(out, maxRow, maxLineRowNum, userLocale, dict, processTableInfo, this.lstObject);

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
     * 表示位置、表示順検索
     * @param maxTableCol テーブルの列数
     * @param maxRow 1列毎の最大行数
     * @param maxLineRowNum 1列毎の行内順序最大
     * @param lstTableInfo 項目情報
     * @return 工程端末テーブル情報
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private ProcessTableInfo getProcessTableBeanList(String maxTableCol, String maxRow, String maxLineRowNum,
            List<Map<String, Object>> lstTableInfo) throws Exception {

        ProcessTableInfo ptf = new ProcessTableInfo();
        List<ProcessTableBean> list = new ArrayList<ProcessTableBean>();

        List<String> colPosCountlist = new ArrayList<String>();
        Map<String, Integer> colPosCountMap = new HashMap<String, Integer>();

        if (lstTableInfo != null) {

            // 各項目の位置を検索
            for (int i = 0; i < lstTableInfo.size(); i++) {
                ProcessTableBean ptb = new ProcessTableBean();
                Map<String, Object> mapData = lstTableInfo.get(i);
                Map<String, String> headInfo = (Map<String, String>)mapData.get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);
                Map<String, Object> mapColInfo = (Map<String, Object>) mapData.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

                // 列位置
                String colPos = (String)mapColInfo.get(INFO_COL_POSITION);
                if (StringUtil.isNullOrEmpty(colPos)) {
                    colPos = String.valueOf(i);
                }
                ptb.setColPosition(colPos);

                // 行位置
                String rowPos = (String)mapColInfo.get(INFO_ROW_POSITION);
                if (StringUtil.isNullOrEmpty(rowPos)) {
                    rowPos = DEFAULT_POS_ROW_COL;
                }
                ptb.setRowPosition(rowPos);

                // 行順番
                String rowNum = (String)mapColInfo.get(INFO_ROW_NUMBER);
                if (StringUtil.isNullOrEmpty(rowNum)) {
                    rowNum = DEFAULT_POS_ROW_COL;
                }
                ptb.setRowNumber(rowNum);

                // リソースID
                String resId = "";
                if (!StringUtil.isNullOrEmpty(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID))) {
                    resId = headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID);
                }
                ptb.setResourceId(resId);

                // 行数
                String rows = "";
                if (!StringUtil.isNullOrEmpty((String)headInfo.get(INFO_HEADER_ROW))) {
                    rows = (String)headInfo.get(INFO_HEADER_ROW);
                }
                ptb.setRows(rows);

                // 列数
                String cols = "";
                if (!StringUtil.isNullOrEmpty((String)headInfo.get(INFO_HEADER_COL))) {
                    cols = (String)headInfo.get(INFO_HEADER_COL);
                }
                ptb.setCols(cols);

                // 1行あたりの列数
                String rowLineNum = (String)mapColInfo.get(INFO_ROW_LINE_NUM);
                if (StringUtil.isNullOrEmpty(rowLineNum)) {
                    rowLineNum = DEFAULT_POS_ROW_COL;
                }
                ptb.setRowLineNum(rowLineNum);

                // ヘッダー追加クラス
                String add_class = "";
                if (!StringUtil.isNullOrEmpty(headInfo.get(INFO_CLASS))) {
                    add_class = headInfo.get(INFO_CLASS);
                }
                ptb.setHeaderAddClass(add_class);

                // カラム追加クラス
                if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_CLASS))) {
                	ptb.setColumnAddClass((String) mapColInfo.get(INFO_CLASS));
                }

                // ヘッダーの追加スタイル
                String style = "";
                if (!StringUtil.isNullOrEmpty((String)headInfo.get(INFO_STYLE))) {
                	style = (String)headInfo.get(INFO_STYLE);
                }
                ptb.setHeaderStyle(style);

                // カラム追加クラス
                if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_STYLE))) {
                	ptb.setColumnStyle((String) mapColInfo.get(INFO_STYLE));
                }

                ptb.setTableInfo(mapData);
                ptb.setRowEmpty(true);
                ptb.setColEmpty(true);

                list.add(ptb);

                colPosCountlist.add(colPos);
            }

            // 各列のデータ件数を調べる
            for (String s : colPosCountlist) {
                colPosCountMap.merge(s, 1, Integer::sum);
            }
            ptf.setProcessTableInfo(colPosCountMap);
            ptf.setProcessTableBeanList(list);
        }

        return ptf;
    }

    /**
     * ヘッダー部の出力
     * @param out 出力先
     * @param maxRow 1列毎の最大行数
     * @param maxLineRowNum 1列毎の行内順序最大
     * @param userLocale ユーザーロケール
     * @param processTableInfo 工程端末テーブル情報
     * @return 最終表示項目Index
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private int outHeader(JspWriter out, String maxRow, String maxLineRowNum, Locale userLocale,
            ProcessTableInfo processTableInfo) throws Exception {

        int lastDispIndex = -1;
        out.print("<thead>\n");
        out.print("<tr>\n");

        List<ProcessTableBean> list = processTableInfo.getProcessTableBeanList();
        Map<String, Integer> map = processTableInfo.getProcessTableInfo();

        if (map != null && list != null) {
            for (ProcessTableBean ptb : list) {

                if (ptb.getTableInfo() == null) {
                    ptb.setRowEmpty(true);
                    ptb.setColEmpty(true);
                    // 空データ
                    outHeaderColumnEmpty(out, maxRow, maxLineRowNum, ptb);
                    continue;
                }

                Map<String, String> headInfo = (Map<String, String>) ptb.getTableInfo().get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);

                if (!headInfo.get(INFO_TAG_TYPE).equals(TAG_TYPE_CHECKBOX) && !headInfo.get(INFO_TAG_TYPE).equals(TAG_TYPE_BLANK)
                		&& !headInfo.get(INFO_TAG_TYPE).equals(TAG_TYPE_HIDDEN) && StringUtil.isNullOrEmpty(ptb.getResourceId())) {
                    ptb.setRowEmpty(true);
                    ptb.setColEmpty(true);
                    // 空データ
                    outHeaderColumnEmpty(out, maxRow, maxLineRowNum, ptb);
                    continue;
                }
                Map<String, Object> mapColInfo = (Map<String, Object>) ptb.getTableInfo().get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

                String mapColPosition = (String)mapColInfo.get(INFO_COL_POSITION);
                // [2018/10/23 浜本記載]
                //   画面アイテム定義「テーブルカスタムタグ定義(工程端末)」シートの「1行内の列数」、「表示列」、「表示行」、「1行内の表示順序」（BH～BH列）を指定しないと
                //     nullpointer exceptionが発生するので、ガイドを出してスローする
                if(StringUtil.isNullOrEmpty(mapColPosition)){
                    // 定義シートに誤りがあります。(ブック:{0} シート:{1} 誤り箇所:{2} 詳細:{3}
                    logger.severe("In the table custom tag definition (for Panecom) of the screen item definition, make the following definition. [row_line_num / col_pos / row_pos / row_num]");

                    throw new GnomesAppException(null,
                            GnomesMessagesConstants.ME01_0203,
                            "DisplayItemDefinition",
                            "PaneconTableCustomTagDefinition",
                            "row_line_num,col_pos,row_pos,row_num",
                            "An error will result if you do not define it."
                        );
                }

                int dataSize = map.get(mapColPosition);

                // 1データのみ
                if (dataSize == DEFAULT_ROW_NUMBER) {
                    outHeaderColumn(out, userLocale, ptb, mapColInfo);
                }
                // 複数
                else if (dataSize > DEFAULT_ROW_NUMBER) {
                    outHeaderColumns(out, maxRow, maxLineRowNum, userLocale, ptb);
                }

            }
        }
        out.print("</tr>\n");
        out.print("</thead>\n");

        return lastDispIndex;
    }

    /**
     * 空のヘッダー
     * @param out 出力先
     * @param maxRow 1列毎の最大行数
     * @param maxLineRowNum 1列毎の行内順序最大
     * @param ptb 工程端末一覧情報bean
     * @throws IOException 入出力例外
     */
    private void outHeaderColumnEmpty(JspWriter out, String maxRow, String maxLineRowNum,
            ProcessTableBean ptb) throws IOException {

        String iRowLineNum = ptb.getRowLineNum();

        if (ptb.isRowEmpty()) {
            // 列の先頭
            if (ptb.getRowPosition().equals(DEFAULT_POS_ROW_COL)) {
                out.print("<th>");
                out.print("<div>&nbsp;</div>");
            }
            // 各列の最終位置
            else if (ptb.getRowPosition().equals(maxRow) && ptb.getRowNumber().equals(iRowLineNum)) {
                out.print("<div>&nbsp;</div>");
                out.print("</th>\n");
            }
            else {
                out.print("<div>&nbsp;</div>");
            }
        }
        else if (ptb.isColEmpty()) {
            // 列の先頭
            if (iRowLineNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<th>");
                out.print("<div><span class=\"\">&nbsp;</span>");
            }
            // 各列の最終位置
            else if (ptb.getRowPosition().equals(maxRow) && ptb.getRowNumber().equals(iRowLineNum)) {
                out.print("<span class=\"\">&nbsp;</span></div>");
                out.print("</th>\n");
            }
            else {
                out.print("<span class=\"\">&nbsp;</span>");
            }
        }
    }


    /**
     * header出力（1つの場合）
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param ptb 工程端末一覧情報bean
     * @param mapColInfo 項目辞書
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private void outHeaderColumn(JspWriter out, Locale userLocale, ProcessTableBean ptb,
            Map<String, Object> mapColInfo) throws Exception {

        String label = "";

        Map<String, String> headInfo = (Map<String, String>) ptb.getTableInfo().get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);

        String type = headInfo.get(INFO_TAG_TYPE);
        if (type == null) {
            out.print("<th></th>");
            return;
        }

        String style = ptb.getHeaderStyle();
        switch (type) {
        case TAG_TYPE_CHECKBOX:
            out.print("<th");
            if (style != null) {
                out.print(" style=\""+style + "\"");
            }
            out.print(">");

            // タグ名
            String tagName = (String) mapColInfo.get(INFO_TAG_NAME);
            // 全チェックonclick
            String onclickAllCheck = "checkAll('" + tagName + "', this.checked, true);";

            out.print(
                    "<input class=\"common-input-checkbox\"  type=\"checkbox\" onclick=\"" + onclickAllCheck + "\">");
            out.print("</th>\n");
            break;
        case TAG_TYPE_BUTTON:
            out.print("<th");
            if (style != null) {
                out.print(" style=\""+style + "\"");
            }
            out.print(">");
            out.print("</th>\n");
            break;
        case TAG_TYPE_IMG_PATTERN:
            out.print("<th");
            if (style != null) {
                out.print(" style=\""+style + "\"");
            }
            out.print(">");
            out.print(this.getDispColHeader(dictId, headInfo, mapColInfo, userLocale));
            out.print("</th>\n");
            break;
        case TAG_TYPE_HIDDEN:
            out.print("<th style=\"display:none\">");
            out.print("</th>\n");
            break;
        default:
            // 折り返すため、nowrapを追加
            String styleClass = headInfo.get(INFO_CLASS);

            if (styleClass != null) {
                out.print("<th class=\"" + styleClass + "\"");
                if (style != null) {
                    out.print(" style=\"" + style + "\"");
                }
                out.print(">");
            } else if (type.equals("date")) {
                out.print("<th");
                if (style != null) {
                    out.print(" style=\"" + style + "\"");
                }
                out.print(">");
            } else {
                out.print("<th");
                if (style != null) {
                    out.print(" style=\"" + style + "\"");
                }
                out.print(">");
            }

            out.print(this.getDispColHeader(dictId, headInfo, mapColInfo, userLocale));
            out.print("</th>\n");
            break;
        }
    }

    /**
     * header出力（複数の場合）
     * @param out 出力先
     * @param maxRow 1列毎の最大行数
     * @param maxLineRowNum 1列毎の行内順序最大
     * @param userLocale ユーザーロケール
     * @param ptb 工程端末一覧情報bean
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private void outHeaderColumns(JspWriter out, String maxRow, String maxLineRowNum,
            Locale userLocale,  ProcessTableBean ptb) throws Exception {

        Map<String, String> headInfo = (Map<String, String>) ptb.getTableInfo().get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);

        Map<String, Object> mapColInfo = (Map<String, Object>) ptb.getTableInfo().get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

        String row_pos = ptb.getRowPosition();
        String row_num = ptb.getRowNumber();

        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        String rows = ptb.getRows();

        String label = "";
        String type = (String) headInfo.get(INFO_TAG_TYPE);
        if (type == null) {
            out.print("<th></th>");
            return;
        }

        if (row_pos.equals(DEFAULT_POS_ROW_COL) && row_num.equals(DEFAULT_POS_ROW_COL) && !type.equals(TAG_TYPE_HIDDEN)) {
            out.print("<th>");
        }

        String add_class = "";
        if (!StringUtil.isNullOrEmpty(ptb.getHeaderAddClass())) {
        	add_class = " " + ptb.getHeaderAddClass();
        }
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getHeaderStyle())) {
        	style = " style=\"" + ptb.getHeaderStyle() + "\"";
        }
        switch (type) {
        case TAG_TYPE_CHECKBOX:

            // タグ名
            String tagName = (String) mapColInfo.get(INFO_TAG_NAME);
            // 全チェックonclick
            String onclickAllCheck = "checkAll('" + tagName + "', this.checked, true);";

            out.print("<input class=\"common-input-checkbox\" type=\"checkbox\" onclick=\"" + onclickAllCheck + "\">");
            break;
        case TAG_TYPE_BUTTON:

            break;
        case TAG_TYPE_IMG_PATTERN:
            label = this.getDispColHeader(dictId, headInfo, mapColInfo, userLocale);
            if (!StringUtil.isNullOrEmpty(label)) {
                out.print(getStringEscapeHtml(label));
            }
            break;
        case TAG_TYPE_HIDDEN:
            out.print("<th style=\"display:none\">");
            out.print("</th>\n");
            break;
        default:
            label = this.getDispColHeader(dictId, headInfo, mapColInfo, userLocale);

            // ヘッダタイプがblank(空白)の場合、ヘッダ文言は空白
            if (type.equals(TAG_TYPE_BLANK)) {
                label =  CSS_HALF_SPACE;
            }

            if (linunum == DEFAULT_ROW_NUMBER) {
                out.print("<div class=\"common-table-col-1" + add_class + "\"" + style + ">");
                out.print(label);
                out.print("</div>\n");
            }
            else {
            	String widthClass = "table-col-" + row_line_num;

                if (row_num.equals(DEFAULT_POS_ROW_COL)) {
                    out.print("<div class=\"common-table-col-1\">");
                    out.print("<span class=\"common-table-header-style " + widthClass + add_class + "\"" + style + ">");
                    out.print(label);
                    out.print("</span>");
                }
                else if (row_num.equals(row_line_num)) {
                    out.print("<span class=\"common-table-header-style " + widthClass + add_class + "\"" + style + ">");
                    out.print(label);
                    out.print("</span>");
                    out.print("</div>\n");
                }
                else {
                    out.print("<span class=\"common-table-header-style " + widthClass + add_class + "\"" + style + ">");
                    out.print(label);
                    out.print("</span>");
                }
            }
            break;
        }

        if (row_pos.equals(rows) && row_num.equals(row_line_num)) {
            out.print("</th>\n");
        }
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
     * ボディ部の出力
     * @param out 出力先
     * @param maxRow 1列毎の最大行数
     * @param maxLineRowNum 1列毎の行内順序最大
     * @param userLocale ユーザーロケール
     * @param dict 辞書
     * @param processTableInfo 工程端末テーブル情報
     * @param lstData 一覧データ
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private void outBody(JspWriter out, String maxRow, String maxLineRowNum, Locale userLocale,
            GnomesCTagDictionary dict, ProcessTableInfo processTableInfo, List<Object> lstData) throws Exception {

        // 出力元データのクラス
        Class<?> clsRowData = null;

        out.print("<tbody>\n");
        if (lstData != null) {

        SearchSetting searchSetting = this.bean.getSearchSettingMap().get(this.dictId);
        int displayCount = lstData.size();
        // 上限件数を越えた場合,通知するメッセージング設定する
        if (displayCount > searchSetting.getMaxDispCount()) {
        	// 上限件数
        	displayCount = searchSetting.getMaxDispCount();
        	// 通知するメッセージングの為、
        	String categoryName = "";
    		String iconName = "";
    		MstrMessageDefine mstrMsgdef = this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(GnomesMessagesConstants.ME01_0246);
    		if (!Objects.isNull(mstrMsgdef)) {
    			// 種別(名称)
    	        CommonEnums.MessageCategory msgCategory = CommonEnums.MessageCategory.getEnum(mstrMsgdef.getCategory());
    	        // リソースよりユーザ言語で取得
    	        categoryName = ResourcesHandler.getString(msgCategory.toString(), userLocale);
    	        // メッセージ重要度
    	        CommonEnums.MessageLevel msgLevel = CommonEnums.MessageLevel.getEnum(mstrMsgdef.getMessage_level());
    	        // アイコン名
    	        iconName = ResourcesHandler.getString(MessagesHandler.getMessageIconResKey(msgCategory,msgLevel));
    		}
    		out.print("            <input type=\"hidden\" name=\"overSizeMesBody\" value=\"" + MessagesHandler.getString(GnomesMessagesConstants.ME01_0246, userLocale, displayCount) + "\">\n");
    	    out.print("            <input type=\"hidden\" name=\"overSizeMesCategory\" value=\"" + categoryName + "\">\n");
    	    out.print("            <input type=\"hidden\" name=\"overSizeMesIconName\" value=\"" + iconName + "\">\n");
        }

            if (lstData.size() > 0) {
                clsRowData = lstData.get(0).getClass();
            }

            for (int i = 0; i < displayCount; i++) {

                out.print("<tr class=\"common-operater-table-tr-fxed \">\n");

                Object objRowData = lstData.get(i);

                List<ProcessTableBean> tableBean = processTableInfo.getProcessTableBeanList();

                for (ProcessTableBean ptb : tableBean) {
                    Map<String, Object> mapCol = ptb.getTableInfo();
                    if (mapCol == null) {
                        // 空データ
                        outBodyEmpty(out, maxRow, maxLineRowNum, ptb);
                    } else {
                        Map<String, Object> mapColInfo = (Map<String, Object>) mapCol.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

                        String type = (String) mapColInfo.get(INFO_TAG_TYPE);

                        switch (type) {
                        case TAG_TYPE_CHECKBOX:
                            outCheckBox(out, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_BUTTON:
                            outButton(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_IMG_PATTERN:
                            outImgPattern(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i, dict);
                            break;
                        case TAG_TYPE_TEXT:
                            outText(out, dict, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_NUMBER:
                            outNumber(out, dict, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_DATE:
                        case TAG_TYPE_ZONEDDATETIME:
                            outDate(out, userLocale, mapColInfo, clsRowData, objRowData, ptb);
                            break;
                        case TAG_TYPE_PULLDOWN_CODE:
                            outPullDownConstant(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i, true);
                            break;
                        // プルダウン（コード等定数） 先頭空白無し
                        case TAG_TYPE_PULLDOWN_CODE_NO_SPACE:
                            outPullDownConstant(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i, false);
                            break;
                        case TAG_TYPE_PULLDOWN_DATA:
                            outPullDownData(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i, true);
                            break;
                         // プルダウン（データ項目） 先頭空白無し
                        case TAG_TYPE_PULLDOWN_DATA_NO_SPACE:
                            outPullDownData(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i, false);
                            break;
                        /*
                         * 現状想定されていないため、コメントアウト
                        case TAG_TYPE_TEXTAREA:
                            outTextArea(out, dict, mapColInfo, clsRowData, objRowData, ptb);
                            break;
                        */
                        case TAG_TYPE_HIDDEN:
                            outHidden(out, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;

                        case TAG_TYPE_INPUT_TEXT:
                            inputText(out, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_INPUT_NUMBER:
                            inputNumber(out, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_INPUT_DATE:
                        case TAG_TYPE_INPUT_ZONEDDATETIME:
                            inputDate(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_INPUT_DATA_TYPE:
                            inputDataType(out, userLocale, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_OUTPUT_DATA_TYPE:
                            outputDataType(out, dict, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                        case TAG_TYPE_BLANK:
                            outputBlank(out, ptb);
                            break;

                            /*
                         * 現状想定されていないため、コメントアウト
                        case TAG_TYPE_INPUT_TEXTAREA:
                            inputTextArea(out, resBundle, dict, mapColInfo, clsRowData, objRowData, ptb, i);
                            break;
                         */
                        default:
                            break;

                        }
                    }
                }
                out.print("</tr>\n");
            }
        }
        out.print("</tbody>\n");

    }

    /**
     * 空ボディ出力
     * @param out 出力先
     * @param maxRow 1列毎の最大行数
     * @param maxLineRowNum 1列毎の行内順序最大
     * @param ptb 工程端末一覧情報bean
     * @throws IOException 入出力例外
     */
    private void outBodyEmpty(JspWriter out, String maxRow, String maxLineRowNum,
            ProcessTableBean ptb) throws IOException {

        String iRowLineNum = ptb.getRowLineNum();

        // 行が空の場合
        if (ptb.isRowEmpty()) {
            // 各列の先頭
            if (ptb.getRowPosition().equals(DEFAULT_POS_ROW_COL)) {
                out.print("<td>");
                out.print("<div>&nbsp;</div>");
            }
            // 各列の最終位置
            else if (ptb.getRowPosition().equals(maxRow) && ptb.getRowNumber().equals(iRowLineNum)) {
                out.print("<div>&nbsp;</div>");
                out.print("</td>\n");
            }
            else {
                out.print("<div>&nbsp;</div>");
            }
        }
        else if (ptb.isColEmpty()) {// 列が空の場合（列内複数列の場合も含む）
            // 各列の先頭
            if (iRowLineNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<td>");
                out.print("<div><span class=\"\">&nbsp;</span>");
            }
            // 各列の最終位置
            else if (ptb.getRowPosition().equals(maxRow) && ptb.getRowNumber().equals(iRowLineNum)) {
                out.print("<span class=\"\">&nbsp;</span></div>");
                out.print("</td>\n");
            }
            else {
                out.print("<span class=\"\">&nbsp;</span>");
            }
        }

    }

//  現状想定されていないため、コメントアウト
//    /**
//     * テキストエリア入力
//     * @param out 出力先
//     * @param mapColInfo 項目辞書
//     * @param ptb
//     * @param clsData Beanクラス
//     * @param data Beanデータ
//     * @param rowCnt 行数
//     * @throws Exception 例外
//     */
//    private void inputTextArea(JspWriter out, ResourceBundle resBundle, GnomesCTagDictionary dict,
//            Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData, ProcessTableBean ptb, int i) throws Exception {
//        String style = ptb.getStyle();
//        String styleClass = (String) mapColInfo.get(INFO_CLASS);
//        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
//        String rows = (String) mapColInfo.get(INFO_ROWS);
//        String name = (String) mapColInfo.get(INFO_TAG_NAME);
//
//        String rowCount = ptb.getRows();
//
//        String rowpos = ptb.getRowPosition();
//        String rowNum = ptb.getRowNumber();
//
//        String row_line_num = ptb.getRowLineNum();
//        int linunum = Integer.valueOf(row_line_num);
//
//        String strVal = "";
////        Object value = getData(clsRowData, objRowData, paramName);
//        Object value = responseContext.getResponseFormBean(objRowData,
//                paramName, i, name);
//        if (value != null) {
//            strVal = value.toString();
//        }
//
//        boolean hidden = false;
//        if (mapColInfo.containsKey(INFO_HIDDEN)) {
//            hidden = true;
//        }
//
//        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
//            if (hidden) {
//                out.print("<td  style=\"display:none\">\n>");
//            }
//            else {
//                out.print("<td");
//                if (styleClass != null) {
//                  out.print(" class=\"" + styleClass + " \"");
//                }
//                out.print(">\n");
//            }
//        }
//
//        if (linunum == DEFAULT_ROW_NUMBER) {
//            out.print("<div class=\"common-table-col-1\"");
//            if (!StringUtil.isNullOrEmpty(style)) {
//                out.print(" style=\"" + style + "\"");
//            }
//            if (name != null) {
//                out.print(" name=\"" + name + "\"");
//            }
//            out.print(">");
//            out.print("<textarea name=\"" + name + "\" rows=\"" + rows + "\" class=\"common-table-col-1 common-keyboard-input");
//            if (styleClass != null) {
//                out.print(" " + styleClass);
//            }
//
//            out.print("\">");
//            out.print(strVal);
//            out.print("</textarea>");
//            out.print("</div>\n");
//        }
//        else {
//            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
//                out.print("<div>");
//                out.print("<span class=\"\">");
//                out.print("<textarea name=\"" + name + "\" rows=\"" + rows + "\" class=\" common-keyboard-input ");
//                if (styleClass != null) {
//                    out.print(" " + styleClass);
//                }
//                if(!StringUtil.isNullOrEmpty(style)){
//                    out.print("\" style=\"" + style);
//                }
//                out.print("\">");
//                out.print(strVal);
//                out.print("</textarea>");
//                out.print("</span>");
//            }
//            else if (rowNum.equals(row_line_num)) {
//                out.print("<span class=\" common-table-body-columns-2\">");
//                out.print("<textarea name=\"" + name + "\" rows=\"" + rows + "\" class=\" common-keyboard-input ");
//                if (styleClass != null) {
//                    out.print(" " + styleClass);
//                }
//                if(!StringUtil.isNullOrEmpty(style)){
//                    out.print("\" style=\"" + style);
//                }
//                out.print("\">");
//                out.print(strVal);
//                out.print("</textarea>");
//                out.print("</span>");
//                out.print("</div>\n");
//            }
//            else {
//                out.print("<span class=\" common-table-body-columns-2\">");
//                out.print("<textarea name=\"" + name + "\" rows=\"" + rows + "\" class=\" common-keyboard-input ");
//                if (styleClass != null) {
//                    out.print(" " + styleClass);
//                }
//                if(!StringUtil.isNullOrEmpty(style)){
//                    out.print("\" style=\"" + style);
//                }
//                out.print("\">\n");
//                out.print(strVal);
//                out.print("</textarea>");
//                out.print("</span>");
//            }
//        }
//
//        if (rowpos.equals(rowCount) && rowNum.equals(row_line_num)) {
//            out.print("</td>\n");
//        }
//    }

    /**
     * タグタイプ：入力形式をパラメータから決定
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private void inputDataType(JspWriter out,
    		Locale userLocale,
    		Map<String, Object> mapColInfo,
    		Class<?> clsRowData,
            Object objRowData,
            ProcessTableBean ptb,
            int i) throws Exception {

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
            inputDateBase(out, userLocale, beanParamName, datePattern, mapColInfo, clsRowData,
                    objRowData, ptb, i);
            return;
        }

        switch (dataType) {
            // 数値入力の場合
            case PARAM_DATA_TYPE_DIV_NUMBER:
                inputNumberBase(out, beanParamName, mapColInfo, clsRowData, objRowData, ptb, i,true);
                return;
            // 二値(プルダウン)
            case PARAM_DATA_TYPE_DIV_PULLDOWN:

                // 行データよりプルダウン候補リストを取得
                // プルダウン候補パラメータ名
                String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
                // プルダウン候補パラメータ名のデータをBeanから取得
                List<Object> listValue = (List<Object>) getData(clsRowData, objRowData, listParamName);

                outPullDownBase(
                        out,
                        userLocale,
                        listValue,
                        beanParamName,
                        beanParamName,
                        mapColInfo,
                        clsRowData,
                        objRowData,
                        ptb,
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

                outPullDownBase(
                		out,
                        userLocale,
                        listValue2,
                        beanParamName,
                        beanParamName,
                        mapColInfo,
                        clsRowData,
                        objRowData,
                        ptb,
                        i,
                        false);
                return;
            // 上記以外の場合
            default:
                // 文字入力の場合
                inputTextBase(out, beanParamName, mapColInfo, clsRowData, objRowData, ptb, i,true);
                return;
        }

    }

    /**
     * タグタイプ：表示形式をパラメータから決定
     * @param out 出力先
     * @param dict 辞書
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
	private void outputDataType(
            JspWriter out,
            GnomesCTagDictionary dict,
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            ProcessTableBean ptb, int rowCnt) throws Exception {

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
            outDateBase(out, beanParamName, datePattern, mapColInfo, clsRowData,
                    objRowData, ptb);
            return;
        }

        switch (dataType) {
            // 数値入力の場合
            case PARAM_DATA_TYPE_DIV_NUMBER:
                outNumberBase(out, beanParamName, dict, mapColInfo, clsRowData, objRowData, ptb, rowCnt);
                return;
            // 二値(プルダウン)
            case PARAM_DATA_TYPE_DIV_PULLDOWN:
            case PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE:

                // 選択値取得
                Object selValue = getData(clsRowData, objRowData, beanParamName);

                // 行データよりプルダウン候補リストを取得
                // プルダウン候補パラメータ名
                String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
                // プルダウン候補パラメータ名のデータをBeanから取得
                List<Object> listValue = (List<Object>) getData(clsRowData, objRowData, listParamName);

                String strVal = "";
                String[] outSel = new String[2];
                if (getSelect(listValue, selValue, outSel)) {
                    strVal = outSel[0];
                    if (outSel[1].length() > 0) {
                        strVal = outSel[0] + " " + outSel[1];
                    }
                }

                outTextCommon(
                        out,
                        strVal,
                        dict,
                        mapColInfo,
                        clsRowData,
                        objRowData,
                        ptb, rowCnt);
                return;
            // 上記以外の場合
            default:
                // 文字入力の場合
                outTextBase(out, beanParamName, dict, mapColInfo, clsRowData, objRowData, ptb, rowCnt);
                return;
        }
    }


    /**
     * 日付入力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    private void inputDate(JspWriter out, Locale userLocale, Map<String, Object> mapColInfo, Class<?> clsRowData,
            Object objRowData, ProcessTableBean ptb, int i) throws Exception {
        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // フォーマットリソースID
        String formatResourceId = (String) mapColInfo
                .get(INFO_FORMAT_RESOURCE_ID);
        // 日付パターン
        String datePattern = ResourcesHandler.getString(formatResourceId, userLocale);

        inputDateBase(out, userLocale, paramName, datePattern, mapColInfo, clsRowData, objRowData, ptb, i);
    }

    /**
     * 日付入力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    private void inputDateBase(JspWriter out, Locale userLocale, String paramName, String datePattern, Map<String, Object> mapColInfo, Class<?> clsRowData,
            Object objRowData, ProcessTableBean ptb, int i) throws Exception {
        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }
        // タグ名
        String name = (String) mapColInfo.get(INFO_TAG_NAME);

        // 列数（1列あたり）
        String cols = ptb.getCols();
        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        // 表示データを取得
        String strDate = null;
        Object valueObj = responseContext.getResponseFormBean(objRowData,
                paramName, i, name);

        String inputDatePattern = getStringDateFormatHtml(datePattern);

        if (valueObj instanceof String) {
            strDate = (String) valueObj;
        } else {
            strDate = getStringDate(this.dictId, paramName, valueObj, datePattern);
        }
        if (strDate == null) {
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

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            if (hidden) {
                out.print("<td style=\"display:none\">\n");
            }
            else {
                out.print("<td>\n");
            }
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1\">");
            out.print("<input type=\"text\" class=\"datetime common-table-col-1" + styleClass + "\" name=\"" + name + "\"" + style);
            out.print(" data-date-format=\"" + inputDatePattern + "\"");
            out.print(" value=\"" + getStringEscapeHtmlValue(strDate) + "\"" + strReadOnly + tabIndex + onchange);
            out.print(">");
            out.print("</div>\n");
        }
        //  1列以上の場合
        else {

        	String widthClass = "table-col-" + row_line_num;

            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input type=\"text\" class=\"datetime common-table-col-input" + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(" data-date-format=\"" + inputDatePattern + "\"");
                out.print(" value=\"" + getStringEscapeHtmlValue(strDate) + "\"" + strReadOnly + tabIndex + onchange);
                out.print(">");
                out.print("</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input type=\"text\" class=\"datetime common-table-col-input" + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(" data-date-format=\"" + inputDatePattern + "\"");
                out.print(" value=\"" + getStringEscapeHtmlValue(strDate) + "\"" + strReadOnly + tabIndex + onchange);
                out.print(">");
                out.print("</span>");
                out.print("</div>\n");
            }
            else {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input type=\"text\" class=\"datetime common-table-col-input" + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(" data-date-format=\"" + inputDatePattern + "\"");
                out.print(" value=\"" + getStringEscapeHtmlValue(strDate) + "\"" + strReadOnly + tabIndex + onchange);
                out.print(">");
                out.print("</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(cols)) {
            out.print("</td>\n");
        }
    }


    /**
     * 数値入力
     * @param out 出力先
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    private void inputNumber(JspWriter out, Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData,
            ProcessTableBean ptb, int i) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        inputNumberBase(out, paramName, mapColInfo, clsRowData, objRowData, ptb, i,false);

    }

    /**
     * 数値入力
     * @param out 出力先
     * @param paramName ビーンのパラメータ名
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    private void inputNumberBase(JspWriter out, String paramName, Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData,
            ProcessTableBean ptb, int i,boolean isMultiType) throws Exception {
        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }
        // 小数点桁数パラメータ名
        String decimalPointName = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_PARAM_NAME);
        // 小数点桁数（固定値）
        String decimalPointValue = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_VALUE);
        String strNum = "";
        Integer intObj = null;
        // タグ名
        String name = (String) mapColInfo.get(INFO_TAG_NAME);

        // 最大桁数
        String inputMaxLength = (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH);
        // 整数入力桁数
        String inputIntegerDigits = (String) mapColInfo.get(INFO_INPUT_INTEGER_DIGITS);
        // 小数点入力桁数
        String inputDecimalDigits = (String) mapColInfo.get(INFO_INPUT_DECIMAL_DIGITS);
        // カンマ区切りフォーマットを行うか否か
        String inputIsCommaFormat = (String) mapColInfo.get(INFO_INPUT_IS_COMMA_FORMAT);

        // 列数（1列あたり）
        String cols = ptb.getCols();
        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        // 小数点桁数パラメータ名 の指定がある場合、Beanより小数点桁数を取得
        if (decimalPointName != null) {
            intObj = (Integer) (getData(clsRowData, objRowData, decimalPointName));

        }
        // 小数点桁数（固定値）の指定がある場合、小数点桁数（固定値）を取得
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

        if (!StringUtil.isNullOrEmpty(paramName)
                && !StringUtil.isNullOrEmpty(name)) {

            Object valueObj = responseContext.getResponseFormBean(objRowData,
                    paramName, i, name);
            if (valueObj instanceof String) {
                strNum = (String) valueObj;
            } else {
                strNum = getStringNumber(this.dictId, paramName, valueObj, intObj);
            }
        }

        // 非表示項目か否か
        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        // 入力値変更後の処理
        String onchange = getOnChange(mapColInfo);

        // 数値入力クラス
        String strInputClass = " common-keyboard-input-num";
        // 入力可能か否か
        String strReadOnly = "";
        String tabIndex = "";
        if (isInputReadOnlyParam(clsRowData, objRowData, paramName)) {
            strReadOnly = " readonly";
            // キーボードを出力しない
            strInputClass = "";
            tabIndex = " tabindex=\"-1\"";
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

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            if (hidden) {
                out.print("<td style=\"display:none\">\n");
            }
            else {
                out.print("<td>\n");
            }
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1\">");
            out.print("<input class=\"common-table-col-1 common-text-number gnomes-number"+ strInputClass + styleClass + "\" name=\"" + name + "\"" + style);
            out.print(strMaxLength + strIntegerDigits + strDecimalDigits + " value=\""+ getStringEscapeHtmlValue(strNum) +"\"" + strReadOnly + tabIndex + onchange + ">");
            out.print("</div>\n");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input class=\"common-text-number common-table-col-input gnomes-number"+ strInputClass + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(strMaxLength + strIntegerDigits + strDecimalDigits + " value=\""+ getStringEscapeHtmlValue(strNum) +"\"" + strReadOnly + tabIndex + onchange + ">");
                out.print("</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input class=\"common-text-number common-table-col-input gnomes-number"+ strInputClass + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(strMaxLength + strIntegerDigits + strDecimalDigits + " value=\""+ getStringEscapeHtmlValue(strNum) +"\"" + strReadOnly + tabIndex + onchange + ">");
                out.print("</span>");
                out.print("</div>\n");
            }
            else {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input class=\"common-text-number common-table-col-input gnomes-number"+ strInputClass + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(strMaxLength + strIntegerDigits + strDecimalDigits + " value=\""+ getStringEscapeHtmlValue(strNum) +"\"" + strReadOnly + tabIndex + onchange + ">");
                out.print("</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(cols)) {
            out.print("</td>\n");
        }
    }

    /**
     * テキスト入力
     * @param out 出力先
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    private void inputText(JspWriter out, Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData, ProcessTableBean ptb, int i) throws Exception {
        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        inputTextBase(out, paramName, mapColInfo, clsRowData, objRowData, ptb, i,false);

    }

    /**
     * テキスト入力
     * @param out 出力先
     * @param paramName ビーンのパラメータ名
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param i 行数
     * @throws Exception 例外
     */
    private void inputTextBase(
            JspWriter out,
            String paramName,
            Map<String, Object> mapColInfo,
            Class<?> clsRowData,
            Object objRowData,
            ProcessTableBean ptb,
            int i,
            boolean isMultiType   ) throws Exception {

        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }
        // タグ名
        String name = (String) mapColInfo.get(INFO_TAG_NAME);

        // 列数（1列あたり）
        String cols = ptb.getCols();
        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        String strVal = "";
        Object value = responseContext.getResponseFormBean(objRowData,
                paramName, i, name);

        if (value != null) {
            strVal = value.toString();
        }

        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        // キーボード出力クラス
        String keyboardClass  = " common-keyboard-input-char";

        // 入力値変更後の処理
        String onchange = getOnChange(mapColInfo);

        // 入力可能か否か
        String strReadOnly = "";
        String tabIndex = "";
        if (isInputReadOnlyParam(clsRowData, objRowData, paramName)) {
            strReadOnly = " readonly";
            // キーボードを出力しない
            keyboardClass = "";
            tabIndex = " tabindex=\"-1\"";
        }

        String inputMaxLength = (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH);

        // 可変タイプ対応
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

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            if (hidden) {
                out.print("<td style=\"display:none\">\n");
            }
            else {
                out.print("<td>\n");
            }
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1\">");
            out.print("<input type=\"text\" class=\"common-table-col-1" + keyboardClass + styleClass + "\" name=\"" + name + "\"" + style);
            out.print(strMaxLength + " value=\""+getStringEscapeHtmlValue(strVal)+"\"" + strReadOnly + tabIndex + onchange + ">");
            out.print("</div>\n");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input type=\"text\" class=\"common-table-col-input " + keyboardClass + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(strMaxLength + " value=\""+getStringEscapeHtmlValue(strVal)+"\"" + strReadOnly + tabIndex + onchange + ">");
                out.print("</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input type=\"text\" class=\"common-table-col-input " + keyboardClass + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(strMaxLength + " value=\""+getStringEscapeHtmlValue(strVal)+"\"" + strReadOnly + tabIndex + onchange + ">");
                out.print("</span>");
                out.print("</div>\n");
            }
            else {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<input type=\"text\" class=\"common-table-col-input " + keyboardClass + styleClass + "\" name=\"" + name + "\"" + style);
                out.print(strMaxLength + " value=\""+getStringEscapeHtmlValue(strVal)+"\"" + strReadOnly + tabIndex + onchange + ">");
                out.print("</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(cols)) {
            out.print("</td>\n");
        }
    }


    /**
     * 非表示出力
     * @param out 出力先
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outHidden(
            JspWriter out,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {
        // タグ名
        String name = (String) mapColInfo.get(INFO_TAG_NAME);
        // パラメータ名
        String valueName = (String) mapColInfo.get(INFO_PARAM_NAME);
        Object value = getData(clsData, data, valueName);

        out.print("<td style=\"display:none\">\n");
        out.print("    <input type=\"hidden\" name=\"" + name
                + "\" value=\"" + getStringEscapeHtmlValue(String.valueOf(value))
                + "\"/>");
        out.print("</td>\n");

    }

    /**
     * チェックボックス出力
     * @param out 出力先
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outCheckBox(
            JspWriter out,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {
        // スタイル
        String style = ptb.getColumnStyle();
        // タグ名
        String name = (String) mapColInfo.get(INFO_TAG_NAME);
        // パラメータ名
        String valueName = (String) mapColInfo.get(INFO_PARAM_NAME);
        // onclick
        String onclick = (String) mapColInfo.get(INFO_ON_CLICK);
        // チェックボックスクラス
        String checkboxclass = (String) mapColInfo.get(INFO_CLASS);

        String strChecked = "";
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

        boolean hidden = false;
        // hidden項目か否か
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        if (!hidden) {
            out.print("<td class=\"common-text-center");
            // チェックボックスクラスの設定
            if (!StringUtil.isNullOrEmpty(checkboxclass)) {
                out.append(" " + checkboxclass);
            }
            out.print("\">\n");
        } else {
            out.print("<td class=\"common-text-center\" style=\"display:none\">\n");
        }
        // リソースにするまたは辞書に追加する
        out.print("<input class=\"common-input-checkbox\" style=\"" + style + "\" type=\"checkbox\" name=\""+name+"\"  value=\"" + String.valueOf(rowCnt) + "\" onclick=\"" + strOnclick + "\" " + strChecked + ">\n");
        out.print("</td>\n");
    }

    /**
     * ボタン出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outButton(
            JspWriter out,
            Locale userLocale,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {
        // スタイル
        String style = ptb.getColumnStyle();
        // ボタンクラス
        String btnclass = ptb.getColumnAddClass();
        if (StringUtil.isNullOrEmpty(btnclass)) {
            btnclass = "";
        }

        // onclick
        String onclick = (String) mapColInfo.get(INFO_ON_CLICK);

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

        // ボタン辞書Id
        String buttonDicId = "";

        // 項目がある場合
        if(tagName != null) {
        	 // ボタン辞書Id
        	buttonDicId = prefix + "." + tagName;
        	// ボタンの値を取得
        	onclick = gnomesCTagButtonCommon.getOnclickButtonAttribute(this.bean,buttonDicId);
        }

        // イメージソース
        String imgsrc = (String) mapColInfo.get(INFO_IMG_SRC);
        // タイトルリソースID
        String titleResouceId = (String) mapColInfo.get(INFO_TITLE);
        String title = "";
        String titleResouce = "";

        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);


        // タイトルリソースからタイトルを取得
        if (titleResouceId != null) {
            titleResouce = this.getStringEscapeHtml(ResourcesHandler.getString(titleResouceId, userLocale));
            title = " title=\"" + titleResouce + "\"";
        }

        // パラメータ名(表示非表示データを格納されたパラメータ名, 押したボタンの行数を格納するパラメータ名)
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        if (StringUtil.isNullOrEmpty(infoParamName)) {
        	infoParamName = "";
        }
        String paramNames[] = infoParamName.split(",");


        // 列すべて非表示項目か否か
        boolean allHidden = false;

        // 非表示項目か否か
        Boolean hidden = false;
        // 非表示項目か否かが設定されている場合、列すべて非表示
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            allHidden = true;
        }
        // パラメータ名より表示非表示の切替
        else if (!StringUtil.isNullOrEmpty(paramNames[0])) {
        	hidden = ConverterUtils.IntTobool((Integer)getData(clsData, data, paramNames[0]));
        }

        // onclickに何行目が押されたかどうかをparam_nameに設定されたパラメータに設定
        if (paramNames.length >= 2 && !StringUtil.isNullOrEmpty(paramNames[1])) {
        	rowCnt = rowCnt + 1;
        	onclick = "setInputValue('" + paramNames[1] + "', "+ rowCnt + ");" + onclick;
        }

        if (!StringUtil.isNullOrEmpty(style)) {
        	style = "style=\"" + style + "\"";
        }
        else {
        	style = "";
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
        // ボタンにラベルが指定されている場合は、イメージは出力せず
        // 指定したラベルが出力される
        if(StringUtils.isEmpty(buttonLabelString)) {
            //従来はイメージを貼り付ける
            if (!StringUtil.isNullOrEmpty(imgsrc)) {
            	imgsrc = "<img src=\""+ imgsrc + "\"" + " alt=\""+ titleResouce + "\">\n";
            }
            else {
            	imgsrc = "";
            }
        }
        // イメージではなくラベルの場合は、ラベルを出力する
        else {
            imgsrc = buttonLabelString;
        }

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            if (allHidden) {
                out.print("<td style=\"display:none\">\n");
            }
            else {
                out.print("<td>");
            }
        }

        // tabindex設定
        String tabindex = "-1";

        // テーブル辞書情報取得
        GnomesCTagDictionary dict = this.getCTagDictionary();

        // ボタン辞書取得
        Map<String, Object> mapInfo = dict.getButtonInfo(buttonDicId);

        // パーツ権限情報Listを取得
        List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = this.getPartsPrivilegeResultInfoList(this.bean);

        // 非活性Class名
        String disableClassName = "";
        // 非表示Class名
        String hideClassName = "";

        //ボタンに付属させるnameタグ(idタグ)
        String name = outIdNameString(mapColInfo);

        // パーツ権限を取得
        PartsPrivilegeResultInfo partsPrivilegeResultInfo = getPartsPrivilegeResultInfo((String) mapInfo.get(
                INFO_BUTTON_BUTTON_ID), stmPartsPrivilegeResultInfo);

        // 権限があるかをチェック
        if (partsPrivilegeResultInfo != null && !partsPrivilegeResultInfo.isPrivilege()) {
            // 権限無しの場合、ボタン表示区分を取得
            DisplayDivision displayDivision = partsPrivilegeResultInfo.getDisplayDiv();

            if (displayDivision == DisplayDivision.DISABLE) {
                // ボタン表示区分が非活性である場合
                disableClassName = " common-menu-panecon-table-disabled";
            }
            else if (displayDivision == DisplayDivision.HIDE) {
                // ボタン表示区分が非表示である場合
                hideClassName = " common-menu-panecon-table-hide";
            }
        }

        // スタイルClass名が空白であるかをチェック
        if (!StringUtil.isNullOrEmpty(btnclass) || !StringUtil.isNullOrEmpty(
                disableClassName) || !StringUtil.isNullOrEmpty(hideClassName)) {
            btnclass = "class=\"" + btnclass + disableClassName + hideClassName + "\"";
        }
        else {
            btnclass = "";
        }

        if (!StringUtil.isNullOrEmpty(disableClassName) || !StringUtil.isNullOrEmpty(hideClassName)) {
            // 非活性Class名、または、非表示Class名が空白ではない場合
            //  1列の場合
            if (linunum == DEFAULT_ROW_NUMBER) {
                out.print("<div class=\"common-table-col-1 common-text-center\">");
                // その項目が非表示の場合
                if (!hidden) {
                    out.print(
                            "<button " + btnclass + name + " " + style + " " + title + " tabindex=\"" + tabindex + "\" type=\"button\">\n");
                    out.print(imgsrc);
                    out.print("</button>\n");
                }
                else {
                    out.print(CSS_HALF_SPACE);
                }
                out.print("</div>");
            }
            //  1列以上の場合
            else {
                String widthClass = "table-col-" + row_line_num;
                // 行番号が1
                if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                    out.print("<div class=\"common-text-center\">");
                    out.print("<span class=\"" + widthClass + "\">");
                    // その項目が非表示の場合
                    if (!hidden) {
                        out.print(
                                "<button " + btnclass + name +" " + style + " " + title + " tabindex=\"" + tabindex + "\" type=\"button\">\n");
                        out.print(imgsrc);

                        out.print("</button>\n");
                    }
                    else {
                        out.print(CSS_HALF_SPACE);
                    }
                    out.print("</span>");
                }
                // 行番号が最後
                else if (rowNum.equals(row_line_num)) {
                    out.print("<span class=\"" + widthClass + "\">");
                    // その項目が非表示の場合
                    if (!hidden) {
                        out.print(
                                "<button " + btnclass + name +" " + style + " " + title + " tabindex=\"" + tabindex + "\" type=\"button\">\n");
                        out.print(imgsrc);

                        out.print("</button>\n");
                    }
                    else {
                        out.print(CSS_HALF_SPACE);
                    }
                    out.print("</span>");
                    out.print("</div>");
                }
                else {
                    out.print("<span class=\"" + widthClass + "\">");
                    // その項目が非表示の場合
                    if (!hidden) {
                        out.print(
                                "<button " + btnclass + name +" " + style + " " + title + " tabindex=\"" + tabindex + "\" type=\"button\">\n");
                        out.print(imgsrc);
                        out.print("</button>\n");
                    }
                    else {
                        out.print(CSS_HALF_SPACE);
                    }
                    out.print("</span>");
                }
            }
        }
        else {
            // 権限ありの場合、または、ボタン表示区分が「エラーを表示（従来の動き）」である場合

            //  1列の場合
            if (linunum == DEFAULT_ROW_NUMBER) {
                out.print("<div class=\"common-table-col-1 common-text-center\">");
                // その項目が非表示の場合
                if (!hidden) {
                    out.print(
                            "<button " + btnclass + name + " " + style + " " + title + " onclick=\"" + onclick + "\" tabindex=\"" + tabindex + "\" type=\"button\">\n");
                    out.print(imgsrc);
                    out.print("</button>\n");
                }
                else {
                    out.print(CSS_HALF_SPACE);
                }
                out.print("</div>");
            }
            //  1列以上の場合
            else {
                String widthClass = "table-col-" + row_line_num;
                // 行番号が1
                if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                    out.print("<div class=\"common-text-center\">");
                    out.print("<span class=\"" + widthClass + "\">");
                    // その項目が非表示の場合
                    if (!hidden) {
                        out.print(
                                "<button " + btnclass + name +" " + style + " " + title + " onclick=\"" + onclick + "\" tabindex=\"" + tabindex + "\" type=\"button\">\n");
                        out.print(imgsrc);

                        out.print("</button>\n");
                    }
                    else {
                        out.print(CSS_HALF_SPACE);
                    }
                    out.print("</span>");
                }
                // 行番号が最後
                else if (rowNum.equals(row_line_num)) {
                    out.print("<span class=\"" + widthClass + "\">");
                    // その項目が非表示の場合
                    if (!hidden) {
                        out.print(
                                "<button " + btnclass + name + " " + style + " " + title + " onclick=\"" + onclick + "\" tabindex=\"" + tabindex + "\" type=\"button\">\n");
                        out.print(imgsrc);

                        out.print("</button>\n");
                    }
                    else {
                        out.print(CSS_HALF_SPACE);
                    }
                    out.print("</span>");
                    out.print("</div>");
                }
                else {
                    out.print("<span class=\"" + widthClass + "\">");
                    // その項目が非表示の場合
                    if (!hidden) {
                        out.print(
                                "<button " + btnclass + name + " " + style + " " + title + " onclick=\"" + onclick + "\" tabindex=\"" + tabindex + "\" type=\"button\">\n");
                        out.print(imgsrc);
                        out.print("</button>\n");
                    }
                    else {
                        out.print(CSS_HALF_SPACE);
                    }
                    out.print("</span>");
                }
            }
        }
        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
    }

    /**
     * 画像パターン出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @param dict 辞書
     * @throws Exception 例外
     */
    private void outImgPattern(
            JspWriter out,
            Locale userLocale,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt,
            GnomesCTagDictionary dict) throws Exception {
        // スタイル
        String style = ptb.getColumnStyle();

        // パターンID
        String patternId = (String) mapColInfo.get(INFO_PATTERN_ID);
        // イメージクラス
        String imgclass = ptb.getColumnAddClass();

        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        // パラメータ名(パターン値が格納されたパラメータ名, 押したボタンの行数を格納するパラメータ名)
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        if (StringUtil.isNullOrEmpty(infoParamName)) {
        	infoParamName = "";
        }
        String paramNames[] = infoParamName.split(",");

        // Beanからデータを取得
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

        // パターン定義からイメージのタイトルを取得
        //String altResId = (String) mapPtn.get(GnomesCTagDictionary.MAP_NAME_PTN_TITLE);
        //String alt = this.getStringEscapeHtml(ResourcesHandler.getString(altResId, gnomesSessionBean.getUserLocale()));
        String alt = "";

        boolean hidden = false;
        if (mapColInfo.containsKey(INFO_HIDDEN)) {
            hidden = true;
        }

        String buttonTag = "";
        // onclickが設定されている場合、ボタン要素を追加
        if (!StringUtil.isNullOrEmpty(onclick)) {
        	buttonTag = " onclick=\"" + onclick + "\"";
        	if (StringUtil.isNullOrEmpty(style)){
        		style = "";
        	}
        	style += " cursor: pointer;";
        }

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            if (hidden) {
                out.print("<td style=\"display:none\">\n");
            }
            else {
                out.print("<td>");
            }
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1 common-table-provision-icon\">");
            if (!StringUtil.isNullOrEmpty(iconPath)) {
                out.print("<img src=\""+ iconPath + "\" alt=\""+ alt + "\"" + buttonTag);
                if (imgclass != null) {
                    out.print(" class=\""+imgclass+"\"");
                }
                if (!StringUtil.isNullOrEmpty(style)) {
                    out.print(" style=\"" + style + "\"");
                }
                out.print(">\n");
            }
            out.print("</div>\n");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span class=\"" + widthClass + "\">");
                if (!StringUtil.isNullOrEmpty(iconPath)) {
                    out.print("<img src=\""+ iconPath + "\" alt=\""+ alt + "\"" + buttonTag);
                    if (imgclass != null) {
                        out.print(" class=\""+imgclass+"\"");
                    }
                    if(!StringUtil.isNullOrEmpty(style)){
                        out.print(" style=\"" + style + "\"");
                    }
                    out.print(">\n");
                }
                out.print("</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span class=\"" + widthClass + "\">");
                if (!StringUtil.isNullOrEmpty(iconPath)) {
                    out.print("<img src=\""+ iconPath + "\" alt=\""+ alt + "\"" + buttonTag);
                    if (imgclass != null) {
                        out.print(" class=\""+imgclass+"\"");
                    }
                    if (!StringUtil.isNullOrEmpty(style)) {
                        out.print(" style=\"" + style + "\"");
                    }
                    out.print(">\n");
                }
                out.print("</span>");
                out.print("</div>");
            }
            else {
                out.print("<span class=\"" + widthClass + "\">");
                if (!StringUtil.isNullOrEmpty(iconPath)) {
                    out.print("<img src=\""+ iconPath + "\" alt=\""+ alt + "\"" + buttonTag);
                    if (imgclass != null) {
                        out.print(" class=\""+imgclass+"\"");
                    }
                    if (!StringUtil.isNullOrEmpty(style)) {
                        out.print(" style=\"" + style + "\"");
                    }
                    out.print(">\n");
                }
                out.print("</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
    }

    /**
     * テキスト出力
     * @param out 出力先
     * @param dict 辞書
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outText(
            JspWriter out,
            GnomesCTagDictionary dict,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        outTextBase(
            out,
            paramName,
            dict,
            mapColInfo,
            clsData,
            data,
            ptb, rowCnt);
    }

    /**
     * テキスト出力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param dict 辞書
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outTextBase(
            JspWriter out,
            String paramName,
            GnomesCTagDictionary dict,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {

        // Beanから値を取得
        String strVal = "";
        Object value = getData(clsData, data, paramName);
        if (value != null) {
            strVal = value.toString();
        }

        outTextCommon(
                out,
                strVal,
                dict,
                mapColInfo,
                clsData,
                data,
                ptb, rowCnt);

    }

    /**
     * テキスト出力
     * @param out 出力先
     * @param strVal 出力値
     * @param dict 辞書
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outTextCommon(
            JspWriter out,
            String strVal,
            GnomesCTagDictionary dict,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {
        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }

        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        //ボタンに付属させるnameタグ(idタグ)
        String name = outIdNameString(mapColInfo);


        if (strVal.equals("")) {
            strVal = CSS_HALF_SPACE;
        } else {
            strVal = this.getStringEscapeHtmlValue(strVal);
        }

        // パターンID
        String patternId = (String) mapColInfo.get(INFO_PATTERN_ID);
        // パターンIDが指定されている場合、表示値をキーにスタイルクラスを取得
        if(!StringUtil.isNullOrEmpty(patternId)) {
        	styleClass += " " + getPatternValue(dict, patternId, strVal);
        }

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            out.print("<td>\n");
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div " + name + " class=\"common-table-col-1" + styleClass + "\"" + style + ">"+ strVal + "</div>");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span " + name + " class=\"" + widthClass + styleClass + "\"" + style + ">" + strVal + "</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span " + name + " class=\"" + widthClass + styleClass + "\"" + style + ">" + strVal + "</span>");
                out.print("</div>");
            }
            else {
                out.print("<span " + name + " class=\"" + widthClass + styleClass + "\"" + style + ">" + strVal + "</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
    }


    /**
     * 数値出力
     * @param out 出力先
     * @param dict 辞書
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outNumber(
            JspWriter out,
            GnomesCTagDictionary dict,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        outNumberBase(
                out,
                paramName,
                dict,
                mapColInfo,
                clsData,
                data,
                ptb, rowCnt);

    }

    /**
     * 数値出力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param dict 辞書
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @throws Exception 例外
     */
    private void outNumberBase(
            JspWriter out,
            String paramName,
            GnomesCTagDictionary dict,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb, int rowCnt) throws Exception {
        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }

        // 小数点桁数パラメータ名
        String decimalPointName = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_PARAM_NAME);
        // 小数点桁数（固定値）
        String decimalPointValue = (String) mapColInfo
                .get(INFO_DECIMAL_POINT_VALUE);
        Object valueObj = getData(clsData, data, paramName);
        String strNum = "";
        Integer intObj = null;

        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        //ボタンに付属させるnameタグ(idタグ)
        String name = outIdNameString(mapColInfo);

        // 小数点桁数パラメータ名の指定がある場合にBeanより桁数を取得する
        if (decimalPointName != null) {
            intObj = (Integer) (getData(clsData, data, decimalPointName));
        }
        // 小数点桁数（固定値）の指定がある場合に桁数を取得する
        else if (decimalPointValue != null) {
            intObj = Integer.valueOf(decimalPointValue);
        }

        // Beanから取得したデータ、小数点桁数から数値を取得
        strNum = this.getStringEscapeHtmlValue(getStringNumber(this.dictId, paramName, valueObj, intObj));

        if (strNum.equals("")) {
            strNum = CSS_HALF_SPACE;
        }

        // パターンID
        String patternId = (String) mapColInfo.get(INFO_PATTERN_ID);
        // パターンIDが指定されている場合、表示値をキーにスタイルクラスを取得
        if(!StringUtil.isNullOrEmpty(patternId)) {
        	styleClass += " " + getPatternValue(dict, patternId, getStringNumber(this.dictId, paramName, valueObj, null));
        }

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            out.print("<td>\n");
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1 common-text-number" + styleClass + "\"" + style + ">"+ strNum + "</div>");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span " + name + " class=\"" + widthClass + " common-text-number" + styleClass + "\"" + style + ">" + strNum + "</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span " + name + " class=\"" + widthClass + " common-text-number" + styleClass + "\"" + style + ">" + strNum + "</span>");
                out.print("</div>");
            }
            else {
                out.print("<span " + name + " class=\"" + widthClass + " common-text-number" + styleClass + "\"" + style + ">" + strNum + "</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
    }


    /**
     * 日付出力
     * @param out 出力先
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @throws Exception 例外
     */
    private void outDate(
            JspWriter out,
            Locale userLocale,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data, ProcessTableBean ptb) throws Exception {

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
                mapColInfo,
                clsData,
                data, ptb);
    }

    /**
     * 日付出力
     * @param out 出力先
     * @param paramName パラメータ名
     * @param datePattern 日付パターン
     * @param userLocale ユーザーロケール
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @throws Exception 例外
     */
    private void outDateBase(
            JspWriter out,
            String paramName,
            String datePattern,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data, ProcessTableBean ptb) throws Exception {
        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }
        Object valueObj = getData(clsData, data, paramName);
        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        //ボタンに付属させるnameタグ(idタグ)
        String name = outIdNameString(mapColInfo);

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        // 日付情報を取得
        String strDate = this.getStringEscapeHtmlValue(getStringDate(this.dictId, paramName, valueObj, datePattern));

        if (strDate.equals("")) {
            strDate = CSS_HALF_SPACE;
        }

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            out.print("<td>\n");
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1" + styleClass + "\"" + style + ">"+ strDate + "</div>");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span " + name + " class=\"" + widthClass + styleClass + "\"" + style + ">" + strDate + "</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span " + name + " class=\"" + widthClass + styleClass + "\"" + style + ">" + strDate + "</span>");
                out.print("</div>");
            }
            else {
                out.print("<span " + name + " class=\"" + widthClass + styleClass + "\"" + style + ">" + strDate + "</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
    }


    /**
     * プルダウン出力（コード等定数）
     * @param out 出力先
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @param defaultSpace 先頭空白有無
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private void outPullDownConstant(
            JspWriter out,
            Locale userLocale,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb,
            int rowCnt,
            boolean defaultSpace) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        // FormBeanから取得するBean名取得
        String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);

        // FormBeanからプルダウンを取得
        Class<?> clsBean = bean.getClass();
        List<Object> listValue = (List<Object>) getData(clsBean, this.bean, listParamName);

        // FormBeanからプルダウン選択状態を取得
        String selectParamName = (String)mapColInfo.get(INFO_SELECT_PARAM_NAME);

        outPullDownBase(
                out,
                userLocale,
                listValue,
                selectParamName,
                paramName,
                mapColInfo,
                clsData,
                data,
                ptb,
                rowCnt,
                defaultSpace);
    }

    /**
     * プルダウン出力（コード等定数）
     * @param out 出力先
     * @param listValue 選択候補
     * @param selectParamName 選択値パラメータ名
     * @param paramName 読取フラグ確認用パラメータ名
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @param defaultSpace 先頭空白有無
     * @throws Exception 例外
     */
    private void outPullDownBase(
            JspWriter out,
            Locale userLocale,
            List<Object> listValue,
            String selectParamName,
            String paramName,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb,
            int rowCnt,
            boolean defaultSpace) throws Exception {
        // スタイル
        String style = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnStyle())) {
        	style = " style=\"" + ptb.getColumnStyle() + "\"";
        }
        // スタイルクラス
        String styleClass = "";
        if (!StringUtil.isNullOrEmpty(ptb.getColumnAddClass())) {
        	styleClass = " " + ptb.getColumnAddClass();
        }
        // タグ名
        String name = (String)mapColInfo.get(INFO_TAG_NAME);

        if (StringUtil.isNullOrEmpty(selectParamName)) {
        	selectParamName = "";
        }

        String selValue = "";

        // 選択値パラメータ名と差し替え用選択名パラメータ名を分割
        String selectParamNames[] = selectParamName.split(",");

        Object valueObj = null;
        // プルダウン選択項目値を取得
        if (selectParamNames.length >= 1 && !StringUtil.isNullOrEmpty(selectParamNames[0])) {
            valueObj = responseContext.getResponseFormBean(data,
            		selectParamNames[0], rowCnt, name);
        }

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

        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            out.print("<td>\n");
        }

        // スタイル
        String titleLabel = ResourcesHandler.getString(ptb.getResourceId(), userLocale);

        // プルダウン選択テーブルダイアログの出力
        String strOnclick = "onclick=\"MakePullDownTableModal($(this), '" + titleLabel + "');\"";

        // readonlyの場合、選択データを隠し項目として出力し、プルダウン部はdisabledとして表示
        // 入力可能か否か
        String strDisabled = "";
        if (isInputReadOnlyParam(clsData, data, paramName)) {
            out.print("<input type=\"hidden\" name=\""+ name + "\" value=\"" + selValue + "\">");
            strDisabled = " " +INPUT_DISABLED;
            name = name + "_pulldown";// リネーム
        }

        // 入力値変更後の処理
        String strOnchange = getOnChange(mapColInfo);

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1\">");
            out.print("<select name=\"" + name + "\" " + strOnclick + strOnchange + " class=\"common-table-col-select"+ styleClass + "\"" + style + strDisabled + ">");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<select name=\"" + name + "\" " + strOnclick + strOnchange + " class=\"common-table-col-select"+ styleClass + "\"" + style + strDisabled + ">");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<select name=\"" + name + "\" " + strOnclick + strOnchange + " class=\"common-table-col-select" + styleClass + "\"" + style + strDisabled + ">");
            }
            else {
                out.print("<span class=\"" + widthClass + "\">");
                out.print("<select name=\"" + name + "\" " + strOnclick + strOnchange + " class=\"common-table-col-select"+ styleClass + "\"" + style + strDisabled + ">");
            }
        }

        // 先頭空白有りの場合
        if (defaultSpace) {
        	out.print("<option value=\"\"></option>\n");
        }

        // プルダウン内のデータ生成
        outSelectOption(out, listValue, selValue, selName);

        out.print("</select>\n");

        // 先頭位置の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("</div>\n");
        }
        else {
            if (rowNum.equals(row_line_num)) {
                out.print("</span>");
                out.print("</div>\n");
            } else {
                out.print("</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
    }


    /**
     * プルダウン出力（データ項目）
     * @param out 出力先
     * @param mapColInfo 項目辞書
     * @param clsData Beanクラス
     * @param data Beanデータ
     * @param ptb 工程端末一覧情報bean
     * @param rowCnt 行数
     * @param defaultSpace 先頭空白有無
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private void outPullDownData(
            JspWriter out,
            Locale userLocale,
            Map<String, Object> mapColInfo,
            Class<?> clsData,
            Object data,
            ProcessTableBean ptb,
            int rowCnt,
            boolean defaultSpace) throws Exception {

        // パラメータ名
        String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);

        // プルダウン候補パラメータ名
        String listParamName = (String)mapColInfo.get(INFO_LIST_PARAM_NAME);
        Class<?> clsBean = bean.getClass();

        // プルダウン候補パラメータ名のデータをBeanから取得
        List<Object> listValue = (List<Object>) getData(clsBean, this.bean, listParamName);

        // FormBeanからプルダウン選択状態を取得
        String selectParamName = (String)mapColInfo.get(INFO_SELECT_PARAM_NAME);

        outPullDownBase(
                out,
                userLocale,
                listValue,
                selectParamName,
                paramName,
                mapColInfo,
                clsData,
                data,
                ptb,
                rowCnt,
                defaultSpace);

    }

    /**
     * 空白出力
     * @param out 出力先
     * @param ptb 工程端末一覧情報bean
     * @throws Exception 例外
     */
    private void outputBlank(
            JspWriter out,
            ProcessTableBean ptb) throws Exception {

        // 行数（1列あたり）
        String rows = ptb.getRows();

        // 行位置
        String rowpos = ptb.getRowPosition();
        // 行順番
        String rowNum = ptb.getRowNumber();

        // 1行内の列数
        String row_line_num = ptb.getRowLineNum();
        int linunum = Integer.valueOf(row_line_num);

        String strVal = CSS_HALF_SPACE;

        // 先頭位置の場合
        if (rowpos.equals(DEFAULT_POS_ROW_COL) && rowNum.equals(DEFAULT_POS_ROW_COL)) {
            out.print("<td>\n");
        }

        //  1列の場合
        if (linunum == DEFAULT_ROW_NUMBER) {
            out.print("<div class=\"common-table-col-1\">"+ strVal + "</div>");
        }
        //  1列以上の場合
        else {
        	String widthClass = "table-col-" + row_line_num;
            // 行番号が1
            if (rowNum.equals(DEFAULT_POS_ROW_COL)) {
                out.print("<div>");
                out.print("<span class=\"" + widthClass + "\">" + strVal + "</span>");
            }
            // 行番号が最後
            else if (rowNum.equals(row_line_num)) {
                out.print("<span class=\"" + widthClass + "\">" + strVal + "</span>");
                out.print("</div>");
            }
            else {
                out.print("<span class=\"" + widthClass + "\">" + strVal + "</span>");
            }
        }

        if (rowpos.equals(rows) && rowNum.equals(row_line_num)) {
            out.print("</td>\n");
        }
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
        // ","がない。つまり区切られていない場合は
        // nullリターンとする（つまりLength指定なし)
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
        // ","がない。つまり区切られていない場合は固定列の対応とし
        // maxLengthStringの値をそのまま返す
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

    /**
     * 終了処理
     */
    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * 解放処理
     */
    @Override
    public void release() {
    }

}
