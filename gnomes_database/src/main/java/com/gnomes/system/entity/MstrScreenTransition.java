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
 * Zm003画面遷移情報マスタ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/10/09 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "mstr_screen_transition")
@NamedQueries({
        @NamedQuery(name = "MstrScreenTransition.findAll", query = "SELECT p FROM MstrScreenTransition p"),
        @NamedQuery(name = "MstrScreenTransition.findByPK", query = "SELECT p FROM MstrScreenTransition p WHERE p.screen_transition_key = :screen_transition_key")
})
public class MstrScreenTransition extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_screen_transition";

    /** 画面遷移情報キー */
    public static final String COLUMN_NAME_SCREEN_TRANSITION_KEY = "screen_transition_key";

    /** nk画面ID */
    public static final String COLUMN_NAME_SCREEN_ID = "screen_id";

    /** nkコマンドID */
    public static final String COLUMN_NAME_COMMAND_ID = "command_id";

    /** メニュー表示名のリソースID */
    public static final String COLUMN_NAME_MENU_RESOURCE_ID = "menu_resource_id";

    /** ブックマーク登録可否 */
    public static final String COLUMN_NAME_IS_BOOKMARK = "is_bookmark";

    /** 画面遷移情報キー */
    private String screen_transition_key;
    /** nk画面ID */
    private String screen_id;
    /** nkコマンドID */
    private String command_id;
    /** メニュー表示名のリソースID */
    private String menu_resource_id;
    /** ブックマーク登録可否 */
    private int is_bookmark;


    /**
     * Zm003画面遷移情報マスタエンティティ コンストラクタ
     */
    public MstrScreenTransition() {
    }

    /**
     * Zm003画面遷移情報マスタエンティティ コンストラクタ
     * @param screen_transition_key 画面遷移情報キー
     * @param screen_id nk画面ID
     * @param command_id nkコマンドID
     * @param menu_resource_id メニュー表示名のリソースID
     * @param is_bookmark ブックマーク登録可否
     * @param version 更新バージョン
     */
    public MstrScreenTransition(String screen_transition_key, String screen_id, String command_id, String menu_resource_id, int is_bookmark, int version) {
        this.screen_transition_key = screen_transition_key;
        this.screen_id = screen_id;
        this.command_id = command_id;
        this.menu_resource_id = menu_resource_id;
        this.is_bookmark = is_bookmark;
        super.setVersion(version);
    }

    /**
     * Zm003画面遷移情報マスタエンティティ コンストラクタ
     * @param screen_transition_key 画面遷移情報キー
     * @param screen_id nk画面ID
     * @param command_id nkコマンドID
     * @param menu_resource_id メニュー表示名のリソースID
     * @param is_bookmark ブックマーク登録可否
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
    public MstrScreenTransition(String screen_transition_key, String screen_id, String command_id, String menu_resource_id, int is_bookmark, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.screen_transition_key = screen_transition_key;
        this.screen_id = screen_id;
        this.command_id = command_id;
        this.menu_resource_id = menu_resource_id;
        this.is_bookmark = is_bookmark;
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
     * 画面遷移情報キーを取得
     * @return 画面遷移情報キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getScreen_transition_key() {
        return this.screen_transition_key;
    }

    /**
     * 画面遷移情報キーを設定
     * @param screen_transition_key 画面遷移情報キー (null不可)
     */
    public void setScreen_transition_key(String screen_transition_key) {
        this.screen_transition_key = screen_transition_key;
    }

    /**
     * nk画面IDを取得
     * @return nk画面ID
     */
    @Column(nullable = false, length = 12)
    public String getScreen_id() {
        return this.screen_id;
    }

    /**
     * nk画面IDを設定
     * @param screen_id nk画面ID (null不可)
     */
    public void setScreen_id(String screen_id) {
        this.screen_id = screen_id;
    }

    /**
     * nkコマンドIDを取得
     * @return nkコマンドID
     */
    @Column(nullable = false, length = 30)
    public String getCommand_id() {
        return this.command_id;
    }

    /**
     * nkコマンドIDを設定
     * @param command_id nkコマンドID (null不可)
     */
    public void setCommand_id(String command_id) {
        this.command_id = command_id;
    }

    /**
     * メニュー表示名のリソースIDを取得
     * @return メニュー表示名のリソースID
     */
    @Column(nullable = false, length = 40)
    public String getMenu_resource_id() {
        return this.menu_resource_id;
    }

    /**
     * メニュー表示名のリソースIDを設定
     * @param menu_resource_id メニュー表示名のリソースID (null不可)
     */
    public void setMenu_resource_id(String menu_resource_id) {
        this.menu_resource_id = menu_resource_id;
    }

    /**
     * ブックマーク登録可否を取得
     * @return ブックマーク登録可否
     */
    @Column(nullable = false, length = 1)
    public int getIs_bookmark() {
        return this.is_bookmark;
    }

    /**
     * ブックマーク登録可否を設定
     * @param is_bookmark ブックマーク登録可否 (null不可)
     */
    public void setIs_bookmark(int is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

}
