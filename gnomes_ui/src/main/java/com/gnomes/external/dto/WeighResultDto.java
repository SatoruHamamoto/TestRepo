package com.gnomes.external.dto;

import java.math.BigDecimal;

import com.gnomes.common.constants.CommonEnums.WeighProcessResultType;
import com.gnomes.common.exception.GnomesAppException;

/**
 * 秤量結果クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/07/05 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class WeighResultDto {

    /** 秤量値. */
    private BigDecimal weighedValue;

    /** GnomesAppException. */
    private GnomesAppException gnomesAppException;

    /** 秤量処理結果区分. */
    private WeighProcessResultType weighProcessResultType;

    /**
     * コンストラクタ.
     */
    public WeighResultDto() {

        this.weighedValue = BigDecimal.ZERO;
        this.gnomesAppException = null;
        this.weighProcessResultType = WeighProcessResultType.Failure;

    }

    /**
     * 秤量値を取得.
     * @return 秤量値
     */
    public BigDecimal getWeighedValue() {
        return weighedValue;
    }

    /**
     * 秤量値を設定.
     * @param weighedValue 秤量値
     */
    public void setWeighedValue(BigDecimal weighedValue) {
        this.weighedValue = weighedValue;
    }

    /**
     * GnomesAppExceptionを設定
     * @return GnomesAppException
     */
    public GnomesAppException getGnomesAppException() {
        return gnomesAppException;
    }

    /**
     * GnomesAppExceptionを取得
     * @param gnomesAppException
     */
    public void setGnomesAppException(GnomesAppException gnomesAppException) {
        this.gnomesAppException = gnomesAppException;
    }

    /**
     * 秤量処理結果区分を取得.
     * @return 秤量処理結果区分
     */
    public WeighProcessResultType getWeighProcessResultType() {
        return weighProcessResultType;
    }

    /**
     * 秤量処理結果区分を設定.
     * @param weighProcessResultType 秤量処理結果区分
     */
    public void setWeighProcessResultType(WeighProcessResultType weighProcessResultType) {
        this.weighProcessResultType = weighProcessResultType;
    }

}
