package com.gnomes.rest.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.rest.client.DemandGnomesServiceRun;

/**
*
* JAX-RS Web サービスリソース  基底クラス (コマンド使用)
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public abstract class BaseServiceLogic extends BaseService {

    // ロジックファクトリー
    @Inject
    transient LogicFactory logicFactory;

    @Inject
    SystemFormBean systemFormBean;

    @Inject
    protected GnomesEjbBean     ejbBean;

    @Inject
    DemandGnomesServiceRun demandGnomesServiceRun;

    public RestServiceResult doLogic(Object input) {

        // アプリケーション Bean、セッションBean のユーザ情報を保持 （コンストラクタではできないため、このタイミングで実施
        if (! (ejbBean.isEjbBatch()) && gnomesSessionBean.getUserId() != null) {
            req.setSessionInfo();
        }

        req.setServiceRequest(input);

        RestServiceResult result = new RestServiceResult();
        result.setIsSuccess(false);
        result.setIsSessionError(true);

        try {
            // windowIdの確認
            String windowId = req.getServiceRequestParam("windowId");
            if (windowId != null) {
                boolean isFound = false;
                for (String item : gnomesSessionBean.getWindowIdList()) {
                    if (item.equals(windowId)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    return result;
                }
            }

            result.setIsSessionError(false);
            Object commandResponse;

            //EJBバッチの場合はSF専用を呼び出す
            if(ejbBean.isEjbBatch()){
                commandResponse = logicFactory.executeServiceLogicForSF();
            }
            else {
                commandResponse = logicFactory.executeServiceLogic();
            }

            // 個別の戻り値を設定
            result.setCommandResponse(commandResponse);

            //メッセージを設定
            req.setMessageBean(result.getMessageBean());

            // 承認者IDを設定
            result.setCertUserId(systemFormBean.getCertUserId());

            // 成功設定
            result.setIsSuccess(true);
            
            // バッチコマンド実行（バッチ処理後に続いて別のバッチ処理を起動する場合）
            demandGnomesServiceRun.execute();
            
        } catch (GnomesException ex) {
            // コマンド、ビジネスロジックでGnomesExceptionをスローした場合
            if (ex.getMessageNo() == null) {

                // メッセージ設定
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                //                MessagesHandler.setMessageNo(req , GnomesMessagesConstants.ME01_0001);

                //エラーログ
                //                this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(req, ex));
                //                this.logHelper.severe(this.logger, null, null, ex.getMessage(), ex);

                ex.setMessageNo(GnomesMessagesConstants.ME01_0001);
                doCatchException(ex);

                // コマンド、ビジネスロジックでGnomesAppException発生しGnomesExceptionに変えた場合
            } else {
                // 処理なし
            }

            try {
                //メッセージを設定
                req.setMessageBean(result.getMessageBean());
            } catch (Exception eex) {
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException eex2 = new GnomesException(eex,
                        GnomesMessagesConstants.ME01_0001);

                doCatchException(eex2);

                try {
                    //メッセージを設定
                    req.setMessageBean(result.getMessageBean());
                } catch (Exception eex3) {
                    // 処理なし
                }
            }
            // 呼び出し元のBL用にエラー情報を設定
            setExceptionInfo(ex, result);
        } catch (GnomesAppException ex) {
            doCatchGnomesAppException(ex, result);
        } catch (Throwable e) {
            if (e.getCause() instanceof GnomesAppException) {
                doCatchGnomesAppException((GnomesAppException) e.getCause(),
                        result);
            } else {

                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException ex = new GnomesException(e,
                        GnomesMessagesConstants.ME01_0001);

                doCatchException(ex);

                //メッセージを設定
                try {
                    req.setMessageBean(result.getMessageBean());
                } catch (Exception eex) {
                    // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                    GnomesException eex2 = new GnomesException(eex,
                            GnomesMessagesConstants.ME01_0001);

                    doCatchException(eex2);

                    try {
                        //メッセージを設定
                        req.setMessageBean(result.getMessageBean());
                    } catch (Exception eex3) {
                        // 処理なし
                    }
                }
                // 呼び出し元のBL用にエラー情報を設定
                setExceptionInfo(ex, result);

                //レスポンス （MES-1 ではシステムエラー画面ではなく、エラー画面を表示する）
                responseContext.setForward(CommonConstants.PATH_ERR_PAGE);
            }
        }

        return result;
    }

    /**
     * SF専用WEBサービスロジック
     *
     * @param input
     * @return
     */
    public GnomesWebServiceResult doLogicForSF(Object input) {

        // アプリケーション Bean、セッションBean のユーザ情報を保持 （コンストラクタではできないため、このタイミングで実施
        //        if (gnomesSessionBean.getUserId() != null) {
        //            req.setSessionInfo();
        //        }

        req.setServiceRequest(input);

        GnomesWebServiceResult result = new GnomesWebServiceResult();
        result.setIsSuccess(false);
        result.setIsSessionError(true);

        try {
            // windowIdの確認
            String windowId = req.getServiceRequestParam("windowId");
            if (windowId != null) {
                boolean isFound = false;
                for (String item : gnomesSessionBean.getWindowIdList()) {
                    if (item.equals(windowId)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    return result;
                }
            }

            result.setIsSessionError(false);

            Object commandResponse = logicFactory.executeServiceLogicForSF();
            // 個別の戻り値を設定
            result.setCommandResponse(commandResponse);

            // 成功設定
            result.setIsSuccess(true);

            // バッチコマンド実行（バッチ処理後に続いて別のバッチ処理を起動する場合）
            demandGnomesServiceRun.execute();

        } catch (GnomesException ex) {
            List<String> messageList = result.getMessageList();
            messageList.add(ex.getMessage());
        } catch (GnomesAppException ex) {
            List<String> messageList = result.getMessageList();
            messageList.add(ex.getMessage());
        } catch (Throwable e) {
            List<String> messageList = result.getMessageList();
            messageList.add(e.getMessage());
        }

        return result;
    }

    /**
     * 外部から起動時の共通情報を設定
     */
    protected void setReqOtherInfo() {

        // エリアID
        req.setAreaId("");
        // エリア名
        req.setAreaName("");
        // コンピュータ名
        req.setComputerName("");
        // IPアドレス
        req.setIpAddress("");
        // 言語
        req.setLanguage(Locale.getDefault().getLanguage());
        // ユーザロケール
        req.setUserLocale(Locale.getDefault());

        // 拠点コード
        req.setSiteCode("");
        // 拠点名
        req.setSiteName("");
        // ユーザID
        req.setUserId("OTHER");
        // ユーザ名
        req.setUserName("OTHER");
        // ユーザKey
        req.setUserKey("");

    }

    private void doCatchGnomesAppException(GnomesAppException ex,
            RestServiceResult result) {
        GnomesException e;

        if (ex.getMessageNo() == null) {
            // 通常は Message No. を設定する。
            e = new GnomesException(ex, GnomesMessagesConstants.ME01_0001);
        } else {
            e = new GnomesException(ex);
        }

        doCatchException(e);

        try {
            req.setMessageBean(result.getMessageBean());
        } catch (Exception eex) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException eex2 = new GnomesException(eex,
                    GnomesMessagesConstants.ME01_0001);

            doCatchException(eex2);

            try {
                //メッセージを設定
                req.setMessageBean(result.getMessageBean());
            } catch (Exception eex3) {
                // 処理なし
            }
        }
        // 呼び出し元のBL用にエラー情報を設定
        setExceptionInfo(ex, result);

    }

    private void doCatchException(GnomesException ex) {

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
            ex.setMessageParams((String[]) params.toArray(new String[0]));

            // メッセージ設定
            MessagesHandler.setMessageNo(req, ex.getMessageNo(), params);

        } else {
            // メッセージ設定
            MessagesHandler.setMessageGnomesException(req, ex);
        }

        //エラーログ
        this.logHelper.severe(this.logger, null, null,
                MessagesHandler.getExceptionMessage(req, ex), ex);

    }

    /**
     * 呼び出し元のBL用にエラー情報を設定.
     * @param e エラー情報
     * @param result サービス実行結果
     */
    private void setExceptionInfo(Exception e, RestServiceResult result) {

        if (e instanceof GnomesAppException) {
            //            result.setGnomesAppException((GnomesAppException) e.getCause());
            result.setGnomesAppException((GnomesAppException) e);
        } else if (e instanceof GnomesException) {

            GnomesException ex = (GnomesException) e;
            // 複数メッセージ対応
            /*
            result.setGnomesAppException(
                    this.exceptionFactory.createGnomesAppException(
                            null, ex.getMessageNo(), ex.getMessageParams()));
            */
            GnomesAppException ex2 = this.exceptionFactory
                    .createGnomesAppException(e.getMessage());
            ex2.setMessageNo(ex.getMessageNo());
            ex2.setMessageParams(ex.getMessageParams());
            ex2.setChildMessageDatas(ex.getChildMessageDatas());
            result.setGnomesAppException(ex2);

        } else {
            result.setGnomesAppException(
                    this.exceptionFactory.createGnomesAppException(null,
                            GnomesMessagesConstants.ME01_0001, e));
        }

    }
}
