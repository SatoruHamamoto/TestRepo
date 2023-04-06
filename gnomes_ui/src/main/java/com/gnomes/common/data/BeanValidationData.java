package com.gnomes.common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 入力項目ドメイン定義:Beanバリデーション情報
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/13 KCC/A.Oomori                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BeanValidationData {

    /** 整数最大値 */
    private String maxValue;

    /** 実数最大値 */
    private String decimalMaxValue;

	/** 整数最小値 */
    private String minValue;

    /** 実数最小値 */
    private String decimalMinValue;

	/** バイト数 */
    private String byteSize;

    /** 最大文字数 */
    private String maxLength;

    /** 最小文字数 */
    private String minLength;

    /** 最大整数桁数 */
    private String maxIntegerDigit;

    /** 最大小数桁数 */
    private String maxDecimalPlace;

    /** 正規表現 */
    private String pattern;

    /**
     * 整数最大値を取得
	 * @return maxValue
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * 整数最大値を設定
	 * @param maxValue 整数最大値
	 */
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

    /**
     * 実数最大値を取得
	 * @return decimalMaxValue
	 */
	public String getDecimalMaxValue() {
		return decimalMaxValue;
	}

	/**
	 * 実数最大値を設定
	 * @param decimalMaxValue 実数最大値
	 */
	public void setDecimalMaxValue(String decimalMaxValue) {
		this.decimalMaxValue = decimalMaxValue;
	}

    /**
     * 整数最小値を取得
	 * @return minValue
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * 整数最小値を設定
	 * @param minValue 整数最小値
	 */
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

    /**
     * 実数最小値を取得
	 * @return decimalMinValue
	 */
	public String getDecimalMinValue() {
		return decimalMinValue;
	}

	/**
	 * 実数最小値を設定
	 * @param decimalMinValue 実数最小値
	 */
	public void setDecimalMinValue(String decimalMinValue) {
		this.decimalMinValue = decimalMinValue;
	}

	/**
	 * バイト数を取得
	 * @return byteSize
	 */
	public String getByteSize() {
		return byteSize;
	}

	/**
	 * バイト数を設定
	 * @param byteSize バイト数
	 */
	public void setByteSize(String byteSize) {
		this.byteSize = byteSize;
	}

	/**
	 * 最大文字数を取得
	 * @return maxLength
	 */
	public String getMaxLength() {
		return maxLength;
	}

	/**
	 * 最大文字数を設定
	 * @param maxLength 最大文字数
	 */
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * 最小文字数を取得
	 * @return minLength
	 */
	public String getMinLength() {
		return minLength;
	}

	/**
	 * 最小文字数を設定
	 * @param minLength 最小文字数
	 */
	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	/**
	 * 最大整数桁数を取得
	 * @return maxIntegerDigit
	 */
	public String getMaxIntegerDigit() {
		return maxIntegerDigit;
	}

	/**
	 * 最大整数桁数を設定
	 * @param maxIntegerDigit 最大整数桁数
	 */
	public void setMaxIntegerDigit(String maxIntegerDigit) {
		this.maxIntegerDigit = maxIntegerDigit;
	}

	/**
	 * 最大小数桁数を取得
	 * @return maxDecimalPlace
	 */
	public String getMaxDecimalPlace() {
		return maxDecimalPlace;
	}

	/**
	 * 最大小数桁数を設定
	 * @param maxDecimalPlace 最大小数桁数
	 */
	public void setMaxDecimalPlace(String maxDecimalPlace) {
		this.maxDecimalPlace = maxDecimalPlace;
	}

	/**
	 * 正規表現を取得
	 * @return pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * 正規表現を設定
	 * @param pattern 正規表現
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
