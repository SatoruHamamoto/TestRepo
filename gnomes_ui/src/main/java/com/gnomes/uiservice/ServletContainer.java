package com.gnomes.uiservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.util.StringUtil;

import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.ResourcePathConstants;
import com.gnomes.rest.client.DemandGnomesServiceRun;

/**
 * サーブレットコンテナ
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
@WebServlet(name = "servletContainer", urlPatterns = { "/gnomes" })
public class ServletContainer extends HttpServlet implements Filter {

    public static final boolean USE_ATTRIBUTE = true;

    //ロガー
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    // ロジックファクトリー
    @Inject
    transient LogicFactory logicFactory;

    // セッション Baen
    @Inject
    GnomesSessionBean gnomesSessionBean;

    //リクエストコンテキスト
    @Inject
    ContainerRequest requestContext;

    @Inject
    ContainerResponse responseContext;

    //通知メッセージビーン
    @Inject
    MessageBean messageBean;

    @Inject
    ContainerSession sessionContext;

    @Inject
    DocumentFileDownloadAndDisplay documentFileDownloadAndDisplay;

    @Inject
    DemandGnomesServiceRun demandGnomesServiceRun;

    /* (非 Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) {
        try{
            chain.doFilter(request, response);
        }catch(Exception e){
            this.logHelper.severe(this.logger, null, null, "An unexpected error occurred : "+ e.getMessage(),e);
        }
    }

    /* (非 Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    /* (非 Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        boolean isTrendErr = false;

        // 開始ログ
        this.logHelper.fine(this.logger, null, null, "com.gnomes.uiservice.ServletContainer#service [start]");

        // 文字コード設定
        response.setContentType("text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try
        {
            String command = request.getParameter("command");
            //ＵＲＬを直接指定したり、F5を押すことでエラーに飛ばさなようにステータスを
            // SC_NO_CONTENT にして戻る
            if (request.getMethod().equals("GET") &&
                    request.getRequestURI().equals("/UI/gnomes") &&
                    StringUtil.isNullOrEmpty(command)) {

                responseContext.setHttpStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            responseContext.setLogicError(true);

            //リダイレクト情報引き継ぎ
            if (sessionContext.getRequest() != null && sessionContext.getRequest().isRedirect()) {
                requestContext.setRequest(sessionContext.getRequest());
                sessionContext.clear();
            }
            else
            if (request.getAttribute(ContainerResponse.ATTR_FORWARD_CMD_NAME) != null) {
                // 情報引継
                requestContext.setAttributeCommand(
                        (String)request.getAttribute(ContainerResponse.ATTR_FORWARD_CMD_NAME),
                        (Map<String, Object>)request.getAttribute(ContainerResponse.ATTR_FORWARD_PARAMS_NAME) );
            }

            // アプリケーション Bean、セッションBean のユーザ情報を保持 （コンストラクタではできないため、このタイミングで実施
            if (gnomesSessionBean.getUserId() != null) {
                requestContext.setSessionInfo();
            }

            // ラッパー設定
            requestContext.setWrapperRequest(request);

            //windowIdの取得
            String windowId = null;
            if (requestContext.isForwardAttribute()) {
                Object obj = requestContext.getAttributeParameter(ContainerRequest.REQUEST_NAME_WINDOW_ID);
                if (obj != null) {
                    windowId = (String) obj;
                }
            }
            else if (requestContext.isRedirect()) {
                Object obj = requestContext.getRedirectParameter(ContainerRequest.REQUEST_NAME_WINDOW_ID);
                if (obj != null) {
                    windowId = (String) obj;
                }
            }

            if (windowId == null) {
                windowId = requestContext.getWrapperRequest().getParameter(ContainerRequest.REQUEST_NAME_WINDOW_ID);
            }

            // 別windowで開くの場合はwindowIdはNULL
            if (!logicFactory.isLoginCommand() && windowId != null) {

                boolean isFound = false;
                for (String item : gnomesSessionBean.getWindowIdList()) {
                    if (item.equals(windowId)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    //----------------------------------------------------------------------------------
                    // レアケース：セッションが切れても延命コマンドがくる場合がある。
                    // WATCHDOG_CONVERSIONコマンドで来た場合はエラーにせず状態維持する
                    //----------------------------------------------------------------------------------
                    if (StringUtil.isNotNull(command) && command.equals(CommandIdConstants.COMMAND_ID_WATCHDOG_CONVERSION)){
                        responseContext.setHttpStatus(HttpServletResponse.SC_NO_CONTENT);
                        return;
                    }
                    //レスポンス （MES-1 ではシステムエラー画面ではなく、複数ウィンドウエラー画面を表示する）
                    responseContext.setForward("/jsp/gnomes_err_multi_window.jsp");
                    return;
                }
            }

            //コマンド実行
            logicFactory.executeLogic();

            if(! StringUtil.isNullOrEmpty(requestContext.getCommandValidErrorInfo())){
                //処理中エラーをクライアントで検知するためクッキーを設定する
                Cookie cookie = new Cookie("response",URLEncoder.encode(requestContext.getCommandValidErrorInfo(), "UTF-8"));
                cookie.setPath("/");
                response.addCookie(cookie);

                if(windowId != null && !windowId.isEmpty()){
                	Cookie cookieWindowId = new Cookie("windowId", URLEncoder.encode(windowId,"UTF-8"));
                	cookieWindowId.setPath("/");
                	response.addCookie(cookieWindowId);
                }
            }

            //メッセージを設定
            try {
            	requestContext.setMessageBean(messageBean, true);
            } catch(GnomesAppException e) {
                isTrendErr = true;
            	throw e;
            }

            // 下記処理は業務側のメッセージ対応時に削除
            if (messageBean.getMessage() == null || messageBean.getMessage().length() == 0) {
                // 下記処理は業務側
                // messageBean.setTitle(responseContext.getMessageTitle());
                messageBean.setMessage(responseContext.getMessage());
                messageBean.setCommand(responseContext.getMessageCommand());
            }
/* メッセージマスタより出力有無を処理するので、下記処理は削除

            // バリデーションエラーメッセージをログ出力
            if (messageBean.getMessage() != null && (!messageBean.getMessage().isEmpty())) {
                StringBuilder message = new StringBuilder();
                message.append(messageBean.getMessage());
                message.append(System.lineSeparator());
                message.append(messageBean.getMessageDetail());
                this.logHelper.severe(this.logger, null, null, message.toString());
            }
*/
            //バッチコマンド実行
            demandGnomesServiceRun.execute();
        }
        catch (GnomesException ex)
        {
            // コマンド、ビジネスロジックでGnomesExceptionをスローした場合
            if (ex.getMessageNo() == null) {

                // メッセージ設定
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
//                MessagesHandler.setMessageNo(this.requestContext , GnomesMessagesConstants.ME01_0001);
                // エラーログ
//                this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this.requestContext, ex));
//                this.logHelper.severe(this.logger, null, null, ex.getMessage(), ex);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0001);
                doCatchException(ex);

            // コマンド、ビジネスロジックでGnomesAppException発生しGnomesExceptionに変えた場合
            } else {
                // 処理なし
            }

            try {
                //メッセージを設定
                requestContext.setMessageBean(messageBean, isTrendErr);

                // デフォルトフォワード設定前に例外発生の場合
                if (responseContext.getUri() == null) {
                    //レスポンス （MES-1 ではシステムエラー画面ではなく、エラー画面を表示する）
                    responseContext.setForward(CommonConstants.PATH_ERR_PAGE);
                }

            }
            catch (Exception eex) {
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException eex2 = new GnomesException(eex, GnomesMessagesConstants.ME01_0001);

                doCatchException(eex2);

                try {
                    //メッセージを設定
                    requestContext.setMessageBean(messageBean);
                }
                catch (Exception eex3) {
                    // 処理なし
                }

                //レスポンス （MES-1 ではシステムエラー画面ではなく、エラー画面を表示する）
//                responseContext.setForward(CommonConstants.PATH_ERR_PAGE);
                setForwardErrPage(request);
            }

        }

        catch (GnomesAppException ex)
        {
            GnomesException e;

            if (ex.getMessageNo() == null) {
                // 通常は Message No. を設定する。
//                e = new GnomesException(ex.getMessage(), ex);
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                e = new GnomesException(ex, GnomesMessagesConstants.ME01_0001);
            } else {
                e = new GnomesException(ex);
            }

            doCatchException(e);

            try {
                requestContext.setMessageBean(messageBean, isTrendErr);
            }
            catch (Exception eex) {
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException eex2 = new GnomesException(eex, GnomesMessagesConstants.ME01_0001);

                doCatchException(eex2);

                try {
                    //メッセージを設定
                    requestContext.setMessageBean(messageBean);
                }
                catch (Exception eex3) {
                    // 処理なし
                }
            }

            //レスポンス （MES-1 ではシステムエラー画面ではなく、エラー画面を表示する）
//            responseContext.setForward(CommonConstants.PATH_ERR_PAGE);
            setForwardErrPage(request);
        }
        catch (Throwable e)
        {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = new GnomesException(e, GnomesMessagesConstants.ME01_0001);

            doCatchException(ex);

            //メッセージを設定
            try {
                requestContext.setMessageBean(messageBean);
                // (ME01.0231) 最後に操作が行われてから一定時間が経過しました。ログインから操作を行ってください。
                messageBean.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0231));
            }
            catch (Exception eex) {
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException eex2 = new GnomesException(eex, GnomesMessagesConstants.ME01_0001);

                doCatchException(eex2);

                try {
                    //メッセージを設定
                    requestContext.setMessageBean(messageBean);
                }
                catch (Exception eex3) {
                    // 処理なし
                }
            }

            //レスポンス （MES-1 ではシステムエラー画面ではなく、エラー画面を表示する）
//            responseContext.setForward(CommonConstants.PATH_ERR_PAGE);
            setForwardErrPage(request);
        }
        finally {

            //HTTPステータスを設定する S_OK以外にSC_NO_CONTENTもありうる
            response.setStatus(responseContext.getHttpStatus());

            try {
            	// レスポンスにダウンロードファイルが設定されている場合
                if (responseContext.getFileDownLoadDatas() != null) {
                    //テキストなど画面表示したいものはPDFと同じように画面表示する
                    //それ以外はダウンロードされる（XLSなど）
                    if(responseContext.getIsDisplayFile()){
                        documentFileDownloadAndDisplay.dispFile(response);
                    }
                    else {
                        // ダウンロード
                        try {
                            documentFileDownloadAndDisplay.download(response);
                        }
                        catch (Exception eex) {
                            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                            GnomesException eex2 = new GnomesException(eex, GnomesMessagesConstants.ME01_0001);

                            doCatchException(eex2);

                            try {
                                //メッセージを設定
                                requestContext.setMessageBean(messageBean);
                            }
                            catch (Exception eex3) {
                                // 処理なし
                            }

                            // 遷移先画面で表示
                            response.reset();
                            response.setContentType("text/html; charset=UTF-8");

                            responseContext.forward(request, response);
                        }
                    }
                }
                // レスポンスにPDFファイルが設定されている場合
                else if (responseContext.getFilePDFData() != null) {
                    // PDF出力
                    try {
                        documentFileDownloadAndDisplay.dispPDF(response);
                    }
                    catch (Exception eex) {
                        // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                        GnomesException eex2 = new GnomesException(eex, GnomesMessagesConstants.ME01_0001);

                        doCatchException(eex2);

                        try {
                            //メッセージを設定
                            requestContext.setMessageBean(messageBean);
                        }
                        catch (Exception eex3) {
                            // 処理なし
                        }

                        // 遷移先画面で表示
                        response.reset();
                        response.setContentType("text/html; charset=UTF-8");

                        responseContext.forward(request, response);
                    }
                } else {
                    responseContext.forward(request, response);
                }
            }
            catch (Exception e) {
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException ex = new GnomesException(e, GnomesMessagesConstants.ME01_0001);

                doCatchException(ex);
                e.printStackTrace();            }
        }

        // 終了ログ
        this.logHelper.fine(this.logger, null, null, "com.gnomes.uiservice.ServletContainer#service [end]");
    }


    /**
     * エラーページ遷移設定
     * @param request リクエスト
     */
    private void setForwardErrPage(HttpServletRequest request) {

    	// ログイン画面ボタンの為、コマンド ID から最初の文字二つを取得
    	if (requestContext.getCommandId() != null) {
			String commandId = requestContext.getCommandId();
	        String twoFirstCommandId = commandId.substring(0, 2); // MG or OP
	        messageBean.setCommand(twoFirstCommandId);
		}
        // 遷移先がダウンロードパスでない場合（別Windowで開いていない場合
        if (responseContext.getUri() == null || !responseContext.getUri().equals(ResourcePathConstants.RESOURCE_PATH_DOWNLOAD)) {
            //レスポンス （MES-1 ではシステムエラー画面ではなく、エラー画面を表示する）
            responseContext.setForward(CommonConstants.PATH_ERR_PAGE);
        }
    }


    private void doCatchException(GnomesException ex) {

        // (ME01_0001)アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
        // の場合
        if (ex.getMessageNo() != null && GnomesMessagesConstants.ME01_0001.equals(ex.getMessageNo())) {

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

            for (int i=params.size() ; i<10 ; i++) {
                params.add("");
            }
            // メソッド最後のログ出力に備える
            ex.setMessageParams((String[])params.toArray(new String[0]));

            // メッセージ設定
            MessagesHandler.setMessageNo(this.requestContext , ex.getMessageNo(), params);

        } else {
            // メッセージ設定
            MessagesHandler.setMessageGnomesException(this.requestContext , ex);
        }

        //エラーログ
        this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this.requestContext, ex), ex);

    }


    /* (非 Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest
                && res instanceof HttpServletResponse)) {
            //error
            throw new ServletException("non-HTTP request or response");
        }
        service((HttpServletRequest) req, (HttpServletResponse) res);
    }

    /* (非 Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        // 開始ログ
        this.logHelper.fine(this.logger, null, null, "com.gnomes.uiservice.ServletContainer#init [start]");

        super.init();

        // 終了ログ
        this.logHelper.fine(this.logger, null, null, "com.gnomes.uiservice.ServletContainer#init [end]");
    }

    /*
     * Servlet初期化パラメーター。
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
    }
}
