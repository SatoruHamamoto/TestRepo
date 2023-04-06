package com.gnomes.common.data;

import java.time.LocalDateTime;

/*
 *
 * 監査証跡情報
 *
 */

public class AuditTrailInfo {

    /** アクセス（操作）日時 */
    private LocalDateTime accessDateTime;
    /** 操作PCコンピュータ名または IPアドレス */
    private String acesssComputerNamer;
    /** 操作ユーザID */
    private String occrUserId;
    /** 操作ユーザ名 */
    private String occrUserName;
    /** 操作内容（画面ID） */
    private String accessScreenId;
    /** 操作内容（画面名） */
    private String accessScreenName;
    /** 操作内容（クラス名） */
    private String accessClassName;
    /** 操作内容（メソッド名） */
    private String accessMethodName;
    /** 操作内容（機能（コマンドID)） */
    private String accessCommandId;
    /** 変更データ項目（リスト） */
    private String[] accessDataItemList;
    /** 変更データ内容（リスト） */
    private String[] accessDataContentList;


    /**
     * AuditTrailInfo コンストラクタ
     */
    public AuditTrailInfo() {
    }

    /**
     * アクセス（操作）日時を取得
     * @return アクセス（操作）日時
     */
    public LocalDateTime getAccessDateTime() {
        return accessDateTime;
    }

    /**
     * アクセス（操作）日時を設定
     * @param accessDateTime アクセス（操作）日時
     */
    public void setAccessDateTime(LocalDateTime accessDateTime) {
        this.accessDateTime = accessDateTime;
    }

    /**
     * 操作PCコンピュータ名または IPアドレスを取得
     * @return 操作PCコンピュータ名または IPアドレス
     */
    public String getAcesssComputerNamer() {
        return acesssComputerNamer;
    }

    /**
     * 操作PCコンピュータ名または IPアドレスを設定
     * @param acesssComputerNamer 操作PCコンピュータ名または IPアドレス
     */
    public void setAcesssComputerNamer(String acesssComputerNamer) {
        this.acesssComputerNamer = acesssComputerNamer;
    }

    /**
     * 操作ユーザIDを取得
     * @return 操作ユーザID
     */
    public String getOccrUserId() {
        return occrUserId;
    }

    /**
     * 操作ユーザIDを設定
     * @param occrUserId 操作ユーザID
     */
    public void setOccrUserId(String occrUserId) {
        this.occrUserId = occrUserId;
    }

    /**
     * 操作ユーザ名を取得
     * @return 操作ユーザ名
     */
    public String getOccrUserName() {
        return occrUserName;
    }

    /**
     * 操作ユーザ名を設定
     * @param occrUserName 操作ユーザ名
     */
    public void setOccrUserName(String occrUserName) {
        this.occrUserName = occrUserName;
    }

    /**
     * 操作内容（画面ID）を取得
     * @return 操作内容（画面ID）
     */
    public String getAccessScreenId() {
        return accessScreenId;
    }

    /**
     * 操作内容（画面ID）を設定
     * @param accessScreenId 操作内容（画面ID）
     */
    public void setAccessScreenId(String accessScreenId) {
        this.accessScreenId = accessScreenId;
    }

    /**
     * 操作内容（画面名）を取得
     * @return  操作内容（画面名）
     */
    public String getAccessScreenName() {
        return accessScreenName;
    }

    /**
     * 操作内容（画面名）を設定
     * @param accessScreenName 操作内容（画面名）
     */
    public void setAccessScreenName(String accessScreenName) {
        this.accessScreenName = accessScreenName;
    }

    /**
     * 操作内容（クラス名）を取得
     * @return 操作内容（クラス名）
     */
    public String getAccessClassName() {
        return accessClassName;
    }

    /**
     * 操作内容（クラス名）を設定
     * @param screenId 操作内容（クラス名）
     */
    public void setAccessClassName(String accessClassName) {
        this.accessClassName = accessClassName;
    }

    /**
     * 操作内容（メソッド名）を取得
     * @return 操作内容（メソッド名）
     */
    public String getAccessMethodName() {
        return accessMethodName;
    }

    /**
     * 操作内容（メソッド名）を設定
     * @param screenId 操作内容（メソッド名）
     */
    public void setAccessMethodName(String accessMethodName) {
        this.accessMethodName = accessMethodName;
    }

    /**
     *操作内容（機能（コマンドID)を取得
     * @return 操作内容（機能（コマンドID)を取得
     */
    public String getAccessCommandId() {
        return accessCommandId;
    }

    /**
     * 操作内容（機能（コマンドID)を取得設定
     * @param accessCommandId 操作内容（機能（コマンドID)を取得
     */
    public void setAccessCommandId(String accessCommandId) {
        this.accessCommandId = accessCommandId;
    }

    /**
     * 変更データ項目（リスト）を取得
     * @return 変更データ項目（リスト）
     */
    public String[] getAccessDataItemList() {
        return accessDataItemList;
    }

    /**
     * 変更データ項目（リスト）を取得設定
     * @param accessDataItemList 変更データ項目（リスト）
     */
    public void setAccessDataItemList(String[] accessDataItemList) {
        this.accessDataItemList = accessDataItemList;
    }

    /**
     * 変更データ内容（リスト）を取得
     * @return 変更データ内容（リスト）
     */
    public String[] getAccessDataContentList() {
        return accessDataContentList;
    }

    /**
     * 変更データ内容（リスト）を設定
     * @param accessDataContentList 変更データ内容（リスト）
     */
    public void setAccessDataContentList(String[] accessDataContentList) {
        this.accessDataContentList = accessDataContentList;
    }

}
