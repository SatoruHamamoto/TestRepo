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
 * Zm023権限階層定義マスタ エンティティ
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
@Table(name = "mstr_position")
@NamedQueries({
        @NamedQuery(name = "MstrPosition.findAll", query = "SELECT p FROM MstrPosition p"),
        @NamedQuery(name = "MstrPosition.findByPK", query = "SELECT p FROM MstrPosition p WHERE p.position_key = :position_key")
})
public class MstrPosition extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_position";

    /** 階層キー */
    public static final String COLUMN_NAME_POSITION_KEY = "position_key";

    /** nk階層ID */
    public static final String COLUMN_NAME_POSITION_ID = "position_id";

    /** 階層名 */
    public static final String COLUMN_NAME_POSITION_NAME = "position_name";

    /** 説明 */
    public static final String COLUMN_NAME_EXPLANATION = "explanation";

    /** 階層キー */
    private String position_key;
    /** nk階層ID */
    private String position_id;
    /** 階層名 */
    private String position_name;
    /** 説明 */
    private String explanation;


    /**
     * Zm023権限階層定義マスタエンティティ コンストラクタ
     */
    public MstrPosition() {
    }

    /**
     * Zm023権限階層定義マスタエンティティ コンストラクタ
     * @param position_key 階層キー
     * @param position_id nk階層ID
     * @param version 更新バージョン
     */
    public MstrPosition(String position_key, String position_id, int version) {
        this.position_key = position_key;
        this.position_id = position_id;
        super.setVersion(version);
    }

    /**
     * Zm023権限階層定義マスタエンティティ コンストラクタ
     * @param position_key 階層キー
     * @param position_id nk階層ID
     * @param position_name 階層名
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
    public MstrPosition(String position_key, String position_id, String position_name, String explanation, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.position_key = position_key;
        this.position_id = position_id;
        this.position_name = position_name;
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
     * 階層キーを取得
     * @return 階層キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPosition_key() {
        return this.position_key;
    }

    /**
     * 階層キーを設定
     * @param position_key 階層キー (null不可)
     */
    public void setPosition_key(String position_key) {
        this.position_key = position_key;
    }

    /**
     * nk階層IDを取得
     * @return nk階層ID
     */
    @Column(nullable = false, length = 32)
    public String getPosition_id() {
        return this.position_id;
    }

    /**
     * nk階層IDを設定
     * @param position_id nk階層ID (null不可)
     */
    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    /**
     * 階層名を取得
     * @return 階層名
     */
    @Column(length = 20)
    public String getPosition_name() {
        return this.position_name;
    }

    /**
     * 階層名を設定
     * @param position_name 階層名
     */
    public void setPosition_name(String position_name) {
        this.position_name = position_name;
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
