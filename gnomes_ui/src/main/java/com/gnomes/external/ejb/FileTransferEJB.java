package com.gnomes.external.ejb;


import java.util.Properties;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.picketbox.util.StringUtil;

import com.gnomes.common.batch.batchlet.BaseEJB;
import com.gnomes.common.batch.batchlet.EJBWrapper;
import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.logic.BLFileTransfer;

/**
 * ファイル送受信 EJB クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/28 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Stateless
public class FileTransferEJB extends BaseEJB {

    /**
     * ファイル送受信 ビジネス クラス
     */
    @Inject
    protected BLFileTransfer bLFileTransfer;

    /**
     * HTTPServerRequest
     */
    @Inject
    protected HttpServletRequest request;

    /**
     * ロジックファクトリー
     */
    @Inject
    transient LogicFactory logicFactory;


    /**
     * コンストラクター
     */
    public FileTransferEJB() {

    }

    /**
     * 受信要求
     * @param p
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void recvRequest(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String dataType = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0056));
        String recvFileName = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0057));

        bLFileTransfer.recvRequest(dataType, recvFileName);

    }

    /**
     * 受信定周期処理
     * @param p
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void recvProc(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String externalTargetCode = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0055));

        bLFileTransfer.recvProc(externalTargetCode);

    }

    /**
     * 受信状態変更
     * @param p
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void recvChangeState(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String externalIfRecvQueKey = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0058));
        Integer isChangeStateflag = null;
        String changeFlag = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0059));

        if (!StringUtil.isNullOrEmpty(changeFlag)) {
            isChangeStateflag = Integer.valueOf(changeFlag);
        }
        bLFileTransfer.recvChangeState(externalIfRecvQueKey, isChangeStateflag);

    }

    /**
     * ファイル送信処理
     * @param p
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void sendProc(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String externalTargetCode = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0055));

        bLFileTransfer.sendProc(externalTargetCode, null, null);

    }

    /**
     * ファイル送信結果
     * @param p
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void sendResult(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String externalTargetCode = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0055));
        Integer isSendSuccess = null;
        String sendSuccess = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0060));
        if (!StringUtil.isNullOrEmpty(sendSuccess)) {
            isSendSuccess = Integer.valueOf(sendSuccess);
        }
        bLFileTransfer.sendResult(externalTargetCode, isSendSuccess);

    }

    /**
     * ファイル送信状態変更
     * @param p
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void sendChangeState(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String sendStateKey = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0061));
        Integer isChangeStateflag = null;
        String chageStateFlag = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0059));
        if (!StringUtil.isNullOrEmpty(chageStateFlag)) {
            isChangeStateflag = Integer.valueOf(chageStateFlag);
        }
        bLFileTransfer.sendChangeState(sendStateKey, isChangeStateflag);

    }
}
