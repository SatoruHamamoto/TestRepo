package com.gnomes.common.interceptor;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.weld.context.SessionContext;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundSessionContext;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;


/**
 * SessionContextをマニュアルでアクティベートするインターセプター
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/06 YJP/K.Nakanishi           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@SessionContextOperation
public class SessionContextOperationInterceptor {

    /** The SessionContext */
    @Inject
    private BoundSessionContext m_sessionContext;

    @Inject
    private GnomesSessionBean gnomesSessionBean;

    @Inject
    private GnomesEjbBean       ejbBean;

    /*
    * システム定義 Dao
    */
   @Inject
   protected MstrSystemDefineDao mstrSystemDefineDao;

    /**
     * @param p_invocationContext
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object activateSessionContext(InvocationContext p_invocationContext) throws Exception {
        try {
            HashMap<String, Object> currentSessionMap = new HashMap<String, Object>();
        	m_sessionContext.associate(currentSessionMap);
        	m_sessionContext.activate();

        	// システム定数：ログイン時の拠点選択設定 の取得
            MstrSystemDefine mstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.SECURITY, SystemDefConstants.CODE_SITE_SELECTABLE_SETTING);

            // セッション Baen設定（ インターセプターによりSessionContextをマニュアルでアクティベートしている為）
            // ただしEJBバッチ用では処理しない
            if(! ejbBean.isEjbBatch()){
                this.gnomesSessionBean.setRegionType(RegionType.NORMAL.getValue());
                this.gnomesSessionBean.setSiteCode(mstrSystemDefine.getChar1());
            }
            //バッチの場合はejbBeanに設定する
            else {
                this.ejbBean.setSiteCode(mstrSystemDefine.getChar1());
                this.ejbBean.setRegionType(RegionType.NORMAL.getValue());
            }

            return p_invocationContext.proceed();
        } finally {
        	m_sessionContext.invalidate();
        	m_sessionContext.deactivate();
        }
    }
}
