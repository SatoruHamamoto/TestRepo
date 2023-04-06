package com.gnomes.common.validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.validator.constraints.GnomesPast;

/**
 * 過去日バリデータ処理を行う。（日付型）
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
public class GnomesPastValidatorForDate implements ConstraintValidator<GnomesPast, Date> {

    /**
     * 過去日バリデータ（日付型） コンストラクター
     */
    public GnomesPastValidatorForDate() {
    }

    @Override
    public void initialize(GnomesPast constraintAnnotation) {
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        ZonedDateTime date2zd = date.toInstant().atZone(ZoneId.systemDefault());

        return date2zd.isBefore(ZonedDateTime.now());
    }
}
