package com.gnomes.common.util;
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/06/11 20:44 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

import java.lang.reflect.Method;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.Column;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;

/**
 * エンティティに関する共通のユーティリティ関数群
 *  今後拡張していく
 *
 * @author 03501213
 *
 */
@Dependent
public class GnomesEnitityUtils
{
    @Inject
    private GnomesExceptionFactory gnomesExceptionFactory;
    /**
     * エンティティのメソッド名につく@Column(length=xx)のxxを抜き出す
     *
     * @param className     @調べたいクラス
     * @param methodName    @Columnのついているクラス名
     * @return
     * @throws GnomesAppException
     */
    public int getColumnLength(Class classObj,String methodName) throws GnomesAppException{

        final String thisMethodName ="getColumnLength";

        //メソッドオブジェクトを取り出す。
        Method method = null;
        try {
            method = classObj.getMethod(methodName, new Class[] {});
        }
        catch (NoSuchMethodException e) {
            throw gnomesExceptionFactory.createGnomesAppException(e.getMessage(), e);
        }
        catch (SecurityException e) {
            throw gnomesExceptionFactory.createGnomesAppException(e.getMessage(), e);
        }
        //メソッドオブジェクトにくっついている"@Column"のアノテーションを
        Column column = method.getAnnotation(Column.class);

        //@Columnアノテーションがなかったら例外飛びます
        if(Objects.isNull(column)){
            throw gnomesExceptionFactory.createGnomesAppException(thisMethodName);
        }
        return column.length();
    }
}
