package com.gnomes.common.tags;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.jsp.JspWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.system.view.TestFormBean;

class GnomesCTagSearchMenuOutSearchMenuItemTest {

    private static final String SEARCH_TABLE_TAG_ID = "test_FilterTableMenu";

    @Spy
    @InjectMocks
    GnomesCTagSearchMenu cTagSearchMenu;

    @Mock
    JspWriter out;
    @Mock
    GnomesSessionBean gnomesSessionBean;

    private InOrder inOrder;
    private TestFormBean bean;
    private MstSearchInfo searchMasterInfo;
    private SearchSetting searchInfo;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        inOrder = inOrder(out);
        bean = new TestFormBean();
        bean.setUserLocale(Locale.JAPAN);
        cTagSearchMenu.setBean(bean);
        cTagSearchMenu.setDictId("test_dict_id");

        doReturn(bean.getUserLocale()).when(gnomesSessionBean).getUserLocale();
        initMockGetSearchSetting(new String[] { "100", "1000" }, new String[] { "pattern_01", "pattern_02" });
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが文字入力 (含む、から始まるなど）の場合文字検索 (含む、から始まるなど）ボックスが出力されることの確認")
    @ValueSource(booleans = { false, true })
    void test_outSearchMenuItem_conditionType_string_like(boolean isPanecon) throws Exception {
        ConditionType type = ConditionType.STRING_LIKE;

        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが文字プルダウンの場合文字検索プルダウンが出力されることの確認")
    @ValueSource(booleans = { false, true })
    void test_outSearchMenuItem_conditionType_string_pulldown(boolean isPanecon) throws Exception {
        ConditionType type = ConditionType.STRING_PULLDOWN;

        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    private static Stream<Arguments> stringPullDownAutocompleteProvider() {
        return Arrays.stream(new Arguments[] {
            Arguments.of(ConditionType.STRING_PULLDOWN_AUTOCOMPLETE, false),
            Arguments.of(ConditionType.STRING_PULLDOWN_AUTOCOMPLETE, true),
            Arguments.of(ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_1, false),
            Arguments.of(ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_1, true),
            Arguments.of(ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_2, false),
            Arguments.of(ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_2, true),
        });
    }
    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが文字 プルダウン(オートコンプリート有り) value + text 表示の場合文字検索プルダウン(オートコンプリート有り) value + text 表示が出力されることの確認")
    @MethodSource("stringPullDownAutocompleteProvider")
    void test_outSearchMenuItem_conditionType_string_pulldown_autocomplete(ConditionType type, boolean isPanecon) throws Exception {
        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが数値プルダウンの場合数値検索プルダウンが出力されることの確認")
    @ValueSource(booleans = { false, true })
    void test_outSearchMenuItem_conditionType_number_pulldown(boolean isPanecon) throws Exception {
        ConditionType type = ConditionType.NUMBER_PULLDOWN;

        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    private static Stream<Arguments> numberPullDownAutocompleteProvider() {
        return Arrays.stream(new Arguments[] {
            Arguments.of(ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE, false),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE, true),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_1, false),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_1, true),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_2, false),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_2, true),
        });
    }
    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが数値プルダウン(オートコンプリート有り)の場合数値検索プルダウン(オートコンプリート有り)が出力されることの確認")
    @MethodSource("numberPullDownAutocompleteProvider")
    void test_outSearchMenuItem_conditionType_number_pulldown_autocomplete(ConditionType type, boolean isPanecon) throws Exception {
        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが数値入力の場合数値検索ボックスが出力されることの確認")
    @ValueSource(booleans = { false, true })
    void test_outSearchMenuItem_conditionType_number(boolean isPanecon) throws Exception {
        ConditionType type = ConditionType.NUMBER;

        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    @ParameterizedTest
    @Disabled
    @ValueSource(booleans = { false, true })
    @DisplayName("検索メニュー項目表示出力：条件タイプがfrom (数値 プルダウン) to (数値 プルダウン)の場合数値検索プルダウンfrom (数値 プルダウン) to (数値 プルダウン)が出力されることの確認")
    void test_outSearchMenuItem_conditionType_number_pulldown_range(boolean isPanecon) throws Exception {
        ConditionType type = ConditionType.NUMBER_PULLDOWN_RANGE;

        initMockGetSearchSetting(new String[] { "pattern_01", "pattern_02" }, new String[] { "pattern_01", "pattern_02" });
        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    private static Stream<Arguments> numberPullDownRangeAutocompleteProvider() {
        return Arrays.stream(new Arguments[] {
            Arguments.of(ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE, false),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE, true),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1, false),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1, true),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2, false),
            Arguments.of(ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2, true),
        });
    }
    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプがfrom (数値 プルダウン) to (数値 プルダウン)(オートコンプリート有り)の場合数値検索プルダウンfrom (数値 プルダウン) to (数値 プルダウン)(オートコンプリート有り)が出力されることの確認")
    @MethodSource("numberPullDownRangeAutocompleteProvider")
    void test_outSearchMenuItem_conditionType_number_pulldown_range_autocomplete(ConditionType type, boolean isPanecon) throws Exception {
        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    private static Stream<Arguments> dateProvider() {
        return Arrays.stream(new Arguments[] {
            Arguments.of(ConditionType.DATE, "2015/12/01", "2015/12/31"),
            Arguments.of(ConditionType.MULTIFORMAT_DATESTR, "2015/12/01", "2015/12/31"),
            Arguments.of(ConditionType.DATE_YM, "2015/11", "2015/12"),
            Arguments.of(ConditionType.DATE_TIME, "2015/12/01 00:00", "2015/12/31 23:59"),
            Arguments.of(ConditionType.DATE_TIME_SS, "2015/12/01 00:00:00", "2015/12/31 23:59:59"),
        });
    }
    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプが日付の場合日付検索ボックスが出力されることの確認")
    @MethodSource("dateProvider")
    void test_outSearchMenuItem_conditionType_date(ConditionType type, String dateFrom, String dateTo) throws Exception {
        initMockGetSearchSetting(new String[] { dateFrom, dateTo }, new String[] { "pattern_01", "pattern_02" });
        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, false, bean.getUserLocale());

        verifyOut(type, false);
    }

    @ParameterizedTest
    @Disabled
    @DisplayName("検索メニュー項目表示出力：条件タイプがチェックボックスの場合チェックボックスが出力されることの確認")
    @ValueSource(booleans = { false, true })
    void test_outSearchMenuItem_conditionType_checkbox(boolean isPanecon) throws Exception {
        ConditionType type = ConditionType.CHECKBOX;

        initMockGetSearchSetting(new String[] { "1", "0" }, new String[] { "pattern_01", "pattern_03" });
        initMockGetSearchMenuInfo(type);
        cTagSearchMenu.outSearchMenuItem(out, SEARCH_TABLE_TAG_ID, searchMasterInfo, searchInfo, isPanecon, bean.getUserLocale());

        verifyOut(type, isPanecon);
    }

    private void verifyOut(ConditionType type, boolean isPanecon) throws IOException {
        ConditionInfo conditionInfo = searchInfo.getConditionInfos().get(0);
        String textDialogClass = "";
        String pulldownDialogOnclick = "";
        String numberDialogClass = "";
        String dateDialogClass = "datetime";
        String checkboxClass = "";
        String checkboxStyle = "style=\"width: 1em;\"";

        String columnId = conditionInfo.getColumnId();
        String columnName = SEARCH_TABLE_TAG_ID + "_" + columnId;

        MstCondition mstCondition = searchMasterInfo.getMstCondition(columnId);
        String text = mstCondition.getText();
        LinkedHashMap<String, String> patterns = mstCondition.getPatterns();
        List<String> parametars = conditionInfo.getParameters();
        List<String> patternKeys = conditionInfo.getPatternKeys();

        if (isPanecon) {
            textDialogClass = "common-keyboard-input-char";
            pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + text + "');\"";
            numberDialogClass = "common-keyboard-input-num";
            checkboxClass = "common-input-checkbox";
            checkboxStyle = "style=\"width: 3em;\"";
        }

        String mode = "";
        String dateFrom ="";
        String dateTo ="";

        inOrder.verify(out, times(1)).print("    <ul>\n");
        inOrder.verify(out, times(1)).print("<li>");

        switch (type) {
            // 文字入力＋プルダウン入力(含む、から始まるなど）
            case STRING_LIKE:
                if (isPanecon) {
                    pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + getLabel(GnomesResourcesConstants.YY01_0085) + "');\"";
                }

                inOrder.verify(out, times(1)).print(text + "\n");
                inOrder.verify(out, times(1)).print(
                    "  <input type=\"text\" name=\""+ columnName + "_value\" class=\"" + textDialogClass + "\" value=\"" + parametars.get(0) + "\">\n");
                inOrder.verify(out, times(1)).print("      <select name=\""+ columnName + "_condition\" " + pulldownDialogOnclick + ">\n");
                for (int i = 0; i < patterns.size(); i++) {
                    String strIndex = String.valueOf(i);
                    if (patternKeys.size() > 0 && patternKeys.get(0).equals(strIndex)) {
                        inOrder.verify(out, times(1)).print("              <option value=\"" + strIndex
                            + "\" selected=\"selected\">" + patterns.get(strIndex) + "</option>\n");
                    } else {
                        inOrder.verify(out, times(1)).print("              <option value=\"" + strIndex + "\">"
                            + patterns.get(strIndex) + "</option>\n");
                    }
                }
                inOrder.verify(out, times(1)).print("      </select>\n");
                break;

            // プルダウン入力（コード等定数）
            case STRING_PULLDOWN:
                inOrder.verify(out, times(1)).print(text + "\n");
                inOrder.verify(out, times(1)).print("  <select name=\""+ columnName + "_value\" " + pulldownDialogOnclick + ">\n");

                veriryOutPulldownOption(patterns, patternKeys, 0);

                inOrder.verify(out, times(1)).print("  </select>\n");
                break;

            // プルダウン入力（オートコンプリート有り）
            case STRING_PULLDOWN_AUTOCOMPLETE:
            case STRING_PULLDOWN_AUTOCOMPLETE_1:
            case STRING_PULLDOWN_AUTOCOMPLETE_2:
                mode = getMode(type);
                inOrder.verify(out, times(1)).print(text + "\n");
                inOrder.verify(out, times(1))
                    .print("  <select name=\"" + columnName + "_value\" class=\"" + GnomesCTagBase.CLASS_AUTO_COMBO
                        + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick + ">\n");

                veriryOutPulldownOption(patterns, patternKeys, 0);

                inOrder.verify(out, times(1)).print("  </select>\n");
                break;

            // プルダウン入力(数値）
            case NUMBER_PULLDOWN:
                inOrder.verify(out, times(1)).print(text + "\n");
                inOrder.verify(out, times(1)).print("  <select name=\""+ columnName + "_value\" " + pulldownDialogOnclick + ">\n");

                veriryOutPulldownOption(patterns, patternKeys, 0);

                inOrder.verify(out, times(1)).print("      </select>\n");
                break;

            // プルダウン入力(数値）（オートコンプリート有り）
            case NUMBER_PULLDOWN_AUTOCOMPLETE:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_2:
                mode = getMode(type);
                inOrder.verify(out, times(1)).print(text + "\n");
                inOrder.verify(out, times(1))
                    .print("  <select name=\"" + columnName + "_value\" class=\"width-100 "
                        + GnomesCTagBase.CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick
                        + ">\n");

                veriryOutPulldownOption(patterns, patternKeys, 0);

                inOrder.verify(out, times(1)).print("      </select>\n");
                break;

            // 数値入力(範囲)
            case NUMBER:
                inOrder.verify(out, times(1)).print(text + "&nbsp;From<br>\n");
                inOrder.verify(out, times(1))
                    .print("  <input type=\"text\" name=\"" + columnName
                        + "_from\"  class=\"common-text-number gnomes-number gnomes-number-format " + numberDialogClass
                        + "\" value=\"" + formatNumberString(parametars.get(0)) + "\">\n");
                inOrder.verify(out, times(1)).print("</li>\n");
                inOrder.verify(out, times(1)).print("<li>" + text + "&nbsp;To&nbsp;&nbsp;<br>\n");
                inOrder.verify(out, times(1))
                    .print("  <input type=\"text\" name=\"" + columnName
                        + "_to\" class=\"common-text-number gnomes-number gnomes-number-format " + numberDialogClass
                        + "\" value=\"" + formatNumberString(parametars.get(1)) + "\">\n");
                break;

            // from (数値 プルダウン) to (数値 プルダウン)
            case NUMBER_PULLDOWN_RANGE:
                if (isPanecon) {
                    pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + text + "&nbsp;From" + "');\"";
                }

                inOrder.verify(out, times(1)).print(text + "&nbsp;From<br>\n");
                inOrder.verify(out, times(1)).print("  <select name=\""+ columnName + "_from\" " + pulldownDialogOnclick + ">\n");

                veriryOutPulldownOption(patterns, parametars, 0);

                inOrder.verify(out, times(1)).print("      </select>\n");
                inOrder.verify(out, times(1)).print("</li>\n");

                if (isPanecon) {
                    pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + text + "&nbsp;To" + "');\"";
                }

                inOrder.verify(out, times(1)).print("<li>" + text + "&nbsp;To&nbsp;&nbsp;<br>\n");
                inOrder.verify(out, times(1)).print("  <select name=\""+ columnName + "_to\" " + pulldownDialogOnclick + ">\n");

                veriryOutPulldownOption(patterns, parametars, 1);

                inOrder.verify(out, times(1)).print("      </select>\n");
                break;

            // from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り）
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2:
                mode = getMode(type);
                if (isPanecon) {
                    pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + text + "&nbsp;From" + "');\"";
                }

                inOrder.verify(out, times(1)).print(text + "&nbsp;From<br>\n");
                inOrder.verify(out, times(1))
                    .print("  <select name=\"" + columnName + "_from\" class=\"width-100 "
                        + GnomesCTagBase.CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick
                        + ">\n");

                veriryOutPulldownOption(patterns, parametars, 0);

                inOrder.verify(out, times(1)).print("      </select>\n");
                inOrder.verify(out, times(1)).print("</li>\n");

                if (isPanecon) {
                    pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + text + "&nbsp;To" + "');\"";
                }

                inOrder.verify(out, times(1)).print("<li>" + text + "&nbsp;To&nbsp;&nbsp;<br>\n");
                inOrder.verify(out, times(1)).print(
                    "  <select name=\"" + columnName + "_to\" class=\"width-100 " + GnomesCTagBase.CLASS_AUTO_COMBO
                        + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick + ">\n");

                veriryOutPulldownOption(patterns, parametars, 1);

                inOrder.verify(out, times(1)).print("      </select>\n");
                break;

            // 日付(範囲)(年月日
            case DATE:
            case MULTIFORMAT_DATESTR:
            // 日付(範囲)(年月
            case DATE_YM:
            // 日時 (範囲)(年月日時間（分まで
            case DATE_TIME:
            // 日時 (年月日時間（秒まで
            case DATE_TIME_SS:
                veriryOutDateRange(type, text, columnName, dateDialogClass, parametars, dateFrom, dateTo);
                break;

            // チェックボックス
            case CHECKBOX:
                inOrder.verify(out, times(1)).print(text + "<br>\n");
                for (Map.Entry<String, String> pattern : patterns.entrySet()) {
                    if (patternKeys.contains(pattern.getKey())) {
                        inOrder.verify(out, times(1))
                            .print("    <label style=\"vertical-align: middle;\"><input name=\"" + columnName
                                + "_check\" class=\"" + checkboxClass + "\" " + checkboxStyle
                                + " type=\"checkbox\" value=\"" + pattern.getKey() + "\" checked><span>&nbsp;"
                                + pattern.getValue() + "</span></label><br>\n");
                    } else {
                        inOrder.verify(out, times(1)).print("    <label style=\"vertical-align: middle;\"><input name=\""
                            + columnName + "_check\" class=\"" + checkboxClass + "\" " + checkboxStyle
                            + " type=\"checkbox\" value=\"" + pattern.getKey() + "\"><span>&nbsp;" + pattern.getValue()
                            + "</span></label><br>\n");
                    }
                }
                break;

            default:
        }

        inOrder.verify(out, times(1)).print("</li>\n");
        inOrder.verify(out, times(1)).print("    </ul>\n");
    }

    private void veriryOutPulldownOption(LinkedHashMap<String, String> patterns, List<String> keys, int index) throws IOException {
        inOrder.verify(out, times(1)).print("    <option value=\"\"></option>\n");

        for (Map.Entry<String, String> pattern : patterns.entrySet()) {
            if (keys.get(index).equals(pattern.getKey())) {
                inOrder.verify(out, times(1)).print("              <option value=\"" + pattern.getKey()
                    + "\" selected=\"selected\">" + pattern.getValue() + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("              <option value=\"" + pattern.getKey() + "\">"
                    + pattern.getValue() + "</option>\n");
            }
        }
    }

    /**
     * 日付検索ボックス出力確認
     * @param type 条件タイプ
     * @param text 表示カラム名
     * @param columnName カラム名
     * @param dateDialogClass 日付入力補助ダイアログ呼出クラス名
     * @param parameters 選択肢
     * @param dateFrom 日付（From）
     * @param dateTo 日付（To）
     * @throws IOException
     */
    private void veriryOutDateRange(ConditionType type,
        String text,
        String columnName,
        String dateDialogClass,
        List<String> parameters,
        String dateFrom,
        String dateTo) throws IOException {
        String resourceId = "";
        switch (type) {
            case DATE:
            case MULTIFORMAT_DATESTR:
                resourceId = GnomesResourcesConstants.YY01_0073;
                break;
            case DATE_YM:
                resourceId = GnomesResourcesConstants.YY01_0095;
                break;
            case DATE_TIME:
                resourceId = GnomesResourcesConstants.YY01_0045;
                break;
            case DATE_TIME_SS:
                resourceId = GnomesResourcesConstants.YY01_0096;
                break;
            default:
        }

        String dateFormat = getLabel(resourceId);

        inOrder.verify(out, times(1)).print(text + "&nbsp;From " + dateFrom + "<br>\n");
        inOrder.verify(out, times(1))
            .print("  <input name=\"" + columnName + "_from\" class=\"" + dateDialogClass
                + "\" data-date-format=\"" + dateFormat + "\" type=\"text\" value=\"" + parameters.get(0) + "\">\n");

        inOrder.verify(out, times(1)).print("</li>\n");
        inOrder.verify(out, times(1)).print("<li>" + text + "&nbsp;To&nbsp;&nbsp; " + dateTo + "<br>\n");
        inOrder.verify(out, times(1))
            .print("  <input name=\"" + columnName + "_to\" class=\"" + dateDialogClass
                + "\" data-date-format=\"" + dateFormat + "\" type=\"text\" value=\"" + parameters.get(1) + "\">\n");
    }

    private void initMockGetSearchSetting(String[] params, String[] keys) {
        searchInfo = new SearchSetting();

        List<ConditionInfo> conditionInfos = new ArrayList<>();
        ConditionInfo conditionInfo = new ConditionInfo();

        List<String> parametars = new ArrayList<>();
        parametars.add(params[0]);
        parametars.add(params[1]);

        List<String> patternKeys = new ArrayList<>();
        patternKeys.add(keys[0]);
        patternKeys.add(keys[1]);

        conditionInfo.setHiddenItem(false);
        conditionInfo.setColumnId("test_column");
        conditionInfo.setPatternKeys(patternKeys);
        conditionInfo.setParameters(parametars);

        conditionInfos.add(conditionInfo);
        searchInfo.setConditionInfos(conditionInfos);
    }

    private void initMockGetSearchMenuInfo(ConditionType type) {
        searchMasterInfo = new MstSearchInfo();

        List<MstCondition> mstConditions = new ArrayList<>();
        MstCondition mstCondition = new MstCondition();

        LinkedHashMap<String, String> patterns = new LinkedHashMap<>();
        patterns.put("pattern_01", "include");
        patterns.put("pattern_02", "begin with");

        List<ConditionParamSaveType> saveTypes = new ArrayList<>();
        saveTypes.add(ConditionParamSaveType.SAVE);
        saveTypes.add(ConditionParamSaveType.SAVE);

        mstCondition.setType(type);
        mstCondition.setColumnName(searchInfo.getConditionInfos().get(0).getColumnId());
        mstCondition.setText("Column");
        mstCondition.setPatterns(patterns);
        mstCondition.setSaveParamTypes(saveTypes);

        mstConditions.add(mstCondition);
        searchMasterInfo.setMstConditions(mstConditions);
    }

    private String getLabel(String resourceId) {
        String strLabel = "";

        switch (resourceId) {
            case GnomesResourcesConstants.YY01_0085:
                strLabel = "曖昧検索";
                break;
            case GnomesResourcesConstants.YY01_0073:
                strLabel = "YYYY/MM/DD";
                break;
            case GnomesResourcesConstants.YY01_0095:
                strLabel = "YYYY/MM";
                break;
            case GnomesResourcesConstants.YY01_0045:
                strLabel = "YYYY/MM/DD HH:mm";
                break;
            case GnomesResourcesConstants.YY01_0096:
                strLabel = "YYYY/MM/DD HH:mm:ss";
                break;
            default:
        }
        return strLabel;
    }

    private String getMode(ConditionType type) {
        String mode = "";
        switch (type) {
            case STRING_PULLDOWN_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1:
                mode = GnomesCTagBase.AUTO_MODE_VALUE;
                break;
            case STRING_PULLDOWN_AUTOCOMPLETE_2:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_2:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2:
                mode = GnomesCTagBase.AUTO_MODE_TEXT;
                break;
            default:
                mode = GnomesCTagBase.AUTO_MODE_VALUE_TEXT;
        }
        return mode;
    }

    private String formatNumberString(String value) {
        return String.format("%,d", Integer.parseInt(value));
    }

}
