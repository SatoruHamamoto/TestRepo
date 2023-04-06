package com.gnomes.common.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.GnomesDateTimeFormat;
import com.gnomes.common.constants.CommonEnums.RoundCalculateDiv;
import com.gnomes.common.dto.PullDownDto;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * コンバータクラス 様々な種類のコンバートを行う共通クラス
 * （日付時刻、JSON、論理型、LinkHashMapなど）<br>
 * （注意）タイムゾーンを使った共通関数一覧は使用禁止とする2018/09/05
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/01 YJP/K.Gotanda             初版
 * R0.01.02 2018/09/05 YJP/S.Hamamoto			Date型のコンバートを追加
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class ConverterUtils
{

    /**
     * 最大少数桁数
     */
    public static final int DECIMAL_MAX_VALUE = 10;

    /**
     * タイムゾーン付日付型から日付型に変換（通常はあまり使いません）
     * @param value 変換元文字列
     * @return 変換結果
     * @throws ParseException
     */
    public static Date utcToDate(final ZonedDateTime date) throws ParseException
    {
        if (date == null) {
            return null;
        }

        Date result;
        // UTC(協定世界時)
        if (ZoneId.systemDefault().getId().equals(CommonConstants.ZONEID_UTC)) {
            result = Date.from(date.withZoneSameInstant(ZoneId.of(CommonConstants.ZONEID_UTC)).withZoneSameLocal(
                    ZoneId.systemDefault()).toInstant());
        }
        // デフォルトタイムゾーン
        else {
            result = Date.from(date.withZoneSameLocal(ZoneId.systemDefault()).toInstant());
        }
        return result;
    }

    /**
     * タイムゾーン付日付型から日付型に変換（通常はあまり使いません）
     * @param value 変換元文字列
     * @return 変換結果
     */
    public static Timestamp utcToTimestamp(final ZonedDateTime date)
    {

        if (date == null) {
            return null;
        }

        Timestamp result;
        // UTC(協定世界時)
        if (ZoneId.systemDefault().getId().equals(CommonConstants.ZONEID_UTC)) {
            result = Timestamp.from(date.withZoneSameInstant(ZoneId.of(CommonConstants.ZONEID_UTC)).withZoneSameLocal(
                    ZoneId.systemDefault()).toInstant());
        }
        // デフォルトタイムゾーン
        else {
            result = Timestamp.from(date.withZoneSameInstant(ZoneId.systemDefault()).withZoneSameLocal(
                    ZoneId.systemDefault()).toInstant());
        }
        return result;
    }

    /**
     * 日付からタイムゾーン付日付型に変換（通常はあまり使いません）
     * @param value 変換元文字列
     * @param format フォーマット
     * @return 変換結果
     */
    public static ZonedDateTime dateToUtc(final Date date)
    {
        if (date == null) {
            return null;
        }

        ZonedDateTime result = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // UTC(協定世界時)
        if (ZoneId.systemDefault().getId().equals(CommonConstants.ZONEID_UTC)) {
            result = result.withZoneSameLocal(ZoneId.of(CommonConstants.ZONEID_GMT)).withZoneSameInstant(
                    ZoneId.systemDefault());
        }
        // デフォルトタイムゾーン
        else {
            result = result.withZoneSameLocal(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.systemDefault());
        }
        return result;
    }

    /* DateからLocalDateTime変換（通常はあまり使いません）
     * @param date 変換元日付
     * @return 変換後日付
     */
    public static LocalDateTime dateToLocalDateTime(Date date)
    {

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);

    }

    /**
     * （非推奨）文字列からタイムゾーン付日付型に変換
     * @param value 変換元文字列
     * @param format フォーマット
     * @return 変換結果
     * @throws ParseException 変換エラー
     */
    public static ZonedDateTime stringToUtcFormat(final String value, final String format) throws ParseException
    {

        ZonedDateTime result = null;
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        result = LocalDateTime.parse(value, dtf).atZone(ZoneId.systemDefault());

        if (dtf.format(result).equalsIgnoreCase(value)) {
            return result;
        }
        else {
            throw new ParseException("Unparseable date: \"" + value + "\"", 0);
        }
    }

    /**
     * （非推奨）タイムゾーン付日付型から文字列変換
     * @param value 変換元
     * @param format 変換フォーマット
     * @return 変換結果
     */
    public static String utcToString(final ZonedDateTime date, final String format)
    {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * （非推奨）TimeStamp型からタイムゾーン付日付型変換
     * @param timestamp 変換元
     * @param format 変換フォーマット
     * @return
     * @throws ParseException
     */
    public static String tsToString(final Timestamp timestamp, final String format) throws ParseException
    {
        Date date = new Date(timestamp.getTime());
        return dateTimeToString(date, format);
        //    	SimpleDateFormat sdf = new SimpleDateFormat(format);
        //    	return sdf.format(timestamp);
    }

    /**
     * （非推奨）文字列から日付型に変換。引数のフォーマット文字列（yyyy/mm/dd hh:mmなど)を指定し
     * Date型の値に変換する。フォーマットを自動判定する場合は、stringToDateTime()を使用すると良い
     * @param value 変換元文字列
     * @param format フォーマット
     * @return 変換結果
     * @throws ParseException 変換エラー
     */
    public static Date stringToDateFormat(final String value, final String format) throws ParseException
    {

        Date result = null;
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(true);
        result = sdf.parse(value);
        if (sdf.format(result).equalsIgnoreCase(value)) {
            return result;
        }
        else {
            throw new ParseException("Unparseable date: \"" + value + "\"", 0);
        }
    }

    /**
     * 文字列からDate型の日時に変換します。<br>
     * 文字列のサポートするフォーマットは以下の通り<br>
     * ・yyyy/MM/dd HH:mm:ss<br>
     * ・yyyy/MM/dd HH:mm<br>
     * ・yyyy/MM/dd<br>
     * ・yyyy/MM ( 追加 ) "2020/01" -> 2020/01/01 00:00:00 日は1日に固定<br>
     *
     * フォーマットを自動対応します。
     * @param value 変換元文字列
     * @return 変換日時
     * @throws ParseException 変換エラー
     */
    public static Date stringToDateTime(final String value) throws ParseException
    {
        return stringToDateTime(value, 1);
    }

    /**
     * 文字列からDate型の日時に変換します。<br>
     * 文字列のサポートするフォーマットは以下の通り<br>
     * ・yyyy/MM/dd HH:mm:ss<br>
     * ・yyyy/MM/dd HH:mm<br>
     * ・yyyy/MM/dd<br>
     * ・yyyy/MM ( 追加 ) "2020/01" -> 2020/01/01 00:00:00<br>
     *
     * フォーマットを自動対応します。
     * @param value 変換元文字列
     * @param complementDay yyyy/mmフォーマットのみ与えられる補完する日
     * @return 変換日時
     * @throws ParseException 変換エラー
     */
    public static Date stringToDateTime(final String value, int complementDay) throws ParseException
    {
        return stringToDateTime(value, complementDay, Locale.getDefault());
    }

    /**
     * 文字列からDate型の日時に変換します。<br>
     * 文字列のサポートするフォーマットは以下の通り<br>
     * ・yyyy/MM/dd HH:mm:ss<br>
     * ・yyyy/MM/dd HH:mm<br>
     * ・yyyy/MM/dd<br>
     * ・yyyy/MM ( 追加 ) "2020/01" -> 2020/01/01 00:00:00<br>
     *
     * フォーマットを自動対応します。
     * @param value 変換元文字列
     * @param complementDay yyyy/mmフォーマットのみ与えられる補完する日
     * @param locale ロケール
     * @return 変換日時
     * @throws ParseException 変換エラー
     */
    public static Date stringToDateTime(final String value, int complementDay, Locale locale) throws ParseException
    {
        Date result = null;
        ParseException pex = null;

        if (value == null) {
            return null;
        }

        //例外として、yyyy/MMの場合を別扱いにする
        if (value.matches("[0-9]{2,4}/[0-9]{1,2}$")) {
            //年と月を抜き出す。すでにフォーマットチェックしているので抜き出しは単純でよい
            String[] ym = value.split("/");
            Calendar ymCalender = Calendar.getInstance();
            ymCalender.clear();
            ymCalender.set(Integer.parseInt(ym[0]), Integer.parseInt(ym[1]) - 1, complementDay);
            return ymCalender.getTime();
        }

        // YY01.0001=yyyy/MM/dd HH:mm:ss
        // YY01.0002=yyyy/MM/dd HH:mm
        // YY01.0003=yyyy/MM/dd
        // YY01.0004=yyyy/MM
        String[] ptnIds = {GnomesResourcesConstants.YY01_0001, GnomesResourcesConstants.YY01_0002,
                GnomesResourcesConstants.YY01_0003};

        // Convert all other types to String & handle
        final String stringValue = value.trim();
        if (stringValue.length() == 0) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setLenient(true);

        for (String ptnId : ptnIds) {
            String ptn = ResourcesHandler.getString(ptnId, locale);

            try {
                sdf.applyPattern(ptn);
                result = sdf.parse(value);
                if (sdf.format(result).equalsIgnoreCase(value)) {
                    break;
                }
                else {
                    throw new ParseException("Unparseable date: \"" + value + "\"", 0);
                }
            }
            catch (ParseException e) {
                pex = e;
                continue;
            }
        }
        if (result == null && pex != null) {
            throw pex;
        }

        return result;
    }

    /**
     * 文字列からDate型の日付として変換します。<br>
     * 文字列のサポートするフォーマットは以下の通り<br>
     * ・yyyy/MM/dd HH:mm:ss<br>
     * ・yyyy/MM/dd HH:mm<br>
     * ・yyyy/MM/dd<br>
     * ・yyyy/MM ( 追加 ) "2020/01" -> 2020/01/01 00:00:00 日は1日に設定されます<br>
     *
     * 日時が入っていても、日付のみ出力します（時分秒＝0:00:00にリセットされます）。
     * @param value 変換元文字列
     * @return 日付のみ変換されたDate型データ
     * @throws ParseException 変換エラー
     */
    public static Date stringToDate(final String value) throws ParseException
    {
        return stringToDate(value, 1);
    }

    /**
     * 文字列からDate型の日付として変換します。<br>
     * 文字列のサポートするフォーマットは以下の通り<br>
     * ・yyyy/MM/dd HH:mm:ss<br>
     * ・yyyy/MM/dd HH:mm<br>
     * ・yyyy/MM/dd<br>
     * ・yyyy/MM ( 追加 ) "2020/01" -> 2020/01/01 00:00:00<br>
     *
     * 日時が入っていても、日付のみ出力します（時分秒＝0:00:00にリセットされます）。
     * @param value 変換元文字列
     * @param complementDay yyyy/mmフォーマットのみ与えられる補完する日
     * @return 日付のみ変換されたDate型データ
     * @throws ParseException 変換エラー
     */
    public static Date stringToDate(final String value, int complementDay) throws ParseException
    {

        Date d = ConverterUtils.stringToDateTime(value, complementDay);
        if (d == null) {
            return null;
        }

        Calendar dateTime = Calendar.getInstance();
        dateTime.setTime(d);
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
        return date.getTime();
    }

    /**
     * （非推奨）フォーマットを指定して日付型データから日付・時刻文字列に変換する。<br>
     * フォーマットは("yyyy/mm/dd hh:mm")などを指定する。<br>
     * フォーマットを直接文字で指定すると汎用性に欠けるので、フォーマットをenum化した版の<br>
     * dateTimeToString()を使用することをお勧めする。<br>
     * フォーマットが他のデータまたは変数入っている場合はこちらを使ってもよい。
     * @param value 変換元
     * @param format 変換フォーマット
     * @return 変換結果
     * @throws ParseException 変換エラー
     */
    public static String dateTimeToString(final Object value, final String format) throws ParseException
    {
        return ConverterUtils.dateTimeToString(value, null, null, format);
    }

    /**
     * フォーマットを指定して日付型データから日付・時刻文字列に変換する<br>
     * フォーマットは GnomesDateTimeFormat のENUMから指定する<br>
     * 例）dtString1 = ConverterUtils.dateTimeToString(dateValue,GnomesDateTimeFormat.YMD);
     * @param value 変換元Dateの値
     * @param enmFormat　フォーマット列挙
     * @return　変換結果の文字列
     * @throws ParseException
     */
    public static String dateTimeToString(final Date value, GnomesDateTimeFormat enmFormat) throws ParseException
    {
        String format = ResourcesHandler.getString(enmFormat.toString());
        return ConverterUtils.dateTimeToString(value, null, null, format);
    }

    /**
     * （非推奨）タイムゾーンやフォーマット、地域を指定した日付型から文字列変換
     * @param value 変換元
     * @param locale ロケール
     * @param timeZone タイムゾーン
     * @param format 変換フォーマット
     * @return 変換結果
     * @throws ParseException 変換エラー
     */
    public static String dateTimeToString(final Object value, final Locale locale, final TimeZone timeZone,
            final String format) throws ParseException
    {

        Date date = null;

        if (value == null) {
            return "";
        }

        if (value instanceof Date) {
            date = (Date) value;
        }
        else if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;

            ZonedDateTime zdt = localDateTime.atZone(timeZone.toZoneId());
            date = Date.from(zdt.toInstant());
        }
        else if (value instanceof ZonedDateTime) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            date = Date.from(zdt.toInstant());
        }
        else if (value instanceof Calendar) {
            date = ((Calendar) value).getTime();
        }
        else if (value instanceof Long) {
            date = new Date(((Long) value).longValue());
        }
        else if (value instanceof String) {
            date = ConverterUtils.stringToDateTime((String) value);
        }

        String result = null;
        if ((locale != null || format != null) && date != null) {
            DateFormat dataFormat = null;
            if (!StringUtil.isNullOrEmpty(format)) {
                dataFormat = new SimpleDateFormat(format);
            }
            else {
                if (Objects.isNull(locale)) {
                    dataFormat = DateFormat.getDateInstance(DateFormat.SHORT);
                }
                else {
                    dataFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
                }
            }

            if (timeZone != null) {
                dataFormat.setTimeZone(timeZone);
            }
            result = dataFormat.format(date);
        }
        else {
            result = value.toString();
        }

        return result;
    }

    /**
     * 文字列から数値変換
     * @param isCurrency 通貨フラグ
     * @param value 変換元
     * @return 変換結果
     * @throws ParseException 変換エラー
     */
    public static Number stringToNumber(final boolean isCurrency, final String value) throws ParseException
    {
        return stringToNumber(isCurrency, value, Locale.getDefault().toString());
    }

    /**
     * 文字列から数値変換
     * @param isCurrency 通貨フラグ
     * @param value 変換元
     * @param ロケール locale
     * @return 変換結果
     * @throws ParseException 変換エラー
     */
    public static Number stringToNumber(final boolean isCurrency, String value, String locale) throws ParseException
    {
        //System.out.println("locale..." + locale);

        Number result = null;
        NumberFormat nfNum = null;
        ParsePosition pp = new ParsePosition(0);

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        if (isCurrency) {
            // 通貨
            nfNum = NumberFormat.getCurrencyInstance();
        }
        else {

            if (locale != null) {
                String[] l = locale.split(CommonConstants.SPLIT_CHAR);
                if (l.length >= 2) {
                    // 数値
                    nfNum = NumberFormat.getNumberInstance(new Locale(l[0], l[1]));

                    DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale(l[0], l[1]));
                    char groupingSeparator = dfs.getGroupingSeparator();

                    if (groupingSeparator == CommonConstants.NON_BREAKING_SPACE) {

                        value = value.replace(' ', CommonConstants.NON_BREAKING_SPACE);

                    }

                }
                else {
                    throw new IllegalArgumentException("Locale characters cannot be abbreviated (e.g. \"ja\")");
                }
            }
            else {
                // 数値
                nfNum = NumberFormat.getNumberInstance();
            }
        }
        result = nfNum.parse(value, pp);

        // "123A"のような後方チェックに対応
        if (value.length() != pp.getIndex()) {
            throw new ParseException("ParsePosition not same", 0);
        }
        return result;
    }

    /**
     * 数値から文字列変換
     * @param isCurrency 通貨フラグ
     * @param value 変換元
     * @param decimalPoint 少数点桁数
     * @return 変換結果
     */
    public static String numberToString(final boolean isCurrency, final Object value, final Integer decimalPoint)
    {

        return ConverterUtils.numberToString(isCurrency, null, value, decimalPoint);
    }

    /**
     * 数値から文字列変換
     * @param isCurrency 通貨フラグ
     * @param locale ロケール文字
     * @param value 変換元
     * @param decimalPoint 少数点桁数
     * @return 変換結果
     */
    public static String numberToString(final boolean isCurrency, final String locale, final Object value,
            final Integer decimalPoint)
    {
        String result = null;
        NumberFormat nfNum = null;

        if (value == null) {
            return "";
        }

        if (isCurrency) {
            // 通貨
            if (locale == null) {
                nfNum = NumberFormat.getCurrencyInstance();
            }
            else {
                // en_US
                // ja_JP
                String[] l = locale.split(CommonConstants.SPLIT_CHAR);
                nfNum = NumberFormat.getCurrencyInstance(new Locale(l[0], l[1]));
            }
        }
        else {
            if (locale == null) {
                // 数値
                nfNum = NumberFormat.getNumberInstance();
            }
            else {
                String[] l = locale.split(CommonConstants.SPLIT_CHAR);
                nfNum = NumberFormat.getNumberInstance(new Locale(l[0], l[1]));

            }
        }

        if (decimalPoint != null) {
            int decimalValue = decimalPoint.intValue();

            if (decimalValue > DECIMAL_MAX_VALUE) {
                decimalValue = DECIMAL_MAX_VALUE;
            }

            nfNum.setMaximumFractionDigits(decimalValue); // 少数部分の最大桁数を設定
            nfNum.setMinimumFractionDigits(decimalValue); // 少数部分の最小桁数を設定
        }

        result = nfNum.format(new BigDecimal(value.toString()));

        return result;
    }

    /**
     * 論理型から数値変換
     * @param bool 真偽値
     * @return 変換結果
     */
    public static int boolToInt(boolean bool)
    {
        if (bool)
            return 1;
        return 0;
    }

    /**
     * 数値から論理型変換
     * @param i 数値フラグ
     * @return 変換結果
     */
    public static boolean IntTobool(int i)
    {
        if (i == boolToInt(true))
            return true;
        return false;
    }

    /**
     * Json読み込み
     * @param json Json文字
     * @exception 例外
     */
    public static <T> T readJson(String json, Class<T> valueType) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, valueType);
    }

    public static <T> T readJson(String json, TypeReference<?> valueTypeRef) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, valueTypeRef);

    }

    /**
     * Json生成
     * @return json文字列
     * @JsonProcessingException json作成例外
     */
    public static String getJson(Object obj) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(obj);
        return json;
    }

    /**
     * 左パディング.
     * <pre>
     * パディング桁数が未指定の場合、値をそのまま返却する。
     * </pre>
     * @param value 値
     * @param padValue パディング値
     * @param padLength パディング桁数
     * @return パディング後文字列
     */
    public static String lpad(String value, String padValue, int padLength)
    {

        if (StringUtil.isNullOrEmpty(padValue)) {
            return value;
        }

        String rValue = "";
        int startLen = 0;

        if (!StringUtil.isNullOrEmpty(value)) {

            rValue = value;
            startLen = value.getBytes().length;

        }

        StringBuilder result = new StringBuilder();

        for (int i = startLen; i < padLength; i++) {
            result.append(padValue);
        }

        return result.append(rValue).toString();

    }

    /**
     * 右パディング.
     * <pre>
     * パディング桁数が未指定の場合、値をそのまま返却する。
     * </pre>
     * @param value 値
     * @param padValue パディング値
     * @param padLength パディング桁数
     * @return パディング後文字列
     */
    public static String rpad(String value, String padValue, int padLength)
    {

        if (StringUtil.isNullOrEmpty(padValue)) {
            return value;
        }

        int spaceLen = 0;

        StringBuilder result = new StringBuilder();

        if (!StringUtil.isNullOrEmpty(value)) {

            spaceLen = padLength - value.getBytes().length;
            result.append(value);

        }
        else {
            spaceLen = padLength;
        }

        for (int i = 0; i < spaceLen; i++) {
            result.append(padValue);
        }

        return result.toString();

    }

    /**
     * 数値パディング
     * @param value 値
     * @param integerDigits 整数部桁数
     * @param fractionDigits 小数部桁数
     * @return パディング後文字列
     */
    public static String decimalPad(BigDecimal value, int integerDigits, int fractionDigits)
    {

        if (Objects.isNull(value)) {
            return "";
        }

        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumIntegerDigits(integerDigits);
        df.setMinimumIntegerDigits(integerDigits);
        df.setMaximumFractionDigits(fractionDigits);
        df.setMinimumFractionDigits(fractionDigits);

        return df.format(value);

    }

    /**
     * 数値パディング(小数値のみ)
     * @param value 値
     * @param fractionDigits 小数部桁数
     * @return パディング後文字列
     */
    public static String decimalPadFraction(BigDecimal value, int fractionDigits)
    {

        if (Objects.isNull(value)) {
            return "";
        }

        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(fractionDigits);
        df.setMinimumFractionDigits(fractionDigits);

        return df.format(value);

    }

    /**
     * プルダウンDTOからLinkHashMap変換
     * @param pullDownDto 真偽値
     * @return 変換結果
     */
    public static LinkedHashMap<String, String> pulldownDtoToLinkedHashMap(List<PullDownDto> pullDownDtoList)
    {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        for (PullDownDto pullDownDto : pullDownDtoList) {
            // 追加
            result.put(pullDownDto.getValue(), pullDownDto.getName());
        }

        return result;
    }

    /**
     * 丸め処理
     * @param val 丸め対象値
     * @param decimalPlace 小数点以下有効桁数
     * @param calculateDiv 丸め演算方式区分
     * @return 丸め処理後の値
     */
    public static BigDecimal roundCalculate(BigDecimal val, Integer decimalPlace, RoundCalculateDiv calculateDiv)
    {

        // 丸め処理後の値
        BigDecimal afterVal = null;

        switch (calculateDiv) {

            // 丸め演算方式区分に、四捨五入が設定されている場合
            case RoundCalculateDiv_RoundHalfUp :

                afterVal = val.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
                break;

            // 丸め演算方式区分に、切り上げが設定されている場合
            case RoundCalculateDiv_RoundUp :

                afterVal = val.setScale(decimalPlace, BigDecimal.ROUND_UP);
                break;

            // 丸め演算方式区分に、切り捨てが設定されている場合
            case RoundCalculateDiv_RoundDown :

                afterVal = val.setScale(decimalPlace, BigDecimal.ROUND_DOWN);
                break;

            // 丸め演算方式区分に、丸めなしが設定されている場合
            case RoundCalculateDiv_RoundNone :

                afterVal = val;
                break;
            
      		// 丸め演算方式区分に、切り捨てが設定されている場合
    		case RoundCalculateDiv_RoundJIS:

    			afterVal = val.setScale(decimalPlace, BigDecimal.ROUND_HALF_EVEN);
    			break;

            default :

                afterVal = val;

        }

        return afterVal;

    }

}
