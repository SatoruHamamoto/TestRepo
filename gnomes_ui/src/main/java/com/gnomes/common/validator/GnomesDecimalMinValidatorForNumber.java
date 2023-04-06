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
import com.gnomes.common.validator.constraints.GnomesDecimalMin;

/**
 * 数値最少値バリデータ処理を行う。（数値）
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
public class GnomesDecimalMinValidatorForNumber extends BaseValidator implements ConstraintValidator<GnomesDecimalMin, Number> {
    private BigDecimal minValue;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainMinValue;

    /**
     * 数値最少値バリデータ（数値） コンストラクター
     */
    public GnomesDecimalMinValidatorForNumber() {
    }

    @Override
    public void initialize(GnomesDecimalMin gnomesDecimalMin) {
        try {
            this.minValue = new BigDecimal(gnomesDecimalMin.value());
            this.resourceId = gnomesDecimalMin.resourceId();
    		this.dataName = ResourcesHandler.getString(this.resourceId);
            this.inputDomainId = gnomesDecimalMin.inputDomainId();

            // ドメインIDが定義されている場合
        	if (!StringUtil.isNullOrEmpty(inputDomainId)) {
        		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainMinValue = inputDomain.getBeanValidationData().getDecimalMinValue();
				this.minValue = new BigDecimal(this.domainMinValue);

        	}
        } catch (NumberFormatException nfe) {

        	String resourceId =ResourcesHandler.getString(gnomesDecimalMin.resourceId());
            String name = gnomesDecimalMin.annotationType().getName().toString();
            String value = gnomesDecimalMin.value();

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
            if (((Double) value).doubleValue() >= Double.POSITIVE_INFINITY) {
                return true;
            }
            if ((Double.isNaN(((Double) value).doubleValue()))
                    || (((Double) value).doubleValue() <= Double.NEGATIVE_INFINITY)) {
                return false;
            }
        } else if ((value instanceof Float)) {
            if (((Float) value).floatValue() >= Float.POSITIVE_INFINITY) {
                return true;
            }
            if ((Float.isNaN(((Float) value).floatValue()))
                    || (((Float) value).floatValue() <= Float.NEGATIVE_INFINITY)) {
                return false;
            }
        }
        int comparisonResult;
        //int comparisonResult;
        if ((value instanceof BigDecimal)) {
            comparisonResult = ((BigDecimal) value).compareTo(this.minValue);
        } else {
            //int comparisonResult;
            if ((value instanceof BigInteger)) {
                comparisonResult = new BigDecimal((BigInteger) value).compareTo(this.minValue);
            } else {
                //int comparisonResult;
                if ((value instanceof Long)) {
                    comparisonResult = BigDecimal.valueOf(value.longValue()).compareTo(this.minValue);
                } else {
                    //int comparisonResult;
                    if ((value instanceof Float)) {
                        comparisonResult = BigDecimal.valueOf(value.floatValue()).compareTo(BigDecimal.valueOf(this.minValue.floatValue()));
                    } else {
                        comparisonResult = BigDecimal.valueOf(value.doubleValue()).compareTo(this.minValue);
                    }
                }
            }
        }
        boolean result = comparisonResult >= 0;
        // ドメイン定義使用、且つチェックエラーの場合
		if (!result && !StringUtil.isNullOrEmpty(inputDomainId)) {

			Map<String, String[]> errMessageMap = new HashMap<>();
			errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0008, dataName, this.domainMinValue});
			req.addValidationDomainError(this.resourceId, errMessageMap);
			return true;
        }

        return result;
    }
}
