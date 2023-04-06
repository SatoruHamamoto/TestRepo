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
 * Zm001システム定義マスタ エンティティ
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
@Table(name = "mstr_system_define")
@NamedQueries({
        @NamedQuery(name = "MstrSystemDefine.findAll", query = "SELECT p FROM MstrSystemDefine p"),
        @NamedQuery(name = "MstrSystemDefine.findByPK", query = "SELECT p FROM MstrSystemDefine p WHERE p.system_define_key = :system_define_key")
})
public class MstrSystemDefine extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_system_define";

    /** システム定数キー */
    public static final String COLUMN_NAME_SYSTEM_DEFINE_KEY = "system_define_key";

    /** nkシステム定義区分 */
    public static final String COLUMN_NAME_SYSTEM_DEFINE_TYPE = "system_define_type";

    /** nkシステム定義コード */
    public static final String COLUMN_NAME_SYSTEM_DEFINE_CODE = "system_define_code";

    /** システム定義名 */
    public static final String COLUMN_NAME_SYSTEM_DEFINE_NAME = "system_define_name";

    /** 文字値1 */
    public static final String COLUMN_NAME_CHAR1 = "char1";

    /** 文字値2 */
    public static final String COLUMN_NAME_CHAR2 = "char2";

    /** 数値1 */
    public static final String COLUMN_NAME_NUMERIC1 = "numeric1";

    /** 数値2 */
    public static final String COLUMN_NAME_NUMERIC2 = "numeric2";

    /** 説明 */
    public static final String COLUMN_NAME_EXPLANATION = "explanation";

    /** システム定数キー */
    private String system_define_key;
    /** nkシステム定義区分 */
    private String system_define_type;
    /** nkシステム定義コード */
    private String system_define_code;
    /** システム定義名 */
    private String system_define_name;
    /** 文字値1 */
    private String char1;
    /** 文字値2 */
    private String char2;
    /** 数値1 */
    private BigDecimal numeric1;
    /** 数値2 */
    private BigDecimal numeric2;
    /** 説明 */
    private String explanation;


    /**
     * Zm001システム定義マスタエンティティ コンストラクタ
     */
    public MstrSystemDefine() {
    }

    /**
     * Zm001システム定義マスタエンティティ コンストラクタ
     * @param system_define_key システム定数キー
     * @param system_define_type nkシステム定義区分
     * @param system_define_code nkシステム定義コード
     * @param version 更新バージョン
     */
    public MstrSystemDefine(String system_define_key, String system_define_type, String system_define_code, int version) {
        this.system_define_key = system_define_key;
        this.system_define_type = system_define_type;
        this.system_define_code = system_define_code;
        super.setVersion(version);
    }

    /**
     * Zm001システム定義マスタエンティティ コンストラクタ
     * @param system_define_key システム定数キー
     * @param system_define_type nkシステム定義区分
     * @param system_define_code nkシステム定義コード
     * @param system_define_name システム定義名
     * @param char1 文字値1
     * @param char2 文字値2
     * @param numeric1 数値1
     * @param numeric2 数値2
     * @param explanation 説明
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
    public MstrSystemDefine(String system_define_key, String system_define_type, String system_define_code, String system_define_name, String char1, String char2, BigDecimal numeric1, BigDecimal numeric2, String explanation, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.system_define_key = system_define_key;
        this.system_define_type = system_define_type;
        this.system_define_code = system_define_code;
        this.system_define_name = system_define_name;
        this.char1 = char1;
        this.char2 = char2;
        this.numeric1 = numeric1;
        this.numeric2 = numeric2;
        this.explanation = explanation;
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
     * システム定数キーを取得
     * @return システム定数キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getSystem_define_key() {
        return this.system_define_key;
    }

    /**
     * システム定数キーを設定
     * @param system_define_key システム定数キー (null不可)
     */
    public void setSystem_define_key(String system_define_key) {
        this.system_define_key = system_define_key;
    }

    /**
     * nkシステム定義区分を取得
     * @return nkシステム定義区分
     */
    @Column(nullable = false, length = 40)
    public String getSystem_define_type() {
        return this.system_define_type;
    }

    /**
     * nkシステム定義区分を設定
     * @param system_define_type nkシステム定義区分 (null不可)
     */
    public void setSystem_define_type(String system_define_type) {
        this.system_define_type = system_define_type;
    }

    /**
     * nkシステム定義コードを取得
     * @return nkシステム定義コード
     */
    @Column(nullable = false, length = 40)
    public String getSystem_define_code() {
        return this.system_define_code;
    }

    /**
     * nkシステム定義コードを設定
     * @param system_define_code nkシステム定義コード (null不可)
     */
    public void setSystem_define_code(String system_define_code) {
        this.system_define_code = system_define_code;
    }

    /**
     * システム定義名を取得
     * @return システム定義名
     */
    @Column(length = 40)
    public String getSystem_define_name() {
        return this.system_define_name;
    }

    /**
     * システム定義名を設定
     * @param system_define_name システム定義名
     */
    public void setSystem_define_name(String system_define_name) {
        this.system_define_name = system_define_name;
    }

    /**
     * 文字値1を取得
     * @return 文字値1
     */
    @Column(length = 200)
    public String getChar1() {
        return this.char1;
    }

    /**
     * 文字値1を設定
     * @param char1 文字値1
     */
    public void setChar1(String char1) {
        this.char1 = char1;
    }

    /**
     * 文字値2を取得
     * @return 文字値2
     */
    @Column(length = 200)
    public String getChar2() {
        return this.char2;
    }

    /**
     * 文字値2を設定
     * @param char2 文字値2
     */
    public void setChar2(String char2) {
        this.char2 = char2;
    }

    /**
     * 数値1を取得
     * @return 数値1
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getNumeric1() {
        return this.numeric1;
    }

    /**
     * 数値1を設定
     * @param numeric1 数値1
     */
    public void setNumeric1(BigDecimal numeric1) {
        this.numeric1 = numeric1;
    }

    /**
     * 数値2を取得
     * @return 数値2
     */
    @Column(precision = 17, scale = 5)
    public BigDecimal getNumeric2() {
        return this.numeric2;
    }

    /**
     * 数値2を設定
     * @param numeric2 数値2
     */
    public void setNumeric2(BigDecimal numeric2) {
        this.numeric2 = numeric2;
    }

    /**
     * 説明を取得
     * @return 説明
     */
    @Column(length = 100)
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * 説明を設定
     * @param explanation 説明
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
