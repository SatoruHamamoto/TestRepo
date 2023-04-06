package com.gnomes.common.validator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.validator.constraints.GnomesProhibitionString;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * 文字列パターンバリデータ処理を行う。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/03/02 YJP/Nweniwah               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesProhibitionStringValidator extends BaseValidator implements ConstraintValidator<GnomesProhibitionString, CharSequence> {
	private Pattern pattern;
	private String regexp;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainPattern;

	/** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao mstrSystemDefineDao;

	/**
	 * 文字列パターンバリデータ コンストラクター
	 */
	public GnomesProhibitionStringValidator() {
	}

    @Override
	public void initialize(GnomesProhibitionString gnomesProhibitionString) {
    	GnomesProhibitionString.Flag[] flags = gnomesProhibitionString.flags();
		int intFlag = 0;
		for (GnomesProhibitionString.Flag flag : flags) {
			intFlag |= flag.getValue();
		}
		try {

			// バリデーション情報取得
			MstrSystemDefine mstrSystemDefine  = this.mstrSystemDefineDao.getMstrSystemDefine(
	                SystemDefConstants.VALIDATOR,
	                SystemDefConstants.PROHIBITION_STRING);
	        if (Objects.isNull(mstrSystemDefine)
	                || StringUtil.isNullOrEmpty(mstrSystemDefine.getChar1())) {
	        	this.regexp = "";
	        } else {
	        	this.regexp = mstrSystemDefine.getChar1();
	        }

			this.resourceId = gnomesProhibitionString.resourceId();
			this.dataName = ResourcesHandler.getString(this.resourceId);
	        this.inputDomainId = gnomesProhibitionString.inputDomainId();

	        // ドメインIDが定義されている場合
	    	if (!StringUtil.isNullOrEmpty(inputDomainId)) {

				InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);
				this.domainPattern = inputDomain.getBeanValidationData().getPattern();
				this.regexp = this.domainPattern;
    		}

			this.pattern = Pattern.compile(regexp, intFlag);
		} catch (PatternSyntaxException e) {

            String name = gnomesProhibitionString.annotationType().getName().toString();

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
		int count = 0;
		while(m.find()){
			count++;
		}
		if (count > 0) {
			return false;
		}
		return true;
	}
}
