package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
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
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableInputNumberTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";
    /** 辞書：最大桁数 */
    private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";
    /** 辞書：整数入力桁数 */
    private static final String INFO_INPUT_INTEGER_DIGITS = "input_integer_digits";
    /** 辞書：小数点入力桁数 */
    private static final String INFO_INPUT_DECIMAL_DIGITS = "input_decimal_digits";
    /** 辞書：カンマ区切りフォーマットを行うか否か */
    private static final String INFO_INPUT_IS_COMMA_FORMAT = "input_is_comma_format";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String INPUT_STYLE = "padding: 8px 12px;";
    private static final int ROW_COUNT = 3;

    /** 変換前データ(円周率小数11桁) */
    private static final Double RAW = 3.14159265359;
    /** 小数点桁数（Bean） */
    private static final int BEAN_DP_VAL = 2;
    /** 小数点桁数 */
    private static final int DP_VAL = 5;
    /** 小数点入力桁数 */
    private static final int INPUT_DP_VAL = 4;


    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    ContainerResponse responseContext;
    @Mock
    GnomesSessionBean gnomesSessionBean;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;
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
        cTagTable.setBean(new TestFormBean());
        cTagTable.setDictId("test_dict_id");

        doReturn(BEAN_DP_VAL).when(cTagTable).getData(any(), any(), anyString());
        doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), anyString(), anyString(), anyString(), anyString(), any())).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resHandlerMock.close();
    }

    @Test
    @DisplayName("数値入力：タグ名が未設定の場合inputタグのname属性が出力されないかつvalue属性が空で出力されることの確認")
    void test_inputNumber_tagName_empty() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        mapInfo("test_input_number", "", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + "\"" + " maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // value属性が空で出力される
        inOrder.verify(out, times(1)).print(" value=\"" + "" + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：パラメータ名が未設定の場合inputタグのvalue属性が空で出力されることの確認")
    void test_inputNumber_paramName_empty() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + "\"" + " maxlength=\"" + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // value属性が空で出力される
        inOrder.verify(out, times(1)).print(" value=\"" + "" + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：最大入力文字数が未設定の場合inputタグのmaxlength属性が出力されないことの確認")
    void test_inputNumber_inputMaxLength_empty() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // maxlength属性が出力されない
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + "" + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：FormBeanから取得した表示データが文字列型の場合変換は行われず出力されることの確認")
    void test_inputNumber_getResponseFormBean_type_string() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = String.valueOf(RAW);
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(strVal);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                // 次回入力以降の小数点桁数の制限は行う
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // 表示内容は変換されない
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：小数点桁数パラメータ名の指定がある場合Beanより小数点桁数によって表示データの変換が行われることの確認")
    void test_inputNumber_decimalPoint_decimalPointName() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        // Beanから取得した小数点桁数（2桁）で変換される
        String strVal = "3.14";
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 桁数設定がある場合class属性に'number-digit-check'が追加される
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                // 小数点桁数が出力される
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：小数点桁数の指定がある場合指定された小数点桁数によって表示データの変換が行われることの確認")
    void test_inputNumber_decimalPoint_decimalPointValue() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        // 指定した小数点桁数（5桁）で変換される
        String strVal = "3.14159";
        mapInfo("test_input_number", "test_tag", null, String.valueOf(DP_VAL), "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 桁数設定がある場合class属性に'number-digit-check'が追加される
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                // 小数点桁数が出力される
                + String.valueOf(DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：小数点入力桁数の指定がある場合指定された小数点入力桁数によって表示データの変換が行われることの確認")
    void test_inputNumber_decimalPoint_inputDecimalDigits() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        // 指定した小数点入力桁数（4桁）で変換される
        String strVal = "3.1416";
        mapInfo("test_input_number", "test_tag", null, null, "10", "", String.valueOf(INPUT_DP_VAL), "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 桁数設定がある場合class属性に'number-digit-check'が追加される
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                // 小数点入力桁数が出力される
                + String.valueOf(INPUT_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：小数点桁数が未指定の場合指定されたロケールに対する数値フォーマットの小数点桁数によって表示データが変換が行われることの確認")
    void test_inputNumber_decimalPoint_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        // 処理中でNumberFormat.getNumberInstance(Locale)にで取得した数値フォーマットの少数部分の最大桁数（3桁）で変換される
        String strVal = "3.142";
        mapInfo("test_input_number", "test_tag", null, null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 桁数設定が未指定の場合class属性に'number-digit-check'は追加されない
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + "\"" + " maxlength=\""
                // 小数点入力桁数が出力されない
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：整数入力桁数の指定のある場合inputタグに整数入力桁数が出力されることの確認")
    void test_inputNumber_digits_integerDigits() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String integerDigits = "3";
        String strVal = "3.142";
        mapInfo("test_input_number", "test_tag", null, null, "10", integerDigits, "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 桁数設定がある場合class属性に'number-digit-check'が追加される
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                // 整数入力桁数が出力される
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-INTEGER-DIGITS=\"" + integerDigits + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：カンマ区切り場合inputタグのclass属性に'gnomes-number-format'が出力されることの確認")
    void test_inputNumber_inputIsCommaFormat_true() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = "3.142";
        mapInfo("test_input_number", "test_tag", null, null, "10", "", "", "true", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // class属性に'gnomes-number-format'が追加される
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " gnomes-number-format" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：入力不可の場合先頭の値がinputタグに'readonly'と'tabindex=1'が出力されることの確認")
    void test_inputNumber_isInputReadOnlyParam_true() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = "3.14";
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(true);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 入力不可の場合桁数指定があっても追加クラスnumber-digit-checkは出力されない
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // 'readonly'と'tabindex=1'が出力される
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " readonly" + " tabindex=\"-1\""
            + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_inputNumber_hidden_true() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = "3.14";
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", true);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：tdのスタイルがNullの場合tdタグとdivタグのstyle属性が出力されないことの確認")
    void test_inputNumber_td_style_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = "3.14";
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", null, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：inputのスタイルがNullの場合inputタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_inputNumber_input_style_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = "3.14";
        mapInfo("test_input_number", "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, null, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("数値入力：スタイルクラスがNullの場合tdタグのclass属性が出力されないことの確認")
    void test_inputNumber_styleClass_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        String strVal = "3.14";
        mapInfo(null, "test_tag", "test_decimal", null, "10", "", "", "", false);
        setupMockGetResponseFormBean(RAW);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputNumberBase", out, "test_param", TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input class=\"common-text-number " + " number-digit-check" + "\"" + " maxlength=\""
                + (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH) + "\"" + " data-DECIMAL-DIGITS=\""
                + String.valueOf(BEAN_DP_VAL) + "\"");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + strVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"" + ">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String styleClass,
        String tagName,
        String decimalPointName, String decimalPointValue,
        String maxLength,
        String inputIntegerDigits,
        String inputDecimalDigits,
        String inputIsCommaFormat,
        boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        if (styleClass != null) {
            mapColInfo.put(INFO_CLASS, styleClass);
        }
        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
        if (decimalPointName != null) {
            mapColInfo.put(GnomesCTagTable.INFO_DECIMAL_POINT_PARAM_NAME, decimalPointName);
        }
        if (decimalPointValue != null) {
            mapColInfo.put(GnomesCTagTable.INFO_DECIMAL_POINT_VALUE, decimalPointValue);
        }
        mapColInfo.put(INFO_TAG_NAME, tagName);
        mapColInfo.put(INFO_INPUT_MAX_LENGTH, maxLength);
        mapColInfo.put(INFO_INPUT_INTEGER_DIGITS, inputIntegerDigits);
        mapColInfo.put(INFO_INPUT_DECIMAL_DIGITS, inputDecimalDigits);
        mapColInfo.put(INFO_INPUT_IS_COMMA_FORMAT, inputIsCommaFormat);
    }

    private void setupMockIsInputReadOnlyParam(boolean isInputReadOnlyParam) {
        doReturn(isInputReadOnlyParam).when(cTagTable).isInputReadOnlyParam(any(), any(), any());
    }

    private void setupMockGetResponseFormBean(Object valueObj)
        throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        doReturn(valueObj).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(),
            any());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
