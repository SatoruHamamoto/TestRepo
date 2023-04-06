package com.gnomes.system.data;

import javax.enterprise.context.Dependent;

/**
 * JSON データ用のクラス定義(セキュリティサービス)
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/19 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class CertInfo {

    /**
     * ユーザID
     */
    private String userId;

    /**
     * パスワード
     */
    private String password;

    /**
     * 変更するパスワード
     */
    private String newPassword;

    /**
     * 変更するパスワード（確認）
     */
    private String newPasswordConfirm;

    /**
     * 変更するパスワード（暗号化）
     */
    private String newPasswordEncrypted;

    /**
     * ロケールID
     */
    private String localeId;

    /**
     * 端末ID
     */
    private String computerId;

    /**
     * 拠点コード
     */
    private String siteCode;

    /**
     * 領域区分
     */
    private String regionType;

    /**
     * 認証成功か否か
     */
    private Integer isSuccess;

    /**
     * パスワード更新成功か否か
     */
    private Boolean isSuccessChange;

    /**
     * パスワード変更必要か否か
     */
    private Integer isChangePassword;

    /**
     * 初期化パスワードか否か
     */
    private Integer isInitPassword;

    /**
     * メッセージ情報
     */
    private String message;

    /**
     * リンク情報
     */
    private String[] linkInfo;

    /**
     * 画面Id
     */
    private String screenId;

    /**
     * 画面名
     */
    private String screenName;

    /**
     * ログインユーザID
    */
    private String loginUserId;

    /**
     *  ログインユーザパスワード
     */
    private String loginUserPassword;

    /**
     * 認証者ユーザID
     */
    private String certUserId;

    /**
     * 認証者ユーザパスワード
     */
    private String certUserPassword;


    /**
     * パスワード変更ボタン名
     */
    private String changePswBtnName;

    /**
     * パスワードがハッシュ値として入っているかどうか
     * true:ハッシュ値
     * false/null:従来
     */
    private boolean passwordIsHash;

    /**
     * ユーザIDを取得
     * @return ユーザID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザIDを設定
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * パスワードを取得
     * @return パスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 変更するパスワードを取得
     * @return 変更するパスワード
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 変更するパスワードを設定
     * @param newPassword 変更するパスワード
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * 変更するパスワード（確認）を取得
     * @return 変更するパスワード（確認）
     */
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    /**
     * 変更するパスワード（確認）を設定
     * @param newPasswordConfirm 変更するパスワード（確認）
     */
    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

	/**
	 * 変更するパスワード（暗号化）を取得
	 * @return 変更するパスワード（暗号化）
	 */
	public String getNewPasswordEncrypted() {
		return newPasswordEncrypted;
	}

	/**
	 * 変更するパスワード（暗号化）を設定
	 * @param newPasswordEncrypted 変更するパスワード（暗号化）
	 */
	public void setNewPasswordEncrypted(String newPasswordEncrypted) {
		this.newPasswordEncrypted = newPasswordEncrypted;
	}

    /**
     * ロケールIDを取得
     * @return ロケールID
     */
    public String getLocaleId() {
        return localeId;
    }

    /**
     * ロケールIDを設定
     * @param localeId ロケールID
     */
    public void setLocalId(String localeId) {
        this.localeId = localeId;
    }

	/**
	 * 端末IDを取得
	 * @return 端末ID
	 */
	public String getComputerId() {
		return computerId;
	}

	/**
	 * 端末IDを設定
	 * @param computerId 端末ID
	 */
	public void setComputerId(String computerId) {
		this.computerId = computerId;
	}

    /**
     * 拠点コードを取得
     * @return 拠点コード
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * 拠点コードを設定
     * @param siteCode 拠点コード
     */
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    /**
     * 領域区分を取得
     * @return 領域区分
     */
    public String getRegionType() {
        return regionType;
    }

    /**
     * 領域区分を設定
     * @param regionType 領域区分
     */
    public void setRegionType(String regionType) {
        this.regionType = regionType;

    }

    /**
     * 認証成功か否かを取得
     * @return 認証成功か否か
     */
    public Integer getIsSuccess() {
        return isSuccess;
    }

    /**
     * 認証成功か否かを設定
     * @param isSuccess 認証成功か否か
     */
    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * パスワード更新成功か否かを取得
     * @return パスワード更新成功か否か
     */
    public Boolean getIsSuccessChange() {
        return isSuccessChange;
    }

    /**
     * パスワード更新成功か否かを設定
     * @param isSuccessChange パスワード更新成功か否か
     */
    public void setIsSuccessChange(Boolean isSuccessChange) {
        this.isSuccessChange = isSuccessChange;
    }

    /**
     * パスワード変更必要か否かを取得
     * @return パスワード変更必要か否か
     */
    public Integer getIsChangePassword() {
        return isChangePassword;
    }

    /**
     * パスワード変更必要か否かを設定
     * @param isChangePassword パスワード変更必要か否か
     */
    public void setIsChangePassword(Integer isChangePassword) {
        this.isChangePassword = isChangePassword;
    }

    /**
     * 初期化パスワードか否かを取得
     * @return 初期化パスワードか否か
     */
	public Integer getIsInitPassword() {
		return isInitPassword;
	}

    /**
     * 初期化パスワードか否かを設定
     * @param isInitPassword 初期化パスワードか否か
     */
	public void setIsInitPassword(Integer isInitPassword) {
		this.isInitPassword = isInitPassword;
	}

    /**
     * メッセージ情報を取得
     * @return メッセージ情報
     */
    public String getMessage() {
        return message;
    }

    /**
     * メッセージ情報を設定
     * @param message メッセージ情報
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * リンク情報を取得
     * @return リンク情報
     */
    public String[] getLinkInfo() {
        return linkInfo;
    }

    /**
     * リンク情報を設定
     * @param linkInfo リンク情報
     */
    public void setLinkInfo(String[] linkInfo) {
        this.linkInfo = linkInfo;
    }

    /**
     * 画面Idを取得
     * @return 画面Id
     */
	public String getScreenId() {
		return screenId;
	}

	 /**
     * 画面Idを設定
     * @param screenId 画面Id
     */
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	/**
     * 画面名を取得
     * @return 画面名
     */
	public String getScreenName() {
		return screenName;
	}

	 /**
     * 画面名を設定
     * @param screenName 画面名
     */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	/**
     * ログインユーザIDを取得
     * @return loginUserId
     */
	public String getLoginUserId() {
		return loginUserId;
	}

	/**
	 * ログインユーザIDを設定
	 * @param loginUserId ログインユーザID
	 */
	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}

	/**
	 * ログインユーザパスワードを取得
	 * @return loginUserPassword
	 */
	public String getLoginUserPassword() {
		return loginUserPassword;
	}

	/**
	 * ログインユーザパスワードを設定
	 * @param loginUserPassword ログインユーザパスワード
	 */
	public void setLoginUserPassword(String loginUserPassword) {
		this.loginUserPassword = loginUserPassword;
	}

	/**
	 * 認証ユーザIDを取得
	 * @return certUserId
	 */
	public String getCertUserId() {
		return certUserId;
	}

	/**
	 * 認証ユーザIDを設定
	 * @param certUserId 認証ユーザID
	 */
	public void setCertUserId(String certUserId) {
		this.certUserId = certUserId;
	}

	/**
	 * 認証ユーザパスワードを取得
	 * @return certUserPassword
	 */
	public String getCertUserPassword() {
		return certUserPassword;
	}

	/**
	 * 認証ユーザパスワードを設定
	 * @param certUserPassword 認証ユーザパスワード
	 */
	public void setCertUserPassword(String certUserPassword) {
		this.certUserPassword = certUserPassword;
	}

	/**
	 * パスワード変更ボタン名を取得
	 * @return changePswBtnName
	 */
	public String getChangePswBtnName() {
		return changePswBtnName;
	}

	/**
	 * パスワード変更ボタン名を設定
	 * @param changePswBtnName パスワード変更ボタン名
	 */
	public void setChangePswBtnName(String changePswBtnName) {
		this.changePswBtnName = changePswBtnName;
	}

    /**
     * 入っているパスワードがHash値かどうかを取得
     * @return passwordIsHash
     */
    public boolean isPasswordIsHash() {
        return passwordIsHash;
    }

    /**
     * 入っているパスワードがHash値かどうかを設定
     * @param passwordIsHash セットする passwordIsHash
     */
    public void setPasswordIsHash(boolean passwordIsHash) {
        this.passwordIsHash = passwordIsHash;
    }
}
