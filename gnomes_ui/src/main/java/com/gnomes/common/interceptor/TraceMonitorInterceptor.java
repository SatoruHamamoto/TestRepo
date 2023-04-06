package com.gnomes.common.interceptor;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.gnomes.common.util.StringUtils;
import com.gnomes.uiservice.ContainerRequest;

/**
 * トレースモニター  クラス名、メソッド名、パラメータの保持
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@TraceMonitor
@Dependent
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class TraceMonitorInterceptor
{

    @Inject
    ContainerRequest requestContext;

    @AroundInvoke
    public Object traceMonitorHandling(InvocationContext context) throws Exception
    {

        TraceMonitor anno = context.getMethod().isAnnotationPresent(TraceMonitor.class)
                ? context.getMethod().getAnnotation(TraceMonitor.class)
                : context.getTarget().getClass().getSuperclass().getAnnotation(TraceMonitor.class);

        if (anno == null) {
            // メソッド実行
            return context.proceed();
        }

        String clazz = context.getTarget().getClass().getSuperclass().getName();
        String method = context.getMethod().getName();
        String args = (context.getParameters() != null && 0 < context.getParameters().length)
                ? StringUtils.joinString(context.getParameters())
                : "";

        long start = System.currentTimeMillis();

        // トレースログ開始追加
        requestContext.addTraceMonitorStart(anno.prefixStarted(), clazz, method, args);

        Object completed = null;
        Throwable suspened = null;

        try {
            // メソッド実行
            completed = context.proceed();
            return completed;
        } catch (Throwable t) {
            suspened = t;
            throw t;
        }
        finally {
            long end = System.currentTimeMillis();

            // トレースログ終了追加
            if (suspened == null) {
                requestContext.addTraceMonitorFinally(anno.prefixCompleted(), clazz, method, args, String.format(
                        "return [%s] ........ Time is %s.", (completed != null) ? completed : "NULL",
                        (end - start) + "ms"));
            } else {
                requestContext.addTraceMonitorFinally(anno.prefixSuspended(), clazz, method, args, String.format(
                        "throw [%s] '%s' ........ Time is %s ms.", suspened.getClass().getName(), suspened.getMessage(),
                        (end - start) + "ms"));
            }

        }
    }

}
