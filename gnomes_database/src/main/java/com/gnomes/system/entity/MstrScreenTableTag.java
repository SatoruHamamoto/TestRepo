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
 * Zm008画面テーブル設定マスタ エンティティ
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
@Table(name = "mstr_screen_table_tag")
@NamedQueries({
        @NamedQuery(name = "MstrScreenTableTag.findAll", query = "SELECT p FROM MstrScreenTableTag p"),
        @NamedQuery(name = "MstrScreenTableTag.findByPK", query = "SELECT p FROM MstrScreenTableTag p WHERE p.screen_table_tag_key = :screen_table_tag_key")
})
public class MstrScreenTableTag extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_screen_table_tag";

    /** 画面テーブル設定キー */
    public static final String COLUMN_NAME_SCREEN_TABLE_TAG_KEY = "screen_table_tag_key";

    /** nk画面ID */
    public static final String COLUMN_NAME_SCREEN_ID = "screen_id";

    /** nkテーブルタグID */
    public static final String COLUMN_NAME_TABLE_TAG_ID = "table_tag_id";

    /** テーブルタグ名 画面名・テーブルカスタムタグ名 */
    public static final String COLUMN_NAME_TABLE_TAG_NAME = "table_tag_name";

    /** 表示件数上限 */
    public static final String COLUMN_NAME_MAX_LIST_DISPLAY_COUNT = "max_list_display_count";

    /** １ページの表示件数 ページングがある場合に設定 */
    public static final String COLUMN_NAME_ONE_PAGE_LIST_DISPLAY_COUNT = "one_page_list_display_count";

    /** 画面テーブル設定キー */
    private String screen_table_tag_key;
    /** nk画面ID */
    private String screen_id;
    /** nkテーブルタグID */
    private String table_tag_id;
    /** テーブルタグ名 画面名・テーブルカスタムタグ名 */
    private String table_tag_name;
    /** 表示件数上限 */
    private int max_list_display_count;
    /** １ページの表示件数 ページングがある場合に設定 */
    private Integer one_page_list_display_count;


    /**
     * Zm008画面テーブル設定マスタエンティティ コンストラクタ
     */
    public MstrScreenTableTag() {
    }

    /**
     * Zm008画面テーブル設定マスタエンティティ コンストラクタ
     * @param screen_table_tag_key 画面テーブル設定キー
     * @param screen_id nk画面ID
     * @param table_tag_id nkテーブルタグID
     * @param table_tag_name テーブルタグ名 画面名・テーブルカスタムタグ名
     * @param max_list_display_count 表示件数上限
     * @param version 更新バージョン
     */
    public MstrScreenTableTag(String screen_table_tag_key, String screen_id, String table_tag_id, String table_tag_name, int max_list_display_count, int version) {
        this.screen_table_tag_key = screen_table_tag_key;
        this.screen_id = screen_id;
        this.table_tag_id = table_tag_id;
        this.table_tag_name = table_tag_name;
        this.max_list_display_count = max_list_display_count;
        super.setVersion(version);
    }

    /**
     * Zm008画面テーブル設定マスタエンティティ コンストラクタ
     * @param screen_table_tag_key 画面テーブル設定キー
     * @param screen_id nk画面ID
     * @param table_tag_id nkテーブルタグID
     * @param table_tag_name テーブルタグ名 画面名・テーブルカスタムタグ名
     * @param max_list_display_count 表示件数上限
     * @param one_page_list_display_count １ページの表示件数 ページングがある場合に設定
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
    public MstrScreenTableTag(String screen_table_tag_key, String screen_id, String table_tag_id, String table_tag_name, int max_list_display_count, Integer one_page_list_display_count, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.screen_table_tag_key = screen_table_tag_key;
        this.screen_id = screen_id;
        this.table_tag_id = table_tag_id;
        this.table_tag_name = table_tag_name;
        this.max_list_display_count = max_list_display_count;
        this.one_page_list_display_count = one_page_list_display_count;
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
     * 画面テーブル設定キーを取得
     * @return 画面テーブル設定キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getScreen_table_tag_key() {
        return this.screen_table_tag_key;
    }

    /**
     * 画面テーブル設定キーを設定
     * @param screen_table_tag_key 画面テーブル設定キー (null不可)
     */
    public void setScreen_table_tag_key(String screen_table_tag_key) {
        this.screen_table_tag_key = screen_table_tag_key;
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
     * nkテーブルタグIDを取得
     * @return nkテーブルタグID
     */
    @Column(nullable = false, length = 32)
    public String getTable_tag_id() {
        return this.table_tag_id;
    }

    /**
     * nkテーブルタグIDを設定
     * @param table_tag_id nkテーブルタグID (null不可)
     */
    public void setTable_tag_id(String table_tag_id) {
        this.table_tag_id = table_tag_id;
    }

    /**
     * テーブルタグ名 画面名・テーブルカスタムタグ名を取得
     * @return テーブルタグ名 画面名・テーブルカスタムタグ名
     */
    @Column(nullable = false, length = 40)
    public String getTable_tag_name() {
        return this.table_tag_name;
    }

    /**
     * テーブルタグ名 画面名・テーブルカスタムタグ名を設定
     * @param table_tag_name テーブルタグ名 画面名・テーブルカスタムタグ名 (null不可)
     */
    public void setTable_tag_name(String table_tag_name) {
        this.table_tag_name = table_tag_name;
    }

    /**
     * 表示件数上限を取得
     * @return 表示件数上限
     */
    @Column(nullable = false, length = 5)
    public int getMax_list_display_count() {
        return this.max_list_display_count;
    }

    /**
     * 表示件数上限を設定
     * @param max_list_display_count 表示件数上限 (null不可)
     */
    public void setMax_list_display_count(int max_list_display_count) {
        this.max_list_display_count = max_list_display_count;
    }

    /**
     * １ページの表示件数 ページングがある場合に設定を取得
     * @return １ページの表示件数 ページングがある場合に設定
     */
    @Column(length = 5)
    public Integer getOne_page_list_display_count() {
        return this.one_page_list_display_count;
    }

    /**
     * １ページの表示件数 ページングがある場合に設定を設定
     * @param one_page_list_display_count １ページの表示件数 ページングがある場合に設定
     */
    public void setOne_page_list_display_count(Integer one_page_list_display_count) {
        this.one_page_list_display_count = one_page_list_display_count;
    }

}
