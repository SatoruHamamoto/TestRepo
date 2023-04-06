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
 * Zm010メッセージ定義マスタ エンティティ
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
@Table(name = "mstr_message_define")
@NamedQueries({
        @NamedQuery(name = "MstrMessageDefine.findAll", query = "SELECT p FROM MstrMessageDefine p"),
        @NamedQuery(name = "MstrMessageDefine.findByPK", query = "SELECT p FROM MstrMessageDefine p WHERE p.message_define_key = :message_define_key")
})
public class MstrMessageDefine extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_message_define";

    /** メッセージ定義キー */
    public static final String COLUMN_NAME_MESSAGE_DEFINE_KEY = "message_define_key";

    /** nkメッセージNO */
    public static final String COLUMN_NAME_MESSAGE_NO = "message_no";

    /** リソースID */
    public static final String COLUMN_NAME_RESOURCE_ID = "resource_id";

    /** カテゴリ */
    public static final String COLUMN_NAME_CATEGORY = "category";

    /** メッセージ重要度 */
    public static final String COLUMN_NAME_MESSAGE_LEVEL = "message_level";

    /** メッセージ履歴記録可否 */
    public static final String COLUMN_NAME_IS_MESSAGE_HISTORY_REC = "is_message_history_rec";

    /** ログ出力有無 */
    public static final String COLUMN_NAME_IS_LOGGING = "is_logging";

    /** プッシュ通知フラグ */
    public static final String COLUMN_NAME_IS_NOTICE_PUSH = "is_notice_push";

    /** メッセージボタンモード */
    public static final String COLUMN_NAME_MESSAGE_BTN_MODE = "message_btn_mode";

    /** メッセージデフォルトボタンモード */
    public static final String COLUMN_NAME_MESSAGE_DEFAULT_BTN_MODE = "message_default_btn_mode";

    /** メール送信先グループID */
    public static final String COLUMN_NAME_SEND_MAIL_GROUP_ID = "send_mail_group_id";

    /** メール送信抑制上限数 */
    public static final String COLUMN_NAME_SEND_MAIL_RESTRAIN_LIMIT = "send_mail_restrain_limit";

    /** メール送信抑制期間（時間） */
    public static final String COLUMN_NAME_SEND_MAIL_RESTRAIN_LIMIT_TIME = "send_mail_restrain_limit_time";

    /** Talendジョブ名 */
    public static final String COLUMN_NAME_TALEND_JOB_NAME = "talend_job_name";

    /** Talendコンテキストパラメータ */
    public static final String COLUMN_NAME_TALEND_CONTEXT_PARAM = "talend_context_param";

    /** メールメッセージタイトルリソースID */
    public static final String COLUMN_NAME_MESSAGE_TITLE_RESOURCE_ID = "message_title_resource_id";

    /** メールメッセージ本文リソースID */
    public static final String COLUMN_NAME_MESSAGE_TEXT_RESOURCE_ID = "message_text_resource_id";

    /** メッセージ定義キー */
    private String message_define_key;
    /** nkメッセージNO */
    private String message_no;
    /** リソースID */
    private String resource_id;
    /** カテゴリ */
    private Integer category;
    /** メッセージ重要度 */
    private Integer message_level;
    /** メッセージ履歴記録可否 */
    private Integer is_message_history_rec;
    /** ログ出力有無 */
    private Integer is_logging;
    /** プッシュ通知フラグ */
    private Integer is_notice_push;
    /** メッセージボタンモード */
    private Integer message_btn_mode;
    /** メッセージデフォルトボタンモード */
    private Integer message_default_btn_mode;
    /** メール送信先グループID */
    private String send_mail_group_id;
    /** メール送信抑制上限数 */
    private Integer send_mail_restrain_limit;
    /** メール送信抑制期間（時間） */
    private Integer send_mail_restrain_limit_time;
    /** Talendジョブ名 */
    private String talend_job_name;
    /** Talendコンテキストパラメータ */
    private String talend_context_param;
    /** メールメッセージタイトルリソースID */
    private String message_title_resource_id;
    /** メールメッセージ本文リソースID */
    private String message_text_resource_id;


    /**
     * Zm010メッセージ定義マスタエンティティ コンストラクタ
     */
    public MstrMessageDefine() {
    }

    /**
     * Zm010メッセージ定義マスタエンティティ コンストラクタ
     * @param message_define_key メッセージ定義キー
     * @param message_no nkメッセージNO
     * @param version 更新バージョン
     */
    public MstrMessageDefine(String message_define_key, String message_no, int version) {
        this.message_define_key = message_define_key;
        this.message_no = message_no;
        super.setVersion(version);
    }

    /**
     * Zm010メッセージ定義マスタエンティティ コンストラクタ
     * @param message_define_key メッセージ定義キー
     * @param message_no nkメッセージNO
     * @param resource_id リソースID
     * @param category カテゴリ
     * @param message_level メッセージ重要度
     * @param is_message_history_rec メッセージ履歴記録可否
     * @param is_logging ログ出力有無
     * @param is_notice_push プッシュ通知フラグ
     * @param message_btn_mode メッセージボタンモード
     * @param message_default_btn_mode メッセージデフォルトボタンモード
     * @param send_mail_group_id メール送信先グループID
     * @param send_mail_restrain_limit メール送信抑制上限数
     * @param send_mail_restrain_limit_time メール送信抑制期間（時間）
     * @param talend_job_name Talendジョブ名
     * @param talend_context_param Talendコンテキストパラメータ
     * @param message_title_resource_id メールメッセージタイトルリソースID
     * @param message_text_resource_id メールメッセージ本文リソースID
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
    public MstrMessageDefine(String message_define_key, String message_no, String resource_id, Integer category, Integer message_level, Integer is_message_history_rec, Integer is_logging, Integer is_notice_push, Integer message_btn_mode, Integer message_default_btn_mode, String send_mail_group_id, Integer send_mail_restrain_limit, Integer send_mail_restrain_limit_time, String talend_job_name, String talend_context_param, String message_title_resource_id, String message_text_resource_id, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.message_define_key = message_define_key;
        this.message_no = message_no;
        this.resource_id = resource_id;
        this.category = category;
        this.message_level = message_level;
        this.is_message_history_rec = is_message_history_rec;
        this.is_logging = is_logging;
        this.is_notice_push = is_notice_push;
        this.message_btn_mode = message_btn_mode;
        this.message_default_btn_mode = message_default_btn_mode;
        this.send_mail_group_id = send_mail_group_id;
        this.send_mail_restrain_limit = send_mail_restrain_limit;
        this.send_mail_restrain_limit_time = send_mail_restrain_limit_time;
        this.talend_job_name = talend_job_name;
        this.talend_context_param = talend_context_param;
        this.message_title_resource_id = message_title_resource_id;
        this.message_text_resource_id = message_text_resource_id;
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
     * メッセージ定義キーを取得
     * @return メッセージ定義キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getMessage_define_key() {
        return this.message_define_key;
    }

    /**
     * メッセージ定義キーを設定
     * @param message_define_key メッセージ定義キー (null不可)
     */
    public void setMessage_define_key(String message_define_key) {
        this.message_define_key = message_define_key;
    }

    /**
     * nkメッセージNOを取得
     * @return nkメッセージNO
     */
    @Column(nullable = false, length = 40)
    public String getMessage_no() {
        return this.message_no;
    }

    /**
     * nkメッセージNOを設定
     * @param message_no nkメッセージNO (null不可)
     */
    public void setMessage_no(String message_no) {
        this.message_no = message_no;
    }

    /**
     * リソースIDを取得
     * @return リソースID
     */
    @Column(length = 40)
    public String getResource_id() {
        return this.resource_id;
    }

    /**
     * リソースIDを設定
     * @param resource_id リソースID
     */
    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    /**
     * カテゴリを取得
     * @return カテゴリ
     */
    @Column(length = 2)
    public Integer getCategory() {
        return this.category;
    }

    /**
     * カテゴリを設定
     * @param category カテゴリ
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * メッセージ重要度を取得
     * @return メッセージ重要度
     */
    @Column(length = 2)
    public Integer getMessage_level() {
        return this.message_level;
    }

    /**
     * メッセージ重要度を設定
     * @param message_level メッセージ重要度
     */
    public void setMessage_level(Integer message_level) {
        this.message_level = message_level;
    }

    /**
     * メッセージ履歴記録可否を取得
     * @return メッセージ履歴記録可否
     */
    @Column(length = 1)
    public Integer getIs_message_history_rec() {
        return this.is_message_history_rec;
    }

    /**
     * メッセージ履歴記録可否を設定
     * @param is_message_history_rec メッセージ履歴記録可否
     */
    public void setIs_message_history_rec(Integer is_message_history_rec) {
        this.is_message_history_rec = is_message_history_rec;
    }

    /**
     * ログ出力有無を取得
     * @return ログ出力有無
     */
    @Column(length = 1)
    public Integer getIs_logging() {
        return this.is_logging;
    }

    /**
     * ログ出力有無を設定
     * @param is_logging ログ出力有無
     */
    public void setIs_logging(Integer is_logging) {
        this.is_logging = is_logging;
    }

    /**
     * プッシュ通知フラグを取得
     * @return プッシュ通知フラグ
     */
    @Column(length = 1)
    public Integer getIs_notice_push() {
        return this.is_notice_push;
    }

    /**
     * プッシュ通知フラグを設定
     * @param is_notice_push プッシュ通知フラグ
     */
    public void setIs_notice_push(Integer is_notice_push) {
        this.is_notice_push = is_notice_push;
    }

    /**
     * メッセージボタンモードを取得
     * @return メッセージボタンモード
     */
    @Column(length = 2)
    public Integer getMessage_btn_mode() {
        return this.message_btn_mode;
    }

    /**
     * メッセージボタンモードを設定
     * @param message_btn_mode メッセージボタンモード
     */
    public void setMessage_btn_mode(Integer message_btn_mode) {
        this.message_btn_mode = message_btn_mode;
    }

    /**
     * メッセージデフォルトボタンモードを取得
     * @return メッセージデフォルトボタンモード
     */
    @Column(length = 2)
    public Integer getMessage_default_btn_mode() {
        return this.message_default_btn_mode;
    }

    /**
     * メッセージデフォルトボタンモードを設定
     * @param message_default_btn_mode メッセージデフォルトボタンモード
     */
    public void setMessage_default_btn_mode(Integer message_default_btn_mode) {
        this.message_default_btn_mode = message_default_btn_mode;
    }

    /**
     * メール送信先グループIDを取得
     * @return メール送信先グループID
     */
    @Column(length = 38)
    public String getSend_mail_group_id() {
        return this.send_mail_group_id;
    }

    /**
     * メール送信先グループIDを設定
     * @param send_mail_group_id メール送信先グループID
     */
    public void setSend_mail_group_id(String send_mail_group_id) {
        this.send_mail_group_id = send_mail_group_id;
    }

    /**
     * メール送信抑制上限数を取得
     * @return メール送信抑制上限数
     */
    @Column(length = 2)
    public Integer getSend_mail_restrain_limit() {
        return this.send_mail_restrain_limit;
    }

    /**
     * メール送信抑制上限数を設定
     * @param send_mail_restrain_limit メール送信抑制上限数
     */
    public void setSend_mail_restrain_limit(Integer send_mail_restrain_limit) {
        this.send_mail_restrain_limit = send_mail_restrain_limit;
    }

    /**
     * メール送信抑制期間（時間）を取得
     * @return メール送信抑制期間（時間）
     */
    @Column(length = 4)
    public Integer getSend_mail_restrain_limit_time() {
        return this.send_mail_restrain_limit_time;
    }

    /**
     * メール送信抑制期間（時間）を設定
     * @param send_mail_restrain_limit_time メール送信抑制期間（時間）
     */
    public void setSend_mail_restrain_limit_time(Integer send_mail_restrain_limit_time) {
        this.send_mail_restrain_limit_time = send_mail_restrain_limit_time;
    }

    /**
     * Talendジョブ名を取得
     * @return Talendジョブ名
     */
    @Column(length = 100)
    public String getTalend_job_name() {
        return this.talend_job_name;
    }

    /**
     * Talendジョブ名を設定
     * @param talend_job_name Talendジョブ名
     */
    public void setTalend_job_name(String talend_job_name) {
        this.talend_job_name = talend_job_name;
    }

    /**
     * Talendコンテキストパラメータを取得
     * @return Talendコンテキストパラメータ
     */
    @Column(length = 1000)
    public String getTalend_context_param() {
        return this.talend_context_param;
    }

    /**
     * Talendコンテキストパラメータを設定
     * @param talend_context_param Talendコンテキストパラメータ
     */
    public void setTalend_context_param(String talend_context_param) {
        this.talend_context_param = talend_context_param;
    }

    /**
     * メールメッセージタイトルリソースIDを取得
     * @return メールメッセージタイトルリソースID
     */
    @Column(length = 40)
    public String getMessage_title_resource_id() {
        return this.message_title_resource_id;
    }

    /**
     * メールメッセージタイトルリソースIDを設定
     * @param message_title_resource_id メールメッセージタイトルリソースID
     */
    public void setMessage_title_resource_id(String message_title_resource_id) {
        this.message_title_resource_id = message_title_resource_id;
    }

    /**
     * メールメッセージ本文リソースIDを取得
     * @return メールメッセージ本文リソースID
     */
    @Column(length = 40)
    public String getMessage_text_resource_id() {
        return this.message_text_resource_id;
    }

    /**
     * メールメッセージ本文リソースIDを設定
     * @param message_text_resource_id メールメッセージ本文リソースID
     */
    public void setMessage_text_resource_id(String message_text_resource_id) {
        this.message_text_resource_id = message_text_resource_id;
    }

}
