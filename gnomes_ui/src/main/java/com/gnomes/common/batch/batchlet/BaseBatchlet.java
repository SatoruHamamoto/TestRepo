package com.gnomes.common.batch.batchlet;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.batch.api.AbstractBatchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * jBatch Batchlet 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/22 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class BaseBatchlet extends AbstractBatchlet {

    /**
     * プロセス実行結果：成功
     */
    public static final String PROCESS_RESULT_COMPLETED = "COMPLETED";

    @Inject
    protected JobContext jobContext;

    /** バッチ処理実行中マップ */
    private static Map<String, String> batchInProcessMap = new ConcurrentHashMap<>();

    /**
     * バッチで共通のリクエストパラメータを個別に設定
     * 個別に設定不要の場合は、処理なしとする
     * @param p 設定プロパティ
     */
    public abstract void setBatchCommonRequestPartameter(Properties p);

    /**
     * プロパティ取得
     * @return プロパティ
     */
    public Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        Properties p = operator.getParameters(jobContext.getExecutionId());

        // 個別共通設定
        setBatchCommonRequestPartameter(p);
        return p;
    }

    /**
     * バッチ処理実行中マップ設定
     * @param className クラス名
     * @param methodName メソッド名
     */
    public static void setBatchInProcessMap(String className, String methodName) {

        String keyValue = getKeyValue(className, methodName);

        if (!batchInProcessMap.containsKey(keyValue)) {
            batchInProcessMap.put(keyValue, keyValue);
        }

    }

    /**
     *
     * バッチ処理実行中確認
     * @param className クラス名
     * @param methodName メソッド名
     * @return 未実行の場合、<code>true</code>を返却
     */
    public boolean checkBatchInProcess(String className, String methodName) {

        String keyValue = getKeyValue(className, methodName);

        if (batchInProcessMap.containsKey(keyValue)) {
            return false;
        }
        return true;

    }

    /**
     * バッチ処理実行中マップクリア
     * @param className クラス名
     * @param methodName メソッド名
     */
    public static void clearBatchInProcessMap(String className, String methodName) {

        String keyValue = getKeyValue(className, methodName);

        if (batchInProcessMap.containsKey(keyValue)) {
            batchInProcessMap.remove(keyValue);
        }

    }

    /**
     * バッチ処理実行中マップキー値取得
     * @param className クラス名
     * @param methodName メソッド名
     * @return バッチ処理実行中マップキー値
     */
    private static String getKeyValue(String className, String methodName) {

        StringBuilder keyValue = new StringBuilder();
        keyValue.append(className).append(".").append(methodName);
        return keyValue.toString();

    }

}
