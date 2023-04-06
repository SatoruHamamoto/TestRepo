package com.gnomes.common.validator;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.InputDomain;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.validator.constraints.ByteSize;

/**
 * バイト長バリデータ処理を行う。
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
public class ByteSizeValidator extends BaseValidator implements ConstraintValidator<ByteSize, String> {

    private int size;
    private String charset;
	private String dataName;
	private String resourceId;
	private String inputDomainId;
	private String domainByteSize;

    /**
     * バイト長バリデータ コンストラクター
     */
    public ByteSizeValidator() {
    }

    @Override
    public void initialize(ByteSize byteSize) {
    	this.size = byteSize.size();
    	this.charset = byteSize.charset();
        this.resourceId = byteSize.resourceId();
		this.dataName = ResourcesHandler.getString(this.resourceId);
        this.inputDomainId =byteSize.inputDomainId();

        // ドメインIDが定義されている場合
    	if (!StringUtil.isNullOrEmpty(inputDomainId)) {
    		try {
				InputDomain inputDomain = gnomesSystemBean.getInputDomain(inputDomainId);

				this.domainByteSize = inputDomain.getBeanValidationData().getByteSize();
				this.size = Integer.valueOf(this.domainByteSize);

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
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isByteSizeValid(value);
    }

    private boolean isByteSizeValid(String value) {
        if (value == null) {
            return true;
        }
        byte[] byteValue = value.getBytes(Charset.forName(charset));

        boolean result = byteValue.length <= size;

        // ドメイン定義使用、且つチェックエラーの場合
 		if( !result && !StringUtil.isNullOrEmpty(inputDomainId)){

 			Map<String, String[]> errMessageMap = new HashMap<>();
 			errMessageMap.put(this.resourceId, new String[]{ GnomesMessagesConstants.MV01_0012, dataName, this.domainByteSize});
 			req.addValidationDomainError(this.resourceId, errMessageMap);
 			return true;
         }

 		return result;
    }
}
