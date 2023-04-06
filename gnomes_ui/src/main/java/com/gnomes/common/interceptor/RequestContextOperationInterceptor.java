package com.gnomes.common.interceptor;

import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.Unbound;

import com.gnomes.common.logging.LogHelper;


/**
 * RequestContextをマニュアルでアクティベートするインターセプター
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/06 YJP/K.Nakanishi           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@RequestContextOperation
public class RequestContextOperationInterceptor {

    /** The RequestContext */
    @Inject
    @Unbound
    private RequestContext m_requestContext;

    /**
     * @param p_invocationContext
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object activateRequestContext(InvocationContext p_invocationContext) throws Exception {
        try {
            m_requestContext.activate();
            return p_invocationContext.proceed();
        } finally {
            m_requestContext.invalidate();
            m_requestContext.deactivate();
        }
    }
}
