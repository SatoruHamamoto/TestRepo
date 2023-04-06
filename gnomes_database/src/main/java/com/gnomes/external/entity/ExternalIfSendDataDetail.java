package com.gnomes.external.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi131外部I/F送信データ詳細 エンティティ
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
@Table(name = "external_if_send_data_detail")
@NamedQueries({
        @NamedQuery(name = "ExternalIfSendDataDetail.findAll", query = "SELECT p FROM ExternalIfSendDataDetail p"),
        @NamedQuery(name = "ExternalIfSendDataDetail.findByPK", query = "SELECT p FROM ExternalIfSendDataDetail p WHERE p.external_if_send_data_detail_key = :external_if_send_data_detail_key")
})
public class ExternalIfSendDataDetail extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "external_if_send_data_detail";

    /** 外部I/F送信データ詳細キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_SEND_DATA_DETAIL_KEY = "external_if_send_data_detail_key";

    /** 外部I/F送信状態キー (FK) */
    public static final String COLUMN_NAME_EXTERNAL_IF_SEND_STATUS_KEY = "external_if_send_status_key";

    /** 作成日時 */
    public static final String COLUMN_NAME_CREATE_DATE = "create_date";

    /** ファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** 行No */
    public static final String COLUMN_NAME_LINE_NUMBER = "line_number";

    /** 行データ */
    public static final String COLUMN_NAME_LINE_DATA = "line_data";

    /** 外部I/F送信データ詳細キー */
    private String external_if_send_data_detail_key;
    /** 外部I/F送信状態キー (FK) */
    private String external_if_send_status_key;
    /** 作成日時 */
    private Date create_date;
    /** ファイル種別 */
    private String file_type;
    /** 行No */
    private int line_number;
    /** 行データ */
    private String line_data;

    /** Zi130外部I/F送信状態キュー */
    private QueueExternalIfSendStatus queueExternalIfSendStatus;

    /**
     * Zi131外部I/F送信データ詳細エンティティ コンストラクタ
     */
    public ExternalIfSendDataDetail() {
    }

    /**
     * Zi131外部I/F送信データ詳細エンティティ コンストラクタ
     * @param external_if_send_data_detail_key 外部I/F送信データ詳細キー
     * @param external_if_send_status_key 外部I/F送信状態キー (FK)
     * @param create_date 作成日時
     * @param file_type ファイル種別
     * @param line_number 行No
     * @param version 更新バージョン
     */
    public ExternalIfSendDataDetail(String external_if_send_data_detail_key, String external_if_send_status_key, Date create_date, String file_type, int line_number, int version) {
        this.external_if_send_data_detail_key = external_if_send_data_detail_key;
        this.external_if_send_status_key = external_if_send_status_key;
        this.create_date = create_date;
        this.file_type = file_type;
        this.line_number = line_number;
        super.setVersion(version);
    }

    /**
     * Zi131外部I/F送信データ詳細エンティティ コンストラクタ
     * @param external_if_send_data_detail_key 外部I/F送信データ詳細キー
     * @param external_if_send_status_key 外部I/F送信状態キー (FK)
     * @param create_date 作成日時
     * @param file_type ファイル種別
     * @param line_number 行No
     * @param line_data 行データ
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
    public ExternalIfSendDataDetail(String external_if_send_data_detail_key, String external_if_send_status_key, Date create_date, String file_type, int line_number, String line_data, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_send_data_detail_key = external_if_send_data_detail_key;
        this.external_if_send_status_key = external_if_send_status_key;
        this.create_date = create_date;
        this.file_type = file_type;
        this.line_number = line_number;
        this.line_data = line_data;
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
     * 外部I/F送信データ詳細キーを取得
     * @return 外部I/F送信データ詳細キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_send_data_detail_key() {
        return this.external_if_send_data_detail_key;
    }

    /**
     * 外部I/F送信データ詳細キーを設定
     * @param external_if_send_data_detail_key 外部I/F送信データ詳細キー (null不可)
     */
    public void setExternal_if_send_data_detail_key(String external_if_send_data_detail_key) {
        this.external_if_send_data_detail_key = external_if_send_data_detail_key;
    }

    /**
     * 外部I/F送信状態キー (FK)を取得
     * @return 外部I/F送信状態キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getExternal_if_send_status_key() {
        return this.external_if_send_status_key;
    }

    /**
     * 外部I/F送信状態キー (FK)を設定
     * @param external_if_send_status_key 外部I/F送信状態キー (FK) (null不可)
     */
    public void setExternal_if_send_status_key(String external_if_send_status_key) {
        this.external_if_send_status_key = external_if_send_status_key;
    }

    /**
     * 作成日時を取得
     * @return 作成日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getCreate_date() {
        return this.create_date;
    }

    /**
     * 作成日時を設定
     * @param create_date 作成日時 (null不可)
     */
    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
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
     * 行Noを取得
     * @return 行No
     */
    @Column(nullable = false, length = 6)
    public int getLine_number() {
        return this.line_number;
    }

    /**
     * 行Noを設定
     * @param line_number 行No (null不可)
     */
    public void setLine_number(int line_number) {
        this.line_number = line_number;
    }

    /**
     * 行データを取得
     * @return 行データ
     */
    @Column(length = 2045)
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
     * Zi130外部I/F送信状態キューを取得
     * @return Zi130外部I/F送信状態キュー
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "external_if_send_status_key", referencedColumnName = "external_if_send_status_key", insertable = false, updatable = false) 
    public QueueExternalIfSendStatus getQueueExternalIfSendStatus() {
        return this.queueExternalIfSendStatus;
    }

    /**
     * Zi130外部I/F送信状態キューを設定
     * @return Zi130外部I/F送信状態キュー
     */ 
    public void setQueueExternalIfSendStatus(QueueExternalIfSendStatus queueExternalIfSendStatus) {
        this.queueExternalIfSendStatus = queueExternalIfSendStatus;
    }

}
