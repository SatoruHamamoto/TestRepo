package com.gnomes.external.logic.talend;

import com.gnomes.external.data.SendRecvDataBean;

/**
 * 送受信データテスト用のモデルクラス
 * 
 * @author 30041979
 *
 */
public class TestSendRecvDataBase extends SendRecvDataBean {

	private String checkRequire = "notNull";

	private String checkLength;

	public String getCheckRequire() {
		return checkRequire;
	}

	public void setCheckRequire(String checkRequire) {
		this.checkRequire = checkRequire;
	}

	public String getCheckLength() {
		return checkLength;
	}

	public void setCheckLength(String checkLength) {
		this.checkLength = checkLength;
	}

}
