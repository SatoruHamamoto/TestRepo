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
import com.gnomes.common.validator.constraints.GnomesDecimalMax;

/**
 * 数値最大値バリデータ処理を行う。（数値）
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
public class GnomesDecimalMaxValidatorForNumber extends BaseValidator implements ConstraintValidator<GnomesDecimalMax, Number> {
    private BigDecimal maxValue;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainMaxValue;

    /**
     * 数値最大値バリデータ（数値） コンストラクター
     */
    public GnomesDecimalMaxValidatorForNumber() {
    }

    @Override
    public void initialize(GnomesDecimalMax gnomesDecimalMax) {
        try {
            this.maxValue = new BigDecimal(gnomesDecimalMax.value());
            this.resourceId = gnomesDecimalMax.resourceId();
    		this.dataName = ResourcesHandler.getString(this.resourceId);
            this.inputDomainId = gnomesDecimalMax.inputDomainId();

            // ドメインIDが定義されている場合
        	if (!StringUtil.isNullOrEmpty(inputDomainId)) {
        		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainMaxValue = inputDomain.getBeanValidationData().getDecimalMaxValue();
				this.maxValue = new BigDecimal(this.domainMaxValue);

        	}
        } catch (NumberFormatException nfe) {

            String resourceId =ResourcesHandler.getString(gnomesDecimalMax.resourceId());
            String name = gnomesDecimalMax.annotationType().getName().toString();
            String value = gnomesDecimalMax.value();

            GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

            throw gnomesExceptionFactory.createGnomesException(
                    null, GnomesMessagesConstants.MV01_0015, resourceId, name, value);

        }
        // ドメイン定義参照時のエラー
     	catch (Exception e) {

     			GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

     	        throw gnomesExceptionFactory.createGnomesException(
     	                null, GnomesMessagesConstants.ME01_0216, dataName, inputDomainId);
     	}
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if ((value instanceof Double)) {
            if (((Double) value).doubleValue() <= Double.NEGATIVE_INFINITY) {
                return true;
            }
            if ((Double.isNaN(((Double) value).doubleValue()))
                    || (((Double) value).doubleValue() >= Double.POSITIVE_INFINITY)) {
                return false;
            }
        } else if ((value instanceof Float)) {
            if (((Float) value).floatValue() <= Float.NEGATIVE_INFINITY) {
                return true;
            }
            if ((Float.isNaN(((Float) value).floatValue()))
                    || (((Float) value).floatValue() >= Float.POSITIVE_INFINITY)) {
                return false;
            }
        }
        int comparisonResult;
        if ((value instanceof BigDecimal)) {
            comparisonResult = ((BigDecimal) value).compareTo(this.maxValue);
        } else {
            if ((value instanceof BigInteger)) {
                comparisonResult = new BigDecimal((BigInteger) value).compareTo(this.maxValue);
            } else {
                if ((value instanceof Long)) {
                    comparisonResult = BigDecimal.valueOf(value.longValue()).compareTo(this.maxValue);
                } else {
                    if ((value instanceof Float)) {
                        comparisonResult = BigDecimal.valueOf(value.floatValue()).compareTo(BigDecimal.valueOf(this.maxValue.floatValue()));
                    } else {
                        comparisonResult = BigDecimal.valueOf(value.doubleValue()).compareTo(this.maxValue);
                    }
                }
            }
        }

        boolean result = comparisonResult <= 0;
        // ドメイン定義使用、且つチェックエラーの場合
		if (!result && !StringUtil.isNullOrEmpty(inputDomainId)) {

			Map<String, String[]> errMessageMap = new HashMap<>();
			errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0007, dataName, this.domainMaxValue});
			req.addValidationDomainError(this.resourceId, errMessageMap);
			return true;
        }

        return result;
    }
}
