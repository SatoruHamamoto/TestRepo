package com.gnomes.common.persistence;

import java.util.Objects;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/06/13 10:42 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * ローカル使用専用エンティティマネージャー
 *  通常GnomesEntityManagerはCDIインジェクトして使うが
 *  こちらはローカル変数にてNewして使う。内部にCDIのInjectはなく、
 *  トランザクションの制御を兼ね備える
 *
 *  複数のトランザクションを挟む際、CDIのインジェクトでは１つしか
 *  扱えず、呼び出し先で共有できるメリットはあるが、その先で目的の違う
 *  別なトランザクションを使おうとすると同じオブジェクトになるため
 *  問題が起こる場合に選択肢の候補とする
 *
 * @author 03501213
 *
 */
public class GnomesLocalEntityManager
{
    /**
     * エンティティマネージャー
     */
    private EntityManager entityManager;

    /**
     * セッション
     */
    private Session       session;

    /**
     * トランザクション
     */
    private Transaction   transaction;

    /**
     * エンティティマネージャーの取得
     * @return  エンティティマネージャー
     */
    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    /**
     * コンストラクタ
     * エンティティマネージャーは外部から指定する
     *
     * @param entityManager     エンティティマネージャーオブジェクト
     */
    public GnomesLocalEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    /**
     * トランザクション開始
     *
     * @param em            対象のエンティティマネージャー
     * @param session       セッションオブジェクト
     * @param transaction   トランザクションオブジェクト
     */
    public void begin()
    {
        if (Objects.isNull(session) || (!session.isOpen())) {
            session = this.entityManager.unwrap(Session.class);
        }
        Transaction transaction = session.getTransaction();

        if (Objects.isNull(transaction) || (!TransactionStatus.ACTIVE.equals(transaction.getStatus()))) {

            session.beginTransaction();
        }
        return;
    }
    /**
     * トランザクションコミット
     *
     * @param em            対象のエンティティマネージャー
     * @param session       セッションオブジェクト
     * @param transaction   トランザクションオブジェクト
     */
    public void commit()
    {
        Transaction transaction = session.getTransaction();

        if ((!Objects.isNull(transaction)) && (TransactionStatus.ACTIVE.equals(transaction.getStatus()))) {
            transaction.commit();
        }
    }

    /**
     * 明示的にクローズ
     *
     * @param em            対象のエンティティマネージャー
     * @param session       セッションオブジェクト
     * @param transaction   トランザクションオブジェクト
     */
    public void close()
    {
        if (Objects.nonNull(this.entityManager) && this.entityManager.isOpen()) {
            this.entityManager.close();
        }
        if (Objects.nonNull(session) && session.isOpen()) {
            session.close();
        }
    }
}
