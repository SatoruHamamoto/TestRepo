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
 * Zm002端末定義マスタ エンティティ
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
@Table(name = "mstr_computer")
@NamedQueries({
        @NamedQuery(name = "MstrComputer.findAll", query = "SELECT p FROM MstrComputer p"),
        @NamedQuery(name = "MstrComputer.findByPK", query = "SELECT p FROM MstrComputer p WHERE p.computer_key = :computer_key")
})
public class MstrComputer extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "mstr_computer";

    /** 端末定義キー */
    public static final String COLUMN_NAME_COMPUTER_KEY = "computer_key";

    /** nk端末ID */
    public static final String COLUMN_NAME_COMPUTER_ID = "computer_id";

    /** 端末名 */
    public static final String COLUMN_NAME_COMPUTER_NAME = "computer_name";

    /** 説明 */
    public static final String COLUMN_NAME_EXPLANATION = "explanation";

    /** 管理端末で選択可能フラグ */
    public static final String COLUMN_NAME_MANAGE_SELECTABLE_FLAG = "manage_selectable_flag";

    /** 工程端末で選択可能フラグ */
    public static final String COLUMN_NAME_OPERATE_SELECTABLE_FLAG = "operate_selectable_flag";

    /** 端末定義キー */
    private String computer_key;
    /** nk端末ID */
    private String computer_id;
    /** 端末名 */
    private String computer_name;
    /** 説明 */
    private String explanation;
    /** 管理端末で選択可能フラグ */
    private int manage_selectable_flag;
    /** 工程端末で選択可能フラグ */
    private int operate_selectable_flag;


    /**
     * Zm002端末定義マスタエンティティ コンストラクタ
     */
    public MstrComputer() {
    }

    /**
     * Zm002端末定義マスタエンティティ コンストラクタ
     * @param computer_key 端末定義キー
     * @param computer_id nk端末ID
     * @param computer_name 端末名
     * @param manage_selectable_flag 管理端末で選択可能フラグ
     * @param operate_selectable_flag 工程端末で選択可能フラグ
     * @param version 更新バージョン
     */
    public MstrComputer(String computer_key, String computer_id, String computer_name, int manage_selectable_flag, int operate_selectable_flag, int version) {
        this.computer_key = computer_key;
        this.computer_id = computer_id;
        this.computer_name = computer_name;
        this.manage_selectable_flag = manage_selectable_flag;
        this.operate_selectable_flag = operate_selectable_flag;
        super.setVersion(version);
    }

    /**
     * Zm002端末定義マスタエンティティ コンストラクタ
     * @param computer_key 端末定義キー
     * @param computer_id nk端末ID
     * @param computer_name 端末名
     * @param explanation 説明
     * @param manage_selectable_flag 管理端末で選択可能フラグ
     * @param operate_selectable_flag 工程端末で選択可能フラグ
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
    public MstrComputer(String computer_key, String computer_id, String computer_name, String explanation, int manage_selectable_flag, int operate_selectable_flag, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.computer_key = computer_key;
        this.computer_id = computer_id;
        this.computer_name = computer_name;
        this.explanation = explanation;
        this.manage_selectable_flag = manage_selectable_flag;
        this.operate_selectable_flag = operate_selectable_flag;
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
     * 端末定義キーを取得
     * @return 端末定義キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getComputer_key() {
        return this.computer_key;
    }

    /**
     * 端末定義キーを設定
     * @param computer_key 端末定義キー (null不可)
     */
    public void setComputer_key(String computer_key) {
        this.computer_key = computer_key;
    }

    /**
     * nk端末IDを取得
     * @return nk端末ID
     */
    @Column(nullable = false, length = 20)
    public String getComputer_id() {
        return this.computer_id;
    }

    /**
     * nk端末IDを設定
     * @param computer_id nk端末ID (null不可)
     */
    public void setComputer_id(String computer_id) {
        this.computer_id = computer_id;
    }

    /**
     * 端末名を取得
     * @return 端末名
     */
    @Column(nullable = false, length = 40)
    public String getComputer_name() {
        return this.computer_name;
    }

    /**
     * 端末名を設定
     * @param computer_name 端末名 (null不可)
     */
    public void setComputer_name(String computer_name) {
        this.computer_name = computer_name;
    }

    /**
     * 説明を取得
     * @return 説明
     */
    @Column(length = 40)
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * 説明を設定
     * @param explanation 説明
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     * 管理端末で選択可能フラグを取得
     * @return 管理端末で選択可能フラグ
     */
    @Column(nullable = false, length = 1)
    public int getManage_selectable_flag() {
        return this.manage_selectable_flag;
    }

    /**
     * 管理端末で選択可能フラグを設定
     * @param manage_selectable_flag 管理端末で選択可能フラグ (null不可)
     */
    public void setManage_selectable_flag(int manage_selectable_flag) {
        this.manage_selectable_flag = manage_selectable_flag;
    }

    /**
     * 工程端末で選択可能フラグを取得
     * @return 工程端末で選択可能フラグ
     */
    @Column(nullable = false, length = 1)
    public int getOperate_selectable_flag() {
        return this.operate_selectable_flag;
    }

    /**
     * 工程端末で選択可能フラグを設定
     * @param operate_selectable_flag 工程端末で選択可能フラグ (null不可)
     */
    public void setOperate_selectable_flag(int operate_selectable_flag) {
        this.operate_selectable_flag = operate_selectable_flag;
    }

}
