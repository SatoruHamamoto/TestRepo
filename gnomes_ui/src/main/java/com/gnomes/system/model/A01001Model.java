package com.gnomes.system.model;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.DisplayType;
import com.gnomes.common.dto.ICommonPullDown;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.model.BaseModel;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.system.dao.InfoComputerDao;
import com.gnomes.system.data.A01001FunctionBean;
import com.gnomes.system.entity.InfoComputer;
import com.gnomes.system.view.A01001FormBean;

/**
 * ログイン画面 モデル
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/26 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class A01001Model extends BaseModel {

    @Inject
    A01001FunctionBean a01001FunctionBean;

    @Inject
    A01001FormBean a01001FormBean;

    /**
     *  プルダウン取得
     */
    @Inject
    ICommonPullDown commonPullDown;

    /**
     *  端末情報 Dao
     */
    @Inject
    InfoComputerDao infoComputerDao;

    /**
     *  通常領域参照用エンティティマネージャー
     */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private transient EntityManagerFactory emf;

    /**
     * コンストラクター
     */
    public A01001Model(){}

    /**
     * ログイン画面.画面表示データ取得
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void getDispData() throws GnomesAppException {

        // 端末選択プルダウン情報設定
        //a01001FunctionBean.setComputerList(commonPullDown.getPD0006(DisplayType.CLIENT));

        // 拠点選択プルダウン設定
        a01001FunctionBean.setSiteList(commonPullDown.getPD0007());

        // ロケール選択プルダウン設定
        a01001FunctionBean.setLocaleList(commonPullDown.getPD0004());

        EntityManager em = null;
        try {
            em = this.emf.createEntityManager();

            // 端末情報の取得(拠点初期選択状態を設定に必要)
            List<InfoComputer> infoComputerList = infoComputerDao.getInfoComputer(em);
            Map<String, String> computerInfoMap = new HashMap<>();
            for(InfoComputer data: infoComputerList){
            	// 端末ID、拠点コードをマッピング
                computerInfoMap.put(data.getComputer_id(), data.getSite_code());
            }

            // 端末拠点情報設定
            a01001FunctionBean.setComputerInfoMap(computerInfoMap);

            // セッション取得時エラーメッセージの設定
            a01001FunctionBean.setErrMessageNoSession(MessagesHandler.getString(GnomesLogMessageConstants.ME01_0040));

            // システムロケール設定
            a01001FunctionBean.setSystemLocale(Locale.getDefault().toString());
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    /**
     * ログイン画面.端末データ取得
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void getDispDataComputerList() throws GnomesAppException {

        // 端末選択プルダウン設定
//    	a01001FunctionBean.setComputerList(commonPullDown.getPD0005(gnomesSessionBean.getUserId()));
        a01001FunctionBean.setComputerList(commonPullDown.getPD0006(DisplayType.CLIENT));

    }
}
