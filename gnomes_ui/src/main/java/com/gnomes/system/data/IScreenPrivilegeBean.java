package com.gnomes.system.data;

import java.util.List;

/**
*
* 画面セキュリティービーン インターフェイス
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/01/23 KCC/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public interface IScreenPrivilegeBean {

    /**
     * 工程コードを取得
     * @return
     */
    public List<String> getProcessCode();

    /**
     * 拠点コードを取得
     * @return
     */
    public List<String> getSiteCode();

    /**
     * 指図工程コードを取得
     * @return
     */
    public List<String> getOrderProcessCode();

    /**
     * 作業工程コードを取得
     * @return
     */
    public List<String> getWorkProcessCode();

    /**
     * パーツ権限結果情報を設定
     * @param partsPrivilegeResultInfo パーツ権限結果情報
     */
    public void setPartsPrivilegeResultInfo(List<PartsPrivilegeResultInfo> partsPrivilegeResultInfo);

    /**
     * パーツ権限結果情報を取得
     * @return パーツ権限結果情報
     */
    public List<PartsPrivilegeResultInfo> getPartsPrivilegeResultInfo();


}
