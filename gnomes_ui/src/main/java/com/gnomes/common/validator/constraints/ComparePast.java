package com.gnomes.common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

//import gnomes.sample.validation.ByteSizeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.ComparePastValidator;

/**
 * 日付相関バリデータアノテーション定義
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
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy={ComparePastValidator.class})
public @interface ComparePast {

    String message() default GnomesMessagesConstants.MV01_0011;

    String[] messageParams() default {"resourceId", "compareResourceId"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String compareField();

    String fieldFormatId() default "";

    String compareFieldFormatId() default "";

    String resourceId();

    String compareResourceId();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    public @interface List{
        ComparePast[] value();
    }
}
