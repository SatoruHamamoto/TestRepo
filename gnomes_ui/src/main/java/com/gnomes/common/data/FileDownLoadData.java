package com.gnomes.common.data;

/**
 * ダウンロードファイルデータビーン
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
public class FileDownLoadData {


    /**
     * 保存ファイル名
     */
    private String saveFileName;

    /**
     * 読込ファイル
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
     * @return saveFileName
     */
    public String getSaveFileName() {
        return saveFileName;
    }

    /**
     * @param saveFileName セットする saveFileName
     */
    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
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
