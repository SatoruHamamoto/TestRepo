package com.gnomes.common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.ByteSizeValidator;

/**
 * バイト長バリデータアノテーション定義
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
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy={ByteSizeValidator.class})
public @interface ByteSize {

    String message() default GnomesMessagesConstants.MV01_0012;

    String[] messageParams() default {"resourceId", "size"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String charset() default "UTF-8";

    int size() default 0;

    String resourceId();

    String inputDomainId() default "";

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    public @interface List{
        ByteSize[] value();
    }
}
