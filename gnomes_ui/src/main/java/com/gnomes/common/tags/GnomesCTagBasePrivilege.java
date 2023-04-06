package com.gnomes.common.tags;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayConfirmFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayDiscardChangeFlag;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * ボタンを扱うカスタムタグの基底
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
public abstract class GnomesCTagBasePrivilege extends GnomesCTagBase {

    // 属性マップのキー：非活性
    protected static final String MAP_KEY_DISABLE = "disabled";

    // 属性マップのキー：onclick
    protected static final String MAP_KEY_ONCLICK = "onclick";

    /**
     * 権限からボタンのhtml属性取得
     * @param partsPrivilegeResultInfo パーツ権限
     * @param title タイトル
     * @param okBtnLabel OKボタンラベル
     * @param script OKボタンスクリプト
     * @param onclick onclickコマンド
     * @param userLocale ユーザーロケール
     * @return ボタンのhtml属性
     */
    protected Map<String,String> getButtonAttribute(PartsPrivilegeResultInfo partsPrivilegeResultInfo, String title, String okBtnLabel, String script, String onclick, Locale userLocale) {

        Map<String, String> mapAttribute = new HashMap<String, String>();
        String disabled = "";

        if (partsPrivilegeResultInfo != null) {


            // onclickがない場合
            if (onclick == null || onclick.length() == 0) {

                // 権限有りの場合、確認、認証ダイアログの有無のチェック
                if(partsPrivilegeResultInfo.isPrivilege()){

                    String certUserId = gnomesSessionBean.getUserId();

                    // 確認ダイアログ有
                    if (partsPrivilegeResultInfo.getDisplayConfirmFlag()
                            == PrivilegeDisplayConfirmFlag.PrivilegeDisplayConfirmFlag_ON)
                    {
                        // メッセージに改行が入っていると、JavaScriptの引数に改行が入り、メソッドが動かなくなるので、
                        // メッセージ内の改行を<br>に置換する。
                        onclick = "GnomesConfirmBTN('"+ title + "','"
                                        + (partsPrivilegeResultInfo.getConfirmMessage() == null ? null:
                                           partsPrivilegeResultInfo.getConfirmMessage().replace("\n", "<br>")) + "','"
                                        + okBtnLabel + "',"
                                        + partsPrivilegeResultInfo.getIsNecessaryPassword().getValue() + ",'"
                                        + script + "','"
                                        + "','"
                                        + certUserId + "','"
                                        + partsPrivilegeResultInfo.getButtonId() + "','"
                                        + "true');";
                    }

                    // データ入力時、確認ダイアログ有
                    else if (partsPrivilegeResultInfo.getDisplayDiscardChangeFlag()
                            == PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_ON)
                    {
                        // 入力された内容を取消します。よろしいですか？
                        // メッセージに改行が入っていると、JavaScriptの引数に改行が入り、メソッドが動かなくなるので、メッセージの改行を<br>に置換する。
                        String strInputCheck = MessagesHandler.getString(
                                    GnomesLogMessageConstants.MC01_0004, userLocale) == null ? null:
                                    MessagesHandler.getString(GnomesLogMessageConstants.MC01_0004, userLocale)
                                        .replace("\n", "<br>");

                        String okLabel = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0019, userLocale);     //   "OK"

                        onclick = "inputCheck('"+ title + "','"
                                + strInputCheck + "','"
                                + okLabel + "',"
                                + partsPrivilegeResultInfo.getIsNecessaryPassword().getValue() + ",'"
                                + script + "','"
                                + "','"
                                + certUserId + "','"
                                + partsPrivilegeResultInfo.getButtonId() + "');";
                    }

                    // 確認ダイアログなし
                    else
                    {
                        switch (partsPrivilegeResultInfo.getIsNecessaryPassword())
                        {
                            // 認証ダイアログを表示する
                            case PrivilegeIsnecessaryPassword_SINGLE:
                            	// 代替承認認証ダイアログを判定する
                            	if (!StringUtil.isNullOrEmpty(partsPrivilegeResultInfo.getSubstituteFlag()) && partsPrivilegeResultInfo.getSubstituteFlag().equals("AlternateApprove")) {
                            		onclick = "IsnecessaryPassword('" + title +  "','"
                                            + certUserId +  "','"
                                            + script +  "','"
                                            + partsPrivilegeResultInfo.getButtonId() +  "',"
                                            + "false, true" + ");";
                            	} else {
                            		onclick = "IsnecessaryPassword('" + title +  "','"
                                            + certUserId +  "','"
                                            + script +  "','"
                                            + partsPrivilegeResultInfo.getButtonId() +  "',"
                                            + "false" + ");";
                            	}
                                break;

                            // ダブル認証ダイアログを表示する
                            case PrivilegeIsnecessaryPassword_DOUBLE:
                                onclick = "IsnecessaryPassword('" + title +  "','"
                                        + certUserId +  "','"
                                        + script +  "','"
                                        + partsPrivilegeResultInfo.getButtonId() +  "',"
                                        + "true" + ");";
                                break;

                            // 認証ダイアログなし
                            case PrivilegeIsnecessaryPassword_NONE:
                                onclick = "setButtonId('" + partsPrivilegeResultInfo.getButtonId() + "'); " +  script.replace("\\", "");
                                break;
                        }
                    }
                }
                // 権限無しの場合、確認、認証ダイアログなし
                else {
                    onclick = "setButtonId('" + partsPrivilegeResultInfo.getButtonId() + "'); " +  script.replace("\\", "");
                }
            }
            if (onclick != null) {
                // 2重サブミットチェック有
                if(partsPrivilegeResultInfo.getIsCheckDoubleSubmit()){
                    onclick = "doubleCheck();" + onclick;

                }
//                else {
//                    onclick = "resetDoubleCheck();" + onclick;
//                }
            }
        }

        if (onclick == null && script != null) {
            onclick = script.replace("\\", "");
        }

        mapAttribute.put(MAP_KEY_ONCLICK, onclick);
        mapAttribute.put(MAP_KEY_DISABLE, disabled);

        return mapAttribute;
    }
}
