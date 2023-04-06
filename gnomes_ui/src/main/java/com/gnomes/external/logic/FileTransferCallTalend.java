package com.gnomes.external.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvRetryFlag;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesScreenEntityManagerBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.external.logic.talend.DeleteExternalIfRecvQueJob;
import com.gnomes.external.logic.talend.DeleteSendStateJob;
import com.gnomes.external.logic.talend.FileRenameJob;
import com.gnomes.external.logic.talend.InsertSendRecvActualDataJob;
import com.gnomes.external.logic.talend.UpdateExternalIfRecvQueJob;
import com.gnomes.external.logic.talend.UpdateSendRecvActualDataJob;
import com.gnomes.external.logic.talend.UpdateSendStateJob;

/**
 * ファイル送信受信 ロジック 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/15 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class FileTransferCallTalend extends BaseJobLogic
{

    /** 外部IF受信キュー更新JOB */
    @Inject
    protected UpdateExternalIfRecvQueJob  updateRecvQueJob;

    /** 外部IF受信キュー削除JOB */
    @Inject
    protected DeleteExternalIfRecvQueJob  deleteRecvQueJob;

    /** 外部IF送信状態キュー更新JOB */
    @Inject
    protected UpdateSendStateJob          updateSendStateJob;

    /** 外部IF送信状態キューデータ削除JOB */
    @Inject
    protected DeleteSendStateJob          deleteSendStateJob;

    /** 外部IF送受信実績データ登録JOB */
    @Inject
    protected InsertSendRecvActualDataJob insertSendRecvActualDataJob;

    /** 外部IF送受信実績データ更新JOB */
    @Inject
    protected UpdateSendRecvActualDataJob updateSendRecvActualDataJob;

    /** 外部IF送受信実績データ更新JOB */
    @Inject
    protected FileRenameJob               fileRenameJob;

    /** 悲観的ロックセッション. */
    @Inject
    @New
    protected PessimisticLockSession      lockSession;

    /**
     * EJB専用のエンティティマネージャーを管理するBean
     */
    @Inject
    protected GnomesEjbBean               gnomesEjbBean;

    @Inject
    protected GnomesScreenEntityManagerBean scrBean;

    @Inject
    protected GnomesEntityManager         enitityManager;

    /**
     * コンストラクタ.
     */
    public FileTransferCallTalend()
    {

    }

    /**
     * 受信要求呼出
     *  通常画面処理トランザクションで実施
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    @GnomesTransactional
    public void runRecvRequestJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};

        try {
            // Jobの実行
            TalendJobRun.runJobProcess(CommonConstants.RECV_REQUEST_JOBS, jobParam, false);

            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {
                //エラー行情報が入っていない場合
                if (Objects.isNull(super.fileTransferBean.getErrorLineInfo())) {
                    throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0247,
                    		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());
                }
                else {
                    //エラー情報が入っている場合
                    String detailMessage = MakeDetailMessage(super.fileTransferBean.getErrorLineInfo());

                    throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0249,
                    		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName(),
                            detailMessage);
                }
            }

        }
        catch (GnomesAppException exx) {
            throw exx;
        }
        catch (GnomesException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_RECV_REQUEST_JOBS);

        }

    }

    /**
     * エラー情報を1つの文字にして加工する
     *
     * @param errorLineInfo
     * @return
     */
    private String MakeDetailMessage(Map<Integer, String> errorLineInfo)
    {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (Map.Entry<Integer, String> entry : errorLineInfo.entrySet()) {
            if(count > 0){
                sb.append("\n");
            }
            //行番号が０の場合は行のある問題ではないので（しかも1件だけ）
            //出力の方法を特別にする
            if(entry.getKey().equals(0)){
                sb.append(entry.getValue());
            }
            else {
                sb.append(String.format("%d:%s", entry.getKey(),entry.getValue()));
            }
            count++;
        }

        return sb.toString();
    }

    /**
     * 受信定周期処理 実行
     * 　ステータスや履歴はlockSessionで
     * 　BLは通常画面処理トランザクションで実行される
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runRecvProcJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};
        // 処理成功フラグ
        boolean successFlag = true;
        try {
            // 受信キューの更新
            this.updateRecvQueJob.process();

            // Jobの実行
            TalendJobRun.runJobProcess(CommonConstants.RECV_PROC_JOBS, jobParam, false);

            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {
                if (super.fileTransferBean.getErrorLineInfo() == null || super.fileTransferBean.getErrorLineInfo().size() == 0) {
                    // エラー情報設定
                    Map<Integer, String> errorLineInfo = new HashMap<>();
                    errorLineInfo.put(1, super.fileTransferBean.getErrorComment());
                    super.fileTransferBean.setErrorLineInfo(errorLineInfo);
                }

                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0247,
                		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());
                this.logHelper.severe(this.logger, null, null, super.fileTransferBean.getErrorComment());

                // 送受信結果を設定
                super.fileTransferBean.setSendRecvResult(false);
            }

            // 受信OKの場合
            if (super.fileTransferBean.isSendRecvResult()) {
                // 受信キュー削除
                this.deleteRecvQueJob.process();
                // ステータス = OK
                super.fileTransferBean.setStatus(SendRecvStateType.OK);
                // 送受信実績データ登録
                this.insertSendRecvActualDataJob.process();
                // 受信ファイル移動
                TalendJobRun.runJobProcess(CommonConstants.RECV_PROC_JOBS_MOVE_FILE, jobParam, false);

                // エラーが出力された場合
                if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {
                    // エラー情報設定
                    Map<Integer, String> errorLineInfo = new HashMap<>();
                    errorLineInfo.put(1, super.fileTransferBean.getErrorComment());
                    super.fileTransferBean.setErrorLineInfo(errorLineInfo);

                    throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0247,
                    		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());

                }

            }
            else { // 受信NGの場合

                // ステータス = NG
                super.fileTransferBean.setStatus(SendRecvStateType.NG);
                // 受信キュー更新
                this.updateRecvQueJob.process();
                // 送受信実績データ登録
                this.insertSendRecvActualDataJob.process();
            }

        }
        catch (GnomesAppException exx) {
            successFlag = false;
            throw exx;
        }
        catch (GnomesException ex) {
            successFlag = false;
            throw ex;
        }
        catch (Exception e) {
            successFlag = false;
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_RECV_PROC_JOBS);
        }
        finally {

        }

    }

    /**
     * 受信状態変更呼出
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runRecvChangeStateJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};

        try {
            // 再処理フラグが１（再処理する）の場合
            if (ExternalIfSendRecvRetryFlag.ON.equals(super.fileTransferBean.getRetryFlag())) {

                // 受信キュー更新
                this.updateRecvQueJob.process();
                // 送受信実績データ更新
                this.updateSendRecvActualDataJob.process();

            }

            // クリアフラグが１（クリアする）の場合
            if (ExternalIfSendRecvClearFlag.ON.equals(super.fileTransferBean.getClearFlag())) {

                // 受信キュー削除
                this.deleteRecvQueJob.process();
                // 送受信実績データ更新
                this.updateSendRecvActualDataJob.process();

                // 受信ファイル移動
                TalendJobRun.runJobProcess(CommonConstants.RECV_CHANGESTATE_JOBS, jobParam, false);

                // エラーが出力された場合
                if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {

                    throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0247,
                    		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());

                }
            }

        }
        catch (GnomesAppException exx) {
            throw exx;
        }
        catch (GnomesException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_RECV_CHANGE_STATE_JOBS);

        }

    }

    /**
     * 送信要求呼出
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runSendRequestJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};

        // 既にトランザクションが別に生成されていない場合を示すフラグ
        // バッチから呼ばれた場合でしか使わない
        boolean initialTransactionStarted = true;

        //トランザクションを生成またはBeanにエンティティマネージャーを設定する
        if(this.gnomesEjbBean.isEjbBatch()){
            EntityManager checkEml = this.fileTransferBean.getEml();

            //エンティティマネージャーが作られていることを示す
            //-----------------------------------------------------------------------------------------
            //トランザクションがアクティブの場合、これより前にトランザクションが生成されている事の証明
            //特に外部I/F受信の過程で送信処理をする場合、受信の中で生まれたトランザクションを使うことに
            //なるため、何もしないし、クローズもしない
            //  倉庫システムから到着報告を受信してERPに送信する場合はこちら
            //-----------------------------------------------------------------------------------------
            if(Objects.nonNull(checkEml) && checkEml.isOpen()){
                EntityTransaction transaction = checkEml.getTransaction();
                if(Objects.nonNull(transaction) && transaction.isActive()){
                    initialTransactionStarted = false;
                }
            }
            //-----------------------------------------------------------------------------------------
            // トランザクションがオープンされていない。つまり、受信中の送信ではなく、送信処理だけで
            //  呼ばれた場合はこちらを通る。個別トランザクションが必要なのでオープンする
            //  在庫照合の送信等はこちら
            //-----------------------------------------------------------------------------------------
            if(initialTransactionStarted){
                // セッション情報作成(通常領域固定)
                this.lockSession.createSessionNormal();

                // 独自管理エンティティマネージャーを使ってセッションを設定
                this.fileTransferBean.setEml(this.lockSession.getEntityManager());

                // トランザクション開始
                this.lockSession.begin();
            }
        }
        else {
            //-----------------------------------------------------------------------------------------
            // 画面から送信処理をコールした場合、個別トランザクションは使わない。そのため
            //  画面から呼ばれたら必ず入ってくるGnomesScreenEntityManagerBeanを使う
            //-----------------------------------------------------------------------------------------
            this.fileTransferBean.setEml(this.scrBean.getEntityManager());
        }

        boolean successFlag = true;

        try {
            // 送信要求
            TalendJobRun.runJobProcess(CommonConstants.SEND_REQUEST_JOBS, jobParam, false);

            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(this.fileTransferBean.getErrorJobName())) {

                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0248,
                		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());
            }

        }
        catch (GnomesAppException exx) {
            successFlag = false;
            throw exx;
        }
        catch (GnomesException ex) {
            successFlag = false;
            throw ex;
        }
        catch (Exception e) {
            successFlag = false;
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_SEND_REQUEST_JOBS);

        }
        finally {
            //成功・失敗を受けてトランザクションを制御する
            //(バッチ処理の場合のみ、かつ、受信中の送信は対象外
            //  画面は呼び元のGnomesTransactionalが実施するため）
            if (successFlag) {
                if(this.ejbBean.isEjbBatch() && initialTransactionStarted){
                    this.lockSession.commit();
                }
            }
            else {
                if(this.ejbBean.isEjbBatch() && initialTransactionStarted){
                    this.lockSession.rollback();
                }
            }
            if(this.ejbBean.isEjbBatch() && initialTransactionStarted){
                this.lockSession.close();
            }
        }

    }

    /**
     * 送信定周期処理呼出
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runSendProcJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};

        try {
            // 送信定周期処理
            TalendJobRun.runJobProcess(CommonConstants.SEND_PROC_JOBS, jobParam, false);

            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {

                // エラー情報設定
                Map<Integer, String> errorLineInfo = new HashMap<>();
                errorLineInfo.put(1, super.fileTransferBean.getErrorComment());
                super.fileTransferBean.setErrorLineInfo(errorLineInfo);

                // Talendのエラー内容を登録
                if(super.fileTransferBean.getErrorComment() != null && super.fileTransferBean.getErrorComment().length() > 0){
                	MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0238,
                			super.fileTransferBean.getErrorComment());
                }

                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0248,
                		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());

            }

        }
        catch (GnomesAppException exx) {
            throw exx;
        }
        catch (GnomesException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_SEND_PROC_JOBS);

        }

    }

    /**
     * 送信キュー更新
     */
    @TraceMonitor
    @ErrorHandling
    public void updateSendStateJob()
    {

        // 送信キュー更新
        this.updateSendStateJob.process();

    }

    /**
     * 送信エラー処理呼出
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runSendErrorJobs() throws GnomesAppException
    {

        try {
            // 送信キュー更新
            this.updateSendStateJob.process();
            // 送受信実績データ登録
            this.insertSendRecvActualDataJob.process();

        }
        catch (GnomesAppException exx) {
            throw exx;
        }
        catch (Exception e) {
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_SEND_ERR_JOBS);

        }

    }

    /**
     * 送信結果呼出
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runSendResultJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};

        try {

            // FileTransferBean.ステータスが 2(OK)の場合
            if (SendRecvStateType.OK.equals(super.fileTransferBean.getStatus())) {

                // 送信キュー削除
                this.deleteSendStateJob.process();
                // 送受信実績データ登録
                this.insertSendRecvActualDataJob.process();

                // ファイルリネーム
                this.fileRenameJob.process();
                // ファイル移動
                TalendJobRun.runJobProcess(CommonConstants.SEND_RESULT_JOBS_MOVE_FILE, jobParam, false);

                // エラーが出力された場合
                if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {

                    // エラー情報設定
                    Map<Integer, String> errorLineInfo = new HashMap<>();
                    errorLineInfo.put(1, super.fileTransferBean.getErrorComment());
                    super.fileTransferBean.setErrorLineInfo(errorLineInfo);

                    throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0248,
                    		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());
                }

            }
            // FileTransferBean.ステータスが 3(NG)の場合
            else if (SendRecvStateType.NG.equals(super.fileTransferBean.getStatus())) {

                // 送信キュー更新（送信NG）
                this.updateSendStateJob.process();
                // 送受信実績データ登録
                this.insertSendRecvActualDataJob.process();
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0119,
                        this.fileTransferBean.getSendRecvFileName());
                this.logHelper.severe(this.logger, null, null, MessagesHandler.getString(
                        GnomesLogMessageConstants.ME01_0119, this.fileTransferBean.getSendRecvFileName()));

            }

        }
        catch (GnomesAppException exx) {
            throw exx;
        }
        catch (GnomesException ex) {
            throw ex;
        }
        catch (Exception e) {

            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_SEND_RESULT_JOBS);

        }

    }

    /**
     * 送信状態変更呼出
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void runSendChangeStateJobs() throws GnomesAppException
    {

        // 実行ジョブ引数（コンテキスト）
        String[] jobParam = {""};

        try {

            // 再処理フラグが１（再処理する）の場合
            if (ExternalIfSendRecvRetryFlag.ON.equals(super.fileTransferBean.getRetryFlag())) {
                // 送信状態更新（要求中）
                this.updateSendStateJob.process();

            }
            // クリアフラグが１（クリアする）の場合
            if (ExternalIfSendRecvClearFlag.ON.equals(super.fileTransferBean.getClearFlag())) {
                // 送信キュー削除
                this.deleteSendStateJob.process();

            }
            // 送受信実績データ更新
            this.updateSendRecvActualDataJob.process();

            // ファイルリネーム
            this.fileRenameJob.process();

            // ファイル移動
            TalendJobRun.runJobProcess(CommonConstants.SEND_CHANGESTATE_JOBS_MOVE_FILE, jobParam, false);

            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(super.fileTransferBean.getErrorJobName())) {

                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0248,
                		super.fileTransferBean.getSendRecvFileName(), super.fileTransferBean.getErrorJobName());

            }

        }
        catch (GnomesAppException exx) {
            throw exx;
        }
        catch (GnomesException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0131,
                    CommonConstants.RUN_SEND_CHANGE_STATE_JOBS);
        }
        finally {
            if ((!Objects.isNull(this.fileTransferBean.getEml())) && this.fileTransferBean.getEml().isOpen()) {
                // クローズ
                this.fileTransferBean.getEml().close();
            }

        }

    }

    /**
     * キューの比較（BLFileTransferで取得したキュー情報と本クラスで取得したキュー情報に差異がある場合、false）
     * @throws GnomesAppException
     */
    @TraceMonitor
    public boolean compareQue(Map<?, ?> beforeQue, Map<?, ?> afterQue) throws GnomesAppException
    {

        // containerRequest,sendDataDetailsは判定しない
        beforeQue.remove(CommonConstants.CONTAINER_REQUEST);
        beforeQue.remove(CommonConstants.SEND_DATA_DETAILS);
        afterQue.remove(CommonConstants.CONTAINER_REQUEST);
        afterQue.remove(CommonConstants.SEND_DATA_DETAILS);
        Object[] beforeQueData = beforeQue.values().toArray();
        Object[] afterQueData = afterQue.values().toArray();
        for (int i = 0; i < beforeQueData.length; i++) {
            if (!beforeQueData[i].equals(afterQueData[i])) {
                return false;
            }
        }

        return true;
    }

}