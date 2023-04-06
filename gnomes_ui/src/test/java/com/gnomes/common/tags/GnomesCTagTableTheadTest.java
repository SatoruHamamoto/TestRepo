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
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;

class GnomesCTagTableTheadTest {

    /** 辞書：スタイルクラス */
    private static final String INFO_CLASS = "class";
    /** 辞書：スタイル */
    private static final String INFO_STYLE = "style";
    /** 辞書：タグ名 */
    private static final String INFO_TAG_NAME = "name";

    @InjectMocks
    GnomesCTagTable cTagTable;

    @Mock
    JspWriter out;
    @Mock
    GnomesCTagTableCommon gnomesCTagTableCommon;

    private InOrder inOrder;
    private MockedStatic<ResourcesHandler> resHandlerMock;
    private int lastDispIndex = -1;
    private Map<String, Object> mapColInfo;
    private Map<String, String> headInfo;

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
        inOrder = inOrder(out);
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("theadタグの出力：ソートダイアログIDがNullの場合に設定された値が出力されることの確認")
    void test_outHeader_print_tableSortdialogId_null() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getAllTagTypeTableInfo();
        lastDispIndex = TagType.HIDDEN.getIndex() - 1;
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, lastDispIndex);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n"); // IDが出力されない
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：ソートダイアログIDがNullでない場合に設定された値が出力されることの確認")
    void test_outHeader_print_tableSortdialogId_not_null() throws GnomesAppException, IOException {
        String tableSortdialogId = "test_tableSortdialogId";
        List<Map<String, Object>> info = getAllTagTypeTableInfo();
        lastDispIndex = TagType.HIDDEN.getIndex() - 1;
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, tableSortdialogId);
        assertEquals(index, lastDispIndex);

        inOrder.verify(out, times(1)).print("<thead>\n");
        // IDが出力される
        inOrder.verify(out, times(1)).print("<tr id=\""+ tableSortdialogId +"_tr\" name=\""+ tableSortdialogId +"_tr\">\n");
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプがチェックボックスの場合<input type='checkbox'>が出力されることの確認")
    void test_outHeader_print_tagType_checkbox() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.CHECKBOX, null);
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, 0);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------- チェックボックス -------
        inOrder.verify(out, times(1)).print("<th class=\"common-table-checkbox\"");
        inOrder.verify(out, times(1)).print(" style=\""+ headInfo.get(INFO_STYLE) + "\"");
        inOrder.verify(out, times(1)).print(">");
        inOrder.verify(out, times(1)).print("<input type=\"checkbox\" onclick=\"" + "checkAll('" + (String) mapColInfo.get(INFO_TAG_NAME) + "', this.checked, true);" + "\">");
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプがボタンの場合<th></th>のみが出力されることの確認")
    void test_outHeader_print_tagType_button() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.BUTTON, null);
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, 0);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ ボタン ------------
        inOrder.verify(out, times(1)).print("<th");
        inOrder.verify(out, times(1)).print(" style=\""+ headInfo.get(INFO_STYLE) + "\"");
        inOrder.verify(out, times(1)).print(">");
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプがイメージパターンの場合リソースIDが出力されることの確認")
    void test_outHeader_print_tagType_img_pattern() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.IMG_PATTERN, null);
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, 0);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------- イメージパターン -------
        inOrder.verify(out, times(1)).print("<th");
        inOrder.verify(out, times(1)).print(" style=\""+ headInfo.get(INFO_STYLE) + "\"");
        inOrder.verify(out, times(1)).print(">");
        inOrder.verify(out, times(1)).print(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID));
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプが非表示項目の場合<th style='display:none'>が出力されることの確認")
    void test_outHeader_print_tagType_hidden() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.HIDDEN, null);
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, -1);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ---------- 非表示項目 ----------
        inOrder.verify(out, times(1)).print("<th style=\"display:none\">");
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプがその他かつスタイルクラスが存在する場合<th class=スタイルクラス>が出力されることの確認")
    void test_outHeader_print_tagType_other_styleClass_not_null() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.TEXT, "test_style");
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, 0);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ その他 ------------
        // <th class=スタイルクラス>が出力される
        inOrder.verify(out, times(1)).print("<th class=\"" + headInfo.get(INFO_CLASS) + "\"");
        inOrder.verify(out, times(1)).print("\" style=\"" + headInfo.get(INFO_STYLE));
        inOrder.verify(out, times(1)).print(">");
        inOrder.verify(out, times(1)).print(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID));
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプがdateかつスタイルクラスが存在しない場合<th></th>のみ出力されることの確認")
    void test_outHeader_print_tagType_date_styleClass_null() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.DATE, null);
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, 0);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------- date -------------
        // <th></th>のみ出力される
        inOrder.verify(out, times(1)).print("<th");
        inOrder.verify(out, times(1)).print(" style=\""+ headInfo.get(INFO_STYLE) + "\"");
        inOrder.verify(out, times(1)).print(">");
        inOrder.verify(out, times(1)).print(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID));
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    @Test
    @DisplayName("theadタグの出力：タグタイプがその他かつスタイルクラスが存在しない場合<th></th>のみ出力されることの確認")
    void test_outHeader_print_tagType_other_styleClass_null() throws GnomesAppException, IOException {
        List<Map<String, Object>> info = getTableInfo(TagType.TEXT, null);
        int index = ReflectionTestUtils.invokeMethod(cTagTable, "outHeader", out, Locale.getDefault(), info, null);
        assertEquals(index, 0);

        inOrder.verify(out, times(1)).print("<thead>\n");
        inOrder.verify(out, times(1)).print("<tr>\n");
        // ------------ その他 ------------
        // <th></th>のみ出力される
        inOrder.verify(out, times(1)).print("<th");
        inOrder.verify(out, times(1)).print(" style=\""+ headInfo.get(INFO_STYLE) + "\"");
        inOrder.verify(out, times(1)).print(">");
        inOrder.verify(out, times(1)).print(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID));
        inOrder.verify(out, times(1)).print("</th>\n");
        // --------------------------------
        inOrder.verify(out, times(1)).print("</tr>\n");
        inOrder.verify(out, times(1)).print("</thead>\n");
    }

    private List<Map<String, Object>> getAllTagTypeTableInfo() throws GnomesAppException {
        List<Map<String, Object>> lstTableInfo = new ArrayList<>();
        int i = 0;
        while (true) {
            Map<String, Object> tr = new HashMap<>();
            mapColInfo = new HashMap<String, Object>();
            headInfo = new HashMap<String, String>();

            TagType tag = TagType.valueOf(i);
            mapColInfo.put(INFO_TAG_NAME, "test_tag_name" + i);
            headInfo.put(GnomesCTagTable.INFO_TAG_TYPE, tag != null ? tag.getType() : null);
            headInfo.put(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID, "test_label_" + i);
            headInfo.put(INFO_STYLE, "border: 2px solid;");

            tr.put(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO, mapColInfo);
            tr.put(GnomesCTagDictionary.MAP_NAME_TABLE_INFO, headInfo);
            lstTableInfo.add(tr);

            if (tag == null) {
                break;
            }
            i++;
        }
        return lstTableInfo;
    }

    private List<Map<String, Object>> getTableInfo(TagType tagType, String styleClass) throws GnomesAppException {
        List<Map<String, Object>> lstTableInfo = new ArrayList<>();
        Map<String, Object> tr = new HashMap<>();
        mapColInfo = new HashMap<String, Object>();
        headInfo = new HashMap<String, String>();

        mapColInfo.put(INFO_TAG_NAME, "test_tag_name");
        headInfo.put(GnomesCTagTable.INFO_TAG_TYPE, tagType != null ? tagType.getType() : null);
        headInfo.put(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID, "test_label");
        headInfo.put(INFO_CLASS, styleClass);
        headInfo.put(INFO_STYLE, "border: 2px solid;");

        tr.put(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO, mapColInfo);
        tr.put(GnomesCTagDictionary.MAP_NAME_TABLE_INFO, headInfo);
        lstTableInfo.add(tr);
        return lstTableInfo;
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
