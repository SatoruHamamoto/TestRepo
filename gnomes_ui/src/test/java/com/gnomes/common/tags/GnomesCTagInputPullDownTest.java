package com.gnomes.common.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

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
import org.picketbox.util.StringUtil;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagInputPullDownTest {

    /** 辞書：ラベルリソースID */
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";
    /** 辞書：単位ラベルリソースID */
    private static final String INFO_UNIT_LIST = "label_list";
    /** 辞書：プルダウン名 */
    private static final String INFO_PULLDOWN_NAME = "pulldown_name";
    /** 辞書：オートコンプリート */
    private static final String INFO_IS_AUTOCOMLETE = "is_autocomplete";
    /** 辞書：プルダウン候補パラメータ名 */
    private static final String INFO_LIST_PARAM_NAME = "list_param_name";
    /** 辞書：プルダウン選択パラメータ名 */
    private static final String INFO_SELECT_PARAM_NAME = "select_param_name";
    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";
    /** 辞書：先頭空白 */
    private static final String INFO_DEFAULT_SPACE = "default_space";
    /** 辞書：プルダウンのスタイル */
    private static final String INFO_PULLDOWN_STYLE = "style";
    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";
    /** 辞書：左側ラベルの追加クラス */
    private static final String INFO_HEADER_ADD_CLASS = "header_add_class";
    /** 辞書：右側ラベルの追加クラス */
    private static final String INFO_DATA_ADD_CLASS = "data_add_class";
    /** 辞書：INPUTタイプ **/
    private static final String INFO_INPUT_TYPE = "input_type";
    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";
    /** 辞書：onChangeイベントクラス名 **/
    private  static final String  INFO_ONCHANGE_EVENT = "on_change_event";

    private static final String SEL_VALUE = "test_key_2";
    private static final String REPLACE_VALUE = "test_replace";
    private static final String UNIT = "Pa";

    @Spy
    @InjectMocks
    GnomesCTagInputPullDown cTagInputPullDown;

    @Mock
    JspWriter out;
    @Mock
    LogHelper logHelper;
    @Mock
    PageContext pageContext;
    @Mock
    GnomesCTagDictionary cTagDictionary;
    @Mock
    GnomesSessionBean gnomesSessionBean;
    @Mock
    ContainerResponse responseContext;

    private InOrder inOrder;
    private Map<String, Object> mapInfo;
    private List<String[]> pulldownList;
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
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);
        cTagInputPullDown.setBean(new TestFormBean());
        cTagInputPullDown.setDictId("test_dict_id");

        doReturn(Locale.getDefault()).when(gnomesSessionBean).getUserLocale();
        doReturn(out).when(pageContext).getOut();
        doReturn(cTagDictionary).when(cTagInputPullDown).getCTagDictionary();
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("プルダウンタグ出力：BeanがNullだった場合GnomesAppExceptionが発生することの確認")
    void test_doStartTag_GnomesAppException_bean_null() throws JspException {
        cTagInputPullDown.setBean(null);

        JspException e = assertThrows(JspException.class, () -> cTagInputPullDown.doStartTag());
        assertTrue(e.getCause() instanceof GnomesAppException);
        assertTrue(e.getMessage().endsWith(GnomesCTagBase.NO_BEAN_ERR_MES));
    }

    @Test
    @DisplayName("プルダウンタグ出力：工程端末の場合プルダウン選択テーブルダイアログの出力を行うonclickイベントがselectタグに出力されることの確認")
    void test_doStartTag_isPanecon_true() throws Exception {
        // 工程端末の場合
        String strInputClass = "common-data-input-size-item-code ";
        String strOnclick = "onclick=\"MakePullDownTableModal($(this), '" + null + "', '" + "" + "');\"";
        String strOnchange = "";
        setupMockGetPullDownInfo("true", "", null, "", "", "", null, null, null, null, null, "", null, "", null, false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data-titleless" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + "" + "\" class=\"" + strInputClass + "" + "\""
            + strOnchange + strOnclick + " " + "" + ">");
        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：ラベルリソースIDが設定されている場合ラベル出力されることの確認")
    void test_doStartTag_labelRourceId_out_label() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", null, "", "", "", null, null, null, null, null, "", null, "", null, false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagInputPullDown.doStartTag());

        // ラベルが出力される
        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + "" + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");

        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + "" + "\" class=\"" + " " + "\"" + " "+ "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + "\"" + "" + " " + "" + ">");
        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：選択候補リストが取得できている場合プルダウン内データが出力されることの確認")
    void test_doStartTag_listParamName_out_label() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", "", "", null, null, null, null, null, "", null, "", null, false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + "" + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\"" + " " + "\"" + " "+ "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + "\"" + "" + " " + "" + ">");

        // プルダウン内データの生成
        verifyOutSelectOption(false);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：選択値パラメータ名が設定されている場合FormBeanから取得した選択値が選択状態で出力されることの確認")
    void test_doStartTag_selValue_out_option() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(1), "", null, null, null, null, null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, null);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + "" + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\"" + " " + "\"" + " "+ "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + "\"" + "" + " " + "" + ">");

        // プルダウン内データの生成
        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：差し替え用選択名パラメータ名が設定されている場合FormBeanから取得した差し替え用プルダウン選択項目名が選択状態で出力されることの確認")
    void test_doStartTag_replaceValue_out_option() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", null, null, null, null, null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + "" + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\"" + " " + "\"" + " "+ "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + "\"" + "" + " " + "" + ">");

        // プルダウン内データの生成
        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：onChangeイベントが設定されている場合onChange属性に設定したonChangeイベントが追加されることの確認")
    void test_doStartTag_out_onchange() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", "testChangeFunc();", null,
            null, null, null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + "" + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\"" + " " + "\"" + "onchange=\""
        // 設定したonChangeイベントが出力される
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：追加クラスが設定されている場合selectタグのclass属性に設定した追加クラスが追加されることの確認")
    void test_doStartTag_out_addClass() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", "testChangeFunc();",
            "test_add_class", null, null, null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + "" + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
            // 設定した追加クラスが出力される
            + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\"" + "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：左側ラベルの追加クラスが設定されている場合div(ラベル)タグのclass属性に設定した左側ラベルの追加クラスが追加されることの確認")
    void test_doStartTag_out_headerAddClass() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", "testChangeFunc();",
            "test_add_class", "test_header_add_class", null, null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        // 設定した左側ラベルの追加クラスが出力される
        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + "" + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
            + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\"" + "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：右側ラベルの追加クラスが設定されている場合divタグのclass属性に設定した右側ラベルの追加クラスが追加されることの確認")
    void test_doStartTag_out_dataAddClass() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        // 設定した右側ラベルの追加クラスが出力される
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
            + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\"" + "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：右側ラベルの追加クラスが設定されている場合divタグのclass属性に設定した右側ラベルの追加クラスが追加されることの確認")
    void test_doStartTag_out_style() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, "", null, "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        // 設定した右側ラベルの追加クラスが出力される
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
            + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\"" + "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：単位ラベルリソースIDが設定されている場合単位が出力されることの確認")
    void test_doStartTag_out_unit() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "", "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, "", "test_pascal", "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
            + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\"" + "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        // 単位の出力
        inOrder.verify(out, times(1)).print(UNIT);
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：先頭空白フラグが設定されている場合プルダウンの先頭に空白が出力されることの確認")
    void test_doStartTag_out_space() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "true", "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, "", "test_pascal", "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1)).print("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
            + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\"" + "onchange=\""
            + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + "" + " " + "" + ">");

        // 空行出力
        inOrder.verify(out, times(1)).print("<option value=\"\"></option>");
        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print(UNIT);
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @ParameterizedTest
    @ValueSource(strings = { GnomesCTagBase.AUTO_MODE_VALUE,
                             GnomesCTagBase.AUTO_MODE_TEXT,
                             "4" })
    @DisplayName("プルダウンタグ出力：オートコンプリート有りの場合selectタグにdata-mode属性が出力されることの確認")
    void test_doStartTag_out_autocomplete(String autocomplte) throws Exception {
        String mode = autocomplte;
        if (!mode.equals(GnomesCTagBase.AUTO_MODE_VALUE) && !mode.equals(GnomesCTagBase.AUTO_MODE_TEXT)) {
            mode = GnomesCTagBase.AUTO_MODE_VALUE_TEXT;
        }

        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "true", "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, mode, "test_pascal", "", null, true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        // data-modeが出力
        inOrder.verify(out, times(1))
            .println("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" class=\""
                + GnomesCTagBase.CLASS_AUTO_COMBO + " " + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\" "
                + "onchange=\""
                + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + " data-mode=\"" + mode
                + "\" " + "" + ">");

        inOrder.verify(out, times(1)).print("<option value=\"\"></option>");
        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print(UNIT);
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：プルダウンが活性かつ領域ごと操作可否を画面アイテム定義辞書から入手しそれが現在の接続情報と食い違っている場合プルダウンがdisabledになることの確認")
    void test_doStartTag_setupMockJudgeDisplayFromConnectionArea_false() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "true",
            "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, GnomesCTagBase.AUTO_MODE_VALUE,
            "test_pascal", "", null, true);
        setupMockJudgeDisplayFromConnectionArea(false);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        // 選択データを隠し項目として出力し、プルダウン部はdisabledとして表示
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\""+ (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" value=\"" + SEL_VALUE + "\">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1))
            // プルダウン名がリネーム
            .println("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "_pulldown" + "\" class=\""
                + GnomesCTagBase.CLASS_AUTO_COMBO + " " + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\" "
                + "onchange=\""
                + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + " data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE
                // disabledを出力
                + "\" " + GnomesCTagBase.INPUT_DISABLED + ">");

        inOrder.verify(out, times(1)).print("<option value=\"\"></option>");
        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print(UNIT);
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("プルダウンタグ出力：プルダウンが活性かつFormBeanから取得した入力活性非活性判定がFalseの場合プルダウンがdisabledになることの確認")
    void test_doStartTag_inputType_readonly() throws Exception {
        setupMockGetPullDownInfo("", "test_pulldown_label", "test_pulldown", "test_list_param", getSelectParamNames(2), "true",
            "testChangeFunc();",
            "test_add_class", "test_header_add_class", "test_data_add_class", null, GnomesCTagBase.AUTO_MODE_VALUE,
            "test_pascal", "", "0", true);
        setupMockJudgeDisplayFromConnectionArea(true);
        setupMockGetResponseFormBean(SEL_VALUE, REPLACE_VALUE);
        assertEquals(0, cTagInputPullDown.doStartTag());

        // 選択データを隠し項目として出力し、プルダウン部はdisabledとして表示
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\""+ (String) mapInfo.get(INFO_PULLDOWN_NAME) + "\" value=\"" + SEL_VALUE + "\">\n");
        inOrder.verify(out, times(1)).print("<div class=\"common-header-col-title " + " " + (String) mapInfo.get(INFO_HEADER_ADD_CLASS) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + " " + " " + (String) mapInfo.get(INFO_DATA_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1))
            // プルダウン名がリネーム
            .println("<select name=\"" + (String) mapInfo.get(INFO_PULLDOWN_NAME) + "_pulldown" + "\" class=\""
                + GnomesCTagBase.CLASS_AUTO_COMBO + " " + " " + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\" "
                + "onchange=\""
                + GnomesCTagBase.SET_WARMING_FLAG + (String) mapInfo.get(INFO_ONCHANGE_EVENT) + "\"" + " data-mode=\"" + GnomesCTagBase.AUTO_MODE_VALUE
                // disabledを出力
                + "\" " + GnomesCTagBase.INPUT_DISABLED + ">");

        inOrder.verify(out, times(1)).print("<option value=\"\"></option>");
        verifyOutSelectOption(true);

        inOrder.verify(out, times(1)).print("</select>");
        inOrder.verify(out, times(1)).print(UNIT);
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    private void verifyOutSelectOption(boolean isSelect) throws IOException {
        for (String[] pulldown : pulldownList) {
            if (isSelect && pulldown[0].equals(SEL_VALUE)) {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + " selected" + ">" + pulldown[1] + "</option>\n");
            } else {
                inOrder.verify(out, times(1)).print("<option value=\"" + pulldown[0] + "\"" + "" + ">" + pulldown[1] + "</option>\n");
            }
        }
    }

    /**
     * プルダウン辞書情報取得処理のモック化
     * @param isPanecon 工程端末表示か否か
     * @param labelRourceId ラベルリソースID
     * @param pulldownName プルダウン名
     * @param listParamName プルダウン候補パラメータ名
     * @param selectParamName プルダウン選択パラメータ名
     * @param dsFlag 先頭空白有りか否か
     * @param onChange onChangeイベントクラス名
     * @param addClass 追加クラス
     * @param headerAddClass 左側ラベルの追加クラス
     * @param dataAddClass 右側ラベルの追加クラス
     * @param style プルダウンのスタイル
     * @param isAutocomplete オートコンプリート
     * @param unitList 単位ラベルリソースID
     * @param strInputType INPUTタイプ
     * @param labelInputActivity 入力活性非活性Bean
     * @param isSelect 選択状態か否か
     * @throws Exception
     */
    private void setupMockGetPullDownInfo(String isPanecon,
        String labelRourceId,
        String pulldownName,
        String listParamName,
        String selectParamName,
        String dsFlag,
        String onChange,
        String addClass,
        String headerAddClass,
        String dataAddClass,
        String style,
        String isAutocomplete,
        String unitList,
        String strInputType,
        String labelInputActivity,
        boolean isSelect) throws Exception {
        mapInfo = new HashMap<String, Object>();

        if (pulldownName != null) {
            mapInfo.put(INFO_PULLDOWN_NAME, pulldownName);
        }
        if (onChange != null) {
            mapInfo.put(INFO_ONCHANGE_EVENT, onChange);
        }
        if (addClass != null) {
            mapInfo.put(INFO_ADD_CLASS, addClass);
        }
        if (headerAddClass != null) {
            mapInfo.put(INFO_HEADER_ADD_CLASS, headerAddClass);
        }
        if (dataAddClass != null) {
            mapInfo.put(INFO_DATA_ADD_CLASS, dataAddClass);
        }
        if (style != null) {
            mapInfo.put(INFO_PULLDOWN_STYLE, style);
        }
        if (unitList != null) {
            mapInfo.put(INFO_UNIT_LIST, unitList);
        }
        if (labelInputActivity != null) {
            mapInfo.put(INFO_INPUT_ACTIVITY, labelInputActivity);
        }
        mapInfo.put(INFO_IS_PANECON, isPanecon);
        mapInfo.put(INFO_LABEL_RESOUCE_ID, labelRourceId);
        mapInfo.put(INFO_LIST_PARAM_NAME, listParamName);
        mapInfo.put(INFO_SELECT_PARAM_NAME, selectParamName);
        mapInfo.put(INFO_DEFAULT_SPACE, dsFlag);
        mapInfo.put(INFO_IS_AUTOCOMLETE, isAutocomplete);
        mapInfo.put(INFO_INPUT_TYPE, strInputType);

        doReturn(mapInfo).when(cTagDictionary).getPullDownInfo(anyString());
        setupMockGetData(selectParamName, listParamName, labelInputActivity, unitList);
    }

    private void setupMockGetData(String selectParamName, String listParamName, String labelInputActivity, String unitList)
        throws Exception {
        List<Object> lstValue = null;
        boolean inputActivityData = false;
        List<Object> lstUnit = new ArrayList<>(Arrays.asList("pascal"));
        pulldownList = new ArrayList<String[]>();
        String selectParamNames[] = selectParamName.split(",");
        if (!StringUtil.isNullOrEmpty(listParamName)) {
            lstValue = new ArrayList<>(Arrays.asList("test_item_1", "test_item2", "test_item3"));
            for (int i = 0; i < lstValue.size(); i++) {
                String key = "test_key_" + i;
                String val = "test_value_" + i;
                if (key.equals(SEL_VALUE)
                    && (selectParamNames.length == 2 && !StringUtil.isNullOrEmpty(selectParamNames[1]))) {
                    val = REPLACE_VALUE;
                }
                String[] pulldown = { key, val };
                pulldownList.add(pulldown);
            }
        }

        if (labelInputActivity != null && !labelInputActivity.equals("0")) {
            inputActivityData = true;
        }

        if (pulldownList.size() > 0) {

            if (!StringUtil.isNullOrEmpty(labelInputActivity) && !StringUtil.isNullOrEmpty(unitList)) {
                doReturn(lstValue)
                    // 選択値取得用
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .doReturn(inputActivityData)
                    // プルダウン内のデータ生成用
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .doReturn(lstUnit)
                    .doReturn(SEL_VALUE, UNIT)
                    .when(cTagInputPullDown).getData(any(), any(), any());
                return;
            }

            if (StringUtil.isNullOrEmpty(labelInputActivity) && !StringUtil.isNullOrEmpty(unitList)) {
                doReturn(lstValue)
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .doReturn(lstUnit)
                    .doReturn(SEL_VALUE, UNIT)
                    .when(cTagInputPullDown).getData(any(), any(), any());
            } else if (StringUtil.isNullOrEmpty(unitList) && !StringUtil.isNullOrEmpty(labelInputActivity)) {
                doReturn(lstValue)
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .doReturn(inputActivityData)
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .when(cTagInputPullDown).getData(any(), any(), any());

            } else {
                doReturn(lstValue)
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .doReturn(pulldownList.get(0)[0], pulldownList.get(0)[1])
                    .doReturn(pulldownList.get(1)[0], pulldownList.get(1)[1])
                    .doReturn(pulldownList.get(2)[0], pulldownList.get(2)[1])
                    .when(cTagInputPullDown).getData(any(), any(), any());
            }

        } else {

            if (!StringUtil.isNullOrEmpty(labelInputActivity) && !StringUtil.isNullOrEmpty(unitList)) {
                doReturn(lstValue, inputActivityData, lstUnit)
                    .doReturn(SEL_VALUE, UNIT)
                    .when(cTagInputPullDown).getData(any(), any(), any());
                return;
            }

            if (StringUtil.isNullOrEmpty(labelInputActivity) && !StringUtil.isNullOrEmpty(unitList)) {
                doReturn(lstValue, lstUnit)
                    .doReturn(SEL_VALUE, UNIT)
                    .when(cTagInputPullDown).getData(any(), any(), any());
            } else if (StringUtil.isNullOrEmpty(unitList) && !StringUtil.isNullOrEmpty(labelInputActivity)) {
                doReturn(lstValue, inputActivityData).doReturn(inputActivityData).when(cTagInputPullDown).getData(any(), any(), any());
            } else {
                doReturn(lstValue).when(cTagInputPullDown).getData(any(), any(), any());
            }
        }
    }

    private void setupMockJudgeDisplayFromConnectionArea(boolean isDisplay) {
        doReturn(isDisplay).when(cTagInputPullDown).judgeDisplayFromConnectionArea(any());
    }

    private void setupMockGetResponseFormBean(String selVal, String selName)
        throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        doReturn(selVal, selName).when(responseContext).getResponseFormBean(any(), anyString(), anyString());
    }

    private String getSelectParamNames(int length) {
        String selectParamName = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("test_select_param_name_");
            sb.append(i);
            if (i != (length - 1)) {
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            selectParamName = sb.toString();
        }
        return selectParamName;
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
