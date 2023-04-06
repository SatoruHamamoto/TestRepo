package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;

class GnomesCTagTableOutNumberTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String DIV_STYLE = "padding: 8px 12px;";
    private static final String PATTERN_CLASS = "test_pattern_class";

    /** 変換前データ */
    private static final Double RAW = 3.14159265359;
    /** 小数点桁数（固定値） */
    private static final int FIXED_DP_VAL = 3;
    /** 小数点桁数（Bean） */
    private static final int BEAN_DP_VAL = 2;

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    GnomesCTagDictionary cTagDictionary;
    @Mock
    GnomesSessionBean gnomesSessionBean;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;
    private int dpVal;
    private String strNum;
    private MockedStatic<MessagesHandler> msgHandlerMock;
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
        inOrder = inOrder(out);
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

        doReturn(RAW, BEAN_DP_VAL).when(cTagTable).getData(any(), any(), anyString());
        doReturn(PATTERN_CLASS).when(cTagTable).getPatternValue(any(), anyString(), anyString());

        doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), anyString(), anyString(), anyString(), anyString(), any())).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));

        cTagTable.setDictId("test_dict_id");
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resHandlerMock.close();
    }

    @Test
    @DisplayName("数値出力：小数点桁数パラメータ名がNullの場合小数点桁数（固定値）によって変換された数値が出力されることの確認")
    void test_outNumber_decimalPointName_null() throws Exception {
        mapInfo("test_number", null, FIXED_DP_VAL, "test_tag", "test_pattern", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：小数点桁数（固定値）がNullの場合Beanから取得した桁数によって変換された数値が出力されることの確認")
    void test_outNumber_decimalPointValue_null() throws Exception {
        mapInfo("test_number", "test_decimal", null, "test_tag", "test_pattern", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：タグ名が未設定の場合divタグのname属性が出力されないことの確認")
    void test_outNumber_tagName_empty() throws Exception {
        mapInfo("test_number", "test_decimal", FIXED_DP_VAL, "", "test_pattern", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_outNumber_hidden_true() throws Exception {
        mapInfo("test_number", "test_decimal", FIXED_DP_VAL, "test_tag", "test_pattern", true);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：パターンIDが未設定の場合divタグにパターンクラスが出力されないことの確認")
    void test_outNumber_patternId_empty() throws Exception {
        mapInfo("test_number", "test_decimal", FIXED_DP_VAL, "test_tag", "", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // パターンクラスが出力されない
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + "" + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：スタイルクラスがNullの場合tdタグのclass属性が出力されないことの確認")
    void test_outNumber_styleClass_null() throws Exception {
        mapInfo(null, "test_decimal", FIXED_DP_VAL, "test_tag", "test_pattern", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：tdのスタイルがNullの場合tdタグのstyle属性が出力されないことの確認")
    void test_outNumber_td_style_null() throws Exception {
        mapInfo("test_number", "test_decimal", FIXED_DP_VAL, "test_tag", "test_pattern", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, null, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値出力：divのスタイルがNullの場合divタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_outNumber_div_style_null() throws Exception {
        mapInfo("test_number", "test_decimal", FIXED_DP_VAL, "test_tag", "test_pattern", false);
        setupMockDefaultStringNumber();
        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, null, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + PATTERN_CLASS + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }


    @ParameterizedTest
    @ValueSource(ints = { 1, 4 })
    @DisplayName("数値出力：複数の小数点桁数パターンによって変換された数値が出力されることの確認")
    void test_outNumber_decimalPoint_parameterized(int decimalPoint) throws Exception {
        mapInfo("test_number", null, decimalPoint, "test_tag", "", false);
        // GnomesCTagBase.getStringNumberはモック化しない
        if (decimalPoint == 1) {
            strNum = "3.1";
        } else if (decimalPoint == 4) {
            strNum = "3.1416";
        }

        ReflectionTestUtils.invokeMethod(cTagTable, "outNumberBase", out, "test_param", cTagDictionary, TD_STYLE, DIV_STYLE, mapColInfo, null, null, 3, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-text-number " + "" + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        // 変換された数値が出力される
        inOrder.verify(out, times(1)).print(">" + strNum + "</div>");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String styleClass, String decimalPointName, Integer decimalPointValue, String tagName, String patternId, boolean isHidden) throws Exception {
        mapColInfo = new HashMap<String, Object>();
        dpVal = 0;

        if (styleClass != null) {
            mapColInfo.put(INFO_CLASS, styleClass);
        }
        if (decimalPointName != null) {
            mapColInfo.put(GnomesCTagTable.INFO_DECIMAL_POINT_PARAM_NAME, decimalPointName);
            dpVal = BEAN_DP_VAL;
        }
        if (decimalPointValue != null) {
            mapColInfo.put(GnomesCTagTable.INFO_DECIMAL_POINT_VALUE, String.valueOf(decimalPointValue));
            dpVal = decimalPointValue;
        }
        mapColInfo.put(INFO_TAG_NAME, tagName);
        mapColInfo.put(GnomesCTagTable.INFO_PATTERN_ID, patternId);
        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
    }

    private void setupMockDefaultStringNumber() throws Exception {
        // どちらの小数点桁数が採用されたかを判定
        if (dpVal == FIXED_DP_VAL) {
            strNum = "3.141";
        } else if (dpVal == BEAN_DP_VAL){
            strNum = "3.14";
        }
        doReturn(strNum, "0").when(cTagTable).getStringNumber(anyString(), anyString(), any(), any());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
