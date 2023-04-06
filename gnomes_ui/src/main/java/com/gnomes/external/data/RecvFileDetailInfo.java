package com.gnomes.external.data;

import java.io.Serializable;

/**
 * 受信ファイル明細情報
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class RecvFileDetailInfo implements Serializable {

    /**
     * 行No
     */
    private int lineNo;

    /**
     * 行ステータス
     */
    private int lineState;

    /**
     * 行データ
     */
    private String lineData;

    /**
     * コメント
     */
    private String comment;

	/**
	 * @return lineNo
	 */
	public int getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo セットする lineNo
	 */
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return lineState
	 */
	public int getLineState() {
		return lineState;
	}

	/**
	 * @param lineState セットする lineState
	 */
	public void setLineState(int lineState) {
		this.lineState = lineState;
	}

	/**
	 * @return lineData
	 */
	public String getLineData() {
		return lineData;
	}

	/**
	 * @param lineData セットする lineData
	 */
	public void setLineData(String lineData) {
		this.lineData = lineData;
	}

	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment セットする comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

}
