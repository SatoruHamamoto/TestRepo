package com.gnomes.rest.service;

import java.util.List;

import com.gnomes.system.data.PopupMessageInfo;

/**
 * JSON データ用のクラス定義(ポップアップメッセージサービス)
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/18 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

/**
 * JSON データ用のクラス定義（ポップアップメッセージサービス）
 */
public class PopupMessage {

    /**
     * 処理成功か否か
     */
    private Integer isSuccess;

    /**
     * ポップアップメッセージ一覧
     */
    private List<PopupMessageInfo> popupMessageInfoList;

	/**
	 * @return isSuccess
	 */
	public Integer getIsSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess セットする isSuccess
	 */
	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return popupMessageInfoList
	 */
	public List<PopupMessageInfo> getPopupMessageInfoList() {
		return popupMessageInfoList;
	}

	/**
	 * @param popupMessageInfoList セットする popupMessageInfoList
	 */
	public void setPopupMessageInfoList(List<PopupMessageInfo> popupMessageInfoList) {
		this.popupMessageInfoList = popupMessageInfoList;
	}





}
