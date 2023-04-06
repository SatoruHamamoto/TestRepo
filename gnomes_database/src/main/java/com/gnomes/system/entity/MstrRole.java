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
 * Zm022役割（ロール）定義マスタ エンティティ
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
@Table(name = "mstr_role")
@NamedQueries({
        @NamedQuery(name = "MstrRole.findAll", query = "SELECT p FROM MstrRole p"),
        @NamedQuery(name = "MstrRole.findByPK", query = "SELECT p FROM MstrRole p WHERE p.role_key = :role_key")
})
public class MstrRole extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_role";

    /** ロールキー */
    public static final String COLUMN_NAME_ROLE_KEY = "role_key";

    /** nkロールID */
    public static final String COLUMN_NAME_ROLE_ID = "role_id";

    /** ロール名 */
    public static final String COLUMN_NAME_ROLE_NAME = "role_name";

    /** 説明 */
    public static final String COLUMN_NAME_EXPLANATION = "explanation";

    /** ロールキー */
    private String role_key;
    /** nkロールID */
    private String role_id;
    /** ロール名 */
    private String role_name;
    /** 説明 */
    private String explanation;


    /**
     * Zm022役割（ロール）定義マスタエンティティ コンストラクタ
     */
    public MstrRole() {
    }

    /**
     * Zm022役割（ロール）定義マスタエンティティ コンストラクタ
     * @param role_key ロールキー
     * @param role_id nkロールID
     * @param version 更新バージョン
     */
    public MstrRole(String role_key, String role_id, int version) {
        this.role_key = role_key;
        this.role_id = role_id;
        super.setVersion(version);
    }

    /**
     * Zm022役割（ロール）定義マスタエンティティ コンストラクタ
     * @param role_key ロールキー
     * @param role_id nkロールID
     * @param role_name ロール名
     * @param explanation 説明
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
    public MstrRole(String role_key, String role_id, String role_name, String explanation, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.role_key = role_key;
        this.role_id = role_id;
        this.role_name = role_name;
        this.explanation = explanation;
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
     * ロールキーを取得
     * @return ロールキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getRole_key() {
        return this.role_key;
    }

    /**
     * ロールキーを設定
     * @param role_key ロールキー (null不可)
     */
    public void setRole_key(String role_key) {
        this.role_key = role_key;
    }

    /**
     * nkロールIDを取得
     * @return nkロールID
     */
    @Column(nullable = false, length = 32)
    public String getRole_id() {
        return this.role_id;
    }

    /**
     * nkロールIDを設定
     * @param role_id nkロールID (null不可)
     */
    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    /**
     * ロール名を取得
     * @return ロール名
     */
    @Column(length = 20)
    public String getRole_name() {
        return this.role_name;
    }

    /**
     * ロール名を設定
     * @param role_name ロール名
     */
    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    /**
     * 説明を取得
     * @return 説明
     */
    @Column(length = 40)
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * 説明を設定
     * @param explanation 説明
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
