package com.gnomes.common.validator.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.GnomesNotBlankValidator;

/**
 * 空白バリデータアノテーション定義
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
@Constraint(validatedBy = {GnomesNotBlankValidator.class})
@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD,
        java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
        java.lang.annotation.ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@NotNull
public @interface GnomesNotBlank {
    String message() default GnomesMessagesConstants.MV01_0002;

    String[] messageParams() default {"resourceId"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String resourceId();

    @Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
            java.lang.annotation.ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface List {
        GnomesNotBlank[] value();
    }
}
