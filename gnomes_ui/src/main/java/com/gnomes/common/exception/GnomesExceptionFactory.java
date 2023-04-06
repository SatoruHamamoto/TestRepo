package com.gnomes.common.exception;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.MessageData;

/**
 * GnomesExceptionファクトリ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/01 YJP/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@Dependent
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class GnomesExceptionFactory {


    public GnomesExceptionFactory() {

    }

    /**
     * @param message
     */
    public GnomesException createGnomesException(String message) {
        return new GnomesException(message);
    }
    /**
     * @param cause
     */
    public GnomesException createGnomesException(Throwable cause) {
        return new GnomesException(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GnomesException createGnomesException(String message, Throwable cause) {
        return new GnomesException(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GnomesException createGnomesException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        return new GnomesException(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesException createGnomesException(String message, String messageNo, Object ... params) {
        return new GnomesException(message, messageNo, params);
    }

    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesException createGnomesException(Throwable cause, String messageNo) {
        return new GnomesException(cause);

    }




    public GnomesAppException createGnomesAppException(String message) {
        return new GnomesAppException(message);
    }

    /**
     * @param cause
     */
    public GnomesAppException createGnomesAppException(Throwable cause) {
        return new GnomesAppException(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GnomesAppException createGnomesAppException(String message, Throwable cause) {
        return new GnomesAppException(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GnomesAppException createGnomesAppException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        return new GnomesAppException(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * @param messageNo メッセージNo
     */
    public GnomesAppException createGnomesAppException(String message, String messageNo) {
        return new GnomesAppException(message, messageNo);
    }

    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesAppException createGnomesAppException(String message, String messageNo, Object ... params) {
        return new GnomesAppException(message, messageNo, params);
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     */
    public GnomesAppException createGnomesAppException(String message, String messageNo, Throwable cause) {
        return new GnomesAppException(message, messageNo, cause);
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     * @param params 置き換えパラメータ
     */
    public GnomesAppException createGnomesAppException(String message, String messageNo, Throwable cause, Object ... params) {
        return new GnomesAppException(message, messageNo, cause, params);
    }

    /**
     * @param messageData メッセージデータ
     */
    public GnomesAppException createGnomesAppException(MessageData messageData) {
        return new GnomesAppException(messageData);
    }




    public GnomesPeriodicException createGnomesPeriodicException(String message) {
        return new GnomesPeriodicException(message);
    }

    /**
     * @param cause
     */
    public GnomesPeriodicException createGnomesPeriodicException(Throwable cause) {
        return new GnomesPeriodicException(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GnomesPeriodicException createGnomesPeriodicException(String message, Throwable cause) {
        return new GnomesPeriodicException(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GnomesPeriodicException createGnomesPeriodicException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        return new GnomesPeriodicException(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * @param messageNo メッセージNo
     */
    public GnomesPeriodicException createGnomesPeriodicException(String message, String messageNo) {
        return new GnomesPeriodicException(message, messageNo);
    }

    /**
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public GnomesPeriodicException createGnomesPeriodicException(String message, String messageNo, Object ... params) {
        return new GnomesPeriodicException(message, messageNo, params);
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     */
    public GnomesPeriodicException createGnomesPeriodicException(String message, String messageNo, Throwable cause) {
        return new GnomesPeriodicException(message, messageNo, cause);
    }

    /**
     * @param messageNo メッセージNo
     * @param cause
     * @param params 置き換えパラメータ
     */
    public GnomesPeriodicException createGnomesPeriodicException(String message, String messageNo, Throwable cause, Object ... params) {
        return new GnomesPeriodicException(message, messageNo, cause, params);
    }

    /**
     * @param messageData メッセージデータ
     */
    public GnomesPeriodicException createGnomesPeriodicException(MessageData messageData) {
        return new GnomesPeriodicException(messageData);
    }




}