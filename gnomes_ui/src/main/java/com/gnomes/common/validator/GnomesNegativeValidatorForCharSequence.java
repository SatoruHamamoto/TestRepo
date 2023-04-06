package com.gnomes.common.validator;

import java.text.ParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.util.ConverterUtils;
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
public class GnomesNegativeValidatorForCharSequence implements ConstraintValidator<GnomesNegative, CharSequence> {

    /**
     * 数値≧0バリデーション（文字列） コンストラクター
     */
    public GnomesNegativeValidatorForCharSequence() {
    }

    @Override
    public void initialize(GnomesNegative parameters) {
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.toString().trim().isEmpty()) {
            return true;
        }
        try {
            if(0 <= ConverterUtils.stringToNumber(false, value.toString()).doubleValue()){
                return false;
            }
        } catch (ParseException e) {
        }
        return true;

    }

}
