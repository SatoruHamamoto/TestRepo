package com.gnomes.system.logic;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJbatchLogic;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * 帳票印刷プレビュー バッチ呼び出し クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class PrintPreviewCallBatchlet extends BaseJbatchLogic
{

    /** コンストラクタ */
    public PrintPreviewCallBatchlet()
    {

    }

    /**
     * 帳票印刷プレビュー バッチ呼び出し
     * @param dupCheck バッチ重複チェック有無
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void printPreviewJbatch(String localeId, Boolean dupCheck) throws Exception
    {

        // PrintPreviewBatchletを実行
        Map<String, String> mapProperties = new HashMap<>();
        mapProperties.put(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0106), localeId);
        this.runBatch("gnomes_printPreviewBatchlet", mapProperties, dupCheck);

    }

}
