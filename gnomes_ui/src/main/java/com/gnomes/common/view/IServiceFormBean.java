package com.gnomes.common.view;

import com.gnomes.common.command.RequestServiceFileInfo;

/**
 * Interface for ServiceFormBean
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface IServiceFormBean extends IFormBean {

	/**
	 * インポートファイル取得値を取得
	 * @return インポートファイル取得値
	 */
	public RequestServiceFileInfo getRequestServiceFileInfo();
}
