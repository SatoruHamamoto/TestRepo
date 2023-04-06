package com.gnomes.system.data.printout;

import java.util.Map;

import com.gnomes.common.constants.CommonEnums.ElectronicFileCreateType;
import com.gnomes.common.constants.CommonEnums.PrintType;
import com.gnomes.common.constants.CommonEnums.RePrintMark;

/**
 * 帳票印字要求情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/17 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ReportPrintOutRequestInfo {

    /** 帳票様式番号 */
    private String printCommandNo;

    /** 要求内連番 */
    private int requestSeq;

    /** 帳票種類 */
    private PrintType printerType;

    /** 印字枚数 */
    private int printTimes;

    /** 帳票名 */
    private String reportName;

    /** プリンタID */
    private String printerId;

    /** プリンタ名 */
    private String printerName;

    /** 印字理由コード */
    private String printReasonCode;

    /** 印字理由名 */
    private String printReasonName;

    /** 印字理由コメント */
    private String printReasonComment;

    /** 印字パラメータ */
    private Map<String, String> printParameterMap;

    /** 再印刷マーク出力有無 */
    private RePrintMark isRePrintMark;

    /** 再印刷マーク出力位置 */
    private String positionRePrintMark;

    /** 電子ファイル作成区分 */
    private ElectronicFileCreateType isFileCreateType;

    /** PDF電子ファイル名 */
    private String pdfFileName;

    /**
     * 帳票様式番号を取得
     * @return 帳票様式番号
     */
    public String getPrintCommandNo() {
        return printCommandNo;
    }

    /**
     * 帳票様式番号を設定
     * @param printCommandNo 帳票様式番号
     */
    public void setPrintCommandNo(String printCommandNo) {
        this.printCommandNo = printCommandNo;
    }

    /**
     * 要求内連番を取得
     * @return 要求内連番
     */
    public int getRequestSeq() {
        return requestSeq;
    }

    /**
     * 要求内連番を設定
     * @param requestSeq 要求内連番
     */
    public void setRequestSeq(int requestSeq) {
        this.requestSeq = requestSeq;
    }

    /**
     * 帳票種類を取得
     * @return 帳票種類
     */
    public PrintType getPrinterType() {
        return printerType;
    }

    /**
     * 帳票種類を設定
     * @param printerType 帳票種類
     */
    public void setPrinterType(PrintType printerType) {
        this.printerType = printerType;
    }

    /**
     * 印字枚数を取得
     * @return 印字枚数
     */
    public int getPrintTimes() {
        return printTimes;
    }

    /**
     * 印字枚数を設定
     * @param printTimes 印字枚数
     */
    public void setPrintTimes(int printTimes) {
        this.printTimes = printTimes;
    }

    /**
     * 帳票名を取得
     * @return 帳票名
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * 帳票名を設定
     * @param reportName 帳票名
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * プリンタIDを取得
     * @return プリンタID
     */
    public String getPrinterId() {
        return printerId;
    }

    /**
     * プリンタIDを設定
     * @param printerId プリンタID
     */
    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    /**
     * プリンタ名を取得
     * @return プリンタ名
     */
    public String getPrinterName() {
        return printerName;
    }

    /**
     * プリンタ名を設定
     * @param printerName プリンタ名
     */
    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    /**
     * 印字理由コードを設定
     * @return 印字理由コード
     */
    public String getPrintReasonCode() {
        return printReasonCode;
    }

    /**
     * 印字理由コードを取得
     * @param printReasonCode 印字理由コード
     */
    public void setPrintReasonCode(String printReasonCode) {
        this.printReasonCode = printReasonCode;
    }

    /**
     * 印字理由名を取得
     * @return 印字理由名
     */
    public String getPrintReasonName() {
        return printReasonName;
    }

    /**
     * 印字理由名を設定
     * @param printReasonName 印字理由名
     */
    public void setPrintReasonName(String printReasonName) {
        this.printReasonName = printReasonName;
    }

    /**
     * 印字理由コメントを取得
     * @return 印字理由コメント
     */
    public String getPrintReasonComment() {
        return printReasonComment;
    }

    /**
     * 印字理由コメントを設定
     * @param printReasonComment 印字理由コメント
     */
    public void setPrintReasonComment(String printReasonComment) {
        this.printReasonComment = printReasonComment;
    }

    /**
     * 印字パラメータを取得
	 * @return printParameterMap 印字パラメータ
	 */
	public Map<String, String> getPrintParameterMap() {
		return printParameterMap;
	}

	/**
	 * 印字パラメータを設定
	 * @param printParameterMap セットする 印字パラメータ
	 */
	public void setPrintParameterMap(Map<String, String> printParameterMap) {
		this.printParameterMap = printParameterMap;
	}

    /**
     * 再印刷マーク出力有無を取得
     * @return 再印刷マーク出力有無
     */
    public RePrintMark getIsRePrintMark() {
        return isRePrintMark;
    }

    /**
     * 再印刷マーク出力有無を設定
     * @param isRePrintMark 再印刷マーク出力有無
     */
    public void setIsRePrintMark(RePrintMark isRePrintMark) {
        this.isRePrintMark = isRePrintMark;
    }

    /**
     *  再印刷マーク出力位置を取得
     * @return  再印刷マーク出力位置
     */
    public String getPositionRePrintMark() {
        return positionRePrintMark;
    }

    /**
     *  再印刷マーク出力位置を設定
     * @param positionRePrintMark  再印刷マーク出力位置
     */
    public void setPositionRePrintMark(String positionRePrintMark) {
        this.positionRePrintMark = positionRePrintMark;
    }

    /**
     * 電子ファイル作成区分を取得
     * @return 電子ファイル作成区分
     */
    public ElectronicFileCreateType getIsFileCreateType() {
        return isFileCreateType;
    }

    /**
     * 電子ファイル作成区分を設定
     * @param isFileCreateType 電子ファイル作成区分
     */
    public void setIsFileCreateType(ElectronicFileCreateType isFileCreateType) {
        this.isFileCreateType = isFileCreateType;
    }

    /**
     * PDF電子ファイル名を取得
     * @return PDF電子ファイル名
     */
    public String getPdfFileName() {
        return pdfFileName;
    }

    /**
     * PDF電子ファイル名を設定
     * @param pdfFileName PDF電子ファイル名
     */
    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

}
