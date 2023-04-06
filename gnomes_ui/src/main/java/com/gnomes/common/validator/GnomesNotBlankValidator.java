package com.gnomes.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.validator.constraints.GnomesNotBlank;

/**
 * 空白バリデータ処理を行う。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/18 YJP/Y.Oota                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesNotBlankValidator implements ConstraintValidator<GnomesNotBlank, CharSequence> {

    /**
     * 空白バリデータ コンストラクター
     */
    public GnomesNotBlankValidator() {
	}

    @Override
	public void initialize(GnomesNotBlank annotation) {
	}

    @Override
	public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
		if (charSequence == null) {
			return true;
		}
		return charSequence.toString().trim().length() > 0;
	}
}
