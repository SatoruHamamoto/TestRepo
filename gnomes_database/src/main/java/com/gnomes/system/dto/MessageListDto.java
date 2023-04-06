package com.gnomes.system.dto;

import java.util.Date;


/**
 * 関連テーブル：message DTO
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/05/08 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MessageListDto implements IMessageBaseDto {

    /** テーブル名 */
    public static final String TABLE_NAME = "message";

    /** メッセージキー */
    public static final String MESSAGE_KEY = "message_key";

    /** 発生日時 */
    public static final String OCCUR_DATE = "occur_date";

    /** 発生者ID */
    public static final String OCCUR_USER_ID = "occur_user_id";

    /** 発生者名 */
    public static final String OCCUR_USER_NAME = "occur_user_name";

    /** 発生元コンピュータ名 */
    public static final String ORIGIN_COMPUTER_NAME = "origin_computer_name";

    /** メッセージNo */
    public static final String MESSAGE_NO = "message_no";

    /** リソースID */
    public static final String RESOURCE_ID = "resource_id";

    /** カテゴリ */
    public static final String CATEGORY = "category";

    /** メッセージ重要度 */
    public static final String MESSAGE_LEVEL = "message_level";

    /** メッセージ発生元ソース */
    public static final String MESSAGE_ORIGIN_SOURCE = "message_origin_source";

    /** DB領域区分 */
    public static final String DB_AREA_DIV = "db_area_div";

    /** メッセージ補足情報１ */
    public static final String MESSAGE_PARAM1 = "message_param1";

    /** メッセージ補足情報２ */
    public static final String MESSAGE_PARAM2 = "message_param2";

    /** メッセージ補足情報３ */
    public static final String MESSAGE_PARAM3 = "message_param3";

    /** メッセージ補足情報４ */
    public static final String MESSAGE_PARAM4 = "message_param4";

    /** メッセージ補足情報５ */
    public static final String MESSAGE_PARAM5 = "message_param5";

    /** メッセージ補足情報６ */
    public static final String MESSAGE_PARAM6 = "message_param6";

    /** メッセージ補足情報７ */
    public static final String MESSAGE_PARAM7 = "message_param7";

    /** メッセージ補足情報８ */
    public static final String MESSAGE_PARAM8 = "message_param8";

    /** メッセージ補足情報９ */
    public static final String MESSAGE_PARAM9 = "message_param9";

    /** メッセージ補足情報１０ */
    public static final String MESSAGE_PARAM10 = "message_param10";

    /** 発生拠点コード */
    public static final String OCCUR_SITE_CODE = "occur_site_code";

    /** ガイダンスメッセージ文字列 */
    public static final String GUIDANCE_MSG = "guidance_msg";

    /** リンク情報 */
    public static final String LINK_INFO = "link_info";

    /** リンク名 */
    public static final String LINK_NAME = "link_name";

    /** メッセージキー */
    private String message_key;
    /** 発生日時 */
    private Date occur_date;
    /** 発生者ID */
    private String occur_user_id;
    /** 発生者名 */
    private String occur_user_name;
    /** 発生元コンピュータ名 */
    private String origin_computer_name;
    /** メッセージNo */
    private String message_no;
    /** リソースID */
    private String resource_id;
    /** カテゴリ */
    private Integer category;
    /** メッセージ重要度 */
    private Integer message_level;
    /** メッセージ発生元ソース */
    private String message_origin_source;
    /** DB領域区分 */
    private Integer db_area_div;
    /** メッセージ補足情報１ */
    private String message_param1;
    /** メッセージ補足情報２ */
    private String message_param2;
    /** メッセージ補足情報３ */
    private String message_param3;
    /** メッセージ補足情報４ */
    private String message_param4;
    /** メッセージ補足情報５ */
    private String message_param5;
    /** メッセージ補足情報６ */
    private String message_param6;
    /** メッセージ補足情報７ */
    private String message_param7;
    /** メッセージ補足情報８ */
    private String message_param8;
    /** メッセージ補足情報９ */
    private String message_param9;
    /** メッセージ補足情報１０ */
    private String message_param10;
    /** 発生拠点コード */
    private String occur_site_code;
    /** ガイダンスメッセージ文字列 */
    private String guidance_msg;
    /** リンク情報 */
    private String link_info;
    /** リンク名 */
    private String link_name;

    /**
     * MessageListDto・コンストラクタ
     */
    public MessageListDto() {
    }

    /**
     * MessageListDto・コンストラクタ
     * @param message_key メッセージキー
     * @param occur_date 発生日時
     * @param occur_user_id 発生者ID
     * @param occur_user_name 発生者名
     * @param origin_computer_name 発生元コンピュータ名
     * @param message_no メッセージNo
     * @param resource_id リソースID
     * @param category カテゴリ
     * @param message_level メッセージ重要度
     * @param message_origin_source メッセージ発生元ソース
     * @param db_area_div DB領域区分
     * @param message_param1 メッセージ補足情報１
     * @param message_param2 メッセージ補足情報２
     * @param message_param3 メッセージ補足情報３
     * @param message_param4 メッセージ補足情報４
     * @param message_param5 メッセージ補足情報５
     * @param message_param6 メッセージ補足情報６
     * @param message_param7 メッセージ補足情報７
     * @param message_param8 メッセージ補足情報８
     * @param message_param9 メッセージ補足情報９
     * @param message_param10 メッセージ補足情報１０
     * @param occur_site_code 発生拠点コード
     * @param guidance_msg ガイダンスメッセージ文字列
     * @param link_info リンク情報
     * @param link_name リンク名
     */
    public MessageListDto(String message_key, Date occur_date, String occur_user_id, String occur_user_name, String origin_computer_name, String message_no, String resource_id, Integer category, Integer message_level, String message_origin_source, Integer db_area_div, String message_param1, String message_param2, String message_param3, String message_param4, String message_param5, String message_param6, String message_param7, String message_param8, String message_param9, String message_param10, String occur_site_code, String guidance_msg, String link_info, String link_name) {
        super();
        this.message_key = message_key;
        this.occur_date = occur_date;
        this.occur_user_id = occur_user_id;
        this.occur_user_name = occur_user_name;
        this.origin_computer_name = origin_computer_name;
        this.message_no = message_no;
        this.resource_id = resource_id;
        this.category = category;
        this.message_level = message_level;
        this.message_origin_source = message_origin_source;
        this.db_area_div = db_area_div;
        this.message_param1 = message_param1;
        this.message_param2 = message_param2;
        this.message_param3 = message_param3;
        this.message_param4 = message_param4;
        this.message_param5 = message_param5;
        this.message_param6 = message_param6;
        this.message_param7 = message_param7;
        this.message_param8 = message_param8;
        this.message_param9 = message_param9;
        this.message_param10 = message_param10;
        this.occur_site_code = occur_site_code;
        this.guidance_msg = guidance_msg;
        this.link_info = link_info;
        this.link_name = link_name;
    }

    /**
     * メッセージキーを取得
     * @return メッセージキー
     */
    public String getMessage_key() {
        return this.message_key;
    }

    /**
     * メッセージキーを設定
     * @param message_key メッセージキー
     */
    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    /**
     * 発生日時を取得
     * @return 発生日時
     */
    public Date getOccur_date() {
        return this.occur_date;
    }

    /**
     * 発生日時を設定
     * @param occur_date 発生日時
     */
    public void setOccur_date(Date occur_date) {
        this.occur_date = occur_date;
    }

    /**
     * 発生者IDを取得
     * @return 発生者ID
     */
    public String getOccur_user_id() {
        return this.occur_user_id;
    }

    /**
     * 発生者IDを設定
     * @param occur_user_id 発生者ID
     */
    public void setOccur_user_id(String occur_user_id) {
        this.occur_user_id = occur_user_id;
    }

    /**
     * 発生者名を取得
     * @return 発生者名
     */
    public String getOccur_user_name() {
        return this.occur_user_name;
    }

    /**
     * 発生者名を設定
     * @param occur_user_name 発生者名
     */
    public void setOccur_user_name(String occur_user_name) {
        this.occur_user_name = occur_user_name;
    }

    /**
     * 発生元コンピュータ名を取得
     * @return 発生元コンピュータ名
     */
    public String getOrigin_computer_name() {
        return this.origin_computer_name;
    }

    /**
     * 発生元コンピュータ名を設定
     * @param origin_computer_name 発生元コンピュータ名
     */
    public void setOrigin_computer_name(String origin_computer_name) {
        this.origin_computer_name = origin_computer_name;
    }

    /**
     * メッセージNoを取得
     * @return メッセージNo
     */
    public String getMessage_no() {
        return this.message_no;
    }

    /**
     * メッセージNoを設定
     * @param message_no メッセージNo
     */
    public void setMessage_no(String message_no) {
        this.message_no = message_no;
    }

    /**
     * リソースIDを取得
     * @return リソースID
     */
    public String getResource_id() {
        return this.resource_id;
    }

    /**
     * リソースIDを設定
     * @param resource_id リソースID
     */
    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
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
     * メッセージ重要度を取得
     * @return メッセージ重要度
     */
    public Integer getMessage_level() {
        return this.message_level;
    }

    /**
     * メッセージ重要度を設定
     * @param message_level メッセージ重要度
     */
    public void setMessage_level(Integer message_level) {
        this.message_level = message_level;
    }

    /**
     * メッセージ発生元ソースを取得
     * @return メッセージ発生元ソース
     */
    public String getMessage_origin_source() {
        return this.message_origin_source;
    }

    /**
     * メッセージ発生元ソースを設定
     * @param message_origin_source メッセージ発生元ソース
     */
    public void setMessage_origin_source(String message_origin_source) {
        this.message_origin_source = message_origin_source;
    }

    /**
     * DB領域区分を取得
     * @return DB領域区分
     */
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

    /**
     * メッセージ補足情報１を取得
     * @return メッセージ補足情報１
     */
    public String getMessage_param1() {
        return this.message_param1;
    }

    /**
     * メッセージ補足情報１を設定
     * @param message_param1 メッセージ補足情報１
     */
    public void setMessage_param1(String message_param1) {
        this.message_param1 = message_param1;
    }

    /**
     * メッセージ補足情報２を取得
     * @return メッセージ補足情報２
     */
    public String getMessage_param2() {
        return this.message_param2;
    }

    /**
     * メッセージ補足情報２を設定
     * @param message_param2 メッセージ補足情報２
     */
    public void setMessage_param2(String message_param2) {
        this.message_param2 = message_param2;
    }

    /**
     * メッセージ補足情報３を取得
     * @return メッセージ補足情報３
     */
    public String getMessage_param3() {
        return this.message_param3;
    }

    /**
     * メッセージ補足情報３を設定
     * @param message_param3 メッセージ補足情報３
     */
    public void setMessage_param3(String message_param3) {
        this.message_param3 = message_param3;
    }

    /**
     * メッセージ補足情報４を取得
     * @return メッセージ補足情報４
     */
    public String getMessage_param4() {
        return this.message_param4;
    }

    /**
     * メッセージ補足情報４を設定
     * @param message_param4 メッセージ補足情報４
     */
    public void setMessage_param4(String message_param4) {
        this.message_param4 = message_param4;
    }

    /**
     * メッセージ補足情報５を取得
     * @return メッセージ補足情報５
     */
    public String getMessage_param5() {
        return this.message_param5;
    }

    /**
     * メッセージ補足情報５を設定
     * @param message_param5 メッセージ補足情報５
     */
    public void setMessage_param5(String message_param5) {
        this.message_param5 = message_param5;
    }

    /**
     * メッセージ補足情報６を取得
     * @return メッセージ補足情報６
     */
    public String getMessage_param6() {
        return this.message_param6;
    }

    /**
     * メッセージ補足情報６を設定
     * @param message_param6 メッセージ補足情報６
     */
    public void setMessage_param6(String message_param6) {
        this.message_param6 = message_param6;
    }

    /**
     * メッセージ補足情報７を取得
     * @return メッセージ補足情報７
     */
    public String getMessage_param7() {
        return this.message_param7;
    }

    /**
     * メッセージ補足情報７を設定
     * @param message_param7 メッセージ補足情報７
     */
    public void setMessage_param7(String message_param7) {
        this.message_param7 = message_param7;
    }

    /**
     * メッセージ補足情報８を取得
     * @return メッセージ補足情報８
     */
    public String getMessage_param8() {
        return this.message_param8;
    }

    /**
     * メッセージ補足情報８を設定
     * @param message_param8 メッセージ補足情報８
     */
    public void setMessage_param8(String message_param8) {
        this.message_param8 = message_param8;
    }

    /**
     * メッセージ補足情報９を取得
     * @return メッセージ補足情報９
     */
    public String getMessage_param9() {
        return this.message_param9;
    }

    /**
     * メッセージ補足情報９を設定
     * @param message_param9 メッセージ補足情報９
     */
    public void setMessage_param9(String message_param9) {
        this.message_param9 = message_param9;
    }

    /**
     * メッセージ補足情報１０を取得
     * @return メッセージ補足情報１０
     */
    public String getMessage_param10() {
        return this.message_param10;
    }

    /**
     * メッセージ補足情報１０を設定
     * @param message_param10 メッセージ補足情報１０
     */
    public void setMessage_param10(String message_param10) {
        this.message_param10 = message_param10;
    }

    /**
     * 発生拠点コードを取得
     * @return 発生拠点コード
     */
    public String getOccur_site_code() {
        return this.occur_site_code;
    }

    /**
     * 発生拠点コードを設定
     * @param occur_site_code 発生拠点コード
     */
    public void setOccur_site_code(String occur_site_code) {
        this.occur_site_code = occur_site_code;
    }

    /**
     * ガイダンスメッセージ文字列を取得
     * @return ガイダンスメッセージ文字列
     */
    public String getGuidance_msg() {
        return this.guidance_msg;
    }

    /**
     * ガイダンスメッセージ文字列を設定
     * @param guidance_msg ガイダンスメッセージ文字列
     */
    public void setGuidance_msg(String guidance_msg) {
        this.guidance_msg = guidance_msg;
    }

    /**
     * リンク情報を取得
     * @return リンク情報
     */
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
     * リンク名を取得
     * @return リンク名
     */
    public String getLink_name() {
        return this.link_name;
    }

    /**
     * リンク名を設定
     * @param link_name リンク名
     */
    public void setLink_name(String link_name) {
        this.link_name = link_name;
    }

}
