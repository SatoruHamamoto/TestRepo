package com.gnomes.system.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zh003パスワード変更履歴詳細（シノニム） エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/05/25 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "hist_change_password_detail_Write")
@NamedQueries({
        @NamedQuery(name = "HistChangePasswordDetailWrite.findAll", query = "SELECT p FROM HistChangePasswordDetailWrite p"),
        @NamedQuery(name = "HistChangePasswordDetailWrite.findByPK", query = "SELECT p FROM HistChangePasswordDetailWrite p WHERE p.hist_change_password_detail_key = :hist_change_password_detail_key")
})
public class HistChangePasswordDetailWrite extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "hist_change_password_detail_Write";

    /** パスワード変更履歴詳細キー */
    public static final String COLUMN_NAME_HIST_CHANGE_PASSWORD_DETAIL_KEY = "hist_change_password_detail_key";

    /** パスワード変更履歴キー (FK) */
    public static final String COLUMN_NAME_HIST_CHANGE_PASSWORD_KEY = "hist_change_password_key";

    /** パスワード */
    public static final String COLUMN_NAME_PASSWORD = "password";

    /** パスワード変更履歴詳細キー */
    private String hist_change_password_detail_key;
    /** パスワード変更履歴キー (FK) */
    private String hist_change_password_key;
    /** パスワード */
    private String password;

    /** Zh002パスワード変更履歴（シノニム） */
    private HistChangePasswordWrite histChangePasswordWrite;

    /**
     * Zh003パスワード変更履歴詳細（シノニム）エンティティ コンストラクタ
     */
    public HistChangePasswordDetailWrite() {
    }

    /**
     * Zh003パスワード変更履歴詳細（シノニム）エンティティ コンストラクタ
     * @param hist_change_password_detail_key パスワード変更履歴詳細キー
     * @param hist_change_password_key パスワード変更履歴キー (FK)
     * @param version 更新バージョン
     */
    public HistChangePasswordDetailWrite(String hist_change_password_detail_key, String hist_change_password_key, int version) {
        this.hist_change_password_detail_key = hist_change_password_detail_key;
        this.hist_change_password_key = hist_change_password_key;
        super.setVersion(version);
    }

    /**
     * Zh003パスワード変更履歴詳細（シノニム）エンティティ コンストラクタ
     * @param hist_change_password_detail_key パスワード変更履歴詳細キー
     * @param hist_change_password_key パスワード変更履歴キー (FK)
     * @param password パスワード
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
    public HistChangePasswordDetailWrite(String hist_change_password_detail_key, String hist_change_password_key, String password, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.hist_change_password_detail_key = hist_change_password_detail_key;
        this.hist_change_password_key = hist_change_password_key;
        this.password = password;
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
     * パスワード変更履歴詳細キーを取得
     * @return パスワード変更履歴詳細キー
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getHist_change_password_detail_key() {
        return this.hist_change_password_detail_key;
    }

    /**
     * パスワード変更履歴詳細キーを設定
     * @param hist_change_password_detail_key パスワード変更履歴詳細キー (null不可)
     */
    public void setHist_change_password_detail_key(String hist_change_password_detail_key) {
        this.hist_change_password_detail_key = hist_change_password_detail_key;
    }

    /**
     * パスワード変更履歴キー (FK)を取得
     * @return パスワード変更履歴キー (FK)
     */
    @Column(nullable = false, length = 38)
    public String getHist_change_password_key() {
        return this.hist_change_password_key;
    }

    /**
     * パスワード変更履歴キー (FK)を設定
     * @param hist_change_password_key パスワード変更履歴キー (FK) (null不可)
     */
    public void setHist_change_password_key(String hist_change_password_key) {
        this.hist_change_password_key = hist_change_password_key;
    }

    /**
     * パスワードを取得
     * @return パスワード
     */
    @Column(length = 265)
    public String getPassword() {
        return this.password;
    }

    /**
     * パスワードを設定
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Zh002パスワード変更履歴（シノニム）を取得
     * @return Zh002パスワード変更履歴（シノニム）
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "hist_change_password_key", referencedColumnName = "hist_change_password_key", insertable = false, updatable = false) 
    public HistChangePasswordWrite getHistChangePasswordWrite() {
        return this.histChangePasswordWrite;
    }

    /**
     * Zh002パスワード変更履歴（シノニム）を設定
     * @return Zh002パスワード変更履歴（シノニム）
     */ 
    public void setHistChangePasswordWrite(HistChangePasswordWrite histChangePasswordWrite) {
        this.histChangePasswordWrite = histChangePasswordWrite;
    }

}
