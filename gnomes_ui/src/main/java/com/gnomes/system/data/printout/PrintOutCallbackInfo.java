package com.gnomes.system.data.printout;

import com.gnomes.rest.service.BaseServiceParam;

/**
 * 帳票印刷コールバック情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/04 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class PrintOutCallbackInfo extends BaseServiceParam {

    /** 印字 */
    public static final String PRINT = "1";

    /** 再印字 */
    public static final String REPRINT = "2";

    /** ReportID */
    private String reportId;

    /** 帳票印字エラーメッセージ */
    private String printErrorMsg;

    /** パス名 */
    private String pathName;

    /** PDFファイル名 */
    private String pdfFileName;

    /** 印字種別 */
    private String printType;

    /** 再印字対象ファイル名 */
    private String rePrintFileName;

    /** 印字枚数 */
    private int printoutCopies;

    /** 印字依頼回数 */
    private int printRequestCount;

    /** nk要求イベントID */
    private String requestEventId;

    /** nk要求内連番 */
    private int requestSeq;

    /**
     * ReportIDを取得
     * @return ReportID
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * ReportIDを設定
     * @param reportId ReportID
     */
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    /**
     * 帳票印字エラーメッセージを取得
     * @return 帳票印字エラーメッセージ
     */
    public String getPrintErrorMsg() {
        return printErrorMsg;
    }

    /**
     * 帳票印字エラーメッセージを設定
     * @param printErrorMsg 帳票印字エラーメッセージ
     */
    public void setPrintErrorMsg(String printErrorMsg) {
        this.printErrorMsg = printErrorMsg;
    }

    /**
     * パス名を取得
     * @return パス名
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * パス名を設定
     * @param pathName パス名
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /**
     * PDFファイル名を取得
     * @return PDFファイル名
     */
    public String getPdfFileName() {
        return pdfFileName;
    }

    /**
     * PDFファイル名を設定
     * @param pdfFileName PDFファイル名
     */
    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    /**
     * 印字種別を取得
     * @return 印字種別
     */
    public String getPrintType() {
        return printType;
    }

    /**
     * 印字種別を設定
     * @param printType 印字種別
     */
    public void setPrintType(String printType) {
        this.printType = printType;
    }

    /**
     * 再印字対象ファイル名を取得
     * @return 再印字対象ファイル名
     */
    public String getRePrintFileName() {
        return rePrintFileName;
    }

    /**
     * 再印字対象ファイル名を設定
     * @param rePrintFileName 再印字対象ファイル名
     */
    public void setRePrintFileName(String rePrintFileName) {
        this.rePrintFileName = rePrintFileName;
    }

    /**
     * 印字枚数を取得
     * @return 印字枚数
     */
    public int getPrintoutCopies() {
        return printoutCopies;
    }

    /**
     * 印字枚数を設定
     * @param printoutCopies 印字枚数
     */
    public void setPrintoutCopies(int printoutCopies) {
        this.printoutCopies = printoutCopies;
    }

    /**
     * 現印字枚数を取得
     * @return 現印字枚数
     */
    public int getPrintRequestCount() {
        return printRequestCount;
    }

    /**
     * 印字依頼回数を設定
     * @param printRequestCount 印字依頼回数
     */
    public void setPrintRequestCount(int printRequestCount) {
        this.printRequestCount = printRequestCount;
    }

    /**
     * nk要求イベントIDを取得
     * @return nk要求イベントID
     */
    public String getRequestEventId() {
        return requestEventId;
    }

    /**
     * nk要求イベントIDを設定
     * @param requestEventId nk要求イベントID
     */
    public void setRequestEventId(String requestEventId) {
        this.requestEventId = requestEventId;
    }
    /**
     * nk要求内連番を取得
     * @return nk要求内連番
     */
    public int getRequestSeq() {
        return requestSeq;
    }

    /**
     * nk要求内連番を設定
     * @param requestSeq nk要求内連番
     */
    public void setRequestSeq(int requestSeq) {
        this.requestSeq = requestSeq;
    }

}
