package com.gnomes.external.data;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Column;

/**
 * 外部I/Fシステム定義 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class SystemDefine implements Serializable {

    /** 外部I/F対象システムコード */
    private String external_target_code;

    /** 外部I/F対象システム名称 */
    private String external_target_name;

    /** 送受信モード */
    private Integer send_recv_mode;

    /** 送信一時フォルダパス */
    private String send_temp_folder_path;

    /** 送信先フォルダパス */
    private String send_folder_path;

    /** 送信バックアップフォルダパス */
    private String send_backup_folder_path;

    /** 受信先フォルダパス */
    private String recv_folder_path;

    /** 受信処理フォルダパス */
    private String recv_proc_folder_path;

    /** 受信バックアップフォルダパス */
    private String recv_backup_folder_path;

    /** プロトコル種別 */
    private Integer protocol_type;

    /** タイムゾーン */
    private String time_zone;

    /**
     * 外部I/F対象システムコードを取得
     * @return 外部I/F対象システムコード
     */
    @Column(nullable = false, length = 2)
    public String getExternal_target_code() {
        return this.external_target_code;
    }

    /**
     * 外部I/F対象システムコードを設定
     * @param external_target_code 外部I/F対象システムコード (null不可)
     */
    public void setExternal_target_code(String external_target_code) {
        this.external_target_code = external_target_code;
    }

    /**
     * 外部I/F対象システム名称を取得
     * @return 外部I/F対象システム名称
     */
    @Column(nullable = false, length = 20)
    public String getExternal_target_name() {
        return this.external_target_name;
    }

    /**
     * 外部I/F対象システム名称を設定
     * @param external_target_name 外部I/F対象システム名称 (null不可)
     */
    public void setExternal_target_name(String external_target_name) {
        this.external_target_name = external_target_name;
    }

    /**
     * 送受信モードを取得
     * @return 送受信モード
     */
    @Column(nullable = false, length = 1)
    public int getSend_recv_mode() {
        return this.send_recv_mode;
    }

    /**
     * 送受信モードを設定
     * @param sendmode 送受信モード (null不可)
     */
    public void setSend_recv_mode(int send_recv_mode) {
        this.send_recv_mode = send_recv_mode;
    }

    /**
     * 送信一時フォルダパスを取得
     * @return 送信一時フォルダパス
     */
    @Column(length = 1000)
    public String getSend_temp_folder_path() {
        return this.send_temp_folder_path;
    }

    /**
     * 送信一時フォルダパスを設定
     * @param send_temp_folder_path 送信一時フォルダパス
     */
    public void setSend_temp_folder_path(String send_temp_folder_path) {
        this.send_temp_folder_path = send_temp_folder_path;
    }

    /**
     * 送信先フォルダパスを取得
     * @return 送信先フォルダパス
     */
    @Column(length = 1000)
    public String getSend_folder_path() {
        return this.send_folder_path;
    }

    /**
     * 送信先フォルダパスを設定
     * @param send_folder_path 送信先フォルダパス
     */
    public void setSend_folder_path(String send_folder_path) {
        this.send_folder_path = send_folder_path;
    }

    /**
     * 送信バックアップフォルダパスを取得
     * @return 送信バックアップフォルダパス
     */
    @Column(length = 1000)
    public String getSend_backup_folder_path() {
        return this.send_backup_folder_path;
    }

    /**
     * 送信バックアップフォルダパスを設定
     * @param send_backup_folder_path 送信バックアップフォルダパス
     */
    public void setSend_backup_folder_path(String send_backup_folder_path) {
        this.send_backup_folder_path = send_backup_folder_path;
    }

    /**
     * 受信先フォルダパスを取得
     * @return 受信先フォルダパス
     */
    @Column(length = 1000)
    public String getRecv_folder_path() {
        return this.recv_folder_path;
    }

    /**
     * 受信先フォルダパスを設定
     * @param recv_folder_path 受信先フォルダパス
     */
    public void setRecv_folder_path(String recv_folder_path) {
        this.recv_folder_path = recv_folder_path;
    }

    /**
     * 受信処理フォルダパスを取得
     * @return 受信処理フォルダパス
     */
    @Column(length = 1000)
    public String getRecv_proc_folder_path() {
        return this.recv_proc_folder_path;
    }

    /**
     * 受信処理フォルダパスを設定
     * @param recv_proc_folder_path 受信処理フォルダパス
     */
    public void setRecv_proc_folder_path(String recv_proc_folder_path) {
        this.recv_proc_folder_path = recv_proc_folder_path;
    }

    /**
     * 受信バックアップフォルダパスを取得
     * @return 受信バックアップフォルダパス
     */
    @Column(length = 1000)
    public String getRecv_backup_folder_path() {
        return this.recv_backup_folder_path;
    }

    /**
     * 受信バックアップフォルダパスを設定
     * @param recv_backup_folder_path 受信バックアップフォルダパス
     */
    public void setRecv_backup_folder_path(String recv_backup_folder_path) {
        this.recv_backup_folder_path = recv_backup_folder_path;
    }

    /**
     * プロトコル種別を取得
     * @return プロトコル種別
     */
    @Column(length = 1)
    public Integer getProtocol_type() {
        return this.protocol_type;
    }

    /**
     * プロトコル種別を設定
     * @param protocol_type プロトコル種別
     */
    public void setProtocol_type(Integer protocol_type) {
        this.protocol_type = protocol_type;
    }

    /**
     * タイムゾーンを取得
     * @return タイムゾーン
     */
    @Column(length = 100)
    public String getTime_zone() {
        return this.time_zone;
    }

    /**
     * タイムゾーンを設定
     * @param time_zone タイムゾーン
     */
    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

}