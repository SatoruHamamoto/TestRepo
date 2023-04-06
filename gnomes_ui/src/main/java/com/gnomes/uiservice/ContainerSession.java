package com.gnomes.uiservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;

/**
 * コンテナセッション
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
@SessionScoped
public class ContainerSession implements Serializable {

    // ロガー
//    @Inject
//    transient Logger logger;

//    @Inject
//    ContainerRequest requestContext;

    // リクエスト
    ContainerRequest.REQUEST request = null;

    /**
     *
     */
    public ContainerSession() {
    }

    /**
     * @return request
     */
    public ContainerRequest.REQUEST getRequest() {
        return request;
    }

    public void setRedirect(String baseUri, String redirectCommand, Map<String, Object> redirectParameters,
            List<Object> traceMonitorList) {
        request = new ContainerRequest.REQUEST();
        request.setBaseUri(baseUri);
        request.setRedirect(true);
        request.setRedirectCommand(redirectCommand);
        request.setRedirectParameters(redirectParameters);
        request.setTraceMonitorList(traceMonitorList);
    }

    public void clear() {
        request = null;
    }
}
