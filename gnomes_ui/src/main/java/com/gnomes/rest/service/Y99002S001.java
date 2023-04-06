package com.gnomes.rest.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.gnomes.common.interceptor.GnomesTransactional;

/**
*
* JAX-RS Web サービス共通リソースクラス （コマンド使用）
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/

@Path("Y99002S001")
@RequestScoped
public class Y99002S001 extends BaseServiceLogic {


    /**
     * ファイルインポート
     * @param input
     * @return
     */
    @Path("importDataFile")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public RestServiceResult importDataFile(MultipartFormDataInput input) {

        return doLogic(input);

    }

    /**
     * リモート接続.
     * @param input
     * @return
     */
    @Path("remoteConnect")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public RestServiceResult remoteConnect(RemoteConnectDataInput input) {

        return doLogic(input);

    }

    /**
     * アップロード
     * @param input
     * @return 処理結果
     */
    @Path("uploadFile")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public RestServiceResult uploadFile(MultipartFormDataInput input) {

        return doLogic(input);

    }

    /**
     * windowを閉じる
     * @param input
     * @return 処理結果
     */
    @Path("closeWindow")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public RestServiceResult closeWindow(MultipartFormDataInput input) {

        return doLogic(input);

    }

    /**
     * sample
     * @param input
     * @return 処理結果
     */
    @Path("sample")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public RestServiceResult sample(MultipartFormDataInput input) {

        return doLogic(input);

    }

}
