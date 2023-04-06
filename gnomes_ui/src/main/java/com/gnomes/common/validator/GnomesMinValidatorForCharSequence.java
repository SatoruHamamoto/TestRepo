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
import com.gnomes.common.validator.constraints.GnomesMin;

/**
 * 最少値バリデータ処理を行う。（文字列）
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
public class GnomesMinValidatorForCharSequence extends BaseValidator implements ConstraintValidator<GnomesMin, CharSequence> {
    private Long minValue;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainMinValue;

    /**
     * 最少値バリデータ（文字列） コンストラクター
     */
    public GnomesMinValidatorForCharSequence() {
    }

    @Override
    public void initialize(GnomesMin gnomesMin) {
        this.minValue = gnomesMin.value();
        this.resourceId = gnomesMin.resourceId();
		this.dataName = ResourcesHandler.getString(this.resourceId);
        this.inputDomainId = gnomesMin.inputDomainId();

        // ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty(inputDomainId)) {
    		try {
				InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainMinValue = inputDomain.getBeanValidationData().getMinValue();
				this.minValue = ConverterUtils.stringToNumber(false, this.domainMinValue).longValue();

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
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.toString().trim().isEmpty()) {
            return true;
        }
        try {
            if(this.minValue > ConverterUtils.stringToNumber(false, value.toString()).longValue()){
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
