package com.gnomes.system.dto;

import java.math.BigInteger;

/**
 * 関連テーブル：M201:message DTO
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MessageCntDto {

    /** テーブル名 */
    public static final String TABLE_NAME = "M201:message";

    /** 発生全件数 */
    public static final String ALL_CNT = "allCnt";

    /** 発生中全件数 */
    public static final String OCCUR_CNT = "occurCnt";

    /** アラーム全件数 */
    public static final String ALERT_CNT = "alertCnt";

    /** オペレーションガイド全件数 */
    public static final String OPERATION_GUIDE_CNT = "operationGuideCnt";

    /** 発生全件数 */
    private BigInteger allCnt;
//    /** 発生中全件数 */
//    private BigInteger occurCnt;
    /** アラーム全件数 */
    private BigInteger alertCnt;
    /** オペレーションガイド全件数 */
    private BigInteger operationGuideCnt;

    /**
     * MessageCntDto・コンストラクタ
     */
    public MessageCntDto() {
    }

    /**
     * MessageCntDto・コンストラクタ
     * @param allCnt 発生全件数
     * @param occurCnt 発生中全件数
     * @param alartCnt アラーム全件数
     * @param operationGuideCnt オペレーションガイド全件数
     */
    public MessageCntDto(BigInteger allCnt, BigInteger alertCnt, BigInteger operationGuideCnt) {
        super();
        this.allCnt = allCnt;
//        this.occurCnt = occurCnt;
        this.alertCnt = alertCnt;
        this.operationGuideCnt = operationGuideCnt;
    }

    /**
     * 発生全件数を取得
     * @return 発生全件数
     */
    public BigInteger getAllCnt() {
        return this.allCnt;
    }

    /**
     * 発生全件数を設定
     * @param allCnt 発生全件数
     */
    public void setAllCnt(BigInteger allCnt) {
        this.allCnt = allCnt;
    }

//    /**
//     * 発生中全件数を取得
//     * @return 発生中全件数
//     */
//    public BigInteger getOccurCnt() {
//        return this.occurCnt;/
//    }
//
//    /**
//     * 発生中全件数を設定
//     * @param occurCnt 発生中全件数
//     */
//    public void setOccurCnt(BigInteger occurCnt) {
//        this.occurCnt = occurCnt;
//    }

    /**
     * アラーム全件数を取得
     * @return アラーム全件数
     */
    public BigInteger getAlertCnt() {
        return this.alertCnt;
    }

    /**
     * アラーム全件数を設定
     * @param alertCnt アラーム全件数
     */
    public void setAlartCnt(BigInteger alertCnt) {
        this.alertCnt = alertCnt;
    }

    /**
     * オペレーションガイド全件数を取得
     * @return オペレーションガイド全件数
     */
    public BigInteger getOperationGuideCnt() {
        return this.operationGuideCnt;
    }

    /**
     * オペレーションガイド全件数を設定
     * @param operationGuideCnt オペレーションガイド全件数
     */
    public void setOperationGuideCnt(BigInteger operationGuideCnt) {
        this.operationGuideCnt = operationGuideCnt;
    }

}
