package com.gnomes.external.logic.talend;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;

/**
 * ファイル名リネーム
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.SHibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class FileRenameJob extends BaseJobLogic {

    /**
     * ファイル名リネーム
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws Exception {

        // ファイル名をリネームする。
        String fileName = fileTransferBean.getSendRecvFileName();
        String orgFileName = fileName;

        // ファイルのリネーム
        String recvFilePath = fileTransferBean.getMoveFromFolderName();

        // 共通機能のシステム日時を取得し、fileNameに追記する。
        synchronized (this) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String datePattern = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0054);

            fileName = fileName + ConverterUtils.tsToString(timestamp, datePattern);

            File file = new File(recvFilePath, orgFileName);
            File fileMove = new File(recvFilePath, fileName);
            // 同じファイル名のファイルがある場合、再度ファイル名指定
            if (fileMove.exists()) {
                timestamp = new Timestamp(System.currentTimeMillis());
                datePattern = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0054);

                fileName = orgFileName + ConverterUtils.tsToString(timestamp, datePattern);
                fileMove = new File(recvFilePath, fileName);
            }
            //参照元ファイルが存在しないならばエラーにしてスローする
            if(! file.exists()) {
                //ME01.0044:参照ファイルが存在しません。\nファイル名： {0} */
                String msg = MessagesHandler.getString(GnomesMessagesConstants.ME01_0044,recvFilePath+orgFileName);
                Map<Integer, String> errorLineInfo = new HashMap<>();
                errorLineInfo.put(0, msg);
                super.fileTransferBean.setErrorLineInfo(errorLineInfo);

                throw super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0044,
                        new Object[]{recvFilePath+orgFileName});
            }

            if (!file.renameTo(fileMove)) {

                // ME01.0191:ファイル名の変更に失敗しました。（ディレクトリパス: {0}、変更前: {1}、変更後: {2}）
                String msg = MessagesHandler.getString(GnomesMessagesConstants.ME01_0191,recvFilePath, orgFileName, fileName);
                Map<Integer, String> errorLineInfo = new HashMap<>();
                errorLineInfo.put(0, msg);
                super.fileTransferBean.setErrorLineInfo(errorLineInfo);

                throw super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0191,
                        new Object[]{recvFilePath, orgFileName, fileName});

            }
        }
        fileTransferBean.setSendRecvFileName(fileName);
    }

}
