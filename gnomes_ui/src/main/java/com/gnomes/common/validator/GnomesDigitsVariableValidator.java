package com.gnomes.common.validator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.validator.constraints.GnomesDigitsVariable;

/**
 * 小数点以下数値桁数（可変）バリデータ処理を行う。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/02/28 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesDigitsVariableValidator implements ConstraintValidator<GnomesDigitsVariable, Object> {

	@Inject
    GnomesSessionBean gnomesSessionBean;

    private String field;
    private String maxFractionLengthField;
    private String message;

    @Override
    public void initialize(GnomesDigitsVariable digitsVariable) {
        field = digitsVariable.field();
        maxFractionLengthField = digitsVariable.maxFractionLengthField();
        message = digitsVariable.message();
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        boolean ret = isCheckDigits(bean);
        if (!ret) {
            // デフォルトConstraintViolationオブジェクト無効化
            context.disableDefaultConstraintViolation();
            // メッセージ、フィールド指定
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field).addConstraintViolation();
        }
        return ret;
    }

    /**
     * 小数点以下数値桁数（可変）バリデータ
     * @param bean ビーン
     * @return 結果
     */
    public boolean isCheckDigits(Object bean) {
        Object fieldValue = getFieldValue(field, bean);
        Integer maxFractionLength = (Integer)getFieldValue(maxFractionLengthField, bean);

        if (fieldValue == null || maxFractionLength == null || fieldValue.toString().trim().isEmpty() || maxFractionLength.toString().trim().isEmpty() ) {
            // 値なし
            return true;
        }

        BigDecimal fieldValueBigDecimal;
        if (fieldValue instanceof CharSequence) {
        	fieldValueBigDecimal = getBigDecimalValue((CharSequence)fieldValue);
    		if (fieldValueBigDecimal == null) {
    			return true;
    		}
        }
        else if (fieldValue instanceof BigDecimal) {
        	fieldValueBigDecimal = (BigDecimal)fieldValue;
        }
        else {
            return false;
        }
        BigDecimal noZero = fieldValueBigDecimal.stripTrailingZeros();
        Integer fractionPartLength = noZero.scale() < 0 ? 0 : noZero.scale();

        return (maxFractionLength >= fractionPartLength);

    }

    /**
     * フィールド値取得
     * @param field フィールド名
     * @param bean 取得元bean
     * @return フィールド値
     */
    private Object getFieldValue(String field, Object bean) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(field, bean.getClass());
            Method method = pd.getReadMethod();
            try {
                return method.invoke(bean, (Object[]) null);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                // none
            }
        } catch (IntrospectionException e) {
            // none
        }
        return null;
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
