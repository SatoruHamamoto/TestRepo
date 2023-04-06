package com.gnomes.system.logic;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;

/**
 * 帳票印刷プレビュー処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
/**
 * @author Jixin.Sun
 *
 */
@Dependent
public class K02APrintPreview extends PrintPreviewLogic
{

    /**
     * キュー処理専用クラス
     * トランザクション制御のため別クラスに移動
     */
    @Inject
    protected K02A01PrintPreviewQueueProc k02A01PrintPreviewQueueProc;

    /** クラス名 */
    protected static final String         CLASS_NAME       = "com.gnomes.system.logic.K02APrintPreview";

    /** メソッド名 (バッチからコールメソッドを指定されるため) */
    protected static final String         MAIN_METHOD_NAME = "printPreview";

    /**
     * 帳票印刷プレビュー処理.
     * <pre>
     * 印刷プレビューキューより印刷プレビュー要求を取り出し、帳票印刷プレビュー処理を行う。
     * 本処理でキューに溜まった複数の印刷要求を処理し、何もなくなったら終わる
     * 1つの印刷要求が何らかの原因で失敗しても他のキューは処理される
     * </pre>
     * @param localeId          ロケールＩＤ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void printReview(String localeId) throws GnomesAppException
    {
        /** メソッド名：帳票印刷プレビュー処理 */
        final String methodName = "printPreview";

        try {

            //  帳票印刷プレビュー設定情報チェック
            if (Objects.isNull(super.gnomesSystemBean.getcGenReportMeta())) {
                // 帳票印刷プレビュー設定情報が Web.xml に正しく設定されていません。
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0161);
            }

            // 無限ループ始まり
            boolean continueFlag = true;
            while (continueFlag) {

                try {

                    // 1件ずつキューを呼んで処理する
                    // キューがなければfalseで戻る
                    continueFlag = k02A01PrintPreviewQueueProc.printPreviewQueueProc(localeId);

                }
                catch (Exception e) {
                    // ログに記録する
                    this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage());
                    // 印刷プレビューキューが削除されず、無限ループになる可能性があるのでbreak;
                    continueFlag = false;
                }
            }

        }
        catch (Exception e) {
            // ログに記録する
            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * クラス名取得
     * @return クラス名
     */
    public String getClassName()
    {
        return CLASS_NAME;
    }

    /**
     * メソッド名取得
     * @return メソッド名
     */
    public String getMethodName()
    {
        return MAIN_METHOD_NAME;
    }

}
