package com.gnomes.rest.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gnomes.common.util.ConverterUtils;
import com.gnomes.system.data.CertInfo;

/**
 * セキュリティサービス リソースクラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/19 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

// セキュリティサービス のリソース名 （クラス単位）
@Path("A01001S000")
@RequestScoped
public class A01001S000 extends BaseService {

    /** コンストラクタ */
    public A01001S000() {

    }

    /**
     * セッション情報設定
     * @param certInfo 認証情報
     * @return 認証情報
     */
    @Path("setSessionBean")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertInfo setSessionBean(CertInfo certInfo) {

        // 領域区分
        gnomesSessionBean.setRegionType(certInfo.getRegionType());
        certInfo.setIsSuccess(ConverterUtils.boolToInt(true));
        return certInfo;

    }

}
