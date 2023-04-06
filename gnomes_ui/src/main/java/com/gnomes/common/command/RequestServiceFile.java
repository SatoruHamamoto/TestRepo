package com.gnomes.common.command;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * リクエストサービスファイル 限定子。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface RequestServiceFile {

    public enum MultiPartFileType {
        CSVFILE, // CSVファイル
        EXCEL // XLS,XLSXファイル
    }

    /**
     * HTTPリクエストパラメーター名称。
     * <pre>
     * 未指定の場合、インジェクションするフィールド名を名称とします。
     * </pre>
     * @return name
     */
    @Nonbinding
    String[] value() default { "" };

    /**
     * データタイプ
     * @return DataType
     */
    @Nonbinding
    MultiPartFileType fileType() default MultiPartFileType.CSVFILE;

    /**
     * 文字コード名
     * @return String
     */
    @Nonbinding
    String charsetName() default "";

    /**
     * 項目名のリソースID
     * @return String
     */
    @Nonbinding
    String resourceId() default "";

    /**
     * データクラス
     * @return Class<?>[]
     */
    @Nonbinding
    Class<?>[] dataClazz() default Object.class;

    /**
     * シート名
     * @return String[]
     */
    @Nonbinding
    String[] sheetName() default { "" };

    /**
     * 定義キー
     * @return String[]
     */
    @Nonbinding
    String[] definitionKey() default { "" };

}
