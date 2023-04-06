package com.gnomes.common.validator.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.GnomesURLValidatorForCharSequence;

/**
 * URLバリデータアノテーション定義
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
@Constraint(validatedBy = {GnomesURLValidatorForCharSequence.class})
public @interface GnomesURL {
    String message() default GnomesMessagesConstants.MV01_0025;

    String[] messageParams() default {"resourceId"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String resourceId();

    @Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
            java.lang.annotation.ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)

    public static @interface List {
        GnomesURL[] value();
    }
}
