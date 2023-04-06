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
 * Zr122帳票ラベル印字履歴パラメータ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/04/17 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "history_printout_parameter")
@NamedQueries({
        @NamedQuery(name = "HistoryPrintoutParameter.findAll", query = "SELECT p FROM HistoryPrintoutParameter p"),
        @NamedQuery(name = "HistoryPrintoutParameter.findByPK", query = "SELECT p FROM HistoryPrintoutParameter p WHERE p.history_printout_parameter_key = :history_printout_parameter_key")
})
public class HistoryPrintoutParameter extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "history_printout_parameter";

    /** 帳票ラベル印字履歴パラメータキー */
    public static final String COLUMN_NAME_HISTORY_PRINTOUT_PARAMETER_KEY = "history_printout_parameter_key";

    /** nk帳票ラベル印字履歴キー (FK) */
    public static final String COLUMN_NAME_HISTORY_PRINTOUT_KEY = "history_printout_key";

    /** nk要求イベントID */
    public static final String COLUMN_NAME_EVENT_ID = "event_id";

    /** nk要求内連番 */
    public static final String COLUMN_NAME_REQUEST_SEQ = "request_seq";

    /** nkパラメータ名 */
    public static final String COLUMN_NAME_PARAMETER_NAME = "parameter_name";

    /** パラメータ表示名 */
    public static final String COLUMN_NAME_PARAMETER_DISP_NAME = "parameter_disp_name";

    /** パラメータ値 */
    public static final String COLUMN_NAME_PARAMETER_VALUE = "parameter_value";

    /** 帳票ラベル印字履歴パラメータキー */
    private String history_printout_parameter_key;
    /** nk帳票ラベル印字履歴キー (FK) */
    private String history_printout_key;
    /** nk要求イベントID */
    private String event_id;
    /** nk要求内連番 */
    private int request_seq;
    /** nkパラメータ名 */
    private String parameter_name;
    /** パラメータ表示名 */
    private String parameter_disp_name;
    /** パラメータ値 */
    private String parameter_value;


    /**
     * Zr122帳票ラベル印字履歴パラメータエンティティ コンストラクタ
     */
    public HistoryPrintoutParameter() {
    }

    /**
     * Zr122帳票ラベル印字履歴パラメータエンティティ コンストラクタ
     * @param history_printout_parameter_key 帳票ラベル印字履歴パラメータキー
     * @param history_printout_key nk帳票ラベル印字履歴キー (FK)
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param parameter_name nkパラメータ名
     * @param version 更新バージョン
     */
    public HistoryPrintoutParameter(String history_printout_parameter_key, String history_printout_key, String event_id, int request_seq, String parameter_name, int version) {
        this.history_printout_parameter_key = history_printout_parameter_key;
        this.history_printout_key = history_printout_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.parameter_name = parameter_name;
        super.setVersion(version);
    }

    /**
     * Zr122帳票ラベル印字履歴パラメータエンティティ コンストラクタ
     * @param history_printout_parameter_key 帳票ラベル印字履歴パラメータキー
     * @param history_printout_key nk帳票ラベル印字履歴キー (FK)
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param parameter_name nkパラメータ名
     * @param parameter_disp_name パラメータ表示名
     * @param parameter_value パラメータ値
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
    public HistoryPrintoutParameter(String history_printout_parameter_key, String history_printout_key, String event_id, int request_seq, String parameter_name, String parameter_disp_name, String parameter_value, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.history_printout_parameter_key = history_printout_parameter_key;
        this.history_printout_key = history_printout_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.parameter_name = parameter_name;
        this.parameter_disp_name = parameter_disp_name;
        this.parameter_value = parameter_value;
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
     * 帳票ラベル印字履歴パラメータキーを取得
     * @return 帳票ラベル印字履歴パラメータキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getHistory_printout_parameter_key() {
        return this.history_printout_parameter_key;
    }

    /**
     * 帳票ラベル印字履歴パラメータキーを設定
     * @param history_printout_parameter_key 帳票ラベル印字履歴パラメータキー (null不可)
     */
    public void setHistory_printout_parameter_key(String history_printout_parameter_key) {
        this.history_printout_parameter_key = history_printout_parameter_key;
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
     * nkパラメータ名を取得
     * @return nkパラメータ名
     */
    @Column(nullable = false, length = 50)
    public String getParameter_name() {
        return this.parameter_name;
    }

    /**
     * nkパラメータ名を設定
     * @param parameter_name nkパラメータ名 (null不可)
     */
    public void setParameter_name(String parameter_name) {
        this.parameter_name = parameter_name;
    }

    /**
     * パラメータ表示名を取得
     * @return パラメータ表示名
     */
    @Column(length = 40)
    public String getParameter_disp_name() {
        return this.parameter_disp_name;
    }

    /**
     * パラメータ表示名を設定
     * @param parameter_disp_name パラメータ表示名
     */
    public void setParameter_disp_name(String parameter_disp_name) {
        this.parameter_disp_name = parameter_disp_name;
    }

    /**
     * パラメータ値を取得
     * @return パラメータ値
     */
    @Column(length = 50)
    public String getParameter_value() {
        return this.parameter_value;
    }

    /**
     * パラメータ値を設定
     * @param parameter_value パラメータ値
     */
    public void setParameter_value(String parameter_value) {
        this.parameter_value = parameter_value;
    }

}
