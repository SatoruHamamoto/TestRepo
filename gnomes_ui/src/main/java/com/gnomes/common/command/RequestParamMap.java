package com.gnomes.common.command;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target( { METHOD, FIELD } )
public @interface RequestParamMap {

    /**
     * HTTPリクエストパラメーター名称。
     * <pre>
     * 未指定の場合、インジェクションするフィールド名を名称とします。
     * </pre>
     * @return String
     */
    @Nonbinding
    String value() default "";

    /**
     * 拡張HTTPリクエストパラメーター名称
     * ２このリクエストパラメーターから生成時に使用
     * @return String
     */
    @Nonbinding
    String extValue() default "";

    /**
     * 項目名のリソースID
     * @return String
     */
    @Nonbinding
    String resourceId() default "";


    /**
     * 日付型フォーマットのリソースID
     * @return String
     */
    @Nonbinding
    String dateFormatResourceId() default "";

    @Nonbinding
    boolean isCurrency() default false;
}
