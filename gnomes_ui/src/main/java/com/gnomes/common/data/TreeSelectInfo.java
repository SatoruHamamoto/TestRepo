package com.gnomes.common.data;

/**
 * ツリーデータ情報
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/09/25 YJP/S.Hosokawa            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */


public class TreeSelectInfo {

	/** 閉じている項目ID配列 */
	private String[] treeClosedIdArray;

	/** 押した項目ID */
	private String treeExecuteId;


    /**
     * TreeData コンストラクタ
     */
	public TreeSelectInfo() {

	}

	/**
	 * @return treeClosedIdArray
	 */
	public String[] getTreeClosedIdArray() {
		return treeClosedIdArray;
	}


	/**
	 * @param treeClosedIdArray セットする treeClosedIdArray
	 */
	public void setTreeClosedIdArray(String[] treeClosedIdArray) {
		this.treeClosedIdArray = treeClosedIdArray;
	}


	/**
	 * @return treeExecuteId
	 */
	public String getTreeExecuteId() {
		return treeExecuteId;
	}


	/**
	 * @param treeExecuteId セットする treeExecuteId
	 */
	public void setTreeExecuteId(String treeExecuteId) {
		this.treeExecuteId = treeExecuteId;
	}


}
