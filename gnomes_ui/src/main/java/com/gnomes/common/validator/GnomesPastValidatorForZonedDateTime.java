package com.gnomes.common.validator;

import java.time.ZonedDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.validator.constraints.GnomesPast;

/**
 * 過去日バリデータ処理を行う。（ゾーン日付型）
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
public class GnomesPastValidatorForZonedDateTime implements ConstraintValidator<GnomesPast, ZonedDateTime> {

    /**
     * 過去日バリデータ（ゾーン日付型） コンストラクター
     */
    public GnomesPastValidatorForZonedDateTime() {
    }

    @Override
    public void initialize(GnomesPast constraintAnnotation) {
    }

    @Override
    public boolean isValid(ZonedDateTime date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }
        return date.isBefore(ZonedDateTime.now());
    }

}
