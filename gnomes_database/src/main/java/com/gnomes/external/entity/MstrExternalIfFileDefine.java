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
 * Zm131外部I/Fファイル構成定義マスタ エンティティ
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
@Table(name = "mstr_external_if_file_define")
@NamedQueries({
        @NamedQuery(name = "MstrExternalIfFileDefine.findAll", query = "SELECT p FROM MstrExternalIfFileDefine p"),
        @NamedQuery(name = "MstrExternalIfFileDefine.findByPK", query = "SELECT p FROM MstrExternalIfFileDefine p WHERE p.external_if_file_define_key = :external_if_file_define_key")
})
public class MstrExternalIfFileDefine extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_external_if_file_define";

    /** 外部I/Fファイル構成定義キー */
    public static final String COLUMN_NAME_EXTERNAL_IF_FILE_DEFINE_KEY = "external_if_file_define_key";

    /** nkファイル種別 */
    public static final String COLUMN_NAME_FILE_TYPE = "file_type";

    /** nk定義項目コード */
    public static final String COLUMN_NAME_DEFINITION_CODE = "definition_code";

    /** 定義項目名称 */
    public static final String COLUMN_NAME_DEFINITION_NAME = "definition_name";

    /** 定義項目設定値 */
    public static final String COLUMN_NAME_SET_VALUE = "set_value";

    /** 定義項目説明 */
    public static final String COLUMN_NAME_EXPLANATION = "explanation";

    /** 外部I/Fファイル構成定義キー */
    private String external_if_file_define_key;
    /** nkファイル種別 */
    private String file_type;
    /** nk定義項目コード */
    private String definition_code;
    /** 定義項目名称 */
    private String definition_name;
    /** 定義項目設定値 */
    private String set_value;
    /** 定義項目説明 */
    private String explanation;


    /**
     * Zm131外部I/Fファイル構成定義マスタエンティティ コンストラクタ
     */
    public MstrExternalIfFileDefine() {
    }

    /**
     * Zm131外部I/Fファイル構成定義マスタエンティティ コンストラクタ
     * @param external_if_file_define_key 外部I/Fファイル構成定義キー
     * @param file_type nkファイル種別
     * @param definition_code nk定義項目コード
     * @param version 更新バージョン
     */
    public MstrExternalIfFileDefine(String external_if_file_define_key, String file_type, String definition_code, int version) {
        this.external_if_file_define_key = external_if_file_define_key;
        this.file_type = file_type;
        this.definition_code = definition_code;
        super.setVersion(version);
    }

    /**
     * Zm131外部I/Fファイル構成定義マスタエンティティ コンストラクタ
     * @param external_if_file_define_key 外部I/Fファイル構成定義キー
     * @param file_type nkファイル種別
     * @param definition_code nk定義項目コード
     * @param definition_name 定義項目名称
     * @param set_value 定義項目設定値
     * @param explanation 定義項目説明
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
    public MstrExternalIfFileDefine(String external_if_file_define_key, String file_type, String definition_code, String definition_name, String set_value, String explanation, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.external_if_file_define_key = external_if_file_define_key;
        this.file_type = file_type;
        this.definition_code = definition_code;
        this.definition_name = definition_name;
        this.set_value = set_value;
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
     * 外部I/Fファイル構成定義キーを取得
     * @return 外部I/Fファイル構成定義キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getExternal_if_file_define_key() {
        return this.external_if_file_define_key;
    }

    /**
     * 外部I/Fファイル構成定義キーを設定
     * @param external_if_file_define_key 外部I/Fファイル構成定義キー (null不可)
     */
    public void setExternal_if_file_define_key(String external_if_file_define_key) {
        this.external_if_file_define_key = external_if_file_define_key;
    }

    /**
     * nkファイル種別を取得
     * @return nkファイル種別
     */
    @Column(nullable = false, length = 16)
    public String getFile_type() {
        return this.file_type;
    }

    /**
     * nkファイル種別を設定
     * @param file_type nkファイル種別 (null不可)
     */
    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    /**
     * nk定義項目コードを取得
     * @return nk定義項目コード
     */
    @Column(nullable = false, length = 80)
    public String getDefinition_code() {
        return this.definition_code;
    }

    /**
     * nk定義項目コードを設定
     * @param definition_code nk定義項目コード (null不可)
     */
    public void setDefinition_code(String definition_code) {
        this.definition_code = definition_code;
    }

    /**
     * 定義項目名称を取得
     * @return 定義項目名称
     */
    @Column(length = 64)
    public String getDefinition_name() {
        return this.definition_name;
    }

    /**
     * 定義項目名称を設定
     * @param definition_name 定義項目名称
     */
    public void setDefinition_name(String definition_name) {
        this.definition_name = definition_name;
    }

    /**
     * 定義項目設定値を取得
     * @return 定義項目設定値
     */
    @Column(length = 1000)
    public String getSet_value() {
        return this.set_value;
    }

    /**
     * 定義項目設定値を設定
     * @param set_value 定義項目設定値
     */
    public void setSet_value(String set_value) {
        this.set_value = set_value;
    }

    /**
     * 定義項目説明を取得
     * @return 定義項目説明
     */
    @Column(length = 1000)
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * 定義項目説明を設定
     * @param explanation 定義項目説明
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
