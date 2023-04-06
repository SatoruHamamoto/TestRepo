package com.gnomes.external.logic.talend;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.external.dao.ExternalIfSendDataDetailDao;
import com.gnomes.external.entity.ExternalIfSendDataDetail;

/**
 * 外部送信データ詳細登録
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class InsertSendDataDetailJob extends BaseJobLogic {

    /** 外部I/F送信データ詳細 Dao */
    @Inject
    protected ExternalIfSendDataDetailDao externalIfSendDataDetailDao;

    /**
     * 外部送信データ詳細登録
     */
    @ErrorHandling
    @TraceMonitor
    public void process() {

        // 外部送信データ詳細 登録
        List<ExternalIfSendDataDetail> sendDataDetailList = new ArrayList<ExternalIfSendDataDetail>();

        int index = 1;

        // システム日時
        Timestamp sysDate = ConverterUtils.utcToTimestamp(ConverterUtils.dateToLocalDateTime(
                CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        for (String lineData: super.fileTransferBean.getLineDataList()) {

            ExternalIfSendDataDetail sendDataDetail = new ExternalIfSendDataDetail();
            // 外部I/F送信データ詳細キー
            sendDataDetail.setExternal_if_send_data_detail_key(UUID.randomUUID().toString());
            // 外部I/F送信状態キー
            sendDataDetail.setExternal_if_send_status_key(
                    super.fileTransferBean.getQueueExternalIfSendStatus().getExternal_if_send_status_key());
            // 作成日時 = 共通機能.システム日時
            sendDataDetail.setCreate_date(sysDate);
            // ファイル種別
            sendDataDetail.setFile_type(super.fileTransferBean.getFileType());
            // 行No
            sendDataDetail.setLine_number(index);
            // 行データ
            sendDataDetail.setLine_data(lineData);

            sendDataDetailList.add(sendDataDetail);
            index++;

        }
        // 外部I/F送信データ詳細登録
        this.externalIfSendDataDetailDao.insert(sendDataDetailList, super.fileTransferBean.getEml());

    }

}
