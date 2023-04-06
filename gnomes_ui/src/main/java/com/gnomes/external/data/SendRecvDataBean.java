package com.gnomes.external.data;

import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 * 送受信 データビーン
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/011/08 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@RequestScoped
public class SendRecvDataBean  implements java.io.Serializable {

    /** 項目チェックリスト */
    private Map<Integer, DataDefine> checkList;

    /**
     * 項目チェックリストを取得
     * @return checkList
     */
    public Map<Integer,DataDefine> getCheckList() {
        return this.checkList;
    }

    /**
     * 項目チェックリストを設定
     * @param checkList 入力項目チェックリスト
     */
    public void setCheckList(Map<Integer,DataDefine> checkList) {
        this.checkList = checkList;
    }


}
