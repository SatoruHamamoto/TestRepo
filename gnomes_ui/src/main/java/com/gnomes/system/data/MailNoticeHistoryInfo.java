package com.gnomes.system.data;

import java.util.Date;

import com.gnomes.common.constants.CommonEnums.MailNoticeStatus;

/**
 * メール通知履歴情報
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
public class MailNoticeHistoryInfo {

    /** メッセージキー */
    private String messageKey;

    /** 発生日時 */
    private Date occurdate;

    /** メッセージNo */
    private String messageno;

    /** メール通知状況 */
    private MailNoticeStatus mailNoticeStatus;

    /** 失敗理由 */
    private String failureReason;

    /**
     * メッセージキー取得
     * @return メッセージキー
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * メッセージキー設定
     * @param messageKey メッセージキー
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * 発生日時取得.
     * @return 発生日時
     */
    public Date getOccurdate() {
        return occurdate;
    }

    /**
     * 発生日時設定
     * @param occurdate 発生日時
     */
    public void setOccurdate(Date occurdate) {
        this.occurdate = occurdate;
    }

    /**
     * メッセージNo取得
     * @return メッセージNo
     */
    public String getMessageno() {
        return messageno;
    }

    /**
     * メッセージNo設定
     * @param messageno メッセージNo
     */
    public void setMessageno(String messageno) {
        this.messageno = messageno;
    }

    /**
     * メール通知状況取得
     * @return メール通知状況
     */
    public MailNoticeStatus getMailNoticeStatus() {
        return mailNoticeStatus;
    }

    /**
     * メール通知状況設定
     * @param mailNoticeStatus メール通知状況
     */
    public void setMailNoticeStatus(MailNoticeStatus mailNoticeStatus) {
        this.mailNoticeStatus = mailNoticeStatus;
    }

    /**
     * 失敗理由取得
     * @return 失敗理由
     */
    public String getFailureReason() {
        return failureReason;
    }

    /**
     * 失敗理由設定
     * @param failureReason 失敗理由
     */
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

}
