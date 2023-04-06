package com.gnomes.common.data;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.logging.LogHelper;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/18 20:39 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * 画面用エンティティマネージャー管理Bean
 *
 * 画面から呼び出しBLやModelで使用するトランザクションの元EntityManagerは
 * 以前はSessionScopedのGnomesSessionBeanで共有していたが、複数の画面で同時操作
 * すると同じトランザクションオブジェクトを使いまわし問題が起こる。
 * トランザクションは画面からの呼び出し単位（リクエストスコープ）で毎回生成管理
 * するために本クラスをインジェクトして使用する
 *
 * 姉妹品としてGnomesEJBBeanがある。こちらはEJBバッチで処理されるエンティティマネージャー
 * で、排他的に利用される（GnomesEjbBeanオブジェクトが使用しているときはそちらの
 * エンティティマネージャーが使用される）
 *
 * @author 03501213
 *
 */
@RequestScoped
public class GnomesScreenEntityManagerBean {

    /**
     * クラス名（ログ用）
     */
    private static final String className="GnomesScreenEntityManagerBean";

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory emf;

    /** エンティティマネージャーファクトリ（保管領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE)
    private EntityManagerFactory emfStorage;

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    @Inject
    protected GnomesSessionBean gnomesSessionBean;


    /**
     * エンティティマネージャーを名前付きで複数管理する
     * 一般BLの中で処理するトランザクションを複数管理することがあり、例えば
     * 一般BLでエラーになってロールバックしてもステータスや履歴はコミットする
     * 用途があるため
     */
    private HashMap<String, EntityManager> emMap = new HashMap<>();

    /**
     * 後処理
     * リクエストスコープの最後にエンティティマネージャーが正しくクローズさえたか
     * どうかを判断して必要に応じてクローズする
     * Commit/Rollbackの制御はTransactionalインタセプタが責任を持つ
    */
    @PreDestroy
    private void destroy() {
        final String methodName="destroy";

        for (EntityManager em : emMap.values()) {
            if(em.isOpen()){
                this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> Request Scoped finalize EntityManager Closed");
                em.close();
            }
        }
        emMap.clear();
    }

    /**
     * 通常トランザクションのエンティティマネージャーを取得
     * 画面と共通で呼び出されるBLなどはこちらら呼ばれる
     * 主にGnomesEntityManagerからのコール
     * @return
     */
    public EntityManager getEntityManager() {
        return getEntityManager(GnomesScreenEntityManagerBean.class.getName());
    }

    /**
     * 名前付きエンティティマネージャーの獲得
     * 通常トランザクションとは異なる別なトランザクションを実施したい場合
     * 管理する代表のクラス名を指定して取得する
     * 今は主にPessimisticLockSessionから呼ばれるが将来
     * 通常とは別トランザクションを作る場合はこれを使う
     *
     * @param className 管理する側のクラス名
     * @return クラス名で獲得したエンティティマネージャー（空なら作る）
     */
    public EntityManager getEntityManager(String className) {
        final String methodName="getEntityManager";
        synchronized (this) {

            if (emMap.containsKey(className)) {
                EntityManager em = emMap.get(className);
                //emがクローズしていたらオープンしなおす
                if(! em.isOpen()){
                    this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> EntityManager is Closed Reopen Session");
                    emMap.remove(className);
                    em = this.getEntityManagerFactory().createEntityManager();
                    this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> EntityManager(" + className + " Created");
                    emMap.put(className, em);
                }
                return em;
            } else {
                EntityManager em = this.getEntityManagerFactory().createEntityManager();
                emMap.put(className, em);
                this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> EntityManager(" + className + " Created");
                return em;
            }
        }
    }

    /**
     * セッションビーンの領域を指定してEMFを入手する
     * @return
     */
    protected EntityManagerFactory getEntityManagerFactory() {

        //通常画面処理だとsessionBeanを見てどちらかを選ぶ
        if (RegionType.NORMAL.equals(RegionType.getEnum(
                this.gnomesSessionBean.getRegionType()))) {

            return this.emf;

        } else if (RegionType.STORAGE.equals(RegionType.getEnum(
                this.gnomesSessionBean.getRegionType()))) {

            return this.emfStorage;

        }

        return null;
    }
}
