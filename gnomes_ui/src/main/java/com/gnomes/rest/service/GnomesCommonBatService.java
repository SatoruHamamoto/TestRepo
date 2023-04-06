package com.gnomes.rest.service;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesEjbBean;



/**
 * Gnomes共通バッチ サービスリソースクラス （コマンド使用）
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Path("GnomesCommonBatService")
@RequestScoped
public class GnomesCommonBatService extends BaseServiceLogic {

    @Inject
    protected GnomesEjbBean   ejbBean;
    /**
     * 定周期呼出
     * @param input
     * @return
     */
    @Path("scheduler")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public RestServiceResult importDataFile(MultipartFormDataInput input) {

        // ユーザ情報はテーブル等から取ってきて、固定のユーザ情報を登録

        return doLogic(input);

    }


    /**
     * バッチ起動
     * @param input サービス呼び出しパラメータ
     * @return
     */
    @Path("startBatchProcess")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestServiceResult startBatchProcess(GnomesWebServiceDataInput input) {

        ejbBean.setEjbBatch(true);

        setCommonBatch();

        return doLogic(input);

    }


    /**
     * バッチ起動時の共通情報を設定
     */
    private void setCommonBatch() {

        // エリアID
        req.setAreaId("");
        // エリア名
        req.setAreaName("");
        // コンピュータ名
        req.setComputerName("");
        // IPアドレス
        req.setIpAddress(CommonConstants.IPADDRESS_LOCALHOST);
        // 言語
        req.setLanguage(Locale.getDefault().getLanguage());
        // ユーザロケール
        req.setUserLocale(Locale.getDefault());

        // 拠点コード
        req.setSiteCode("");
        // 拠点名
        req.setSiteName("");
        // ユーザID
        req.setUserId("SYS_BATCH");
        // ユーザ名
        req.setUserName(CommonConstants.USERNAME_BATCH);
        // ユーザKey
        req.setUserKey("");
    }

}
