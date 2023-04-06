package com.gnomes.common.validator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.validator.constraints.ComparePast;

/**
 * 日付相関バリデータ処理を行う。
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
public class ComparePastValidator extends BaseValidator implements ConstraintValidator<ComparePast, Object> {

    @Inject
    GnomesSessionBean gnomesSessionBean;

    private String field;
    private String compareField;
    private String fieldFormatId;
    private String compareFieldFormatId;
    private String message;

    private String resourceId;
    private String compareResourceId;

    @Override
    public void initialize(ComparePast comparePast) {
        field = comparePast.field();
        compareField = comparePast.compareField();
        fieldFormatId = comparePast.fieldFormatId();
        compareFieldFormatId = comparePast.compareFieldFormatId();
        message = comparePast.message();
        resourceId = comparePast.resourceId();
        compareResourceId = comparePast.compareResourceId();
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        boolean ret = isComparePast(bean);
        if (!ret) {
            Map<String, String[]> errMessageMap = new HashMap<>();
			errMessageMap.put(this.resourceId,
					new String[]{
							message,
							ResourcesHandler.getString(this.resourceId),
							ResourcesHandler.getString(this.compareResourceId)
					});
			req.addValidationDomainError(this.resourceId, errMessageMap);
        }
        return ret;
    }

    /**
     * 日付相関バリデータ
     * @param bean ビーン
     * @return 結果
     */
    private boolean isComparePast(Object bean) {
        Object fieldValue = getFieldValue(field, bean);
        Object compareFieldValue = getFieldValue(compareField, bean);

        if (fieldValue == null || compareFieldValue == null || fieldValue.toString().trim().isEmpty() || compareFieldValue.toString().trim().isEmpty() ) {
            // 値なし
            return true;
        }

        if (fieldValue instanceof String) {
//            ZonedDateTime fieldDateValue = null;
//            ZonedDateTime compareFieldDateValue = null;
            Date fieldDateValue = null;
            Date compareFieldDateValue = null;


            try {
                String format = ResourcesHandler.getString(fieldFormatId, gnomesSessionBean.getUserLocale());
//                fieldDateValue = ConverterUtils.stringToUtcFormat((String)fieldValue, format);
                fieldDateValue = ConverterUtils.stringToDateFormat((String)fieldValue, format);

                String compareFieldFormat = ResourcesHandler.getString(compareFieldFormatId, gnomesSessionBean.getUserLocale());
//                compareFieldDateValue = ConverterUtils.stringToUtcFormat((String)compareFieldValue, compareFieldFormat);
                compareFieldDateValue = ConverterUtils.stringToDateFormat((String)compareFieldValue, compareFieldFormat);
            } catch (ParseException | DateTimeParseException pe) {
                // 変換エラー
                return true;
            }

            if (fieldDateValue == null || compareFieldDateValue == null) {
                // 変換エラー
                return true;
            }
            int compare = fieldDateValue.compareTo(compareFieldDateValue);
            return (compare <= 0);
        }
        else if (fieldValue instanceof Date) {
            Date fieldDateValue = (Date)fieldValue;
            Date compareFieldDateValue = (Date)compareFieldValue;

            int compare = fieldDateValue.compareTo(compareFieldDateValue);
            return (compare <= 0);
        }
        return false;
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
}
