package com.gnomes.common.util;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.external.data.FileTransferBean;


/**
 * TalendJOB共通処理クラス
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
public class JobBase {

    public static void setContextInfo(Properties context) throws Exception {
    	try{

    		Set<String> contextNameList = context.stringPropertyNames();
    		for(String contextName: contextNameList){

    	        CDI<Object> current = CDI.current();
    	        Class<?> clazz;
    	        clazz = Class.forName(new FileTransferBean().getClass().getName());

    	        Instance<?> instance = current.select(clazz);
    	    	Method method = clazz.getMethod("get"+contextName, new Class[0]);
    	    	Object result = method.invoke(instance.get(),  (Object[]) null);

    	    	if(!Objects.isNull(result)){
    	    		context.setProperty(contextName, (String)result);
    	    	}

    		}

    	}

    	catch(IllegalStateException e){

    	}

    }

    /**
     * コンテキスト情報設定.
     * <pre>
     * 設定対象クラスをCDIより取得し、対象の項目値をコンテキストに設定する。
     * </pre>
     * @param context コンテキスト情報
     * @param className 設定対象クラス名(パッケージ名含む)
     * @throws Exception
     */
    public static void setContextInfo(Properties context, String className) throws Exception {

        CDI<Object> current = CDI.current();
        Class<?> clazz = Class.forName(className);
        Instance<?> instance = current.select(clazz);

        Set<String> contextNameList = context.stringPropertyNames();

        for (String contextName: contextNameList) {

            Method method = clazz.getMethod("get"+ StringUtils.apitalise(contextName), new Class[0]);
            Object result = method.invoke(instance.get(),  (Object[]) null);

            if (!Objects.isNull(result)) {
                context.setProperty(contextName, (String)result);
            }

        }

    }

    /**
     * 配列をジョブの引数へ設定するコンテキスト形式に変換
     * @param data コンテキストへ設定する文字列データ
     * @throws Exception
     */
    public static String[] convertContext(String[] data) throws Exception {

    	String[] contextParam = new String[data.length];

    	for(int i = 0; i < data.length; i++){

    		contextParam[i] = "--context_param " + data[i];

    	}

    	return contextParam;

    }

    /**
     * 対象クラスのインスタンスをCDIより取得.
     * <pre>
     * 取得できなかった場合、nullを返却。
     * </pre>
     * @param className クラス名
     * @return CDIより取得した対象クラスのインスタンス
     * @throws GnomesAppException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
	public static <T> T getCDIInstance(String className) throws Exception {

        CDI<Object> current = CDI.current();
        Class<?> clazz;
        clazz = Class.forName(className);

        Instance<?> instance = current.select(clazz);

        if (!instance.iterator().hasNext()) {
            Exception ex = new Exception(
                    "not found cdi class [" + className + "]");
            throw ex;
        }
        return (T) instance.get();
    }


}
