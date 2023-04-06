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
 * Zm112設備I/Fマスタ エンティティ
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
@Table(name = "mstr_equipment_if")
@NamedQueries({
        @NamedQuery(name = "MstrEquipmentIf.findAll", query = "SELECT p FROM MstrEquipmentIf p"),
        @NamedQuery(name = "MstrEquipmentIf.findByPK", query = "SELECT p FROM MstrEquipmentIf p WHERE p.equipment_if_key = :equipment_if_key")
})
public class MstrEquipmentIf extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_equipment_if";

    /** 設備IFキー */
    public static final String COLUMN_NAME_EQUIPMENT_IF_KEY = "equipment_if_key";

    /** 設備キー (FK) */
    public static final String COLUMN_NAME_EQUIPMENT_KEY = "equipment_key";

    /** nk設備ID */
    public static final String COLUMN_NAME_EQUIPMENT_ID = "equipment_id";

    /** nk設備IFID */
    public static final String COLUMN_NAME_EQUIPMENT_IF_ID = "equipment_if_id";

    /** 設備IFタイプ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_TYPE = "equipment_if_type";

    /** 設備IFマシン名 */
    public static final String COLUMN_NAME_EQUIPMENT_IF_COMPUTER_NAME = "equipment_if_computer_name";

    /** 設備IFサブタイプ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_SUB_TYPE = "equipment_if_sub_type";

    /** IF設定オプション１ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_OPTION1 = "equipment_if_option1";

    /** IF設定オプション２ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_OPTION2 = "equipment_if_option2";

    /** IF設定オプション３ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_OPTION3 = "equipment_if_option3";

    /** IF設定オプション４ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_OPTION4 = "equipment_if_option4";

    /** IF設定オプション５ */
    public static final String COLUMN_NAME_EQUIPMENT_IF_OPTION5 = "equipment_if_option5";

    /** 設備IFキー */
    private String equipment_if_key;
    /** 設備キー (FK) */
    private String equipment_key;
    /** nk設備ID */
    private String equipment_id;
    /** nk設備IFID */
    private String equipment_if_id;
    /** 設備IFタイプ */
    private String equipment_if_type;
    /** 設備IFマシン名 */
    private String equipment_if_computer_name;
    /** 設備IFサブタイプ */
    private String equipment_if_sub_type;
    /** IF設定オプション１ */
    private String equipment_if_option1;
    /** IF設定オプション２ */
    private String equipment_if_option2;
    /** IF設定オプション３ */
    private String equipment_if_option3;
    /** IF設定オプション４ */
    private String equipment_if_option4;
    /** IF設定オプション５ */
    private String equipment_if_option5;


    /**
     * Zm112設備I/Fマスタエンティティ コンストラクタ
     */
    public MstrEquipmentIf() {
    }

    /**
     * Zm112設備I/Fマスタエンティティ コンストラクタ
     * @param equipment_if_key 設備IFキー
     * @param equipment_key 設備キー (FK)
     * @param equipment_id nk設備ID
     * @param equipment_if_id nk設備IFID
     * @param equipment_if_computer_name 設備IFマシン名
     * @param equipment_if_sub_type 設備IFサブタイプ
     * @param equipment_if_option1 IF設定オプション１
     * @param equipment_if_option2 IF設定オプション２
     * @param equipment_if_option3 IF設定オプション３
     * @param equipment_if_option4 IF設定オプション４
     * @param equipment_if_option5 IF設定オプション５
     * @param version 更新バージョン
     */
    public MstrEquipmentIf(String equipment_if_key, String equipment_key, String equipment_id, String equipment_if_id, String equipment_if_computer_name, String equipment_if_sub_type, String equipment_if_option1, String equipment_if_option2, String equipment_if_option3, String equipment_if_option4, String equipment_if_option5, int version) {
        this.equipment_if_key = equipment_if_key;
        this.equipment_key = equipment_key;
        this.equipment_id = equipment_id;
        this.equipment_if_id = equipment_if_id;
        this.equipment_if_computer_name = equipment_if_computer_name;
        this.equipment_if_sub_type = equipment_if_sub_type;
        this.equipment_if_option1 = equipment_if_option1;
        this.equipment_if_option2 = equipment_if_option2;
        this.equipment_if_option3 = equipment_if_option3;
        this.equipment_if_option4 = equipment_if_option4;
        this.equipment_if_option5 = equipment_if_option5;
        super.setVersion(version);
    }

    /**
     * Zm112設備I/Fマスタエンティティ コンストラクタ
     * @param equipment_if_key 設備IFキー
     * @param equipment_key 設備キー (FK)
     * @param equipment_id nk設備ID
     * @param equipment_if_id nk設備IFID
     * @param equipment_if_type 設備IFタイプ
     * @param equipment_if_computer_name 設備IFマシン名
     * @param equipment_if_sub_type 設備IFサブタイプ
     * @param equipment_if_option1 IF設定オプション１
     * @param equipment_if_option2 IF設定オプション２
     * @param equipment_if_option3 IF設定オプション３
     * @param equipment_if_option4 IF設定オプション４
     * @param equipment_if_option5 IF設定オプション５
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
    public MstrEquipmentIf(String equipment_if_key, String equipment_key, String equipment_id, String equipment_if_id, String equipment_if_type, String equipment_if_computer_name, String equipment_if_sub_type, String equipment_if_option1, String equipment_if_option2, String equipment_if_option3, String equipment_if_option4, String equipment_if_option5, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.equipment_if_key = equipment_if_key;
        this.equipment_key = equipment_key;
        this.equipment_id = equipment_id;
        this.equipment_if_id = equipment_if_id;
        this.equipment_if_type = equipment_if_type;
        this.equipment_if_computer_name = equipment_if_computer_name;
        this.equipment_if_sub_type = equipment_if_sub_type;
        this.equipment_if_option1 = equipment_if_option1;
        this.equipment_if_option2 = equipment_if_option2;
        this.equipment_if_option3 = equipment_if_option3;
        this.equipment_if_option4 = equipment_if_option4;
        this.equipment_if_option5 = equipment_if_option5;
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
     * 設備IFキーを取得
     * @return 設備IFキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getEquipment_if_key() {
        return this.equipment_if_key;
    }

    /**
     * 設備IFキーを設定
     * @param equipment_if_key 設備IFキー (null不可)
     */
    public void setEquipment_if_key(String equipment_if_key) {
        this.equipment_if_key = equipment_if_key;
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
     * nk設備IFIDを取得
     * @return nk設備IFID
     */
    @Column(nullable = false, length = 40)
    public String getEquipment_if_id() {
        return this.equipment_if_id;
    }

    /**
     * nk設備IFIDを設定
     * @param equipment_if_id nk設備IFID (null不可)
     */
    public void setEquipment_if_id(String equipment_if_id) {
        this.equipment_if_id = equipment_if_id;
    }

    /**
     * 設備IFタイプを取得
     * @return 設備IFタイプ
     */
    @Column(length = 50)
    public String getEquipment_if_type() {
        return this.equipment_if_type;
    }

    /**
     * 設備IFタイプを設定
     * @param equipment_if_type 設備IFタイプ
     */
    public void setEquipment_if_type(String equipment_if_type) {
        this.equipment_if_type = equipment_if_type;
    }

    /**
     * 設備IFマシン名を取得
     * @return 設備IFマシン名
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_computer_name() {
        return this.equipment_if_computer_name;
    }

    /**
     * 設備IFマシン名を設定
     * @param equipment_if_computer_name 設備IFマシン名 (null不可)
     */
    public void setEquipment_if_computer_name(String equipment_if_computer_name) {
        this.equipment_if_computer_name = equipment_if_computer_name;
    }

    /**
     * 設備IFサブタイプを取得
     * @return 設備IFサブタイプ
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_sub_type() {
        return this.equipment_if_sub_type;
    }

    /**
     * 設備IFサブタイプを設定
     * @param equipment_if_sub_type 設備IFサブタイプ (null不可)
     */
    public void setEquipment_if_sub_type(String equipment_if_sub_type) {
        this.equipment_if_sub_type = equipment_if_sub_type;
    }

    /**
     * IF設定オプション１を取得
     * @return IF設定オプション１
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_option1() {
        return this.equipment_if_option1;
    }

    /**
     * IF設定オプション１を設定
     * @param equipment_if_option1 IF設定オプション１ (null不可)
     */
    public void setEquipment_if_option1(String equipment_if_option1) {
        this.equipment_if_option1 = equipment_if_option1;
    }

    /**
     * IF設定オプション２を取得
     * @return IF設定オプション２
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_option2() {
        return this.equipment_if_option2;
    }

    /**
     * IF設定オプション２を設定
     * @param equipment_if_option2 IF設定オプション２ (null不可)
     */
    public void setEquipment_if_option2(String equipment_if_option2) {
        this.equipment_if_option2 = equipment_if_option2;
    }

    /**
     * IF設定オプション３を取得
     * @return IF設定オプション３
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_option3() {
        return this.equipment_if_option3;
    }

    /**
     * IF設定オプション３を設定
     * @param equipment_if_option3 IF設定オプション３ (null不可)
     */
    public void setEquipment_if_option3(String equipment_if_option3) {
        this.equipment_if_option3 = equipment_if_option3;
    }

    /**
     * IF設定オプション４を取得
     * @return IF設定オプション４
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_option4() {
        return this.equipment_if_option4;
    }

    /**
     * IF設定オプション４を設定
     * @param equipment_if_option4 IF設定オプション４ (null不可)
     */
    public void setEquipment_if_option4(String equipment_if_option4) {
        this.equipment_if_option4 = equipment_if_option4;
    }

    /**
     * IF設定オプション５を取得
     * @return IF設定オプション５
     */
    @Column(nullable = false, length = 50)
    public String getEquipment_if_option5() {
        return this.equipment_if_option5;
    }

    /**
     * IF設定オプション５を設定
     * @param equipment_if_option5 IF設定オプション５ (null不可)
     */
    public void setEquipment_if_option5(String equipment_if_option5) {
        this.equipment_if_option5 = equipment_if_option5;
    }

}
