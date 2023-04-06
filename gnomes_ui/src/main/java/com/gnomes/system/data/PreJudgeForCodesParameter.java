package com.gnomes.system.data;

/**
 * judgePersonsLicenseOption.preJudgeForCodes()で使うパラメータクラス
 * (非CDI)
 * @author 03501213
 *
 */
public class PreJudgeForCodesParameter
{
    /**
     * 拠点コード
     */
    private String siteCode         = null;

    /**
     * 指図工程コード
     */
    private String orderProcessCode = null;

    /**
     * 作業工程コード
     */
    private String workProcessCode  = null;

    /**
     * 作業場所コード
     */
    private String workCellCode     = null;

    /**
     * 拠点コードを取得
     * @return 拠点コード
     */
    public String getSiteCode()
    {
        return siteCode;
    }

    /**
     * 拠点コードを設定
     * @param siteCode 拠点コード
     */
    public void setSiteCode(String siteCode)
    {
        this.siteCode = siteCode;
    }

    /**
     * 指図工程コードを取得
     * @return 指図工程コード
     */
    public String getOrderProcessCode()
    {
        return orderProcessCode;
    }

    /**
     * 指図工程コードを設定
     * @param orderProcessCode 指図工程コード
     */
    public void setOrderProcessCode(String orderProcessCode)
    {
        this.orderProcessCode = orderProcessCode;
    }

    /**
     * 作業工程コードを取得
     * @return 作業工程コード
     */
    public String getWorkProcessCode()
    {
        return workProcessCode;
    }

    /**
     * 作業工程コードを設定
     * @param workProcessCode 作業工程コード
     */
    public void setWorkProcessCode(String workProcessCode)
    {
        this.workProcessCode = workProcessCode;
    }

    /**
     * 作業場所コードを取得
     * @return 作業場所コード
     */
    public String getWorkCellCode()
    {
        return workCellCode;
    }

    /**
     * 作業場所コードを設定
     * @param workCellCode 作業場所コード
     */
    public void setWorkCellCode(String workCellCode)
    {
        this.workCellCode = workCellCode;
    }

    /**
     *
     * コンストラクタ
     *
     * @param siteCode          拠点コード
     * @param orderProcessCode  指図工程コード
     * @param workProcessCode   作業工程コード
     * @param workCellCode      作業場所コード
     */
    public PreJudgeForCodesParameter(String siteCode, String orderProcessCode, String workProcessCode,
            String workCellCode)
    {
        this.siteCode = siteCode;
        this.orderProcessCode = orderProcessCode;
        this.workProcessCode = workProcessCode;
        this.workCellCode = workCellCode;
    }
}
