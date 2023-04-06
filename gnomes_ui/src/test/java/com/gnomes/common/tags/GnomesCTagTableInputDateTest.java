package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import org.junit.jupiter.params.provider.Arguments;
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

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableInputDateTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String INPUT_STYLE = "padding: 8px 12px;";
    private static final int ROW_COUNT = 3;

    private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_PATTERN_REPLACE = "YYYY/MM/DD HH:mm:ss";
    private static final String DEFAULT_STR_DATE = "2015/12/31 15:30:30";

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

        doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), anyString(), anyString(), anyString(), anyString(), any())).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resHandlerMock.close();
    }

    @Test
    @DisplayName("日付入力：スタイルクラスがNullの場合tdタグのclass属性が出力されないことの確認")
    void test_inputDate_styleClass_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        mapInfo(null, "test_tag", false);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + DEFAULT_STR_DATE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付入力：タグ名が未設定の場合inputタグのname属性が出力されないことの確認")
    void test_inputDate_tagName_empty() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        mapInfo("test_input_date", "", false);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 出力されない
        inOrder.verify(out, times(0)).print(" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + DEFAULT_STR_DATE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付入力：FormBeanから取得した表示データが文字列型の場合フォーマットは行われず出力されることの確認")
    void test_inputDate_formBean_type_string() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        // Thu Dec 31 15:30:30 JST 2015
        String inputStrDate = convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN).toString();
        mapInfo("test_input_date", "test_tag", false);
        setupMockGetResponseFormBean(inputStrDate);
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        // フォーマットされない
        inOrder.verify(out, times(1)).print(" value=\"" + inputStrDate + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private static Stream<Arguments> dateFormatProvider() {
        return Arrays.stream(new Arguments[] {
            Arguments.of("yyyy/MM/dd HH:mm", "2015/12/31 15:30"),
            Arguments.of("yyyy/MM/dd", "2015/12/31"),
            Arguments.of("yyyy/MM", "2015/12")
        });
    }
    @ParameterizedTest
    @MethodSource("dateFormatProvider")
    @DisplayName("日付入力：複数の日付フォーマットによって変換された日付が出力されることの確認")
    void test_inputDate_dateFormat_parameterized(String pattern, String expectedVal) throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        String replaceStr = replaceDateFormat(pattern);
        mapInfo("test_input_date", "test_tag", false);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", pattern, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // 'y'と'd'が大文字に置換された日付フォーマットが出力される
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + replaceStr + "\"");
        // パラメータの日付フォーマットによって変換された日付が出力される
        inOrder.verify(out, times(1)).print(" value=\"" + expectedVal + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付入力：入力不可の場合先頭の値がinputタグに'readonly'と'tabindex=1'が出力されることの確認")
    void test_inputDate_isInputReadOnlyParam_true() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        mapInfo("test_input_date", "test_tag", false);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(true);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        // 'readonly'と'tabindex=1'が出力される
        inOrder.verify(out, times(1)).print(" value=\"" + DEFAULT_STR_DATE + "\"" + " readonly" + " tabindex=\"-1\""
            + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付入力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_inputDate_hidden_true() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        mapInfo("test_input_date", "test_tag", true);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + DEFAULT_STR_DATE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付入力：tdのスタイルがNullの場合tdタグとdivタグのstyle属性が出力されないことの確認")
    void test_inputDate_td_style_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        mapInfo("test_input_date", "test_tag", false);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, null, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + DEFAULT_STR_DATE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付入力：inputのスタイルがNullの場合inputタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_inputDate_input_style_null() throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException, ParseException {
        mapInfo("test_input_date", "test_tag", false);
        setupMockGetResponseFormBean(convertStringToDate(DEFAULT_STR_DATE, DATE_PATTERN));
        setupMockIsInputReadOnlyParam(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "inputDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, null, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div\n");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-date-format=\"" + DATE_PATTERN_REPLACE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + DEFAULT_STR_DATE + "\"" + " onchange=\"" + "setWarningFlag();" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String styleClass, String tagName, boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        mapColInfo.put(INFO_TAG_NAME, tagName);
        if (styleClass != null) {
            mapColInfo.put(INFO_CLASS, styleClass);
        }
        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
    }

    private void setupMockIsInputReadOnlyParam(boolean isInputReadOnlyParam) {
        doReturn(isInputReadOnlyParam).when(cTagTable).isInputReadOnlyParam(any(), any(), any());
    }

    private void setupMockGetResponseFormBean(Object valueObj)
        throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        doReturn(valueObj).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(),
            any());
    }

    private Date convertStringToDate(String strDate, String datePattern) throws ParseException {
        return new SimpleDateFormat(datePattern).parse(strDate);
    }

    private String replaceDateFormat(String pattern) {
        String replaceStr = "";
        switch (pattern) {
        case "yyyy/MM/dd HH:mm":
            replaceStr = "YYYY/MM/DD HH:mm";
            break;
        case "yyyy/MM/dd":
            replaceStr = "YYYY/MM/DD";
            break;
        case "yyyy/MM":
            replaceStr = "YYYY/MM";
            break;
        }
        return replaceStr;
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
