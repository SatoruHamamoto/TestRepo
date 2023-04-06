package com.gnomes.common.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.command.RequestParam;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.data.IScreenPrivilegeBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * 画面共通項目用 フォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/22 YJP/Y.Oota                 初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("SystemFormBean")
@RequestScoped
public class SystemFormBean implements java.io.Serializable, IScreenPrivilegeBean{

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    /**
     * WindowId
     */
    @Inject
    @RequestParam("windowId")
    private String windowId;

    /** ログインユーザID */
    @Inject
    @RequestParam("loginUserId")
    private String loginUserId;

    /** ログインユーザパスワード */
    @Inject
    @RequestParam("loginUserPassword")
    private String loginUserPassword;

    /** 認証者ユーザID */
    @Inject
    @RequestParam("certUserId")
    private String certUserId;

    /** 認証者ユーザパスワード */
    @Inject
    @RequestParam("certUserPassword")
    private String certUserPassword;

    /** 認証者ユーザID(参照用) */
    private String checkedCertUserId;

    /** 代替者ユーザID */
    @Inject
    @RequestParam("substituteUserId")
    private String substituteUserId;

    /** 代替者ユーザパスワード */
    @Inject
    @RequestParam("substituteUserPassword")
    private String substituteUserPassword;

    /** ボタンID */
    @Inject
    @RequestParam("buttonId")
    private String buttonId;

    /** ボタン名 */
    private String buttonName;

    /** 代替フラグ */
    @Inject
    @RequestParam("substituteFlag")
    private String substituteFlag;

    /** 工程コード */
    private List<String> processCode = null;

    /** 拠点コード */
    private List<String> siteCode = null;

    /** 指図工程コード */
    private List<String> orderProcessCode = null;

    /** 作業工程コード */
    private List<String> workProcessCode = null;

    /** パーツ権限結果情報 */
    private List<PartsPrivilegeResultInfo> partsPrivilegeResultInfo = null;

    /** ダブル認証か否か */
    @Inject
    @RequestParam("isDoubleCheck")
    private String isDoubleCheck;

    /** スクロール位置保持有無 */
    @Inject
    @RequestParam("keepScrollPosition")
    private String keepScrollPosition;
    
    /** 画面遷移時パラメータ */
    @Inject
    @RequestParam("screenTransitionMode")
    private String screenTransitionMode; 

    /** 初期処理 */
    @PostConstruct
    private void init() {
        // 以下の行は画面ID、画面名を CotainerRequest に設定するために必須のため削除しないこと
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /** 後処理 */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

    /**
     * ウインドウIdを取得する
     *
     * @return ウインドウId
     */
    public String getWindowId(){
        return this.windowId;
    }
    /**
     * ウインドウIDを設定する
     * @param windowId  ウインドウId
     */
    public void setWindowId(String windowId){
        this.windowId = windowId;
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
	 * 認証者ユーザID(参照用)を取得
	 * @return isDoubleCheck
	 */
	public String getCheckedCertUserId() {
		return checkedCertUserId;
	}

	/**
	 * 認証者ユーザID(参照用)を設定
	 * @param isDoubleCheck 認証者ユーザID(参照用)
	 */
	public void setCheckedCertUserId(String checkedCertUserId) {
		this.checkedCertUserId = checkedCertUserId;
	}

	/**
	 * 代替ユーザIDを取得
	 * @return substituteUserId
	 */
	public String getSubstituteUserId() {
		return substituteUserId;
	}

	/**
	 * 代替ユーザIDを設定
	 * @param substituteUserId 代替ユーザID
	 */
	public void setSubstituteUserId(String substituteUserId) {
		this.substituteUserId = substituteUserId;
	}

	/**
	 * 代替ユーザパスワードを取得
	 * @return substituteUserPassword
	 */
	public String getSubstituteUserPassword() {
		return substituteUserPassword;
	}

	/**
	 * 代替ユーザパスワードを設定
	 * @param substituteUserPassword 代替ユーザパスワード
	 */
	public void setSubstituteUserPassword(String substituteUserPassword) {
		this.substituteUserPassword = substituteUserPassword;
	}

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
     * 代替フラグを取得
     * @return substituteFlag ボ代替フラグ
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
	 * 工程コードを取得
	 * @return userLocale
	 */
    @Override
	public List<String> getProcessCode() {
		return processCode;
	}

	/**
	 * 工程コードを設定
	 * @param processCode 工程コード
	 */
	public void setProcessCode(List<String> processCode) {
		this.processCode = processCode;
	}

    /**
     * 拠点コードを取得
     * @return null
     */
	@Override
	public List<String> getSiteCode() {
		return this.siteCode;
	}

	/**
	 * 拠点コードを設定
	 * @param siteCode 拠点コード
	 */
	public void setSiteCode(List<String> siteCode) {
		this.siteCode = siteCode;
	}

    /**
     * 指図工程コードを取得
     * @return null
     */
	@Override
	public List<String> getOrderProcessCode() {
		return this.orderProcessCode;
	}

	/**
	 * 指図工程コードを設定
	 * @param orderProcessCode 指図工程コード
	 */
	public void setOrderProcessCode(List<String> orderProcessCode) {
		this.orderProcessCode = orderProcessCode;
	}

    /**
     * 作業工程コードを取得
     * @return null
     */
	@Override
	public List<String> getWorkProcessCode() {
		return this.workProcessCode;
	}

	/**
	 * 作業工程コードを設定
	 * @param workProcessCode 作業工程コード
	 */
	public void setWorkProcessCode(List<String> workProcessCode) {
		this.workProcessCode = workProcessCode;
	}

	/**
	 * パーツ権限結果情報を取得
	 * @return partsPrivilegeResultInfo
	 */
	@Override
	public List<PartsPrivilegeResultInfo> getPartsPrivilegeResultInfo() {
		return this.partsPrivilegeResultInfo;
	}

	/**
	 * パーツ権限結果情報を設定
	 * @param partsPrivilegeResultInfo パーツ権限結果情報
	 */
	@Override
	public void setPartsPrivilegeResultInfo(List<PartsPrivilegeResultInfo> partsPrivilegeResultInfo) {
		this.partsPrivilegeResultInfo = partsPrivilegeResultInfo;
	}

	/**
	 * ダブル認証か否かを取得
	 * @return isDoubleCheck
	 */
	public String getIsDoubleCheck() {
		return isDoubleCheck;
	}

	/**
	 * ダブル認証か否かを設定
	 * @param isDoubleCheck ダブル認証か否か
	 */
	public void setIsDoubleCheck(String isDoubleCheck) {
		this.isDoubleCheck = isDoubleCheck;
	}

	/**
	 * スクロール位置保持有無 を取得
	 * @return keepScrollPosition
	 */
	public String getKeepScrollPosition() {
		return keepScrollPosition;
	}

	/**
	 * スクロール位置保持有無 を設定
	 * @param keepScrollPosition スクロール位置保持有無
	 */
	public void setKeepScrollPosition(String keepScrollPosition) {
		this.keepScrollPosition = keepScrollPosition;
	}
	
	/**
	 * 画面遷移パラメータ を取得
	 * @return screenTransitionMode
	 */
	public String getScreenTransitionMode() {
		return screenTransitionMode;
	}

	/**
	 * 画面遷移パラメータ を設定
	 * @param screenTransitionMode 画面遷移パラメータ
	 */
	public void setScreenTransitionMode(String screenTransitionMode) {
		this.screenTransitionMode = screenTransitionMode;
	}

	/**
	 * 初期化処理
	 */
    public void clear(){
    	this.loginUserId = null;
    	this.loginUserPassword = null;
    	this.certUserId = null;
    	this.certUserPassword = null;
    	this.checkedCertUserId = null;
    	this.substituteUserId = null;
    	this.substituteUserPassword = null;
    	this.buttonId = null;
    	this.substituteFlag = null;
    	this.processCode = null;
    	this.siteCode = null;
    	this.orderProcessCode = null;
    	this.workProcessCode = null;
    	this.partsPrivilegeResultInfo = new ArrayList<>();
    	this.isDoubleCheck = null;
    	this.keepScrollPosition = null;
    	this.screenTransitionMode = null;

    }

}