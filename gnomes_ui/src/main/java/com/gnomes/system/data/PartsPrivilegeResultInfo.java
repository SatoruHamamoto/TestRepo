package com.gnomes.system.data;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayConfirmFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayDiscardChangeFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayFinishFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeIsnecessaryPassword;

/**
*
* パーツ権限結果情報
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/01/23 KCC/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public class PartsPrivilegeResultInfo {

    /**  パーツID(画面項目ID):String */
    private String tagId;

    /** 画面ID:String */
// 未使用のため削除
//    private String screenId;

    /** ボタンID:String */
    private String buttonId;

    /** ボタン名:String */
    private String buttonName;

    /** 権限ID:String */
    private String privilegeId;

    /** 確認ダイアログ出力時のメッセージ:String */
    private String confirmMessage;

    /** 完了ダイアログ出力時のメッセージ:String */
    private String completionMessage;

    /** 確認ダイアログの表示有無:列挙型 */
    private PrivilegeDisplayConfirmFlag displayConfirmFlag;

    /** 完了ダイアログの表示有無:列挙型 */
    private PrivilegeDisplayFinishFlag displayFinishFlag;

    /** データ入力時確認ダイアログの表示有無:列挙型 */
    private PrivilegeDisplayDiscardChangeFlag displayDiscardChangeFlag;

    /** ユーザ認証の有無:列挙型 */
    private PrivilegeIsnecessaryPassword isNecessaryPassword;

    /** 権限有無:boolean */
    private boolean isPrivilege;

    /** 2重サブミットチェック有無:boolean */
    private boolean isCheckDoubleSubmit;

    /** 代替フラグ */
    private String substituteFlag;

    /** ボタン表示区分 */
    private DisplayDivision displayDivision;

    /**
     * パーツID(画面項目ID)を取得
     * @return tagId パーツID(画面項目ID)
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * パーツID(画面項目ID)の設定
     * @param tagId パーツID(画面項目ID)
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * 画面IDを取得
     * @return screenId 画面ID
     */
// 未使用項目のため削除
/*
    public String getScreenId() {
        return screenId;
    }
*/
    /**
     * 画面IDを設定
     * @param screenId 画面ID
     */
 // 未使用項目のため削除
/*
    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }
*/
    /**
     * ボタンIDを取得
     * @return buttonId ボタンID
     */
    public String getButtonId() {
        return buttonId;
    }

    /**
     * ボタンIDを設定
     * @param buttonId ボタンID
     */
    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

	/**
	 * ボタン名を取得
	 * @return buttonName ボタン名
	 */
	public String getButtonName() {
		return buttonName;
	}

	/**
	 * ボタン名を設定
	 * @param buttonName ボタン名
	 */
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

    /**
     * 権限IDを取得
     * @return privilegeId 権限ID
     */
    public String getPrivilegeId() {
        return privilegeId;
    }

    /**
     * 権限IDの設定
     * @param privilegeId 権限ID
     */
    public void setPrivilegeId(String privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * 確認ダイアログ出力時のメッセージを取得
     * @return confirmMessage 確認ダイアログ出力時のメッセージ
     */
    public String getConfirmMessage() {
        return confirmMessage;
    }

    /**
     * 確認ダイアログ出力時のメッセージの設定
     * @param confirmMessage 確認ダイアログ出力時のメッセージ
     */
    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    /**
     * 完了ダイアログ出力時のメッセージを取得
     * @return completionMessage 完了ダイアログ出力時のメッセージ
     */
    public String getCompletionMessage() {
        return completionMessage;
    }

    /**
     * 完了ダイアログ出力時のメッセージの設定
     * @param completionMessage 完了ダイアログ出力時のメッセージ
     */
    public void setCompletionMessage(String completionMessage) {
        this.completionMessage = completionMessage;
    }

    /**
     * 確認ダイアログの表示有無を取得
     * @return displayConfirmFlag 確認ダイアログの表示有無
     */
	public PrivilegeDisplayConfirmFlag getDisplayConfirmFlag() {
		return displayConfirmFlag;
	}

    /**
     * 確認ダイアログの表示有無の設定
     * @param displayConfirmFlag 確認ダイアログの表示有無
     */
	public void setDisplayConfirmFlag(PrivilegeDisplayConfirmFlag displayConfirmFlag) {
		this.displayConfirmFlag = displayConfirmFlag;
	}

    /**
     * 完了ダイアログの表示有無を取得
     * @return displayFinishFlag 完了ダイアログの表示有無
     */
	public PrivilegeDisplayFinishFlag getDisplayFinishFlag() {
		return displayFinishFlag;
	}

    /**
     * 完了ダイアログの表示有無の設定
     * @param displayFinishFlag 完了ダイアログの表示有無
     */
	public void setDisplayFinishFlag(PrivilegeDisplayFinishFlag displayFinishFlag) {
		this.displayFinishFlag = displayFinishFlag;
	}

    /**
     * データ入力時確認ダイアログの表示有無を取得
     * @return displayDiscardChangeFlag データ入力時確認ダイアログの表示有無
     */
	public PrivilegeDisplayDiscardChangeFlag getDisplayDiscardChangeFlag() {
		return displayDiscardChangeFlag;
	}

    /**
     * データ入力時確認ダイアログの表示有無の設定
     * @param displayDiscardChangeFlag データ入力時確認ダイアログの表示有無
     */
	public void setDisplayDiscardChangeFlag(PrivilegeDisplayDiscardChangeFlag displayDiscardChangeFlag) {
		this.displayDiscardChangeFlag = displayDiscardChangeFlag;
	}

    /**
     * ユーザ認証の有無を取得
     * @return isNecessaryPassword ユーザ認証の有無
     */
    public CommonEnums.PrivilegeIsnecessaryPassword getIsNecessaryPassword() {
        return isNecessaryPassword;
    }

    /**
     * ユーザ認証の有無の設定
     * @param isNecessaryPassword ユーザ認証の有無
     */
    public void setIsNecessaryPassword(CommonEnums.PrivilegeIsnecessaryPassword isNecessaryPassword) {
        this.isNecessaryPassword = isNecessaryPassword;
    }

    /**
     * 権限有無を取得
     * @return isPrivilege 権限有無
     */
    public boolean isPrivilege() {
        return isPrivilege;
    }

    /**
     * 権限有無の設定
     * @param isPrivilege 権限有無
     */
    public void setPrivilege(boolean isPrivilege) {
        this.isPrivilege = isPrivilege;
    }

    /**
     * 2重チェック有無の取得
     * @return isCheckDoubleSubmit
     */
    public boolean getIsCheckDoubleSubmit() {
        return isCheckDoubleSubmit;
    }

    /**
     * 2重チェック有無の設定
     * @param isCheckDoubleSubmit 2重チェック有無
     */
    public void setIsCheckDoubleSubmit(boolean isCheckDoubleSubmit) {
        this.isCheckDoubleSubmit = isCheckDoubleSubmit;
    }

    /**
     * 代替フラグを取得
     * @return substituteFlag 代替フラグ
     */
    public String getSubstituteFlag() {
        return substituteFlag;
    }

    /**
     * 代替フラグを設定
     * @param substituteFlag 代替フラグ
     */
    public void setSubstituteFlag(String substituteFlag) {
        this.substituteFlag = substituteFlag;
    }

    /**
     * ボタン表示区分を取得
     * @return ボタン表示区分
     */
    public DisplayDivision getDisplayDiv()
    {
        return this.displayDivision;
    }

    /**
     * ボタン表示区分を設定
     * @param display_div ボタン表示区分
     */
    public void setDisplayDiv(Integer displayDiv)
    {
        this.displayDivision = DisplayDivision.getEnum(displayDiv);
    }

}
