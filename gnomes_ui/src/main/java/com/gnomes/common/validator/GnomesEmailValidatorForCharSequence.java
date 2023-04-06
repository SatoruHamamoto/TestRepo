package com.gnomes.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.validator.routines.EmailValidator;

import com.gnomes.common.validator.constraints.GnomesEmail;

/**
 * 文字列がメールアドレスのフォーマットであるか
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
public class GnomesEmailValidatorForCharSequence implements ConstraintValidator<GnomesEmail, CharSequence> {

    /**
     * メールアドレスバリデーション コンストラクター
     */
    public GnomesEmailValidatorForCharSequence() {
    }

    @Override
    public void initialize(GnomesEmail gnomesPast) {
    }

    @Override
    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        if (charSequence == null || charSequence.toString().trim().isEmpty()) {
            return true;
        }
        EmailValidator ev = EmailValidator.getInstance(true);
        return ev.isValid(charSequence.toString());
    }

}
