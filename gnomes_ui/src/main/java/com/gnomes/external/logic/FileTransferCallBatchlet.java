package com.gnomes.external.logic;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJbatchLogic;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * ファイル送信受信 batchlet呼出クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/15 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class FileTransferCallBatchlet extends BaseJbatchLogic {


    /**
     * デフォルト・コンストラクタ
     */
    public FileTransferCallBatchlet() {

    }

    /**
     * batchlet呼出
     */
    @TraceMonitor
    @ErrorHandling

    public void sampleJbach() throws Exception {

        // fileTransferBeanに受け渡し情報を設定

        // FileTransferSendProcBatchletを実行
        this.runBatch("gnomes_fileTransferRecvProcBatchlet", false);
    }

    /**
     * 送信状態登録batchlet呼出
     * @param <T>
     */
    @TraceMonitor
    @ErrorHandling

    public void insertSendStateJbatch() throws Exception {

        // fileTransferBeanに受け渡し情報を設定

        // FileTransferSendProcBatchletを実行
        this.runBatch("gnomes_insertSendStateBatchlet", false);
    }



    /**
     * 送信状態更新batchlet呼出
     * @param <T>
     */
    @TraceMonitor
    @ErrorHandling

    public void updateSendStateJbatch() throws Exception {

        // fileTransferBeanに受け渡し情報を設定

        // FileTransferSendProcBatchletを実行

        this.runBatch("gnomes_updateSendStateBatchlet", false);
    }

    /**
     * 送信状態登録、更新batchlet呼出
     * @param <T>
     */
    @TraceMonitor
    @ErrorHandling
    public void insertAndUpdateSendStateJbatch() throws Exception {

        // fileTransferBeanに受け渡し情報を設定

        // FileTransferSendProcBatchletを実行
          Map<String, String> mapProperties = new HashMap<String,String>() {
              {
                  put("index", "6");
              }
          };

        this.runBatch("gnomes_insertAndUpdateSendStateBatchlet", mapProperties, false);
    }

    /**
     * 受信定周期処理batchlet呼出
     * @param <T>
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void recvProcJbatch(String externalTargetCode, Boolean dupCheck) throws Exception {

        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0055), externalTargetCode);
            }
        };
        this.runBatch(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0062), mapProperties, dupCheck);
    }

    /**
     * 受信要求batchlet呼出
     * @param <T>
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void recvRequestJbatch(String dataType, String recvFileName) throws Exception {

        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0056), dataType);
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0057), recvFileName);
            }
        };
        this.runBatch(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0063), mapProperties, false);
    }

    /**
     * 受信状態変更batchlet呼出（再受信）
     * @param <T>
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void recvChangeStateJbatch(String externalIfRecvQueKey, String isChangeStateflag) throws Exception {
        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0058), externalIfRecvQueKey);
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0059), isChangeStateflag);
            }
        };
        this.runBatch(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0064), mapProperties, false);
    }

    /**
     * ファイル送信処理batchlet呼出
     * @param <T>
     * @throws Exception
     */
    public void sendProcJbatch(String externalTargetCode, Boolean dupCheck) throws Exception {

        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0055), externalTargetCode);
            }
        };
        this.runBatch(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0065), mapProperties, dupCheck);
    }

    /**
     * ファイル送信結果batchlet呼出
     * @param <T>
     * @throws Exception
     */
    public void sendResultJbatch(String externalTargetCode, String isSendSuccess) throws Exception {

        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0055), externalTargetCode);
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0060), isSendSuccess);
            }
        };
        this.runBatch(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0066), mapProperties, false);
    }

    /**
     * 送信状態変更batchlet呼出
     * @param <T>
     * @throws Exception
     */
    public void sendChangeStateJbatch(String sendStateKey, String isChangeStateflag) throws Exception {

        Map<String, String> mapProperties = new HashMap<String,String>() {
            {
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0061), sendStateKey);
                put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0059), isChangeStateflag);
            }
        };
        this.runBatch(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0067), mapProperties, false);
    }

}
