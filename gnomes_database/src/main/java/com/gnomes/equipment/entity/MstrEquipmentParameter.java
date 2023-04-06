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
 * Zm111設備パラメータマスタ エンティティ
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
@Table(name = "mstr_equipment_parameter")
@NamedQueries({
        @NamedQuery(name = "MstrEquipmentParameter.findAll", query = "SELECT p FROM MstrEquipmentParameter p"),
        @NamedQuery(name = "MstrEquipmentParameter.findByPK", query = "SELECT p FROM MstrEquipmentParameter p WHERE p.equipment_parameter_key = :equipment_parameter_key")
})
public class MstrEquipmentParameter extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_equipment_parameter";

    /** 設備パラメータキー */
    public static final String COLUMN_NAME_EQUIPMENT_PARAMETER_KEY = "equipment_parameter_key";

    /** 設備キー (FK) */
    public static final String COLUMN_NAME_EQUIPMENT_KEY = "equipment_key";

    /** nk設備ID */
    public static final String COLUMN_NAME_EQUIPMENT_ID = "equipment_id";

    /** nkパラメータ項目ID */
    public static final String COLUMN_NAME_PARAMETER_ITEM_ID = "parameter_item_id";

    /** パラメータ項目名 */
    public static final String COLUMN_NAME_PARAMETER_ITEM_NAME = "parameter_item_name";

    /** 設備パラメータキー */
    private String equipment_parameter_key;
    /** 設備キー (FK) */
    private String equipment_key;
    /** nk設備ID */
    private String equipment_id;
    /** nkパラメータ項目ID */
    private String parameter_item_id;
    /** パラメータ項目名 */
    private String parameter_item_name;


    /**
     * Zm111設備パラメータマスタエンティティ コンストラクタ
     */
    public MstrEquipmentParameter() {
    }

    /**
     * Zm111設備パラメータマスタエンティティ コンストラクタ
     * @param equipment_parameter_key 設備パラメータキー
     * @param equipment_key 設備キー (FK)
     * @param equipment_id nk設備ID
     * @param parameter_item_id nkパラメータ項目ID
     * @param parameter_item_name パラメータ項目名
     * @param version 更新バージョン
     */
    public MstrEquipmentParameter(String equipment_parameter_key, String equipment_key, String equipment_id, String parameter_item_id, String parameter_item_name, int version) {
        this.equipment_parameter_key = equipment_parameter_key;
        this.equipment_key = equipment_key;
        this.equipment_id = equipment_id;
        this.parameter_item_id = parameter_item_id;
        this.parameter_item_name = parameter_item_name;
        super.setVersion(version);
    }

    /**
     * Zm111設備パラメータマスタエンティティ コンストラクタ
     * @param equipment_parameter_key 設備パラメータキー
     * @param equipment_key 設備キー (FK)
     * @param equipment_id nk設備ID
     * @param parameter_item_id nkパラメータ項目ID
     * @param parameter_item_name パラメータ項目名
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
    public MstrEquipmentParameter(String equipment_parameter_key, String equipment_key, String equipment_id, String parameter_item_id, String parameter_item_name, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.equipment_parameter_key = equipment_parameter_key;
        this.equipment_key = equipment_key;
        this.equipment_id = equipment_id;
        this.parameter_item_id = parameter_item_id;
        this.parameter_item_name = parameter_item_name;
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
     * 設備パラメータキーを取得
     * @return 設備パラメータキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getEquipment_parameter_key() {
        return this.equipment_parameter_key;
    }

    /**
     * 設備パラメータキーを設定
     * @param equipment_parameter_key 設備パラメータキー (null不可)
     */
    public void setEquipment_parameter_key(String equipment_parameter_key) {
        this.equipment_parameter_key = equipment_parameter_key;
    }

    /**
     * 設備キー (FK)を取得
     * @return 設備キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getEquipment_key() {
        return this.equipment_key;
    }

    /**
     * 設備キー (FK)を設定
     * @param equipment_key 設備キー (FK) (null不可)
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
     * nkパラメータ項目IDを取得
     * @return nkパラメータ項目ID
     */
    @Column(nullable = false, length = 50)
    public String getParameter_item_id() {
        return this.parameter_item_id;
    }

    /**
     * nkパラメータ項目IDを設定
     * @param parameter_item_id nkパラメータ項目ID (null不可)
     */
    public void setParameter_item_id(String parameter_item_id) {
        this.parameter_item_id = parameter_item_id;
    }

    /**
     * パラメータ項目名を取得
     * @return パラメータ項目名
     */
    @Column(nullable = false, length = 50)
    public String getParameter_item_name() {
        return this.parameter_item_name;
    }

    /**
     * パラメータ項目名を設定
     * @param parameter_item_name パラメータ項目名 (null不可)
     */
    public void setParameter_item_name(String parameter_item_name) {
        this.parameter_item_name = parameter_item_name;
    }

}
