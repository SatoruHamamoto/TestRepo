package com.gnomes.common.command;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.RequestParamAllowNull;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.uiservice.ContainerRequest;

@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class RequestParamMapProducer
{

    //ロガー
    @Inject
    transient Logger                         logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper                      logHelper;

    @Inject
    ContainerRequest                         requestContext;

    @Inject
    protected GnomesSessionBean              gnomesSessionBean;

    /** nullを許可するリクエストパラメータMap */
    protected static HashMap<String, String> requestParamAllowNullMap;

    /**
     * static初期化.
     */
    static {
        requestParamAllowNullMap = RequestParamAllowNull.getAllEnum();
    }

    /**
     * リクエストパラメータ取得 String
     *
     * @param ip インジェクトポイント
     * @return String 取得値
     */
    @Produces
    @RequestParamMap
    public String getStringRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        String requestParamValue = null;

        try {
            Object v = requestContext.getRequestParamValueWithWapper(requestParamName);
            if (v != null) {
                requestParamValue = (String) v;
            }
            else {
                //リクエストパラメータの値ががnullの場合定義がおかしい
                //nullを許可するリクエストパラメータではない場合メッセージを出力
                if (!requestParamAllowNullMap.containsKey(requestParamName)) {
                    this.logHelper.fine(this.logger, "RequestParamMapProducer", "getStringRequestParamMap",
                            "The request parameter acquisition result was NULL. It is possible that the component with the following name does not exist in JSP or custom tag definition. [" + requestParamName + "]");
                }
            }

        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }
        return requestParamValue;
    }

    /**
     * HTTPリクエストパラメータ取得 Date
     *
     * @param ip インジェクトポイント
     * @return Date 取得値
     */
    @Produces
    @RequestParamMap
    public Date getDateRequestParamMap(InjectionPoint ip) throws GnomesAppException, ParseException
    {

        boolean dateFormatIsYYmm = false;
        Object requestParamValue = this.getObjectRequestParamMap(ip);
        Object extRequestParamValue = this.getObjectExtRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがDate型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof Date) {
            return (Date) requestParamValue;
        }

        //        if (((String)(requestParamValue)).trim().length() == 0) {
        //            return null;
        //        }

        //requestParamValueがYYYY/MMの場合はextRequestParamValueの値は無視する
        String checkFormatValue = (String) requestParamValue;
        String yMPattern = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0108);
        if (checkFormatValue.matches(yMPattern)) {
            extRequestParamValue = null;
            dateFormatIsYYmm = true;
        }

        //

        String strDateTime = ((String) (requestParamValue)).trim();

        String strDateTimeTime = null;

        //拡張パラメータの場合は一度文字に変換する
        if (Objects.nonNull(extRequestParamValue)) {
            strDateTimeTime = ((String) (extRequestParamValue)).trim();
        }

        //日付が空白（省略されている場合のチェック）
        if (StringUtil.isNullOrEmpty(strDateTime)) {
            //日付が省略されていて、時刻もnull or ""ならば
            //完全省略されているものとみなしnullで返却する
            if (StringUtil.isNullOrEmpty(strDateTimeTime)) {
                return null;
            }
            //拡張リクエストパラメータ（時刻等）が"00:00"の場合は
            //時刻が入っていてもデフォルト値が入っていたらnullで返却する
            if (strDateTimeTime.equals(CommonConstants.DEFAULT_TIMESTRING)) {
                return null;
            }
        }

        if (extRequestParamValue != null) {
            strDateTime = (String) (requestParamValue) + " " + (String) (extRequestParamValue);
        }
        else {
            strDateTime = (String) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        // 日付型変換
        Date value = null;
        String formatId = "";
        String format = "";
        try {
            formatId = annotation.dateFormatResourceId();
            //年月(yyyy/MM)の場合は特別にformatIdを固定する
            if (dateFormatIsYYmm) {
                format = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0094,
                        gnomesSessionBean.getUserLocale());
            }
            else {
                format = ResourcesHandler.getString(formatId, gnomesSessionBean.getUserLocale());
            }

            //Web.xmlのタイムゾーンがUTCの場合,
            if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {
                // ロケール(タイムゾーン)のString型の値をUTCのDate型に変更
                value = GnomesDateUtil.convertLocalStringToDateFormat(strDateTime.toString(), format,
                        gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
            }
            else {
                //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                value = ConverterUtils.stringToDateFormat(strDateTime.toString(), format);
            }

        }
        catch (DateTimeParseException e) {
            // 変換エラー
            String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
            String resourceId = annotation.resourceId();
            String label = ResourcesHandler.getString(resourceId);
            addRequestErr(dataType, resourceId, label);
            StringBuilder sb = new StringBuilder();
            sb.append("Validation Check Error DateTimeFormat is invalid.");
            sb.append(" value = " + strDateTime);
            sb.append(" format = " + format);
            sb.append(" label = " + label);
            logHelper.severe(logger, null, null, sb.toString());
        }

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 ZonedDateTime
     *
     * @param ip インジェクトポイント
     * @return Date 取得値
     */
    @Produces
    @RequestParamMap
    public ZonedDateTime getZonedDateTimeRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがDate型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof ZonedDateTime) {
            return (ZonedDateTime) requestParamValue;
        }

        //        if (((String)(requestParamValue)).trim().length() == 0) {
        //            return null;
        //        }

        String strDateTime = null;
        Object extRequestParamValue = this.getObjectExtRequestParamMap(ip);
        if (extRequestParamValue != null) {
            strDateTime = (String) (requestParamValue) + " " + (String) (extRequestParamValue);
        }
        else {
            strDateTime = (String) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        // 日付型変換
        ZonedDateTime value = null;
        try {
            String formatId = annotation.dateFormatResourceId();
            String format = ResourcesHandler.getString(formatId);
            // yyyy/MM/dd に対応できない
            //            value = ConverterUtils.stringToUtcFormat(strDateTime, format);
            Date date = ConverterUtils.stringToDateFormat(strDateTime, format);
            value = ConverterUtils.dateToUtc(date);
        }
        catch (DateTimeParseException | ParseException e) {
            // 変換エラー
            String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
            String resourceId = annotation.resourceId();
            String label = ResourcesHandler.getString(resourceId);
            addRequestErr(dataType, resourceId, label);
        }

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 Integer
     *
     * @param ip インジェクトポイント
     * @return Integer 取得値
     */
    @Produces
    @RequestParamMap
    public Integer getIntegerRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがInteger型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof Integer) {
            return (Integer) requestParamValue;
        }

        if (((String) (requestParamValue)).trim().length() == 0) {
            return null;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        Integer value = null;
        try {
            Number num = ConverterUtils.stringToNumber(annotation.isCurrency(), (String) requestParamValue);
            value = Integer.parseInt(num.toString());
        }
        catch (ParseException e) {
            // 変換エラー
            String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
            String resourceId = annotation.resourceId();
            String label = ResourcesHandler.getString(resourceId);
            addRequestErr(dataType, resourceId, label);
        }

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 BigDecimal
     *
     * @param ip インジェクトポイント
     * @return BigDecimal 取得値
     */
    @Produces
    @RequestParamMap
    public BigDecimal getBigDecimalRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがBigDecimal型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof BigDecimal) {
            return (BigDecimal) requestParamValue;
        }

        if (((String) (requestParamValue)).trim().length() == 0) {
            return null;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        BigDecimal value = null;
        try {
            Number num = ConverterUtils.stringToNumber(annotation.isCurrency(), (String) requestParamValue, gnomesSessionBean.getUserLocale().toString());
            value = new BigDecimal(num.toString());
        }
        catch (ParseException e) {
            // 変換エラー
            String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
            String resourceId = annotation.resourceId();
            String label = ResourcesHandler.getString(resourceId);
            addRequestErr(dataType, resourceId, label);
        }

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 Long
     *
     * @param ip インジェクトポイント
     * @return BigDecimal 取得値
     */
    @Produces
    @RequestParamMap
    public Long getLongRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがLong型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof Long) {
            return (Long) requestParamValue;
        }

        if (((String) (requestParamValue)).trim().length() == 0) {
            return null;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        Long value = null;
        try {
            Number num = ConverterUtils.stringToNumber(annotation.isCurrency(), (String) requestParamValue);
            value = Long.parseLong(num.toString());
        }
        catch (ParseException e) {
            // 変換エラー
            String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
            String resourceId = annotation.resourceId();
            String label = ResourcesHandler.getString(resourceId);
            addRequestErr(dataType, resourceId, label);
        }

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 SearchSetting
     *
     * @param ip インジェクトポイント
     * @return SearchSetting 取得値
     */
    @Produces
    @RequestParamMap
    public SearchSetting getSearchSettingRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        // JSON形式から取得
        SearchSetting value = getObjectJson(ip, SearchSetting.class);

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 SearchSetting
     *
     * @param ip インジェクトポイント
     * @return SearchSetting 取得値
     */
    @Produces
    @RequestParamMap
    public Map<String, SearchSetting> getMapSearchSettingRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        // JSON形式から取得
        //        Map<String, SearchSetting> value = getTypeReferenceJson(ip);
        Map<String, SearchSetting> value = getTypeReferenceJson(ip, new TypeReference<Map<String, SearchSetting>>()
        {
        });

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得 Map<String, String>
     *
     * @param ip インジェクトポイント
     * @return Map<String, String> 取得値
     */
    @Produces
    @RequestParamMap
    public Map<String, String> getMapStringRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        // JSON形式から取得
        Map<String, String> value = getTypeReferenceJson(ip);

        return value;
    }

    /**
     * リクエストパラメータ取得 String配列
     *
     * @param ip インジェクトポイント
     * @return String 取得値
     */
    @Produces
    @RequestParamMap
    public String[] getStringRequestParamMaps(InjectionPoint ip) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        String[] requestParamValue = null;

        try {
            Object v = requestContext.getRequestParamValuesWithWapper(requestParamName);
            if (v != null) {
                requestParamValue = (String[]) v;
            }

        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }
        return requestParamValue;
    }

    /**
     * リクエストパラメータ取得 Stringリスト
     *
     * @param ip インジェクトポイント
     * @return String 取得値
     */
    @Produces
    @RequestParamMap
    public List<String> getStringRequestParamMapList(InjectionPoint ip) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        List<String> requestParamValue = null;

        try {
            Object v = requestContext.getRequestParamValuesWithWapper(requestParamName);
            if (v != null) {
                requestParamValue = Arrays.asList((String[]) v);
            }

        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }
        return requestParamValue;
    }

    /**
     * HTTPリクエストパラメータ取得 Date配列
     *
     * @param ip インジェクトポイント
     * @return Date[] 取得値
     * @throws ParseException
     */
    @Produces
    @RequestParamMap
    public Date[] getDateRequestParamMaps(InjectionPoint ip) throws GnomesAppException, ParseException
    {

        Object requestParamValue = this.getObjectsRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがDate型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof Date[]) {
            return (Date[]) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        // 日付型変換
        Date result[] = null;

        String[] strValue = (String[]) requestParamValue;
        result = new Date[strValue.length];

        String formatId = annotation.dateFormatResourceId();
        String format = ResourcesHandler.getString(formatId, gnomesSessionBean.getUserLocale());

        for (int i = 0; i < strValue.length; i++) {

            try {
                if (strValue[i].trim().length() > 0) {

                    //Web.xmlのタイムゾーンがUTCの場合,
                    if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {
                        // ロケール(タイムゾーン)のString型の値をUTCのDate型に変更
                        result[i] = GnomesDateUtil.convertLocalStringToDateFormat(strValue[i], format,
                                gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
                    }
                    else {
                        //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                        result[i] = ConverterUtils.stringToDateFormat(strValue[i], format);
                    }
                }
            }
            catch (DateTimeParseException e) {
                // 変換エラー
                String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
                String resourceId = annotation.resourceId();
                String label = ResourcesHandler.getString(resourceId);
                addRequestTableErr(dataType, i, resourceId, label);
            }

        }

        return result;
    }

    /**
     * HTTPリクエストパラメータ取得 ZonedDateTime配列
     *
     * @param ip インジェクトポイント
     * @return Date 取得値
     */
    @Produces
    @RequestParamMap
    public ZonedDateTime[] getZonedDateTimeRequestParamMaps(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectsRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがDate型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof ZonedDateTime[]) {
            return (ZonedDateTime[]) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        // 日付型変換
        ZonedDateTime result[] = null;

        String[] strValue = (String[]) requestParamValue;
        result = new ZonedDateTime[strValue.length];

        String formatId = annotation.dateFormatResourceId();
        String format = ResourcesHandler.getString(formatId);

        for (int i = 0; i < strValue.length; i++) {

            try {
                if (strValue[i].trim().length() > 0) {
                    // yyyy/MM/ddに対応できない
                    //                        result[i] = ConverterUtils.stringToUtcFormat(strValue[i], format);
                    Date date = ConverterUtils.stringToDateFormat(strValue[i], format);
                    result[i] = ConverterUtils.dateToUtc(date);
                }
            }
            catch (DateTimeParseException | ParseException e) {
                // 変換エラー
                String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
                String resourceId = annotation.resourceId();
                String label = ResourcesHandler.getString(resourceId);
                addRequestTableErr(dataType, i, resourceId, label);
            }

        }

        return result;
    }

    /**
     * HTTPリクエストパラメータ取得 Integer配列
     *
     * @param ip インジェクトポイント
     * @return Integer 取得値
     */
    @Produces
    @RequestParamMap
    public Integer[] getIntegerRequestParamMaps(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectsRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがInteger型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof Integer[]) {
            return (Integer[]) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        Integer[] result = null;

        String[] strValue = (String[]) requestParamValue;
        result = new Integer[strValue.length];

        for (int i = 0; i < strValue.length; i++) {
            try {

                if (strValue[i].trim().length() > 0) {
                    Number num = ConverterUtils.stringToNumber(annotation.isCurrency(), strValue[i]);
                    result[i] = Integer.parseInt(num.toString());
                }
            }
            catch (ParseException e) {
                // 変換エラー
                String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
                String resourceId = annotation.resourceId();
                String label = ResourcesHandler.getString(resourceId);
                addRequestTableErr(dataType, i, resourceId, label);
            }

        }

        return result;
    }

    /**
     * HTTPリクエストパラメータ取得 BigDecimal配列
     *
     * @param ip インジェクトポイント
     * @return BigDecimal 取得値
     */
    @Produces
    @RequestParamMap
    public BigDecimal[] getBigDecimalRequestParamMaps(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectsRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがBigDecima[]l型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof BigDecimal[]) {
            return (BigDecimal[]) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        BigDecimal[] result = null;
        String[] strValue = (String[]) requestParamValue;
        result = new BigDecimal[strValue.length];

        for (int i = 0; i < strValue.length; i++) {

            try {
                if (strValue[i].trim().length() > 0) {
                    Number num = ConverterUtils.stringToNumber(annotation.isCurrency(), strValue[i]);
                    result[i] = new BigDecimal(num.toString());
                }
            }
            catch (ParseException e) {
                // 変換エラー
                String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
                String resourceId = annotation.resourceId();
                String label = ResourcesHandler.getString(resourceId);
                addRequestTableErr(dataType, i, resourceId, label);
            }
        }

        return result;
    }

    /**
     * HTTPリクエストパラメータ取得 Long
     *
     * @param ip インジェクトポイント
     * @return BigDecimal 取得値
     */
    @Produces
    @RequestParamMap
    public Long[] getLongRequestParamMaps(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = this.getObjectsRequestParamMap(ip);

        if (requestParamValue == null) {
            return null;
        }

        // リクエストがLong型の場合（フォワード、リダイレクト時）はそのまま
        if (requestParamValue instanceof Long[]) {
            return (Long[]) requestParamValue;
        }

        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        Long[] result = null;
        String[] strValue = (String[]) requestParamValue;
        result = new Long[strValue.length];

        for (int i = 0; i < strValue.length; i++) {
            try {

                if (strValue[i].trim().length() > 0) {
                    Number num = ConverterUtils.stringToNumber(annotation.isCurrency(), strValue[i]);
                    result[i] = Long.parseLong(num.toString());
                }
            }
            catch (ParseException e) {
                // 変換エラー
                String dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
                String resourceId = annotation.resourceId();
                String label = ResourcesHandler.getString(resourceId);
                addRequestTableErr(dataType, i, resourceId, label);
            }
        }

        return result;
    }

    /**
     * リクエストパラメータ取得 Object
     * @param ip インジェクトポイント
     * @return Object 取得値
     * @throws GnomesAppException
     */
    private Object getObjectRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        Object requestParamValue = null;

        try {
            requestParamValue = requestContext.getRequestParamValueWithWapper(requestParamName);
        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }
        return requestParamValue;
    }

    /**
     * 拡張リクエストパラメータ取得 Object
     * @param ip インジェクトポイント
     * @return Object 取得値
     * @throws GnomesAppException
     */
    private Object getObjectExtRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        Object requestParamValue = null;
        String requestParamName = getExtRequestParamName(ip);
        if (requestParamName.length() > 0) {
            try {
                requestParamValue = requestContext.getRequestParamValueWithWapper(requestParamName);
            }
            catch (Exception e) {
                //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
                GnomesAppException ex = new GnomesAppException(e);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
                Object[] errParam = {requestParamName};
                ex.setMessageParams(errParam);
                throw ex;
            }
        }
        return requestParamValue;
    }

    /**
     * リクエストパラメータ取得 Object配列
     * @param ip インジェクトポイント
     * @return Object 取得値
     * @throws GnomesAppException
     */
    private Object getObjectsRequestParamMap(InjectionPoint ip) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        Object requestParamValue = null;

        try {
            requestParamValue = requestContext.getRequestParamValuesWithWapper(requestParamName);
        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }
        return requestParamValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T getTypeReferenceJson(InjectionPoint ip) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        Object requestParamValue = null;

        T value = null;

        try {
            requestParamValue = requestContext.getRequestParamValueJson(requestParamName);

            if (requestParamValue == null) {
                return null;
            }

            TypeReference<?> valueTypeRef = new TypeReference<T>()
            {
            };

            // リクエストがT型の場合（フォワード、リダイレクト時）はそのまま
            if (requestParamValue.getClass() == valueTypeRef.getType().getClass()) {
                return (T) requestParamValue;
            }

            if (((String) (requestParamValue)).trim().length() == 0) {
                return null;
            }

            value = ConverterUtils.readJson((String) requestParamValue, valueTypeRef);
        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private <T> T getTypeReferenceJson(InjectionPoint ip, TypeReference<?> valueTypeRef) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        Object requestParamValue = null;

        T value = null;

        try {
            requestParamValue = requestContext.getRequestParamValueJson(requestParamName);

            if (requestParamValue == null) {
                return null;
            }

            // リクエストがT型の場合（フォワード、リダイレクト時）はそのまま
            if (requestParamValue.getClass() == valueTypeRef.getType().getClass()) {
                return (T) requestParamValue;
            }

            if (((String) (requestParamValue)).trim().length() == 0) {
                return null;
            }

            value = ConverterUtils.readJson((String) requestParamValue, valueTypeRef);
        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }

        return value;
    }

    /**
     * JSONデータ取得
     * @param ip インジェクトポイント
     * @param valueType Objectクラスタイプ
     * @return Objectクラス
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    private <T> T getObjectJson(InjectionPoint ip, Class<T> valueType) throws GnomesAppException
    {

        String requestParamName = getRequestParamName(ip);
        Object requestParamValue = null;

        T value = null;

        try {
            requestParamValue = requestContext.getRequestParamValueJson(requestParamName);

            if (requestParamValue == null) {
                return null;
            }

            // リクエストがT型の場合（フォワード、リダイレクト時）はそのまま
            if (requestParamValue.getClass() == valueType.getClass()) {
                return (T) requestParamValue;
            }

            value = ConverterUtils.readJson((String) requestParamValue, valueType);
        }
        catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {requestParamName};
            ex.setMessageParams(errParam);
            throw ex;
        }

        return value;
    }

    /**
     * リクエスト値の型変換エラー追加
     * @param dataType 変換タイプ
     * @param name フィールド名
     */
    private void addRequestErr(String dataType, String key, String label)
    {

        if (requestContext.getRequestParamMapErr().containsKey(key) == false) {

            String messageNo = GnomesMessagesConstants.MV01_0001;

            String[] params = {messageNo, label, dataType};

            requestContext.getRequestParamMapErr().put(key, params);
        }
    }

    /**
     * リクエスト値の型変換エラー追加(テーブル入力）
     * @param dataType 変換タイプ
     * @param index 行数
     * @param key キー
     * @param label ラベル名
     */
    private void addRequestTableErr(String dataType, int index, String key, String label)
    {

        String errKey = key + CommonConstants.REQUEST_ERR_KEY_SEPARATOR + String.valueOf(index + 1);

        if (requestContext.getRequestParamMapErr().containsKey(errKey) == false) {
            String strLine = MessagesHandler.getString(GnomesLogMessageConstants.MV01_0026, String.valueOf(index + 1));
            String[] params = {GnomesMessagesConstants.MV01_0001, label + strLine, dataType};
            requestContext.getRequestParamMapErr().put(errKey, params);
        }
    }

    /**
     * パラメータ名取得
     * @param ip インジェクトポイント
     * @return パラメータ名
     */
    private String getRequestParamName(InjectionPoint ip)
    {
        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        String requestParamName = annotation.value();
        if ("".equals(requestParamName)) {
            requestParamName = ip.getMember().getName();
        }
        return requestParamName;
    }

    /**
     * 拡張パラメータ名取得
     * @param ip インジェクトポイント
     * @return パラメータ名
     */
    private String getExtRequestParamName(InjectionPoint ip)
    {
        RequestParamMap annotation = ip.getAnnotated().getAnnotation(RequestParamMap.class);

        String requestParamName = annotation.extValue();
        return requestParamName;
    }

}
