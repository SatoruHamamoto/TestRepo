package com.gnomes.system.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * Zm027ユーザ委託権限マスタ エンティティ
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
@Table(name = "mstr_person_privilege")
@NamedQueries({
        @NamedQuery(name = "MstrPersonPrivilege.findAll", query = "SELECT p FROM MstrPersonPrivilege p"),
        @NamedQuery(name = "MstrPersonPrivilege.findByPK", query = "SELECT p FROM MstrPersonPrivilege p WHERE p.person_privilege_key = :person_privilege_key")
})
public class MstrPersonPrivilege extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_person_privilege";

    /** ユーザ権限キー */
    public static final String COLUMN_NAME_PERSON_PRIVILEGE_KEY = "person_privilege_key";

    /** 権限キー (FK) */
    public static final String COLUMN_NAME_PRIVILEGE_KEY = "privilege_key";

    /** nkユーザID */
    public static final String COLUMN_NAME_USER_ID = "user_id";

    /** nk拠点コード */
    public static final String COLUMN_NAME_SITE_CODE = "site_code";

    /** nk指図工程コード */
    public static final String COLUMN_NAME_ORDER_PROCESS_CODE = "order_process_code";

    /** nk作業工程コード */
    public static final String COLUMN_NAME_WORK_PROCESS_CODE = "work_process_code";

    /** nk作業場所コード */
    public static final String COLUMN_NAME_WORK_CELL_CODE = "work_cell_code";

    /** nk権限ID */
    public static final String COLUMN_NAME_PRIVILEGE_ID = "privilege_id";

    /** 予備１(文字) */
    public static final String COLUMN_NAME_SPARE_CHAR1 = "spare_char1";

    /** 予備２(文字) */
    public static final String COLUMN_NAME_SPARE_CHAR2 = "spare_char2";

    /** 予備３(文字) */
    public static final String COLUMN_NAME_SPARE_CHAR3 = "spare_char3";

    /** 予備４(文字) */
    public static final String COLUMN_NAME_SPARE_CHAR4 = "spare_char4";

    /** 予備１(数値) */
    public static final String COLUMN_NAME_SPARE_NUMERIC1 = "spare_numeric1";

    /** 予備２(数値) */
    public static final String COLUMN_NAME_SPARE_NUMERIC2 = "spare_numeric2";

    /** 予備３(数値) */
    public static final String COLUMN_NAME_SPARE_NUMERIC3 = "spare_numeric3";

    /** 予備４(数値) */
    public static final String COLUMN_NAME_SPARE_NUMERIC4 = "spare_numeric4";

    /** 予備５(数値) */
    public static final String COLUMN_NAME_SPARE_NUMERIC5 = "spare_numeric5";

    /** 予備６(数値) */
    public static final String COLUMN_NAME_SPARE_NUMERIC6 = "spare_numeric6";

    /** ユーザ権限キー */
    private String person_privilege_key;
    /** 権限キー (FK) */
    private String privilege_key;
    /** nkユーザID */
    private String user_id;
    /** nk拠点コード */
    private String site_code;
    /** nk指図工程コード */
    private String order_process_code;
    /** nk作業工程コード */
    private String work_process_code;
    /** nk作業場所コード */
    private String work_cell_code;
    /** nk権限ID */
    private String privilege_id;
    /** 予備１(文字) */
    private String spare_char1;
    /** 予備２(文字) */
    private String spare_char2;
    /** 予備３(文字) */
    private String spare_char3;
    /** 予備４(文字) */
    private String spare_char4;
    /** 予備１(数値) */
    private BigDecimal spare_numeric1;
    /** 予備２(数値) */
    private BigDecimal spare_numeric2;
    /** 予備３(数値) */
    private BigDecimal spare_numeric3;
    /** 予備４(数値) */
    private BigDecimal spare_numeric4;
    /** 予備５(数値) */
    private BigDecimal spare_numeric5;
    /** 予備６(数値) */
    private BigDecimal spare_numeric6;


    /**
     * Zm027ユーザ委託権限マスタエンティティ コンストラクタ
     */
    public MstrPersonPrivilege() {
    }

    /**
     * Zm027ユーザ委託権限マスタエンティティ コンストラクタ
     * @param person_privilege_key ユーザ権限キー
     * @param privilege_key 権限キー (FK)
     * @param user_id nkユーザID
     * @param site_code nk拠点コード
     * @param order_process_code nk指図工程コード
     * @param work_process_code nk作業工程コード
     * @param work_cell_code nk作業場所コード
     * @param privilege_id nk権限ID
     * @param version 更新バージョン
     */
    public MstrPersonPrivilege(String person_privilege_key, String privilege_key, String user_id, String site_code, String order_process_code, String work_process_code, String work_cell_code, String privilege_id, int version) {
        this.person_privilege_key = person_privilege_key;
        this.privilege_key = privilege_key;
        this.user_id = user_id;
        this.site_code = site_code;
        this.order_process_code = order_process_code;
        this.work_process_code = work_process_code;
        this.work_cell_code = work_cell_code;
        this.privilege_id = privilege_id;
        super.setVersion(version);
    }

    /**
     * Zm027ユーザ委託権限マスタエンティティ コンストラクタ
     * @param person_privilege_key ユーザ権限キー
     * @param privilege_key 権限キー (FK)
     * @param user_id nkユーザID
     * @param site_code nk拠点コード
     * @param order_process_code nk指図工程コード
     * @param work_process_code nk作業工程コード
     * @param work_cell_code nk作業場所コード
     * @param privilege_id nk権限ID
     * @param spare_char1 予備１(文字)
     * @param spare_char2 予備２(文字)
     * @param spare_char3 予備３(文字)
     * @param spare_char4 予備４(文字)
     * @param spare_numeric1 予備１(数値)
     * @param spare_numeric2 予備２(数値)
     * @param spare_numeric3 予備３(数値)
     * @param spare_numeric4 予備４(数値)
     * @param spare_numeric5 予備５(数値)
     * @param spare_numeric6 予備６(数値)
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
    public MstrPersonPrivilege(String person_privilege_key, String privilege_key, String user_id, String site_code, String order_process_code, String work_process_code, String work_cell_code, String privilege_id, String spare_char1, String spare_char2, String spare_char3, String spare_char4, BigDecimal spare_numeric1, BigDecimal spare_numeric2, BigDecimal spare_numeric3, BigDecimal spare_numeric4, BigDecimal spare_numeric5, BigDecimal spare_numeric6, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.person_privilege_key = person_privilege_key;
        this.privilege_key = privilege_key;
        this.user_id = user_id;
        this.site_code = site_code;
        this.order_process_code = order_process_code;
        this.work_process_code = work_process_code;
        this.work_cell_code = work_cell_code;
        this.privilege_id = privilege_id;
        this.spare_char1 = spare_char1;
        this.spare_char2 = spare_char2;
        this.spare_char3 = spare_char3;
        this.spare_char4 = spare_char4;
        this.spare_numeric1 = spare_numeric1;
        this.spare_numeric2 = spare_numeric2;
        this.spare_numeric3 = spare_numeric3;
        this.spare_numeric4 = spare_numeric4;
        this.spare_numeric5 = spare_numeric5;
        this.spare_numeric6 = spare_numeric6;
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
     * ユーザ権限キーを取得
     * @return ユーザ権限キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPerson_privilege_key() {
        return this.person_privilege_key;
    }

    /**
     * ユーザ権限キーを設定
     * @param person_privilege_key ユーザ権限キー (null不可)
     */
    public void setPerson_privilege_key(String person_privilege_key) {
        this.person_privilege_key = person_privilege_key;
    }

    /**
     * 権限キー (FK)を取得
     * @return 権限キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getPrivilege_key() {
        return this.privilege_key;
    }

    /**
     * 権限キー (FK)を設定
     * @param privilege_key 権限キー (FK) (null不可)
     */
    public void setPrivilege_key(String privilege_key) {
        this.privilege_key = privilege_key;
    }

    /**
     * nkユーザIDを取得
     * @return nkユーザID
     */
    @Column(nullable = false, length = 20)
    public String getUser_id() {
        return this.user_id;
    }

    /**
     * nkユーザIDを設定
     * @param user_id nkユーザID (null不可)
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * nk拠点コードを取得
     * @return nk拠点コード
     */
    @Column(nullable = false, length = 20)
    public String getSite_code() {
        return this.site_code;
    }

    /**
     * nk拠点コードを設定
     * @param site_code nk拠点コード (null不可)
     */
    public void setSite_code(String site_code) {
        this.site_code = site_code;
    }

    /**
     * nk指図工程コードを取得
     * @return nk指図工程コード
     */
    @Column(nullable = false, length = 20)
    public String getOrder_process_code() {
        return this.order_process_code;
    }

    /**
     * nk指図工程コードを設定
     * @param order_process_code nk指図工程コード (null不可)
     */
    public void setOrder_process_code(String order_process_code) {
        this.order_process_code = order_process_code;
    }

    /**
     * nk作業工程コードを取得
     * @return nk作業工程コード
     */
    @Column(nullable = false, length = 20)
    public String getWork_process_code() {
        return this.work_process_code;
    }

    /**
     * nk作業工程コードを設定
     * @param work_process_code nk作業工程コード (null不可)
     */
    public void setWork_process_code(String work_process_code) {
        this.work_process_code = work_process_code;
    }

    /**
     * nk作業場所コードを取得
     * @return nk作業場所コード
     */
    @Column(nullable = false, length = 20)
    public String getWork_cell_code() {
        return this.work_cell_code;
    }

    /**
     * nk作業場所コードを設定
     * @param work_cell_code nk作業場所コード (null不可)
     */
    public void setWork_cell_code(String work_cell_code) {
        this.work_cell_code = work_cell_code;
    }

    /**
     * nk権限IDを取得
     * @return nk権限ID
     */
    @Column(nullable = false, length = 32)
    public String getPrivilege_id() {
        return this.privilege_id;
    }

    /**
     * nk権限IDを設定
     * @param privilege_id nk権限ID (null不可)
     */
    public void setPrivilege_id(String privilege_id) {
        this.privilege_id = privilege_id;
    }

    /**
     * 予備１(文字)を取得
     * @return 予備１(文字)
     */
    @Column(length = 80)
    public String getSpare_char1() {
        return this.spare_char1;
    }

    /**
     * 予備１(文字)を設定
     * @param spare_char1 予備１(文字)
     */
    public void setSpare_char1(String spare_char1) {
        this.spare_char1 = spare_char1;
    }

    /**
     * 予備２(文字)を取得
     * @return 予備２(文字)
     */
    @Column(length = 80)
    public String getSpare_char2() {
        return this.spare_char2;
    }

    /**
     * 予備２(文字)を設定
     * @param spare_char2 予備２(文字)
     */
    public void setSpare_char2(String spare_char2) {
        this.spare_char2 = spare_char2;
    }

    /**
     * 予備３(文字)を取得
     * @return 予備３(文字)
     */
    @Column(length = 80)
    public String getSpare_char3() {
        return this.spare_char3;
    }

    /**
     * 予備３(文字)を設定
     * @param spare_char3 予備３(文字)
     */
    public void setSpare_char3(String spare_char3) {
        this.spare_char3 = spare_char3;
    }

    /**
     * 予備４(文字)を取得
     * @return 予備４(文字)
     */
    @Column(length = 80)
    public String getSpare_char4() {
        return this.spare_char4;
    }

    /**
     * 予備４(文字)を設定
     * @param spare_char4 予備４(文字)
     */
    public void setSpare_char4(String spare_char4) {
        this.spare_char4 = spare_char4;
    }

    /**
     * 予備１(数値)を取得
     * @return 予備１(数値)
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getSpare_numeric1() {
        return this.spare_numeric1;
    }

    /**
     * 予備１(数値)を設定
     * @param spare_numeric1 予備１(数値)
     */
    public void setSpare_numeric1(BigDecimal spare_numeric1) {
        this.spare_numeric1 = spare_numeric1;
    }

    /**
     * 予備２(数値)を取得
     * @return 予備２(数値)
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getSpare_numeric2() {
        return this.spare_numeric2;
    }

    /**
     * 予備２(数値)を設定
     * @param spare_numeric2 予備２(数値)
     */
    public void setSpare_numeric2(BigDecimal spare_numeric2) {
        this.spare_numeric2 = spare_numeric2;
    }

    /**
     * 予備３(数値)を取得
     * @return 予備３(数値)
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getSpare_numeric3() {
        return this.spare_numeric3;
    }

    /**
     * 予備３(数値)を設定
     * @param spare_numeric3 予備３(数値)
     */
    public void setSpare_numeric3(BigDecimal spare_numeric3) {
        this.spare_numeric3 = spare_numeric3;
    }

    /**
     * 予備４(数値)を取得
     * @return 予備４(数値)
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getSpare_numeric4() {
        return this.spare_numeric4;
    }

    /**
     * 予備４(数値)を設定
     * @param spare_numeric4 予備４(数値)
     */
    public void setSpare_numeric4(BigDecimal spare_numeric4) {
        this.spare_numeric4 = spare_numeric4;
    }

    /**
     * 予備５(数値)を取得
     * @return 予備５(数値)
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getSpare_numeric5() {
        return this.spare_numeric5;
    }

    /**
     * 予備５(数値)を設定
     * @param spare_numeric5 予備５(数値)
     */
    public void setSpare_numeric5(BigDecimal spare_numeric5) {
        this.spare_numeric5 = spare_numeric5;
    }

    /**
     * 予備６(数値)を取得
     * @return 予備６(数値)
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getSpare_numeric6() {
        return this.spare_numeric6;
    }

    /**
     * 予備６(数値)を設定
     * @param spare_numeric6 予備６(数値)
     */
    public void setSpare_numeric6(BigDecimal spare_numeric6) {
        this.spare_numeric6 = spare_numeric6;
    }

}
