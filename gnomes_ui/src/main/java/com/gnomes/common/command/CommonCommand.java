package com.gnomes.common.command;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import com.gnomes.common.constants.CommonEnums.DoingClassMethodDiv;
import com.gnomes.common.data.CommandData;
import com.gnomes.common.data.CommandScreenData;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.uiservice.ContainerRequest;

/**
 * 共通コマンド 共通処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/19 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class CommonCommand {

    @Inject
    protected GnomesSystemBean gnomesSystemBean;

    @Inject
    LogHelper loghelper;

    @Inject
    Logger logger;

    @Inject
    protected
    GnomesExceptionFactory exceptionFactory;

    /** メッセージ オブジェクト NULL */
    private static final String MES_OBJ_NULL = "NULL";

    /** メッセージ クラス名 切り文字 */
    private static final String MES_CLASS_SEPARATOR = ".";

    /** CDIプロキシー判定文字 */
    private static final String PROXY_CHECK_STRING = "$Proxy$";

    /**
     * 共通コマンド情報取得
     * @param commandId コマンドID
     * @return 共通コマンド情報
     * @throws GnomesAppException
     */
    public CommandData getCommandData(String commandId) throws GnomesAppException {

        CommandData commandData = null;

        try {
        	List<CommandData> lstCommandData = gnomesSystemBean.getCommandDatas()
                .getCommandDataList().stream()
                .filter(item -> commandId.equals(item.getCommandId()))
                .collect(Collectors.toList());

            if (lstCommandData.size() > 0) {
                commandData = lstCommandData.get(0);
            }
        }
        catch(NullPointerException ex){
            // エラー
            //ME01.0178：「コマンドIDが見つかりません。commandId=%s Bean定義シートを確認してください」
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0178, commandId);
        	throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0178, commandId);
        }


        return commandData;
    }

    /**
     * 共通画面情報取得
     * @param screenId 画面ID
     * @return 共通画面情報
     * @throws GnomesAppException
     */
    public CommandScreenData getCommandScreenData(String screenId) throws GnomesAppException {

        CommandScreenData commandScreenData = null;

        try {
        	List<CommandScreenData> lstCommandScreenData = gnomesSystemBean
                .getCommandDatas().getScreenDataList().stream()
                .filter(item -> screenId.equals(item.getScreenId()))
                .collect(Collectors.toList());

            if (lstCommandScreenData.size() > 0) {
                commandScreenData = lstCommandScreenData
                        .get(0);
            }
        }
        catch(NullPointerException ex){
            // エラー
            //ME01.0179：「画面IDが見つかりません。screenId={0} Bean定義シートと定数シートを確認してください」
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0179, screenId);
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0179, screenId);
        }
        return commandScreenData;
    }

    /**
     * Beanマッピング
     *
     * @param dst 設定先
     * @param src 取得元
     * @param map マッピング情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    public void mappingBean(Object dst, Object src, Map<String, String> map)
            throws GnomesAppException {

        for (Map.Entry<String, String> m : map.entrySet()) {

            try {
                PropertyDescriptor srcProperties = new PropertyDescriptor(
                        m.getValue(), src.getClass());
                PropertyDescriptor dstProperties = new PropertyDescriptor(
                        m.getKey(), dst.getClass());

                Method getter = srcProperties.getReadMethod(); //getter取得
                Method setter = dstProperties.getWriteMethod(); //setter取得

                Object value = getter.invoke(src, (Object[]) null); //プロパティ値を取得
                setter.invoke(dst, value); //プロパティ値をセット

            } catch (Exception e) {
                // ME01.0152：「Beanマッピングでエラーが発生しました。詳細についてはログを確認してください。設定先：{0} 、取得元：{1}」
                GnomesAppException gae = new GnomesAppException(e);
                gae.setMessageNo(GnomesMessagesConstants.ME01_0152);

                String dstClassName = MES_OBJ_NULL;
                if (dst != null) {
                    String key = MES_OBJ_NULL;
                    if (m.getKey() != null) {
                        key = m.getKey();
                    }
                    dstClassName = dst.getClass().getName()
                            .concat(MES_CLASS_SEPARATOR).concat(key);
                }
                String srcClassName = MES_OBJ_NULL;
                if (src != null) {
                    String key = MES_OBJ_NULL;
                    if (m.getValue() != null) {
                        key = m.getValue();
                    }
                    srcClassName = src.getClass().getName()
                            .concat(MES_CLASS_SEPARATOR).concat(key);
                }

                Object[] errParam = {
                        dstClassName,
                        srcClassName
                };
                gae.setMessageParams(errParam);

                throw gae;
            }
        }
    }

    /**
     * クラスメソッド実行
     * @param doClassMethod クラスメソッド名
     * @param doingClassMethodDiv 実行クラスメソッド区分
     * @param containerReq リクエストコンテナのインスタンス
     * @throws GnomesAppException
     */
    @TraceMonitor
    public void doClassMethod(String doClassMethod,
            DoingClassMethodDiv doingClassMethodDiv, ContainerRequest containerReq)
                    throws GnomesAppException {
        if (doClassMethod == null) {
            return;
        }

        String[] str2Ary = doClassMethod.split("\\.");
        String methodName = str2Ary[str2Ary.length - 1];
        String className = doClassMethod.substring(0, doClassMethod.length()
                - (methodName.length() + 1));

        try {
            Object obj = getCDIInstance(className);

            switch (doingClassMethodDiv){
            case BUSINESS_LOGIC:
                // BLクラスの場合実行するクラス名をリクエストコンテナに設定
                String classNameBuf = obj.getClass().getSimpleName();

                int index = classNameBuf.indexOf(PROXY_CHECK_STRING);

                // CDIの$Proxy$_$$_WeldClientProxy が付与されていたら省く
                if ( index > 0 ){
                    containerReq.setBusinessClassName(classNameBuf.substring(0, index));
                }
                // 付与されていなかったらそのまま入れる
                else {
                    containerReq.setBusinessClassName(classNameBuf);
                }

                break;
            default:
            }

            Method getMethod = obj.getClass().getMethod(methodName);
            getMethod.invoke(obj);

        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {

            if (e instanceof InvocationTargetException) {
                Throwable cause = e.getCause();
                if (cause instanceof GnomesException) {
                    throw (GnomesException)cause;
                }
            }

            // ME01.0239：「予期せぬエラーがおきました。全てのブラウザーを閉じて最初から操作を行って下さい。」
            GnomesAppException gae = new GnomesAppException(e);
            gae.setMessageNo(GnomesMessagesConstants.ME01_0239);

            gae.addChildMessageData(new MessageData(GnomesMessagesConstants.ME01_0238, new Object[]{className}));
            gae.addChildMessageData(new MessageData(GnomesMessagesConstants.ME01_0238, new Object[]{methodName}));

            throw gae;
        }
    }

    /**
     * クラスメソッド実行
     * @param doClassMethod クラスメソッド名
     * @throws GnomesAppException
     */
    @TraceMonitor
    public void doClassMethod(String doClassMethod)
                    throws GnomesAppException {
        this.doClassMethod(doClassMethod, DoingClassMethodDiv.OTHER, null);
    }

    /**
     * ビーンプロパティ取得
     * @param bean 取得元ビーン
     * @param propertyName 取得プロパティ名
     * @return 取得値
     * @throws GnomesAppException
     */
    @TraceMonitor
    public Object getBeanProperty(Object bean, String propertyName)
            throws GnomesAppException {

        Object value = null;

        PropertyDescriptor srcProperties;
        try {
            srcProperties = new PropertyDescriptor(
                    propertyName, bean.getClass());
            Method getter = srcProperties.getReadMethod(); //getter取得

            value = getter.invoke(bean, (Object[]) null); //プロパティ値を取得

        } catch (IntrospectionException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {

            // ME01.0154：「ビーンプロパティ取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 、取得プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(e);
            gae.setMessageNo(GnomesMessagesConstants.ME01_0154);

            Object[] errParam = {
                    bean.getClass().getName(),
                    propertyName
            };
            gae.setMessageParams(errParam);

            throw gae;
        }

        return value;
    }

    /**
     * 対象クラスのインスタンスをCDIより取得.
     * <pre>
     * 取得できなかった場合、nullを返却。
     * </pre>
     * @param className クラス名
     * @return CDIより取得した対象クラスのインスタンス
     * @throws GnomesAppException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    @TraceMonitor
    public <T> T getCDIInstance(String className) throws GnomesAppException {
        Instance<?> instance;

        try {
            CDI<Object> current = CDI.current();
            Class<?> clazz;
            clazz = Class.forName(className);
            instance = current.select(clazz);

        } catch (Exception e) {
            // ME01.0155：「CDIインスタンス取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 」
            GnomesAppException gae = new GnomesAppException(e);
            gae.setMessageNo(GnomesMessagesConstants.ME01_0155);

            Object[] errParam = {
                    className
            };
            gae.setMessageParams(errParam);

            throw gae;
        }

        if (!instance.iterator().hasNext()) {
            // ME01.0156：「指定されたクラス：{0}のCDIインスタンスは見つかりません。」
            throw new GnomesAppException(null,
                    GnomesMessagesConstants.ME01_0156, className);
        }

        return (T) instance.get();

    }

}
