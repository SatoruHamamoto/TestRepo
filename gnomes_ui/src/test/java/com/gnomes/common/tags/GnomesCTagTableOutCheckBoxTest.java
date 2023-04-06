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

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableOutCheckBoxTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";
    /** 辞書：オンクリック */
    private static final String INFO_ON_CLICK = "onclick";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String INPUT_STYLE = "border: 1px solid;";

    private static final int ROW_COUNT = 3;

    @Spy
    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    ContainerResponse responseContext;

    private InOrder inOrder;
    private Map<String, Object> mapColInfo;
    private String[] onclicks;

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

//    private void outCheckBox(
//        JspWriter out,
//        String style,String colStyle,//追加
//        Map<String, Object> mapColInfo,
//        Class<?> clsData,
//        Object data,
//        int rowCnt,
//        boolean isLastDispCol)

    @Test
    @DisplayName("チェックボックス出力：タグ名が無い場合チェックされていない状態で出力されることの確認")
    void test_outCheckBox_no_checked_tagName_empty() throws Exception {
        mapInfo("", "test_param", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // checkedでないこと
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：パラメータ名が無い場合チェックされていない状態で出力されることの確認")
    void test_outCheckBox_no_checked_valueName_empty() throws Exception {
        mapInfo("test_name", "", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // checkedでないこと
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：取得したレスポンスデータが'0'の場合チェックされていない状態で出力されることの確認")
    void test_outCheckBox_no_checked_getResponse_no_checked() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_NO_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // checkedでないこと
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：取得したレスポンスデータが'1'の場合チェックされていない状態で出力されることの確認")
    void test_outCheckBox_checked_getResponse_checked() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // checkedであること
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：onclickが設定されていない場合onclickが無い状態で出力されることの確認")
    void test_outCheckBox_no_onclick() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 0, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // onclick未設定
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：設定されたonclickのサイズが1の場合チェック時のイベントのみが出力されることの確認")
    void test_outCheckBox_onclick_checked() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 1, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // チェック時イベントのみ出力
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };"  + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：設定されたonclickのサイズが2の場合チェック時と非チェック時のイベントが出力されることの確認")
    void test_outCheckBox_onclick_checked_no_checked() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        // チェック時と非チェック時イベントが出力
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：非表示項目に設定された場合'display:none'が出力されることの確認")
    void test_outCheckBox_hidden_true() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 2, true);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td class=\"common-text-center\" style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：チェックボックスクラスが設定されてないの場合チェックボックスクラスが出力されないことの確認")
    void test_outCheckBox_checkboxClass_empty() throws Exception {
        mapInfo("test_name", "test_param", "", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        // 出力されない
        inOrder.verify(out, times(0)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：tdのstyleが未指定の場合style属性が出力されないことの確認")
    void test_outCheckBox_td_style_empty() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, "", INPUT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + INPUT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("チェックボックス出力：inputのstyleが未指定の場合inputのstyle属性にtdのstyleが適用されることの確認")
    void test_outCheckBox_input_style_empty() throws Exception {
        mapInfo("test_name", "test_param", "test_class", 2, false);
        setupMock(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
        ReflectionTestUtils.invokeMethod(cTagTable, "outCheckBox", out, TD_STYLE, "", mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td class=\"common-text-center");
        inOrder.verify(out, times(1)).append(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print("\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" value=\"" + String.valueOf(ROW_COUNT) + "\" onclick=\"" + "if(this.checked){ "
            + onclicks[0] + " };" + "if(!this.checked){ " + onclicks[1] + " };" + "\" " + "checked" + ">\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String tagName, String paramName, String className, int onclickSize, boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        mapColInfo.put(INFO_TAG_NAME, tagName);
        mapColInfo.put(GnomesCTagTable.INFO_PARAM_NAME, paramName);
        mapColInfo.put(INFO_CLASS, className);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < onclickSize; i++) {
            sb.append("test_onclick_");
            sb.append(i);
            sb.append("();");
            if (i != (onclickSize - 1)) {
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            String onclick = sb.toString();
            onclicks = onclick.split(",");
            mapColInfo.put(INFO_ON_CLICK, onclick);
        }

        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
    }

    private void setupMock(Integer checkedValue)
        throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        doReturn(checkedValue).when(responseContext).getResponseCheckBoxFormBean(any(),
            anyString(), anyInt(), anyString());
    }

}
