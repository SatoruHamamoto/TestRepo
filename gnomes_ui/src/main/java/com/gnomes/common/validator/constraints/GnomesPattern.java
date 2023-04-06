package com.gnomes.common.validator.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.GnomesPatternValidator;

/**
 * 文字列パターンバリデータアノテーション定義
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
@Constraint(validatedBy = {GnomesPatternValidator.class})
public @interface GnomesPattern {
	String regexp() default "";

	Flag[] flags() default {};

	String message() default GnomesMessagesConstants.MV01_0009;

    String[] messageParams() default {"resourceId"};

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String resourceId();

	String inputDomainId() default "";

	@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD,
			java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
			java.lang.annotation.ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface List {
	    GnomesPattern[] value();
	}

	public static enum Flag {
		UNIX_LINES(1), CASE_INSENSITIVE(2), COMMENTS(4), MULTILINE(8), DOTALL(32), UNICODE_CASE(64), CANON_EQ(128);

		private final int value;

		private Flag(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}
}
