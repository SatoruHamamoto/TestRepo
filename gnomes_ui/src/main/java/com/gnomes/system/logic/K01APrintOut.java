package com.gnomes.system.logic;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.picketbox.util.StringUtil;

import com.gnomes.common.batch.batchlet.BaseBatchlet;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.PrintoutStatus;
import com.gnomes.common.constants.CommonEnums.ReprintFlag;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.HistoryPrintoutParameter;
import com.gnomes.system.entity.TmpQueuePrintout;
import com.gnomes.system.entity.TmpQueuePrintoutParameter;

import biz.grandsight.ex.rs.CReportGen;
import biz.grandsight.ex.rs.CReportGenException;

/**
 * 帳票印刷処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/19 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
/**
 * @author 03501213
 *
 */
@Dependent
public class K01APrintOut extends PrintOutLogic {
    /** バッチエンティティマネージャー */
    @Inject
    protected GnomesEntityManager em;

    /**
     * キュー処理専用クラス
     * トランザクション制御のため別クラスに移動
     */
    @Inject
    protected K01A01PrintOutQueueProc k01A01PrintOutQueueProc;

    /** クラス名 */
    protected static final String className = "com.gnomes.system.logic.K01APrintOut";

    /** メソッド名 (バッチからコールメソッドを指定されるため) */
    protected static final String mainMethodName = "print";

    /**
     * 帳票印刷処理.
     * <pre>
     * 印字キューより印字要求を取り出し、帳票印字処理を行う。
     * 本処理でキューに溜まった複数の印刷要求を処理し、何もなくなったら終わる
     * 1つの印字要求が何らかの原因で失敗しても他のキューは処理される
     * </pre>
     * @param localeId          ロケールＩＤ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void print(String localeId) throws GnomesAppException {
        /** メソッド名：帳票印刷処理 */
        final String methodName = "print";

        // バッチ処理実行中設定
        BaseBatchlet.setBatchInProcessMap(className, methodName);

        try {

            //  帳票印字設定情報チェック
            if (Objects.isNull(super.gnomesSystemBean.getcGenReportMeta())) {
                // 帳票印字設定情報が Web.xml に正しく設定されていません。
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0161);
            }

            //無限ループ始まり
            boolean continueFlag = true;
            while (continueFlag) {

                try {

                    //1件ずつキューを呼んで処理する
                    //キューがなければfalseで戻る
                    continueFlag = k01A01PrintOutQueueProc.printOutQueueProc(localeId);

                } catch (Exception e) {
                    //ログに記録する
                    this.logHelper.severe(logger, className, methodName, e.getMessage());
                    // 印字キューが削除されず、無限ループになる可能性があるのでbreak;
                    continueFlag = false;
                }
            }
        } catch (Exception e) {
            //ログに記録する
            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);
            throw e;
        } finally {
            // バッチ処理実行中クリア
            BaseBatchlet.clearBatchInProcessMap(className, methodName);
        }
    }

    /**
     * クラス名取得
     * @return クラス名
     */
    public String getClassName() {
        return className;
    }

    /**
     * メソッド名取得
     * @return メソッド名
     */
    public String getMethodName() {
        return mainMethodName;
    }

}
