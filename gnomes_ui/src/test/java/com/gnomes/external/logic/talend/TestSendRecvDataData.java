package com.gnomes.external.logic.talend;

import java.math.BigDecimal;
import java.util.Date;

/**
 * データ型の送受信データのテストモデルクラス
 *
 * @author 30041979
 *
 */
public class TestSendRecvDataData extends TestSendRecvDataBase {

	private Date checkDate;

	private Integer checkInteger;

	private String checkString;

	private BigDecimal checkBigDecimal;

	private String checkFixedValue;

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public Integer getCheckInteger() {
		return checkInteger;
	}

	public void setCheckInteger(Integer checkInteger) {
		this.checkInteger = checkInteger;
	}

	public String getCheckString() {
		return checkString;
	}

	public void setCheckString(String checkString) {
		this.checkString = checkString;
	}

	public BigDecimal getCheckBigDecimal() {
		return checkBigDecimal;
	}

	public void setCheckBigDecimal(BigDecimal checkBigDecimal) {
		this.checkBigDecimal = checkBigDecimal;
	}

	public String getCheckFixedValue() {
		return checkFixedValue;
	}

	public void setCheckFixedValue(String checkFixedValue) {
		this.checkFixedValue = checkFixedValue;
	}
}
