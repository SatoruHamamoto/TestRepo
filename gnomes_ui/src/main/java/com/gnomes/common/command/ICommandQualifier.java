package com.gnomes.common.command;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * コマンド 限定子。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/01 YJP/K.Gotanda             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Qualifier
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ICommandQualifier {

    /**
     * コマンド名称。
     * @return name
     */
    public String value() default "";
}
