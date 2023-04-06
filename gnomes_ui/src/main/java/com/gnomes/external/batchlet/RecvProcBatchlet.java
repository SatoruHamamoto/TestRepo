package com.gnomes.external.batchlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketbox.util.StringUtil;

import com.gnomes.common.batch.batchlet.BaseBatchlet;
import com.gnomes.common.data.EventDrivenFromBatchData;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.external.ejb.FileTransferEJB;
import com.gnomes.rest.client.DemandGnomesServiceRun;

/**
 * 受信定周期処理 バッチレット クラス
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
public class RecvProcBatchlet extends BaseBatchlet {

    @EJB
    FileTransferEJB fileTransferEJB;

    @Inject
    private GnomesEjbBean ejbBean;

    @Inject
    EventDrivenFromBatchData eventDrivenFromBatchData;

    @Inject
    DemandGnomesServiceRun demandGnomesServiceRun;

    /** 外部IF送信コマンド */
    private static final String SEND_IF_COMMAND = "S01005C200";

    @Override
    public String process() throws Exception {

        ejbBean.setEjbBatch(true);

        // ビジネスロジック実行
        fileTransferEJB.recvProc(this.getParameters());

        // 送信バッチコマンドパラメータ取得
        List<String> externalTargetCodeList = new ArrayList<>();
        externalTargetCodeList = eventDrivenFromBatchData.getSendProcParameter();

        // 受信処理内で送信処理があれば送信バッチコマンド実行
        if(externalTargetCodeList.size() != 0){
            for(String externalTargetCode : externalTargetCodeList){
                if (!StringUtil.isNullOrEmpty(externalTargetCode)) {
                    eventDrivenFromBatchData.setCommandAfterBatchCommandId(SEND_IF_COMMAND, externalTargetCode);
                    demandGnomesServiceRun.execute();
                }
            }
        }

        return PROCESS_RESULT_COMPLETED;
    }

    @Override
    public void setBatchCommonRequestPartameter(Properties p) {

    }
}
