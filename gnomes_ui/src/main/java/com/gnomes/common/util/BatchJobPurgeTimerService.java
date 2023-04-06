/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/02/17 20:24 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
package com.gnomes.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TimerService;
import javax.inject.Inject;

import com.gnomes.common.logging.LogHelper;

/**
 * JBatchのJOBを実行すると(in-memory)レポジトリに履歴が残るためメモリリークする
 * そのため、タイマーを使って定周期でリポジトリの掃除をして回避する
 * 時間間隔は5分とする
 * @author 03501213
 *
 */
@Singleton
public class BatchJobPurgeTimerService
{
    protected static final String className           = "BatchJobPurgeTimerService";
    /**
     * バッチJOB名（gnomes_ui/resources/batch-jobs/gnomes_purge_batchlet.xml)
     */
    protected static final String BATCHLET            = "gnomes_purge_batchlet";

    /** ログヘルパー */
    @Inject
    protected LogHelper           logHelper;

    @Inject
    protected transient Logger    logger;

    @Resource
    private TimerService          timerService;

    @Schedule(hour = "*", minute = "*/5", second = "0", persistent = false)
    public void timeout()
    {
        final String methodName = "timeout";
        Map<String, String> mapProperties = new HashMap<String, String>();
        JobOperator jobOperator = BatchRuntime.getJobOperator();

        try {
            //定周期バッチレポジトリ掃除実行
            jobOperator.start(BATCHLET, null);
        }
        catch (Exception e) {
            this.logHelper.severe(logger, className, methodName, "batch exec exception", e);
        }
    }
}
