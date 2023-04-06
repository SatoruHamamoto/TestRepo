package com.gnomes.external.data;

import java.io.Serializable;
import java.util.List;

/**
 * 受信ファイル情報
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
public class RecvFileInfo implements Serializable {

    /**
     * 外部I/F対象システムコード
     */
    private String externalTargetCode;

	/**
     * ファイル名称
     */
    private String dataTypeName;

    /**
     * ステータス
     */
    private int state;

    /**
     * 受信ファイルパス
     */
    private String recvFilePath;

    /**
     * 受信ファイルバックアップパス
     */
    private String recvFileBkPath;

    /**
     * ファイル形式
     */
    private int fileFormat;

    /**
	 * @return recvFilePath
	 */
	public String getRecvFilePath() {
		return recvFilePath;
	}

	/**
	 * @param recvFilePath セットする recvFilePath
	 */
	public void setRecvFilePath(String recvFilePath) {
		this.recvFilePath = recvFilePath;
	}

	/**
	 * @return recvFileBkPath
	 */
	public String getRecvFileBkPath() {
		return recvFileBkPath;
	}

	/**
	 * @param recvFileBkPath セットする recvFileBkPath
	 */
	public void setRecvFileBkPath(String recvFileBkPath) {
		this.recvFileBkPath = recvFileBkPath;
	}

	/**
     * 受信ファイル明細情報
     */
    private List<RecvFileDetailInfo> detailList;

    /**
	 * @return externalTargetCode
	 */
	public String getExternalTargetCode() {
		return externalTargetCode;
	}

	/**
	 * @param externalTargetCode セットする externalTargetCode
	 */
	public void setExternalTargetCode(String externalTargetCode) {
		this.externalTargetCode = externalTargetCode;
	}

	/**
	 * @return dataTypeName
	 */
	public String getDataTypeName() {
		return dataTypeName;
	}

	/**
	 * @param dataTypeName セットする dataTypeName
	 */
	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	/**
	 * @return state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state セットする state
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @return detailList
	 */
	public List<RecvFileDetailInfo> getDetailList() {
		return detailList;
	}

	/**
	 * @param detailList セットする detailList
	 */
	public void setDetailList(List<RecvFileDetailInfo> detailList) {
		this.detailList = detailList;
	}

	/**
	 * @return fileFormat
	 */
	public int getFileFormat() {
		return fileFormat;
	}

	/**
	 * @param fileFormat セットする fileFormat
	 */
	public void setFileFormat(int fileFormat) {
		this.fileFormat = fileFormat;
	}

}
