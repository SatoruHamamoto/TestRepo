package com.gnomes.common.interceptor;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import javax.interceptor.InterceptorBinding;

/**
 * 画面ID、画面名の設定用 限定子。
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
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface ScreenInfo {

    /**
     * 画面ID、画面名の設定、設定時に RequestCotainer にも設定する
     * <pre>
     * インジェクションするフィールド名を名称とします。
     * </pre>
     */
    @Nonbinding
    String name() default "";
}
