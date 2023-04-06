package com.gnomes.equipment.data;


/**
 * 設備I/F読取データ情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/01/24 YJP/S.Kohno               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class EquipmentIfReadData {

    /** パラメータ項目ID */
    private String parameterItemId;

    /** パラメータ項目値 */
    private String value;

    /** 読取品質Goodフラグ */
    private Boolean isGoodQuality;


    /**
     * パラメータ項目ID取得
     * @return パラメータ項目ID
     */
    public String getParameterItemId() {
        return parameterItemId;
    }

    /**
     * パラメータ項目ID設定
     * @param parameterItemId パラメータ項目ID
     */
    public void setParameterItemId(String parameterItemId) {
        this.parameterItemId = parameterItemId;
    }

    /**
     * パラメータ項目値取得
     * @return パラメータ項目値
     */
    public String getValue() {
        return value;
    }

    /**
     * パラメータ項目値設定
     * @param value パラメータ項目値
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 読取品質Goodフラグ取得
     * @return 読取品質Goodフラグ
     */
    public Boolean getIsGoodQuality() {
        return isGoodQuality;
    }

    /**
     * 読取品質Goodフラグ設定
     * @param isGoodQuality 読取品質Goodフラグ
     */
    public void setIsGoodQuality(Boolean isGoodQuality) {
        this.isGoodQuality = isGoodQuality;
    }
}
