package com.gnomes.system.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zh002パスワード変更履歴 エンティティ
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
@Table(name = "hist_change_password")
@NamedQueries({
        @NamedQuery(name = "HistChangePassword.findAll", query = "SELECT p FROM HistChangePassword p"),
        @NamedQuery(name = "HistChangePassword.findByPK", query = "SELECT p FROM HistChangePassword p WHERE p.hist_change_password_key = :hist_change_password_key")
})
public class HistChangePassword extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "hist_change_password";

    /** パスワード変更履歴キー */
    public static final String COLUMN_NAME_HIST_CHANGE_PASSWORD_KEY = "hist_change_password_key";

    /** 発生日時 */
    public static final String COLUMN_NAME_OCCUR_DATETIME = "occur_datetime";

    /** 従業員No */
    public static final String COLUMN_NAME_USER_NUMBER = "user_number";

    /** 端末ID */
    public static final String COLUMN_NAME_CLIENT_DEVICE_ID = "client_device_id";

    /** 操作内容 */
    public static final String COLUMN_NAME_OPERATION_CONTENT = "operation_content";

    /** 従業員名 */
    public static final String COLUMN_NAME_USER_NAME = "user_name";

    /** 端末名 */
    public static final String COLUMN_NAME_CLIENT_DEVICE_NAME = "client_device_name";

    /** IPアドレス */
    public static final String COLUMN_NAME_IP_ADDRESS = "ip_address";

    /** 変更NGフラグ */
    public static final String COLUMN_NAME_NG_FLAG = "ng_flag";

    /** NG詳細情報 */
    public static final String COLUMN_NAME_NG_DETAIL = "ng_detail";

    /** パスワード変更履歴キー */
    private String hist_change_password_key;
    /** 発生日時 */
    private Date occur_datetime;
    /** 従業員No */
    private String user_number;
    /** 端末ID */
    private String client_device_id;
    /** 操作内容 */
    private String operation_content;
    /** 従業員名 */
    private String user_name;
    /** 端末名 */
    private String client_device_name;
    /** IPアドレス */
    private String ip_address;
    /** 変更NGフラグ */
    private Integer ng_flag;
    /** NG詳細情報 */
    private String ng_detail;

    /** Zh003パスワード変更履歴詳細 */
    private Set<HistChangePasswordDetail> histChangePasswordDetails;

    /**
     * Zh002パスワード変更履歴エンティティ コンストラクタ
     */
    public HistChangePassword() {
    }

    /**
     * Zh002パスワード変更履歴エンティティ コンストラクタ
     * @param hist_change_password_key パスワード変更履歴キー
     * @param occur_datetime 発生日時
     * @param user_number 従業員No
     * @param client_device_id 端末ID
     * @param operation_content 操作内容
     * @param version 更新バージョン
     */
    public HistChangePassword(String hist_change_password_key, Date occur_datetime, String user_number, String client_device_id, String operation_content, int version) {
        this.hist_change_password_key = hist_change_password_key;
        this.occur_datetime = occur_datetime;
        this.user_number = user_number;
        this.client_device_id = client_device_id;
        this.operation_content = operation_content;
        super.setVersion(version);
    }

    /**
     * Zh002パスワード変更履歴エンティティ コンストラクタ
     * @param hist_change_password_key パスワード変更履歴キー
     * @param occur_datetime 発生日時
     * @param user_number 従業員No
     * @param client_device_id 端末ID
     * @param operation_content 操作内容
     * @param user_name 従業員名
     * @param client_device_name 端末名
     * @param ip_address IPアドレス
     * @param ng_flag 変更NGフラグ
     * @param ng_detail NG詳細情報
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
    public HistChangePassword(String hist_change_password_key, Date occur_datetime, String user_number, String client_device_id, String operation_content, String user_name, String client_device_name, String ip_address, Integer ng_flag, String ng_detail, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.hist_change_password_key = hist_change_password_key;
        this.occur_datetime = occur_datetime;
        this.user_number = user_number;
        this.client_device_id = client_device_id;
        this.operation_content = operation_content;
        this.user_name = user_name;
        this.client_device_name = client_device_name;
        this.ip_address = ip_address;
        this.ng_flag = ng_flag;
        this.ng_detail = ng_detail;
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
     * パスワード変更履歴キーを取得
     * @return パスワード変更履歴キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getHist_change_password_key() {
        return this.hist_change_password_key;
    }

    /**
     * パスワード変更履歴キーを設定
     * @param hist_change_password_key パスワード変更履歴キー (null不可)
     */
    public void setHist_change_password_key(String hist_change_password_key) {
        this.hist_change_password_key = hist_change_password_key;
    }

    /**
     * 発生日時を取得
     * @return 発生日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getOccur_datetime() {
        return this.occur_datetime;
    }

    /**
     * 発生日時を設定
     * @param occur_datetime 発生日時 (null不可)
     */
    public void setOccur_datetime(Date occur_datetime) {
        this.occur_datetime = occur_datetime;
    }

    /**
     * 従業員Noを取得
     * @return 従業員No
     */
    @Column(nullable = false, length = 20)
    public String getUser_number() {
        return this.user_number;
    }

    /**
     * 従業員Noを設定
     * @param user_number 従業員No (null不可)
     */
    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    /**
     * 端末IDを取得
     * @return 端末ID
     */
    @Column(nullable = false, length = 20)
    public String getClient_device_id() {
        return this.client_device_id;
    }

    /**
     * 端末IDを設定
     * @param client_device_id 端末ID (null不可)
     */
    public void setClient_device_id(String client_device_id) {
        this.client_device_id = client_device_id;
    }

    /**
     * 操作内容を取得
     * @return 操作内容
     */
    @Column(nullable = false, length = 100)
    public String getOperation_content() {
        return this.operation_content;
    }

    /**
     * 操作内容を設定
     * @param operation_content 操作内容 (null不可)
     */
    public void setOperation_content(String operation_content) {
        this.operation_content = operation_content;
    }

    /**
     * 従業員名を取得
     * @return 従業員名
     */
    @Column(length = 40)
    public String getUser_name() {
        return this.user_name;
    }

    /**
     * 従業員名を設定
     * @param user_name 従業員名
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * 端末名を取得
     * @return 端末名
     */
    @Column(length = 40)
    public String getClient_device_name() {
        return this.client_device_name;
    }

    /**
     * 端末名を設定
     * @param client_device_name 端末名
     */
    public void setClient_device_name(String client_device_name) {
        this.client_device_name = client_device_name;
    }

    /**
     * IPアドレスを取得
     * @return IPアドレス
     */
    @Column(length = 40)
    public String getIp_address() {
        return this.ip_address;
    }

    /**
     * IPアドレスを設定
     * @param ip_address IPアドレス
     */
    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    /**
     * 変更NGフラグを取得
     * @return 変更NGフラグ
     */
    @Column(length = 1)
    public Integer getNg_flag() {
        return this.ng_flag;
    }

    /**
     * 変更NGフラグを設定
     * @param ng_flag 変更NGフラグ
     */
    public void setNg_flag(Integer ng_flag) {
        this.ng_flag = ng_flag;
    }

    /**
     * NG詳細情報を取得
     * @return NG詳細情報
     */
    @Column(length = 100)
    public String getNg_detail() {
        return this.ng_detail;
    }

    /**
     * NG詳細情報を設定
     * @param ng_detail NG詳細情報
     */
    public void setNg_detail(String ng_detail) {
        this.ng_detail = ng_detail;
    }

    /**
     * Zh003パスワード変更履歴詳細のListを取得
     * @return Zh003パスワード変更履歴詳細のList
     */
    @OneToMany(fetch= FetchType.LAZY, mappedBy = "histChangePassword", cascade = CascadeType.ALL, orphanRemoval=true)
    public Set<HistChangePasswordDetail> getHistChangePasswordDetails() {
        return this.histChangePasswordDetails;
    }

    /**
     * Zh003パスワード変更履歴詳細のListを設定
     * @return Zh003パスワード変更履歴詳細のList
     */ 
    public void setHistChangePasswordDetails(Set<HistChangePasswordDetail> histChangePasswordDetails) {
        this.histChangePasswordDetails = histChangePasswordDetails;
    }

}
