package com.gnomes.common.command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.ISearchInfoFormBean;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.search.data.SearchSetting.DispType;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.dao.MstrMessageDefineDao;

/**
 * 画面コマンド 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/12 YJP/K.Gotand              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public abstract class ScreenBaseCommand extends BaseCommand implements IScreenCommand {


    @Inject
    protected
    SearchInfoController searchInfoController;

    @Inject
    MstrMessageDefineDao mstrMessageDefineDao;

     /**
     * デフォルトコンストラクタ
     */
    public ScreenBaseCommand() {
    }


    public void setupCommand() {

    }

    /**
     * 共通メイン処理（トランザクションなし）
     */
    public void mainExecuteNoTransactional() throws Exception {
        // 継承元でコーディング
    }

    /**
     * トランザクション管理有無
     * @return
     */
    public boolean isTransactional() {
        return true;
    }

    /**
     * コマンドID、画面ID、画面名を CotainerRequest に設定する
     * @param コマンドID
     * @param FromBean
     * @param CotainerRequest
     */
    public void setScreenInfo() {

        IScreenFormBean formBean = getFormBean();

        if (formBean != null) {

            String orgScreenId = formBean.getOrgScreenId();
            // formBeanに記載がない場合もあるので、ここでリクエストに必ず設定
            requestContext.setOrgScreenId(orgScreenId);

            if (StringUtil.isNullOrEmpty(orgScreenId)) {
                this.logHelper.fine(this.logger, null, null,
                        "ScreenId: " + formBean.getScreenId() + "ScreenName: " + formBean.getScreenName());
            } else {
                this.logHelper.fine(this.logger, null, null,
                        "orgScreenId: " + orgScreenId + " ScreenId: " + formBean.getScreenId() + "ScreenName: " + formBean.getScreenName());
            }
            //操作内容リソースキーの設定
            requestContext.setOperationResourceKey(operationStr + requestContext.getCommandId());

        }
        else {
            this.logHelper.fine(this.logger, null, null, "ScreenId: NULL "  + "ScreenName: NULL");
        }
    }

    /**
     * メッセージ比較 検査対象リストと一致するエラーメッセージを返す
     * @param checkList 検査対象リスト
     * @param convertarErrorList コンバータエラーリスト
     * @param validaterErrorList バリデータエラーリスト
     * @param outputMessageNo
     * @param outputParamsNo
     */
    public void compareMessages(Map<Integer, String> checkList
                                , Map<String, String[]> convertarErrorList
                                , Set<ConstraintViolation<Object>> validaterErrorList) {

        if(Objects.isNull(checkList) || checkList.size()==0) return;

        // コンバータによるエラーチェック
        if(!Objects.isNull(convertarErrorList) && convertarErrorList.size() > 0 ){
            Iterator<Map.Entry<Integer, String>> itr = checkList.entrySet().iterator();
            // 検査対象リスト分比較を繰り返し
            while(itr.hasNext()) {
                Map.Entry<Integer, String> check = itr.next();
                for(Entry<String, String[]> e : convertarErrorList.entrySet()) {
                    // メッセージ出力対象チェック
                    if(check.getValue().equals(e.getKey())){
                        requestErr.put(e.getKey(), e.getValue());
                    }
                }
            }
            if(requestErr.entrySet().size() > 0){
                return;
            }
        }

        // バリデータによるチェック
        if (!Objects.isNull(validaterErrorList) && validaterErrorList.size() != 0) {

            Iterator<Map.Entry<Integer, String>> itr = checkList.entrySet().iterator();
            // 検査対象リスト分比較を繰り返し
            while(itr.hasNext()) {
                Map.Entry<Integer, String> check = itr.next();

                //List<String> messages = new ArrayList<String>();
                Iterator<ConstraintViolation<Object>> iterator = validaterErrorList.iterator();
                while (iterator.hasNext()) {
                    ConstraintViolation<Object> violation = iterator.next();
                    Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();

                    String resourceId = "";
                    // アノテーションのリソースIDから項目名取得
                    if(attributes.containsKey(RESOURCE_ID)){
                        resourceId = attributes.get(RESOURCE_ID).toString();
                    }

                    // メッセージ出力対象チェック
                    if(check.getValue().equals(resourceId)){
                        List<String> objs = new ArrayList<String>();

                        objs.add(violation.getMessage());
                        if(attributes.containsKey(MESSAGE_PARAMS)){
                            // 出力パラメータ対象取得
                            String[] messageParams = (String[]) attributes.get(MESSAGE_PARAMS);

                            for(String messageParam : messageParams){
                                if(attributes.containsKey(messageParam)){
                                    if(messageParam.equals(RESOURCE_ID)){
                                        objs.add(ResourcesHandler.getString(attributes.get(messageParam).toString()));
                                    }else{
                                        objs.add(attributes.get(messageParam).toString());
                                    }
                                }
                            }
                        }
                        requestErr.put(resourceId, (String[])objs.toArray(new String[0]));
                    }
                }
            }
        }

        return;
    }
    /**
     * Validate処理実行
     * @throws Exception
     */
    public boolean commonValidate(IScreenFormBean formBean){

        Map<String, String[]> requestParamErr = new HashMap<String, String[]>();

        requestParamErr.putAll(reqParamProducer.getRequestErr());
        requestParamErr.putAll(requestContext.getRequestParamMapErr());

        // パラメータの変換チェックでエラーがない場合
        if (setRequestParamError(formBean.getCheckList(), requestParamErr) == false) {
            // formBeanのバリデータによるチェック
            validateFormBean(formBean.getCheckList(), formBean);
        }

        if (requestErr.entrySet().size() > 0) {
            // requestErrをソート
            sortRequestErr();
            return true;
        }
        return false;
    }

    /**
     * Validate処理結果確認
     * @throws Exception
     */
    public boolean validateResultCheck(IScreenFormBean formBean){

        // commonValidate,validateのエラー処理
        if (requestErr.entrySet().size() > 0) {
/*
            // メッセージ詳細
            StringBuilder mesDetail = new StringBuilder();
            for(Entry<String, String[]> e : requestErr.entrySet()) {

                if (mesDetail.length() > 0) {
                    mesDetail.append(System.lineSeparator());
                }

                mesDetail.append(this.getErrorMessage(e));
            }
            //操作エラーです。
            responseContext.setMessageInfo(GnomesMessagesConstants.YY01_0013, mesDetail.toString());
*/
            List<MessageData> lstMessageData = new ArrayList<>();

            for (Entry<String, String[]> e : requestErr.entrySet()) {
                // 子追加
                MessageData addMessageData = this.getErrorMessageData(e);
                lstMessageData.add(addMessageData);
            }

            //操作エラーです。
            MessageData mesOwner = new MessageData(
                    GnomesMessagesConstants.YY01_0013, null);

            responseContext.setMessage(mesOwner,
                    (MessageData[]) lstMessageData.toArray(new MessageData[0]));
            return false;
        }

        return true;

    }



    /**
     * 検索情報の初期設定
     * @param tableTagName テーブルタグ辞書名
     * @param formBean フォームビーン
     * @throws Exception
     */
    protected void initSearchInfo(DispType dispType, String tableTagName, ISearchInfoFormBean formBean) throws Exception {

        try {
            IScreenFormBean fb = (IScreenFormBean)formBean;

            // 検索条件クリア
            formBean.setJsonSearchInfo(null);

            // localstrageのユーザ検索条件設定情報
            Map<String, String> userSearchSetting = null;
            if (fb.getJsonSaveSearchInfos() != null && fb.getJsonSaveSearchInfos().length() > 0) {
                // convert JSON string to Map
                userSearchSetting = ConverterUtils.readJson(fb.getJsonSaveSearchInfos(), new TypeReference<Map<String, String>>(){});
            }
            // 遷移先画面の検索情報を取得
            if (userSearchSetting != null) {
                Object[] params = {
                    requestContext.getUserId(),
                    fb.getScreenId()
                };
                MessageFormat   messageFormat = new MessageFormat(SearchInfoController.FMT_LOCAL_STRAGE_KEY);
                String key =  messageFormat.format(params);
                formBean.setJsonSearchInfo((String) userSearchSetting.get(key));
            }

            // formBeanに検索マスター情報の作成
            MstSearchInfo mst = searchInfoController.getMstSearchInfo(
                    tableTagName,
                    ((IScreenFormBean)formBean).getUserLocale());
            formBean.setJsonSearchMasterInfo(ConverterUtils.getJson(mst));

            // 検索情報を渡されたか判定
            if (formBean.getJsonSearchInfo() == null || formBean.getJsonSearchInfo().length() <= 0) {
                // formBeanの検索情報が未設定の場合は、初期設定で作成
                formBean.setJsonSearchInfo(
                        ConverterUtils.getJson(mst.getDefaultSearchSetting()));
            }

            // 表示タイプ設定
            formBean.setJsonSearchInfo(
                    setTableDispType(fb, dispType, formBean.getJsonSearchInfo()));
        }
        catch(Exception e)
        {
            //   メッセージ： 検索情報の初期設定時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\n（テーブルタグ名:｛0｝）
            GnomesAppException ex =new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0076);
            String param = (tableTagName == null ? "" : tableTagName);

            Object[] errParam = {
                    param
            };
            ex.setMessageParams(errParam);
            throw ex;

        }

    }

    /**
     * 表示タイプ設定
     * @param fb フォームビーン
     * @param dispType 表示タイプ
     * @param jsonSearchInfo 検索条件
     * @return 検索条件
     * @throws Exception 例外
     */
    protected String setTableDispType(IScreenFormBean fb, DispType dispType, String jsonSearchInfo) throws Exception {

        SearchSetting searchSetting = ConverterUtils.readJson(jsonSearchInfo, SearchSetting.class);

        searchSetting.setDispType(dispType);

        // 最大表示件数
        // システム定数テーブルより取得
        int maxDispCount =
            mstrSystemDefineDao.getMstrSystemDefine(
                    SystemDefConstants.MAX_LIST_DISPLAY_COUNT,
                    fb.getScreenId())
                .getNumeric1().intValue();

        searchSetting.setMaxDispCount(maxDispCount);

        // ページングの場合
        if (dispType == DispType.DispType_Paging) {

            // ページは1
            searchSetting.setNowPage(1);

            // 1ページ表示件数
            // システム定数テーブルより取得
            int onePageDispCount =
                mstrSystemDefineDao.getMstrSystemDefine(
                        SystemDefConstants.ONE_PAGE_LIST_DISPLAY_COUNT,
                        fb.getScreenId())
                    .getNumeric1().intValue();

            searchSetting.setOnePageDispCount(onePageDispCount);
        }

        String str = ConverterUtils.getJson(searchSetting);
        return str;
    }
}
