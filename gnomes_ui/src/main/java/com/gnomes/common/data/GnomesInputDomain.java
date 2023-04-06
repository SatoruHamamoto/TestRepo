package com.gnomes.common.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 入力項目ドメイン定義
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/13 KCC/A.Oomori                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class GnomesInputDomain {

	/**
     * 入力項目ドメイン定義情報
     */
    @XmlElementWrapper
    @XmlElement(name = "inputDomain")
    List<InputDomain> inputDomainList;

	/**
	 * 入力項目ドメイン定義情報の取得
	 * @return inputDomainList
	 */
	public List<InputDomain> getInputDomainList() {
		return inputDomainList;
	}

	/**
	 * 入力項目ドメイン定義情報の設定
	 * @param inputDomainList 入力項目ドメイン定義情報
	 */
	public void setInputDomainList(List<InputDomain> inputDomainList) {
		this.inputDomainList = inputDomainList;
	}


}
