package com.gnomes.common.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagHiddenTest {

    /** 辞書：テキスト入力用のBean **/
    private static final String INFO_PARAM_NAME = "param_name";
    /** 辞書：テキスト入力参照用名称 **/
    private static final String INFO_INPUT_TEXT_NAME = "input_text_name";

    private final static String TEST_VALUE = "test_input_text";
    private final static String STR_INPUT_TEXT = "test_str_input_text";

    @Spy
    @InjectMocks
    GnomesCTagHidden cTagHidden;

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

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bean = new TestFormBean();

        setupMockHiddenInfo();
        doReturn(out).when(pageContext).getOut();
        doReturn(cTagDictionary).when(cTagHidden).getCTagDictionary();
        doReturn(STR_INPUT_TEXT).when(cTagHidden).getData(any(), any(), anyString());

        cTagHidden.setValue(null);
        cTagHidden.setBean(bean);
        cTagHidden.setDictId("test_dict_id");
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("hiddenタグ出力：valueがNullじゃない場合の出力確認")
    void test_doStartTag_value_not_null() throws JspException, IOException {
        cTagHidden.setValue(TEST_VALUE);

        assertEquals(cTagHidden.doStartTag(), Tag.SKIP_BODY);

        // setValueされたvalueが出力される
        verify(out, times(1)).print("<input type=\"text\"" + " name=\"" + (String) mapInfo.get(INFO_INPUT_TEXT_NAME)
            + "\"" + " hidden" + " value=\"" + TEST_VALUE + "\"" + ">\n");
    }

    @Test
    @DisplayName("hiddenタグ出力：valueがNullの場合の出力確認")
    void test_doStartTag_value_null() throws JspException, IOException {
        assertEquals(cTagHidden.doStartTag(), Tag.SKIP_BODY);

        // getDataで取得したvalueが出力される
        verify(out, times(1)).print("<input type=\"text\"" + " name=\"" + (String) mapInfo.get(INFO_INPUT_TEXT_NAME)
            + "\"" + " hidden" + " value=\"" + STR_INPUT_TEXT + "\"" + ">\n");
    }

    @Test
    @DisplayName("hiddenタグ出力：valueがNullかつBeanがNullの場合の出力確認")
    void test_doStartTag_value_null_bean_null() throws JspException, IOException {
        cTagHidden.setBean(null);

        assertEquals(cTagHidden.doStartTag(), Tag.SKIP_BODY);

        // value属性がない状態で出力される
        verify(out, times(1)).print("<input type=\"text\"" + " name=\"" + (String) mapInfo.get(INFO_INPUT_TEXT_NAME)
            + "\"" + " hidden" + "" + ">\n");
    }

    private void setupMockHiddenInfo() throws GnomesAppException {
        mapInfo = new HashMap<String, Object>();

        mapInfo.put(INFO_PARAM_NAME, "test_param_name");
        mapInfo.put(INFO_INPUT_TEXT_NAME, "test_input_text_name");

        doReturn(mapInfo).when(cTagDictionary).getHiddenInfo(anyString());
    }

}
