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
 * Zi011メール通知キュー エンティティ
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
@Table(name = "tmp_queue_mail_notice")
@NamedQueries({
        @NamedQuery(name = "TmpQueueMailNotice.findAll", query = "SELECT p FROM TmpQueueMailNotice p"),
        @NamedQuery(name = "TmpQueueMailNotice.findByPK", query = "SELECT p FROM TmpQueueMailNotice p WHERE p.tmp_queue_mail_notice_key = :tmp_queue_mail_notice_key")
})
public class TmpQueueMailNotice extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "tmp_queue_mail_notice";

    /** メール通知キューキー */
    public static final String COLUMN_NAME_TMP_QUEUE_MAIL_NOTICE_KEY = "tmp_queue_mail_notice_key";

    /** メッセージキー */
    public static final String COLUMN_NAME_MESSAGE_KEY = "message_key";

    /** 発生日時 */
    public static final String COLUMN_NAME_OCCUR_DATE = "occur_date";

    /** メッセージNo */
    public static final String COLUMN_NAME_MESSAGE_NO = "message_no";

    /** メール通知状況 */
    public static final String COLUMN_NAME_MAIL_NOTICE_STATUS = "mail_notice_status";

    /** メール通知キューキー */
    private String tmp_queue_mail_notice_key;
    /** メッセージキー */
    private String message_key;
    /** 発生日時 */
    private Date occur_date;
    /** メッセージNo */
    private String message_no;
    /** メール通知状況 */
    private int mail_notice_status;


    /**
     * Zi011メール通知キューエンティティ コンストラクタ
     */
    public TmpQueueMailNotice() {
    }

    /**
     * Zi011メール通知キューエンティティ コンストラクタ
     * @param tmp_queue_mail_notice_key メール通知キューキー
     * @param message_key メッセージキー
     * @param occur_date 発生日時
     * @param mail_notice_status メール通知状況
     * @param version 更新バージョン
     */
    public TmpQueueMailNotice(String tmp_queue_mail_notice_key, String message_key, Date occur_date, int mail_notice_status, int version) {
        this.tmp_queue_mail_notice_key = tmp_queue_mail_notice_key;
        this.message_key = message_key;
        this.occur_date = occur_date;
        this.mail_notice_status = mail_notice_status;
        super.setVersion(version);
    }

    /**
     * Zi011メール通知キューエンティティ コンストラクタ
     * @param tmp_queue_mail_notice_key メール通知キューキー
     * @param message_key メッセージキー
     * @param occur_date 発生日時
     * @param message_no メッセージNo
     * @param mail_notice_status メール通知状況
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
    public TmpQueueMailNotice(String tmp_queue_mail_notice_key, String message_key, Date occur_date, String message_no, int mail_notice_status, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.tmp_queue_mail_notice_key = tmp_queue_mail_notice_key;
        this.message_key = message_key;
        this.occur_date = occur_date;
        this.message_no = message_no;
        this.mail_notice_status = mail_notice_status;
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
     * メール通知キューキーを取得
     * @return メール通知キューキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getTmp_queue_mail_notice_key() {
        return this.tmp_queue_mail_notice_key;
    }

    /**
     * メール通知キューキーを設定
     * @param tmp_queue_mail_notice_key メール通知キューキー (null不可)
     */
    public void setTmp_queue_mail_notice_key(String tmp_queue_mail_notice_key) {
        this.tmp_queue_mail_notice_key = tmp_queue_mail_notice_key;
    }

    /**
     * メッセージキーを取得
     * @return メッセージキー
     */
    @Column(nullable = false, length = 38)
    public String getMessage_key() {
        return this.message_key;
    }

    /**
     * メッセージキーを設定
     * @param message_key メッセージキー (null不可)
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
     * メッセージNoを取得
     * @return メッセージNo
     */
    @Column(length = 40)
    public String getMessage_no() {
        return this.message_no;
    }

    /**
     * メッセージNoを設定
     * @param message_no メッセージNo
     */
    public void setMessage_no(String message_no) {
        this.message_no = message_no;
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

}
