package com.gnomes.external.data;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Column;

/**
 * 外部I/Fデータ項目定義
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class DataDefine implements Serializable {

    /** データ項目番号 */
    private Integer data_item_number;

    /** 送受信区分 */
    private int send_recv_type;

    /** データ項目識別ID */
    private String data_item_id;

    /** データ項目名称 */
    private String data_item_name;

    /** データ開始位置 */
    private Integer data_start;

    /** データ桁数 */
    private Integer data_length;

    /** フォーマット識別ID(文字列・日付・数値) */
    private int format_id;

    /** 日付フォーマット */
    private String date_format;

    /** 必須チェックあり/なし */
    private int data_check;

    /** 桁数チェックあり/なし */
    private int length_check;

    /** データ項目識別ID有効/無効 */
    private int is_data_item_id;

    /** データ処理区分 */
    private Integer data_proc_type;

    /** 固定値文字列 */
    private String fixed_value_string;

    /** 除外文字列 */
    private String remove_string;

    /** ヘッダー項目種類 */
    private Integer header_item_type;

    /** CSVデータ位置 */
    private Integer csv_data_position;


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
     * データ項目識別IDを取得
     * @return データ項目識別ID
     */
    @Column(nullable = false, length = 10)
    public String getData_item_id() {
        return this.data_item_id;
    }

    /**
     * データ項目識別IDを設定
     * @param data_item_id データ項目識別ID (null不可)
     */
    public void setData_item_id(String data_item_id) {
        this.data_item_id = data_item_id;
    }

    /**
     * データ項目名称を取得
     * @return データ項目名称
     */
    @Column(length = 20)
    public String getData_item_name() {
        return this.data_item_name;
    }

    /**
     * データ項目名称を設定
     * @param data_item_name データ項目名称
     */
    public void setData_item_name(String data_item_name) {
        this.data_item_name = data_item_name;
    }

    /**
     * データ項目番号を取得
     * @return データ項目番号
     */
    @Column(length = 4)
    public Integer getData_item_number() {
        return this.data_item_number;
    }

    /**
     * データ項目番号を設定
     * @param data_item_number データ項目番号
     */
    public void setData_item_number(Integer data_item_number) {
        this.data_item_number = data_item_number;
    }

    /**
     * データ開始位置を取得
     * @return データ開始位置
     */
    @Column(length = 4)
    public Integer getData_start() {
        return this.data_start;
    }

    /**
     * データ開始位置を設定
     * @param data_start データ開始位置
     */
    public void setData_start(Integer data_start) {
        this.data_start = data_start;
    }

    /**
     * データ桁数を取得
     * @return データ桁数
     */
    @Column(length = 4)
    public Integer getData_length() {
        return this.data_length;
    }

    /**
     * データ桁数を設定
     * @param data_length データ桁数
     */
    public void setData_length(Integer data_length) {
        this.data_length = data_length;
    }

    /**
     * フォーマット識別ID(文字列・日付・数値)を取得
     * @return フォーマット識別ID(文字列・日付・数値)
     */
    @Column(nullable = false, length = 2)
    public int getFormat_id() {
        return this.format_id;
    }

    /**
     * フォーマット識別ID(文字列・日付・数値)を設定
     * @param format_id フォーマット識別ID(文字列・日付・数値) (null不可)
     */
    public void setFormat_id(int format_id) {
        this.format_id = format_id;
    }

    /**
     * 日付フォーマットを取得
     * @return 日付フォーマット
     */
    @Column(length = 10)
    public String getDate_format() {
        return this.date_format;
    }

    /**
     * 日付フォーマットを設定
     * @param date_format 日付フォーマット
     */
    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    /**
     * 必須チェックあり/なしを取得
     * @return 必須チェックあり/なし
     */
    @Column(nullable = false, length = 1)
    public int getData_check() {
        return this.data_check;
    }

    /**
     * 必須チェックあり/なしを設定
     * @param data_check 必須チェックあり/なし (null不可)
     */
    public void setData_check(int data_check) {
        this.data_check = data_check;
    }

    /**
     * 桁数チェックあり/なしを取得
     * @return 桁数チェックあり/なし
     */
    @Column(nullable = false, length = 1)
    public int getLength_check() {
        return this.length_check;
    }

    /**
     * 桁数チェックあり/なしを設定
     * @param length_check 桁数チェックあり/なし (null不可)
     */
    public void setLength_check(int length_check) {
        this.length_check = length_check;
    }

    /**
     * データ項目識別ID有効/無効を取得
     * @return データ項目識別ID有効/無効
     */
    @Column(nullable = false, length = 1)
    public int getIsdata_item_id() {
        return this.is_data_item_id;
    }

    /**
     * データ項目識別ID有効/無効を設定
     * @param is_data_item_id データ項目識別ID有効/無効 (null不可)
     */
    public void setIsdata_item_id(int is_data_item_id) {
        this.is_data_item_id = is_data_item_id;
    }

    /**
     * データ処理区分を取得
     * @return データ処理区分
     */
    @Column(length = 2)
    public Integer getData_proc_type() {
        return this.data_proc_type;
    }

    /**
     * データ処理区分を設定
     * @param data_proc_type データ処理区分
     */
    public void setData_proc_type(Integer data_proc_type) {
        this.data_proc_type = data_proc_type;
    }

    /**
     * 固定値文字列を取得
     * @return 固定値文字列
     */
    @Column(length = 1000)
    public String getFixed_value_string() {
        return this.fixed_value_string;
    }

    /**
     * 固定値文字列を設定
     * @param data_item_name 固定値文字列
     */
    public void setFixed_value_string(String fixed_value_string) {
        this.fixed_value_string = fixed_value_string;
    }

    /**
     * 除外文字列を取得
     * @return 除外文字列
     */
    @Column(length = 1000)
    public String getRemove_string() {
    	return this.remove_string;
    }

    /**
     * 除外文字列を設定
     * @param remove_string 除外文字列
     */
    public void setRemove_string_string(String remove_string) {
    	this.remove_string = remove_string;
    }

    /**
     * ヘッダー項目種類を取得
     * @return ヘッダー項目種類
     */
    @Column(length = 2)
    public Integer getHeader_item_type() {
        return this.header_item_type;
    }

    /**
     * ヘッダー項目種類を設定
     * @param is_data_item_id ヘッダー項目種類
     */
    public void setHeader_item_type(Integer header_item_type) {
        this.header_item_type = header_item_type;
    }

    /**
     * CSVデータ位置を取得
     * @return CSVデータ位置
     */
    public Integer getCsv_data_position() {
    	return this.csv_data_position;
    }

    /**
     * CSVデータ位置を設定
     * @param csv_data_position CSVデータ位置
     */
    public void setCsv_data_position(Integer csv_data_position) {
    	this.csv_data_position = csv_data_position;
    }

}
