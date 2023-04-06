package com.gnomes.system.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.dto.CountDto;
import com.gnomes.common.dto.MstrScreenButtonDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.MstrPersonPosition;
import com.gnomes.system.entity.MstrPersonSubstitute;
import com.gnomes.system.entity.MstrPrivilege;
import com.gnomes.system.entity.MstrScreenButton;

/**
 * ユーザ作業権限判定 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/07/25 YJP/K.Tada                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class JudgePersonLicenseDao extends BaseDao implements Serializable {

    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public JudgePersonLicenseDao() {
    }

    /**
     * ユーザ作業権限判定
     * @param userId ユーザID
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonLicense(String userId,
            String privilegeId,
            String siteCode,
            String orderProcessCode,
            String workProcessCode
            ) throws GnomesAppException {

//-[DEBUG]---------------------------
        // 引数チェック
        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(userId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(privilegeId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(privilegeId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<CountDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_JUDGE_PERSON_LICENSE_COUNT, CountDto.class);

        // パラメータ設定
        query.setParameter(MstrPrivilege.COLUMN_NAME_PRIVILEGE_ID, privilegeId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_USER_ID, userId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);

        // 件数を取得する
        BigInteger count = query.getSingleResult().getCnt();

        // 判定（1以上で権限あり）
        if (count.compareTo(BigInteger.valueOf(0)) > 0) {
            logHelper.info(this.logger, null, null, "judgePersonLicense userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I have the authority");
            return true;
        }
        logHelper.info(this.logger, null, null, "judgePersonLicense userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I do not have the authority");
        return false;
    }

    /**
     * ユーザ作業権限判定(作業場所含む)
     * @param userId ユーザID
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonLicenseAddWorkCell(String userId,
            String privilegeId,
            String siteCode,
            String orderProcessCode,
            String workProcessCode,
            String workCellCode
            ) throws GnomesAppException {

        // 引数チェック
        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(userId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(privilegeId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(privilegeId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workCellCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workCellCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<CountDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_JUDGE_PERSON_LICENSE_COUNT_ADD_WORK_CELL, CountDto.class);

        // パラメータ設定
        query.setParameter(MstrPrivilege.COLUMN_NAME_PRIVILEGE_ID, privilegeId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_USER_ID, userId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_CELL_CODE, workCellCode);

        // 件数を取得する
        BigInteger count = query.getSingleResult().getCnt();

        // 判定（1以上で権限あり）
        if (count.compareTo(BigInteger.valueOf(0)) > 0) {
            logHelper.info(this.logger, null, null, "judgePersonLicense userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I have the authority");
            return true;
        }
        logHelper.info(this.logger, null, null, "judgePersonLicense userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I do not have the authority");
        return false;
    }

    /**
     * ユーザ委託代替作業権限を元にした権限判定
     * @param userId ユーザID
     * @param privilegeId 権限ID
     * @param date システム日付
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonLicenseSubstitute(String userId,
            String privilegeId,
            Date date,
            String siteCode,
            String orderProcessCode,
            String workProcessCode
            ) throws GnomesAppException {

        // 引数チェック
        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(userId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(privilegeId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(privilegeId));
            throw ex;
        }

        if (date == null) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(date));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<CountDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_JUDGE_PERSON_LICENSE_SUBSTITUTE_COUNT, CountDto.class);

        // パラメータ設定
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_PRIVILEGE_ID, privilegeId);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_SUBSTITUTE_USER_ID, userId);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);
        query.setParameter("date_time", date);

        // 件数を取得する
        BigInteger count = query.getSingleResult().getCnt();

        // 判定（1以上で権限あり）
        if (count.compareTo(BigInteger.valueOf(0)) > 0) {
            logHelper.info(this.logger, null, null, "judgePersonLicense\"Substitute\" Return userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I have the authority");
            return true;
        }
        logHelper.info(this.logger, null, null, "judgePersonLicense\"Substitute\" Return userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I do not have the authority");

        return false;
    }

    /**
     * ユーザ委託代替作業権限を元にした権限判定(作業場所含む)
     * @param userId ユーザID
     * @param privilegeId 権限ID
     * @param date システム日付
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonLicenseSubstituteAddWorkCell(String userId,
            String privilegeId,
            Date date,
            String siteCode,
            String orderProcessCode,
            String workProcessCode,
            String workCellCode
            ) throws GnomesAppException {

        // 引数チェック
        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(userId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(privilegeId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(privilegeId));
            throw ex;
        }

        if (date == null) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(date));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workCellCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workCellCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<CountDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_JUDGE_PERSON_LICENSE_SUBSTITUTE_COUNT_ADD_WORK_CELL, CountDto.class);

        // パラメータ設定
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_PRIVILEGE_ID, privilegeId);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_SUBSTITUTE_USER_ID, userId);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);
        query.setParameter(MstrPersonSubstitute.COLUMN_NAME_WORK_CELL_CODE, workCellCode);
        query.setParameter("date_time", date);

        // 件数を取得する
        BigInteger count = query.getSingleResult().getCnt();

        // 判定（1以上で権限あり）
        if (count.compareTo(BigInteger.valueOf(0)) > 0) {
            logHelper.info(this.logger, null, null, "judgePersonLicense\"Substitute\" Return userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I have the authority");
            return true;
        }
        logHelper.info(this.logger, null, null, "judgePersonLicense\"Substitute\" Return userId = [" + userId + "] privilegeId=[" + privilegeId + "] ---- I do not have the authority");

        return false;
    }

    /**
     * ダブル認証権限判定
     * @param certUserId ダブルチェック承認者
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonLicenseDoubleCheck(String certUserId,
            String privilegeId,
            String siteCode,
            String orderProcessCode,
            String workProcessCode
            ) throws GnomesAppException {

        // 引数チェック
        if (StringUtil.isNullOrEmpty(certUserId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(certUserId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(privilegeId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(privilegeId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<CountDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_JUDGE_PERSON_LICENSE_DOUBLE_CHECK_COUNT, CountDto.class);

        // パラメータ設定
        query.setParameter(MstrPrivilege.COLUMN_NAME_PRIVILEGE_ID, privilegeId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_USER_ID, certUserId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);

        // 件数を取得する
        BigInteger count = query.getSingleResult().getCnt();

        // 判定（1以上で権限あり）
        if (count.compareTo(BigInteger.valueOf(0)) > 0) {
            logHelper.info(this.logger, null, null, "judgePersonLicense\"DoubleCheck\" Return certUserId = [" + certUserId + "] privilegeId=[" + privilegeId + "] ---- I have the authority" );

            return true;
        }
        logHelper.info(this.logger, null, null, "judgePersonLicense\"DoubleCheck\" Return certUserId = [" + certUserId + "] privilegeId=[" + privilegeId + "] ---- I do not have the authority" );
        return false;
    }

    /**
     * ダブル認証権限判定(作業場所含む)
     * @param certUserId ダブルチェック承認者
     * @param privilegeId 権限ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @return 権限有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean judgePersonLicenseDoubleCheckAddWorkCell(String certUserId,
            String privilegeId,
            String siteCode,
            String orderProcessCode,
            String workProcessCode,
            String workCellCode
            ) throws GnomesAppException {

        // 引数チェック
        if (StringUtil.isNullOrEmpty(certUserId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(certUserId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(privilegeId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(privilegeId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workCellCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workCellCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<CountDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_JUDGE_PERSON_LICENSE_DOUBLE_CHECK_COUNT_ADD_WORK_CELL, CountDto.class);

        // パラメータ設定
        query.setParameter(MstrPrivilege.COLUMN_NAME_PRIVILEGE_ID, privilegeId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_USER_ID, certUserId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_CELL_CODE, workCellCode);

        // 件数を取得する
        BigInteger count = query.getSingleResult().getCnt();

        // 判定（1以上で権限あり）
        if (count.compareTo(BigInteger.valueOf(0)) > 0) {
            logHelper.info(this.logger, null, null, "judgePersonLicense\"DoubleCheck\" Return certUserId = [" + certUserId + "] privilegeId=[" + privilegeId + "] ---- I have the authority" );

            return true;
        }
        logHelper.info(this.logger, null, null, "judgePersonLicense\"DoubleCheck\" Return certUserId = [" + certUserId + "] privilegeId=[" + privilegeId + "] ---- I do not have the authority" );
        return false;
    }

    /**
     * 画面に権限があるボタンリスト取得
     * @param userId ユーザID
     * @param screen_id 画面ID
     * @param siteCode 拠点コード
     * @param orderProcessCode 指図工程コード
     * @param workProcessCode 作業工程コード
     * @param workCellCode 作業場所コード
     * @return 画面に権限があるボタンリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrScreenButtonDto> getMstrScreenButtonList(String userId,
            String screen_id,
            String siteCode,
            String orderProcessCode,
            String workProcessCode,
            String workCellCode
            ) throws GnomesAppException {

        // 引数チェック
        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(userId));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(screen_id)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(screen_id));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(siteCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(orderProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(orderProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workProcessCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workProcessCode));
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(workCellCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    String.valueOf(workCellCode));
            throw ex;
        }

        // クエリ作成
        TypedQuery<MstrScreenButtonDto> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_GET_SCREEN_ENABLE_BUTTON_LIST, MstrScreenButtonDto.class);

        // パラメータ設定
        query.setParameter(MstrScreenButton.COLUMN_NAME_SCREEN_ID, screen_id);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_USER_ID, userId);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_SITE_CODE, siteCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_ORDER_PROCESS_CODE, orderProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_PROCESS_CODE, workProcessCode);
        query.setParameter(MstrPersonPosition.COLUMN_NAME_WORK_CELL_CODE, workCellCode);

        // 件数を取得する
        List<MstrScreenButtonDto> result = query.getResultList();

        return result;
    }

}
