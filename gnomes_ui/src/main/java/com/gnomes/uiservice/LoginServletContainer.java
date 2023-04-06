package com.gnomes.uiservice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;

/**
 * ログインサーブレットコンテナ
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
@WebServlet(name = "LoginServletContainer", urlPatterns = { "/gnomes-login" })
public class LoginServletContainer extends ServletContainer implements Filter {

    /* (非 Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        //ログイン画面表示用コマンドか否かをチェック
        if (!logicFactory.isLoginCommand()) {
            // 不正なアクセスです。ログイン画面からログインしてください。
            throw new ServletException(MessagesHandler.getString(GnomesMessagesConstants.ME01_0030));
        }
        super.service(request,  response);

    }

}
