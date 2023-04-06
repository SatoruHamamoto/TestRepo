package com.gnomes.equipment.entity;

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
 * Zm110設備マスタ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_equipment")
@NamedQueries({
        @NamedQuery(name = "MstrEquipment.findAll", query = "SELECT p FROM MstrEquipment p"),
        @NamedQuery(name = "MstrEquipment.findByPK", query = "SELECT p FROM MstrEquipment p WHERE p.equipment_key = :equipment_key")
})
public class MstrEquipment extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_equipment";

    /** 設備キー */
    public static final String COLUMN_NAME_EQUIPMENT_KEY = "equipment_key";

    /** nk設備ID */
    public static final String COLUMN_NAME_EQUIPMENT_ID = "equipment_id";

    /** 設備名 */
    public static final String COLUMN_NAME_EQUIPMENT_NAME = "equipment_name";

    /** 表示順序 */
    public static final String COLUMN_NAME_DISPLAY_ORDER = "display_order";

    /** 設備キー */
    private String equipment_key;
    /** nk設備ID */
    private String equipment_id;
    /** 設備名 */
    private String equipment_name;
    /** 表示順序 */
    private Integer display_order;


    /**
     * Zm110設備マスタエンティティ コンストラクタ
     */
    public MstrEquipment() {
    }

    /**
     * Zm110設備マスタエンティティ コンストラクタ
     * @param equipment_key 設備キー
     * @param equipment_id nk設備ID
     * @param equipment_name 設備名
     * @param version 更新バージョン
     */
    public MstrEquipment(String equipment_key, String equipment_id, String equipment_name, int version) {
        this.equipment_key = equipment_key;
        this.equipment_id = equipment_id;
        this.equipment_name = equipment_name;
        super.setVersion(version);
    }

    /**
     * Zm110設備マスタエンティティ コンストラクタ
     * @param equipment_key 設備キー
     * @param equipment_id nk設備ID
     * @param equipment_name 設備名
     * @param display_order 表示順序
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
    public MstrEquipment(String equipment_key, String equipment_id, String equipment_name, Integer display_order, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.equipment_key = equipment_key;
        this.equipment_id = equipment_id;
        this.equipment_name = equipment_name;
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
     * 設備キーを取得
     * @return 設備キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getEquipment_key() {
        return this.equipment_key;
    }

    /**
     * 設備キーを設定
     * @param equipment_key 設備キー (null不可)
     */
    public void setEquipment_key(String equipment_key) {
        this.equipment_key = equipment_key;
    }

    /**
     * nk設備IDを取得
     * @return nk設備ID
     */
    @Column(nullable = false, length = 20)
    public String getEquipment_id() {
        return this.equipment_id;
    }

    /**
     * nk設備IDを設定
     * @param equipment_id nk設備ID (null不可)
     */
    public void setEquipment_id(String equipment_id) {
        this.equipment_id = equipment_id;
    }

    /**
     * 設備名を取得
     * @return 設備名
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_name() {
        return this.equipment_name;
    }

    /**
     * 設備名を設定
     * @param equipment_name 設備名 (null不可)
     */
    public void setEquipment_name(String equipment_name) {
        this.equipment_name = equipment_name;
    }

    /**
     * 表示順序を取得
     * @return 表示順序
     */
    @Column(length = 11)
    public Integer getDisplay_order() {
        return this.display_order;
    }

    /**
     * 表示順序を設定
     * @param display_order 表示順序
     */
    public void setDisplay_order(Integer display_order) {
        this.display_order = display_order;
    }

}
