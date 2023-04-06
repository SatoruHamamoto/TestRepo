package com.gnomes.system.dto;



/**
 * 関連テーブル：message DTO
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/06/11 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MessageWatcherSearchListDto {

    /** テーブル名 */
    public static final String TABLE_NAME = "message";

    /** メッセージNO */
    public static final String MESSAGE_NO = "message_no";

    /** カテゴリ */
    public static final String CATEGORY = "category";

    /** 監視機能用Key */
    public static final String WATCHER_SEARCH_KEY = "watcher_search_key";

    /** メッセージNO */
    private String message_no;
    /** カテゴリ */
    private Integer category;
    /** 監視機能用Key */
    private String watcher_search_key;

    /**
     * MessageWatcherSearchListDto・コンストラクタ
     */
    public MessageWatcherSearchListDto() {
    }

    /**
     * MessageWatcherSearchListDto・コンストラクタ
     * @param message_no メッセージNO
     * @param category カテゴリ
     * @param watcher_search_key 監視機能用Key
     */
    public MessageWatcherSearchListDto(String message_no, Integer category, String watcher_search_key) {
        super();
        this.message_no = message_no;
        this.category = category;
        this.watcher_search_key = watcher_search_key;
    }

    /**
     * メッセージNOを取得
     * @return メッセージNO
     */
    public String getMessage_no() {
        return this.message_no;
    }

    /**
     * メッセージNOを設定
     * @param message_no メッセージNO
     */
    public void setMessage_no(String message_no) {
        this.message_no = message_no;
    }

    /**
     * カテゴリを取得
     * @return カテゴリ
     */
    public Integer getCategory() {
        return this.category;
    }

    /**
     * カテゴリを設定
     * @param category カテゴリ
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * 監視機能用Keyを取得
     * @return 監視機能用Key
     */
    public String getWatcher_search_key() {
        return this.watcher_search_key;
    }

    /**
     * 監視機能用Keyを設定
     * @param watcher_search_key 監視機能用Key
     */
    public void setWatcher_search_key(String watcher_search_key) {
        this.watcher_search_key = watcher_search_key;
    }

}
