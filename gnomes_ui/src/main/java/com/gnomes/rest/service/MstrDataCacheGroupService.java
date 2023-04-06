package com.gnomes.rest.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * マスタデータキャッシュグループサービスリソースクラス （コマンド使用）
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/25 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Path("MstrDataCacheGroupService")
@RequestScoped
public class MstrDataCacheGroupService extends BaseServiceLogic {

    /**
     * キャッシュグループリセット.
     * <pre>
     * キャッシュグループに属するマスタ定義情報の初期化を行う。
     * </pre>
     * @param input Webサービスデータインプット
     * @return Webサービス結果
     */
    @Path("reset")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestServiceResult reset(GnomesWebServiceDataInput input) {

        // 共通情報設定
        super.setReqOtherInfo();

        return doLogic(input);

    }

}
