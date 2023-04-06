package com.gnomes.common.validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.validator.constraints.GnomesDigits;

/**
 * 数値桁数バリデータ処理を行う。（文字列）
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
public class GnomesDigitsValidatorForCharSequence extends BaseValidator implements ConstraintValidator<GnomesDigits, CharSequence> {
    private int maxIntegerDigit;
    private int maxDecimalPlace;
	private String dataName;
	private String resourceId;
	private String inputDomainIdIntegerDigit;
	private String inputDomainIdDecimalPlace;
	private String domainMaxIntegerDigit;
	private String domainMaxDecimalPlace;

    /**
     * 数値桁数バリデータ（文字列） コンストラクター
     */
	public GnomesDigitsValidatorForCharSequence() {
	}

    @Override
	public void initialize(GnomesDigits gnomesDigits) {
        this.maxIntegerDigit = gnomesDigits.integer();
        this.maxDecimalPlace = gnomesDigits.fraction();
        this.resourceId = gnomesDigits.resourceId();
		this.dataName = ResourcesHandler.getString(this.resourceId);
        this.inputDomainIdIntegerDigit = gnomesDigits.inputDomainIdIntegerDigit();
        this.inputDomainIdDecimalPlace = gnomesDigits.inputDomainIdDecimalPlace();

        // ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty( this.inputDomainIdIntegerDigit)) {
    		try {
				InputDomain inputDomainInteger = gnomesSystemBean.getInputDomain(inputDomainIdIntegerDigit);

				this.domainMaxIntegerDigit = inputDomainInteger.getBeanValidationData().getMaxIntegerDigit();
				this.maxIntegerDigit = Integer.valueOf(this.domainMaxIntegerDigit);

    		}
    		// ドメイン定義参照時のエラー
    		catch (Exception e) {

				GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

		        throw gnomesExceptionFactory.createGnomesException(
		                null, GnomesMessagesConstants.ME01_0216, dataName, this.inputDomainIdIntegerDigit);
    		}

		}
    	// ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty( this.inputDomainIdDecimalPlace)) {
    		try {

				InputDomain inputDomainDecimal = gnomesSystemBean.getInputDomain(inputDomainIdDecimalPlace);

				this.domainMaxDecimalPlace = inputDomainDecimal.getBeanValidationData().getMaxDecimalPlace();
				this.maxDecimalPlace = Integer.valueOf(this.domainMaxDecimalPlace);

    		}
    		// ドメイン定義参照時のエラー
    		catch (Exception e) {

				GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();

		        throw gnomesExceptionFactory.createGnomesException(
		                null, GnomesMessagesConstants.ME01_0216, dataName, this.inputDomainIdDecimalPlace);
    		}

		}
	}

    @Override
	public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
		if (charSequence == null) {
			return true;
		}
		BigDecimal bigNum = getBigDecimalValue(charSequence);
		if (bigNum == null) {
			return true;
		}

        BigDecimal noZero = bigNum.stripTrailingZeros();
        int integerPartLength = noZero.precision() - noZero.scale();
        int decimalPartLength = noZero.scale() < 0 ? 0 : noZero.scale();

		boolean result = ((this.maxIntegerDigit >= integerPartLength) && (this.maxDecimalPlace >= decimalPartLength));

        // ドメイン定義使用、且つチェックエラーの場合
		if (!result && (!StringUtil.isNullOrEmpty(this.inputDomainIdIntegerDigit) || !StringUtil.isNullOrEmpty(this.inputDomainIdDecimalPlace))) {

			Map<String, String[]> errMessageMap = new HashMap<>();
			errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0003, dataName, this.domainMaxIntegerDigit, this.domainMaxDecimalPlace});
			req.addValidationDomainError(this.resourceId, errMessageMap);
			return true;
		}
        return result;
	}

    /**
     * 文字列からBigDecimal変換
     * @param charSequence 変換元文字列
     * @return 変換結果値
     */
	private BigDecimal getBigDecimalValue(CharSequence charSequence) {
		BigDecimal bd;
		try {
			bd = new BigDecimal(charSequence.toString());
		} catch (NumberFormatException nfe) {
			return null;
		}
		return bd;
	}
}
