package com.gnomes.system.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zm101拠点マスタ エンティティ
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
@Table(name = "mstr_site")
@NamedQueries({
        @NamedQuery(name = "MstrSite.findAll", query = "SELECT p FROM MstrSite p"),
        @NamedQuery(name = "MstrSite.findByPK", query = "SELECT p FROM MstrSite p WHERE p.site_key = :site_key")
})
public class MstrSite extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_site";

    /** 拠点キー */
    public static final String COLUMN_NAME_SITE_KEY = "site_key";

    /** nk拠点コード */
    public static final String COLUMN_NAME_SITE_CODE = "site_code";

    /** 拠点名 */
    public static final String COLUMN_NAME_SITE_NAME = "site_name";

    /** 企業コード */
    public static final String COLUMN_NAME_ENTERPRISE_CODE = "enterprise_code";

    /** 拠点キー */
    private String site_key;
    /** nk拠点コード */
    private String site_code;
    /** 拠点名 */
    private String site_name;
    /** 企業コード */
    private String enterprise_code;


    /**
     * Zm101拠点マスタエンティティ コンストラクタ
     */
    public MstrSite() {
    }

    /**
     * Zm101拠点マスタエンティティ コンストラクタ
     * @param site_key 拠点キー
     * @param site_code nk拠点コード
     * @param enterprise_code 企業コード
     * @param version 更新バージョン
     */
    public MstrSite(String site_key, String site_code, String enterprise_code, int version) {
        this.site_key = site_key;
        this.site_code = site_code;
        this.enterprise_code = enterprise_code;
        super.setVersion(version);
    }

    /**
     * Zm101拠点マスタエンティティ コンストラクタ
     * @param site_key 拠点キー
     * @param site_code nk拠点コード
     * @param site_name 拠点名
     * @param enterprise_code 企業コード
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
    public MstrSite(String site_key, String site_code, String site_name, String enterprise_code, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.site_key = site_key;
        this.site_code = site_code;
        this.site_name = site_name;
        this.enterprise_code = enterprise_code;
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
     * 拠点キーを取得
     * @return 拠点キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getSite_key() {
        return this.site_key;
    }

    /**
     * 拠点キーを設定
     * @param site_key 拠点キー (null不可)
     */
    public void setSite_key(String site_key) {
        this.site_key = site_key;
    }

    /**
     * nk拠点コードを取得
     * @return nk拠点コード
     */
    @Column(nullable = false, length = 20)
    public String getSite_code() {
        return this.site_code;
    }

    /**
     * nk拠点コードを設定
     * @param site_code nk拠点コード (null不可)
     */
    public void setSite_code(String site_code) {
        this.site_code = site_code;
    }

    /**
     * 拠点名を取得
     * @return 拠点名
     */
    @Column(length = 30)
    public String getSite_name() {
        return this.site_name;
    }

    /**
     * 拠点名を設定
     * @param site_name 拠点名
     */
    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    /**
     * 企業コードを取得
     * @return 企業コード
     */
    @Column(nullable = false, length = 20)
    public String getEnterprise_code() {
        return this.enterprise_code;
    }

    /**
     * 企業コードを設定
     * @param enterprise_code 企業コード (null不可)
     */
    public void setEnterprise_code(String enterprise_code) {
        this.enterprise_code = enterprise_code;
    }

}
