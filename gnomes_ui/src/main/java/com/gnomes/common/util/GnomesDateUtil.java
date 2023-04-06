package com.gnomes.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * システム日時取得共通処理（UTC対応）クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/09/10 YJP/K.Nakanishi           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class GnomesDateUtil
{

    /**
     * 任意のロケール(タイムゾーン)のString型の値をUTCのDate型に変更.
     * <pre>
     * 任意のロケール(タイムゾーン)のString型の値をUTCのDate型に変更する。
     * </pre>
     * @param localeDate ロケール(タイムゾーン)の日時
     * @param format ロケール(タイムゾーン)の日時のフォーマット
     * @param timezone タイムゾーン
     * @return UTCの日時
     * @throws ParseException
     * @throws GnomesAppException
     */
    public static Date convertStringLocaleToUtc(String localeDate, String format, TimeZone timezone)
            throws GnomesAppException
    {

        // 変換処理を記述する
        Date utcDate = new Date();
        LocalDateTime ldt = LocalDateTime.parse(localeDate, DateTimeFormatter.ofPattern(format));
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(timezone.getID()));
        // UTCに変換
        ZonedDateTime zdtUtc = zdt.withZoneSameInstant(ZoneId.of(CommonConstants.ZONEID_UTC));
        // 文字列に変換
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ResourcesHandler.getString(
                GnomesResourcesConstants.YY01_0107));
        String formattedString = zdtUtc.format(formatter);

        SimpleDateFormat sdf = new SimpleDateFormat(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0107));
        sdf.setTimeZone(TimeZone.getTimeZone(CommonConstants.ZONEID_UTC));
        try {
            // Dateに変換
            utcDate = sdf.parse(formattedString);

        }
        catch (ParseException e) {
            // 年月日の変換ができませんでした。年月日の設定：{0}
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0254);
            Object[] errParam = {ResourcesHandler.getString(GnomesResourcesConstants.YY01_0107)};
            ex.setMessageParams(errParam);
            throw ex;
        }

        return utcDate;
    }

    /**
     * UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更.
     * <pre>
     * UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更する。
     * </pre>
     * @param utcDate UTCの日時
     * @param timezone タイムゾーン
     * @return ロケール(タイムゾーン)の日時
     * @throws GnomesAppException
     */

    public static String convertStringUtcToLocale(Date utcDate, String format, TimeZone timezone)
            throws GnomesAppException
    {

        // 変換処理を記述する
        Instant instant = utcDate.toInstant();
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(CommonConstants.ZONEID_UTC));
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(CommonConstants.ZONEID_UTC));

        // 任意のタイムゾーンに変換
        ZonedDateTime zdtTz = zdt.withZoneSameInstant(ZoneId.of(timezone.getID()));

        // 文字列に変換
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String tzDateString = zdtTz.format(formatter);

        return tzDateString;
    }

    /**
     * システム日時(ZonedDateTime型)取得.
     * <pre>
     * UTCもしくはデフォルトロケールに対応したシステム日付の取得を行う。
     * </pre>
     * @return システム日時
     */
    public static ZonedDateTime getZonedDateTimeNow()
    {

        // 処理を記述する
        ZonedDateTime zdt = ZonedDateTime.now();
        LocalDateTime ldt = LocalDateTime.now();

        // 日時をUTCもしくはローカル日付を使用するか判断
        if (TimeZone.getDefault().getID().equals(CommonConstants.ZONEID_UTC)) {
            // UTCを設定
            zdt = ldt.atZone(ZoneId.of(CommonConstants.ZONEID_UTC));
        }
        else {
            // ローカル日付を設定
            zdt = ldt.atZone(ZoneId.systemDefault());
        }

        return zdt;
    }

    /**
     * 任意のロケール(タイムゾーン)のString型の値をDate型に変更.
     * <pre>
     * 任意のロケール(タイムゾーン)のString型の値をDate型に変更する。
     * </pre>
     * @param localeDate ロケール(タイムゾーン)の日時
     * @param format ロケール(タイムゾーン)の日時のフォーマット
     * @param timezone タイムゾーン
     * @param locale ロケール
     * @return Date型
     * @throws ParseException
     * @throws GnomesAppException
     */
    public static Date convertLocalStringToDateFormat(String localeDate, String format, TimeZone timezone,
            Locale locale) throws GnomesAppException
    {

        String yearMonthDayFormat = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0003, locale); //年月日(yyyy/MM/dd)
        String yearMonthFormat = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0094, locale); //年月(yyyy/MM)
        Date date = null;

        if (format.equals(yearMonthDayFormat) || format.equals(yearMonthFormat)) {
            try {
                // 日付フォーマットが年月日、年月だった場合、UTC、ユーザロケールに関わらず、UTCのDate型に変換は行わない
                // 文字列からDate型に変換
                date = ConverterUtils.stringToDateFormat(localeDate, format);
            }
            catch (ParseException e) {
                // 年月日の変換ができませんでした。年月日の設定：{0}
                GnomesAppException ex = new GnomesAppException(e);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0254);
                Object[] errParam = {ResourcesHandler.getString(GnomesResourcesConstants.YY01_0003, locale)};
                ex.setMessageParams(errParam);
                throw ex;
            }
        }
        else {
            // 日付フォーマットが年月日、年月以外だったら、UTCのDate型に変換する
            date = convertStringLocaleToUtc(localeDate, format, timezone);
        }

        return date;
    }

    /**
     * UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更.
     * <pre>
     * UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更する。
     * </pre>
     * @param date 日時
     * @param datePattern ロケール(タイムゾーン)の日時のフォーマット
     * @param timezone タイムゾーン
     * @param locale ロケール
     * @return ロケール(タイムゾーン)の日時
     * @throws GnomesAppException
     * @throws ParseException
     */
    public static String convertDateToLocaleString(Date date, String datePattern, TimeZone timezone, Locale locale)
            throws GnomesAppException
    {

        String yearMonthDayFormat = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0003, locale); //年月日(yyyy/MM/dd)
        String yearMonthFormat = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0094, locale); //年月(yyyy/MM)
        String stringDate = null;

        if (datePattern.equals(yearMonthDayFormat) || datePattern.equals(yearMonthFormat)) {
            try {
                // 日付フォーマットが年月日、年月だった場合、UTC、ユーザロケールに関わらず、UTCのDate型に変換は行わない
                // 文字列からDate型に変換
                stringDate = ConverterUtils.dateTimeToString(date, datePattern);
            }
            catch (ParseException e) {
                // 年月日の変換ができませんでした。年月日の設定：{0}
                GnomesAppException ex = new GnomesAppException(e);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0254);
                Object[] errParam = {ResourcesHandler.getString(GnomesResourcesConstants.YY01_0003, locale)};
                ex.setMessageParams(errParam);
                throw ex;
            }
        }
        else {
            // 日付フォーマットが年月日、年月以外だったら、UTCのDate型に変換する
            stringDate = convertStringUtcToLocale(date, datePattern, timezone);
        }
        return stringDate;
    }

    /**
     * 年月日時が存在するフォーマットの場合のみ任意のロケール(タイムゾーン)のString型の値をUTCのDate型に変更.
     * <pre>
     * 年月日時が存在するフォーマットの場合のみ、任意のロケール(タイムゾーン)のString型の値をUTCのDate型に変更する。
     * </pre>
     * @param localeDate ロケール(タイムゾーン)の日時
     * @param format ロケール(タイムゾーン)の日時のフォーマット
     * @param timezone タイムゾーン
     * @return UTCの日時
     * @throws ParseException
     * @throws GnomesAppException
     */
    public static Date convertStringLocaleToUtcDateTimePattern(String localeDate, String format, TimeZone timezone)
            throws GnomesAppException
    {

        try {
            LocalDateTime ldt = LocalDateTime.parse(localeDate, DateTimeFormatter.ofPattern(format));
            // 日付フォーマットが年月日時分秒、年月日時分だったら、UTCのDate型に変換する
            return convertStringLocaleToUtc(localeDate, format, timezone);

        }
       //年月日時が存在しないフォーマットの場合はエラーをキャッチ
        catch (DateTimeParseException dpe) {
            try {
                //ローカル時刻のままDateに変換
                Date localDate = ConverterUtils.stringToDateFormat(localeDate, format);
                return localDate;
            }
            catch (ParseException e) {
                // 年月日の変換ができませんでした。年月日の設定：{0}
                GnomesAppException ex = new GnomesAppException(e);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0254);
                Object[] errParam = {ResourcesHandler.getString(GnomesResourcesConstants.YY01_0107)};
                ex.setMessageParams(errParam);
                throw ex;
            }
        }
    }

    /**
     * 年月日時が存在するフォーマットの場合のみUTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更.
     * <pre>
     * 年月日時が存在するフォーマットの場合のみ、UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更する。
     * </pre>
     * @param utcDate UTCの日時
     * @param timezone タイムゾーン
     * @return ロケール(タイムゾーン)の日時
     * @throws GnomesAppException
     */

    public static String convertStringUtcToLocaleDateTimePattern(Date utcDate, String format, TimeZone timezone)
            throws GnomesAppException
    {

        //現在時刻取得
        LocalDateTime nowLdt = LocalDateTime.now();
        //現在時刻を引数のフォーマットで文字列化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String nowStr = nowLdt.format(formatter);

        try {
            //文字列化した現在時刻を日付変換
            LocalDateTime.parse(nowStr, DateTimeFormatter.ofPattern(format));
            //例外がなければUTC→ローカル変換
            return convertStringUtcToLocale(utcDate, format, timezone);

        }
        //年月日時が存在しないフォーマットの場合はエラーをキャッチ
        catch (DateTimeParseException e) {
            // 例外が起きた場合はUTCのまま文字列に変換
            Instant instant = utcDate.toInstant();
            LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(CommonConstants.ZONEID_UTC));
            return ldt.format(formatter);
        }

    }
}
