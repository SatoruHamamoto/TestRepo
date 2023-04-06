package com.gnomes.equipment.data;

import java.util.List;

/**
 * 設備I/Fパラメータ情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class EquipmentIfParamInfo {

    /** サブシステムID */
    private String subSystemId;

    /** アイテム名リスト */
    private List<String> itemNameList;

    /** 特殊変換処理区分リスト */
    private List<Integer> specialConvertDivList;

    /**
     * サブシステムIDを取得
     * @return サブシステムID
     */
    public String getSubSystemId() {
        return subSystemId;
    }

    /**
     * サブシステムIDを設定
     * @param subSystemId サブシステムID
     */
    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    /**
     * アイテム名リストを取得
     * @return アイテム名リスト
     */
    public List<String> getItemNameList() {
        return itemNameList;
    }

    /**
     * アイテム名リストを設定
     * @param itemNameList アイテム名リスト
     */
    public void setItemNameList(List<String> itemNameList) {
        this.itemNameList = itemNameList;
    }

    /**
     * 特殊変換処理区分を取得
     * @return
     */
    public List<Integer> getSpecialConvertDivList()
    {
        return specialConvertDivList;
    }

    /**
     * 特殊変換処理区分を設定
     * @param specialConvertDivList
     */
    public void setSpecialConvertDivList(List<Integer> specialConvertDivList)
    {
        this.specialConvertDivList = specialConvertDivList;
    }
}
