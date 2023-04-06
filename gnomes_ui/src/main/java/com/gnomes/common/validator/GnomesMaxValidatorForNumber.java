package com.gnomes.common.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.validator.constraints.GnomesMax;

/**
 *  最大値バリデータ処理を行う。（数値）
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/18 YJP/Y.Oota                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesMaxValidatorForNumber extends BaseValidator implements ConstraintValidator<GnomesMax, Number> {

	private long maxValue;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainMaxValue;

	/**
     * 最大値バリデータ（数値） コンストラクター
     */
	public GnomesMaxValidatorForNumber() {
	}

    @Override
	public void initialize(GnomesMax gnomesMax) {
		this.maxValue = gnomesMax.value();
		this.resourceId = gnomesMax.resourceId();
		this.dataName = ResourcesHandler.getString(this.resourceId);
        this.inputDomainId = gnomesMax.inputDomainId();

        // ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty(inputDomainId)) {
    		try {
				InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainMaxValue = inputDomain.getBeanValidationData().getMaxValue();
				this.maxValue = ConverterUtils.stringToNumber(false, this.domainMaxValue).longValue();

    		}
    		// ドメイン定義参照時のエラー
    		catch (Exception e) {

				GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

		        throw gnomesExceptionFactory.createGnomesException(
		                null, GnomesMessagesConstants.ME01_0216, dataName, inputDomainId);
    		}

		}
	}

    @Override
	public boolean isValid(Number value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		boolean result = true;
		if ((value instanceof Double)) {
			if (((Double) value).doubleValue() <= Double.NEGATIVE_INFINITY) {
				result = true;
			}
			if ((Double.isNaN(((Double) value).doubleValue()))
					|| (((Double) value).doubleValue() >= Double.POSITIVE_INFINITY)) {
				result = false;
			}
		} else if ((value instanceof Float)) {
			if (((Float) value).floatValue() <= Float.NEGATIVE_INFINITY) {
				result = true;
			}
			if ((Float.isNaN(((Float) value).floatValue()))
					|| (((Float) value).floatValue() >= Float.POSITIVE_INFINITY)) {
				result = false;
			}
		} else if ((value instanceof BigDecimal)) {
			result = ((BigDecimal) value).compareTo(BigDecimal.valueOf(this.maxValue)) != 1;
		} else if ((value instanceof BigInteger)) {
			result =  ((BigInteger) value).compareTo(BigInteger.valueOf(this.maxValue)) != 1;
		} else {
			long longValue = value.longValue();

			result = longValue <= this.maxValue;
		}

		// ドメイン定義使用、且つチェックエラーの場合
		if( !result && !StringUtil.isNullOrEmpty(inputDomainId)){

			Map<String, String[]> errMessageMap = new HashMap<>();
			errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0007, dataName, this.domainMaxValue});
			req.addValidationDomainError(this.resourceId, errMessageMap);
			return true;
        }

		return result;
	}
}
