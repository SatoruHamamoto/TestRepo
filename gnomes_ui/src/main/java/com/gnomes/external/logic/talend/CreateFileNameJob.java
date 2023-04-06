package com.gnomes.external.logic.talend;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.dao.ExternalIfSendFileSeqNoDao;
import com.gnomes.external.entity.ExternalIfSendFileSeqNo;

/**
 * 送信ファイル名作成
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class CreateFileNameJob extends BaseJobLogic {

    /** 外部I/F送信ファイル連番管理 Dao */
    @Inject
    protected ExternalIfSendFileSeqNoDao externalIfSendFileSeqNoDao;

    /**
     * 送信ファイル名作成
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws GnomesAppException, Exception {

        // X210:外部送信ファイル連番管理取得
        ExternalIfSendFileSeqNo externalSendFileSeqNo  = externalIfSendFileSeqNoDao.getExternalIfSendFileSeqNoQuery(fileTransferBean.getFileType());

        if(Objects.isNull(externalSendFileSeqNo)){
            //ME01.0111：「対象データの連番を取得できませんでした。」
            fileTransferBean.setStatus(SendRecvStateType.FailedCreateFile);
            fileTransferBean.setProcType(2);

            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0111);
            throw ex;
        }

        // X210.最大値（9999）＜（X210.現在値 + 1）の場合、
        if(externalSendFileSeqNo.getMax_seq() < (externalSendFileSeqNo.getSeq()+1)){
            // X210.現在値 = X210.最小値（0000）に戻す
            externalSendFileSeqNo.setSeq(externalSendFileSeqNo.getMin_seq());

        }

        int currentValue = externalSendFileSeqNo.getSeq()+1;
        String convCurrentValue = String.format("%04d", currentValue);

        // ファイル名（”X102.送受信ファイル名”＋”.” + 処理４で作成した連番）
        String fileName = fileTransferBean.getFileDefine().getFile_name() + "." + convCurrentValue;
        fileTransferBean.setSendRecvFileName(fileName);

        // X210.現在値 + 1に更新する。
        externalSendFileSeqNo.setSeq(currentValue);
        externalIfSendFileSeqNoDao.update(externalSendFileSeqNo);
    }

}
