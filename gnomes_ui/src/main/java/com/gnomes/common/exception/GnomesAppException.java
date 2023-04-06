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
 * R0.01.01 2016/07/15 YJP/K.Fujiwara              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesAppException extends Exception {

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
    private List<MessageData> childMessageDatas = new ArrayList<>();

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

    public GnomesAppException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public GnomesAppException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GnomesAppException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GnomesAppException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * @param message
     */
    public GnomesAppException(String message, String messageNo) {
        super(message);
        this.messageNo = messageNo;
    }

    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesAppException(String message, String messageNo, Object ... params) {
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
    public GnomesAppException(String message, String messageNo, Throwable cause) {
        super(message, cause);
        this.messageNo = messageNo;
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     * @param params 置き換えパラメータ
     */
    public GnomesAppException(String message, String messageNo, Throwable cause, Object ... params) {
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
    public GnomesAppException(MessageData messageData) {
        this.messageNo = messageData.getMessageNo();
        this.messageParams = messageData.getParams();
    }

    /**
     *
     * @param messageData メッセージデータ
     * @param cause スローアブル
     */
    public GnomesAppException(MessageData messageData, Throwable cause) {
        super(cause);
        this.messageNo = messageData.getMessageNo();
        this.messageParams = messageData.getParams();
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
     * 子メッセージ追加
     * @param childMessageData
     */
    public void addChildMessageData(MessageData childMessageData) {
        this.childMessageDatas.add(childMessageData);
    }


}
