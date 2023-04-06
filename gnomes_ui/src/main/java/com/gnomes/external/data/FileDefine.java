package com.gnomes.external.data;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Column;

/**
 * 外部I/Fファイル構成定義
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class FileDefine implements Serializable {

    /** ファイル種別 */
    private String file_type;

    /** ファイル名称 */
    private String data_type_name;

    /** 送受信区分 */
    private int send_recv_type;

    /** 外部I/F対象システムコード */
    private String ext_target_code;

    /** データ項目長 */
    private int data_length;

    /** ファイル形式(CSV/固定長) */
    private int file_format;

    /** CSV区切り文字 */
    private String csv_delimiter;

    /** 文字列の括り文字 */
    private String char_bundle;

    /** 小数点以下の桁数 */
    private int decimal_length;

    /** 送受信ファイル名 */
    private String file_name;

    /** ビーンクラス名 */
    private String bean_class_name;

    /** コールクラス名 */
    private String call_class_name;

    /** 使用可能か否か */
    private int is_usable;

    /** エラー時に処理続行するか否か */
    private int is_continue_error;

    /** 作成ファイル文字コード */
    private String create_file_encode;

    /** 外部I/FフォーマットID */
    private String external_if_format_id;

    /** ヘッダ行数 */
    private int header_line_count;

    /** ヘッダーフォーマットID */
    private String header_format_id;

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
     * @param data_type ファイル種別 (null不可)
     */
    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    /**
     * ファイル名称を取得
     * @return ファイル名称
     */
    @Column(nullable = false, length = 20)
    public String getData_type_name() {
        return this.data_type_name;
    }

    /**
     * ファイル名称を設定
     * @param data_type_name ファイル名称 (null不可)
     */
    public void setData_type_name(String data_type_name) {
        this.data_type_name = data_type_name;
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
    public String getExt_target_code() {
        return this.ext_target_code;
    }

    /**
     * 外部I/F対象システムコードを設定
     * @param external_target_code 外部I/F対象システムコード (null不可)
     */
    public void setExt_target_code(String ext_target_code) {
        this.ext_target_code = ext_target_code;
    }

    /**
     * データ項目長を取得
     * @return データ項目長
     */
    @Column(nullable = false, length = 4)
    public int getData_length() {
        return this.data_length;
    }

    /**
     * データ項目長を設定
     * @param data_length データ項目長 (null不可)
     */
    public void setData_length(int data_length) {
        this.data_length = data_length;
    }

    /**
     * ファイル形式(CSV/固定長)を取得
     * @return ファイル形式(CSV/固定長)
     */
    @Column(nullable = false, length = 1)
    public int getFile_format() {
        return this.file_format;
    }

    /**
     * ファイル形式(CSV/固定長)を設定
     * @param file_format ファイル形式(CSV/固定長) (null不可)
     */
    public void setFile_format(int file_format) {
        this.file_format = file_format;
    }

    /**
     * CSV区切り文字を取得
     * @return CSV区切り文字
     */
    @Column(length = 2)
    public String getCsv_delimiter() {
        return this.csv_delimiter;
    }

    /**
     * CSV区切り文字を設定
     * @param csv_delimiter CSV区切り文字
     */
    public void setCsv_delimiter(String csv_delimiter) {
        this.csv_delimiter = csv_delimiter;
    }

    /**
     * 文字列の括り文字を取得
     * @return 文字列の括り文字
     */
    @Column(length = 2)
    public String getChar_bundle() {
        return this.char_bundle;
    }

    /**
     * 文字列の括り文字を設定
     * @param char_bundle 文字列の括り文字
     */
    public void setChar_bundle(String char_bundle) {
        this.char_bundle = char_bundle;
    }

    /**
     * 小数点以下の桁数を取得
     * @return 小数点以下の桁数
     */
    @Column(nullable = false, length = 4)
    public int getDecimal_length() {
        return this.decimal_length;
    }

    /**
     * 小数点以下の桁数を設定
     * @param decimal_length 小数点以下の桁数 (null不可)
     */
    public void setDecimal_length(int decimal_length) {
        this.decimal_length = decimal_length;
    }

    /**
     * 送受信ファイル名を取得
     * @return 送受信ファイル名
     */
    @Column(nullable = false, length = 20)
    public String getFile_name() {
        return this.file_name;
    }

    /**
     * 送受信ファイル名を設定
     * @param file_name 送受信ファイル名 (null不可)
     */
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    /**
     * ビーンクラス名を取得
     * @return ビーンクラス名
     */
    @Column(nullable = false, length = 80)
    public String getBean_class_name() {
        return this.bean_class_name;
    }

    /**
     * ビーンクラス名を設定
     * @param bean_class_name ビーンクラス名 (null不可)
     */
    public void setBean_class_name(String bean_class_name) {
        this.bean_class_name = bean_class_name;
    }

    /**
     * コールクラス名を取得
     * @return コールクラス名
     */
    @Column(nullable = false, length = 80)
    public String getCall_class_name() {
        return this.call_class_name;
    }

    /**
     * コールクラス名を設定
     * @param call_class_name コールクラス名 (null不可)
     */
    public void setCall_class_name(String call_class_name) {
        this.call_class_name = call_class_name;
    }

    /**
     * 使用可能か否かを取得
     * @return 使用可能か否か
     */
    @Column(nullable = false, length = 1)
    public int getIs_usable() {
        return this.is_usable;
    }

    /**
     * 使用可能か否かを設定
     * @param is_usable 使用可能か否か (null不可)
     */
    public void setIs_usable(int is_usable) {
        this.is_usable = is_usable;
    }

    /**
     * エラー時に処理続行するか否かを取得
     * @return エラー時に処理続行するか否か
     */
    @Column(nullable = false, length = 1)
    public int getIs_continue_error() {
        return this.is_continue_error;
    }

    /**
     * エラー時に処理続行するか否かを設定
     * @param is_continue_error エラー時に処理続行するか否か (null不可)
     */
    public void setIs_continue_error(int is_continue_error) {
        this.is_continue_error = is_continue_error;
    }

    /**
     * 作成ファイル文字コードを取得
     * @return create_file_encode 作成ファイル文字コード
     */
    public String getCreate_file_encode() {
        return create_file_encode;
    }

    /**
     * 作成ファイル文字コードを設定
     * @param create_file_encode 作成ファイル文字コード
     */
    public void setCreate_file_encode(String create_file_encode) {
        this.create_file_encode = create_file_encode;
    }

    /** 外部I/FフォーマットIDを取得
     * @return 外部I/FフォーマットID
     */
    public String getExternal_if_format_id() {
        return external_if_format_id;
    }

    /**
     * 外部I/FフォーマットIDを設定
     * @param external_if_format_id 外部I/FフォーマットID
     */
    public void setExternal_if_format_id(String external_if_format_id) {
        this.external_if_format_id = external_if_format_id;
    }

    /**
     * ヘッダ行数を取得
     * @return ヘッダ行数
     */
    @Column(nullable = false, length = 4)
    public int getHeader_line_count() {
        return this.header_line_count;
    }

    /**
     * ヘッダ行数を設定
     * @param data_length ヘッダ行数 (null不可)
     */
    public void setHeader_line_count(int header_line_count) {
        this.header_line_count = header_line_count;
    }

    /** ヘッダーフォーマットIDを取得
     * @return ヘッダーフォーマットID
     */
    public String getHeader_format_id() {
        return header_format_id;
    }

    /**
     * ヘッダーフォーマットIDを設定
     * @param external_if_format_id ヘッダーフォーマットID
     */
    public void setHeader_format_id(String header_format_id) {
        this.header_format_id = header_format_id;
    }


}
