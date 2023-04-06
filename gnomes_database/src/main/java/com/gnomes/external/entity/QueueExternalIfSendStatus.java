package com.gnomes.external.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi130外部I/F送信状態キュー エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/05/28 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "queue_external_if_send_status")
@NamedQueries({
        @NamedQuery(name = "QueueExternalIfSendStatus.findAll", query = "SELECT p FROM QueueExternalIfSendStatus p"),
        @NamedQuery(name = "QueueExternalIfSendStatus.findByPK", query = "SELECT p FROM QueueExternalIfSendStatus p WHERE p.external_if_send_status_key = :external_if_send_status_key")
})
public class QueueExternalIfSendStatus extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "queue_external_if_send_status";

    /** 外部I/F送信状態キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_SEND_STATUS_KEY = "external_if_send_status_key";

    /** 作成日時 */
    public static final String COLUMN_NAME_CREATE_DATE = "create_date";

    /** 要求内連番 */
    public static final String COLUMN_NAME_REQUEST_SEQ = "request_seq";

    /** ファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** 外部I/F対象システムコード */
    public static final String COLUMN_NAME_EXTERNAL_IF_TARGET_CODE = "external_if_target_code";

    /** ファイル名称 */
    public static final String COLUMN_NAME_FILE_NAME = "file_name";

    /** 送信ファイル名 */
    public static final String COLUMN_NAME_SEND_FILE_NAME = "send_file_name";

    /** コールクラス名 */
    public static final String COLUMN_NAME_CALL_CLASS_NAME = "call_class_name";

    /** 送信状態 */
    public static final String COLUMN_NAME_SEND_STATUS = "send_status";

    /** コールバック先プログラム名 */
    public static final String COLUMN_NAME_CALL_BACK_CLASS_NAME = "call_back_class_name";

    /** 送信処理日時 */
    public static final String COLUMN_NAME_SEND_DATE = "send_date";

    /** 外部I/F送信状態キー */
    private String external_if_send_status_key;
    /** 作成日時 */
    private Date create_date;
    /** 要求内連番 */
    private int request_seq;
    /** ファイル種別 */
    private String file_type;
    /** 外部I/F対象システムコード */
    private String external_if_target_code;
    /** ファイル名称 */
    private String file_name;
    /** 送信ファイル名 */
    private String send_file_name;
    /** コールクラス名 */
    private String call_class_name;
    /** 送信状態 */
    private int send_status;
    /** コールバック先プログラム名 */
    private String call_back_class_name;
    /** 送信処理日時 */
    private Date send_date;

    /** Zi131外部I/F送信データ詳細 */
    private Set<ExternalIfSendDataDetail> externalIfSendDataDetails;

    /**
     * Zi130外部I/F送信状態キューエンティティ コンストラクタ
     */
    public QueueExternalIfSendStatus() {
    }

    /**
     * Zi130外部I/F送信状態キューエンティティ コンストラクタ
     * @param external_if_send_status_key 外部I/F送信状態キー
     * @param create_date 作成日時
     * @param request_seq 要求内連番
     * @param file_type ファイル種別
     * @param send_status 送信状態
     * @param version 更新バージョン
     */
    public QueueExternalIfSendStatus(String external_if_send_status_key, Date create_date, int request_seq, String file_type, int send_status, int version) {
        this.external_if_send_status_key = external_if_send_status_key;
        this.create_date = create_date;
        this.request_seq = request_seq;
        this.file_type = file_type;
        this.send_status = send_status;
        super.setVersion(version);
    }

    /**
     * Zi130外部I/F送信状態キューエンティティ コンストラクタ
     * @param external_if_send_status_key 外部I/F送信状態キー
     * @param create_date 作成日時
     * @param request_seq 要求内連番
     * @param file_type ファイル種別
     * @param external_if_target_code 外部I/F対象システムコード
     * @param file_name ファイル名称
     * @param send_file_name 送信ファイル名
     * @param call_class_name コールクラス名
     * @param send_status 送信状態
     * @param call_back_class_name コールバック先プログラム名
     * @param send_date 送信処理日時
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
    public QueueExternalIfSendStatus(String external_if_send_status_key, Date create_date, int request_seq, String file_type, String external_if_target_code, String file_name, String send_file_name, String call_class_name, int send_status, String call_back_class_name, Date send_date, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_send_status_key = external_if_send_status_key;
        this.create_date = create_date;
        this.request_seq = request_seq;
        this.file_type = file_type;
        this.external_if_target_code = external_if_target_code;
        this.file_name = file_name;
        this.send_file_name = send_file_name;
        this.call_class_name = call_class_name;
        this.send_status = send_status;
        this.call_back_class_name = call_back_class_name;
        this.send_date = send_date;
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
     * 外部I/F送信状態キーを取得
     * @return 外部I/F送信状態キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_send_status_key() {
        return this.external_if_send_status_key;
    }

    /**
     * 外部I/F送信状態キーを設定
     * @param external_if_send_status_key 外部I/F送信状態キー (null不可)
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
     * 要求内連番を取得
     * @return 要求内連番
     */
    @Column(nullable = false, length = 3)
    public int getRequest_seq() {
        return this.request_seq;
    }

    /**
     * 要求内連番を設定
     * @param request_seq 要求内連番 (null不可)
     */
    public void setRequest_seq(int request_seq) {
        this.request_seq = request_seq;
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
     * 外部I/F対象システムコードを取得
     * @return 外部I/F対象システムコード
     */
    @Column(length = 2)
    public String getExternal_if_target_code() {
        return this.external_if_target_code;
    }

    /**
     * 外部I/F対象システムコードを設定
     * @param external_if_target_code 外部I/F対象システムコード
     */
    public void setExternal_if_target_code(String external_if_target_code) {
        this.external_if_target_code = external_if_target_code;
    }

    /**
     * ファイル名称を取得
     * @return ファイル名称
     */
    @Column(length = 80)
    public String getFile_name() {
        return this.file_name;
    }

    /**
     * ファイル名称を設定
     * @param file_name ファイル名称
     */
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    /**
     * 送信ファイル名を取得
     * @return 送信ファイル名
     */
    @Column(length = 80)
    public String getSend_file_name() {
        return this.send_file_name;
    }

    /**
     * 送信ファイル名を設定
     * @param send_file_name 送信ファイル名
     */
    public void setSend_file_name(String send_file_name) {
        this.send_file_name = send_file_name;
    }

    /**
     * コールクラス名を取得
     * @return コールクラス名
     */
    @Column(length = 80)
    public String getCall_class_name() {
        return this.call_class_name;
    }

    /**
     * コールクラス名を設定
     * @param call_class_name コールクラス名
     */
    public void setCall_class_name(String call_class_name) {
        this.call_class_name = call_class_name;
    }

    /**
     * 送信状態を取得
     * @return 送信状態
     */
    @Column(nullable = false, length = 1)
    public int getSend_status() {
        return this.send_status;
    }

    /**
     * 送信状態を設定
     * @param send_status 送信状態 (null不可)
     */
    public void setSend_status(int send_status) {
        this.send_status = send_status;
    }

    /**
     * コールバック先プログラム名を取得
     * @return コールバック先プログラム名
     */
    @Column(length = 64)
    public String getCall_back_class_name() {
        return this.call_back_class_name;
    }

    /**
     * コールバック先プログラム名を設定
     * @param call_back_class_name コールバック先プログラム名
     */
    public void setCall_back_class_name(String call_back_class_name) {
        this.call_back_class_name = call_back_class_name;
    }

    /**
     * 送信処理日時を取得
     * @return 送信処理日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getSend_date() {
        return this.send_date;
    }

    /**
     * 送信処理日時を設定
     * @param send_date 送信処理日時
     */
    public void setSend_date(Date send_date) {
        this.send_date = send_date;
    }

    /**
     * Zi131外部I/F送信データ詳細のListを取得
     * @return Zi131外部I/F送信データ詳細のList
     */
    @OneToMany(fetch= FetchType.LAZY, mappedBy = "queueExternalIfSendStatus", cascade = CascadeType.ALL, orphanRemoval=true)
    public Set<ExternalIfSendDataDetail> getExternalIfSendDataDetails() {
        return this.externalIfSendDataDetails;
    }

    /**
     * Zi131外部I/F送信データ詳細のListを設定
     * @return Zi131外部I/F送信データ詳細のList
     */ 
    public void setExternalIfSendDataDetails(Set<ExternalIfSendDataDetail> externalIfSendDataDetails) {
        this.externalIfSendDataDetails = externalIfSendDataDetails;
    }

}
