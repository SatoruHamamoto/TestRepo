package com.gnomes.external.logic.talend;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.dao.ExtIfDataSrActualDao;
import com.gnomes.external.entity.ExtIfDataSrActual;

/**
 * 送受信実績データ更新
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/07 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class UpdateSendRecvActualDataJob extends BaseJobLogic {

    /** 外部I/Fデータファイル送受信実績 Dao */
    @Inject
    protected ExtIfDataSrActualDao extIfDataSrActualDao;

    /** 定数：半角スペース. */
    private static final String HALF_SPACE = " ";

    /**
     * 送受信実績データ更新.
     * <pre>
     * 送受信実績データを更新する。
     * </pre>
     * @throws GnomesAppException
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws GnomesAppException {

        // 外部I/Fデータファイル送受信実績取得
        ExtIfDataSrActual extIfDataSrActual = this.getExternalIfDataSendRecvActual();
        // 外部I/Fデータファイル送受信実績更新
        this.update(extIfDataSrActual);

    }

    /**
     * 外部I/Fデータファイル送受信実績取得
     * @return 外部I/Fデータファイル送受信実績
     * @throws GnomesAppException
     */
    private ExtIfDataSrActual getExternalIfDataSendRecvActual() throws GnomesAppException {

        // 外部I/F送受信キューキー
        String externalIfDataSrActualKey = "";

        if (SendRecvType.Send.equals(super.fileTransferBean.getSendRecvType())) {

            externalIfDataSrActualKey =
                    super.fileTransferBean.getQueueExternalIfSendStatus().getExternal_if_send_status_key();

        } else if (SendRecvType.Recv.equals(super.fileTransferBean.getSendRecvType())) {

            externalIfDataSrActualKey =
                    super.fileTransferBean.getQueueExternalIfRecv().getQueue_external_if_recv_key();

        }

        // 外部I/Fデータファイル送受信実績取得
        ExtIfDataSrActual extIfDataSrActual =
                this.extIfDataSrActualDao.getExternalIfDataSendRecvActual(
                        externalIfDataSrActualKey, super.fileTransferBean.getSendRecvType(), null,
                        super.fileTransferBean.getEml());

        // 該当データが取得できなかった場合
        if (Objects.isNull(extIfDataSrActual)) {
            // データがみつかりません。（{0}）(ME01.0026）
            StringBuilder argument = new StringBuilder();
            argument.append(CommonConstants.TABLE_NAME).append(ExtIfDataSrActual.TABLE_NAME);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(ExtIfDataSrActual.COLUMN_NAME_EXTERNAL_IF_SEND_RECV_QUEUE_KEY);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(externalIfDataSrActualKey);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(ExtIfDataSrActual.COLUMN_NAME_SEND_RECV_TYPE);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(super.fileTransferBean.getSendRecvType().getValue());

            throw super.exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0026, new Object[]{argument.toString()});

        }

        return extIfDataSrActual;

    }

    /**
     * 更新
     * @param extIfDataSrActual 送受信実績データ
     */
    private void update(ExtIfDataSrActual extIfDataSrActual) {

        // 再処理フラグ
        if (!Objects.isNull(super.fileTransferBean.getRetryFlag())) {
            extIfDataSrActual.setReprocessing_flag(
                    super.fileTransferBean.getRetryFlag().getValue());
        }
        // クリアフラグ
        if (!Objects.isNull(super.fileTransferBean.getClearFlag())) {
            extIfDataSrActual.setClear_flag(
                    super.fileTransferBean.getClearFlag().getValue());
        }

        this.extIfDataSrActualDao.update(extIfDataSrActual, super.fileTransferBean.getEml());

    }

}
