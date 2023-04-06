package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.BookMarkType;
import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * メニューを扱うカスタムタグの基底
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/1/18 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public abstract class GnomesCTagBaseMenu extends GnomesCTagBaseBookmark {

    /** 辞書：メニュー項目name */
    protected static final String INFO_ITEM_NAME = "item_name";

    /** 辞書：メニュー項目リソースID */
    protected static final String INFO_ITEM_RESOURCE_ID = "item_resource_id";

    /** 辞書：メニュー項目コマンドID */
    protected static final String INFO_ITEM_COMMAND_ID = "item_command_id";

    /** 辞書：メニュー項目Onclick */
    protected static final String INFO_ITEM_ONCLICK = "item_onclick";

    /** 辞書：メニュー項目間区切り */
    protected static final String INFO_SEPARATE = "separate";

    /** 辞書：メニュー項目スタイルシート追加 */
    protected static final String INFO_ITEM_ADD_STYLE = "item_add_style";

    /** 辞書：メニュー項目ボタンID */
    protected static final String INFO_ITEM_BUTTON_ID = "item_button_id";

    @Inject
    SearchInfoController searchInfoController;

    @Inject
    GnomesCTagDictionary gnomesCTagDictionary;

    /**
     * メニュー項目表示出力
     * @param out 出力先
     * @param menuItemInfo 項目辞書
     * @param stmPartsPrivilegeResultInfo パーツ権限情報
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    protected void outMenuItem(JspWriter out, Map<String, Object> menuItemInfo, List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo, Locale userLocale) throws Exception {
        outMenuItem(out, menuItemInfo, stmPartsPrivilegeResultInfo, userLocale, null, null);
    }

    /**
     * メニュー項目表示出力
     * @param out 出力先
     * @param menuItemInfo 項目辞書
     * @param stmPartsPrivilegeResultInfo パーツ権限情報
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    protected void outMenuItem(JspWriter out, Map<String, Object> menuItemInfo, List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo, Locale userLocale, Object baseFormBean, String dictId) throws Exception {

        for (int i=1 ; ; i++) {

            // メニュー項目取得
            Map<String, Object> menuItemInfoDetail = (Map<String, Object>)menuItemInfo.get(ConverterUtils.numberToString(false, userLocale.toString(), i, null));

            if (Objects.isNull(menuItemInfoDetail)) {
                break;
            }

            // メニュー項目name名
            String itemName= (String)menuItemInfoDetail.get(INFO_ITEM_NAME);

            // メニュー項目リソースID
            String strLabel = "";
            String okResourceLabel = "";
            String itemResourceId= (String)menuItemInfoDetail.get(INFO_ITEM_RESOURCE_ID);
            if (!StringUtil.isNullOrEmpty(itemResourceId)) {
                strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(itemResourceId, userLocale));
                okResourceLabel = strLabel;
            }

            // onclick
            String onclick = "";
            String okScript = "";
            // コマンドID取得
            if (!StringUtil.isNullOrEmpty((String)menuItemInfoDetail.get(INFO_ITEM_COMMAND_ID))) {
                onclick = String.format(COMMAND_SCRIPT_FORMAT, (String)menuItemInfoDetail.get(INFO_ITEM_COMMAND_ID));
                okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, (String)menuItemInfoDetail.get(INFO_ITEM_COMMAND_ID));
            }
            // onclick取得
            else if (!StringUtil.isNullOrEmpty((String)menuItemInfoDetail.get(INFO_ITEM_ONCLICK))) {
                onclick = (String)menuItemInfoDetail.get(INFO_ITEM_ONCLICK);

            }

            // 項目スタイルシート追加クラス取得
            String addStyle = (String)menuItemInfoDetail.get(INFO_ITEM_ADD_STYLE);
            if (StringUtil.isNullOrEmpty(addStyle)) {
                 addStyle = "";
            }

            // ボタンID取得
            String buttonId = (String)menuItemInfoDetail.get(INFO_ITEM_BUTTON_ID);

            //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
            String disabledClass = judgeAndSetDisabledClass(menuItemInfoDetail);

            PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

            // 権限無しのClass名
            String nonePrivilegeClassName = "";

            if (!StringUtil.isNullOrEmpty(buttonId)) {
                // パーツ権限を取得
                partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(buttonId, stmPartsPrivilegeResultInfo);

				if (partsPrivilegeResultInfo != null) {
					 partsPrivilegeResultInfo.setSubstituteFlag(addStyle); // for checking className(AlternateApprove) 代替承認
				}

                // 権限からボタンのhtml属性取得
                Map<String, String> mapAttribute =
                        getButtonAttribute(partsPrivilegeResultInfo, strLabel, okResourceLabel, okScript, (String)menuItemInfoDetail.get(INFO_ITEM_ONCLICK), userLocale);

                // onclickスクリプト
                onclick = mapAttribute.get(MAP_KEY_ONCLICK);

                // disableが空でない（権限無し）場合
                if (!StringUtil.isNullOrEmpty(mapAttribute.get(MAP_KEY_DISABLE))) {
                    // 非活性表示クラス＊業務側の処理によりで対応すること
                    // disabledClass = "common-disabled";
                }

                // 権限があるかをチェック
                if (partsPrivilegeResultInfo != null && !partsPrivilegeResultInfo.isPrivilege()) {
                    // 権限無しの場合、ボタン表示区分を取得
                    DisplayDivision displayDivision = partsPrivilegeResultInfo.getDisplayDiv();

                    if (displayDivision == DisplayDivision.DISABLE) {
                        // ボタン表示区分が非活性である場合
                        nonePrivilegeClassName = " common-base-menu-disabled";
                    }
                    else if (displayDivision == DisplayDivision.HIDE) {
                        // ボタン表示区分が非表示である場合
                        nonePrivilegeClassName = " common-base-menu-hide";
                    }
                    else {
                        nonePrivilegeClassName = "";
                    }
                }
            }

            // BaseFormBeanのカスタムタグの非表示区分よりstyleを取得する
            String strStyle = "";
            if (baseFormBean != null) {
                String key = dictId + "." + itemName;
                strStyle = this.getStyleHiddenKindWithAttribute(baseFormBean, key);
            }

            // 次の項目との区切り線の表示の有無
            String itemSeparate= (String)menuItemInfoDetail.get(INFO_SEPARATE);

            // 区切Class名
            String itemSeparateClassName = "";
            if (!StringUtil.isNullOrEmpty(itemSeparate)) {
                // 区切り有り
                itemSeparateClassName = " common-menu-separation";
            }

            if (!StringUtil.isNullOrEmpty(nonePrivilegeClassName)) {
                // 権限無しのClass名が空白ではない場合
                out.print(
                        "      <li class=\"" + itemSeparateClassName + disabledClass + "\"" + strStyle + "><div name=\"" + itemName + "\" class=\"" + addStyle + nonePrivilegeClassName + "\"><span>" + strLabel + "</span></div></li>\n");
            }
            else {
                // 権限ありの場合、または、ボタン表示区分が「エラーを表示（従来の動き）」である場合
                out.print(
                        "      <li class=\"" + itemSeparateClassName + disabledClass + "\"" + strStyle + "><div name=\"" + itemName + "\" class=\"common-menu-item " + addStyle + "\"><span onclick=\"" + onclick + "\">" + strLabel + "</span></div></li>\n");
            }
        }
    }

	/**
     * メニュー項目表示出力(Bean定義)
     * @param out 出力先
     * @param menuItemInfoBeanList メニュー項目Beanリスト
     * @param stmPartsPrivilegeResultInfo パーツ権限情報
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    protected void outMenuItemFormBean(JspWriter out, List<MenuItemInfoBean> menuItemInfoBeanList, List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo, Locale userLocale) throws Exception {

        for (MenuItemInfoBean menuItemInfoBean: menuItemInfoBeanList) {

            // メニュー項目name名
            String itemName= menuItemInfoBean.getItemName();

            // メニュー項目リソースID
            String strLabel = "";
            String okResourceLabel = "";
            String itemResourceId= menuItemInfoBean.getItemResourceId();
            if (!StringUtil.isNullOrEmpty(itemResourceId)) {
                strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(itemResourceId, userLocale));
                okResourceLabel = strLabel;
            }

            // onclick
            String onclick = "";
            String okScript = "";
            // コマンドID取得
            if (!StringUtil.isNullOrEmpty(menuItemInfoBean.getItemCommandId())) {
                onclick = String.format(COMMAND_SCRIPT_FORMAT, menuItemInfoBean.getItemCommandId());
                okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, menuItemInfoBean.getItemCommandId());
            }
            // onclick取得
            else if (!StringUtil.isNullOrEmpty(menuItemInfoBean.getItemOnclick())) {
                onclick = menuItemInfoBean.getItemOnclick();

            }

            // ボタンID取得
            String buttonId = menuItemInfoBean.getItemButtonId();
            String disabledClass = "";
            PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

            if (!StringUtil.isNullOrEmpty(buttonId)) {
                // パーツ権限を取得
                partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(buttonId, stmPartsPrivilegeResultInfo);
                // 権限からボタンのhtml属性取得
                Map<String, String> mapAttribute =
                        getButtonAttribute(partsPrivilegeResultInfo, strLabel, okResourceLabel, okScript, menuItemInfoBean.getItemOnclick(), userLocale);

                // onclickスクリプト
                onclick = mapAttribute.get(MAP_KEY_ONCLICK);

                // disableが空でない（権限無し）場合
                if (!StringUtil.isNullOrEmpty(mapAttribute.get(MAP_KEY_DISABLE))) {
                    // 非活性表示クラス＊業務側の処理によりで対応すること
                    // disabledClass = "common-disabled";
                }

            }

            // スタイルシート追加クラス取得
            String addStyle = menuItemInfoBean.getAddStyle();
            if (StringUtil.isNullOrEmpty(addStyle)) {
                 addStyle = "";
            }
            
            //接続領域ごと操作可否で領域が違うと非表示になる
            disabledClass = judgeAndSetDisabledClassByBean(menuItemInfoBean);

            // 区切り有り
            if (menuItemInfoBean.getIsSeparate()) {
                out.print("      <li class=\"common-menu-separation "+ disabledClass +"\"><div name=\"" + itemName + "\" class=\"common-menu-item " + addStyle + "\"><span onclick=\"" + onclick + "\">" + strLabel + "</span></div></li>\n");

            }
            // 区切り無し
            else {
                out.print("      <li class=\""+ disabledClass +"\"><div name=\"" + itemName + "\" class=\"common-menu-item " + addStyle + "\"><span onclick=\"" + onclick + "\">" + strLabel + "</span></div></li>\n");

            }

        }

    }

    /**
     * メニュー項目表示出力(ブックマーク)
     * @param out 出力先
     * @param bookMarkType ブックマーク区分
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    protected void outBookMarkMenuItem(JspWriter out, BookMarkType bookMarkType, Locale userLocale) throws Exception {

        String onclickValue = "";

        String strLabel = "";

        String errorTitle = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0005);

        String errorMessage = "";

        String okButtonLabel = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0019);

        String bookmarkAction = "";

        // ブックマーク区分がブックマーク済の場合
        if (bookMarkType == BookMarkType.MARKED) {
        	errorMessage = MessagesHandler.getString(GnomesMessagesConstants.MC01_0011);
        	onclickValue = CommandIdConstants.COMMAND_ID_Y99003C001;
            strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0071, userLocale));
        }
        // ブックマーク区分がブックマーク未の場合
        else if (bookMarkType == BookMarkType.NO_MARK) {
        	errorMessage = MessagesHandler.getString(GnomesMessagesConstants.MC01_0010);
        	onclickValue = CommandIdConstants.COMMAND_ID_Y99003C002;
            strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0070, userLocale));
        }

        bookmarkAction = "MakeModalBookmark('" + errorTitle + "', '"+ errorMessage + "', '"+ okButtonLabel + "','"
                + onclickValue + "');";

        out.print("      <li class=\"common-menu-separation\"><div class=\"common-menu-item\"><span name=\"bookmark\" onclick=\"" + bookmarkAction + "\">" + strLabel + "</span></div></li>\n");

    }

}
