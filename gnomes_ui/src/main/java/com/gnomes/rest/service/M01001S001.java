package com.gnomes.rest.service;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.system.logic.BLMessage;

/**
 * メッセージサービス リソースクラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/12 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

// メッセージサービス のリソース名 （クラス単位）
@Path("M01001S001")
@RequestScoped
public class M01001S001 extends BaseService {
    @Context
    private UriInfo context;

    /**
     * メッセージ機能
     */
    @Inject
    BLMessage blMessage;


    public M01001S001() {
    }

    /**
     * ポップアップメッセージを取得する。
     * @return List<PopupMessageInfo>
     */
    @Path("getPoupuMessage")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    //[2019/03/05 浜本記載]
    // 独自トランザクションのため外す
    //@GnomesTransactional

    public PopupMessage getPoupuMessage(){

    	PopupMessage popupMessage = new PopupMessage();

    	try {
    		popupMessage.setPopupMessageInfoList(blMessage.getPopupMessage());
    		popupMessage.setIsSuccess(ConverterUtils.boolToInt(true));
    	}
    	catch(Exception e) {

            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            String errMessage = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0001);

            //エラーログ
            this.logHelper.severe(this.logger, null, null, errMessage);
            this.logHelper.severe(this.logger, null, null, e.getMessage(), e);

            popupMessage.setPopupMessageInfoList(new ArrayList<>());
    		popupMessage.setIsSuccess(ConverterUtils.boolToInt(false));
    	}
    	return popupMessage;
    }

}
