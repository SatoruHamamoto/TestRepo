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
* JAX-RS Web ブックマークサービス
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2018/02/01 YJP/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/

@Path("BookMarkService")
@RequestScoped
public class Y99003S001 extends BaseServiceLogic {

    /**
     * ブックマーク
     * @param input インプット
     * @return 処理結果
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public RestServiceResult doCommand(MultipartFormDataInput input) {

        return doLogic(input);

    }


}
