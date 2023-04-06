package com.gnomes.rest.service;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.picketbox.util.StringUtil;

import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.UserInfo;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesPasswordEncoder;
import com.gnomes.common.util.Holder;
import com.gnomes.system.dao.InfoComputerDao;
import com.gnomes.system.dao.InfoUserDao;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.entity.InfoUser;
import com.gnomes.system.logic.BLSecurity;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.MessageBean;

/**
 * セキュリティサービス リソースクラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/19 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

// セキュリティサービス のリソース名 （クラス単位）
@Path("A01001S001")
@RequestScoped
public class A01001S001 extends BaseService  {

    /**
     * セキュリティ機能
     */
    @Inject
    protected BLSecurity blSecurity;

    /**
     * HTTPServerRequest
     */
    @Inject
    protected HttpServletRequest request;

    /**
     * 通知メッセージビーン
     */
    @Inject
    protected MessageBean messageBean;

    /**
     *  端末情報
     */
    @Inject
    protected InfoComputerDao infoComputerDao;

    /**
     * ロジックファクトリー
     */
    @Inject
    transient LogicFactory logicFactory;

    @Inject
    protected GnomesEntityManager em;

    /** ユーザ情報 Dao */
    @Inject
    protected InfoUserDao infoUserDao;


    @Inject
    protected GnomesExceptionFactory gnomesExceptionFactory;

    @Inject
    ContainerRequest requestContext;

    @Inject
    protected A01001S001Proc a01001S001Proc;

    /**
     * エンティティマネージャーファクトリ（通常領域）
     */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory emf;

    /**
     * エンティティマネージャーファクトリ（保管領域）
     */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE)
    private EntityManagerFactory emfStorage;
    /**
     * デフォルト・コンストラクタ
     */
    public A01001S001() {
    }
    /**
     * ユーザ認証（Salt取得）
     * @param certInfo 認証情報
     * @return 認証情報
     */
    @Path("GetSalt")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo getSalt(CertInfo certInfo) {
        EntityManager em = null;
        String salt = null;
        try {
            if (gnomesSessionBean.getRegionType().equals(RegionType.NORMAL.getValue())) {
                em = this.emf.createEntityManager();
            }
            else {
                em = this.emfStorage.createEntityManager();
            }
            // トランザクション開始
            EntityTransaction tran = em.getTransaction();
            tran.begin();

            try {
                InfoUser dataInfoUser = new InfoUser();
                dataInfoUser = infoUserDao.getInfoUser(certInfo.getUserId(), em);
                salt = GnomesPasswordEncoder.getSalt(dataInfoUser.getPassword());
                //tran.commit();
            }
            catch(Exception ex){
                //tran.rollback();
            }
        }
        finally {
            // クローズ
            if (em != null) {
                em.close();
            }
        }

        certInfo.setPassword(salt);
        certInfo.setIsSuccess(1);
        return certInfo;
    }

    /**
     * ユーザ認証（ログイン時）
     * @param certInfo 認証情報
     * @return 認証情報
     * @throws Exception
     */
    @Path("Certify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public CertInfo certify(CertInfo certInfo) throws Exception {

        // クライアント IP アドレスの取得
        String ipAddress = request.getRemoteAddr();
        String computerName = request.getRemoteHost();

        System.out.println(
                request.getRemoteAddr() + " " + request.getRemoteHost());

        // ユーザ情報
        UserInfo userInfo = new UserInfo();

        userInfo.setIpAddress(ipAddress);
        // ログイン認証か否か
        Boolean isLoginCertify = true;

        // 出力パラメータ 認証成功か否か
        Holder<Integer> isSuccess = new Holder<Integer>(0);

        // 出力パラメータ パスワード変更必要か否か
        Holder<Integer> isChangePassword = new Holder<Integer>(0);

        // 出力パラメータ メッセージ情報
        Holder<String> message = new Holder<String>("");

        certInfo.setIsChangePassword(1);

        label : try {

            // 入力チェック
            // JSONデータ.ユーザID が NULL または空文字の場合、エラーとし、メッセージを設定する。
            if (certInfo.getUserId() == null
                    || certInfo.getUserId().isEmpty()) {
                // ME01.0018:「ユーザーID、パスワードを入力してください。」
                MessagesHandler.setMessageNo(this.req,
                        GnomesMessagesConstants.MG01_0018, "");
                this.logHelper.severe(this.logger, null, null, MessagesHandler
                        .getString(GnomesLogMessageConstants.MG01_0018, ""));
                certInfo.setMessage(MessagesHandler
                        .getString(GnomesLogMessageConstants.ME01_0018));
                certInfo.setIsSuccess(ConverterUtils.boolToInt(false));

                return certInfo;
            }

            // JSONデータ.拠点コード が NULL または空文字の場合、エラーとし、メッセージを設定する。
            else if (certInfo.getSiteCode() == null
                    || certInfo.getSiteCode().isEmpty()) {
                // ME01.0027:「使用する拠点が設定されていません。」
                MessagesHandler.setMessageNo(this.req,
                        GnomesMessagesConstants.ME01_0027);
                certInfo.setMessage(MessagesHandler
                        .getString(GnomesLogMessageConstants.ME01_0027));
                certInfo.setIsSuccess(ConverterUtils.boolToInt(false));

                return certInfo;
            }

            // 認証用データ取得処理
            blSecurity.getCertificate(certInfo, userInfo, isLoginCertify,
                    isSuccess, isChangePassword, message, em.getEntityManager());
            certInfo.setIsSuccess(isSuccess.value);
            certInfo.setIsChangePassword(isChangePassword.value);
            certInfo.setMessage(message.value);
            certInfo.setLinkInfo(this.req.getLinkInfo());

            if (certInfo.getIsSuccess() == 0) {
                this.logHelper.severe(this.logger, null, null,
                        MessagesHandler.getString(
                                GnomesLogMessageConstants.MG01_0018,
                                certInfo.getUserId()));
                break label;
            }

            request.getSession();
            // ログアウトおよび現セッションの変更
            if (StringUtil.isNullOrEmpty(gnomesSessionBean.getUserId())) {
                request.logout();
                request.changeSessionId();
            }
            // 認証
            request.login(certInfo.getUserId(), certInfo.getPassword());

            // ログ出力メッセージ： ログイン認証成功  (ユーザーID： {0})   (MG01.0004）
            this.logHelper.fine(this.logger, null, null,
                    MessagesHandler.getString(
                            GnomesLogMessageConstants.MG01_0004,
                            certInfo.getUserId()));

            certInfo.setIsSuccess(ConverterUtils.boolToInt(true));

            gnomesSessionBean = logicFactory.createSessionBean();

            // ユーザ情報、認証情報設定
            // ユーザKey
            gnomesSessionBean.setUserKey(userInfo.getUserKey());
            // ユーザID
            gnomesSessionBean.setUserId(userInfo.getUserId());
            // ユーザ名
            gnomesSessionBean.setUserName(userInfo.getUserName());
            // パスワード
            gnomesSessionBean.setPassword(userInfo.getPassword());
            // ロケールID
            gnomesSessionBean.setLocaleId(userInfo.getLocaleId());
            // 言語
            gnomesSessionBean.setLanguage(userInfo.getLanguage());
            // IPアドレス
            gnomesSessionBean.setIpAddress(ipAddress);
            // 初期設定時端末名
            gnomesSessionBean.setInitComputerName(computerName);
            // 端末ID
            gnomesSessionBean.setComputerId(userInfo.getComputerId());
            // 端末名
            gnomesSessionBean.setComputerName(userInfo.getComputerName());
            // 拠点コード
            gnomesSessionBean.setSiteCode(userInfo.getSiteCode());
            // 拠点名
            gnomesSessionBean.setSiteName(userInfo.getSiteName());
            // 領域区分
            gnomesSessionBean.setRegionType(certInfo.getRegionType());
            // 指図工程
            gnomesSessionBean.setOrderProcessCode(userInfo.getProcessCode());
            // 作業工程
            gnomesSessionBean.setWorkProcessCode(userInfo.getWorkProcessCode());
            // 作業場所
            gnomesSessionBean.setWorkCellCode(userInfo.getWorkCellCode());
            // スクリーンロック中か否か
            gnomesSessionBean.setIsScreenLocked(userInfo.getIsScreenLocked());
            // スクリーンロック起動時間（分）
            gnomesSessionBean.setScreenLockTimeoutTime(
                    userInfo.getScreenLockTimeoutTime());
            // メッセージ一覧画面最大表示件数
            gnomesSessionBean
                    .setMaxListDisplayCount(userInfo.getMaxListDisplayCount());
            // ポップアップメッセージ表示件数
            gnomesSessionBean
                    .setPopupDisplayCount(userInfo.getPopupDisplayCount());
            // ポップアップメッセージ監視周期
            gnomesSessionBean
                    .setWatchPeriodForPopup(userInfo.getWatchPeriodForPopup());

        } catch (ServletException e) {
            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            // ME01.0018:「ユーザーID、パスワードを入力してください。」
            MessagesHandler.setMessageNo(this.req,
                    GnomesMessagesConstants.MG01_0018, certInfo.getUserId());
            certInfo.setMessage(MessagesHandler
                    .getString(GnomesLogMessageConstants.ME01_0018));
            GnomesException eex2 = exceptionFactory.createGnomesException(e,
                    GnomesMessagesConstants.ME01_0001);
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getString(
                            GnomesLogMessageConstants.MG01_0018,
                            certInfo.getUserId()),
                    eex2);

        } catch (IllegalStateException e) {
            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(e.getMessage());
            throw e;
        } catch (Exception e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = exceptionFactory.createGnomesException(e,
                    GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req,
                    GnomesMessagesConstants.ME01_0001);

            // エラーログ
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, ex), ex);

            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(
                    MessagesHandler.getExceptionMessage(this.req, ex));
            throw e;
        }

        // アカウントロックチェック,認証情報の登録
        try {

            blSecurity.checkLockAccount(certInfo.getIsSuccess(), em.getEntityManager());

            // 認証情報の登録
            //blSecurity.insertHistCertification(certInfo, userInfo, ConverterUtils.IntTobool(certInfo.getIsSuccess()), em);
        } catch (Exception ex) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException eex2 = exceptionFactory.createGnomesException(ex,
                    GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req,
                    GnomesMessagesConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, eex2), eex2);

            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(
                    MessagesHandler.getExceptionMessage(this.req, eex2));
            throw ex;
        }
        return certInfo;
    }

    /**
     * ユーザ認証（画面ロック解除時）
     * @param certInfo 認証情報
     * @return 認証情報
     * @throws Exception
     */
    @Path("CertifyForResume")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo certifyForResume(CertInfo certInfo) throws Exception {

        //セッション切れの後にロック画面からユーザ認証をする場合があるので、エラーを返す
        if (CheckSessionArrveOrDeadErrorSet(certInfo) == false){
            return certInfo;
        }

        req.setSessionInfo();

        // トランザクション開始
        a01001S001Proc.certifyForResumeProc(certInfo, em.getEntityManager());

        return certInfo;

    }

    /**
     * パスワード変更
     * @param certInfo 認証情報
     * @return 認証情報
     * @throws Exception
     */
    @Path("ChangePassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo changePassword(CertInfo certInfo) throws Exception {

        //セッション切れの後にパスワード変更をする場合があるので、エラーを返す
        if (CheckSessionArrveOrDeadErrorSet(certInfo) == false){
            return certInfo;
        }

        req.setSessionInfo();

        this.a01001S001Proc.changePasswordProc(certInfo, em.getEntityManager());

        return certInfo;
    }

    /**
     * ログアウト
     * @param certInfo 認証情報
     * @return 認証情報
     * @throws Exception
     */
    @Path("Logout")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo logout(CertInfo certInfo) throws Exception {

        //セッション切れの後にロック画面からログアウトをする場合があるので何もせず返す
        if (gnomesSessionBean == null
                || gnomesSessionBean.getIpAddress() == null) {

            certInfo.setIsSuccess(ConverterUtils.boolToInt(true));
            return certInfo;
        }

        this.a01001S001Proc.logoutProc(certInfo, em.getEntityManager());

        return certInfo;
    }



    /**
     * セッションロック設定
     * @param certInfo 認証情報
     * @return 認証情報
     */
    @Path("LockSession")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo lockSession(CertInfo certInfo) {

        try {
            GnomesSessionBean gnomesSessionBean = logicFactory.getSessionBean();
            gnomesSessionBean.setIsScreenLocked(true);
            certInfo.setIsSuccess(ConverterUtils.boolToInt(true));
        } catch (Exception ex) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException eex2 = exceptionFactory.createGnomesException(ex,
                    GnomesMessagesConstants.ME01_0001);

            // メッセージ設定
            MessagesHandler.setMessageNo(this.req,
                    GnomesMessagesConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, eex2), eex2);

            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(
                    MessagesHandler.getExceptionMessage(this.req, eex2));
        }

        return certInfo;
    }
    /**
     * セッションロック設定
     * @param certInfo 認証情報
     * @return 認証情報
     */
    @Path("WatchDogSession")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo watchDogSession(CertInfo certInfo) {

        try {
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

        return certInfo;
    }


    /**
     * セッションが生きているかどうか確認して、死んでいるならエラーメッセージを設定する
     * @return false:死んでいる true:生きている
     */
    private boolean CheckSessionArrveOrDeadErrorSet(CertInfo certInfo){

        //セッション切れの後にロック画面からユーザ認証をする場合があるので、エラーを返す
        if (gnomesSessionBean == null
                || gnomesSessionBean.getIpAddress() == null) {
            MessagesHandler.setMessageNo(this.req,
                    GnomesMessagesConstants.ME01_0040);
            this.logHelper.severe(this.logger, null, null, MessagesHandler
                    .getString(GnomesLogMessageConstants.ME01_0040));
            certInfo.setIsSuccess(ConverterUtils.boolToInt(false));
            certInfo.setMessage(MessagesHandler.getString(
                    GnomesLogMessageConstants.ME01_0040, Locale.getDefault()));
            return false;
        }
        return true;
    }


}
