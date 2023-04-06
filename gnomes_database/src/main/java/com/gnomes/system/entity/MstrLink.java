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
 * Zr012リンク情報マスタ エンティティ
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
@Table(name = "mstr_link")
@NamedQueries({
        @NamedQuery(name = "MstrLink.findAll", query = "SELECT p FROM MstrLink p"),
        @NamedQuery(name = "MstrLink.findByPK", query = "SELECT p FROM MstrLink p WHERE p.link_key = :link_key")
})
public class MstrLink extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_link";

    /** リンク情報キー */
    public static final String COLUMN_NAME_LINK_KEY = "link_key";

    /** メッセージキー (FK) */
    public static final String COLUMN_NAME_MESSAGE_KEY = "message_key";

    /** ガイダンスリソースID */
    public static final String COLUMN_NAME_GUIDANCE_RESOURCE_ID = "guidance_resource_id";

    /** リンク情報 */
    public static final String COLUMN_NAME_LINK_INFO = "link_info";

    /** リンク名リソースID */
    public static final String COLUMN_NAME_LINK_NAME_RESOURCE_ID = "link_name_resource_id";

    /** リンク情報キー */
    private String link_key;
    /** メッセージキー (FK) */
    private String message_key;
    /** ガイダンスリソースID */
    private String guidance_resource_id;
    /** リンク情報 */
    private String link_info;
    /** リンク名リソースID */
    private String link_name_resource_id;


    /**
     * Zr012リンク情報マスタエンティティ コンストラクタ
     */
    public MstrLink() {
    }

    /**
     * Zr012リンク情報マスタエンティティ コンストラクタ
     * @param link_key リンク情報キー
     * @param message_key メッセージキー (FK)
     * @param version 更新バージョン
     */
    public MstrLink(String link_key, String message_key, int version) {
        this.link_key = link_key;
        this.message_key = message_key;
        super.setVersion(version);
    }

    /**
     * Zr012リンク情報マスタエンティティ コンストラクタ
     * @param link_key リンク情報キー
     * @param message_key メッセージキー (FK)
     * @param guidance_resource_id ガイダンスリソースID
     * @param link_info リンク情報
     * @param link_name_resource_id リンク名リソースID
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
    public MstrLink(String link_key, String message_key, String guidance_resource_id, String link_info, String link_name_resource_id, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.link_key = link_key;
        this.message_key = message_key;
        this.guidance_resource_id = guidance_resource_id;
        this.link_info = link_info;
        this.link_name_resource_id = link_name_resource_id;
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
     * リンク情報キーを取得
     * @return リンク情報キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getLink_key() {
        return this.link_key;
    }

    /**
     * リンク情報キーを設定
     * @param link_key リンク情報キー (null不可)
     */
    public void setLink_key(String link_key) {
        this.link_key = link_key;
    }

    /**
     * メッセージキー (FK)を取得
     * @return メッセージキー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getMessage_key() {
        return this.message_key;
    }

    /**
     * メッセージキー (FK)を設定
     * @param message_key メッセージキー (FK) (null不可)
     */
    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    /**
     * ガイダンスリソースIDを取得
     * @return ガイダンスリソースID
     */
    @Column(length = 40)
    public String getGuidance_resource_id() {
        return this.guidance_resource_id;
    }

    /**
     * ガイダンスリソースIDを設定
     * @param guidance_resource_id ガイダンスリソースID
     */
    public void setGuidance_resource_id(String guidance_resource_id) {
        this.guidance_resource_id = guidance_resource_id;
    }

    /**
     * リンク情報を取得
     * @return リンク情報
     */
    @Column(length = 2000)
    public String getLink_info() {
        return this.link_info;
    }

    /**
     * リンク情報を設定
     * @param link_info リンク情報
     */
    public void setLink_info(String link_info) {
        this.link_info = link_info;
    }

    /**
     * リンク名リソースIDを取得
     * @return リンク名リソースID
     */
    @Column(length = 40)
    public String getLink_name_resource_id() {
        return this.link_name_resource_id;
    }

    /**
     * リンク名リソースIDを設定
     * @param link_name_resource_id リンク名リソースID
     */
    public void setLink_name_resource_id(String link_name_resource_id) {
        this.link_name_resource_id = link_name_resource_id;
    }

}
