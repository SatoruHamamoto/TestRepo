package com.gnomes.common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
/**
 * 入力項目ドメイン定義情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/13 KCC/A.Oomori                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InputDomain {

    /** ドメインID */
    @XmlAttribute
    private String id;

    /**
     * Beanバリデーションデータ
     */
    @XmlElement(name = "beanValidationData")
    BeanValidationData beanValidationData;

    /**
     * カスタムタグデータ
     */
    @XmlElement(name = "customTagData")
    CustomTagData customTagData;

	/**
	 * ドメインID
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * ドメインID
	 * @param id ドメインID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Beanバリデーションデータ
	 * @return beanValidationData
	 */
	public BeanValidationData getBeanValidationData() {
		return beanValidationData;
	}

	/**
	 * Beanバリデーションデータ
	 * @param beanValidationData Beanバリデーションデータ
	 */
	public void setBeanValidationData(BeanValidationData beanValidationData) {
		this.beanValidationData = beanValidationData;
	}

	/**
	 * カスタムタグデータ
	 * @return customTagData
	 */
	public CustomTagData getCustomTagData() {
		return customTagData;
	}

	/**
	 * カスタムタグデータ
	 * @param customTagData カスタムタグデータ
	 */
	public void setCustomTagData(CustomTagData customTagData) {
		this.customTagData = customTagData;
	}

}
