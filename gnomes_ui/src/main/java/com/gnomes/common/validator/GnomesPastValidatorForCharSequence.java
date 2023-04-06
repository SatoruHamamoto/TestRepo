package com.gnomes.common.validator;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.validator.constraints.GnomesPast;

/**
 * 過去日バリデータ処理を行う。（文字列）
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
public class GnomesPastValidatorForCharSequence implements ConstraintValidator<GnomesPast, CharSequence> {

    @Inject
    GnomesSessionBean gnomesSessionBean;

    private String formatId;

    /**
     * 過去日バリデータ（文字列） コンストラクター
     */
    public GnomesPastValidatorForCharSequence() {
    }

    @Override
    public void initialize(GnomesPast gnomesPast) {
        this.formatId = gnomesPast.formatId();
    }

    @Override
    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        if (charSequence == null || charSequence.toString().trim().isEmpty()) {
            return true;
        }

        ZonedDateTime fieldDateValue = null;

        try {
            String format = ResourcesHandler.getString(formatId, gnomesSessionBean.getUserLocale());
            fieldDateValue = ConverterUtils.stringToUtcFormat(charSequence.toString(), format);
        } catch (ParseException | DateTimeParseException pe) {
            // 変換エラー
            return true;
        }

        return fieldDateValue.isBefore(ZonedDateTime.now());
    }
}
