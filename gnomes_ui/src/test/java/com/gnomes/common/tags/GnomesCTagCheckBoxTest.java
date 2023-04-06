package com.gnomes.common.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagCheckBoxTest {

    /** 辞書：ラベルリソースID */
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";
    /** 辞書：チェックボックスの選択されたボタン値 */
    private static final String INFO_SELECT_ITEM_BEAN = "select_item_bean";
    /** 辞書：チェックボックスの参照名 */
    private static final String INFO_CHECK_BOX_NAME = "checkbox_name";
    /** 辞書：チェックボックスのアイテムリスト */
    private static final String INFO_CHECK_BOX_ITEM = "checkbox_item";
    /** 辞書：縦・横指定 **/
    private static final String INFO_IS_VERTICAL = "is_vertical";
    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";
    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";
    /** 辞書：INPUT追加クラス */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";
    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";
    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

    private static final String STR_VERTICAL_BR = "<br>";
    private static final String STR_VERTICAL_COL_DATA = "common-vertical-col-data";
    private static final String STR_HEADER_COL_DATA = "common-header-col-data";
    private static final String STR_TITLE_ADD_CLASS = " common-panecon-header-col-title-checkbox ";
    private static final String STR_INPUT_ADD_STYLE = "common-input-checkbox ";

    private static  final String ONCHANGE = " onchange=\"" + GnomesCTagBase.SET_WARMING_FLAG +  "\"";

    @Spy
    @InjectMocks
    GnomesCTagCheckBox cTagCheckBox;

    @Mock
    JspWriter out;
    @Mock
    LogHelper logHelper;
    @Mock
    PageContext pageContext;
    @Mock
    GnomesSessionBean gnomesSessionBean;
    @Mock
    ContainerResponse responseContext;
    @Mock
    GnomesCTagDictionary cTagDictionary;

    private TestFormBean bean;
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

        List<String> lstCheckBoxChecked = new ArrayList<>();
        lstCheckBoxChecked.add("test_checkBoxItemValue");

        doReturn(out).when(pageContext).getOut();
        doReturn(Locale.getDefault()).when(gnomesSessionBean).getUserLocale();
        doReturn(cTagDictionary).when(cTagCheckBox).getCTagDictionary();
        doReturn(lstCheckBoxChecked).when(responseContext).getResponsesFormBean(any(), anyString(), anyString());
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));


        cTagCheckBox.setBean(bean);
        cTagCheckBox.setDictId("test_dict_id");
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("チェックボックスタグ出力：BeanがNullだった場合GnomesAppExceptionが発生")
    void test_doStartTag_GnomesAppException_bean_null() throws JspException {
        cTagCheckBox.setBean(null);

        JspException e = assertThrows(JspException.class, () -> cTagCheckBox.doStartTag());
        assertTrue(e.getCause() instanceof GnomesAppException);
        assertTrue(e.getMessage().endsWith(GnomesCTagBase.NO_BEAN_ERR_MES));
    }

    @Test
    @DisplayName("チェックボックスタグ出力：縦出力時の改行設定確認")
    void test_doStartTag_isVertical_true() throws Exception {
        setupMockCheckBoxInfo(true, false, false);
        setupMockGetData(true);

        int result = cTagCheckBox.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);

        // class="common-vertical-col-data"
        verify(out, times(1)).print("<div class=\"" + STR_VERTICAL_COL_DATA + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\">");
        verify(out, times(1)).print("<input type=\"checkbox\" name=\""
            + (String) mapInfo.get(INFO_CHECK_BOX_NAME) + "\" value=\""
            + "test_checkBoxItemValue"
            + "\"" + "checked" +  " class=\"" + (String) mapInfo.get(INFO_INPUT_ADD_STYLE) + "\"" + ONCHANGE
            + ">"
            + "test_checkBoxItemText"
            + STR_VERTICAL_BR + "\n"); // 改行有り
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("チェックボックスタグ出力：縦出力をしない改行設定確認")
    void test_doStartTag_isVertical_false() throws Exception {
        setupMockCheckBoxInfo(false, false, false);
        setupMockGetData(true);

        int result = cTagCheckBox.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);

        // class="common-header-col-data"
        verify(out, times(1)).print("<div class=\"" + STR_HEADER_COL_DATA + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\">");
        verify(out, times(1)).print("<input type=\"checkbox\" name=\""
            + (String) mapInfo.get(INFO_CHECK_BOX_NAME) + "\" value=\""
            + "test_checkBoxItemValue"
            + "\"" + "checked" +  " class=\"" + (String) mapInfo.get(INFO_INPUT_ADD_STYLE) + "\"" + ONCHANGE
            + ">"
            + "test_checkBoxItemText"
            + "\n"); // 改行無し
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("チェックボックスタグ出力：端末が工程端末の場合のスタイルクラス確認")
    void test_doStartTag_isPanecon_true() throws Exception {
        setupMockCheckBoxInfo(true, true, false);
        setupMockGetData(true);

        int result = cTagCheckBox.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);

        verify(out, times(1)).print("<div class=\"" + STR_VERTICAL_COL_DATA + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\">");
        verify(out, times(1)).print("<input type=\"checkbox\" name=\""
            + (String) mapInfo.get(INFO_CHECK_BOX_NAME) + "\" value=\""
            + "test_checkBoxItemValue"
            // スタイル追加
            + "\"" + "checked" +  " class=\"" + STR_INPUT_ADD_STYLE + (String) mapInfo.get(INFO_INPUT_ADD_STYLE) + "\"" + ONCHANGE
            + ">"
            + "test_checkBoxItemText"
            + STR_VERTICAL_BR + "\n");
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("チェックボックスタグ出力：端末が工程端末かつラベル出力の確認")
    void test_doStartTag_isPanecon_true_isPrintLabel_true() throws Exception {
        setupMockCheckBoxInfo(false, true, true);
        setupMockGetData(true);

        int result = cTagCheckBox.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);

        // ラベル出力
        verify(out, times(1)).print("  <div class=\"common-header-col-title"
            // スタイル追加
            + STR_TITLE_ADD_CLASS + (String) mapInfo.get(INFO_LABEL_CLASS_NAME) + "\">"
            + (String) mapInfo.get(INFO_LABEL_RESOUCE_ID) + "</div>");
        verify(out, times(1)).print("<div class=\"" + STR_HEADER_COL_DATA + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\">");
        verify(out, times(1)).print("<input type=\"checkbox\" name=\""
            + (String) mapInfo.get(INFO_CHECK_BOX_NAME) + "\" value=\""
            + "test_checkBoxItemValue"
            // スタイル追加
            + "\"" + "checked" + " class=\"" + STR_INPUT_ADD_STYLE + (String) mapInfo.get(INFO_INPUT_ADD_STYLE) + "\"" + ONCHANGE
            + ">"
            + "test_checkBoxItemText"
            + "\n"); // 改行無し
        verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("チェックボックスタグ出力：チェックボックスが非活性で出力されることの確認")
    void test_doStartTag_inputActivity_false() throws Exception {
        setupMockCheckBoxInfo(true, false, false);
        setupMockGetData(false); // 非活性

        int result = cTagCheckBox.doStartTag();

        assertEquals(result, Tag.SKIP_BODY);

        verify(out, times(1)).print("<div class=\"" + STR_VERTICAL_COL_DATA + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\">");
        // disabled
        verify(out, times(1)).print("<input type=\"checkbox\" disabled "
            + "checked" + " class=\"" + (String) mapInfo.get(INFO_INPUT_ADD_STYLE) + "\"" + ONCHANGE + ">"
            + "test_checkBoxItemText"
            + STR_VERTICAL_BR + "\n");
        verify(out, times(1)).print("</div>\n");
    }

    private void setupMockCheckBoxInfo(boolean isVertical, boolean isPanecon, boolean isPrintLabel) throws GnomesAppException {
        mapInfo = new HashMap<String, Object>();

        mapInfo.put(INFO_SELECT_ITEM_BEAN, "0");
        mapInfo.put(INFO_CHECK_BOX_NAME, "test_check_box");
        mapInfo.put(INFO_IS_VERTICAL, isVertical ? "true" : "false");
        mapInfo.put(INFO_ADD_CLASS, "test_add_class");
        mapInfo.put(INFO_IS_PANECON, isPanecon ? "true" : "");
        mapInfo.put(INFO_INPUT_ADD_STYLE, "test_input_add_style");
        mapInfo.put(INFO_CHECK_BOX_ITEM, "test_check_box_item");
        mapInfo.put(INFO_LABEL_RESOUCE_ID, isPrintLabel ?  "test_label_resource_id" : null);
        mapInfo.put(INFO_LABEL_CLASS_NAME, "test_label_class_name");
        mapInfo.put(INFO_INPUT_ACTIVITY, "test_input_activity");

        doReturn(mapInfo).when(cTagDictionary).getCheckBoxInfo(anyString());
    }

    private void setupMockGetData(boolean inputActivity) throws Exception {
        List<Object> checkBoxItems = new ArrayList<>();
        checkBoxItems.add("test_item");
        doReturn(checkBoxItems, inputActivity, "test_checkBoxItemValue", "test_checkBoxItemText").when(cTagCheckBox)
            .getData(any(), any(), anyString());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
