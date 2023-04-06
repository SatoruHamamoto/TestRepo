package com.gnomes.external.logic.talend;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.dao.QueueExternalIfSendStatusDao;
import com.gnomes.external.entity.ExternalIfSendDataDetail;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 送信状態取得
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class GetSendStateJob extends BaseJobLogic
{

    /** 外部I/F送信状態キュー Dao */
    @Inject
    protected QueueExternalIfSendStatusDao queueExternalIfSendStatusDao;

    @Inject
    protected GnomesEntityManager     em;

    /**
     * 送信状態取得
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    @GnomesTransactional
    public void process() throws Exception
    {

        // X001:外部送信データ詳細 取得
        List<QueueExternalIfSendStatus> sendStateList = this.queueExternalIfSendStatusDao.getSendStateQuery(
                SendRecvStateType.Request.getValue(), fileTransferBean.getExternalTargetCode(), em.getEntityManager());

        if (Objects.isNull(sendStateList) || sendStateList.isEmpty()) {
            // ME01.0116:「対象の送信データは存在しません。」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0116);

        }

        super.fileTransferBean.setSendStateList(sendStateList);
        List<ExternalIfSendDataDetail> sendDataDetailList = new ArrayList<>(sendStateList.get(
                0).getExternalIfSendDataDetails());
        super.fileTransferBean.setExternalIfSendDataDetail(sendDataDetailList);
    }
}
