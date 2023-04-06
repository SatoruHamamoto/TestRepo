package com.gnomes.common.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.validator.constraints.GnomesPattern;

/**
 * 文字列パターンバリデータ処理を行う。
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
public class GnomesPatternValidator extends BaseValidator implements ConstraintValidator<GnomesPattern, CharSequence> {
	private Pattern pattern;
	private String regexp;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainPattern;

	/**
	 * 文字列パターンバリデータ コンストラクター
	 */
	public GnomesPatternValidator() {
	}

    @Override
	public void initialize(GnomesPattern gnomesPattern) {
	    GnomesPattern.Flag[] flags = gnomesPattern.flags();
		int intFlag = 0;
		for (GnomesPattern.Flag flag : flags) {
			intFlag |= flag.getValue();
		}
		try {
			this.regexp = gnomesPattern.regexp();
			this.resourceId = gnomesPattern.resourceId();
			this.dataName = ResourcesHandler.getString(this.resourceId);
	        this.inputDomainId = gnomesPattern.inputDomainId();

	        // ドメインIDが定義されている場合
	    	if (!StringUtil.isNullOrEmpty(inputDomainId)) {

				InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainPattern = inputDomain.getBeanValidationData().getPattern();
				this.regexp = this.domainPattern;

    		}

			this.pattern = Pattern.compile(regexp, intFlag);
		} catch (PatternSyntaxException e) {

            String name = gnomesPattern.annotationType().getName().toString();

            GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

            throw gnomesExceptionFactory.createGnomesException(
                    null, GnomesMessagesConstants.MV01_0015, this,resourceId, name, this.regexp);

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
		if (value == null) {
			return true;
		}
		Matcher m = this.pattern.matcher(value);

		return m.matches();
	}
}
