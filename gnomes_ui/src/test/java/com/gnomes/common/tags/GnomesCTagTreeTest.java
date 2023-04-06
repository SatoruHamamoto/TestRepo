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
import javax.servlet.jsp.PageContext;

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
import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.TreeData;
import com.gnomes.common.data.TreeSelectInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;

class GnomesCTagTreeTest {

    /** 辞書：ツリーID */
    private static final String INFO_TREE_ID = "tree_id";
    /** ツリーデータ定義Bean */
    private static final String INFO_TREE_DATA_BEAN = "tree_data_bean";
    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";
    /** 辞書：選択・開閉項目情報参照Bean */
    private static final String INFO_TREE_NODE_INFO_BEAN = "tree_node_info_bean";
    /** ツリーコマンド実行フォーマット */
    private static final String COMMAND_SCRIPT_FORMAT_TREE = "treeCommandSubmit(this.id, \'%s\');";

    private static final String SELECTED_ID = "test_tree_id_0";
    private static final String CLOSED_ID = "test_tree_id_1";

    @Spy
    @InjectMocks
    GnomesCTagTree cTagTree;

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

    private InOrder inOrder;
    private Map<String, Object> mapInfo;
    private List<TreeData> treeList;
    TreeSelectInfo treeSelectInfo;
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
        cTagTree.setBean(new TestFormBean());
        cTagTree.setDictId("test_dict_id");

        doReturn(Locale.getDefault()).when(gnomesSessionBean).getUserLocale();
        doReturn(out).when(pageContext).getOut();
        doReturn(cTagDictionary).when(cTagTree).getCTagDictionary();
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("ツリータグ出力：接続領域が異なる場合非活性になることの確認")
    void test_doStartTag_inactive_disabled() throws JsonProcessingException, Exception {
        mapInfo("test_class", "test_tree_data_bean", "test_tree_node_info_bean");
        // 接続領域が異なる場合
        setupMockJudgeAndSetActivityInClass(true);
        setupMockGetData(initTreeData(false, false), null);
        assertEquals(0, cTagTree.doStartTag());

        // 非活性になる
        inOrder.verify(out, times(1)).print("<ul " + " disabled " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // ツリー出力確認
        verifyOutTreeView(1);

        inOrder.verify(out, times(1)).print("</ul>\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + null + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "\" value='" + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    @Test
    @DisplayName("ツリータグ出力：スタイルシート追加クラスがNullの場合uiタグのclass属性にスタイルシート追加クラスが出力されないことの確認")
    void test_doStartTag_addStyle_null() throws JsonProcessingException, Exception {
        mapInfo(null, "test_tree_data_bean", "test_tree_node_info_bean");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(false, false), null);
        assertEquals(0, cTagTree.doStartTag());

        // スタイルシート追加クラスが出力されない
        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID) + "\" class=\"treeview " + "\" >\n");

        // ツリー出力確認
        verifyOutTreeView(1);

        inOrder.verify(out, times(1)).print("</ul>\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + null + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "\" value='" + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    @Test
    @DisplayName("ツリータグ出力：ツリーデータ定義Bean名が未設定の場合ツリーが出力されないことの確認")
    void test_doStartTag_treeDataBeanName_empty() throws JsonProcessingException, Exception {
        mapInfo("test_class", "", "test_tree_node_info_bean");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(false, false), null);
        assertEquals(0, cTagTree.doStartTag());

        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // ツリーは出力されない
        verifyOutTreeView(0);

        inOrder.verify(out, times(1)).print("</ul>\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + null + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + null + "\" value='" + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + null + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    @Test
    @DisplayName("ツリータグ出力：ツリーデータが存在しない場合ツリーが出力されないことの確認")
    void test_doStartTag_treeData_exists_false() throws JsonProcessingException, Exception {
        mapInfo("test_class", "test_tree_data_bean", "test_tree_node_info_bean");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(true, false), null);
        assertEquals(0, cTagTree.doStartTag());

        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // ツリーは出力されない
        verifyOutTreeView(0);

        inOrder.verify(out, times(1)).print("</ul>\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + null + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + null + "\" value='" + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + null + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    @Test
    @DisplayName("ツリータグ出力：辞書：選択・開閉項目情報参照Bean名が未設定の場合出力されているツリーが閉じられていないかつツリーが選択状態でないことの確認")
    void test_doStartTag_treeNodeInfoBeanName_empty() throws JsonProcessingException, Exception {
        mapInfo("test_class", "test_tree_data_bean", "");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(false, true), createTreeNodeInfoJson(SELECTED_ID, false));
        assertEquals(0, cTagTree.doStartTag());

        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // 'selected'が出力されない
        // liタグにclass='closed'がつかないこと
        verifyOutTreeView();

        inOrder.verify(out, times(1)).print("</ul>\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + null + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "\" value='" + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + "" + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    @Test
    @DisplayName("ツリータグ出力：BeanからJSONデータが取得できない場合出力されているツリーが閉じられていないかつツリーが選択状態でないことの確認")
    void test_doStartTag_getData_json_fail() throws JsonProcessingException, Exception {
        mapInfo("test_class", "test_tree_data_bean", "test_tree_node_info_bean");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(false, true), null);
        assertEquals(0, cTagTree.doStartTag());

        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // 'selected'が出力されない
        // liタグにclass='closed'がつかないこと
        verifyOutTreeView();

        inOrder.verify(out, times(1)).print("</ul>\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + null + "\" />\n");
        // JSONデータが出力されない
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "\" value='" + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }



    @Test
    @DisplayName("ツリータグ出力：取得したJSONデータに閉じている項目IDが存在しない場合出力されているツリーが閉じられていないことの確認")
    void test_doStartTag_idList_empty() throws JsonProcessingException, Exception {
        String treeNodeInfoJson = createTreeNodeInfoJson(SELECTED_ID, true);
        mapInfo("test_class", "test_tree_data_bean", "test_tree_node_info_bean");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(false, true), treeNodeInfoJson);
        assertEquals(0, cTagTree.doStartTag());

        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // JSONから選択状態のIDは取得できているため'selected'出力される
        // liタグにclass='closed'がつかないこと
        verifyOutTreeView();

        inOrder.verify(out, times(1)).print("</ul>\n");
        // JSONから選択状態のIDは取得できているためツリー選択項目IDは出力される
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + SELECTED_ID + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "\" value='" + treeNodeInfoJson + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    @Test
    @DisplayName("ツリータグ出力：取得したJSONデータにツリー選択項目IDが存在しない場合ツリーが選択状態でないことの確認")
    void test_doStartTag_treeSelectedId_empty() throws JsonProcessingException, Exception {
        String treeNodeInfoJson = createTreeNodeInfoJson("", false);
        mapInfo("test_class", "test_tree_data_bean", "test_tree_node_info_bean");
        setupMockJudgeAndSetActivityInClass(false);
        setupMockGetData(initTreeData(false, true), treeNodeInfoJson);
        assertEquals(0, cTagTree.doStartTag());

        inOrder.verify(out, times(1)).print("<ul " + " id=\"" + (String) mapInfo.get(INFO_TREE_ID)
            + "\" class=\"treeview " + (String) mapInfo.get(INFO_ADD_STYLE) + "\" >\n");

        // 'selected'が出力されない
        // JSONから閉じている項目ID配列は取得できているためclass='closed'は出力される
        verifyOutTreeView();

        inOrder.verify(out, times(1)).print("</ul>\n");
        // ツリー選択項目IDは出力されない
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId" + "\" value=\"" + "" + "\" />\n");
        inOrder.verify(out, times(1)).print("<input type=\"hidden\" name=\"" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "\" value='" + treeNodeInfoJson + "' />\n");
        inOrder.verify(out, times(1)).print("<script>\n");
        inOrder.verify(out, times(1)).print("$(document).ready(function () {\n");
        inOrder.verify(out, times(1)).print("  $(\"#" + (String) mapInfo.get(INFO_TREE_ID) + "\").treeview({ \n");
        inOrder.verify(out, times(1)).print("    toggle : function(){treeCommandSubmit($('input[name=" + (String) mapInfo.get(INFO_TREE_ID)
                + "_SelectedNodeId" + "]').val(), '" + (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) + "');} \n");
        inOrder.verify(out, times(1)).print("  });\n");
        inOrder.verify(out, times(1)).print("});\n");
        inOrder.verify(out, times(1)).print("</script>\n");
    }

    /**
     * ツリー出力の実行回数検証
     * パラメータに渡した数値分ツリーが出力されたかを検証する
     * @param count 実行回数
     * @throws IOException
     */
    private void verifyOutTreeView(int count) throws IOException {
        String treeNodeInfoBeanName = count > 0 ? (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN) : null;

        for (TreeData tree : treeList) {
            inOrder.verify(out, times(count))
                .print("<li><span id=\"" + tree.getId() + "\" class=\"client-treeview-column" + "" + "\" onclick=\""
                    + String.format(GnomesCTagBase.COMMAND_SET_FORMAT, tree.getCommandId())
                    + String.format(COMMAND_SCRIPT_FORMAT_TREE, treeNodeInfoBeanName) + "\">"
                    + tree.getName() + "</span>");
            inOrder.verify(out, times(count)).print("</li>\n");
        }
    }

    /**
     * 出力ツリーの状態検証
     * 検証前に作成したモックの内容によって出力されたツリーの選択・開閉状態を検証する
     * @throws IOException
     */
    private void verifyOutTreeView() throws IOException {
        String isSelectedClass = "";
        String isClosedClass = "";

        if (!StringUtil.isNullOrEmpty((String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN))) {
            if (treeSelectInfo != null) {
                if (!StringUtil.isNullOrEmpty(treeSelectInfo.getTreeExecuteId())) {
                    isSelectedClass = " selected";
                }
                if (!StringUtil.isNullOrEmpty(treeSelectInfo.getTreeClosedIdArray()[0])) {
                    isClosedClass = " class=\"closed\"";
                }
            }
        }

        for (int i = 0; i < treeList.size(); i++) {
            TreeData tree = treeList.get(i);
            if (i == 0) {
                inOrder.verify(out, times(1))
                    .print("<li><span id=\"" + tree.getId() + "\" class=\"client-treeview-column" + isSelectedClass + "\" onclick=\""
                    + String.format(GnomesCTagBase.COMMAND_SET_FORMAT, tree.getCommandId())
                    + String.format(COMMAND_SCRIPT_FORMAT_TREE, (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN)) + "\">"
                    + tree.getName() + "</span>");
            } else {
                inOrder.verify(out, times(1))
                    .print("<li" + isClosedClass + "><span id=\"" + tree.getId() + "\" class=\"client-treeview-column folder"
                        + "" + "\" onclick=\""
                        + String.format(GnomesCTagBase.COMMAND_SET_FORMAT, tree.getCommandId()) + "$('input[name="
                        + ((String) mapInfo.get(INFO_TREE_ID) + "_SelectedNodeId") + "]').val(this.id);" + "\">"
                        + tree.getName() + "</span>");
                inOrder.verify(out, times(1)).print("<ul>\n");

                List<TreeData> childTreeList = tree.getTreeList();
                inOrder.verify(out, times(1))
                    .print("<li><span id=\"" + childTreeList.get(0).getId() + "\" class=\"client-treeview-column" + ""
                        + "\" onclick=\""
                        + String.format(GnomesCTagBase.COMMAND_SET_FORMAT, childTreeList.get(0).getCommandId())
                        + String.format(COMMAND_SCRIPT_FORMAT_TREE, (String) mapInfo.get(INFO_TREE_NODE_INFO_BEAN))
                        + "\">"
                        + childTreeList.get(0).getName() + "</span>");
                inOrder.verify(out, times(1)).print("</li>\n");

                inOrder.verify(out, times(1)).print("</ul>\n");
            }
            inOrder.verify(out, times(1)).print("</li>\n");
        }
    }

    private void mapInfo(String addStyle, String treeDataBeanName, String treeNodeInfoBeanName) throws GnomesAppException {
        mapInfo = new HashMap<String, Object>();

        mapInfo.put(INFO_TREE_ID, "test_tree");
        mapInfo.put(INFO_TREE_DATA_BEAN, treeDataBeanName);
        mapInfo.put(INFO_TREE_NODE_INFO_BEAN, treeNodeInfoBeanName);
        if (addStyle != null) {
            mapInfo.put(INFO_ADD_STYLE, addStyle);
        }
        doReturn(mapInfo).when(cTagDictionary).getTreeInfo(anyString());
    }

    private void setupMockJudgeAndSetActivityInClass(boolean isInactive) {
        String inactive = "";
        if (isInactive) {
            inactive = " disabled ";
        }
        doReturn(inactive).when(cTagTree).judgeAndSetActivityInClass(anyMap());
    }

    private void setupMockGetData(TreeData treeData, String json) throws Exception {
        doReturn(treeData, json).when(cTagTree).getData(any(), any(), any());
    }

    private TreeData initTreeData(boolean isEmptyTreeList, boolean existsChild) {
        TreeData data = new TreeData();
        treeList = new ArrayList<TreeData>();
        for (int i = 0; i < 2; i++) {
            TreeData treeData = new TreeData();
            treeData.setId("test_tree_id_" + i);
            treeData.setName("test_tree_name_" + i);
            treeData.setCommandId("test_tree_command_id_" + i);
            List<TreeData> childList = new ArrayList<>();
            if (existsChild && i == 1) {
                TreeData childTree = new TreeData();
                childTree.setId("child_tree_id");
                childTree.setName("child_tree_name");
                childTree.setCommandId("child_tree_command_id");
                childTree.setTreeList(new ArrayList<TreeData>());
                childList.add(childTree);
            }
            treeData.setTreeList(childList);
            treeList.add(treeData);
        }
        if (!isEmptyTreeList) {
            data.setTreeList(treeList);
        }
        return data;
    }

    private String createTreeNodeInfoJson(String id, boolean isEmpty) throws JsonProcessingException {
        treeSelectInfo = new TreeSelectInfo();
        String[] ids = new String[1];
        if (!isEmpty) {
            ids[0] = CLOSED_ID;
        }
        treeSelectInfo.setTreeExecuteId(id);
        treeSelectInfo.setTreeClosedIdArray(ids);

        ObjectMapper mapper = new ObjectMapper();
       return mapper.writeValueAsString(treeSelectInfo);
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
