package com.gnomes.external.logic.talend;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Validator;

import org.apache.commons.beanutils.BeanUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.ExternalIfIsDataItemId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * ビーンバリデーション 実行クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class BeanValidationJob extends BaseJobLogic
{

    /** バリデータ */
    @Inject
    protected Validator            validator;

    /** アノテーション定義要素：resourceId */
    private static final String    RESOURCE_ID    = "resourceId";

    /** アノテーション定義要素：messageParams */
    private static final String    MESSAGE_PARAMS = "messageParams";

    /** メッセージ定義 Dao */
    @Inject
    protected MstrMessageDefineDao mstrMessageDefineDao;

    /**
     * ビーンバリデーション 呼出
     * @throws ReflectiveOperationException
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws GnomesAppException, ReflectiveOperationException
    {

        Map<Integer, DataDefine> validateCheckList = new HashMap<>();
        List<DataDefine> externalIfDataDefineList = fileTransferBean.getDataDefine();
        // チェック項目の設定
        for (DataDefine dataDefine : externalIfDataDefineList) {
            // 外部I/F データ項目識別ID有効/無効 が無効ではない場合
            if (!ExternalIfIsDataItemId.INVALID.equals(ExternalIfIsDataItemId.getEnum(
                    dataDefine.getIsdata_item_id()))) {

                if (dataDefine.getData_check() == 0 || dataDefine.getLength_check() == 0) {
                    validateCheckList.put(dataDefine.getData_item_number(), dataDefine);
                }

            }

        }
        Map<Integer, DataDefine> sortMap = validateCheckList.entrySet().stream()
                .sorted(Map.Entry.<Integer, DataDefine>comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        fileTransferBean.setValidateCheckList(sortMap);

        int index = 1;
        StringBuilder allLineError = new StringBuilder();
        Map<Integer, String> errorLineInfo = new HashMap<>();

        // 送信データ行ごとにバリデートチェック
        for (SendRecvDataBean sendRecvDataBean : fileTransferBean.getSendRecvDataBeanList()) {
            //
            sendRecvDataBean.setCheckList(fileTransferBean.getValidateCheckList());
            String validateResult = this.validateFormBean(fileTransferBean.getValidateCheckList(), sendRecvDataBean,
                    index);
            // エラー情報が存在する場合
            if (!StringUtil.isNullOrEmpty(validateResult)) {
                if (allLineError.length() > 0) {
                    allLineError.append(System.lineSeparator());
                }
                allLineError.append(validateResult);
                errorLineInfo.put(index, validateResult);
            }
            index++;
        }

        fileTransferBean.setErrorLineInfo(errorLineInfo);

        if (allLineError.length() > 0) {
            String errorComment = MessagesHandler.getString(GnomesMessagesConstants.ME01_0106,
            		fileTransferBean.getSendRecvDataBeanList().get(0).getClass().getSimpleName(),
            		allLineError.toString());
            fileTransferBean.setErrorComment(errorComment);
            //ME01.0106：「データが不正です。 詳細はエラーメッセージを確認してください。\n{0}」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
            		GnomesMessagesConstants.ME01_0106,
            		fileTransferBean.getSendRecvDataBeanList().get(0).getClass().getSimpleName(),
                    allLineError.toString());
            throw ex;

        }

    }

    /**
     * ビーンバリデーション 呼出
     * @param sendRecvDataBeanList 送信データBeanリスト
     * @param dataDefineList 外部IFデータ項目定義情報リスト
     * @throws GnomesAppException
     * @throws ReflectiveOperationException
     */
    @ErrorHandling
    @TraceMonitor
    public void process(List<SendRecvDataBean> sendRecvDataBeanList, List<DataDefine> dataDefineList)
            throws GnomesAppException, ReflectiveOperationException
    {

        // 送受信データBeanList設定
        this.fileTransferBean.setSendRecvDataBeanList(sendRecvDataBeanList);
        // 外部I/Fデータ項目定義リスト設定
        this.fileTransferBean.setDataDefine(dataDefineList);
        // ビーンバリデーション 呼出
        this.process();

    }

    /**
     * ビーンバリデーション 実行
     *
     * @param checkList マスターから入手したフィールドの定義(see.mstr_external_if_data_define)
     * @param formBean  データが入っている1件分のデータ
     * @param lineNo    このデータは入手したテキストの何行目なのかを示す
     * @return
     * @throws ReflectiveOperationException
     */
    private String validateFormBean(Map<Integer, DataDefine> checkList, SendRecvDataBean formBean, Integer lineNo)
            throws ReflectiveOperationException
    {

        // チェック項目が空の場合、戻り値に空に設定し、処理終了
        if (Objects.isNull(checkList) || checkList.size() == 0) {
            return "";
        }

        Integer lineNumber = 0;
        String fieldName = "";
        StringBuilder mesDetail = new StringBuilder();

        //チェックリストを回して、formBeanの対象のメンバ変数値を検査する
        Iterator<Map.Entry<Integer, DataDefine>> itrDataDefine = formBean.getCheckList().entrySet().iterator();
        while (itrDataDefine.hasNext()) {
            Map.Entry<Integer, DataDefine> dataDefine = itrDataDefine.next();
            //formBeanの対象のメンバ変数値を取得
            String checkValue = getValueFromSendRecvDataBean(formBean, dataDefine.getValue().getData_item_id());

            //個別検証を実行する
            List<ValidataionErrorInfo> validataionErrorInfo = validateValueFromSendRecvDataBean(checkValue,
                    dataDefine.getValue(), dataDefine.getKey());

            //エラーが発生した場合の処理（件数1件以上）
            //エラーが返ってきた場合エラー情報を格納する
            for (ValidataionErrorInfo errInfo : validataionErrorInfo) {
                // ({0}行目、{1}番目）エラーメッセージ
                lineNumber = errInfo.getColumnNo();
                List<String> errDetails = errInfo.getMessageParams();

                mesDetail.append(MessagesHandler.getString(GnomesMessagesConstants.MV01_0027, lineNo.toString(),
                        lineNumber.toString(), this.getErrorMessage(errInfo.getMessagResourceNo(),
                                (String[]) errDetails.toArray(new String[0]))));
            }
        }

        return mesDetail.toString();
    }

    /**
     * 引数のcheckValueを受けてバリデーションを実施
     *      必須チェックか桁チェックのみ実施。将来はチェック処理を拡張することを想定
     *
     * @param checkValue    チェック対象の値
     * @param dataDefine    チェックの条件がある情報
     * @param columnNo      チェックするカラムの位置
     */
    private List<ValidataionErrorInfo> validateValueFromSendRecvDataBean(String checkValue, DataDefine dataDefine,
            Integer columnNo)
    {
        List<ValidataionErrorInfo> validataionErrorInfo = new ArrayList<>();
        //必須チェックをチェックする(0がチェックすることになっている)
        if (dataDefine.getData_check() == 0) {
            if (StringUtil.isNullOrEmpty(checkValue)) {
                /** {0}は必須入力です。 */
                ValidataionErrorInfo errInfo = new ValidataionErrorInfo();
                errInfo.setMessagResourceNo(GnomesMessagesConstants.MV01_0002);
                errInfo.setColumnNo(columnNo);
                List<String> errParam = new ArrayList<>();

                errParam.add(dataDefine.getData_item_name()); //{0}はアイテム名

                errInfo.setMessageParams(errParam);

                //メッセージを登録
                validataionErrorInfo.add(errInfo);
            }
        }


        //データ長をチェックする(0がチェックすることになっている)
        if (dataDefine.getLength_check() == 0) {
            //送受信でデータタイプがDate型の場合はデータ長はチェックしない
            if(dataDefine.getFormat_id() == CommonEnums.FormatId.Date.getValue()){
                return validataionErrorInfo;
            }
            //対象文字がnullの場合は何もしない
            if (!Objects.isNull(checkValue)) {
                int checkLength = dataDefine.getData_length();
                if (checkValue.length() > checkLength) {
                    /** {0}は{2}文字以下で入力してください。 */
                    ValidataionErrorInfo errInfo = new ValidataionErrorInfo();
                    errInfo.setMessagResourceNo(GnomesMessagesConstants.MV01_0032);
                    errInfo.setColumnNo(columnNo);
                    List<String> errParam = new ArrayList<>();
                    errParam.add(dataDefine.getData_item_name()); //{0}はアイテム名
                    errParam.add("");                           //{1}はスキップ
                    errParam.add(Integer.toString(checkLength));//{2}は検査する文字幅

                    errInfo.setMessageParams(errParam);

                    //メッセージを登録
                    validataionErrorInfo.add(errInfo);
                }
            }
        }
        return validataionErrorInfo;
    }

    /**
     * formBeanの値とメンバー変数名から変数の値を入手
     *
     * @param formBean      データが集まっている抜き出す元データ
     * @param data_item_id  取り出すメンバーの変数名
     *
     * @return  入手したオブジェクト
     * @throws ReflectiveOperationException
     */
    private String getValueFromSendRecvDataBean(SendRecvDataBean formBean, String data_item_id)
            throws ReflectiveOperationException
    {

        try {
        	Object o = BeanUtils.getProperty(formBean, data_item_id);
        	if(o != null){
        		return o.toString();
        	} else {
        		return null;
        	}
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw e;
        }
    }

    /**
    * エラーメッセージを取得
    * @param convertarError エラー情報
    * @return エラーメッセージ
    */
    private String getErrorMessage(String[] convertarError)
    {

        // メッセージNoの取出
        String messageNo = convertarError[0];
        // パラメータの取出
        Object[] params = null;

        if (convertarError.length > 1) {
            params = new Object[convertarError.length - 1];
            for (int i = 1; i < convertarError.length; i++) {
                params[i - 1] = convertarError[i];
            }
        }

        MstrMessageDefine mstrMsgDef = null;

        try {
            // メッセージ定義を取得
            mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(messageNo);

        }
        catch (GnomesAppException ea) {
            // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
        }

        if (!Objects.isNull(mstrMsgDef)) {
            messageNo = mstrMsgDef.getResource_id();
        }

        String message = MessagesHandler.getString(messageNo, req.getUserLocale(), params);

        return message;
    }

    /**
    * エラーメッセージを取得(最新版）
    * @param convertarError エラー情報
    * @return エラーメッセージ
    */
    private String getErrorMessage(String resourceId, String[] convertarError)
    {

        // メッセージNoの取出
        String messageNo = resourceId;

        MstrMessageDefine mstrMsgDef = null;

        try {
            // メッセージ定義を取得
            mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(messageNo);

        }
        catch (GnomesAppException ea) {
            // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
        }

        if (!Objects.isNull(mstrMsgDef)) {
            messageNo = mstrMsgDef.getResource_id();
        }

        String message = MessagesHandler.getString(messageNo, req.getUserLocale(), convertarError);

        return message;
    }

}
