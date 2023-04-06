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
import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.view.TestFormBean;

class GnomesCTagMenuTest {

    /** 辞書：メニュータイトルリソースID */
    private static final String INFO_MENU_TITLE_RESOURCE_ID = "menu_title_resource_id";
    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_TITLE_ADD_STYLE = "title_add_style";
    /** 辞書：メニューボックススタイルクラス追加 */
    private static final String INFO_MENU_BOX_ADD_CLASS = "menu_box_add_style";
    /** 辞書：メニュー項目定義Bean */
    private static final String INFO_MENU_ITEM_LIST_BEAN = "menu_item_list_bean";

    private static final String LABEL_RESOUCE_ID = "test_resource_id";
    private static final String LABEL = "テスト";
    private static final String ON_CLICK = "testClickFunc();";

    @Spy
    @InjectMocks
    GnomesCTagMenu cTagMenu;

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
    private Map<String, Object> menuInfo;
    private Map<String, Object> menuItemInfoDetail;
    private List<MenuItemInfoBean> menuItemInfoBeanList;
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
        TestFormBean bean = new TestFormBean();
        bean.setUserLocale(Locale.getDefault());
        cTagMenu.setBean(bean);
        cTagMenu.setDictId("test_dict_id");

        doReturn(Locale.getDefault()).when(gnomesSessionBean).getUserLocale();
        doReturn(out).when(pageContext).getOut();
        doReturn(cTagDictionary).when(cTagMenu).getCTagDictionary();
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString(), any())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
    }

    @Test
    @DisplayName("メニュータグ出力：BeanがNullだった場合GnomesAppExceptionが発生することの確認")
    void test_doStartTag_GnomesAppException_bean_null() throws JspException {
        cTagMenu.setBean(null);

        JspException e = assertThrows(JspException.class, () -> cTagMenu.doStartTag());
        assertTrue(e.getCause() instanceof GnomesAppException);
        assertTrue(e.getMessage().endsWith(GnomesCTagBase.NO_BEAN_ERR_MES));
    }

    @Test
    @DisplayName("メニュータグ出力：メニュータイトルリソースIDが設定されている場合メニュータイトルが出力されることの確認")
    void test_doStartTag_out_label() throws Exception {
        setupMockGetMenuInfo(LABEL_RESOUCE_ID, "", "", "");
        setupMockGetMenuItemInfo(true, null, null, null, null, null, null, null);

        assertEquals(0, cTagMenu.doStartTag());

        // ラベルを出力
        verifyOutHeader();
        inOrder.verify(out, times(1)).print("    <ul>\n");
        inOrder.verify(out, times(1)).print("    </ul>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");
    }

    @Test
    @DisplayName("メニュータグ出力：タイトルスタイルシート追加クラスが設定されている場合メニュータイトル部分に追加クラスが出力されることの確認")
    void test_doStartTag_out_addStyle() throws Exception {
        setupMockGetMenuInfo(LABEL_RESOUCE_ID, "", "test_label_class", "");
        setupMockGetMenuItemInfo(true, null, null, null, null, null, null, null);

        assertEquals(0, cTagMenu.doStartTag());

        // 追加クラスを出力
        verifyOutHeader();
        inOrder.verify(out, times(1)).print("    <ul>\n");
        inOrder.verify(out, times(1)).print("    </ul>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");
    }

    @Test
    @DisplayName("メニュータグ出力：メニューボックススタイルシート追加クラスが設定されている場合メニューボックス部分に追加クラスが出力されることの確認")
    void test_doStartTag_out_menuBoxAddStyle() throws Exception {
        setupMockGetMenuInfo(LABEL_RESOUCE_ID, "", "test_label_class", "test_menu_box_class");
        setupMockGetMenuItemInfo(true, null, null, null, null, null, null, null);

        assertEquals(0, cTagMenu.doStartTag());

        // 追加クラスを出力
        verifyOutHeader();
        inOrder.verify(out, times(1)).print("    <ul>\n");
        inOrder.verify(out, times(1)).print("    </ul>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");
    }

    @Test
    @DisplayName("メニュータグ出力：メニュー項目辞書情報を取得した場合メニュー項目が出力されることの確認")
    void test_doStartTag_out_menuItem() throws Exception {
        setupMockGetMenuInfo(LABEL_RESOUCE_ID, "", "test_label_class", "test_menu_box_class");
        setupMockGetMenuItemInfo(false, "test_item", LABEL_RESOUCE_ID, "", ON_CLICK, "test_item_add_class", "",
            "test_separate");

        assertEquals(0, cTagMenu.doStartTag());

        verifyOutHeader();
        inOrder.verify(out, times(1)).print("    <ul>\n");

        // メニュー項目表示出力
        verifyOutMenuItem();

        inOrder.verify(out, times(1)).print("    </ul>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");
    }

    @Test
    @DisplayName("メニュータグ出力：メニュー項目定義Beanを取得した場合メニュー項目出力内容が出力されることの確認")
    void test_doStartTag_out_outMenuItemFormBean() throws Exception {
        setupMockGetMenuInfo(LABEL_RESOUCE_ID, "test_item_menu_bean", "test_label_class", "test_menu_box_class");
        setupMockGetMenuItemInfo(false, "test_item", LABEL_RESOUCE_ID, "", ON_CLICK, "test_item_add_class", "",
            "test_separate");

        assertEquals(0, cTagMenu.doStartTag());

        verifyOutHeader();
        inOrder.verify(out, times(1)).print("    <ul>\n");
        verifyOutMenuItem();

        // メニュー項目出力内容出力
        verifyOutMenuItemFormBean();

        inOrder.verify(out, times(1)).print("    </ul>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");
    }

    private void verifyOutHeader() throws IOException {
        String strLabel = "";
        if ((String)menuInfo.get(INFO_MENU_TITLE_RESOURCE_ID) != null) {
            strLabel = LABEL;
        }

        inOrder.verify(out, times(1)).print("  <div class=\"common-flexMenu-function-header clearfix\">\n");
        inOrder.verify(out, times(1)).print("    <span class=\"common-flexMenu-function-header-title "
            + (String) menuInfo.get(INFO_TITLE_ADD_STYLE) + "\">" + strLabel + "\n");
        inOrder.verify(out, times(1)).print(
            "      <span class=\"common-flexMenu-function-header-icon\"><img alt=\"\" src=\"./images/gnomes/icons/icon-arrow-7.png\"></span>\n");
        inOrder.verify(out, times(1)).print("    </span>\n");
        inOrder.verify(out, times(1)).print("  </div>\n");
        inOrder.verify(out, times(1)).print("  <div class=\"common-flexMenu-function-box common-flexMenu-size "
            + (String) menuInfo.get(INFO_MENU_BOX_ADD_CLASS) + "\">\n");
    }

    private void verifyOutMenuItem() throws IOException {
        String itemSeparateClassName = "";
        String disabledClass = "";
        String strLabel = "";
        if ((String) menuItemInfoDetail.get(GnomesCTagBaseMenu.INFO_ITEM_RESOURCE_ID) != null) {
            strLabel = LABEL;
        }
        if (!StringUtil.isNullOrEmpty((String) menuItemInfoDetail.get(GnomesCTagBaseMenu.INFO_SEPARATE))) {
            itemSeparateClassName = " common-menu-separation";
        }

        inOrder.verify(out, times(1)).print("      <li class=\"" + itemSeparateClassName + disabledClass + "\"" + ""
            + "><div name=\"" + (String) menuItemInfoDetail.get(GnomesCTagBaseMenu.INFO_ITEM_NAME)
            + "\" class=\"common-menu-item " + (String) menuItemInfoDetail.get(GnomesCTagBaseMenu.INFO_ITEM_ADD_STYLE)
            + "\"><span onclick=\"" + menuItemInfoDetail.get(GnomesCTagBaseMenu.INFO_ITEM_ONCLICK) + "\">" + strLabel
            + "</span></div></li>\n");
    }

    private void verifyOutMenuItemFormBean() throws IOException {
        MenuItemInfoBean bean = menuItemInfoBeanList.get(0);
        inOrder.verify(out, times(1))
            .print("      <li class=\"common-menu-separation " + "" + "\"><div name=\"" + bean.getItemName()
                + "\" class=\"common-menu-item " + bean.getAddStyle() + "\"><span onclick=\"" + bean.getItemOnclick()
                + "\">" + LABEL
                + "</span></div></li>\n");
    }

    /**
     * メニュー（右ペイン）辞書情報取得処理のモック化
     * @param labelRourceId メニュータイトルリソースID
     * @param menuItemListBeanName メニュー項目定義Bean
     * @param addStyle スタイルシート追加クラス
     * @param menuBoxAddStyle メニューボックススタイルクラス追加
     * @throws Exception
     */
    private void setupMockGetMenuInfo(String labelRourceId,
        String menuItemListBeanName,
        String addStyle,
        String menuBoxAddStyle) throws Exception {
        menuInfo = new HashMap<String, Object>();

        if (labelRourceId != null) {
            menuInfo.put(INFO_MENU_TITLE_RESOURCE_ID, labelRourceId);
        }

        menuInfo.put(INFO_MENU_ITEM_LIST_BEAN, menuItemListBeanName);
        menuInfo.put(INFO_TITLE_ADD_STYLE, addStyle);
        menuInfo.put(INFO_MENU_BOX_ADD_CLASS, menuBoxAddStyle);

        setupMockGetData(menuItemListBeanName);
        doReturn(menuInfo).when(cTagDictionary).getMenuInfo(anyString());
    }

    /**
     * メニュー項目定義情報取得処理のモック化
     * @param isNull nullか否か
     * @param itemName メニュー項目名
     * @param itemResourceId メニュー項目リソースID
     * @param commandId メニュー項目コマンドID
     * @param onclick メニュー項目Onclick
     * @param addStyle メニュー項目スタイルシート追加
     * @param buttonId メニュー項目ボタンID
     * @param itemSeparate メニュー項目間区切り
     * @throws GnomesAppException
     */
    private void setupMockGetMenuItemInfo(boolean isNull,
        String itemName,
        String itemResourceId,
        String commandId,
        String onclick,
        String addStyle,
        String buttonId,
        String itemSeparate) throws GnomesAppException {
        Map<String, Object> menuItemInfo = null;
        menuItemInfoDetail = new HashMap<String, Object>();

        if (!isNull) {
            menuItemInfo = new HashMap<String, Object>();
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_NAME, itemName);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_RESOURCE_ID, itemResourceId);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_COMMAND_ID, commandId);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_ONCLICK, onclick);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_ADD_STYLE, addStyle);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_ITEM_BUTTON_ID, buttonId);
            menuItemInfoDetail.put(GnomesCTagBaseMenu.INFO_SEPARATE, itemSeparate);
            menuItemInfo.put("1", menuItemInfoDetail);
        }
        doReturn(menuItemInfo).when(cTagDictionary).getMenuItemInfo(anyString());
    }

    private void setupMockGetData(String menuItemListBeanName) throws Exception {
        menuItemInfoBeanList = null;
        if (!StringUtil.isNullOrEmpty(menuItemListBeanName)) {
            menuItemInfoBeanList = new ArrayList<MenuItemInfoBean>();
            MenuItemInfoBean bean = new MenuItemInfoBean();
            bean.setItemName("test_item_1");
            bean.setItemResourceId(LABEL_RESOUCE_ID);
            bean.setItemOnclick(ON_CLICK);
            bean.setAddStyle("test_item_add_class_1");
            bean.setIsSeparate(true);
            menuItemInfoBeanList.add(bean);
        }
        doReturn(menuItemInfoBeanList).when(cTagMenu).getData(any(), any(), any());
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
