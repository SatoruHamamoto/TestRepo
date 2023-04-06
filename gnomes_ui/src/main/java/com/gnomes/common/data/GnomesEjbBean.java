package com.gnomes.common.data;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.dao.MstrSiteDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSite;
import com.gnomes.system.entity.MstrSystemDefine;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/08 10:50 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * DBトランザクション他バッチ処理内部で使われるリクエストスコープのBean
 * エンティティマネージャーを複数保持しており用途に応じて複数のトランザクションを
 * 管理することができる。
 * バッチ処理ではprocess(BaseBatchletを継承したクラス）に必ずInjectすること
 * さもなくば、複数のインスタンスが作成される可能性あり
 *
 * @author 03501213
 *
 */
@RequestScoped
public class GnomesEjbBean {

    /**
     * クラス名（ログ用）
     */
    private static final String className="GnomesEjbBean";

    /** エンティティマネージャーファクトリ（通常領域）
     * バッチの場合は通常領域しかアクセスできない
    */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    protected EntityManagerFactory emf;

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /*
    * システム定義 Dao
    */
   @Inject
   protected MstrSystemDefineDao mstrSystemDefineDao;

   /**
    * サイトマスタDAO
    */
   @Inject
   protected MstrSiteDao mstrSiteDao;

    /**
     * サイトコード（バッチ用）
     */
    private String siteCode;

    /**
     * サイト名（バッチ用）
     */
    private String siteName;

    /**
     * 領域区分
     */
    private String regionType;

    /**
     * サイトコードを取得する
     * @return バッチ用で指定したサイトコード
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * サイトコードを設定する
     * @param siteCode 設定するサイトコード
     */
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    /**
     * サイト名を取得する
     * @return  バッチ用で指定したサイト名
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * サイト名を設定する
     * @param siteName  設定するサイト名
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * 領域を取得する（バッチの場合は通常）
     * @return
     */
    public String getRegionType() {
        return regionType;
    }

    /**
     * 領域を設定する
     *
     * @param regionType
     */
    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    /**
     * この処理はEJBスレッドかそうでないかを示す
     * EJBスレッドは処理を始める前に必ずtrueにする
     * falseだと通常画面サーブレットから呼ばれる処理としてDBが管理される
     */
    private boolean isEjbBatch = false;

    /**
     * エンティティマネージャーを名前付きで複数管理する
     * EJBの中ではTarendの中でステータスを管理するトランザクションと
     * 一般BLの中で処理するトランザクションを複数管理することがあり、例えば
     * 一般BLでエラーになってロールバックしてもステータスや履歴はコミットする
     * 用途があるため
     */
    private HashMap<String, EntityManager> emMap = new HashMap<>();

    /**
     * オブジェクト初期処理を行う
     * バッチ処理されることを示すオブジェクトに生まれたとき、サイトコードや領域などの変数を設定する
     * @throws GnomesAppException
     */
    private void initForEjbBatch() throws GnomesAppException
    {
        //サイトコードをシステム定義から入手する
        // システム定数：ログイン時の拠点選択設定 の取得
        MstrSystemDefine mstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.SECURITY, SystemDefConstants.CODE_SITE_SELECTABLE_SETTING);

        setRegionType(RegionType.NORMAL.getValue());
        setSiteCode(mstrSystemDefine.getChar1());

        //サイトコードを取得してrequestContextに設定する
        MstrSite mstrSite = mstrSiteDao.getMstrSite(this.siteCode);

        //サイトリストが取れたとき（取れなかったらnullのまま）
        if (!Objects.isNull(mstrSite)) {
            setSiteName(mstrSite.getSite_name());
        }

    }

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
        return getEntityManager(GnomesEjbBean.class.getName());
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
                    em = this.emf.createEntityManager();
                    this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> EntityManager(" + className + " Created");
                    emMap.put(className, em);
                }
                return em;
            } else {
                EntityManager em = this.emf.createEntityManager();
                emMap.put(className, em);
                this.logHelper.fine(this.logger, className, methodName, "<TRANSACTION> EntityManager(" + className + " Created");
                return em;
            }
        }
    }

    /**
     * この処理はEJBスレッドかそうでないかを示す
     * EJBスレッドは処理を始める前に必ずtrueにする
     * falseだと通常画面サーブレットから呼ばれる処理としてDBが管理される
     * @return false:画面から呼ばれる true:EJBから呼ばれる
     */
    public boolean isEjbBatch() {
        return isEjbBatch;
    }

    /**
     * EJBスレッドだということを明示する
     * EJBスレッドは処理を始める前に必ずtrueでセットする
     */
    public void setEjbBatch(boolean isEjbBatch) {
        final String methodName = "setEjbBatch";
        this.isEjbBatch = isEjbBatch;
        if(isEjbBatch){
            try {
                initForEjbBatch();
            } catch (Exception e) {
                //念のためExceptionをトレースする
                this.logHelper.severe(this.logger, className, methodName, e.getMessage(),e);
            }
        }
    }

}
