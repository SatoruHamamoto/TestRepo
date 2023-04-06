package com.gnomes.rest.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class GnomesWebServiceResult {
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
    private List<String> messageList = new ArrayList<>();

    /**
     * 認証者ID
     */
    private String certUserId;


    /**
     * 個別返却値
     */
    private Object commandResponse;

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
	 * @return messageList
	 */
	public  List<String> getMessageList() {
		return messageList;
	}

	/**
	 * @param messageList セットする messageBean
	 */
	public void setMessageBean(List<String>  messageList) {
		this.messageList = messageList;
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


}
