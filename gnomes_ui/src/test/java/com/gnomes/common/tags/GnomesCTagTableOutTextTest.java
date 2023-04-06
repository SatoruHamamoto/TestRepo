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

class GnomesCTagTableOutTextTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String DIV_STYLE = "padding: 8px 12px;";

    private static final String PATTERN_CLASS = "test_pattern_class";

    private static final String STR_VALUE = "テスト";

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    GnomesCTagDictionary cTagDictionary;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;

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
        doReturn(PATTERN_CLASS).when(cTagTable).getPatternValue(any(), anyString(), anyString());
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("テキスト出力：パターンIDが未設定の場合divタグのclass属性が空の状態が出力されることの確認")
    void test_outTextCommon_patternId_empty() throws IOException {
        mapInfo("test_text", "", "test_tag", false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outTextCommon", out, cTagDictionary, STR_VALUE, TD_STYLE, DIV_STYLE, mapColInfo);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // class属性が空
        inOrder.verify(out, times(1)).print("<div class=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_VALUE + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト出力：タグ名が未設定の場合divタグのname属性が出力されないことの確認")
    void test_outTextCommon_tagName_empty() throws IOException {
        mapInfo("test_text", "test_pattern", "", false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outTextCommon", out, cTagDictionary, STR_VALUE, TD_STYLE, DIV_STYLE, mapColInfo);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" name=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_VALUE + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト出力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_outTextCommon_hidden_true() throws IOException {
        mapInfo("test_text", "test_pattern", "test_tag", true);
        ReflectionTestUtils.invokeMethod(cTagTable, "outTextCommon", out, cTagDictionary, STR_VALUE, TD_STYLE, DIV_STYLE, mapColInfo);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_VALUE + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト出力：スタイルクラスがNullの場合tdタグのclass属性が出力されないことの確認")
    void test_outTextCommon_styleClass_null() throws IOException {
        mapInfo(null, "test_pattern", "test_tag", false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outTextCommon", out, cTagDictionary, STR_VALUE, TD_STYLE, DIV_STYLE, mapColInfo);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_VALUE + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト出力：tdのスタイルがNullの場合tdタグのstyle属性が出力されないことの確認")
    void test_outTextCommon_td_style_null() throws IOException {
        mapInfo("test_text", "test_pattern", "test_tag", false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outTextCommon", out, cTagDictionary, STR_VALUE, null, DIV_STYLE, mapColInfo);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_VALUE + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("テキスト出力：divのスタイルがNullの場合divタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_outTextCommon_div_style_null() throws IOException {
        mapInfo("test_text", "test_pattern", "test_tag", false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outTextCommon", out, cTagDictionary, STR_VALUE, TD_STYLE, null, mapColInfo);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"" + PATTERN_CLASS + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_VALUE + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String styleClass, String patternId, String tagName, boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        mapColInfo.put(GnomesCTagTable.INFO_PATTERN_ID, patternId);
        mapColInfo.put(INFO_TAG_NAME, tagName);
        if (styleClass != null) {
            mapColInfo.put(INFO_CLASS, styleClass);
        }
        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
    }

}
