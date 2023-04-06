package com.gnomes.system.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi003テーブル検索条件管理 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/10/03 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "table_search_setting")
@NamedQueries({
        @NamedQuery(name = "TableSearchSetting.findAll", query = "SELECT p FROM TableSearchSetting p"),
        @NamedQuery(name = "TableSearchSetting.findByPK", query = "SELECT p FROM TableSearchSetting p WHERE p.table_search_setting_key = :table_search_setting_key")
})
public class TableSearchSetting extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "table_search_setting";

    /** テーブル検索条件管理キー */
    public static final String COLUMN_NAME_TABLE_SEARCH_SETTING_KEY = "table_search_setting_key";

    /** nkユーザID */
    public static final String COLUMN_NAME_USER_ID = "user_id";

    /** nkテーブルID */
    public static final String COLUMN_NAME_TABLE_ID = "table_id";

    /** nk設定種類 */
    public static final String COLUMN_NAME_SETTING_TYPE = "setting_type";

    /** 設定 */
    public static final String COLUMN_NAME_SETTING = "setting";

    /** テーブル検索条件管理キー */
    private String table_search_setting_key;
    /** nkユーザID */
    private String user_id;
    /** nkテーブルID */
    private String table_id;
    /** nk設定種類 */
    private int setting_type;
    /** 設定 */
    private String setting;


    /**
     * Zi003テーブル検索条件管理エンティティ コンストラクタ
     */
    public TableSearchSetting() {
    }

    /**
     * Zi003テーブル検索条件管理エンティティ コンストラクタ
     * @param table_search_setting_key テーブル検索条件管理キー
     * @param user_id nkユーザID
     * @param table_id nkテーブルID
     * @param setting_type nk設定種類
     * @param setting 設定
     * @param version 更新バージョン
     */
    public TableSearchSetting(String table_search_setting_key, String user_id, String table_id, int setting_type, String setting, int version) {
        this.table_search_setting_key = table_search_setting_key;
        this.user_id = user_id;
        this.table_id = table_id;
        this.setting_type = setting_type;
        this.setting = setting;
        super.setVersion(version);
    }

    /**
     * Zi003テーブル検索条件管理エンティティ コンストラクタ
     * @param table_search_setting_key テーブル検索条件管理キー
     * @param user_id nkユーザID
     * @param table_id nkテーブルID
     * @param setting_type nk設定種類
     * @param setting 設定
     * @param first_regist_event_id 登録イベントID
     * @param first_regist_user_number 登録従業員No
     * @param first_regist_user_name 登録従業員名
     * @param first_regist_datetime 登録日時
     * @param last_regist_event_id 更新イベントID
     * @param last_regist_user_number 更新従業員No
     * @param last_regist_user_name 更新従業員名
     * @param last_regist_datetime 更新日時
     * @param version 更新バージョン
     */
    public TableSearchSetting(String table_search_setting_key, String user_id, String table_id, int setting_type, String setting, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.table_search_setting_key = table_search_setting_key;
        this.user_id = user_id;
        this.table_id = table_id;
        this.setting_type = setting_type;
        this.setting = setting;
        super.setFirst_regist_event_id(first_regist_event_id);
        super.setFirst_regist_user_number(first_regist_user_number);
        super.setFirst_regist_user_name(first_regist_user_name);
        super.setFirst_regist_datetime(first_regist_datetime);
        super.setLast_regist_event_id(last_regist_event_id);
        super.setLast_regist_user_number(last_regist_user_number);
        super.setLast_regist_user_name(last_regist_user_name);
        super.setLast_regist_datetime(last_regist_datetime);
        super.setVersion(version);
    }

    /**
     * テーブル検索条件管理キーを取得
     * @return テーブル検索条件管理キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getTable_search_setting_key() {
        return this.table_search_setting_key;
    }

    /**
     * テーブル検索条件管理キーを設定
     * @param table_search_setting_key テーブル検索条件管理キー (null不可)
     */
    public void setTable_search_setting_key(String table_search_setting_key) {
        this.table_search_setting_key = table_search_setting_key;
    }

    /**
     * nkユーザIDを取得
     * @return nkユーザID
     */
    @Column(nullable = false, length = 20)
    public String getUser_id() {
        return this.user_id;
    }

    /**
     * nkユーザIDを設定
     * @param user_id nkユーザID (null不可)
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * nkテーブルIDを取得
     * @return nkテーブルID
     */
    @Column(nullable = false, length = 40)
    public String getTable_id() {
        return this.table_id;
    }

    /**
     * nkテーブルIDを設定
     * @param table_id nkテーブルID (null不可)
     */
    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    /**
     * nk設定種類を取得
     * @return nk設定種類
     */
    @Column(nullable = false, length = 1)
    public int getSetting_type() {
        return this.setting_type;
    }

    /**
     * nk設定種類を設定
     * @param setting_type nk設定種類 (null不可)
     */
    public void setSetting_type(int setting_type) {
        this.setting_type = setting_type;
    }

    /**
     * 設定を取得
     * @return 設定
     */
    @Column(nullable = false, length = 4000)
    @Lob
    public String getSetting() {
        return this.setting;
    }

    /**
     * 設定を設定
     * @param setting 設定 (null不可)
     */
    public void setSetting(String setting) {
        this.setting = setting;
    }

}
