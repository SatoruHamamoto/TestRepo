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
 * Zm011メッセージグループマスタ エンティティ
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
@Table(name = "mstr_message_group")
@NamedQueries({
        @NamedQuery(name = "MstrMessageGroup.findAll", query = "SELECT p FROM MstrMessageGroup p"),
        @NamedQuery(name = "MstrMessageGroup.findByPK", query = "SELECT p FROM MstrMessageGroup p WHERE p.message_group_key = :message_group_key")
})
public class MstrMessageGroup extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_message_group";

    /** メッセージグループキー */
    public static final String COLUMN_NAME_MESSAGE_GROUP_KEY = "message_group_key";

    /** nkメール送信先グループID */
    public static final String COLUMN_NAME_SEND_MAIL_GROUP_ID = "send_mail_group_id";

    /** nkメール送信ユーザID */
    public static final String COLUMN_NAME_SEND_MAIL_USER_ID = "send_mail_user_id";

    /** nkメール送信区分 */
    public static final String COLUMN_NAME_SEND_MAIL_TYPE = "send_mail_type";

    /** メッセージグループキー */
    private String message_group_key;
    /** nkメール送信先グループID */
    private String send_mail_group_id;
    /** nkメール送信ユーザID */
    private String send_mail_user_id;
    /** nkメール送信区分 */
    private Integer send_mail_type;


    /**
     * Zm011メッセージグループマスタエンティティ コンストラクタ
     */
    public MstrMessageGroup() {
    }

    /**
     * Zm011メッセージグループマスタエンティティ コンストラクタ
     * @param message_group_key メッセージグループキー
     * @param send_mail_group_id nkメール送信先グループID
     * @param version 更新バージョン
     */
    public MstrMessageGroup(String message_group_key, String send_mail_group_id, int version) {
        this.message_group_key = message_group_key;
        this.send_mail_group_id = send_mail_group_id;
        super.setVersion(version);
    }

    /**
     * Zm011メッセージグループマスタエンティティ コンストラクタ
     * @param message_group_key メッセージグループキー
     * @param send_mail_group_id nkメール送信先グループID
     * @param send_mail_user_id nkメール送信ユーザID
     * @param send_mail_type nkメール送信区分
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
    public MstrMessageGroup(String message_group_key, String send_mail_group_id, String send_mail_user_id, Integer send_mail_type, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.message_group_key = message_group_key;
        this.send_mail_group_id = send_mail_group_id;
        this.send_mail_user_id = send_mail_user_id;
        this.send_mail_type = send_mail_type;
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
     * メッセージグループキーを取得
     * @return メッセージグループキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getMessage_group_key() {
        return this.message_group_key;
    }

    /**
     * メッセージグループキーを設定
     * @param message_group_key メッセージグループキー (null不可)
     */
    public void setMessage_group_key(String message_group_key) {
        this.message_group_key = message_group_key;
    }

    /**
     * nkメール送信先グループIDを取得
     * @return nkメール送信先グループID
     */
    @Column(nullable = false, length = 38)
    public String getSend_mail_group_id() {
        return this.send_mail_group_id;
    }

    /**
     * nkメール送信先グループIDを設定
     * @param send_mail_group_id nkメール送信先グループID (null不可)
     */
    public void setSend_mail_group_id(String send_mail_group_id) {
        this.send_mail_group_id = send_mail_group_id;
    }

    /**
     * nkメール送信ユーザIDを取得
     * @return nkメール送信ユーザID
     */
    @Column(length = 20)
    public String getSend_mail_user_id() {
        return this.send_mail_user_id;
    }

    /**
     * nkメール送信ユーザIDを設定
     * @param send_mail_user_id nkメール送信ユーザID
     */
    public void setSend_mail_user_id(String send_mail_user_id) {
        this.send_mail_user_id = send_mail_user_id;
    }

    /**
     * nkメール送信区分を取得
     * @return nkメール送信区分
     */
    @Column(length = 1)
    public Integer getSend_mail_type() {
        return this.send_mail_type;
    }

    /**
     * nkメール送信区分を設定
     * @param send_mail_type nkメール送信区分
     */
    public void setSend_mail_type(Integer send_mail_type) {
        this.send_mail_type = send_mail_type;
    }

}
