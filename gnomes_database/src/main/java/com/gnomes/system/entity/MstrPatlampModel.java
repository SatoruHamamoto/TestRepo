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
 * Zm005パトランプ機種マスタ エンティティ
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
@Table(name = "mstr_patlamp_model")
@NamedQueries({
        @NamedQuery(name = "MstrPatlampModel.findAll", query = "SELECT p FROM MstrPatlampModel p"),
        @NamedQuery(name = "MstrPatlampModel.findByPK", query = "SELECT p FROM MstrPatlampModel p WHERE p.patlamp_key = :patlamp_key")
})
public class MstrPatlampModel extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_patlamp_model";

    /** パトランプ機種キー */
    public static final String COLUMN_NAME_PATLAMP_KEY = "patlamp_key";

    /** nkパトランプ機種ID */
    public static final String COLUMN_NAME_PATLAMP_MODEL_ID = "patlamp_model_id";

    /** パトランプ機種名 */
    public static final String COLUMN_NAME_PATLAMP_MODEL_NAME = "patlamp_model_name";

    /** Talendジョブ名 */
    public static final String COLUMN_NAME_TALEND_JOB_NAME = "talend_job_name";

    /** パトランプ機種キー */
    private String patlamp_key;
    /** nkパトランプ機種ID */
    private String patlamp_model_id;
    /** パトランプ機種名 */
    private String patlamp_model_name;
    /** Talendジョブ名 */
    private String talend_job_name;


    /**
     * Zm005パトランプ機種マスタエンティティ コンストラクタ
     */
    public MstrPatlampModel() {
    }

    /**
     * Zm005パトランプ機種マスタエンティティ コンストラクタ
     * @param patlamp_key パトランプ機種キー
     * @param patlamp_model_id nkパトランプ機種ID
     * @param patlamp_model_name パトランプ機種名
     * @param talend_job_name Talendジョブ名
     * @param version 更新バージョン
     */
    public MstrPatlampModel(String patlamp_key, String patlamp_model_id, String patlamp_model_name, String talend_job_name, int version) {
        this.patlamp_key = patlamp_key;
        this.patlamp_model_id = patlamp_model_id;
        this.patlamp_model_name = patlamp_model_name;
        this.talend_job_name = talend_job_name;
        super.setVersion(version);
    }

    /**
     * Zm005パトランプ機種マスタエンティティ コンストラクタ
     * @param patlamp_key パトランプ機種キー
     * @param patlamp_model_id nkパトランプ機種ID
     * @param patlamp_model_name パトランプ機種名
     * @param talend_job_name Talendジョブ名
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
    public MstrPatlampModel(String patlamp_key, String patlamp_model_id, String patlamp_model_name, String talend_job_name, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.patlamp_key = patlamp_key;
        this.patlamp_model_id = patlamp_model_id;
        this.patlamp_model_name = patlamp_model_name;
        this.talend_job_name = talend_job_name;
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
     * パトランプ機種キーを取得
     * @return パトランプ機種キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPatlamp_key() {
        return this.patlamp_key;
    }

    /**
     * パトランプ機種キーを設定
     * @param patlamp_key パトランプ機種キー (null不可)
     */
    public void setPatlamp_key(String patlamp_key) {
        this.patlamp_key = patlamp_key;
    }

    /**
     * nkパトランプ機種IDを取得
     * @return nkパトランプ機種ID
     */
    @Column(nullable = false, length = 20)
    public String getPatlamp_model_id() {
        return this.patlamp_model_id;
    }

    /**
     * nkパトランプ機種IDを設定
     * @param patlamp_model_id nkパトランプ機種ID (null不可)
     */
    public void setPatlamp_model_id(String patlamp_model_id) {
        this.patlamp_model_id = patlamp_model_id;
    }

    /**
     * パトランプ機種名を取得
     * @return パトランプ機種名
     */
    @Column(nullable = false, length = 40)
    public String getPatlamp_model_name() {
        return this.patlamp_model_name;
    }

    /**
     * パトランプ機種名を設定
     * @param patlamp_model_name パトランプ機種名 (null不可)
     */
    public void setPatlamp_model_name(String patlamp_model_name) {
        this.patlamp_model_name = patlamp_model_name;
    }

    /**
     * Talendジョブ名を取得
     * @return Talendジョブ名
     */
    @Column(nullable = false, length = 40)
    public String getTalend_job_name() {
        return this.talend_job_name;
    }

    /**
     * Talendジョブ名を設定
     * @param talend_job_name Talendジョブ名 (null不可)
     */
    public void setTalend_job_name(String talend_job_name) {
        this.talend_job_name = talend_job_name;
    }

}
