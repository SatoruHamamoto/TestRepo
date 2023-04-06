package com.gnomes.system.view;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.command.RequestParam;
import com.gnomes.common.command.RequestParam.DataType;
import com.gnomes.common.dto.PullDownDto;
import com.gnomes.common.interceptor.ScreenInfo;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.common.view.ScreenIdConstants;
import com.gnomes.system.data.A01001FunctionBean;

/**
 * ログイン画面 フォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/22 YJP/Y.Oota                 初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("A01001FormBean")
@RequestScoped
public class A01001FormBean extends BaseFormBean implements  java.io.Serializable, IScreenFormBean {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    @Inject
    A01001FunctionBean functionBean;

    /** ユーザーID */
    private String userId;

    /** パスワード */
    private String password;

    /** 言語リスト */
    private List<PullDownDto> languageList;

    /** エリアリスト */
    private List<PullDownDto> areaList;

    /** 拠点リスト */
    private List<PullDownDto> siteList;

    /** 新規パスワード */
    private String newPassword;

    /** 新規確認パスワード */
    private String newConfirmPassword;

    /** セッション取得エラーメッセージ */
    private String errMessageNoSession;

    /** システムロケールID */
    private String systemLocale;

    /** ユーザ言語 */
    private Locale userLocale;

    /** ロケールリスト */
    private List<PullDownDto> localeList;

    /** 端末リスト */
    private List<PullDownDto> computerList;

    /** 端末拠点情報 */
    private Map<String, String> computerInfoMap;

    /** コンピュータ名 */
    @Inject
    @RequestParam("computerName")
    private String computerName;

    /** 拠点コード */
    private String siteCode;

    /** 入力項目チェックリスト */
    private Map<Integer,String> checkList;

    /**
     * 入力項目チェックリストを取得
     * @return checkList
     */
    @Override
    public Map<Integer,String> getCheckList() {
        return this.checkList;
    }

    /**
     * 入力項目チェックリストを設定
     * @param checkList 入力項目チェックリスト
     */
    @Override
    public void setCheckList(Map<Integer,String> checkList) {
        this.checkList = checkList;
    }

    /**
     * ユーザーIDを取得
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザーIDを設定
     * @param userId ユーザーID
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * 言語リストを取得
     * @return languageList
     */
    public List<PullDownDto> getLanguageList() {
        return languageList;
    }

    /**
     * 言語リストを設定
     * @param languageList 言語リスト
     */
    public void setLanguageList(List<PullDownDto> languageList) {
        this.languageList = languageList;
    }

    /**
     * エリアリストを取得
     * @return areaList
     */
    public List<PullDownDto> getAreaList() {
        return areaList;
    }

    /**
     * エリアリストを設定
     * @param areaList エリアリスト
     */
    public void setAreaList(List<PullDownDto> areaList) {
        this.areaList = areaList;
    }

    /**
     * 拠点リストを取得
     * @return siteList
     */
    public List<PullDownDto> getSiteList() {
        return siteList;
    }

    /**
     * 拠点リストを設定
     * @param siteList 拠点リスト
     */
    public void setSiteList(List<PullDownDto> siteList) {
        this.siteList = siteList;
    }

    /**
     * 新規パスワードを取得
     * @return newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 新規パスワードを設定
     * @param newPassword  新規パスワード
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * 新規確認パスワードを取得
     * @return newConfirmPassword
     */
    public String getNewConfirmPassword() {
        return newConfirmPassword;
    }

    /**
     * 新規確認パスワードを設定
     * @param newConfirmPassword 新規確認パスワード
     */
    public void setNewConfirmPassword(String newConfirmPassword) {
        this.newConfirmPassword = newConfirmPassword;
    }

    /** 初期処理 */
    @PostConstruct
    private void init() {
        // 以下の行は画面ID、画面名を CotainerRequest に設定するために必須のため削除しないこと
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this + " ScreenId: " + this.getScreenId() + "ScreenName: " + this.getScreenName());
    }

    /** 後処理 */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

    /**
    * 画面タイトルを取得
    */
    public String getScreenTitle(){
       return ResourcesHandler.getString(GnomesResourcesConstants.SC02_A01001);
       }

    /**
     * 画面IDを取得
     * @return screenId
     */
    @ScreenInfo(name="screenId")
    public String getScreenId() {
        return ScreenIdConstants.SCID_A01001;
    }

    /**
     * 画面名を取得
     * @return screenName
     */
    @ScreenInfo(name="screenName")
    public String getScreenName() {
        return ResourcesHandler.getString(GnomesResourcesConstants.SC01_A01001);
    }

    /**
     * セッション取得エラーメッセージを取得
     * @return errMessageNoSession
     */
    public String getErrMessageNoSession() {
        return errMessageNoSession;
    }

    /**
     * セッション取得エラーメッセージを設定
     * @param errMessageNoSession  セッション取得エラーメッセージ
     */
    public void setErrMessageNoSession(String errMessageNoSession) {
        this.errMessageNoSession = errMessageNoSession;
    }

    /**
     * システムロケールを取得
     * @return systemLocale
     */
    public String getSystemLocale() {
        return systemLocale;
    }

    /**
     * システムロケールを設定
     * @param systemLocale システムロケール
     */
    public void setSystemLocale(String systemLocale) {
        this.systemLocale = systemLocale;
    }

    /**
     * ユーザ言語を取得
     * @return userLocale
     */
    public Locale getUserLocale() {
        return this.userLocale;
    }

    /**
     * ユーザ言語を設定
     * @param userLocale ユーザ言語
     */
    public void setUserLocale(Locale locale) {
        this.userLocale = locale;
    }

    /**
     * ロケールリストを取得
     * @return localeList
     */
    public List<PullDownDto> getLocaleList() {
        return localeList;
    }

    /**
     * ロケールリストを設定
     * @param localeList ロケールリスト
     */
    public void setLocaleList(List<PullDownDto> localeList) {
        this.localeList = localeList;
    }

    /**
     * 端末リストを取得
     * @return computerList
     */
    public List<PullDownDto> getComputerList() {
        return computerList;
    }

    /**
     * 端末リストを設定
     * @param computerList 端末リスト
     */
    public void setComputerList(List<PullDownDto> computerList) {
        this.computerList = computerList;
    }

    /**
     * 端末拠点情報を取得
     * @return computerInfoMap
     */
    public Map<String, String> getComputerInfoMap() {
        return computerInfoMap;
    }

    /**
     * 端末拠点情報を設定
     * @param computerInfoMap 端末拠点情報
     */
    public void setComputerInfoMap(Map<String, String> computerInfoMap) {
        this.computerInfoMap = computerInfoMap;
    }

    /**
     * コンピュータ名を取得
     * @return computerName
     */
    @Override
    public String getComputerName() {
        return  this.computerName;
    }

    /**
     * コンピュータ名を設定
     * @param computerName コンピュータ名
     */
    @Override
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

    // 保存済みの検索条件
    @Inject
    @RequestParam(value = "jsonSaveSearchInfos", dataType = DataType.JSON)
    private String jsonSaveSearchInfos;

    public String getJsonSaveSearchInfos() {
        return jsonSaveSearchInfos;
    }

    /**
     * FunctionBean→FormBean マッピング
     * @param functionBean ファンクションビーン
     */
    public void setFormBean(A01001FunctionBean functionBean) {

        // 拠点リスト
        this.siteList = functionBean.getSiteList();

        // セッション取得エラーメッセージ
        this.errMessageNoSession = functionBean.getErrMessageNoSession();

        // システムロケールID
        this.systemLocale = functionBean.getSystemLocale();

        // ロケールリスト
        this.localeList = functionBean.getLocaleList();

        // 端末リスト
        this.computerList = functionBean.getComputerList();

        // 端末拠点情報
        this.computerInfoMap = functionBean.getComputerInfoMap();
    }

}