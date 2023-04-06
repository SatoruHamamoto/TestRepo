package com.gnomes.external.logic.talend;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.external.dao.QueueExternalIfSendStatusDao;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 外部I/F送信状態キュー登録
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class InsertSendStateJob extends BaseJobLogic {

    /** 外部I/F送信状態キュー Dao */
    @Inject
    protected QueueExternalIfSendStatusDao queueExternalIfSendStatusDao;

    /**
     * 外部I/F送信状態キュー登録
     */
    @ErrorHandling
    @TraceMonitor
    public void process() {

        QueueExternalIfSendStatus data = new QueueExternalIfSendStatus();

        // システム日付
        Timestamp sysDate = ConverterUtils.utcToTimestamp(ConverterUtils.dateToLocalDateTime(
                CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        // 外部I/F送信状態キー
        data.setExternal_if_send_status_key(UUID.randomUUID().toString());
        // 作成日時
        data.setCreate_date(sysDate);
        // 要求内連番
        data.setRequest_seq(super.fileTransferBean.getRequestSeq());
        // ファイル種別
        data.setFile_type(super.fileTransferBean.getFileType());
        // 外部I/F対象システムコード
        data.setExternal_if_target_code(super.fileTransferBean.getFileDefine().getExt_target_code());
        // ファイル名称
        data.setFile_name(super.fileTransferBean.getFileDefine().getData_type_name());
        // 送信ファイル名
        data.setSend_file_name(super.fileTransferBean.getSendRecvFileName());
        // コールクラス名
        data.setCall_class_name(super.fileTransferBean.getFileDefine().getCall_class_name());
        // 送信状態
        data.setSend_status(super.fileTransferBean.getStatus().getValue());
        // 送信処理日時
        data.setSend_date(sysDate);

        // 外部I/F送信状態キュー 登録
        this.queueExternalIfSendStatusDao.insert(data, super.fileTransferBean.getEml());

        super.fileTransferBean.setQueueExternalIfSendStatus(data);

    }

}
