package com.gnomes.external.data;

import java.math.BigDecimal;

import com.gnomes.common.exception.GnomesAppException;

/**
 * 秤量結果
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class WeighResult {

    /** 秤量値 */
    private BigDecimal weighValue;

    /** 単位 */
    private String unit;

    /** 安定値フラグ */
    private Boolean stableValue;

    /** エラー情報. */
    private GnomesAppException gnomesAppException;

    /**
     * コンストラクタ
     */
    public WeighResult() {

        this.weighValue = null;
        this.unit = null;
        this.stableValue = null;
        this.gnomesAppException = null;

    }

    /**
     * 秤量値を取得
     * @return 秤量値
     */
    public BigDecimal getWeighValue() {
        return weighValue;
    }

    /**
     * 秤量値を設定
     * @param weighValue 秤量値
     */
    public void setWeighValue(BigDecimal weighValue) {
        this.weighValue = weighValue;
    }

    /**
     * 単位を取得
     * @return 単位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 単位を設定
     * @param unit 単位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 安定値フラグを取得
     * @return 安定値フラグ
     */
    public Boolean isStableValue() {
        return stableValue;
    }

    /**
     * 安定値フラグを設定
     * @param stableValue 安定値フラグ
     */
    public void setStableValue(Boolean stableValue) {
        this.stableValue = stableValue;
    }

    /**
     * エラー情報を取得
     * @return エラー情報
     */
    public GnomesAppException getGnomesAppException() {
        return gnomesAppException;
    }

    /**
     * エラー情報を設定
     * @param gnomesAppException エラー情報
     */
    public void setGnomesAppException(GnomesAppException gnomesAppException) {
        this.gnomesAppException = gnomesAppException;
    }

}
