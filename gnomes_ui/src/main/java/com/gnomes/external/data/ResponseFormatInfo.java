package com.gnomes.external.data;

/**
 * 秤量器応答フォーマット情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/H.Yamada              初版
 * R0.01.02 2018/12/13 YJP/S.Kohno               パラメーター見直し
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ResponseFormatInfo {

    /** 応答区分判定文字列 */
    private String responseDivString;

	/** 応答区分 */
    private Integer responseType;

    /** 電文終端改行コード */
    private String linefeedCode;

    /**
     * 応答区分判定文字列を取得
     */
    public String getResponseDivString() {
		return responseDivString;
	}

    /**
     * 応答区分判定文字列を設定
     * @param responseDivString 応答区分判定文字列
     */
	public void setResponseDivString(String responseDivString) {
		this.responseDivString = responseDivString;
	}

    /**
     * 応答区分を取得
     * @return 応答区分
     */
    public Integer getResponseType() {
        return responseType;
    }

    /**
     * 応答区分を設定
     * @param responseType 応答区分
     */
    public void setResponseType(Integer responseType) {
        this.responseType = responseType;
    }

    /**
     * 電文終端改行文字 を得る
     * @return null or CR or CR+LF
     */
    public String getLinefeedCode()
    {
        return linefeedCode;
    }

    /**
     * 電文終端改行文字 を設定する
     * @param linefeedCodeDiv null or CR or CR+LF
     */
    public void setLinefeedCode(String linefeedCode)
    {
        this.linefeedCode = linefeedCode;
    }
}
