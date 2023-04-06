package com.gnomes.common.tags;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayConfirmFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeIsnecessaryPassword;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.view.TestFormBean;

class GnomesCTagMenuOutMenuItemFormBeanTest {

    private static final String ON_CLICK = "testClickFunc();";
    private static final String BUTTON_ID = "test_button";
    private static final String USER_ID = "test_user_01";

    @Spy
    @InjectMocks
    GnomesCTagMenu cTagMenu;

    @Mock
    JspWriter out;
    @Mock
    GnomesSessionBean gnomesSessionBean;

    private TestFormBean bean;
    private InOrder inOrder;
    private List<MenuItemInfoBean> menuItemInfoBeanList;
    private  List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo;

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

        initPartsPrivilegeResultInfo();
        doReturn(USER_ID).when(gnomesSessionBean).getUserId();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("メニュー項目表示出力(Bean定義)：メニュー項目リソースIDが設定されている場合メニュー項目にラベルが出力されることの確認")
    void test_outMenuItemFormBean_out_strLabel() throws Exception {
        initMenuItemInfoBeanList(GnomesResourcesConstants.DI02_0068, "", "", "", "", false);
        cTagMenu.outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, bean.getUserLocale());

        verifyOut(menuItemInfoBeanList.get(0), null);
    }

    @Test
    @DisplayName("メニュー項目表示出力(Bean定義)：メニュー項目コマンドIDが設定されている場合出力されるonclickイベントがコマンド実行フォーマットによって置換されることの確認")
    void test_outMenuItemFormBean_out_fotmat_onclick() throws Exception {
        initMenuItemInfoBeanList(GnomesResourcesConstants.DI02_0068, "test_item_command", "", "", "", false);
        cTagMenu.outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, bean.getUserLocale());

        verifyOut(menuItemInfoBeanList.get(0), null);
    }

    @Test
    @DisplayName("メニュー項目表示出力(Bean定義)：メニュー項目Onclickが設定されている場合onclickイベントが出力されることの確認")
    void test_outMenuItemFormBean_out_onclick() throws Exception {
        initMenuItemInfoBeanList(GnomesResourcesConstants.DI02_0068, "", ON_CLICK, "", "", false);
        cTagMenu.outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, bean.getUserLocale());

        verifyOut(menuItemInfoBeanList.get(0), null);
    }

    @Test
    @DisplayName("メニュー項目表示出力(Bean定義)：メニュー項目コマンドIDとメニュー項目Onclickが未設定かつメニュー項目ボタンIDが設定されている場合パーツ権限結果情報を元に生成したonclickイベントが出力されることの確認")
    void test_outMenuItemFormBean_out_onclick_buttonAttribute() throws Exception {
        initMenuItemInfoBeanList(GnomesResourcesConstants.DI02_0068, "", "", BUTTON_ID, "", false);
        cTagMenu.outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, bean.getUserLocale());

        verifyOut(menuItemInfoBeanList.get(0), stmPartsPrivilegeResultInfo.get(0));
    }

    @Test
    @DisplayName("メニュー項目表示出力(Bean定義)：スタイルシート追加クラスが設定されている場合メニュー項目に追加クラスが出力されることの確認")
    void test_outMenuItemFormBean_out_addStyle() throws Exception {
        initMenuItemInfoBeanList(GnomesResourcesConstants.DI02_0068, "", "", BUTTON_ID, "test_add_class", false);
        cTagMenu.outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, bean.getUserLocale());

        verifyOut(menuItemInfoBeanList.get(0), stmPartsPrivilegeResultInfo.get(0));
    }

    @Test
    @DisplayName("メニュー項目表示出力(Bean定義)：メニュー項目間区切りが設定されている場合区切Class名が出力されることの確認")
    void test_outMenuItemFormBean_isSeparate_true() throws Exception {
        initMenuItemInfoBeanList(GnomesResourcesConstants.DI02_0068, "", "", BUTTON_ID, "test_add_class", true);
        cTagMenu.outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, bean.getUserLocale());

        verifyOut(menuItemInfoBeanList.get(0), stmPartsPrivilegeResultInfo.get(0));
    }

    private void verifyOut(MenuItemInfoBean menuItemInfoBean, PartsPrivilegeResultInfo partsPrivilegeResultInfo) throws IOException {
        String itemName = menuItemInfoBean.getItemName();
        String strLabel = getLabel(menuItemInfoBean.getItemResourceId());
        String onclick = menuItemInfoBean.getItemOnclick();
        String addStyle = menuItemInfoBean.getAddStyle();
        String disabledClass = "";

        if (!StringUtil.isNullOrEmpty(menuItemInfoBean.getItemCommandId())) {
            onclick = "commandSubmit('" + menuItemInfoBean.getItemCommandId() + "');";
        }

        if (!StringUtil.isNullOrEmpty(menuItemInfoBean.getItemButtonId()) && StringUtil.isNullOrEmpty(onclick)) {
            onclick = "GnomesConfirmBTN('"+ strLabel + "','"
                + partsPrivilegeResultInfo.getConfirmMessage().replace("\n", "<br>") + "','"
                + strLabel + "',"
                + partsPrivilegeResultInfo.getIsNecessaryPassword().getValue() + ",'"
                + "" + "','"
                + "','"
                + USER_ID + "','"
                + partsPrivilegeResultInfo.getButtonId() + "','"
                + "true');";
        }

        if (menuItemInfoBean.getIsSeparate()) {
            inOrder.verify(out, times(1))
                .print("      <li class=\"common-menu-separation " + disabledClass + "\"><div name=\"" + itemName
                    + "\" class=\"common-menu-item " + addStyle + "\"><span onclick=\"" + onclick + "\">" + strLabel
                    + "</span></div></li>\n");
        } else {
            inOrder.verify(out, times(1)).print(
                "      <li class=\"" + disabledClass + "\"><div name=\"" + itemName + "\" class=\"common-menu-item "
                    + addStyle + "\"><span onclick=\"" + onclick + "\">" + strLabel + "</span></div></li>\n");
        }
    }

    /**
     * メニュー項目Beanリスト初期化
     * @param resourceId メニュー項目リソースID
     * @param commandId メニュー項目コマンドID
     * @param onclick メニュー項目onclick
     * @param buttonId メニュー項目ボタンID
     * @param addStyle スタイルシートの追加設定
     * @param isSeparate メニュー項目間区切りの有無
     */
    private void initMenuItemInfoBeanList(String resourceId,
        String commandId,
        String onclick,
        String buttonId,
        String addStyle,
        boolean isSeparate) {
        menuItemInfoBeanList = new ArrayList<MenuItemInfoBean>();

        MenuItemInfoBean menuItemInfoBean = new MenuItemInfoBean();
        menuItemInfoBean.setItemName("test_item_0");
        menuItemInfoBean.setItemResourceId(resourceId);
        menuItemInfoBean.setItemCommandId(commandId);
        menuItemInfoBean.setItemOnclick(onclick);
        menuItemInfoBean.setItemButtonId(buttonId);
        menuItemInfoBean.setAddStyle(addStyle);
        menuItemInfoBean.setIsSeparate(isSeparate);

        menuItemInfoBeanList.add(menuItemInfoBean);
    }

    private List<PartsPrivilegeResultInfo> initPartsPrivilegeResultInfo() {
        stmPartsPrivilegeResultInfo = new ArrayList<>();
        PartsPrivilegeResultInfo info = new PartsPrivilegeResultInfo();
        info.setButtonId(BUTTON_ID);
        info.setPrivilege(true);
        info.setDisplayConfirmFlag(PrivilegeDisplayConfirmFlag.PrivilegeDisplayConfirmFlag_ON);
        info.setConfirmMessage("Test Confirm");
        info.setIsNecessaryPassword(PrivilegeIsnecessaryPassword.PrivilegeIsnecessaryPassword_SINGLE);
        stmPartsPrivilegeResultInfo.add(info);
        return stmPartsPrivilegeResultInfo;
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
