package com.gnomes.external.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.ExternalIfProtocolType;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendIsSummary;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvMode;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvRetryFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendResult;
import com.gnomes.common.constants.CommonEnums.SendRecvChangeStateType;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.EventDrivenFromBatchData;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.dao.MstrExternalIfDataDefineDao;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.dao.MstrExternalIfSystemDefineDao;
import com.gnomes.external.dao.QueueExternalIfRecvDao;
import com.gnomes.external.dao.QueueExternalIfSendStatusDao;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SendTelegramInfo;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.external.entity.ExternalIfSendDataDetail;
import com.gnomes.external.entity.QueueExternalIfRecv;
import com.gnomes.external.entity.QueueExternalIfSendStatus;
import com.gnomes.external.logic.talend.BeanValidationJob;
import com.gnomes.external.logic.talend.GetExternalIfMstrJob;
import com.gnomes.external.logic.talend.SendDataConvJob;
import com.gnomes.external.logic.talend.SendDataMappingJob;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * ファイル送受信 ビジネス クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/28 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BLFileTransfer extends BaseLogic {

    /** エンティティマネージャーファクトリ（通常領域）
     * キューのロック付き取得処理で使われる
    */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory emf;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /** デフォルト要求処理待機時間(秒) */
    protected int DEFAULT_REQUEST_WAIT_TIME = 10;

    /** デフォルト要求処理待機回数 */
    protected int DEFAULT_REQUEST_WAIT_COUNT = 3;

    /** 悲観的ロックセッション. */
    @Inject
    protected PessimisticLockSession lockSession;

    /** 排他ロック */
    static ReentrantLock lock = new ReentrantLock();

    /** クラス名 */
    private static final String className = "BLFileTransfer";

    /** 送受信 トランスファ */
    @Inject
    protected FileTransferBean fileTransferBean;

    /** ファイル送信受信 ロジック */
    @Inject
    protected FileTransferCallTalend fileTransferCallTalend;

    /** 外部I/Fシステム定義マスタ Dao */
    @Inject
    protected MstrExternalIfSystemDefineDao mstrExternalIfSystemDefineDao;

    /** 外部I/Fファイル構成定義マスタ Dao */
    @Inject
    protected MstrExternalIfFileDefineDao mstrExternalIfFileDefineDao;

    /** 外部I/Fデータ項目定義マスタ Dao */
    @Inject
    protected MstrExternalIfDataDefineDao mstrExternalIfDataDefineDao;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao mstrSystemDefineDao;

    /** 外部I/F送信状態キュー Dao */
    @Inject
    protected QueueExternalIfSendStatusDao queueExternalIfSendStatusDao;

    /** 外部I/F受信キュー Dao */
    @Inject
    protected QueueExternalIfRecvDao queueExternalIfRecvDao;

    /** 外部連携マスタ取得JOB */
    @Inject
    protected GetExternalIfMstrJob getExternalIfMstrJob;

    /** ビーンバリデーション実行JOB */
    @Inject
    protected BeanValidationJob beanValidationJob;

    /** 送信伝文マッピングJOB */
    @Inject
    protected SendDataMappingJob sendDataMappingJob;

    /** 送信伝文データ変換JOB */
    @Inject
    protected SendDataConvJob sendDataConvJob;

    /** 受信キュー処理 */
    @Inject
    protected BLFileTransferRecvDataProc bLFileTransferRecvDataProc;

    /** 送信キュー処理 */
    @Inject
    protected BLFileTranserSendDataProc bLFileTranserSendDataProc;

    @Inject
    protected GnomesEjbBean ejbBean;

    /** バッチイベント駆動化データ */
    @Inject
    EventDrivenFromBatchData eventDrivenFromBatchData;

    /** 定義項目コード */
    protected static final String DEFINITION_CODE = "ext_target_code";

    /**
     * コンストラクター
     */
    public BLFileTransfer() {

    }

    /**
     * 受信要求受付
     * @param String fileType ファイル種別
     * @param String recvFileName 受信ファイル名
     */
    @TraceMonitor
    @ErrorHandling
    public void recvRequest(String fileType, String recvFileName) throws GnomesAppException {

    	//成功フラグONに設定
    	this.fileTransferBean.setSuccessFlag(true);

    	try {

    		// ファイル種別 パラメータチェック
    		if (StringUtil.isNullOrEmpty(fileType)) {

    			StringBuilder params = new StringBuilder();
    			params.append("fileType：").append(fileType);

    			// ME01.0050:「パラメータが不正です。({0})」
    			throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
    					params.toString());

    		}

    		//ファイル種別を監視機能用Keyを設定
    		req.setWatcherSearchKey(fileType);

    		// 受信ファイル名 パラメータチェック
    		if (StringUtil.isNullOrEmpty(recvFileName)) {

    			StringBuilder params = new StringBuilder();
    			params.append("recvFileName：").append(recvFileName);

    			// ME01.0050:「パラメータが不正です。({0})」
    			throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
    					params.toString());

    		}


    		// パラメータをFileTransferBeanに設定
    		// ファイル種別
    		this.fileTransferBean.setFileType(fileType);
    		// 送受信ファイル名
    		this.fileTransferBean.setSendRecvFileName(recvFileName);

    		// 外部I/Fファイル構成定義取得
    		FileDefine fileDefine = this.getFileDefine(fileType, SendRecvType.Recv);

    		// 外部I/Fファイル構成定義
    		this.fileTransferBean.setFileDefine(fileDefine);
    		// ファイル名称
    		this.fileTransferBean.setDataTypeName(fileDefine.getData_type_name());
    		// 外部I/F対象システムコード
    		this.fileTransferBean.setExternalTargetCode(fileDefine.getExt_target_code());

    		// 外部I/Fシステム定義を取得する
    		SystemDefine externalIfSystem = this.getSystemDefine(fileDefine.getExt_target_code());

    		// 外部I/Fシステム定義
    		this.fileTransferBean.setSystemDefine(externalIfSystem);
    		// 遷移元フォルダ名
    		this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getRecv_folder_path());
    		// 遷移先フォルダ名
    		this.fileTransferBean.setMoveToFolderName(externalIfSystem.getRecv_proc_folder_path());

    		// 通常画面処理トランザクションを使用する
    		this.fileTransferBean.setEml(ejbBean.getEntityManager());
    		// Jobを実行する
    		try {
    			// 受信要求呼出
    			this.fileTransferCallTalend.runRecvRequestJobs();

    		} catch (Exception e) {
    			//JOB実行でエラーになった場合
    			//成功フラグをOFF
    			this.fileTransferBean.setSuccessFlag(false);
    		}
    	} catch (Exception e) {
    		//JOB以外でエラーになった場合
			//成功フラグをOFF
			this.fileTransferBean.setSuccessFlag(false);
    		//例外をスロー
    		throw e;
    	} finally {

    		if(this.fileTransferBean.isSuccessFlag()){
    			//ファイル受信要求処理を完了しました。
    			req.addMessageInfo(GnomesMessagesConstants.MG01_0040);
    		} else {
    			//ファイル受信要求処理が失敗しました。
    			req.addMessageInfo(GnomesMessagesConstants.MG01_0045);
    		}
    	}


    }

    /**
     * 受信処理要求
     * @param externalIftargetCode 外部I/F対象システムコード
     * @return
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void recvProc(String externalIftargetCode) throws Exception {

    	//成功フラグONに設定
    	this.fileTransferBean.setSuccessFlag(true);
    	//finaly句でメッセージを出力するフラグ
    	boolean finalyMessageFlg = true;

    	try{
    		// パラメータチェック
    		if (StringUtil.isNullOrEmpty(externalIftargetCode)) {

    			StringBuilder params = new StringBuilder();
    			params.append("externalIftargetCode：").append(externalIftargetCode);

    			// ME01.0050:「パラメータが不正です。({0})」
    			throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
    					params.toString());

    		}

    		//キュー取得する
    		List<QueueExternalIfRecv> recvQueList = null;
    		try{
    			recvQueList = getRevQueueWithLockSesion(externalIftargetCode);

        		if (Objects.isNull(recvQueList) || recvQueList.isEmpty()) {
        			//処理対象の受信ファイルがありません。ファイル受信処理を終了します。
        			req.addMessageInfo(GnomesMessagesConstants.MG01_0042);
            		//キュー存在チェックでエラー発生時は上記のメッセージのみ出力する
            		finalyMessageFlg = false;
        			//キューがなかったらそのままリターン
        			return;
        		}
    		} catch (Exception e){
        		//エラーキューがある場合はその旨のメッセージのみ出力する
        		finalyMessageFlg = false;
        		throw e;
    		}

            // 改行コード取得
            MstrSystemDefine delimiterDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                    SystemDefConstants.TYPE_EXTERNAL_IF, SystemDefConstants.CODE_FILE_DEFAULT_DELIMITER);

            if(delimiterDefine != null){
            	//取得できた場合
            	try{
	            	int delimiterCode = Integer.parseInt(delimiterDefine.getChar1());
	            	String delimiterStr = CommonEnums.FileDefaultDelimiter.getEnum(delimiterCode);
	            	if(delimiterStr != null && !delimiterStr.isEmpty()){
	            		//改行コードを設定
	            		this.fileTransferBean.setFileDefaultDelimiter(delimiterStr);
	            	} else {
	            		//Enumにない値だった場合は場合はデフォルトの改行コードを設定
	            		this.fileTransferBean.setFileDefaultDelimiter(CommonConstants.FILE_DEFAULT_DELIMITER);
	            	}
            	} catch(NumberFormatException nfex){
            		//数値変換に失敗した場合は場合はデフォルトの改行コードを設定
            		this.fileTransferBean.setFileDefaultDelimiter(CommonConstants.FILE_DEFAULT_DELIMITER);
            	}
            } else {
            	//取得できなかった場合はデフォルトの改行コードを設定
            	this.fileTransferBean.setFileDefaultDelimiter(CommonConstants.FILE_DEFAULT_DELIMITER);
            }

    		// 外部I/Fシステム定義を取得
    		SystemDefine externalIfSystem = this.getSystemDefine(externalIftargetCode);

    		// 外部I/Fシステム定義
    		this.fileTransferBean.setSystemDefine(externalIfSystem);
    		// 遷移元フォルダ名
    		this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getRecv_proc_folder_path());
    		// 遷移先フォルダ名
    		this.fileTransferBean.setMoveToFolderName(externalIfSystem.getRecv_backup_folder_path());
    		// プロトコル種別
    		this.fileTransferBean.setProcType(externalIfSystem.getProtocol_type());
    		// 送受信区分 = 受信
    		this.fileTransferBean.setSendRecvType(SendRecvType.Recv);

    		// デフォルトの受信ファイルの文字コードを獲得
    		MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
    				SystemDefConstants.TYPE_EXTERNAL_IF, SystemDefConstants.CODE_RECVFILE_DEFAULT_CHARSET);

    		if (Objects.isNull(mstrSystemDefine)) {
    			//システム定義が取得できない場合は、本ソースに固定のデフォルト文字コードを指定
    			this.fileTransferBean.setRecvFileEncode(CommonConstants.RECVFILE_DEFAULT_CHARSET);
    		} else {
    			// 正しく取得出来たらシステムのファイルエンコードを設定
    			this.fileTransferBean.setRecvFileEncode(mstrSystemDefine.getChar1());
    		}

    		// 送受信モードが 0（ファイル種別毎）の場合
    		if (ExternalIfSendRecvMode.FILE_TYPE
    				.equals(ExternalIfSendRecvMode.getEnum(externalIfSystem.getSend_recv_mode()))) {

    			// 1件目のファイル種別
    			String firstFileType = recvQueList.get(0).getFile_type();

    			// 1件目のファイル種別をキーにデータを絞り込む
    			recvQueList = recvQueList.stream().filter(item -> item.getFile_type().equals(firstFileType))
    					.collect(Collectors.toList());

    		}

    		// セッション情報作成(通常領域固定)
    		this.lockSession.createSessionNormal();

    		try {
    			// 独自管理エンティティマネージャー
    			this.fileTransferBean.setEml(this.lockSession.getEntityManager());

    			// 取得した外部I/F受信キューリストの件数分、以下の処理を実行
    			for (QueueExternalIfRecv queData : recvQueList) {
    				// トランザクション開始
    				this.lockSession.begin();

    				//ファイル種別を監視機能用Keyを設定
    				req.setWatcherSearchKey(queData.getFile_type());

    				try {
    					//1件分のキュー処理を行う
    					bLFileTransferRecvDataProc.QueueExternalIfRecvDataProc(queData, externalIftargetCode);
    				}
    				//bLFileTransferRecvDataProcで通常画面処理トランザクションが
    				//ロールバックされる。ここで外側のCatchに飛ぶと管理トランザクションが
    				//ロールバックされるので、例外を飛ばす
    				catch (Exception e) {
    				}

    				//ロックセッション（管理トランザクション）は、実績やキューの更新が
    				//あるのでコミットする
    				this.lockSession.commit();

    			}

    		} catch (Exception ex) {
    			//ハンドルされない例外が出たらロールバックされる
    			//TarendのJOBではステータスを持っているのでここでは
    			//未知の例外が出たときが条件
    			this.lockSession.rollback();
    			throw ex;
    		} finally {
    			//実績やキューの更新用のエンティティマネージャーはクローズされる
    			this.lockSession.close();
    		}
    	} catch (Exception exc){
			//成功フラグをOFFに設定
        	this.fileTransferBean.setSuccessFlag(false);
    		throw exc;
    	} finally {
    		if(finalyMessageFlg){
    			//エラー情報がないかつ成功フラグがONの場合
    			if(!this.fileTransferBean.isFoundErrorInfo() && this.fileTransferBean.isSuccessFlag()){
    				//ファイル受信処理を完了しました。
    				req.addMessageInfo(GnomesMessagesConstants.MG01_0041);
    			} else {
    				//ファイル受信処理が失敗しました。
    				req.addMessageInfo(GnomesMessagesConstants.MG01_0046);
    			}
    		}
    	}
    }

    /**
     *
     * キューを取得
     *
     * @param externalIftargetCode
     * @return 取れたキューのリスト
     * @throws Exception 中にエラーがあったらスローされる
     */
    private List<QueueExternalIfRecv> getRevQueueWithLockSesion(String externalIftargetCode) throws Exception {
        // 外部I/F受信キューからキュー情報を取得
        List<QueueExternalIfRecv> recvQueList = this.queueExternalIfRecvDao.getExternalIfRecvQueQuery(
                CommonEnums.SendRecvStateType.Request.getValue(), externalIftargetCode,
                this.ejbBean.getEntityManager());

        // キューが存在しなかった場合、処理終了
        if (Objects.isNull(recvQueList) || recvQueList.isEmpty()) {
            return recvQueList;
        }

        //失敗したキューも存在したら何もしない
        // 外部I/F受信キューからキュー情報を取得
        List<QueueExternalIfRecv> recvQueListOfNG = this.queueExternalIfRecvDao.getExternalIfRecvQueQuery(
                CommonEnums.SendRecvStateType.NG.getValue(), externalIftargetCode, this.ejbBean.getEntityManager());

        // 失敗レコードが存在したら、処理終了
        if ((!Objects.isNull(recvQueListOfNG)) && (recvQueListOfNG.size() > 0)) {

            // ME01.0229:外部I/F受信要求は、過去のエラー電文が残っているため、処理できませんでした。
            //  \nエラーになっている電文を対処してください。\n
            //  外部I/F対象システムコード={0} ファイル名称={1} ファイル種別={2} 受信日時={3} 受信ファイル名={4}
            QueueExternalIfRecv recv = recvQueListOfNG.get(0);

            //ロールバックされる（DBアップデートはないので結果的に何もならない）
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0229,
                    recv.getExternal_if_target_code(), recv.getFile_name(), recv.getFile_type(),
                    recv.getRecv_date().toString(), recv.getRecv_file_name());
        }
        return recvQueList;
    }

    /**
     * 受信状態変更
     * @param externalIfRecvKey 受信キューキー
     * @param reRecvClearFlag 再受信クリアフラグ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void recvChangeState(String externalIfRecvKey, Integer reRecvClearFlag) throws GnomesAppException {

        // 受信キューキー パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfRecvKey)) {

            StringBuilder params = new StringBuilder();
            params.append("externalIfRecvKey：").append(externalIfRecvKey);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }

        // 再受信クリアフラグ パラメータチェック
        if (Objects.isNull(reRecvClearFlag)) {

            StringBuilder params = new StringBuilder();
            params.append("isChangeStateflag：").append(reRecvClearFlag);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
        }

        // 処理成功フラグ
        boolean successFlag = true;

        try {

            // セッション情報作成(通常領域固定)
            this.lockSession.createSessionNormal();
            // 独自管理エンティティマネージャー
            this.fileTransferBean.setEml(this.lockSession.getEntityManager());

            // 外部I/F受信キューからキュー情報を取得
            QueueExternalIfRecv recvQue = this.getExternalIfRecvQueQueryLock(externalIfRecvKey);

            if (Objects.isNull(recvQue)) {
                return;
            }

            // 外部I/Fシステム定義からデータを取得する。
            SystemDefine externalIfSystem = this.getSystemDefine(recvQue.getExternal_if_target_code());

            if (externalIfSystem.getRecv_backup_folder_path().isEmpty()) {
                // 外部I/Fファイル連携処理：{0} （処理名：{1}) にてエラーが発生しました。
                // エラーの詳細については、メッセージ履歴を確認してください。
                GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0109, String.valueOf(recvQue.getExternal_if_target_code()));
                throw ex;
            }

            // 送受信区分
            this.fileTransferBean.setSendRecvType(SendRecvType.Recv);
            // 外部I/F受信キュー
            this.fileTransferBean.setQueueExternalIfRecv(recvQue);
            // ファイル種別
            this.fileTransferBean.setFileType(recvQue.getFile_type());
            // 外部I/Fシステム定義
            this.fileTransferBean.setSystemDefine(externalIfSystem);
            // 遷移元フォルダ名
            this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getRecv_proc_folder_path());
            // 遷移先フォルダ名
            this.fileTransferBean.setMoveToFolderName(externalIfSystem.getRecv_backup_folder_path());
            // 送受信ファイル名
            this.fileTransferBean.setSendRecvFileName(recvQue.getRecv_file_name());

            // 移動ファイルの存在チェックを行うか否か
            this.fileTransferBean.setIsFileCheck("false");

            // 入力パラメータ.再受信クリアフラグが 0（再受信）
            if (SendRecvChangeStateType.Retry.equals(SendRecvChangeStateType.getEnum(reRecvClearFlag))) {
                // ステータス = 要求中
                this.fileTransferBean.setStatus(SendRecvStateType.Request);
                // 再処理フラグ
                this.fileTransferBean.setRetryFlag(ExternalIfSendRecvRetryFlag.ON);
                // クリアフラグ
                this.fileTransferBean.setClearFlag(null);

            } else if (SendRecvChangeStateType.Clear.equals(SendRecvChangeStateType.getEnum(reRecvClearFlag))) {

                // 再処理フラグ
                this.fileTransferBean.setRetryFlag(null);
                // クリアフラグ
                this.fileTransferBean.setClearFlag(ExternalIfSendRecvClearFlag.ON);

            }
            // 受信状態変更呼出
            this.fileTransferCallTalend.runRecvChangeStateJobs();

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
            // セッションクローズ
            this.lockSession.close();

        }

    }

    /**
     * ファイル送信要求
     * @param sendDataList 送信データリスト
     * @param fileType ファイル種別
     * @param requestSeq 要求内連番
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void sendRequest(List<String> sendDataList, String fileType, Integer requestSeq) throws GnomesAppException {
        // 送信状態を要求中としてファイル送信要求を行う。
        this.sendRequest(sendDataList, fileType, requestSeq, SendRecvStateType.Request);
    }

    /**
     * ファイル送信要求
     * @param sendDataList 送信データリスト
     * @param fileType ファイル種別
     * @param requestSeq 要求内連番
     * @param sendState 送信状態
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void sendRequest(List<String> sendDataList, String fileType, Integer requestSeq, SendRecvStateType sendState)
            throws GnomesAppException {

        //-----------------------------------------------------------------------------------------
        // 事前処理
        //  受信処理中の送信処理ではfileTransferBeanの中身が置き換わり、のちに受信の続きをすると
        //  動きがおかしくなるため、事前に受信のデータをバックアップして、最後にリストアする
        //-----------------------------------------------------------------------------------------
        FileTransferBean backupFileTransferBean = backupRecvRequestForFileTransferBean();

		//呼び出し元が外部I/F受信処理から呼ばれる送信処理の場合
        //送信のエンティティはステータスや管理情報を含め通常エンティティで動く
        //これは、送信でエラーになったりそのあとの処理のエラー（例外ロールバック）
        //では、送信したことも無効になるので送信キューやステータス等もロールバックするのが理由
		if(ejbBean.isEjbBatch()){
		    fileTransferBean.setEml(ejbBean.getEntityManager());
		}

        // 送信データリスト パラメータチェック
        if (Objects.isNull(sendDataList) || sendDataList.isEmpty()) {

            StringBuilder params = new StringBuilder();
            params.append("sendDataList：").append(sendDataList);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }
        // ファイル種別 パラメータチェック
        if (StringUtil.isNullOrEmpty(fileType)) {

            StringBuilder params = new StringBuilder();
            params.append("fileType：").append(fileType);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }
        // 要求内連番
        if (Objects.isNull(requestSeq)) {

            StringBuilder params = new StringBuilder();
            params.append("requestSeq：").append(requestSeq);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }
        // 送信状態
        if (Objects.isNull(sendState)) {

            StringBuilder params = new StringBuilder();
            params.append("sendState：").append(sendState);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }
        // 送信状態が0(要求中)、-1(待機中)以外の場合、エラー
        else if (!sendState.equals(SendRecvStateType.Request) && !sendState.equals(SendRecvStateType.Waiting)) {
            // ME01.0210:「送信状態は要求中,待機中以外を指定することはできません。」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0210);
        }

        // 行データList
        this.fileTransferBean.setLineDataList(sendDataList);
        // ファイル種別
        this.fileTransferBean.setFileType(fileType);
        // 送受信区分 = 送信
        this.fileTransferBean.setSendRecvType(SendRecvType.Send);
        // 要求内連番
        this.fileTransferBean.setRequestSeq(requestSeq);
        // 送信状態
        this.fileTransferBean.setStatus(sendState);

        try {
            // 送信要求呼出
            this.fileTransferCallTalend.runSendRequestJobs();

            // ファイル種別ごとの外部I/F対象システムコード取得
            String[] extTargetCode = mstrExternalIfFileDefineDao.getSetValue(fileType, DEFINITION_CODE);

            // バッチイベント駆動化データに外部I/F対象システムコード設定
            eventDrivenFromBatchData.getSendProcParameter().add(extTargetCode[0]);

        } catch (GnomesAppException e) {
            // エラーログ出力
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(super.req, e));

        } catch (Exception e) {
            // エラーログ出力
            GnomesAppException ex = super.exceptionFactory.createGnomesAppException(GnomesMessagesConstants.ME01_0001,
                    e);
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(super.req, ex));

        }
        //-----------------------------------------------------------------------------------------
        // 事後処理
        //  受信処理中の送信処理ではfileTransferBeanの中身が置き換わり、のちに受信の続きをすると
        //  動きがおかしくなるため、事前に受信のデータをバックアップして、最後にリストアする
        //  例外で抜けたときはリストアしないので、送信時のエラー情報などが入っている
        //-----------------------------------------------------------------------------------------
        restoreRecvRequestForFileTransferBean(backupFileTransferBean);
    }

    /**
     * FileTransferBeanから事前に受信のデータをバックアップ
     */
    private FileTransferBean backupRecvRequestForFileTransferBean() {

        //fileTransferBeanの中身をチェック。何も入っていなかったらnullで返す
        if (Objects.isNull(this.fileTransferBean.getSendRecvType())
                || this.fileTransferBean.getSendRecvType() != SendRecvType.Recv) {
            //送受信タイプがRecvが入っていることを想定しているので
            //Recv以外が入っていたら何もしない
            return null;
        }

        FileTransferBean backupFileTransferBean = new FileTransferBean();
        /**
         * ＜受信中の送信処理における特殊処理＞
         *      受信の中で送信処理をしているが、fileTransferBeanに設定された受信のための設定が
         *      送信のために変わることにより、送信の後に行われる受信の後始末で動作異常が起こる
         *      そのため、送信前に一度設定をバックアップし、終わったらリストアする処理を施す
         */
        // 送受信区分
        backupFileTransferBean.setSendRecvType(this.fileTransferBean.getSendRecvType());
        //送受信ファイル名
        backupFileTransferBean.setSendRecvFileName(this.fileTransferBean.getSendRecvFileName());
        //送受信データBeanList
        backupFileTransferBean.setSendRecvDataBeanList(this.fileTransferBean.getSendRecvDataBeanList());
        // ファイル種別
        backupFileTransferBean.setFileType(this.fileTransferBean.getFileType());
        // 行データList
        backupFileTransferBean.setLineDataList(this.fileTransferBean.getLineDataList());
        // 要求内連番
        backupFileTransferBean.setRequestSeq(this.fileTransferBean.getRequestSeq());
        // 送信状態
        backupFileTransferBean.setStatus(this.fileTransferBean.getStatus());
        // エンティティマネージャーをバックアップ
        backupFileTransferBean.setEml(this.fileTransferBean.getEml());


        return backupFileTransferBean;
    }

    /**
     * 事前に受信のデータをバックアップした内容をリストアする
     */
    private void restoreRecvRequestForFileTransferBean(FileTransferBean backupFileTransferBean) {

        //バックアップが何も入っていなかったら何もしない
        if (Objects.isNull(backupFileTransferBean)) {
            return;
        }
        //受信中の送信処理における特殊処理の復元作業
        // 送受信区分
        this.fileTransferBean.setSendRecvType(backupFileTransferBean.getSendRecvType());
        //送受信ファイル名
        this.fileTransferBean.setSendRecvFileName(backupFileTransferBean.getSendRecvFileName());
        //送受信データBeanList
        this.fileTransferBean.setSendRecvDataBeanList(backupFileTransferBean.getSendRecvDataBeanList());
        // ファイル種別
        this.fileTransferBean.setFileType(backupFileTransferBean.getFileType());
        // 行データList
        this.fileTransferBean.setLineDataList(backupFileTransferBean.getLineDataList());
        // 要求内連番
        this.fileTransferBean.setRequestSeq(backupFileTransferBean.getRequestSeq());
        // 送信状態
        this.fileTransferBean.setStatus(backupFileTransferBean.getStatus());
        // エンティティマネージャーをリストア
        this.fileTransferBean.setEml(backupFileTransferBean.getEml());

    }

    /**
     * 送信処理要求
     * <pre>
     * 要求処理待機時間(秒)が未設定の場合、デフォルト値(10秒)
     * 要求処理待機回数が未設定の場合、デフォルト値(3回)
     * </pre>
     * @param externalIfTargetCode 外部I/F対象システムコード
     * @param requestWaitTimeParam 要求処理待機時間(秒)
     * @param requestWaitCountParam 要求処理待機回数
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void sendProc(String externalIfTargetCode, Integer requestWaitTimeParam, Integer requestWaitCountParam)
            throws GnomesAppException {

    	//成功フラグONに設定
    	this.fileTransferBean.setSuccessFlag(true);
    	//スキップフラグOFFに設定
    	this.fileTransferBean.setSkipFlag(false);
    	//キュー存在フラグONに設定
    	this.fileTransferBean.setQuereExistFlag(true);
    	//finaly句でメッセージを出力するフラグ
    	boolean finalyMessageFlg = true;

        try {
            // 外部IF対象システムコード パラメータチェック
            if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {

                StringBuilder params = new StringBuilder();
                params.append("externalIfTargetCode：").append(externalIfTargetCode);

                // ME01.0050:「パラメータが不正です。({0})」
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                        params.toString());

            }

            // 要求処理待機時間(秒) パラメータチェック
            int requestWaitTime = DEFAULT_REQUEST_WAIT_TIME * 1000;
            if (!Objects.isNull(requestWaitTimeParam)) {
                requestWaitTime = requestWaitTimeParam * 1000;
            }

            // 要求処理待機回数 パラメータチェック
            int requestWaitCount = DEFAULT_REQUEST_WAIT_COUNT;
            if (!Objects.isNull(requestWaitCountParam)) {
                requestWaitCount = requestWaitCountParam;
            }

            // 要求中の外部I/F送信状態キューリストを取得
            List<QueueExternalIfSendStatus> sendStateList = this.queueExternalIfSendStatusDao.getSendStateQuery(
                    SendRecvStateType.Request.getValue(), externalIfTargetCode, ejbBean.getEntityManager());

            // キューが存在しなかった場合、処理終了
            if (Objects.isNull(sendStateList) || sendStateList.isEmpty()) {
            	this.fileTransferBean.setQuereExistFlag(false);
                return;
            }

            // 改行コード取得
            MstrSystemDefine delimiterDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                    SystemDefConstants.TYPE_EXTERNAL_IF, SystemDefConstants.CODE_FILE_DEFAULT_DELIMITER);

            if(delimiterDefine != null){
            	//取得できた場合
            	try{
	            	int delimiterCode = Integer.parseInt(delimiterDefine.getChar1());
	            	String delimiterStr = CommonEnums.FileDefaultDelimiter.getEnum(delimiterCode);
	            	if(delimiterStr != null && !delimiterStr.isEmpty()){
	            		//改行コードを設定
	            		this.fileTransferBean.setFileDefaultDelimiter(delimiterStr);
	            	} else {
	            		//Enumにない値だった場合は場合はデフォルトの改行コードを設定
	            		this.fileTransferBean.setFileDefaultDelimiter(CommonConstants.FILE_DEFAULT_DELIMITER);
	            	}
            	} catch(NumberFormatException nfex){
            		//数値変換に失敗した場合は場合はデフォルトの改行コードを設定
            		this.fileTransferBean.setFileDefaultDelimiter(CommonConstants.FILE_DEFAULT_DELIMITER);
            	}
            } else {
            	//取得できなかった場合はデフォルトの改行コードを設定
            	this.fileTransferBean.setFileDefaultDelimiter(CommonConstants.FILE_DEFAULT_DELIMITER);
            }

            // 外部I/Fシステム定義取得
            SystemDefine externalIfSystem = this.getSystemDefine(externalIfTargetCode);

            // 外部I/Fシステム定義
            this.fileTransferBean.setSystemDefine(externalIfSystem);
            // 遷移元フォルダ名
            this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getSend_temp_folder_path());
            // 遷移先フォルダ名
            this.fileTransferBean.setMoveToFolderName(externalIfSystem.getSend_folder_path());
            // 送受信区分 = 送信
            this.fileTransferBean.setSendRecvType(SendRecvType.Send);

            try {
                // 送受信モードが 0（ファイル種別毎）の場合
                if (ExternalIfSendRecvMode.FILE_TYPE
                        .equals(ExternalIfSendRecvMode.getEnum(externalIfSystem.getSend_recv_mode()))) {

                    // 1件目のファイル種別
                    String firstFileType = sendStateList.get(0).getFile_type();

                    // 1件目のファイル種別をキーにデータを絞り込む
                    sendStateList = sendStateList.stream().filter(item -> item.getFile_type().equals(firstFileType))
                            .collect(Collectors.toList());

                }

                // プロトコル種別が 0（HULFT）の場合
                if (ExternalIfProtocolType.HULFT
                        .equals(ExternalIfProtocolType.getEnum(externalIfSystem.getProtocol_type()))) {

                    // HULFT送信コマンド取得
                    MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                            SystemDefConstants.TYPE_EXTERNAL_IF, SystemDefConstants.CODE_HULFT_COMMAND);

                    if (Objects.isNull(mstrSystemDefine)) {
                        // ME01.0134:「外部I/Fファイル連携処理：{0} （処理名：{1}) にてエラーが発生しました。　エラーの詳細については、メッセージ履歴を確認してください。 」
                        throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0134,
                                ResourcesHandler.getString(GnomesResourcesConstants.OPE_S01005C200),
                                CommonConstants.SEND_PROC);

                    }
                    // HULFT送信コマンドファイルチェック
                    this.checkHulftSendCommand(mstrSystemDefine.getChar1());

                    // HULFT送信コマンド
                    this.fileTransferBean.setHulftSendCommand(mstrSystemDefine.getChar1());

                    for (QueueExternalIfSendStatus data : sendStateList) {

                    	//ファイル種別を監視機能用Keyを設定
                    	req.setWatcherSearchKey(data.getFile_type());

                        boolean bContinue = true; //続行がデフォルト
                        try {

                            bContinue = bLFileTranserSendDataProc.QueueExternalIfSendDataProc(data,
                                    externalIfTargetCode, requestWaitCount, requestWaitTime, sendStateList);

                        } catch (Exception e) {
                            //キューのDBが取れないなどで例外が飛ぶ場合
                            //  既にメイントランザクションはロールバックされている
                            // またコンティニューされる
                        	//成功フラグOFFに設定
                        	this.fileTransferBean.setSuccessFlag(false);
                        }

                        //返事値がfalseだったら次のキューを処理せず抜ける
                        if (!bContinue) {
                            break;
                        }
                    }

                }

                // HULFT以外の場合
                else {
                    // 現時点では未実装とする。
                }

            } catch(Exception e){
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
            	throw e;
            }
            finally {

                // セッションクローズ
                this.lockSession.close();
            }
        } catch(Exception e){
        	//成功フラグOFFに設定
        	this.fileTransferBean.setSuccessFlag(false);
        	throw e;
        } finally {
        	if(finalyMessageFlg){
        		if(!this.fileTransferBean.isQuereExistFlag()){
        			//ファイル送信処理を完了しました。
        			//処理対象の送信ファイルがありません。ファイル送信処理を終了します。
        			req.addMessageInfo(GnomesMessagesConstants.MG01_0048);
        		} else if(this.fileTransferBean.isSkipFlag()){
        			//スキップ時のメッセージはスキップ判定時に出力済み
        		} else if(this.fileTransferBean.isSuccessFlag()){
        			//ファイル送信処理を完了しました。
        			req.addMessageInfo(GnomesMessagesConstants.MG01_0038);
        		} else {
        			//ファイル送信処理が失敗しました。
        			req.addMessageInfo(GnomesMessagesConstants.MG01_0043);
        		}
        	}
        }

    }
    /**
     * 外部I/F受信キュー(ロック)取得
     * @param externalIfRecvKey 外部I/F受信キューキー
     * @return 外部I/F受信キュー
     * @throws GnomesAppException
     */
    private QueueExternalIfRecv getExternalIfRecvQueQueryLock(String externalIfRecvKey) throws GnomesAppException {

        QueueExternalIfRecv recvQue = this.queueExternalIfRecvDao.getExternalIfRecvQueQueryLock(externalIfRecvKey,
                this.lockSession);

        if (Objects.isNull(recvQue)) {
            // ME01.0162:「ロックの取得に失敗しました。（テーブル名：{0}、値：{1}）」
            String message = MessagesHandler.getExceptionMessage(this.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0162,
                            new Object[]{QueueExternalIfSendStatus.TABLE_NAME, externalIfRecvKey}));

            this.logHelper.severe(this.logger, null, null, message);

        }

        return recvQue;

    }
    /**
     * ファイル送信結果登録.
     * <pre>
     * 送信結果を受け、処理を実行する。
     * </pre>
     * @param externalIfTargetCode 外部IF対象システムコード
     * @param isSendSuccess 送信結果
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void sendResult(String externalIfTargetCode, Integer isSendSuccess) throws GnomesAppException {
    	//成功フラグONに設定
    	this.fileTransferBean.setSuccessFlag(true);
    	//スキップフラグOFFに設定
    	this.fileTransferBean.setSkipFlag(false);

        try {
            // 外部IF対象システムコード パラメータチェック
            if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {

                StringBuilder params = new StringBuilder();
                params.append("externalIfTargetCode：").append(externalIfTargetCode);
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
                // ME01.0050:「パラメータが不正です。({0})」
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                        params.toString());

            }
            // 送信結果 パラメータチェック
            if (Objects.isNull(isSendSuccess)) {

                StringBuilder params = new StringBuilder();
                params.append("isSendSuccess：").append(isSendSuccess);
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
                // ME01.0050:「パラメータが不正です。({0})」
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                        params.toString());

            }

            // 実行中の外部I/F送信状態キューリスト取得
            List<QueueExternalIfSendStatus> sendStateList = this.queueExternalIfSendStatusDao.getSendStateQuery(
                    SendRecvStateType.Running.getValue(), externalIfTargetCode, ejbBean.getEntityManager());

            // 送信キューが取得できなかった場合
            if (Objects.isNull(sendStateList) || sendStateList.isEmpty()) {
            	//スキップフラグONに設定
            	this.fileTransferBean.setSkipFlag(true);
                return;
            }

            // 処理成功フラグ
            boolean successFlag = true;

            try {
                lock.lock();
                // セッション情報作成(通常領域固定)
                this.lockSession.createSessionNormal();
                // 独自時管理エンティティマネージャー
                this.fileTransferBean.setEml(this.lockSession.getEntityManager());

                // 外部I/F送信状態キュー(ロック)取得
                QueueExternalIfSendStatus sendState = this
                        .getExternalIfSendStatusLock(sendStateList.get(0).getExternal_if_send_status_key());

                if (Objects.isNull(sendState)) {
                	//スキップフラグONに設定
                	this.fileTransferBean.setSkipFlag(true);
                    return;
                }

                //ファイル種別を監視機能用Keyを設定
                req.setWatcherSearchKey(sendState.getFile_type());

                // 外部I/Fシステム定義取得
                SystemDefine externalIfSystem = this.getSystemDefine(sendState.getExternal_if_target_code());

                // 外部I/F送信状態キュー
                this.fileTransferBean.setQueueExternalIfSendStatus(sendState);
                // 外部I/F送信データ詳細
                this.fileTransferBean.setExternalIfSendDataDetail(
                        new ArrayList<ExternalIfSendDataDetail>(sendState.getExternalIfSendDataDetails()));
                // ファイル種別
                this.fileTransferBean.setFileType(sendState.getFile_type());
                // 外部I/Fシステム定義
                this.fileTransferBean.setSystemDefine(externalIfSystem);
                // 遷移元フォルダ名
                this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getSend_folder_path());
                // 遷移先フォルダ名
                this.fileTransferBean.setMoveToFolderName(externalIfSystem.getSend_backup_folder_path());
                // 送受信ファイル名
                this.fileTransferBean.setSendRecvFileName(sendState.getSend_file_name());
                // 送受信区分 = 送信
                this.fileTransferBean.setSendRecvType(SendRecvType.Send);

                // 移動ファイルの存在チェックを行うか否か
                this.fileTransferBean.setIsFileCheck("true");

                // 送信結果の判定
                if (ExternalIfSendResult.OK.equals(ExternalIfSendResult.getEnum(isSendSuccess))) {
                    // ステータス = OK
                    this.fileTransferBean.setStatus(SendRecvStateType.OK);

                } else if (ExternalIfSendResult.NG.equals(ExternalIfSendResult.getEnum(isSendSuccess))) {
                    // ステータス = NG
                    this.fileTransferBean.setStatus(SendRecvStateType.NG);

                    Map<Integer, String> errorLineInfo = new HashMap<>();
                    errorLineInfo.put(1, MessagesHandler.getString(GnomesLogMessageConstants.ME01_0119,
                            sendState.getSend_file_name()));

                    // エラー行情報設定
                    this.setErrorLineInfo(errorLineInfo);

                } else {
                    return;
                }

                try {
                    // 送信処理呼出
                    this.fileTransferCallTalend.runSendResultJobs();

                } catch (Exception e) {
                    // ロールバック後、セッション情報再作成
                    this.reCreateSession(false);

                	//成功フラグOFFに設定
                	this.fileTransferBean.setSuccessFlag(false);

                    // 外部I/F送信状態キュー(ロック)取得
                    sendState = this.getExternalIfSendStatusLock(sendStateList.get(0).getExternal_if_send_status_key());

                    if (!Objects.isNull(sendState)) {

                        // 外部I/F送信状態キュー
                        this.fileTransferBean.setQueueExternalIfSendStatus(sendState);
                        // 外部I/F送信データ詳細
                        this.fileTransferBean.setExternalIfSendDataDetail(
                                new ArrayList<ExternalIfSendDataDetail>(sendState.getExternalIfSendDataDetails()));

                        // ステータス = 要求失敗
                        this.fileTransferBean.setStatus(SendRecvStateType.Failed);
                        // 送信エラー処理呼出
                        this.fileTransferCallTalend.runSendErrorJobs();
                    }
                }

            } catch (GnomesAppException e) {
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
                successFlag = false;
                // エラーログ出力
                this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(super.req, e));

            } catch (Exception e) {
            	//成功フラグOFFに設定
            	this.fileTransferBean.setSuccessFlag(false);
                successFlag = false;
                // エラーログ出力
                GnomesAppException ex = super.exceptionFactory
                        .createGnomesAppException(GnomesMessagesConstants.ME01_0001, e);

                this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(super.req, ex));

            } finally {
                if (successFlag) {
                    // トランザクションコミット
                    this.lockSession.commit();
                } else {
                    // トランザクションロールバック
                    this.lockSession.rollback();
                }
                // セッションクローズ
                this.lockSession.close();
                lock.unlock();
            }

        } finally {

        	if(this.fileTransferBean.isSkipFlag()){
            	//実行中の送信ファイルがありません。ファイル送信結果登録処理を終了します。
            	req.addMessageInfo(GnomesMessagesConstants.MG01_0049);
        	} else if(this.fileTransferBean.isSuccessFlag()){
        		if(this.fileTransferBean.getStatus().equals(SendRecvStateType.OK)){
        			//ファイル送信結果を成功で登録しました。
        			req.addMessageInfo(GnomesMessagesConstants.MG01_0039);
        		} else {
        			//ファイル送信結果を失敗で登録しました。
        			req.addMessageInfo(GnomesMessagesConstants.MG01_0044);
        		}
        	} else {
                //ファイル送信結果処理にエラーが発生しました。
                req.addMessageInfo(GnomesMessagesConstants.MG01_0047);
        	}

        }

    }

    /**
     * 再送信かクリア実行
     * @param externalIfSendStatusKey 外部I/F送信状態キー
     * @param reSendClearFlag 再送信クリアフラグ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void sendChangeState(String externalIfSendStatusKey, Integer reSendClearFlag) throws GnomesAppException {

        // 外部I/F送信状態キー パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfSendStatusKey)) {

            StringBuilder params = new StringBuilder();
            params.append("externalIfSendStatusKey：").append(externalIfSendStatusKey);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }

        // 再送信クリアフラグ パラメータチェック
        if (Objects.isNull(reSendClearFlag)) {

            StringBuilder params = new StringBuilder();
            params.append("isChangeStateflag：").append(reSendClearFlag);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }

        // 処理成功フラグ
        boolean successFlag = true;

        try {
            // セッション情報作成(通常領域固定)
            this.lockSession.createSessionNormal();
            // トランザクション開始
            this.lockSession.begin();
            // 独自時管理エンティティマネージャー
            this.fileTransferBean.setEml(this.lockSession.getEntityManager());

            // 外部I/F送信状態キュー(ロック)取得
            QueueExternalIfSendStatus sendStatus = this.getExternalIfSendStatusLock(externalIfSendStatusKey);

            if (Objects.isNull(sendStatus)) {
                return;
            }

            // 外部I/Fシステム定義取得
            SystemDefine externalIfSystem = this.getSystemDefine(sendStatus.getExternal_if_target_code());

            // 送受信区分
            this.fileTransferBean.setSendRecvType(SendRecvType.Send);
            // 外部I/F送信状態キュー
            this.fileTransferBean.setQueueExternalIfSendStatus(sendStatus);
            // ファイル種別
            this.fileTransferBean.setFileType(sendStatus.getFile_type());
            // 外部I/Fシステム定義
            this.fileTransferBean.setSystemDefine(externalIfSystem);
            // 遷移元フォルダ名
            this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getSend_folder_path());
            // 遷移先フォルダ名
            this.fileTransferBean.setMoveToFolderName(externalIfSystem.getSend_backup_folder_path());
            // 送受信ファイル名
            this.fileTransferBean.setSendRecvFileName(sendStatus.getSend_file_name());

            // 移動ファイルの存在チェックを行うか否か
            this.fileTransferBean.setIsFileCheck("false");

            // 一時フォルダにエラーファイルが存在する場合
            File file = new File(externalIfSystem.getSend_temp_folder_path() + sendStatus.getSend_file_name());
            if (file.exists()) {
                // 遷移元フォルダ名
                this.fileTransferBean.setMoveFromFolderName(externalIfSystem.getSend_temp_folder_path());
            }

            if (SendRecvChangeStateType.Retry.equals( // 再送信クリアフラグが再送信の場合
                    SendRecvChangeStateType.getEnum(reSendClearFlag))) {

                // ステータス = 要求中
                this.fileTransferBean.setStatus(SendRecvStateType.Request);
                // 再処理フラグ = 再処理する
                this.fileTransferBean.setRetryFlag(ExternalIfSendRecvRetryFlag.ON);
                // クリアフラグ
                this.fileTransferBean.setClearFlag(null);

            } else if (SendRecvChangeStateType.Clear.equals( // 再送信クリアフラグがクリアの場合
                    SendRecvChangeStateType.getEnum(reSendClearFlag))) {

                // 再処理フラグ
                this.fileTransferBean.setRetryFlag(null);
                // クリアフラグ = クリアする
                this.fileTransferBean.setClearFlag(ExternalIfSendRecvClearFlag.ON);

            } else {
                return;
            }

            // 送信処理呼出
            this.fileTransferCallTalend.runSendChangeStateJobs();

        } catch (GnomesAppException e) {
            successFlag = false;
            throw e;
        } catch (GnomesException e) {
            successFlag = false;
            throw e;
        } catch (Exception e) {
            successFlag = false;
            throw super.exceptionFactory.createGnomesAppException(GnomesMessagesConstants.ME01_0001, e);

        } finally {
            if (successFlag) {
                // トランザクションコミット
                this.lockSession.commit();
            } else {
                // トランザクションロールバック
                this.lockSession.rollback();
            }
            // セッションクローズ
            this.lockSession.close();

        }

    }

    /**
     * 送信状態変更（待機中→要求中）
     * @param externalIfSendStatusKey 外部I/F送信状態キー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void sendChangeStateRequest(String externalIfSendStatusKey) throws GnomesAppException {

        // 外部I/F送信状態キー パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfSendStatusKey)) {

            StringBuilder params = new StringBuilder();
            params.append("externalIfSendStatusKey：").append(externalIfSendStatusKey);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,
                    params.toString());

        }

        // 処理成功フラグ
        boolean successFlag = true;

        try {
            // セッション情報作成(通常領域固定)
            this.lockSession.createSessionNormal();
            // トランザクション開始
            this.lockSession.begin();

            // 外部I/F送信状態キュー(ロック)取得
            QueueExternalIfSendStatus sendStatus = this.getExternalIfSendStatusLock(externalIfSendStatusKey);

            // ロックの取得に失敗した場合、エラー
            if (Objects.isNull(sendStatus)) {
                // ME01.0162:「ロックの取得に失敗しました。（テーブル名：{0}、値：{1}）」
                GnomesAppException ex = super.exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0162,
                        new Object[]{QueueExternalIfSendStatus.TABLE_NAME, externalIfSendStatusKey});

                throw ex;
            }

            // 取得した外部I/F送信状態キューのステータスが-1（待機中）以外の場合、エラー
            if (sendStatus.getSend_status() != SendRecvStateType.Waiting.getValue()) {

                // ME01.0209:「送信状態が待機中の送信キューを指定してください。 」
                GnomesAppException ex = super.exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0209);

                String message = MessagesHandler.getExceptionMessage(this.req, ex);
                this.logHelper.severe(this.logger, null, null, message);
                throw ex;
            }
            // 外部I/F送信状態キュー.ステータスを要求中に変更
            sendStatus.setSend_status(SendRecvStateType.Request.getValue());

        } catch (GnomesAppException e) {
            successFlag = false;
            throw e;
        } catch (Exception e) {
            successFlag = false;
            throw super.exceptionFactory.createGnomesAppException(GnomesMessagesConstants.ME01_0001, e);

        } finally {
            if (successFlag) {
                // トランザクションコミット
                this.lockSession.commit();
            } else {
                // トランザクションロールバック
                this.lockSession.rollback();
            }
            // セッションクローズ
            this.lockSession.close();

        }

    }

    /**
     * 送信伝文作成.
     * <pre>
     * 送信伝文の作成処理を行う。
     * </pre>
     * @param sendRecvDataBeanList 送信データBeanリスト
     * @param fileType ファイル種別
     * @return 送信伝文情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public SendTelegramInfo createSendTelegram(List<SendRecvDataBean> sendRecvDataBeanList, String fileType)
            throws GnomesAppException {

        SendTelegramInfo sendTelegramInfo = new SendTelegramInfo();

        // 入力パラメータチェック
        this.inputParamCheck(sendRecvDataBeanList, fileType);
        // 外部IF連携マスタ情報取得
        FileTransferBean fileTransferBean = this.getExternalIfMstrJob.process(fileType);
        // 送受信データバリデーションチェック
        try {
            this.beanValidationJob.process(sendRecvDataBeanList, fileTransferBean.getDataDefine());
        }
        //マスタの定義とBean変数名が一致しない
        catch (ReflectiveOperationException e) {
            GnomesAppException ex = new GnomesAppException(e);
            //            ex.setMessageNo(GnomesMessagesConstants.ME01_0124);
            //            Object[] errParam = {
            //                    jobXmlName
            //            };
            //            ex.setMessageParams(errParam);
            throw ex;
        }

        // 送信伝文マッピング
        List<Map<String, String>> sendDataMappingList = this.sendDataMappingJob.process(sendRecvDataBeanList,
                fileTransferBean.getDataDefine());
        // 送信伝文データ変換
        List<String> sendTelegramList = this.sendDataConvJob.process(sendDataMappingList,
                fileTransferBean.getFileDefine(), fileTransferBean.getDataDefine());

        // 送信伝文リスト
        sendTelegramInfo.setSendTelegramList(sendTelegramList);
        // まとめ可否
        sendTelegramInfo.setIsSummary(
                ExternalIfSendIsSummary.getEnum(fileTransferBean.getMstrExternalIfFormatDefine().getIs_summary()));

        return sendTelegramInfo;

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
     * 外部I/Fシステム定義取得
     * @param externalIfTargetCode 外部I/F対象システムコード
     * @return 外部I/Fシステム定義
     * @throws GnomesAppException
     */
    private SystemDefine getSystemDefine(String externalIfTargetCode) throws GnomesAppException {

        SystemDefine systemDefine = this.mstrExternalIfSystemDefineDao.getSystemDefine(externalIfTargetCode);

        if (Objects.isNull(systemDefine)) {
            // ME01.0109:「X101:上位I/F受信ファイル構成定義に対象のデータが存在しません。外部I/F対象システムコード:{0} 」
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0109,
                    externalIfTargetCode);

        }

        return systemDefine;

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
            // X102:外部I/Fファイル構成定義に対象のデータが存在しません。ファイル種別:{0} 送受信区分:{1}
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0107, fileType,
                    sendRecvType);

        }

        return externalIfFileDefine;

    }

    /**
     * 入力パラメータチェック.
     * <pre>
     * 入力パラメータのチェックを行う。
     * </pre>
     * @param sendRecvDataBeanList 送信データBeanリスト
     * @param fileType ファイル種別
     * @throws GnomesAppException
     */
    private void inputParamCheck(List<SendRecvDataBean> sendRecvDataBeanList, String fileType)
            throws GnomesAppException {

        // 送信データBeanリスト
        if (Objects.isNull(sendRecvDataBeanList) || sendRecvDataBeanList.isEmpty()) {

            StringBuilder params = new StringBuilder();
            params.append("sendRecvDataBeanList：").append(sendRecvDataBeanList);

            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050, params.toString());

        }

        // ファイル種別
        if (StringUtil.isNullOrEmpty(fileType)) {

            StringBuilder params = new StringBuilder();
            params.append("fileType：").append(fileType);

            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050, params.toString());

        }

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
     * HULFT送信コマンドチェック
     * <pre>
     * HULFT送信コマンドのチェックを行う。
     * </pre>
     * @param command HULFT送信コマンド
     * @throws GnomesAppException
     */
    private void checkHulftSendCommand(String command) throws GnomesAppException {

        // HULFT送信コマンドチェック
        if (StringUtil.isNullOrEmpty(command)) {
            // ME01.0199:HULFT送信コマンドが設定されていません。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0199);

        }

        File file = new File(command);

        // HULFT送信コマンド存在チェック
        if (!file.exists()) {
            // ME01.0200:HULFT送信コマンドが存在しません。（HULFT送信コマンド: {0}）
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0200, command);

        }

    }


}
