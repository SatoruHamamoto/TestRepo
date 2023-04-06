package com.gnomes.common.logic;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.uiservice.ContainerRequest;

/**
 * ロジック 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/25 YJP/Y.Oota                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public abstract class BaseLogic {

    @Inject
    protected
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected
    ContainerRequest req;

    @Inject
    protected
    GnomesSessionBean gnomesSessionBean;

    @Inject
    protected
    GnomesSystemBean gnomesSystemBean;

    @Inject
    protected
    GnomesExceptionFactory exceptionFactory;

    /**
     * デフォルトコンストラクタ
     */
    public BaseLogic() {
    }

    /**
     * 遷移画面スタックに存在するか
     * @param screenId 画面ID
     * @return true:存在する false:存在しない
     */
    protected boolean existScreeenStack(String screenId) {
        String windowId = req.getWrapperRequest().getParameter(ContainerRequest.REQUEST_NAME_WINDOW_ID);
        return gnomesSessionBean.existScreeenStack(windowId, screenId);
    }

    /**
     * 直前の戻る画面IDを取得
     * @return 画面ID
     */
    protected String getPreviousScreenId() {
        String windowId = req.getWrapperRequest().getParameter(ContainerRequest.REQUEST_NAME_WINDOW_ID);
        String previousScreenId = gnomesSessionBean.getPreviousScreenId(windowId);
        return previousScreenId;
    }

    /**
     * 遷移画面スタックをクリアする
     */
    protected void clearScreenStack() {
        String windowId = req.getWrapperRequest().getParameter(ContainerRequest.REQUEST_NAME_WINDOW_ID);
        String screenId = req.getScreenId();
        gnomesSessionBean.clearScreenStack(windowId, screenId);
    }

    /**
     * ビーンパラメータ取得
     * 引数のプロパティ名に対応するGetterより値を取得する
     * @param bean ビーン
     * @param propertyName プロパティ名
     * @return value 取得データ
     * @throws GnomesAppException
     */
    protected Object getObjectPropertyValue(Object bean, String propertyName) throws GnomesAppException {

    	try {
            PropertyDescriptor srcProperties = new PropertyDescriptor(
            		propertyName, bean.getClass());

            Method getter = srcProperties.getReadMethod(); //getter取得

            Object value = getter.invoke(bean, (Object[]) null); //プロパティ値を取得

            return value;

        } catch (Exception e) {
        	// エラー
            // ME01.0158：「パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}」
        	logHelper.severeOfResourceID(logger, GnomesMessagesConstants.ME01_0158, propertyName);
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0158, propertyName);

        }
    }

    /**
     * ビーンパラメータ取得(複数項目)
     * 引数のプロパティ名に対応するGetterより値を取得する
     * @param bean ビーン
     * @param propertyName プロパティ名
     * @return map Key:プロパティ名 Value:取得データ
     * @throws GnomesAppException
     */
    protected Map<String, Object> getObjectPropertyValueMap(Object bean, List<String> propertyNameList) throws GnomesAppException {

    	Map<String, Object> map = new HashMap<>();

		for (String propertyName: propertyNameList) {
			try {
                PropertyDescriptor srcProperties = new PropertyDescriptor(
                		propertyName, bean.getClass());

                Method getter = srcProperties.getReadMethod(); //getter取得

                Object value = getter.invoke(bean, (Object[]) null); //プロパティ値を取得

                map.put(propertyName, value);
			} catch (Exception e) {
	        	// エラー
	            // ME01.0158：「パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}」
	        	logHelper.severeOfResourceID(logger, GnomesMessagesConstants.ME01_0158, propertyName);
	            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0158, propertyName);
		    }
		}
        return map;
    }

    /**
     * ビーンパラメータ設定
     * 引数のプロパティ名に対応するSetterより値を設定する
     * @param bean ビーン
     * @param propertyName プロパティ名
     * @param value 設定値
     * @throws GnomesAppException
     */
    protected void setObjectPropertyValue(Object bean, String propertyName, Object value) throws GnomesAppException {

    	try {
    		PropertyDescriptor dstProperties = new PropertyDescriptor(
                    propertyName, bean.getClass());

    		Method setter = dstProperties.getWriteMethod(); //setter取得

    		setter.invoke(bean, value); //プロパティ値をセット

        } catch (Exception e) {
        	// エラー
            // ME01.0163：「プロパティ設定でエラーが発生しました。詳細についてはログを確認してください。オブジェクト：{0} 、プロパティ：{1}」
        	logHelper.severeOfResourceID(logger, GnomesMessagesConstants.ME01_0163, bean.getClass().getName(), propertyName);
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0163, bean.getClass().getName(), propertyName);

        }
    }
}
