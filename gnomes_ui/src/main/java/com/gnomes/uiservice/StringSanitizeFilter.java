package com.gnomes.uiservice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * サニタイズフィルタクラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/02/21 YJP/T.Kamizuru              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class StringSanitizeFilter implements Filter {


    /** エンコード */
    private String encoding = null;

    /**
     * フィルタ処理
     * @param request サニタイズ前のリクエスト
     * @param response ServletResponse
     * @param chain FilterChain
     * @throws IOException, ServletException
     */
    public void doFilter(ServletRequest request,ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {

            //エンコード設定
            request.setCharacterEncoding(encoding);

            ServletRequest sanitizedRequest = request;
            if (request instanceof HttpServletRequest && request.getParameterMap().size() > 0) {

                //サニタイズ処理
                sanitizedRequest = new SanitizeHttpServletRequestWrapper((HttpServletRequest) request);

            }

            //サニタイズされたリクエスト処理を実行する。
            chain.doFilter(sanitizedRequest, response);
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * フィルタ初期設定
     * @param filterConfig web.xmlから取得したfilterのパラメータ
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        //web.xmlで指定されたエンコードを設定する。
        this.encoding = filterConfig.getInitParameter("encoding");
    }

    /**
     * サービス終了時処理
     */
    public void destroy() {
        //処理なし
    }
}
