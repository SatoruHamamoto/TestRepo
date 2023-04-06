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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi020ユーザ情報 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/08/19 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "info_user")
@NamedQueries({
        @NamedQuery(name = "InfoUser.findAll", query = "SELECT p FROM InfoUser p"),
        @NamedQuery(name = "InfoUser.findByPK", query = "SELECT p FROM InfoUser p WHERE p.info_user_key = :info_user_key")
})
public class InfoUser extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "info_user";

    /** ユーザ情報キー */
    public static final String COLUMN_NAME_INFO_USER_KEY = "info_user_key";

    /** nkユーザID */
    public static final String COLUMN_NAME_USER_ID = "user_id";

    /** パスワード */
    public static final String COLUMN_NAME_PASSWORD = "password";

    /** パスワード更新日時 */
    public static final String COLUMN_NAME_PASSWORD_UPDATE_DATE = "password_update_date";

    /** 認証エラー回数 */
    public static final String COLUMN_NAME_CERTIFY_FAILURE_TIMES = "certify_failure_times";

    /** アカウント有効期間（from） */
    public static final String COLUMN_NAME_USER_ID_VALID_FROM = "user_id_valid_from";

    /** アカウント有効期間（to） */
    public static final String COLUMN_NAME_USER_ID_VALID_TO = "user_id_valid_to";

    /** アカウント更新日時 */
    public static final String COLUMN_NAME_USER_ID_EXTENDED_DATE = "user_id_extended_date";

    /** アカウント状態（ロックアウトか否か） */
    public static final String COLUMN_NAME_IS_LOCK_OUT = "is_lock_out";

    /** アカウントロックアウト日時 */
    public static final String COLUMN_NAME_LOCK_OUT_DATE = "lock_out_date";

    /** アカウントロックアウト解除理由 */
    public static final String COLUMN_NAME_OPEN_USER_REASON = "open_user_reason";

    /** アカウントロックアウト解除者ID */
    public static final String COLUMN_NAME_OPEN_USER_ID = "open_user_id";

    /** アカウントロックアウト解除者名 */
    public static final String COLUMN_NAME_OPEN_USER_NAME = "open_user_name";

    /** アカウントロックアウト解除日時 */
    public static final String COLUMN_NAME_OPEN_USER_DATE = "open_user_date";

    /** ユーザ選択ロケールID */
    public static final String COLUMN_NAME_SELECT_LOCALE_ID = "select_locale_id";

    /** ユーザ情報キー */
    private String info_user_key;
    /** nkユーザID */
    private String user_id;
    /** パスワード */
    private String password;
    /** パスワード更新日時 */
    private Date password_update_date;
    /** 認証エラー回数 */
    private int certify_failure_times;
    /** アカウント有効期間（from） */
    private Date user_id_valid_from;
    /** アカウント有効期間（to） */
    private Date user_id_valid_to;
    /** アカウント更新日時 */
    private Date user_id_extended_date;
    /** アカウント状態（ロックアウトか否か） */
    private Integer is_lock_out;
    /** アカウントロックアウト日時 */
    private Date lock_out_date;
    /** アカウントロックアウト解除理由 */
    private String open_user_reason;
    /** アカウントロックアウト解除者ID */
    private String open_user_id;
    /** アカウントロックアウト解除者名 */
    private String open_user_name;
    /** アカウントロックアウト解除日時 */
    private Date open_user_date;
    /** ユーザ選択ロケールID */
    private String select_locale_id;


    /**
     * Zi020ユーザ情報エンティティ コンストラクタ
     */
    public InfoUser() {
    }

    /**
     * Zi020ユーザ情報エンティティ コンストラクタ
     * @param info_user_key ユーザ情報キー
     * @param user_id nkユーザID
     * @param certify_failure_times 認証エラー回数
     * @param user_id_valid_from アカウント有効期間（from）
     * @param user_id_valid_to アカウント有効期間（to）
     * @param version 更新バージョン
     */
    public InfoUser(String info_user_key, String user_id, int certify_failure_times, Date user_id_valid_from, Date user_id_valid_to, int version) {
        this.info_user_key = info_user_key;
        this.user_id = user_id;
        this.certify_failure_times = certify_failure_times;
        this.user_id_valid_from = user_id_valid_from;
        this.user_id_valid_to = user_id_valid_to;
        super.setVersion(version);
    }

    /**
     * Zi020ユーザ情報エンティティ コンストラクタ
     * @param info_user_key ユーザ情報キー
     * @param user_id nkユーザID
     * @param password パスワード
     * @param password_update_date パスワード更新日時
     * @param certify_failure_times 認証エラー回数
     * @param user_id_valid_from アカウント有効期間（from）
     * @param user_id_valid_to アカウント有効期間（to）
     * @param user_id_extended_date アカウント更新日時
     * @param is_lock_out アカウント状態（ロックアウトか否か）
     * @param lock_out_date アカウントロックアウト日時
     * @param open_user_reason アカウントロックアウト解除理由
     * @param open_user_id アカウントロックアウト解除者ID
     * @param open_user_name アカウントロックアウト解除者名
     * @param open_user_date アカウントロックアウト解除日時
     * @param select_locale_id ユーザ選択ロケールID
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
    public InfoUser(String info_user_key, String user_id, String password, Date password_update_date, int certify_failure_times, Date user_id_valid_from, Date user_id_valid_to, Date user_id_extended_date, Integer is_lock_out, Date lock_out_date, String open_user_reason, String open_user_id, String open_user_name, Date open_user_date, String select_locale_id, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.info_user_key = info_user_key;
        this.user_id = user_id;
        this.password = password;
        this.password_update_date = password_update_date;
        this.certify_failure_times = certify_failure_times;
        this.user_id_valid_from = user_id_valid_from;
        this.user_id_valid_to = user_id_valid_to;
        this.user_id_extended_date = user_id_extended_date;
        this.is_lock_out = is_lock_out;
        this.lock_out_date = lock_out_date;
        this.open_user_reason = open_user_reason;
        this.open_user_id = open_user_id;
        this.open_user_name = open_user_name;
        this.open_user_date = open_user_date;
        this.select_locale_id = select_locale_id;
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
     * ユーザ情報キーを取得
     * @return ユーザ情報キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getInfo_user_key() {
        return this.info_user_key;
    }

    /**
     * ユーザ情報キーを設定
     * @param info_user_key ユーザ情報キー (null不可)
     */
    public void setInfo_user_key(String info_user_key) {
        this.info_user_key = info_user_key;
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
     * パスワードを取得
     * @return パスワード
     */
    @Column(length = 512)
    public String getPassword() {
        return this.password;
    }

    /**
     * パスワードを設定
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * パスワード更新日時を取得
     * @return パスワード更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getPassword_update_date() {
        return this.password_update_date;
    }

    /**
     * パスワード更新日時を設定
     * @param password_update_date パスワード更新日時
     */
    public void setPassword_update_date(Date password_update_date) {
        this.password_update_date = password_update_date;
    }

    /**
     * 認証エラー回数を取得
     * @return 認証エラー回数
     */
    @Column(nullable = false, length = 5)
    public int getCertify_failure_times() {
        return this.certify_failure_times;
    }

    /**
     * 認証エラー回数を設定
     * @param certify_failure_times 認証エラー回数 (null不可)
     */
    public void setCertify_failure_times(int certify_failure_times) {
        this.certify_failure_times = certify_failure_times;
    }

    /**
     * アカウント有効期間（from）を取得
     * @return アカウント有効期間（from）
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getUser_id_valid_from() {
        return this.user_id_valid_from;
    }

    /**
     * アカウント有効期間（from）を設定
     * @param user_id_valid_from アカウント有効期間（from） (null不可)
     */
    public void setUser_id_valid_from(Date user_id_valid_from) {
        this.user_id_valid_from = user_id_valid_from;
    }

    /**
     * アカウント有効期間（to）を取得
     * @return アカウント有効期間（to）
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getUser_id_valid_to() {
        return this.user_id_valid_to;
    }

    /**
     * アカウント有効期間（to）を設定
     * @param user_id_valid_to アカウント有効期間（to） (null不可)
     */
    public void setUser_id_valid_to(Date user_id_valid_to) {
        this.user_id_valid_to = user_id_valid_to;
    }

    /**
     * アカウント更新日時を取得
     * @return アカウント更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getUser_id_extended_date() {
        return this.user_id_extended_date;
    }

    /**
     * アカウント更新日時を設定
     * @param user_id_extended_date アカウント更新日時
     */
    public void setUser_id_extended_date(Date user_id_extended_date) {
        this.user_id_extended_date = user_id_extended_date;
    }

    /**
     * アカウント状態（ロックアウトか否か）を取得
     * @return アカウント状態（ロックアウトか否か）
     */
    @Column(length = 1)
    public Integer getIs_lock_out() {
        return this.is_lock_out;
    }

    /**
     * アカウント状態（ロックアウトか否か）を設定
     * @param is_lock_out アカウント状態（ロックアウトか否か）
     */
    public void setIs_lock_out(Integer is_lock_out) {
        this.is_lock_out = is_lock_out;
    }

    /**
     * アカウントロックアウト日時を取得
     * @return アカウントロックアウト日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getLock_out_date() {
        return this.lock_out_date;
    }

    /**
     * アカウントロックアウト日時を設定
     * @param lock_out_date アカウントロックアウト日時
     */
    public void setLock_out_date(Date lock_out_date) {
        this.lock_out_date = lock_out_date;
    }

    /**
     * アカウントロックアウト解除理由を取得
     * @return アカウントロックアウト解除理由
     */
    @Column(length = 60)
    public String getOpen_user_reason() {
        return this.open_user_reason;
    }

    /**
     * アカウントロックアウト解除理由を設定
     * @param open_user_reason アカウントロックアウト解除理由
     */
    public void setOpen_user_reason(String open_user_reason) {
        this.open_user_reason = open_user_reason;
    }

    /**
     * アカウントロックアウト解除者IDを取得
     * @return アカウントロックアウト解除者ID
     */
    @Column(length = 20)
    public String getOpen_user_id() {
        return this.open_user_id;
    }

    /**
     * アカウントロックアウト解除者IDを設定
     * @param open_user_id アカウントロックアウト解除者ID
     */
    public void setOpen_user_id(String open_user_id) {
        this.open_user_id = open_user_id;
    }

    /**
     * アカウントロックアウト解除者名を取得
     * @return アカウントロックアウト解除者名
     */
    @Column(length = 40)
    public String getOpen_user_name() {
        return this.open_user_name;
    }

    /**
     * アカウントロックアウト解除者名を設定
     * @param open_user_name アカウントロックアウト解除者名
     */
    public void setOpen_user_name(String open_user_name) {
        this.open_user_name = open_user_name;
    }

    /**
     * アカウントロックアウト解除日時を取得
     * @return アカウントロックアウト解除日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getOpen_user_date() {
        return this.open_user_date;
    }

    /**
     * アカウントロックアウト解除日時を設定
     * @param open_user_date アカウントロックアウト解除日時
     */
    public void setOpen_user_date(Date open_user_date) {
        this.open_user_date = open_user_date;
    }

    /**
     * ユーザ選択ロケールIDを取得
     * @return ユーザ選択ロケールID
     */
    @Column(length = 20)
    public String getSelect_locale_id() {
        return this.select_locale_id;
    }

    /**
     * ユーザ選択ロケールIDを設定
     * @param select_locale_id ユーザ選択ロケールID
     */
    public void setSelect_locale_id(String select_locale_id) {
        this.select_locale_id = select_locale_id;
    }

}
