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
 * Zi005端末工程作業場所選択情報 エンティティ
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
@Table(name = "info_computer_proc_workcell")
@NamedQueries({
        @NamedQuery(name = "InfoComputerProcWorkcell.findAll", query = "SELECT p FROM InfoComputerProcWorkcell p"),
        @NamedQuery(name = "InfoComputerProcWorkcell.findByPK", query = "SELECT p FROM InfoComputerProcWorkcell p WHERE p.info_computer_proc_workcell_key = :info_computer_proc_workcell_key")
})
public class InfoComputerProcWorkcell extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "info_computer_proc_workcell";

    /** 端末工程作業場所選択情報キー */
    public static final String COLUMN_NAME_INFO_COMPUTER_PROC_WORKCELL_KEY = "info_computer_proc_workcell_key";

    /** nk端末ID */
    public static final String COLUMN_NAME_CLIENT_DEVICE_ID = "client_device_id";

    /** nk拠点コード */
    public static final String COLUMN_NAME_SITE_CODE = "site_code";

    /** 指図工程コード */
    public static final String COLUMN_NAME_PROCESS_CODE = "process_code";

    /** 作業工程コード */
    public static final String COLUMN_NAME_WORKPROCESS_CODE = "workprocess_code";

    /** 作業場所コード */
    public static final String COLUMN_NAME_WORKCELL_CODE = "workcell_code";

    /** 端末工程作業場所選択情報キー */
    private String info_computer_proc_workcell_key;
    /** nk端末ID */
    private String client_device_id;
    /** nk拠点コード */
    private String site_code;
    /** 指図工程コード */
    private String process_code;
    /** 作業工程コード */
    private String workprocess_code;
    /** 作業場所コード */
    private String workcell_code;


    /**
     * Zi005端末工程作業場所選択情報エンティティ コンストラクタ
     */
    public InfoComputerProcWorkcell() {
    }

    /**
     * Zi005端末工程作業場所選択情報エンティティ コンストラクタ
     * @param info_computer_proc_workcell_key 端末工程作業場所選択情報キー
     * @param client_device_id nk端末ID
     * @param site_code nk拠点コード
     * @param version 更新バージョン
     */
    public InfoComputerProcWorkcell(String info_computer_proc_workcell_key, String client_device_id, String site_code, int version) {
        this.info_computer_proc_workcell_key = info_computer_proc_workcell_key;
        this.client_device_id = client_device_id;
        this.site_code = site_code;
        super.setVersion(version);
    }

    /**
     * Zi005端末工程作業場所選択情報エンティティ コンストラクタ
     * @param info_computer_proc_workcell_key 端末工程作業場所選択情報キー
     * @param client_device_id nk端末ID
     * @param site_code nk拠点コード
     * @param process_code 指図工程コード
     * @param workprocess_code 作業工程コード
     * @param workcell_code 作業場所コード
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
    public InfoComputerProcWorkcell(String info_computer_proc_workcell_key, String client_device_id, String site_code, String process_code, String workprocess_code, String workcell_code, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.info_computer_proc_workcell_key = info_computer_proc_workcell_key;
        this.client_device_id = client_device_id;
        this.site_code = site_code;
        this.process_code = process_code;
        this.workprocess_code = workprocess_code;
        this.workcell_code = workcell_code;
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
     * 端末工程作業場所選択情報キーを取得
     * @return 端末工程作業場所選択情報キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getInfo_computer_proc_workcell_key() {
        return this.info_computer_proc_workcell_key;
    }

    /**
     * 端末工程作業場所選択情報キーを設定
     * @param info_computer_proc_workcell_key 端末工程作業場所選択情報キー (null不可)
     */
    public void setInfo_computer_proc_workcell_key(String info_computer_proc_workcell_key) {
        this.info_computer_proc_workcell_key = info_computer_proc_workcell_key;
    }

    /**
     * nk端末IDを取得
     * @return nk端末ID
     */
    @Column(nullable = false, length = 20)
    public String getClient_device_id() {
        return this.client_device_id;
    }

    /**
     * nk端末IDを設定
     * @param client_device_id nk端末ID (null不可)
     */
    public void setClient_device_id(String client_device_id) {
        this.client_device_id = client_device_id;
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
     * 指図工程コードを取得
     * @return 指図工程コード
     */
    @Column(length = 20)
    public String getProcess_code() {
        return this.process_code;
    }

    /**
     * 指図工程コードを設定
     * @param process_code 指図工程コード
     */
    public void setProcess_code(String process_code) {
        this.process_code = process_code;
    }

    /**
     * 作業工程コードを取得
     * @return 作業工程コード
     */
    @Column(length = 20)
    public String getWorkprocess_code() {
        return this.workprocess_code;
    }

    /**
     * 作業工程コードを設定
     * @param workprocess_code 作業工程コード
     */
    public void setWorkprocess_code(String workprocess_code) {
        this.workprocess_code = workprocess_code;
    }

    /**
     * 作業場所コードを取得
     * @return 作業場所コード
     */
    @Column(length = 20)
    public String getWorkcell_code() {
        return this.workcell_code;
    }

    /**
     * 作業場所コードを設定
     * @param workcell_code 作業場所コード
     */
    public void setWorkcell_code(String workcell_code) {
        this.workcell_code = workcell_code;
    }

}
