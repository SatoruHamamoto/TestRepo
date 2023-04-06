package com.gnomes.external.entity;

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
 * Zm132外部I/Fフォーマット定義マスタ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/05/28 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_external_if_format_define")
@NamedQueries({
        @NamedQuery(name = "MstrExternalIfFormatDefine.findAll", query = "SELECT p FROM MstrExternalIfFormatDefine p"),
        @NamedQuery(name = "MstrExternalIfFormatDefine.findByPK", query = "SELECT p FROM MstrExternalIfFormatDefine p WHERE p.external_if_format_define_key = :external_if_format_define_key")
})
public class MstrExternalIfFormatDefine extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_external_if_format_define";

    /** 外部I/Fフォーマット定義キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_FORMAT_DEFINE_KEY = "external_if_format_define_key";

    /** nkフォーマットID */
    public static final String COLUMN_NAME_FORMAT_ID = "format_id";

    /** フォーマット名称 */
    public static final String COLUMN_NAME_FORMAT_NAME = "format_name";

    /** まとめ可否 */
    public static final String COLUMN_NAME_IS_SUMMARY = "is_summary";

    /** 外部I/Fフォーマット定義キー */
    private String external_if_format_define_key;
    /** nkフォーマットID */
    private String format_id;
    /** フォーマット名称 */
    private String format_name;
    /** まとめ可否 */
    private int is_summary;


    /**
     * Zm132外部I/Fフォーマット定義マスタエンティティ コンストラクタ
     */
    public MstrExternalIfFormatDefine() {
    }

    /**
     * Zm132外部I/Fフォーマット定義マスタエンティティ コンストラクタ
     * @param external_if_format_define_key 外部I/Fフォーマット定義キー
     * @param format_id nkフォーマットID
     * @param format_name フォーマット名称
     * @param is_summary まとめ可否
     * @param version 更新バージョン
     */
    public MstrExternalIfFormatDefine(String external_if_format_define_key, String format_id, String format_name, int is_summary, int version) {
        this.external_if_format_define_key = external_if_format_define_key;
        this.format_id = format_id;
        this.format_name = format_name;
        this.is_summary = is_summary;
        super.setVersion(version);
    }

    /**
     * Zm132外部I/Fフォーマット定義マスタエンティティ コンストラクタ
     * @param external_if_format_define_key 外部I/Fフォーマット定義キー
     * @param format_id nkフォーマットID
     * @param format_name フォーマット名称
     * @param is_summary まとめ可否
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
    public MstrExternalIfFormatDefine(String external_if_format_define_key, String format_id, String format_name, int is_summary, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_format_define_key = external_if_format_define_key;
        this.format_id = format_id;
        this.format_name = format_name;
        this.is_summary = is_summary;
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
     * 外部I/Fフォーマット定義キーを取得
     * @return 外部I/Fフォーマット定義キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_format_define_key() {
        return this.external_if_format_define_key;
    }

    /**
     * 外部I/Fフォーマット定義キーを設定
     * @param external_if_format_define_key 外部I/Fフォーマット定義キー (null不可)
     */
    public void setExternal_if_format_define_key(String external_if_format_define_key) {
        this.external_if_format_define_key = external_if_format_define_key;
    }

    /**
     * nkフォーマットIDを取得
     * @return nkフォーマットID
     */
    @Column(nullable = false, length = 80)
    public String getFormat_id() {
        return this.format_id;
    }

    /**
     * nkフォーマットIDを設定
     * @param format_id nkフォーマットID (null不可)
     */
    public void setFormat_id(String format_id) {
        this.format_id = format_id;
    }

    /**
     * フォーマット名称を取得
     * @return フォーマット名称
     */
    @Column(nullable = false, length = 64)
    public String getFormat_name() {
        return this.format_name;
    }

    /**
     * フォーマット名称を設定
     * @param format_name フォーマット名称 (null不可)
     */
    public void setFormat_name(String format_name) {
        this.format_name = format_name;
    }

    /**
     * まとめ可否を取得
     * @return まとめ可否
     */
    @Column(nullable = false, length = 1)
    public int getIs_summary() {
        return this.is_summary;
    }

    /**
     * まとめ可否を設定
     * @param is_summary まとめ可否 (null不可)
     */
    public void setIs_summary(int is_summary) {
        this.is_summary = is_summary;
    }

}
