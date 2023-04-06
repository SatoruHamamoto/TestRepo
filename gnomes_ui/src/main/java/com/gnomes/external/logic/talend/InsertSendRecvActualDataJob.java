package com.gnomes.external.logic.talend;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvRetryFlag;
import com.gnomes.common.constants.CommonEnums.FileFormat;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.external.dao.ExtIfDataSrActualDao;
import com.gnomes.external.dao.ExtIfDataSrActualDetailDao;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.entity.ExtIfDataSrActual;
import com.gnomes.external.entity.ExtIfDataSrActualDetail;
import com.gnomes.external.entity.ExternalIfSendDataDetail;

/**
 * 送受信実績データ登録
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class InsertSendRecvActualDataJob extends BaseJobLogic {

    /** 外部I/Fデータファイル送受信実績 Dao */
    @Inject
    protected ExtIfDataSrActualDao extIfDataSrActualDao;

    /** 外部I/Fデータファイル送受信実績詳細 Dao */
    @Inject
    protected ExtIfDataSrActualDetailDao extIfDataSrActualDetailDao;

    /**
     * 送受信実績データ登録.
     * <pre>
     * 送受信実績データ、送受信実績データ詳細を登録する。
     * </pre>
     * @throws GnomesAppException
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws GnomesAppException {

        // 送受信区分が受信の場合
        if (SendRecvType.Recv.equals(super.fileTransferBean.getSendRecvType())) {
            // 受信処理
            this.recvProcess();

        } // 送受信区分が送信の場合
        else if (SendRecvType.Send.equals(super.fileTransferBean.getSendRecvType())) {
            // 送信処理
            this.sendProcess();

        }

    }

    /**
     * 受信処理
     * @throws GnomesAppException
     */
    private void recvProcess() throws GnomesAppException {

        // 外部I/Fデータファイル送受信実績取得
        ExtIfDataSrActual extIfDataSrActual =
                this.extIfDataSrActualDao.getExternalIfDataSendRecvActual(
                        super.fileTransferBean.getQueueExternalIfRecv().getQueue_external_if_recv_key(),
                        SendRecvType.Recv, ExternalIfSendRecvClearFlag.ON,
                        super.fileTransferBean.getEml());

        // 該当データが取得できなかった場合
        if (Objects.isNull(extIfDataSrActual)) {

            extIfDataSrActual = new ExtIfDataSrActual();
            // 外部I/Fデータファイル送受信実績キー
            extIfDataSrActual.setExternal_if_data_sr_actual_key(UUID.randomUUID().toString());
            // 外部I/F送受信キューキー
            extIfDataSrActual.setExternal_if_send_recv_queue_key(
                    super.fileTransferBean.getQueueExternalIfRecv().getQueue_external_if_recv_key());
            // 通信日時
            extIfDataSrActual.setOccur_date(super.fileTransferBean.getQueueExternalIfRecv().getRecv_date());
            // ファイル種別
            extIfDataSrActual.setFile_type(super.fileTransferBean.getQueueExternalIfRecv().getFile_type());
            // 送受信区別
            extIfDataSrActual.setSend_recv_type(super.fileTransferBean.getSendRecvType().getValue());
            // 外部I/F対象システムコード
            extIfDataSrActual.setExternal_if_target_code(super.fileTransferBean.getQueueExternalIfRecv().getExternal_if_target_code());
            // ファイル名称
            extIfDataSrActual.setFile_name(super.fileTransferBean.getQueueExternalIfRecv().getFile_name());
            // 送受信ファイル名
            extIfDataSrActual.setSend_recv_file_name(super.fileTransferBean.getQueueExternalIfRecv().getRecv_file_name());
            // ステータス
            extIfDataSrActual.setStatus(super.fileTransferBean.getStatus().getValue());
            // 再処理フラグ(再処理しない)
            extIfDataSrActual.setReprocessing_flag(ExternalIfSendRecvRetryFlag.OFF.getValue());
            // クリアフラグ(クリアしない)
            extIfDataSrActual.setClear_flag(ExternalIfSendRecvClearFlag.OFF.getValue());
            // 送受信処理日時
            extIfDataSrActual.setSend_recv_date(ConverterUtils.utcToTimestamp(
                    ConverterUtils.dateToLocalDateTime(
                            CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault())));

            // 外部I/Fデータファイル送受信実績登録
            this.extIfDataSrActualDao.insert(extIfDataSrActual, super.fileTransferBean.getEml());


        } else {
            // ステータス
            extIfDataSrActual.setStatus(super.fileTransferBean.getStatus().getValue());
            // 送受信処理日時
            extIfDataSrActual.setSend_recv_date(ConverterUtils.utcToTimestamp(
                    ConverterUtils.dateToLocalDateTime(
                            CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault())));

            // 外部I/Fデータファイル送受信実績更新
            this.extIfDataSrActualDao.update(extIfDataSrActual, super.fileTransferBean.getEml());
            // 外部I/Fデータファイル送受信実績詳細削除
            this.extIfDataSrActualDetailDao.detele(
                    extIfDataSrActual.getExternal_if_data_sr_actual_key(), super.fileTransferBean.getEml());

        }

        // FileTransferBean.外部送信データ詳細が NULL の場合、処理を終了する
        if (Objects.isNull(super.fileTransferBean.getLineDataList())
                || super.fileTransferBean.getLineDataList().isEmpty()) {
            return;
        }
        
        //　外部IFファイル定義のファイル形式がXMLである場合、実績登録をせず終了する
        FileDefine fileDefine = super.fileTransferBean.getFileDefine();
        if(fileDefine != null && fileDefine.getFile_format() == FileFormat.Xml.getValue() ) {
        	return;
        }

        int index = 1;

        List<ExtIfDataSrActualDetail> extIfDataSrActualDetailList = new ArrayList<ExtIfDataSrActualDetail>();

        for (String recvLineData : super.fileTransferBean.getLineDataList()) {

            // エラー情報の取得
            this.getErrorInfo(index, super.fileTransferBean.getErrorLineInfo());

            ExtIfDataSrActualDetail externalIfDataDetail = new ExtIfDataSrActualDetail();
            // 外部I/Fデータファイル送受信実績詳細キー
            externalIfDataDetail.setExternal_if_data_sr_actual_detail_key(UUID.randomUUID().toString());
            // 外部I/Fデータファイル送受信実績キー
            externalIfDataDetail.setExternal_if_data_sr_actual_key(
                    extIfDataSrActual.getExternal_if_data_sr_actual_key());
            // 通信日時
            externalIfDataDetail.setOccur_date(super.fileTransferBean.getQueueExternalIfRecv().getRecv_date());
            // ファイル種別
            externalIfDataDetail.setFile_type(super.fileTransferBean.getQueueExternalIfRecv().getFile_type());
            // 送受信区別
            externalIfDataDetail.setSend_recv_type(super.fileTransferBean.getSendRecvType().getValue());
            // 行No.
            externalIfDataDetail.setLine_number(index);
            // 行データ
            externalIfDataDetail.setLine_data(recvLineData);
            // 行ステータス
            externalIfDataDetail.setLine_status(super.fileTransferBean.getLineStatus());
            // 詳細コメント
            String messageDetail;
            if(super.fileTransferBean.getErrorComment() != null){
            	if(super.fileTransferBean.getErrorComment().length() > CommonConstants.SR_ACTUAL_DETAIL_COMMENT_MAX_LENGTH){
            		messageDetail = super.fileTransferBean.getErrorComment().substring(0, CommonConstants.SR_ACTUAL_DETAIL_COMMENT_MAX_LENGTH);
            	} else {
            		messageDetail = super.fileTransferBean.getErrorComment();
            	}
            	externalIfDataDetail.setDetail_comment(messageDetail);
            }

            index++;

            extIfDataSrActualDetailList.add(externalIfDataDetail);

        }
        // 外部I/Fデータファイル送受信実績詳細登録
        this.extIfDataSrActualDetailDao.insert(extIfDataSrActualDetailList, super.fileTransferBean.getEml());

    }

    /**
     * 送信処理
     * @throws GnomesAppException
     */
    private void sendProcess() throws GnomesAppException {

        // 外部I/Fデータファイル送受信実績取得
        ExtIfDataSrActual extIfDataSrActual =
                this.extIfDataSrActualDao.getExternalIfDataSendRecvActual(
                        super.fileTransferBean.getQueueExternalIfSendStatus().getExternal_if_send_status_key(),
                        SendRecvType.Send, ExternalIfSendRecvClearFlag.ON,
                        super.fileTransferBean.getEml());

        // 該当データが取得できなかった場合
        if (Objects.isNull(extIfDataSrActual)) {

            extIfDataSrActual = new ExtIfDataSrActual();
            // 外部I/Fデータファイル送受信実績キー
            extIfDataSrActual.setExternal_if_data_sr_actual_key(UUID.randomUUID().toString());
            // 外部I/F送受信キューキー
            extIfDataSrActual.setExternal_if_send_recv_queue_key(
                    super.fileTransferBean.getQueueExternalIfSendStatus().getExternal_if_send_status_key());
            // 通信日時
            extIfDataSrActual.setOccur_date(super.fileTransferBean.getQueueExternalIfSendStatus().getCreate_date());
            // ファイル種別
            extIfDataSrActual.setFile_type(super.fileTransferBean.getQueueExternalIfSendStatus().getFile_type());
            // 送受信区別
            extIfDataSrActual.setSend_recv_type(super.fileTransferBean.getSendRecvType().getValue());
            // 外部I/F対象システムコード
            extIfDataSrActual.setExternal_if_target_code(super.fileTransferBean.getQueueExternalIfSendStatus().getExternal_if_target_code());
            // ファイル名
            extIfDataSrActual.setFile_name(super.fileTransferBean.getQueueExternalIfSendStatus().getFile_name());
            // 送受信ファイル名
            extIfDataSrActual.setSend_recv_file_name(super.fileTransferBean.getQueueExternalIfSendStatus().getSend_file_name());
            // ステータス
            extIfDataSrActual.setStatus(super.fileTransferBean.getStatus().getValue());
            // 再処理フラグ(再処理しない)
            extIfDataSrActual.setReprocessing_flag(ExternalIfSendRecvRetryFlag.OFF.getValue());
            // クリアフラグ(クリアしない)
            extIfDataSrActual.setClear_flag(ExternalIfSendRecvClearFlag.OFF.getValue());
            // 送受信処理日時
            extIfDataSrActual.setSend_recv_date(ConverterUtils.utcToTimestamp(
                    ConverterUtils.dateToLocalDateTime(
                            CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault())));

            // 外部I/Fデータファイル送受信実績登録
            this.extIfDataSrActualDao.insert(extIfDataSrActual, super.fileTransferBean.getEml());

        } else {
            // ステータス
            extIfDataSrActual.setStatus(super.fileTransferBean.getStatus().getValue());
            // 送受信処理日時
            extIfDataSrActual.setSend_recv_date(ConverterUtils.utcToTimestamp(
                    ConverterUtils.dateToLocalDateTime(
                            CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault())));

            // 外部I/Fデータファイル送受信実績更新
            this.extIfDataSrActualDao.update(extIfDataSrActual, super.fileTransferBean.getEml());
            // 外部I/Fデータファイル送受信実績詳細削除
            this.extIfDataSrActualDetailDao.detele(
                    extIfDataSrActual.getExternal_if_data_sr_actual_key(), super.fileTransferBean.getEml());

        }

        // FileTransferBean.外部送信データ詳細＝NULLの場合、処理を終了する
        if (Objects.isNull(super.fileTransferBean.getExternalIfSendDataDetail())
                || super.fileTransferBean.getExternalIfSendDataDetail().isEmpty()) {
            return;
        }

        int index = 1;

        List<ExtIfDataSrActualDetail> extIfDataSrActualDetailList = new ArrayList<ExtIfDataSrActualDetail>();

        for (ExternalIfSendDataDetail sendDataDetail : super.fileTransferBean.getExternalIfSendDataDetail()) {

            // エラー情報の取得
            this.getErrorInfo(index, super.fileTransferBean.getErrorLineInfo());

            ExtIfDataSrActualDetail externalIfDataDetail = new ExtIfDataSrActualDetail();
            // 外部I/Fデータファイル送受信実績詳細キー
            externalIfDataDetail.setExternal_if_data_sr_actual_detail_key(UUID.randomUUID().toString());
            // 外部I/Fデータファイル送受信実績キー
            externalIfDataDetail.setExternal_if_data_sr_actual_key(
                    extIfDataSrActual.getExternal_if_data_sr_actual_key());
            // 通信日時
            externalIfDataDetail.setOccur_date(
                    super.fileTransferBean.getQueueExternalIfSendStatus().getCreate_date());
            // ファイル種別
            externalIfDataDetail.setFile_type(
                    super.fileTransferBean.getQueueExternalIfSendStatus().getFile_type());
            // 送受信区別
            externalIfDataDetail.setSend_recv_type(super.fileTransferBean.getSendRecvType().getValue());
            // 行No.
            externalIfDataDetail.setLine_number(sendDataDetail.getLine_number());
            // 行データ
            externalIfDataDetail.setLine_data(sendDataDetail.getLine_data());
            // 行ステータス
            externalIfDataDetail.setLine_status(super.fileTransferBean.getLineStatus());
            // 詳細コメント
            String messageDetail;
            if(super.fileTransferBean.getErrorComment() != null){
            	if(super.fileTransferBean.getErrorComment().length() > CommonConstants.SR_ACTUAL_DETAIL_COMMENT_MAX_LENGTH){
            		messageDetail = super.fileTransferBean.getErrorComment().substring(0, CommonConstants.SR_ACTUAL_DETAIL_COMMENT_MAX_LENGTH);
            	} else {
            		messageDetail = super.fileTransferBean.getErrorComment();
            	}
            	externalIfDataDetail.setDetail_comment(messageDetail);
            }
            index++;

            extIfDataSrActualDetailList.add(externalIfDataDetail);

        }
        // 外部I/Fデータファイル送受信実績詳細登録
        this.extIfDataSrActualDetailDao.insert(
                extIfDataSrActualDetailList, super.fileTransferBean.getEml());

    }

    /**
     * エラー情報取得
     * @param index ループ回数
     * @param map エラー行情報
     */
    private void getErrorInfo(int index, Map<Integer, String> errorInfo) {

        if (!Objects.isNull(errorInfo)
                && errorInfo.containsKey(index)) {
            super.fileTransferBean.setLineStatus(2);
            super.fileTransferBean.setErrorComment(errorInfo.get(index));

        } else {
            super.fileTransferBean.setLineStatus(1);
            super.fileTransferBean.setErrorComment("");
        }
    }

}
