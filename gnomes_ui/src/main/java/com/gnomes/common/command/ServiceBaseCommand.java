package com.gnomes.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportDataBase;
import com.gnomes.common.importdata.ImportExportColumnDef;
import com.gnomes.common.importdata.ImportExportDefBase;
import com.gnomes.common.importdata.ImportExportDefExcel;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.rest.service.BaseImportFileCommandResponse;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * サービスコマンド 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public abstract class ServiceBaseCommand extends BaseCommand
        implements IServiceCommand {

    @Inject
    RequestServiceFileProducer reqServiceFileProducer;

    /**
     * インポートファイルのバリデーションエラー有無フラグ
     */
    private boolean isImportValidateError = false;

    /**
     * デフォルトコンストラクタ
     */
    public ServiceBaseCommand() {
    }

    public void setupCommand() {
    }

    /**
     * 共通メイン処理（トランザクションなし）
     */
    public void mainExecuteNoTransactional() throws Exception {
        // 継承元でコーディング
    }

    /**
     * トランザクション管理有無
     * @return
     */
    public boolean isTransactional() {
        return true;
    }

    public boolean isImportValidateError() {
        return isImportValidateError;
    }

    /**
    * コマンドID、画面ID、画面名を CotainerRequest に設定する
    * @param コマンドID
    * @param FromBean
    * @param CotainerRequest
    */
    public void setScreenInfo() {

        IServiceFormBean formBean = getFormBean();

        if (formBean != null) {

            this.logHelper.fine(this.logger, null, null, "ScreenId: " + formBean.getScreenId()
                    + "ScreenName: " + formBean.getScreenName());
            //操作内容リソースキーの設定
            requestContext.setOperationResourceKey(
                    operationStr + requestContext.getCommandId());

        } else {
            this.logHelper.fine(this.logger, null, null, "ScreenId: NULL " + "ScreenName: NULL");
        }
    }

    /**
    * Validate処理実行
    * @throws Exception
    */
    public boolean commonValidate(IServiceFormBean formBean) {

        Map<String, String[]> requestParamErr = new HashMap<String, String[]>();

        requestParamErr.putAll(reqParamProducer.getRequestErr());
        requestParamErr.putAll(requestContext.getRequestParamMapErr());

        // パラメータの変換チェックでエラーがない場合
        if (setRequestParamError(formBean.getCheckList(),
                requestParamErr) == false) {

            // formBeanのバリデータによるチェックでエラーがない場合
            if (validateFormBean(formBean.getCheckList(), formBean) == false) {

                // インポートファイルの変換チェックでエラーがない場合
                if (setRequestFileError(
                        reqServiceFileProducer.getRequestErr()) == false) {

                    // インポートファイルのバリデータによるチェック
                    RequestServiceFileInfo reqServiceFileInfo = formBean
                            .getRequestServiceFileInfo();
                    if (reqServiceFileInfo != null) {

                        // インポートデータののバリデータによるチェック
                        for (Entry<String, List<?>> e : reqServiceFileInfo
                                .getRequestServiceFileInfos().entrySet()) {

                            // インポート定義情報
                            ImportExportDefBase impExportDefinition = reqServiceFileInfo
                                    .getImportExportDefinition(e.getKey());

                            for (int i = 0; i < e.getValue().size(); i++) {
                                validateInputRow(i, e.getValue().get(i),
                                        impExportDefinition);
                            }
                        }
                    }
                }
            }
        }
        if (requestErr.entrySet().size() == 0) {
            // requestErrをソート
            sortRequestErr();
            return false;
        }
        return true;
    }

    /**
    * Validate処理結果確認
    * @throws Exception
    */
    public boolean validateResultCheck(IServiceFormBean formBean) {

        isImportValidateError = false;

        // commonValidate,validateのエラー処理
        if (requestErr.entrySet().size() > 0) {

            // インポートのエラーキーパターン
            Pattern pattern = Pattern.compile(ImportDataBase.PATTERN_ERR_KEY);

            /*
             * LinkedHashMap:キーを格納した順に保持
             * を使用するので、並び替え不要
             */
            /*
                       // 2.Map.Entryのリストを作成する
                       List<Entry<String,String[]>> listEntries = new ArrayList<Entry<String, String[]>>(requestErr.entrySet());
                       // 3.比較関数Comparatorを使用してMap.Entryの値を比較する(昇順)
                       Collections.sort(listEntries, new Comparator<Entry<String, String[]>>() {
               public int compare(Entry<String, String[]> obj1, Entry<String, String[]> obj2) {

                               Matcher matcher1 = pattern.matcher(obj1.getKey());
                               Matcher matcher2 = pattern.matcher(obj2.getKey());
                               if(!matcher1.matches() && !matcher2.matches()) {
                       return obj1.getKey().compareTo(obj2.getKey());
                               } else if(!matcher1.matches() && matcher2.matches()) {
                                   return 1;
                               } else if(matcher1.matches() && !matcher2.matches()) {
                                   return -1;
                               } else {
                                   int ans = matcher1.group(2).compareTo(matcher2.group(2));
                                   if (ans != 0) {
                                       return ans;
                                   }
                                   ans = matcher1.group(3).compareTo(matcher2.group(3));
                                   if (ans != 0) {
                                       return ans;
                                   }
                                   ans = matcher1.group(4).compareTo(matcher2.group(4));
                                   return ans;
                               }

               }
                       });
            */
            // メッセージ詳細
            List<MessageData> lstMessageData = new ArrayList<>();
            for (Entry<String, String[]> e : requestErr.entrySet()) {

                if (isImportValidateError == false) {
                    Matcher matcher = pattern.matcher(e.getKey());
                    // キーがインポートのエラーキーパターンにマッチした場合
                    if (matcher.find()) {
                        // インポートファイルのバリデーションエラー
                        isImportValidateError = true;
                    }
                }

                // 子追加
                MessageData addMessageData = this.getErrorMessageData(e);
                lstMessageData.add(addMessageData);
            }

            //操作エラーです。
            MessageData mesOwner = new MessageData(
                    GnomesMessagesConstants.YY01_0013, null);

            responseContext.setMessage(mesOwner,
                    (MessageData[]) lstMessageData.toArray(new MessageData[0]));

            // インポートファイルでエラーの場合
            if (isImportValidateError == true) {
                FileUpLoadData fileData = reqServiceFileProducer
                        .getRequestFile();

                // セッションにファイルを保持
                gnomesSessionBean.setRequestImpErrFile(fileData);

                // セッションにエラーを保持
                gnomesSessionBean.setRequestImpErr(requestErr);

                if ((getCommandResponse() instanceof BaseImportFileCommandResponse)) {
                    // インポートバリデーションエラーを設定(画面のエラーダウンロードリンクを表示するため
                    ((BaseImportFileCommandResponse) getCommandResponse())
                            .setImportValidateError(true);
                }
            }

            return false;
        }

        return true;

    }

    /**
    * リクエストファイルのバリデータによるチェック結果を設定
    * @param convertarErrorList リクエストファイルのバリデータによるチェック結果
    * @return true:エラーあり,false:エラーなし
    */
    private boolean setRequestFileError(
            Map<String, String[]> convertarErrorList) {

        boolean isError = false;

        // コンバータによるエラーチェック
        if (!Objects.isNull(convertarErrorList)
                && convertarErrorList.size() > 0) {
            for (Entry<String, String[]> e : convertarErrorList.entrySet()) {
                requestErr.put(e.getKey(), e.getValue());
                isError = true;
            }
        }
        return isError;
    }

    /**
     * インポート
     * @param rowIndex
     * @param rowBean
     * @param importExportDefinition
     * @return
     */
    private boolean validateInputRow(
            int rowIndex,
            Object rowBean,
            ImportExportDefBase importExportDefinition) {
        boolean isErr = false;
        // バリデータによるチェックリスト
        Set<ConstraintViolation<Object>> validaterErrorList = validator
                .validate(rowBean);

        String sheetStr = "";
        String sheetName = null;

        // Excel判定
        if (importExportDefinition instanceof ImportExportDefExcel) {

            sheetName = ((ImportExportDefExcel) importExportDefinition)
                    .getSheetName();
            if (sheetName != null && sheetName.length() > 0) {
                // "シート「{0}」："
                sheetStr = MessagesHandler.getString(
                        GnomesLogMessageConstants.ME01_0088,
                        requestContext.getUserLocale(),
                        sheetName);
            }
        }

        // バリデータによるチェック
        if (!Objects.isNull(validaterErrorList)
                && validaterErrorList.size() != 0) {

            Iterator<ConstraintViolation<Object>> iterator = validaterErrorList
                    .iterator();
            while (iterator.hasNext()) {

                ConstraintViolation<Object> violation = iterator.next();

                // プロパティ名から、インポートエクスポート定義を取得
                ImportExportColumnDef colDef = new ImportExportColumnDef();
                String propertyPath = violation.getPropertyPath().toString();
                for (ImportExportColumnDef def : importExportDefinition
                        .getImportExportColumDefinitions()) {
                    if (propertyPath.compareTo(def.getFieldName()) == 0) {
                        colDef = def;
                    }
                }

                Map<String, Object> attributes = violation
                        .getConstraintDescriptor().getAttributes();

                List<String> objs = new ArrayList<String>();
                String[] messageParams = null;
                if (attributes.containsKey(MESSAGE_PARAMS)) {
                    // 出力パラメータ対象取得
                    messageParams = (String[]) attributes.get(MESSAGE_PARAMS);

                    for (String messageParam : messageParams) {
                        if (attributes.containsKey(messageParam)) {
                            if (messageParam.equals(RESOURCE_ID)) {
                                objs.add(ResourcesHandler.getString(
                                        attributes.get(messageParam).toString(),
                                        requestContext.getUserLocale()));
                            } else {
                                objs.add(attributes.get(messageParam)
                                        .toString());
                            }
                        }
                    }
                }

                // メッセージマスタからメッセージのリソースIDを取得
                String detailMessageId = violation.getMessage();
                MstrMessageDefine mstrMsgDef = null;

                try {
                    // メッセージ定義を取得
                    mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(detailMessageId);

                } catch (GnomesAppException ea) {
                    // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
                }

                if (!Objects.isNull(mstrMsgDef)) {
                    detailMessageId = mstrMsgDef.getResource_id();
                }

                String mesDetail = MessagesHandler.getString(detailMessageId,
                        requestContext.getUserLocale(), objs.toArray());

                //行    12,列     1：個数は50以下の値を入力してください。
                //行{0,number,     0},列{1,number,     0}：{3}
                //{3}は

                String messageNo = GnomesMessagesConstants.MV01_0017;
                String[] params = {
                        messageNo,
                        sheetStr,
                        String.format(ImportDataBase.FORMAT_ROW_COL,
                                String.valueOf(rowIndex + 1)),
                        String.format(ImportDataBase.FORMAT_ROW_COL,
                                colDef.getPosition()),
                        mesDetail };
                String key = String.format(ImportDataBase.FORMAT_ERR_KEY,
                        importExportDefinition.getKey(), rowIndex + 1,
                        colDef.getPosition());

                if (sheetName != null && sheetName.length() > 0) {
                    // マップキーにシート名を付加
                    key = key + String.format(ImportDataBase.FORMAT_ERR_KEY_OPT_SHEET_NAME, sheetName);
                }

                //putRequestErr(key, params);

                requestErr.put(key, params);

                isErr = true;
            }
        }
        return isErr;
    }

}
