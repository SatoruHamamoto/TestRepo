package com.gnomes.common.util;

import java.lang.reflect.Method;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.uiservice.ContainerRequest;

/**
 * TalendJOB実行クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/15 YJP/A.Oomori           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TalendJobRun {

    @Inject
    protected ContainerRequest req;

    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    @Inject
    protected GnomesSystemBean gnomesSystemBean;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /** Talend実行ジョブ名 */
    private static final String JOB_METHOD_NAME = "runJob";


    public TalendJobRun() {
    }

    /**
     * メゾット呼出（上位連携）
     * @param jobName ジョブ名
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public static void runJobProcess(String jobName) throws NoSuchMethodException, SecurityException, Exception {
        FileTransferBean fileTransferBean = JobBase.getCDIInstance(new FileTransferBean().getClass().getName());
        String dataType = fileTransferBean.getFileType();
        MstrExternalIfFileDefineDao externalIfFileDefineDao = JobBase.getCDIInstance(new MstrExternalIfFileDefineDao().getClass().getName());

        // X102から実行クラス、メゾット名の取得
        String[] runJobClassMethod = externalIfFileDefineDao.getSetValue(dataType, jobName);

        // 取得できなかった場合、処理を終了する。
        if(Objects.isNull(runJobClassMethod)){
            return;
        }

        System.out.println("Start " + runJobClassMethod[0] + " : " + runJobClassMethod[1]);

        CDI<Object> current = CDI.current();
        Class<?> clazz;
        clazz = Class.forName(runJobClassMethod[0]);

        Instance<?> instance = current.select(clazz);
        Method method = clazz.getMethod(runJobClassMethod[1], new Class[0]);
        method.invoke(instance.get(),  (Object[]) null);

        System.out.println("End " + runJobClassMethod[0] + " : " + runJobClassMethod[1]);

    }

    /**
     * メゾット呼出（上位連携）
     * @param jobName ジョブ名
     * @param jobParam 呼出メゾット引数
     * @param isJobCDI CDIを使用するかどうか
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public static void runJobProcess(String jobName, Object jobParam, boolean isJobCDI) throws NoSuchMethodException, SecurityException, Exception {
        FileTransferBean fileTransferBean = JobBase.getCDIInstance(new FileTransferBean().getClass().getName());
        String dataType = fileTransferBean.getFileType();
        MstrExternalIfFileDefineDao externalIfFileDefineDao = JobBase.getCDIInstance(new MstrExternalIfFileDefineDao().getClass().getName());

        // X102から実行クラス、メゾット名の取得
        String[] runJobClassMethod = externalIfFileDefineDao.getSetValue(dataType, jobName);

        // 取得できなかった場合、処理を終了する。
        if (Objects.isNull(runJobClassMethod)) {
            return;
        }

        System.out.println("Start " + runJobClassMethod[0] + " : " + runJobClassMethod[1]);

        Class<?> clazz;
        clazz = Class.forName(runJobClassMethod[0]);
        Object instance;

        // 呼出ジョブがCDIかどうか
        if(isJobCDI){
            CDI<Object> current = CDI.current();
            instance = current.select(clazz).get();
        }
        else{
            instance = clazz.newInstance();
        }

        Method method;

        // 呼出ジョブに引数が設定されている場合
        if(!Objects.isNull(jobParam)){
            method = clazz.getMethod(runJobClassMethod[1], jobParam.getClass());
            method.invoke(instance, new Object[]{jobParam});
        }
        else{
            method = clazz.getMethod(runJobClassMethod[1], new Class[0]);
            method.invoke(instance, (Object[])null);
        }

        System.out.println("End " + runJobClassMethod[0] + " : " + runJobClassMethod[1]);

    }


    /**
     * Talendジョブ呼出
     * @param jobName ジョブ名
     * @param jobContext ジョブコンテキスト
     * @param isJobCDI CDIを使用するかどうか
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public static void runJob(String jobName, Object jobContext, boolean isJobCDI) throws NoSuchMethodException, SecurityException, Exception {

        System.out.println("Start " + jobName + " : " + JOB_METHOD_NAME);

        Class<?> clazz;
        clazz = Class.forName(jobName);
        Object instance;

        // 呼出ジョブがCDIかどうか
        if(isJobCDI){
            CDI<Object> current = CDI.current();
            instance = current.select(clazz).get();
        }
        else{
            instance = clazz.newInstance();
        }

        Method method;

        // 呼出ジョブに引数が設定されている場合
        if(!Objects.isNull(jobContext)){
            method = clazz.getMethod(JOB_METHOD_NAME, String[].class);
            method.invoke(instance, new Object[]{jobContext});
        }
        else{
            method = clazz.getMethod(JOB_METHOD_NAME, new Class[0]);
            method.invoke(instance, (Object[])null);
        }

        System.out.println("End " + jobName + " : " + JOB_METHOD_NAME);

    }


}
