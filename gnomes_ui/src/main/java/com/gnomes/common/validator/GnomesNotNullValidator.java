package com.gnomes.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.validator.constraints.GnomesNotNull;

/**
 * NULLバリデータ処理を行う。
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
public class GnomesNotNullValidator implements ConstraintValidator<GnomesNotNull, Object> {

    /**
     *  NULLバリデータ コンストラクター
     */
    public GnomesNotNullValidator() {
	}

    @Override
	public void initialize(GnomesNotNull parameters) {
	}

    @Override
	public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
		return object != null;
	}
}
