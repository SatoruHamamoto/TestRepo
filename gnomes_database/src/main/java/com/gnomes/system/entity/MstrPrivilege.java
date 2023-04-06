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
 * Zm021権限(作業権限)定義マスタ エンティティ
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
@Table(name = "mstr_privilege")
@NamedQueries({
        @NamedQuery(name = "MstrPrivilege.findAll", query = "SELECT p FROM MstrPrivilege p"),
        @NamedQuery(name = "MstrPrivilege.findByPK", query = "SELECT p FROM MstrPrivilege p WHERE p.privilege_key = :privilege_key")
})
public class MstrPrivilege extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_privilege";

    /** 権限キー */
    public static final String COLUMN_NAME_PRIVILEGE_KEY = "privilege_key";

    /** nk権限ID */
    public static final String COLUMN_NAME_PRIVILEGE_ID = "privilege_id";

    /** 権限名 */
    public static final String COLUMN_NAME_PRIVILEGE_NAME = "privilege_name";

    /** 説明 */
    public static final String COLUMN_NAME_EXPLANATION = "explanation";

    /** 表示順 */
    public static final String COLUMN_NAME_DISPLAY_ORDER = "display_order";

    /** 権限キー */
    private String privilege_key;
    /** nk権限ID */
    private String privilege_id;
    /** 権限名 */
    private String privilege_name;
    /** 説明 */
    private String explanation;
    /** 表示順 */
    private Integer display_order;


    /**
     * Zm021権限(作業権限)定義マスタエンティティ コンストラクタ
     */
    public MstrPrivilege() {
    }

    /**
     * Zm021権限(作業権限)定義マスタエンティティ コンストラクタ
     * @param privilege_key 権限キー
     * @param privilege_id nk権限ID
     * @param version 更新バージョン
     */
    public MstrPrivilege(String privilege_key, String privilege_id, int version) {
        this.privilege_key = privilege_key;
        this.privilege_id = privilege_id;
        super.setVersion(version);
    }

    /**
     * Zm021権限(作業権限)定義マスタエンティティ コンストラクタ
     * @param privilege_key 権限キー
     * @param privilege_id nk権限ID
     * @param privilege_name 権限名
     * @param explanation 説明
     * @param display_order 表示順
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
    public MstrPrivilege(String privilege_key, String privilege_id, String privilege_name, String explanation, Integer display_order, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.privilege_key = privilege_key;
        this.privilege_id = privilege_id;
        this.privilege_name = privilege_name;
        this.explanation = explanation;
        this.display_order = display_order;
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
     * 権限キーを取得
     * @return 権限キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPrivilege_key() {
        return this.privilege_key;
    }

    /**
     * 権限キーを設定
     * @param privilege_key 権限キー (null不可)
     */
    public void setPrivilege_key(String privilege_key) {
        this.privilege_key = privilege_key;
    }

    /**
     * nk権限IDを取得
     * @return nk権限ID
     */
    @Column(nullable = false, length = 32)
    public String getPrivilege_id() {
        return this.privilege_id;
    }

    /**
     * nk権限IDを設定
     * @param privilege_id nk権限ID (null不可)
     */
    public void setPrivilege_id(String privilege_id) {
        this.privilege_id = privilege_id;
    }

    /**
     * 権限名を取得
     * @return 権限名
     */
    @Column(length = 20)
    public String getPrivilege_name() {
        return this.privilege_name;
    }

    /**
     * 権限名を設定
     * @param privilege_name 権限名
     */
    public void setPrivilege_name(String privilege_name) {
        this.privilege_name = privilege_name;
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

    /**
     * 表示順を取得
     * @return 表示順
     */
    @Column(length = 4)
    public Integer getDisplay_order() {
        return this.display_order;
    }

    /**
     * 表示順を設定
     * @param display_order 表示順
     */
    public void setDisplay_order(Integer display_order) {
        this.display_order = display_order;
    }

}
