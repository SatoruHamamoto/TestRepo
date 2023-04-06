package com.gnomes.common.resource.spi;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.InheritedBundleManager;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.StringUtils;
import com.gnomes.system.data.IMessageInfoBase;
import com.gnomes.system.dto.IMessageBaseDto;
import com.gnomes.uiservice.ContainerRequest;

/**
 * ここにクラス概要を入力してください。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class MessagesHandler {

    /**
     * デフォルト・コンストラクタ
     *
     */
    private MessagesHandler() {
    }

    /**
     * リソース取得
     * @param    リソースKey
     * @return   文字列
     */
    public static String getString(String key) {
        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[2],  Locale.getDefault());
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[1],  Locale.getDefault());
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0],  Locale.getDefault());
                try {
                    return bundle.getString(key);
                } catch (MissingResourceException e3) {
                    e3.printStackTrace();

                }
            }
        }
        return key;
    }

    /**
     * リソース取得
     * @param    リソースKey
     * @param    ロケール
     * @return   文字列
     */
    public static String getString(String key, Locale locale) {
        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[2], locale);
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[1], locale);
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0], locale);
                try {
                    return bundle.getString(key);
                } catch (MissingResourceException e3) {
                    //e3.printStackTrace();
                    // 指定のロケールで該当リソースKey の文字列が取得できない場合は、システムデフォルトロケールより取得する。
                    bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0],  Locale.getDefault());
                    try {
                        return bundle.getString(key);
                    } catch (MissingResourceException e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }
        return key;
    }

    /**
     * リソース取得
     * @param    リソースKey
     * @param    リソースの引数
     * @return   文字列
     */
    public static String getString(String key, Object ... arguments) {
        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[2],  Locale.getDefault());
        try {
            String  msgValue = bundle.getString(key);
            MessageFormat   messageFormat = new MessageFormat(msgValue);
            return messageFormat.format(arguments);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[1],  Locale.getDefault());
            try {
                String  msgValue = bundle.getString(key);
                MessageFormat   messageFormat = new MessageFormat(msgValue);
                return messageFormat.format(arguments);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0],  Locale.getDefault());
                try {
                    String  msgValue = bundle.getString(key);
                    MessageFormat   messageFormat = new MessageFormat(msgValue);
                    return messageFormat.format(arguments);
                } catch (MissingResourceException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return key;
    }

   /**
     * リソース取得
     * @param    リソースKey
     * @param    ロケール
     * @param    リソースの引数
     * @return   文字列
     */
    public static String getString(String key, Locale locale, Object ... arguments) {
        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[2], locale);
        try {
            String  msgValue = bundle.getString(key);
            MessageFormat   messageFormat = new MessageFormat(msgValue);
            return messageFormat.format(arguments);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[1], locale);
            try {
                String  msgValue = bundle.getString(key);
                MessageFormat   messageFormat = new MessageFormat(msgValue);
                return messageFormat.format(arguments);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0], locale);
                try {
                    String  msgValue = bundle.getString(key);
                    MessageFormat   messageFormat = new MessageFormat(msgValue);
                    return messageFormat.format(arguments);
                } catch (MissingResourceException e3) {
                    // 指定のロケールで該当リソースKey の文字列が取得できない場合は、システムデフォルトロケールより取得する。
                    bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0],  Locale.getDefault());
                    try {
                        String  msgValue = bundle.getString(key);
                        MessageFormat   messageFormat = new MessageFormat(msgValue);
                        return messageFormat.format(arguments);
                    } catch (MissingResourceException e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }
        return key;
    }

    /**
     * リソース取得(リソースの引数がリストの場合)
     * @param    リソースKey
     * @param    リソースの引数(リスト)
     * @return   文字列
     */
    public static String getStringParamList(String key, List<String> arguments) {
        // ロケールをシステムデフォルトロケールとしてリソース取得
        return getStringParamList(key, Locale.getDefault(), arguments);
    }

   /**
     * リソース取得(リソースの引数がリストの場合)
     * @param    リソースKey
     * @param    ロケール
     * @param    リソースの引数
     * @return   文字列
     */
    public static String getStringParamList(String key, Locale locale, List<String> arguments) {
        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[2], locale);
        try {
            String  msgValue = bundle.getString(key);
            return getMessageParamListFormat(msgValue, arguments);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[1], locale);
            try {
                String  msgValue = bundle.getString(key);
                return getMessageParamListFormat(msgValue, arguments);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0], locale);
                try {
                    String  msgValue = bundle.getString(key);
                    return getMessageParamListFormat(msgValue, arguments);
                } catch (MissingResourceException e3) {
                    // 指定のロケールで該当リソースKey の文字列が取得できない場合は、システムデフォルトロケールより取得する。
                    bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesMessagesList()[0],  Locale.getDefault());
                    try {
                        String  msgValue = bundle.getString(key);
                        return getMessageParamListFormat(msgValue, arguments);
                    } catch (MissingResourceException e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }
        return key;
    }


    /**
     * メッセージ設定
     * @param requestContext ContainerRequest
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public static void setMessageNo(ContainerRequest requestContext, String messageNo) {
        requestContext.addMessageInfo(messageNo);
    }

    /**
     * メッセージ設定
     * @param requestContext ContainerRequest
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public static void setMessageNo(ContainerRequest requestContext, String messageNo, Object ... params) {
        requestContext.addMessageInfo(messageNo, params);
    }

    /**
     * メッセージ設定
     * @param requestContex ContainerRequest
     * @param mesOwner 親メッセージ
     * @param mesChilds 子メッセージ
     */
    public static void  setMessage(ContainerRequest requestContex, MessageData mesOwner, MessageData[] mesChilds) {
        requestContex.addMessageInfo(mesOwner, mesChilds);
    }


    /**
     * 例外をメッセージに設定
     * @param requestConte ContainerRequest
     * @param e GnomesException
     */
    public static void setMessageGnomesException(ContainerRequest requestConte, GnomesException e) {

        MessageData mesOwner = null;
        MessageData[] mesChilds = null;

        if (e.getMessageNo() != null) {
            // メッセージ設定
            mesOwner = new MessageData(e.getMessageNo(), e.getMessageParams());
            mesOwner.setMessageCommand(e.getCommand());
            mesOwner.setMessageCancelOnClick(e.getMessageCancelOnClick());
            mesOwner.setMessageOkOnClick(e.getMessageOkOnClick());
        }
        if (e.getChildMessageDatas() != null) {
            // 子メッセージ設定
            mesChilds = (MessageData[])e.getChildMessageDatas().toArray(new MessageData[0]);
        }
        if (mesOwner != null) {
            setMessage(requestConte, mesOwner, mesChilds);
        }
    }

    /**
     * 例外のメッセージを取得
     * @param requestContext ContainerRequest
     * @param ex 例外
     * @return String
     */
    public static String getExceptionMessage(ContainerRequest requestContext, GnomesAppException ex) {
        return requestContext.getExceptionMessage(ex);
    }

    /**
     * 例外のメッセージを取得
     * @param requestContext ContainerRequest
     * @param ex 例外
     * @return String
     */
    public static String getExceptionMessage(ContainerRequest requestContext, GnomesException ex) {
        return requestContext.getExceptionMessage(ex);
    }

    /**
     * メッセージ情報一覧取得
     * @param lstDto メッセージDTOリスト
     * @param locale ロケール
     * @param clazz メッセージ情報のクラス
     * @return List<T>
     * @throws Exception
     */
    @ErrorHandling
    public static <T extends IMessageInfoBase, S extends IMessageBaseDto> List<T> getMessageInfoList(
            List<S> lstDto,
            Locale locale,
            Class<T> clazz) throws Exception
    {

        List<T> lstBean = new ArrayList<T>();

            // 日付フォーマット
            String datePattern = ResourcesHandler.getString(CommonConstants.RES_OCCURDATE_FORMAT);

            for (S data : lstDto) {
                T newMessageInfo = clazz.newInstance();

                // 発生日時
                newMessageInfo.setOccurDate(ConverterUtils.dateTimeToString(data.getOccur_date(), datePattern));

                // 種別
                CommonEnums.MessageCategory msgCategory = null;
                // メッセージ重要度
                CommonEnums.MessageLevel msgLevel = null;

                if (data.getCategory() != null) {
                    // 種別(名称)
                    msgCategory = CommonEnums.MessageCategory.getEnum(data.getCategory());
                    if (msgCategory != null) {
                        // リソースより取得
                        newMessageInfo.setCategoryName(ResourcesHandler.getString(msgCategory.toString(), locale));
                    }
                }


                if (data.getMessage_level() != null) {
                    // メッセージ重要度(名称)
                    msgLevel = CommonEnums.MessageLevel.getEnum(data.getMessage_level());
                    if (msgLevel != null) {
                        // リソースより取得
                        newMessageInfo.setMsgLevelName(ResourcesHandler.getString(msgLevel.toString(), locale));
                    }
                }

                // メッセージ作成
                String mes = createMessage(data);
                String[] mesLine = mes.split(StringUtils.LIEN_SEPARATOR_RESOURCE);
                String mesLineEscapeHtml = StringUtils.getStringEscapeHtml(mesLine[0]);

                // メッセージ
                newMessageInfo.setMessage(mesLineEscapeHtml);
                // newMessageInfo.setMessage(mesLine[0]);
                // メッセージ詳細
                StringBuilder mesDetail = new StringBuilder();
                int mesTitleLen = mesLine[0].length() + StringUtils.LIEN_SEPARATOR_RESOURCE.length();
                if (mes.length() > mesTitleLen) {
                    mesDetail.append(mes.substring(mesTitleLen, mes.length()));
                }
                String mesDetailEscapeHtml = StringUtils.getStringEscapeHtml(mesDetail.toString());

                newMessageInfo.setMessageDetail(mesDetailEscapeHtml);

                // メッセージアイコン名
                String messageIconResKey = getMessageIconResKey(msgCategory, msgLevel);
                if(messageIconResKey != null){
                    newMessageInfo.setIconName(
                            ResourcesHandler.getString(messageIconResKey, locale));
                }

                // その他　個別設定
                newMessageInfo.setInfoOther(data);

                lstBean.add(newMessageInfo);
            }

        return lstBean;
    }

    /**
     * メッセージアイコン名のリソースキーを取得
     * @param category メッセージ種別
     * @param level メッセージ重要度
     * @return String
     */
    public static final String getMessageIconResKey(
            CommonEnums.MessageCategory category,
            CommonEnums.MessageLevel level) {

        String key = null;

        if (category == null || level == null) {
            return null;
        }

        switch (category) {
            // 警告
            case MessageCategory_Alarm:
                // メッセージ重要度
                switch (level) {
                    // 軽
                    case MessageLevel_Light:
                        key = GnomesResourcesConstants.YY01_0025;
                        break;
                    // 中
                    case MessageLevel_Middle:
                        key = GnomesResourcesConstants.YY01_0024;
                        break;
                    // 重
                    case MessageLevel_Heavy:
                        key = GnomesResourcesConstants.YY01_0023;
                        break;
                }
                break;
            // 情報
            case MessageCategory_OperationGuide:
                key = GnomesResourcesConstants.YY01_0029;
                break;
            // 確認
            case MessageCategory_Confirm:
                key = GnomesResourcesConstants.YY01_0068;
                break;
            // エラー
            case MessageCategory_Error:
                key = GnomesResourcesConstants.YY01_0031;
                break;
            // デバッグ
            case MessageCategory_Debug:
            	key = GnomesResourcesConstants.YY01_0081;
            	break;
            default:
            	// 何もしない
            	break;
        }
        return key;
    }

    /**
     * メッセージ作成.
     * <pre>
     * メッセージ補足情報１から１０を文字列結合し返却する。
     * </pre>
     * @param data メッセージ情報DTO
     * @return
     */
    private static String createMessage(IMessageBaseDto data) {

        StringBuilder message = new StringBuilder();
        message.append(defaultString(data.getMessage_param1()));
        message.append(defaultString(data.getMessage_param2()));
        message.append(defaultString(data.getMessage_param3()));
        message.append(defaultString(data.getMessage_param4()));
        message.append(defaultString(data.getMessage_param5()));
        message.append(defaultString(data.getMessage_param6()));
        message.append(defaultString(data.getMessage_param7()));
        message.append(defaultString(data.getMessage_param8()));
        message.append(defaultString(data.getMessage_param9()));
        message.append(defaultString(data.getMessage_param10()));
        return message.toString();

    }

    /**
     * 変換対象の文字列がNULLの場合、空文字に変換.
     * @param value 変換対象の文字列
     * @return 変換後文字列
     */
    private static String defaultString(String value) {

        if (value == null) {
            return "";
        }
        return value;
    }

    /**
     * メッセージ取得.
     * @param resourceid リソースID
     * @param msgParamList パラメータ情報リスト
     * @param locale ロケール
     * @return メッセージ
     */
    private static String getMessageParamListFormat(String msgValue, List<String> msgParamList) {

        List<String> mesLineParams = new ArrayList<>();
        List<String> mesDetailparams = new ArrayList<>();

        for(int i = 0; i < msgParamList.size(); i++) {

            if (msgValue.indexOf("{" + i + "}") != -1) {
                // メッセージのパラメータ
                mesLineParams.add(msgParamList.get(i));

            } else if(!StringUtil.isNullOrEmpty(msgParamList.get(i))) {
                // スタックトレースなどの付属情報
                mesDetailparams.add(msgParamList.get(i));
            }
        }

        // メッセージのフォーマット
        MessageFormat   messageFormat = new MessageFormat(msgValue);
        String mes =  messageFormat.format(mesLineParams.toArray());

        // <br>を除去
        mes = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(mes).replaceAll("");

        // スタックトレースなどの付属情報の追加
        if (!mesDetailparams.isEmpty()) {

            mes = mes + StringUtils.LIEN_SEPARATOR_RESOURCE;

            for(int i = 0; i < mesDetailparams.size(); i++) {
                mes = mes + mesDetailparams.get(i);
            }

        }

        return mes;

    }


}
