package com.gnomes.rest.service;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

/**
 * JAX-RS Web サービスリソース  基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/01 YJP/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class BaseService {

    @Inject
    protected
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected
    ContainerRequest req;

    @Inject
    protected
    ContainerResponse responseContext;

    @Inject
    protected
    GnomesSessionBean gnomesSessionBean;

    @Inject
    protected
    GnomesSystemBean gnomesSystemBean;

    @Inject
    protected
    GnomesExceptionFactory exceptionFactory;

    /**
     * デフォルトコンストラクタ
     */
    public BaseService() {
    }

    /**
     * リクエスト情報を設定
     */
    public void setRequest(BaseServiceParam baseParam) {

        this.req.setEventId(baseParam.getEventId());                         // イベントID
        this.req.setAreaId(baseParam.getAreaId());                           // エリアID
        this.req.setAreaName(baseParam.getAreaName());                       // エリア名
        this.req.setComputerName(baseParam.getComputerName());               // コンピュータ名
        this.req.setIpAddress(baseParam.getIpAddress());                     // IPアドレス
        this.req.setLanguage(baseParam.getLanguage());                       // 言語
        this.req.setUserLocale(baseParam.getUserLocale());                   // ユーザロケール
        this.req.setSiteCode(baseParam.getSiteCode());                       // 拠点コード
        this.req.setSiteName(baseParam.getSiteName());                       // 拠点名
        this.req.setUserId(baseParam.getUserId());                           // ユーザID
        this.req.setUserName(baseParam.getUserName());                       // ユーザ名
        this.req.setUserKey(baseParam.getUserKey());                         // ユーザKey
        this.req.setScreenId(baseParam.getScreenId());                       // 画面ID
        this.req.setScreenName(baseParam.getScreenName());                   // 画面名
        this.req.setCommandId(baseParam.getCommandId());                     // コマンドID
        this.req.setTraceClazzNameList(baseParam.getTraceClazzNameList());   // トレースクラス名
        this.req.setTraceMethodNameList(baseParam.getTraceMethodNameList()); // トレースメソッド名

    }

}
