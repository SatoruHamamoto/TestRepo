package com.gnomes.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import com.gnomes.common.command.RequestServiceFile.MultiPartFileType;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportDataCsv;
import com.gnomes.common.importdata.ImportDataExcel;
import com.gnomes.common.importdata.ImportExportDefBase;
import com.gnomes.common.importdata.ImportExportDefCsv;
import com.gnomes.common.importdata.ImportExportDefExcel;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.rest.service.MultiPartCommon;
import com.gnomes.uiservice.ContainerRequest;

/**
 * リクエストサービスファイル プロデューサー。
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
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class RequestServiceFileProducer {

    //ロガー
    @Inject
    transient Logger logger;

    @Inject
    ContainerRequest requestContext;

    @Inject
    GnomesSessionBean gnomesSessionBean;

    @Inject
    MultiPartCommon multiPartCommon;

    @Inject
    GnomesCTagDictionary gnomesCTagDictionary;

    @Inject
    ImportDataCsv importDataCsv;

    @Inject
    ImportDataExcel importDataExcel;

    // リクエスト時のパラメータ変換エラー情報
    private Map<String, String[]> requestErr = new LinkedHashMap<String, String[]>();

    // リクエスト時のファイル
    private FileUpLoadData requestFile = null;

    /**
     * リクエスト時のパラメータ変換エラー情報
     * @return  requestErr Map<String, String[]>
     */
    public Map<String, String[]> getRequestErr() {
        return requestErr;
    }

    /**
     * リクエスト時のファイルを取得
     * @return FileUpLoadData
     */
    public FileUpLoadData getRequestFile() {
        return requestFile;
    }

    /**
     * マルチパートファイル取得
     *
     * @param ip インジェクトポイント
     * @return String 取得値
     * @throws GnomesAppException
     */
    @Produces
    @RequestServiceFile
    public RequestServiceFileInfo getRequestServiceFile(InjectionPoint ip)
            throws GnomesAppException {

        Map<String, List<?>> infos = new HashMap<String, List<?>>();
        List<ImportExportDefBase> impExportDefinitions = new ArrayList<ImportExportDefBase>();

        RequestServiceFile annotation = ip.getAnnotated()
                .getAnnotation(RequestServiceFile.class);

        // ファイル取得
        List<InputPart> inputParts = requestContext.getServiceRequestFile();
        if (inputParts == null) {
            //   メッセージ： "{0}ファイルを選択してください。"
            String itemLabel = ResourcesHandler.getString(
                    annotation.resourceId(), requestContext.getUserLocale());
            String[] errParam = {
                    GnomesMessagesConstants.ME01_0095,
                    itemLabel
            };

            requestErr.put(annotation.resourceId(), errParam);
            return null;
        }
        List<FileUpLoadData> items = multiPartCommon.getUploadFiles(inputParts,
                requestErr);

        if (items.size() == 0) {
            // getUploadFilesでファイルサイズオーバの場合、itemsには追加されない
            return null;
        }

        // 文字コードを設定
        items.get(0).setCharsetName(annotation.charsetName());

        // リクエストファイルの保持
        requestFile = items.get(0);

        // 定義キー分繰り返し
        for (int i = 0; i < annotation.definitionKey().length; i++) {

            // 定義キーでテーブル辞書よりインポートエクスポート定義情報を取得
            //			ImportExportDefinition importExportDefinition =
            //					getImportExportDefinition(annotation.definitionKey()[i], requestContext.getUserLocale());

            //			ImportExportDefBase importExportDefinition =
            //					new ImportExportDefBase(gnomesTagDictionary, annotation.definitionKey()[i],requestContext.getUserLocale());

            ImportExportDefBase importExportDef = null;

            Class<?> clazz = annotation.dataClazz()[i];

            List<?> datas = null;

            // ファイルタイプ判定
            if (annotation.fileType() == MultiPartFileType.CSVFILE) {
                // csvファイルインポート
                importExportDef = new ImportExportDefCsv(
                        gnomesCTagDictionary, annotation.definitionKey()[i],
                        requestContext.getUserLocale());

                datas = importDataCsv.opencsvToBean(
                        (ImportExportDefCsv) importExportDef,
                        items.get(0),
                        clazz,
                        requestErr,
                        requestContext.getUserLocale());
            } else {

                // 対象シート名
                String sheetName = annotation.sheetName()[i];

                importExportDef = new ImportExportDefExcel(
                        gnomesCTagDictionary, annotation.definitionKey()[i],
                        sheetName, requestContext.getUserLocale());

                // excelファイルインポート
                datas = importDataExcel.poiToBean(
                        (ImportExportDefExcel) importExportDef,
                        items.get(0),
                        clazz,
                        requestErr,
                        requestContext.getUserLocale());
            }

            impExportDefinitions.add(importExportDef);
            infos.put(annotation.definitionKey()[i], datas);
        }

        RequestServiceFileInfo result = new RequestServiceFileInfo();
        result.setRequestServiceFileInfos(infos);
        result.setImportExportDefinitions(impExportDefinitions);

        return result;
    }

}
