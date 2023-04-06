package com.gnomes.common.exception;

import java.util.ArrayList;
import java.util.List;

import com.gnomes.common.data.MessageData;

/**
 * ここにクラス概要を入力してください。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/05/17 SGT/K.Nakanishi           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesPeriodicException extends Exception {

    /**
     * エラー有りフラグ
     */
    private int errorFlag;

    /**
     * リトライ実施フラグ
     */
    private int retryFlag;

    /**
     * 異常終了フラグ
     */
    private int periodicProcessStatus;

    /**
     * メッセージNo
     */
    private String messageNo = null;

    /**
     * メッセージパラメータリスト
     */
    private Object[] messageParams = null;

    /**
     * @param message
     */

    public GnomesPeriodicException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public GnomesPeriodicException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GnomesPeriodicException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GnomesPeriodicException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * @param message
     */
    public GnomesPeriodicException(String message, String messageNo) {
        super(message);
        this.messageNo = messageNo;
    }

    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesPeriodicException(String message, String messageNo, Object ... params) {
        this.messageNo = messageNo;

        if (params != null) {
            Object[] paramNullForStrings = new Object[params.length];
            for(int i=0; i<params.length; i++) {
                if(params[i] == null){
                    paramNullForStrings[i] = "null";
                } else {
                    paramNullForStrings[i] = params[i];
                }
            }
            this.messageParams = paramNullForStrings;
        }
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     */
    public GnomesPeriodicException(String message, String messageNo, Throwable cause) {
        super(message, cause);
        this.messageNo = messageNo;
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     * @param params 置き換えパラメータ
     */
    public GnomesPeriodicException(String message, String messageNo, Throwable cause, Object ... params) {
        super(message, cause);
        this.messageNo = messageNo;

        if (params != null) {
            Object[] paramNullForStrings = new Object[params.length];
            for(int i=0; i<params.length; i++) {
                if(params[i] == null){
                    paramNullForStrings[i] = "null";
                } else {
                    paramNullForStrings[i] = params[i];
                }
            }
            this.messageParams = paramNullForStrings;
        }
    }

    /**
     *
     * @param messageData メッセージデータ
     */
    public GnomesPeriodicException(MessageData messageData) {
        this.messageNo = messageData.getMessageNo();
        this.messageParams = messageData.getParams();
    }

    /**
     *
     * @param messageData メッセージデータ
     * @param cause スローアブル
     */
    public GnomesPeriodicException(MessageData messageData, Throwable cause) {
        super(cause);
        this.messageNo = messageData.getMessageNo();
        this.messageParams = messageData.getParams();
    }


    public int getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(int errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(String messageNo) {
        this.messageNo = messageNo;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(Object[] messageParams) {
        this.messageParams = messageParams;
    }

    /**
     * @return retryFlag
     */
    public int getRetryFlag() {
        return retryFlag;
    }

    /**
     * @param retryFlag セットする retryFlag
     */
    public void setRetryFlag(int retryFlag) {
        this.retryFlag = retryFlag;
    }

    /**
     * @return periodicProcessStatus
     */
    public int getPeriodicProcessStatus() {
        return periodicProcessStatus;
    }

    /**
     * @param periodicProcessStatus セットする periodicProcessStatus
     */
    public void setPeriodicProcessStatus(int periodicProcessStatus) {
        this.periodicProcessStatus = periodicProcessStatus;
    }
}
