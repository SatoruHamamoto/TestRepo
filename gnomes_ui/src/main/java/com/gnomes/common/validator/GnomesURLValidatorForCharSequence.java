package com.gnomes.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.validator.routines.UrlValidator;

import com.gnomes.common.validator.constraints.GnomesURL;

/**
 * URLが正しいかどうか
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
public class GnomesURLValidatorForCharSequence implements ConstraintValidator<GnomesURL, CharSequence> {

    /**
     * URLバリデータ コンストラクター
     */
    public GnomesURLValidatorForCharSequence() {
    }

    @Override
    public void initialize(GnomesURL gnomesPast) {
    }

    @Override
    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        if (charSequence == null || charSequence.toString().trim().isEmpty()) {
            return true;
        }

        UrlValidator urlValidator = UrlValidator.getInstance();
        return urlValidator.isValid(charSequence.toString());
    }

}
