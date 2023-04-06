package com.gnomes.external.batchlet;

import java.util.Properties;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.batch.batchlet.BaseBatchlet;
import com.gnomes.common.data.EventDrivenFromBatchData;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.ejb.FileTransferEJB;
import com.gnomes.rest.client.DemandGnomesServiceRun;

/**
 * 受信要求 バッチレット クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
@Named
public class RecvRequestBatchlet extends BaseBatchlet {

    @EJB
    FileTransferEJB fileTransferEJB;

    @Inject
    private GnomesEjbBean ejbBean;

    @Inject
    EventDrivenFromBatchData eventDrivenFromBatchData;

    @Inject
    DemandGnomesServiceRun demandGnomesServiceRun;

    /** 送受信 トランスファー */
    @Inject
    protected FileTransferBean fileTransferBean;

    /** 外部IF受信コマンド */
    private static final String RECV_IF_COMMAND = "S01005C104";

    @Override
    public String process() throws Exception {

        ejbBean.setEjbBatch(true);

        // ビジネスロジック実行
        fileTransferEJB.recvRequest(this.getParameters());

        // 受信バッチコマンドパラメータ取得
        String externalTargetCode = fileTransferBean.getExternalTargetCode();

        // 受信バッチコマンド実行
        eventDrivenFromBatchData.setCommandAfterBatchCommandId(RECV_IF_COMMAND, externalTargetCode);
        demandGnomesServiceRun.execute();

        return PROCESS_RESULT_COMPLETED;
    }

    @Override
    public void setBatchCommonRequestPartameter(Properties p) {

    }
}
