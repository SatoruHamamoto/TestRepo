package com.gnomes.common.validator.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.GnomesDigitsValidatorForBigDecimal;
import com.gnomes.common.validator.GnomesDigitsValidatorForCharSequence;

/**
 * 数値桁数バリデータアノテーション定義
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
@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD,
		java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
		java.lang.annotation.ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {GnomesDigitsValidatorForCharSequence.class, GnomesDigitsValidatorForBigDecimal.class})
public @interface GnomesDigits {
	String message() default GnomesMessagesConstants.MV01_0003;

    String[] messageParams() default {"resourceId", "integer", "fraction"};

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int integer() default 0;

	int fraction() default 0;

	String resourceId();

	String inputDomainIdIntegerDigit() default "";

	String inputDomainIdDecimalPlace() default "";

	@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD,
			java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
			java.lang.annotation.ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface List {
		GnomesDigits[] value();
	}
}
