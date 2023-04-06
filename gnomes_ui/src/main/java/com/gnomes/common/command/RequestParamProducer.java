package com.gnomes.common.command;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.gnomes.common.command.RequestParam.DataType;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.SanitizeHttpServletRequestWrapper;
import com.gnomes.uiservice.ServletContainer;

/**
 * リクエストパラメーター プロデューサー。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/01 YJP/K.Gotanda             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class RequestParamProducer {

    //ロガー
    @Inject
    transient Logger logger;

    @Inject
    ContainerRequest requestContext;

    // リクエスト時のパラメータ変換エラー情報
    private Map<String,String[]> requestErr = new HashMap<String,String[]>();

    /**
     * リクエスト時のパラメータ変換エラー情報
     * @return  requestErr Map<String, String[]>
     */
    public Map<String, String[]> getRequestErr() {
        return requestErr;
    }


    /**
     * HTTPリクエストパラメータ取得
     *
     * @param ip インジェクトポイント
     * @return String 取得値
     */
    @Produces
    @RequestParam
    public String getRequestParam(InjectionPoint ip) throws GnomesAppException {
        RequestParam annotation = ip.getAnnotated()
                .getAnnotation(RequestParam.class);

        String name = annotation.value();
        if ("".equals(name)) {
            name = ip.getMember().getName();
        }

        DataType dataType = annotation.dataType();

        String value = null;
    	try
    	{
	        if (requestContext.getServiceRequest() != null) {
				value = requestContext.getServiceRequestParam(name);
	        } else {
		         // リクエストパラメータ取得
		        if (ServletContainer.USE_ATTRIBUTE) {
		            // Forwardの時は、Forward時にセットされたforwardParameters優先
		            // なければ、リクエストから取得
		            if (requestContext.isForwardAttribute()) {
		                Object obj = requestContext.getAttributeParameter(name);
		                if (obj != null) {
		                    value = (String) obj;
		                }
		            }
		            // リダイレクト
		            else if (requestContext.isRedirect()) {
		                Object obj = requestContext.getRedirectParameter(name);
		                if (obj != null) {
		                    value = (String) obj;
		                }
		            }
		            if (value == null) {

		                if (dataType == DataType.JSON) {
		                    // JSONはサニタイズ処理なし
		                    if ( requestContext.getWrapperRequest() instanceof SanitizeHttpServletRequestWrapper) {
		                        SanitizeHttpServletRequestWrapper wrapper =
		                                (SanitizeHttpServletRequestWrapper)  requestContext.getWrapperRequest();

		                        value = wrapper.getRequest().getParameter(name);
		                    } else {
		                        value = requestContext.getWrapperRequest().getParameter(name);
		                    }
		                } else {
		                    value = requestContext.getWrapperRequest().getParameter(name);
		                }
		            }
		        } else {
		            if (requestContext.isRedirect()) {
		                Object obj = requestContext.getRedirectParameter(name);
		                if (obj != null) {
		                    value = (String) obj;
		                }
		            }
		            if (value == null) {

		                if (dataType == DataType.JSON) {
		                    // JSONはサニタイズ処理なし
		                    if ( requestContext.getWrapperRequest() instanceof SanitizeHttpServletRequestWrapper) {
		                        SanitizeHttpServletRequestWrapper wrapper =
		                                (SanitizeHttpServletRequestWrapper)  requestContext.getWrapperRequest();

		                        value = wrapper.getRequest().getParameter(name);
		                    } else {
		                        value = requestContext.getWrapperRequest().getParameter(name);
		                    }
		                } else {
		                    value = requestContext.getWrapperRequest().getParameter(name);
		                }
		            }
		        }
	        }
	        if (value != null && (dataType != DataType.TEXT && dataType != DataType.JSON)) {
	            // datatypeとformatで型変換エラーチェック
	            try {

	                if (dataType == DataType.NUMBER || dataType == DataType.CURRENCY) {
	                    // 通貨、数値の型チェック
	                    boolean isCurrency = false;
	                    if (dataType == DataType.CURRENCY) {
	                        isCurrency = true;
	                    }
	                    ConverterUtils.stringToNumber(isCurrency, value);
	                } else {
	                    // 日付型のチェック
	                    String formatId = annotation.formatId();
	                    String format = ResourcesHandler.getString(formatId, requestContext.getUserLocale());
	                    ConverterUtils.stringToDateFormat(value, format);
	                }
	            } catch (ParseException e) {
	                // 変換エラー
	                String resourceId = annotation.resourceId();
	                String label = ResourcesHandler.getString(resourceId);

	                addRequestErr(dataType, resourceId, label);
	            }
	        }
    	} catch (Exception e) {
            //   メッセージ： "パラメータ取得時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 パラメータ名： {0}")
            GnomesAppException ex =new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0158);
            Object[] errParam = {
            		name
            };
            ex.setMessageParams(errParam);
            throw ex;
    	}

        return value;
    }

    /**
     * HTTPリクエストパラメータ取得
     *
     * @param ip インジェクトポイント
     * @return String[] 取得値配列
     */
    @Produces
    @RequestParam
    public String[] getRequestParams(InjectionPoint ip) {
        RequestParam annotation = ip.getAnnotated()
                .getAnnotation(RequestParam.class);
        String name = annotation.value();
        if ("".equals(name)) {
            name = ip.getMember().getName();
        }

        String[] value = null;

        if (ServletContainer.USE_ATTRIBUTE) {
            // Forwardの時は、Forward時にセットされたforwardParameters優先
            // なければ、リクエストから取得
            if (requestContext.isForwardAttribute()) {
                Object obj = requestContext.getAttributeParameter(name);
                if (obj != null) {
                    value = (String[]) obj;
                }
            }
            if (value == null ) {
                value = requestContext.getWrapperRequest().getParameterValues(name);
            }
        } else {
            if (requestContext.isRedirect()) {
                Object obj = requestContext.getRedirectParameter(name);
                if (obj != null) {
                    value = (String[]) obj;
                }
            }
            if (value == null) {
                value = requestContext.getWrapperRequest().getParameterValues(name);
            }
        }
        return value;
    }

    /**
     * リクエスト値の型変換エラー追加
     * @param dataType 変換タイプ
     * @param name フィールド名
     */
    private void addRequestErr(DataType dataType, String key, String label) {
        if (requestErr.containsKey(key) == false) {

            String messageNo = GnomesMessagesConstants.MV01_0001;

            String[] params = {messageNo, label, getStringDateTyle(dataType)};

            requestErr.put(key, params);
        }
    }

    /**
     * データタイプの文言取得
     * @param dataType 取得データタイプ
     * @return 取得した文言
     */
    private String getStringDateTyle(DataType dataType) {
        String result = null;
        switch (dataType) {
            case NUMBER:
                result = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
                break;

            case CURRENCY:
                result = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0010);
                break;

            case DATE:
                result = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
                break;

            default:
                throw new GnomesException(GnomesMessagesConstants.ME01_0004);
        }

        return result;
    }

}
