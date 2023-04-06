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
 * Zi002端末情報 エンティティ
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
@Table(name = "info_computer")
@NamedQueries({
        @NamedQuery(name = "InfoComputer.findAll", query = "SELECT p FROM InfoComputer p"),
        @NamedQuery(name = "InfoComputer.findByPK", query = "SELECT p FROM InfoComputer p WHERE p.info_computer_key = :info_computer_key")
})
public class InfoComputer extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "info_computer";

    /** 端末情報キー */
    public static final String COLUMN_NAME_INFO_COMPUTER_KEY = "info_computer_key";

    /** nk端末ID */
    public static final String COLUMN_NAME_COMPUTER_ID = "computer_id";

    /** 拠点コード */
    public static final String COLUMN_NAME_SITE_CODE = "site_code";

    /** 端末情報キー */
    private String info_computer_key;
    /** nk端末ID */
    private String computer_id;
    /** 拠点コード */
    private String site_code;


    /**
     * Zi002端末情報エンティティ コンストラクタ
     */
    public InfoComputer() {
    }

    /**
     * Zi002端末情報エンティティ コンストラクタ
     * @param info_computer_key 端末情報キー
     * @param computer_id nk端末ID
     * @param site_code 拠点コード
     * @param version 更新バージョン
     */
    public InfoComputer(String info_computer_key, String computer_id, String site_code, int version) {
        this.info_computer_key = info_computer_key;
        this.computer_id = computer_id;
        this.site_code = site_code;
        super.setVersion(version);
    }

    /**
     * Zi002端末情報エンティティ コンストラクタ
     * @param info_computer_key 端末情報キー
     * @param computer_id nk端末ID
     * @param site_code 拠点コード
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
    public InfoComputer(String info_computer_key, String computer_id, String site_code, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.info_computer_key = info_computer_key;
        this.computer_id = computer_id;
        this.site_code = site_code;
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
     * 端末情報キーを取得
     * @return 端末情報キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getInfo_computer_key() {
        return this.info_computer_key;
    }

    /**
     * 端末情報キーを設定
     * @param info_computer_key 端末情報キー (null不可)
     */
    public void setInfo_computer_key(String info_computer_key) {
        this.info_computer_key = info_computer_key;
    }

    /**
     * nk端末IDを取得
     * @return nk端末ID
     */
    @Column(nullable = false, length = 20)
    public String getComputer_id() {
        return this.computer_id;
    }

    /**
     * nk端末IDを設定
     * @param computer_id nk端末ID (null不可)
     */
    public void setComputer_id(String computer_id) {
        this.computer_id = computer_id;
    }

    /**
     * 拠点コードを取得
     * @return 拠点コード
     */
    @Column(nullable = false, length = 20)
    public String getSite_code() {
        return this.site_code;
    }

    /**
     * 拠点コードを設定
     * @param site_code 拠点コード (null不可)
     */
    public void setSite_code(String site_code) {
        this.site_code = site_code;
    }

}
