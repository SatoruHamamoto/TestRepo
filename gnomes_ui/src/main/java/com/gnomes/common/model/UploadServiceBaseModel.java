package com.gnomes.common.model;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.rest.service.MultiPartCommon;

/**
 * アップロードサービス モデル基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/02/13 YJP/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class UploadServiceBaseModel extends BaseModel {

    @Inject
    protected MultiPartCommon multiPartCommon;

    protected List<FileUpLoadData> uploadFiles;

    /**
     * アップロードファイル情報取得
     * @throws GnomesAppException　例外
     */
    protected List<FileUpLoadData> getUploadFiles() throws GnomesAppException {

    	// サービスのリクエストからアップロードファイル取得
        List<InputPart> inputParts = req.getServiceRequestFile();
        if (inputParts == null) {

            String itemLabel = ResourcesHandler.getString(GnomesResourcesConstants.DI02_0207, gnomesSessionBean.getUserLocale());

            // メッセージ： "{0}ファイルを選択してください。"
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0095, itemLabel);
            throw ex;

        }

        // アップロードファイル情報取得
        uploadFiles = multiPartCommon.getUploadFiles(inputParts);

        return uploadFiles;

    }
}
