package com.gnomes.common.validator;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.validator.constraints.GnomesSize;

/**
 * 文字長バリデータ処理を行う。
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
public class GnomesSizeValidatorForCharSequence extends BaseValidator implements ConstraintValidator<GnomesSize, CharSequence> {
	private int min;
	private int max;
	private String dataName;
	private String resourceId;
	private String inputDomainIdMax;
	private String inputDomainIdMin;
	private String domainMaxLength;
	private String domainMinLength;

    /**
     * 文字長バリデータ コンストラクター
     */
	public GnomesSizeValidatorForCharSequence() {
	}

    @Override
	public void initialize(GnomesSize gnomesSize) {
		this.min = gnomesSize.min();
		this.max = gnomesSize.max();
        this.resourceId = gnomesSize.resourceId();
		this.dataName = ResourcesHandler.getString(this.resourceId);
        this.inputDomainIdMax = gnomesSize.inputDomainIdMax();
        this.inputDomainIdMin = gnomesSize.inputDomainIdMin();

        // ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty(inputDomainIdMax)) {
    		try {
				InputDomain inputDomainMax = gnomesSystemBean.getInputDomain(inputDomainIdMax);

				this.domainMaxLength = inputDomainMax.getBeanValidationData().getMaxLength();
				this.max = Integer.valueOf(this.domainMaxLength);

    		}
    		// ドメイン定義参照時のエラー
    		catch (Exception e) {

				GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

		        throw gnomesExceptionFactory.createGnomesException(
		                null, GnomesMessagesConstants.ME01_0216, dataName, inputDomainIdMax);
    		}

		}
    	// ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty(inputDomainIdMin)) {
    		try {
				InputDomain inputDomainMin = gnomesSystemBean.getInputDomain(inputDomainIdMin);

				this.domainMinLength = inputDomainMin.getBeanValidationData().getMinLength();
				this.min = Integer.valueOf(this.domainMinLength);

    		}
    		// ドメイン定義参照時のエラー
    		catch (Exception e) {

				GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

		        throw gnomesExceptionFactory.createGnomesException(
		                null, GnomesMessagesConstants.ME01_0216, dataName, inputDomainIdMin);
    		}

		}
	}

    @Override
	public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
		if (charSequence == null) {
			return true;
		}
		int length = charSequence.length();

		boolean result = (length >= this.min) && (length <= this.max);
		// ドメイン定義使用、且つチェックエラーの場合
		if( !result && (!StringUtil.isNullOrEmpty(inputDomainIdMax) || !StringUtil.isNullOrEmpty(inputDomainIdMin))){

			Map<String, String[]> errMessageMap = new HashMap<>();
			errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0010, dataName, this.domainMaxLength, this.domainMinLength});
			req.addValidationDomainError(this.resourceId, errMessageMap);
			return true;
        }

		return result;
	}
}
