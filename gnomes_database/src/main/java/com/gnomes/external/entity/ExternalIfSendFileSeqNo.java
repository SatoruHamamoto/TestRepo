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

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi132外部I/F送信ファイル連番管理 エンティティ
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
@Table(name = "external_if_send_file_seq_no")
@NamedQueries({
        @NamedQuery(name = "ExternalIfSendFileSeqNo.findAll", query = "SELECT p FROM ExternalIfSendFileSeqNo p"),
        @NamedQuery(name = "ExternalIfSendFileSeqNo.findByPK", query = "SELECT p FROM ExternalIfSendFileSeqNo p WHERE p.external_if_send_file_seq_no_key = :external_if_send_file_seq_no_key")
})
public class ExternalIfSendFileSeqNo extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "external_if_send_file_seq_no";

    /** 外部I/F送信ファイル連番管理キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_SEND_FILE_SEQ_NO_KEY = "external_if_send_file_seq_no_key";

    /** nkファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** 現在値 */
    public static final String COLUMN_NAME_SEQ = "seq";

    /** MIN値 */
    public static final String COLUMN_NAME_MIN_SEQ = "min_seq";

    /** MAX値 */
    public static final String COLUMN_NAME_MAX_SEQ = "max_seq";

    /** 外部I/F送信ファイル連番管理キー */
    private String external_if_send_file_seq_no_key;
    /** nkファイル種別 */
    private String file_type;
    /** 現在値 */
    private int seq;
    /** MIN値 */
    private int min_seq;
    /** MAX値 */
    private int max_seq;


    /**
     * Zi132外部I/F送信ファイル連番管理エンティティ コンストラクタ
     */
    public ExternalIfSendFileSeqNo() {
    }

    /**
     * Zi132外部I/F送信ファイル連番管理エンティティ コンストラクタ
     * @param external_if_send_file_seq_no_key 外部I/F送信ファイル連番管理キー
     * @param file_type nkファイル種別
     * @param seq 現在値
     * @param min_seq MIN値
     * @param max_seq MAX値
     * @param version 更新バージョン
     */
    public ExternalIfSendFileSeqNo(String external_if_send_file_seq_no_key, String file_type, int seq, int min_seq, int max_seq, int version) {
        this.external_if_send_file_seq_no_key = external_if_send_file_seq_no_key;
        this.file_type = file_type;
        this.seq = seq;
        this.min_seq = min_seq;
        this.max_seq = max_seq;
        super.setVersion(version);
    }

    /**
     * Zi132外部I/F送信ファイル連番管理エンティティ コンストラクタ
     * @param external_if_send_file_seq_no_key 外部I/F送信ファイル連番管理キー
     * @param file_type nkファイル種別
     * @param seq 現在値
     * @param min_seq MIN値
     * @param max_seq MAX値
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
    public ExternalIfSendFileSeqNo(String external_if_send_file_seq_no_key, String file_type, int seq, int min_seq, int max_seq, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_send_file_seq_no_key = external_if_send_file_seq_no_key;
        this.file_type = file_type;
        this.seq = seq;
        this.min_seq = min_seq;
        this.max_seq = max_seq;
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
     * 外部I/F送信ファイル連番管理キーを取得
     * @return 外部I/F送信ファイル連番管理キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_send_file_seq_no_key() {
        return this.external_if_send_file_seq_no_key;
    }

    /**
     * 外部I/F送信ファイル連番管理キーを設定
     * @param external_if_send_file_seq_no_key 外部I/F送信ファイル連番管理キー (null不可)
     */
    public void setExternal_if_send_file_seq_no_key(String external_if_send_file_seq_no_key) {
        this.external_if_send_file_seq_no_key = external_if_send_file_seq_no_key;
    }

    /**
     * nkファイル種別を取得
     * @return nkファイル種別
     */
    @Column(nullable = false, length = 16)
    public String getFile_type() {
        return this.file_type;
    }

    /**
     * nkファイル種別を設定
     * @param file_type nkファイル種別 (null不可)
     */
    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    /**
     * 現在値を取得
     * @return 現在値
     */
    @Column(nullable = false, length = 6)
    public int getSeq() {
        return this.seq;
    }

    /**
     * 現在値を設定
     * @param seq 現在値 (null不可)
     */
    public void setSeq(int seq) {
        this.seq = seq;
    }

    /**
     * MIN値を取得
     * @return MIN値
     */
    @Column(nullable = false, length = 6)
    public int getMin_seq() {
        return this.min_seq;
    }

    /**
     * MIN値を設定
     * @param min_seq MIN値 (null不可)
     */
    public void setMin_seq(int min_seq) {
        this.min_seq = min_seq;
    }

    /**
     * MAX値を取得
     * @return MAX値
     */
    @Column(nullable = false, length = 6)
    public int getMax_seq() {
        return this.max_seq;
    }

    /**
     * MAX値を設定
     * @param max_seq MAX値 (null不可)
     */
    public void setMax_seq(int max_seq) {
        this.max_seq = max_seq;
    }

}
