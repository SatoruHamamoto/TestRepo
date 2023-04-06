package com.gnomes.system.logic;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.internet.MimeUtility;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.MailNoticeStatus;
import com.gnomes.common.constants.CommonEnums.MessageCategory;
import com.gnomes.common.constants.CommonEnums.MessageLevel;
import com.gnomes.common.constants.CommonEnums.SendMailType;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.common.util.KeyStoreUtilities;
import com.gnomes.system.dao.MessageDao;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.dao.MstrMessageGroupDao;
import com.gnomes.system.dao.MstrPersonDao;
import com.gnomes.system.dao.MstrSiteDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.dao.TmpQueueMailNoticeDao;
import com.gnomes.system.data.MailInfoBean;
import com.gnomes.system.data.MailNoticeHistoryInfo;
import com.gnomes.system.entity.Message;
import com.gnomes.system.entity.MstrMessageDefine;
import com.gnomes.system.entity.MstrMessageGroup;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrSite;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.system.entity.TmpQueueMailNotice;
import com.gnomes.uiservice.ContainerRequest;

import gnomes_project.sendmail_0_1.SendMail;

/**
 * メール通知
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/13 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MailNotice implements Serializable
{

    /** ロガー */
    @Inject
    protected transient Logger      logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper   logHelper;

    /** メール通知キュー Dao */
    @Inject
    protected TmpQueueMailNoticeDao tmpQueueMailNoticeDao;

    /** メッセージ定義 Dao */
    @Inject
    protected MstrMessageDefineDao  mstrMessageDefineDao;

    /** メッセージグループ Dao */
    @Inject
    protected MstrMessageGroupDao   mstrMessageGroupDao;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao   mstrSystemDefineDao;

    /** ユーザ Dao */
    @Inject
    protected MstrPersonDao         mstrPersonDao;

    /** メッセージ情報 Dao */
    @Inject
    protected MessageDao            messageDao;

    /** エリアマスタ Dao */
    @Inject
    protected MstrSiteDao           mstrSiteDao;

    /** リクエストコンテナ */
    @Inject
    protected ContainerRequest      containerRequest;

    /** メール通知履歴 */
    @Inject
    protected MailNoticeHistory     mailNoticeHistory;

    /** メール情報 */
    @Inject
    protected MailInfoBean          mailInfoBean;

    /** キーストアユーティリティ */
    @Inject
    protected KeyStoreUtilities     keyStoreUtilities;

    @Inject
    protected GnomesEntityManager   em;

    /** メール通知状況. */
    private MailNoticeStatus        mailNoticeStatus;

    /** 原因 */
    private String                  cause;

    /** 定数：カンマ */
    private static final String     COMMA                     = ", ";

    /** 定数：テーブル名 */
    private static final String     TABLE_NAME                = "table_name：";

    /** 定数：カラム名 */
    private static final String     COLUMN_NAME               = "column_name：";

    /** 定数：値 */
    private static final String     VALUE                     = "value：";

    /** メールアドレスリストキー；To */
    private static final String     MAIL_ADDRESS_LIST_KEY_TO  = "To";

    /** メールアドレスリストキー；CC */
    private static final String     MAIL_ADDRESS_LIST_KEY_CC  = "CC";

    /** メールアドレスリストキー；BCC */
    private static final String     MAIL_ADDRESS_LIST_KEY_BCC = "Bcc";

    private static final String     className                 = "MailNotice";

    /**
     * メール通知.
     * <pre>
     * メール通知処理を行う。
     * </pre>
     */
    @GnomesTransactional
    public void notification()
    {

        // メール通知キューリスト(通知対象)取得
        List<TmpQueueMailNotice> tmpQueueMailNoticeList = this.tmpQueueMailNoticeDao.getTmpQueueMailNoticeListForNotNotice(
                em.getEntityManager());

        for (TmpQueueMailNotice mailNotice : tmpQueueMailNoticeList) {

            //キュー1件分のメール通知処理
            //データがない、失敗しても続ける(致命的な例外はロールバックする）
            mailNotifyQueueProc(mailNotice);

        }
    }

    /**
     *
     * 取得した1件分のメールキューに対するメール送信を実施
     *
     * @param mailNotice    メール送信キュー情報
     * @return              成功失敗
     */
    @ErrorHandling
    private void mailNotifyQueueProc(TmpQueueMailNotice mailNotice)
    {
        // メール通知状況
        this.mailNoticeStatus = MailNoticeStatus.Notified;
        // 原因
        this.cause = null;

        // メール通知キュー情報取得
        TmpQueueMailNotice tmpQueueMailNotice = this.getTmpQueueMailNotice(mailNotice);

        //データが取れなかったらリターンして次に回す
        if (Objects.isNull(tmpQueueMailNotice)) {
            return;
        }

        //メール送信実務
        sendMailProc(tmpQueueMailNotice);

        //致命的な例外はロールバックされる
        return;
    }

    /**
     * メール送信の実務
     *
     * @param tmpQueueMailNotice    メール送信情報
     */
    @ErrorHandling
    private void sendMailProc(TmpQueueMailNotice tmpQueueMailNotice)
    {
        final String methodName = "sendMailProc";

        try {
            // メール通知情報取得
            MstrMessageDefine mstrMsgdef = this.getMailNoticeInfo(tmpQueueMailNotice);

            // メール通知対象ユーザリスト取得
            List<MstrMessageGroup> sendMailUserList = this.getSendMailUserList(tmpQueueMailNotice,
                    mstrMsgdef.getSend_mail_group_id());

            // メール通知対象ユーザメールアドレス取得
            Map<String, List<String>> mailAddressMap = this.getSendUserMailAddressList(tmpQueueMailNotice,
                    sendMailUserList);

            // メールサーバー情報取得
            this.getMailServerInfo(tmpQueueMailNotice);

            // メール通知処理
            // emはselectで使用するのでロールバックする必要なし
            this.sendMail(tmpQueueMailNotice, mailAddressMap, em.getEntityManager());

        }
        catch (GnomesAppException e) {
            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            this.cause = MessagesHandler.getExceptionMessage(this.containerRequest, e);

            String message = this.getMessage(GnomesMessagesConstants.ME01_0141, this.setParams(tmpQueueMailNotice,
                    cause));

            this.logHelper.severe(this.logger, className, methodName, message);
        }

        try {

            //メール送信に失敗したらキューを更新して再度リトライする
            if (MailNoticeStatus.NoticeFailure.equals(this.mailNoticeStatus)) {
                // メール通知キュー更新する
                // 更新してもう一度親読み直す
                this.updateTmpQueue(tmpQueueMailNotice);

            }
            else {
                //正常に送信出来たらメール送信キューは削除する
                this.deleteTmpQueue(tmpQueueMailNotice);
            }

            // メール通知履歴登録
            this.insertMailNoticeHistory(tmpQueueMailNotice);

        }
        catch (Exception e) {
            //致命的な例外でもロールバックせず続行する
            this.logHelper.severe(this.logger, className, methodName, e.getMessage());
        }
    }

    /**
     * メール通知キュー情報取得.
     *
     * @param em EntityManager
     * @param condition エンティティ(抽出条件)
     * @return メール通知キュー情報
     */
    @ErrorHandling
    private TmpQueueMailNotice getTmpQueueMailNotice(TmpQueueMailNotice condition)
    {
        final String methodName = "getTmpQueueMailNotice";
        try {
            CriteriaBuilder builder = em.getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TmpQueueMailNotice> query = builder.createQuery(TmpQueueMailNotice.class);
            Root<TmpQueueMailNotice> root = query.from(TmpQueueMailNotice.class);
            query.select(root);

            List<Predicate> preds = new ArrayList<>();

            // メール通知キューキー
            preds.add(builder.equal(root.get(TmpQueueMailNotice.COLUMN_NAME_TMP_QUEUE_MAIL_NOTICE_KEY),
                    condition.getTmp_queue_mail_notice_key()));

            // 更新バージョン
            preds.add(builder.equal(root.get(TmpQueueMailNotice.COLUMN_NAME_VERSION), condition.getVersion()));

            query.where(builder.and(preds.toArray(new Predicate[preds.size()])));
            TypedQuery<TmpQueueMailNotice> typedQuery = em.getEntityManager().createQuery(query);

            return typedQuery.getSingleResult();
        }
        //データがない場合はスローせず次に回す（それ以外はスローされる）
        catch (NoResultException e) {
            // 該当データなし
            this.logHelper.severe(this.logger, className, methodName,
                    TmpQueueMailNotice.COLUMN_NAME_TMP_QUEUE_MAIL_NOTICE_KEY + " = " + condition.getTmp_queue_mail_notice_key(),
                    e);
        }
        return null;

    }

    /**
     * メール通知情報取得.
     * <pre>
     * メッセージ定義よりメール通知情報の取得を行う。
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @return メール通知情報
     * @throws GnomesAppException
     */
    private MstrMessageDefine getMailNoticeInfo(TmpQueueMailNotice tmpQueueMailNotice) throws GnomesAppException
    {

        MstrMessageDefine mstrMsgdef = this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(
                tmpQueueMailNotice.getMessage_no());

        // 該当データなし
        if (mstrMsgdef == null) {

            StringBuilder argument = new StringBuilder();
            argument.append(TABLE_NAME);
            argument.append(MstrMessageDefine.TABLE_NAME).append(COMMA);
            argument.append(COLUMN_NAME);
            argument.append(MstrMessageDefine.COLUMN_NAME_MESSAGE_NO).append(COMMA);
            argument.append(VALUE);
            argument.append(tmpQueueMailNotice.getMessage_no());

            this.cause = this.getMessage(GnomesMessagesConstants.ME01_0026, new Object[]{argument.toString()});

            String message = this.getMessage(GnomesMessagesConstants.ME01_0141, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.fine(this.logger, null, null, message);
            return new MstrMessageDefine();

        }
        // メール送信先グループIDチェック
        if (StringUtil.isNullOrEmpty(mstrMsgdef.getSend_mail_group_id())) {

            this.cause = this.getMessage(GnomesMessagesConstants.ME01_0140, null);

            String message = this.getMessage(GnomesMessagesConstants.ME01_0141, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.fine(this.logger, null, null, message);

        }

        return mstrMsgdef;

    }

    /**
     * メール通知対象ユーザリスト取得.
     * <pre>
     * メール通知対象ユーザリストの取得を行う。
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param sendMailGroupid メール送信先グループID
     * @return メール通知対象ユーザリスト
     * @throws GnomesAppException
     */
    private List<MstrMessageGroup> getSendMailUserList(TmpQueueMailNotice tmpQueueMailNotice, String sendMailGroupid)
            throws GnomesAppException
    {

        if (!StringUtil.isNullOrEmpty(this.cause)) {
            return null;
        }

        List<MstrMessageGroup> messageGroupList = this.mstrMessageGroupDao.getMstrMessageGroupList(sendMailGroupid);

        boolean sendMailUserSetFlag = false;

        for (int i = 0; i < messageGroupList.size(); i++) {

            if (!StringUtil.isNullOrEmpty(messageGroupList.get(i).getSend_mail_user_id())) {
                sendMailUserSetFlag = true;
                break;
            }

        }
        // メール通知対象ユーザが取得できなかった場合
        if (!sendMailUserSetFlag) {

            StringBuilder argument = new StringBuilder();
            argument.append(TABLE_NAME);
            argument.append(MstrMessageGroup.TABLE_NAME).append(COMMA);
            argument.append(COLUMN_NAME);
            argument.append(MstrMessageGroup.COLUMN_NAME_SEND_MAIL_GROUP_ID).append(COMMA);
            argument.append(VALUE);
            argument.append(sendMailGroupid);

            this.cause = this.getMessage(GnomesMessagesConstants.ME01_0142, new Object[]{argument.toString()});

            String message = this.getMessage(GnomesMessagesConstants.ME01_0141, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.fine(this.logger, null, null, message);

        }

        return messageGroupList;

    }

    /**
     * メール通知対象ユーザメールアドレス取得.
     * <pre>
     * メール通知対象ユーザメールアドレスの取得を行う。
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param sendMailUserList メール通知対象ユーザリスト
     * @return メール通知対象ユーザメールアドレスマップ
     * @throws GnomesAppException
     */
    private Map<String, List<String>> getSendUserMailAddressList(TmpQueueMailNotice tmpQueueMailNotice,
            List<MstrMessageGroup> sendMailUserList) throws GnomesAppException
    {

        if (!StringUtil.isNullOrEmpty(this.cause)) {
            return null;
        }

        List<String> userIdListTo = null;
        List<String> userIdListCC = null;
        List<String> userIdListBcc = null;

        // メール通知区分毎にユーザIDの振り分けを行う
        for (int i = 0; i < sendMailUserList.size(); i++) {

            if (SendMailType.TO.getValue() == sendMailUserList.get(i).getSend_mail_type()) {
                if (userIdListTo == null) {
                    userIdListTo = new ArrayList<>();
                }
                userIdListTo.add(sendMailUserList.get(i).getSend_mail_user_id());

            }
            else if (SendMailType.CC.getValue() == sendMailUserList.get(i).getSend_mail_type()) {
                if (userIdListCC == null) {
                    userIdListCC = new ArrayList<>();
                }
                userIdListCC.add(sendMailUserList.get(i).getSend_mail_user_id());

            }
            else if (SendMailType.BCC.getValue() == sendMailUserList.get(i).getSend_mail_type()) {
                if (userIdListBcc == null) {
                    userIdListBcc = new ArrayList<>();
                }
                userIdListBcc.add(sendMailUserList.get(i).getSend_mail_user_id());

            }

        }

        Map<String, List<String>> mailAddressMap = new HashMap<>();
        // メール通知区分が To のメールアドレス取得
        List<String> mailAddressListTo = this.getSendMaiiAddressList(userIdListTo);
        if (!(mailAddressListTo == null || mailAddressListTo.isEmpty())) {
            mailAddressMap.put(MAIL_ADDRESS_LIST_KEY_TO, mailAddressListTo);
        }
        // メール通知区分が CC のメールアドレス取得
        List<String> mailAddressListCC = this.getSendMaiiAddressList(userIdListCC);
        if (!(mailAddressListCC == null || mailAddressListCC.isEmpty())) {
            mailAddressMap.put(MAIL_ADDRESS_LIST_KEY_CC, mailAddressListCC);
        }
        // メール通知区分が Bcc のメールアドレス取得
        List<String> mailAddressListBcc = this.getSendMaiiAddressList(userIdListBcc);
        if (!(mailAddressListBcc == null || mailAddressListBcc.isEmpty())) {
            mailAddressMap.put(MAIL_ADDRESS_LIST_KEY_BCC, mailAddressListBcc);
        }

        // 全てのメール通知区分において該当データが存在しない場合
        if (!(mailAddressMap.containsKey(MAIL_ADDRESS_LIST_KEY_TO) || mailAddressMap.containsKey(
                MAIL_ADDRESS_LIST_KEY_CC) || mailAddressMap.containsKey(MAIL_ADDRESS_LIST_KEY_BCC))) {

            StringBuilder argument = new StringBuilder();
            argument.append(TABLE_NAME);
            argument.append(MstrPerson.TABLE_NAME).append(COMMA);
            argument.append(COLUMN_NAME);
            argument.append(MstrPerson.COLUMN_NAME_MAIL_ADDRESS).append(COMMA);
            argument.append(VALUE);

            StringBuilder userid = new StringBuilder();
            for (int i = 0; i < sendMailUserList.size(); i++) {
                if (userid.length() > 0) {
                    userid.append(COMMA);
                }
                userid.append(sendMailUserList.get(i).getSend_mail_user_id());
            }
            argument.append(userid.toString());

            this.cause = this.getMessage(GnomesMessagesConstants.ME01_0143, new Object[]{argument.toString()});

            String message = this.getMessage(GnomesMessagesConstants.ME01_0141, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.fine(this.logger, null, null, message);

        }

        return mailAddressMap;

    }

    /**
     * メールサーバー情報取得.
     * <pre>
     * メールサーバーの情報を取得する
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @throws Exception
     */
    private void getMailServerInfo(TmpQueueMailNotice tmpQueueMailNotice) throws GnomesAppException
    {

        if (!StringUtil.isNullOrEmpty(this.cause)) {
            return;
        }

        StringBuilder cause = new StringBuilder();

        // 差出人取得
        MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.CODE_MAIL_ADDRESS_FROM);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            cause.append(SystemDefConstants.CODE_MAIL_ADDRESS_FROM);

        }
        else {
            this.mailInfoBean.setFrom(mstrSystemDefine.getChar1());
        }

        // 認証 ユーザID取得
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.CODE_AUTHENTICATED_USERID);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(COMMA);
            }
            cause.append(SystemDefConstants.CODE_AUTHENTICATED_USERID);

        }
        else {
            this.mailInfoBean.setAuthenticatedUserId(mstrSystemDefine.getChar1());
        }

        //システム定義のコードからキーストアを調べてパスワードを取得する
        try {
            this.mailInfoBean.setAuthenticatedPassword(keyStoreUtilities.getSecretKeyValue(
                    SystemDefConstants.CODE_AUTHENTICATED_PASSWORD));
        }
        catch (Exception e) {
            throw e;
        }

        // 認証 エンコード取得
        mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_MAIL_SERVER_INFO,
                SystemDefConstants.CODE_ENCODE);

        if (Objects.isNull(mstrSystemDefine) || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {

            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            if (cause.length() > 0) {
                cause.append(COMMA);
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

            this.cause = this.getMessage(GnomesMessagesConstants.ME01_0144, new Object[]{cause.toString()});

            String message = this.getMessage(GnomesMessagesConstants.ME01_0141, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.fine(this.logger, null, null, message);

        }

    }

    /**
     * メール通知処理.
     * <pre>
     * メール通知処理を行う。
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param mailAddressList メールアドレスリスト
     * @throws GnomesAppException
     */
    private void sendMail(TmpQueueMailNotice tmpQueueMailNotice, Map<String, List<String>> mailAddressMap,
            EntityManager em) throws GnomesAppException
    {

        if (!StringUtil.isNullOrEmpty(this.cause)) {
            return;
        }

        Message messageInfo = this.messageDao.getMessage(tmpQueueMailNotice.getMessage_key(), em);
        // メッセージタイトル作成
        String subject = this.createMessageSubject(messageInfo);
        // メッセージ本文作成
        String message = this.createMessageText(messageInfo);

        SendMail sendMail = new SendMail();

        // 宛先
        if (mailAddressMap.containsKey(MAIL_ADDRESS_LIST_KEY_TO)) {
            this.mailInfoBean.setTo(this.editMailAddress(mailAddressMap.get(MAIL_ADDRESS_LIST_KEY_TO)));
        }
        // CC
        if (mailAddressMap.containsKey(MAIL_ADDRESS_LIST_KEY_CC)) {
            this.mailInfoBean.setCc(this.editMailAddress(mailAddressMap.get(MAIL_ADDRESS_LIST_KEY_CC)));
        }
        // BCC
        if (mailAddressMap.containsKey(MAIL_ADDRESS_LIST_KEY_BCC)) {
            this.mailInfoBean.setBcc(this.editMailAddress(mailAddressMap.get(MAIL_ADDRESS_LIST_KEY_BCC)));
        }

        try {
            // 題名
            this.mailInfoBean.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));

        }
        catch (UnsupportedEncodingException e) {
            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            this.cause = e.toString();

            String logMessage = this.getMessage(GnomesMessagesConstants.ME01_0145, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.severe(this.logger, null, null, logMessage);
            return;
        }

        // メッセージ
        this.mailInfoBean.setMessage(message);

        String[][] result = sendMail.runJob(new String[]{});

        if (!"0".equals(result[0][0])) {
            // メール通知状況：失敗
            this.mailNoticeStatus = MailNoticeStatus.NoticeFailure;
            // 原因
            Object[] arguments = new Object[3];
            arguments[0] = mailInfoBean.getJobName();
            arguments[1] = mailInfoBean.getComponentName();
            arguments[2] = mailInfoBean.getErrorMessage();

            this.cause = this.getMessage(GnomesMessagesConstants.ME01_0253, arguments);

            String logMessage = this.getMessage(GnomesMessagesConstants.ME01_0145, this.setParams(tmpQueueMailNotice,
                    this.cause));

            this.logHelper.severe(this.logger, null, null, logMessage);

        }

    }

    /**
     * メール通知キュー更新.
     * <pre>
     * メール通知キューの更新を行う。
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param em エンティティマネージャ
     */
    private void updateTmpQueue(TmpQueueMailNotice tmpQueueMailNotice)
    {

        Timestamp systemDate = ConverterUtils.utcToTimestamp(ConverterUtils.dateToLocalDateTime(
                CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        // メール通知状況
        tmpQueueMailNotice.setMail_notice_status(this.mailNoticeStatus.getValue());
        // 更新イベントID
        tmpQueueMailNotice.setLast_regist_event_id(this.containerRequest.getEventId());
        // 更新従業員No
        tmpQueueMailNotice.setLast_regist_user_number(this.containerRequest.getUserId());
        // 更新従業員名
        tmpQueueMailNotice.setLast_regist_user_name(this.containerRequest.getUserName());
        // 更新日時
        tmpQueueMailNotice.setLast_regist_datetime(systemDate);

        em.getEntityManager().flush();

    }

    /**
     * メール通知キュー削除
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param em エンティティマネージャ
     */
    private void deleteTmpQueue(TmpQueueMailNotice tmpQueueMailNotice)
    {

        em.getEntityManager().remove(tmpQueueMailNotice);
        em.getEntityManager().flush();

    }

    /**
     * メール通知履歴登録.
     * <pre>
     * メール通知履歴の登録を行う。
     * </pre>
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param em エンティティマネージャ
     */
    private void insertMailNoticeHistory(TmpQueueMailNotice tmpQueueMailNotice)
    {

        MailNoticeHistoryInfo historyInfo = new MailNoticeHistoryInfo();
        // メッセージキー
        historyInfo.setMessageKey(tmpQueueMailNotice.getMessage_key());
        // 発生日時
        historyInfo.setOccurdate(tmpQueueMailNotice.getOccur_date());
        // メッセージNo
        historyInfo.setMessageno(tmpQueueMailNotice.getMessage_no());
        // メール通知状況
        historyInfo.setMailNoticeStatus(this.mailNoticeStatus);
        // 失敗理由
        historyInfo.setFailureReason(this.cause);

        this.mailNoticeHistory.registration(historyInfo, em.getEntityManager());

    }

    /**
     * メールアドレスリスト取得.
     * <pre>
     * メールアドレスリストの取得を行う。
     * 取得できなかった場合、<code>null</code>を返却
     * </pre>
     * @param useridList ユーザIDリスト
     * @return メールアドレスリスト
     * @throws GnomesAppException
     */
    private List<String> getSendMaiiAddressList(List<String> useridList) throws GnomesAppException
    {

        List<String> mailAddressList = null;

        if (useridList == null || useridList.isEmpty()) {
            return mailAddressList;
        }
        // ユーザ情報取得
        List<MstrPerson> mstrPersonList = this.mstrPersonDao.getMstrPersonQuery(useridList);

        for (int i = 0; i < mstrPersonList.size(); i++) {

            if (!StringUtil.isNullOrEmpty(mstrPersonList.get(i).getMail_address())) {
                if (mailAddressList == null) {
                    mailAddressList = new ArrayList<>();
                }
                mailAddressList.add(mstrPersonList.get(i).getMail_address());
            }

        }

        return mailAddressList;

    }

    /**
     * メッセージタイトル作成.
     * <pre>
     * メッセージタイトルの作成を行う。
     * </pre>
     * @param messageInfo メッセージ情報
     * @return メッセージタイトル
     */
    private String createMessageSubject(Message messageInfo) throws GnomesAppException
    {

        // メッセージタイトル作成
        List<String> arguments = new ArrayList<>();
        // メッセージNo
        arguments.add(messageInfo.getMessage_no());
        // カテゴリ
        arguments.add(this.getCategoryName(messageInfo.getCategory()));
        // 重要度
        arguments.add(this.getMessageLevelName(messageInfo.getMessage_level()));
        // コンピュータ名
        arguments.add(messageInfo.getOrigin_computer_name());
        // 発生エリア
        arguments.add(this.editSite(messageInfo.getOccur_site_code()));

        String messageTitle = messageInfo.getMessage_title_resource_id();
        if (StringUtil.isNullOrEmpty(messageTitle)) {
            GnomesAppException gae = new GnomesAppException(
                    new IllegalArgumentException("Specify the message title resource ID for the message to be sent by email (message No: " + messageInfo.getMessage_no() + ")"));
            throw gae;
        }

        return MessagesHandler.getString(messageTitle,
                this.containerRequest.getUserLocale(), arguments.toArray(new Object[arguments.size()]));

    }

    /**
     * メッセージ作成.
     * <pre>
     * メッセージの作成を行う。
     * </pre>
     * @param messageInfo メッセージ情報
     * @return
     */
    private String createMessageText(Message messageInfo) throws GnomesAppException
    {

        // メッセージタイトル作成
        List<String> arguments = new ArrayList<>();
        // メッセージNo
        arguments.add(messageInfo.getMessage_no());
        // 発生日時
        arguments.add(this.editOccurdate(messageInfo.getOccur_date()));
        // カテゴリ
        arguments.add(this.getCategoryName(messageInfo.getCategory()));
        // 重要度
        arguments.add(this.getMessageLevelName(messageInfo.getMessage_level()));
        // コンピュータ名
        arguments.add(messageInfo.getOrigin_computer_name());
        // 発生エリア
        arguments.add(this.editSite(messageInfo.getOccur_site_code()));
        // メッセージ
        arguments.add(this.editMessage(messageInfo));

        String messageText = messageInfo.getMessage_text_resource_id();
        if (StringUtil.isNullOrEmpty(messageText)) {
            GnomesAppException gae = new GnomesAppException(
                    new IllegalArgumentException("Specify the message text resource ID for the message to be sent by email (message No: " + messageInfo.getMessage_no() + ")"));
            throw gae;
        }

        return MessagesHandler.getString(messageText,
                this.containerRequest.getUserLocale(), arguments.toArray(new Object[arguments.size()]));
    }

    /**
     * カテゴリ名取得
     * @param category カテゴリ
     * @return カテゴリ名
     */
    private String getCategoryName(int category)
    {

        MessageCategory messageCategory = MessageCategory.getEnum(category);
        if (messageCategory != null) {
            return ResourcesHandler.getString(messageCategory.name(), this.containerRequest.getUserLocale());
        }

        return "";

    }

    /**
     * 重要度名取得
     * @param msglevel 重要度
     * @return 重要度名
     */
    private String getMessageLevelName(int messageLevel)
    {

        MessageLevel messageLevelEnum = MessageLevel.getEnum(messageLevel);
        if (messageLevelEnum != null) {
            return ResourcesHandler.getString(messageLevelEnum.name(), this.containerRequest.getUserLocale());
        }

        return "";

    }

    /**
     * エリア編集
     * <pre>
     * エリアID + " " + エリア名を返却する。
     * エリア名が取得できなかった場合、エリアIDのみ返却
     * </pre>
     * @param siteId エリアID
     * @return 編集後エリア
     */
    private String editSite(String siteId)
    {

        if (!StringUtil.isNullOrEmpty(siteId)) {

            String siteName = null;

            try {
                MstrSite mstrSite = this.mstrSiteDao.getMstrSite(siteId);

                if (mstrSite != null) {
                    siteName = mstrSite.getSite_name();
                }

            }
            catch (GnomesAppException e) {
                // 取得に失敗した場合
                siteName = "";
            }

            StringBuilder site = new StringBuilder();
            site.append(siteId);

            if (!StringUtil.isNullOrEmpty(siteName)) {
                site.append(" ").append(siteName);
            }

            return site.toString();

        }

        return "";

    }

    /**
     * 発生日時編集.
     * @param occurdate 発生日時
     * @return 編集後発生日時
     */
    private String editOccurdate(Date occurdate)
    {

        SimpleDateFormat sdf = new SimpleDateFormat(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0001));
        String microSeconds = String.valueOf(((Timestamp) occurdate).getNanos());
        //なぜ？        .substring(0, 6);
        return sdf.format(occurdate).concat(".").concat(microSeconds);

    }

    /**
     * メッセージ編集
     * @param messageInfo メッセージ情報
     * @return 編集後メッセージ
     */
    private String editMessage(Message messageInfo)
    {

        StringBuilder editMessage = new StringBuilder();

        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param1())) {
            editMessage.append(messageInfo.getMessage_param1());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param2())) {
            editMessage.append(messageInfo.getMessage_param2());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param3())) {
            editMessage.append(messageInfo.getMessage_param3());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param4())) {
            editMessage.append(messageInfo.getMessage_param4());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param5())) {
            editMessage.append(messageInfo.getMessage_param5());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param6())) {
            editMessage.append(messageInfo.getMessage_param6());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param7())) {
            editMessage.append(messageInfo.getMessage_param7());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param8())) {
            editMessage.append(messageInfo.getMessage_param8());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param9())) {
            editMessage.append(messageInfo.getMessage_param9());
        }
        if (!StringUtil.isNullOrEmpty(messageInfo.getMessage_param10())) {
            editMessage.append(messageInfo.getMessage_param10());
        }

        return editMessage.toString();

    }

    /**
     * メールアドレス編集
     * <pre>
     * メールアドレスリストをカンマ区切りの文字列に編集する
     * </pre>
     * @param mailAddressList メールアドレスリスト
     * @return 編集後メールアドレス(カンマ区切り)
     */
    private String editMailAddress(List<String> mailAddressList)
    {

        StringBuilder maliAddress = new StringBuilder();

        for (int i = 0; i < mailAddressList.size(); i++) {
            if (maliAddress.length() > 0) {
                maliAddress.append(COMMA);
            }
            maliAddress.append(mailAddressList.get(i));
        }

        return maliAddress.toString();

    }

    /**
     * リソース取得.
     * @param messageno メッセージNo
     * @param arguments 置き換えパラメータ
     * @return リソース
     */
    private String getMessage(String messageno, Object[] arguments)
    {

        String message = null;

        try {
            MstrMessageDefine mstrMsgdef = this.mstrMessageDefineDao.getMstrMessageDefineForMessageNo(messageno);

            if (mstrMsgdef != null) {

                if (arguments == null || arguments.length == 0) {
                    message = MessagesHandler.getString(mstrMsgdef.getResource_id(),
                            this.containerRequest.getUserLocale());
                }
                else {
                    message = MessagesHandler.getString(mstrMsgdef.getResource_id(),
                            this.containerRequest.getUserLocale(), arguments);
                }

            }

        }
        catch (GnomesAppException e) {
            this.logHelper.severe(logger, null, null, e.getMessage(), e);
        }

        return message;

    }

    /**
     * エラー発生時ログ出力用置き換えパラメータ設定.
     * @param tmpQueueMailNotice メール通知キュー情報
     * @param cause 原因
     * @return 置き換えパラメータ
     */
    private Object[] setParams(TmpQueueMailNotice tmpQueueMailNotice, String cause)
    {

        Object[] arguments = new Object[3];
        arguments[0] = tmpQueueMailNotice.getMessage_no();
        arguments[1] = this.editOccurdate(tmpQueueMailNotice.getOccur_date());
        arguments[2] = cause;

        return arguments;

    }

}
