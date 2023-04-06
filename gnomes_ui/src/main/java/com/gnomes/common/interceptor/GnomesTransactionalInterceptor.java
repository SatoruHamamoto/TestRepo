package com.gnomes.common.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.FlushModeType;

import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.uiservice.ContainerRequest;

/**
 * トランザクション管理インターセプター
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
@Interceptor
@GnomesTransactional
@Dependent
@Priority(Interceptor.Priority.APPLICATION)
public class GnomesTransactionalInterceptor {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    /** エンティティマネージャー */
    @Inject
    protected GnomesEntityManager em;

    /** コンテナリクエスト  */
    @Inject
    protected ContainerRequest containerRequest;

    /** 呼び出し件数カウント */
    private static Map<String, Integer> countMap = new ConcurrentHashMap<>();

    /**
     * ハンドリング処理
     * @param context コンテキスト
     * @return 処理戻り値
     * @throws Exception 例外
     */
    @AroundInvoke
    public Object handling(InvocationContext context) throws Exception {

        String className = context.getTarget().getClass().getSuperclass().getName();
        String methodName = context.getMethod().getName();

        // 開始ログ
        this.logHelper.fine(this.logger, className, methodName,
                String.format("%s#%s [start]", className, methodName));

        Object result = null;

        try {

            if (countMap.containsKey(this.containerRequest.getEventId())) {
                // トランザクションが既に開始されている場合
                Integer count = countMap.get(this.containerRequest.getEventId());
                countMap.remove(this.containerRequest.getEventId());
                countMap.put(this.containerRequest.getEventId(), ++count);
            } else {
                // トランザクション開始
                this.em.begin();
                em.getEntityManager().setFlushMode(FlushModeType.AUTO);
                countMap.put(this.containerRequest.getEventId(), 1);
            }

            // 実行
            result = context.proceed();

            if (countMap.containsKey(this.containerRequest.getEventId())) {

                Integer count = countMap.get(this.containerRequest.getEventId());
                countMap.remove(this.containerRequest.getEventId());

                if (count <= 1) {
                    // コミット
                    this.em.commit();
                } else {
                    countMap.put(this.containerRequest.getEventId(), --count);

                }

            }

            return result;

        } catch (Exception e) {
            // ロールバック
            this.em.rollback();
            countMap.remove(this.containerRequest.getEventId());
            // 例外をそのままスロー
            throw e;
        } finally {
            if (!countMap.containsKey(this.containerRequest.getEventId())) {
                // クローズ
                this.em.close();
            }
            // 終了ログ
            this.logHelper.fine(this.logger, className, methodName,
                    String.format("%s#%s [end]", className, methodName));
        }

    }

}
