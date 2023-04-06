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
 * Zm024階層付加役割マスタ エンティティ
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
@Table(name = "mstr_position_role")
@NamedQueries({
        @NamedQuery(name = "MstrPositionRole.findAll", query = "SELECT p FROM MstrPositionRole p"),
        @NamedQuery(name = "MstrPositionRole.findByPK", query = "SELECT p FROM MstrPositionRole p WHERE p.position_role_key = :position_role_key")
})
public class MstrPositionRole extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_position_role";

    /** 階層付加役割キー */
    public static final String COLUMN_NAME_POSITION_ROLE_KEY = "position_role_key";

    /** 階層キー (FK) */
    public static final String COLUMN_NAME_POSITION_KEY = "position_key";

    /** ロールキー (FK) */
    public static final String COLUMN_NAME_ROLE_KEY = "role_key";

    /** nk階層ID */
    public static final String COLUMN_NAME_POSITION_ID = "position_id";

    /** nkロールID */
    public static final String COLUMN_NAME_ROLE_ID = "role_id";

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

    /** 階層付加役割キー */
    private String position_role_key;
    /** 階層キー (FK) */
    private String position_key;
    /** ロールキー (FK) */
    private String role_key;
    /** nk階層ID */
    private String position_id;
    /** nkロールID */
    private String role_id;
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
     * Zm024階層付加役割マスタエンティティ コンストラクタ
     */
    public MstrPositionRole() {
    }

    /**
     * Zm024階層付加役割マスタエンティティ コンストラクタ
     * @param position_role_key 階層付加役割キー
     * @param position_key 階層キー (FK)
     * @param role_key ロールキー (FK)
     * @param position_id nk階層ID
     * @param role_id nkロールID
     * @param version 更新バージョン
     */
    public MstrPositionRole(String position_role_key, String position_key, String role_key, String position_id, String role_id, int version) {
        this.position_role_key = position_role_key;
        this.position_key = position_key;
        this.role_key = role_key;
        this.position_id = position_id;
        this.role_id = role_id;
        super.setVersion(version);
    }

    /**
     * Zm024階層付加役割マスタエンティティ コンストラクタ
     * @param position_role_key 階層付加役割キー
     * @param position_key 階層キー (FK)
     * @param role_key ロールキー (FK)
     * @param position_id nk階層ID
     * @param role_id nkロールID
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
    public MstrPositionRole(String position_role_key, String position_key, String role_key, String position_id, String role_id, String spare_char1, String spare_char2, String spare_char3, String spare_char4, BigDecimal spare_numeric1, BigDecimal spare_numeric2, BigDecimal spare_numeric3, BigDecimal spare_numeric4, BigDecimal spare_numeric5, BigDecimal spare_numeric6, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.position_role_key = position_role_key;
        this.position_key = position_key;
        this.role_key = role_key;
        this.position_id = position_id;
        this.role_id = role_id;
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
     * 階層付加役割キーを取得
     * @return 階層付加役割キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getPosition_role_key() {
        return this.position_role_key;
    }

    /**
     * 階層付加役割キーを設定
     * @param position_role_key 階層付加役割キー (null不可)
     */
    public void setPosition_role_key(String position_role_key) {
        this.position_role_key = position_role_key;
    }

    /**
     * 階層キー (FK)を取得
     * @return 階層キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getPosition_key() {
        return this.position_key;
    }

    /**
     * 階層キー (FK)を設定
     * @param position_key 階層キー (FK) (null不可)
     */
    public void setPosition_key(String position_key) {
        this.position_key = position_key;
    }

    /**
     * ロールキー (FK)を取得
     * @return ロールキー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getRole_key() {
        return this.role_key;
    }

    /**
     * ロールキー (FK)を設定
     * @param role_key ロールキー (FK) (null不可)
     */
    public void setRole_key(String role_key) {
        this.role_key = role_key;
    }

    /**
     * nk階層IDを取得
     * @return nk階層ID
     */
    @Column(nullable = false, length = 32)
    public String getPosition_id() {
        return this.position_id;
    }

    /**
     * nk階層IDを設定
     * @param position_id nk階層ID (null不可)
     */
    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    /**
     * nkロールIDを取得
     * @return nkロールID
     */
    @Column(nullable = false, length = 32)
    public String getRole_id() {
        return this.role_id;
    }

    /**
     * nkロールIDを設定
     * @param role_id nkロールID (null不可)
     */
    public void setRole_id(String role_id) {
        this.role_id = role_id;
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
