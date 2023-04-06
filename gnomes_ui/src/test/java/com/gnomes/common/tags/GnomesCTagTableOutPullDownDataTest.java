package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagTableOutPullDownDataTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";
    /** 辞書：プルダウン候補パラメータ名 */
    private static final String INFO_LIST_PARAM_NAME = "list_param_name";
    /** 辞書：プルダウン選択パラメータ名 */
    private static final String INFO_SELECT_PARAM_NAME = "select_param_name";
    /** オートコンプリート */
    private static final String INFO_LIST_AUTOCOMPLETE = "list_autocomplete";

    private static final String TD_STYLE = "background-color: #ffcfd8;";
    private static final String SELECT_STYLE = "padding: 8px 12px;";

    private static final String SEL_VALUE = "test_key_1";
    private static final String REPLACE_VALUE = "test_replace";

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
    private List<String[]> pulldownList;


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

        doReturn(SEL_VALUE, REPLACE_VALUE).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(),
            anyString());
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：タグ名が未設定の場合selectタグのname属性が出力されないことの確認")
    void test_outPullDownData_tagName_empty() throws Exception {
        mapInfo("test_pulldown", "", GnomesCTagBase.AUTO_MODE_VALUE, 0, false);
        setupMockReturnBool(false, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        // 出力されないこと
        inOrder.verify(out, times(0)).print("\" name=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：FormBeanから選択値を取得かつ候補リストに選択値が存在する場合選択値が選択された状態で出力されることの確認")
    void test_outPullDownData_select_true() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                // 選択された状態で出力される
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：差し替え用選択名パラメータ名が設定されている場合選択値表示名が差し替えられた状態で出力される")
    void test_outPullDownData_select_true_replacement() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 2, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                // 選択されたかつ差し替えられた状態で出力される
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + REPLACE_VALUE + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：非表示項目に設定されている場合tdタグのstyle属性に'display:none'が出力されることの確認")
    void test_outPullDownData_hidden() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, true);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        // 非表示項目になる
        inOrder.verify(out, times(1)).print("<td style=\"display:none\">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：readonlyの場合選択データを隠し項目として出力しプルダウン部はdisabledとして出力されることの確認")
    void test_outPullDownData_isInputReadOnlyParam_true() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, false);
        setupMockReturnBool(true, true);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        // 隠し項目として出力
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\""+ (String) mapColInfo.get(INFO_TAG_NAME) + "\" value=\"" + SEL_VALUE + "\">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        // リネームされる
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "_pulldown" + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        // 非活性になる
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.INPUT_DISABLED + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                // 選択された状態で出力される
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：スタイルクラスがNullの場合tdタグとselectタグのclass属性が出力されないことの確認")
    void test_outPullDownData_styleClass_null() throws Exception {
        mapInfo(null, "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        // 出力されない
        inOrder.verify(out, times(0)).print(" class=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        // 出力されない
        inOrder.verify(out, times(0)).print(" " + null);
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                // 選択された状態で出力される
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：tdのスタイルがNullの場合tdタグのstyle属性が出力されないことの確認")
    void test_outPullDownData_td_style_null() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, null, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" style=\"" + null + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：selectのスタイルがNullの場合selectタグのstyle属性がtdのスタイルで適用されることの確認")
    void test_outPullDownData_select_style_null() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, null, mapColInfo, null, null, ROW_COUNT, false);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        // tdのスタイルが適用される
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(0)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：空白有りの場合プルダウンに空白の選択肢が出力されることの確認")
    void test_outPullDownData_defaultSpace_true() throws Exception {
        mapInfo("test_pulldown", "test_tag", GnomesCTagBase.AUTO_MODE_VALUE, 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, true);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        // 出力される
        inOrder.verify(out, times(1)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {

                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    @Test
    @DisplayName("プルダウン出力（データ項目）：オートコンプリートが未設定の場合selectタグのclass属性にクラスが追加されないかつdata-mode属性が出力されないことの確認")
    void test_outPullDownData_autoComplete_empty() throws Exception {
        mapInfo("test_pulldown", "test_tag", "", 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, true);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        // 出力されない
        inOrder.verify(out, times(0)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        // 出力されない
        inOrder.verify(out, times(0)).print(" data-mode=\"" + null + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(1)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {

                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }


    @ParameterizedTest
    @ValueSource(strings = {
            GnomesCTagBase.AUTO_MODE_VALUE,
            GnomesCTagBase.AUTO_MODE_TEXT,
            "0"
    })
    @DisplayName("プルダウン出力（データ項目）：オートコンプリートモードがvalue表示かつtext表示でもない場合value + text表示になることの確認")
    void test_outPullDownData_autoComplete_parameterized(String autoComplete) throws Exception {
        String mode = autoComplete;
        if (!mode.equals(GnomesCTagBase.AUTO_MODE_VALUE) && !mode.equals(GnomesCTagBase.AUTO_MODE_TEXT)) {
            // オートコンプリートモードがvalue表示かつtext表示でもない場合value + text表示になる
            mode = GnomesCTagBase.AUTO_MODE_VALUE_TEXT;
        }

        mapInfo("test_pulldown", "test_tag", autoComplete, 1, false);
        setupMockReturnBool(true, false);
        ReflectionTestUtils.invokeMethod(cTagTable, "outPullDownData", out, TD_STYLE, SELECT_STYLE, mapColInfo, null, null, ROW_COUNT, true);

        inOrder.verify(out, times(1)).print("<td");
        inOrder.verify(out, times(1)).print(" class=\"" + (String) mapColInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + TD_STYLE + "\"");
        inOrder.verify(out, times(1)).print(">\n");
        inOrder.verify(out, times(1)).print("<select class=\"common-data-input-size-item-code");
        inOrder.verify(out, times(1)).print(" " + (String) mapColInfo.get(INFO_CLASS));
        inOrder.verify(out, times(1)).print(" " + GnomesCTagBase.CLASS_AUTO_COMBO);
        inOrder.verify(out, times(1)).print("\" name=\"" + (String) mapColInfo.get(INFO_TAG_NAME) + "\"");
        inOrder.verify(out, times(1)).print(" style=\"" + SELECT_STYLE + "\"");
        inOrder.verify(out, times(1)).print(" data-mode=\"" + mode + "\" ");
        inOrder.verify(out, times(1)).print("" + " onchange=\"" + "setWarningFlag();" +  "\"" + ">\n");
        inOrder.verify(out, times(1)).print("<option value=\"\"></option>\n");
        for (String[] pulldown : pulldownList) {
            if (pulldown[0].equals(SEL_VALUE)) {

                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
        inOrder.verify(out, times(1)).print("</select>\n");
        inOrder.verify(out, times(1)).print("</td>\n");
    }

    private void mapInfo(String styleClass, String tagName, String autoComplete, int paramNameLength, boolean isHidden) {
        mapColInfo = new HashMap<String, Object>();

        if (styleClass != null) {
            mapColInfo.put(INFO_CLASS, styleClass);
        }
        if (styleClass != null) {
            mapColInfo.put(INFO_CLASS, styleClass);
        }
        if (isHidden) {
            mapColInfo.put(GnomesCTagTable.INFO_HIDDEN, "hidden");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramNameLength; i++) {
            sb.append("test_select_param_name_");
            sb.append(i);
            if (i != (paramNameLength - 1)) {
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            String paramName = sb.toString();
            mapColInfo.put(INFO_SELECT_PARAM_NAME, paramName);
        }
        mapColInfo.put(INFO_TAG_NAME, tagName);
        mapColInfo.put(GnomesCTagTable.INFO_PARAM_NAME, "test_param");
        mapColInfo.put(INFO_LIST_PARAM_NAME, "test_list");
        mapColInfo.put(INFO_LIST_AUTOCOMPLETE, autoComplete);
    }

    private void setupMockReturnBool(boolean isGetSelect, boolean isInputReadOnlyParam) throws Exception {
        doReturn(isInputReadOnlyParam).when(cTagTable).isInputReadOnlyParam(any(), any(), any());

        List<Object> listValue = new ArrayList<>(Arrays.asList("test_item_1", "test_item2", "test_item3"));
        pulldownList = new ArrayList<String[]>();
        for (int i = 0; i < listValue.size(); i++) {
            String[] pulldown = { "test_key_" + i, "test_value_" + i };
            pulldownList.add(pulldown);
        }
        if (isGetSelect) {
            doReturn(listValue)
            // 選択値取得用
            .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
            .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
            // プルダウン内のデータ生成用
            .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
            .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
            .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
            .when(cTagTable).getData(any(), any(), anyString());
        } else {
            doReturn(false).when(cTagTable).getSelect(anyList(), any(), any());
            doReturn(listValue)
            .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
            .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
            .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
            .when(cTagTable).getData(any(), any(), anyString());
        }
    }

}
