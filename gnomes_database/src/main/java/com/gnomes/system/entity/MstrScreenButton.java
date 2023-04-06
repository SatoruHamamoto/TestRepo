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
 * Zm004画面ボタンマスタ エンティティ
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
@Table(name = "mstr_screen_button")
@NamedQueries({
        @NamedQuery(name = "MstrScreenButton.findAll", query = "SELECT p FROM MstrScreenButton p"),
        @NamedQuery(name = "MstrScreenButton.findByPK", query = "SELECT p FROM MstrScreenButton p WHERE p.screen_button_key = :screen_button_key")
})
public class MstrScreenButton extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_screen_button";

    /** 画面ボタンキー */
    public static final String COLUMN_NAME_SCREEN_BUTTON_KEY = "screen_button_key";

    /** nk画面ID */
    public static final String COLUMN_NAME_SCREEN_ID = "screen_id";

    /** nkボタンID */
    public static final String COLUMN_NAME_BUTTON_ID = "button_id";

    /** 権限ID */
    public static final String COLUMN_NAME_PRIVILEGE_ID = "privilege_id";

    /** 操作内容 */
    public static final String COLUMN_NAME_OPERATION_CONTENT = "operation_content";

    /** 確認ダイアログ表示フラグ */
    public static final String COLUMN_NAME_DISPLAY_CONFIRM_FLAG = "display_confirm_flag";

    /** 確認ダイアログ表示メッセージNo */
    public static final String COLUMN_NAME_CONFIRM_MESSAGE_NO = "confirm_message_no";

    /** データ入力時確認ダイアログ表示フラグ */
    public static final String COLUMN_NAME_DISPLAY_DISCARD_CHANGE_FLAG = "display_discard_change_flag";

    /** 完了ダイアログ表示フラグ */
    public static final String COLUMN_NAME_DISPLAY_FINISH_FLAG = "display_finish_flag";

    /** 完了メッセージNo */
    public static final String COLUMN_NAME_FINISH_MESSAGE_NO = "finish_message_no";

    /** 二重サブミットチェック実施フラグ */
    public static final String COLUMN_NAME_CHECK_DOUBLE_SUBMIT_FLAG = "check_double_submit_flag";

    /** 認証機能区分 */
    public static final String COLUMN_NAME_CERTIFICATE_FUNCTION_DIV = "certificate_function_div";

    /** ボタン表示区分  */
    public static final String COLUMN_NAME_DISPLAY_DIV = "display_div";

    /** 画面ボタンキー */
    private String screen_button_key;
    /** nk画面ID */
    private String screen_id;
    /** nkボタンID */
    private String button_id;
    /** 権限ID */
    private String privilege_id;
    /** 操作内容 */
    private String operation_content;
    /** 確認ダイアログ表示フラグ */
    private Integer display_confirm_flag;
    /** 確認ダイアログ表示メッセージNo */
    private String confirm_message_no;
    /** データ入力時確認ダイアログ表示フラグ */
    private Integer display_discard_change_flag;
    /** 完了ダイアログ表示フラグ */
    private Integer display_finish_flag;
    /** 完了メッセージNo */
    private String finish_message_no;
    /** 二重サブミットチェック実施フラグ */
    private Integer check_double_submit_flag;
    /** 認証機能区分 */
    private int certificate_function_div;
    /** ボタン表示区分 */
    private Integer display_div;


    /**
     * Zm004画面ボタンマスタエンティティ コンストラクタ
     */
    public MstrScreenButton() {
    }

    /**
     * Zm004画面ボタンマスタエンティティ コンストラクタ
     * @param screen_button_key 画面ボタンキー
     * @param screen_id nk画面ID
     * @param button_id nkボタンID
     * @param operation_content 操作内容
     * @param certificate_function_div 認証機能区分
     * @param display_div ボタン表示区分
     * @param version 更新バージョン
     */
    public MstrScreenButton(String screen_button_key, String screen_id, String button_id, String operation_content, int certificate_function_div, Integer display_div, int version) {
        this.screen_button_key = screen_button_key;
        this.screen_id = screen_id;
        this.button_id = button_id;
        this.operation_content = operation_content;
        this.certificate_function_div = certificate_function_div;
        this.display_div = display_div;
        super.setVersion(version);
    }

    /**
     * Zm004画面ボタンマスタエンティティ コンストラクタ
     * @param screen_button_key 画面ボタンキー
     * @param screen_id nk画面ID
     * @param button_id nkボタンID
     * @param privilege_id 権限ID
     * @param operation_content 操作内容
     * @param display_confirm_flag 確認ダイアログ表示フラグ
     * @param confirm_message_no 確認ダイアログ表示メッセージNo
     * @param display_discard_change_flag データ入力時確認ダイアログ表示フラグ
     * @param display_finish_flag 完了ダイアログ表示フラグ
     * @param finish_message_no 完了メッセージNo
     * @param check_double_submit_flag 二重サブミットチェック実施フラグ
     * @param certificate_function_div 認証機能区分
     * @param display_div ボタン表示区分
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
    public MstrScreenButton(String screen_button_key, String screen_id, String button_id, String privilege_id, String operation_content, Integer display_confirm_flag, String confirm_message_no, Integer display_discard_change_flag, Integer display_finish_flag, String finish_message_no, Integer check_double_submit_flag, int certificate_function_div, Integer display_div, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.screen_button_key = screen_button_key;
        this.screen_id = screen_id;
        this.button_id = button_id;
        this.privilege_id = privilege_id;
        this.operation_content = operation_content;
        this.display_confirm_flag = display_confirm_flag;
        this.confirm_message_no = confirm_message_no;
        this.display_discard_change_flag = display_discard_change_flag;
        this.display_finish_flag = display_finish_flag;
        this.finish_message_no = finish_message_no;
        this.check_double_submit_flag = check_double_submit_flag;
        this.certificate_function_div = certificate_function_div;
        this.display_div = display_div;
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
     * 画面ボタンキーを取得
     * @return 画面ボタンキー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getScreen_button_key() {
        return this.screen_button_key;
    }

    /**
     * 画面ボタンキーを設定
     * @param screen_button_key 画面ボタンキー (null不可)
     */
    public void setScreen_button_key(String screen_button_key) {
        this.screen_button_key = screen_button_key;
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
     * nkボタンIDを取得
     * @return nkボタンID
     */
    @Column(nullable = false, length = 32)
    public String getButton_id() {
        return this.button_id;
    }

    /**
     * nkボタンIDを設定
     * @param button_id nkボタンID (null不可)
     */
    public void setButton_id(String button_id) {
        this.button_id = button_id;
    }

    /**
     * 権限IDを取得
     * @return 権限ID
     */
    @Column(length = 32)
    public String getPrivilege_id() {
        return this.privilege_id;
    }

    /**
     * 権限IDを設定
     * @param privilege_id 権限ID
     */
    public void setPrivilege_id(String privilege_id) {
        this.privilege_id = privilege_id;
    }

    /**
     * 操作内容を取得
     * @return 操作内容
     */
    @Column(nullable = false, length = 40)
    public String getOperation_content() {
        return this.operation_content;
    }

    /**
     * 操作内容を設定
     * @param operation_content 操作内容 (null不可)
     */
    public void setOperation_content(String operation_content) {
        this.operation_content = operation_content;
    }

    /**
     * 確認ダイアログ表示フラグを取得
     * @return 確認ダイアログ表示フラグ
     */
    @Column(length = 1)
    public Integer getDisplay_confirm_flag() {
        return this.display_confirm_flag;
    }

    /**
     * 確認ダイアログ表示フラグを設定
     * @param display_confirm_flag 確認ダイアログ表示フラグ
     */
    public void setDisplay_confirm_flag(Integer display_confirm_flag) {
        this.display_confirm_flag = display_confirm_flag;
    }

    /**
     * 確認ダイアログ表示メッセージNoを取得
     * @return 確認ダイアログ表示メッセージNo
     */
    @Column(length = 40)
    public String getConfirm_message_no() {
        return this.confirm_message_no;
    }

    /**
     * 確認ダイアログ表示メッセージNoを設定
     * @param confirm_message_no 確認ダイアログ表示メッセージNo
     */
    public void setConfirm_message_no(String confirm_message_no) {
        this.confirm_message_no = confirm_message_no;
    }

    /**
     * データ入力時確認ダイアログ表示フラグを取得
     * @return データ入力時確認ダイアログ表示フラグ
     */
    @Column(length = 1)
    public Integer getDisplay_discard_change_flag() {
        return this.display_discard_change_flag;
    }

    /**
     * データ入力時確認ダイアログ表示フラグを設定
     * @param display_discard_change_flag データ入力時確認ダイアログ表示フラグ
     */
    public void setDisplay_discard_change_flag(Integer display_discard_change_flag) {
        this.display_discard_change_flag = display_discard_change_flag;
    }

    /**
     * 完了ダイアログ表示フラグを取得
     * @return 完了ダイアログ表示フラグ
     */
    @Column(length = 1)
    public Integer getDisplay_finish_flag() {
        return this.display_finish_flag;
    }

    /**
     * 完了ダイアログ表示フラグを設定
     * @param display_finish_flag 完了ダイアログ表示フラグ
     */
    public void setDisplay_finish_flag(Integer display_finish_flag) {
        this.display_finish_flag = display_finish_flag;
    }

    /**
     * 完了メッセージNoを取得
     * @return 完了メッセージNo
     */
    @Column(length = 40)
    public String getFinish_message_no() {
        return this.finish_message_no;
    }

    /**
     * 完了メッセージNoを設定
     * @param finish_message_no 完了メッセージNo
     */
    public void setFinish_message_no(String finish_message_no) {
        this.finish_message_no = finish_message_no;
    }

    /**
     * 二重サブミットチェック実施フラグを取得
     * @return 二重サブミットチェック実施フラグ
     */
    @Column(length = 1)
    public Integer getCheck_double_submit_flag() {
        return this.check_double_submit_flag;
    }

    /**
     * 二重サブミットチェック実施フラグを設定
     * @param check_double_submit_flag 二重サブミットチェック実施フラグ
     */
    public void setCheck_double_submit_flag(Integer check_double_submit_flag) {
        this.check_double_submit_flag = check_double_submit_flag;
    }

    /**
     * 認証機能区分を取得
     * @return 認証機能区分
     */
    @Column(nullable = false, length = 1)
    public int getCertificate_function_div() {
        return this.certificate_function_div;
    }

    /**
     * 認証機能区分を設定
     * @param certificate_function_div 認証機能区分 (null不可)
     */
    public void setCertificate_function_div(int certificate_function_div) {
        this.certificate_function_div = certificate_function_div;
    }

    /**
     * ボタン表示区分を取得
     * @return ボタン表示区分
     */
    public Integer getDisplay_div()
    {
        return display_div;
    }

    /**
     * ボタン表示区分を設定
     * @param display_div ボタン表示区分
     */
    public void setDisplay_div(Integer display_div)
    {
        this.display_div = display_div;
    }

}
