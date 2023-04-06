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

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi133外部I/F受信キュー エンティティ
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
@Table(name = "queue_external_if_recv")
@NamedQueries({
        @NamedQuery(name = "QueueExternalIfRecv.findAll", query = "SELECT p FROM QueueExternalIfRecv p"),
        @NamedQuery(name = "QueueExternalIfRecv.findByPK", query = "SELECT p FROM QueueExternalIfRecv p WHERE p.queue_external_if_recv_key = :queue_external_if_recv_key")
})
public class QueueExternalIfRecv extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "queue_external_if_recv";

    /** 外部I/F受信キューキー */
    public static final String COLUMN_NAME_QUEUE_EXTERNAL_IF_RECV_KEY = "queue_external_if_recv_key";

    /** 受信日時 */
    public static final String COLUMN_NAME_RECV_DATE = "recv_date";

    /** ファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** 外部I/F対象システムコード */
    public static final String COLUMN_NAME_EXTERNAL_IF_TARGET_CODE = "external_if_target_code";

    /** ファイル名称 */
    public static final String COLUMN_NAME_FILE_NAME = "file_name";

    /** 受信ファイル名 */
    public static final String COLUMN_NAME_RECV_FILE_NAME = "recv_file_name";

    /** 受信状態 */
    public static final String COLUMN_NAME_RECV_STATUS = "recv_status";

    /** 外部I/F受信キューキー */
    private String queue_external_if_recv_key;
    /** 受信日時 */
    private Date recv_date;
    /** ファイル種別 */
    private String file_type;
    /** 外部I/F対象システムコード */
    private String external_if_target_code;
    /** ファイル名称 */
    private String file_name;
    /** 受信ファイル名 */
    private String recv_file_name;
    /** 受信状態 */
    private int recv_status;


    /**
     * Zi133外部I/F受信キューエンティティ コンストラクタ
     */
    public QueueExternalIfRecv() {
    }

    /**
     * Zi133外部I/F受信キューエンティティ コンストラクタ
     * @param queue_external_if_recv_key 外部I/F受信キューキー
     * @param recv_date 受信日時
     * @param file_type ファイル種別
     * @param external_if_target_code 外部I/F対象システムコード
     * @param file_name ファイル名称
     * @param recv_file_name 受信ファイル名
     * @param recv_status 受信状態
     * @param version 更新バージョン
     */
    public QueueExternalIfRecv(String queue_external_if_recv_key, Date recv_date, String file_type, String external_if_target_code, String file_name, String recv_file_name, int recv_status, int version) {
        this.queue_external_if_recv_key = queue_external_if_recv_key;
        this.recv_date = recv_date;
        this.file_type = file_type;
        this.external_if_target_code = external_if_target_code;
        this.file_name = file_name;
        this.recv_file_name = recv_file_name;
        this.recv_status = recv_status;
        super.setVersion(version);
    }

    /**
     * Zi133外部I/F受信キューエンティティ コンストラクタ
     * @param queue_external_if_recv_key 外部I/F受信キューキー
     * @param recv_date 受信日時
     * @param file_type ファイル種別
     * @param external_if_target_code 外部I/F対象システムコード
     * @param file_name ファイル名称
     * @param recv_file_name 受信ファイル名
     * @param recv_status 受信状態
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
    public QueueExternalIfRecv(String queue_external_if_recv_key, Date recv_date, String file_type, String external_if_target_code, String file_name, String recv_file_name, int recv_status, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.queue_external_if_recv_key = queue_external_if_recv_key;
        this.recv_date = recv_date;
        this.file_type = file_type;
        this.external_if_target_code = external_if_target_code;
        this.file_name = file_name;
        this.recv_file_name = recv_file_name;
        this.recv_status = recv_status;
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
     * 外部I/F受信キューキーを取得
     * @return 外部I/F受信キューキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getQueue_external_if_recv_key() {
        return this.queue_external_if_recv_key;
    }

    /**
     * 外部I/F受信キューキーを設定
     * @param queue_external_if_recv_key 外部I/F受信キューキー (null不可)
     */
    public void setQueue_external_if_recv_key(String queue_external_if_recv_key) {
        this.queue_external_if_recv_key = queue_external_if_recv_key;
    }

    /**
     * 受信日時を取得
     * @return 受信日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getRecv_date() {
        return this.recv_date;
    }

    /**
     * 受信日時を設定
     * @param recv_date 受信日時 (null不可)
     */
    public void setRecv_date(Date recv_date) {
        this.recv_date = recv_date;
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
     * 受信ファイル名を取得
     * @return 受信ファイル名
     */
    @Column(nullable = false, length = 80)
    public String getRecv_file_name() {
        return this.recv_file_name;
    }

    /**
     * 受信ファイル名を設定
     * @param recv_file_name 受信ファイル名 (null不可)
     */
    public void setRecv_file_name(String recv_file_name) {
        this.recv_file_name = recv_file_name;
    }

    /**
     * 受信状態を取得
     * @return 受信状態
     */
    @Column(nullable = false, length = 1)
    public int getRecv_status() {
        return this.recv_status;
    }

    /**
     * 受信状態を設定
     * @param recv_status 受信状態 (null不可)
     */
    public void setRecv_status(int recv_status) {
        this.recv_status = recv_status;
    }

}
