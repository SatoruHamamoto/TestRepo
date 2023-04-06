package com.gnomes.common.batch.batchlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.uiservice.ContainerRequest;

/**
 * EJBラッパー インターセプター
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/26 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@EJBWrapper
@Dependent
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class EJBWrapperInterceptor {

    /**
     * EJB リクエスト パラメータ キー:エリアID
     */
    public static final String EJB_REQUEST_PARAM_KEY_AREAID = "areaId";

    /**
     * EJB リクエスト パラメータ キー:エリア名
     */
    public static final String EJB_REQUEST_PARAM_KEY_AREANAME = "areaName";

    /**
     * EJB リクエスト パラメータ キー:コンピュータ名
     */
    public static final String EJB_REQUEST_PARAM_KEY_COMPUTERNAME = "computerName";

    /**
     * EJB リクエスト パラメータ キー:IPアドレス
     */
    public static final String EJB_REQUEST_PARAM_KEY_IPADDRESS = "ipAddress";

    /**
     * EJB リクエスト パラメータ キー:言語
     */
    public static final String EJB_REQUEST_PARAM_KEY_LANGUAGE = "language";

    /**
     * EJB リクエスト パラメータ キー:ユーザロケール 言語
     */
    public static final String EJB_REQUEST_PARAM_KEY_USERLOCALE_LANGUAGE = "userLocaleLanguage";

    /**
     * EJB リクエスト パラメータ キー:ユーザロケール 国
     */
    public static final String EJB_REQUEST_PARAM_KEY_USERLOCALE_COUNTRY = "userLocaleCountry";

    /**
     * EJB リクエスト パラメータ キー:サイトID
     */
    public static final String EJB_REQUEST_PARAM_KEY_SITECODE = "siteCode";

    /**
     * EJB リクエスト パラメータ キー:サイト名
     */
    public static final String EJB_REQUEST_PARAM_KEY_SITENAME = "siteName";

    /**
     * EJB リクエスト パラメータ キー:ユーザID
     */
    public static final String EJB_REQUEST_PARAM_KEY_USERID = "userId";

    /**
     * EJB リクエスト パラメータ キー:ユーザ名
     */
    public static final String EJB_REQUEST_PARAM_KEY_USERNAME = "userName";

    /**
     * EJB リクエスト パラメータ キー:ユーザKey
     */
    public static final String EJB_REQUEST_PARAM_KEY_USERKEY = "userKey";

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    ContainerRequest requestContext;

    /**
     * インターセプター処理
     * @param context コンテキスト
     * @return メソッド戻り値
     * @throws Exception 例外
     */
    @AroundInvoke
    public Object wrapper(InvocationContext context) {

        String className = context.getTarget().getClass().getSuperclass()
                .getName();
        String methodName = context.getMethod().getName();

        //開始ログ
        this.logHelper.fine(this.logger, className, methodName,
                String.format("EJB %s#%s [start]", className, methodName));

        Object result = null;
        try {
            Object[] objParams = context.getParameters();
            if (objParams == null || objParams.length != 1) {
                // EJBメソッド実行には、プロパティが必須です。クラス名=[{0},メソッド名=[{1}]]
                Object[] errParam = {
                        className,
                        methodName
                };
                GnomesAppException ex = new GnomesAppException(null, GnomesMessagesConstants.ME01_0130, errParam);
                throw ex;
            }

            if (!(objParams[0] instanceof Properties)) {
                // EJBメソッド実行には、プロパティが必須です。クラス名=[{0},メソッド名=[{1}]]
                Object[] errParam = {
                        className,
                        methodName
                };
                GnomesAppException ex = new GnomesAppException(null, GnomesMessagesConstants.ME01_0130, errParam);
                throw ex;
            }

            Properties p = (Properties) objParams[0];

            //共通初期処理
            // requestContextに共通情報の設定
            // エリアID
            requestContext.setAreaId(p.getProperty(EJB_REQUEST_PARAM_KEY_AREAID));
            // エリア名
            requestContext
                    .setAreaName(p.getProperty(EJB_REQUEST_PARAM_KEY_AREANAME));
            // コンピュータ名
            requestContext.setComputerName(
                    p.getProperty(EJB_REQUEST_PARAM_KEY_COMPUTERNAME));
            // IPアドレス
            requestContext
                    .setIpAddress(p.getProperty(EJB_REQUEST_PARAM_KEY_IPADDRESS));
            // 言語
            requestContext
                    .setLanguage(p.getProperty(EJB_REQUEST_PARAM_KEY_LANGUAGE));
            // ユーザロケール
            String language = p
                    .getProperty(EJB_REQUEST_PARAM_KEY_USERLOCALE_LANGUAGE);
            String country = p
                    .getProperty(EJB_REQUEST_PARAM_KEY_USERLOCALE_COUNTRY);
            if (language != null && language.length() > 0 && country != null
                    && country.length() > 0) {
                requestContext.setUserLocale(new Locale(language, country));
            }
            // 拠点コード
            requestContext.setSiteCode(p.getProperty(EJB_REQUEST_PARAM_KEY_SITECODE));
            // 拠点名
            requestContext
                    .setSiteName(p.getProperty(EJB_REQUEST_PARAM_KEY_SITENAME));
            // ユーザID
            requestContext.setUserId(p.getProperty(EJB_REQUEST_PARAM_KEY_USERID));
            // ユーザ名
            requestContext
                    .setUserName(p.getProperty(EJB_REQUEST_PARAM_KEY_USERNAME));
            // ユーザKey
            requestContext.setUserKey(p.getProperty(EJB_REQUEST_PARAM_KEY_USERKEY));


            //実行
            result = context.proceed();
        } catch (GnomesException ex) {
            // ErrorHandlingされていないメソッドでGnomesExceptionをスローした
            if (ex.getMessageNo() == null) {

                // メッセージ設定
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
//                MessagesHandler.setMessageNo(this.requestContext,
//                        GnomesMessagesConstants.ME01_0001);

                //エラーログ
//                this.logHelper.severe(this.logger, className, methodName, MessagesHandler
//                        .getExceptionMessage(this.requestContext, ex));
//                this.logHelper.severe(this.logger, className, methodName, ex.getMessage(), ex);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0001);
                doCatchException(ex, className, methodName);

                // ErrorHandlingされたメソッドでGnomesExceptionをスローした
            } else {
                // 処理なし
            }
        }
        // ErrorHandlingされていないメソッドでGnomesAppExceptionをスローした
        catch (GnomesAppException ex) {
            GnomesException e;

            if (ex.getMessageNo() == null) {
                // 通常は Message No. を設定する。
                e = new GnomesException(ex, GnomesMessagesConstants.ME01_0001);
            } else {
                e = new GnomesException(ex);
            }

            doCatchException(e, className, methodName);
        } catch (Throwable e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = new GnomesException(e,
                    GnomesMessagesConstants.ME01_0001);

            doCatchException(ex, className, methodName);
        }

        //終了ログ
        this.logHelper.fine(this.logger, className, methodName,
                String.format("EJB %s#%s [end]", className, methodName));

        return result;
    }

    /**
     * 例外処理
     * @param ex 例外
     * @param className クラス名
     * @param methodName メソッド名
     */
    private void doCatchException(GnomesException ex, String className, String methodName) {

        // (ME01_0001)アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
        // の場合
        if (ex.getMessageNo() != null && GnomesMessagesConstants.ME01_0001
                .equals(ex.getMessageNo())) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.flush();
            String str = sw.toString();

            List<String> params = new ArrayList<String>();

            Matcher m = Pattern.compile("[\\s\\S]{1,2000}").matcher(str);
            while (m.find()) {
                if (params.size() >= 10) {
                    break;
                }
                params.add(m.group());
            }

            for (int i = params.size(); i < 10; i++) {
                params.add("");
            }

            // メソッド最後のログ出力に備える
            ex.setMessageParams((String[])params.toArray(new String[0]));

            // メッセージ設定
            MessagesHandler.setMessageNo(this.requestContext, ex.getMessageNo(),
                    params);

        } else {
            // メッセージ設定
            MessagesHandler.setMessageGnomesException(this.requestContext , ex);
        }

        //エラーログ
        this.logHelper.severe(this.logger, className, methodName,
                MessagesHandler.getExceptionMessage(this.requestContext, ex)
                ,ex);

    }

}
