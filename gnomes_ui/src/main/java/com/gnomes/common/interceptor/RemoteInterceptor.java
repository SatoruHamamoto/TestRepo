package com.gnomes.common.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.client.Entity;

import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.ProxyImpl;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.rest.service.RemoteConnectDataInput;
import com.gnomes.rest.service.RestServiceResult;

/**
 * リモート接続インターセプター
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@Remote
@Dependent
@Priority(Interceptor.Priority.APPLICATION + 100)
public class RemoteInterceptor {

    /** Logger. */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    /**
     * ハンドリング処理
     * @param context コンテキスト
     * @return 処理戻り値
     * @throws Exception 例外
     */
    @AroundInvoke
    public Object handling(InvocationContext context) throws Exception {

        // クラス名取得
        String className = context.getTarget().getClass().getSuperclass().getName();
        // メソッド名取得
        String methodName = context.getMethod().getName();

        // 実行結果 Object初期化
        Object result = null;

        try {
            // 開始ログ
            this.logHelper.fine(this.logger, className, methodName,
                    String.format("%s#%s [start]", className, methodName));

            // メソッド取得
            Method method = context.getMethod();
            // パラメータ情報
            Object[] parameters = context.getParameters();

            // プロキシ処理クラス
            ProxyImpl proxy = this.getProxyInfo(parameters);

            if (proxy == null
                    || !proxy.isCreateProxy()) {
                // 通常の処理実施
                return context.proceed();
            }

            // リモート呼び出し
            this.logHelper.fine(this.logger, className, methodName,
                    String.format("%s#%s [remote start]", className, methodName));

            Entity<?> paramEntity = this.setParamEntity(proxy, parameters);

            // リモート接続共通コマンド呼び出し
            RestServiceResult restServiceResult =
                    proxy.post(
                            "Y99002S001",
                            "remoteConnect",
                            paramEntity,
                            RestServiceResult.class);

            if (restServiceResult.getIsSuccess()) {

                if (restServiceResult.getCommandResponse() != null) {
                    result = ConverterUtils.readJson(
                            restServiceResult.getCommandResponse().toString(), method.getReturnType());
                }

            } else {
                throw restServiceResult.getGnomesAppException();
            }

            this.logHelper.fine(this.logger, className, methodName,
                    String.format("%s#%s [remote end]", className, methodName));

        } finally {
            // 終了ログ
            this.logHelper.fine(this.logger, className, methodName,
                    String.format("%s#%s [end]", className, methodName));

        }

        return result;

    }

    /**
     * プロキシ処理クラス取得.
     * @param parameters パラメータ情報
     * @return プロキシ処理クラス
     */
    protected ProxyImpl getProxyInfo(Object[] parameters) {

        for (int i = 0; i < parameters.length; i++) {

            if (parameters[i] instanceof ProxyImpl) {
                return (ProxyImpl) parameters[i];
            }

        }
        return null;

    }

    /**
     * Webサービスパラメータ設定.
     * @param className クラス名
     * @param methodName メソッド名
     * @param parameters パラメータ
     * @return jsonデータ
     * @throws Exception
     */
    protected Entity<?> setParamEntity(ProxyImpl proxy, Object[] parameters) throws Exception {

        RemoteConnectDataInput input = new RemoteConnectDataInput();
        // コマンドID
        input.setCommand(CommandIdConstants.COMMAND_ID_REMOTE_CONNECT);
        // クラス名
        input.setClassName(proxy.getRemoteClassName());
        // メソッド名
        input.setMethodName(proxy.getRemoteMethodName());
        // staticメソッドフラグ
        input.setStaticMethodFlag(proxy.isStaticMethodFlag());
        // リクエストパラメータ
        List<String> requestParams = new ArrayList<>();
        // リクエストパラメータクラス
        List<Class<?>> requestParamsType = new ArrayList<>();

        for (int i = 1; i < parameters.length; i++) {
            requestParams.add(ConverterUtils.getJson(parameters[i]));
            requestParamsType.add(parameters[i].getClass());
        }
        input.setRequestParams(requestParams.toArray(new String[requestParams.size()]));
        input.setRequestParamsType(requestParamsType.toArray(new Class<?>[requestParamsType.size()]));

        return Entity.json(input);

    }

}
