package com.gnomes.rest.service;

import java.util.List;
import java.util.Locale;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.uiservice.ContainerRequest;

/**
 *
 * JAX-RS Web サービス  共通パラメータ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class BaseServiceParam {

    /** イベントID */
    private String eventId;

    /** ユーザKey */
    private String userKey;

    /** ユーザID */
    private String userId;

    /** ユーザ名 */
    private String userName;

    /** ユーザロケール */
    private Locale userLocale;

    /** 言語 */
    private String language;

    /** IPアドレス */
    private String ipAddress;

    /** コンピュータ名 */
    private String computerName;

    /** エリアID */
    private String areaId;

    /** エリア名 */
    private String areaName;

    /** 拠点ID */
    private String siteCode;

    /** 拠点名 */
    private String siteName;

    /** 画面名 */
    private String screenId;

    /** 画面名 */
    private String screenName;

    /** コマンドID */
    private String commandId;

    /** トレースクラス名 */
    private List<String> traceClazzNameList;

    /** トレースメソッド名 */
    private List<String> traceMethodNameList;

    // sessionBean start -----
    /** 領域区分 */
    private String regionType;

    /**
     * イベントIDを取得
     * @return イベントID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * イベントIDを設定
     * @param eventId イベントID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

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
     * ユーザロケールを取得
     * @return locale
     */
    public Locale getUserLocale() {
        return userLocale;
    }

    /**
     * ユーザロケールを設定
     * @param userLocale ユーザロケール
     */
    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
    }

    /**
     * 言語を取得
     * @return language
     */
    public String getLanguage() {
        return language;
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
     * コンピュータ名を取得
     * @return computerName
     */
    public String getComputerName() {
        return computerName;
    }

    /**
     * コンピュータ名を設定
     * @param computerName コンピュータ名
     */
    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    /**
     * エリアIDを取得
     * @return areaId
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * エリアIDを設定
     * @param areaId エリアID
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    /**
     * エリア名を取得
     * @return areaName
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * エリア名を設定
     * @param areaName エリア名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * 拠点IDを取得
     * @return siteCode
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * 拠点IDを設定
     * @param siteCode 拠点ID
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
     * 画面IDを取得
     * @return screenId
     */
    public String getScreenId() {
        return screenId;
    }

    /**
     * 画面IDを設定
     * @param screenId
     */
    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    /**
     * 画面名を取得
     * @return screenName
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * 画面名を設定
     * @param screenName
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * コマンドIDを取得
     * @return commandId
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * コマンドIDを設定
     * @param commandId 拠点名
     */
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    /**
     * トレースクラス名を取得
     * @return トレースクラス名
     */
    public List<String> getTraceClazzNameList() {
        return traceClazzNameList;
    }

    /**
     * トレースクラス名を設定
     * @param traceClazzNameList トレースクラス名
     */
    public void setTraceClazzNameList(List<String> traceClazzNameList) {
        this.traceClazzNameList = traceClazzNameList;
    }

    /**
     * トレースメソッド名を取得
     * @return トレースメソッド名
     */
    public List<String> getTraceMethodNameList() {
        return traceMethodNameList;
    }

    /**
     * トレースメソッド名を設定
     * @param traceMethodNameList トレースメソッド名
     */
    public void setTraceMethodNameList(List<String> traceMethodNameList) {
        this.traceMethodNameList = traceMethodNameList;
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
     * リクエスト情報を保持
     * @param req ContainerRequest
     *
     */
    public void setRequest(ContainerRequest req) {

        this.setEventId(req.getEventId());                         // イベントID
        this.setAreaId(req.getAreaId());                           // エリアID
        this.setAreaName(req.getAreaName());                       // エリア名
        this.setComputerName(req.getComputerName());               // コンピュータ名
        this.setIpAddress(req.getIpAddress());                     // IPアドレス
        this.setLanguage(req.getLanguage());                       // 言語
        this.setUserLocale(req.getUserLocale());                   // ユーザロケール
        this.setSiteCode(req.getSiteCode());                       // 拠点ID
        this.setSiteName(req.getSiteName());                       // 拠点名
        this.setUserId(req.getUserId());                           // ユーザID
        this.setUserName(req.getUserName());                       // ユーザ名
        this.setUserKey(req.getUserKey());                         // ユーザKey
        this.setScreenId(req.getScreenId());                       // 画面ID
        this.setScreenName(req.getScreenName());                   // 画面名
        this.setCommandId(req.getCommandId());                     // コマンドID
        this.setTraceClazzNameList(req.getTraceClazzNameList());   // トレースクラス名
        this.setTraceMethodNameList(req.getTraceMethodNameList()); // トレースメソッド名

    }

    /**
     * セッション情報を保持
     * @param gnomesSessionBean セッションビーン
     */
    public void setSessionBean(GnomesSessionBean gnomesSessionBean) {

        this.setRegionType(gnomesSessionBean.getRegionType());     // 領域区分

    }

}
