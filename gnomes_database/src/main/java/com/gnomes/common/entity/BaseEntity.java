package com.gnomes.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OptimisticLockException;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * エンティティ 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/12 YJP/K.Gotand              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@MappedSuperclass
public class BaseEntity extends GnomesEntitiy {

    /** 登録イベントID */
    public static final String COLUMN_NAME_FIRST_REGIST_EVENT_ID = "first_regist_event_id";

    /** 登録従業員No */
    public static final String COLUMN_NAME_FIRST_REGIST_USER_NUMBER = "first_regist_user_number";

    /** 登録従業員名 */
    public static final String COLUMN_NAME_FIRST_REGIST_USER_NAME = "first_regist_user_name";

    /** 登録日時 */
    public static final String COLUMN_NAME_FIRST_REGIST_DATETIME = "first_regist_datetime";

    /** 更新イベントID */
    public static final String COLUMN_NAME_LAST_REGIST_EVENT_ID = "last_regist_event_id";

    /** 更新従業員No */
    public static final String COLUMN_NAME_LAST_REGIST_USER_NUMBER = "last_regist_user_number";

    /** 更新従業員名 */
    public static final String COLUMN_NAME_LAST_REGIST_USER_NAME = "last_regist_user_name";

    /** 更新日時 */
    public static final String COLUMN_NAME_LAST_REGIST_DATETIME = "last_regist_datetime";

    /** 更新バージョン */
    public static final String COLUMN_NAME_VERSION = "version";

    /** 登録イベントID */
    private String first_regist_event_id;
    /** 登録従業員No */
    private String first_regist_user_number;
    /** 登録従業員名 */
    private String first_regist_user_name;
    /** 登録日時 */
    private Date first_regist_datetime;
    /** 更新イベントID */
    private String last_regist_event_id;
    /** 更新従業員No */
    private String last_regist_user_number;
    /** 更新従業員名 */
    private String last_regist_user_name;
    /** 更新日時 */
    private Date last_regist_datetime;
    /** 更新バージョン */
    private int version;

    public BaseEntity() {

    }

    /**
     * 登録イベントIDを取得
     * @return 登録イベントID
     */
    @Column(length = 38)
    public String getFirst_regist_event_id() {
        return first_regist_event_id;
    }

    /**
     * 登録イベントIDを設定
     * @param first_regist_event_id 登録イベントID
     */
    public void setFirst_regist_event_id(String first_regist_event_id) {
        this.first_regist_event_id = first_regist_event_id;
    }

    /**
     * 登録従業員Noを取得
     * @return 登録従業員No
     */
    @Column(length = 20)
    public String getFirst_regist_user_number() {
        return first_regist_user_number;
    }

    /**
     * 登録従業員Noを設定
     * @param first_regist_user_number 登録従業員No
     */
    public void setFirst_regist_user_number(String first_regist_user_number) {
        this.first_regist_user_number = first_regist_user_number;
    }

    /**
     * 登録従業員名を取得
     * @return 登録従業員名
     */
    @Column(length = 40)
    public String getFirst_regist_user_name() {
        return first_regist_user_name;
    }

    /**
     * 登録従業員名を設定
     * @param first_regist_user_name 登録従業員名
     */
    public void setFirst_regist_user_name(String first_regist_user_name) {
        this.first_regist_user_name = first_regist_user_name;
    }

    /**
     * 登録日時を取得
     * @return 登録日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getFirst_regist_datetime() {
        return first_regist_datetime;
    }

    /**
     * 登録日時を設定
     * @param first_regist_datetime 登録日時
     */
    public void setFirst_regist_datetime(Date first_regist_datetime) {
        this.first_regist_datetime = first_regist_datetime;
    }

    /**
     * 更新イベントIDを取得
     * @return 更新イベントID
     */
    @Column(length = 38)
    public String getLast_regist_event_id() {
        return last_regist_event_id;
    }

    /**
     * 更新イベントIDを設定
     * @param last_regist_event_id 更新イベントID
     */
    public void setLast_regist_event_id(String last_regist_event_id) {
        this.last_regist_event_id = last_regist_event_id;
    }

    /**
     * 更新従業員Noを取得
     * @return 更新従業員No
     */
    @Column(length = 20)
    public String getLast_regist_user_number() {
        return last_regist_user_number;
    }

    /**
     * 更新従業員Noを設定
     * @param last_regist_user_number 更新従業員No
     */
    public void setLast_regist_user_number(String last_regist_user_number) {
        this.last_regist_user_number = last_regist_user_number;
    }

    /**
     * 更新従業員名を取得
     * @return 更新従業員名
     */
    @Column(length = 40)
    public String getLast_regist_user_name() {
        return last_regist_user_name;
    }

    /**
     * 更新従業員名を設定
     * @param last_regist_user_name 更新従業員名
     */
    public void setLast_regist_user_name(String last_regist_user_name) {
        this.last_regist_user_name = last_regist_user_name;
    }

    /**
     * 更新日時を取得
     * @return 更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getLast_regist_datetime() {
        return last_regist_datetime;
    }

    /**
     * 更新日時を設定
     * @param last_regist_datetime 更新日時
     */
    public void setLast_regist_datetime(Date last_regist_datetime) {
        this.last_regist_datetime = last_regist_datetime;
    }

    /**
     * 更新バージョンを取得
     * @return 更新バージョン
     */
    @Column(nullable = false, length = 11)
    @Version
    public int getVersion() {
        return this.version;
    }

    /**
     * 更新バージョンを設定
     * @param version 更新バージョン (null不可)
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * 更新バージョンチェック
     */
    public void versionCheck(Integer version) throws OptimisticLockException {
        if (this.getVersion() != version) {
            throw new OptimisticLockException();
        }
    }
}
