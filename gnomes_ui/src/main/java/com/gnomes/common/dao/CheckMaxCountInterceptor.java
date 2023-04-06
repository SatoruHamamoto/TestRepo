package com.gnomes.common.dao;

import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.search.data.SearchSetting.DispType;
import com.gnomes.uiservice.ContainerResponse;

/**
 * 最大件数チェック　インターセプター
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/27 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@Interceptor
@CheckMaxCount
@Dependent
@Priority(Interceptor.Priority.LIBRARY_AFTER)
public class CheckMaxCountInterceptor {

    @Inject
    protected
    ContainerResponse responseContext;

    @AroundInvoke
    public Object checkMaxCountHandling(InvocationContext context) throws Exception {

        SearchSetting searchSetting = null;

        String clazz = context.getTarget().getClass().getSuperclass().getName();
        String method = context.getMethod().getName();

        // パラメータから検索条件を取得
        for (Object obj : context.getParameters()) {
            if (obj instanceof SearchSetting ) {
                searchSetting = (SearchSetting) obj;
                break;
            }
        }
        if (searchSetting == null) {
            // 例外
            GnomesAppException ex =new GnomesAppException("searchSetting is null");
            // 最大件数チェック時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\n（メソッド：{0}.{1}）
            ex.setMessageNo(GnomesMessagesConstants.ME01_0077);
            Object[] errParam = {
                    clazz,
                    method
            };
            ex.setMessageParams(errParam);
            throw ex;
        }

        // 表示タイプがリストでない場合
        if (searchSetting.getDispType() != DispType.DispType_List) {

            GnomesAppException ex =new GnomesAppException("searchSetting dispType not list");
            // 最大件数チェック時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\n（メソッド：{0}.{1}）
            ex.setMessageNo(GnomesMessagesConstants.ME01_0077);
            Object[] errParam = {
                    clazz,
                    method
            };
            ex.setMessageParams(errParam);
            throw ex;
        }

        // メソッド実行
        List<?> lstData = (List<?>) context.proceed();

        if (lstData.size() > searchSetting.getMaxDispCount()) {
            // MG01.0001：「検索件数は最大表示件数（{0}を超えています。検索条件の絞り込みを行ってください。）」
            responseContext.setMessageInfo(GnomesMessagesConstants.MG01_0001,searchSetting.getMaxDispCount());
            //最後を削除
            lstData.remove(searchSetting.getMaxDispCount());
        }

        return lstData;
    }

}
