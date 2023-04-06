package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.jsp.JspWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableTbodyTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：スタイル */
    private static final String INFO_STYLE = "style";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";
    /** 辞書：ボタンクラス */
    private static final String INFO_BUTTON_CLASS = "buttonClass";
    /** オートコンプリート */
    private static final String INFO_LIST_AUTOCOMPLETE = "list_autocomplete";

    private static final String ONCLICK_BUTTON_FUNC = "testBtnClick()";

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    GnomesCTagDictionary dict;
    @Mock
    GnomesCTagButtonCommon cTagButtonCommon;
    @Mock
    ContainerResponse responseContext;

    private InOrder inOrder;
    private List<TestFormBean> lstData;
    private Map<String, Object> mapColInfo;
    private Map<String, String> headInfo;
    private MockedStatic<ResourcesHandler> resHandlerMock;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);
        lstData = new ArrayList<>(Arrays.asList(new TestFormBean()));
        inOrder = inOrder(out);

        doReturn(ONCLICK_BUTTON_FUNC).when(cTagButtonCommon).getOnclickButtonAttribute(any(), anyString());
        doReturn(false).when(cTagTable).getSelect(anyList(), any(), any());
        doReturn(false).when(cTagTable).isInputReadOnlyParam(any(), any(), any());
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
        cTagTable.setBean(lstData.get(0));
        cTagTable.setDictId("test_dict_id");
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがチェックボックスの場合チェックボックスタグが出力されることの確認")
    void test_outBody_print_tagType_checkbox() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        List<Map<String, Object>> info = getTableInfo(TagType.CHECKBOX);
        doReturn(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE).when(responseContext).getResponseCheckBoxFormBean(any(),
            anyString(), anyInt(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------- チェックボックス -------
        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + "0" + "\" onclick=\"" + "" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがボタンの場合ボタンが出力されることの確認")
    void test_outBody_print_tagType_button() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.BUTTON);
        doReturn(0).when(cTagTable).getData(any(), any(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ ボタン ------------
        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1))
            .print("        <div class=\"" + "" + "\" style=\"" + (String) mapColInfo.get(INFO_STYLE) + ""
                + "\"" + "" + " onclick=\"" + "setInputValue('"
                + ((String) mapColInfo.get(GnomesCTagTable.INFO_PARAM_NAME)).split(",")[1] + "', " + 1 + ");"
                + ONCLICK_BUTTON_FUNC
                + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + null + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがイメージパターンの場合イメージパターンが出力されることの確認")
    void test_outBody_print_tagType_img_pattern() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.IMG_PATTERN);
        String iconPath = "test_iconPath.png";
        doReturn("test_iconPath").when(cTagTable).getData(any(), any(), anyString());
        doReturn(iconPath).when(cTagTable).getPatternValue(any(), anyString(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------- イメージパターン -------
        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<img src=\"" + iconPath + "\" alt=\"\"" + " onclick=\"" + "setInputValue('"
            + ((String) mapColInfo.get(GnomesCTagTable.INFO_PARAM_NAME)).split(",")[1] + "', " + 1 + ");"
            + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがラベル（テキスト）の場合ラベル（テキスト）が出力されることの確認")
    void test_outBody_print_tagType_text() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.TEXT);
        String textValue = "TEST_TEXT";
        doReturn(textValue).when(cTagTable).getData(any(), any(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ テキスト ----------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(">"+ textValue + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがラベル（数値）の場合ラベル（数値）が出力されることの確認")
    void test_outBody_print_tagType_number() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.NUMBER);
        String strNum = "3000";
        doReturn(strNum).when(cTagTable).getData(any(), any(), anyString());
        doReturn(strNum).when(cTagTable).getStringNumber(anyString(), anyString(), any(), any());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // -------------- 数値 ------------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + "" + "\"");
        inOrder.verify(out, times(1)).print(">"+ strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @ParameterizedTest
    @MethodSource("getSourceDatetime")
    @DisplayName("tbodyタグの出力：タグタイプがラベル（日付）の場合ラベル（日付）が出力されることの確認")
    void test_outBody_print_tagType_datetime(TagType type) throws Exception {
        List<Map<String, Object>> info = getTableInfo(type);
        String strDate = "2015/12/31 15:30:30";
        doReturn(strDate).when(cTagTable).getData(any(), any(), anyString());
        doReturn(strDate).when(cTagTable).getStringDate(anyString(), anyString(), any(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // -------------- 日付 ------------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div");
        inOrder.verify(out, times(1)).print(">" + strDate + "</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがプルダウン（コード等定数）の場合プルダウン（コード等定数）が出力されることの確認")
    void test_outBody_print_tagType_pulldown_code() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.PULLDOWN_CODE);
        List<Object> listValue = new ArrayList<>();
        doReturn(listValue).when(cTagTable).getData(any(), any(), any());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // --- プルダウン（コード等定数） ---
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(1)).print("<option value=\"\"></option>\n"); // 先頭空白有り
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがプルダウン（コード等定数）先頭空白無しの場合プルダウン（コード等定数）先頭空白無しが出力されることの確認")
    void test_outBody_print_tagType_pulldown_code_no_space() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.PULLDOWN_CODE_NO_SPACE);
        List<Object> listValue = new ArrayList<>();
        doReturn(listValue).when(cTagTable).getData(any(), any(), any());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // --- プルダウン（コード等定数） ---
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n"); // 先頭空白無し
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがプルダウン（データ項目）の場合プルダウン（データ項目）が出力されることの確認")
    void test_outBody_print_tagType_pulldown_data() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.PULLDOWN_DATA);
        List<Object> listValue = new ArrayList<>();
        doReturn(listValue).when(cTagTable).getData(any(), any(), any());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ---- プルダウン（データ項目） ----
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + GnomesCTagTable.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print(" data-mode=\"" + (String) mapColInfo.get(INFO_LIST_AUTOCOMPLETE) + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(1)).print("<option value=\"\"></option>\n"); // 先頭空白有り
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがプルダウン（データ項目） 先頭空白無しの場合プルダウン（データ項目） 先頭空白無しが出力されることの確認")
    void test_outBody_print_tagType_pulldown_data_no_space() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.PULLDOWN_DATA_NO_SPACE);
        List<Object> listValue = new ArrayList<>();
        doReturn(listValue).when(cTagTable).getData(any(), any(), any());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ---- プルダウン（データ項目） ----
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + GnomesCTagTable.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print(" data-mode=\"" + (String) mapColInfo.get(INFO_LIST_AUTOCOMPLETE) + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n"); // 先頭空白無し
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプが非表示項目の場合非表示項目が出力されることの確認")
    void test_outBody_print_tagType_hidden() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.HIDDEN);
        String value = "test_hidden";
        doReturn(value).when(cTagTable).getData(any(), any(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ----------- 非表示項目 ----------
        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("    <input type=\"hidden\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME)
            + "\" value=\"" + String.valueOf(value)
            + "\"/>");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがテキスト入力の場合テキスト入力が出力されることの確認")
    void test_outBody_print_tagType_input_text() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.INPUT_TEXT);
        String strVal = "INPUT_TEXT";
        doReturn(strVal).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // --------- テキスト入力 ----------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" +" onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプが数値入力の場合数値入力が出力されることの確認")
    void test_outBody_print_tagType_input_number() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.INPUT_NUMBER);
        String strNum = "3000";
        doReturn(strNum).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(), anyString());
        doReturn(strNum).when(cTagTable).getStringNumber(anyString(), anyString(), any(), any());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ 数値入力 -----------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strNum + "\"" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @ParameterizedTest
    @MethodSource("getSourceInputDatetime")
    @DisplayName("tbodyタグの出力：タグタイプが日付入力の場合日付入力が出力されることの確認")
    void test_outBody_print_tagType_input_datetime(TagType type) throws Exception {
        List<Map<String, Object>> info = getTableInfo(type);
        String strDate = "2015/12/31 15:30:30";
        doReturn(strDate).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ 日時入力 ----------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\" class=\"datetime \"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + (String) mapColInfo.get(GnomesCTagTable.INFO_FORMAT_RESOURCE_ID) + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strDate + "\"" + " onchange=\"" + "setWarningFlag();" +  "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプが表示形式をパラメータから決定の場合データタイプによって入力タグが変化することの確認")
    void test_outBody_print_tagType_input_data_type() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.INPUT_DATA_TYPE);
        String strVal = "INPUT_TEXT";
        // 2の場合テキスト入力タグが出力される
        doReturn(2).when(cTagTable).getData(any(), any(), anyString());
        doReturn(strVal).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // --------- テキスト入力 ----------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプが表示形式をパラメータから決定の場合データタイプによって出力タグが変化することの確認")
    void test_outBody_print_tagType_out_data_type() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.OUTPUT_DATA_TYPE);
        String strVal = "OUTPUT_TEXT";
        // 2の場合テキスト出力タグが出力される
        doReturn(2, strVal).when(cTagTable).getData(any(), any(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // --------- テキスト入力 ----------
        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + "\"");
        inOrder.verify(out, times(1)).print(">"+ strVal + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    @Test
    @DisplayName("tbodyタグの出力：タグタイプがプログレスの場合プログレスが出力されることの確認")
    void test_outBody_print_tagType_progress() throws Exception {
        List<Map<String, Object>> info = getTableInfo(TagType.PROGRESS);
        String[] intStr = { "10", "20" };
        doReturn(intStr[0], intStr[1]).when(cTagTable).getData(any(), any(), anyString());
        ReflectionTestUtils.invokeMethod(cTagTable, "outBody", out, Locale.getDefault(), dict, info, lstData, 3);

        inOrder.verify(out, times(1)).print("<tbody>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ---------- プログレス -----------
        inOrder.verify(out, times(1)).print("<td><div");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(String.format("%3d", Integer.parseInt(intStr[0])) + "%");
        inOrder.verify(out, times(1)).print("<progress value=\"" + intStr[0] + "\" max=\"100\"");
        inOrder.verify(out, times(2)).print("></progress><br />");
        inOrder.verify(out, times(1)).print("</div></td>\n");
        // ---------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</tbody>\n");
    }

    private List<Map<String, Object>> getTableInfo(TagType tagType) {
        List<Map<String, Object>> lstTableInfo = new ArrayList<>();
        Map<String, Object> tr = new HashMap<>();
        mapColInfo = new HashMap<String, Object>();
        headInfo = new HashMap<String, String>();

        mapColInfo.put(INFO_TAG_NAME, "test_tag_name");
        mapColInfo.put(GnomesCTagTable.INFO_TAG_TYPE, tagType != null ? tagType.getType() : null);
        mapColInfo.put(INFO_STYLE, "width: 16px;");
        mapColInfo.put(GnomesCTagTable.INFO_PARAM_NAME, "test_param_name_1,test_param_name_2");
        mapColInfo.put(GnomesCTagTable.INFO_PATTERN_ID, "test_pattern_id");
        mapColInfo.put(INFO_BUTTON_CLASS, "test_button_class");
        mapColInfo.put(GnomesCTagTable.INFO_FORMAT_RESOURCE_ID, "YYYY/MM/DD HH:mm:ss");
        mapColInfo.put(INFO_LIST_AUTOCOMPLETE, "1");
        headInfo.put(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID, "test_label");
        headInfo.put(INFO_CLASS, "test_style");
        headInfo.put(INFO_STYLE, "border: 2px solid;");

        tr.put(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO, mapColInfo);
        tr.put(GnomesCTagDictionary.MAP_NAME_TABLE_INFO, headInfo);
        lstTableInfo.add(tr);
        return lstTableInfo;
    }

    public static Stream<TagType> getSourceDatetime() {
        return Stream.of(TagType.DATE, TagType.ZONEDDATETIME);
    }

    public static Stream<TagType> getSourceInputDatetime() {
        return Stream.of(TagType.INPUT_DATE, TagType.INPUT_ZONEDDATETIME);
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
