package com.gnomes.common.data;

/**
 * アップロードファイルデータビーン
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/06 YJP/K.Fujiwara             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

public class FileUpLoadData {

    /**
     * 実ファイル名
     */
    private String fileName;

    /**
     * システム内ファイル名
     */
    private String systemFileName;

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
     * @return systemFileName
     */
    public String getSystemFileName() {
        return systemFileName;
    }

    /**
     * @param systemFileName セットする systemFileName
     */
    public void setSystemFileName(String systemFileName) {
        this.systemFileName = systemFileName;
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
