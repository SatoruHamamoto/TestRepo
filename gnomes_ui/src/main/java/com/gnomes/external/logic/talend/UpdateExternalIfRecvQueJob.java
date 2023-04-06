package com.gnomes.external.logic.talend;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.external.dao.QueueExternalIfRecvDao;
import com.gnomes.external.entity.QueueExternalIfRecv;

/**
 * 外部IF受信キュー更新
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class UpdateExternalIfRecvQueJob extends BaseJobLogic {

    /** 外部I/F受信キュー Dao */
    @Inject
    protected QueueExternalIfRecvDao queueExternalIfRecvDao;

    /**
     * 外部IF受信キュー更新
     */
    @ErrorHandling
    @TraceMonitor
    public void process() {

        // 外部I/F受信キュー更新
        QueueExternalIfRecv externalIfRecvQue = super.fileTransferBean.getQueueExternalIfRecv();
        externalIfRecvQue.setRecv_status(super.fileTransferBean.getStatus().getValue());
        this.queueExternalIfRecvDao.update(externalIfRecvQue, super.fileTransferBean.getEml());

    }

}
