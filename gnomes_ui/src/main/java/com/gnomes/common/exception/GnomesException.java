package com.gnomes.common.exception;

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
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesException extends RuntimeException {

    /**
     * コマンド
     */
    private String command;

    /**
     * メッセージNo
     */
    private String messageNo = null;

    /**
     * メッセージパラメータリスト
     */
    private Object[] messageParams = null;

    /**
     * 子メッセージデータ
     */
    private List<MessageData> childMessageDatas = null;

    /**
     * キャンセルボタンOnclick
     */
    private String messageCancelOnClick;

    /**
     * OKボタンOnclick
     */
    private String messageOkOnClick;

    /**
     * @param message
     */
    public GnomesException(String message) {
        super(message);
    }
    /**
     * @param cause
     */
    public GnomesException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクター
     * @param gnomesAppException GnomesAppException
     */
    public GnomesException(GnomesAppException gnomesAppException) {
        super(gnomesAppException);

        this.messageNo = gnomesAppException.getMessageNo();
        this.messageParams = gnomesAppException.getMessageParams();
        this.childMessageDatas = gnomesAppException.getChildMessageDatas();
        this.command = gnomesAppException.getCommand();
        this.messageCancelOnClick = gnomesAppException.getMessageCancelOnClick();
        this.messageOkOnClick = gnomesAppException.getMessageOkOnClick();
    }


    /**
     * @param message
     * @param cause
     */
    public GnomesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GnomesException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesException(String message, String messageNo, Object ... params) {
        this.messageNo = messageNo;
        this.messageParams = params;
    }

    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesException(Throwable cause, String messageNo) {
        super(cause);
        this.messageNo = messageNo;
    }



    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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
     * @return childMessageDatas
     */
    public List<MessageData> getChildMessageDatas() {
        return childMessageDatas;
    }
    /**
     * @param childMessageDatas セットする childMessageDatas
     */
    public void setChildMessageDatas(List<MessageData> childMessageDatas) {
        this.childMessageDatas = childMessageDatas;
    }
    /**
     * @return messageCancelOnClick
     */
    public String getMessageCancelOnClick() {
        return messageCancelOnClick;
    }
    /**
     * @param messageCancelOnClick セットする messageCancelOnClick
     */
    public void setMessageCancelOnClick(String messageCancelOnClick) {
        this.messageCancelOnClick = messageCancelOnClick;
    }
    /**
     * @return messageOkOnClick
     */
    public String getMessageOkOnClick() {
        return messageOkOnClick;
    }
    /**
     * @param messageOkOnClick セットする messageOkOnClick
     */
    public void setMessageOkOnClick(String messageOkOnClick) {
        this.messageOkOnClick = messageOkOnClick;
    }


}
