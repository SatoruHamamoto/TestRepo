package com.gnomes.external.logic.talend;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.external.dao.QueueExternalIfSendStatusDao;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 送信状態の更新
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.SHibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Named("UpdateSendStateJob")
public class UpdateSendStateJob extends BaseJobLogic {

    /** 外部I/F送信状態キュー Dao */
    @Inject
    protected QueueExternalIfSendStatusDao queueExternalIfSendStatusDao;

    /**
     * 送信状態更新
     * @return 更新成功フラグ
     */
    @ErrorHandling
    @TraceMonitor
    public void process() {

        // 送信状態 更新
        QueueExternalIfSendStatus sendStatus = super.fileTransferBean.getQueueExternalIfSendStatus();
        sendStatus.setSend_status(fileTransferBean.getStatus().getValue());
        this.queueExternalIfSendStatusDao.update(sendStatus, super.fileTransferBean.getEml());

    }

}
