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
 * Zm020ユーザマスタ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/11/11 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_person")
@NamedQueries({
        @NamedQuery(name = "MstrPerson.findAll", query = "SELECT p FROM MstrPerson p"),
        @NamedQuery(name = "MstrPerson.findByPK", query = "SELECT p FROM MstrPerson p WHERE p.user_key = :user_key")
})
public class MstrPerson extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_person";

    /** ユーザーキー */
    public static final String COLUMN_NAME_USER_KEY = "user_key";

    /** nkユーザID */
    public static final String COLUMN_NAME_USER_ID = "user_id";

    /** ユーザ名 */
    public static final String COLUMN_NAME_USER_NAME = "user_name";

    /** メールアドレス */
    public static final String COLUMN_NAME_MAIL_ADDRESS = "mail_address";

    /** 説明1（ふりがな） */
    public static final String COLUMN_NAME_EXPLANATION1 = "explanation1";

    /** 説明2（略名） */
    public static final String COLUMN_NAME_EXPLANATION2 = "explanation2";

    /** ユーザーキー */
    private String user_key;
    /** nkユーザID */
    private String user_id;
    /** ユーザ名 */
    private String user_name;
    /** メールアドレス */
    private String mail_address;
    /** 説明1（ふりがな） */
    private String explanation1;
    /** 説明2（略名） */
    private String explanation2;


    /**
     * Zm020ユーザマスタエンティティ コンストラクタ
     */
    public MstrPerson() {
    }

    /**
     * Zm020ユーザマスタエンティティ コンストラクタ
     * @param user_key ユーザーキー
     * @param user_id nkユーザID
     * @param version 更新バージョン
     */
    public MstrPerson(String user_key, String user_id, int version) {
        this.user_key = user_key;
        this.user_id = user_id;
        super.setVersion(version);
    }

    /**
     * Zm020ユーザマスタエンティティ コンストラクタ
     * @param user_key ユーザーキー
     * @param user_id nkユーザID
     * @param user_name ユーザ名
     * @param mail_address メールアドレス
     * @param explanation1 説明1（ふりがな）
     * @param explanation2 説明2（略名）
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
    public MstrPerson(String user_key, String user_id, String user_name, String mail_address, String explanation1, String explanation2, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.user_key = user_key;
        this.user_id = user_id;
        this.user_name = user_name;
        this.mail_address = mail_address;
        this.explanation1 = explanation1;
        this.explanation2 = explanation2;
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
     * ユーザーキーを取得
     * @return ユーザーキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getUser_key() {
        return this.user_key;
    }

    /**
     * ユーザーキーを設定
     * @param user_key ユーザーキー (null不可)
     */
    public void setUser_key(String user_key) {
        this.user_key = user_key;
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
     * ユーザ名を取得
     * @return ユーザ名
     */
    @Column(length = 40)
    public String getUser_name() {
        return this.user_name;
    }

    /**
     * ユーザ名を設定
     * @param user_name ユーザ名
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * メールアドレスを取得
     * @return メールアドレス
     */
    @Column(length = 320)
    public String getMail_address() {
        return this.mail_address;
    }

    /**
     * メールアドレスを設定
     * @param mail_address メールアドレス
     */
    public void setMail_address(String mail_address) {
        this.mail_address = mail_address;
    }

    /**
     * 説明1（ふりがな）を取得
     * @return 説明1（ふりがな）
     */
    @Column(length = 40)
    public String getExplanation1() {
        return this.explanation1;
    }

    /**
     * 説明1（ふりがな）を設定
     * @param explanation1 説明1（ふりがな）
     */
    public void setExplanation1(String explanation1) {
        this.explanation1 = explanation1;
    }

    /**
     * 説明2（略名）を取得
     * @return 説明2（略名）
     */
    @Column(length = 40)
    public String getExplanation2() {
        return this.explanation2;
    }

    /**
     * 説明2（略名）を設定
     * @param explanation2 説明2（略名）
     */
    public void setExplanation2(String explanation2) {
        this.explanation2 = explanation2;
    }

}
