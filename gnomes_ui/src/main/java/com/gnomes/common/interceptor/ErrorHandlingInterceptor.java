package com.gnomes.common.interceptor;

import java.util.Objects;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NonexistentConversationException;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PessimisticLockException;
import javax.validation.ValidationException;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

/**
 * エラーハンドリングインターセプター
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@ErrorHandling
@Dependent
@Priority(Interceptor.Priority.APPLICATION)
public class ErrorHandlingInterceptor {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    @Resource(lookup = CommonConstants.APP_NAME)
    String appName;

    @Inject
    ContainerRequest requestContext;

    @Inject
    ContainerResponse responseContext;

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
//        this.logHelper.fine(this.logger, className, methodName,
//                String.format("%s#%s [start]", className, methodName));

        this.requestContext.setIsThrowException(true);

        // 実行
        Object result = null;
        try {
            result = context.proceed();
            this.requestContext.setIsThrowException(false);
        }
        catch (GnomesException e) {
            // Gnomeシステム例外はスローする
            throw e;
        }
        catch (GnomesAppException e) {

            GnomesException ex;

            if (e.getMessageNo() == null) {
                // 通常は Message No. を設定する。
//                ex = new GnomesException(e.getMessage(), e);
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                ex = new GnomesException(e, GnomesMessagesConstants.ME01_0001);
            } else {
                ex = new GnomesException(e);
            }

            doCatchException(ex, className, methodName);
            this.requestContext.setIsThrowException(false);
            throw ex;
        }
        // 楽観ロック例外、悲観ロック例外 処理
        catch (OptimisticLockException | PessimisticLockException e) {

            // （ME01.0003）他ユーザが更新しました。最新を取得して再操作してください。
            GnomesException ex = new GnomesException(e, GnomesMessagesConstants.ME01_0003);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.requestContext , GnomesMessagesConstants.ME01_0003);

            // エラーログ
            this.logHelper.severe(this.logger, className, methodName, MessagesHandler.getExceptionMessage(this.requestContext, ex), ex);

            //トレースモニター情報ログ
            if (requestContext.existTraceMonitor()) {
                StringBuilder trace = new StringBuilder(128);
                trace.append("trace message");
                trace.append(System.getProperty("line.separator"));
                trace.append(requestContext.dumpTraceMonitor());
                this.logHelper.severe(this.logger, className, methodName, trace.toString());
                requestContext.setIsThrowException(true);
            }
            throw ex;
        }
        // カンバゼーション  No conversation found
        catch(NonexistentConversationException e) {
            // エラー画面に遷移させるため、そのままスローする
            throw e;
        }
        //DAOのSelectで件数が0件の場合にハンドルされない状態で例外をキャッチした場合
        //xxxテーブルが取得できませんでしたのAppExceptionを作る
        catch(NoResultException e){

            Object[] parameters = context.getParameters();

            /** データベースからのデータ取得が出来ませんでした。\n詳細のデータアクセス及びパラメータを確認ください。 */
            GnomesAppException ex =  exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0241);

            /** データアクセスクラス名 [{0}] メソッド名 [{1}] */
            ex.addChildMessageData(new MessageData(GnomesMessagesConstants.ME01_0242,new String[]{className,methodName}));
            if(! Objects.isNull(parameters)){
                int count=1;
                for(Object param : parameters){
                    /** パラメータ：No[{0}] 値 [{1}] */
                    ex.addChildMessageData(new MessageData(GnomesMessagesConstants.ME01_0243,new String[]{String.valueOf(count),param.toString()}));
                    count++;
                }
            }

            this.logHelper.severe(this.logger, className, methodName,
                    MessagesHandler.getString(GnomesMessagesConstants.ME01_0241),e);
            throw ex;

        }
        // Validate不正 何もしない
        catch (ValidationException e) {

        }
        catch (Exception e) {

            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = new GnomesException(e, GnomesMessagesConstants.ME01_0001);

            doCatchException(ex, className, methodName);
            throw ex;
        }

        // 終了ログ
//        this.logHelper.fine(this.logger, className, methodName,
//                String.format("%s#%s [end]", className, methodName));

        return result;
    }

    /**
     * エラー処理
     * @param ex 例外
     * @param className クラス名
     * @param methodName メソッド名
     */
    private void doCatchException(GnomesException ex, String className, String methodName) {

        int maxNumParams = 10;              // Message テーブルの補足情報の最大数
        String maxParamLength = "2000";     // Message テーブルの補足情報のデータ長

        Throwable exlooper = ex;
        String exClassName = ex.getClass().toString();

        // 予期しないエラー（エラーNo.なし）以外、かつ内部エラーが存在する場合
//        if (ex.getMessageNo() != null && ex.getCause() != null && ex.getCause().getClass().toString() != exClassName) {
        if (ex.getMessageNo() != null && ex.getCause() != null
                && !ex.getCause().getClass().toString().equals(exClassName)
                && GnomesMessagesConstants.ME01_0001
                        .equals(ex.getMessageNo())) {

            /** ---------------------------------------------------------------------------
             * [2020/05/09 浜本記載] アプリケーションエラーのログに対してコールスタックを
             * 子メッセージに指定して表示してもユーザにって不明な内容なので
             * ダイアログに出さず、メッセージ履歴にも残さない。（ログは残す）
             * ロジックはコメントアウトして置く
             *  （コールスタック以外のなにか必要な情報を残す可能性があるため）
            ----------------------------------------------------------------------------**/

            ////StringWriter sw = new StringWriter();
            //PrintWriter pw = new PrintWriter(sw);
            // 内部例外がなくなるか、メッセージが同じになるまで繰り返す
            //while (exlooper != null && ex.getCause().getClass().toString() != exClassName) {
            //    // 内部例外も含め出力する。
            //    exClassName = exlooper.getClass().toString();
            //    exlooper.printStackTrace(pw);
            //    exlooper = exlooper.getCause();
            //}
            //pw.flush();
            //String str = sw.toString();

            //List<String> params = new ArrayList<>();

            // メッセージパラメータの追加
            //Object[] messageParams = ex.getMessageParams();
            //if(messageParams != null){
            //    for(int i=0; i<messageParams.length; i++) {
            //        if (params.size() >= maxNumParams) {
            //            break;
            //        }
            //        params.add(messageParams[i].toString());
            //    }
            //}

            // スタックトレースの追加
            //Matcher m = Pattern.compile("[\\s\\S]{1,"+ maxParamLength + "}").matcher(str);
            //while (m.find()) {
            //    if (params.size() >= maxNumParams) {
            //        break;
            //    }
            //    params.add(m.group());
            //}

            //for (int i=params.size() ; i<maxNumParams; i++) {
            //    params.add("");
            //}

            // メソッド最後のログ出力に備える
            //ex.setMessageParams((String[])params.toArray(new String[0]));

            // メッセージ設定
            //List<MessageData> childMessageDatas = new ArrayList<MessageData>();
            //for (Object stackTrace: params){
            //     MessageData messageData = new MessageData(GnomesMessagesConstants.ME01_0238, new Object[]{stackTrace});
            //     childMessageDatas.add(messageData);
            //}
            // ダミーメッセージパラメータの設定
            Object[] dummyMessageParams = new Object[10];
                for(int i=0; i<dummyMessageParams.length; i++) {
                    dummyMessageParams[i] = "";
            }
            ex.setMessageParams(dummyMessageParams);
            //ex.setChildMessageDatas(childMessageDatas);
            MessagesHandler.setMessageGnomesException(this.requestContext , ex);

        } else {
            // メッセージ設定
            MessagesHandler.setMessageGnomesException(this.requestContext , ex);
        }

        //エラーログ
        this.logHelper.severe(this.logger, className, methodName, MessagesHandler.getExceptionMessage(this.requestContext, ex), ex);

        //トレースモニター情報ログ
        if (requestContext.existTraceMonitor()) {
            StringBuilder trace = new StringBuilder(128);
            trace.append(System.getProperty("line.separator"));
            trace.append("---- TRACE MESSAGE (START) ----");
            trace.append(System.getProperty("line.separator"));
            trace.append(requestContext.dumpTraceMonitor());
            trace.append(System.getProperty("line.separator"));
            trace.append("---- TRACE MESSAGE (END) ----");
            trace.append(System.getProperty("line.separator"));
            this.logHelper.severe(this.logger, className, methodName, trace.toString());
            requestContext.setIsThrowException(true);
        }
    }

}
