package com.gnomes.rest.service;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.uiservice.MessageBean;

/**
 * JAX-RS Web サービス結果
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/26 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class RestServiceResult {
    /**
     * 処理成功有無
     */
    private Boolean isSuccess;

    /**
     * セッションエラー有無
     */
    private Boolean isSessionError;

    /**
     * メッセージビーン
     */
    private MessageBean messageBean = new MessageBean();

    /**
     * 認証者ID
     */
    private String certUserId;


    /**
     * 個別返却値
     */
    private Object commandResponse;

    /** GnomesAppException. */
    private GnomesAppException gnomesAppException;

	/**
	 * @return commandResponse
	 */
	public Object getCommandResponse() {
		return commandResponse;
	}

	/**
	 * @param commandResponse セットする commandResponse
	 */
	public void setCommandResponse(Object commandResponse) {
		this.commandResponse = commandResponse;
	}

	/**
	 * @return isSuccess
	 */
	public Boolean getIsSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess セットする isSuccess
	 */
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return messageBean
	 */
	public MessageBean getMessageBean() {
		return messageBean;
	}

	/**
	 * @param messageBean セットする messageBean
	 */
	public void setMessageBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}

	/**
	 * @return certUserId
	 */
	public String getCertUserId() {
		return certUserId;
	}

	/**
	 * @param certUserId セットする certUserId
	 */
	public void setCertUserId(String certUserId) {
		this.certUserId = certUserId;
	}

	/**
	 * @return gnomesAppException
	 */
	public GnomesAppException getGnomesAppException() {
		return gnomesAppException;
	}

	/**
	 * @param gnomesAppException セットする gnomesAppException
	 */
	public void setGnomesAppException(GnomesAppException gnomesAppException) {
		this.gnomesAppException = gnomesAppException;
	}

    /**
     * @return isSessionError
     */
    public Boolean getIsSessionError() {
        return isSessionError;
    }

    /**
     * @param isSessionError セットする isSessionError
     */
    public void setIsSessionError(Boolean isSessionError) {
        this.isSessionError = isSessionError;
    }

}
