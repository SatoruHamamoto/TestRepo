package com.gnomes.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

/**
 * モデル 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/01 YJP/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public abstract class BaseModel {

    @Inject
    protected
    transient Logger logger;

    @Inject
    protected
    ContainerRequest req;

    @Inject
    protected
    ContainerResponse responseContext;

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
    public BaseModel() {
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
	 * 表示用のテーブルヘッダーを設定する。
	 *
	 * @param bean    BaseFunctionBean
	 * @param tableId テーブルID
	 * @param map     ヘッダーマップ
	 */
	protected void setTableDispHeaders(BaseFunctionBean bean, String tableId, Map<String, String> map) {
		bean.getTableDispHeaders().put(tableId, map);
	}

	/**
	 * 表示用のテーブルヘッダーを設定する。
	 *
	 * @param bean       BaseFunctionBean
	 * @param tableId    テーブルID
	 * @param columnName テーブルカラム名
	 * @param value      値
	 */
	protected void setTableDispHeader(BaseFunctionBean bean, String tableId, String columnName, String value) {
		Map<String, String> headerMap = bean.getTableDispHeaders().get(tableId);
		if (headerMap == null) {
			headerMap = new HashMap<>();
			bean.getTableDispHeaders().put(tableId, headerMap);
		}
		headerMap.put(columnName, value);
	}

	 /**
     * tagIdの対応するパーツ権限情報を取得
     * @param buttonId ボタンID
     * @param stmPartsPrivilegeResultInfo パーツ権限情報List
     * @return パーツ権限情報
     */
    protected PartsPrivilegeResultInfo getPartsPrivilegeResultInfo(final String buttonId, List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo) {

        PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

        if (stmPartsPrivilegeResultInfo != null) {

            List<PartsPrivilegeResultInfo> partsPrivilegeResultInfos =
                    stmPartsPrivilegeResultInfo.stream()
                    .filter(item -> item.getButtonId().equals(buttonId))
                    .collect(Collectors.toList());
            if (partsPrivilegeResultInfos.size() == 1) {
                partsPrivilegeResultInfo = partsPrivilegeResultInfos.get(0);
            }
        }
        return partsPrivilegeResultInfo;
    }
}
