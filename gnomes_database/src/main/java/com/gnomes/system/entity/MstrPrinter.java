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
 * Zm007プリンタマスタ エンティティ
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
@Table(name = "mstr_printer")
@NamedQueries({
        @NamedQuery(name = "MstrPrinter.findAll", query = "SELECT p FROM MstrPrinter p"),
        @NamedQuery(name = "MstrPrinter.findByPK", query = "SELECT p FROM MstrPrinter p WHERE p.printer_key = :printer_key")
})
public class MstrPrinter extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_printer";

    /** プリンタキー */
    public static final String COLUMN_NAME_PRINTER_KEY = "printer_key";

    /** nkプリンタID */
    public static final String COLUMN_NAME_PRINTER_ID = "printer_id";

    /** プリンタ名 */
    public static final String COLUMN_NAME_PRINTER_NAME = "printer_name";

    /** 説明１（機種） */
    public static final String COLUMN_NAME_EXPLANATION1 = "explanation1";

    /** 説明２（設置場所） */
    public static final String COLUMN_NAME_EXPLANATION2 = "explanation2";

    /** プリンタの名前またはアドレス */
    public static final String COLUMN_NAME_PRINTER_PATH = "printer_path";

    /** 印字時プリンター出力フラグ */
    public static final String COLUMN_NAME_PRINT_PRINTER_OUTPUT_FLAG = "print_printer_output_flag";

    /** 再印字時プリンター出力フラグ */
    public static final String COLUMN_NAME_REPRINT_PRINTER_OUTPUT_FLAG = "reprint_printer_output_flag";

    /** プリンタキー */
    private String printer_key;
    /** nkプリンタID */
    private String printer_id;
    /** プリンタ名 */
    private String printer_name;
    /** 説明１（機種） */
    private String explanation1;
    /** 説明２（設置場所） */
    private String explanation2;
    /** プリンタの名前またはアドレス */
    private String printer_path;
    /** 印字時プリンター出力フラグ */
    private int print_printer_output_flag;
    /** 再印字時プリンター出力フラグ */
    private int reprint_printer_output_flag;


    /**
     * Zm007プリンタマスタエンティティ コンストラクタ
     */
    public MstrPrinter() {
    }

    /**
     * Zm007プリンタマスタエンティティ コンストラクタ
     * @param printer_key プリンタキー
     * @param printer_id nkプリンタID
     * @param printer_name プリンタ名
     * @param printer_path プリンタの名前またはアドレス
     * @param print_printer_output_flag 印字時プリンター出力フラグ
     * @param reprint_printer_output_flag 再印字時プリンター出力フラグ
     * @param version 更新バージョン
     */
    public MstrPrinter(String printer_key, String printer_id, String printer_name, String printer_path, int print_printer_output_flag, int reprint_printer_output_flag, int version) {
        this.printer_key = printer_key;
        this.printer_id = printer_id;
        this.printer_name = printer_name;
        this.printer_path = printer_path;
        this.print_printer_output_flag = print_printer_output_flag;
        this.reprint_printer_output_flag = reprint_printer_output_flag;
        super.setVersion(version);
    }

    /**
     * Zm007プリンタマスタエンティティ コンストラクタ
     * @param printer_key プリンタキー
     * @param printer_id nkプリンタID
     * @param printer_name プリンタ名
     * @param explanation1 説明１（機種）
     * @param explanation2 説明２（設置場所）
     * @param printer_path プリンタの名前またはアドレス
     * @param print_printer_output_flag 印字時プリンター出力フラグ
     * @param reprint_printer_output_flag 再印字時プリンター出力フラグ
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
    public MstrPrinter(String printer_key, String printer_id, String printer_name, String explanation1, String explanation2, String printer_path, int print_printer_output_flag, int reprint_printer_output_flag, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.printer_key = printer_key;
        this.printer_id = printer_id;
        this.printer_name = printer_name;
        this.explanation1 = explanation1;
        this.explanation2 = explanation2;
        this.printer_path = printer_path;
        this.print_printer_output_flag = print_printer_output_flag;
        this.reprint_printer_output_flag = reprint_printer_output_flag;
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
     * プリンタキーを取得
     * @return プリンタキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPrinter_key() {
        return this.printer_key;
    }

    /**
     * プリンタキーを設定
     * @param printer_key プリンタキー (null不可)
     */
    public void setPrinter_key(String printer_key) {
        this.printer_key = printer_key;
    }

    /**
     * nkプリンタIDを取得
     * @return nkプリンタID
     */
    @Column(nullable = false, length = 20)
    public String getPrinter_id() {
        return this.printer_id;
    }

    /**
     * nkプリンタIDを設定
     * @param printer_id nkプリンタID (null不可)
     */
    public void setPrinter_id(String printer_id) {
        this.printer_id = printer_id;
    }

    /**
     * プリンタ名を取得
     * @return プリンタ名
     */
    @Column(nullable = false, length = 128)
    public String getPrinter_name() {
        return this.printer_name;
    }

    /**
     * プリンタ名を設定
     * @param printer_name プリンタ名 (null不可)
     */
    public void setPrinter_name(String printer_name) {
        this.printer_name = printer_name;
    }

    /**
     * 説明１（機種）を取得
     * @return 説明１（機種）
     */
    @Column(length = 40)
    public String getExplanation1() {
        return this.explanation1;
    }

    /**
     * 説明１（機種）を設定
     * @param explanation1 説明１（機種）
     */
    public void setExplanation1(String explanation1) {
        this.explanation1 = explanation1;
    }

    /**
     * 説明２（設置場所）を取得
     * @return 説明２（設置場所）
     */
    @Column(length = 40)
    public String getExplanation2() {
        return this.explanation2;
    }

    /**
     * 説明２（設置場所）を設定
     * @param explanation2 説明２（設置場所）
     */
    public void setExplanation2(String explanation2) {
        this.explanation2 = explanation2;
    }

    /**
     * プリンタの名前またはアドレスを取得
     * @return プリンタの名前またはアドレス
     */
    @Column(nullable = false, length = 1024)
    public String getPrinter_path() {
        return this.printer_path;
    }

    /**
     * プリンタの名前またはアドレスを設定
     * @param printer_path プリンタの名前またはアドレス (null不可)
     */
    public void setPrinter_path(String printer_path) {
        this.printer_path = printer_path;
    }

    /**
     * 印字時プリンター出力フラグを取得
     * @return 印字時プリンター出力フラグ
     */
    @Column(nullable = false, length = 1)
    public int getPrint_printer_output_flag() {
        return this.print_printer_output_flag;
    }

    /**
     * 印字時プリンター出力フラグを設定
     * @param print_printer_output_flag 印字時プリンター出力フラグ (null不可)
     */
    public void setPrint_printer_output_flag(int print_printer_output_flag) {
        this.print_printer_output_flag = print_printer_output_flag;
    }

    /**
     * 再印字時プリンター出力フラグを取得
     * @return 再印字時プリンター出力フラグ
     */
    @Column(nullable = false, length = 1)
    public int getReprint_printer_output_flag() {
        return this.reprint_printer_output_flag;
    }

    /**
     * 再印字時プリンター出力フラグを設定
     * @param reprint_printer_output_flag 再印字時プリンター出力フラグ (null不可)
     */
    public void setReprint_printer_output_flag(int reprint_printer_output_flag) {
        this.reprint_printer_output_flag = reprint_printer_output_flag;
    }

}
