package com.gnomes.system.data.printout;

/**
 * 電子帳票情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/18 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class PrintOutInfo {

    /** 移動先ディレクトリパス */
    private String directoryPathTo;

    /** Excelファイル名 */
    private String excelFileName;

    /** PDFファイル名(リネーム前) */
    private String pdfFileNameFrom;

    /** PDFファイル名 */
    private String pdfFileName;

    /** Excelファイル情報 */
    private byte[] excelFileInfo;

    /** PDFファイル情報 */
    private byte[] pdfFileInfo;

    /** エラーメッセージ */
    private String errMessage;

    /**
     * 移動先ディレクトリパスを取得
     * @return 移動先ディレクトリパス
     */
    public String getDirectoryPathTo() {
        return directoryPathTo;
    }

    /**
     * 移動先ディレクトリパスを設定
     * @param directoryPathTo 移動先ディレクトリパス
     */
    public void setDirectoryPathTo(String directoryPathTo) {
        this.directoryPathTo = directoryPathTo;
    }

    /**
     * Excelファイル名を取得
     * @return Excelファイル名
     */
    public String getExcelFileName() {
        return excelFileName;
    }

    /**
     * Excelファイル名を設定
     * @param excelFileName Excelファイル名
     */
    public void setExcelFileName(String excelFileName) {
        this.excelFileName = excelFileName;
    }

    /**
     * PDFファイル名(リネーム前)を取得
     * @return PDFファイル名(リネーム前)
     */
    public String getPdfFileNameFrom() {
        return pdfFileNameFrom;
    }

    /**
     * PDFファイル名(リネーム前)を設定
     * @param pdfFileNameFrom PDFファイル名(リネーム前)
     */
    public void setPdfFileNameFrom(String pdfFileNameFrom) {
        this.pdfFileNameFrom = pdfFileNameFrom;
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
     * Excelファイル情報を取得
     * @return Excelファイル情報
     */
    public byte[] getExcelFileInfo() {
        return excelFileInfo;
    }

    /**
     * Excelファイル情報を設定
     * @param excelFileInfo Excelファイル情報
     */
    public void setExcelFileInfo(byte[] excelFileInfo) {
        this.excelFileInfo = excelFileInfo;
    }

    /**
     * PDFファイル情報を取得
     * @return PDFファイル情報
     */
    public byte[] getPdfFileInfo() {
        return pdfFileInfo;
    }

    /**
     * PDFファイル情報を設定
     * @param pdfFileInfo PDFファイル情報
     */
    public void setPdfFileInfo(byte[] pdfFileInfo) {
        this.pdfFileInfo = pdfFileInfo;
    }

    /**
     * エラーメッセージを取得
     * @return エラーメッセージ
     */
    public String getErrMessage() {
        return errMessage;
    }

    /**
     * エラーメッセージを設定
     * @param errMessage エラーメッセージ
     */
    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }



}
