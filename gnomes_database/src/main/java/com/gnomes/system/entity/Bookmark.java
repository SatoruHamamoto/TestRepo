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
 * Zi004ブックマーク管理 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/04/30 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "bookmark")
@NamedQueries({
        @NamedQuery(name = "Bookmark.findAll", query = "SELECT p FROM Bookmark p"),
        @NamedQuery(name = "Bookmark.findByPK", query = "SELECT p FROM Bookmark p WHERE p.bookmark_key = :bookmark_key")
})
public class Bookmark extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "bookmark";

    /** ブックマーク管理キー */
    public static final String COLUMN_NAME_BOOKMARK_KEY = "bookmark_key";

    /** nkユーザID */
    public static final String COLUMN_NAME_USER_ID = "user_id";

    /** nkブックマーク画面ID */
    public static final String COLUMN_NAME_BOOKMARK_SCREEN_ID = "bookmark_screen_id";

    /** nk画面遷移情報キー */
    public static final String COLUMN_NAME_SCREEN_TRANSITION_KEY = "screen_transition_key";
    
    /** nk画面遷移情報キー */
    public static final String COLUMN_NAME_BOOKMARK_PARAMETER = "bookmark_parameter";

    /** DB領域区分 */
    public static final String COLUMN_NAME_DB_AREA_DIV = "db_area_div";

    /** ブックマーク管理キー */
    private String bookmark_key;
    /** nkユーザID */
    private String user_id;
    /** nkブックマーク画面ID */
    private String bookmark_screen_id;
    /** nk画面遷移情報キー */
    private String screen_transition_key;
    /** nkパラメーター*/
    private String bookmark_parameter;
    /** DB領域区分 */
    private Integer db_area_div;


    /**
     * Zi004ブックマーク管理エンティティ コンストラクタ
     */
    public Bookmark() {
    }

    /**
     * Zi004ブックマーク管理エンティティ コンストラクタ
     * @param bookmark_key ブックマーク管理キー
     * @param user_id nkユーザID
     * @param bookmark_screen_id nkブックマーク画面ID
     * @param screen_transition_key nk画面遷移情報キー
     * @param bookmark_parameter nkパラメーター
     * @param version 更新バージョン
     */
    public Bookmark(String bookmark_key, String user_id, String bookmark_screen_id, String screen_transition_key, String bookmark_parameter,int version) {
        this.bookmark_key = bookmark_key;
        this.user_id = user_id;
        this.bookmark_screen_id = bookmark_screen_id;
        this.screen_transition_key = screen_transition_key;
        this.bookmark_parameter = bookmark_parameter;
        super.setVersion(version);
    }

    /**
     * Zi004ブックマーク管理エンティティ コンストラクタ
     * @param bookmark_key ブックマーク管理キー
     * @param user_id nkユーザID
     * @param bookmark_screen_id nkブックマーク画面ID
     * @param screen_transition_key nk画面遷移情報キー
     * @param db_area_div DB領域区分
     * @param bookmark_parameter nkパラメーター
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
    public Bookmark(String bookmark_key, String user_id, String bookmark_screen_id, String screen_transition_key, String bookmark_parameter,Integer db_area_div, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.bookmark_key = bookmark_key;
        this.user_id = user_id;
        this.bookmark_screen_id = bookmark_screen_id;
        this.screen_transition_key = screen_transition_key;
        this.db_area_div = db_area_div;
        this.bookmark_parameter = bookmark_parameter;
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
     * ブックマーク管理キーを取得
     * @return ブックマーク管理キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getBookmark_key() {
        return this.bookmark_key;
    }

    /**
     * ブックマーク管理キーを設定
     * @param bookmark_key ブックマーク管理キー (null不可)
     */
    public void setBookmark_key(String bookmark_key) {
        this.bookmark_key = bookmark_key;
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
     * nkブックマーク画面IDを取得
     * @return nkブックマーク画面ID
     */
    @Column(nullable = false, length = 12)
    public String getBookmark_screen_id() {
        return this.bookmark_screen_id;
    }

    /**
     * nkブックマーク画面IDを設定
     * @param bookmark_screen_id nkブックマーク画面ID (null不可)
     */
    public void setBookmark_screen_id(String bookmark_screen_id) {
        this.bookmark_screen_id = bookmark_screen_id;
    }

    /**
     * nk画面遷移情報キーを取得
     * @return nk画面遷移情報キー
     */
    @Column(nullable = false, length = 38)
    public String getScreen_transition_key() {
        return this.screen_transition_key;
    }

    /**
     * nk画面遷移情報キーを設定
     * @param screen_transition_key nk画面遷移情報キー (null不可)
     */
    public void setScreen_transition_key(String screen_transition_key) {
        this.screen_transition_key = screen_transition_key;
    }

    /**
     * nkパラメーターを取得
     * @param screen_transition_key nkパラメーター (null可)
     */
    @Column(length = 40)
    public String getbookmark_parameter() {
		return bookmark_parameter;
	}

    /**
     * nkパラメーターを設定
     * @param parameter nkパラメーター (null可)
     */
	public void setbookmark_parameter(String bookmark_parameter) {
		this.bookmark_parameter = bookmark_parameter;
	}

	/**
     * DB領域区分を取得
     * @return DB領域区分
     */
    @Column(length = 1)
    public Integer getDb_area_div() {
        return this.db_area_div;
    }

    /**
     * DB領域区分を設定
     * @param db_area_div DB領域区分
     */
    public void setDb_area_div(Integer db_area_div) {
        this.db_area_div = db_area_div;
    }

}
