package com.gnomes.external.data;

import java.util.List;

import com.gnomes.common.constants.CommonEnums.ExternalIfSendIsSummary;

/**
 * 送信伝文情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class SendTelegramInfo {

    /** 送信伝文リスト */
    private List<String> sendConvDataList;

    /** まとめ可否 */
    private ExternalIfSendIsSummary isSummary;

    /** 送信伝文リストを取得
     * @return 送信伝文リスト
     */
    public List<String> getSendTelegramList() {
        return sendConvDataList;
    }

    /**
     * 送信伝文リストを設定
     * @param sendTelegramList 送信伝文リスト
     */
    public void setSendTelegramList(List<String> sendTelegramList) {
        this.sendConvDataList = sendTelegramList;
    }

    /**
     * まとめ可否を取得
     * @return まとめ可否
     */
    public ExternalIfSendIsSummary getIsSummary() {
        return isSummary;
    }

    /**
     * まとめ可否を設定
     * @param isSummary まとめ可否
     */
    public void setIsSummary(ExternalIfSendIsSummary isSummary) {
        this.isSummary = isSummary;
    }

}
