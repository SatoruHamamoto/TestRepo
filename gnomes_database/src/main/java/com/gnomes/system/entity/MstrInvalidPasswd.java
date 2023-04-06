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
 * Zm031パスワード禁止文字マスタ エンティティ
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
@Table(name = "mstr_invalid_passwd")
@NamedQueries({
        @NamedQuery(name = "MstrInvalidPasswd.findAll", query = "SELECT p FROM MstrInvalidPasswd p"),
        @NamedQuery(name = "MstrInvalidPasswd.findByPK", query = "SELECT p FROM MstrInvalidPasswd p WHERE p.invalid_passwd_key = :invalid_passwd_key")
})
public class MstrInvalidPasswd extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_invalid_passwd";

    /** パスワード使用禁止文字列キー */
    public static final String COLUMN_NAME_INVALID_PASSWD_KEY = "invalid_passwd_key";

    /** nk禁止文字列名 */
    public static final String COLUMN_NAME_DISAPPROVE_CHARACTOR_NAME = "disapprove_charactor_name";

    /** 禁止文字列 */
    public static final String COLUMN_NAME_DISAPPROVE_CHARACTOR = "disapprove_charactor";

    /** パスワード使用禁止文字列キー */
    private String invalid_passwd_key;
    /** nk禁止文字列名 */
    private String disapprove_charactor_name;
    /** 禁止文字列 */
    private String disapprove_charactor;


    /**
     * Zm031パスワード禁止文字マスタエンティティ コンストラクタ
     */
    public MstrInvalidPasswd() {
    }

    /**
     * Zm031パスワード禁止文字マスタエンティティ コンストラクタ
     * @param invalid_passwd_key パスワード使用禁止文字列キー
     * @param disapprove_charactor_name nk禁止文字列名
     * @param version 更新バージョン
     */
    public MstrInvalidPasswd(String invalid_passwd_key, String disapprove_charactor_name, int version) {
        this.invalid_passwd_key = invalid_passwd_key;
        this.disapprove_charactor_name = disapprove_charactor_name;
        super.setVersion(version);
    }

    /**
     * Zm031パスワード禁止文字マスタエンティティ コンストラクタ
     * @param invalid_passwd_key パスワード使用禁止文字列キー
     * @param disapprove_charactor_name nk禁止文字列名
     * @param disapprove_charactor 禁止文字列
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
    public MstrInvalidPasswd(String invalid_passwd_key, String disapprove_charactor_name, String disapprove_charactor, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.invalid_passwd_key = invalid_passwd_key;
        this.disapprove_charactor_name = disapprove_charactor_name;
        this.disapprove_charactor = disapprove_charactor;
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
     * パスワード使用禁止文字列キーを取得
     * @return パスワード使用禁止文字列キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getInvalid_passwd_key() {
        return this.invalid_passwd_key;
    }

    /**
     * パスワード使用禁止文字列キーを設定
     * @param invalid_passwd_key パスワード使用禁止文字列キー (null不可)
     */
    public void setInvalid_passwd_key(String invalid_passwd_key) {
        this.invalid_passwd_key = invalid_passwd_key;
    }

    /**
     * nk禁止文字列名を取得
     * @return nk禁止文字列名
     */
    @Column(nullable = false, length = 20)
    public String getDisapprove_charactor_name() {
        return this.disapprove_charactor_name;
    }

    /**
     * nk禁止文字列名を設定
     * @param disapprove_charactor_name nk禁止文字列名 (null不可)
     */
    public void setDisapprove_charactor_name(String disapprove_charactor_name) {
        this.disapprove_charactor_name = disapprove_charactor_name;
    }

    /**
     * 禁止文字列を取得
     * @return 禁止文字列
     */
    @Column(length = 256)
    public String getDisapprove_charactor() {
        return this.disapprove_charactor;
    }

    /**
     * 禁止文字列を設定
     * @param disapprove_charactor 禁止文字列
     */
    public void setDisapprove_charactor(String disapprove_charactor) {
        this.disapprove_charactor = disapprove_charactor;
    }

}
