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
 * Zm006パトランプマスタ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/04/16 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_patlamp")
@NamedQueries({
        @NamedQuery(name = "MstrPatlamp.findAll", query = "SELECT p FROM MstrPatlamp p"),
        @NamedQuery(name = "MstrPatlamp.findByPK", query = "SELECT p FROM MstrPatlamp p WHERE p.patlamp_key = :patlamp_key")
})
public class MstrPatlamp extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_patlamp";

    /** パトランプキー */
    public static final String COLUMN_NAME_PATLAMP_KEY = "patlamp_key";

    /** nkパトランプID */
    public static final String COLUMN_NAME_PATLAMP_ID = "patlamp_id";

    /** パトランプ名 */
    public static final String COLUMN_NAME_PATLAMP_NAME = "patlamp_name";

    /** パトランプ機種ID */
    public static final String COLUMN_NAME_PATLAMP_MODEL_ID = "patlamp_model_id";

    /** IPアドレス */
    public static final String COLUMN_NAME_IP_ADDRESS = "ip_address";

    /** 点灯鳴動パラメーター文字列01 */
    public static final String COLUMN_NAME_LIGHT_SOUND_PARAMETER_STRING_01 = "light_sound_parameter_string_01";

    /** 点灯鳴動パラメーター文字列02 */
    public static final String COLUMN_NAME_LIGHT_SOUND_PARAMETER_STRING_02 = "light_sound_parameter_string_02";

    /** 点灯鳴動パラメーター文字列03 */
    public static final String COLUMN_NAME_LIGHT_SOUND_PARAMETER_STRING_03 = "light_sound_parameter_string_03";

    /** 点灯鳴動パラメーター文字列04 */
    public static final String COLUMN_NAME_LIGHT_SOUND_PARAMETER_STRING_04 = "light_sound_parameter_string_04";

    /** 点灯鳴動パラメーター文字列05 */
    public static final String COLUMN_NAME_LIGHT_SOUND_PARAMETER_STRING_05 = "light_sound_parameter_string_05";

    /** パトランプキー */
    private String patlamp_key;
    /** nkパトランプID */
    private String patlamp_id;
    /** パトランプ名 */
    private String patlamp_name;
    /** パトランプ機種ID */
    private String patlamp_model_id;
    /** IPアドレス */
    private String ip_address;
    /** 点灯鳴動パラメーター文字列01 */
    private String light_sound_parameter_string_01;
    /** 点灯鳴動パラメーター文字列02 */
    private String light_sound_parameter_string_02;
    /** 点灯鳴動パラメーター文字列03 */
    private String light_sound_parameter_string_03;
    /** 点灯鳴動パラメーター文字列04 */
    private String light_sound_parameter_string_04;
    /** 点灯鳴動パラメーター文字列05 */
    private String light_sound_parameter_string_05;


    /**
     * Zm006パトランプマスタエンティティ コンストラクタ
     */
    public MstrPatlamp() {
    }

    /**
     * Zm006パトランプマスタエンティティ コンストラクタ
     * @param patlamp_key パトランプキー
     * @param patlamp_id nkパトランプID
     * @param patlamp_name パトランプ名
     * @param patlamp_model_id パトランプ機種ID
     * @param ip_address IPアドレス
     * @param version 更新バージョン
     */
    public MstrPatlamp(String patlamp_key, String patlamp_id, String patlamp_name, String patlamp_model_id, String ip_address, int version) {
        this.patlamp_key = patlamp_key;
        this.patlamp_id = patlamp_id;
        this.patlamp_name = patlamp_name;
        this.patlamp_model_id = patlamp_model_id;
        this.ip_address = ip_address;
        super.setVersion(version);
    }

    /**
     * Zm006パトランプマスタエンティティ コンストラクタ
     * @param patlamp_key パトランプキー
     * @param patlamp_id nkパトランプID
     * @param patlamp_name パトランプ名
     * @param patlamp_model_id パトランプ機種ID
     * @param ip_address IPアドレス
     * @param light_sound_parameter_string_01 点灯鳴動パラメーター文字列01
     * @param light_sound_parameter_string_02 点灯鳴動パラメーター文字列02
     * @param light_sound_parameter_string_03 点灯鳴動パラメーター文字列03
     * @param light_sound_parameter_string_04 点灯鳴動パラメーター文字列04
     * @param light_sound_parameter_string_05 点灯鳴動パラメーター文字列05
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
    public MstrPatlamp(String patlamp_key, String patlamp_id, String patlamp_name, String patlamp_model_id, String ip_address, String light_sound_parameter_string_01, String light_sound_parameter_string_02, String light_sound_parameter_string_03, String light_sound_parameter_string_04, String light_sound_parameter_string_05, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.patlamp_key = patlamp_key;
        this.patlamp_id = patlamp_id;
        this.patlamp_name = patlamp_name;
        this.patlamp_model_id = patlamp_model_id;
        this.ip_address = ip_address;
        this.light_sound_parameter_string_01 = light_sound_parameter_string_01;
        this.light_sound_parameter_string_02 = light_sound_parameter_string_02;
        this.light_sound_parameter_string_03 = light_sound_parameter_string_03;
        this.light_sound_parameter_string_04 = light_sound_parameter_string_04;
        this.light_sound_parameter_string_05 = light_sound_parameter_string_05;
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
     * パトランプキーを取得
     * @return パトランプキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPatlamp_key() {
        return this.patlamp_key;
    }

    /**
     * パトランプキーを設定
     * @param patlamp_key パトランプキー (null不可)
     */
    public void setPatlamp_key(String patlamp_key) {
        this.patlamp_key = patlamp_key;
    }

    /**
     * nkパトランプIDを取得
     * @return nkパトランプID
     */
    @Column(nullable = false, length = 20)
    public String getPatlamp_id() {
        return this.patlamp_id;
    }

    /**
     * nkパトランプIDを設定
     * @param patlamp_id nkパトランプID (null不可)
     */
    public void setPatlamp_id(String patlamp_id) {
        this.patlamp_id = patlamp_id;
    }

    /**
     * パトランプ名を取得
     * @return パトランプ名
     */
    @Column(nullable = false, length = 40)
    public String getPatlamp_name() {
        return this.patlamp_name;
    }

    /**
     * パトランプ名を設定
     * @param patlamp_name パトランプ名 (null不可)
     */
    public void setPatlamp_name(String patlamp_name) {
        this.patlamp_name = patlamp_name;
    }

    /**
     * パトランプ機種IDを取得
     * @return パトランプ機種ID
     */
    @Column(nullable = false, length = 20)
    public String getPatlamp_model_id() {
        return this.patlamp_model_id;
    }

    /**
     * パトランプ機種IDを設定
     * @param patlamp_model_id パトランプ機種ID (null不可)
     */
    public void setPatlamp_model_id(String patlamp_model_id) {
        this.patlamp_model_id = patlamp_model_id;
    }

    /**
     * IPアドレスを取得
     * @return IPアドレス
     */
    @Column(nullable = false, length = 40)
    public String getIp_address() {
        return this.ip_address;
    }

    /**
     * IPアドレスを設定
     * @param ip_address IPアドレス (null不可)
     */
    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    /**
     * 点灯鳴動パラメーター文字列01を取得
     * @return 点灯鳴動パラメーター文字列01
     */
    @Column(length = 100)
    public String getLight_sound_parameter_string_01() {
        return this.light_sound_parameter_string_01;
    }

    /**
     * 点灯鳴動パラメーター文字列01を設定
     * @param light_sound_parameter_string_01 点灯鳴動パラメーター文字列01
     */
    public void setLight_sound_parameter_string_01(String light_sound_parameter_string_01) {
        this.light_sound_parameter_string_01 = light_sound_parameter_string_01;
    }

    /**
     * 点灯鳴動パラメーター文字列02を取得
     * @return 点灯鳴動パラメーター文字列02
     */
    @Column(length = 100)
    public String getLight_sound_parameter_string_02() {
        return this.light_sound_parameter_string_02;
    }

    /**
     * 点灯鳴動パラメーター文字列02を設定
     * @param light_sound_parameter_string_02 点灯鳴動パラメーター文字列02
     */
    public void setLight_sound_parameter_string_02(String light_sound_parameter_string_02) {
        this.light_sound_parameter_string_02 = light_sound_parameter_string_02;
    }

    /**
     * 点灯鳴動パラメーター文字列03を取得
     * @return 点灯鳴動パラメーター文字列03
     */
    @Column(length = 100)
    public String getLight_sound_parameter_string_03() {
        return this.light_sound_parameter_string_03;
    }

    /**
     * 点灯鳴動パラメーター文字列03を設定
     * @param light_sound_parameter_string_03 点灯鳴動パラメーター文字列03
     */
    public void setLight_sound_parameter_string_03(String light_sound_parameter_string_03) {
        this.light_sound_parameter_string_03 = light_sound_parameter_string_03;
    }

    /**
     * 点灯鳴動パラメーター文字列04を取得
     * @return 点灯鳴動パラメーター文字列04
     */
    @Column(length = 100)
    public String getLight_sound_parameter_string_04() {
        return this.light_sound_parameter_string_04;
    }

    /**
     * 点灯鳴動パラメーター文字列04を設定
     * @param light_sound_parameter_string_04 点灯鳴動パラメーター文字列04
     */
    public void setLight_sound_parameter_string_04(String light_sound_parameter_string_04) {
        this.light_sound_parameter_string_04 = light_sound_parameter_string_04;
    }

    /**
     * 点灯鳴動パラメーター文字列05を取得
     * @return 点灯鳴動パラメーター文字列05
     */
    @Column(length = 100)
    public String getLight_sound_parameter_string_05() {
        return this.light_sound_parameter_string_05;
    }

    /**
     * 点灯鳴動パラメーター文字列05を設定
     * @param light_sound_parameter_string_05 点灯鳴動パラメーター文字列05
     */
    public void setLight_sound_parameter_string_05(String light_sound_parameter_string_05) {
        this.light_sound_parameter_string_05 = light_sound_parameter_string_05;
    }

}