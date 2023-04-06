package com.gnomes.external.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.ExternalIfSendIsSummary;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvIsContinueError;
import com.gnomes.common.constants.CommonEnums.ExternalIfTelegramIsUsable;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.dao.MstrExternalIfDataDefineDao;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.dao.QueueExternalIfSendStatusDao;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SendTelegramInfo;
import com.gnomes.external.entity.ExternalIfSendDataDetail;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/17 16:08 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

/**
 * BLFileTransferの送信に関する詳細ロジック
 * キューの１つを対応するロジッククラス
 *
 * @author 03501213
 *
 */
@Dependent
public class BLFileTranserSendDataProc extends BaseLogic {

    /** 外部I/F送信状態キュー Dao */
    @Inject
    protected QueueExternalIfSendStatusDao queueExternalIfSendStatusDao;

    @Inject
    protected GnomesEjbBean ejbBean;

    /** 悲観的ロックセッション. */
    @Inject
    protected PessimisticLockSession lockSession;

    /** 送受信 トランスファ */
    @Inject
    protected FileTransferBean fileTransferBean;

    /** 排他ロック */
    static ReentrantLock lock = new ReentrantLock();

    /** 外部I/Fファイル構成定義マスタ Dao */
    @Inject
    protected MstrExternalIfFileDefineDao mstrExternalIfFileDefineDao;

    /** 外部I/Fデータ項目定義マスタ Dao */
    @Inject
    protected MstrExternalIfDataDefineDao mstrExternalIfDataDefineDao;

    /** ファイル送信受信 ロジック */
    @Inject
    protected FileTransferCallTalend fileTransferCallTalend;

    /** 送信伝文ヘッダ作成 ロジック */
    @Inject
    protected SendDataCreateHeader sendDataCreateHeader;

    /**
     * 受信キュー1件分の処理
    * 　メイントランザクションは毎回Begin/Commit/Rollbackされる
    *
     * @param data                      処理したいキューの1個分のデータ
     * @param externalIfTargetCode      親元から呼ばれるターゲットコード
     * @param requestWaitCount          要求処理待機回数
     * @param requestWaitTime           1回の待機に何秒待つか
     * @throws Exception
     */
    @GnomesTransactional
    public boolean QueueExternalIfSendDataProc(QueueExternalIfSendStatus data, String externalIfTargetCode,
            int requestWaitCount, int requestWaitTime, List<QueueExternalIfSendStatus> sendStateList) throws Exception {

        FileDefine externalIfFileDefine;

        // エラー行情報はキューごとに
        Map<Integer, String> errorLineInfo = new HashMap<>();

        //-----------------------------------------------------------------------------------------
        //  待機時間中に該当キューのステータスが実行中になるまで待つ
        //  dataの内容にかかわらず、キューにある全部に対して実行中の送信状態キューを取得
        //  複数のキューがループで動くので、前回処理したキューがまだ実行中であることを検知したい
        //-----------------------------------------------------------------------------------------
        boolean bRunningTimeout = WaitAndCheckForRunningSendQueue(externalIfTargetCode, requestWaitCount,
                requestWaitTime);

        //いつまで経っても実行中のキューが抜けない場合はtrueで帰ってくる
        if (bRunningTimeout) {
        	//成功フラグOFFに設定
        	this.fileTransferBean.setSkipFlag(true);
            //何もせずリターンする、親のループが途中で抜ける


            return false;
        }

        //-----------------------------------------------------------------------------------------
        //  実行中なキューが０になったので次に進む
        //-----------------------------------------------------------------------------------------
        // 処理成功フラグ
        boolean successFlag = true;

        try {
            //-----------------------------------------------------------------
            // キュー情報の悲観ロック
            //-----------------------------------------------------------------
            lock.lock();

            //-----------------------------------------------------------------
            // 管理トランザクションを生成
            //-----------------------------------------------------------------
            // セッション情報作成(通常領域固定)
            this.lockSession.createSessionNormal();
            //-----------------------------------------------------------------
            // 独自時管理エンティティマネージャーを設定
            //-----------------------------------------------------------------
            this.fileTransferBean.setEml(this.lockSession.getEntityManager());
            //-----------------------------------------------------------------
            // 管理トランザクション開始
            //-----------------------------------------------------------------
            this.lockSession.begin();

            //-----------------------------------------------------------------
            // 処理対象のキューを指定して送信状態を見る
            //-----------------------------------------------------------------
            QueueExternalIfSendStatus sendStatus = this
                    .getExternalIfSendStatusLock(data.getExternal_if_send_status_key());

            // キューのバージョンとステータスのバージョンが合わなかったらこの
            // セッションはロールバックして抜ける
            if (Objects.isNull(sendStatus) || data.getVersion() != sendStatus.getVersion()) {
                // 管理トランザクションロールバック
                successFlag = false;
                //この関数をスローする。メイントランザクションもロールバックされる
                throw this.exceptionFactory.createGnomesAppException("sendStatus is null or queue version not equal");
            }

            //-----------------------------------------------------------------
            // 送信tarendを呼ぶ準備を行う一連の作業
            //-----------------------------------------------------------------

            // 処理中の送信状態キューを設定
            //--------------------------------
            // 1.外部I/F送信状態キュー
            //--------------------------------
            this.fileTransferBean.setQueueExternalIfSendStatus(sendStatus);
            // 外部I/F送信データ詳細
            List<ExternalIfSendDataDetail> sendDataDetail = new ArrayList<ExternalIfSendDataDetail>(
                    sendStatus.getExternalIfSendDataDetails());
            //--------------------------------
            // 2.行Noを昇順並び替え
            //--------------------------------
            Collections.sort(sendDataDetail, Comparator.comparing(ExternalIfSendDataDetail::getLine_number));
            this.fileTransferBean.setExternalIfSendDataDetail(sendDataDetail);

            //--------------------------------
            // 3.ファイル種別を設定
            //--------------------------------
            this.fileTransferBean.setFileType(sendStatus.getFile_type());

            //--------------------------------
            // 4.送受信ファイル名を設定する
            //--------------------------------
            this.fileTransferBean.setSendRecvFileName(sendStatus.getSend_file_name());

            //--------------------------------
            // 5.移動ファイルの存在チェックを行うか否かを[行う]に設定
            //--------------------------------
            this.fileTransferBean.setIsFileCheck("true");

            //--------------------------------
            // 6.外部I/Fファイル構成定義を取得
            //--------------------------------
            try {
                // 外部I/Fファイル構成定義を取得
                externalIfFileDefine = this.getFileDefine(sendStatus.getFile_type(), SendRecvType.Send);

                //外部I/Fファイル構成定義にヘッダーフォーマットIDが存在する場合はヘッダーを作成
                if(externalIfFileDefine.getHeader_format_id() != null && !externalIfFileDefine.getHeader_format_id().isEmpty()){
                	String headerLine = this.createSendTelegramHeader(externalIfFileDefine, sendDataDetail);
                	if(headerLine != null && !headerLine.isEmpty()){
                		ExternalIfSendDataDetail externalIfSendDataDetail = new ExternalIfSendDataDetail();
                		externalIfSendDataDetail.setLine_data(headerLine);
                		sendDataDetail.add(0, externalIfSendDataDetail);
                		this.fileTransferBean.setExternalIfSendDataDetail(sendDataDetail);
                	}

                }

            } catch (GnomesAppException e) {
                //例外が出たらエラーコメントを設定する
                this.fileTransferBean.setErrorComment(MessagesHandler.getExceptionMessage(super.req, e));

                errorLineInfo.put(1, this.fileTransferBean.getErrorComment());
                // エラ-行情報
                this.setErrorLineInfo(errorLineInfo);
                // ステータス = 要求失敗
                this.fileTransferBean.setStatus(SendRecvStateType.Failed);
                // 送信エラー処理呼出
                this.fileTransferCallTalend.runSendErrorJobs();
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
                //スローしないのでメイントランザクションは外側でエラー処理を判定されロールバック
                //管理トランザクションはコミットされる
                //しかも他のキューを続行しない
                return false;

            }
            //--------------------------------
            // 7.対象のファイル種別が使用可能か否か判定
            //  （使用不可）の場合このキューは使用できないので抜ける
            //--------------------------------
            if (ExternalIfTelegramIsUsable.NO
                    .equals(ExternalIfTelegramIsUsable.getEnum(externalIfFileDefine.getIs_usable()))) {

                // ME01.0129:「対象の伝文グループは使用不可能です。対象システムコード:{0} 」
                this.fileTransferBean.setErrorComment(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0129,
                        data.getExternal_if_target_code()));

                errorLineInfo.put(1, this.fileTransferBean.getErrorComment());
                // エラ-行情報
                this.setErrorLineInfo(errorLineInfo);
                // ステータス = 要求失敗
                this.fileTransferBean.setStatus(SendRecvStateType.Failed);
                // 送信エラー処理呼出
                this.fileTransferCallTalend.runSendErrorJobs();
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
                //スローしないのでメイントランザクションは外側でエラー処理を判定されロールバック
                //管理トランザクションはコミットされる
                //しかも他のキューを続行しない
                return false;

            }
            //--------------------------------
            // 8.対象のファイル種別がエラー時に処理続行するか否か判定
            //   中断の場合はそのキューの中身を調べ、NG、要求NG,作成失敗のステータスになっていたら
            //  処理を中断
            //--------------------------------
            // 対象のファイル種別がエラー時に処理続行するか否か=0：中断の場合
            if (ExternalIfSendRecvIsContinueError.STOP
                    .equals(ExternalIfSendRecvIsContinueError.getEnum(externalIfFileDefine.getIs_continue_error()))) {

                List<Integer> status = new ArrayList<>();
                // ステータスNG
                status.add(SendRecvStateType.NG.getValue());
                // ステータス要求NG
                status.add(SendRecvStateType.Failed.getValue());
                // ステータスファイル作成失敗
                status.add(SendRecvStateType.FailedCreateFile.getValue());
                // 外部I/F受信キューからキュー情報を取得
                List<QueueExternalIfSendStatus> sendQueNGList = this.queueExternalIfSendStatusDao
                        .getSendStateQuery(status, externalIfTargetCode, ejbBean.getEntityManager());

                // ステータスNGキューが存在する場合
                if (!Objects.isNull(sendQueNGList) && sendQueNGList.size() > 0) {
                    //処理を中断
                    //スローしないのでメイントランザクションコミットされる
                    //管理トランザクションもコミットされる
                    //続行して他のキューを処理する
                    //お知らせする必要はあるのか？
                    return true; //続行して別なキューを処理する
                }

            }
            //--------------------------------
            // 9.作成ファイル文字コードを設定
            //--------------------------------
            this.fileTransferBean.setCreateFileEncode(externalIfFileDefine.getCreate_file_encode());

            //--------------------------------
            // 10.ステータス = 実行中に設定する
            //--------------------------------
            this.fileTransferBean.setStatus(SendRecvStateType.Running);

            //-----------------------------------------------------------------
            // 準備ができたので送信tarendの処理を行う
            //-----------------------------------------------------------------

            try {
                //--------------------------------
                // 送信キュー更新
                //--------------------------------
                this.fileTransferCallTalend.updateSendStateJob();
                // コミット後、セッション再作成
                this.reCreateSession(true);
                //ここでアンロック
                lock.unlock();

                //--------------------------------
                // 送信処理呼出
                //--------------------------------
                this.fileTransferCallTalend.runSendProcJobs();

                //ロック？
                lock.lock();

            } catch (Exception e) {
                if (!lock.isLocked()) {
                    lock.lock();
                }
                // ロールバック後、セッション再作成
                this.reCreateSession(false);
                // 外部I/F送信状態キュー(ロック)取得
                sendStatus = this.getExternalIfSendStatusLock(sendStateList.get(0).getExternal_if_send_status_key());

                // fileTransferBeannに外部I/F送信状態キュー(ロック)の状態を設定
                this.fileTransferBean.setQueueExternalIfSendStatus(sendStatus);

                // 外部I/F送信データ詳細を設定
                if (Objects.nonNull(sendStatus)) {
                    this.fileTransferBean.setExternalIfSendDataDetail(
                            new ArrayList<ExternalIfSendDataDetail>(sendStatus.getExternalIfSendDataDetails()));
                }
                // ステータス = 要求失敗を設定
                this.fileTransferBean.setStatus(SendRecvStateType.Failed);

                // 送信エラー処理呼出
                this.fileTransferCallTalend.runSendErrorJobs();

            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);

                //スローしないのでメイントランザクションはコミットされる
            }

        } catch (GnomesAppException e) {
            successFlag = false;
            // エラーログ出力
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(super.req, e));

        } catch (Exception e) {
            successFlag = false;
            // エラーログ出力
            GnomesAppException ex = super.exceptionFactory.createGnomesAppException(GnomesMessagesConstants.ME01_0001,
                    e);

            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(super.req, ex));

        } finally {
            if (successFlag) {
                // トランザクションコミット
                this.lockSession.commit();
            } else {
                // トランザクションロールバック
                this.lockSession.rollback();
            }
            if (lock.isLocked()) {
                lock.unlock();
            }

        }
        //次のキューに進む
        return true;

    }

    /**
     * 全部のキューに実行中のものがあるかどうかチェックし、待機オーバーしたら諦める
     *
     * @param externalIfTargetCode
     * @param requestWaitCount
     * @param requestWaitTime
     * @return true: 実行中が残っていて待機オーバー false:実行中のキューがないので続行
     * @throws Exception
     */
    private boolean WaitAndCheckForRunningSendQueue(String externalIfTargetCode, int requestWaitCount,
            int requestWaitTime) throws Exception {

        // 待機回数初期化
        int waitCount = 0;

        while (true) {

            List<QueueExternalIfSendStatus> sendStateRunningList;
            try {
                sendStateRunningList = this.queueExternalIfSendStatusDao.getSendStateQuery(
                        SendRecvStateType.Running.getValue(), externalIfTargetCode, ejbBean.getEntityManager());
            } catch (Exception e) {
                //キューが取れないときは致命的なのでスローする(メイントランザクションはロールバックされる)
                throw e;
            }

            // 実行中のキューが存在しない場合は、この待機を抜ける
            if (Objects.isNull(sendStateRunningList) || sendStateRunningList.isEmpty()) {
                waitCount = 0;
                return false; //実行中のキューがないので続行
            }

            // requestWaitCount で示す実行中の件数がオーバーしたら、まだ処理中のキューが
            //  あるとみなしリターンする
            waitCount++;

            // 待機回数が要求処理待機回数を超えた場合、処理終了
            if (requestWaitCount < waitCount) {
                // ME01.0201:送信処理要求待機回数を超えたため、処理を終了します。待機回数：{0}, 待機時間(秒)：{1}
                this.logHelper.fine(this.logger, null, null, MessagesHandler
                        .getString(GnomesMessagesConstants.ME01_0201, requestWaitCount, (requestWaitTime / 1000)));
            	//外部I/F送信要求は、実行中の電文が残っているため、処理できませんでした。
                //実行中になっている電文を対処してください。
                //外部I/F対象システムコード={0} ファイル名称={1} ファイル種別={2} 送信日時={3} 送信ファイル名={4}
            	List<String> msgParam = new ArrayList<String>();
            	QueueExternalIfSendStatus quere = sendStateRunningList.get(0);

            	msgParam.add(String.valueOf(externalIfTargetCode));
            	msgParam.add(String.valueOf(quere.getFile_name()));
            	msgParam.add(String.valueOf(quere.getFile_type()));
            	msgParam.add(String.valueOf(quere.getSend_date().toString()));
            	msgParam.add(String.valueOf(quere.getSend_file_name()));

    			MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0252,
    					msgParam);
                return true; //何時までたっても実行ステータスが消えないのであきらめる
            }

            try {
                // 要求処理待機時間(秒)待機
                Thread.sleep(requestWaitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return true; //続行しない
            }
        }
    }

    /**
     * 外部I/F送信状態キュー(ロック)取得
     * @param externalIfSendStatusKey 外部I/F送信状態キー
     * @return 外部I/F送信状態キュー
     * @throws GnomesAppException
     */
    private QueueExternalIfSendStatus getExternalIfSendStatusLock(String externalIfSendStatusKey)
            throws GnomesAppException {

        QueueExternalIfSendStatus sendStatus = this.queueExternalIfSendStatusDao
                .getExternalIfSendStatusLock(externalIfSendStatusKey, this.lockSession);

        if (Objects.isNull(sendStatus)) {
            // ME01.0162:「ロックの取得に失敗しました。（テーブル名：{0}、値：{1}）」
            String message = MessagesHandler.getExceptionMessage(this.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0162,
                            new Object[]{QueueExternalIfSendStatus.TABLE_NAME, externalIfSendStatusKey}));

            this.logHelper.severe(this.logger, null, null, message);

        }

        return sendStatus;

    }
    /**
     * 外部I/Fファイル構成定義マスタ取得
     * @param fileType ファイル種別
     * @param sendRecvType 送受信区分
     * @return
     * @throws GnomesAppException
     */
    private FileDefine getFileDefine(String fileType, SendRecvType sendRecvType) throws GnomesAppException {

        //外部I/Fファイル構成定義マスタをファイルタイプ
        //キャッシュ化されているのでエンティティマネージャーはいらないで検索
        FileDefine externalIfFileDefine = this.mstrExternalIfFileDefineDao.getFileDefine(fileType);

        if (Objects.isNull(externalIfFileDefine)) {
            // 外部I/Fファイル構成定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分:{1}
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0107, fileType,
                    sendRecvType);

        }

        return externalIfFileDefine;
    }

    /**
     * 外部I/Fデータ項目定義マスタ取得
     * @param fileType ファイル種別
     * @param sendRecvType 送受信区分
     * @return
     * @throws GnomesAppException
     */
    private List<DataDefine> getDataDefine(String fileType, String formatID, SendRecvType sendRecvType) throws GnomesAppException {

    	//外部I/Fデータ項目定義マスタをファイルタイプ
    	//キャッシュ化されているのでエンティティマネージャーはいらないで検索
    	List<DataDefine> externalIfDateDefineList = this.mstrExternalIfDataDefineDao.getDataDefineList(formatID);

    	if (Objects.isNull(externalIfDateDefineList) || externalIfDateDefineList.isEmpty()) {
    		// 外部I/Fデータ項目定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分{1}、フォーマットID:{2}
    		throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0108, fileType,
    				sendRecvType, formatID);
    	}

    	return externalIfDateDefineList;
    }
    /**
     * エラー行情報設定
     * @param errorComment エラー行情報
     */
    private void setErrorLineInfo(Map<Integer, String> errorLineInfo) {

        List<ExternalIfSendDataDetail> detailList = new ArrayList<>();
        ExternalIfSendDataDetail detail = new ExternalIfSendDataDetail();
        detail.setLine_number(1);
        detailList.add(detail);

        // 外部I/F送信データ詳細
        this.fileTransferBean.setExternalIfSendDataDetail(detailList);
        // エラー行情報
        this.fileTransferBean.setErrorLineInfo(errorLineInfo);

    }
    /**
     * セッション情報再作成
     * @param commitFlag コミットフラグ
     */
    private void reCreateSession(boolean commitFlag) {

        if (commitFlag) {
            // トランザクションコミット
            this.lockSession.commit();
        } else {
            // トランザクションロールバック
            this.lockSession.rollback();
        }
        // セッションクローズ
        this.lockSession.close();
        // セッション情報作成
        this.lockSession.createSessionNormal();
        // トランザクション開始
        this.lockSession.begin();
        this.fileTransferBean.setEml(this.lockSession.getEntityManager());

    }

    /**
     * 送信伝文ヘッダー作成
     * <pre>
     * 送信伝文のヘッダー作成処理を行う。
     * </pre>
     * @param fileDefine ファイル構成定義
     * @param externalIfSendDataDetail 送信データ詳細
     * @return 送信伝文ヘッダー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public String createSendTelegramHeader(FileDefine fileDefine, List<ExternalIfSendDataDetail> externalIfSendDataDetail)
            throws GnomesAppException {

    	List<DataDefine> dataDefineList = new ArrayList<DataDefine>();

        //ヘッダーフォーマットIDからヘッダーのファイル構成定義取得
    	if(fileDefine.getHeader_format_id() != null && !fileDefine.getHeader_format_id().isEmpty()){

    		dataDefineList = this.getDataDefine(fileDefine.getFile_type(), fileDefine.getHeader_format_id(), SendRecvType.Send);

    	} else {
    		return null;
    	}

    	String headerLineStr = null;
    	//ヘッダー作成処理
    	if(dataDefineList != null && !dataDefineList.isEmpty()){
    		headerLineStr = sendDataCreateHeader.process(externalIfSendDataDetail, fileDefine, dataDefineList);
    	}

        return headerLineStr;

    }
}