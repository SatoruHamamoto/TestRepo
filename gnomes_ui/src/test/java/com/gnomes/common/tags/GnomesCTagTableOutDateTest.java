package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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

class GnomesCTagTableOutDateTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String DIV_STYLE = "padding: 8px 12px;";

    private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final String STR_DATE = "2015/12/31 15:30:30";

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
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

        doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), anyString(), anyString(), anyString(), anyString(), any())).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));

        cTagTable.setDictId("test_dict_id");
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resHandlerMock.close();
    }

    @Test
    @DisplayName("日付出力：タグ名が未設定の場合divタグのname属性が出力されないことの確認")
    void test_outDate_tagName_null() throws Exception {
        mapInfo("test_date", "", false);
        setupMockDefaultStringDate();
        ReflectionTestUtils.invokeMethod(cTagTable, "outDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, DIV_STYLE, mapColInfo, null, null);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div");
        // 出力されない
        inOrder.verify(out, times(0)).print(" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_DATE + "</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付出力：スタイルクラスがNullの場合tdタグのclass属性が出力されないことの確認")
    void test_outDate_styleClass_null() throws Exception {
        mapInfo(null, "test_tag", false);
        setupMockDefaultStringDate();
        ReflectionTestUtils.invokeMethod(cTagTable, "outDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, DIV_STYLE, mapColInfo, null, null);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_DATE + "</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付出力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_outDate_hidden_true() throws Exception {
        mapInfo("test_date", "test_tag", true);
        setupMockDefaultStringDate();
        ReflectionTestUtils.invokeMethod(cTagTable, "outDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, DIV_STYLE, mapColInfo, null, null);

        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<div");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_DATE + "</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付出力：tdのスタイルがNullの場合tdタグのstyle属性が出力されないことの確認")
    void test_outDate_td_style_null() throws Exception {
        mapInfo("test_date", "test_tag", false);
        setupMockDefaultStringDate();
        ReflectionTestUtils.invokeMethod(cTagTable, "outDateBase", out, "test_param", DATE_PATTERN, null, DIV_STYLE, mapColInfo, null, null);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_DATE + "</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("日付出力：divのスタイルがNullの場合divタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_outDate_div_style_null() throws Exception {
        mapInfo("test_date", "test_tag", false);
        setupMockDefaultStringDate();
        ReflectionTestUtils.invokeMethod(cTagTable, "outDateBase", out, "test_param", DATE_PATTERN, TD_STYLE, null, mapColInfo, null, null);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + STR_DATE + "</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd" }
    )
    @DisplayName("日付出力：複数の日付フォーマットによって変換された日付が出力されることの確認")
    void test_outDate_dateFormat_parameterized(String pattern) throws Exception {
        mapInfo("test_date", "test_tag", false);
        // GnomesCTagBase.getStringDateはモック化しない
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(STR_DATE);
        String outDate = sdf.format(date);
        doReturn(outDate).when(cTagTable).getData(any(), any(), anyString());

        ReflectionTestUtils.invokeMethod(cTagTable, "outDateBase", out, "test_param", pattern, TD_STYLE, DIV_STYLE, mapColInfo, null, null);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<div");
        inOrder.verify(out, times(1)).print(" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // フォーマットされた日付が出力される
        inOrder.verify(out, times(1)).print(" style=\"" + DIV_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">" + outDate + "</div>\n");
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

    private void setupMockDefaultStringDate() throws Exception {
        doReturn(STR_DATE).when(cTagTable).getData(any(), any(), anyString());
        doReturn(STR_DATE).when(cTagTable).getStringDate(anyString(), anyString(), any(), anyString());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
