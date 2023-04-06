package com.gnomes.common.validator;

import java.text.ParseException;
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
import com.gnomes.common.validator.constraints.GnomesDecimalMin;

/**
 * 数値最少値バリデータ処理を行う。（文字列）
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
public class GnomesDecimalMinValidatorForCharSequence extends BaseValidator implements ConstraintValidator<GnomesDecimalMin, CharSequence> {
    private Number minValue;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainMinValue;

    /**
     * 数値最少値バリデータ（文字列） コンストラクター
     */
    public GnomesDecimalMinValidatorForCharSequence() {
    }

    @Override
    public void initialize(GnomesDecimalMin gnomesDecimalMin) {
        try {
            this.minValue = ConverterUtils.stringToNumber(false, gnomesDecimalMin.value());
            this.resourceId = gnomesDecimalMin.resourceId();
    		this.dataName = ResourcesHandler.getString(this.resourceId);
            this.inputDomainId = gnomesDecimalMin.inputDomainId();

            // ドメインIDが定義されている場合
        	if (!StringUtil.isNullOrEmpty(inputDomainId)) {
        		InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainMinValue = inputDomain.getBeanValidationData().getDecimalMinValue();
				this.minValue = ConverterUtils.stringToNumber(false, this.domainMinValue);

        	}
        } catch (ParseException e) {

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
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.toString().trim().isEmpty()) {
            return true;
        }
        try {
            if(this.minValue.doubleValue() > ConverterUtils.stringToNumber(false, value.toString()).doubleValue()){
            	// ドメイン定義使用、且つチェックエラーの場合
				if (!StringUtil.isNullOrEmpty(inputDomainId)) {

					Map<String, String[]> errMessageMap = new HashMap<>();
					errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0008, dataName, this.domainMinValue});
					req.addValidationDomainError(this.resourceId, errMessageMap);
					return true;
		        }
                return false;
            }
        } catch (ParseException e) {
        }
        return true;
    }
}
