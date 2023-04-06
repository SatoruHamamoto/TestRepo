package com.gnomes.common.logic;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.batch.batchlet.EJBWrapperInterceptor;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;

/**
 * Jbatch 起動ロジック 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/20 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BaseJbatchLogic extends BaseLogic {

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected transient Logger logger;

    private final String className = "BaseJbatchLogic";

    /**
     * コンストラクタ.
     */
    public BaseJbatchLogic() {
        super();
    }


    /**
     * バッチ実行
     * @param jobXmlName 定義xmlファイル名
     * @param dupCheck   重複チェック有無
     * @return JobExecution
     * @throws GnomesAppException
     */
    public JobExecution runBatch(String jobXmlName, Boolean dupCheck) throws GnomesAppException {
        return runBatch(jobXmlName, null, dupCheck);
    }

    /**
     * バッチ実行
     * @param jobXmlName 定義xmlファイル名
     * @param mapProperties 個別プロパティ
     * @param dupCheck      重複チェック有無
     * @return JobExecution
     * @throws GnomesAppException
     */
    public JobExecution runBatch(String jobXmlName,
            Map<String, String> mapProperties, Boolean dupCheck) throws GnomesAppException {

        final String methodName="runBatch";
        JobExecution jobExecution = null;
        JobOperator jobOperator = BatchRuntime.getJobOperator();

        Properties p = new Properties();

        // 共通情報をプロパティに設定
        setCommonBatchJobParameter(p);

        //---------------------------------------------------------------------
        // 個別情報を設定
        //---------------------------------------------------------------------
        if (mapProperties != null) {
            for (Map.Entry<String, String> e : mapProperties.entrySet()) {
                p.setProperty(e.getKey(), e.getValue());
            }
        }
        try {

            //---------------------------------------------------------------------
            // 実行中JOB存在チェックをして、すでに存在する場合は何もせずリターンする
            //---------------------------------------------------------------------
            if(dupCheck && checkExistRunningJob(jobXmlName,mapProperties)){
                return null;
            }

            //---------------------------------------------------------------------
            // 実行中の相手がいないので、実行開始
            //---------------------------------------------------------------------
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
     * 現在実行中のバッチJOBと今から実行するJOBから比較し
     *  実行中JOBが存在していたらtrueを返す
     *
     * @param jobXmlName        JBatch JOB名
     * @param mapProperties     パラメータ
     * @return
     */
    private boolean checkExistRunningJob(String jobName, Map<String, String> mapProperties)
    {
        final String methodName ="checkExistRunningJob";
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        List<Long> listJobExecutionIds = null;

        //---------------------------------------------------------------------
        //  現在実行中のJOB一覧を得る。無い場合は NoSuchJobExeptionが出力されるので
        //  その場合はチェックをしない
        //---------------------------------------------------------------------
        try {
            listJobExecutionIds = jobOperator.getRunningExecutions(jobName);
        }
        // 一覧が取得できない（存在しない）場合は実行中JOBがないことを意味するので
        //  falseを返す
        catch (NoSuchJobException ex) {
            return false;
        }

        //---------------------------------------------------------------------
        // 仕掛かりJOBリストが取れても件数０なら何もしない
        //---------------------------------------------------------------------
        if (!Objects.isNull(listJobExecutionIds) && listJobExecutionIds.size() == 0) {
            return false;
        }


        //---------------------------------------------------------------------
        //  同一JOB名が存在する場合は、そのパラメータも調べ、完全一致した場合は
        //  実行中JOBがいるものとし、trueで返す
        //---------------------------------------------------------------------
        for (Long executionId : listJobExecutionIds) {

            //一覧からJOB実行情報を得る
            JobExecution otherJobExecution = jobOperator.getJobExecution(executionId);

            //JOB名が一致するか確認
            if (jobName.equals(otherJobExecution.getJobName())) {

                //JOB名が一致する場合、引数のパラメータをチェックする
                //パラメータが無い場合は、JOBだけの一致で確認する
                if(Objects.isNull(mapProperties) || mapProperties.size() == 0){
                    return true;
                }

                Properties properties = otherJobExecution.getJobParameters();

                boolean propertyHit = true;
                for (Map.Entry<String, String> mapProperty : mapProperties.entrySet()) {
                    //プロパティのキー値が存在するかどうか調べる
                    String propValue = (String) properties.get(mapProperty.getKey());

                    //存在しない場合はfalseで抜ける
                    if (Objects.isNull(propValue)) {
                        propertyHit=false;
                        break;
                    }
                    //値が取れても一致しない場合は falseで抜ける
                    if (!propValue.equals(mapProperty.getValue())) {
                        propertyHit=false;
                        break;
                    }
                }
                //プロパティが一致しないJOBの場合は、違うJOBを探すため
                // continue;
                if(propertyHit){
                    //すべてのプロパティが一致したら一致したことを返す
                    return true;
                }
            }
        }

        return false;
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
        // サイトID
        p.setProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_SITECODE,
                req.getSiteCode());
        // サイト名
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
