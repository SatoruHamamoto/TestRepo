package com.gnomes.common.interceptor;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.system.model.PopupMessageModel;
import com.gnomes.uiservice.ContainerRequest;

/**
 * ポップアップメッセージ　インターセプター
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/16 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@PopupMessageHandling
@Dependent
@Priority(Interceptor.Priority.APPLICATION)
public class PopupMessageHandlingInterceptor {


    /**
     * ロガー
     */
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    ContainerRequest requestContext;

    // セッション Baen
    @Inject
    GnomesSessionBean gnomesSessionBean;

    @Inject
    PopupMessageModel popupMessageLogic;

    /**
     * ハンドリング処理
     * @param context コンテキスト
     * @return 処理戻り値
     * @throws Exception 例外
     */
    @AroundInvoke
    public Object handling(InvocationContext context) throws Exception {

        String className = context.getTarget().getClass().getSuperclass().getName();
        String methodName = context.getMethod().getName();

        // 開始ログ
        this.logHelper.fine(this.logger, className, methodName,
                String.format("%s#%s [start]", className, methodName));

        Object completed = null;

        // ポップアップメッセージ一覧取得
        try {
            popupMessageLogic.getPopupMessage();
        }
        catch (Exception e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            String errMessage = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, className, methodName, errMessage, e);

            gnomesSessionBean.setMessageList(new ArrayList<>());
        }

        // メソッド実行
        try {
            completed = context.proceed();
            return completed;
        } catch (Throwable t) {
            throw t;
        } finally {
            // 終了ログ
            this.logHelper.fine(this.logger, className, methodName,
                    String.format("%s#%s [end]", className, methodName));
        }
    }



}
