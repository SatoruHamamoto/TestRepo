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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

class GnomesCTagTableOutImgPatternTest {

    /** 辞書：オンクリック */
    private static final String INFO_ON_CLICK = "onclick";
    /** 辞書：ボタンクラス */
    private static final String INFO_BUTTON_CLASS = "buttonClass";

    private static final String ICON_PATH = "../images/icon.png";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String IMG_STYLE = "padding: 8px 12px;";
    private static final int ROW_COUNT = 3;

    private static final String DEFAULT_BUTTON_FUNC = "defaultBtn();";

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    GnomesCTagDictionary cTagDictionary;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;
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
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("画像パターン出力：パラメータ名の2つ目の要素が未設定の場合imgタグのonclick属性にsetInputValue関数が出力されないことの確認")
    void test_outImgPattern_paramName_second_empty() throws Exception {
        mapInfo("test_button", DEFAULT_BUTTON_FUNC, 1, false);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // setInputValue関数は出力されない
        inOrder.verify(out, times(1)).print("<img src=\""+ ICON_PATH + "\" alt=\"\"" + " onclick=\"" + DEFAULT_BUTTON_FUNC + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(1)).print(" class=\""+ (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：onclickが未設定の場合imgタグのonclick属性にsetInputValue関数のみ出力されることの確認")
    void test_outImgPattern_onclick_empty() throws Exception {
        mapInfo("test_button", "", 2, false);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // setInputValue関数のみ出力される
        inOrder.verify(out, times(1)).print("<img src=\"" + ICON_PATH + "\" alt=\"\"" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "
                + String.valueOf(ROW_COUNT + 1) + ");" + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：onclickが未設定かつパラメータ名の2つ目の要素が未設定の場合imgタグのonclick属性が出力されないことの確認")
    void test_outImgPattern_onclick_paramName_empty() throws Exception {
        mapInfo("test_button", "", 1, false);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // onclick属性が出力されない
        inOrder.verify(out, times(1)).print("<img src=\"" + ICON_PATH + "\" alt=\"\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_outImgPattern_hidden_true() throws Exception {
        mapInfo("test_button", DEFAULT_BUTTON_FUNC, 2, true);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\" style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<img src=\"" + ICON_PATH + "\" alt=\"\"" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "
                + String.valueOf(ROW_COUNT + 1) + ");" + DEFAULT_BUTTON_FUNC + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：tdのスタイルが未設定の場合tdタグのstyle属性が出力されないことの確認")
    void test_outImgPattern_td_style_empty() throws Exception {
        mapInfo("test_button", DEFAULT_BUTTON_FUNC, 2, false);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), null, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        // style属性が出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<img src=\"" + ICON_PATH + "\" alt=\"\"" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "
                + String.valueOf(ROW_COUNT + 1) + ");" + DEFAULT_BUTTON_FUNC + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：アイコンパスが取得できなかった場合imgタグが出力されないことの確認")
    void test_outImgPattern_getPatternValue_fail() throws Exception {
        mapInfo("test_button", DEFAULT_BUTTON_FUNC, 2, false);
        setupMocks(true);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // imgタグが出力されない
        inOrder.verify(out, times(0)).print("<img src=\"" + "" + "\" alt=\"\"" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "
                + String.valueOf(ROW_COUNT + 1) + ");" + DEFAULT_BUTTON_FUNC + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(0)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        inOrder.verify(out, times(0)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(0)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：イメージクラスがNullの場合imgタグのclass属性が出力されないことの確認")
    void test_outImgPattern_imgClass_empty() throws Exception {
        mapInfo(null, DEFAULT_BUTTON_FUNC, 2, false);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, IMG_STYLE,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<img src=\"" + ICON_PATH + "\" alt=\"\"" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "
                + String.valueOf(ROW_COUNT + 1) + ");" + DEFAULT_BUTTON_FUNC + "\" style=\"cursor: pointer;\"");
        // class属性が出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + "" + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + IMG_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("画像パターン出力：imgのスタイルが未設定の場合imgタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_outImgPattern_img_style_empty() throws Exception {
        mapInfo("test_button", DEFAULT_BUTTON_FUNC, 2, false);
        setupMocks(false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outImgPattern", out, Locale.getDefault(), TD_STYLE, null,
            mapColInfo, null, null, ROW_COUNT, cTagDictionary, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<img src=\"" + ICON_PATH + "\" alt=\"\"" + " onclick=\"" + "setInputValue('" + paramNames[1] + "', "
                + String.valueOf(ROW_COUNT + 1) + ");" + DEFAULT_BUTTON_FUNC + "\" style=\"cursor: pointer;\"");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_BUTTON_CLASS) + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }


    private void mapInfo(String imgClass, String onclick, int paramNameLength, boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        mapColInfo.put(GnomesCTagTable.INFO_PATTERN_ID, "test_pattern");
        mapColInfo.put(INFO_ON_CLICK, onclick);

        if (imgClass != null) {
            mapColInfo.put(INFO_BUTTON_CLASS, imgClass);
        }
        if (isHidden) {
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
    }

    private void setupMocks(boolean isNull) throws Exception {
        String iconPath = null;
        if (!isNull) {
            iconPath = ICON_PATH;
        }
        doReturn("test_icon_path").when(cTagTable).getData(any(), any(), anyString());
        doReturn(iconPath).when(cTagTable).getPatternValue(any(), anyString(), anyString());
    }
}
