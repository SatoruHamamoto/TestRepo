package com.gnomes.common.batch.batchlet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.uiservice.ContainerRequest;

/**
 * EJB 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/20 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Stateless
public class BaseEJB {

    @Inject
    protected ContainerRequest containerRequest;


    protected void setSessionInfo() throws Exception{
    	 // セッション Baen
        GnomesSessionBean gnomesSessionBeanEjb = new GnomesSessionBean();
		// ユーザ情報、認証情報設定
		// ユーザKey
		gnomesSessionBeanEjb.setUserKey("");
		// ユーザID
        gnomesSessionBeanEjb.setUserId(CommonConstants.USERID_SYSTEM);
        // ユーザ名
        gnomesSessionBeanEjb.setUserName(CommonConstants.USERNAME_SYSTEM);
        // パスワード
        gnomesSessionBeanEjb.setPassword("");
        // ロケールID
        gnomesSessionBeanEjb.setLocaleId("");
        // 言語
        gnomesSessionBeanEjb.setLanguage("");
        // IPアドレス
        gnomesSessionBeanEjb.setIpAddress("");
        // 初期設定時コンピュータ名
        gnomesSessionBeanEjb.setInitComputerName("");
        // エリアID
        gnomesSessionBeanEjb.setAreaId("");
        // エリア名
        gnomesSessionBeanEjb.setAreaName("");
        // サイトID
        //gnomesSessionBeanEjb.setSiteCode("");
        // サイト名
        //gnomesSessionBeanEjb.setSiteName("");
        // トップ画面ID
        gnomesSessionBeanEjb.setTopScreenId("");
        // トップ画面名
        gnomesSessionBeanEjb.setTopScreenName("");
        // スクリーンロック中か否か
        gnomesSessionBeanEjb.setIsScreenLocked(false);
        // スクリーンロック起動時間（分）
        gnomesSessionBeanEjb.setScreenLockTimeoutTime(10);
        // メッセージ一覧画面最大表示件数
        gnomesSessionBeanEjb.setMaxListDisplayCount(100);
        // ポップアップメッセージ表示件数
        gnomesSessionBeanEjb.setPopupDisplayCount(100);
        // ポップアップメッセージ監視周期
        gnomesSessionBeanEjb.setWatchPeriodForPopup(10);

        containerRequest.setGnomesSessionBeanEjb(gnomesSessionBeanEjb);

    }
}
