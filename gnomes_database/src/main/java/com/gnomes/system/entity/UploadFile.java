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
 * Zr001アップロードファイル管理 エンティティ
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
@Table(name = "upload_file")
@NamedQueries({
        @NamedQuery(name = "UploadFile.findAll", query = "SELECT p FROM UploadFile p"),
        @NamedQuery(name = "UploadFile.findByPK", query = "SELECT p FROM UploadFile p WHERE p.upload_file_key = :upload_file_key")
})
public class UploadFile extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "upload_file";

    /** アップロードファイル管理キー */
    public static final String COLUMN_NAME_UPLOAD_FILE_KEY = "upload_file_key";

    /** nkフォルダ名 */
    public static final String COLUMN_NAME_FOLDER_NAME = "folder_name";

    /** nk実ファイル名 */
    public static final String COLUMN_NAME_FILE_NAME = "file_name";

    /** システム内ファイル名 */
    public static final String COLUMN_NAME_SYSTEM_FILE_NAME = "system_file_name";

    /** 保管モード */
    public static final String COLUMN_NAME_SAVE_MODE = "save_mode";

    /** アップロードファイル管理キー */
    private String upload_file_key;
    /** nkフォルダ名 */
    private String folder_name;
    /** nk実ファイル名 */
    private String file_name;
    /** システム内ファイル名 */
    private String system_file_name;
    /** 保管モード */
    private int save_mode;


    /**
     * Zr001アップロードファイル管理エンティティ コンストラクタ
     */
    public UploadFile() {
    }

    /**
     * Zr001アップロードファイル管理エンティティ コンストラクタ
     * @param upload_file_key アップロードファイル管理キー
     * @param folder_name nkフォルダ名
     * @param file_name nk実ファイル名
     * @param system_file_name システム内ファイル名
     * @param save_mode 保管モード
     * @param version 更新バージョン
     */
    public UploadFile(String upload_file_key, String folder_name, String file_name, String system_file_name, int save_mode, int version) {
        this.upload_file_key = upload_file_key;
        this.folder_name = folder_name;
        this.file_name = file_name;
        this.system_file_name = system_file_name;
        this.save_mode = save_mode;
        super.setVersion(version);
    }

    /**
     * Zr001アップロードファイル管理エンティティ コンストラクタ
     * @param upload_file_key アップロードファイル管理キー
     * @param folder_name nkフォルダ名
     * @param file_name nk実ファイル名
     * @param system_file_name システム内ファイル名
     * @param save_mode 保管モード
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
    public UploadFile(String upload_file_key, String folder_name, String file_name, String system_file_name, int save_mode, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.upload_file_key = upload_file_key;
        this.folder_name = folder_name;
        this.file_name = file_name;
        this.system_file_name = system_file_name;
        this.save_mode = save_mode;
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
     * アップロードファイル管理キーを取得
     * @return アップロードファイル管理キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getUpload_file_key() {
        return this.upload_file_key;
    }

    /**
     * アップロードファイル管理キーを設定
     * @param upload_file_key アップロードファイル管理キー (null不可)
     */
    public void setUpload_file_key(String upload_file_key) {
        this.upload_file_key = upload_file_key;
    }

    /**
     * nkフォルダ名を取得
     * @return nkフォルダ名
     */
    @Column(nullable = false, length = 2000)
    public String getFolder_name() {
        return this.folder_name;
    }

    /**
     * nkフォルダ名を設定
     * @param folder_name nkフォルダ名 (null不可)
     */
    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    /**
     * nk実ファイル名を取得
     * @return nk実ファイル名
     */
    @Column(nullable = false, length = 2000)
    public String getFile_name() {
        return this.file_name;
    }

    /**
     * nk実ファイル名を設定
     * @param file_name nk実ファイル名 (null不可)
     */
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    /**
     * システム内ファイル名を取得
     * @return システム内ファイル名
     */
    @Column(nullable = false, length = 2000)
    public String getSystem_file_name() {
        return this.system_file_name;
    }

    /**
     * システム内ファイル名を設定
     * @param system_file_name システム内ファイル名 (null不可)
     */
    public void setSystem_file_name(String system_file_name) {
        this.system_file_name = system_file_name;
    }

    /**
     * 保管モードを取得
     * @return 保管モード
     */
    @Column(nullable = false, length = 1)
    public int getSave_mode() {
        return this.save_mode;
    }

    /**
     * 保管モードを設定
     * @param save_mode 保管モード (null不可)
     */
    public void setSave_mode(int save_mode) {
        this.save_mode = save_mode;
    }

}
