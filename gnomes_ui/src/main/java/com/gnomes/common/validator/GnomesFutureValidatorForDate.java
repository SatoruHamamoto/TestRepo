package com.gnomes.common.validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gnomes.common.validator.constraints.GnomesFuture;

/**
 * 未来日バリデータ処理を行う。（日付型）
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
public class GnomesFutureValidatorForDate implements ConstraintValidator<GnomesFuture, Date> {

    /**
     * 未来日バリデータ（日付型） コンストラクター
     */
    public GnomesFutureValidatorForDate() {
    }

    @Override
    public void initialize(GnomesFuture constraintAnnotation) {
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        ZonedDateTime date2zd = date.toInstant().atZone(ZoneId.systemDefault());
        return date2zd.isAfter(ZonedDateTime.now());
    }
}
