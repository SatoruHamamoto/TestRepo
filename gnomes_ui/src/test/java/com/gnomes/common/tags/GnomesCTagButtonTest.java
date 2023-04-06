package com.gnomes.common.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayDiscardChangeFlag;
import com.gnomes.common.constants.CommonEnums.TagHiddenKind;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.view.TestFormBean;

class GnomesCTagButtonTest {

    /** 辞書：ボタン名 */
    private static final String INFO_BUTTON_NAME = "button_name";
    /** 辞書：ボタンリソースID */
    private static final String INFO_BUTTON_RESOUCE_ID = "button_resource_id";
    /** 辞書：コマンドID */
    private static final String INFO_COMMAND_ID = "command_id";
    /** 辞書：BeanからコマンドIDを取得 */
    private static final String INFO_BEAN_COMMAND_ID = "bean_command_id";
    /** 辞書：オンクリック */
    private static final String INFO_ONCLICK = "onclick";
    /** 辞書：画像パス */
    private static final String INFO_IMAGE = "image";
    /** 辞書：OKボタンのリソースID */
    private static final String INFO_OK_RESOURCE_ID = "ok_resource_id";
    /** 辞書：スタイルシート指定クラス */
    private static final String INFO_STYLE_CLASS = "style_class";
    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";
    /** 辞書：ボタンID */
    private static final String INFO_BUTTON_ID = "button_id";
    /**  辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";
    /** 警告チェック処理 */
    private static final String WARMING_ALERT_PROCESS = "warmingAlertProcess(\'%s\',\'%s\');";

    private static final String COMMAND_SCRIPT_FORMAT = "commandSubmit(\'%s\');";

    private TestFormBean bean;

    @Spy
    @InjectMocks
    GnomesCTagButton cTagButton;

    @Spy
    GnomesCTagDictionary cTagDictionary;
    @Mock
    JspWriter out;
    @Mock
    LogHelper logHelper;
    @Mock
    PageContext pageContext;
    @Mock
    PartsPrivilegeResultInfo partsPrivilegeResultInfo;

    private Map<String, Object> mapInfo;
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
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);
        bean = new TestFormBean();
        bean.setTagHiddenKindMap(new HashMap<String, TagHiddenKind>());

        doReturn(out).when(pageContext).getOut();
        doReturn("test_str_style").when(cTagButton).getStyleHiddenKindWithAttribute(any(), anyString());
        doReturn("test_bean_command_id").when(cTagButton).getData(any(), any(), any());

        cTagButton.setBean(bean);
        cTagButton.setDictId("test_dict_id");

        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("ボタンタグ出力：BeanがNullだった場合GnomesAppExceptionが発生")
    void test_doStartTag_GnomesAppException_bean_null() throws JspException {
        cTagButton.setBean(null);

        JspException e = assertThrows(JspException.class, () -> cTagButton.doStartTag());
        assertTrue(e.getCause() instanceof GnomesAppException);
        assertTrue(e.getMessage().endsWith(GnomesCTagBase.NO_BEAN_ERR_MES));
    }

    @Test
    @DisplayName("ボタンタグ出力：画像ボタンの主力")
    void test_doStartTag_print_imageButton() throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(INFO_COMMAND_ID, "", false, false);

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1)).print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\""
            + mapInfo.get(INFO_ADD_STYLE) + "" + "\"" + "test_str_style" + " onclick=\""
            + String.format(COMMAND_SCRIPT_FORMAT, (String) mapInfo.get(INFO_COMMAND_ID)) + "\" tabindex=\"" + ""
            + "\">\n");
        verify(out, times(1)).print("    <img alt=\"\" src=\"" + mapInfo.get(INFO_IMAGE) + "\">\n");
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ボタンタグ出力：ラベルボタンの出力")
    void test_doStartTag_print_labelButton() throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(INFO_COMMAND_ID, "", true, false);

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1)).print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\""
            + mapInfo.get(INFO_STYLE_CLASS) + " " + mapInfo.get(INFO_ADD_STYLE) + "" + "\"" + "test_str_style"
            + " onclick=\"" + String.format(COMMAND_SCRIPT_FORMAT, (String) mapInfo.get(INFO_COMMAND_ID))
            + "\" tabindex=\"" + "" + "\">" + mapInfo.get(INFO_BUTTON_RESOUCE_ID));
        verify(out, times(1)).print("</div>\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        INFO_COMMAND_ID,
        INFO_BEAN_COMMAND_ID,
        ""
    })
    @DisplayName("ボタンタグ出力：コマンドID別onclick属性出力確認")
    void test_doStartTag_print_commandId(String commandId) throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(commandId, "", false, false);
        String onclickStr = (String) mapInfo.get(INFO_ONCLICK);
        if (!commandId.equals("")) {
            onclickStr = String.format(COMMAND_SCRIPT_FORMAT, (String) mapInfo.get(commandId));
        }

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1))
            .print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\"" + mapInfo.get(INFO_ADD_STYLE) + ""
                + "\"" + "test_str_style" + " onclick=\"" + onclickStr + "\" tabindex=\"" + "" + "\">\n");
        verify(out, times(1)).print("    <img alt=\"\" src=\"" + mapInfo.get(INFO_IMAGE) + "\">\n");
        verify(out, times(1)).print("</div>\n");
    }

    @ParameterizedTest
    @ValueSource(booleans = {
        true, // 工程端末
        false // 管理端末
    })
    @DisplayName("ボタンタグ出力：端末別tabindex属性出力確認")
    void test_doStartTag_print_panecon(boolean isPanecon) throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(INFO_COMMAND_ID, "", false, isPanecon);
        String tabindex = isPanecon ? "-1" : "";

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1)).print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\""
            + mapInfo.get(INFO_ADD_STYLE) + "" + "\"" + "test_str_style" + " onclick=\""
            + String.format(COMMAND_SCRIPT_FORMAT, (String) mapInfo.get(INFO_COMMAND_ID)) + "\" tabindex=\"" + tabindex
            + "\">\n");
        verify(out, times(1)).print("    <img alt=\"\" src=\"" + mapInfo.get(INFO_IMAGE) + "\">\n");
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ボタンタグ出力：ボタンID有りかつパーツ権限情報がNullの場合の出力確認")
    void test_doStartTag_print_buttonId_partsPrivilegeResultInfo_null() throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(INFO_COMMAND_ID, "test_button_id", false, false);
        // パーツ権限情報がNull
        setupMockPartsPrivilegeResultInfo(true, PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_ON);

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1)).print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\""
            + mapInfo.get(INFO_ADD_STYLE) + "" + "\"" + "test_str_style" + " onclick=\""
            + (String) mapInfo.get(INFO_ONCLICK) + "\" tabindex=\"" + ""
            + "\">\n");
        verify(out, times(1)).print("    <img alt=\"\" src=\"" + mapInfo.get(INFO_IMAGE) + "\">\n");
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ボタンタグ出力：ボタンID有りかつデータ入力時確認ダイアログを表示する場合の出力確認")
    void test_doStartTag_print_buttonId_privilegeDisplayDiscardChangeFlag_on() throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(INFO_COMMAND_ID, "test_button_id", false, false);
        // パーツ権限情報有り、データ入力時確認ダイアログを表示する
        setupMockPartsPrivilegeResultInfo(false, PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_ON);
        String uncOnClick = URLEncoder.encode((String) mapInfo.get(INFO_ONCLICK), "UTF-8");
        String warmingAlertProcess = String.format(WARMING_ALERT_PROCESS, 1, uncOnClick);
        String onclick = "var success = " + warmingAlertProcess + "if (success) { " + (String) mapInfo.get(INFO_ONCLICK) + "}";

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1)).print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\""
            + mapInfo.get(INFO_ADD_STYLE) + "" + "\"" + "test_str_style" + " onclick=\""
            + onclick + "\" tabindex=\"" + ""
            + "\">\n");
        verify(out, times(1)).print("    <img alt=\"\" src=\"" + mapInfo.get(INFO_IMAGE) + "\">\n");
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ボタンタグ出力：ボタンID有りかつデータ入力時確認ダイアログを表示しない場合の出力確認")
    void test_doStartTag_print_buttonId_privilegeDisplayDiscardChangeFlag_off() throws JspException, GnomesAppException, IOException {
        setupMockButtonInfo(INFO_COMMAND_ID, "test_button_id", false, false);
        // パーツ権限情報有り、データ入力時確認ダイアログを表示する
        setupMockPartsPrivilegeResultInfo(false, PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_OFF);
        String uncOnClick = URLEncoder.encode((String) mapInfo.get(INFO_ONCLICK), "UTF-8");
        String warmingAlertProcess = String.format(WARMING_ALERT_PROCESS, 0, uncOnClick);
        String onclick = "var success = " + warmingAlertProcess + "if (success) { " + (String) mapInfo.get(INFO_ONCLICK) + "}";

        int result = cTagButton.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);
        // Domが生成されていること
        verify(out, times(1)).print("<div name=\"" + mapInfo.get(INFO_BUTTON_NAME) + "\" class=\""
            + mapInfo.get(INFO_ADD_STYLE) + "" + "\"" + "test_str_style" + " onclick=\""
            + onclick + "\" tabindex=\"" + ""
            + "\">\n");
        verify(out, times(1)).print("    <img alt=\"\" src=\"" + mapInfo.get(INFO_IMAGE) + "\">\n");
        verify(out, times(1)).print("</div>\n");
    }

    private void setupMockButtonInfo(String commandId, String buttonId, boolean isEmptyImagePath, boolean isPanecon) throws GnomesAppException {
        mapInfo = new HashMap<>();
        mapInfo.put(INFO_BUTTON_NAME, "test_button");
        mapInfo.put(INFO_BUTTON_RESOUCE_ID, "test_button_resource_id");

        if (commandId.equals(INFO_COMMAND_ID)) {
            mapInfo.put(INFO_COMMAND_ID, "test_command_id");
        } else if (commandId.equals(INFO_BEAN_COMMAND_ID)){
            mapInfo.put(INFO_BEAN_COMMAND_ID, "test_bean_command_id");
        }

        mapInfo.put(INFO_ONCLICK, "test_onclick");
        mapInfo.put(INFO_IMAGE, isEmptyImagePath ? null : "test_image");
        mapInfo.put(INFO_OK_RESOURCE_ID, "test_ok_resource_id");
        mapInfo.put(INFO_STYLE_CLASS, "test_style_class");
        mapInfo.put(INFO_ADD_STYLE, "test_add_style");
        mapInfo.put(INFO_BUTTON_ID, buttonId);
        mapInfo.put(INFO_IS_PANECON, isPanecon ? String.valueOf(isPanecon) : "");

        Map<String, Object> buttonDict = new HashMap<>();
        buttonDict.put(cTagButton.getDictId(), mapInfo);

        Whitebox.setInternalState(cTagDictionary, "buttonDict", buttonDict);
    }

    private void setupMockPartsPrivilegeResultInfo(boolean isNull, PrivilegeDisplayDiscardChangeFlag flag) {
        Map<String, String> mapAttribute = new HashMap<>();
        mapAttribute.put(GnomesCTagBasePrivilege.MAP_KEY_ONCLICK, (String)mapInfo.get(INFO_ONCLICK));
        mapAttribute.put(GnomesCTagBasePrivilege.MAP_KEY_DISABLE, "");

        doReturn(isNull ? null : partsPrivilegeResultInfo).when(cTagButton).getPartsPrivilegeResultInfo(anyString(), any());
        doReturn(mapAttribute).when(cTagButton).getButtonAttribute(any(PartsPrivilegeResultInfo.class), anyString(),
            anyString(), anyString(), anyString(), any(Locale.class));
        doReturn(flag).when(partsPrivilegeResultInfo).getDisplayDiscardChangeFlag();
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
