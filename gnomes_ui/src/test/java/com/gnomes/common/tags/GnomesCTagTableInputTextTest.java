package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableInputTextTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";
    /** 辞書：最大桁数 */
    private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";

    private static final int COLUMN_CLASS_INDEX_TD = 0;
    private static final int COLUMN_CLASS_INDEX_INNER = 1;

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String INPUT_STYLE = "padding: 8px 12px;";
    private static final int ROW_COUNT = 3;

    private static final String MAX_LENGTH_BEFORE_COMMA = "40";
    private static final String INPUT_TEXT_VALUE = "テスト";

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    ContainerResponse responseContext;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;
    private String[] styleClasses;

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
        cTagTable.setBean(new TestFormBean());
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("テキスト入力：タグ名が未設定の場合inputタグのname属性が出力されないことの確認")
    void test_inputText_tagName_empty() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "", "20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：FormBeanから表示データが取得できなかった場合inputタグのvalue属性が空で出力されることの確認")
    void test_inputText_value_empty() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", "20", false);
        setupMockGetResponseFormBean(null);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // valueが空で出力される
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：スタイルクラス配列の長さが1の場合inputタグのclass属性が出力されないことの確認")
    void test_inputText_styleClasses_length_one() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(1, "test_tag", "20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：スタイルクラス配列の長さが0の場合tdタグとinputタグのclass属性が出力されないことの確認")
    void test_inputText_styleClasses_length_zero() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(0, "test_tag", "20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：最大入力桁数が未設定の場合inputタグのmaxLength属性が出力されないことの確認")
    void test_inputText_inputMaxLength_empty() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", "", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：可変型データタイプかつ最大入力桁数がカンマ区切りの場合先頭の値がinputタグのmaxLength属性に出力されることの確認")
    void test_inputText_isMultiType_true_inputMaxLength_comma() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", MAX_LENGTH_BEFORE_COMMA + ",20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, true);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + MAX_LENGTH_BEFORE_COMMA + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_inputText_hidden_true() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", "20", true);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：入力不可の場合先頭の値がinputタグに'readonly'と'tabindex=1'が出力されることの確認")
    void test_inputText_iisInputReadOnlyParam_true() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", "20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(true);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // 'readonly'と'tabindex=1'が出力される
        verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " value=\""
                + INPUT_TEXT_VALUE + "\"" + " readonly" + " tabindex=\"-1\"" + " onchange=\"" + "setWarningFlag();"
                + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：tdのスタイルがNullの場合tdタグとdivタグのstyle属性が出力されないことの確認")
    void test_inputText_td_style_null() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", "20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", null, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト入力：inputのスタイルがNullの場合inputタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_inputText_input_style_null() throws IOException, IllegalArgumentException, IllegalAccessException, GnomesAppException {
        mapInfo(2, "test_tag", "20", false);
        setupMockGetResponseFormBean(INPUT_TEXT_VALUE);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputTextBase", out, "test_param", TD_STYLE, null, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_TD] + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"text\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + styleClasses[COLUMN_CLASS_INDEX_INNER] + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\""  + " value=\""
            + INPUT_TEXT_VALUE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(int classLength, String tagName, String maxLength, boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classLength; i++) {
            sb.append("test_input_text_");
            sb.append(i);
            if (i != (classLength - 1)) {
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            String classes = sb.toString();
            styleClasses = classes.split(",");
            mapColInfo.put(INFO_CLASS, classes);
        }

        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
        mapColInfo.put(INFO_TAG_NAME, tagName);
        mapColInfo.put(INFO_INPUT_MAX_LENGTH, maxLength);
    }

    private void setupMockIsInputReadOnlyParam(boolean isInputReadOnlyParam) {
        doReturn(isInputReadOnlyParam).when(cTagTable).isInputReadOnlyParam(any(), any(), any());
    }

    private void setupMockGetResponseFormBean(String valueObj)
        throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        doReturn(valueObj).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(),
            any());
    }

}
