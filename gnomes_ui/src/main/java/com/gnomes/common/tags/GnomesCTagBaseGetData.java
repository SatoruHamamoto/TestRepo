package com.gnomes.common.tags;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;

/**
 * タグ データ取得 共通処理
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/1/18 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagBaseGetData
{

    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /**
     * Objectのゲッターメソッドから値取得
     *
     * @param c 対象クラス
     * @param object 対象Object
     * @param name 取得項目名
     * @return 取得値
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    protected Object getObjectData(Class<?> c, Object object, String name) throws IntrospectionException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Object ret = null;

        // デスクリプタを用意
        PropertyDescriptor nameProp = new PropertyDescriptor(name, c);
        Method nameGetter = nameProp.getReadMethod();
        ret = nameGetter.invoke(object, (Object[]) null);

        return ret;
    }

    /**
     * Objectのフィールドから値取得
     *
     * @param object 取得元beanObject
     * @param name 取得フィールド名
     * @return 取得値
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected Object getObjectDataFromField(Object object, String name) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException
    {

        Field f = object.getClass().getDeclaredField(name);
        f.setAccessible(true);

        return f.get(object);
    }

    /**
     * 数値の文字列変換
     *
     * @param valueObj 対象値
     * @param isCurrency 金額判定フラグ
     * @param decimalPoint 小数点桁数
     * @return 変換後文字列
     * @throws ParseException 変換エラー
     */
    protected String getStringNumber(Object valueObj, boolean isCurrency, Integer decimalPoint) throws ParseException
    {
        String result = "";
        Object value = valueObj;

        Locale userLocal = gnomesSessionBean.getUserLocale();
        if (value != null) {
            if (value instanceof String) {
                String strNum = value.toString();
                value = ConverterUtils.stringToNumber(false, strNum,gnomesSessionBean.getUserLocale().toString());
            }
            result = ConverterUtils.numberToString(isCurrency, userLocal.toString(), value, decimalPoint);
        }
        return result;
    }

    /**
     * 日付の文字列変換
     *
     * @param valueObj 対象値
     * @param datePattern 変換フォーマット
     * @param isZonedDataTime タイムゾーン判定フラグ
     * @return 変換後文字列
     * @throws ParseException 変換エラー
     * @throws GnomesAppException
     */
    protected String getStringDate(Object valueObj, String datePattern) throws ParseException, GnomesAppException
    {
        String result = "";
        Object value = valueObj;

        if (value != null) {
            if (value instanceof String) {
                String strDate = value.toString();
                try {
                    value = ConverterUtils.stringToDateTime(strDate);
                }
                catch (ParseException e) {
                    value = ConverterUtils.stringToDateFormat(strDate, datePattern);
                }
            }

            if (value != null) {
                //Web.xmlのタイムゾーンがUTCの場合,
                if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {
                    //UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更
                    result = GnomesDateUtil.convertDateToLocaleString((Date) value, datePattern,
                            gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
                }
                else {
                    //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                    result = ConverterUtils.dateTimeToString((Date) value, datePattern);
                }
                return result;
            }

        }
        result = ConverterUtils.dateTimeToString(value, datePattern);

        return result;
    }
}
