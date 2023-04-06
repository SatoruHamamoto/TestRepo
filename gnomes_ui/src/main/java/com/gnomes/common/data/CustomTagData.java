package com.gnomes.common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 入力項目ドメイン定義：カスタムタグ情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/13 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTagData {

    /**
     * 最大入力文字数
     *
     * 対象カスタムタグ：テキスト、テキストエリア、数値
     */
    private String inputMaxLength;

    /**
     * 整数入力桁数
     *
     * 対象カスタムタグ：数値
     */
    private String inputIntegerDigits;

    /**
     * 小数点入力桁数
     *
     * 対象カスタムタグ：数値
     */
    private String inputDecimalDigits;

	/**
	 * 最大入力文字数を取得
	 * @return inputMaxLength
	 */
	public String getInputMaxLength() {
		return inputMaxLength;
	}

	/**
	 * 最大入力文字数を設定
	 * @param inputMaxLength 最大入力文字数
	 */
	public void setInputMaxLength(String inputMaxLength) {
		this.inputMaxLength = inputMaxLength;
	}

	/**
	 * 整数入力桁数を取得
	 * @return inputIntegerDigits
	 */
	public String getInputIntegerDigits() {
		return inputIntegerDigits;
	}

	/**
	 * 整数入力桁数を設定
	 * @param inputIntegerDigits 整数入力桁数
	 */
	public void setInputIntegerDigits(String inputIntegerDigits) {
		this.inputIntegerDigits = inputIntegerDigits;
	}

	/**
	 * 小数点入力桁数を取得
	 * @return inputDecimalDigits
	 */
	public String getInputDecimalDigits() {
		return inputDecimalDigits;
	}

	/**
	 * 小数点入力桁数を設定
	 * @param inputDecimalDigits 小数点入力桁数
	 */
	public void setInputDecimalDigits(String inputDecimalDigits) {
		this.inputDecimalDigits = inputDecimalDigits;
	}

}
