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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zr010メッセージ情報 エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/06/04 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "message")
@NamedQueries({
        @NamedQuery(name = "Message.findAll", query = "SELECT p FROM Message p"),
        @NamedQuery(name = "Message.findByPK", query = "SELECT p FROM Message p WHERE p.message_key = :message_key")
})
public class Message extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "message";

    /** メッセージキー */
    public static final String COLUMN_NAME_MESSAGE_KEY = "message_key";

    /** 発生日時 */
    public static final String COLUMN_NAME_OCCUR_DATE = "occur_date";

    /** 発生者ID */
    public static final String COLUMN_NAME_OCCUR_USER_ID = "occur_user_id";

    /** 発生者名 */
    public static final String COLUMN_NAME_OCCUR_USER_NAME = "occur_user_name";

    /** 発生元コンピュータID */
    public static final String COLUMN_NAME_ORIGIN_COMPUTER_ID = "origin_computer_id";

    /** 発生元コンピュータ名 */
    public static final String COLUMN_NAME_ORIGIN_COMPUTER_NAME = "origin_computer_name";

    /** 発生元IPアドレス クライアント */
    public static final String COLUMN_NAME_ORIJIN_IP_ADDRESS = "origin_ip_address";

    /** 発生元画面（機能）ID */
    public static final String COLUMN_NAME_ORIGIN_SCREEN_ID = "origin_screen_id";

    /** 発生元画面（機能）名 */
    public static final String COLUMN_NAME_ORIGIN_SCREEN_NAME = "origin_screen_name";

    /** 発生元ボタンID */
    public static final String COLUMN_NAME_ORIGIN_BUTTON_ID = "origin_button_id";

    /** 発生元ボタン操作内容 */
    public static final String COLUMN_NAME_ORIGIN_BUTTON_OPERATION_CONTENT = "origin_button_operation_content";

    /** 発生元コマンドID */
    public static final String COLUMN_NAME_ORIGIN_COMMAND_ID = "origin_command_id";

    /** 発生元コマンド名 */
    public static final String COLUMN_NAME_ORIGIN_COMMAND_NAME = "origin_command_name";

    /** 発生拠点コード */
    public static final String COLUMN_NAME_OCCUR_SITE_CODE = "occur_site_code";

    /** メッセージNo */
    public static final String COLUMN_NAME_MESSAGE_NO = "message_no";

    /** リソースID */
    public static final String COLUMN_NAME_RESOURCE_ID = "resource_id";

    /** カテゴリ */
    public static final String COLUMN_NAME_CATEGORY = "category";

    /** メッセージ重要度 */
    public static final String COLUMN_NAME_MESSAGE_LEVEL = "message_level";

    /** メッセージ補足情報１ */
    public static final String COLUMN_NAME_MESSAGE_PARAM1 = "message_param1";

    /** メッセージ補足情報２ */
    public static final String COLUMN_NAME_MESSAGE_PARAM2 = "message_param2";

    /** メッセージ補足情報３ */
    public static final String COLUMN_NAME_MESSAGE_PARAM3 = "message_param3";

    /** メッセージ補足情報４ */
    public static final String COLUMN_NAME_MESSAGE_PARAM4 = "message_param4";

    /** メッセージ補足情報５ */
    public static final String COLUMN_NAME_MESSAGE_PARAM5 = "message_param5";

    /** メッセージ補足情報６ */
    public static final String COLUMN_NAME_MESSAGE_PARAM6 = "message_param6";

    /** メッセージ補足情報７ */
    public static final String COLUMN_NAME_MESSAGE_PARAM7 = "message_param7";

    /** メッセージ補足情報８ */
    public static final String COLUMN_NAME_MESSAGE_PARAM8 = "message_param8";

    /** メッセージ補足情報９ */
    public static final String COLUMN_NAME_MESSAGE_PARAM9 = "message_param9";

    /** メッセージ補足情報１０ */
    public static final String COLUMN_NAME_MESSAGE_PARAM10 = "message_param10";

    /** 操作区分 */
    public static final String COLUMN_NAME_OPERATION_TYPE = "operation_type";

    /** DB領域区分 */
    public static final String COLUMN_NAME_DB_AREA_DIV = "db_area_div";

    /** 監視機能用Key */
    public static final String COLUMN_NAME_WATCHER_SEARCH_KEY = "watcher_search_key";

    /** メッセージコード */
    public static final String COLUMN_NAME_MESSAGE_CODE = "message_code";

    /** メッセージキー名 */
    public static final String COLUMN_NAME_MESSAGE_KEY_NAME = "message_key_name";

    /** メッセージ発生元ソース */
    public static final String COLUMN_NAME_MESSAGE_ORIGIN_SOURCE = "message_origin_source";

    /** Talendジョブ名 */
    public static final String COLUMN_NAME_TALEND_JOB_NAME = "talend_job_name";

    /** Talendコンテキストパラメータ */
    public static final String COLUMN_NAME_TALEND_CONTEXT_PARAM = "talend_context_param";

    /** メッセージタイトルリソースID */
    public static final String COLUMN_NAME_MESSAGE_TITLE_RESOURCE_ID = "message_title_resource_id";

    /** メッセージ本文リソースID */
    public static final String COLUMN_NAME_MESSAGE_TEXT_RESOURCE_ID = "message_text_resource_id";

    /** ガイダンスメッセージ文字列 */
    public static final String COLUMN_NAME_GUIDANCE_MESSAGE = "guidance_message";

    /** リンク情報 */
    public static final String COLUMN_NAME_LINK_INFO = "link_info";

    /** リンク名 */
    public static final String COLUMN_NAME_LINK_NAME = "link_name";

    /** メッセージキー */
    private String message_key;
    /** 発生日時 */
    private Date occur_date;
    /** 発生者ID */
    private String occur_user_id;
    /** 発生者名 */
    private String occur_user_name;
    /** 発生元コンピュータID */
    private String origin_computer_id;
    /** 発生元コンピュータ名 */
    private String origin_computer_name;
    /** 発生元IPアドレス クライアント */
    private String origin_ip_address;
    /** 発生元画面（機能）ID */
    private String origin_screen_id;
    /** 発生元画面（機能）名 */
    private String origin_screen_name;
    /** 発生元ボタンID */
    private String origin_button_id;
    /** 発生元ボタン操作内容 */
    private String origin_button_operation_content;
    /** 発生元コマンドID */
    private String origin_command_id;
    /** 発生元コマンド名 */
    private String origin_command_name;
    /** 発生拠点コード */
    private String occur_site_code;
    /** メッセージNo */
    private String message_no;
    /** リソースID */
    private String resource_id;
    /** カテゴリ */
    private Integer category;
    /** メッセージ重要度 */
    private Integer message_level;
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
    /** 操作区分 */
    private String operation_type;
    /** DB領域区分 */
    private Integer db_area_div;
    /** 監視機能用Key */
    private String watcher_search_key;
    /** メッセージコード */
    private String message_code;
    /** メッセージキー名 */
    private String message_key_name;
    /** メッセージ発生元ソース */
    private String message_origin_source;
    /** Talendジョブ名 */
    private String talend_job_name;
    /** Talendコンテキストパラメータ */
    private String talend_context_param;
    /** メッセージタイトルリソースID */
    private String message_title_resource_id;
    /** メッセージ本文リソースID */
    private String message_text_resource_id;
    /** ガイダンスメッセージ文字列 */
    private String guidance_message;
    /** リンク情報 */
    private String link_info;
    /** リンク名 */
    private String link_name;


    /**
     * Zr010メッセージ情報エンティティ コンストラクタ
     */
    public Message() {
    }

    /**
     * Zr010メッセージ情報エンティティ コンストラクタ
     * @param message_key メッセージキー
     * @param occur_date 発生日時
     * @param version 更新バージョン
     */
    public Message(String message_key, Date occur_date, int version) {
        this.message_key = message_key;
        this.occur_date = occur_date;
        super.setVersion(version);
    }

    /**
     * Zr010メッセージ情報エンティティ コンストラクタ
     * @param message_key メッセージキー
     * @param occur_date 発生日時
     * @param occur_user_id 発生者ID
     * @param occur_user_name 発生者名
     * @param origin_computer_id 発生元コンピュータID
     * @param origin_computer_name 発生元コンピュータ名
     * @param origin_ip_address 発生元IPアドレス クライアント
     * @param origin_screen_id 発生元画面（機能）ID
     * @param origin_screen_name 発生元画面（機能）名
     * @param origin_button_id 発生元ボタンID
     * @param origin_button_operation_content 発生元ボタン操作内容
     * @param origin_command_id 発生元コマンドID
     * @param origin_command_name 発生元コマンド名
     * @param occur_site_code 発生拠点コード
     * @param message_no メッセージNo
     * @param resource_id リソースID
     * @param category カテゴリ
     * @param message_level メッセージ重要度
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
     * @param operation_type 操作区分
     * @param db_area_div DB領域区分
     * @param watcher_search_key 監視機能用Key
     * @param message_code メッセージコード
     * @param message_key_name メッセージキー名
     * @param message_origin_source メッセージ発生元ソース
     * @param talend_job_name Talendジョブ名
     * @param talend_context_param Talendコンテキストパラメータ
     * @param message_title_resource_id メッセージタイトルリソースID
     * @param message_text_resource_id メッセージ本文リソースID
     * @param guidance_message ガイダンスメッセージ文字列
     * @param link_info リンク情報
     * @param link_name リンク名
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
    public Message(String message_key, Date occur_date, String occur_user_id, String occur_user_name, String origin_computer_id, String origin_computer_name, String origin_ip_address, String origin_screen_id, String origin_screen_name, String origin_button_id, String origin_button_operation_content, String origin_command_id, String origin_command_name, String occur_site_code, String message_no, String resource_id, Integer category, Integer message_level, String message_param1, String message_param2, String message_param3, String message_param4, String message_param5, String message_param6, String message_param7, String message_param8, String message_param9, String message_param10, String operation_type, Integer db_area_div, String watcher_search_key, String message_code, String message_key_name, String message_origin_source, String talend_job_name, String talend_context_param, String message_title_resource_id, String message_text_resource_id, String guidance_message, String link_info, String link_name, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.message_key = message_key;
        this.occur_date = occur_date;
        this.occur_user_id = occur_user_id;
        this.occur_user_name = occur_user_name;
        this.origin_computer_id = origin_computer_id;
        this.origin_computer_name = origin_computer_name;
        this.origin_ip_address = origin_ip_address;
        this.origin_screen_id = origin_screen_id;
        this.origin_screen_name = origin_screen_name;
        this.origin_button_id = origin_button_id;
        this.origin_button_operation_content = origin_button_operation_content;
        this.origin_command_id = origin_command_id;
        this.origin_command_name = origin_command_name;
        this.occur_site_code = occur_site_code;
        this.message_no = message_no;
        this.resource_id = resource_id;
        this.category = category;
        this.message_level = message_level;
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
        this.operation_type = operation_type;
        this.db_area_div = db_area_div;
        this.watcher_search_key = watcher_search_key;
        this.message_code = message_code;
        this.message_key_name = message_key_name;
        this.message_origin_source = message_origin_source;
        this.talend_job_name = talend_job_name;
        this.talend_context_param = talend_context_param;
        this.message_title_resource_id = message_title_resource_id;
        this.message_text_resource_id = message_text_resource_id;
        this.guidance_message = guidance_message;
        this.link_info = link_info;
        this.link_name = link_name;
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
     * メッセージキーを取得
     * @return メッセージキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getMessage_key() {
        return this.message_key;
    }

    /**
     * メッセージキーを設定
     * @param message_key メッセージキー (null不可)
     */
    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    /**
     * 発生日時を取得
     * @return 発生日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getOccur_date() {
        return this.occur_date;
    }

    /**
     * 発生日時を設定
     * @param occur_date 発生日時 (null不可)
     */
    public void setOccur_date(Date occur_date) {
        this.occur_date = occur_date;
    }

    /**
     * 発生者IDを取得
     * @return 発生者ID
     */
    @Column(length = 20)
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
    @Column(length = 40)
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
     * 発生元コンピュータIDを取得
     * @return 発生元コンピュータID
     */
    @Column(length = 20)
    public String getOrigin_computer_id() {
        return this.origin_computer_id;
    }

    /**
     * 発生元コンピュータIDを設定
     * @param origin_computer_id 発生元コンピュータID
     */
    public void setOrigin_computer_id(String origin_computer_id) {
        this.origin_computer_id = origin_computer_id;
    }

    /**
     * 発生元コンピュータ名を取得
     * @return 発生元コンピュータ名
     */
    @Column(length = 40)
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
     * 発生元IPアドレス クライアントを取得
     * @return 発生元IPアドレス クライアント
     */
    @Column(length = 40)
    public String getOrigin_ip_address() {
        return this.origin_ip_address;
    }

    /**
     * 発生元IPアドレス クライアントを設定
     * @param origin_ip_address 発生元IPアドレス クライアント
     */
    public void setOrigin_ip_address(String origin_ip_address) {
        this.origin_ip_address = origin_ip_address;
    }

    /**
     * 発生元画面（機能）IDを取得
     * @return 発生元画面（機能）ID
     */
    @Column(length = 12)
    public String getOrigin_screen_id() {
        return this.origin_screen_id;
    }

    /**
     * 発生元画面（機能）IDを設定
     * @param origin_screen_id 発生元画面（機能）ID
     */
    public void setOrigin_screen_id(String origin_screen_id) {
        this.origin_screen_id = origin_screen_id;
    }

    /**
     * 発生元画面（機能）名を取得
     * @return 発生元画面（機能）名
     */
    @Column(length = 40)
    public String getOrigin_screen_name() {
        return this.origin_screen_name;
    }

    /**
     * 発生元画面（機能）名を設定
     * @param origin_screen_name 発生元画面（機能）名
     */
    public void setOrigin_screen_name(String origin_screen_name) {
        this.origin_screen_name = origin_screen_name;
    }

    /**
     * 発生元ボタンIDを取得
     * @return 発生元ボタンID
     */
    @Column(length = 32)
    public String getOrigin_button_id() {
        return this.origin_button_id;
    }

    /**
     * 発生元ボタンIDを設定
     * @param origin_button_id 発生元ボタンID
     */
    public void setOrigin_button_id(String origin_button_id) {
        this.origin_button_id = origin_button_id;
    }

    /**
     * 発生元ボタン操作内容を取得
     * @return 発生元ボタン操作内容
     */
    @Column(length = 40)
    public String getOrigin_button_operation_content() {
        return this.origin_button_operation_content;
    }

    /**
     * 発生元ボタン操作内容を設定
     * @param origin_button_operation_content 発生元ボタン操作内容
     */
    public void setOrigin_button_operation_content(String origin_button_operation_content) {
        this.origin_button_operation_content = origin_button_operation_content;
    }

    /**
     * 発生元コマンドIDを取得
     * @return 発生元コマンドID
     */
    @Column(length = 20)
    public String getOrigin_command_id() {
        return this.origin_command_id;
    }

    /**
     * 発生元コマンドIDを設定
     * @param origin_command_id 発生元コマンドID
     */
    public void setOrigin_command_id(String origin_command_id) {
        this.origin_command_id = origin_command_id;
    }

    /**
     * 発生元コマンド名を取得
     * @return 発生元コマンド名
     */
    @Column(length = 40)
    public String getOrigin_command_name() {
        return this.origin_command_name;
    }

    /**
     * 発生元コマンド名を設定
     * @param origin_command_name 発生元コマンド名
     */
    public void setOrigin_command_name(String origin_command_name) {
        this.origin_command_name = origin_command_name;
    }

    /**
     * 発生拠点コードを取得
     * @return 発生拠点コード
     */
    @Column(length = 20)
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
     * メッセージNoを取得
     * @return メッセージNo
     */
    @Column(length = 40)
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
    @Column(length = 40)
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
    @Column(length = 2)
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
    @Column(length = 2)
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
     * メッセージ補足情報１を取得
     * @return メッセージ補足情報１
     */
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
    @Column(length = 2000)
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
     * 操作区分を取得
     * @return 操作区分
     */
    @Column(length = 2)
    public String getOperation_type() {
        return this.operation_type;
    }

    /**
     * 操作区分を設定
     * @param operation_type 操作区分
     */
    public void setOperation_type(String operation_type) {
        this.operation_type = operation_type;
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

    /**
     * 監視機能用Keyを取得
     * @return 監視機能用Key
     */
    @Column(length = 200)
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

    /**
     * メッセージコードを取得
     * @return メッセージコード
     */
    @Column(length = 20)
    public String getMessage_code() {
        return this.message_code;
    }

    /**
     * メッセージコードを設定
     * @param message_code メッセージコード
     */
    public void setMessage_code(String message_code) {
        this.message_code = message_code;
    }

    /**
     * メッセージキー名を取得
     * @return メッセージキー名
     */
    @Column(length = 40)
    public String getMessage_key_name() {
        return this.message_key_name;
    }

    /**
     * メッセージキー名を設定
     * @param message_key_name メッセージキー名
     */
    public void setMessage_key_name(String message_key_name) {
        this.message_key_name = message_key_name;
    }

    /**
     * メッセージ発生元ソースを取得
     * @return メッセージ発生元ソース
     */
    @Column(length = 256)
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
     * Talendジョブ名を取得
     * @return Talendジョブ名
     */
    @Column(length = 100)
    public String getTalend_job_name() {
        return this.talend_job_name;
    }

    /**
     * Talendジョブ名を設定
     * @param talend_job_name Talendジョブ名
     */
    public void setTalend_job_name(String talend_job_name) {
        this.talend_job_name = talend_job_name;
    }

    /**
     * Talendコンテキストパラメータを取得
     * @return Talendコンテキストパラメータ
     */
    @Column(length = 1000)
    public String getTalend_context_param() {
        return this.talend_context_param;
    }

    /**
     * Talendコンテキストパラメータを設定
     * @param talend_context_param Talendコンテキストパラメータ
     */
    public void setTalend_context_param(String talend_context_param) {
        this.talend_context_param = talend_context_param;
    }

    /**
     * メッセージタイトルリソースIDを取得
     * @return メッセージタイトルリソースID
     */
    @Column(length = 40)
    public String getMessage_title_resource_id() {
        return this.message_title_resource_id;
    }

    /**
     * メッセージタイトルリソースIDを設定
     * @param message_title_resource_id メッセージタイトルリソースID
     */
    public void setMessage_title_resource_id(String message_title_resource_id) {
        this.message_title_resource_id = message_title_resource_id;
    }

    /**
     * メッセージ本文リソースIDを取得
     * @return メッセージ本文リソースID
     */
    @Column(length = 40)
    public String getMessage_text_resource_id() {
        return this.message_text_resource_id;
    }

    /**
     * メッセージ本文リソースIDを設定
     * @param message_text_resource_id メッセージ本文リソースID
     */
    public void setMessage_text_resource_id(String message_text_resource_id) {
        this.message_text_resource_id = message_text_resource_id;
    }

    /**
     * ガイダンスメッセージ文字列を取得
     * @return ガイダンスメッセージ文字列
     */
    @Column(length = 2000)
    public String getGuidance_message() {
        return this.guidance_message;
    }

    /**
     * ガイダンスメッセージ文字列を設定
     * @param guidance_message ガイダンスメッセージ文字列
     */
    public void setGuidance_message(String guidance_message) {
        this.guidance_message = guidance_message;
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
     * リンク名を取得
     * @return リンク名
     */
    @Column(length = 2000)
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
