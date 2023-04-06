package com.gnomes.common.validator;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.validator.constraints.GnomesNegative;

/**
 * チェックする数値≧0の場合、エラー
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/22 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesNegativeValidatorForNumber implements ConstraintValidator<GnomesNegative, Number> {

    /**
     * 数値≧0バリデーション（数値） コンストラクター
     */
    public GnomesNegativeValidatorForNumber() {
    }

    @Override
    public void initialize(GnomesNegative constraintAnnotation) {
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if ((value instanceof Double)) {
            if (((Double) value).doubleValue() <= Double.NEGATIVE_INFINITY) {
                return true;
            }
            if ((Double.isNaN(((Double) value).doubleValue()))
                    || (((Double) value).doubleValue() >= Double.POSITIVE_INFINITY)) {
                return false;
            }
        } else if ((value instanceof Float)) {
            if (((Float) value).floatValue() <= Float.NEGATIVE_INFINITY) {
                return true;
            }
            if ((Float.isNaN(((Float) value).floatValue()))
                    || (((Float) value).floatValue() >= Float.POSITIVE_INFINITY)) {
                return false;
            }
        }

        int comparisonResult;
        //int comparisonResult;
        if ((value instanceof BigDecimal)) {
            comparisonResult = ((BigDecimal) value).compareTo(BigDecimal.ZERO);
        } else {
            //int comparisonResult;
            if ((value instanceof BigInteger)) {
                comparisonResult = new BigDecimal((BigInteger) value).compareTo(BigDecimal.ZERO);
            } else {
                //int comparisonResult;
                if ((value instanceof Long)) {
                    comparisonResult = BigDecimal.valueOf(value.longValue()).compareTo(BigDecimal.ZERO);
                } else {
                    //int comparisonResult;
                    if ((value instanceof Float)) {
                        comparisonResult = BigDecimal.valueOf(value.floatValue()).compareTo(BigDecimal.ZERO);
                    } else {
                        comparisonResult = BigDecimal.valueOf(value.doubleValue()).compareTo(BigDecimal.ZERO);
                    }
                }
            }
        }
        return comparisonResult < 0;
    }

}
