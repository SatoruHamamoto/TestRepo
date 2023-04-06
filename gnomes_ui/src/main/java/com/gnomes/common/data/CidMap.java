package com.gnomes.common.data;
/**
 * cidマップクラス定義.
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/19 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class CidMap {

    /** cid */
    private String cid;

    /** functionBeanのクラス名 */
    private String functionBeanClassName;

    /** windowId */
    private String windowId;

    /**
     * @return cid
     */
    public String getCid() {
        return cid;
    }

    /**
     * @param cid セットする cid
     */
    public void setCid(String cid) {
        this.cid = cid;
    }

    /**
     * @return functionBeanClassName
     */
    public String getFunctionBeanClassName() {
        return functionBeanClassName;
    }

    /**
     * @param functionBeanClassName セットする functionBeanClassName
     */
    public void setFunctionBeanClassName(String functionBeanClassName) {
        this.functionBeanClassName = functionBeanClassName;
    }

    /**
     * @return windowId
     */
    public String getWindowId() {
        return windowId;
    }

    /**
     * @param windowId セットする windowId
     */
    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }



}
