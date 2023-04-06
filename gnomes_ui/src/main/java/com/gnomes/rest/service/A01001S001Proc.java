package com.gnomes.rest.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.picketbox.util.StringUtil;

import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.CertificateType;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.UserInfo;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.Holder;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.logic.BLSecurity;
import com.gnomes.system.logic.GnomesLdapLoginModule;
import com.gnomes.uiservice.ContainerRequest;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/03/24 14:25 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * セキュリティサービス ロジッククラス
 * @author 03501213
 *
 */
@Dependent
public class A01001S001Proc extends BaseService {
    /**
     * セキュリティ機能
     */
    @Inject
    protected BLSecurity blSecurity;

    /**
     * ロジックファクトリー
     */
    @Inject
    transient LogicFactory logicFactory;

    /**
     * リクエストコンテクスト
     */
    @Inject
    ContainerRequest requestContext;

    /**
     * HTTPServerRequest
     */
    @Inject
    protected HttpServletRequest request;

    /**
     * ユーザ認証（画面ロック解除時）メイン処理
     *
     * @param certInfo
     * @param em
     * @throws Exception
     */
    @GnomesTransactional
    public void certifyForResumeProc(CertInfo certInfo, EntityManager em) throws Exception {

        //セッション切れの後にロック画面からユーザ認証をする場合があるので、エラーを返す
        if (gnomesSessionBean == null || gnomesSessionBean.getIpAddress() == null) {
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0040);
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getString(GnomesLogMessageConstants.ME01_0040));
            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0040, Locale.getDefault()));
            return;
        }

        // ユーザ情報
        UserInfo userInfo = new UserInfo();

        userInfo.setIpAddress(gnomesSessionBean.getIpAddress());
        Locale localeId = gnomesSessionBean.getUserLocale();

        // ログイン認証か否か
        Boolean isLoginCertify = false;

        // 出力パラメータ 認証成功か否か
        Holder<Integer> isSuccess = new Holder<Integer>(0);

        // 出力パラメータ パスワード変更必要か否か
        Holder<Integer> isChangePassword = new Holder<Integer>(0);

        // 出力パラメータ メッセージ情報
        Holder<String> message = new Holder<String>("");

        certInfo.setIsChangePassword(1);

        certInfo.setComputerId(gnomesSessionBean.getComputerId());

        try {

            // 入力チェック
            // JSONデータ.ユーザID が NULL または空文字の場合、エラーとし、メッセージを設定する。
            if (certInfo.getUserId() == null || certInfo.getUserId().isEmpty()) {
                // ME01.0018:「ユーザーID、パスワードを入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.MG01_0018, certInfo.getUserId());
                this.logHelper.severe(this.logger, null, null,
                        MessagesHandler.getString(GnomesLogMessageConstants.MG01_0018, ""));
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0018, localeId));
                certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
                return;
            }

            // 取得した GnomesSessionBean のユーザIDがnullの場合、リロード
            if (gnomesSessionBean.getUserId() == null || gnomesSessionBean.getUserId().equals("")) {
                certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0040, localeId));
                return;
            }

            // 取得した GnomesSessionBean のユーザID と入力パラメータのユーザID が一致しない場合、エラーメッセージ
            if (!gnomesSessionBean.getUserId().equals(certInfo.getUserId())) {
                // ME01.0014:「ログインユーザのみロック解除可能です。 」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0014);
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0014, localeId));
                certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
                return;
            }

            // 認証用データ取得処理
            blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
            certInfo.setIsSuccess(isSuccess.value);
            certInfo.setIsChangePassword(isChangePassword.value);
            certInfo.setMessage(message.value);
            certInfo.setLinkInfo(this.req.getLinkInfo());

            // LDAP認証時
            if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.LDAP)) {
                GnomesLdapLoginModule ldapLoginModule = new GnomesLdapLoginModule();
                Hashtable<String, String> moduleOption = gnomesSystemBean.getModuleOptionInfo();
                boolean isLdapLoginSuccess = ldapLoginModule.validateUser(certInfo.getUserId(), certInfo.getPassword(),
                        moduleOption);

                if (!isLdapLoginSuccess) {
                    System.out.println("Search validateUser failed");
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.MG01_0018, certInfo.getUserId());
                    this.logHelper.severe(this.logger, null, null,
                            MessagesHandler.getString(GnomesLogMessageConstants.MG01_0018, certInfo.getUserId()));
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0018, localeId));
                    certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
                }

            }

            // アカウントロックチェック
            blSecurity.checkLockAccount(certInfo.getIsSuccess(), em);

            // 認証失敗の場合
            if (certInfo.getIsSuccess() == 0) {

                // 認証情報の登録
                blSecurity.insertHistCertification(certInfo, userInfo, false, CertificateType.LOCK, null, em);
                this.logHelper.severe(this.logger, null, null,
                        MessagesHandler.getString(GnomesLogMessageConstants.MG01_0018, certInfo.getUserId()));
                return;
            }

            // 認証情報の登録
            blSecurity.insertHistCertification(certInfo, userInfo, true, CertificateType.LOCK, null, em);
            // ログ出力メッセージ： ログイン認証成功  (ユーザーID： {0})   (MG01.0004）
            this.logHelper.fine(this.logger, null, null,
                    MessagesHandler.getString(GnomesLogMessageConstants.MG01_0004, certInfo.getUserId()));

            unlockSession(certInfo);
            certInfo.setIsSuccess(ConverterUtils.boolToInt(true));
        } catch (Exception ex) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException eex2 = exceptionFactory.createGnomesException(ex, GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this.req, eex2), eex2);

            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(MessagesHandler.getExceptionMessage(this.req, eex2));

            throw ex;
        }
        return;
    }

    /**
     *  パスワード変更メイン処理
     *
     * @param certInfo  認証情報
     * @param em    エンティティマネージャー
     * @throws Exception
     */
    @GnomesTransactional
    public void changePasswordProc(CertInfo certInfo, EntityManager em) throws Exception {
        try {
            // 入力チェック
            // JSONデータ.ユーザID が NULL または空文字の場合、エラーとし、メッセージを設定する。
            if (certInfo.getUserId() == null || certInfo.getUserId().isEmpty()) {
                // ME01.0018:「ユーザーID、パスワードを入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0018);
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0018,
                        gnomesSessionBean.getUserLocale()));
                certInfo.setIsSuccessChange(false);
                return;
            }

            // パスワード変更処理
            blSecurity.changePassword(certInfo, em);

        } catch (Exception ex) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException eex2 = exceptionFactory.createGnomesException(ex, GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this.req, eex2), eex2);

            certInfo.setMessage(MessagesHandler.getExceptionMessage(this.req, eex2));
            certInfo.setIsSuccessChange(false);
            throw ex;

        }
        return;
    }
    /**
     * ログアウトメイン処理
     * @param certInfo  認証情報
     * @param em        エンティティマネージャー
     * @return 認証情報
     * @throws Exception
     */
    @GnomesTransactional
    public void logoutProc(CertInfo certInfo, EntityManager em) throws Exception {

        // ログアウトを行う。
        try {
            //既にユーザIDがNULLならば何もしない
            if (!StringUtil.isNullOrEmpty(gnomesSessionBean.getUserId())) {

                requestContext.setSessionInfo();
                //メッセージ情報追加
                // パラメータ情報（リスト）の作成
                List<String> params = new ArrayList<String>();
                params.add(gnomesSessionBean.getUserName()); //ユーザ名
                params.add(certInfo.getScreenName()); //画面名
                params.add(certInfo.getScreenId()); //画面ID
                params.add(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0058)); //操作内容
                params.add(this.getClass().getSimpleName()); //コマンドID

                requestContext.addMessageInfoForStartLogging(gnomesSessionBean.getUserName(), params, true);
                // 画面に出力しないメッセージを設定
                requestContext.addMessageInfoForLogging(GnomesMessagesConstants.MV01_0029, params, true);

                request.logout();
                HttpSession session = request.getSession(false);
                session.invalidate();
                gnomesSessionBean.clear();
                return;
            }

        } catch (ServletException e) {
            this.logHelper.severe(this.logger, null, null, "Authentication Error:" + e.getMessage());
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = exceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0001);

            // エラーログ
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this.req, ex), ex);
            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            // ME01.0018:「ユーザーID、パスワードを入力してください。」
            certInfo.setMessage(MessagesHandler.getExceptionMessage(this.req, ex));

            throw e;
        }
        return;
    }
    /**
     * セッションロック解除
     * @param certInfo 認証情報
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    private void unlockSession(CertInfo certInfo) {

        try {
            GnomesSessionBean gnomesSessionBean = logicFactory.getSessionBean();
            gnomesSessionBean.setIsScreenLocked(false);
            certInfo.setIsSuccess(ConverterUtils.boolToInt(true));
        } catch (Exception ex) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException eex2 = exceptionFactory.createGnomesException(ex, GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this.req, eex2), eex2);

            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(MessagesHandler.getExceptionMessage(this.req, eex2));
        }
    }

}
