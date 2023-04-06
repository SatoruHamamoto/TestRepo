package com.gnomes.system.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.UploadfileSaveMode;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.dao.UploadFileDao;
import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.system.entity.UploadFile;

/**
 * ファイルアップロードダウンロード 共通処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/26 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BLFileManager extends BaseLogic {

    @Inject
    UploadFileDao uploadfileDao;

    @Inject
    MstrSystemDefineDao mstrSystemDefineDao;

    /** ファイル名登録ログのフォーマット */
    private static final String LOG_REG_FILENAME_FORMAT =
            UploadFile.COLUMN_NAME_UPLOAD_FILE_KEY + "=[{0}]," +
            UploadFile.COLUMN_NAME_FOLDER_NAME + "=[{1}]," +
            UploadFile.COLUMN_NAME_SYSTEM_FILE_NAME + "=[{2}]," +
            UploadFile.COLUMN_NAME_FILE_NAME + "=[{3}]";

    /** ファイル登録ログのフォーマット */
    private static final String LOG_REG_FILE_FORMAT = "systemFile=[{0}], uploadFile=[{1}]";

    /** 削除ログのフォーマット */
    private static final String LOG_DELETE_FORMAT = "systemFile=[{0}]";

    /** ファイル参照ログのフォーマット */
    private static final String LOG_GETFILE_FORMAT = UploadFile.COLUMN_NAME_UPLOAD_FILE_KEY + "=[{0}],systemFile=[{1}]";

    public BLFileManager() {}


    /**
     * ファイル名の登録
     * @param subPathSystemDefineCode サブフォルダパス取得先システム定義コード
     * @param saveMode 保存モード
     * @param isCompulsion 強制フラグ
     * @param items アップロードファイル情報
     * @return false:登録不可（上書き不可） true:登録完了
     * @throws GnomesAppException 例外
     */
    @TraceMonitor
    @ErrorHandling
    public boolean registrationFileName(String subPathSystemDefineCode, UploadfileSaveMode saveMode, boolean isCompulsion, List<FileUpLoadData> items) throws GnomesAppException {

    	// サブフォルダパスを取得
    	String folderPath = getSubFolderPath(subPathSystemDefineCode);

        // 重複不可の場合排他?
        // 重複可能の場合はDB存在確認なし
        List<UploadFile> logInfos = new ArrayList<UploadFile>();

        if (saveMode == UploadfileSaveMode.NoOverlap) {

            for (FileUpLoadData item : items) {

                // アップロードファイル管理テーブルより取得
                List<UploadFile> datas = uploadfileDao.getUploadFileWithFileName(folderPath, item.getFileName());

                // データが存在し、強制でない場合
                if (isCompulsion == false && datas.size() > 0) {
                    return false;
                }

                // データが存在している場合
                if (datas.size() > 0) {
                    // システムファイル名をセットする
                    item.setSystemFileName(datas.get(0).getSystem_file_name());
                    logInfos.add(datas.get(0));

                // データが存在しない場合
                } else {
                    try
                    {
                        // アップロードファイル管理を追加
                        UploadFile newItem = insertUploadfile(saveMode, folderPath, item.getFileName());
                        // システムファイル名をセットする
                        item.setSystemFileName(newItem.getSystem_file_name());
                        logInfos.add(newItem);
                    }
                    catch (ParseException e)
                    {
                        // メッセージ： アップロード時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\nファイル名： {0}
                        GnomesAppException ex =exceptionFactory.createGnomesAppException(e);
                        ex.setMessageNo(GnomesMessagesConstants.ME01_0042);
                        Object[] errParam = {
                                item.getFileName()
                        };
                        ex.setMessageParams(errParam);
                        throw ex;
                    }
                }
            }

        // 重複可能の場合
        } else {
            for (FileUpLoadData item : items) {
                try
                {
                    // アップロードファイル管理を追加
                    UploadFile newItem = insertUploadfile(saveMode, folderPath, item.getFileName());
                    // システムファイル名をセットする
                    item.setSystemFileName(newItem.getSystem_file_name());
                    logInfos.add(newItem);
                }
                catch (ParseException e)
                {
                    // メッセージ： アップロード時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\nファイル名： {0}
                    GnomesAppException ex =exceptionFactory.createGnomesAppException(e);
                    ex.setMessageNo(GnomesMessagesConstants.ME01_0042);
                    Object[] errParam = {
                            item.getFileName()
                    };
                    ex.setMessageParams(errParam);
                    throw ex;
                }

            }
        }

        // トレースログ
        // アップロードファイル名登録処理を実行しました。登録内容=[{0}]
        // {0}は [ファイル管理キー、フォルダーパス、システムファイル名, アップロードファイル名] 複数の場合は改行で
        StringBuilder logParam = new StringBuilder();
        for (UploadFile logItem : logInfos) {
            if (logParam.length() > 0) {
                logParam.append(StringUtil.PROPERTY_DEFAULT_SEPARATOR);
            }

            logParam.append(
                    MessageFormat.format(LOG_REG_FILENAME_FORMAT,
                            logItem.getUpload_file_key(),
                            logItem.getFolder_name(),
                            logItem.getSystem_file_name(),
                            logItem.getFile_name()
                            ));
        }

        String log = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0010,logParam.toString());
        this.logHelper.fine(this.logger, null, null, log);

        // 上書き確認が必要な場合、false
        return true;
    }

    /**
     * アップロードファイル管理の追加
     * @param saveMode 保管モード
     * @param folderPath フォルダー名
     * @param fileName ファイル名
     * @return アップロードファイル管理レコード
     * @throws ParseException
     */
    private UploadFile insertUploadfile(UploadfileSaveMode saveMode, String folderPath, String fileName) throws ParseException {

        String systemFileName = UUID.randomUUID().toString();

        UploadFile insertItem = new UploadFile();
        // ファイル管理キー
        insertItem.setUpload_file_key(UUID.randomUUID().toString());
        // ファイル名
        insertItem.setFile_name(fileName);
        // フォルダー名
        insertItem.setFolder_name(folderPath);
        // 保管モード
        insertItem.setSave_mode(saveMode.getValue());
        // システムファイル名
        insertItem.setSystem_file_name(systemFileName);
        // リクエスト情報
        insertItem.setReq(req);
//        // 更新日
//        insertItem.setRegistereddate(ConverterUtils.utcToDate(ZonedDateTime.now()));
//        // 更新者コード
//        insertItem.setRegistrantcode(req.getUserId());
//        // 更新者名
//        insertItem.setRegistrantname(req.getUserName());

        uploadfileDao.insert(insertItem);

        return insertItem;
    }

    /**
     * ファイル登録
     * @param folderPath フォルダーパス
     * @param items アップロードファイル情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void registrationFile(String folderPath, List<FileUpLoadData> items) throws GnomesAppException {

        String basePath = getRootPath();

        for (FileUpLoadData item : items) {
            String fileName = basePath + "\\" + folderPath + "\\" + item.getSystemFileName();
            try {
                writeFile(item.getData(), fileName);
            } catch (IOException e) {
                // 保存エラー
                // メッセージ： アップロード時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\nファイル名： {0}
                GnomesAppException ex =exceptionFactory.createGnomesAppException(e);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0042);
                Object[] errParam = {
                        item.getFileName()
                };
                ex.setMessageParams(errParam);
                throw ex;
            }
        }

        // トレースログ
        // アップロードファイル登録処理を実行しました。登録内容=[{0}]
        // {0}は [システムファイル名パス, アップロードファイル名] 複数の場合は改行で
        StringBuilder logParam = new StringBuilder();
        for (FileUpLoadData item : items) {
            if (logParam.length() > 0) {
                logParam.append(StringUtil.PROPERTY_DEFAULT_SEPARATOR);
            }

            String fileName = basePath + "\\" + folderPath + "\\" + item.getSystemFileName();
            logParam.append(
                    MessageFormat.format (LOG_REG_FILE_FORMAT,
                            fileName,
                            item.getFileName()));
        }

        String log = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0011, logParam.toString());
        this.logHelper.fine(this.logger, null, null, log);
    }


    /**
     * ファイル削除
     * @param folderPath フォルダーパス
     * @param sysFileNames システムファイル名
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void deleteFile(String folderPath, List<String> sysFileNames) throws GnomesAppException {

        String basePath = getRootPath();

        // アップロードファイル管理　削除
        for (String sysFileName : sysFileNames) {
            for (UploadFile item : uploadfileDao.getUploadFileWithSysFileName(folderPath, sysFileName)) {
                uploadfileDao.detele(item);
            }
        }

        // 実ファイル削除
        for (String sysFileName : sysFileNames) {
            String filePath = basePath + "\\" + folderPath + "\\" + sysFileName;
            File file = new File(filePath);
            if (file.exists()){
                if (!file.delete()){
                    // ファイル削除に失敗しました。
                    // メッセージ： ファイル削除時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\nファイル名： {0}
                    GnomesAppException ex =exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0043, sysFileName);
                    throw ex;
                }
            }
        }

        // トレースログ
        // アップロードファイル削除処理を実行しました。削除内容=[{0}]
        // {0}は [システムファイル名パス] 複数の場合は改行で
        StringBuilder logParam = new StringBuilder();
        for (String sysFileName : sysFileNames) {
            if (logParam.length() > 0) {
                logParam.append(StringUtil.PROPERTY_DEFAULT_SEPARATOR);
            }

            String filePath = basePath + "\\" + folderPath + "\\" + sysFileName;
            logParam.append(
                    MessageFormat.format( LOG_DELETE_FORMAT,
                            filePath));
        }

        String log = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0012, logParam.toString());
        this.logHelper.fine(this.logger, null, null, log);
    }

    /**
     * ファイル参照
     * @param subPathSystemDefineCode サブフォルダパス取得先システム定義コード
     * @param sysFileNames システムファイル名
     * @return ファイルダウンロード情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<FileDownLoadData> getFile(String subPathSystemDefineCode, List<String> sysFileNames) throws GnomesAppException {

        List<UploadFile> logInfos = new ArrayList<>();
        List<FileDownLoadData> items = new ArrayList<>();

        // サブフォルダパスを取得
    	String folderPath = getSubFolderPath(subPathSystemDefineCode);
        String basePath = getRootPath();

        for (String sysFileName : sysFileNames) {
            List<UploadFile> uploadItems = uploadfileDao.getUploadFileWithSysFileName(folderPath, sysFileName);
            if (uploadItems.size() == 0) {
                // ファイルが存在しません
                // メッセージ： 参照ファイルが存在しません。\nファイル名： {0}
                GnomesAppException ex =exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0044, sysFileName);
                throw ex;
            }

            for (UploadFile uploadItem : uploadItems) {

                logInfos.add(uploadItem);

                FileDownLoadData addItem = new FileDownLoadData();

                String filePath = basePath + "\\" + folderPath + "\\" + sysFileName;
                addItem.setLoadFilePath(filePath);
                addItem.setSaveFileName(uploadItem.getFile_name());

                File file = new File(filePath);
                if (!file.exists()) {
                    // ファイルが存在しません
                    // メッセージ： ファイル読込時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\nファイル名： {0}
                    GnomesAppException ex =exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0045, sysFileName);
                    throw ex;
                }
                items.add(addItem);
            }


        }

        // トレースログ
        // ファイル参照処理を実行しました。参照内容=[{0}]
        // {0}は [ファイル管理キー、システムファイル名パス] 複数の場合は改行で
        StringBuilder logParam = new StringBuilder();
        for (UploadFile uploadItem : logInfos) {
            if (logParam.length() > 0) {
                logParam.append(StringUtil.PROPERTY_DEFAULT_SEPARATOR);
            }

            String filePath = basePath + "\\" + folderPath + "\\" + uploadItem.getSystem_file_name();
            logParam.append(
                    MessageFormat.format( LOG_GETFILE_FORMAT,
                            uploadItem.getUpload_file_key(),
                            filePath));
        }

        String log = MessagesHandler.getString(GnomesLogMessageConstants.MG01_0013, logParam.toString());
        this.logHelper.fine(this.logger, null, null, log);

        return items;
    }

    /**
     * アップロード保管フォルダパス名（システム共通のルートフォルダ）を取得
     * @return アップロード保管フォルダパス名（システム共通のルートフォルダ）
     * @throws GnomesAppException
     */
    private String getRootPath() throws GnomesAppException {
        String rootPath = null;

        MstrSystemDefine item = mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.UPLOAD,SystemDefConstants.UPLOAD_ROOT_PATH);
        if (item != null) {
            rootPath = item.getChar1();
        }
        return rootPath;
    }


    /**
     * ファイル保存
     * @param content 保存データ
     * @param filename 保存ファイル名パス
     * @throws IOException 書き込み例外
     */
    private void writeFile(byte[] content, String filename) throws IOException {

        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = null;
        try
        {
            fop = new FileOutputStream(file);
            fop.write(content);
            fop.flush();
        }
        finally
        {
            if (fop != null) {
                fop.close();
            }
        }

    }

    /**
     * サブフォルダパスを取得
     * @param subPathSystemDefineCode サブフォルダパス取得先システム定義コード
     * @return ファイルパス
     * @throws GnomesAppException
     */
    private String getSubFolderPath(String subPathSystemDefineCode) throws GnomesAppException {

        String subFolderPath = null;

        // システム定義マスタよりサブフォルダパスを取得
        MstrSystemDefine mstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.UPLOAD_SUB_PATH, subPathSystemDefineCode);

        // サブフォルダパスを取得できた場合
        if (mstrSystemDefine != null && !StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {
        	subFolderPath = mstrSystemDefine.getChar1();
        }
        // サブフォルダパスを取得できなかった場合、エラー
        else {
            StringBuilder params = new StringBuilder();
            params.append(MstrSystemDefine.TABLE_NAME);
            params.append(CommonConstants.PERIOD).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_TYPE);
            params.append(CommonConstants.COLON).append(SystemDefConstants.UPLOAD_SUB_PATH);
            params.append(CommonConstants.COMMA).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_CODE);
            params.append(CommonConstants.COLON).append(subPathSystemDefineCode);
            // メッセージ：データが見つかりません。（テーブル名, 区分, コード）
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
            		GnomesMessagesConstants.ME01_0026, params.toString());
            throw ex;
        }

        return subFolderPath;
    }

}
