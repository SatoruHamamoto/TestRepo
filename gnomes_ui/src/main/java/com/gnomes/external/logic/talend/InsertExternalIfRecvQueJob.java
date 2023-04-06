package com.gnomes.external.logic.talend;

import java.time.ZoneId;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.external.dao.QueueExternalIfRecvDao;
import com.gnomes.external.entity.QueueExternalIfRecv;

/**
 * 外部I/F受信キュー登録
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.SHibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class InsertExternalIfRecvQueJob extends BaseJobLogic {

    /** 外部I/F受信キュー Dao */
    @Inject
    protected QueueExternalIfRecvDao queueExternalIfRecvDao;

    /**
     * 外部I/F受信キューを登録
     */
    @ErrorHandling
    @TraceMonitor
    public void process() {

        QueueExternalIfRecv externalIfRecvQue = new QueueExternalIfRecv();

        // 外部I/F受信キューキー = 共通機能.UUID作成処理により設定
        externalIfRecvQue.setQueue_external_if_recv_key(UUID.randomUUID().toString());
        // 受信日時 = 共通機能.システム日時取得
        externalIfRecvQue.setRecv_date(ConverterUtils.utcToTimestamp(
                ConverterUtils.dateToLocalDateTime(
                        CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault())));
        // 外部IFターゲットコード
        externalIfRecvQue.setExternal_if_target_code(super.fileTransferBean.getExternalTargetCode());
        // ファイル名称
        externalIfRecvQue.setFile_name(super.fileTransferBean.getDataTypeName());
        // ファイル種別 = FileTransferBean.ファイル種別
        externalIfRecvQue.setFile_type(super.fileTransferBean.getFileType());
        // 受信ファイル名 = FileTransferBean.受信ファイル名
        externalIfRecvQue.setRecv_file_name(super.fileTransferBean.getSendRecvFileName());
        // ステータス = 0:要求中
        externalIfRecvQue.setRecv_status(SendRecvStateType.Request.getValue());

        // 外部I/F受信キューに登録
        this.queueExternalIfRecvDao.insert(externalIfRecvQue, super.fileTransferBean.getEml());
        super.fileTransferBean.setQueueExternalIfRecv(externalIfRecvQue);

    }

}
