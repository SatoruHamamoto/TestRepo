package com.gnomes.common.persistence;

import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesScreenEntityManagerBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.logging.LogHelper;

/**
 * エンティティマネージャー
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/04 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class GnomesEntityManager {

    /** セッションビーン */
    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /** セッション */
    private Session session;

    /** トランザクション */
    private Transaction transaction;

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /**
     * トレースログ用
     */
    private static final String className = "GnomesEntityManager";

    /**
     * このスレッドがEJBかどうか、またトランザクションのストアでもある
     * EJBスレッドの場合、エンティティマネージャーはejbBeanから入手する
     */
    @Inject
    protected GnomesEjbBean ejbBean;

    /**
     * 画面から呼ばれる処理のエンティティマネージャーを管理するBean
     */
    @Inject
    protected GnomesScreenEntityManagerBean scrBean;

    /**
     * エンティティマネージャー取得
     * 通常画面からだとgnomesSessionBeanから取得するが
     * EJBスレッドはsessionBeanがないため、ejbBeanから入手する
     *
     * @return エンティティマネージャー
     */
    public EntityManager getEntityManager() {

        if (ejbBean.isEjbBatch()) {
            //EJBスレッドはsessionBeanがないため、ejbBeanから入手する
            return ejbBean.getEntityManager();
        } else {
            //通常画面からだとgnomesSessionBeanから取得 -> Sessionはやめる
            return scrBean.getEntityManager();
        }
    }

    /**
     * トランザクション開始
     */
    public void begin() {

        final String methodName="begin";

        EntityManager em = this.getEntityManager();

        if (Objects.isNull(this.session) || (!this.session.isOpen())) {
            this.session = em.unwrap(Session.class);
        }

        if (Objects.isNull(this.transaction) || (!TransactionStatus.ACTIVE.equals(this.transaction.getStatus()))) {

            this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> GnomesEntityManager Begin Transaction called");
            this.transaction = this.session.beginTransaction();

        }

    }

    /**
     * コミット
     */
    public void commit() {
        final String methodName="commit";

        if ((!Objects.isNull(this.transaction)) && (TransactionStatus.ACTIVE.equals(this.transaction.getStatus()))) {
            this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> GnomesEntityManager Commit called");
            this.transaction.commit();
        }
    }

    /**
     * ロールバック
     */
    public void rollback() {
        final String methodName="rollback";

        //NULLチェック
        if (Objects.isNull(this.transaction)){
            return;
        }
        //ステータスがアクティブかMARKED_ROLLBACK(ロールバック専用としてマーク)されている場合は
        //ロールバックする
        TransactionStatus status = this.transaction.getStatus();
        if(TransactionStatus.ACTIVE.equals(status) || TransactionStatus.MARKED_ROLLBACK.equals(status)){
            this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> GnomesEntityManager rollback called");
            this.transaction.rollback();
        }
    }

    /**
     * 明示的にクローズ
     */
    public void close() {
        final String methodName="close";

        EntityManager em;
        if (this.ejbBean.isEjbBatch()) {
            em = this.ejbBean.getEntityManager();
        } else {
            em = this.scrBean.getEntityManager();
        }
        if (!Objects.isNull(em)) {
            if (em.isOpen()) {
                this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> GnomesEntityManager closed");
                em.close();
            }
        }
    }
}
