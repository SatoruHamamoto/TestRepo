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
 * Zr131外部I/Fデータファイル送受信実績詳細 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "ext_if_data_sr_actual_detail")
@NamedQueries({
        @NamedQuery(name = "ExtIfDataSrActualDetail.findAll", query = "SELECT p FROM ExtIfDataSrActualDetail p"),
        @NamedQuery(name = "ExtIfDataSrActualDetail.findByPK", query = "SELECT p FROM ExtIfDataSrActualDetail p WHERE p.external_if_data_sr_actual_detail_key = :external_if_data_sr_actual_detail_key")
})
public class ExtIfDataSrActualDetail extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "ext_if_data_sr_actual_detail";

    /** 外部I/Fデータファイル送受信実績詳細キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_DATA_SR_ACTUAL_DETAIL_KEY = "external_if_data_sr_actual_detail_key";

    /** 外部I/Fデータファイル送受信実績キー (FK) */
    public static final String COLUMN_NAME_EXTERNAL_IF_DATA_SR_ACTUAL_KEY = "external_if_data_sr_actual_key";

    /** 通信日時 */
    public static final String COLUMN_NAME_OCCUR_DATE = "occur_date";

    /** ファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** 送受信区分 */
    public static final String COLUMN_NAME_SEND_RECV_TYPE = "send_recv_type";

    /** 行NO. */
    public static final String COLUMN_NAME_LINE_NUMBER = "line_number";

    /** 行データ */
    public static final String COLUMN_NAME_LINE_DATA = "line_data";

    /** 行ステータス */
    public static final String COLUMN_NAME_LINE_STATUS = "line_status";

    /** 詳細コメント */
    public static final String COLUMN_NAME_DETAIL_COMMENT = "detail_comment";

    /** 外部I/Fデータファイル送受信実績詳細キー */
    private String external_if_data_sr_actual_detail_key;
    /** 外部I/Fデータファイル送受信実績キー (FK) */
    private String external_if_data_sr_actual_key;
    /** 通信日時 */
    private Date occur_date;
    /** ファイル種別 */
    private String file_type;
    /** 送受信区分 */
    private int send_recv_type;
    /** 行NO. */
    private int line_number;
    /** 行データ */
    private String line_data;
    /** 行ステータス */
    private Integer line_status;
    /** 詳細コメント */
    private String detail_comment;


    /**
     * Zr131外部I/Fデータファイル送受信実績詳細エンティティ コンストラクタ
     */
    public ExtIfDataSrActualDetail() {
    }

    /**
     * Zr131外部I/Fデータファイル送受信実績詳細エンティティ コンストラクタ
     * @param external_if_data_sr_actual_detail_key 外部I/Fデータファイル送受信実績詳細キー
     * @param external_if_data_sr_actual_key 外部I/Fデータファイル送受信実績キー (FK)
     * @param occur_date 通信日時
     * @param file_type ファイル種別
     * @param send_recv_type 送受信区分
     * @param line_number 行NO.
     * @param detail_comment 詳細コメント
     * @param version 更新バージョン
     */
    public ExtIfDataSrActualDetail(String external_if_data_sr_actual_detail_key, String external_if_data_sr_actual_key, Date occur_date, String file_type, int send_recv_type, int line_number, String detail_comment, int version) {
        this.external_if_data_sr_actual_detail_key = external_if_data_sr_actual_detail_key;
        this.external_if_data_sr_actual_key = external_if_data_sr_actual_key;
        this.occur_date = occur_date;
        this.file_type = file_type;
        this.send_recv_type = send_recv_type;
        this.line_number = line_number;
        this.detail_comment = detail_comment;
        super.setVersion(version);
    }

    /**
     * Zr131外部I/Fデータファイル送受信実績詳細エンティティ コンストラクタ
     * @param external_if_data_sr_actual_detail_key 外部I/Fデータファイル送受信実績詳細キー
     * @param external_if_data_sr_actual_key 外部I/Fデータファイル送受信実績キー (FK)
     * @param occur_date 通信日時
     * @param file_type ファイル種別
     * @param send_recv_type 送受信区分
     * @param line_number 行NO.
     * @param line_data 行データ
     * @param line_status 行ステータス
     * @param detail_comment 詳細コメント
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
    public ExtIfDataSrActualDetail(String external_if_data_sr_actual_detail_key, String external_if_data_sr_actual_key, Date occur_date, String file_type, int send_recv_type, int line_number, String line_data, Integer line_status, String detail_comment, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_data_sr_actual_detail_key = external_if_data_sr_actual_detail_key;
        this.external_if_data_sr_actual_key = external_if_data_sr_actual_key;
        this.occur_date = occur_date;
        this.file_type = file_type;
        this.send_recv_type = send_recv_type;
        this.line_number = line_number;
        this.line_data = line_data;
        this.line_status = line_status;
        this.detail_comment = detail_comment;
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
     * 外部I/Fデータファイル送受信実績詳細キーを取得
     * @return 外部I/Fデータファイル送受信実績詳細キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_data_sr_actual_detail_key() {
        return this.external_if_data_sr_actual_detail_key;
    }

    /**
     * 外部I/Fデータファイル送受信実績詳細キーを設定
     * @param external_if_data_sr_actual_detail_key 外部I/Fデータファイル送受信実績詳細キー (null不可)
     */
    public void setExternal_if_data_sr_actual_detail_key(String external_if_data_sr_actual_detail_key) {
        this.external_if_data_sr_actual_detail_key = external_if_data_sr_actual_detail_key;
    }

    /**
     * 外部I/Fデータファイル送受信実績キー (FK)を取得
     * @return 外部I/Fデータファイル送受信実績キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getExternal_if_data_sr_actual_key() {
        return this.external_if_data_sr_actual_key;
    }

    /**
     * 外部I/Fデータファイル送受信実績キー (FK)を設定
     * @param external_if_data_sr_actual_key 外部I/Fデータファイル送受信実績キー (FK) (null不可)
     */
    public void setExternal_if_data_sr_actual_key(String external_if_data_sr_actual_key) {
        this.external_if_data_sr_actual_key = external_if_data_sr_actual_key;
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
     * 行NO.を取得
     * @return 行NO.
     */
    @Column(nullable = false, length = 6)
    public int getLine_number() {
        return this.line_number;
    }

    /**
     * 行NO.を設定
     * @param line_number 行NO. (null不可)
     */
    public void setLine_number(int line_number) {
        this.line_number = line_number;
    }

    /**
     * 行データを取得
     * @return 行データ
     */
    @Column(length = 2048)
    public String getLine_data() {
        return this.line_data;
    }

    /**
     * 行データを設定
     * @param line_data 行データ
     */
    public void setLine_data(String line_data) {
        this.line_data = line_data;
    }

    /**
     * 行ステータスを取得
     * @return 行ステータス
     */
    @Column(length = 1)
    public Integer getLine_status() {
        return this.line_status;
    }

    /**
     * 行ステータスを設定
     * @param line_status 行ステータス
     */
    public void setLine_status(Integer line_status) {
        this.line_status = line_status;
    }

    /**
     * 詳細コメントを取得
     * @return 詳細コメント
     */
    @Column(nullable = false, length = 1000)
    public String getDetail_comment() {
        return this.detail_comment;
    }

    /**
     * 詳細コメントを設定
     * @param detail_comment 詳細コメント (null不可)
     */
    public void setDetail_comment(String detail_comment) {
        this.detail_comment = detail_comment;
    }

}
