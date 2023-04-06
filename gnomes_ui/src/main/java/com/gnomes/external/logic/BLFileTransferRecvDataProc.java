package com.gnomes.external.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvIsContinueError;
import com.gnomes.common.constants.CommonEnums.ExternalIfTelegramIsUsable;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.dao.MstrExternalIfDataDefineDao;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.dao.QueueExternalIfRecvDao;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.QueueExternalIfRecv;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/10 20:36 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * BLFileTransferの受信に関する詳細ロジック
 * キューの１つを対応するロジッククラス
 * @author 03501213
 *
 */
@Dependent
public class BLFileTransferRecvDataProc extends BaseLogic {

    /** クラス名 */
    private static final String className = "BLFileTransferRecvDataProc";

    /** 送受信 トランスファ */
    @Inject
    protected FileTransferBean fileTransferBean;

    /** 悲観的ロックセッション. */
    @Inject
    protected PessimisticLockSession lockSession;

    /** 外部I/Fファイル構成定義マスタ Dao */
    @Inject
    protected MstrExternalIfFileDefineDao mstrExternalIfFileDefineDao;

    /** 外部I/F受信キュー Dao */
    @Inject
    protected QueueExternalIfRecvDao queueExternalIfRecvDao;

    /** 外部I/Fデータ項目定義マスタ Dao */
    @Inject
    protected MstrExternalIfDataDefineDao mstrExternalIfDataDefineDao;

    /** ファイル送信受信 ロジック */
    @Inject
    protected FileTransferCallTalend fileTransferCallTalend;

    /**
    *
    * 受信キュー1件分の処理
    * 　トランザクションは毎回Begin/Commit/Rollbackされる
    *
    * @param queData
    * @param externalIftargetCode
    * @param lockSessionEm
    * @throws Exception
    */
    @GnomesTransactional
    public void QueueExternalIfRecvDataProc(QueueExternalIfRecv queData,
            String externalIftargetCode) throws Exception {
        //-----------------------------------------------------------------
        // エラー情報をクリアする（キューごとにエラーを処理するため）
        //-----------------------------------------------------------------
        fileTransferBean.clearErrorInfo();

        final String methodName = "QueueExternalIfRecvDataProc";
        try {
            //-----------------------------------------------------------------
            // キュー情報の悲観ロック
            //-----------------------------------------------------------------
            QueueExternalIfRecv recvQueue = this.getExternalIfRecvQueQueryLock(
                    queData.getQueue_external_if_recv_key());

            //キューがなかったら何もせず
            if (Objects.isNull(recvQueue)) {
                return;
            }

            //-----------------------------------------------------------------
            // 処理中の情報を外部I/F受信キューに設定
            //-----------------------------------------------------------------
            // 外部I/F受信キュー
            this.fileTransferBean.setQueueExternalIfRecv(recvQueue);
            // 送受信ファイル名
            this.fileTransferBean
                    .setSendRecvFileName(recvQueue.getRecv_file_name());
            // ファイル種別
            this.fileTransferBean.setFileType(recvQueue.getFile_type());
            // 外部I/F対象システムコード
            this.fileTransferBean.setExternalTargetCode(
                    recvQueue.getExternal_if_target_code());
            // ステータス = 実行中
            this.fileTransferBean.setStatus(SendRecvStateType.Running);
            // 移動ファイルの存在チェックを行う
            this.fileTransferBean.setIsFileCheck("true");

            //-----------------------------------------------------------------
            // 外部I/Fファイル構成定義を取得する
            //  無かったらスローされる
            //-----------------------------------------------------------------
            FileDefine fileDefine = this.getFileDefine(recvQueue.getFile_type(),
                    SendRecvType.Recv);

            //-----------------------------------------------------------------
            // 対象のファイル種別が使用可能か否かが0（使用不可）の場合
            // 「対象の伝文グループは使用不可能です。対象システムコード:{0}」でスローされる
            //-----------------------------------------------------------------
            if (ExternalIfTelegramIsUsable.NO.equals(ExternalIfTelegramIsUsable
                    .getEnum(fileDefine.getIs_usable()))) {

                // 対象の伝文グループは使用不可能です。対象システムコード:{0}
                // スローするので通常画面処理トランザクションはロールバックされる
                // 管理トランザクションはコミットされる
                throw super.exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0129,
                        recvQueue.getExternal_if_target_code());

            }
            //-----------------------------------------------------------------
            // 対象のファイル種別がエラー時に処理続行しない（中断）する場合
            //  今ある全部のキュー情報を取得し、エラーが存在したら中断する
            //-----------------------------------------------------------------
            if (ExternalIfSendRecvIsContinueError.STOP
                    .equals(ExternalIfSendRecvIsContinueError
                            .getEnum(fileDefine.getIs_continue_error()))) {

                List<Integer> status = new ArrayList<>();
                // ステータスNG
                status.add(SendRecvStateType.NG.getValue());
                // 外部I/F受信キューからキュー情報を取得
                List<QueueExternalIfRecv> recvQueNGList = this.queueExternalIfRecvDao
                        .getExternalIfRecvQueQuery(status, externalIftargetCode,
                                this.lockSession.getEntityManager());

                // ステータスNGキューが存在する場合
                if (!Objects.isNull(recvQueNGList)
                        && recvQueNGList.size() > 0) {
                    // 処理を中断
                    return;
                }

            }
            //-----------------------------------------------------------------
            // 外部I/Fファイル構成定義のBeanクラス名を設定する
            //  クラス名に対する実態が存在しなかったら
            //  このキューの一連の作業を中断し他のキューの処理をする
            //-----------------------------------------------------------------
            try {
                // 外部I/Fファイル構成定義をBeanに入れる
                this.fileTransferBean.setFileDefine(fileDefine);
                // 送受信データBeanクラス
                this.fileTransferBean.setBeanClass(
                        Class.forName(fileDefine.getBean_class_name()));
                // システム定義の文字コードをBeanに設定する
                String charset = fileDefine.getCreate_file_encode();
                if(charset != null && !charset.isEmpty()){
                    String lastChar = charset.substring(charset.length() - CommonConstants.CREATE_FILE_ENCODE_BOM.length());
                    if(lastChar.equals(CommonConstants.CREATE_FILE_ENCODE_BOM)){
                        charset = charset.substring(0, charset.length() - CommonConstants.CREATE_FILE_ENCODE_BOM.length());
                    }
                    this.fileTransferBean.setRecvFileEncode(charset);
                }
            } catch (ClassNotFoundException e) {

                // ME01.0127:「該当クラスが存在しません。クラス名=[{0}]」
                String message = MessagesHandler.getExceptionMessage(this.req,
                        super.exceptionFactory.createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0127,
                                fileDefine.getBean_class_name()));

                this.logHelper.severe(this.logger, className, methodName,
                        message);
                //このキュー処理は失敗するが他のキューは続行される
                return;
            }
            //-----------------------------------------------------------------
            // 外部I/Fデータ項目定義を取得する
            //  存在しなかったら異常なのでスローする
            //（スローするが他のキューは処理される)
            //-----------------------------------------------------------------
            List<DataDefine> dataDefineList = this.mstrExternalIfDataDefineDao
                    .getDataDefineList(fileDefine.getExternal_if_format_id());

            // 取得できなかった場合
            if (Objects.isNull(dataDefineList) || dataDefineList.isEmpty()) {

                // X103:上位I/Fデータ項目定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分{1}
                throw super.exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0108,
                        recvQueue.getFile_type(), SendRecvType.Recv);

            }

            //-----------------------------------------------------------------
            // 外部I/Fデータ項目定義リストをセットする
            //-----------------------------------------------------------------
            this.fileTransferBean.setDataDefine(dataDefineList);

            //-----------------------------------------------------------------
            //  Talendジョブ実行準備
            //-----------------------------------------------------------------
            // エラー情報をクリアする
            this.fileTransferBean.clearErrorInfo();

            //-----------------------------------------------------------------
            //  準備できたのでTalendジョブ実行
            // 受信定周期処理
            //-----------------------------------------------------------------
            this.fileTransferCallTalend.runRecvProcJobs();



        } catch (GnomesAppException e) {
            // エラーログ出力
            this.logHelper.severe(this.logger, className, methodName,
                    MessagesHandler.getExceptionMessage(super.req, e));
            throw e;

        } catch (Exception e) {
            // エラーログ出力
            GnomesAppException ex = super.exceptionFactory
                    .createGnomesAppException(GnomesMessagesConstants.ME01_0001,
                            e);
            this.logHelper.severe(this.logger, className, methodName,
                    MessagesHandler.getExceptionMessage(super.req, ex));
            //スローした先でロールバックされる
            throw e;
        } finally {
            //管理トランザクションはここでコミットされる
            this.lockSession.commit();

            //最終的にRollbackが必要かどうか判断
            if (this.fileTransferBean.isFoundErrorInfo()) {
                //スローして通常トランザクションはrollbackさせる
                throw this.exceptionFactory.createGnomesAppException(
                        this.fileTransferBean.getErrorComment());
            }
        }
    }
    /**
     * 外部I/F受信キュー(ロック)取得
     * @param externalIfRecvKey 外部I/F受信キューキー
     * @return 外部I/F受信キュー
     * @throws GnomesAppException
     */
    private QueueExternalIfRecv getExternalIfRecvQueQueryLock(
            String externalIfRecvKey) throws GnomesAppException {

        QueueExternalIfRecv recvQue = this.queueExternalIfRecvDao
                .getExternalIfRecvQueQueryLock(externalIfRecvKey,
                        this.lockSession);

        if (Objects.isNull(recvQue)) {
            // ME01.0162:「ロックの取得に失敗しました。（テーブル名：{0}、値：{1}）」
            String message = MessagesHandler
                    .getExceptionMessage(this.req,
                            super.exceptionFactory.createGnomesAppException(
                                    null, GnomesMessagesConstants.ME01_0162,
                                    new Object[]{
                                            QueueExternalIfSendStatus.TABLE_NAME,
                                            externalIfRecvKey}));

            this.logHelper.severe(this.logger, null, null, message);

        }

        return recvQue;

    }
    /**
     * 外部I/Fファイル構成定義マスタ取得
     * @param fileType ファイル種別
     * @param sendRecvType 送受信区分
     * @return
     * @throws GnomesAppException
     */
    private FileDefine getFileDefine(String fileType, SendRecvType sendRecvType)
            throws GnomesAppException {

        //外部I/Fファイル構成定義マスタをファイルタイプ
        //キャッシュ化されているのでエンティティマネージャーはいらないで検索
        FileDefine externalIfFileDefine = this.mstrExternalIfFileDefineDao
                .getFileDefine(fileType);

        if (Objects.isNull(externalIfFileDefine)) {
            // X102:外部I/Fファイル構成定義に対象のデータが存在しません。ファイル種別:{0} 送受信区分:{1}
            throw super.exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0107, fileType, sendRecvType);

        }

        return externalIfFileDefine;

    }
}