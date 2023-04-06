package com.gnomes.system.logic;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.data.PopupMessageInfo;
import com.gnomes.system.model.PopupMessageModel;

/**
 * メッセージ機能
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/12 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BLMessage extends BaseLogic {

    /** ポップアップメッセージ機能 */
    @Inject
    protected PopupMessageModel popupMessageInfoData;

    /** GnomesExceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory gnomesExceptionFactory;

    /**
     * デフォルト・コンストラクタ
     */
    public BLMessage() {
    }


    /**
     * ポップアップメッセージ取得
     * @param popupMessageInfoList ポップアップメッセージ情報一覧
     */
    @TraceMonitor
    @ErrorHandling
    public List<PopupMessageInfo> getPopupMessage() throws GnomesException
    {
        List<PopupMessageInfo> result = null;

        try {
            result = popupMessageInfoData.getPopupMessage();
        }
        catch (Exception e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = gnomesExceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            throw ex;
        }

        return result;
    }

}
