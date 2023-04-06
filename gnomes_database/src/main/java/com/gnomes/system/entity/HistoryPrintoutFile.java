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
 * Zr121帳票ラベル印字履歴ファイル エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/01/24 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "history_printout_file")
@NamedQueries({
        @NamedQuery(name = "HistoryPrintoutFile.findAll", query = "SELECT p FROM HistoryPrintoutFile p"),
        @NamedQuery(name = "HistoryPrintoutFile.findByPK", query = "SELECT p FROM HistoryPrintoutFile p WHERE p.history_printout_file_key = :history_printout_file_key")
})
public class HistoryPrintoutFile extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "history_printout_file";

    /** 帳票ラベル印字履歴ファイルキー */
    public static final String COLUMN_NAME_HISTORY_PRINTOUT_FILE_KEY = "history_printout_file_key";

    /** nk帳票ラベル印字履歴キー (FK) */
    public static final String COLUMN_NAME_HISTORY_PRINTOUT_KEY = "history_printout_key";

    /** nk要求イベントID */
    public static final String COLUMN_NAME_EVENT_ID = "event_id";

    /** nk要求内連番 */
    public static final String COLUMN_NAME_REQUEST_SEQ = "request_seq";

    /** PDF電子ファイル */
    public static final String COLUMN_NAME_PDF_REPORT_FILE = "pdf_report_file";

    /** EXCEL電子ファイル */
    public static final String COLUMN_NAME_EXCEL_REPORT_FILE = "excel_report_file";

    /** 帳票ラベル印字履歴ファイルキー */
    private String history_printout_file_key;
    /** nk帳票ラベル印字履歴キー (FK) */
    private String history_printout_key;
    /** nk要求イベントID */
    private String event_id;
    /** nk要求内連番 */
    private int request_seq;
    /** PDF電子ファイル */
    private byte[] pdf_report_file;
    /** EXCEL電子ファイル */
    private byte[] excel_report_file;


    /**
     * Zr121帳票ラベル印字履歴ファイルエンティティ コンストラクタ
     */
    public HistoryPrintoutFile() {
    }

    /**
     * Zr121帳票ラベル印字履歴ファイルエンティティ コンストラクタ
     * @param history_printout_file_key 帳票ラベル印字履歴ファイルキー
     * @param history_printout_key nk帳票ラベル印字履歴キー (FK)
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param version 更新バージョン
     */
    public HistoryPrintoutFile(String history_printout_file_key, String history_printout_key, String event_id, int request_seq, int version) {
        this.history_printout_file_key = history_printout_file_key;
        this.history_printout_key = history_printout_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        super.setVersion(version);
    }

    /**
     * Zr121帳票ラベル印字履歴ファイルエンティティ コンストラクタ
     * @param history_printout_file_key 帳票ラベル印字履歴ファイルキー
     * @param history_printout_key nk帳票ラベル印字履歴キー (FK)
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param pdf_report_file PDF電子ファイル
     * @param excel_report_file EXCEL電子ファイル
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
    public HistoryPrintoutFile(String history_printout_file_key, String history_printout_key, String event_id, int request_seq, byte[] pdf_report_file, byte[] excel_report_file, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.history_printout_file_key = history_printout_file_key;
        this.history_printout_key = history_printout_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.pdf_report_file = pdf_report_file;
        this.excel_report_file = excel_report_file;
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
     * 帳票ラベル印字履歴ファイルキーを取得
     * @return 帳票ラベル印字履歴ファイルキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getHistory_printout_file_key() {
        return this.history_printout_file_key;
    }

    /**
     * 帳票ラベル印字履歴ファイルキーを設定
     * @param history_printout_file_key 帳票ラベル印字履歴ファイルキー (null不可)
     */
    public void setHistory_printout_file_key(String history_printout_file_key) {
        this.history_printout_file_key = history_printout_file_key;
    }

    /**
     * nk帳票ラベル印字履歴キー (FK)を取得
     * @return nk帳票ラベル印字履歴キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getHistory_printout_key() {
        return this.history_printout_key;
    }

    /**
     * nk帳票ラベル印字履歴キー (FK)を設定
     * @param history_printout_key nk帳票ラベル印字履歴キー (FK) (null不可)
     */
    public void setHistory_printout_key(String history_printout_key) {
        this.history_printout_key = history_printout_key;
    }

    /**
     * nk要求イベントIDを取得
     * @return nk要求イベントID
     */
    @Column(nullable = false, length = 38)
    public String getEvent_id() {
        return this.event_id;
    }

    /**
     * nk要求イベントIDを設定
     * @param event_id nk要求イベントID (null不可)
     */
    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    /**
     * nk要求内連番を取得
     * @return nk要求内連番
     */
    @Column(nullable = false, length = 3)
    public int getRequest_seq() {
        return this.request_seq;
    }

    /**
     * nk要求内連番を設定
     * @param request_seq nk要求内連番 (null不可)
     */
    public void setRequest_seq(int request_seq) {
        this.request_seq = request_seq;
    }

    /**
     * PDF電子ファイルを取得
     * @return PDF電子ファイル
     */
    @Column
    public byte[] getPdf_report_file() {
        return this.pdf_report_file;
    }

    /**
     * PDF電子ファイルを設定
     * @param pdf_report_file PDF電子ファイル
     */
    public void setPdf_report_file(byte[] pdf_report_file) {
        this.pdf_report_file = pdf_report_file;
    }

    /**
     * EXCEL電子ファイルを取得
     * @return EXCEL電子ファイル
     */
    @Column
    public byte[] getExcel_report_file() {
        return this.excel_report_file;
    }

    /**
     * EXCEL電子ファイルを設定
     * @param excel_report_file EXCEL電子ファイル
     */
    public void setExcel_report_file(byte[] excel_report_file) {
        this.excel_report_file = excel_report_file;
    }

}
