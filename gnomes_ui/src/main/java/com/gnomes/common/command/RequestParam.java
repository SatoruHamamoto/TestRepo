package com.gnomes.common.command;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * リクエストパラメーター 限定子。
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
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface RequestParam {

    public enum DataType {
        TEXT,           // 文字
        DATE,           // 日付
        NUMBER,         // 数値
        CURRENCY,       // 通貨
        JSON            // JSON エスケープ デコード
    }

    /**
     * HTTPリクエストパラメーター名称。
     * <pre>
     * 未指定の場合、インジェクションするフィールド名を名称とします。
     * </pre>
     * @return name
     */
    @Nonbinding
    String value() default "";

    /**
     * データタイプ
     * @return DataType
     */
    @Nonbinding
    DataType dataType() default DataType.TEXT;

    /**
     * 項目名のリソースID
     * @return String
     */
    @Nonbinding
    String resourceId() default "";


    /**
     * フォーマットのリソースID
     * @return String
     */
    @Nonbinding
    String formatId() default "";
}
