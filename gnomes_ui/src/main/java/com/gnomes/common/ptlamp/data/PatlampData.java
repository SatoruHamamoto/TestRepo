package com.gnomes.common.ptlamp.data;

import java.util.List;

/**
 * パトランプ情報クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/02 YJP/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class PatlampData {

	/** IPアドレス */
	private String ip_address;

	/** talendジョブ名 */
	private String talendJoobName;

	/** 点灯鳴動パラメーター文字列 */
	private List<String> lightSoundParameterStringList;


	/**
	 * IPアドレスを取得
	 * @return IPアドレス
	 */
	public String getIp_address() {
		return ip_address;
	}

	/**
	 * IPアドレスを設定
	 * @param ip_address IPアドレス
	 */
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	/**
	 * talendジョブ名を取得
	 * @return talendジョブ名
	 */
	public String getTalendJoobName() {
		return talendJoobName;
	}

	/**
	 * talendジョブ名を設定
	 * @param talendJoobName talendジョブ名
	 */
	public void setTalendJoobName(String talendJoobName) {
		this.talendJoobName = talendJoobName;
	}

	/**
	 * 点灯鳴動パラメーター文字列を取得
	 * @return 点灯鳴動パラメーター文字列
	 */
	public List<String> getLightSoundParameterStringList() {
		return lightSoundParameterStringList;
	}

	/**
	 * 点灯鳴動パラメーター文字列を設定
	 * @param lightSoundParameterStringList 点灯鳴動パラメーター文字列
	 */
	public void setLightSoundParameterStringList(List<String> lightSoundParameterStringList) {
		this.lightSoundParameterStringList = lightSoundParameterStringList;
	}

}
