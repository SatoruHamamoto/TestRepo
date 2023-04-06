package com.gnomes.system.data;

import javax.enterprise.context.RequestScoped;

/**
 * メール情報
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/15 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class MailInfoBean {

    /** 差出人 */
    private String from;

    /** 宛先 */
    private String to;

    /** CC */
    private String cc;

    /** BCC */
    private String bcc;

    /** 題名 */
    private String subject;

    /** メッセージ */
    private String message;

    /** 認証ユーザID */
    private String authenticatedUserId;

    /** 認証パスワード */
    private String authenticatedPassword;

    /** ジョブ名 */
    private String jobName;

    /** コンポーネント名 */
    private String componentName;

    /** エンコード */
    private String encode;

    /** ホスト名 */
    private String mailhost;

    /** ポート番号 */
    private String port;

    /** エラーメッセージ */
    private String errorMessage;

    /** メール送信処理種別
     * | 設定値            | 暗号化方式   | 認証 |
     * |-------------------|--------------|------|
     * | "STARTTLS,NOAUTH" | STARTTLS     | なし |
     * | "SSL,NOAUTH"      | SSL          | なし |
     * | "NOSECURE,NOAUTH" | なし（平文） | なし |
     * | "STARTTLS,AUTH"   | STARTTLS     | あり |
     * | "SSL,AUTH"        | SSL          | あり |
     * | "NOSECURE,AUTH"   | なし（平文） | あり |
     */
    private String mailSendDiv;

    /**
     * 差出人を取得
     * @return 差出人
     */
    public String getFrom() {
        return from;
    }

    /**
     * 差出人を設定
     * @param mailAddressFrom 差出人
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 宛先を設定
     * @return 宛先
     */
    public String getTo() {
        return to;
    }

    /**
     * 宛先を取得
     * @param to 宛先
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * CCを取得
     * @return CC
     */
    public String getCc() {
        return cc;
    }

    /**
     * CCを設定
     * @param cc CC
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * BCCを取得
     * @return BCC
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * BCCを設定
     * @param bcc BCC
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * 題名を取得
     * @return 題名
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 題名を設定
     * @param subject 題名
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * メッセージを取得
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * メッセージを設定
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 認証ユーザIDを取得
     * @return 認証ユーザID
     */
    public String getAuthenticatedUserId() {
        return authenticatedUserId;
    }

    /**
     * 認証ユーザIDを設定
     * @param authenticatedUserId 認証ユーザID
     */
    public void setAuthenticatedUserId(String authenticatedUserId) {
        this.authenticatedUserId = authenticatedUserId;
    }

    /**
     * 認証パスワードを取得
     * @return 認証パスワード
     */
    public String getAuthenticatedPassword() {
        return authenticatedPassword;
    }

    /**
     * 認証パスワードを設定
     * @param authenticatedPassword 認証パスワード
     */
    public void setAuthenticatedPassword(String authenticatedPassword) {
        this.authenticatedPassword = authenticatedPassword;
    }

    /**
     * ジョブ名取得
     * @return ジョブ名
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * ジョブ名を設定
     * @param jobName ジョブ名
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * コンポーネント名を取得
     * @return コンポーネント名
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * コンポーネント名を設定
     * @param componentName コンポーネント名
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * エンコードを取得
     * @return エンコード
     */
    public String getEncode() {
        return encode;
    }

    /**
     * エンコードを設定
     * @param encode エンコード
     */
    public void setEncode(String encode) {
        this.encode = encode;
    }

    /**
     * ホスト名を取得
     * @return ホスト名
     */
    public String getMailhost() {
    	return mailhost;
    }

    /**
     * ホスト名を設定
     * @param mailhost ホスト名
     */
    public void setMailhost(String mailhost) {
    	this.mailhost = mailhost;
    }

    /**
     * ポート番号を取得
     * @return エンコード
     */
    public String getPort() {
    	return port;
    }

    /**
     * ポート番号を設定
     * @param port ポート番号
     */
    public void setPort(String port) {
    	this.port = port;
    }
    /**
     * メール送信処理種別を取得
     *
     * @return メール送信処理種別
     * | 設定値            | 暗号化方式   | 認証 |
     * |-------------------|--------------|------|
     * | "STARTTLS,NOAUTH" | STARTTLS     | なし |
     * | "SSL,NOAUTH"      | SSL          | なし |
     * | "NOSECURE,NOAUTH" | なし（平文） | なし |
     * | "STARTTLS,AUTH"   | STARTTLS     | あり |
     * | "SSL,AUTH"        | SSL          | あり |
     * | "NOSECURE,AUTH"   | なし（平文） | あり |
     */
    public String getMailSendDiv()
    {
        return mailSendDiv;
    }

    /**
     * メール送信処理種別を設定
     *
     * @param mailSendDiv　以下の表の設定値のいずれか
     * | 設定値            | 暗号化方式   | 認証 |
     * |-------------------|--------------|------|
     * | "STARTTLS,NOAUTH" | STARTTLS     | なし |
     * | "SSL,NOAUTH"      | SSL          | なし |
     * | "NOSECURE,NOAUTH" | なし（平文） | なし |
     * | "STARTTLS,AUTH"   | STARTTLS     | あり |
     * | "SSL,AUTH"        | SSL          | あり |
     * | "NOSECURE,AUTH"   | なし（平文） | あり |
     */
    public void setMailSendDiv(String mailSendDiv)
    {
        this.mailSendDiv = mailSendDiv;
    }

    /**
     * エラーメッセージを取得
     * @return エラーメッセージ
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * エラーメッセージを設定
     * @param errrorMessage エラーメッセージ
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
