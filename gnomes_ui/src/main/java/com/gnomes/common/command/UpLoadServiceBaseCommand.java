package com.gnomes.common.command;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.rest.service.FileUploadResponse;
import com.gnomes.rest.service.MultiPartCommon;

public abstract class UpLoadServiceBaseCommand extends ServiceBaseCommand {

    @Inject
    protected MultiPartCommon multiPartCommon;

    protected FileUploadResponse cmdResponse = new FileUploadResponse();

    protected List<FileUpLoadData> uploadFiles;

    /**
     * リスエストの上書き可
     */
    private static final String REQUEST_IS_OVERWRITE_OK = "1";

    @Override
    public Object getCommandResponse() {
        return cmdResponse;
    }

    /**
     * 処理成功フラグを設定
     * @param isSuccess 処理成功フラグ
     */
    protected void setResponseIsSuccess(boolean isSuccess) {
        cmdResponse.setIsSuccess(isSuccess);
    }

    /**
     * 上書き確認設定
     * @param nextCommand OKまたはCancel選択後の実行コマンド
     * @param mes 上書き確認メッセージ
     */
    protected void setResponsOverWriteConfirm(String nextCommand, String mes) {
        cmdResponse.setIsCheckOverwrite(true);
        cmdResponse.setMessage(mes);
        cmdResponse.setCommand(nextCommand);
    }

    /**
     * 上書きフラグ判定
     * @param isOverwrite 上書きフラグ
     * @return true:上書きOK false:上書きCancel
     */
    protected boolean isOverwrite(String isOverwrite) {
        return REQUEST_IS_OVERWRITE_OK.equals(isOverwrite);
    }



    /**
     * ファイル情報をレスポンスに設定
     */
    protected void setResponseFile() {
        List<String> fileNames = new ArrayList<>();

        for (FileUpLoadData item : uploadFiles) {
            fileNames.add(item.getFileName());
        }

        // ファイル名を返す
        cmdResponse.setFilenames(fileNames);

    }


    /**
     * アップロードバリデーション
     * @throws GnomesAppException　例外
     */
    protected void upLoadValidate(String titleResId) throws GnomesAppException {

        List<InputPart> inputParts = requestContext.getServiceRequestFile();
        if (inputParts == null) {
            //   メッセージ： "{0}ファイルを選択してください。"
            String itemLabel = ResourcesHandler.getString(titleResId, gnomesSessionBean.getUserLocale());
            String[] errParam = {
                    GnomesMessagesConstants.ME01_0095,
                    itemLabel
            };

            requestErr.put(titleResId, errParam);
        } else {
            uploadFiles = multiPartCommon.getUploadFiles(inputParts, requestErr);
        }
    }
}
