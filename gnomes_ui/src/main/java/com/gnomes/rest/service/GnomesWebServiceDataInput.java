package com.gnomes.rest.service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * GRANDSIGHT-SF スケジューラからのサービス呼び出しパラメータ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesWebServiceDataInput {

    /** コマンドID. */
    private String command;

    /** パラメータリスト */
    private List<String> paramList = new ArrayList<>();

    /** バッチ重複チェック有無 */
    private Boolean dupCheck = true;

    /**
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command セットする command
     */
    public void setCommand(String command) {
        this.command = command;
    }

	/**
	 * @return paramList
	 */
	public List<String> getParamList() {
		return paramList;
	}

	/**
	 * @param paramList セットする paramList
	 */
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

    /**
     * @return dupCheck
     */
    public Boolean getDupCheck() {
        return dupCheck;
    }

    /**
     * @param dupCheck セットする dupCheck
     */
    public void setDupCheck(Boolean dupCheck) {
        this.dupCheck = dupCheck;
    }

}
