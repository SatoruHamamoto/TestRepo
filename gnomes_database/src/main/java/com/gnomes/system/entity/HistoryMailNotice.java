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

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zr011メール通知履歴 エンティティ
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
@Table(name = "history_mail_notice")
@NamedQueries({
        @NamedQuery(name = "HistoryMailNotice.findAll", query = "SELECT p FROM HistoryMailNotice p"),
        @NamedQuery(name = "HistoryMailNotice.findByPK", query = "SELECT p FROM HistoryMailNotice p WHERE p.history_mail_notice_key = :history_mail_notice_key")
})
public class HistoryMailNotice extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "history_mail_notice";

    /** メール通知履歴キー */
    public static final String COLUMN_NAME_HISTORY_MAIL_NOTICE_KEY = "history_mail_notice_key";

    /** メッセージキー (FK) */
    public static final String COLUMN_NAME_MESSAGE_KEY = "message_key";

    /** 発生日時 */
    public static final String COLUMN_NAME_OCCUR_DATE = "occur_date";

    /** メッセージNO */
    public static final String COLUMN_NAME_MESSAGE_NO = "message_no";

    /** 通知日時 */
    public static final String COLUMN_NAME_NOTICE_DATE = "notice_date";

    /** メール通知状況 */
    public static final String COLUMN_NAME_MAIL_NOTICE_STATUS = "mail_notice_status";

    /** 失敗理由 */
    public static final String COLUMN_NAME_FAILURE_REASON = "failure_reason";

    /** メール通知履歴キー */
    private String history_mail_notice_key;
    /** メッセージキー (FK) */
    private String message_key;
    /** 発生日時 */
    private Date occur_date;
    /** メッセージNO */
    private String message_no;
    /** 通知日時 */
    private Date notice_date;
    /** メール通知状況 */
    private int mail_notice_status;
    /** 失敗理由 */
    private String failure_reason;


    /**
     * Zr011メール通知履歴エンティティ コンストラクタ
     */
    public HistoryMailNotice() {
    }

    /**
     * Zr011メール通知履歴エンティティ コンストラクタ
     * @param history_mail_notice_key メール通知履歴キー
     * @param message_key メッセージキー (FK)
     * @param occur_date 発生日時
     * @param notice_date 通知日時
     * @param mail_notice_status メール通知状況
     * @param version 更新バージョン
     */
    public HistoryMailNotice(String history_mail_notice_key, String message_key, Date occur_date, Date notice_date, int mail_notice_status, int version) {
        this.history_mail_notice_key = history_mail_notice_key;
        this.message_key = message_key;
        this.occur_date = occur_date;
        this.notice_date = notice_date;
        this.mail_notice_status = mail_notice_status;
        super.setVersion(version);
    }

    /**
     * Zr011メール通知履歴エンティティ コンストラクタ
     * @param history_mail_notice_key メール通知履歴キー
     * @param message_key メッセージキー (FK)
     * @param occur_date 発生日時
     * @param message_no メッセージNO
     * @param notice_date 通知日時
     * @param mail_notice_status メール通知状況
     * @param failure_reason 失敗理由
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
    public HistoryMailNotice(String history_mail_notice_key, String message_key, Date occur_date, String message_no, Date notice_date, int mail_notice_status, String failure_reason, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.history_mail_notice_key = history_mail_notice_key;
        this.message_key = message_key;
        this.occur_date = occur_date;
        this.message_no = message_no;
        this.notice_date = notice_date;
        this.mail_notice_status = mail_notice_status;
        this.failure_reason = failure_reason;
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
     * メール通知履歴キーを取得
     * @return メール通知履歴キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getHistory_mail_notice_key() {
        return this.history_mail_notice_key;
    }

    /**
     * メール通知履歴キーを設定
     * @param history_mail_notice_key メール通知履歴キー (null不可)
     */
    public void setHistory_mail_notice_key(String history_mail_notice_key) {
        this.history_mail_notice_key = history_mail_notice_key;
    }

    /**
     * メッセージキー (FK)を取得
     * @return メッセージキー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getMessage_key() {
        return this.message_key;
    }

    /**
     * メッセージキー (FK)を設定
     * @param message_key メッセージキー (FK) (null不可)
     */
    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    /**
     * 発生日時を取得
     * @return 発生日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getOccur_date() {
        return this.occur_date;
    }

    /**
     * 発生日時を設定
     * @param occur_date 発生日時 (null不可)
     */
    public void setOccur_date(Date occur_date) {
        this.occur_date = occur_date;
    }

    /**
     * メッセージNOを取得
     * @return メッセージNO
     */
    @Column(length = 40)
    public String getMessage_no() {
        return this.message_no;
    }

    /**
     * メッセージNOを設定
     * @param message_no メッセージNO
     */
    public void setMessage_no(String message_no) {
        this.message_no = message_no;
    }

    /**
     * 通知日時を取得
     * @return 通知日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getNotice_date() {
        return this.notice_date;
    }

    /**
     * 通知日時を設定
     * @param notice_date 通知日時 (null不可)
     */
    public void setNotice_date(Date notice_date) {
        this.notice_date = notice_date;
    }

    /**
     * メール通知状況を取得
     * @return メール通知状況
     */
    @Column(nullable = false, length = 1)
    public int getMail_notice_status() {
        return this.mail_notice_status;
    }

    /**
     * メール通知状況を設定
     * @param mail_notice_status メール通知状況 (null不可)
     */
    public void setMail_notice_status(int mail_notice_status) {
        this.mail_notice_status = mail_notice_status;
    }

    /**
     * 失敗理由を取得
     * @return 失敗理由
     */
    @Column(length = 2000)
    public String getFailure_reason() {
        return this.failure_reason;
    }

    /**
     * 失敗理由を設定
     * @param failure_reason 失敗理由
     */
    public void setFailure_reason(String failure_reason) {
        this.failure_reason = failure_reason;
    }

}
