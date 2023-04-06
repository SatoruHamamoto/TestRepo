package com.gnomes.common.tags;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.BookMarkType;
import com.gnomes.common.constants.CommonEnums.DisplayType;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.StringUtils;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.data.PopupMessageInfo;
import com.gnomes.system.entity.MstrMessageDefine;
import com.gnomes.system.entity.MstrSystemDefine;
/**
 * ヘッダー カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagHeader extends GnomesCTagBaseMenu {

    /** 辞書：表示画面タイプ */
    private static final String INFO_DISPLAY_TYPE = "display_type";

    /** 辞書：メニュー項目定義Bean */
    private static final String INFO_MENU_ITEM_LIST_BEAN = "menu_item_list_bean";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao mstrSystemDefineDao;

    /** メッセージ定義 Dao */
    @Inject
    protected MstrMessageDefineDao mstrMessageDefineDao;

    /**
     * 辞書IDを取得
     * @return dictId
     */
    public String getDictId() {
        return dictId;
    }

    /**
     * 辞書IDを設定
     * @param dictId 辞書ID
     */
    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    /**
     * Bean取得
     * @return bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Bean設定
     * @param bean Bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * ヘッダータグ出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {

            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // ヘッダー辞書取得
            Map<String,Object> headerInfo = dict.getHeaderInfo(this.dictId);

            // 表示画面区分を取得
            String strDisplayType = (String)headerInfo.get(INFO_DISPLAY_TYPE);
            DisplayType displayType = DisplayType.getEnum(strDisplayType);

            switch (displayType) {
                // 管理端末画面
                case CLIENT:

                    // パーツ権限情報Listを取得
                    List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);

                    // メニュー項目定義Bean名を取得
                    String menuItemListBeanName = (String)headerInfo.get(INFO_MENU_ITEM_LIST_BEAN);

                    // メニュー項目辞書取得
                    Map<String,Object> headerMenuItemInfo = dict.getHeaderMenuItemInfo(this.dictId);

                    outClientHeader(out, menuItemListBeanName, headerMenuItemInfo,
                                    stmPartsPrivilegeResultInfo, displayType);

                    break;
                // 工程端末画面
                case PANECON:
                    // パーツ権限情報Listを取得
                    List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfoPanecon = getPartsPrivilegeResultInfoList(this.bean);

                    // メニュー項目定義Bean名を取得
                    String menuItemListBeanNamePanecon = (String)headerInfo.get(INFO_MENU_ITEM_LIST_BEAN);

                    // メニュー項目辞書取得
                    Map<String,Object> headerMenuItemInfoPanecon = dict.getHeaderMenuItemInfo(this.dictId);

                    outPaneconHeader(out, menuItemListBeanNamePanecon, headerMenuItemInfoPanecon,
                                    stmPartsPrivilegeResultInfoPanecon, displayType);
                    break;

                 // 管理メインメニュー
                case MAINMENU:
                    // パーツ権限情報Listを取得
                    List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfoMainmenu = getPartsPrivilegeResultInfoList(this.bean);

                    // メニュー項目定義Bean名を取得
                    String mainMenuItemListBeanName = (String)headerInfo.get(INFO_MENU_ITEM_LIST_BEAN);

                    // メニュー項目辞書取得
                    Map<String,Object> headerMainMenuItemInfo = dict.getHeaderMenuItemInfo(this.dictId);

                    outClientHeader(out, mainMenuItemListBeanName, headerMainMenuItemInfo,
                    		stmPartsPrivilegeResultInfoMainmenu, displayType);

                    break;

                // 工程メインメニュー
                case PANECON_MAINMENU:
                    // パーツ権限情報Listを取得
                    List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfoPaneconMainMenu = getPartsPrivilegeResultInfoList(this.bean);

                    // メニュー項目定義Bean名を取得
                    String mainMenuItemListBeanNamePanecon = (String)headerInfo.get(INFO_MENU_ITEM_LIST_BEAN);

                    // メニュー項目辞書取得
                    Map<String,Object> headerMainMenuItemInfoPanecon = dict.getHeaderMenuItemInfo(this.dictId);

                    outPaneconHeader(out, mainMenuItemListBeanNamePanecon, headerMainMenuItemInfoPanecon,
                    		stmPartsPrivilegeResultInfoPaneconMainMenu, displayType);
                    break;
            }



        } catch(Exception e) {
            if (out != null) {
                try {
                    out.print(getTagErrMes());
                    out.flush();
                } catch (IOException e1) {
                    throw new JspTagException(e1);
                } catch (Exception e2) {
                    throw new JspTagException(e2);
                }
            }

            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    /**
     * ヘッダー出力(管理端末画面)
     * @param out 出力先
     * @param menuItemListBeanName メニュー項目定義Bean名
     * @param headerMenuItemInfo メニュー項目辞書
     * @param stmPartsPrivilegeResultInfo パーツ権限情報List
     * @param displayType 表示画面区分
     * @throws Exception 例外
     */
    private void outClientHeader(JspWriter out,
            String menuItemListBeanName,
            Map<String,Object> headerMenuItemInfo,
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo,
            DisplayType displayType) throws Exception {

        Locale userLocale = gnomesSessionBean.getUserLocale();

        //Beanの指定が間違ってnullになっているのをチェック
        if (this.bean == null) {
            logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
            throw new GnomesAppException(NO_BEAN_ERR_MES);
        }

        // 画面ID取得
        String screenId  = ((IScreenFormBean)this.bean).getScreenId();

        // 画面名取得
        String screenName = this.getStringEscapeHtml(((IScreenFormBean)this.bean).getScreenName());

        // 編集有フラグ値
        String inputChange = this.getStringEscapeHtml(((BaseFormBean)this.bean).getInputChangeFlag());
        if (inputChange == null) {
        	inputChange = "";
        }

        // 参照先領域名取得
        String regionType = "";
        RegionType region = RegionType.getEnum(this.gnomesSessionBean.getRegionType());

        if (RegionType.NORMAL.equals(region)) {
            regionType = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0115, userLocale));
        } else if (RegionType.STORAGE.equals(region)) {
            regionType = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0116, userLocale));
        }

        // ユーザID取得
        String userId = gnomesSessionBean.getUserId();

        // ユーザ名取得
        String userName = this.getStringEscapeHtml(gnomesSessionBean.getUserName());

        // コンピュータ名取得
        String computerName  = gnomesSessionBean.getComputerName();

        // エリアID
        String areaId = gnomesSessionBean.getAreaId();

        // 拠点・エリア名
        String siteAreaName = gnomesSessionBean.getSiteName();
        if (!StringUtil.isNullOrEmpty(gnomesSessionBean.getAreaName())) {
            siteAreaName = siteAreaName + "&nbsp;" + gnomesSessionBean.getAreaName();
        }

        // メッセージ一覧の取得
        List<PopupMessageInfo> messageList = gnomesSessionBean.getMessageList();

        // ホーム画像パス
        String homeImagePath = "./images/gnomes/icons/icon-menu-20.png";

        // ホーム画像onclick
        String homeOnclick = "document.main.command.value='" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0100, userLocale) + "';document.main.submit();";

        // 工場画像パス取得
        String factoryImagePath = "./images/gnomes/icons/icon-menu-21.png";

        // 工場画像コマンドID
        String factoryOnclick = "";

        // ユーザ画像パス
        String userImagePath = "./images/gnomes/icons/icon-user-1.png";

        // ユーザ画像onclick
        String userOnclick = "GnomesConfirmBTN('" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0015, userLocale) + "', '"
                    + MessagesHandler.getString(GnomesMessagesConstants.MC01_0002, userLocale) + "', '"
                    + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0071, userLocale)
                    + "', PrivilegeIsnecessaryPassword_NONE, 'logout();');";

        // ロゴ画像パス取得
        String logoImagePath = "./images/gnomes/logo/StartupClient.jpg";

        // ロゴコマンドID
        String logoOnclick = "lockTimeOut();";

        String prohibitionChars = "";
		String categoryName = "";
		String iconName = "";

        // 禁止文字取得
		MstrSystemDefine mstrSystemDefine  = this.mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.VALIDATOR,
                SystemDefConstants.PROHIBITION_STRING);
		if (!Objects.isNull(mstrSystemDefine) && !StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {
			 prohibitionChars = StringUtils.getStringEscapeHtml(mstrSystemDefine.getChar1());
		}

		// 禁止文字チェックの為、エラーメッセージ設定する
		MstrMessageDefine mstrMsgdef = this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(GnomesMessagesConstants.ME01_0245);
		if (!Objects.isNull(mstrMsgdef)) {
			// 種別(名称)
	        CommonEnums.MessageCategory msgCategory = CommonEnums.MessageCategory.getEnum(mstrMsgdef.getCategory());
	        // リソースよりユーザ言語で取得
	        categoryName = ResourcesHandler.getString(msgCategory.toString(), userLocale);
	        // メッセージ重要度
	        CommonEnums.MessageLevel msgLevel = CommonEnums.MessageLevel.getEnum(mstrMsgdef.getMessage_level());
	        // アイコン名
	        iconName = ResourcesHandler.getString(MessagesHandler.getMessageIconResKey(msgCategory,msgLevel));
		}

		String warningCategoryName = "";
		String warningIconName = "";
		// 集中データがある場合、エラーメッセージ設定する
		MstrMessageDefine mstrMsgdefForWarning= this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(GnomesMessagesConstants.ME01_1001);
		if (!Objects.isNull(mstrMsgdefForWarning)) {
			// 種別(名称)
	        CommonEnums.MessageCategory msgCategory = CommonEnums.MessageCategory.getEnum(mstrMsgdefForWarning.getCategory());
	        // リソースよりユーザ言語で取得
	        warningCategoryName = ResourcesHandler.getString(msgCategory.toString(), userLocale);
	        // メッセージ重要度
	        CommonEnums.MessageLevel msgLevel = CommonEnums.MessageLevel.getEnum(mstrMsgdefForWarning.getMessage_level());
	        // アイコン名
	        warningIconName = ResourcesHandler.getString(MessagesHandler.getMessageIconResKey(msgCategory,msgLevel));
		}

        out.print("<header class=\"common-client-header\">\n");
        out.print("  <div class=\"common-client-header-inner clearfix\">\n");
        out.print("    <div class=\"common-client-header-info\">\n");
        // 参照先領域名、画面名、画面ID
        out.print("      <div class=\"common-header-screenName\">"
                                + regionType + "<br>" + screenName + "</div>\n");
        out.print("      <p class=\"common-hidden\" id=\"screenId\">" + screenId + "</p>\n");
        out.print("      <p class=\"common-hidden\" id=\"screenName\">" + screenName + "</p>\n");

        switch (displayType) {
	        // 管理端末画面
	        case CLIENT:
	            // ホームボタン
	            out.print("      <div class=\"common-header-homeIcon\">\n");
	            out.print("        <a onclick=\"" + homeOnclick + "\"><img alt=\""
	                                    + ResourcesHandler.getString(GnomesResourcesConstants.DI91_0229, userLocale)
	                                    + "\" src=\"" + homeImagePath + "\"></a>\n");
	            out.print("      </div>\n");
	        break;
	        // 管理メインメニュー
	        case MAINMENU:
	        	// ログアウト画像パス
	            String logoutImagePath = "./images/gnomes/icons/icon-menu-33.png";
	            // ログアウトonclick
	            String logoutOnclick = "GnomesConfirmBTN('" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0015) + "', '"
	                    + MessagesHandler.getString(GnomesMessagesConstants.MC01_0002) + "', '"
	                    + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0071)
	                    + "', PrivilegeIsnecessaryPassword_NONE, 'logout();');";

	        	out.print("      <div class=\"common-mainmenu-logoutButton\"><a onclick=\"" + logoutOnclick
	                + "\"><img src=\"" + logoutImagePath + "\" alt=\""
	                + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0071) + "\"></a></div>\n");
	        break;
        }

        // ユーザ情報
        out.print("      <div class=\"common-header-user-unit\">\n");
        out.print("        <div class=\"common-header-userIcon\"><a onclick=\""
                                + logoOnclick + "\"><img src=\"" + userImagePath
                                + "\" alt=\"" + ResourcesHandler.getString(GnomesResourcesConstants.DI91_0230, userLocale)
                                + "\"></a></div>\n");
        out.print("        <div class=\"common-header-user-info\">\n");
        out.print("          <span class=\"common-header-userName\">" + userName + "</span><br>\n");
        out.print("          <span class=\"common-header-deviceName\" id=\"dispComputerName\">"
                                + computerName + "</span>\n");
        out.print("          <p class=\"common-hidden\" id=\"userId\">" + userId + "</p>\n");
        out.print("        </div>\n");
        out.print("      </div>\n");

        // エリア情報
        out.print("      <div class=\"common-header-factory-unit\">\n");
        out.print("        <div class=\"common-header-factoryIcon\"><a class=\"no-click-event\" onclick=\""
                                + factoryOnclick + "\"><img src=\"" + factoryImagePath
                                + "\" alt=\"" + ResourcesHandler.getString(GnomesResourcesConstants.DI91_0231, userLocale)
                                + "\"></a></div>\n");
        out.print("        <div class=\"common-header-factoryName\" id=\"siteAreaName\">"
                                + siteAreaName + "</div>\n");
        out.print("        <p class=\"common-hidden\" id=\"areaId\">" + areaId + "</p>\n");
        out.print("            <input type=\"hidden\" name=\"prohibitString\" value=\"" + prohibitionChars + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgBodyName\" value=\"" + MessagesHandler.getString(GnomesMessagesConstants.ME01_0245, userLocale) + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgCategoryName\" value=\"" + categoryName + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgIconName\" value=\"" + iconName + "\">\n");
        // 警告ダイアログ表示情報
        out.print("            <input type=\"hidden\" name=\"inputChangeFlag\" value=\"" + inputChange + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesWarningBody\" value=\"" + MessagesHandler.getString(GnomesMessagesConstants.ME01_1001, userLocale) + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgWarningCategoryName\" value=\"" + warningCategoryName + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgWarningIconName\" value=\"" + warningIconName + "\">\n");
        out.print("      </div>\n");


        // メッセージ一覧のデータが存在する場合
        if (messageList != null && messageList.size() > 0) {
            // メッセージ一覧
            out.print("      <div class=\"common-header-messagePanel\">\n");

            outMessageList(out, messageList, userLocale, true);

            out.print("      </div>\n");
        }

        // メニュー
        outMenu(out, menuItemListBeanName, headerMenuItemInfo, stmPartsPrivilegeResultInfo, displayType, userLocale);

        out.print("    </div>\n");
        out.print("  </div>\n");

 		if (displayType == CommonEnums.DisplayType.MAINMENU) {

 			 String versionNum = "";
 	        // ソフトウェアのバージョン取得
 	 		MstrSystemDefine systemDefineForSofversion  = this.mstrSystemDefineDao.getMstrSystemDefine(
 	                 SystemDefConstants.SOFTWARE_VERSION,
 	                 SystemDefConstants.VERSION_NUMBER);
 	 		if (!Objects.isNull(systemDefineForSofversion) && !StringUtil.isNullOrEmpty(systemDefineForSofversion.getChar1())) {
 	 			versionNum = StringUtils.getStringEscapeHtml(systemDefineForSofversion.getChar1());
 	 		}

	 		out.print("      <div class=\"version-logo\" style=\"float: right;\">\n");

	 		out.print("      <div class=\"common-mainmenu-version\"><span class=\"version-number\">" + versionNum + "</span>\n");
	 		out.print("      </div>\n");
 		}

        // ロゴ
        out.print("  <div class=\"common-header-productLogo\"><a onclick=\""
                        + logoOnclick + "\"><img src=\"" + logoImagePath + "\" alt=\""
                        + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0129, userLocale) + "\"></a></div></div>\n");

        if (displayType == CommonEnums.DisplayType.MAINMENU) {
        	out.print("  </div>\n");
        }
        out.print("</header>\n");

    }

    /**
     * ヘッダー出力(工程端末画面)
     * @param out 出力先
     * @param menuItemListBeanName メニュー項目辞書
     * @param headerMenuItemInfo メニュー項目定義Bean名
     * @param stmPartsPrivilegeResultInfo パーツ権限情報List
     * @param displayType 表示画面区分
     * @throws Exception 例外
     */
    private void outPaneconHeader(JspWriter out,
            String menuItemListBeanName,
            Map<String, Object> headerMenuItemInfo,
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo,
            DisplayType displayType) throws Exception {

        Locale userLocale = gnomesSessionBean.getUserLocale();

        //Beanの指定が間違ってnullになっているのをチェック
        if (this.bean == null) {
            logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
            throw new GnomesAppException(NO_BEAN_ERR_MES);
        }

        // 画面ID取得
        String screenId  = ((IScreenFormBean)this.bean).getScreenId();

        // 画面名取得
        String screenName = this.getStringEscapeHtml(((IScreenFormBean)this.bean).getScreenName());

        // 編集有フラグ値
        String inputChange = this.getStringEscapeHtml(((BaseFormBean)this.bean).getInputChangeFlag());
        if (inputChange == null) {
        	inputChange = "";
        }

        String strOrderProcess = "";
        // 指図工程コード取得
        String orderProcessCode = this.getStringEscapeHtml(gnomesSessionBean.getOrderProcessCode());

        // 指図工程名取得
        String orderProcessName = this.getStringEscapeHtml(gnomesSessionBean.getOrderProcessName());

        if (!StringUtil.isNullOrEmpty(orderProcessCode)) {
        	strOrderProcess += "<span class=\"common-panecon-work-unit\">" + orderProcessCode + "</span>";
        }
        if (!StringUtil.isNullOrEmpty(orderProcessName)) {
        	strOrderProcess += "<span>" + orderProcessName + "</span>";
        }

        String strWorkProcess = "";
        // 作業工程コード取得
        String workProcessCode = this.getStringEscapeHtml(gnomesSessionBean.getWorkProcessCode());

        // 作業工程名取得
        String workProcessName = this.getStringEscapeHtml(gnomesSessionBean.getWorkProcessName());

        if (!StringUtil.isNullOrEmpty(workProcessCode)) {
        	strWorkProcess += "<span class=\"common-panecon-work-unit\">" + workProcessCode + "</span>";
        }
        if (!StringUtil.isNullOrEmpty(workProcessName)) {
        	strWorkProcess += "<span>" + workProcessName + "</span>";
        }

        String strWorkCell = "";
        // 作業場所コード取得
        String workCellCode = this.getStringEscapeHtml(gnomesSessionBean.getWorkCellCode());

        // 作業場所名取得
        String workCellName = this.getStringEscapeHtml(gnomesSessionBean.getWorkCellName());

        if (!StringUtil.isNullOrEmpty(workCellCode)) {
        	strWorkCell += "<span class=\"common-panecon-work-unit\">" + workCellCode + "</span>";
        }
        if (!StringUtil.isNullOrEmpty(workCellName)) {
        	strWorkCell += "<span>" + workCellName + "</span>";
        }

        // ユーザID取得
        String userId = gnomesSessionBean.getUserId();

        // ユーザ名取得
        String userName = this.getStringEscapeHtml(gnomesSessionBean.getUserName());

        // コンピュータ名取得
        String computerName  = gnomesSessionBean.getComputerName();

        // ホーム画像onclick
        String homeOnclick = "document.main.command.value='" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0101, userLocale) + "';document.main.submit();";

        // ホーム画像パス
        String homeImagePath = "./images/gnomes/icons/icon-menu-20.png";

        // 工場画像パス取得
        String factoryImagePath = "./images/gnomes/icons/icon-menu-21.png";

        // 工場画像コマンドID
        String factoryOnclick = "";

        // ユーザ画像パス
        String userImagePath = "./images/gnomes/icons/icon-user-1.png";

        // ユーザ画像onclick
        String userOnclick = "GnomesConfirmBTN('" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0015, userLocale) + "', '"
                        + MessagesHandler.getString(GnomesMessagesConstants.MC01_0002, userLocale) + "', '"
                        + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0071, userLocale)
                        + "', PrivilegeIsnecessaryPassword_NONE, 'logout();');";

        // ロゴ画像パス取得
        String logoImagePath = "./images/gnomes/logo/StartupClient.jpg";

        // ロゴ画像コマンドID
        String logoOnclick = "lockTimeOut();";

        String prohibitionChars = "";
		String categoryName = "";
		String iconName = "";

        // 禁止文字取得
		MstrSystemDefine mstrSystemDefine  = this.mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.VALIDATOR,
                SystemDefConstants.PROHIBITION_STRING);
		if (!Objects.isNull(mstrSystemDefine) && !StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {
			 prohibitionChars = StringUtils.getStringEscapeHtml(mstrSystemDefine.getChar1());
		}

		// 禁止文字チェックの為、エラーメッセージ設定する
		MstrMessageDefine mstrMsgdef = this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(GnomesMessagesConstants.ME01_0245);
		if (!Objects.isNull(mstrMsgdef)) {
			// 種別(名称)
	        CommonEnums.MessageCategory msgCategory = CommonEnums.MessageCategory.getEnum(mstrMsgdef.getCategory());
	        // リソースよりユーザ言語で取得
	        categoryName = ResourcesHandler.getString(msgCategory.toString(), userLocale);
	        // メッセージ重要度
	        CommonEnums.MessageLevel msgLevel = CommonEnums.MessageLevel.getEnum(mstrMsgdef.getMessage_level());
	        // アイコン名
	        iconName = ResourcesHandler.getString(MessagesHandler.getMessageIconResKey(msgCategory,msgLevel));
		}

		String warningCategoryName = "";
		String warningIconName = "";
		// 集中データがある場合、エラーメッセージ設定する
		MstrMessageDefine mstrMsgdefForWarning= this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(GnomesMessagesConstants.ME01_1001);
		if (!Objects.isNull(mstrMsgdefForWarning)) {
			// 種別(名称)
	        CommonEnums.MessageCategory msgCategory = CommonEnums.MessageCategory.getEnum(mstrMsgdefForWarning.getCategory());
	        // リソースよりユーザ言語で取得
	        warningCategoryName = ResourcesHandler.getString(msgCategory.toString(), userLocale);
	        // メッセージ重要度
	        CommonEnums.MessageLevel msgLevel = CommonEnums.MessageLevel.getEnum(mstrMsgdefForWarning.getMessage_level());
	        // アイコン名
	        warningIconName = ResourcesHandler.getString(MessagesHandler.getMessageIconResKey(msgCategory,msgLevel));
		}

        out.print("<header class=\"common-panecon-header\">\n");
        out.print("  <div class=\"common-panecon-header-inner clearfix\">\n");
        out.print("    <div class=\"common-panecon-header-info\">\n");
        // 参照先領域名、画面名、画面ID
        out.print("      <div class=\"common-panecon-header-area-left\">\n");
        out.print("        <div class=\"common-panecon-header-screenName\">" + screenName + "</div>\n");
        out.print("        <p class=\"common-hidden\" id=\"screenId\">" + screenId + "</p>\n");
        out.print("        <p class=\"common-hidden\" id=\"screenName\">" + screenName + "</p>\n");
        out.print("      </div>\n");

        out.print("      <div>\n");

        switch (displayType) {
	        // 工程端末画面
	        case PANECON:
	        	// ホームボタン
	            out.print("        <div class=\"common-header-homeIcon\">\n");
	            out.print("          <a onclick=\"" + homeOnclick + "\"><img alt=\""
	                                            + ResourcesHandler.getString(GnomesResourcesConstants.DI91_0229, userLocale)
	                                            + "\" src=\"" + homeImagePath + "\"></a>\n");
	            out.print("        </div>\n");

	        break;
	        // 工程メインメニュー
	        case PANECON_MAINMENU:
	        	// ログアウト画像パス
	            String logoutImagePath = "./images/gnomes/icons/icon-menu-33.png";

	            // ログアウトonclick
	            String logoutOnclick = "GnomesConfirmBTN('" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0015) + "', '"
	                    + MessagesHandler.getString(GnomesMessagesConstants.MC01_0002) + "', '"
	                    + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0071)
	                    + "', PrivilegeIsnecessaryPassword_NONE, 'logout();');";

	        	out.print("      <div class=\"common-mainmenu-logoutButton\"><a onclick=\"" + logoutOnclick
	                + "\"><img src=\"" + logoutImagePath + "\" alt=\""
	                + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0071) + "\"></a></div>\n");

	        break;
        }

        // ユーザ情報
        out.print("        <div class=\"common-header-user-unit\">\n");
        out.print("          <div class=\"common-header-userIcon\"><a onclick=\"" + logoOnclick + "\"><img src=\""
                                        + userImagePath + "\" alt=\""
                                        + ResourcesHandler.getString(GnomesResourcesConstants.DI91_0230, userLocale) + "\"></a></div>\n");
        out.print("          <div class=\"common-header-user-info\">\n");
        out.print("            <span class=\"common-header-userName\">" + userName + "</span><br>\n");
        out.print("            <span class=\"common-header-deviceName\" id=\"dispComputerName\">"
                                        + computerName + "</span>\n");
        out.print("            <p class=\"common-hidden\" id=\"userId\">" + userId + "</p>\n");
        out.print("          </div>\n");
        out.print("        </div>\n");

        // 指図工程、作業工程、作業場所
        out.print("        <div class=\"common-header-factory-unit\">\n");
        out.print("          <div class=\"common-header-panecon-factoryIcon\"><a onclick=\"" + factoryOnclick
                                        + "\"><img src=\"" + factoryImagePath + "\" alt=\""
                                        + ResourcesHandler.getString(GnomesResourcesConstants.DI91_0231, userLocale) + "\"></a></div>\n");
        out.print("          <div class=\"common-header-panecon-factoryInfo\">\n");
        out.print("            <div class=\"common-header-panecon-faactroyinfo-work\"><span class=\"common-panecon-work-unit\">"
                            + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0109, userLocale))
                            + "</span>"
                            + strOrderProcess
                            + "</div>\n");
        out.print("            <div class=\"common-header-panecon-faactroyinfo-work\"><span class=\"common-panecon-work-unit\">"
                            + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0111, userLocale))
                            + "</span>"
                            + strWorkProcess
                            + "</div>\n");
        out.print("            <div class=\"common-header-panecon-faactroyinfo-work\"><span class=\"common-panecon-work-unit\">"
                            + this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0113, userLocale))
                            + "</span>"
                            + strWorkCell
                            + "</div>\n");
        out.print("          </div>\n");
        out.print("            <input type=\"hidden\" name=\"prohibitString\" value=\"" + prohibitionChars + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgBodyName\" value=\"" + MessagesHandler.getString(GnomesMessagesConstants.ME01_0245, userLocale) + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgCategoryName\" value=\"" + categoryName + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgIconName\" value=\"" + iconName + "\">\n");

        // 警告ダイアログ表示情報
        out.print("            <input type=\"hidden\" name=\"inputChangeFlag\" value=\"" + inputChange + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesWarningBody\" value=\"" + MessagesHandler.getString(GnomesMessagesConstants.ME01_1001, userLocale) + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgWarningCategoryName\" value=\"" + warningCategoryName + "\">\n");
        out.print("            <input type=\"hidden\" name=\"mesgWarningIconName\" value=\"" + warningIconName + "\">\n");

        out.print("        </div>\n");

        // メニュー
        outMenu(out, menuItemListBeanName, headerMenuItemInfo, stmPartsPrivilegeResultInfo, displayType, userLocale);

        out.print("      </div>\n");

        out.print("    </div>\n");
        out.print("  </div>\n");

        // ロゴ
        out.print("  <div class=\"common-panecon-header-productLogo\"><a onclick=\""
                        + logoOnclick + "\"><img src=\"" + logoImagePath + "\" alt=\""
                        + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0129, userLocale) + "\"></a></div>\n");
        out.print("</header>\n");

    }

    /**
     * ヘッダー項目表示出力(Bean定義)
     * @param out 出力先
     * @param messageList メッセージ一覧
     * @param userLocale ユーザロケール
     * @param panecon 工程端末か否か
     * @throws Exception 例外
     */
    private void outMessageList(JspWriter out,
            List<PopupMessageInfo> messageList,
            Locale userLocale,
            boolean panecon) throws Exception {

        // 一件目のメッセージを初期表示
        out.print("        <div class=\"ui-input-text ui-body-inherit ui-corner-all"
                                + " ui-shadow-inset common-header-messageList-size\">\n");
        out.print("          <div  id=\"messageDropDown\" class=\"dropdown\">\n");
        out.print("          </div>\n");
        out.print("        </div>\n");
        out.print("        <div id=\"popupMessageInfos\" style=\"display: none;\">\n");

        for (PopupMessageInfo messageInfo: messageList) {

            // 発生日時
            out.print("          <p name=\"popupMessageInfo_occurDate\">"
                                    + messageInfo.getOccurDate() + "<p>\n");
            // メッセージno.
            out.print("          <p name=\"popupMessageInfo_messageNo\">"
                                    + messageInfo.getMessageNo() + "<p>\n");
            // 発生者id
            out.print("          <p name=\"popupMessageInfo_occrUserId\">"
                                    + messageInfo.getOccrUserId() + "<p>\n");
            // 発生者名
            out.print("          <p name=\"popupMessageInfo_occrUserName\">"
                                    + messageInfo.getOccrUserName() + "<p>\n");
            // 発生元コンピュータ名
            out.print("          <p name=\"popupMessageInfo_occrHost\">"
                                    + messageInfo.getOccrHost() + "<p>\n");
            // 種別
            out.print("          <p name=\"popupMessageInfo_category\">"
                                    + messageInfo.getCategory() + "<p>\n");
            // 種別(名称)
            out.print("          <p name=\"popupMessageInfo_categoryName\">"
                                    + messageInfo.getCategoryName() + "<p>\n");
            // メッセージ重要度
            out.print("          <p name=\"popupMessageInfo_msgLevel\">"
                                    + messageInfo.getMsgLevel() + "<p>\n");
            // メッセージ重要度(名称)
            out.print("          <p name=\"popupMessageInfo_msgLevelName\">"
                                    + messageInfo.getMsgLevelName() + "<p>\n");
            // メッセージ
            out.print("          <p name=\"popupMessageInfo_message\">"
                                    + messageInfo.getMessage() + "<p>\n");
            // メッセージ詳細
            out.print("          <p name=\"popupMessageInfo_messageDetail\">"
                                    + messageInfo.getMessageDetail() + "<p>\n");
            // メッセージアイコン名
            out.print("          <p name=\"popupMessageInfo_iconName\">"
                                    + messageInfo.getIconName() + "<p>\n");

            // 領域
            out.print("          <p name=\"popupMessageInfo_dbAreaDiv\">"
                                    + messageInfo.getDbAreaDiv() + "<p>\n");

            if (!StringUtil.isNullOrEmpty(messageInfo.getGuidanceMessage())) {
                // ガイダンスメッセージ
                out.print("          <p name=\"popupMessageInfo_guidanceMessage\">"
                        + messageInfo.getGuidanceMessage() + "<p>\n");
                // リンクURL
                out.print("          <p name=\"popupMessageInfo_linkURL\">" + messageInfo.getLinkURL() + "<p>\n");
                // リンク名
                out.print("          <p name=\"popupMessageInfo_linkName\">" + messageInfo.getLinkName() + "<p>\n");
            }
            else {
                // ガイダンスメッセージ
                out.print("          <p name=\"popupMessageInfo_guidanceMessage\"><p>\n");
                // リンクURL
                out.print("          <p name=\"popupMessageInfo_linkURL\"><p>\n");
                // リンク名
                out.print("          <p name=\"popupMessageInfo_linkName\"><p>\n");
            }
            out.print("\n");

        }

        out.print("        </div>\n");


    }

    /**
     * メニューアイコン、画面共通プルダウン出力
     * @param out 出力先
     * @param menuItemListBeanName メニュー項目辞書
     * @param headerMenuItemInfo メニュー項目定義Bean名
     * @param stmPartsPrivilegeResultInfo パーツ権限情報List
     * @param displayType 表示画面区分
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private void outMenu(JspWriter out, String menuItemListBeanName,
            Map<String, Object> headerMenuItemInfo,
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo,
            DisplayType displayType,
            Locale userLocale) throws Exception {
        // 出力元データのクラス
        Class<?> clsBean = bean.getClass();


        if (displayType == CommonEnums.DisplayType.PANECON_MAINMENU) {
        	String versionNum = "";
            // ソフトウェアのバージョン取得
     		MstrSystemDefine systemDefineForSofversion  = this.mstrSystemDefineDao.getMstrSystemDefine(
                     SystemDefConstants.SOFTWARE_VERSION,
                     SystemDefConstants.VERSION_NUMBER);
     		if (!Objects.isNull(systemDefineForSofversion) && !StringUtil.isNullOrEmpty(systemDefineForSofversion.getChar1())) {
     			versionNum = StringUtils.getStringEscapeHtml(systemDefineForSofversion.getChar1());
     		}

     		out.print("      <div class=\"version-logo\" style=\"float: right;\">\n");

     		out.print("      <div class=\"common-mainmenu-version\"><span class=\"version-number\">" + versionNum + "</span>\n");
     		out.print("      </div>\n");
        }
        // メニュー
        if (displayType == CommonEnums.DisplayType.PANECON || displayType == CommonEnums.DisplayType.PANECON_MAINMENU) {
            out.print("      <div class=\"common-header-menu-unit\" style=\"float: right;\">\n");
        } else {
            out.print("      <div class=\"common-header-menu-unit\">\n");
        }
        out.print("        <div class=\"common-header-menuIcon\">\n");
        out.print("          <a><img src=\"./images/gnomes/icons/icon-menu-22.png\" alt=\""
                            + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0004, userLocale) + "\"></a>\n");
        out.print("        </div>\n");
        out.print("        <div class=\"common-header-menu-list\">\n");
        out.print("          <ul>\n");

        // 辞書にメニュー項目が定義されている場合
        if (headerMenuItemInfo != null) {
            outMenuItem(out, headerMenuItemInfo, stmPartsPrivilegeResultInfo, userLocale);
        }

        // メニュー項目Beanが指定されている場合、取得したメニュー項目を追加表示
        if (!StringUtil.isNullOrEmpty(menuItemListBeanName)) {

            // 出力元データのクラス
            try {
                List<MenuItemInfoBean> menuItemInfoBeanList = (List<MenuItemInfoBean>)getData(clsBean, bean, menuItemListBeanName);

                // メニュー項目定義Beanを取得した場合
                if (menuItemInfoBeanList != null && menuItemInfoBeanList.size() > 0) {
                    // メニュー項目出力内容を定義Beanの情報を元に追加表示
                    outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, userLocale);
                }
            }
            catch(Exception e) {

            }

        }

        // ホーム画像onclick for manage
        String homeOnclick = "document.main.command.value='" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0100, userLocale) + "';document.main.submit();";

        if (displayType == CommonEnums.DisplayType.PANECON || displayType == CommonEnums.DisplayType.PANECON_MAINMENU) {
        	// ホーム画像onclick for panecon
            homeOnclick = "document.main.command.value='" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0101, userLocale) + "';document.main.submit();";
        }

        out.print("<li class=\"\"><div name=\"logOut\" class=\"common-menu-item \"><span onclick=\""
                + homeOnclick + "\">" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0103, userLocale) + "</span></div></li>");

        // 画面ロック
        out.print("<li class=\"\"><div name=\"logOut\" class=\"common-menu-item \"><span onclick=\"lockTimeOut();\">" + ResourcesHandler.getString(GnomesResourcesConstants.YY01_0104, userLocale) + "</span></div></li>");

        // ブックマーク区分
        BookMarkType bookMarkType = getScreenBookMarkType();
        // ブックマーク対象画面の場合
        if (bookMarkType != BookMarkType.NONE) {
            outBookMarkMenuItem(out, bookMarkType, userLocale);
        }

        out.print("          </ul>\n");
        out.print("        </div>\n");
        out.print("      </div>\n");

        if (displayType == CommonEnums.DisplayType.PANECON_MAINMENU) {
        	out.print("      </div>\n");
        }
    }

    /**
     * 終了処理
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * 解放処理
     */
    public void release() {}

}