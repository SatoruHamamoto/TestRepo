package com.gnomes.common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.validator.GnomesDigitsVariableValidator;

/**
 * 小数点以下数値桁数（可変）バリデータアノテーション定義
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/02/28 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy={GnomesDigitsVariableValidator.class})
public @interface GnomesDigitsVariable {

    String message() default GnomesMessagesConstants.MV01_0031;

    String[] messageParams() default {"resourceId"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String maxFractionLengthField();

    String resourceId();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    public @interface List{
        GnomesDigitsVariable[] value();
    }
}
