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
 * Zm113設備I/Fパラメータマスタ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/10/28 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_equipment_if_parameter")
@NamedQueries({
        @NamedQuery(name = "MstrEquipmentIfParameter.findAll", query = "SELECT p FROM MstrEquipmentIfParameter p"),
        @NamedQuery(name = "MstrEquipmentIfParameter.findByPK", query = "SELECT p FROM MstrEquipmentIfParameter p WHERE p.equipment_if_parameter_key = :equipment_if_parameter_key")
})
public class MstrEquipmentIfParameter extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_equipment_if_parameter";

    /** 設備IFパラメータキー */
    public static final String COLUMN_NAME_EQUIPMENT_IF_PARAMETER_KEY = "equipment_if_parameter_key";

    /** 設備パラメータキー (FK) */
    public static final String COLUMN_NAME_EQUIPMENT_PARAMETER_KEY = "equipment_parameter_key";

    /** nk設備ID */
    public static final String COLUMN_NAME_EQUIPMENT_ID = "equipment_id";

    /** nkパラメータ項目ID */
    public static final String COLUMN_NAME_PARAMETER_ITEM_ID = "parameter_item_id";

    /** 設備IFキー (FK) */
    public static final String COLUMN_NAME_EQUIPMENT_IF_KEY = "equipment_if_key";

    /** 設備IFID */
    public static final String COLUMN_NAME_EQUIPMENT_IF_ID = "equipment_if_id";

    /** データアイテム */
    public static final String COLUMN_NAME_DATA_ITEM_ADDRESS = "data_item_address";

    /** 特殊変換処理区分 */
    public static final String COLUMN_NAME_SPECIAL_CONVERT_DIV = "special_convert_div";

    /** 設備IFパラメータキー */
    private String equipment_if_parameter_key;
    /** 設備パラメータキー (FK) */
    private String equipment_parameter_key;
    /** nk設備ID */
    private String equipment_id;
    /** nkパラメータ項目ID */
    private String parameter_item_id;
    /** 設備IFキー (FK) */
    private String equipment_if_key;
    /** 設備IFID */
    private String equipment_if_id;
    /** データアイテム */
    private String data_item_address;
    /** 特殊変換処理区分 */
    private int special_convert_div;


    /**
     * Zm113設備I/Fパラメータマスタエンティティ コンストラクタ
     */
    public MstrEquipmentIfParameter() {
    }

    /**
     * Zm113設備I/Fパラメータマスタエンティティ コンストラクタ
     * @param equipment_if_parameter_key 設備IFパラメータキー
     * @param equipment_parameter_key 設備パラメータキー (FK)
     * @param equipment_id nk設備ID
     * @param parameter_item_id nkパラメータ項目ID
     * @param equipment_if_key 設備IFキー (FK)
     * @param equipment_if_id 設備IFID
     * @param data_item_address データアイテム
     * @param special_convert_div 特殊変換処理区分
     * @param version 更新バージョン
     */
    public MstrEquipmentIfParameter(String equipment_if_parameter_key, String equipment_parameter_key, String equipment_id, String parameter_item_id, String equipment_if_key, String equipment_if_id, String data_item_address, int special_convert_div, int version) {
        this.equipment_if_parameter_key = equipment_if_parameter_key;
        this.equipment_parameter_key = equipment_parameter_key;
        this.equipment_id = equipment_id;
        this.parameter_item_id = parameter_item_id;
        this.equipment_if_key = equipment_if_key;
        this.equipment_if_id = equipment_if_id;
        this.data_item_address = data_item_address;
        this.special_convert_div = special_convert_div;
        super.setVersion(version);
    }

    /**
     * Zm113設備I/Fパラメータマスタエンティティ コンストラクタ
     * @param equipment_if_parameter_key 設備IFパラメータキー
     * @param equipment_parameter_key 設備パラメータキー (FK)
     * @param equipment_id nk設備ID
     * @param parameter_item_id nkパラメータ項目ID
     * @param equipment_if_key 設備IFキー (FK)
     * @param equipment_if_id 設備IFID
     * @param data_item_address データアイテム
     * @param special_convert_div 特殊変換処理区分
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
    public MstrEquipmentIfParameter(String equipment_if_parameter_key, String equipment_parameter_key, String equipment_id, String parameter_item_id, String equipment_if_key, String equipment_if_id, String data_item_address, int special_convert_div, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.equipment_if_parameter_key = equipment_if_parameter_key;
        this.equipment_parameter_key = equipment_parameter_key;
        this.equipment_id = equipment_id;
        this.parameter_item_id = parameter_item_id;
        this.equipment_if_key = equipment_if_key;
        this.equipment_if_id = equipment_if_id;
        this.data_item_address = data_item_address;
        this.special_convert_div = special_convert_div;
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
     * 設備IFパラメータキーを取得
     * @return 設備IFパラメータキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getEquipment_if_parameter_key() {
        return this.equipment_if_parameter_key;
    }

    /**
     * 設備IFパラメータキーを設定
     * @param equipment_if_parameter_key 設備IFパラメータキー (null不可)
     */
    public void setEquipment_if_parameter_key(String equipment_if_parameter_key) {
        this.equipment_if_parameter_key = equipment_if_parameter_key;
    }

    /**
     * 設備パラメータキー (FK)を取得
     * @return 設備パラメータキー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getEquipment_parameter_key() {
        return this.equipment_parameter_key;
    }

    /**
     * 設備パラメータキー (FK)を設定
     * @param equipment_parameter_key 設備パラメータキー (FK) (null不可)
     */
    public void setEquipment_parameter_key(String equipment_parameter_key) {
        this.equipment_parameter_key = equipment_parameter_key;
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
     * 設備IFキー (FK)を取得
     * @return 設備IFキー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getEquipment_if_key() {
        return this.equipment_if_key;
    }

    /**
     * 設備IFキー (FK)を設定
     * @param equipment_if_key 設備IFキー (FK) (null不可)
     */
    public void setEquipment_if_key(String equipment_if_key) {
        this.equipment_if_key = equipment_if_key;
    }

    /**
     * 設備IFIDを取得
     * @return 設備IFID
     */
    @Column(nullable = false, length = 40)
    public String getEquipment_if_id() {
        return this.equipment_if_id;
    }

    /**
     * 設備IFIDを設定
     * @param equipment_if_id 設備IFID (null不可)
     */
    public void setEquipment_if_id(String equipment_if_id) {
        this.equipment_if_id = equipment_if_id;
    }

    /**
     * データアイテムを取得
     * @return データアイテム
     */
    @Column(nullable = false, length = 50)
    public String getData_item_address() {
        return this.data_item_address;
    }

    /**
     * データアイテムを設定
     * @param data_item_address データアイテム (null不可)
     */
    public void setData_item_address(String data_item_address) {
        this.data_item_address = data_item_address;
    }

    /**
     * 特殊変換処理区分を取得
     * @return 特殊変換処理区分
     */
    @Column(nullable = false, length = 1)
    public int getSpecial_convert_div() {
        return this.special_convert_div;
    }

    /**
     * 特殊変換処理区分を設定
     * @param special_convert_div 特殊変換処理区分 (null不可)
     */
    public void setSpecial_convert_div(int special_convert_div) {
        this.special_convert_div = special_convert_div;
    }

}
