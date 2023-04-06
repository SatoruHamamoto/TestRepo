package com.gnomes.common.importdata;

import java.util.Map;

/**
 * インポートデータカラム 定義情報クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ImportExportColumnDef {

    /**
     * カラムタイプ
     */
    public enum ImportExportColumnDataType {
        TEXT,           // 文字
        DATE,           // 日付
        ZONEDDATETIME,  // 日付タイムゾーン
        NUMBER,         // 数値
        CURRENCY,       // 通貨
        IMG_PATTERN     // イメージパターン
    }

    /** ヘッダ項目名のリソースID */
    private String headerResouceId;

    /** フォーマットのリソースID */
    //    private String formatResourceId;

    /** カラムデータタイプ */
    private ImportExportColumnDataType dataType;

    /** フィールド名 */
    private String fieldName;

    /** 取込位置 */
    private int position;

    /** フォーマット */
    private String format;

    /** 少数桁のフィールド名 */
    private String decimalPointFieldName;

    /** イメージパターン */
    private Map<String, Object> mapImgPtn;

    /**
     * @return headerResouceId
     */
    public String getHeaderResouceId() {
        return headerResouceId;
    }

    /**
     * @param headerResouceId セットする headerResouceId
     */
    public void setHeaderResouceId(String headerResouceId) {
        this.headerResouceId = headerResouceId;
    }

    /**
     * @return dateType
     */
    public ImportExportColumnDataType getDataType() {
        return dataType;
    }

    /**
     * @param dateType セットする dateType
     */
    public void setDataType(ImportExportColumnDataType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName セットする fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position セットする position
     */
    public void setPosition(int position) {
        this.position = position;
    }


    /**
     * @return decimalPointFieldName
     */
    public String getDecimalPointFieldName() {
        return decimalPointFieldName;
    }

    /**
     * @param decimalPointFieldName セットする decimalPointFieldName
     */
    public void setDecimalPointFieldName(String decimalPointFieldName) {
        this.decimalPointFieldName = decimalPointFieldName;
    }

    /**
     * @return format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format セットする format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return mapImgPtn
     */
    public Map<String, Object> getMapImgPtn() {
        return mapImgPtn;
    }

    /**
     * @param mapImgPtn セットする mapImgPtn
     */
    public void setMapImgPtn(Map<String, Object> mapImgPtn) {
        this.mapImgPtn = mapImgPtn;
    }

}
