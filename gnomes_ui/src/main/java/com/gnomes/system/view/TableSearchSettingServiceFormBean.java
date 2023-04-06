package com.gnomes.system.view;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.command.RequestParamMap;
import com.gnomes.common.command.RequestServiceFileInfo;
import com.gnomes.common.interceptor.ScreenInfo;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.common.view.ScreenIdConstants;
import com.gnomes.system.data.TableSearchSettingFunctionBean;

/**
 * テーブル検索条件管理サービス フォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                     -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("TableSearchSettingServiceFormBean")
@RequestScoped
public class TableSearchSettingServiceFormBean extends BaseFormBean implements Serializable, IServiceFormBean {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** TableSearchSettingFunctionBean */
    @Inject
    protected TableSearchSettingFunctionBean functionBean;

    /** コンピュータ名 */
    private String computerName;

    /** テーブルID */
    @Inject
    @RequestParamMap(value = "tableId")
    private String tableId;

    /** 設定種類 */
    @Inject
    @RequestParamMap(value = "settingType")
    private Integer settingType;

    /** 設定 */
    @Inject
    @RequestParamMap(value = "setting")
    private SearchSetting setting;


    /** 保存画面ID */
    @Inject
    @RequestParamMap(value = "saveScreenId")
    private String saveScreenId;

    /**
     * @return saveScreenId
     */
    public String getSaveScreenId() {
        return saveScreenId;
    }

    /**
     * @param saveScreenId セットする saveScreenId
     */
    public void setSaveScreenId(String saveScreenId) {
        this.saveScreenId = saveScreenId;
    }

    // カスタムタグの変更時の対応
    /**
     * ユーザロケールを取得
     * @return userLocale
     */
    @Override
    public Locale getUserLocale() {
        return null;
    }

    // カスタムタグの変更時の対応
    /**
     * ユーザロケールを設定
     * @param locale ユーザロケール
     */
    @Override
    public void setUserLocale(Locale locale) {

    }

    /**
     * cidを取得
     * @return cid
     */
    public String getCid() {
        return functionBean.getCid();
    }

    /**
     * インポートファイル取得値を取得
     * @return null
     */
    @Override
    public RequestServiceFileInfo getRequestServiceFileInfo() {
        return null;
    }

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
     * テーブルIDを取得
     * @return tableId
     */
    public String getTableId() {
        return this.tableId;
    }

    /**
     * テーブルIDを設定
     * @param tableId テーブルID
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    /**
     * 設定種類を取得
     * @return settingType
     */
    public Integer getSettingType() {
        return this.settingType;
    }

    /**
     * 設定種類を設定
     * @param settingType 設定種類
     */
    public void setSettingType(Integer settingType) {
        this.settingType = settingType;
    }

    /**
     * 設定を取得
     * @return setting
     */
    public SearchSetting getSetting() {
        return this.setting;
    }

    /**
     * 設定を設定
     * @param setting 設定
     */
    public void setSetting(SearchSetting setting) {
        this.setting = setting;
    }

    /**
     * 画面タイトルを取得
     * @return 画面タイトル
     */
    @Override
    public String getScreenTitle(){
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_Y99004);
    }

    /**
     * 画面IDを取得
     * @return 画面ID
     */
    @Override
    @ScreenInfo(name="screenId")
    public String getScreenId(){
        return ScreenIdConstants.SCID_Y99004;
    }

    /**
     * 画面名を取得
     * @return 画面名
     */
    @Override
    @ScreenInfo(name="screenName")
    public String getScreenName(){
        return ResourcesHandler.getString(GnomesResourcesConstants.SC01_Y99004);
    }

    /**
     * コンピュータ名を取得
     * @return computerName
     */
    @Override
    public String getComputerName() {
        return this.computerName;
    }

    /**
     * コンピュータ名を設定
     * @param computerName コンピュータ名
     */
    @Override
    public void setComputerName(String computerName) {
        this.computerName = computerName;
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

}
