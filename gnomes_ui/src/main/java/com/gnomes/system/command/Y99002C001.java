package com.gnomes.system.command;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.gnomes.common.command.ICommandQualifier;
import com.gnomes.common.command.IScreenCommand;
import com.gnomes.common.command.ScreenBaseCommand;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exportdata.ExportDataCsv;
import com.gnomes.common.exportdata.ExportDataExcel;
import com.gnomes.common.importdata.ImportDataBase;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.StringUtils;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.common.view.ResourcePathConstants;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.system.view.Y99002FormBean;

/**
 *
 * インポートファイルエラー ダウンロード
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/22 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_Y99002C001)
public class Y99002C001 extends ScreenBaseCommand implements IScreenCommand {

    /**
     * インポートファイルエラー ダウンロード ビーン
     */
    @Inject
    Y99002FormBean y99002FormBean;

    @Inject
    MstrMessageDefineDao mstrMessageDefineDao;

    @Inject
    ExportDataCsv exportDataCsv;

    @Inject
    ExportDataExcel exportDataExcel;

    /**
     * コンストラクター
     */
    public Y99002C001() {
        // ページトークン更新なし
        setTokenUpdateFlgFalse();
    }

    @TraceMonitor
    @ErrorHandling
    @Override
    @GnomesTransactional
    public void mainExecute() throws Exception {

        List<FileDownLoadData> files = new ArrayList<FileDownLoadData>();

        FileDownLoadData file = this.getExportImportErrorData();

        files.add(file);
        responseContext.setFileDownLoadDatas(files);

    }

    /**
     * インポートファイルのエラー情報をダウンロード形式で取得する
     * @return FileDownLoadData
     * @throws GnomesAppException
     * @throws Exception
     */
    private FileDownLoadData getExportImportErrorData()
            throws GnomesAppException {
        // セッションにあるエラーインポートファイルとエラー情報より
        //   ない場合はエラー

        FileDownLoadData errFileDownLoadData = null;
        String uploadFileName = "";

        try {
            FileUpLoadData fileUpLoadData = gnomesSessionBean
                    .getRequestImpErrFile();

            uploadFileName = fileUpLoadData.getFileName();

            // エラー情報を取得
            Map<String, String[]> impErr = gnomesSessionBean.getRequestImpErr();

            // 追加情報
            Map<String, List<String>> addData = new LinkedHashMap<String, List<String>>();

            // シートごとに作成
            Pattern sheetNamePattern = Pattern
                    .compile(ImportDataBase.PATTERN_ERR_KEY_POT_SHEET_NAME);

            for (Entry<String, String[]> e : impErr.entrySet()) {

                String sheetName;

                Matcher matcher = sheetNamePattern.matcher(e.getKey());

                // シート名あり
                if (matcher.find()) {
                    sheetName = matcher.group(1);
                    // シート名なし
                } else {
                    sheetName = "";
                }

                List<String> datas = addData.get(sheetName);
                List<String> addList = new ArrayList<String>();
                // まだ未登録
                if (datas == null) {
                    // (エラー情報)
                    addList.add(ResourcesHandler.getString(
                            GnomesResourcesConstants.DI01_0103,
                            requestContext.getUserLocale()));

                    // 登録済み
                } else {
                    addList.addAll(datas);
                }

                // エラーメッセージ追加
                addList.add(getErrorMessage(e));

                addData.put(sheetName, addList);
            }

            // ファイル名の拡張子からファイルタイプを判別
            // 拡張子取得
            String suffix = StringUtils.getSuffix(fileUpLoadData.getFileName());

            // 拡張子からファイルタイプ判定
            ImportDataBase.ImportFileType type = null;
            for (ImportDataBase.ImportFileType d : ImportDataBase.ImportFileType
                    .values()) {
                for (String ext : d.getValue()) {
                    if (ext.equalsIgnoreCase(suffix)) {
                        type = d;
                        break;
                    }
                }
            }

            errFileDownLoadData = new FileDownLoadData();
            byte[] outByte = null;

            // CSVファイルの場合
            if (type == ImportDataBase.ImportFileType.FileType_Csv) {

                // ファイルに追加
                outByte = exportDataCsv.addTail(fileUpLoadData.getData(),
                        fileUpLoadData.getCharsetName(), addData, 1);

                // Excelファイルの場合
            } else if (type == ImportDataBase.ImportFileType.FileType_Excel) {

                outByte = exportDataExcel.addTail(fileUpLoadData.getData(),
                        addData, 1);

            }

            errFileDownLoadData
                    .setSaveFileName(
                            getErrFileName(fileUpLoadData.getFileName()));
            errFileDownLoadData.setData(outByte);

        } catch (EncryptedDocumentException | InvalidFormatException
                | IOException | NullPointerException
                | IllegalArgumentException e1) {
            //   メッセージ： "エラーファイルの作成処理でエラーが発生しました。 詳細はエラーメッセージを確認してください。 ファイル名： {0}")
            GnomesAppException ex = new GnomesAppException(e1);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0097);
            Object[] errParam = {
                    uploadFileName
            };
            ex.setMessageParams(errParam);
            throw ex;

        }

        return errFileDownLoadData;
    }

    /**
     * エラーファイル名を取得
     * @param fileName ファイル名
     * @return エラーファイル名
     * @throws GnomesAppException
     */
    private String getErrFileName(String fileName) throws GnomesAppException {

        MstrSystemDefine item = mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.IMPORT_EXPORT,
                SystemDefConstants.IMPORT_EXPORT_IMPORT_ERR_FILENAME_FORMAT);

        String errFileNameFormat = item.getChar1();
        String[] f = fileName.split("\\.");

        MessageFormat messageFormat = new MessageFormat(errFileNameFormat);
        return messageFormat.format(f);
    }

    @Override
    public void preExecute() throws Exception {
        // 処理なし

    }

    @Override
    public void postExecute() throws Exception {
        // 処理なし

    }

    @Override
    public void initCheckList() throws GnomesAppException, Exception {
        // 処理なし

    }

    @Override
    public void validate() throws GnomesAppException {
        // 処理なし

    }

    @Override
    public void setFormBeanForRestore() throws GnomesException {
        // 処理なし

    }

    @Override
    public IScreenFormBean getFormBean() {
        return y99002FormBean;
    }

    @Override
    public void setDefaultForward() {
        responseContext
                .setForward(ResourcePathConstants.RESOURCE_PATH_DOWNLOAD);
    }

}
