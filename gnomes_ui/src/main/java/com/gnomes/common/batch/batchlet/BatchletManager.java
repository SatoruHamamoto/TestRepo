package com.gnomes.common.batch.batchlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;

/**
 * jBatch Batchlet起動クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class BatchletManager extends BaseLogic {



    @Inject
    protected JobContext jobContext;

    /**
     * デフォルト・コンストラクタ
     */
    public BatchletManager(){

    }

    @TraceMonitor
    @ErrorHandling
    @GnomesTransactional
    public void sampleJbach() throws Exception {

        // fileTransferBeanに受け渡し情報を設定

        // FileTransferSendProcBatchletを実行
        this.runBatch("gnomes_fileTransferRecvProcBatchlet");
    }

    @TraceMonitor
    @ErrorHandling
    @GnomesTransactional
    public void updateSendStateJbatch() throws Exception {

        // fileTransferBeanに受け渡し情報を設定

        // FileTransferSendProcBatchletを実行
        this.runBatch("gnomes_updateSendStateBatchlet");
    }

    @TraceMonitor
    @ErrorHandling
    @GnomesTransactional
    public void Jbatch() throws Exception {

        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put("key", "1234");
            }
        };

        this.runBatch("gnomes_fileTransferSendRequestClearBatchlet", mapProperties);

    }


    /**
     * バッチ実行
     * @param jobXmlName 定義xmlファイル名
     * @return JobExecution
     * @throws GnomesAppException
     */
    public JobExecution runBatch(String jobXmlName) throws GnomesAppException {
        return runBatch(jobXmlName, null);
    }

    /**
     * バッチ実行
     * @param jobXmlName 定義xmlファイル名
     * @param mapProperties 個別プロパティ
     * @return JobExecution
     * @throws GnomesAppException
     */
    public JobExecution runBatch(String jobXmlName,
            Map<String, String> mapProperties) throws GnomesAppException {

        JobExecution jobExecution = null;
        JobOperator jobOperator = BatchRuntime.getJobOperator();

        Properties p = new Properties();

        // 共通情報をプロパティに設定
        setCommonBatchJobParameter(p);

        // 個別情報を設定
        if (mapProperties != null) {
            for (Map.Entry<String, String> e : mapProperties.entrySet()) {
                p.setProperty(e.getKey(), e.getValue());
            }
        }
        try {
            Long executionId = jobOperator.start(jobXmlName, p);
            jobExecution = jobOperator.getJobExecution(executionId);
        } catch (Exception e) {
            // バッチ起動時にエラーが発生しました。詳細はエラーメッセージを確認してください。ジョブXmlファイル=[{0}]
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0124);
            Object[] errParam = {
                    jobXmlName
            };
            ex.setMessageParams(errParam);
            throw ex;
        }

        // 戻り値の利用例
        // BatchStatus s = jobExecution.getBatchStatus();
        // getExitStatusがprocessのreturn値
        // String exitStatus = jobExecution.getExitStatus();

        return jobExecution;
    }

    /**
     * バッチパラメータ共通設定
     * @param p パラメータ
     */
    private void setCommonBatchJobParameter(Properties p) {

        // コンピュータ名
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_COMPUTERNAME,
                req.getComputerName());
        // IPアドレス
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_IPADDRESS,
                req.getIpAddress());
        // 言語
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_LANGUAGE,
                req.getLanguage());
        // ユーザロケール
        p.setProperty(
                EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERLOCALE_LANGUAGE,
                req.getUserLocale().getLanguage());
        p.setProperty(
                EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERLOCALE_COUNTRY,
                req.getUserLocale().getCountry());
        // 拠点コード
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_SITECODE,
                req.getSiteCode());
        // 拠点名
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_SITENAME,
                req.getSiteName());
        // ユーザID
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERID,
                req.getUserId());
        // ユーザ名
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERNAME,
                req.getUserName());
        // ユーザKey
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERKEY,
                req.getUserKey());
    }

}
