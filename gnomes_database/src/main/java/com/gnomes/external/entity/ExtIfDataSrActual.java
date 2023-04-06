package com.gnomes.external.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zr130外部I/Fデータファイル送受信実績 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/05/28 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "ext_if_data_sr_actual")
@NamedQueries({
        @NamedQuery(name = "ExtIfDataSrActual.findAll", query = "SELECT p FROM ExtIfDataSrActual p"),
        @NamedQuery(name = "ExtIfDataSrActual.findByPK", query = "SELECT p FROM ExtIfDataSrActual p WHERE p.external_if_data_sr_actual_key = :external_if_data_sr_actual_key")
})
public class ExtIfDataSrActual extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "ext_if_data_sr_actual";

    /** 外部I/Fデータファイル送受信実績キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_DATA_SR_ACTUAL_KEY = "external_if_data_sr_actual_key";

    /** 外部I/F送受信キューキー */
    public static final String COLUMN_NAME_EXTERNAL_IF_SEND_RECV_QUEUE_KEY = "external_if_send_recv_queue_key";

    /** 通信日時 */
    public static final String COLUMN_NAME_OCCUR_DATE = "occur_date";

    /** ファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** 送受信区分 */
    public static final String COLUMN_NAME_SEND_RECV_TYPE = "send_recv_type";

    /** 外部I/F対象システムコード */
    public static final String COLUMN_NAME_EXTERNAL_IF_TARGET_CODE = "external_if_target_code";

    /** ファイル名称 */
    public static final String COLUMN_NAME_FILE_NAME = "file_name";

    /** 送受信ファイル名 */
    public static final String COLUMN_NAME_SEND_RECV_FILE_NAME = "send_recv_file_name";

    /** ステータス */
    public static final String COLUMN_NAME_STATUS = "status";

    /** 再処理フラグ */
    public static final String COLUMN_NAME_REPROCESSING_FLAG = "reprocessing_flag";

    /** クリアフラグ */
    public static final String COLUMN_NAME_CLEAR_FLAG = "clear_flag";

    /** 送受信処理日時 */
    public static final String COLUMN_NAME_SEND_RECV_DATE = "send_recv_date";

    /** 外部I/Fデータファイル送受信実績キー */
    private String external_if_data_sr_actual_key;
    /** 外部I/F送受信キューキー */
    private String external_if_send_recv_queue_key;
    /** 通信日時 */
    private Date occur_date;
    /** ファイル種別 */
    private String file_type;
    /** 送受信区分 */
    private int send_recv_type;
    /** 外部I/F対象システムコード */
    private String external_if_target_code;
    /** ファイル名称 */
    private String file_name;
    /** 送受信ファイル名 */
    private String send_recv_file_name;
    /** ステータス */
    private int status;
    /** 再処理フラグ */
    private Integer reprocessing_flag;
    /** クリアフラグ */
    private Integer clear_flag;
    /** 送受信処理日時 */
    private Date send_recv_date;


    /**
     * Zr130外部I/Fデータファイル送受信実績エンティティ コンストラクタ
     */
    public ExtIfDataSrActual() {
    }

    /**
     * Zr130外部I/Fデータファイル送受信実績エンティティ コンストラクタ
     * @param external_if_data_sr_actual_key 外部I/Fデータファイル送受信実績キー
     * @param external_if_send_recv_queue_key 外部I/F送受信キューキー
     * @param occur_date 通信日時
     * @param file_type ファイル種別
     * @param send_recv_type 送受信区分
     * @param external_if_target_code 外部I/F対象システムコード
     * @param file_name ファイル名称
     * @param send_recv_file_name 送受信ファイル名
     * @param status ステータス
     * @param version 更新バージョン
     */
    public ExtIfDataSrActual(String external_if_data_sr_actual_key, String external_if_send_recv_queue_key, Date occur_date, String file_type, int send_recv_type, String external_if_target_code, String file_name, String send_recv_file_name, int status, int version) {
        this.external_if_data_sr_actual_key = external_if_data_sr_actual_key;
        this.external_if_send_recv_queue_key = external_if_send_recv_queue_key;
        this.occur_date = occur_date;
        this.file_type = file_type;
        this.send_recv_type = send_recv_type;
        this.external_if_target_code = external_if_target_code;
        this.file_name = file_name;
        this.send_recv_file_name = send_recv_file_name;
        this.status = status;
        super.setVersion(version);
    }

    /**
     * Zr130外部I/Fデータファイル送受信実績エンティティ コンストラクタ
     * @param external_if_data_sr_actual_key 外部I/Fデータファイル送受信実績キー
     * @param external_if_send_recv_queue_key 外部I/F送受信キューキー
     * @param occur_date 通信日時
     * @param file_type ファイル種別
     * @param send_recv_type 送受信区分
     * @param external_if_target_code 外部I/F対象システムコード
     * @param file_name ファイル名称
     * @param send_recv_file_name 送受信ファイル名
     * @param status ステータス
     * @param reprocessing_flag 再処理フラグ
     * @param clear_flag クリアフラグ
     * @param send_recv_date 送受信処理日時
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
    public ExtIfDataSrActual(String external_if_data_sr_actual_key, String external_if_send_recv_queue_key, Date occur_date, String file_type, int send_recv_type, String external_if_target_code, String file_name, String send_recv_file_name, int status, Integer reprocessing_flag, Integer clear_flag, Date send_recv_date, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_data_sr_actual_key = external_if_data_sr_actual_key;
        this.external_if_send_recv_queue_key = external_if_send_recv_queue_key;
        this.occur_date = occur_date;
        this.file_type = file_type;
        this.send_recv_type = send_recv_type;
        this.external_if_target_code = external_if_target_code;
        this.file_name = file_name;
        this.send_recv_file_name = send_recv_file_name;
        this.status = status;
        this.reprocessing_flag = reprocessing_flag;
        this.clear_flag = clear_flag;
        this.send_recv_date = send_recv_date;
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
     * 外部I/Fデータファイル送受信実績キーを取得
     * @return 外部I/Fデータファイル送受信実績キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_data_sr_actual_key() {
        return this.external_if_data_sr_actual_key;
    }

    /**
     * 外部I/Fデータファイル送受信実績キーを設定
     * @param external_if_data_sr_actual_key 外部I/Fデータファイル送受信実績キー (null不可)
     */
    public void setExternal_if_data_sr_actual_key(String external_if_data_sr_actual_key) {
        this.external_if_data_sr_actual_key = external_if_data_sr_actual_key;
    }

    /**
     * 外部I/F送受信キューキーを取得
     * @return 外部I/F送受信キューキー
     */
    @Column(nullable = false, length = 38)
    public String getExternal_if_send_recv_queue_key() {
        return this.external_if_send_recv_queue_key;
    }

    /**
     * 外部I/F送受信キューキーを設定
     * @param external_if_send_recv_queue_key 外部I/F送受信キューキー (null不可)
     */
    public void setExternal_if_send_recv_queue_key(String external_if_send_recv_queue_key) {
        this.external_if_send_recv_queue_key = external_if_send_recv_queue_key;
    }

    /**
     * 通信日時を取得
     * @return 通信日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getOccur_date() {
        return this.occur_date;
    }

    /**
     * 通信日時を設定
     * @param occur_date 通信日時 (null不可)
     */
    public void setOccur_date(Date occur_date) {
        this.occur_date = occur_date;
    }

    /**
     * ファイル種別を取得
     * @return ファイル種別
     */
    @Column(nullable = false, length = 16)
    public String getFile_type() {
        return this.file_type;
    }

    /**
     * ファイル種別を設定
     * @param file_type ファイル種別 (null不可)
     */
    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    /**
     * 送受信区分を取得
     * @return 送受信区分
     */
    @Column(nullable = false, length = 2)
    public int getSend_recv_type() {
        return this.send_recv_type;
    }

    /**
     * 送受信区分を設定
     * @param send_recv_type 送受信区分 (null不可)
     */
    public void setSend_recv_type(int send_recv_type) {
        this.send_recv_type = send_recv_type;
    }

    /**
     * 外部I/F対象システムコードを取得
     * @return 外部I/F対象システムコード
     */
    @Column(nullable = false, length = 2)
    public String getExternal_if_target_code() {
        return this.external_if_target_code;
    }

    /**
     * 外部I/F対象システムコードを設定
     * @param external_if_target_code 外部I/F対象システムコード (null不可)
     */
    public void setExternal_if_target_code(String external_if_target_code) {
        this.external_if_target_code = external_if_target_code;
    }

    /**
     * ファイル名称を取得
     * @return ファイル名称
     */
    @Column(nullable = false, length = 80)
    public String getFile_name() {
        return this.file_name;
    }

    /**
     * ファイル名称を設定
     * @param file_name ファイル名称 (null不可)
     */
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    /**
     * 送受信ファイル名を取得
     * @return 送受信ファイル名
     */
    @Column(nullable = false, length = 80)
    public String getSend_recv_file_name() {
        return this.send_recv_file_name;
    }

    /**
     * 送受信ファイル名を設定
     * @param send_recv_file_name 送受信ファイル名 (null不可)
     */
    public void setSend_recv_file_name(String send_recv_file_name) {
        this.send_recv_file_name = send_recv_file_name;
    }

    /**
     * ステータスを取得
     * @return ステータス
     */
    @Column(nullable = false, length = 1)
    public int getStatus() {
        return this.status;
    }

    /**
     * ステータスを設定
     * @param status ステータス (null不可)
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 再処理フラグを取得
     * @return 再処理フラグ
     */
    @Column(length = 1)
    public Integer getReprocessing_flag() {
        return this.reprocessing_flag;
    }

    /**
     * 再処理フラグを設定
     * @param reprocessing_flag 再処理フラグ
     */
    public void setReprocessing_flag(Integer reprocessing_flag) {
        this.reprocessing_flag = reprocessing_flag;
    }

    /**
     * クリアフラグを取得
     * @return クリアフラグ
     */
    @Column(length = 1)
    public Integer getClear_flag() {
        return this.clear_flag;
    }

    /**
     * クリアフラグを設定
     * @param clear_flag クリアフラグ
     */
    public void setClear_flag(Integer clear_flag) {
        this.clear_flag = clear_flag;
    }

    /**
     * 送受信処理日時を取得
     * @return 送受信処理日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getSend_recv_date() {
        return this.send_recv_date;
    }

    /**
     * 送受信処理日時を設定
     * @param send_recv_date 送受信処理日時
     */
    public void setSend_recv_date(Date send_recv_date) {
        this.send_recv_date = send_recv_date;
    }

}
