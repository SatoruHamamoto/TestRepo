package com.gnomes.common.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
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

class GnomesCTagRadioButtonTest {

    /** 辞書：選択値取得Bean */
    private static final String INFO_SELECT_ITEM_BEAN = "select_item_bean";
    /** 辞書：ラジオボタン参照用名称 */
    private static final String INFO_RADIO_NAME = "radio_name";
    /** 辞書：選択項目取得Bean */
    private static final String INFO_RADIO_ITEM = "radio_item";
    /** 辞書：縦表示か否か */
    private static final String INFO_IS_VERTICAL = "is_vertical";
    /** 辞書：ラベルリソースID */
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";
    /** 辞書：ラベル出力スタイルシートの追加設定 */
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";
    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";
    /** 辞書：INPUTの追加クラス */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";
    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";
    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

    private static final String LABEL_RESOUCE_ID = "test_resource_id";
    private static final String LABEL = "テスト";
    private static final String SELECT_ITEM = "test_value_1";

    @Spy
    @InjectMocks
    GnomesCTagRadioButton cTagRadioButton;

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
    private List<String[]> radioList;
    private String strSelectItem;
    boolean inputActivity;
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
        cTagRadioButton.setBean(new TestFormBean());
        cTagRadioButton.setDictId("test_dict_id");

        doReturn(Locale.getDefault()).when(gnomesSessionBean).getUserLocale();
        doReturn(out).when(pageContext).getOut();
        doReturn(cTagDictionary).when(cTagRadioButton).getCTagDictionary();
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：BeanがNullだった場合GnomesAppExceptionが発生することの確認")
    void test_doStartTag_GnomesAppException_bean_null() throws JspException {
        cTagRadioButton.setBean(null);

        JspException e = assertThrows(JspException.class, () -> cTagRadioButton.doStartTag());
        assertTrue(e.getCause() instanceof GnomesAppException);
        assertTrue(e.getMessage().endsWith(GnomesCTagBase.NO_BEAN_ERR_MES));
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：ラジオボタン辞書情報がすべて未設定の場合ラジオボタンが出力されないことの確認")
    void test_doStartTag_getRadioInfo_empty() throws Exception {
        setupMockGetRadioInfo(null, "", "", "", "", "", "", "", "", null, 0);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：ラベルリソースIDが設定されている場合左ラベルが出力されることの確認")
    void test_doStartTag_labelRourceId_out_left_label() throws Exception {
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "", "", "", "", "", "", "", "", null, 0);
        assertEquals(0, cTagRadioButton.doStartTag());

        // 左ラベル出力
        inOrder.verify(out, times(1)).print("  <div class=\"common-header-col-title" + "" + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2 })
    @DisplayName("ラジオボタンタグ出力：選択項目のリストが取得できた場合ラジオボタンが出力されることの確認")
    void test_doStartTag_out_radio(int itemListSize) throws Exception {
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "test_radio", "", "", "", "", "test_radio_items", "", "", null, itemListSize);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print("  <div class=\"common-header-col-title" + "" + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");

        // ラジオボタン出力
        verifyOutRadioButton();

        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：Beanから選択したボタン値が取得できた場合選択状態のラジオボタンが出力されることの確認")
    void test_doStartTag_out_select() throws Exception {
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "test_radio", "test_select", "", "", "", "test_radio_items", "", "", null, 2);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print("  <div class=\"common-header-col-title" + "" + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");

        // Beanから取得したボタン値が選択状態で出力される
        verifyOutRadioButton();

        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：縦出力が有効の場合ラジオボタンの背後に改行タグが出力されることの確認")
    void test_doStartTag_isVertical_true() throws Exception {
        String strTitleAddClass = " common-vertical-col-title";
        String strHeaderColData = "common-vertical-col-data";
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "test_radio", "test_select", "true", "", "", "test_radio_items", "", "", null, 2);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print(
            "  <div class=\"common-header-col-title" + strTitleAddClass + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + strHeaderColData + "" + "\">");

        // 改行タグ出力
        verifyOutRadioButton();

        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：工業端末の場合inputタグに追加クラスが出力されることの確認")
    void test_doStartTag_isPanecon_true() throws Exception {
        String strTitleAddClass = " common-panecon-header-col-title-radiobutton";
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "test_radio", "test_select", "", "true", "", "test_radio_items", "", "", null, 2);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print(
            "  <div class=\"common-header-col-title" + strTitleAddClass + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");

        // 追加クラス出力
        verifyOutRadioButton();

        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：ラベルの追加クラスが設定されている場合左ラベルのclass属性に追加クラスが出力されることの確認")
    void test_doStartTag_out_strLabelClassName() throws Exception {
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "test_radio", "test_select", "", "", "", "test_radio_items", "test_label_add_class",
            "", null, 1);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print("  <div class=\"common-header-col-title" + "" + " "
        // 前方にスペースを加えた追加クラスを出力
            + (String) mapInfo.get(INFO_LABEL_CLASS_NAME) + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");

        verifyOutRadioButton();

        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：Beanから取得した入力活性非活性情報がFalseである場合出力されるラジオボタンが非活性(disabled)であることの確認")
    void test_doStartTag_inputActivity_false() throws Exception {
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "test_radio", "test_select", "", "", "", "test_radio_items", "", "",
            "test_inputActivity", 2);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print("  <div class=\"common-header-col-title" + "" + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1)).print("<div class=\"" + "common-header-col-data" + "" + "\">");

        // 非活性なラジオボタンが出力される
        verifyOutRadioButton();

        inOrder.verify(out, times(1)).print("</div>\n");
    }

    @Test
    @DisplayName("ラジオボタンタグ出力：追加クラスが設定されている場合divタグに追加クラスが出力されることの確認")
    void test_doStartTag_out_addClass() throws Exception {
        setupMockGetRadioInfo(LABEL_RESOUCE_ID, "", "", "", "", "", "", "", "test_add_class", null, 0);
        assertEquals(0, cTagRadioButton.doStartTag());

        inOrder.verify(out, times(1)).print("  <div class=\"common-header-col-title" + "" + "" + "\">" + LABEL + "</div>");
        inOrder.verify(out, times(1))
        // 前方にスペースを加えた追加クラスを出力
            .print("<div class=\"" + "common-header-col-data" + " " + (String) mapInfo.get(INFO_ADD_CLASS) + "\">");
        inOrder.verify(out, times(1)).print("</div>\n");
    }

    private void verifyOutRadioButton() throws IOException {
        String strChecked = "";
        String hiddenValue = null;
        String onchange = " onchange=\"" + GnomesCTagBase.SET_WARMING_FLAG +  "\"";
        String strVerticalBr="";
        String strInputAddStyle = (String)mapInfo.get(INFO_INPUT_ADD_STYLE);
        String strRadioName = (String)mapInfo.get(INFO_RADIO_NAME);

        if (((String) mapInfo.get(INFO_IS_VERTICAL)).equals("true")) {
            strVerticalBr = "<br>";
        }
        if (!StringUtil.isNullOrEmpty((String) mapInfo.get(INFO_IS_PANECON))) {
            strInputAddStyle = "common-panecon-input-radiobutton " + strInputAddStyle;
        }
        if (!StringUtil.isNullOrEmpty(strInputAddStyle)) {
            strInputAddStyle = " class=\"" + strInputAddStyle + "\"";
        }

        for (String[] radio : radioList) {
            if (radio[0].equals(strSelectItem)) {
                strChecked = " checked=\"checked\"";
                hiddenValue = radio[0];
            }

            if (inputActivity) {
                inOrder.verify(out, times(1)).print("<input type=\"radio\" name=\"" + strRadioName + "\" value=\""
                    + radio[0] + "\"" + strChecked + strInputAddStyle + onchange + ">" + radio[1] + strVerticalBr
                    + "\n");
            } else {
                inOrder.verify(out, times(1)).print("<input type=\"radio\" disabled " +
                    strChecked + strInputAddStyle + onchange + ">" + radio[1]
                    + strVerticalBr + "\n");
                if (!StringUtil.isNullOrEmpty(hiddenValue)) {
                    inOrder.verify(out, times(1)).print(
                        "<input type=\"hidden\" name=\"" + strRadioName + "\" value=\"" + hiddenValue + "\" >\n");
                }
            }
        }
    }

    /**
     * ラジオボタン辞書情報取得処理のモック化
     * @param labelRourceId ラベルリソースID
     * @param strRadioName ラジオボタン参照用名称
     * @param labelSelectItem 選択値取得Bean
     * @param strIsVertical 縦表示か否か
     * @param isPanecon 工程端末表示か否か
     * @param strInputAddStyle INPUTの追加クラス
     * @param labelradioItems 選択項目取得Bean
     * @param strLabelClassName ラベル出力スタイルシートの追加設定
     * @param strAddClass 追加クラス
     * @param labelInputActivity 入力活性非活性Bean
     * @param listSize 項目値リストサイズ
     * @throws Exception
     */
    private void setupMockGetRadioInfo(String labelRourceId,
        String strRadioName,
        String labelSelectItem,
        String strIsVertical,
        String isPanecon,
        String strInputAddStyle,
        String labelradioItems,
        String strLabelClassName,
        String strAddClass,
        String labelInputActivity,
        int listSize) throws Exception {
        mapInfo = new HashMap<String, Object>();
        inputActivity = true;
        int size = (listSize > 2) ? 2 : listSize;

        if (labelRourceId != null) {
            mapInfo.put(INFO_LABEL_RESOUCE_ID, labelRourceId);
        }
        if (labelInputActivity != null) {
            mapInfo.put(INFO_INPUT_ACTIVITY, labelInputActivity);
            inputActivity = false;
        }

        mapInfo.put(INFO_RADIO_NAME, strRadioName);
        mapInfo.put(INFO_SELECT_ITEM_BEAN, labelSelectItem);
        mapInfo.put(INFO_IS_VERTICAL, strIsVertical);
        mapInfo.put(INFO_IS_PANECON, isPanecon);
        mapInfo.put(INFO_INPUT_ADD_STYLE, strInputAddStyle);
        mapInfo.put(INFO_RADIO_ITEM, labelradioItems);
        mapInfo.put(INFO_LABEL_CLASS_NAME, strLabelClassName);
        mapInfo.put(INFO_ADD_CLASS, strAddClass);

        doReturn(mapInfo).when(cTagDictionary).getRadioInfo(anyString());
        setupMockGetData(labelradioItems, size, inputActivity);
        setupMockGetResponseFormBean(labelSelectItem);
    }

    private void setupMockGetData(String radioItem, int listSize, boolean inputActivity) throws Exception {
        List<Object> dataList = null;
        radioList = new ArrayList<String[]>();

        if (!StringUtil.isNullOrEmpty(radioItem)) {
            dataList = new ArrayList<Object>();
            for (int i = 0; i < listSize; i++) {
                String[] radio = { ("test_value_" + i), ("test_text_" + i) };
                dataList.add("test_item_" + i);
                radioList.add(radio);
            }
        }

        if (listSize == 1) {
            if (!inputActivity) {
                doReturn(dataList)
                    .doReturn(inputActivity)
                    .doReturn(radioList.get(0)[0], radioList.get(0)[1])
                    .when(cTagRadioButton).getData(any(), any(), any());
            } else {
                doReturn(dataList)
                    .doReturn(radioList.get(0)[0], radioList.get(0)[1])
                    .when(cTagRadioButton).getData(any(), any(), any());
            }
        } else if (listSize > 1) {
            if (!inputActivity) {
                doReturn(dataList)
                    .doReturn(inputActivity)
                    .doReturn(radioList.get(0)[0], radioList.get(0)[1])
                    .doReturn(radioList.get(1)[0], radioList.get(1)[1])
                    .when(cTagRadioButton).getData(any(), any(), any());
            } else {
                doReturn(dataList)
                    .doReturn(radioList.get(0)[0], radioList.get(0)[1])
                    .doReturn(radioList.get(1)[0], radioList.get(1)[1])
                    .when(cTagRadioButton).getData(any(), any(), any());
            }
        } else {
            doReturn(dataList).when(cTagRadioButton).getData(any(), any(), any());
        }
    }

    private void setupMockGetResponseFormBean(String labelSelectItem)
        throws IllegalArgumentException, IllegalAccessException, GnomesAppException, IOException {
        strSelectItem = "";
        if (!StringUtil.isNullOrEmpty(labelSelectItem)) {
            strSelectItem = SELECT_ITEM;
        }
        doReturn(strSelectItem).when(responseContext).getResponseFormBean(any(), anyString(), anyString());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            if (result.equals(LABEL_RESOUCE_ID)) {
                result = LABEL;
            }
            return result;
        };
    }

}
