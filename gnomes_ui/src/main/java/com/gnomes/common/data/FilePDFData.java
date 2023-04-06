package com.gnomes.common.data;

/**
 * PDFファイルデータビーン
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/02/15 YJP/A.Oomori             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class FilePDFData {

    /**
     * 実ファイル名
     */
    private String fileName;

    /**
     * 読込ファイルパス
     */
    private String loadFilePath;

    /**
     * ファイルデータ
     */
    private byte[] data;

    /**
     * 文字コード名
     */
    private String charsetName;

    /**
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName セットする fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return loadFilePath
     */
    public String getLoadFilePath() {
        return loadFilePath;
    }

    /**
     * @param loadFilePath セットする loadFilePath
     */
    public void setLoadFilePath(String loadFilePath) {
        this.loadFilePath = loadFilePath;
    }

    /**
     * @return data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data セットする data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return charsetName
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * @param charsetName セットする charsetName
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }





}
