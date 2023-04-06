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
 * Zi126印刷プレビューキュー
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "tmp_queue_print_preview")
@NamedQueries({@NamedQuery(name = "TmpQueuePrintPreview.findAll", query = "SELECT p FROM TmpQueuePrintPreview p"),
        @NamedQuery(name = "TmpQueuePrintPreview.findByPK", query = "SELECT p FROM TmpQueuePrintPreview p WHERE p.queue_print_preview_key = :queue_print_preview_key")})
public class TmpQueuePrintPreview extends BaseEntity implements Serializable
{

    /** テーブル名 */
    public static final String TABLE_NAME                          = "tmp_queue_print_preview";

    /** nk印刷プレビューキューキー */
    public static final String COLUMN_NAME_QUEUE_PRINT_PREVIEW_KEY = "queue_print_preview_key";

    /** nk要求イベントID */
    public static final String COLUMN_NAME_EVENT_ID                = "event_id";

    /** nk要求内連番 */
    public static final String COLUMN_NAME_REQUEST_SEQ             = "request_seq";

    /** 端末ID */
    public static final String COLUMN_NAME_COMPUTER_ID             = "computer_id";

    /** 端末名 */
    public static final String COLUMN_NAME_COMPUTER_NAME           = "computer_name";

    /** 帳票様式番号 */
    public static final String COLUMN_NAME_PRINT_COMMAND_NO        = "print_command_no";

    /** 帳票印刷プレビュー日時 */
    public static final String COLUMN_NAME_PRINT_PREVIEW_DATE      = "print_preview_date";

    /** 帳票種類 */
    public static final String COLUMN_NAME_PRINTER_TYPE            = "printer_type";

    /** 印字枚数 */
    public static final String COLUMN_NAME_PRINT_TIMES             = "print_times";

    /** 帳票名 */
    public static final String COLUMN_NAME_REPORT_NAME             = "report_name";

    /** DB領域区分 */
    public static final String COLUMN_NAME_DB_AREA_DIV             = "db_area_div";

    /** プリンタID */
    public static final String COLUMN_NAME_PRINTER_ID              = "printer_id";

    /** プリンタ名 */
    public static final String COLUMN_NAME_PRINTER_NAME            = "printer_name";

    /** ユーザID */
    public static final String COLUMN_NAME_USER_ID                 = "user_id";

    /** ユーザ名 */
    public static final String COLUMN_NAME_USER_NAME               = "user_name";

    /** 画面表示用Key情報 */
    public static final String COLUMN_NAME_DISPLAY_KEY_TEXT        = "display_key_text";

    /** 印字理由コード */
    public static final String COLUMN_NAME_PRINT_REASON_CODE       = "print_reason_code";

    /** 印字理由名 */
    public static final String COLUMN_NAME_PRINT_REASON_NAME       = "print_reason_name";

    /** 印字理由コメント */
    public static final String COLUMN_NAME_PRINT_REASON_COMMENT    = "print_reason_comment";

    /** 再印刷マーク出力有無 */
    public static final String COLUMN_NAME_IS_RE_PRINT_MARK        = "is_re_print_mark";

    /** 再印刷マーク出力位置 */
    public static final String COLUMN_NAME_POSITION_RE_PRINT_MARK  = "position_re_print_mark";

    /** 電子ファイル作成区分 */
    public static final String COLUMN_NAME_IS_FILE_CREATE_TYPE     = "is_file_create_type";

    /** PDF電子ファイル名 */
    public static final String COLUMN_NAME_PDF_FILE_NAME           = "pdf_file_name";

    /** 電子ファイル格納パス */
    public static final String COLUMN_NAME_PREVIEW_FOLDER_PATH     = "preview_folder_path";

    /** 帳票印刷プレビュー状態 */
    public static final String COLUMN_NAME_PRINT_PREVIEW_STATUS    = "print_preview_status";

    /** プレビュー画面表示状態 */
    public static final String COLUMN_NAME_PREVIEW_DISPLAY_STATUS  = "preview_display_status";

    /** ReportID */
    public static final String COLUMN_NAME_REPORT_ID               = "report_id";

    /** 帳票印字様式マスターの帳票印字機能区分 */
    public static final String COLUMN_NAME_PRINT_FUNCTION_DIV      = "printFunctionDiv";

    /** 帳票印字様式マスターの帳票定義単位区分 */
    public static final String COLUMN_NAME_PRINT_DEFINE_SCOPE_DIV  = "printDefineScopeDiv";

    /** 帳票印字様式マスターの指図工程コード */
    public static final String COLUMN_NAME_PROCESS_CODE            = "processCode";

    /** 帳票印字様式マスターの試験目的区分 */
    public static final String COLUMN_NAME_INSPECTION_PURPOSE_DIV  = "inspectionPurposeDiv";

    /** 帳票印字様式マスターの品目タイプコード */
    public static final String COLUMN_NAME_ITEM_TYPE_CODE          = "itemTypeCode";

    /** 帳票印字様式マスターの品目コード */
    public static final String COLUMN_NAME_ITEM_CODE               = "itemCode";

    /** 帳票印字様式マスターのレシピID */
    public static final String COLUMN_NAME_RECIPE_ID               = "recipeID";

    /** 帳票印字様式マスターのレシピVER */
    public static final String COLUMN_NAME_RECIPE_VERSION          = "recipeVersion";

    /** 帳票印字様式マスターのレシピREV */
    public static final String COLUMN_NAME_RECIPE_REVISION         = "recipeRevision";

    /** nk印刷プレビューキューキー */
    private String             queue_print_preview_key;
    /** nk要求イベントID */
    private String             event_id;
    /** nk要求内連番 */
    private int                request_seq;
    /** 端末ID */
    private String             computer_id;
    /** 端末名 */
    private String             computer_name;
    /** 帳票様式番号 */
    private String             print_command_no;
    /** 帳票印字日時 */
    private Date               print_preview_date;
    /** 帳票種類 */
    private int                printer_type;
    /** 印字枚数 */
    private int                print_times;
    /** 帳票名 */
    private String             report_name;
    /** DB領域区分 */
    private int                db_area_div;
    /** プリンタID */
    private String             printer_id;
    /** プリンタ名 */
    private String             printer_name;
    /** ユーザID */
    private String             user_id;
    /** ユーザ名 */
    private String             user_name;
    /** 画面表示用Key情報 */
    private String             display_key_text;
    /** 印字理由コード */
    private String             print_reason_code;
    /** 印字理由名 */
    private String             print_reason_name;
    /** 印字理由コメント */
    private String             print_reason_comment;
    /** 再印刷マーク出力有無 */
    private int                is_re_print_mark;
    /** 再印刷マーク出力位置 */
    private String             position_re_print_mark;
    /** 電子ファイル作成区分 */
    private int                is_file_create_type;
    /** PDF電子ファイル名 */
    private String             pdf_file_name;
    /** 電子ファイル格納パス */
    private String             preview_folder_path;
    /** 帳票印刷プレビュー状態 */
    private int                print_preview_status;
    /** プレビュー画面表示状態 */
    private int                preview_display_status;
    /** ReportID */
    private String             report_id;
    /** 帳票印字様式マスターの帳票印字機能区分 */
    private String             printFunctionDiv;
    /** 帳票印字様式マスターの帳票定義単位区分 */
    private Integer            printDefineScopeDiv;
    /** 帳票印字様式マスターの指図工程コード */
    private String             processCode;
    /** 帳票印字様式マスターの試験目的区分 */
    private Integer            inspectionPurposeDiv;
    /** 帳票印字様式マスターの品目タイプコード */
    private String             itemTypeCode;
    /** 帳票印字様式マスターの品目コード */
    private String             itemCode;
    /** 帳票印字様式マスターのレシピID */
    private String             recipeID;
    /** 帳票印字様式マスターのレシピVER */
    private String             recipeVersion;
    /** 帳票印字様式マスターのレシピREV */
    private Integer            recipeRevision;

    /**
     * Zi126印刷プレビューキューエンティティ コンストラクタ
     */
    public TmpQueuePrintPreview()
    {
    }

    /**
     * Zi126印刷プレビューキュー コンストラクタ
     * @param queue_print_preview_key nk印刷プレビューキューキー
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param computer_id 端末ID
     * @param computer_name 端末名
     * @param print_command_no 帳票様式番号
     * @param print_preview_date 帳票印字日時
     * @param printer_type 帳票種類
     * @param print_times 印字枚数
     * @param report_name 帳票名
     * @param db_area_div DB領域区分
     * @param printer_id プリンタID
     * @param printer_name プリンタ名
     * @param user_id ユーザID
     * @param user_name ユーザ名
     * @param is_re_print_mark 再印刷マーク出力有無
     * @param is_file_create_type 電子ファイル作成区分
     * @param print_preview_status 帳票印刷プレビュー状態
     * @param preview_display_status プレビュー画面表示状態
     * @param report_id ReportID
     * @param version 更新バージョン
     * @param preview_folder_path 電子ファイル格納パス
     
     */
    public TmpQueuePrintPreview(String queue_print_preview_key, String event_id, int request_seq, String computer_id,
            String computer_name, String print_command_no, Date print_preview_date, int printer_type, int print_times,
            String report_name, int db_area_div, String printer_id, String printer_name, String user_id,
            String preview_folder_path, String user_name, int is_re_print_mark, int is_file_create_type,
            int print_preview_status, int preview_display_status, String report_id, int version)
    {
        this.queue_print_preview_key = queue_print_preview_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.computer_id = computer_id;
        this.computer_name = computer_name;
        this.print_command_no = print_command_no;
        this.print_preview_date = print_preview_date;
        this.printer_type = printer_type;
        this.print_times = print_times;
        this.report_name = report_name;
        this.db_area_div = db_area_div;
        this.printer_id = printer_id;
        this.printer_name = printer_name;
        this.user_id = user_id;
        this.user_name = user_name;
        this.is_re_print_mark = is_re_print_mark;
        this.is_file_create_type = is_file_create_type;
        this.print_preview_status = print_preview_status;
        this.preview_display_status = preview_display_status;
        this.preview_folder_path = preview_folder_path;
        this.report_id = report_id;
        super.setVersion(version);
    }

    /**
     * Zi126印刷プレビューキューエンティティ コンストラクタ
     * @param queue_print_preview_key nk印刷プレビューキューキー
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param computer_id 端末ID
     * @param computer_name 端末名
     * @param print_command_no 帳票様式番号
     * @param print_preview_date 帳票印刷プレビュー日時
     * @param printer_type 帳票種類
     * @param print_times 印字枚数
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
     * @param preview_folder_path 電子ファイル格納パス
     * @param print_preview_status 帳票印刷プレビュー状態
     * @param preview_display_status プレビュー画面表示状態
     * @param report_id ReportID
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
    public TmpQueuePrintPreview(String queue_print_preview_key, String event_id, int request_seq, String computer_id,
            String computer_name, String print_command_no, Date print_preview_date, int printer_type, int print_times,
            String report_name, int db_area_div, String printer_id, String printer_name, String user_id,
            String user_name, String display_key_text, String print_reason_code, String print_reason_name,
            String print_reason_comment, int is_re_print_mark, String position_re_print_mark, int is_file_create_type,
            String pdf_file_name, String preview_folder_path, int print_preview_status, int preview_display_status,
            String report_id, String printFunctionDiv, Integer printDefineScopeDiv, String processCode,
            Integer inspectionPurposeDiv, String itemTypeCode, String itemCode, String recipeID, String recipeVersion,
            Integer recipeRevision, String first_regist_event_id, String first_regist_user_number,
            String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id,
            String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version)
    {
        this.queue_print_preview_key = queue_print_preview_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.computer_id = computer_id;
        this.computer_name = computer_name;
        this.print_command_no = print_command_no;
        this.print_preview_date = print_preview_date;
        this.printer_type = printer_type;
        this.print_times = print_times;
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
        this.preview_folder_path = preview_folder_path;
        this.print_preview_status = print_preview_status;
        this.preview_display_status = preview_display_status;
        this.report_id = report_id;
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
     * nk印刷プレビューキューキーを取得
     * @return nk印刷プレビューキューキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getQueue_print_preview_key()
    {
        return this.queue_print_preview_key;
    }

    /**
     * nk印刷プレビューキューキーを設定
     * @param queue_print_preview_key nk印刷プレビューキューキー (null不可)
     */
    public void setQueue_print_preview_key(String queue_print_preview_key)
    {
        this.queue_print_preview_key = queue_print_preview_key;
    }

    /**
     * nk要求イベントIDを取得
     * @return nk要求イベントID
     */
    @Column(nullable = false, length = 38)
    public String getEvent_id()
    {
        return this.event_id;
    }

    /**
     * nk要求イベントIDを設定
     * @param event_id nk要求イベントID (null不可)
     */
    public void setEvent_id(String event_id)
    {
        this.event_id = event_id;
    }

    /**
     * nk要求内連番を取得
     * @return nk要求内連番
     */
    @Column(nullable = false, length = 3)
    public int getRequest_seq()
    {
        return this.request_seq;
    }

    /**
     * nk要求内連番を設定
     * @param request_seq nk要求内連番 (null不可)
     */
    public void setRequest_seq(int request_seq)
    {
        this.request_seq = request_seq;
    }

    /**
     * 端末IDを取得
     * @return 端末ID
     */
    @Column(nullable = false, length = 20)
    public String getComputer_id()
    {
        return this.computer_id;
    }

    /**
     * 端末IDを設定
     * @param computer_id 端末ID (null不可)
     */
    public void setComputer_id(String computer_id)
    {
        this.computer_id = computer_id;
    }

    /**
     * 端末名を取得
     * @return 端末名
     */
    @Column(nullable = false, length = 30)
    public String getComputer_name()
    {
        return this.computer_name;
    }

    /**
     * 端末名を設定
     * @param computer_name 端末名 (null不可)
     */
    public void setComputer_name(String computer_name)
    {
        this.computer_name = computer_name;
    }

    /**
     * 帳票様式番号を取得
     * @return 帳票様式番号
     */
    @Column(nullable = false, length = 20)
    public String getPrint_command_no()
    {
        return this.print_command_no;
    }

    /**
     * 帳票様式番号を設定
     * @param print_command_no 帳票様式番号 (null不可)
     */
    public void setPrint_command_no(String print_command_no)
    {
        this.print_command_no = print_command_no;
    }

    /**
     * 帳票印字日時を取得
     * @return 帳票印字日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getPrint_preview_date()
    {
        return this.print_preview_date;
    }

    /**
     * 帳票印字日時を設定
     * @param print_preview_date 帳票印字日時 (null不可)
     */
    public void setPrint_preview_date(Date print_preview_date)
    {
        this.print_preview_date = print_preview_date;
    }

    /**
     * 帳票種類を取得
     * @return 帳票種類
     */
    @Column(nullable = false, length = 2)
    public int getPrinter_type()
    {
        return this.printer_type;
    }

    /**
     * 帳票種類を設定
     * @param printer_type 帳票種類 (null不可)
     */
    public void setPrinter_type(int printer_type)
    {
        this.printer_type = printer_type;
    }

    /**
     * 印字枚数を取得
     * @return 印字枚数
     */
    @Column(nullable = false, length = 3)
    public int getPrint_times()
    {
        return this.print_times;
    }

    /**
     * 印字枚数を設定
     * @param print_times 印字枚数 (null不可)
     */
    public void setPrint_times(int print_times)
    {
        this.print_times = print_times;
    }

    /**
     * 帳票名を取得
     * @return 帳票名
     */
    @Column(nullable = false, length = 64)
    public String getReport_name()
    {
        return this.report_name;
    }

    /**
     * 帳票名を設定
     * @param report_name 帳票名 (null不可)
     */
    public void setReport_name(String report_name)
    {
        this.report_name = report_name;
    }

    /**
     * DB領域区分を取得
     * @return DB領域区分
     */
    @Column(nullable = false, length = 1)
    public int getDb_area_div()
    {
        return this.db_area_div;
    }

    /**
     * DB領域区分を設定
     * @param db_area_div DB領域区分 (null不可)
     */
    public void setDb_area_div(int db_area_div)
    {
        this.db_area_div = db_area_div;
    }

    /**
     * プリンタIDを取得
     * @return プリンタID
     */
    @Column(nullable = false, length = 20)
    public String getPrinter_id()
    {
        return this.printer_id;
    }

    /**
     * プリンタIDを設定
     * @param printer_id プリンタID (null不可)
     */
    public void setPrinter_id(String printer_id)
    {
        this.printer_id = printer_id;
    }

    /**
     * プリンタ名を取得
     * @return プリンタ名
     */
    @Column(nullable = false, length = 128)
    public String getPrinter_name()
    {
        return this.printer_name;
    }

    /**
     * プリンタ名を設定
     * @param printer_name プリンタ名 (null不可)
     */
    public void setPrinter_name(String printer_name)
    {
        this.printer_name = printer_name;
    }

    /**
     * ユーザIDを取得
     * @return ユーザID
     */
    @Column(nullable = false, length = 20)
    public String getUser_id()
    {
        return this.user_id;
    }

    /**
     * ユーザIDを設定
     * @param user_id ユーザID (null不可)
     */
    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    /**
     * ユーザ名を取得
     * @return ユーザ名
     */
    @Column(nullable = false, length = 40)
    public String getUser_name()
    {
        return this.user_name;
    }

    /**
     * ユーザ名を設定
     * @param user_name ユーザ名 (null不可)
     */
    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    /**
     * 画面表示用Key情報を取得
     * @return 画面表示用Key情報
     */
    @Column(length = 500)
    public String getDisplay_key_text()
    {
        return this.display_key_text;
    }

    /**
     * 画面表示用Key情報を設定
     * @param display_key_text 画面表示用Key情報
     */
    public void setDisplay_key_text(String display_key_text)
    {
        this.display_key_text = display_key_text;
    }

    /**
     * 印字理由コードを取得
     * @return 印字理由コード
     */
    @Column(length = 20)
    public String getPrint_reason_code()
    {
        return this.print_reason_code;
    }

    /**
     * 印字理由コードを設定
     * @param print_reason_code 印字理由コード
     */
    public void setPrint_reason_code(String print_reason_code)
    {
        this.print_reason_code = print_reason_code;
    }

    /**
     * 印字理由名を取得
     * @return 印字理由名
     */
    @Column(length = 50)
    public String getPrint_reason_name()
    {
        return this.print_reason_name;
    }

    /**
     * 印字理由名を設定
     * @param print_reason_name 印字理由名
     */
    public void setPrint_reason_name(String print_reason_name)
    {
        this.print_reason_name = print_reason_name;
    }

    /**
     * 印字理由コメントを取得
     * @return 印字理由コメント
     */
    @Column(length = 100)
    public String getPrint_reason_comment()
    {
        return this.print_reason_comment;
    }

    /**
     * 印字理由コメントを設定
     * @param print_reason_comment 印字理由コメント
     */
    public void setPrint_reason_comment(String print_reason_comment)
    {
        this.print_reason_comment = print_reason_comment;
    }

    /**
     * 再印刷マーク出力有無を取得
     * @return 再印刷マーク出力有無
     */
    @Column(nullable = false, length = 1)
    public int getIs_re_print_mark()
    {
        return this.is_re_print_mark;
    }

    /**
     * 再印刷マーク出力有無を設定
     * @param is_re_print_mark 再印刷マーク出力有無 (null不可)
     */
    public void setIs_re_print_mark(int is_re_print_mark)
    {
        this.is_re_print_mark = is_re_print_mark;
    }

    /**
     * 再印刷マーク出力位置を取得
     * @return 再印刷マーク出力位置
     */
    @Column(length = 20)
    public String getPosition_re_print_mark()
    {
        return this.position_re_print_mark;
    }

    /**
     * 再印刷マーク出力位置を設定
     * @param position_re_print_mark 再印刷マーク出力位置
     */
    public void setPosition_re_print_mark(String position_re_print_mark)
    {
        this.position_re_print_mark = position_re_print_mark;
    }

    /**
     * 電子ファイル作成区分を取得
     * @return 電子ファイル作成区分
     */
    @Column(nullable = false, length = 1)
    public int getIs_file_create_type()
    {
        return this.is_file_create_type;
    }

    /**
     * 電子ファイル作成区分を設定
     * @param is_file_create_type 電子ファイル作成区分 (null不可)
     */
    public void setIs_file_create_type(int is_file_create_type)
    {
        this.is_file_create_type = is_file_create_type;
    }

    /**
     * PDF電子ファイル名を取得
     * @return PDF電子ファイル名
     */
    @Column(length = 80)
    public String getPdf_file_name()
    {
        return this.pdf_file_name;
    }

    /**
     * PDF電子ファイル名を設定
     * @param pdf_file_name PDF電子ファイル名
     */
    public void setPdf_file_name(String pdf_file_name)
    {
        this.pdf_file_name = pdf_file_name;
    }

    /**
     * 電子ファイル格納パスを取得
     * @return 電子ファイル格納パス
     */
    public String getPreview_folder_path()
    {
        return preview_folder_path;
    }

    /**
     * 電子ファイル格納パスを設定
     * @param preview_folder_path 電子ファイル格納パス
     */
    public void setPreview_folder_path(String preview_folder_path)
    {
        this.preview_folder_path = preview_folder_path;
    }

    /**
     * 帳票印刷プレビュー状態を取得
     * @return 帳票印刷プレビュー状態
     */
    @Column(nullable = false, length = 1)
    public int getPrint_preview_status()
    {
        return this.print_preview_status;
    }

    /**
     * 帳票印刷プレビュー状態を設定
     * @param preview_display_status 帳票印刷プレビュー状態 (null不可)
     */
    public void setPrint_preview_status(int print_preview_status)
    {
        this.print_preview_status = print_preview_status;
    }

    /**
     * プレビュー画面表示状態を取得
     * @return プレビュー画面表示状態
     */
    public int getPreview_display_status()
    {
        return preview_display_status;
    }

    /**
     * プレビュー画面表示状態を設定
     * @param print_preview_status プレビュー画面表示状態 (null不可)
     */
    public void setPreview_display_status(int preview_display_status)
    {
        this.preview_display_status = preview_display_status;
    }

    /**
     * ReportIDを取得
     * @return ReportID
     */
    @Column(nullable = false, length = 100)
    public String getReport_id()
    {
        return this.report_id;
    }

    /**
     * ReportIDを設定
     * @param report_id ReportID (null不可)
     */
    public void setReport_id(String report_id)
    {
        this.report_id = report_id;
    }

    /**
     * 帳票印字様式マスターの帳票印字機能区分を取得
     * @return 帳票印字様式マスターの帳票印字機能区分
     */
    @Column(length = 4)
    public String getPrintFunctionDiv()
    {
        return this.printFunctionDiv;
    }

    /**
     * 帳票印字様式マスターの帳票印字機能区分を設定
     * @param printFunctionDiv 帳票印字様式マスターの帳票印字機能区分
     */
    public void setPrintFunctionDiv(String printFunctionDiv)
    {
        this.printFunctionDiv = printFunctionDiv;
    }

    /**
     * 帳票印字様式マスターの帳票定義単位区分を取得
     * @return 帳票印字様式マスターの帳票定義単位区分
     */
    @Column(length = 2)
    public Integer getPrintDefineScopeDiv()
    {
        return this.printDefineScopeDiv;
    }

    /**
     * 帳票印字様式マスターの帳票定義単位区分を設定
     * @param printDefineScopeDiv 帳票印字様式マスターの帳票定義単位区分
     */
    public void setPrintDefineScopeDiv(Integer printDefineScopeDiv)
    {
        this.printDefineScopeDiv = printDefineScopeDiv;
    }

    /**
     * 帳票印字様式マスターの指図工程コードを取得
     * @return 帳票印字様式マスターの指図工程コード
     */
    @Column(length = 20)
    public String getProcessCode()
    {
        return this.processCode;
    }

    /**
     * 帳票印字様式マスターの指図工程コードを設定
     * @param processCode 帳票印字様式マスターの指図工程コード
     */
    public void setProcessCode(String processCode)
    {
        this.processCode = processCode;
    }

    /**
     * 帳票印字様式マスターの試験目的区分を取得
     * @return 帳票印字様式マスターの試験目的区分
     */
    @Column(length = 1)
    public Integer getInspectionPurposeDiv()
    {
        return this.inspectionPurposeDiv;
    }

    /**
     * 帳票印字様式マスターの試験目的区分を設定
     * @param inspectionPurposeDiv 帳票印字様式マスターの試験目的区分
     */
    public void setInspectionPurposeDiv(Integer inspectionPurposeDiv)
    {
        this.inspectionPurposeDiv = inspectionPurposeDiv;
    }

    /**
     * 帳票印字様式マスターの品目タイプコードを取得
     * @return 帳票印字様式マスターの品目タイプコード
     */
    @Column(length = 20)
    public String getItemTypeCode()
    {
        return this.itemTypeCode;
    }

    /**
     * 帳票印字様式マスターの品目タイプコードを設定
     * @param itemTypeCode 帳票印字様式マスターの品目タイプコード
     */
    public void setItemTypeCode(String itemTypeCode)
    {
        this.itemTypeCode = itemTypeCode;
    }

    /**
     * 帳票印字様式マスターの品目コードを取得
     * @return 帳票印字様式マスターの品目コード
     */
    @Column(length = 20)
    public String getItemCode()
    {
        return this.itemCode;
    }

    /**
     * 帳票印字様式マスターの品目コードを設定
     * @param itemCode 帳票印字様式マスターの品目コード
     */
    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    /**
     * 帳票印字様式マスターのレシピIDを取得
     * @return 帳票印字様式マスターのレシピID
     */
    @Column(length = 20)
    public String getRecipeID()
    {
        return this.recipeID;
    }

    /**
     * 帳票印字様式マスターのレシピIDを設定
     * @param recipeID 帳票印字様式マスターのレシピID
     */
    public void setRecipeID(String recipeID)
    {
        this.recipeID = recipeID;
    }

    /**
     * 帳票印字様式マスターのレシピVERを取得
     * @return 帳票印字様式マスターのレシピVER
     */
    @Column(length = 20)
    public String getRecipeVersion()
    {
        return this.recipeVersion;
    }

    /**
     * 帳票印字様式マスターのレシピVERを設定
     * @param recipeVersion 帳票印字様式マスターのレシピVER
     */
    public void setRecipeVersion(String recipeVersion)
    {
        this.recipeVersion = recipeVersion;
    }

    /**
     * 帳票印字様式マスターのレシピREVを取得
     * @return 帳票印字様式マスターのレシピREV
     */
    @Column(length = 3)
    public Integer getRecipeRevision()
    {
        return this.recipeRevision;
    }

    /**
     * 帳票印字様式マスターのレシピREVを設定
     * @param recipeRevision 帳票印字様式マスターのレシピREV
     */
    public void setRecipeRevision(Integer recipeRevision)
    {
        this.recipeRevision = recipeRevision;
    }
}
