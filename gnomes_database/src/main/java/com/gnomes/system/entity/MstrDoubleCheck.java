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
 * Zm033ダブルチェック確認者に必要な権限 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/10/03 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_double_check")
@NamedQueries({
        @NamedQuery(name = "MstrDoubleCheck.findAll", query = "SELECT p FROM MstrDoubleCheck p"),
        @NamedQuery(name = "MstrDoubleCheck.findByPK", query = "SELECT p FROM MstrDoubleCheck p WHERE p.double_check_key = :double_check_key")
})
public class MstrDoubleCheck extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_double_check";

    /** ダブルチェック権限キー */
    public static final String COLUMN_NAME_DOUBLE_CHECK_KEY = "double_check_key";

    /** 権限キー (FK) */
    public static final String COLUMN_NAME_PRIVILEGE_KEY = "privilege_key";

    /** nk権限ID */
    public static final String COLUMN_NAME_PRIVILEGE_ID = "privilege_id";

    /** nkDC確認者に必要な階層ID */
    public static final String COLUMN_NAME_CHECKER_POSITION_ID = "checker_position_id";

    /** nkDC確認者に必要な役割ID */
    public static final String COLUMN_NAME_CHECKER_ROLE_ID = "checker_role_id";

    /** nkDC確認者に必要な権限ID */
    public static final String COLUMN_NAME_CHECKER_PRIVILEGE_ID = "checker_privilege_id";

    /** DC確認者に必要な階層Key */
    public static final String COLUMN_NAME_CHECKER_POSITION_KEY = "checker_position_key";

    /** DC確認者に必要な役割Key */
    public static final String COLUMN_NAME_CHECKER_ROLE_KEY = "checker_role_key";

    /** DC確認者に必要な権限Key */
    public static final String COLUMN_NAME_CHECKER_PRIVILEGE_KEY = "checker_privilege_key";

    /** 表示順 */
    public static final String COLUMN_NAME_DISPLAY_ORDER = "display_order";

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

    /** ダブルチェック権限キー */
    private String double_check_key;
    /** 権限キー (FK) */
    private String privilege_key;
    /** nk権限ID */
    private String privilege_id;
    /** nkDC確認者に必要な階層ID */
    private String checker_position_id;
    /** nkDC確認者に必要な役割ID */
    private String checker_role_id;
    /** nkDC確認者に必要な権限ID */
    private String checker_privilege_id;
    /** DC確認者に必要な階層Key */
    private String checker_position_key;
    /** DC確認者に必要な役割Key */
    private String checker_role_key;
    /** DC確認者に必要な権限Key */
    private String checker_privilege_key;
    /** 表示順 */
    private int display_order;
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
     * Zm033ダブルチェック確認者に必要な権限エンティティ コンストラクタ
     */
    public MstrDoubleCheck() {
    }

    /**
     * Zm033ダブルチェック確認者に必要な権限エンティティ コンストラクタ
     * @param double_check_key ダブルチェック権限キー
     * @param privilege_key 権限キー (FK)
     * @param privilege_id nk権限ID
     * @param display_order 表示順
     * @param version 更新バージョン
     */
    public MstrDoubleCheck(String double_check_key, String privilege_key, String privilege_id, int display_order, int version) {
        this.double_check_key = double_check_key;
        this.privilege_key = privilege_key;
        this.privilege_id = privilege_id;
        this.display_order = display_order;
        super.setVersion(version);
    }

    /**
     * Zm033ダブルチェック確認者に必要な権限エンティティ コンストラクタ
     * @param double_check_key ダブルチェック権限キー
     * @param privilege_key 権限キー (FK)
     * @param privilege_id nk権限ID
     * @param checker_position_id nkDC確認者に必要な階層ID
     * @param checker_role_id nkDC確認者に必要な役割ID
     * @param checker_privilege_id nkDC確認者に必要な権限ID
     * @param checker_position_key DC確認者に必要な階層Key
     * @param checker_role_key DC確認者に必要な役割Key
     * @param checker_privilege_key DC確認者に必要な権限Key
     * @param display_order 表示順
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
    public MstrDoubleCheck(String double_check_key, String privilege_key, String privilege_id, String checker_position_id, String checker_role_id, String checker_privilege_id, String checker_position_key, String checker_role_key, String checker_privilege_key, int display_order, String spare_char1, String spare_char2, String spare_char3, String spare_char4, BigDecimal spare_numeric1, BigDecimal spare_numeric2, BigDecimal spare_numeric3, BigDecimal spare_numeric4, BigDecimal spare_numeric5, BigDecimal spare_numeric6, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.double_check_key = double_check_key;
        this.privilege_key = privilege_key;
        this.privilege_id = privilege_id;
        this.checker_position_id = checker_position_id;
        this.checker_role_id = checker_role_id;
        this.checker_privilege_id = checker_privilege_id;
        this.checker_position_key = checker_position_key;
        this.checker_role_key = checker_role_key;
        this.checker_privilege_key = checker_privilege_key;
        this.display_order = display_order;
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
     * ダブルチェック権限キーを取得
     * @return ダブルチェック権限キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getDouble_check_key() {
        return this.double_check_key;
    }

    /**
     * ダブルチェック権限キーを設定
     * @param double_check_key ダブルチェック権限キー (null不可)
     */
    public void setDouble_check_key(String double_check_key) {
        this.double_check_key = double_check_key;
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
     * nkDC確認者に必要な階層IDを取得
     * @return nkDC確認者に必要な階層ID
     */
    @Column(length = 32)
    public String getChecker_position_id() {
        return this.checker_position_id;
    }

    /**
     * nkDC確認者に必要な階層IDを設定
     * @param checker_position_id nkDC確認者に必要な階層ID
     */
    public void setChecker_position_id(String checker_position_id) {
        this.checker_position_id = checker_position_id;
    }

    /**
     * nkDC確認者に必要な役割IDを取得
     * @return nkDC確認者に必要な役割ID
     */
    @Column(length = 32)
    public String getChecker_role_id() {
        return this.checker_role_id;
    }

    /**
     * nkDC確認者に必要な役割IDを設定
     * @param checker_role_id nkDC確認者に必要な役割ID
     */
    public void setChecker_role_id(String checker_role_id) {
        this.checker_role_id = checker_role_id;
    }

    /**
     * nkDC確認者に必要な権限IDを取得
     * @return nkDC確認者に必要な権限ID
     */
    @Column(length = 32)
    public String getChecker_privilege_id() {
        return this.checker_privilege_id;
    }

    /**
     * nkDC確認者に必要な権限IDを設定
     * @param checker_privilege_id nkDC確認者に必要な権限ID
     */
    public void setChecker_privilege_id(String checker_privilege_id) {
        this.checker_privilege_id = checker_privilege_id;
    }

    /**
     * DC確認者に必要な階層Keyを取得
     * @return DC確認者に必要な階層Key
     */
    @Column(length = 38)
    public String getChecker_position_key() {
        return this.checker_position_key;
    }

    /**
     * DC確認者に必要な階層Keyを設定
     * @param checker_position_key DC確認者に必要な階層Key
     */
    public void setChecker_position_key(String checker_position_key) {
        this.checker_position_key = checker_position_key;
    }

    /**
     * DC確認者に必要な役割Keyを取得
     * @return DC確認者に必要な役割Key
     */
    @Column(length = 38)
    public String getChecker_role_key() {
        return this.checker_role_key;
    }

    /**
     * DC確認者に必要な役割Keyを設定
     * @param checker_role_key DC確認者に必要な役割Key
     */
    public void setChecker_role_key(String checker_role_key) {
        this.checker_role_key = checker_role_key;
    }

    /**
     * DC確認者に必要な権限Keyを取得
     * @return DC確認者に必要な権限Key
     */
    @Column(length = 38)
    public String getChecker_privilege_key() {
        return this.checker_privilege_key;
    }

    /**
     * DC確認者に必要な権限Keyを設定
     * @param checker_privilege_key DC確認者に必要な権限Key
     */
    public void setChecker_privilege_key(String checker_privilege_key) {
        this.checker_privilege_key = checker_privilege_key;
    }

    /**
     * 表示順を取得
     * @return 表示順
     */
    @Column(nullable = false, length = 3)
    public int getDisplay_order() {
        return this.display_order;
    }

    /**
     * 表示順を設定
     * @param display_order 表示順 (null不可)
     */
    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
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
