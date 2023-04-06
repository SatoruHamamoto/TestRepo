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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zr120帳票ラベル印字履歴 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/04/12 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "history_printout")
@NamedQueries({
        @NamedQuery(name = "HistoryPrintout.findAll", query = "SELECT p FROM HistoryPrintout p"),
        @NamedQuery(name = "HistoryPrintout.findByPK", query = "SELECT p FROM HistoryPrintout p WHERE p.history_printout_key = :history_printout_key")
})
public class HistoryPrintout extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "history_printout";

    /** nk帳票ラベル印字履歴キー */
    public static final String COLUMN_NAME_HISTORY_PRINTOUT_KEY = "history_printout_key";

    /** nk要求イベントID */
    public static final String COLUMN_NAME_EVENT_ID = "event_id";

    /** nk要求内連番 */
    public static final String COLUMN_NAME_REQUEST_SEQ = "request_seq";

    /** 発生日時 */
    public static final String COLUMN_NAME_OCCUR_DATE = "occur_date";

    /** ReportID */
    public static final String COLUMN_NAME_REPORT_ID = "report_id";

    /** 再印字フラグ */
    public static final String COLUMN_NAME_REPRINT_FLAG = "reprint_flag";

    /** 再印字回数 */
    public static final String COLUMN_NAME_PRINTOUT_NUM = "printout_num";

    /** 端末ID */
    public static final String COLUMN_NAME_COMPUTER_ID = "computer_id";

    /** 端末名 */
    public static final String COLUMN_NAME_COMPUTER_NAME = "computer_name";

    /** 帳票様式番号 */
    public static final String COLUMN_NAME_PRINT_COMMAND_NO = "print_command_no";

    /** 帳票印字日時 */
    public static final String COLUMN_NAME_PRINTOUT_DATE = "printout_date";

    /** 帳票種類 */
    public static final String COLUMN_NAME_PRINTER_TYPE = "printer_type";

    /** 印字枚数 */
    public static final String COLUMN_NAME_PRINTOUT_COPIES = "printout_copies";

    /** 帳票名 */
    public static final String COLUMN_NAME_REPORT_NAME = "report_name";

    /** DB領域区分 */
    public static final String COLUMN_NAME_DB_AREA_DIV = "db_area_div";

    /** プリンタID */
    public static final String COLUMN_NAME_PRINTER_ID = "printer_id";

    /** プリンタ名 */
    public static final String COLUMN_NAME_PRINTER_NAME = "printer_name";

    /** ユーザID */
    public static final String COLUMN_NAME_USER_ID = "user_id";

    /** ユーザ名 */
    public static final String COLUMN_NAME_USER_NAME = "user_name";

    /** 画面表示用Key情報 */
    public static final String COLUMN_NAME_DISPLAY_KEY_TEXT = "display_key_text";

    /** 印字理由コード */
    public static final String COLUMN_NAME_PRINT_REASON_CODE = "print_reason_code";

    /** 印字理由名 */
    public static final String COLUMN_NAME_PRINT_REASON_NAME = "print_reason_name";

    /** 印字理由コメント */
    public static final String COLUMN_NAME_PRINT_REASON_COMMENT = "print_reason_comment";

    /** 再印刷マーク出力有無 */
    public static final String COLUMN_NAME_IS_RE_PRINT_MARK = "is_re_print_mark";

    /** 再印刷マーク出力位置 */
    public static final String COLUMN_NAME_POSITION_RE_PRINT_MARK = "position_re_print_mark";

    /** 電子ファイル作成区分 */
    public static final String COLUMN_NAME_IS_FILE_CREATE_TYPE = "is_file_create_type";

    /** PDF電子ファイル名 */
    public static final String COLUMN_NAME_PDF_FILE_NAME = "pdf_file_name";

    /** EXCEL電子ファイル名 */
    public static final String COLUMN_NAME_EXCEL_FILE_NAME = "excel_file_name";

    /** 帳票印字結果状態 */
    public static final String COLUMN_NAME_PRINTOUT_STATUS = "printout_status";

    /** 印字エラーメッセージ */
    public static final String COLUMN_NAME_PRINTOUT_ERROR_MESSAGE = "printout_error_message";

    /** 再印字ソース要求イベントID */
    public static final String COLUMN_NAME_REPRINT_SOURCE_EVENT_ID = "reprint_source_event_id";

    /** 再印字ソース要求内連番 */
    public static final String COLUMN_NAME_REPRINT_SOURCE_REQUEST_SEQ = "reprint_source_request_seq";

    /** 帳票印字様式マスターの帳票印字機能区分 */
    public static final String COLUMN_NAME_PRINT_FUNCTION_DIV = "printFunctionDiv";

    /** 帳票印字様式マスターの帳票定義単位区分 */
    public static final String COLUMN_NAME_PRINT_DEFINE_SCOPE_DIV = "printDefineScopeDiv";

    /** 帳票印字様式マスターの指図工程コード */
    public static final String COLUMN_NAME_PROCESS_CODE = "processCode";

    /** 帳票印字様式マスターの試験目的区分 */
    public static final String COLUMN_NAME_INSPECTION_PURPOSE_DIV = "inspectionPurposeDiv";

    /** 帳票印字様式マスターの品目タイプコード */
    public static final String COLUMN_NAME_ITEM_TYPE_CODE = "itemTypeCode";

    /** 帳票印字様式マスターの品目コード */
    public static final String COLUMN_NAME_ITEM_CODE = "itemCode";

    /** 帳票印字様式マスターのレシピID */
    public static final String COLUMN_NAME_RECIPE_ID = "recipeID";

    /** 帳票印字様式マスターのレシピVER */
    public static final String COLUMN_NAME_RECIPE_VERSION = "recipeVersion";

    /** 帳票印字様式マスターのレシピREV */
    public static final String COLUMN_NAME_RECIPE_REVISION = "recipeRevision";

    /** nk帳票ラベル印字履歴キー */
    private String history_printout_key;
    /** nk要求イベントID */
    private String event_id;
    /** nk要求内連番 */
    private int request_seq;
    /** 発生日時 */
    private Date occur_date;
    /** ReportID */
    private String report_id;
    /** 再印字フラグ */
    private int reprint_flag;
    /** 再印字回数 */
    private Integer printout_num;
    /** 端末ID */
    private String computer_id;
    /** 端末名 */
    private String computer_name;
    /** 帳票様式番号 */
    private String print_command_no;
    /** 帳票印字日時 */
    private Date printout_date;
    /** 帳票種類 */
    private int printer_type;
    /** 印字枚数 */
    private Integer printout_copies;
    /** 帳票名 */
    private String report_name;
    /** DB領域区分 */
    private int db_area_div;
    /** プリンタID */
    private String printer_id;
    /** プリンタ名 */
    private String printer_name;
    /** ユーザID */
    private String user_id;
    /** ユーザ名 */
    private String user_name;
    /** 画面表示用Key情報 */
    private String display_key_text;
    /** 印字理由コード */
    private String print_reason_code;
    /** 印字理由名 */
    private String print_reason_name;
    /** 印字理由コメント */
    private String print_reason_comment;
    /** 再印刷マーク出力有無 */
    private int is_re_print_mark;
    /** 再印刷マーク出力位置 */
    private String position_re_print_mark;
    /** 電子ファイル作成区分 */
    private int is_file_create_type;
    /** PDF電子ファイル名 */
    private String pdf_file_name;
    /** EXCEL電子ファイル名 */
    private String excel_file_name;
    /** 帳票印字結果状態 */
    private Integer printout_status;
    /** 印字エラーメッセージ */
    private String printout_error_message;
    /** 再印字ソース要求イベントID */
    private String reprint_source_event_id;
    /** 再印字ソース要求内連番 */
    private int reprint_source_request_seq;
    /** 帳票印字様式マスターの帳票印字機能区分 */
    private String printFunctionDiv;
    /** 帳票印字様式マスターの帳票定義単位区分 */
    private Integer printDefineScopeDiv;
    /** 帳票印字様式マスターの指図工程コード */
    private String processCode;
    /** 帳票印字様式マスターの試験目的区分 */
    private Integer inspectionPurposeDiv;
    /** 帳票印字様式マスターの品目タイプコード */
    private String itemTypeCode;
    /** 帳票印字様式マスターの品目コード */
    private String itemCode;
    /** 帳票印字様式マスターのレシピID */
    private String recipeID;
    /** 帳票印字様式マスターのレシピVER */
    private String recipeVersion;
    /** 帳票印字様式マスターのレシピREV */
    private Integer recipeRevision;


    /**
     * Zr120帳票ラベル印字履歴エンティティ コンストラクタ
     */
    public HistoryPrintout() {
    }

    /**
     * Zr120帳票ラベル印字履歴エンティティ コンストラクタ
     * @param history_printout_key nk帳票ラベル印字履歴キー
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param occur_date 発生日時
     * @param report_id ReportID
     * @param reprint_flag 再印字フラグ
     * @param computer_id 端末ID
     * @param computer_name 端末名
     * @param printout_date 帳票印字日時
     * @param printer_type 帳票種類
     * @param db_area_div DB領域区分
     * @param printer_id プリンタID
     * @param user_id ユーザID
     * @param user_name ユーザ名
     * @param is_re_print_mark 再印刷マーク出力有無
     * @param is_file_create_type 電子ファイル作成区分
     * @param reprint_source_event_id 再印字ソース要求イベントID
     * @param reprint_source_request_seq 再印字ソース要求内連番
     * @param version 更新バージョン
     */
    public HistoryPrintout(String history_printout_key, String event_id, int request_seq, Date occur_date, String report_id, int reprint_flag, String computer_id, String computer_name, Date printout_date, int printer_type, int db_area_div, String printer_id, String user_id, String user_name, int is_re_print_mark, int is_file_create_type, String reprint_source_event_id, int reprint_source_request_seq, int version) {
        this.history_printout_key = history_printout_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.occur_date = occur_date;
        this.report_id = report_id;
        this.reprint_flag = reprint_flag;
        this.computer_id = computer_id;
        this.computer_name = computer_name;
        this.printout_date = printout_date;
        this.printer_type = printer_type;
        this.db_area_div = db_area_div;
        this.printer_id = printer_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.is_re_print_mark = is_re_print_mark;
        this.is_file_create_type = is_file_create_type;
        this.reprint_source_event_id = reprint_source_event_id;
        this.reprint_source_request_seq = reprint_source_request_seq;
        super.setVersion(version);
    }

    /**
     * Zr120帳票ラベル印字履歴エンティティ コンストラクタ
     * @param history_printout_key nk帳票ラベル印字履歴キー
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param occur_date 発生日時
     * @param report_id ReportID
     * @param reprint_flag 再印字フラグ
     * @param printout_num 再印字回数
     * @param computer_id 端末ID
     * @param computer_name 端末名
     * @param print_command_no 帳票様式番号
     * @param printout_date 帳票印字日時
     * @param printer_type 帳票種類
     * @param printout_copies 印字枚数
     * @param report_name 帳票名
     * @param db_area_div DB領域区分
     * @param printer_id プリンタID
     * @param printer_name プリンタ名
     * @param user_id ユーザID
     * @param user_name ユーザ名
     * @param display_key_text 画面表示用Key情報
     * @param print_reason_code 印字理由コード
     * @param print_reason_name 印字理由名
     * @param print_reason_comment 印字理由コメント
     * @param is_re_print_mark 再印刷マーク出力有無
     * @param position_re_print_mark 再印刷マーク出力位置
     * @param is_file_create_type 電子ファイル作成区分
     * @param pdf_file_name PDF電子ファイル名
     * @param excel_file_name EXCEL電子ファイル名
     * @param printout_status 帳票印字結果状態
     * @param printout_error_message 印字エラーメッセージ
     * @param reprint_source_event_id 再印字ソース要求イベントID
     * @param reprint_source_request_seq 再印字ソース要求内連番
     * @param printFunctionDiv 帳票印字様式マスターの帳票印字機能区分
     * @param printDefineScopeDiv 帳票印字様式マスターの帳票定義単位区分
     * @param processCode 帳票印字様式マスターの指図工程コード
     * @param inspectionPurposeDiv 帳票印字様式マスターの試験目的区分
     * @param itemTypeCode 帳票印字様式マスターの品目タイプコード
     * @param itemCode 帳票印字様式マスターの品目コード
     * @param recipeID 帳票印字様式マスターのレシピID
     * @param recipeVersion 帳票印字様式マスターのレシピVER
     * @param recipeRevision 帳票印字様式マスターのレシピREV
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
    public HistoryPrintout(String history_printout_key, String event_id, int request_seq, Date occur_date, String report_id, int reprint_flag, Integer printout_num, String computer_id, String computer_name, String print_command_no, Date printout_date, int printer_type, Integer printout_copies, String report_name, int db_area_div, String printer_id, String printer_name, String user_id, String user_name, String display_key_text, String print_reason_code, String print_reason_name, String print_reason_comment, int is_re_print_mark, String position_re_print_mark, int is_file_create_type, String pdf_file_name, String excel_file_name, Integer printout_status, String printout_error_message, String reprint_source_event_id, int reprint_source_request_seq, String printFunctionDiv, Integer printDefineScopeDiv, String processCode, Integer inspectionPurposeDiv, String itemTypeCode, String itemCode, String recipeID, String recipeVersion, Integer recipeRevision, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.history_printout_key = history_printout_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.occur_date = occur_date;
        this.report_id = report_id;
        this.reprint_flag = reprint_flag;
        this.printout_num = printout_num;
        this.computer_id = computer_id;
        this.computer_name = computer_name;
        this.print_command_no = print_command_no;
        this.printout_date = printout_date;
        this.printer_type = printer_type;
        this.printout_copies = printout_copies;
        this.report_name = report_name;
        this.db_area_div = db_area_div;
        this.printer_id = printer_id;
        this.printer_name = printer_name;
        this.user_id = user_id;
        this.user_name = user_name;
        this.display_key_text = display_key_text;
        this.print_reason_code = print_reason_code;
        this.print_reason_name = print_reason_name;
        this.print_reason_comment = print_reason_comment;
        this.is_re_print_mark = is_re_print_mark;
        this.position_re_print_mark = position_re_print_mark;
        this.is_file_create_type = is_file_create_type;
        this.pdf_file_name = pdf_file_name;
        this.excel_file_name = excel_file_name;
        this.printout_status = printout_status;
        this.printout_error_message = printout_error_message;
        this.reprint_source_event_id = reprint_source_event_id;
        this.reprint_source_request_seq = reprint_source_request_seq;
        this.printFunctionDiv = printFunctionDiv;
        this.printDefineScopeDiv = printDefineScopeDiv;
        this.processCode = processCode;
        this.inspectionPurposeDiv = inspectionPurposeDiv;
        this.itemTypeCode = itemTypeCode;
        this.itemCode = itemCode;
        this.recipeID = recipeID;
        this.recipeVersion = recipeVersion;
        this.recipeRevision = recipeRevision;
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
     * nk帳票ラベル印字履歴キーを取得
     * @return nk帳票ラベル印字履歴キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getHistory_printout_key() {
        return this.history_printout_key;
    }

    /**
     * nk帳票ラベル印字履歴キーを設定
     * @param history_printout_key nk帳票ラベル印字履歴キー (null不可)
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
     * 発生日時を取得
     * @return 発生日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getOccur_date() {
        return this.occur_date;
    }

    /**
     * 発生日時を設定
     * @param occur_date 発生日時 (null不可)
     */
    public void setOccur_date(Date occur_date) {
        this.occur_date = occur_date;
    }

    /**
     * ReportIDを取得
     * @return ReportID
     */
    @Column(nullable = false, length = 100)
    public String getReport_id() {
        return this.report_id;
    }

    /**
     * ReportIDを設定
     * @param report_id ReportID (null不可)
     */
    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    /**
     * 再印字フラグを取得
     * @return 再印字フラグ
     */
    @Column(nullable = false, length = 1)
    public int getReprint_flag() {
        return this.reprint_flag;
    }

    /**
     * 再印字フラグを設定
     * @param reprint_flag 再印字フラグ (null不可)
     */
    public void setReprint_flag(int reprint_flag) {
        this.reprint_flag = reprint_flag;
    }

    /**
     * 再印字回数を取得
     * @return 再印字回数
     */
    @Column(length = 3)
    public Integer getPrintout_num() {
        return this.printout_num;
    }

    /**
     * 再印字回数を設定
     * @param printout_num 再印字回数
     */
    public void setPrintout_num(Integer printout_num) {
        this.printout_num = printout_num;
    }

    /**
     * 端末IDを取得
     * @return 端末ID
     */
    @Column(nullable = false, length = 20)
    public String getComputer_id() {
        return this.computer_id;
    }

    /**
     * 端末IDを設定
     * @param computer_id 端末ID (null不可)
     */
    public void setComputer_id(String computer_id) {
        this.computer_id = computer_id;
    }

    /**
     * 端末名を取得
     * @return 端末名
     */
    @Column(nullable = false, length = 40)
    public String getComputer_name() {
        return this.computer_name;
    }

    /**
     * 端末名を設定
     * @param computer_name 端末名 (null不可)
     */
    public void setComputer_name(String computer_name) {
        this.computer_name = computer_name;
    }

    /**
     * 帳票様式番号を取得
     * @return 帳票様式番号
     */
    @Column(length = 20)
    public String getPrint_command_no() {
        return this.print_command_no;
    }

    /**
     * 帳票様式番号を設定
     * @param print_command_no 帳票様式番号
     */
    public void setPrint_command_no(String print_command_no) {
        this.print_command_no = print_command_no;
    }

    /**
     * 帳票印字日時を取得
     * @return 帳票印字日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getPrintout_date() {
        return this.printout_date;
    }

    /**
     * 帳票印字日時を設定
     * @param printout_date 帳票印字日時 (null不可)
     */
    public void setPrintout_date(Date printout_date) {
        this.printout_date = printout_date;
    }

    /**
     * 帳票種類を取得
     * @return 帳票種類
     */
    @Column(nullable = false, length = 2)
    public int getPrinter_type() {
        return this.printer_type;
    }

    /**
     * 帳票種類を設定
     * @param printer_type 帳票種類 (null不可)
     */
    public void setPrinter_type(int printer_type) {
        this.printer_type = printer_type;
    }

    /**
     * 印字枚数を取得
     * @return 印字枚数
     */
    @Column(length = 3)
    public Integer getPrintout_copies() {
        return this.printout_copies;
    }

    /**
     * 印字枚数を設定
     * @param printout_copies 印字枚数
     */
    public void setPrintout_copies(Integer printout_copies) {
        this.printout_copies = printout_copies;
    }

    /**
     * 帳票名を取得
     * @return 帳票名
     */
    @Column(length = 20)
    public String getReport_name() {
        return this.report_name;
    }

    /**
     * 帳票名を設定
     * @param report_name 帳票名
     */
    public void setReport_name(String report_name) {
        this.report_name = report_name;
    }

    /**
     * DB領域区分を取得
     * @return DB領域区分
     */
    @Column(nullable = false, length = 1)
    public int getDb_area_div() {
        return this.db_area_div;
    }

    /**
     * DB領域区分を設定
     * @param db_area_div DB領域区分 (null不可)
     */
    public void setDb_area_div(int db_area_div) {
        this.db_area_div = db_area_div;
    }

    /**
     * プリンタIDを取得
     * @return プリンタID
     */
    @Column(nullable = false, length = 20)
    public String getPrinter_id() {
        return this.printer_id;
    }

    /**
     * プリンタIDを設定
     * @param printer_id プリンタID (null不可)
     */
    public void setPrinter_id(String printer_id) {
        this.printer_id = printer_id;
    }

    /**
     * プリンタ名を取得
     * @return プリンタ名
     */
    @Column(length = 128)
    public String getPrinter_name() {
        return this.printer_name;
    }

    /**
     * プリンタ名を設定
     * @param printer_name プリンタ名
     */
    public void setPrinter_name(String printer_name) {
        this.printer_name = printer_name;
    }

    /**
     * ユーザIDを取得
     * @return ユーザID
     */
    @Column(nullable = false, length = 20)
    public String getUser_id() {
        return this.user_id;
    }

    /**
     * ユーザIDを設定
     * @param user_id ユーザID (null不可)
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * ユーザ名を取得
     * @return ユーザ名
     */
    @Column(nullable = false, length = 40)
    public String getUser_name() {
        return this.user_name;
    }

    /**
     * ユーザ名を設定
     * @param user_name ユーザ名 (null不可)
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * 画面表示用Key情報を取得
     * @return 画面表示用Key情報
     */
    @Column(length = 500)
    public String getDisplay_key_text() {
        return this.display_key_text;
    }

    /**
     * 画面表示用Key情報を設定
     * @param display_key_text 画面表示用Key情報
     */
    public void setDisplay_key_text(String display_key_text) {
        this.display_key_text = display_key_text;
    }

    /**
     * 印字理由コードを取得
     * @return 印字理由コード
     */
    @Column(length = 20)
    public String getPrint_reason_code() {
        return this.print_reason_code;
    }

    /**
     * 印字理由コードを設定
     * @param print_reason_code 印字理由コード
     */
    public void setPrint_reason_code(String print_reason_code) {
        this.print_reason_code = print_reason_code;
    }

    /**
     * 印字理由名を取得
     * @return 印字理由名
     */
    @Column(length = 50)
    public String getPrint_reason_name() {
        return this.print_reason_name;
    }

    /**
     * 印字理由名を設定
     * @param print_reason_name 印字理由名
     */
    public void setPrint_reason_name(String print_reason_name) {
        this.print_reason_name = print_reason_name;
    }

    /**
     * 印字理由コメントを取得
     * @return 印字理由コメント
     */
    @Column(length = 100)
    public String getPrint_reason_comment() {
        return this.print_reason_comment;
    }

    /**
     * 印字理由コメントを設定
     * @param print_reason_comment 印字理由コメント
     */
    public void setPrint_reason_comment(String print_reason_comment) {
        this.print_reason_comment = print_reason_comment;
    }

    /**
     * 再印刷マーク出力有無を取得
     * @return 再印刷マーク出力有無
     */
    @Column(nullable = false, length = 1)
    public int getIs_re_print_mark() {
        return this.is_re_print_mark;
    }

    /**
     * 再印刷マーク出力有無を設定
     * @param is_re_print_mark 再印刷マーク出力有無 (null不可)
     */
    public void setIs_re_print_mark(int is_re_print_mark) {
        this.is_re_print_mark = is_re_print_mark;
    }

    /**
     * 再印刷マーク出力位置を取得
     * @return 再印刷マーク出力位置
     */
    @Column(length = 20)
    public String getPosition_re_print_mark() {
        return this.position_re_print_mark;
    }

    /**
     * 再印刷マーク出力位置を設定
     * @param position_re_print_mark 再印刷マーク出力位置
     */
    public void setPosition_re_print_mark(String position_re_print_mark) {
        this.position_re_print_mark = position_re_print_mark;
    }

    /**
     * 電子ファイル作成区分を取得
     * @return 電子ファイル作成区分
     */
    @Column(nullable = false, length = 1)
    public int getIs_file_create_type() {
        return this.is_file_create_type;
    }

    /**
     * 電子ファイル作成区分を設定
     * @param is_file_create_type 電子ファイル作成区分 (null不可)
     */
    public void setIs_file_create_type(int is_file_create_type) {
        this.is_file_create_type = is_file_create_type;
    }

    /**
     * PDF電子ファイル名を取得
     * @return PDF電子ファイル名
     */
    @Column(length = 140)
    public String getPdf_file_name() {
        return this.pdf_file_name;
    }

    /**
     * PDF電子ファイル名を設定
     * @param pdf_file_name PDF電子ファイル名
     */
    public void setPdf_file_name(String pdf_file_name) {
        this.pdf_file_name = pdf_file_name;
    }

    /**
     * EXCEL電子ファイル名を取得
     * @return EXCEL電子ファイル名
     */
    @Column(length = 140)
    public String getExcel_file_name() {
        return this.excel_file_name;
    }

    /**
     * EXCEL電子ファイル名を設定
     * @param excel_file_name EXCEL電子ファイル名
     */
    public void setExcel_file_name(String excel_file_name) {
        this.excel_file_name = excel_file_name;
    }

    /**
     * 帳票印字結果状態を取得
     * @return 帳票印字結果状態
     */
    @Column(length = 1)
    public Integer getPrintout_status() {
        return this.printout_status;
    }

    /**
     * 帳票印字結果状態を設定
     * @param printout_status 帳票印字結果状態
     */
    public void setPrintout_status(Integer printout_status) {
        this.printout_status = printout_status;
    }

    /**
     * 印字エラーメッセージを取得
     * @return 印字エラーメッセージ
     */
    @Column(length = 2000)
    public String getPrintout_error_message() {
        return this.printout_error_message;
    }

    /**
     * 印字エラーメッセージを設定
     * @param printout_error_message 印字エラーメッセージ
     */
    public void setPrintout_error_message(String printout_error_message) {
        this.printout_error_message = printout_error_message;
    }

    /**
     * 再印字ソース要求イベントIDを取得
     * @return 再印字ソース要求イベントID
     */
    @Column(nullable = false, length = 38)
    public String getReprint_source_event_id() {
        return this.reprint_source_event_id;
    }

    /**
     * 再印字ソース要求イベントIDを設定
     * @param reprint_source_event_id 再印字ソース要求イベントID (null不可)
     */
    public void setReprint_source_event_id(String reprint_source_event_id) {
        this.reprint_source_event_id = reprint_source_event_id;
    }

    /**
     * 再印字ソース要求内連番を取得
     * @return 再印字ソース要求内連番
     */
    @Column(nullable = false, length = 3)
    public int getReprint_source_request_seq() {
        return this.reprint_source_request_seq;
    }

    /**
     * 再印字ソース要求内連番を設定
     * @param reprint_source_request_seq 再印字ソース要求内連番 (null不可)
     */
    public void setReprint_source_request_seq(int reprint_source_request_seq) {
        this.reprint_source_request_seq = reprint_source_request_seq;
    }

    /**
     * 帳票印字様式マスターの帳票印字機能区分を取得
     * @return 帳票印字様式マスターの帳票印字機能区分
     */
    @Column(length = 4)
    public String getPrintFunctionDiv() {
        return this.printFunctionDiv;
    }

    /**
     * 帳票印字様式マスターの帳票印字機能区分を設定
     * @param printFunctionDiv 帳票印字様式マスターの帳票印字機能区分
     */
    public void setPrintFunctionDiv(String printFunctionDiv) {
        this.printFunctionDiv = printFunctionDiv;
    }

    /**
     * 帳票印字様式マスターの帳票定義単位区分を取得
     * @return 帳票印字様式マスターの帳票定義単位区分
     */
    @Column(length = 2)
    public Integer getPrintDefineScopeDiv() {
        return this.printDefineScopeDiv;
    }

    /**
     * 帳票印字様式マスターの帳票定義単位区分を設定
     * @param printDefineScopeDiv 帳票印字様式マスターの帳票定義単位区分
     */
    public void setPrintDefineScopeDiv(Integer printDefineScopeDiv) {
        this.printDefineScopeDiv = printDefineScopeDiv;
    }

    /**
     * 帳票印字様式マスターの指図工程コードを取得
     * @return 帳票印字様式マスターの指図工程コード
     */
    @Column(length = 20)
    public String getProcessCode() {
        return this.processCode;
    }

    /**
     * 帳票印字様式マスターの指図工程コードを設定
     * @param processCode 帳票印字様式マスターの指図工程コード
     */
    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    /**
     * 帳票印字様式マスターの試験目的区分を取得
     * @return 帳票印字様式マスターの試験目的区分
     */
    @Column(length = 1)
    public Integer getInspectionPurposeDiv() {
        return this.inspectionPurposeDiv;
    }

    /**
     * 帳票印字様式マスターの試験目的区分を設定
     * @param inspectionPurposeDiv 帳票印字様式マスターの試験目的区分
     */
    public void setInspectionPurposeDiv(Integer inspectionPurposeDiv) {
        this.inspectionPurposeDiv = inspectionPurposeDiv;
    }

    /**
     * 帳票印字様式マスターの品目タイプコードを取得
     * @return 帳票印字様式マスターの品目タイプコード
     */
    @Column(length = 20)
    public String getItemTypeCode() {
        return this.itemTypeCode;
    }

    /**
     * 帳票印字様式マスターの品目タイプコードを設定
     * @param itemTypeCode 帳票印字様式マスターの品目タイプコード
     */
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    /**
     * 帳票印字様式マスターの品目コードを取得
     * @return 帳票印字様式マスターの品目コード
     */
    @Column(length = 20)
    public String getItemCode() {
        return this.itemCode;
    }

    /**
     * 帳票印字様式マスターの品目コードを設定
     * @param itemCode 帳票印字様式マスターの品目コード
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 帳票印字様式マスターのレシピIDを取得
     * @return 帳票印字様式マスターのレシピID
     */
    @Column(length = 20)
    public String getRecipeID() {
        return this.recipeID;
    }

    /**
     * 帳票印字様式マスターのレシピIDを設定
     * @param recipeID 帳票印字様式マスターのレシピID
     */
    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    /**
     * 帳票印字様式マスターのレシピVERを取得
     * @return 帳票印字様式マスターのレシピVER
     */
    @Column(length = 20)
    public String getRecipeVersion() {
        return this.recipeVersion;
    }

    /**
     * 帳票印字様式マスターのレシピVERを設定
     * @param recipeVersion 帳票印字様式マスターのレシピVER
     */
    public void setRecipeVersion(String recipeVersion) {
        this.recipeVersion = recipeVersion;
    }

    /**
     * 帳票印字様式マスターのレシピREVを取得
     * @return 帳票印字様式マスターのレシピREV
     */
    @Column(length = 3)
    public Integer getRecipeRevision() {
        return this.recipeRevision;
    }

    /**
     * 帳票印字様式マスターのレシピREVを設定
     * @param recipeRevision 帳票印字様式マスターのレシピREV
     */
    public void setRecipeRevision(Integer recipeRevision) {
        this.recipeRevision = recipeRevision;
    }

}
