package com.gnomes.system.logic;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.internet.MimeUtility;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.CertificateType;
import com.gnomes.common.constants.CommonEnums.ChangePasswordMailNoticeFlag;
import com.gnomes.common.constants.CommonEnums.ChangePasswordType;
import com.gnomes.common.constants.CommonEnums.HistNgFlag;
import com.gnomes.common.constants.CommonEnums.MailNoticeStatus;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayConfirmFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayDiscardChangeFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayFinishFlag;
import com.gnomes.common.constants.CommonEnums.PrivilegeIsCheckDoubleSubmit;
import com.gnomes.common.constants.CommonEnums.PrivilegeIsnecessaryPassword;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.UserInfo;
import com.gnomes.common.dto.MstrScreenButtonDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.common.util.GnomesPasswordEncoder;
import com.gnomes.common.util.Holder;
import com.gnomes.common.util.KeyStoreUtilities;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.system.dao.HistCertificationWriteDao;
import com.gnomes.system.dao.HistChangePasswordDao;
import com.gnomes.system.dao.HistChangePasswordDetailWriteDao;
import com.gnomes.system.dao.HistChangePasswordWriteDao;
import com.gnomes.system.dao.InfoComputerProcWorkcellDao;
import com.gnomes.system.dao.InfoUserDao;
import com.gnomes.system.dao.JudgePersonLicenseDao;
import com.gnomes.system.dao.MstrComputerDao;
import com.gnomes.system.dao.MstrInvalidPasswdDao;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.dao.MstrMessageGroupDao;
import com.gnomes.system.dao.MstrPersonDao;
import com.gnomes.system.dao.MstrPersonSecPolicyDao;
import com.gnomes.system.dao.MstrScreenButtonDao;
import com.gnomes.system.dao.MstrSiteDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.data.CheckPasswordInfo;
import com.gnomes.system.data.IScreenPrivilegeBean;
import com.gnomes.system.data.MailInfoBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.data.SystemFunctionBean;
import com.gnomes.system.entity.HistCertificationWrite;
import com.gnomes.system.entity.HistChangePassword;
import com.gnomes.system.entity.HistChangePasswordDetail;
import com.gnomes.system.entity.HistChangePasswordDetailWrite;
import com.gnomes.system.entity.HistChangePasswordWrite;
import com.gnomes.system.entity.InfoComputerProcWorkcell;
import com.gnomes.system.entity.InfoUser;
import com.gnomes.system.entity.MstrComputer;
import com.gnomes.system.entity.MstrInvalidPasswd;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrPersonSecPolicy;
import com.gnomes.system.entity.MstrScreenButton;
import com.gnomes.system.entity.MstrSite;
import com.gnomes.system.entity.MstrSystemDefine;

import gnomes_project.sendmail_0_1.SendMail;

/**
 * セキュリティ機能
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/19 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BLSecurity extends BaseLogic
{

    /** システム共通処理用ファンクションビーン */
    @Inject
    protected SystemFunctionBean               systemFunctionBean;

    /** 画面共通項目用 フォームビーン */
    @Inject
    protected SystemFormBean                   systemFormBean;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao              mstrSystemDefineDao;

    /** ユーザアカウントセキュリティポリシー Dao */
    @Inject
    protected MstrPersonSecPolicyDao           mstrPersonSecPolicyDao;

    /** ユーザマスタ Dao */
    @Inject
    protected MstrPersonDao                    mstrPersonDao;

    /** 拠点マスタ Dao */
    @Inject
    protected MstrSiteDao                      mstrSiteDao;

    /** パスワード禁止文字 Dao */
    @Inject
    protected MstrInvalidPasswdDao             mstrInvalidPasswdDao;

    /** 端末定義 Dao */
    @Inject
    protected MstrComputerDao                  mstrComputerDao;

    /** ユーザ情報 Dao */
    @Inject
    protected InfoUserDao                      infoUserDao;

    /** 端末工程作業場所選択情報 Dao */
    @Inject
    protected InfoComputerProcWorkcellDao      infoComputerProcWorkcellDao;

    /** 認証履歴 （シノニム） Dao */
    @Inject
    protected HistCertificationWriteDao        histCertificationWriteDao;

    /** パスワード変更履歴 Dao */
    @Inject
    protected HistChangePasswordDao            histChangePasswordDao;

    /** パスワード変更履歴 （シノニム）Dao */
    @Inject
    protected HistChangePasswordWriteDao       histChangePasswordWriteDao;

    /** パスワード変更履歴詳細 （シノニム） Dao */
    @Inject
    protected HistChangePasswordDetailWriteDao histChangePasswordDetailWriteDao;

    /** 画面ボタンマスタ Dao */
    @Inject
    protected MstrScreenButtonDao              mstrScreenButtonDao;

    /** メッセージ定義 Dao */
    @Inject
    protected MstrMessageDefineDao             mstrMessageDefineDao;

    /** メッセージグループ Dao */
    @Inject
    protected MstrMessageGroupDao              mstrMessageGroupDao;

    /** ユーザ作業権限判定 Dao */
    @Inject
    protected JudgePersonLicenseDao            judgePersonLicenseDao;

    @Inject
    GnomesExceptionFactory                     gnomesExceptionFactory;

    /** キーストアユーティリティ */
    @Inject
    protected KeyStoreUtilities                keyStoreUtilities;

    /** 正規表現：数値 */
    private static final String                NUMBER   = "^[0-9]+$";

    /** 正規表現：アルファベット */
    private static final String                ALPHABET = "^[a-zA-Z]+$";

    /** メール情報 */
    @Inject
    protected MailInfoBean                     mailInfoBean;

    /** メール通知状況. */
    private MailNoticeStatus                   mailNoticeStatus;

    /** 原因 */
    private String                             cause;

    /** 独自管理トランザクション */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    protected transient EntityManagerFactory   emf;

    /** エンティティマネージャーファクトリ（保管領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE)
    private EntityManagerFactory               emfStorage;

    /** セキュアな乱数 */
    private Random rand = new SecureRandom();

    /**
     * デフォルト・コンストラクタ
     */
    public BLSecurity()
    {
    }

    /**
     * ユーザマスタの取得処理
     * @param datasPerson ユーザ情報
     * @param userid ユーザid
     * @param isLoginCertify ログイン認証か否か
     * @param outputIsSuccess 認証成功か否か
     * @param outputMessage メッセージ情報
     * @param em エンティティマネージャ
     * @return
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public boolean checkMstrPersonExitOrNot(List<MstrPerson> datasPerson, String userid, Boolean isLoginCertify,
            Holder<Integer> outputIsSuccess, Holder<String> outputMessage, EntityManager em) throws Exception
    {

        Locale localeId;
        if (!isLoginCertify) {
            localeId = gnomesSessionBean.getUserLocale();
        }
        else {
            localeId = Locale.getDefault();
        }
        // ユーザマスタの取得
        datasPerson = mstrPersonDao.getMstrPersonQuery(userid, em);

        // 存在チェック
        // ユーザが取得できない場合、エラーを設定し終了する。
        if (datasPerson == null || datasPerson.size() == 0) {
            // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198, userid);
            outputMessage.value = MessagesHandler.getString(GnomesMessagesConstants.ME01_0198, localeId);
            outputIsSuccess.value = 0;
            return false;
        }
        return true;
    }

    /**
     * 認証用データ取得処理
     * @param certInfo 認証情報
     * @param userInfo ユーザ情報
     * @param isLoginCertify ログイン認証か否か
     * @param outputIsSuccess 認証成功か否か
     * @param outputIsChangePassword パスワード変更必要か否か
     * @param outputMessage メッセージ情報
     * @param em エンティティマネージャ
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void getCertificate(CertInfo certInfo, UserInfo userInfo, Boolean isLoginCertify,
            Holder<Integer> outputIsSuccess, Holder<Integer> outputIsChangePassword, Holder<String> outputMessage,
            EntityManager em) throws Exception
    {

        Locale localeId;
        if (!isLoginCertify) {
            localeId = gnomesSessionBean.getUserLocale();
        }
        else {
            String selectLocale[] = certInfo.getLocaleId().split(CommonConstants.SPLIT_CHAR);
            if (selectLocale.length >= 2) {
                localeId = new Locale(selectLocale[0], selectLocale[1]);
            } else {
                localeId = Locale.getDefault();
            }
        }

        // ２．ユーザアカウントセキュリテイポリシーの取得
        List<MstrPersonSecPolicy> datasUsrsecPolicy = mstrPersonSecPolicyDao.getMstrPersonSecPolicy();

        InfoUser dataInfoUser;
        // ６．ユーザ情報の取得
        dataInfoUser = infoUserDao.getInfoUser(certInfo.getUserId(), em);

        // 存在チェック
        // ユーザ情報が取得できない場合、新しくユーザ情報を登録する。
        if (dataInfoUser == null) {
            dataInfoUser = new InfoUser();
            dataInfoUser.setInfo_user_key(UUID.randomUUID().toString());
            dataInfoUser.setUser_id(certInfo.getUserId());
            dataInfoUser.setIs_lock_out(0);

            infoUserDao.insert(dataInfoUser, em);
        }
        // 選択したロケールを設定
        dataInfoUser.setSelect_locale_id(localeId.toString());

        systemFunctionBean.setInfoUser(dataInfoUser);

        // 存在チェック
        // ユーザアカウントセキュリテイポリシーが取得できない場合、エラーを設定し終了する。
        if (datasUsrsecPolicy == null || datasUsrsecPolicy.size() == 0) {
            // ME01.0026:「データがみつかりません。（{0}）」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0026, MstrPersonSecPolicy.TABLE_NAME);
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0026, localeId,
                    MstrPersonSecPolicy.TABLE_NAME);
            outputIsSuccess.value = 0;
            return;
        }
        MstrPersonSecPolicy dataUsrsecPolicy = datasUsrsecPolicy.get(0);
        systemFunctionBean.setMstrPersonSecPolicyList(dataUsrsecPolicy);

        String computerName = "";
        // ３．端末定義マスタの取得
        if (certInfo.getComputerId() != null && !certInfo.getComputerId().equals("")) {
            MstrComputer dataMstrComputer = mstrComputerDao.getMstrComputer(certInfo.getComputerId());
            // 存在チェック
            // 端末定義マスタが取得できない場合、エラーを設定し終了する。
            if (dataMstrComputer == null) {
                StringBuilder params = new StringBuilder();
                params.append(MstrComputer.TABLE_NAME);
                params.append(CommonConstants.COLON).append(certInfo.getComputerId());
                // ME01.0250:「指定した端末がありません。（テーブル名:端末ID）」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0250, params.toString());
                outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0250, localeId,
                        params.toString());
                outputIsSuccess.value = 0;
                return;
            }
            if (!StringUtil.isNullOrEmpty(dataMstrComputer.getComputer_name())) {
                computerName = dataMstrComputer.getComputer_name();
            }
        }
        userInfo.setComputerId(certInfo.getComputerId());
        userInfo.setComputerName(computerName);

        String siteName = "";
        // ４．サイトマスタの取得
        if (certInfo.getSiteCode() != null && !certInfo.getSiteCode().equals("")) {
            MstrSite mstrSite = mstrSiteDao.getMstrSite(certInfo.getSiteCode());
            if (mstrSite == null) {
                StringBuilder params = new StringBuilder();
                params.append(MstrSite.TABLE_NAME);
                params.append(CommonConstants.COLON).append(certInfo.getSiteCode());
                // ME01.0026:「データが見つかりません。（テーブル名:拠点コード）」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0026, params.toString());
                outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0026, localeId,
                        params.toString());
                outputIsSuccess.value = 0;
                return;
            }
            if (!StringUtil.isNullOrEmpty(mstrSite.getSite_name())) {
                siteName = mstrSite.getSite_name();
            }
        }
        userInfo.setSiteCode(certInfo.getSiteCode());
        userInfo.setSiteName(siteName);

        List<MstrPerson> datasPerson;
        // ５．ユーザマスタの取得
        datasPerson = mstrPersonDao.getMstrPersonQuery(certInfo.getUserId(), em);

        // 存在チェック
        // ユーザが取得できない場合、エラーを設定し終了する。
        if (datasPerson == null || datasPerson.size() == 0) {
            // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198, certInfo.getUserId());
            outputMessage.value = MessagesHandler.getString(GnomesMessagesConstants.ME01_0198, localeId);
            outputIsSuccess.value = 0;
            return;
        }
        MstrPerson dataPerson = datasPerson.get(0);
        systemFunctionBean.setMstrPerson(dataPerson);

        userInfo.setUserKey(dataPerson.getUser_key());
        userInfo.setUserId(certInfo.getUserId());
        userInfo.setUserName(dataPerson.getUser_name());
        userInfo.setPassword(certInfo.getPassword());

        // ログイン認証の場合、リクエストコンテナのユーザID、ユーザ名を設定する。
        if (isLoginCertify) {
            req.setUserId(certInfo.getUserId());
            req.setUserName(dataPerson.getUser_name());

        }

        // ７．アカウントロックアウトのチェック
        // ６．で取得した ユーザ情報.アカウント状態 ＝ 1(:ロックアウト状態) の場合
        if (dataInfoUser.getIs_lock_out() == 1) {
            // ME01.0010:「ユーザアカウントはロックアウトされています。システム管理者に相談してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0010);
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0010, localeId);
            outputIsSuccess.value = 0;
            return;
        }

        // ８．システム定数の取得
        List<MstrSystemDefine> datasSystemConstant = mstrSystemDefineDao.getMstrSystemDefine();

        String messageMaxListDisplayCount = null;
        String messagePopupDisplayCount = null;
        String messageWatchPeriodForPopup = null;

        for (MstrSystemDefine data : datasSystemConstant) {
            switch (data.getSystem_define_code()) {
                // メッセージ一覧画面最大表示件数
                case SystemDefConstants.MESSAGE_MAX_LIST_DISPLAY_COUNT :
                    messageMaxListDisplayCount = data.getNumeric1().toString();
                    break;
                // ポップアップメッセージ表示件数
                case SystemDefConstants.MESSAGE_POPUP_DISPLAY_COUNT :
                    messagePopupDisplayCount = data.getNumeric1().toString();

                    break;
                // ポップアップメッセージ監視周期（分）
                case SystemDefConstants.MESSAGE_WATCH_PERIOD_FOR_POPUP :
                    messageWatchPeriodForPopup = data.getNumeric1().toString();
                    break;
                default :
                    break;
            }
        }

        StringBuilder params = new StringBuilder();
        params.append(MstrSystemDefine.TABLE_NAME);
        params.append(CommonConstants.PERIOD).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_TYPE);
        params.append(CommonConstants.COLON).append(SystemDefConstants.MESSAGE);
        params.append(CommonConstants.COMMA).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_CODE);
        // 存在チェック
        // 取得できない場合、エラーを設定し終了する。
        if (StringUtil.isNullOrEmpty(messageMaxListDisplayCount)) {
            params.append(CommonConstants.COLON).append(SystemDefConstants.MESSAGE_MAX_LIST_DISPLAY_COUNT);
            // ME01.0026:「データが見つかりません。（テーブル名, 区分, コード）」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0026, params.toString());
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0026, localeId,
                    params.toString());
            outputIsSuccess.value = 0;
            return;
        }
        if (StringUtil.isNullOrEmpty(messagePopupDisplayCount)) {
            params.append(CommonConstants.COLON).append(SystemDefConstants.MESSAGE_POPUP_DISPLAY_COUNT);
            // ME01.0026:「データが見つかりません。（テーブル名, 区分, コード）」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0026, params.toString());
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0026, localeId,
                    params.toString());
            outputIsSuccess.value = 0;
            return;
        }
        if (StringUtil.isNullOrEmpty(messageWatchPeriodForPopup)) {
            params.append(CommonConstants.COLON).append(SystemDefConstants.MESSAGE_WATCH_PERIOD_FOR_POPUP);
            // ME01.0026:「データが見つかりません。（テーブル名, 区分, コード）」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0026, params.toString());
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0026, localeId,
                    params.toString());
            outputIsSuccess.value = 0;
            return;
        }

        // ９．パスワード照合チェック
        // 入力パラメータ.ログイン認証か否か = False の場合
        if (!isLoginCertify) {
            // A101.システム定数.ログインモジュールの種類 = "JDBC" の場合
            if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.JDBC)) {
                if (!StringUtil.isNullOrEmpty(dataInfoUser.getPassword())) {
                    //パスワード一致確認の拡張
                    //パスワードが暗号化されていたらユーザ情報のパスワードを一致判定する
                    if(! this.passwordMatchWithHash(certInfo, dataInfoUser)) {
                        // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
                        MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198, certInfo.getUserId());
                        outputMessage.value = MessagesHandler.getString(GnomesMessagesConstants.ME01_0198, localeId);
                        outputIsSuccess.value = 0;
                        return;
                    }
                }
                else {
                    //パスワード文字が空の場合
                    if (!StringUtil.isNullOrEmpty(GnomesPasswordEncoder.encode(certInfo.getPassword()))) {
                        MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198, certInfo.getUserId());
                        outputMessage.value = MessagesHandler.getString(GnomesMessagesConstants.ME01_0198, localeId);
                        outputIsSuccess.value = 0;
                        return;
                    }
                }
            }
        }
        else {
            //LDAP 認証処理を行う。 （LDAP認証処理は、現時点での実装は保留し、認証成功として処理する）
        }

        // １０．アカウント有効期限、パスワード有効期限チェック
        // システム日時
        Date systemDate = new Date();

        // ユーザ情報.アカウント有効期限(from)
        Date validFromDate = dataInfoUser.getUser_id_valid_from();
        // ユーザ情報.アカウント有効期限(to)
        Date validToDate = dataInfoUser.getUser_id_valid_to();
        // システム日時が、５．で取得した ユーザ情報.アカウント有効期限(from) ～ ユーザ情報.アカウント有効期限(to) の期間内でない場合
        if (systemDate.compareTo(validFromDate) < 0 || systemDate.compareTo(validToDate) > 0) {
            // ME01.0015:「ユーザー有効期限が切れています。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0015);
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0015, localeId);
            outputIsSuccess.value = 0;

            return;
        }

        // カレンダークラスのインスタンスを取得
        Calendar cal = Calendar.getInstance();
        // 現在時刻を設定
        cal.setTime(dataInfoUser.getPassword_update_date());
        cal.add(Calendar.DATE, dataUsrsecPolicy.getPassword_limit_day());
        Date limitDate = cal.getTime();
        // システム日時 > (５．で取得した ユーザ情報.パスワード更新日時 + 2.で取得した A320.パスワード有効期限（日））の場合
        if (systemDate.compareTo(limitDate) > 0) {
            // ME01.0016:「パスワード有効期限が切れています。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0016);
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0016, localeId);
            outputIsSuccess.value = 0;
            return;
        }

        cal.setTime(dataInfoUser.getUser_id_valid_to());
        cal.add(Calendar.DATE, -dataUsrsecPolicy.getUser_id_limit_day());
        limitDate = cal.getTime();

        // 残り日数
        int countDay = getDiffDays(systemDate, validToDate);
        // システム日時 > （５．で取得した ユーザ情報.アカウント有効期限(to)
        //                            － ２．で取得したA320.アカウント期限切れ警告表示（日）） の場合、
        // 警告情報を出力パラメータに追加する。
        if (systemDate.compareTo(limitDate) > 0) {
            // MA01.0002:「残り {0}日でユーザアカウントは無効になります。システム管理者に相談してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.MA01_0002, countDay);
            outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.MA01_0002, localeId, countDay);
        }

        cal.setTime(dataInfoUser.getPassword_update_date());
        cal.add(Calendar.DATE, dataUsrsecPolicy.getPassword_limit_day());
        cal.add(Calendar.DATE, -dataUsrsecPolicy.getPassword_limit_caution());
        limitDate = cal.getTime();
        // システム日時 > (５．で取得した ユーザ情報.パスワード更新日時
        //                     + ２．で取得した A320.パスワード有効期限（日）
        //                    - ２．で取得した A320.パスワード期限切れ警告表示（日））の場合、
        // 警告情報を出力パラメータに追加する。
        if (systemDate.compareTo(limitDate) > 0) {
            cal.setTime(limitDate);
            cal.add(Calendar.DATE, dataUsrsecPolicy.getPassword_limit_caution());
            limitDate = cal.getTime();

            // 認証チェック用 パスワード変更する必要がある場合のみ行う
            if (certInfo.getIsChangePassword() != 0) {

                // 残り日数
                countDay = getDiffDays(systemDate, limitDate);
                // A101.システム定数.ログインモジュールの種類 = "JDBC" の場合
                if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.JDBC)) {
                    // MA01.0003:「残り {0}日でパスワードが無効になります。パスワードを変更してください。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.MA01_0003, countDay);
                    outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.MA01_0003, localeId,
                            countDay);
                    outputIsChangePassword.value = 1;
                }
                else {
                    // MA01.0004:「残り {0}日でパスワードが無効になります。システム管理者に相談してください。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.MA01_0004, countDay);
                    outputMessage.value = MessagesHandler.getString(GnomesLogMessageConstants.MA01_0004, localeId,
                            countDay);
                }
            }
        }

        // １１.ログイン認証時、端末工程作業選択情報から指図工程・作業工程・作業場所を取得する。
        if (isLoginCertify) {
            if (certInfo.getComputerId() != null && !certInfo.getComputerId().equals("")) {
                InfoComputerProcWorkcell infoComputerProcWorkcell = infoComputerProcWorkcellDao.getInfoComputerProcWorkcell(
                        certInfo.getComputerId(), certInfo.getSiteCode());
                if (infoComputerProcWorkcell != null) {
                    userInfo.setProcessCode(infoComputerProcWorkcell.getProcess_code());
                    userInfo.setWorkProcessCode(infoComputerProcWorkcell.getWorkprocess_code());
                    userInfo.setWorkCellCode(infoComputerProcWorkcell.getWorkcell_code());

                }
                // 取得できなかった場合、それぞれ空文字で設定
                else {
                    userInfo.setProcessCode("");
                    userInfo.setWorkProcessCode("");
                    userInfo.setWorkCellCode("");
                }
            }
        }

        // １１．ユーザ情報の設定
        outputIsSuccess.value = 1;

        // ログイン時に選択したロケールを設定
        userInfo.setLocaleId(certInfo.getLocaleId());
        Locale locale = Locale.getDefault();
        String selectLocale[] = certInfo.getLocaleId().split(CommonConstants.SPLIT_CHAR);
        if (selectLocale.length >= 2) {
            locale = new Locale(selectLocale[0], selectLocale[1]);
        }
//      ENUMの該当部をコメントにした為、以下をコメント
//      userInfo.setLanguage("LOCALE_" + GnomesLocale.valueOf(userInfo.getLocaleId()).toReverseUpperCase());
        userInfo.setLanguage("LOCALE_" + userInfo.getLocaleId().toUpperCase(locale));
        userInfo.setIsScreenLocked(false);
        userInfo.setScreenLockTimeoutTime(dataUsrsecPolicy.getApplication_lock_waiting_time());
        userInfo.setMaxListDisplayCount(ConverterUtils.stringToNumber(false, messageMaxListDisplayCount).intValue());
        userInfo.setPopupDisplayCount(ConverterUtils.stringToNumber(false, messagePopupDisplayCount).intValue());
        userInfo.setWatchPeriodForPopup(ConverterUtils.stringToNumber(false, messageWatchPeriodForPopup).intValue());

    }

    /**
     * <p>[概 要] 日付の差分日数取得処理</p>
     * <p>[詳 細] </p>
     * <p>[備 考] </p>
     * @param  fromDate 開始日付
     * @param  toDate 終了日付
     * @return 差分日数（パラメータがnullの場合は0を返します。）
     */
    private int getDiffDays(Date fromDate, Date toDate)
    {

        int diffDays = 0;
        if (fromDate != null && toDate != null) {

            long one_date_time = (long) 1000 * 60 * 60 * 24;
            long datetime1 = fromDate.getTime();
            long datetime2 = toDate.getTime();
            diffDays = (int) ((datetime2 - datetime1) / one_date_time);
        }

        return diffDays + 1;

    }

    /**
     * ロックアカウントチェック
     * @param isSuccess 認証成功か否か
     * @param em エンティティマネージャ
     * @return message
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public String checkLockAccount(int isSuccess, EntityManager em) throws Exception
    {

        // １．ユーザアカウントセキュリテイポリシーの取得
        MstrPersonSecPolicy dataUsrsecPolicy = systemFunctionBean.getMstrUsrsecPolicy();

        // ２．ユーザ情報の取得
        InfoUser dataInfoUser = systemFunctionBean.getInfoUser();

        String message = "";

        // 認証失敗
        if (isSuccess == 0) {
            int userCertifyFailureTimes = dataInfoUser.getCertify_failure_times();
            userCertifyFailureTimes++;

            // ユーザ情報:認証エラー回数の更新
            dataInfoUser.setCertify_failure_times(userCertifyFailureTimes);

            // ３．アカウントロックアウトのチェック
            // 2.で取得した ユーザ情報.認証エラー回数 ≧ 1.で取得した A320.認証エラー許容数（回）の場合
            if (userCertifyFailureTimes >= dataUsrsecPolicy.getCertify_failure_times()) {
                // 未ロックアウトの場合ロックアウト
                if (!Integer.valueOf(1).equals(dataInfoUser.getIs_lock_out())) {
                    dataInfoUser.setIs_lock_out(1);
                    dataInfoUser.setLock_out_date(new Date());
                    // メッセージ設定
                    // ユーザーID：：{0}はロックアウトされました。
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0228,
                            dataInfoUser.getUser_id());
                    String ngDetail = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0228,
                            dataInfoUser.getUser_id());
                    message = ngDetail;
                }
            }
        }
        // 認証成功
        else {
            // ユーザ情報:認証エラー回数を0に更新
            dataInfoUser.setCertify_failure_times(0);
        }

        // 更新反映
        infoUserDao.update(dataInfoUser, em);
        return message;
    }

    /**
     * ロックアカウントチェック（承認者）
     * @param isSuccess 認証成功か否か
     * @param dataInfoUser ユーザ情報
     * @param em エンティティマネージャ
     * @return message
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public String checkLockAccountApprover(int isSuccess, InfoUser dataInfoUser, EntityManager em) throws Exception
    {

        // １．ユーザアカウントセキュリテイポリシーの取得
        MstrPersonSecPolicy dataUsrsecPolicy = systemFunctionBean.getMstrUsrsecPolicy();

        String message = "";

        // 認証失敗
        if (isSuccess == 0) {
            // ３．アカウントロックアウトのチェック
            // 2.で取得した ユーザ情報.認証エラー回数 ＜ 1.で取得した A320.認証エラー許容数（回）の場合
            if (dataInfoUser.getCertify_failure_times() < dataUsrsecPolicy.getCertify_failure_times()) {
                // ユーザ情報:認証エラー回数の更新
                dataInfoUser.setCertify_failure_times(dataInfoUser.getCertify_failure_times() + 1);
            }
            // 2.で取得した ユーザ情報.認証エラー回数 ≧ 1.で取得した A320.認証エラー許容数（回）の場合
            else {
                // ユーザ情報:認証エラー回数、およびユーザ情報にアカウントロックの更新
                dataInfoUser.setCertify_failure_times(dataInfoUser.getCertify_failure_times() + 1);

                // 未ロックアウトの場合ロックアウト
                if (!Integer.valueOf(1).equals(dataInfoUser.getIs_lock_out())) {
                    dataInfoUser.setIs_lock_out(1);
                    dataInfoUser.setLock_out_date(new Date());
                    // メッセージ設定
                    // ユーザーID：：{0}はロックアウトされました。
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0228,
                            dataInfoUser.getUser_id());
                    String ngDetail = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0228,
                            dataInfoUser.getUser_id());
                    message = ngDetail;
                }
            }
        }
        // 認証成功
        else {
            // ユーザ情報:認証エラー回数を0に更新
            dataInfoUser.setCertify_failure_times(0);
        }

        // 更新反映
        infoUserDao.update(dataInfoUser, em);
        return message;
    }

    /**
     * ロックアカウントのロックを解除する
     *
     * @param lockedUserId
     *            ロックアカウントのユーザID
     * @param reason
     *            解除理由
     * @param em
     *            エンティティマネージャ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void unlockAccount(String lockedUserId, String reason, EntityManager em) throws GnomesAppException
    {
        // ログインユーザ情報取得
        String userId = null;
        String userName = null;
        try {
            userId = gnomesSessionBean.getUserId();
            userName = gnomesSessionBean.getUserName();
        }
        catch (NullPointerException e) {
        }

        // ログインユーザが取得されなかった場合
        if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(userName)) {
            String message = "userId or userName is null.";
            this.logHelper.severe(this.logger, null, null, message);

            // ME01.0029:「ログイン認証が行われていません。ログインをやりなおしてください。」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0029);
            throw ex;
        }

        // ロックアカウントのユーザ情報の取得
        InfoUser dataInfoUser = infoUserDao.getInfoUser(lockedUserId, em);

        // ロックアカウントのユーザ情報が取得できない場合、エラー
        if (dataInfoUser == null) {
            String message = "The user information of the target account cannot be acquired.";
            this.logHelper.severe(this.logger, null, null, message);

            // ME01.0026:「データがみつかりません。（テーブル名:ユーザID）」
            StringBuilder params = new StringBuilder();
            params.append(InfoUser.TABLE_NAME).append(CommonConstants.COLON).append(lockedUserId);
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0026,
                    params.toString());
            throw ex;
        }

        // ロックされていない場合（ユーザ情報.アカウント状態 ＝ 1(:ロックアウト状態) ではない場合）、エラー
        if (!Integer.valueOf(1).equals(dataInfoUser.getIs_lock_out())) {
            String message = "The target user's account is not locked.";
            this.logHelper.severe(this.logger, null, null, message);

            // ME01.0225:「ユーザアカウント[{0}]はロックアウトされていません。」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0225,
                    lockedUserId);
            throw ex;
        }

        // ロックアカウントのユーザマスタの取得
        List<MstrPerson> datasPerson = mstrPersonDao.getMstrPersonQuery(lockedUserId, em);

        // ロックアカウントのユーザマスタが取得できない場合、エラー
        if (datasPerson == null || datasPerson.size() == 0) {
            String message = "The user master of the target account cannot be acquired.";
            this.logHelper.severe(this.logger, null, null, message);

            // ME01.0026:「データがみつかりません。（テーブル名:ユーザID）」
            StringBuilder params = new StringBuilder();
            params.append(MstrPerson.TABLE_NAME).append(CommonConstants.COLON).append(lockedUserId);
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0026,
                    params.toString());
            throw ex;
        }
        // 認証履歴を登録する。
        HistCertificationWrite data = new HistCertificationWrite();
        data.setHist_certification_key(UUID.randomUUID().toString());
        data.setOccur_datetime(CurrentTimeStamp.getSystemCurrentTimeStamp());
        data.setUser_number(userId);
        data.setClient_device_id(gnomesSessionBean.getComputerId());
        data.setUser_name(userName);
        data.setClient_device_name(gnomesSessionBean.getComputerName());
        data.setIp_address(gnomesSessionBean.getIpAddress());

        // ロックアウト解除（処理：ログイン、ユーザーID：30039988）
        // ロックアウト解除（処理：{0}、ユーザーID：{1}）
        String operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0033,
                gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(CertificateType.LOGIN.getValue(),
                        gnomesSessionBean.getUserLocale()), lockedUserId);
        data.setOperation_content(operationContent);

        data.setNg_flag(1);

        // ユーザーID：：{0}のロックアウトを解除しました。
        String ngDetail = MessagesHandler.getString(GnomesMessagesConstants.ME01_0227, lockedUserId);
        data.setNg_detail(ngDetail);

        data.setReq(req);
        histCertificationWriteDao.insert(data, em);

        // ロックフラグを解除
        dataInfoUser.setIs_lock_out(0);
        dataInfoUser.setOpen_user_id(userId);
        dataInfoUser.setOpen_user_name(userName);
        dataInfoUser.setOpen_user_date(new Date());
        dataInfoUser.setOpen_user_reason(reason);

        // 認証エラー回数を0に更新
        dataInfoUser.setCertify_failure_times(0);

        // 更新反映
        infoUserDao.update(dataInfoUser, em);
    }

    /**
     * パスワード変更処理
     * @param certInfo 認証情報
     * @param em エンティティマネージャ
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void changePassword(CertInfo certInfo, EntityManager em) throws Exception
    {

        String userid = certInfo.getUserId();
        String password = certInfo.getPassword();
        String newPassword = certInfo.getNewPassword();
        Locale localeId = gnomesSessionBean.getUserLocale();

        // １．ユーザアカウントセキュリテイポリシーの取得
        List<MstrPersonSecPolicy> datasUsrsecPolicy = mstrPersonSecPolicyDao.getMstrPersonSecPolicyQuery();

        // 存在チェック
        // ユーザアカウントセキュリテイポリシーが取得できない場合、エラーを設定し終了する。
        if (datasUsrsecPolicy == null || datasUsrsecPolicy.size() == 0) {
            // ME01.0026:「データがみつかりません。（{0}）」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0026, MstrPersonSecPolicy.TABLE_NAME);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0026, localeId,
                    MstrPersonSecPolicy.TABLE_NAME));
            certInfo.setIsSuccessChange(false);
            return;
        }
        MstrPersonSecPolicy dataUsrsecPolicy = datasUsrsecPolicy.get(0);

        // ２．ユーザマスタの取得
        List<MstrPerson> datasPerson = mstrPersonDao.getMstrPersonQuery(userid, em);

        // 存在チェック
        // ユーザが取得できない場合、エラーを設定し終了する。
        if (datasPerson == null || datasPerson.size() == 0) {
            // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);
            certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        MstrPerson dataPerson = datasPerson.get(0);
        systemFunctionBean.setMstrPerson(dataPerson);

        // ユーザ情報の取得
        InfoUser dataInfoUser = infoUserDao.getInfoUser(userid, em);

        // 存在チェック
        // ユーザ情報が取得できない場合、新しくユーザ情報を登録する。
        if (dataInfoUser == null) {
            dataInfoUser = new InfoUser();
            dataInfoUser.setInfo_user_key(UUID.randomUUID().toString());
            dataInfoUser.setUser_id(userid);
            dataInfoUser.setIs_lock_out(0);

            infoUserDao.insert(dataInfoUser, em);
        }

        // ３．パスワード変更履歴を取得
        List<HistChangePassword> datasHistChangePassword = histChangePasswordDao.getHistChangePassword(userid,
                HistNgFlag.OFF.getValue(), em);

        // パスワードリスト
        List<String> passwordList = new ArrayList<>();

        // パスワード変更履歴詳細からN世代までに登録したパスワードを取得
        if (datasHistChangePassword != null && datasHistChangePassword.size() > 0) {
            // 1.で取得した A320.同一パスワードの使用制限（回）までチェック
            for (int i = 0; i <= dataUsrsecPolicy.getRepeat_same_password() - 1; i++) {
                if (i > datasHistChangePassword.size() - 1) {
                    break;
                }
                HistChangePassword dataHistChangePassword = datasHistChangePassword.get(i);
                // パスワード変更履歴詳細
                HistChangePasswordDetail dataHistChangePasswordDetail = new ArrayList<HistChangePasswordDetail>(
                        dataHistChangePassword.getHistChangePasswordDetails()).get(0);

                if (dataHistChangePasswordDetail != null) {
                    // パスワードを追加
                    passwordList.add(dataHistChangePasswordDetail.getPassword());
                }

            }
        }

        // ５．アカウントロックアウトのチェック
        // ２．で取得した ユーザ情報.アカウント状態（ロックアウトか否か）＝ 1:ロックアウト の場合、
        if (dataInfoUser.getIs_lock_out() == 1) {
            // ME01.0028:「ロックアウトしたユーザーのパスワードは変更できません。システム管理者に相談してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0028);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0028, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // 認証情報.初期化パスワードか否か＝trueの場合
        if (ConverterUtils.IntTobool(certInfo.getIsInitPassword())) {
            // パスワード禁止文字リスト
            List<MstrInvalidPasswd> datasInvalidPasswd = mstrInvalidPasswdDao.getMstrInvalidPasswd();

            // 全ての禁止文字をリストに追加
            List<String> invalidKeysList = new ArrayList<String>();
            for (int i = 0; i < datasInvalidPasswd.size(); i++) {
                invalidKeysList.add(datasInvalidPasswd.get(i).getDisapprove_charactor());
            }
            // 全ての数字をリストに追加
            List<String> numberList = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

            // 全ての英字をリストに追加
            List<String> alphabeticList = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i",
                    "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
                    "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
                    "X", "Y", "Z"));

            // 英字または数字のみのパスワード使用制限 ＝ 1:有
            if (dataUsrsecPolicy.getIs_alphabet_and_numeral_only() == 1) {
                // 英字または数字のみのパスワード使用制限 ＝ 1:有
                if (invalidKeysList.containsAll(numberList)) {
                    // ME01.0232:「パスワードの初期化ができませんでした。英字または数字のみのパスワードは使用できない設定になっていますが、パスワード禁止文字に全ての数字を登録しています。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0232);
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0232, localeId));
                    certInfo.setIsSuccessChange(false);
                    return;
                }
                else if (invalidKeysList.containsAll(alphabeticList)) {
                    // ME01.0233:「パスワードの初期化ができませんでした。英字または数字のみのパスワードは使用できない設定になっていますが、パスワード禁止文字に全ての英字を登録しています。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0233);
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0233, localeId));
                    certInfo.setIsSuccessChange(false);
                    return;
                }
            }
        }

        // ６．パスワードの暗号化
        String encryptedNewPassword;

        //新バージョンでパスワードがHash値で届く場合は、変換せずそのまま使用する
        if(certInfo.isPasswordIsHash()) {
            encryptedNewPassword = newPassword;
        }
        else {
            encryptedNewPassword = GnomesPasswordEncoder.encode(newPassword);
        }

        // ７．初期化パスワードの発行
        // 認証情報.初期化パスワードか否か＝trueの場合
        if (ConverterUtils.IntTobool(certInfo.getIsInitPassword())) {

            // 8桁（A320.パスワード有効桁数が8桁を超える場合はマスタ設定された有効桁数）のパスワードを生成。
            int passwordSize = 8;
            if (passwordSize < dataUsrsecPolicy.getMinimum_password_size()) {
                passwordSize = dataUsrsecPolicy.getMinimum_password_size();
            }

            certInfo.setIsSuccessChange(false);
            // パスワードチェック処理が成功するまでパスワードを発行する。

            // 英数、大文字含む、桁数：A320.パスワード有効桁数に沿ったランダム文字列パスワードの生成を行う。
            String randomPassword = RandomStringUtils.randomAlphanumeric(passwordSize);
            certInfo.setNewPassword(randomPassword);
            certInfo.setNewPasswordConfirm(randomPassword);

            // パスワードの暗号化
            encryptedNewPassword = GnomesPasswordEncoder.encode(randomPassword);
            certInfo.setNewPasswordEncrypted(encryptedNewPassword);

            // ８．パスワードチェック処理
            this.checkPassword(certInfo, dataUsrsecPolicy, passwordList);
        }
        else {

            certInfo.setNewPasswordEncrypted(encryptedNewPassword);
            // ８．パスワードチェック処理
            this.checkPassword(certInfo, dataUsrsecPolicy, passwordList);
            // チェック結果NG
            if (!certInfo.getIsSuccessChange()) {
                return;
            }
        }

        // ９．認証チェック
        // ログインユーザが対象の場合、現在のパスワードと入力パラメータ.パスワードのチェックを行う
        // *他者のパスワードを変更する場合は本処理を行わない。
        if (gnomesSessionBean.getUserId().equals(userid)) {
            if(certInfo.isPasswordIsHash()) {
                if(! (password.equals(dataInfoUser.getPassword()))){
                    // ME01.0024:「古いパスワードが正しくありません。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0024);
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0024, localeId));
                    certInfo.setIsSuccessChange(false);
                    return;
                }
            }
            // 入力パラメータ.パスワード ≠ 2.で取得した ユーザ情報.パスワード の場合
            else if (!GnomesPasswordEncoder.matches(password, dataInfoUser.getPassword())) {
                // ME01.0024:「古いパスワードが正しくありません。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0024);
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0024, localeId));
                certInfo.setIsSuccessChange(false);
                return;
            }
        }

        // 初期化パスワード置き換える処理
        if (ConverterUtils.IntTobool(certInfo.getIsInitPassword()) && !certInfo.getIsSuccessChange()) {
            // 禁止文字チェックと置き換え処理
            this.checkInvalidCharacter(certInfo, dataUsrsecPolicy);
            // 置き換えたパスワードの暗号化
            encryptedNewPassword = GnomesPasswordEncoder.encode(certInfo.getNewPassword());
        }

        // １０．アカウント有効期限、パスワード有効期限チェック
        // システム日時
        Date wdate = new Date();

        Calendar cal = Calendar.getInstance();
        // 現在時刻を設定
        cal.setTime(wdate);
        Date systemDate = cal.getTime();

        // ユーザ情報.アカウント有効期限(from)
        cal.setTime(dataInfoUser.getUser_id_valid_from());
        Date validFromDate = cal.getTime();

        // ユーザ情報.アカウント有効期限(to)
        cal.setTime(dataInfoUser.getUser_id_valid_to());
        Date validToDate = cal.getTime();

        // システム日時が、2.で取得した ユーザ情報.アカウント有効期限(from) ～ ユーザ情報.アカウント有効期限(to) の期間内でない場合
        if (systemDate.compareTo(validFromDate) < 0 || systemDate.compareTo(validToDate) > 0) {
            // ME01.0015:「ユーザー有効期限が切れています。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0015);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0015, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // *他者のパスワードを変更する場合は本処理を行わない。
        if (gnomesSessionBean.getUserId().equals(userid)) {
            cal.setTime(dataInfoUser.getPassword_update_date());
            cal.add(Calendar.DATE, dataUsrsecPolicy.getPassword_limit_day());
            Date limitDate = cal.getTime();

            // システム日時 > (2.で取得した ユーザ情報.パスワード更新日時 + 1.で取得した A320.パスワード有効期限（日））の場合
            if (systemDate.compareTo(limitDate) > 0) {
                // ME01.0016:「パスワード有効期限が切れています。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0016);
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0016, localeId));
                certInfo.setIsSuccessChange(false);
                return;
            }
        }

        // １１．パスワードの更新
        dataInfoUser.setPassword(encryptedNewPassword);
        dataInfoUser.setPassword_update_date(wdate);

        infoUserDao.update(dataInfoUser, em);

        MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.MG01_0019);
        certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.MG01_0020, localeId,
                certInfo.getUserId(), certInfo.getNewPassword()));

    }

    /**
     * 禁止文字チェックと置き換え処理
     * @param certInfo 認証情報
     * @param dataUsrsecPolicy ユーザアカウントセキュリティポリシーマスタ
     * @throws GnomesAppException
     */
    private void checkInvalidCharacter(CertInfo certInfo, MstrPersonSecPolicy dataUsrsecPolicy)
            throws GnomesAppException
    {

        Locale localeId = gnomesSessionBean.getUserLocale();
        // 特定文字列の使用禁止制限＝ 1:有
        if (certInfo.getNewPassword().length() > 0) {
            if (dataUsrsecPolicy.getIs_specific_character_prohibit() == 1) {

                // パスワード禁止文字リスト
                List<MstrInvalidPasswd> datasInvalidPasswd = mstrInvalidPasswdDao.getMstrInvalidPasswd();

                // 全ての英字リスト
                List<String> alphabetList = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h",
                        "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A",
                        "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                        "U", "V", "W", "X", "Y", "Z"));

                // 全ての数字リスト
                List<String> numericList = new ArrayList<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7",
                        "8", "9"));

                // 全ての特殊文字リスト
                List<String> specialList = new ArrayList<String>(Arrays.asList("@", "＠", "」", "「", "#", "%", "&", "<",
                        "~", "(", "[", "{", "\\", "^", "-", "=", "$", "!", "|", "]", "}", ")", "?", "*", "+", ".", ":",
                        ">", "："));

                // 全ての禁止文字をリストに追加
                List<String> invalidList = new ArrayList<String>();
                for (int i = 0; i < datasInvalidPasswd.size(); i++) {
                    invalidList.add(datasInvalidPasswd.get(i).getDisapprove_charactor());
                }
                // 全ての禁止文字を外す
                alphabetList.removeAll(invalidList);

                // 全ての禁止文字を外す
                numericList.removeAll(invalidList);

                // 全ての禁止文字を外す
                specialList.removeAll(invalidList);

                List<String> keyboardList = new ArrayList<String>();
                // 有効文字を追加する
                keyboardList.addAll(alphabetList);
                keyboardList.addAll(numericList);
                keyboardList.addAll(specialList);

                // 禁止文字を外して残った文字がある場合
                if (keyboardList.size() > 0) {

                    String newPassword = certInfo.getNewPassword();
                    String[] newPasswordStringList = new String[newPassword.length()];
                    // パスワード文字列を文字列リストに変換する
                    for (int i = 0; i < newPassword.length(); i++) {
                        newPasswordStringList[i] = String.valueOf(newPassword.charAt(i));
                    }

                    int randomIndex = 0; // when keyboardKeysList length is 1, index is 0

                    // 禁止文字チェックと置き換え処理
                    for (int i = 0; i < newPasswordStringList.length; i++) {
                        for (MstrInvalidPasswd items : datasInvalidPasswd) {
                            // パスワード禁止文字に登録された禁止文字が含まれている場合
                            if (items.getDisapprove_charactor().equals(newPasswordStringList[i])) {
                                if (keyboardList.size() == 1) {
                                    // 禁止文字の代わりにランダム値を置き換え
                                    newPasswordStringList[i] = keyboardList.get(randomIndex);
                                }
                                else {
                                    // 禁止文字の代わりにランダム値を置き換え
                                    randomIndex = rand.nextInt(keyboardList.size());
                                    newPasswordStringList[i] = keyboardList.get(randomIndex);
                                }
                                break;
                            }
                        }
                    }
                    // パスワード文字列リストを文字列に変換する
                    StringBuilder builder = new StringBuilder();
                    for (String s : newPasswordStringList) {
                        builder.append(s);
                    }
                    String newRandomPassword = builder.toString();

                    String[] alphabetKeysList = alphabetList.toArray(new String[0]);
                    String[] numericKeysList = numericList.toArray(new String[0]);

                    // 全て同じ文字または数字の使用禁止制限＝ 1:有
                    if (dataUsrsecPolicy.getIs_one_character_password_prohibit() == 1 && isSonecharacter(
                            newRandomPassword)) {
                        newRandomPassword = this.replaceFirstCharPassword(newPasswordStringList, alphabetKeysList,
                                numericKeysList);
                    }

                    // アカウントと同じパスワードの使用禁止制限＝ 1:有
                    if (dataUsrsecPolicy.getIs_user_id_password_prohibit() == 1 && certInfo.getUserId().equals(
                            newRandomPassword)) {
                        newRandomPassword = this.replaceFirstCharPassword(newPasswordStringList, alphabetKeysList,
                                numericKeysList);
                    }

                    // 英字または数字のみのパスワード使用制限 ＝ 1:有
                    if (dataUsrsecPolicy.getIs_alphabet_and_numeral_only() == 1 && (newRandomPassword.matches(
                            NUMBER) || newRandomPassword.matches(ALPHABET))) {

                        // 禁止文字置き換えたあとで、パスワードが英字または数字のみの場合
                        if (newRandomPassword.matches(NUMBER) && alphabetKeysList.length > 0) {
                            // 数字だったら、一個目に英字を置き換え
                            int randomIndexValue = rand.nextInt(alphabetKeysList.length);
                            newPasswordStringList[0] = alphabetKeysList[randomIndexValue];

                        }
                        else if (newRandomPassword.matches(ALPHABET) && numericKeysList.length > 0) {
                            //英字だったら、一個目に数字を置き換え
                            int randomIndexValue = rand.nextInt(numericKeysList.length);
                            newPasswordStringList[0] = numericKeysList[randomIndexValue];
                        }
                        // builderをクリア
                        builder.delete(0, builder.length());
                        for (String s : newPasswordStringList) {
                            builder.append(s);
                        }
                        newRandomPassword = builder.toString();
                    }
                    // パスワードの暗号化
                    certInfo.setNewPasswordEncrypted(GnomesPasswordEncoder.encode(newRandomPassword));
                    certInfo.setNewPassword(newRandomPassword);
                    certInfo.setIsSuccessChange(true);
                }
                else {
                    // ME01.0230:「パスワードの初期化ができませんでした。全てのキーはパスワード禁止文字に設定されています。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0230);
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0230, localeId));
                    certInfo.setIsSuccessChange(false);
                }
            }
        }
    }

    /**
     * パスワードの先頭1文字を置換える処理
     * @param password パスワード
     * @param alphabetKeysList 英字リスト
     * @param numericKeysList 数字リスト
     * @return パスワード
     */
    private String replaceFirstCharPassword(String[] password, String[] alphabetKeysList, String[] numericKeysList)
    {
        int randomIndex;
        // パスワード文字列リストを文字列に変換する
        StringBuilder newPassword = new StringBuilder();

        // パスワードの先頭1文字が英字または数字のみの場合
        if (password[0].matches(NUMBER) && alphabetKeysList.length > 0) {
            // 数字だったら、一個目に英字を置き換え
            randomIndex = rand.nextInt(alphabetKeysList.length);
            password[0] = alphabetKeysList[randomIndex];

        }
        else if (password[0].matches(ALPHABET) && numericKeysList.length > 0) {
            //英字だったら、一個目に数字を置き換え
            randomIndex = rand.nextInt(numericKeysList.length);
            password[0] = numericKeysList[randomIndex];
        }
        for (String s : password) {
            newPassword.append(s);
        }
        return newPassword.toString();
    }

    /**
     * パスワードチェック処理
     * @param certInfo 認証情報
     * @param dataUsrsecPolicy ユーザアカウントセキュリティポリシーマスタ
     * @param passwordList 過去に登録したパスワードリスト
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void checkPassword(CertInfo certInfo, MstrPersonSecPolicy dataUsrsecPolicy, List<String> passwordList)
            throws Exception
    {

        Locale localeId = gnomesSessionBean.getUserLocale();

        // ８．パスワードのチェック
        // 入力パラメータ.変更するパスワード ≠ 入力パラメータ.変更するパスワード（確認）の場合
        if (!certInfo.getNewPassword().equals(certInfo.getNewPasswordConfirm())) {
            // ME01.0013:「変更するパスワードが有効ではありません。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0013);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0013, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // 入力パラメータ.変更するパスワード < ２．で取得した A320.パスワード有効桁数 の場合
        if (certInfo.getNewPassword().length() < dataUsrsecPolicy.getMinimum_password_size()) {
            // ME01.0019:「パスワードは {0}桁以上入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0019,
                    dataUsrsecPolicy.getMinimum_password_size());
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0019, localeId,
                    dataUsrsecPolicy.getMinimum_password_size()));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // 1.で取得した A320.英字または数字のみのパスワード使用制限 ＝ 1:有
        // かつ 入力パラメータ.変更するパスワードが英字または数字のみの場合
        if (dataUsrsecPolicy.getIs_alphabet_and_numeral_only() == 1 && (certInfo.getNewPassword().matches(
                NUMBER) || certInfo.getNewPassword().matches(ALPHABET))) {
            // ME01.0023:「英字または数字のみのパスワードを使用することはできません。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0023);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0023, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // 1.で取得した A320.全て同じ文字または数字の使用禁止制限＝ 1:有
        // かつ 入力パラメータ.変更するパスワードが全て同一の文字のみの場合
        if (certInfo.getNewPassword().length() > 0) {
            if (dataUsrsecPolicy.getIs_one_character_password_prohibit() == 1 && isSonecharacter(
                    certInfo.getNewPassword())) {
                // ME01.0020:「全て同じ文字または数字パスワードを使用することはできません。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0020);
                certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0020, localeId));
                certInfo.setIsSuccessChange(false);
                return;
            }
        }

        // 1.で取得した A320.アカウントと同じパスワードの使用禁止制限＝ 1:有
        // かつ 入力パラメータ.変更するパスワードが入力パラメータ.ユーザIDと同一の場合
        if (dataUsrsecPolicy.getIs_user_id_password_prohibit() == 1 && certInfo.getUserId().equals(
                certInfo.getNewPassword())) {
            // ME01.0021:「ユーザーIDと同じパスワードを使用することはできません。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0021);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0021, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // 1.で取得した A320.特定文字列の使用禁止制限＝ 1:有
        if (certInfo.getNewPassword().length() > 0) {
            if (dataUsrsecPolicy.getIs_specific_character_prohibit() == 1) {
                List<MstrInvalidPasswd> datasInvalidPasswd = mstrInvalidPasswdDao.getMstrInvalidPasswd();

                String str = "";
                boolean isFound = false;
                for (MstrInvalidPasswd items : datasInvalidPasswd) {
                    // 入力パラメータ.変更するパスワードに A321:パスワード禁止文字に登録された禁止文字が含まれている場合
                    if (certInfo.getNewPassword().matches(".*[" + items.getDisapprove_charactor() + "]+.*")) {
                        isFound = true;
                    }
                    str += items.getDisapprove_charactor() + " ";
                }
                if (isFound == true) {
                    // ME01.0022:「特定文字列 {0} をパスワードに使用することはできません。」
                    MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0022, str);
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0022, localeId, str));
                    certInfo.setIsSuccessChange(false);
                    return;
                }
            }
        }

        // パスワード変更履歴詳細から過去に登録したパスワードを取得、N世代までに登録したパスワードとの比較を行う。
        if (passwordList != null && passwordList.size() > 0) {
            // 1.で取得した A320.同一パスワードの使用制限（回）までチェック
            for (String password : passwordList) {
                // 入力パラメータ.変更するパスワード = パスワード変更履歴詳細.パスワード の場合
                if (GnomesPasswordEncoder.matches(certInfo.getNewPassword(), password)) {
                    // ME01.0017:「最近使用したパスワードを再度使用することはできません。」
                    certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0017, localeId));
                    certInfo.setIsSuccessChange(false);
                    return;
                }
            }
        }

        // 入力パラメータ.パスワード = 入力パラメータ.変更するパスワードの場合
        if (certInfo.getPassword().equals(certInfo.getNewPassword())) {
            // ME01.0197:「現在使用中のパスワードを使用することはできません。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0197);
            certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0197, localeId));
            certInfo.setIsSuccessChange(false);
            return;
        }

        // チェック成功
        certInfo.setIsSuccessChange(true);
    }

    /**
     * パスワード変更メール通知処理
     * @param certInfo 認証情報
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void sendMailChangePassword(CertInfo certInfo) throws Exception
    {
        // システム日付
        Timestamp sysDate = ConverterUtils.utcToTimestamp(ConverterUtils.dateToLocalDateTime(
                CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        String strSystemDate = ConverterUtils.dateTimeToString(sysDate, ResourcesHandler.getString(
                GnomesResourcesConstants.YY01_0001));

        // システム定数の取得
        MstrSystemDefine mstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.SECURITY,
                SystemDefConstants.PASSWORD_CHANGE_MAIL_FLAG);
        // 存在チェック
        // 取得できない場合、エラーを設定し終了する。
        if (mstrSystemDefine == null || mstrSystemDefine.getNumeric1() == null) {
            StringBuilder params = new StringBuilder();
            params.append(MstrSystemDefine.TABLE_NAME);
            params.append(CommonConstants.PERIOD).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_TYPE);
            params.append(CommonConstants.COLON).append(SystemDefConstants.SECURITY);
            params.append(CommonConstants.COMMA).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_CODE);
            params.append(CommonConstants.COLON).append(SystemDefConstants.PASSWORD_CHANGE_MAIL_FLAG);
            // ME01.0026:「データが見つかりません。（テーブル名, 区分, コード）」
            this.cause = MessagesHandler.getString(GnomesMessagesConstants.ME01_0026, gnomesSessionBean.getUserLocale(),
                    params.toString());

            String message = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0145,
                    gnomesSessionBean.getUserLocale(), GnomesMessagesConstants.MG01_0020, strSystemDate, this.cause);

            this.logHelper.fine(this.logger, null, null, message);

            return;
        }

        // メール送信処理
        if (mstrSystemDefine.getNumeric1().intValue() == ChangePasswordMailNoticeFlag.ON.getValue()) {

            // ユーザマスタの取得
            MstrPerson dataPerson = systemFunctionBean.getMstrPerson();

            // メールアドレスが設定されていないユーザの場合
            if (StringUtil.isNullOrEmpty(dataPerson.getMail_address())) {
                // ME01.0196:「メールアドレスが設定されていません。 (ユーザーID： {0}) 」
                this.cause = MessagesHandler.getString(GnomesMessagesConstants.ME01_0196,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId());
                String message = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0145,
                        gnomesSessionBean.getUserLocale(), GnomesMessagesConstants.MG01_0020, strSystemDate,
                        this.cause);

                this.logHelper.fine(this.logger, null, null, message);
                return;
            }

            // メッセージタイトル作成
            String subject = MessagesHandler.getString(GnomesMessagesConstants.YY01_0057,
                    gnomesSessionBean.getUserLocale());

            // メッセージ本文作成
            String message = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0020,
                    gnomesSessionBean.getUserLocale(), certInfo.getUserId(), certInfo.getNewPassword());

            // メールサーバー情報取得
            this.getMailServerInfo(strSystemDate);

            // メールサーバー情報取得時にエラーが発生した場合、メール通知を行わない。
            if (!StringUtil.isNullOrEmpty(this.cause)) {
                return;
            }

            // 宛先 To
            this.mailInfoBean.setTo(dataPerson.getMail_address());

            // 題名
            this.mailInfoBean.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
            // メッセージ
            this.mailInfoBean.setMessage(message);

            // コンテキストパラメータ
            String[] jobParam = {""};
            TalendJobRun.runJob(SendMail.class.getName(), jobParam, false);

            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(mailInfoBean.getJobName())) {
                // メール通知状況：失敗

                this.cause = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0253,
                        gnomesSessionBean.getUserLocale(), mailInfoBean.getJobName(), mailInfoBean.getComponentName(), mailInfoBean.getErrorMessage());

                String logMessage = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0145,
                        gnomesSessionBean.getUserLocale(), GnomesMessagesConstants.MG01_0020, strSystemDate,
                        this.cause);

                this.logHelper.severe(this.logger, null, null, logMessage);

            }

        }
    }

    /**
     * パスワードのチェック情報を取得.
     * @return パスワードのチェック情報
     * @throws Exception
     */
    public CheckPasswordInfo getCheckPasswordInfo() throws GnomesAppException
    {
        CheckPasswordInfo checkPasswordInfo = new CheckPasswordInfo();

        Locale localeId = gnomesSessionBean.getUserLocale();
        
        // ユーザアカウントセキュリテイポリシーの取得
        List<MstrPersonSecPolicy> datasUsrsecPolicy = mstrPersonSecPolicyDao.getMstrPersonSecPolicyQuery();
        MstrPersonSecPolicy dataUsrsecPolicy = datasUsrsecPolicy.get(0);

        // 1.パスワード有効桁数
        Integer minimumPasswordSize = dataUsrsecPolicy.getMinimum_password_size();
        checkPasswordInfo.setMinimumPasswordSize(minimumPasswordSize);
        // ME01.0019:「パスワードは {0}桁以上入力してください。」
        String msgMinimumPasswordSize =  MessagesHandler.getString(GnomesLogMessageConstants.ME01_0019, localeId,
                minimumPasswordSize);
        checkPasswordInfo.setMsgMinimumPasswordSize(msgMinimumPasswordSize);
        
        // 2.英字または数字のみのパスワード使用制限   
        if (dataUsrsecPolicy.getIs_alphabet_and_numeral_only() == 1) {
            // チェックが必要
            checkPasswordInfo.setIsAlphabetAndNumeralOnly(1);
            // ME01.0023:「英字または数字のみのパスワードを使用することはできません。」
            String msgIsAlphabetAndNumeralOnly = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0023, localeId);
            checkPasswordInfo.setMsgIsAlphabetAndNumeralOnly(msgIsAlphabetAndNumeralOnly);            
        }
        else {
            // チェック不要
            checkPasswordInfo.setIsAlphabetAndNumeralOnly(0);
        }
        
        // 3.全て同じ文字または数字の使用禁止制限        
        if (dataUsrsecPolicy.getIs_one_character_password_prohibit() == 1) {
            // チェックが必要
            checkPasswordInfo.setIsOneCharacterPasswordProhibit(1);
            // ME01.0020:「全て同じ文字または数字パスワードを使用することはできません。」
            String msgIsOneCharacterPasswordProhibit = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0020, localeId);
            checkPasswordInfo.setMsgIsOneCharacterPasswordProhibit(msgIsOneCharacterPasswordProhibit);
        }
        else {
            // チェック不要
            checkPasswordInfo.setIsOneCharacterPasswordProhibit(0);
        }
        
        // 4.ユーザIDと同じパスワードの使用禁止制限
        if (dataUsrsecPolicy.getIs_user_id_password_prohibit() == 1) {
            // チェックが必要
            checkPasswordInfo.setIsUserIdPasswordProhibit(1);
            // ME01.0021:「ユーザーIDと同じパスワードを使用することはできません。」
            String msgIsUserIdPasswordProhibit = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0021, localeId);
            checkPasswordInfo.setMsgIsUserIdPasswordProhibit(msgIsUserIdPasswordProhibit);
        }
        else {
            // チェック不要
            checkPasswordInfo.setIsUserIdPasswordProhibit(0);
        }
        
        // 5.禁止文字
        Map<String, String> invalidPasswd = new HashMap<>();
        if (dataUsrsecPolicy.getIs_specific_character_prohibit() == 1) {
            List<MstrInvalidPasswd> datasInvalidPasswd = mstrInvalidPasswdDao.getMstrInvalidPasswd();

            // ループ処理で禁止文字を抽出
            for (MstrInvalidPasswd items : datasInvalidPasswd) {
                // 禁止文字
                String charInvalidPasswd = items.getDisapprove_charactor();
                // ME01.0022:「特定文字列 {0} をパスワードに使用することはできません。」
                String messageInvalidPasswd = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0022, localeId, 
                        charInvalidPasswd);
                
                invalidPasswd.put(charInvalidPasswd, messageInvalidPasswd);
            }
        }
        // 禁止文字が無いケースでも空のMapを格納しておく
        checkPasswordInfo.setInvalidPasswd(invalidPasswd);        
        
        return checkPasswordInfo;
    }

    /**
     * メールサーバー情報取得.
     * @param strSystemDate システム日時
     * @throws Exception
     */
    private void getMailServerInfo(String strSystemDate) throws GnomesAppException
    {

        if (!StringUtil.isNullOrEmpty(this.cause)) {
            return;
        }

        StringBuilder cause = new StringBuilder();

        // 差出人取得
        MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.CODE_MAIL_ADDRESS_FROM_CHANGE_PASSWORD);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            cause.append(SystemDefConstants.CODE_MAIL_ADDRESS_FROM_CHANGE_PASSWORD);

        }
        else {
            this.mailInfoBean.setFrom(mstrSystemDefine.getChar1());
        }

        // 認証 ユーザID取得
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.CODE_AUTHENTICATED_USERID_CHANGE_PASSWORD);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(CommonConstants.COMMA);
            }
            cause.append(SystemDefConstants.CODE_AUTHENTICATED_USERID_CHANGE_PASSWORD);

        }
        else {
            this.mailInfoBean.setAuthenticatedUserId(mstrSystemDefine.getChar1());
        }

        //システム定義のコードからキーストアを調べてパスワードを取得する
        this.mailInfoBean.setAuthenticatedPassword(keyStoreUtilities.getSecretKeyValue(
                SystemDefConstants.CODE_AUTHENTICATED_PASSWORD_CHANGE_PASSWORD));

        // 認証 エンコード取得
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.CODE_ENCODE);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(CommonConstants.COMMA);
            }
            cause.append(SystemDefConstants.CODE_ENCODE);

        }
        else {
            this.mailInfoBean.setEncode(mstrSystemDefine.getChar1());
        }

        // 認証 メールサーバーホスト名取得
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.MAIL_SERVER_HOST);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(CommonConstants.COMMA);
            }
            cause.append(SystemDefConstants.MAIL_SERVER_HOST);

        }
        else {
            this.mailInfoBean.setMailhost(mstrSystemDefine.getChar1());
        }

        // 認証 メールサーバーポート名取得
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.MAIL_SERVER_PORT);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(CommonConstants.COMMA);
            }
            cause.append(SystemDefConstants.MAIL_SERVER_PORT);

        }
        else {
            this.mailInfoBean.setPort(mstrSystemDefine.getChar1());
        }

        /**メールサーバ送信種別を取得
         *
         * | 設定値            | 暗号化方式   | 認証 |
         * |-------------------|--------------|------|
         * | "STARTTLS,NOAUTH" | STARTTLS     | なし |
         * | "SSL,NOAUTH"      | SSL          | なし |
         * | "NOSECURE,NOAUTH" | なし（平文） | なし |
         * | "STARTTLS,AUTH"   | STARTTLS     | あり |
         * | "SSL,AUTH"        | SSL          | あり |
         * | "NOSECURE,AUTH"   | なし（平文） | あり |
        */
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.MAIL_SEND_DIV);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(CommonConstants.COMMA);
            }
            cause.append(SystemDefConstants.MAIL_SEND_DIV);

        }
        else {
            this.mailInfoBean.setMailSendDiv(mstrSystemDefine.getChar1());
        }

        //------------------------------------------------------------------------------------------
        // メールサーバー情報の取得に失敗した場合
        //------------------------------------------------------------------------------------------
        if (MailNoticeStatus.NoticeFailure.equals(this.mailNoticeStatus)) {

            this.cause = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0144,
                    gnomesSessionBean.getUserLocale(), new Object[]{cause.toString()});

            String message = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0145,
                    gnomesSessionBean.getUserLocale(), GnomesMessagesConstants.MG01_0020, strSystemDate, this.cause);

            this.logHelper.fine(this.logger, null, null, message);

        }

    }

    /**
     * 認証履歴登録
     * @param certInfo 認証情報
     * @param userInfo ユーザ情報
     * @param isSuccess 成功か否か
     * @param certType 認証区分
     * @param certUserId 認証者ユーザID
     * @param em エンティティマネージャ
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public void insertHistCertification(CertInfo certInfo, UserInfo userInfo, boolean isSuccess,
            CertificateType certType, String certUserId, EntityManager em) throws GnomesAppException, ParseException
    {

        // 操作内容
        String operationContent;
        // NGフラグ
        int ngFlag;
        // NG詳細情報
        String ngDetail;

        // 認証成功か否か＝trueの場合
        if (isSuccess) {
            if (certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
                // ダブル認証処理成功 (ユーザーID：{0}、認証者：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5})
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0003,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId(), certUserId, req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else if (certType.equals(CertificateType.PRIVILEGE_CHECK)) {
                // 認証処理成功（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0029,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else if (certType.equals(CertificateType.APPROVE_PRIVILEGE_CHECK)) {
                // 承認処理成功（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0034,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else if (certType.equals(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK)) {
                // 代替承認処理成功（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0035,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else {
                // 認証処理成功（処理：{0}、ユーザーID：{1}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0021,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId());
            }
            ngFlag = 0;
            ngDetail = null;
        }
        // 認証成功か否か＝    falseの場合
        else {
            if (certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
                // ダブル認証処理失敗 (ユーザーID：{0}、認証者：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5})
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0023,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId(), certUserId, req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else if (certType.equals(CertificateType.PRIVILEGE_CHECK)) {
                // 認証処理失敗（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0030,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else if (certType.equals(CertificateType.APPROVE_PRIVILEGE_CHECK)) {
                // 承認処理失敗（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0036,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else if (certType.equals(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK)) {
                // 代替承認失敗（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0037,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), req.getScreenId(),
                        req.getScreenName(), systemFormBean.getButtonId(), systemFormBean.getButtonName());
            }
            else {
                // 認証処理失敗（処理：{0}、ユーザーID：{1}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0022,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId());
            }

            ngFlag = 1;
            ngDetail = certInfo.getMessage();
        }

        // 認証履歴を登録する。
        HistCertificationWrite data = new HistCertificationWrite();

        String strUserNumber = certInfo.getUserId();
        // 従業員Noは最大20桁まで
        if (strUserNumber.length() > 20) {
            strUserNumber = strUserNumber.substring(0, 20);
        }

        // userIdとuserNameを設定する
        req.setUserId(userInfo.getUserId());
        req.setUserName(userInfo.getUserName());

        data.setHist_certification_key(UUID.randomUUID().toString());
        data.setOccur_datetime(CurrentTimeStamp.getSystemCurrentTimeStamp());
        data.setUser_number(strUserNumber);
        data.setClient_device_id(certInfo.getComputerId());
        data.setUser_name(req.getUserName());
        data.setClient_device_name(userInfo.getComputerName());
        data.setIp_address(userInfo.getIpAddress());
        data.setOperation_content(operationContent);
        data.setNg_flag(ngFlag);
        data.setNg_detail(ngDetail);
        data.setReq(req);

        histCertificationWriteDao.insert(data, em);

    }

    /**
     * パスワード変更履歴登録
     * @param certInfo 認証情報
     * @param isSuccess 変更成功か否か
     * @param em エンティティマネージャ
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public void insertHistChangePassword(CertInfo certInfo, boolean isSuccess, EntityManager em)
            throws GnomesAppException, ParseException
    {

        // 操作内容
        String operationContent;
        // NGフラグ
        int ngFlag;
        // NG詳細情報
        String ngDetail;

        // パスワード変更区分
        ChangePasswordType changePasswordType = ChangePasswordType.getEnum(certInfo.getIsInitPassword());

        // 変更成功か否か＝trueの場合
        if (isSuccess) {
            if (changePasswordType.equals(ChangePasswordType.CHANGE_PASSWORD)) {
                // パスワード変更成功 (ユーザーID： {0})
                operationContent = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0024,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId());
            }
            else {
                // パスワード初期化成功 (ユーザーID： {0})
                operationContent = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0026,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId());
            }
            ngFlag = 0;
            ngDetail = null;
        }
        // 変更成功か否か＝    falseの場合
        else {
            if (changePasswordType.equals(ChangePasswordType.CHANGE_PASSWORD)) {
                // パスワード変更失敗 (ユーザーID： {0})
                operationContent = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0025,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId());
            }
            else {
                // パスワード初期化失敗 (ユーザーID： {0})
                operationContent = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0027,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId());
            }

            ngFlag = 1;
            ngDetail = certInfo.getMessage();
        }

        // パスワード変更履歴を登録する。
        HistChangePasswordWrite dataHistChangePassword = new HistChangePasswordWrite();

        dataHistChangePassword.setHist_change_password_key(UUID.randomUUID().toString());
        dataHistChangePassword.setOccur_datetime(CurrentTimeStamp.getSystemCurrentTimeStamp());
        dataHistChangePassword.setUser_number(gnomesSessionBean.getUserId());
        dataHistChangePassword.setClient_device_id(gnomesSessionBean.getComputerId());
        dataHistChangePassword.setUser_name(gnomesSessionBean.getUserName());
        dataHistChangePassword.setClient_device_name(gnomesSessionBean.getComputerName());
        dataHistChangePassword.setIp_address(gnomesSessionBean.getIpAddress());
        dataHistChangePassword.setOperation_content(operationContent);
        dataHistChangePassword.setNg_flag(ngFlag);
        dataHistChangePassword.setNg_detail(ngDetail);

        histChangePasswordWriteDao.insert(dataHistChangePassword, em);

        // パスワード変更履歴詳細を登録する。
        HistChangePasswordDetailWrite dataHistChangePasswordDetailWrite = new HistChangePasswordDetailWrite();

        dataHistChangePasswordDetailWrite.setHist_change_password_detail_key(UUID.randomUUID().toString());
        dataHistChangePasswordDetailWrite.setHist_change_password_key(
                dataHistChangePassword.getHist_change_password_key());
        dataHistChangePasswordDetailWrite.setPassword(certInfo.getNewPasswordEncrypted());

        histChangePasswordDetailWriteDao.insert(dataHistChangePasswordDetailWrite, em);

    }

    /**
     * 同一文字判定
     * @param str 判定文字
     * @return 判定結果
     */
    private boolean isSonecharacter(String str)
    {

        char a = str.charAt(0);

        for (int i = 1; i < str.length(); i++) {
            if (a != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * ログインユーザ作業権限チェック
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param certType 認証区分
     * @param certUserId 承認者ユーザID
     * @return 権限有無
     * @throws GnomesAppException
     */
    public Boolean judgeLoginUserLicense(String privilegeId, String siteCode, String orderProcessCode,
            String workProcessCode, CertificateType certType) throws GnomesAppException
    {

        // 権限有無
        boolean isPrivilege = false;

        // 前処理
        // チェック対象ユーザー（セッション.ユーザID）
        String checkUserId = gnomesSessionBean.getUserId();

        // 拠点コード
        String checkSiteCode = siteCode;
        // 指図工程コード
        String checkOrderProcessCode = orderProcessCode;
        // 作業工程コード
        String checkWorkProcessCode = workProcessCode;

        // 拠点コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkSiteCode)) {
            checkSiteCode = CommonConstants.SITE_CODE_ALL;
        }

        // 指図工程コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkOrderProcessCode)) {
            checkOrderProcessCode = CommonConstants.ORDER_PROCESS_CODE_ALL;
        }

        // 作業工程コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkWorkProcessCode)) {
            checkWorkProcessCode = CommonConstants.WORK_PROCESS_CODE_ALL;
        }

        // ３．認証用データ取得処理
        CertInfo certInfo = new CertInfo();
        certInfo.setUserId(checkUserId);
        certInfo.setPassword(null);
        certInfo.setComputerId(gnomesSessionBean.getComputerId());
        certInfo.setLocalId(null);
        certInfo.setSiteCode(null);
        // パスワード期限確認処理を行わない
        certInfo.setIsChangePassword(0);

        // ユーザ情報
        UserInfo userInfo = new UserInfo();
        userInfo.setIpAddress(gnomesSessionBean.getIpAddress());
        userInfo.setUserId(checkUserId);
        userInfo.setUserName(gnomesSessionBean.getUserName());
        userInfo.setComputerName(gnomesSessionBean.getComputerName());
        EntityTransaction tran = null;

        // エンティティマネージャの取得
        EntityManager em;
        if (gnomesSessionBean.getRegionType().equals(RegionType.NORMAL.getValue())) {
            em = this.emf.createEntityManager();
        }
        else {
            em = this.emfStorage.createEntityManager();
        }

        try {
            tran = em.getTransaction();
            tran.begin();
            em.setFlushMode(FlushModeType.AUTO);

            // １．権限ID有無チェック
            // ２．権限情報の取得
            // ３．ユーザに委託された階層を元にした権限判定
            // ４．ユーザに委託された役割を元にした権限判定
            // ５．ユーザに委託された権限を元にした権限判定
            if (certType.equals(CertificateType.APPROVE_PRIVILEGE_CHECK)) {
                isPrivilege = this.judgePersonsLicense(checkUserId, privilegeId, checkSiteCode, checkOrderProcessCode,
                        checkWorkProcessCode);
            }
            // ６．ユーザ委託代替作業権限を元にした権限判定
            if (certType.equals(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK)) {
                isPrivilege = this.judgePersonsLicenseSubstitute(checkUserId, privilegeId, checkSiteCode,
                        checkOrderProcessCode, checkWorkProcessCode);
            }
            this.insertHistCertification(certInfo, userInfo, isPrivilege, certType, checkUserId, em);
            tran.commit();
        }
        catch (GnomesException e) {
            if (!Objects.isNull(tran)) {
                // ロールバック
                tran.rollback();
            }
            throw e;
        }
        catch (Exception e) {
            if (!Objects.isNull(tran)) {
                // ロールバック
                tran.rollback();
            }
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = gnomesExceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            throw ex;
        }
        finally {
            if (!Objects.isNull(em)) {
                em.close();
            }
            em = null;
        }
        return isPrivilege;
    }

    /**
     * ログインユーザ作業権限チェック(作業場所含む)
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @param certType 認証区分
     * @return 権限有無
     * @throws GnomesAppException
     */
    public Boolean judgeLoginUserLicenseAddWorkCell(String privilegeId, String siteCode, String orderProcessCode,
            String workProcessCode, String workCellCode, CertificateType certType) throws GnomesAppException
    {

        // 権限有無
        boolean isPrivilege = false;

        // 前処理
        // チェック対象ユーザー（セッション.ユーザID）
        String checkUserId = gnomesSessionBean.getUserId();

        // 拠点コード
        String checkSiteCode = siteCode;
        // 指図工程コード
        String checkOrderProcessCode = orderProcessCode;
        // 作業工程コード
        String checkWorkProcessCode = workProcessCode;
        // 作業場所コード
        String checkWorkCellCode = workCellCode;

        // 拠点コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkSiteCode)) {
            checkSiteCode = CommonConstants.SITE_CODE_ALL;
        }

        // 指図工程コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkOrderProcessCode)) {
            checkOrderProcessCode = CommonConstants.ORDER_PROCESS_CODE_ALL;
        }

        // 作業工程コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkWorkProcessCode)) {
            checkWorkProcessCode = CommonConstants.WORK_PROCESS_CODE_ALL;
        }

        // 作業場所コードが設定されていない場合、マスターコードを設定
        if (StringUtil.isNullOrEmpty(checkWorkCellCode)) {
            checkWorkCellCode = CommonConstants.WORK_CELL_CODE_ALL;
        }

        // ３．認証用データ取得処理
        CertInfo certInfo = new CertInfo();
        certInfo.setUserId(checkUserId);
        certInfo.setPassword(null);
        certInfo.setComputerId(gnomesSessionBean.getComputerId());
        certInfo.setLocalId(null);
        certInfo.setSiteCode(null);
        // パスワード期限確認処理を行わない
        certInfo.setIsChangePassword(0);

        // ユーザ情報
        UserInfo userInfo = new UserInfo();
        userInfo.setIpAddress(gnomesSessionBean.getIpAddress());
        userInfo.setUserId(checkUserId);
        userInfo.setUserName(gnomesSessionBean.getUserName());
        userInfo.setComputerName(gnomesSessionBean.getComputerName());
        EntityTransaction tran = null;

        // エンティティマネージャの取得
        EntityManager em;
        if (gnomesSessionBean.getRegionType().equals(RegionType.NORMAL.getValue())) {
            em = this.emf.createEntityManager();
        }
        else {
            em = this.emfStorage.createEntityManager();
        }

        try {
            tran = em.getTransaction();
            tran.begin();
            em.setFlushMode(FlushModeType.AUTO);

            // １．権限ID有無チェック
            // ２．権限情報の取得
            // ３．ユーザに委託された階層を元にした権限判定
            // ４．ユーザに委託された役割を元にした権限判定
            // ５．ユーザに委託された権限を元にした権限判定
            if (certType.equals(CertificateType.APPROVE_PRIVILEGE_CHECK)) {
                isPrivilege = this.judgePersonsLicenseAddWorkCell(checkUserId, privilegeId, checkSiteCode,
                        checkOrderProcessCode, checkWorkProcessCode, checkWorkCellCode);
            }
            // ６．ユーザ委託代替作業権限を元にした権限判定
            if (certType.equals(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK)) {
                isPrivilege = this.judgePersonsLicenseSubstituteAddWorkCell(checkUserId, privilegeId, checkSiteCode,
                        checkOrderProcessCode, checkWorkProcessCode, checkWorkCellCode);
            }
            this.insertHistCertification(certInfo, userInfo, isPrivilege, certType, checkUserId, em);
            tran.commit();
        }
        catch (GnomesException e) {
            if (!Objects.isNull(tran)) {
                // ロールバック
                tran.rollback();
            }
            throw e;
        }
        catch (Exception e) {
            if (!Objects.isNull(tran)) {
                // ロールバック
                tran.rollback();
            }
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = gnomesExceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            throw ex;
        }
        finally {
            if (!Objects.isNull(em)) {
                em.close();
            }
            em = null;
        }
        return isPrivilege;
    }

    /**
     * 作業権限チェック
     * @param userId ユーザID
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    private Boolean judgePersonsLicense(String userId, String privilegeId, String siteCode, String orderProcessCode,
            String workProcessCode) throws GnomesAppException
    {

        boolean isPrivilege = false;

        String checkUserId = userId;
        if (checkUserId == null || checkUserId.equals("")) {
            checkUserId = req.getUserId();
        }

        // １．権限ID有無チェック
        if (privilegeId == null || privilegeId.isEmpty()) {
            return true;
        }

        //        [2019/07/30 浜本記載] 注意）権限情報を調べる関数が必要？
        // ２．権限情報の取得
        //        MstrPrivilege mstrPrivilege = mstrPrivilegeDao.getMstrPrivilege(privilegeId);
        //        if (mstrPrivilege == null) {
        //            return true;
        //        }

        // ３．ユーザに委託された階層を元にした権限判定
        // ４．ユーザに委託された役割を元にした権限判定
        // ５．ユーザに委託された権限を元にした権限判定
        isPrivilege = judgePersonLicenseDao.judgePersonLicense(userId, privilegeId, siteCode, orderProcessCode,
                workProcessCode);
        if (isPrivilege) {
            return true;
        }
        return false;
    }

    /**
     * 作業権限チェック(作業場所含む)
     * @param userId ユーザID
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    private Boolean judgePersonsLicenseAddWorkCell(String userId, String privilegeId, String siteCode,
            String orderProcessCode, String workProcessCode, String workCellCode) throws GnomesAppException
    {

        boolean isPrivilege = false;

        String checkUserId = userId;
        if (checkUserId == null || checkUserId.equals("")) {
            checkUserId = req.getUserId();
        }

        // １．権限ID有無チェック
        if (privilegeId == null || privilegeId.isEmpty()) {
            return true;
        }

        //        [2019/07/30 浜本記載] 注意）権限情報を調べる関数が必要？
        // ２．権限情報の取得
        //        MstrPrivilege mstrPrivilege = mstrPrivilegeDao.getMstrPrivilege(privilegeId);
        //        if (mstrPrivilege == null) {
        //            return true;
        //        }

        // ３．ユーザに委託された階層を元にした権限判定
        // ４．ユーザに委託された役割を元にした権限判定
        // ５．ユーザに委託された権限を元にした権限判定
        isPrivilege = judgePersonLicenseDao.judgePersonLicenseAddWorkCell(userId, privilegeId, siteCode,
                orderProcessCode, workProcessCode, workCellCode);
        if (isPrivilege) {
            return true;
        }
        return false;
    }

    /**
     * ユーザ委託代替作業権限を元にした権限判定
     * @param checkUserId ユーザID
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    private boolean judgePersonsLicenseSubstitute(String checkUserId, String privilegeId, String siteCode,
            String orderProcessCode, String workProcessCode) throws GnomesAppException
    {

        // ユーザ委託代替作業権限からユーザIDを取得し権限チェック
        boolean isPrivilege = judgePersonLicenseDao.judgePersonLicenseSubstitute(checkUserId, privilegeId, new Date(),
                siteCode, orderProcessCode, workProcessCode);
        if (isPrivilege) {
            return true;
        }

        return false;
    }

    /**
     * ユーザ委託代替作業権限を元にした権限判定(作業場所含む)
     * @param checkUserId ユーザID
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    private boolean judgePersonsLicenseSubstituteAddWorkCell(String checkUserId, String privilegeId, String siteCode,
            String orderProcessCode, String workProcessCode, String workCellCode) throws GnomesAppException
    {

        // ユーザ委託代替作業権限からユーザIDを取得し権限チェック
        boolean isPrivilege = judgePersonLicenseDao.judgePersonLicenseSubstituteAddWorkCell(checkUserId, privilegeId,
                new Date(), siteCode, orderProcessCode, workProcessCode, workCellCode);
        if (isPrivilege) {
            return true;
        }

        return false;
    }

    /**
     * パーツ権限結果情報設定
     * @param formBean 画面セキュリティビーン
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void setScreenPrivilege(IScreenPrivilegeBean formBean) throws GnomesAppException
    {

        List<PartsPrivilegeResultInfo> resultList = new ArrayList<PartsPrivilegeResultInfo>();

        // 画面IDを元に画面ボタンマスタを取得
        List<MstrScreenButton> mstrScreenButtonList = mstrScreenButtonDao.getMstrScreenButton(req.getScreenId());

        // ユーザIDを取得
        String userId = req.getUserId();
        // 画面IDを取得
        String screenId = req.getScreenId();
        // 拠点コードを取得
        String siteCode = this.gnomesSessionBean.getSiteCode();
        // 指図工程コードを取得
        String orderProcessCode = this.gnomesSessionBean.getOrderProcessCode();
        // 作業工程コードを取得
        String workProcessCode = this.gnomesSessionBean.getWorkProcessCode();
        // 作業場所コードを取得
        String workCellCode = this.gnomesSessionBean.getWorkCellCode();

        // ぞれぞれ値が取れなかったら "-" が割りつく
        if (StringUtil.isNullOrEmpty(siteCode)) {
            //拠点コードを取得できない場合、マスターコードを設定
            siteCode = CommonConstants.SITE_CODE_ALL;
        }
        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            //指図工程コードを取得できない場合、マスターコードを設定
            orderProcessCode = CommonConstants.ORDER_PROCESS_CODE_ALL;
        }
        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            //作業工程コードを取得できない場合、マスターコードを設定
            workProcessCode = CommonConstants.WORK_PROCESS_CODE_ALL;
        }
        if (StringUtil.isNullOrEmpty(workCellCode)) {
            //作業場所コードを取得できない場合、マスターコードを設定
            workCellCode = CommonConstants.WORK_CELL_CODE_ALL;
        }

        // 画面に権限があるボタンリスト取得
        List<MstrScreenButtonDto> privilegeList = judgePersonLicenseDao.getMstrScreenButtonList(userId, screenId,
                siteCode, orderProcessCode, workProcessCode, workCellCode);

        for (MstrScreenButton dataMstrScreenButton : mstrScreenButtonList) {
            PartsPrivilegeResultInfo result = new PartsPrivilegeResultInfo();

            String privilegeId = null;
            if (dataMstrScreenButton != null && !StringUtil.isNullOrEmpty(dataMstrScreenButton.getPrivilege_id())) {
                privilegeId = dataMstrScreenButton.getPrivilege_id();
            }

            // 未使用項目削除
            //            result.setTagId(result.getTagId());
            //            result.setScreenId(dispScreenId);
            if (!StringUtil.isNullOrEmpty(dataMstrScreenButton.getButton_id())) {
                result.setButtonId(dataMstrScreenButton.getButton_id());
            }

            if (!StringUtil.isNullOrEmpty(dataMstrScreenButton.getOperation_content())) {
                result.setButtonName(dataMstrScreenButton.getOperation_content());
            }

            result.setPrivilegeId(privilegeId);

            // 権限有無の判定
            Boolean hasPrivilege = true;
            if (!StringUtil.isNullOrEmpty(privilegeId)) {
                if (privilegeList.stream().noneMatch(mstrScreenButtonDto -> mstrScreenButtonDto.getButtonId().equals(
                        dataMstrScreenButton.getButton_id()))) {
                    hasPrivilege = false;
                }
            }

            // 権限有無の設定
            result.setPrivilege(hasPrivilege);

            // ボタン表示区分を設定
            result.setDisplayDiv(dataMstrScreenButton.getDisplay_div());

            // 2重チェックを行なうか否か
            if (dataMstrScreenButton.getCheck_double_submit_flag() != null && dataMstrScreenButton.getCheck_double_submit_flag() == PrivilegeIsCheckDoubleSubmit.PrivilegeIsCheckDoubleSubmit_True.getValue()) {
                result.setIsCheckDoubleSubmit(true);
            }
            else {
                result.setIsCheckDoubleSubmit(false);
            }

            // 確認ダイアログの表示有無
            if (dataMstrScreenButton.getDisplay_confirm_flag() != null && dataMstrScreenButton.getDisplay_confirm_flag() == PrivilegeDisplayConfirmFlag.PrivilegeDisplayConfirmFlag_ON.getValue()) {
                result.setDisplayConfirmFlag(PrivilegeDisplayConfirmFlag.PrivilegeDisplayConfirmFlag_ON);
                // 確認メッセージ
                result.setConfirmMessage(MessagesHandler.getString(dataMstrScreenButton.getConfirm_message_no()));
            }
            else {
                result.setDisplayConfirmFlag(PrivilegeDisplayConfirmFlag.PrivilegeDisplayConfirmFlag_OFF);
            }

            // 完了ダイアログの表示有無
            if (dataMstrScreenButton.getDisplay_finish_flag() != null && dataMstrScreenButton.getDisplay_finish_flag() == PrivilegeDisplayFinishFlag.PrivilegeDisplayFinishFlag_ON.getValue()) {
                result.setDisplayFinishFlag(PrivilegeDisplayFinishFlag.PrivilegeDisplayFinishFlag_ON);

                // 完了メッセージNoが設定されている場合
                if (!StringUtil.isNullOrEmpty(dataMstrScreenButton.getFinish_message_no())) {
                    // 完了メッセージNo
                    result.setCompletionMessage(dataMstrScreenButton.getFinish_message_no());
                }
                else {
                    // 完了メッセージ（デフォルト）
                    result.setCompletionMessage(GnomesMessagesConstants.MV01_0030);
                }
            }
            else {
                result.setDisplayFinishFlag(PrivilegeDisplayFinishFlag.PrivilegeDisplayFinishFlag_OFF);
            }

            // データ入力時確認ダイアログの表示有無
            if (dataMstrScreenButton.getDisplay_discard_change_flag() != null && dataMstrScreenButton.getDisplay_discard_change_flag() == PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_ON.getValue()) {
                result.setDisplayDiscardChangeFlag(
                        PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_ON);
            }
            else {
                result.setDisplayDiscardChangeFlag(
                        PrivilegeDisplayDiscardChangeFlag.PrivilegeDisplayDiscardChangeFlag_OFF);
            }

            // ユーザ認証の有無
            PrivilegeIsnecessaryPassword isNecessaryPassword = PrivilegeIsnecessaryPassword.getEnum(
                    dataMstrScreenButton.getCertificate_function_div());
            result.setIsNecessaryPassword(isNecessaryPassword);

            resultList.add(result);
        }

        formBean.setPartsPrivilegeResultInfo(resultList);

    }

    /**
     * 操作権限チェック
     * @param loginUserId ユーザID
     * @param screenId 画面ID
     * @param buttonId ボタンID
     * @param partsPrivilegeList パーツ権限結果情報リスト
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonsLicenseCheck(String loginUserId, String screenId, String buttonId,
            List<PartsPrivilegeResultInfo> partsPrivilegeList, String siteCode, String orderProcessCode,
            String workProcessCode, String workCellCode) throws Exception
    {

        // 取得した GnomesSessionBean のユーザID と入力パラメータのユーザID が一致しない場合
        if (!gnomesSessionBean.getUserId().equals(loginUserId)) {
            // ME01.0033:「ログインユーザのみ認証可能です。」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0033);
            throw ex;
        }

        // 画面ID、ボタンIDが空の場合、操作権限有り
        if (StringUtil.isNullOrEmpty(buttonId) || StringUtil.isNullOrEmpty(screenId)) {
            logHelper.fine(this.logger, null, null,
                    "BLSecurity.judgePersonsLicenseCheck: loginUserId=[" + loginUserId + "] screenId =[" + screenId + "] buttonId = [" + buttonId + "] ---- Do nothing because the button ID is null");
            return true;
        }

        // 画面ボタンマスタの取得
        MstrScreenButton dataMstrScreenButton = mstrScreenButtonDao.getMstrScreenButton(screenId, buttonId);

        // 取得した画面ボタンマスタの権限IDが空の場合、操作権限有り
        if (dataMstrScreenButton == null || StringUtil.isNullOrEmpty(dataMstrScreenButton.getPrivilege_id())) {
            return true;
        }

        // ボタンの権限チェック
        if (!Objects.isNull(partsPrivilegeList)) {
            for (PartsPrivilegeResultInfo partsPrivilege : partsPrivilegeList) {
                if (buttonId.equals(partsPrivilege.getButtonId())) {

                    // 権限有無
                    boolean isPrivilege = this.judgePersonsLicenseAddWorkCell(loginUserId,
                            partsPrivilege.getPrivilegeId(), siteCode, orderProcessCode, workProcessCode, workCellCode);

                    // 操作権限が無い場合
                    if (!isPrivilege) {
                        // ユーザ委託代替作業権限を元にした権限判定
                        isPrivilege = this.judgePersonsLicenseSubstituteAddWorkCell(loginUserId,
                                partsPrivilege.getPrivilegeId(), siteCode, orderProcessCode, workProcessCode,
                                workCellCode);
                    }

                    // 押下されたボタンの権限がない場合
                    if (!isPrivilege) {
                        // ME01.0049:「操作権限がないため、処理が行えません。({0}) 」
                        GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0049, partsPrivilege.getButtonId());
                        throw ex;
                    }
                    break;
                }
            }
        }

        return true;
    }

    /**
     * 認証操作権限チェック（ユーザ認証有り）
     * @param loginUserId 作業者ユーザID
     * @param loginUserPassword 作業者パスワード
     * @param certUserId 認証者ユーザID
     * @param certUserPassword 認証者パスワード
     * @param screenId 画面ID
     * @param buttonId ボタンID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @param certType 認証区分
     * @param em エンティティマネージャ
     * @return 権限有無
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonsLicenseCertCheck(String loginUserId, String loginUserPassword, String certUserId,
            String certUserPassword, String screenId, String buttonId, String siteCode, String orderProcessCode,
            String workProcessCode, String workCellCode, CertificateType certType,
            List<PartsPrivilegeResultInfo> partsPrivilegeList, EntityManager em) throws Exception
    {

        // １．入力チェック
        // パラメータ.作業者ユーザID が NULL または空文字の場合
        if (loginUserId == null || loginUserId.equals("")) {
            // ME01.0018:「ユーザーID、パスワードを入力してください。」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0018);
            throw ex;
        }

        if (certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
            // パラメータ.認証者ユーザID が NULL または空文字の場合、
            if (certUserId == null || certUserId.equals("")) {
                // ME01.0031:「承認者のユーザーID、パスワードを入力してください。」
                GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0031);
                throw ex;
            }
            // パラメータ.作業者ユーザID = パラメータ.認証者ユーザID の場合
            else if (loginUserId.equals(certUserId)) {
                // ME01.0032:「作業者と承認者の2名で確認を行ってください。」
                GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0032);
                throw ex;
            }

        }

        // ２．認証済ユーザチェック
        // 取得した GnomesSessionBean のユーザID と入力パラメータのユーザID が一致しない場合
        if (!gnomesSessionBean.getUserId().equals(loginUserId)) {
            // ME01.0033:「ログインユーザのみ認証可能です。」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0033);
            throw ex;
        }

        // ３．認証用データ取得処理
        CertInfo certInfo = new CertInfo();
        certInfo.setUserId(loginUserId);
        certInfo.setPassword(loginUserPassword);
        certInfo.setComputerId(gnomesSessionBean.getComputerId());
        certInfo.setLocalId(gnomesSessionBean.getLocaleId());
        certInfo.setSiteCode(null);
        // パスワード期限確認処理を行わない
        certInfo.setIsChangePassword(0);

        //パスワードがHash値であることを指定
        certInfo.setPasswordIsHash(true);

        // ユーザ情報
        UserInfo userInfo = new UserInfo();

        userInfo.setIpAddress(gnomesSessionBean.getIpAddress());
        userInfo.setUserId(loginUserId);
        userInfo.setUserName(gnomesSessionBean.getUserName());
        userInfo.setComputerName(gnomesSessionBean.getComputerName());

        // ログイン認証か否か
        Boolean isLoginCertify = false;

        // 出力パラメータ 認証成功か否か
        Holder<Integer> isSuccess = new Holder<Integer>(0);

        // 出力パラメータ パスワード変更必要か否か
        Holder<Integer> isChangePassword = new Holder<Integer>(0);

        // 出力パラメータ メッセージ情報
        Holder<String> message = new Holder<String>("");

        getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        certInfo.setMessage(message.value);
        certInfo.setIsSuccess(isSuccess.value);

        // アカウントロックチェック
        String certMessage = checkLockAccount(isSuccess.value, em);
        if (!StringUtil.isNullOrEmpty(certMessage)) {
            // メッセージ設定
            certInfo.setMessage(certMessage);
        }

        // 認証成功か否か＝0：認証失敗の場合
        if (isSuccess.value == 0) {
            // 認証情報の登録
            this.insertHistCertification(certInfo, userInfo, ConverterUtils.IntTobool(isSuccess.value), certType,
                    certUserId, em);
            return false;
        }

        boolean isLdapLoginSuccess;
        Hashtable<String, String> moduleOption;

        // LDAP認証時
        if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.LDAP)) {
            GnomesLdapLoginModule ldapLoginModule = new GnomesLdapLoginModule();
            moduleOption = gnomesSystemBean.getModuleOptionInfo();
            isLdapLoginSuccess = ldapLoginModule.validateUser(certInfo.getUserId(), certInfo.getPassword(),
                    moduleOption);

            if (!isLdapLoginSuccess) {
                // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);

                certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198));
                // 認証情報の登録
                this.insertHistCertification(certInfo, userInfo, isLdapLoginSuccess, certType, certUserId, em);

                return false;
            }
        }

        boolean result = judgePersonsLicenseCheck(loginUserId, screenId, buttonId, partsPrivilegeList, siteCode,
                orderProcessCode, workProcessCode, workCellCode);

        // 認証情報の登録
        this.insertHistCertification(certInfo, userInfo, result, certType, certUserId, em);

        if (result) {
            // ログ出力メッセージ： 認証処理成功  (ユーザーID： {0})   (MG01.0002）
            this.logHelper.fine(this.logger, null, null, MessagesHandler.getString(GnomesLogMessageConstants.MG01_0002,
                    loginUserId));
        }
        else {
            // ログ出力メッセージ： 認証処理エラー
            this.logHelper.fine(this.logger, null, null, certInfo.getMessage());
        }
        // 入力パラメータ.認証区分がダブル権限認証入力以外の場合
        if (!certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
            return result;
        }

        // ４．（承認者）認証用データ取得処理
        certInfo.setUserId(certUserId);
        certInfo.setPassword(certUserPassword);

        // （承認者）ユーザ情報の取得
        InfoUser certInfoUser;
        certInfoUser = infoUserDao.getInfoUser(certUserId, em);

        // 存在チェック
        // ユーザが取得できない場合、エラーを設定し終了する。
        if (certInfoUser == null) {
            // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);
            certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198));
            certInfo.setIsSuccessChange(false);
            return false;
        }

        isSuccess.value = 0;
        this.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        certInfo.setMessage(message.value);
        certInfo.setIsSuccess(isSuccess.value);

        // アカウントロックチェック
        String certApproverMessage = checkLockAccountApprover(isSuccess.value, certInfoUser, em);
        if (!StringUtil.isNullOrEmpty(certApproverMessage)) {
            // メッセージ設定
            certInfo.setMessage(certApproverMessage);
        }

        // 認証成功か否か＝0：認証失敗の場合
        if (isSuccess.value == 0) {
            certInfo.setUserId(loginUserId);
            // 認証情報の登録
            this.insertHistCertification(certInfo, userInfo, ConverterUtils.IntTobool(isSuccess.value), certType,
                    certUserId, em);

            return false;
        }

        // LDAP認証時
        if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.LDAP)) {
            GnomesLdapLoginModule ldapLoginModule = new GnomesLdapLoginModule();
            moduleOption = gnomesSystemBean.getModuleOptionInfo();
            isLdapLoginSuccess = ldapLoginModule.validateUser(certInfo.getUserId(), certInfo.getPassword(),
                    moduleOption);

            if (!isLdapLoginSuccess) {
                // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);
                certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198));
                certInfo.setUserId(loginUserId);
                // （承認者）ユーザ情報:認証エラー回数の更新
                certInfoUser.setCertify_failure_times(certInfoUser.getCertify_failure_times() + 1);
                // 認証情報の登録
                this.insertHistCertification(certInfo, userInfo, isLdapLoginSuccess, certType, certUserId, em);
                return false;
            }
        }

        boolean isSuccessDoubleCheck = judgePersonsLicenseDoubleCheck(certUserId, screenId, buttonId, siteCode,
                orderProcessCode, workProcessCode, workCellCode);
        // ダブル認証チェック失敗時
        if (!isSuccessDoubleCheck) {
            // ME01.0034:「認証者の権限を確認してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0034);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0034));
            certInfo.setUserId(loginUserId);
            // （承認者）ユーザ情報:認証エラー回数の更新
            certInfoUser.setCertify_failure_times(certInfoUser.getCertify_failure_times() + 1);
            // 認証情報の登録
            this.insertHistCertification(certInfo, userInfo, isSuccessDoubleCheck, certType, certUserId, em);

            return false;
        }

        // ログ出力メッセージ： ダブル認証処理成功  (ユーザーID： {0} 、認証者：{1})   (MG01.0003）
        this.logHelper.fine(this.logger, null, null, MessagesHandler.getString(GnomesLogMessageConstants.MG01_0003,
                loginUserId, certUserId));
        certInfo.setUserId(loginUserId);
        // （承認者）ユーザ情報:認証エラー回数の0に更新
        certInfoUser.setCertify_failure_times(0);
        // 認証情報の登録
        this.insertHistCertification(certInfo, userInfo, isSuccessDoubleCheck, certType, certUserId, em);

        // 参照用認証者ユーザIDを設定
        systemFormBean.setCheckedCertUserId(systemFormBean.getCertUserId());
        systemFormBean.setCertUserId(null);

        return true;
    }

    /**
     * ダブル認証権限チェック
     * @param certUserId ダブルチェック承認者
     * @param screenId 画面ID
     * @param buttonId ボタンID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public Boolean judgePersonsLicenseDoubleCheck(String certUserId, String screenId, String buttonId, String siteCode,
            String orderProcessCode, String workProcessCode, String workCellCode) throws Exception
    {

        // １．権限情報の取得
        // 画面ボタンマスタの取得
        MstrScreenButton dataMstrScreenButton = mstrScreenButtonDao.getMstrScreenButton(screenId, buttonId);

        // 存在チェック
        if (dataMstrScreenButton == null || StringUtil.isNullOrEmpty(dataMstrScreenButton.getPrivilege_id())) {
            // 正常終了
            return true;
        }

        // 権限マスタの取得
        //[2019/07/30 浜本記載] 注意）権限マスターになかったら自動権限有にするのを省いたらどうなるか？
        //        MstrPrivilege datasPrivilege = mstrPrivilegeDao.getMstrPrivilege(dataMstrScreenButton.getPrivilege_id());
        //
        //        // 存在チェック
        //        if (datasPrivilege == null) {
        //            // 正常終了
        //            return true;
        //        }

        //        // ２．ダブルチェック確認者に必要な権限を取得
        //        List<MstrDoubleCheck> datasDoublecheck = mstrDoubleCheckDao.getMstrDoubleCheck(datasPrivilege);
        //
        //        // 存在チェック
        //        if (datasDoublecheck == null || datasDoublecheck.size() == 0 ) {
        //            // 正常終了
        //            return true;
        //        }

        // ３．判定処理
        boolean isPrivilege = judgePersonLicenseDao.judgePersonLicenseDoubleCheckAddWorkCell(certUserId,
                dataMstrScreenButton.getPrivilege_id(), siteCode, orderProcessCode, workProcessCode, workCellCode);
        if (!isPrivilege) {
            return false;
        }

        return true;
    }

    /**
     * 変更パスワードの為、認証操作権限チェック（ユーザ認証有り）
     * @param certInfo 認証情報
     * @param buttonId ボタンID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param certType 認証区分
     * @param partsPrivilegeList
     * @param em エンティティマネージャ
     * @return 権限有無
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonsLicenseCertForChangePassword(CertInfo certInfo, String buttonId, String siteCode,
            String orderProcessCode, String workProcessCode, String workCellCode, CertificateType certType,
            List<PartsPrivilegeResultInfo> partsPrivilegeList, EntityManager em) throws Exception
    {

        // １．入力チェック
        // パラメータ.作業者ユーザID が NULL または空文字の場合
        if (certInfo.getLoginUserId() == null || certInfo.getLoginUserId().equals("")) {
            // ME01.0018:「ユーザーID、パスワードを入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0018);
            certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0018));
            certInfo.setIsSuccessChange(false);
            return false;
        }

        if (certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
            // パラメータ.認証者ユーザID が NULL または空文字の場合、
            if (certInfo.getCertUserId() == null || certInfo.getCertUserId().equals("")) {
                // ME01.0031:「承認者のユーザーID、パスワードを入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0031);
                certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0031));
                certInfo.setIsSuccessChange(false);
                return false;
            }
            // パラメータ.作業者ユーザID = パラメータ.認証者ユーザID の場合
            else if (certInfo.getLoginUserId().equals(certInfo.getCertUserId())) {
                // ME01.0032:「作業者と承認者の2名で確認を行ってください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0032);
                certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0032));
                certInfo.setIsSuccessChange(false);
                return false;
            }
        }

        // ２．認証済ユーザチェック
        // 取得した GnomesSessionBean のユーザID と入力パラメータのユーザID が一致しない場合
        if (!gnomesSessionBean.getUserId().equals(certInfo.getLoginUserId())) {
            // ME01.0033:「ログインユーザのみ認証可能です。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0033);
            certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0033));
            certInfo.setIsSuccessChange(false);
            return false;
        }

        // ３．認証用データ取得処理
        certInfo.setUserId(certInfo.getLoginUserId());
        certInfo.setPassword(certInfo.getLoginUserPassword());
        certInfo.setComputerId(gnomesSessionBean.getComputerId());
        certInfo.setLocalId(null);
        certInfo.setSiteCode(null);
        // パスワード期限確認処理を行わない
        certInfo.setIsChangePassword(0);

        // ユーザ情報
        UserInfo userInfo = new UserInfo();

        userInfo.setIpAddress(gnomesSessionBean.getIpAddress());
        userInfo.setUserId(certInfo.getLoginUserId());
        userInfo.setUserName(gnomesSessionBean.getUserName());
        userInfo.setComputerName(gnomesSessionBean.getComputerName());

        // ログイン認証か否か
        Boolean isLoginCertify = false;

        // 出力パラメータ 認証成功か否か
        Holder<Integer> isSuccess = new Holder<Integer>(0);

        // 出力パラメータ パスワード変更必要か否か
        Holder<Integer> isChangePassword = new Holder<Integer>(0);

        // 出力パラメータ メッセージ情報
        Holder<String> message = new Holder<String>("");

        getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        certInfo.setMessage(message.value);
        certInfo.setIsSuccess(isSuccess.value);

        // アカウントロックチェック
        String certMessage = checkLockAccount(isSuccess.value, em);
        if (!StringUtil.isNullOrEmpty(certMessage)) {
            // メッセージ設定
            certInfo.setMessage(certMessage);
        }
        // 認証成功か否か＝0：認証失敗の場合
        if (isSuccess.value == 0) {
            // 認証情報の登録
            this.insertHistCertificationForChangePassword(certInfo, userInfo, ConverterUtils.IntTobool(isSuccess.value),
                    certType, buttonId, em);
            return false;
        }

        boolean isLdapLoginSuccess;
        Hashtable<String, String> moduleOption;

        // LDAP認証時
        if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.LDAP)) {
            GnomesLdapLoginModule ldapLoginModule = new GnomesLdapLoginModule();
            moduleOption = gnomesSystemBean.getModuleOptionInfo();
            isLdapLoginSuccess = ldapLoginModule.validateUser(certInfo.getUserId(), certInfo.getPassword(),
                    moduleOption);

            if (!isLdapLoginSuccess) {
                // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);

                certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198));
                // 認証情報の登録
                this.insertHistCertificationForChangePassword(certInfo, userInfo, isLdapLoginSuccess, certType,
                        buttonId, em);

                return false;
            }
        }

        boolean result = judgePersonsLicenseCheck(certInfo.getLoginUserId(), certInfo.getScreenId(), buttonId,
                partsPrivilegeList, siteCode, orderProcessCode, workProcessCode, workCellCode);

        // 認証情報の登録
        this.insertHistCertificationForChangePassword(certInfo, userInfo, result, certType, buttonId, em);

        if (result) {
            // ログ出力メッセージ： 認証処理成功  (ユーザーID： {0})   (MG01.0002）
            this.logHelper.fine(this.logger, null, null, MessagesHandler.getString(GnomesLogMessageConstants.MG01_0002,
                    certInfo.getLoginUserId()));
        }
        else {
            // ログ出力メッセージ： 認証処理エラー
            this.logHelper.fine(this.logger, null, null, certInfo.getMessage());
        }
        // 入力パラメータ.認証区分がダブル権限認証入力以外の場合
        if (!certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
            return result;
        }

        // ４．（承認者）認証用データ取得処理
        certInfo.setUserId(certInfo.getCertUserId());
        certInfo.setPassword(certInfo.getCertUserPassword());

        // （承認者）ユーザ情報の取得
        InfoUser certInfoUser;
        certInfoUser = infoUserDao.getInfoUser(certInfo.getCertUserId(), em);

        // 存在チェック
        // ユーザが取得できない場合、エラーを設定し終了する。
        if (certInfoUser == null) {
            // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);
            certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198));
            certInfo.setIsSuccessChange(false);
            return false;
        }

        isSuccess.value = 0;
        this.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        certInfo.setMessage(message.value);
        certInfo.setIsSuccess(isSuccess.value);

        // アカウントロックチェック
        String certApproverMessage = checkLockAccountApprover(isSuccess.value, certInfoUser, em);
        if (!StringUtil.isNullOrEmpty(certApproverMessage)) {
            // メッセージ設定
            certInfo.setMessage(certApproverMessage);
        }

        // 認証成功か否か＝0：認証失敗の場合
        if (isSuccess.value == 0) {
            certInfo.setUserId(certInfo.getLoginUserId());
            // 認証情報の登録
            this.insertHistCertificationForChangePassword(certInfo, userInfo, ConverterUtils.IntTobool(isSuccess.value),
                    certType, buttonId, em);

            return false;
        }

        // LDAP認証時
        if (gnomesSystemBean.getLoginModuleType().equals(CommonConstants.LDAP)) {
            GnomesLdapLoginModule ldapLoginModule = new GnomesLdapLoginModule();
            moduleOption = gnomesSystemBean.getModuleOptionInfo();
            isLdapLoginSuccess = ldapLoginModule.validateUser(certInfo.getUserId(), certInfo.getPassword(),
                    moduleOption);

            if (!isLdapLoginSuccess) {
                // ME01.0198:「ユーザーID、パスワードを正しく入力してください。」
                MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0198);
                certInfo.setMessage(MessagesHandler.getString(GnomesMessagesConstants.ME01_0198));
                certInfo.setUserId(certInfo.getLoginUserId());
                // （承認者）ユーザ情報:認証エラー回数の更新
                certInfoUser.setCertify_failure_times(certInfoUser.getCertify_failure_times() + 1);
                // 認証情報の登録
                this.insertHistCertificationForChangePassword(certInfo, userInfo, isLdapLoginSuccess, certType,
                        buttonId, em);
                return false;
            }
        }

        boolean isSuccessDoubleCheck = judgePersonsLicenseDoubleCheck(certInfo.getCertUserId(), certInfo.getScreenId(),
                buttonId, siteCode, orderProcessCode, workProcessCode, workCellCode);
        // ダブル認証チェック失敗時
        if (!isSuccessDoubleCheck) {
            // ME01.0034:「認証者の権限を確認してください。」
            MessagesHandler.setMessageNo(this.req, GnomesMessagesConstants.ME01_0034);
            certInfo.setMessage(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0034));
            certInfo.setUserId(certInfo.getLoginUserId());
            // （承認者）ユーザ情報:認証エラー回数の更新
            certInfoUser.setCertify_failure_times(certInfoUser.getCertify_failure_times() + 1);
            // 認証情報の登録
            this.insertHistCertificationForChangePassword(certInfo, userInfo, isSuccessDoubleCheck, certType, buttonId,
                    em);

            return false;
        }

        // ログ出力メッセージ： ダブル認証処理成功  (ユーザーID： {0} 、認証者：{1})   (MG01.0003）
        this.logHelper.fine(this.logger, null, null, MessagesHandler.getString(GnomesLogMessageConstants.MG01_0003,
                certInfo.getLoginUserId(), certInfo.getCertUserId()));
        certInfo.setUserId(certInfo.getLoginUserId());
        // （承認者）ユーザ情報:認証エラー回数の0に更新
        certInfoUser.setCertify_failure_times(0);
        // 認証情報の登録
        this.insertHistCertificationForChangePassword(certInfo, userInfo, isSuccessDoubleCheck, certType, buttonId, em);

        // 参照用認証者ユーザIDを設定
        // systemFormBean.setCheckedCertUserId(certInfo.getCertUserId());
        // systemFormBean.setCertUserId(null);

        return true;
    }

    /**
     * 変更パスワードの為、認証履歴登録
     * @param certInfo 認証情報
     * @param userInfo ユーザ情報
     * @param isSuccess 成功か否か
     * @param certType 認証区分
     * @param buttonId ボタンID
     * @param em エンティティマネージャ
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public void insertHistCertificationForChangePassword(CertInfo certInfo, UserInfo userInfo, boolean isSuccess,
            CertificateType certType, String buttonId, EntityManager em) throws GnomesAppException, ParseException
    {

        // 操作内容
        String operationContent;
        // NGフラグ
        int ngFlag;
        // NG詳細情報
        String ngDetail;

        // 認証成功か否か＝trueの場合
        if (isSuccess) {
            if (certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
                // ダブル認証処理成功 (ユーザーID：{0}、認証者：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5})
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0003,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId(), certInfo.getCertUserId(),
                        certInfo.getScreenId(), certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else if (certType.equals(CertificateType.PRIVILEGE_CHECK)) {
                // 認証処理成功（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0029,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), certInfo.getScreenId(),
                        certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else if (certType.equals(CertificateType.APPROVE_PRIVILEGE_CHECK)) {
                // 承認処理成功（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0034,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), certInfo.getScreenId(),
                        certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else if (certType.equals(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK)) {
                // 代替承認処理成功（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0035,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), certInfo.getScreenId(),
                        certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else {
                // 認証処理成功（処理：{0}、ユーザーID：{1}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0021,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId());
            }
            ngFlag = 0;
            ngDetail = null;
        }
        // 認証成功か否か＝    falseの場合
        else {
            if (certType.equals(CertificateType.DOUBLE_PRIVILEGE_CHECK)) {
                // ダブル認証処理失敗 (ユーザーID：{0}、認証者：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5})
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0023,
                        gnomesSessionBean.getUserLocale(), certInfo.getUserId(), certInfo.getCertUserId(),
                        certInfo.getScreenId(), certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else if (certType.equals(CertificateType.PRIVILEGE_CHECK)) {
                // 認証処理失敗（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0030,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), certInfo.getScreenId(),
                        certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else if (certType.equals(CertificateType.APPROVE_PRIVILEGE_CHECK)) {
                // 承認処理失敗（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0036,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), certInfo.getScreenId(),
                        certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else if (certType.equals(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK)) {
                // 代替承認失敗（処理：{0}、ユーザーID：{1}、画面ID：{2}、画面名：{3}、ボタンID：{4}、ボタン名：{5}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0037,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId(), certInfo.getScreenId(),
                        certInfo.getScreenName(), buttonId, certInfo.getChangePswBtnName());
            }
            else {
                // 認証処理失敗（処理：{0}、ユーザーID：{1}）
                operationContent = MessagesHandler.getString(GnomesMessagesConstants.MG01_0022,
                        gnomesSessionBean.getUserLocale(), ResourcesHandler.getString(certType.getValue(),
                                gnomesSessionBean.getUserLocale()), certInfo.getUserId());
            }

            ngFlag = 1;
            ngDetail = certInfo.getMessage();
        }

        // 認証履歴を登録する。
        HistCertificationWrite data = new HistCertificationWrite();

        String strUserNumber = certInfo.getUserId();
        // 従業員Noは最大20桁まで
        if (strUserNumber.length() > 20) {
            strUserNumber = strUserNumber.substring(0, 20);
        }

        // userIdとuserNameを設定する
        req.setUserId(userInfo.getUserId());
        req.setUserName(userInfo.getUserName());

        data.setHist_certification_key(UUID.randomUUID().toString());
        data.setOccur_datetime(CurrentTimeStamp.getSystemCurrentTimeStamp());
        data.setUser_number(strUserNumber);
        data.setClient_device_id(certInfo.getComputerId());
        data.setUser_name(req.getUserName());
        data.setUser_name(userInfo.getUserName());
        data.setClient_device_name(userInfo.getComputerName());
        data.setIp_address(userInfo.getIpAddress());
        data.setOperation_content(operationContent);
        data.setNg_flag(ngFlag);
        data.setNg_detail(ngDetail);
        data.setReq(req);
        histCertificationWriteDao.insert(data, em);
    }
    /**
     * 暗号化されたパスワードか従来パスワードかを判別してパスワード一致確認をする
     * @param certInfo
     * @param dataInfoUser
     * @return 一致:true 不一致:falase
     */
    private boolean passwordMatchWithHash(CertInfo certInfo, InfoUser dataInfoUser) {
        //パスワードが暗号化されていたら登録パスワードと一致確認する
        if(certInfo.isPasswordIsHash()) {
            return certInfo.getPassword().equals(dataInfoUser.getPassword());
        }
        return GnomesPasswordEncoder.matches(certInfo.getPassword(), dataInfoUser.getPassword());
    }
}