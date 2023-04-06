package com.gnomes.common.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.RemoteConnectFormBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IServiceFormBean;

/** リモート接続共通コマンド. */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/23 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_REMOTE_CONNECT)
public class RemoteConnectCommand extends ServiceBaseCommand {

    /** リクエストパラメーター名称: クラス名. */
    protected static final String REQUEST_CLASS_NAME = "className";

    /** リクエストパラメーター名称: メソッド名. */
    protected static final String REQUEST_METHOD_NAME = "methodName";

    /** リクエストパラメーター名称: staticメソッドフラグ. */
    protected static final String REQUEST_STATIC_METHOD_FLAG = "staticMethodFlag";

    /** リクエストパラメーター名称:リクエストパラメータ(Json). */
    protected static final String REQUEST_REQUEST_PARAMS = "requestParams";

    /** リクエストパラメーター名称:リクエストパラメータ型 */
    protected static final String REQUEST_REQUEST_PARAMS_TYPE = "requestParamsType";

    /** LogicFactory. */
    @Inject
    protected LogicFactory logicFactory;

    /** 処理結果. */
    protected Object commandResponse;

    /**
     * コンストラクタ.
     */
    public RemoteConnectCommand() {

    }

    @Override
    public IServiceFormBean getFormBean() {
        return new RemoteConnectFormBean();
    }

    @Override
    public Object getCommandResponse() {
        return commandResponse;
    }

    @Override
    public void mainExecute() throws Exception {

        // クラス名取得
        String className = requestContext.getServiceRequestParam(REQUEST_CLASS_NAME);
        // メソッド名取得
        String methodName = requestContext.getServiceRequestParam(REQUEST_METHOD_NAME);
        // staticメソッドフラグ取得
        boolean staticMethodFlag = (boolean) requestContext.getServiceRequestParamObject(REQUEST_STATIC_METHOD_FLAG);
        // リクエストパラメータクラス取得
        Class<?>[] parameterTypes =
                (Class<?>[]) requestContext.getServiceRequestParamObject(REQUEST_REQUEST_PARAMS_TYPE);
        // リクエストパラメータ取得
        Object[] requestParams = this.getParametars(parameterTypes);


        Class<?> cl = Class.forName(className);
        Object instance;

        try {
            // CDIからインスタンスを取得
            instance = this.logicFactory.getCDIInstance(className);

            if (instance == null) {
                Constructor<?> constructor = cl.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = constructor.newInstance();
            }

        } catch (ClassNotFoundException e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw this.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0001, e);
        }

        Method method = null;
        Object result = null;

        try {

            if (parameterTypes == null || parameterTypes.length == 0) {
                // 引数なしの場合
                method = cl.getDeclaredMethod(methodName);
                method.setAccessible(true);
                if (staticMethodFlag) {
                    result = method.invoke(null);
                } else {
                    result = method.invoke(instance);
                }
            } else {
                // 引数ありの場合
                method = cl.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                if (staticMethodFlag) {
                    result = method.invoke(null, requestParams);
                } else {
                    result = method.invoke(instance, requestParams);
                }
            }

        } catch (InvocationTargetException e) {

            throw exceptionFactory.createGnomesAppException(e.getTargetException());

        }

        if (result != null) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            this.commandResponse = mapper.writeValueAsString(result);

        }

    }

    @Override
    public void preExecute() throws Exception {
        // 処理なし

    }

    @Override
    public void postExecute() throws Exception {
        // 処理なし

    }

    @Override
    public void initCheckList() throws GnomesAppException, Exception {
        // 処理なし

    }

    @Override
    public void validate() throws GnomesAppException {
        // 処理なし

    }

    @Override
    public void setFormBeanForRestore() throws GnomesException {
        // 処理なし

    }

    /**
     * リクエストパラメータ取得.
     * @param parameterTypes リクエストパラメータタイプ
     * @return
     * @throws GnomesAppException
     */
    protected Object[] getParametars(Class<?>[] parameterTypes) throws GnomesAppException {

        try {

            if (parameterTypes == null || parameterTypes.length == 0) {
                return null;
            }

            String[] params =
                    (String[]) requestContext.getServiceRequestParamObject(REQUEST_REQUEST_PARAMS);

            Object[] requestParams = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                requestParams[i] = ConverterUtils.readJson(params[i], parameterTypes[i]);
            }
            return requestParams;

        } catch (Exception e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw this.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0001, e);

        }
    }

}
