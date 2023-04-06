package com.gnomes.external.logic.talend;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.external.dao.QueueExternalIfSendStatusDao;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 外部I/F送信状態キューデータ削除
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class DeleteSendStateJob extends BaseJobLogic {

    /** 外部I/F送信状態キュー Dao */
    @Inject
    protected QueueExternalIfSendStatusDao queueExternalIfSendStatusDao;

    /**
     * 外部I/F送信状態キュー削除
     */
    @ErrorHandling
    @TraceMonitor
    public void process() {

        // 外部I/F送信状態キュー削除
        QueueExternalIfSendStatus sendStatus = super.fileTransferBean.getQueueExternalIfSendStatus();
        this.queueExternalIfSendStatusDao.delete(sendStatus, super.fileTransferBean.getEml());

    }

}
