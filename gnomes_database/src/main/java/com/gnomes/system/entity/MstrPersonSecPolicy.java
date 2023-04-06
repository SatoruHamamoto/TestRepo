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
 * Zm030ユーザアカウントセキュリティポリシーマスタ エンティティ
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
@Table(name = "mstr_person_sec_policy")
@NamedQueries({
        @NamedQuery(name = "MstrPersonSecPolicy.findAll", query = "SELECT p FROM MstrPersonSecPolicy p"),
        @NamedQuery(name = "MstrPersonSecPolicy.findByPK", query = "SELECT p FROM MstrPersonSecPolicy p WHERE p.person_sec_policy_key = :person_sec_policy_key")
})
public class MstrPersonSecPolicy extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_person_sec_policy";

    /** ユーザアカウントセキュリティポリシーキー */
    public static final String COLUMN_NAME_PERSON_SEC_POLICY_KEY = "person_sec_policy_key";

    /** セキュリティマスタ更新履歴保持期間（日） */
    public static final String COLUMN_NAME_TIMESPAN_KEEP_MODIFIED_HISTORY = "timespan_keep_modified_history";

    /** セキュリティ履歴保持期間（日） */
    public static final String COLUMN_NAME_TIMESPAN_KEEP_SECURITY_HISTORY = "timespan_keep_security_history";

    /** 認証エラー許容数（回） */
    public static final String COLUMN_NAME_CERTIFY_FAILURE_TIMES = "certify_failure_times";

    /** アカウント期限切れ 警告表示（日） */
    public static final String COLUMN_NAME_USER_ID_LIMIT_DAY = "user_id_limit_day";

    /** パスワード有効期限（日） */
    public static final String COLUMN_NAME_PASSWORD_LIMIT_DAY = "password_limit_day";

    /** パスワード期限切れ警告表示（日） */
    public static final String COLUMN_NAME_PASSWORD_LIMIT_CAUTION = "password_limit_caution";

    /** 同一パスワードの使用制限（回） */
    public static final String COLUMN_NAME_REPEAT_SAME_PASSWORD = "repeat_same_password";

    /** パスワード有効桁数 */
    public static final String COLUMN_NAME_MINIMUM_PASSWORD_SIZE = "minimum_password_size";

    /** 英字または数字のみのパスワード使用制限（有/無） */
    public static final String COLUMN_NAME_IS_ALPHABET_AND_NUMERAL_ONLY = "is_alphabet_and_numeral_only";

    /** 特定文字列の使用禁止制限（有/無） */
    public static final String COLUMN_NAME_IS_SPECIFIC_CHARACTER_PROHIBIT = "is_specific_character_prohibit";

    /** 全て同じ文字または数字の使用禁止制限（有/無） */
    public static final String COLUMN_NAME_IS_ONE_CHARACTER_PASSWORD_PROHIBIT = "is_one_character_password_prohibit";

    /** アカウントと同じパスワードの使用禁止制限（有/無） */
    public static final String COLUMN_NAME_IS_USER_ID_PASSWORD_PROHIBIT = "is_user_id_password_prohibit";

    /** アプリケーションロックの起動時間（分） */
    public static final String COLUMN_NAME_APPLICATION_LOCK_WAITING_TIME = "application_lock_waiting_time";

    /** ログイン情報のデフォルト表示方法（管理端末用) */
    public static final String COLUMN_NAME_DEFAULT_USER_ID_PASSWORD_CLIENT = "default_user_id_password_client";

    /** ログイン情報のデフォルト表示方法(工程端末用) */
    public static final String COLUMN_NAME_DEFAULT_USER_ID_PASSWORD_PANECOM = "default_user_id_password_panecom";

    /** ユーザアカウントセキュリティポリシーキー */
    private String person_sec_policy_key;
    /** セキュリティマスタ更新履歴保持期間（日） */
    private Integer timespan_keep_modified_history;
    /** セキュリティ履歴保持期間（日） */
    private Integer timespan_keep_security_history;
    /** 認証エラー許容数（回） */
    private Integer certify_failure_times;
    /** アカウント期限切れ 警告表示（日） */
    private Integer user_id_limit_day;
    /** パスワード有効期限（日） */
    private Integer password_limit_day;
    /** パスワード期限切れ警告表示（日） */
    private Integer password_limit_caution;
    /** 同一パスワードの使用制限（回） */
    private Integer repeat_same_password;
    /** パスワード有効桁数 */
    private Integer minimum_password_size;
    /** 英字または数字のみのパスワード使用制限（有/無） */
    private Integer is_alphabet_and_numeral_only;
    /** 特定文字列の使用禁止制限（有/無） */
    private Integer is_specific_character_prohibit;
    /** 全て同じ文字または数字の使用禁止制限（有/無） */
    private Integer is_one_character_password_prohibit;
    /** アカウントと同じパスワードの使用禁止制限（有/無） */
    private Integer is_user_id_password_prohibit;
    /** アプリケーションロックの起動時間（分） */
    private Integer application_lock_waiting_time;
    /** ログイン情報のデフォルト表示方法（管理端末用) */
    private Integer default_user_id_password_client;
    /** ログイン情報のデフォルト表示方法(工程端末用) */
    private Integer default_user_id_password_panecom;


    /**
     * Zm030ユーザアカウントセキュリティポリシーマスタエンティティ コンストラクタ
     */
    public MstrPersonSecPolicy() {
    }

    /**
     * Zm030ユーザアカウントセキュリティポリシーマスタエンティティ コンストラクタ
     * @param person_sec_policy_key ユーザアカウントセキュリティポリシーキー
     * @param version 更新バージョン
     */
    public MstrPersonSecPolicy(String person_sec_policy_key, int version) {
        this.person_sec_policy_key = person_sec_policy_key;
        super.setVersion(version);
    }

    /**
     * Zm030ユーザアカウントセキュリティポリシーマスタエンティティ コンストラクタ
     * @param person_sec_policy_key ユーザアカウントセキュリティポリシーキー
     * @param timespan_keep_modified_history セキュリティマスタ更新履歴保持期間（日）
     * @param timespan_keep_security_history セキュリティ履歴保持期間（日）
     * @param certify_failure_times 認証エラー許容数（回）
     * @param user_id_limit_day アカウント期限切れ 警告表示（日）
     * @param password_limit_day パスワード有効期限（日）
     * @param password_limit_caution パスワード期限切れ警告表示（日）
     * @param repeat_same_password 同一パスワードの使用制限（回）
     * @param minimum_password_size パスワード有効桁数
     * @param is_alphabet_and_numeral_only 英字または数字のみのパスワード使用制限（有/無）
     * @param is_specific_character_prohibit 特定文字列の使用禁止制限（有/無）
     * @param is_one_character_password_prohibit 全て同じ文字または数字の使用禁止制限（有/無）
     * @param is_user_id_password_prohibit アカウントと同じパスワードの使用禁止制限（有/無）
     * @param application_lock_waiting_time アプリケーションロックの起動時間（分）
     * @param default_user_id_password_client ログイン情報のデフォルト表示方法（管理端末用)
     * @param default_user_id_password_panecom ログイン情報のデフォルト表示方法(工程端末用)
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
    public MstrPersonSecPolicy(String person_sec_policy_key, Integer timespan_keep_modified_history, Integer timespan_keep_security_history, Integer certify_failure_times, Integer user_id_limit_day, Integer password_limit_day, Integer password_limit_caution, Integer repeat_same_password, Integer minimum_password_size, Integer is_alphabet_and_numeral_only, Integer is_specific_character_prohibit, Integer is_one_character_password_prohibit, Integer is_user_id_password_prohibit, Integer application_lock_waiting_time, Integer default_user_id_password_client, Integer default_user_id_password_panecom, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.person_sec_policy_key = person_sec_policy_key;
        this.timespan_keep_modified_history = timespan_keep_modified_history;
        this.timespan_keep_security_history = timespan_keep_security_history;
        this.certify_failure_times = certify_failure_times;
        this.user_id_limit_day = user_id_limit_day;
        this.password_limit_day = password_limit_day;
        this.password_limit_caution = password_limit_caution;
        this.repeat_same_password = repeat_same_password;
        this.minimum_password_size = minimum_password_size;
        this.is_alphabet_and_numeral_only = is_alphabet_and_numeral_only;
        this.is_specific_character_prohibit = is_specific_character_prohibit;
        this.is_one_character_password_prohibit = is_one_character_password_prohibit;
        this.is_user_id_password_prohibit = is_user_id_password_prohibit;
        this.application_lock_waiting_time = application_lock_waiting_time;
        this.default_user_id_password_client = default_user_id_password_client;
        this.default_user_id_password_panecom = default_user_id_password_panecom;
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
     * ユーザアカウントセキュリティポリシーキーを取得
     * @return ユーザアカウントセキュリティポリシーキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPerson_sec_policy_key() {
        return this.person_sec_policy_key;
    }

    /**
     * ユーザアカウントセキュリティポリシーキーを設定
     * @param person_sec_policy_key ユーザアカウントセキュリティポリシーキー (null不可)
     */
    public void setPerson_sec_policy_key(String person_sec_policy_key) {
        this.person_sec_policy_key = person_sec_policy_key;
    }

    /**
     * セキュリティマスタ更新履歴保持期間（日）を取得
     * @return セキュリティマスタ更新履歴保持期間（日）
     */
    @Column(length = 4)
    public Integer getTimespan_keep_modified_history() {
        return this.timespan_keep_modified_history;
    }

    /**
     * セキュリティマスタ更新履歴保持期間（日）を設定
     * @param timespan_keep_modified_history セキュリティマスタ更新履歴保持期間（日）
     */
    public void setTimespan_keep_modified_history(Integer timespan_keep_modified_history) {
        this.timespan_keep_modified_history = timespan_keep_modified_history;
    }

    /**
     * セキュリティ履歴保持期間（日）を取得
     * @return セキュリティ履歴保持期間（日）
     */
    @Column(length = 4)
    public Integer getTimespan_keep_security_history() {
        return this.timespan_keep_security_history;
    }

    /**
     * セキュリティ履歴保持期間（日）を設定
     * @param timespan_keep_security_history セキュリティ履歴保持期間（日）
     */
    public void setTimespan_keep_security_history(Integer timespan_keep_security_history) {
        this.timespan_keep_security_history = timespan_keep_security_history;
    }

    /**
     * 認証エラー許容数（回）を取得
     * @return 認証エラー許容数（回）
     */
    @Column(length = 5)
    public Integer getCertify_failure_times() {
        return this.certify_failure_times;
    }

    /**
     * 認証エラー許容数（回）を設定
     * @param certify_failure_times 認証エラー許容数（回）
     */
    public void setCertify_failure_times(Integer certify_failure_times) {
        this.certify_failure_times = certify_failure_times;
    }

    /**
     * アカウント期限切れ 警告表示（日）を取得
     * @return アカウント期限切れ 警告表示（日）
     */
    @Column(length = 5)
    public Integer getUser_id_limit_day() {
        return this.user_id_limit_day;
    }

    /**
     * アカウント期限切れ 警告表示（日）を設定
     * @param user_id_limit_day アカウント期限切れ 警告表示（日）
     */
    public void setUser_id_limit_day(Integer user_id_limit_day) {
        this.user_id_limit_day = user_id_limit_day;
    }

    /**
     * パスワード有効期限（日）を取得
     * @return パスワード有効期限（日）
     */
    @Column(length = 5)
    public Integer getPassword_limit_day() {
        return this.password_limit_day;
    }

    /**
     * パスワード有効期限（日）を設定
     * @param password_limit_day パスワード有効期限（日）
     */
    public void setPassword_limit_day(Integer password_limit_day) {
        this.password_limit_day = password_limit_day;
    }

    /**
     * パスワード期限切れ警告表示（日）を取得
     * @return パスワード期限切れ警告表示（日）
     */
    @Column(length = 5)
    public Integer getPassword_limit_caution() {
        return this.password_limit_caution;
    }

    /**
     * パスワード期限切れ警告表示（日）を設定
     * @param password_limit_caution パスワード期限切れ警告表示（日）
     */
    public void setPassword_limit_caution(Integer password_limit_caution) {
        this.password_limit_caution = password_limit_caution;
    }

    /**
     * 同一パスワードの使用制限（回）を取得
     * @return 同一パスワードの使用制限（回）
     */
    @Column(length = 5)
    public Integer getRepeat_same_password() {
        return this.repeat_same_password;
    }

    /**
     * 同一パスワードの使用制限（回）を設定
     * @param repeat_same_password 同一パスワードの使用制限（回）
     */
    public void setRepeat_same_password(Integer repeat_same_password) {
        this.repeat_same_password = repeat_same_password;
    }

    /**
     * パスワード有効桁数を取得
     * @return パスワード有効桁数
     */
    @Column(length = 2)
    public Integer getMinimum_password_size() {
        return this.minimum_password_size;
    }

    /**
     * パスワード有効桁数を設定
     * @param minimum_password_size パスワード有効桁数
     */
    public void setMinimum_password_size(Integer minimum_password_size) {
        this.minimum_password_size = minimum_password_size;
    }

    /**
     * 英字または数字のみのパスワード使用制限（有/無）を取得
     * @return 英字または数字のみのパスワード使用制限（有/無）
     */
    @Column(length = 1)
    public Integer getIs_alphabet_and_numeral_only() {
        return this.is_alphabet_and_numeral_only;
    }

    /**
     * 英字または数字のみのパスワード使用制限（有/無）を設定
     * @param is_alphabet_and_numeral_only 英字または数字のみのパスワード使用制限（有/無）
     */
    public void setIs_alphabet_and_numeral_only(Integer is_alphabet_and_numeral_only) {
        this.is_alphabet_and_numeral_only = is_alphabet_and_numeral_only;
    }

    /**
     * 特定文字列の使用禁止制限（有/無）を取得
     * @return 特定文字列の使用禁止制限（有/無）
     */
    @Column(length = 1)
    public Integer getIs_specific_character_prohibit() {
        return this.is_specific_character_prohibit;
    }

    /**
     * 特定文字列の使用禁止制限（有/無）を設定
     * @param is_specific_character_prohibit 特定文字列の使用禁止制限（有/無）
     */
    public void setIs_specific_character_prohibit(Integer is_specific_character_prohibit) {
        this.is_specific_character_prohibit = is_specific_character_prohibit;
    }

    /**
     * 全て同じ文字または数字の使用禁止制限（有/無）を取得
     * @return 全て同じ文字または数字の使用禁止制限（有/無）
     */
    @Column(length = 1)
    public Integer getIs_one_character_password_prohibit() {
        return this.is_one_character_password_prohibit;
    }

    /**
     * 全て同じ文字または数字の使用禁止制限（有/無）を設定
     * @param is_one_character_password_prohibit 全て同じ文字または数字の使用禁止制限（有/無）
     */
    public void setIs_one_character_password_prohibit(Integer is_one_character_password_prohibit) {
        this.is_one_character_password_prohibit = is_one_character_password_prohibit;
    }

    /**
     * アカウントと同じパスワードの使用禁止制限（有/無）を取得
     * @return アカウントと同じパスワードの使用禁止制限（有/無）
     */
    @Column(length = 1)
    public Integer getIs_user_id_password_prohibit() {
        return this.is_user_id_password_prohibit;
    }

    /**
     * アカウントと同じパスワードの使用禁止制限（有/無）を設定
     * @param is_user_id_password_prohibit アカウントと同じパスワードの使用禁止制限（有/無）
     */
    public void setIs_user_id_password_prohibit(Integer is_user_id_password_prohibit) {
        this.is_user_id_password_prohibit = is_user_id_password_prohibit;
    }

    /**
     * アプリケーションロックの起動時間（分）を取得
     * @return アプリケーションロックの起動時間（分）
     */
    @Column(length = 5)
    public Integer getApplication_lock_waiting_time() {
        return this.application_lock_waiting_time;
    }

    /**
     * アプリケーションロックの起動時間（分）を設定
     * @param application_lock_waiting_time アプリケーションロックの起動時間（分）
     */
    public void setApplication_lock_waiting_time(Integer application_lock_waiting_time) {
        this.application_lock_waiting_time = application_lock_waiting_time;
    }

    /**
     * ログイン情報のデフォルト表示方法（管理端末用)を取得
     * @return ログイン情報のデフォルト表示方法（管理端末用)
     */
    @Column(length = 1)
    public Integer getDefault_user_id_password_client() {
        return this.default_user_id_password_client;
    }

    /**
     * ログイン情報のデフォルト表示方法（管理端末用)を設定
     * @param default_user_id_password_client ログイン情報のデフォルト表示方法（管理端末用)
     */
    public void setDefault_user_id_password_client(Integer default_user_id_password_client) {
        this.default_user_id_password_client = default_user_id_password_client;
    }

    /**
     * ログイン情報のデフォルト表示方法(工程端末用)を取得
     * @return ログイン情報のデフォルト表示方法(工程端末用)
     */
    @Column(length = 1)
    public Integer getDefault_user_id_password_panecom() {
        return this.default_user_id_password_panecom;
    }

    /**
     * ログイン情報のデフォルト表示方法(工程端末用)を設定
     * @param default_user_id_password_panecom ログイン情報のデフォルト表示方法(工程端末用)
     */
    public void setDefault_user_id_password_panecom(Integer default_user_id_password_panecom) {
        this.default_user_id_password_panecom = default_user_id_password_panecom;
    }

}
