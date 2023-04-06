package com.gnomes.external.logic.talend;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * HULFT送信要求処理呼出
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class CallHULFTSendRequestJob extends BaseJobLogic {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /**
     * HULFT送信要求処理呼出
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws GnomesAppException, Exception {

        // Webサービスで外部プログラムを起動する。
        String hulftCommand = fileTransferBean.getHulftSendCommand();
        String fileId = fileTransferBean.getFileType();
        String sendRecvFileName = fileTransferBean.getSendRecvFileName();
        String moveToFolderName = fileTransferBean.getMoveToFolderName();

        ProcessBuilder pb = new ProcessBuilder("cmd.exe" ,"/c" , hulftCommand,"-f",fileId,"-file",moveToFolderName + sendRecvFileName);

        // 形跡を残す
        StringBuilder sb = new StringBuilder();
        sb.append("cmd.exe /c ");
        sb.append(hulftCommand);
        sb.append(" -f " + fileId);
        sb.append(" -file " + moveToFolderName + sendRecvFileName);

        logHelper.info(logger, "CallHULFTSendRequestJob","process", sb.toString());

        try {

            // bat実行
            Process process = pb.start();

            boolean end = process.waitFor(10, TimeUnit.SECONDS); //10秒でタイムアウト

            // 要求処理待機時間(秒)待機

            // 呼出失敗した場合
            if (!end) {
                // ME01.0135:「外部I/Fファイル連携処理：{0} （処理名：{1})、ファイル名:{2}にてエラーが発生しました。　エラーの詳細については、メッセージ履歴を確認してください。」
                GnomesAppException ex = exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0135,
                        ResourcesHandler.getString(GnomesResourcesConstants.OPE_S01005C200),
                        CallHULFTSendRequestJob.class.getSimpleName(),
                        fileTransferBean.getSendRecvFileName());

                throw ex;
            }

        } catch (IOException e) {
            // ME01.0135:「外部I/Fファイル連携処理：{0} （処理名：{1})、ファイル名:{2}にてエラーが発生しました。　エラーの詳細については、メッセージ履歴を確認してください。」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0135,
                    ResourcesHandler.getString(GnomesResourcesConstants.OPE_S01005C200),
                    CallHULFTSendRequestJob.class.getSimpleName(),
                    fileTransferBean.getSendRecvFileName());

            throw ex;
        }


    }

}
