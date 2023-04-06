package com.gnomes.common.tags;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.constants.CommonEnums.TagHiddenKind;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.view.TestFormBean;

@SuppressWarnings("unchecked")
class GnomesCTagMenuOutMenuItemTest {

    private static final String DICT_ID = "test_dict_id";
    private static final String ON_CLICK = "testClickFunc();";
    private static final String BUTTON_ID = "test_button";
    private static final String ITEM_NAME = "test_item_0";

    @Spy
    @InjectMocks
    GnomesCTagMenu cTagMenu;

    @Mock
    JspWriter out;

    private TestFormBean bean;
    private InOrder inOrder;
    private Map<String, Object> menuItemInfo;
    private  List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo;
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
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("メニュー項目表示出力：メニュー項目リソースIDが設定されている場合メニュー項目にラベルが出力されることの確認")
    void test_outMenuItem_out_strLabel() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "", "", "", "", "");
        setupMockJudgeDisplayFromConnectionArea(true);
        cTagMenu.outMenuItem(out, menuItemInfo, null, bean.getUserLocale(), null, DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), null);
    }

    @Test
    @DisplayName("メニュー項目表示出力：メニュー項目コマンドIDが設定されている場合出力されるonclickイベントがコマンド実行フォーマットによって置換されることの確認")
    void test_outMenuItem_out_fotmat_onclick() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "test_item_command", "", "", "", "");
        setupMockJudgeDisplayFromConnectionArea(true);
        cTagMenu.outMenuItem(out, menuItemInfo, null, bean.getUserLocale(), null, DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), null);
    }

    @Test
    @DisplayName("メニュー項目表示出力：メニュー項目Onclickが設定されている場合onclickイベントが出力されることの確認")
    void test_outMenuItem_out_onclick() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "", ON_CLICK, "", "", "");
        setupMockJudgeDisplayFromConnectionArea(true);
        cTagMenu.outMenuItem(out, menuItemInfo, null, bean.getUserLocale(), null, DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), null);
    }

    @Test
    @DisplayName("メニュー項目表示出力：接続領域ごとの操作可否で領域が違うと判定された場合出力したメニュー項目に非表示クラスが追加されることの確認")
    void test_outMenuItem_out_disabledClass() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "test_item_command", ON_CLICK, "", "", "");
        setupMockJudgeDisplayFromConnectionArea(false);
        cTagMenu.outMenuItem(out, menuItemInfo, null, bean.getUserLocale(), null, DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), null);
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 1, 0 })
    @DisplayName("メニュー項目表示出力：ボタンIDが設定されているかつ権限無しかつボタン表示区分が非活性または非表示である場合divタグのclass属性に追加クラスが出力されることの確認")
    void test_outMenuItem_out_nonePrivilegeClassName(int displayDiv) throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "", ON_CLICK, "", BUTTON_ID, "");
        setupMockJudgeDisplayFromConnectionArea(true);
        cTagMenu.outMenuItem(out, menuItemInfo, initPartsPrivilegeResultInfo(displayDiv), bean.getUserLocale(), null,
            DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), stmPartsPrivilegeResultInfo.get(0));
    }

    @Test
    @DisplayName("メニュー項目表示出力：項目スタイルシート追加クラスが設定されている場合メニュー項目に追加クラスが出力されることの確認")
    void test_outMenuItem_out_addStyle() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "", ON_CLICK, "test_add_class", BUTTON_ID, "");
        setupMockJudgeDisplayFromConnectionArea(true);
        cTagMenu.outMenuItem(out, menuItemInfo, initPartsPrivilegeResultInfo(0), bean.getUserLocale(), null,
            DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), stmPartsPrivilegeResultInfo.get(0));
    }

    @Test
    @DisplayName("メニュー項目表示出力：パラメータのBeseFormBeanがnullでない場合カスタムタグの非表示区分から取得したStyleが出力されることの確認")
    void test_outMenuItem_out_strStyle() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "", ON_CLICK, "test_add_class", BUTTON_ID, "");
        setupMockJudgeDisplayFromConnectionArea(true);
        addTagHiddenKindMap();
        cTagMenu.outMenuItem(out, menuItemInfo, initPartsPrivilegeResultInfo(0), bean.getUserLocale(), bean,
            DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), stmPartsPrivilegeResultInfo.get(0));
    }

    @Test
    @DisplayName("メニュー項目表示出力：メニュー項目間区切りが設定されている場合区切Class名が出力されることの確認")
    void test_outMenuItem_out_itemSeparateClassName() throws Exception {
        initMenuItemInfo(false, ITEM_NAME, GnomesResourcesConstants.DI02_0068, "", ON_CLICK, "test_add_class", BUTTON_ID, "test_separate");
        setupMockJudgeDisplayFromConnectionArea(true);
        addTagHiddenKindMap();
        cTagMenu.outMenuItem(out, menuItemInfo, initPartsPrivilegeResultInfo(0), bean.getUserLocale(), bean,
            DICT_ID);

        verifyOut((Map<String, Object>) menuItemInfo.get("1"), stmPartsPrivilegeResultInfo.get(0));
    }

    private void verifyOut(Map<String, Object> menuItemInfoDetail, PartsPrivilegeResultInfo partsPrivilegeResultInfo) throws IOException {
        String itemName = (String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_ITEM_NAME);
        String strLabel = getLabel((String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_ITEM_RESOURCE_ID));
        String onclick = (String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_ITEM_ONCLICK);
        String addStyle = (String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_ITEM_ADD_STYLE);
        String disabledClass = !disabled ? " gnomes_display_none " : "";
        String strStyle = bean.getTagHiddenKindMap() != null ? " style=\"" +  "visibility:hidden;" + "\"" : "";
        String itemSeparateClassName = "";
        String nonePrivilegeClassName = "";

        if (!StringUtil.isNullOrEmpty((String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_ITEM_COMMAND_ID))) {
            onclick = "commandSubmit('" + (String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_ITEM_COMMAND_ID) + "');";
        }

        if (partsPrivilegeResultInfo != null) {
            DisplayDivision div = partsPrivilegeResultInfo.getDisplayDiv();
            if (div == DisplayDivision.DISABLE) {
                nonePrivilegeClassName = " common-base-menu-disabled";
            } else if (div == DisplayDivision.HIDE) {
                nonePrivilegeClassName = " common-base-menu-hide";
            }
        }

        if (!StringUtil.isNullOrEmpty((String) menuItemInfoDetail.get(GnomesCTagMenu.INFO_SEPARATE))) {
            itemSeparateClassName = " common-menu-separation";
        }

        if (!StringUtil.isNullOrEmpty(nonePrivilegeClassName)) {
            inOrder.verify(out, times(1)).print(
                "      <li class=\"" + itemSeparateClassName + disabledClass + "\"" + strStyle + "><div name=\""
                    + itemName + "\" class=\"" + addStyle + nonePrivilegeClassName + "\"><span>" + strLabel
                    + "</span></div></li>\n");
        } else {
            inOrder.verify(out, times(1)).print(
                "      <li class=\"" + itemSeparateClassName + disabledClass + "\"" + strStyle + "><div name=\""
                    + itemName
                    + "\" class=\"common-menu-item " + addStyle + "\"><span onclick=\"" + onclick + "\">" + strLabel
                    + "</span></div></li>\n");
        }
    }


    /**
     * メニュー項目辞書情報初期化
     * @param isNull Nullか否か
     * @param itemName メニュー項目名
     * @param itemResourceId メニュー項目リソースID
     * @param commandId メニュー項目コマンドID
     * @param onclick メニュー項目Onclick
     * @param addStyle メニュー項目スタイルシート追加
     * @param buttonId メニュー項目ボタンID
     * @param itemSeparate メニュー項目間区切り
     */
    private void initMenuItemInfo(boolean isNull,
        String itemName,
        String itemResourceId,
        String commandId,
        String onclick,
        String addStyle,
        String buttonId,
        String itemSeparate) {
        menuItemInfo = new HashMap<String, Object>();
        Map<String, Object> menuItemInfoDetail = null;

        if (!isNull) {
            menuItemInfoDetail = new HashMap<String, Object>();
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_NAME, itemName);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_RESOURCE_ID, itemResourceId);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_COMMAND_ID, commandId);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_ONCLICK, onclick);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_ADD_STYLE, addStyle);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_BUTTON_ID, buttonId);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_SEPARATE, itemSeparate);
        }
        menuItemInfo.put("1", menuItemInfoDetail);
    }

    private List<PartsPrivilegeResultInfo> initPartsPrivilegeResultInfo(int displayDiv) {
        stmPartsPrivilegeResultInfo = new ArrayList<>();
        PartsPrivilegeResultInfo info = new PartsPrivilegeResultInfo();
        info.setButtonId(BUTTON_ID);
        info.setPrivilege(false);
        info.setDisplayDiv(displayDiv);
        stmPartsPrivilegeResultInfo.add(info);
        return stmPartsPrivilegeResultInfo;
    }

    private void addTagHiddenKindMap() {
        Map<String,TagHiddenKind> mapHiddenNone = new HashMap<>();
        mapHiddenNone.put(DICT_ID + "." + ITEM_NAME, TagHiddenKind.HIDDEN);
        bean.setTagHiddenKindMap(mapHiddenNone);
    }

    private void setupMockJudgeDisplayFromConnectionArea(boolean bool) {
        doReturn(bool).when(cTagMenu).judgeDisplayFromConnectionArea(any());
        disabled = bool;
    }

    private String getLabel(String resourceId) {
        String strLabel = "";

        switch (resourceId) {
            case GnomesResourcesConstants.DI02_0068:
                strLabel = "登録";
                break;
            default:
        }
        return strLabel;
    }

}
