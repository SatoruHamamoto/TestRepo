package com.gnomes.external.logic;

import java.util.List;

import javax.enterprise.context.Dependent;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.external.data.SendRecvDataBean;

/**
 * コールクラスのテスト用クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/26 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class SampleTestRecvCallClass {

	/**
	 * コールクラス実行で実行するメソッド（仮定義）
	 * @param list
	 * @param filename
	 * @return
	 */
    @TraceMonitor
    @ErrorHandling
	public boolean execute(List<SendRecvDataBean> list, String filename){
    	// 処理成功
		return true;
	}
}
