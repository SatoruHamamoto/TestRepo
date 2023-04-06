package com.gnomes.common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class CommandScreenData {

    /** 画面ID */
    @XmlAttribute
    private String screenId;

    /** formBeanクラス名 */
    private String commonFormBeanName;

    /** functionBeanクラス名 */
    private String commonFunctionBeanName;

    /**
     * @return screenId
     */
    public String getScreenId() {
        return screenId;
    }

    /**
     * @param screenId セットする screenId
     */
    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    /**
     * @return commonFormBeanName
     */
    public String getCommonFormBeanName() {
        return commonFormBeanName;
    }

    /**
     * @param commonFormBeanName セットする commonFormBeanName
     */
    public void setCommonFormBeanName(String commonFormBeanName) {
        this.commonFormBeanName = commonFormBeanName;
    }

    /**
     * @return commonFunctionBeanName
     */
    public String getCommonFunctionBeanName() {
        return commonFunctionBeanName;
    }

    /**
     * @param commonFunctionBeanName セットする commonFunctionBeanName
     */
    public void setCommonFunctionBeanName(String commonFunctionBeanName) {
        this.commonFunctionBeanName = commonFunctionBeanName;
    }

}
