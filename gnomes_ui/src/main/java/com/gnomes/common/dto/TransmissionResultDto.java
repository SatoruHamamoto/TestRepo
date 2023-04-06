package com.gnomes.common.dto;

import java.util.List;

import com.gnomes.common.exception.GnomesAppException;

/**
 * 秤量器通信結果クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/14 KCC/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class TransmissionResultDto {

    /** GnomesAppException. */
    private GnomesAppException gnomesAppException;

    /** 改行コード */
    protected static final String LINE_SEPARATOR = "\r\n";

    /** 受信データ. */
    @Deprecated
    private String recvData;

    /** 受信データリスト */
    private List<String> recvDataList;

    /**
     * GnomesAppExceptionを設定
     * @return GnomesAppException
     */
    public GnomesAppException getGnomesAppException() {
        return gnomesAppException;
    }

    /**
     * GnomesAppExceptionを取得
     * @param gnomesAppException
     */
    public void setGnomesAppException(GnomesAppException gnomesAppException) {
        this.gnomesAppException = gnomesAppException;
    }

    /**
     * 受信データを取得
     * @return 受信データ
     */
    public String getRecvData() {
        return String.join(LINE_SEPARATOR, recvDataList);
    }

    /**
     * 受信データを設定
     * @param recvData 受信データ
     */
    public void setRecvData(String recvData) {
        this.recvData = recvData;
    }

    /**
     * 受信データリストを取得
     * @return 受信データリスト
     */
	public List<String> getRecvDataList() {
		return recvDataList;
	}

    /**
     * 受信データリストを設定
     * @param recvDataList 受信データリスト
     */
	public void setRecvDataList(List<String> recvDataList) {
		this.recvDataList = recvDataList;
	}

}
