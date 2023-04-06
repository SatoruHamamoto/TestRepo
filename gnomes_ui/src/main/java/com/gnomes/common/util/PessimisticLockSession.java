package com.gnomes.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.PessimisticLockException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.persistence.annotation.GnomesDS;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.uiservice.ContainerRequest;

/**
 * 悲観的ロックセッション
 * 主にキュー処理で使われる
 * 通常画面処理とEJBスレッドで管理が変わる
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/15 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class PessimisticLockSession
{

    /** ロガー */
    @Inject
    protected Logger                 logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper              logHelper;

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory     emf;

    /** エンティティマネージャーファクトリ（保管領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE)
    private EntityManagerFactory     emfStorage;

    /** エンティティマネージャー. */
    private EntityManager            em;

    /** セッション. */
    protected Session                session;

    /** トランザクション. */
    protected Transaction            transaction;

    /** MySQLフラグ. */
    protected boolean                mySqlFlag;

    /** GnomesExceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /** ContainerRequest */
    @Inject
    protected ContainerRequest       req;

    /** セッションビーン */
    @Inject
    protected GnomesSessionBean      gnomesSessionBean;

    /** デフォルトタイムアウト値(ミリ秒) */
    protected static final int       DEFAULT_TIMEOUT = 1000;

    @Inject
    protected GnomesEjbBean         ejbBean;

//デバッグ用で使っていた
//    /** 初期処理 */
//    @PostConstruct
//    private void init() {
//        System.out.println("********************* PessimisticLockSession Create *************************" + this.toString());
//    }
//
//    /** 後処理 */
//    @PreDestroy
//    private void destroy() {
//        System.out.println("********************* PessimisticLockSession Destroy *************************" + this.toString());
//    }


    /**
     * セッション情報作成
     *  sessionBeanの領域指定によって通常か保管が切り替わる
     */
    public void createSession()
    {

        // 領域区分判定
        if (RegionType.NORMAL.equals(
                RegionType.getEnum(this.gnomesSessionBean.getRegionType()))) {
            // 通常領域
            this.em = this.emf.createEntityManager();
        }
        else {
            // 保管領域
            this.em = this.emfStorage.createEntityManager();
        }

        this.session = this.em.unwrap(Session.class);

        Map<String, Object> properties = this.emf.getProperties();

        if (properties.containsKey(Environment.DIALECT)) {

            if (properties.get(Environment.DIALECT).toString().toLowerCase()
                    .contains("mysql")) {

                this.mySqlFlag = true;

            }

        }

    }

    /**
     * セッション情報作成(通常領域固定)
     *  主にEJBスレッドで処理される
     */
    public void createSessionNormal()
    {

        //通常EJBから呼ばれる関数だが、通常画面処理で呼ばれる可能性を考慮
        if(ejbBean.isEjbBatch()){
            this.em = ejbBean.getEntityManager(PessimisticLockSession.class.getName());
        }
        else {
            this.em = this.emf.createEntityManager();
        }

        //セッションの作成
        this.session = this.em.unwrap(Session.class);

        //MySQL互換性を考慮
        Map<String, Object> properties = this.emf.getProperties();
        if (properties.containsKey(Environment.DIALECT)) {

            if (properties.get(Environment.DIALECT).toString().toLowerCase()
                    .contains("mysql")) {

                this.mySqlFlag = true;

            }

        }

    }

    /**
     * エンティティマネージャー取得
     * @return エンティティマネージャー
     */
    public EntityManager getEntityManager()
    {
        //EJBの場合エンティティマネージャーはejbBachビーンにある
        if(ejbBean.isEjbBatch()){
            return ejbBean.getEntityManager(PessimisticLockSession.class.getName());
        }
        //通常画面処理では自身のemを使用
        else {
            return this.em;
        }
    }

    /**
     * 行ロック(待機時間なし)
     * <pre>
     * 待機時間なし行ロックを行う。
     * 但し、MySQLに関しては、待機時間なしの設定は不可のため、最小待機時間(1秒)待機
     * </pre>
     * @param entityClass ロックを行うテーブル
     * @param primaryKeys 1つ以上のサロゲートキーで行を指定
     * @return エンティティ(ロックに失敗した場合、null を返却) 成功した場合、キーが１つならEntity,複数ならEntity[]
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public <T> T lock(Class<T> entityClass, String primaryKeys)
            throws GnomesAppException
    {

        List<T> ret = lock(entityClass, 0, new String[]{primaryKeys});
        if (!Objects.isNull(ret) && ret.size() == 1) {
            return ret.get(0);
        }
        else {
            return null;
        }
    }
    /**
     * 行ロック(待機時間あり)
     * <pre>
     * 待機時間なし行ロックを行う。
     * 但し、MySQLに関しては、待機時間なしの設定は不可のため、最小待機時間(1秒)待機
     * </pre>
     * @param entityClass ロックを行うテーブル
     * @param primaryKeys 1つ以上のサロゲートキーで行を指定
     * @return エンティティ(ロックに失敗した場合、null を返却) 成功した場合、キーが１つならEntity,複数ならEntity[]
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public <T> T lock(Class<T> entityClass, int timeout, String primaryKeys)
            throws GnomesAppException
    {

        List<T> ret = lock(entityClass, timeout, new String[]{primaryKeys});
        if (!Objects.isNull(ret) && ret.size() == 1) {
            return ret.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * 行ロック(待機時間付き)
     * 2019/11/15 バルクアクセス対応
     *  バルクアクセスの場合引数のサロゲートキ-に対するレコードがすべて取得できてロックできたときのみ
     *  成功する１つでも排他エラーのレコードがあれば成功と返さない
     * <pre>
     * 待機時間付きロックを行う。
     * 但し、MySQLに関しては、待機時間なしの設定は不可のため、最小待機時間(1秒)待機
     * </pre>
     * @param entityClass ロックを行うテーブル
     * @param timeout 待機タイムアウトミリ秒　0を指定すると待ち無し排他エラー
     * @param primaryKeys 1つ以上のサロゲートキーで行を指定
     * @return エンティティ(ロックに失敗した場合、null を返却) 成功した場合、キーが１つならEntity,複数ならEntity[]
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public <T> List<T> lock(Class<T> entityClass, int timeout,
            String[] primaryKeys) throws GnomesAppException
    {

        // エンティティ
        T entity = null;

        // 全体的にロック成功を意味するフラグ
        boolean allLockedSuccess = true;
        // エンティティ（複数用）
        List<T> entityList = new ArrayList<>();

        try {
            // トランザクション開始
            if (this.transaction == null || !TransactionStatus.ACTIVE
                    .equals(this.transaction.getStatus())) {

                this.transaction = this.session.beginTransaction();
            }
            // MySQLロック待ちタイムアウト設定待
            // MySQLではロック待ちタイムアウトで待ち無し（０）を指定しても
            //サポートされていないので、最小値1000を指定している
            if (timeout > 0) {
                this.setMySqlLockWaitTimeout(timeout);
            }
            else {
                this.setMySqlLockWaitTimeout(DEFAULT_TIMEOUT);
            }

            //
            //JPA(EntityManager)の設定またはQueryヒントとして、
            //"javax.persistence.lock.timeout"を指定することで、タイムアウト時間を指定することができる。
            //タイムアウトをミリ秒で指定する。1000を指定すると、1秒となる。
            //0を指定した場合、nowait(FOR UPDATE NOWAIT)が付加され、他のトランザクションによってロックされていた場合に、
            //ロックの解放待ちを行わずに排他エラーとなる。
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("javax.persistence.lock.timeout", timeout);

            //バルクアクセス対応。findを数分コールする。コールした戻りエンティティオブジェクトを
            //リストで保持し、最後に関数リターンで返す
            for (String primaryKey : primaryKeys) {

                entity = this.em.find(entityClass, primaryKey,
                        LockModeType.PESSIMISTIC_WRITE, properties);

                if (Objects.isNull(entity)) {
                    // データ未存在
                    this.logHelper.severe(this.logger, null, null,
                            MessagesHandler.getExceptionMessage(this.req,
                                    this.exceptionFactory
                                            .createGnomesAppException(null,
                                                    GnomesMessagesConstants.ME01_0026,
                                                    primaryKey)));

                    //データが存在しなかったらNULLを返す
                    allLockedSuccess = false;
                    return null;
                }
                entityList.add(entity);
            }
        }
        catch (PessimisticEntityLockException e) {
            // ロックタイムアウト
            allLockedSuccess = false;
            throw new PessimisticLockException();
        }
        catch (LockAcquisitionException e) {
            // ロックタイムアウト
            allLockedSuccess = false;
            throw new PessimisticLockException(e);
        }
        catch (LockTimeoutException e) {
            // ロックタイムアウト
            allLockedSuccess = false;
            throw new PessimisticLockException(e);
        }
        catch (Exception e) {
            allLockedSuccess = false;

            if (e instanceof PersistenceException) {
                // MYSQL 対策
                if (e.getCause() != null
                        && e.getCause() instanceof LockAcquisitionException) {
                    // ロックタイムアウト
                    allLockedSuccess = false;
                    throw new PessimisticLockException(e);
                }
            }

            // ME01.0169    行ロック処理時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\n エンティティクラス名： {0}
            String className = null;
            if (entityClass != null) {
                className = entityClass.getSimpleName();
            }
            else {
                className = "";
            }
            throw exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0169, e, className);
        }
        finally {

            //全体的に悲観ロックに失敗した場合、ロールバックする
            //成功したときのロック解除は外側のトランザクションでcommit/rollbackされることを想定
            if (allLockedSuccess == false) {
                if (this.transaction != null && TransactionStatus.ACTIVE
                        .equals(this.transaction.getStatus())) {
                    this.transaction.rollback();
                }
                //this.session.clear();
            }

        }

        //成功した場合
        //シングルアクセス（プライマリキーが１つ）の場合は、１つのエンティティを返す
        // 互換用で、旧メソッドはバルクアクセスでないため、シングルオブジェクトで返す必要があるため
        if (allLockedSuccess) {
            //バルクアクセスの場合、エンティティ配列を返す
            return (entityList);
        }
        else {
            //ここに来る時はエラーなのでnullを返す
            return null;
        }
    }
    /**
     * トランザクション開始
     */
    public void begin()
    {

        if (this.transaction == null || !TransactionStatus.ACTIVE
                .equals(this.transaction.getStatus())) {

            this.transaction = this.session.beginTransaction();

        }

    }

    /**
     * トランザクションコミット
     */
    public void commit()
    {

        if (this.transaction != null && TransactionStatus.ACTIVE
                .equals(this.transaction.getStatus())) {
            this.transaction.commit();
        }

    }

    /**
     * トランザクションロールバック
     */
    public void rollback()
    {

        if (this.transaction != null && TransactionStatus.ACTIVE
                .equals(this.transaction.getStatus())) {
            this.transaction.rollback();
        }

    }

    /**
     * セッションクローズ.
     */
    public void close()
    {

        if (this.transaction != null && TransactionStatus.ACTIVE
                .equals(this.transaction.getStatus())) {
            this.transaction.rollback();
        }

        if ((!Objects.isNull(this.session)) && this.session.isOpen()) {
            this.session.close();
        }

        //エンティティマネージャーもクローズ
        if ((!Objects.isNull(this.em)) && this.em.isOpen()) {
            this.em.close();
        }

    }

    /**
     * MySQLロック待ちタイムアウト設定.
     * <pre>
     * 接続先のDBがMySQLの場合、ロック待ちタイムアウト値の設定を行う。
     * MySQLの待機時間は秒単位のため、1000ミリ秒以下の場合は、強制的に1秒とする。
     * </pre>
     * @param timeout タイムアウト値(ミリ秒)
     */
    private void setMySqlLockWaitTimeout(int timeout)
    {

        if (this.mySqlFlag) {

            int timeoutValue = DEFAULT_TIMEOUT;

            if (timeout > 1000) {
                timeoutValue = timeout;
            }

            String queryString = String.format(
                    "set innodb_lock_wait_timeout = %s", timeoutValue / 1000);
            this.session.createSQLQuery(queryString).executeUpdate();

        }

    }

}
