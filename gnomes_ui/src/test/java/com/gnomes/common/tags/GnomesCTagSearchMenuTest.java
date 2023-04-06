package com.gnomes.common.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.picketbox.util.StringUtil;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagSearchMenuTest {

    /** 辞書：検索メニュータイトルリソースID */
    private static final String INFO_SEARCH_MENU_TITLE_RESOURCE_ID = "search_menu_title_resource_id";
    /** 辞書：検索対象テーブルID */
    private static final String INFO_SEARCH_TABLE_ID = "search_table_id";
    /** 辞書：詳細検索ID */
    private static final String INFO_SEARCH_DETAIL_LINK = "search_detail_link";
    /** 辞書：詳細検索リンクの有無 */
    private static final String INFO_IS_SEARCH_DETAIL_LINK = "is_search_detail_link";
    /** 辞書：タイトルスタイルシート追加クラス */
    private static final String INFO_TITLE_ADD_STYLE = "add_style";
    /** 辞書：メニューボックススタイルクラス追加 */
    private static final String INFO_MENU_BOX_ADD_CLASS = "menu_box_add_style";
    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    @Spy
    @InjectMocks
    GnomesCTagSearchMenu cTagSearchMenu;

    @Mock
    JspWriter out;
    @Mock
    LogHelper logHelper;
    @Mock
    PageContext pageContext;
    @Mock
    ContainerResponse responseContext;
    @Mock
    ContainerRequest requestContext;
    @Mock
    GnomesCTagDictionary cTagDictionary;
    @Mock
    GnomesSessionBean gnomesSessionBean;

    private InOrder inOrder;
    private TestFormBean bean;
    private Map<String,Object> searchMenuInfo;

    private MstSearchInfo searchMasterInfo;
    private SearchSetting searchInfo;

    private boolean disabled;

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
        bean = new TestFormBean();
        bean.setUserLocale(Locale.JAPAN);
        cTagSearchMenu.setBean(bean);
        cTagSearchMenu.setDictId("test_dict_id");

        doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();
        doReturn(out).when(pageContext).getOut();
        doReturn(cTagDictionary).when(cTagSearchMenu).getCTagDictionary();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("検索メニュータグ出力：BeanがNullだった場合GnomesAppExceptionが発生することの確認")
    void test_doStartTag_GnomesAppException_bean_null() throws JspException {
        cTagSearchMenu.setBean(null);

        JspException e = assertThrows(JspException.class, () -> cTagSearchMenu.doStartTag());
        assertTrue(e.getCause() instanceof GnomesAppException);
        assertTrue(e.getMessage().endsWith(GnomesCTagBase.NO_BEAN_ERR_MES));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("検索メニュータグ出力：入力チェックエラーや業務エラーが発生していた場合検索設定をリクエスト情報より取得することの確認")
    void test_doStartTag_getSearchSetting_isLogicError(boolean isLogicError) throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", "", "", "", "", "");
        setupMockContext(isLogicError);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagSearchMenu.doStartTag());

        int count = isLogicError ? 1 : 0;
        verify(requestContext, times(count)).getRequestSearchSetting(anyString());
    }

    @Test
    @Disabled
    @DisplayName("検索メニュータグ出力：検索メニュータイトルリソースIDが設定されている場合ラベルが出力されることの確認")
    void test_doStartTag_out_label() throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", GnomesResourcesConstants.DI90_0029, "", "", "", "");
        setupMockContext(false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagSearchMenu.doStartTag());

        veriryOut();
    }

    @Test
    @Disabled
    @DisplayName("検索メニュータグ出力：タイトルスタイルシート追加クラスが設定されている場合spanタグに追加クラスが出力されることの確認")
    void test_doStartTag_out_titleAddStyle() throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", GnomesResourcesConstants.DI90_0029,
            "test_title_add_style", "", "", "");
        setupMockContext(false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagSearchMenu.doStartTag());

        veriryOut();
    }

    @Test
    @Disabled
    @DisplayName("検索メニュータグ出力：メニューボックススタイルシート追加クラスが設定されている場合メニューボックス側のdivタグに追加クラスが出力されることの確認")
    void test_doStartTag_out_menuBoxAddStyle() throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", GnomesResourcesConstants.DI90_0029,
            "test_title_add_style", "test_menubox_add_style", "", "");
        setupMockContext(false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagSearchMenu.doStartTag());

        veriryOut();
    }

    @Test
    @Disabled
    @DisplayName("検索メニュータグ出力：工程端末表示の場合工程端末であることを示す追加クラスが各タグに出力されることの確認")
    void test_doStartTag_isPanecon_true() throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", GnomesResourcesConstants.DI90_0029,
            "test_title_add_style", "test_menubox_add_style", "true", "");
        setupMockContext(false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagSearchMenu.doStartTag());

        veriryOut();
    }

    @Test
    @Disabled
    @DisplayName("検索メニュータグ出力：詳細検索リンクが有に設定されている場合検索メニュー下部に詳細検索リンクが出力されることの確認")
    void test_doStartTag_out_searchDetailLink() throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", GnomesResourcesConstants.DI90_0029,
            "test_title_add_style", "test_menubox_add_style", "true", "true");
        setupMockContext(false);
        setupMockJudgeDisplayFromConnectionArea(true);
        assertEquals(0, cTagSearchMenu.doStartTag());

        veriryOut();
    }

    @Test
    @Disabled
    @DisplayName("検索メニュータグ出力：接続領域ごと操作可否で領域が異なる場合検索メニューが非活性になることの確認")
    void test_doStartTag_judgeDisplayFromConnectionArea_false() throws Exception {
        setupMockSearchMenuInfo("test_seach_table_01", "test_search_detail_link_01", GnomesResourcesConstants.DI90_0029,
            "test_title_add_style", "test_menubox_add_style", "true", "true");
        setupMockContext(false);
        setupMockJudgeDisplayFromConnectionArea(false);
        assertEquals(0, cTagSearchMenu.doStartTag());

        veriryOut();
    }

    private void veriryOut() throws IOException {
        boolean isPanecon = !StringUtil.isNullOrEmpty((String)searchMenuInfo.get(INFO_IS_PANECON)) ? true : false;
        String inactive = !disabled ? " disabled " : "";
        String titleAddStyle = (String) searchMenuInfo.get(INFO_TITLE_ADD_STYLE);
        String strLabel = getLabel((String) searchMenuInfo.get(INFO_SEARCH_MENU_TITLE_RESOURCE_ID));
        String searchDetailLink = (String)searchMenuInfo.get(INFO_SEARCH_DETAIL_LINK);
        String searchTableTagId = searchDetailLink + "_FilterTableMenu";
        String searchTableId = (String) searchMenuInfo.get(INFO_SEARCH_TABLE_ID);
        String menuBoxAddStyle = (String) searchMenuInfo.get(INFO_MENU_BOX_ADD_CLASS);
        String dataIndexs = getDataIndex();
        String tabindex = isPanecon ? "-1" : "";

        if (isPanecon) {
            inOrder.verify(out, times(1)).print("  <div " + inactive + " class=\"common-flexMenu-search-header panecon-search-menu-header clearfix\">\n");
        } else {
            inOrder.verify(out, times(1)).print("  <div " + inactive + " class=\"common-flexMenu-search-header clearfix\">\n");
        }

        inOrder.verify(out, times(1)).print("    <span class=\"common-flexMenu-search-header-title " + titleAddStyle + "\">" + strLabel+ "\n");
        inOrder.verify(out, times(1)).print("      <span class=\"common-flexMenu-search-header-icon\"><img alt=\"\" src=\"./images/gnomes/icons/icon-arrow-7.png\"></span>\n");
        inOrder.verify(out, times(1)).print("    </span>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");

        if (isPanecon) {
            inOrder.verify(out, times(1)).print("  <div id=\"" + searchTableTagId
                + "\" data-tableid=\"" + searchTableId
                + "\" data-indexs='" + dataIndexs
                + "' class=\"common-flexMenu-search-box common-flexMenu-size " + menuBoxAddStyle + " common-data-area-wrapper-t\">\n");
            inOrder.verify(out, times(1)).print("    <div class=\"common-data-area-t\">\n");

            verufyOutSearchMenuItem(isPanecon, searchTableTagId);

            inOrder.verify(out, times(1)).print("    </div>\n");
            inOrder.verify(out, times(1)).print("  </div>\n");
        } else {
            inOrder.verify(out, times(1)).print("  <div id=\"" + searchTableTagId
                + "\" data-tableid=\"" + searchTableId
                + "\" data-indexs='" + dataIndexs
                + "' class=\"common-flexMenu-search-box common-flexMenu-size " + menuBoxAddStyle + "\">\n");

            verufyOutSearchMenuItem(isPanecon, searchTableTagId);

            inOrder.verify(out, times(1)).print("  </div>\n");
        }

        if (!StringUtil.isNullOrEmpty((String) searchMenuInfo.get(INFO_IS_SEARCH_DETAIL_LINK))) {
            inOrder.verify(out, times(1))
                .print("    <a href=\"#\" class=\"common-link search-detail-link\" onclick=\"$('#" + searchDetailLink
                    + "_Search').on('shown.bs.modal').modal(); return false;\" tabindex=\"" + tabindex + "\">"
                    + getLabel(GnomesResourcesConstants.DI91_0220) + "</a>\n");
        }
    }

    private void verufyOutSearchMenuItem(boolean isPanecon, String searchTableTagId) throws IOException {
        ConditionInfo conditionInfo = searchInfo.getConditionInfos().get(0);

        String textDialogClass = "";
        String pulldownDialogOnclick = "";

        if (isPanecon) {
            textDialogClass = "common-keyboard-input-char";
            pulldownDialogOnclick = "onclick=\"MakePullDownTableModal($(this), '" + getLabel(GnomesResourcesConstants.YY01_0085) + "');\"";
        }

        String columnId = conditionInfo.getColumnId();
        MstCondition mstCondition = searchMasterInfo.getMstCondition(columnId);
        String columnName = searchTableTagId + "_" + columnId;
        String text = mstCondition.getText();
        LinkedHashMap<String, String> patterns = mstCondition.getPatterns();
        List<String> parametars = conditionInfo.getParameters();

        inOrder.verify(out, times(1)).print("    <ul>\n");
        inOrder.verify(out, times(1)).print("<li>");
        inOrder.verify(out, times(1)).print(text + "\n");
        inOrder.verify(out, times(1)).print("  <input type=\"text\" name=\""+ columnName + "_value\" class=\"" + textDialogClass + "\" value=\"" + parametars.get(0) + "\">\n");
        inOrder.verify(out, times(1)).print("      <select name=\""+ columnName + "_condition\" " + pulldownDialogOnclick + ">\n");
        inOrder.verify(out, times(1)).print("              <option value=\"" + "0" + "\" selected=\"selected\">"+ patterns.get("0") + "</option>\n");
        inOrder.verify(out, times(1)).print("      </select>\n");
        inOrder.verify(out, times(1)).print("</li>\n");
        inOrder.verify(out, times(1)).print("    </ul>\n");
    }

    /**
     * 検索メニュー辞書情報取得処理のモック化
     * @param searchTableId
     * @param searchDetailLink
     * @param labelRourceId
     * @param titleAddStyle
     * @param menuBoxAddStyle
     * @param isPanecon
     * @param isSearchDetailLink
     * @throws GnomesAppException
     */
    private void setupMockSearchMenuInfo(String searchTableId,
        String searchDetailLink,
        String labelRourceId,
        String titleAddStyle,
        String menuBoxAddStyle,
        String isPanecon,
        String isSearchDetailLink) throws GnomesAppException {
        searchMenuInfo = new HashMap<String, Object>();

        searchMenuInfo.put(INFO_SEARCH_TABLE_ID, searchTableId);
        searchMenuInfo.put(INFO_SEARCH_DETAIL_LINK, searchDetailLink);
        searchMenuInfo.put(INFO_SEARCH_MENU_TITLE_RESOURCE_ID, labelRourceId);
        searchMenuInfo.put(INFO_TITLE_ADD_STYLE, titleAddStyle);
        searchMenuInfo.put(INFO_MENU_BOX_ADD_CLASS, menuBoxAddStyle);
        searchMenuInfo.put(INFO_IS_PANECON, isPanecon);
        searchMenuInfo.put(INFO_IS_SEARCH_DETAIL_LINK, isSearchDetailLink);

        doReturn(searchMenuInfo).when(cTagDictionary).getSearchMenuInfo(anyString());

        if (!StringUtil.isNullOrEmpty(searchTableId)) {
            initMockGetSearchSetting();
        }
    }

    private void setupMockContext(boolean isLogicError) throws Exception {
        doReturn(isLogicError).when(responseContext).isLogicError();
        if (isLogicError) {
            doReturn(searchInfo).when(requestContext).getRequestSearchSetting(anyString());
        }
    }

    private void setupMockJudgeDisplayFromConnectionArea(boolean bool) {
        doReturn(bool).when(cTagSearchMenu).judgeDisplayFromConnectionArea(any());
        disabled = bool;
    }

    private void initMockGetSearchSetting() {
        Map<String, SearchSetting> searchSettingMap = new HashMap<>();
        searchInfo = new SearchSetting();

        List<ConditionInfo> conditionInfos = new ArrayList<>();
        ConditionInfo conditionInfo = new ConditionInfo();
        conditionInfo.setHiddenItem(false);
        conditionInfo.setColumnId("test_column");
        conditionInfo.setPatternKeys(new ArrayList<String>(Arrays.asList("0")));

        conditionInfos.add(conditionInfo);
        searchInfo.setConditionInfos(conditionInfos);

        searchSettingMap.put((String) searchMenuInfo.get(INFO_SEARCH_TABLE_ID), searchInfo);
        bean.setSearchSettingMap(searchSettingMap);

        initMockGetSearchMenuInfo();
    }

    private void initMockGetSearchMenuInfo() {
        Map<String, MstSearchInfo> mstSearchInfoMap = new HashMap<>();
        searchMasterInfo = new MstSearchInfo();

        List<MstCondition> mstConditions = new ArrayList<>();
        MstCondition mstCondition = new MstCondition();
        LinkedHashMap<String, String> patterns = new LinkedHashMap<>();
        patterns.put("pattern_01", "include");
        mstCondition.setType(ConditionType.STRING_LIKE);
        mstCondition.setColumnName(searchInfo.getConditionInfos().get(0).getColumnId());
        mstCondition.setText("Column");
        mstCondition.setPatterns(patterns);
        mstCondition.setSaveParamTypes(new ArrayList<ConditionParamSaveType>(Arrays.asList(ConditionParamSaveType.SAVE)));

        mstConditions.add(mstCondition);
        searchMasterInfo.setMstConditions(mstConditions);

        mstSearchInfoMap.put((String) searchMenuInfo.get(INFO_SEARCH_TABLE_ID), searchMasterInfo);
        bean.setMstSearchInfoMap(mstSearchInfoMap);
    }

    private String getLabel(String resourceId) {
        String strLabel = "";

        switch (resourceId) {
            case GnomesResourcesConstants.DI90_0029:
                strLabel = "検索";
                break;
            case GnomesResourcesConstants.YY01_0085:
                strLabel = "曖昧検索";
                break;
            case GnomesResourcesConstants.DI91_0220:
                strLabel = "詳細検索";
                break;
            default:
        }
        return strLabel;
    }

    private String getDataIndex() {
        ConditionInfo conditionInfo = searchInfo.getConditionInfos().get(0);
        String dataType = String.format("\"%d.%s\":\"%d\"", 0, conditionInfo.getColumnId(),
            searchMasterInfo.getMstCondition(conditionInfo.getColumnId()).getType().getValue());
        return String.format("{ %s }", dataType);
    }

}
