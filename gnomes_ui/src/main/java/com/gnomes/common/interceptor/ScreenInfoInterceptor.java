package com.gnomes.common.interceptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.gnomes.common.logging.LogHelper;
import com.gnomes.uiservice.ContainerRequest;

/**
 * 画面ID、画面名設定 インタセプタ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/21 YJP/H.Gojo             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@ScreenInfo
@Dependent
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class ScreenInfoInterceptor {

    //ロガー
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    ContainerRequest requestContext;

    /**
     * 画面ID、画面名設定
     *
     * @param ctx InvocationContext
     * @return String 取得値
     */
    @AroundInvoke
    public Object invoke(final InvocationContext ctx) throws Throwable {

        ScreenInfo annotation = (ctx.getMethod().isAnnotationPresent(ScreenInfo.class))
                                ? ctx.getMethod().getAnnotation(ScreenInfo.class)
                                : ctx.getTarget().getClass().getSuperclass().getAnnotation(ScreenInfo.class);


        if (annotation == null) {
            return ctx.proceed();
        }

        Object value = null;
        try {
            value = ctx.proceed();
        } catch (Throwable t) {
            throw t;
        }

        String name = annotation.name();

        // ContainerRequest に ScreenId、ScreanName を設定する。
        try {

            // PropertyDescriptor を生成
            PropertyDescriptor nameProp = new PropertyDescriptor(name, ContainerRequest.class);

            // セッターメソッドを取得
            Method nameSetter = nameProp.getWriteMethod();

            // セッターを使用して設定する
            nameSetter.invoke(requestContext, value);

        } catch (Exception e) {
            // 該当する名称がない場合は何もしない
            e.printStackTrace();
        }

        String className = ctx.getTarget().getClass().getSuperclass().getName();
        String methodName = ctx.getMethod().getName();

        this.logHelper.fine(this.logger, className, methodName, "ScreenInfoInterceptor Set " + ContainerRequest.class.getSimpleName() + "." + name + " to " + value);

        return value;
    }

}

