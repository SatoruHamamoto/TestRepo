package com.gnomes.system.data;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import com.gnomes.common.command.RequestParamMap;
import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.search.data.SearchSetting;

/**
 * ファンクションビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                     -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@ConversationScoped
public class TableSearchSettingFunctionBean extends BaseFunctionBean {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** テーブルID */
    private String tableId;

    /** 設定種類 */
    private Integer settingType;

    /** 設定 */
    private SearchSetting setting;

    /** 保存画面ID */
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

    /**
     * テーブルIDを取得
     * @return テーブルID
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
     * @return 設定種類
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
     * @return 設定
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

    /**
     * クリア処理
     */
    @Override
    public void clear() {
        super.clear();
        this.tableId = null;
        this.settingType = null;
        this.setting = null;
    }

}
