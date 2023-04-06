package com.gnomes.common.data;

import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.gnomes.common.logging.LogHelper;

/**
 * ユーザ情報クラス定義
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

/**
 * ユーザ情報クラス定義
 */
public class UserInfo {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    /** ユーザKey */
    private String userKey;

    /** ユーザID */
    private String userId;

    /** ユーザ名 */
    private String userName;

    /** パスワード */
    private String password;

    /** ロケールID */
    private String localeId;

    /** 言語 */
    private String language;

    /** IPアドレス */
    private String ipAddress;

    /** 端末ID */
    private String computerId;

    /** 端末名 */
    private String computerName;

    /** 拠点コード */
    private String siteCode;

    /** 拠点名 */
    private String siteName;

    /** 指図工程 */
    private String processCode;

    /** 作業工程 */
    private String workProcessCode;

    /** 作業場所 */
    private String workCellCode;

    /** スクリーンロック中か否か */
    private Boolean isScreenLocked;

    /** スクリーンロック起動時間(分） */
    private Integer screenLockTimeoutTime;

    /** メッセージ一覧画面最大表示件数 */
    private Integer maxListDisplayCount;

    /** ポップアップメッセージ表示件数 */
    private Integer popupDisplayCount;

    /** ポップアップメッセージ監視周期（分） */
    private Integer watchPeriodForPopup;




    /**
     * ユーザKeyを取得
     * @return userKey
     */
    public String getUserKey() {
        return userKey;
    }

    /**
     * ユーザKeyを設定
     * @param userKey ユーザKey
     */
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    /**
     * ユーザIDを取得
     * @return userId
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
     * ユーザ名を取得
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ユーザ名を設定
     * @param userName ユーザ名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * パスワードを取得
     * @return password
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
     * ロケールIDを取得
     * @return localeId
     */
    public String getLocaleId() {
        return localeId;
    }

    /**
     * ロケールIDを設定
     * @param localeId ロケールID
     */
    public void setLocaleId(String localeId) {
        this.localeId = localeId;
    }

    /**
     * 言語を取得
     * @return language
     */
    public String getLanguage() {
        if (language == null || language.isEmpty()) {
            return Locale.getDefault().toString();
        }
        else {
            return language;
        }
    }

    /**
     * 言語を設定
     * @param language 言語
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * IPアドレスを取得
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * IPアドレスを設定
     * @param ipAddress IPアドレス
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

	/**
	 * 端末IDを取得
	 * @return computerId
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
     * 端末名を取得
     * @return computerName
     */
    public String getComputerName() {
        return computerName;
    }

    /**
     * 端末名を設定
     * @param computerName 端末名
     */
    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    /**
     * 拠点コードを取得
     * @return siteCode
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
     * 拠点名を取得
     * @return siteName
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * 拠点名を設定
     * @param siteName 拠点名
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

	/**
	 * 指図工程を取得
	 * @return processCode
	 */
	public String getProcessCode() {
		return processCode;
	}

	/**
	 * 指図工程を設定
	 * @param processCode 指図工程
	 */
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	/**
	 * 作業工程を取得
	 * @return workProcessCode
	 */
	public String getWorkProcessCode() {
		return workProcessCode;
	}

	/**
	 * 作業工程を設定
	 * @param workProcessCode 作業工程
	 */
	public void setWorkProcessCode(String workProcessCode) {
		this.workProcessCode = workProcessCode;
	}

	/**
	 * 作業場所を取得
	 * @return workCellCode
	 */
	public String getWorkCellCode() {
		return workCellCode;
	}

	/**
	 * 作業場所を設定
	 * @param workCellCode 作業場所
	 */
	public void setWorkCellCode(String workCellCode) {
		this.workCellCode = workCellCode;
	}

    /**
     * スクリーンロック中か否かを取得
     * @return isScreenLocked
     */
    public Boolean getIsScreenLocked() {
        return isScreenLocked;
    }

    /**
     * スクリーンロック中か否かを設定
     * @param isScreenLocked スクリーンロック中か否か
     */
    public void setIsScreenLocked(Boolean isScreenLocked) {
        this.isScreenLocked = isScreenLocked;
    }

    /**
     * スクリーンロック起動時間(分）を取得
     * @return screenLockTimeoutTime
     */
    public Integer getScreenLockTimeoutTime() {
        return screenLockTimeoutTime;
    }

    /**
     * スクリーンロック起動時間(分）を設定
     * @param screenLockTimeoutTime スクリーンロック起動時間(分）
     */
    public void setScreenLockTimeoutTime(Integer screenLockTimeoutTime) {
        this.screenLockTimeoutTime = screenLockTimeoutTime;
    }

    /**
     * メッセージ一覧画面最大表示件数を取得
     * @return maxListDisplayCount
     */
    public Integer getMaxListDisplayCount() {
        return maxListDisplayCount;
    }

    /**
     * メッセージ一覧画面最大表示件数を設定
     * @param maxListDisplayCount メッセージ一覧画面最大表示件数
     */
    public void setMaxListDisplayCount(Integer maxListDisplayCount) {
        this.maxListDisplayCount = maxListDisplayCount;
    }

    /**
     * ポップアップメッセージ表示件数を取得
     * @return popupDisplayCount
     */
    public Integer getPopupDisplayCount() {
        return popupDisplayCount;
    }

    /**
     * ポップアップメッセージ表示件数を設定
     * @param popupDisplayCount ポップアップメッセージ表示件数
     */
    public void setPopupDisplayCount(Integer popupDisplayCount) {
        this.popupDisplayCount = popupDisplayCount;
    }

    /**
     * ポップアップメッセージ監視周期（分）を取得
     * @return watchPeriodForPopup
     */
    public Integer getWatchPeriodForPopup() {
        return watchPeriodForPopup;
    }

    /**
     * ポップアップメッセージ監視周期（分）を設定
     * @param watchPeriodForPopup ポップアップメッセージ監視周期（分）
     */
    public void setWatchPeriodForPopup(Integer watchPeriodForPopup) {
        this.watchPeriodForPopup = watchPeriodForPopup;
    }

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

}