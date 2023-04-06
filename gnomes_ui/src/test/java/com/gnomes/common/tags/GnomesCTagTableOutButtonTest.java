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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableOutButtonTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：スタイル */
    private static final String INFO_STYLE = "style";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";
    /** 辞書：オンクリック */
    private static final String INFO_ON_CLICK = "onclick";
    /** 辞書：画像ソース */
    private static final String INFO_IMG_SRC = "imgsrc";
    /** 辞書：タイトル */
    private static final String INFO_TITLE = "title";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String DIV_STYLE = "padding: 8px 12px;";
    private static final String INPUT_STYLE = "border: 1px solid;";
    private static final int ROW_COUNT = 3;

    private static final String DEFAULT_BUTTON_FUNC = "defaultBtn();";
    private static final String BUTTON_FUNC = "testBtn();";
    private static final String BUTTON_SRC = "../images/test.png";

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    ContainerResponse responseContext;
    @Mock
    GnomesCTagButtonCommon cTagButtonCommon;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;
    private MockedStatic<ResourcesHandler> resHandlerMock;
    private String[] paramNames;

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
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

        doReturn(BUTTON_FUNC).when(cTagButtonCommon).getOnclickButtonAttribute(any(), anyString());
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));

        cTagTable.setDictId("test_dict_id");
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

//    private void outButton(
//        JspWriter out,
//        Locale userLocale,
//        String style,String colStyle,//追加
//        Map<String, Object> mapColInfo,
//        Class<?> clsData,
//        Object data,
//        int rowCnt,
//        boolean isLastDispCol)

    @Test
    @DisplayName("ボタン出力：タイトルリソースIDがNullの場合divタグのtitle属性が出力されないことの確認")
    void test_outButton_titleResouceId_null() throws Exception{
        mapInfo("test_name", "test_class", DEFAULT_BUTTON_FUNC, BUTTON_SRC, DIV_STYLE, null, 2, false);
        setupMock(0);
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // title属性が出力されない
        inOrder.verify(out, times(1)).print("        <div class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\" style=\"" + DIV_STYLE + ""
            + "\"" + "" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "+ String.valueOf(ROW_COUNT + 1) + ");" + BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("ボタン出力：ボタンクラスが未設定の場合divタグのclass属性が空の状態で出力されることの確認")
    void test_outButton_buttonClass_empty() throws Exception{
        mapInfo("test_name", "", DEFAULT_BUTTON_FUNC, BUTTON_SRC, DIV_STYLE, "test_title", 2, false);
        setupMock(0);
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // class属性が空
        inOrder.verify(out, times(1)).print("        <div class=\"" + "" + "\" style=\"" + DIV_STYLE + ""
            + "\"" + " title=\"" + (String) mapColInfo.get(INFO_TITLE) + "\"" + " onclick=\"" + "setInputValue('"
            + paramNames[1] + "', " + String.valueOf(ROW_COUNT + 1) + ");" + BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("ボタン出力：divタグのスタイルが未設定の場合divタグのstyle属性が空の状態で出力されることの確認")
    void test_outButton_div_style_empty() throws Exception{
        mapInfo("test_name", "test_class", DEFAULT_BUTTON_FUNC, BUTTON_SRC, "", "test_title", 2, false);
        setupMock(0);
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // style属性が空
        inOrder.verify(out, times(1)).print("        <div class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\" style=\"" + "" + ""
            + "\"" + " title=\"" + (String) mapColInfo.get(INFO_TITLE) + "\"" + " onclick=\"" + "setInputValue('"
            + paramNames[1] + "', " + String.valueOf(ROW_COUNT + 1) + ");" + BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("ボタン出力：タグ名がNullの場合divタグのonclick属性はmapColInfoの値が出力されることの確認")
    void test_outButton_tagName_null() throws Exception{
        mapInfo(null, "test_class", DEFAULT_BUTTON_FUNC, BUTTON_SRC, DIV_STYLE, "test_title", 2, false);
        setupMock(0);
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // mapColInfoの値が使用される
        inOrder.verify(out, times(1)).print("        <div class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\" style=\"" + DIV_STYLE + ""
            + "\"" + " title=\"" + (String) mapColInfo.get(INFO_TITLE) + "\"" + " onclick=\"" + "setInputValue('"
            + paramNames[1] + "', " + String.valueOf(ROW_COUNT + 1) + ");" + DEFAULT_BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("ボタン出力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_outButton_allHidden_true() throws Exception{
        mapInfo("test_name", "test_class", DEFAULT_BUTTON_FUNC, BUTTON_SRC, DIV_STYLE, "test_title", 2, true);
        setupMock(0);
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\" style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("        <div class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\" style=\"" + DIV_STYLE + ""
            + "\"" + " title=\"" + (String) mapColInfo.get(INFO_TITLE) + "\"" + " onclick=\"" + "setInputValue('"
            + paramNames[1] + "', " + String.valueOf(ROW_COUNT + 1) + ");" + BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("ボタン出力：GnomesCTagBas#getDataにより非表示項目と判定された場合divタグのstyle属性に'display:none'が出力されることの確認")
    void test_outButton_hidden_true() throws Exception{
        mapInfo("test_name", "test_class", DEFAULT_BUTTON_FUNC, BUTTON_SRC, DIV_STYLE, "test_title", 2, false);
        setupMock(1); // ConverterUtils#IntToboolによりTrueとなる
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 非表示項目になる
        inOrder.verify(out, times(1)).print("        <div class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\" style=\"" + DIV_STYLE + " display: none;"
            + "\"" + " title=\"" + (String) mapColInfo.get(INFO_TITLE) + "\"" + " onclick=\"" + "setInputValue('"
            + paramNames[1] + "', " + String.valueOf(ROW_COUNT + 1) + ");" + BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("ボタン出力：パラメータ名の2つ目の要素が未設定の場合divタグのonclick属性にsetInputValue関数が出力されないことの確認")
    void test_outButton_paramName_second_empty() throws Exception{
        mapInfo("test_name", "test_class", DEFAULT_BUTTON_FUNC, BUTTON_SRC, DIV_STYLE, "test_title", 1, false);
        setupMock(0);
        ReflectionTestUtils.invokeMethod(cTagTable, "outButton", out, Locale.getDefault(), TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // setInputValue関数は出力されない
        inOrder.verify(out, times(1)).print("        <div class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\" style=\"" + DIV_STYLE + ""
            + "\"" + " title=\"" + (String) mapColInfo.get(INFO_TITLE) + "\"" + " onclick=\"" + BUTTON_FUNC
            + "\">\n");
        inOrder.verify(out, times(1)).print("<img alt=\"\" src=\"" + (String) mapColInfo.get(INFO_IMG_SRC) + "\">\n");
        inOrder.verify(out, times(1)).print("</div>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String tagName, String className, String onclick, String imgSrc, String dataStyle,
        String titleResouceId, int paramNameLength, boolean isAllHidden) {
        mapColInfo = new HashMap<String, Object>();

        if (tagName != null) {
            mapColInfo.put(INFO_TAG_NAME, tagName);
        }
        if (titleResouceId != null) {
            mapColInfo.put(INFO_TITLE, titleResouceId);
        }
        mapColInfo.put(INFO_CLASS, className);
        mapColInfo.put(INFO_ON_CLICK, onclick);
        mapColInfo.put(INFO_IMG_SRC, imgSrc);
        mapColInfo.put(INFO_STYLE, dataStyle);

        if (isAllHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramNameLength; i++) {
            sb.append("test_param_");
            sb.append(i);
            if (i != (paramNameLength - 1)) {
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            String paramName = sb.toString();
            paramNames = paramName.split(",");
            mapColInfo.put(GnomesCTagTable.INFO_PARAM_NAME, paramName);
        }

        if (isAllHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
    }

    private void setupMock(Integer data) throws Exception {
        doReturn(data).when(cTagTable).getData(any(), any(), anyString());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
