package com.gnomes.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.CertificateType;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.data.CidMap;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.validator.GnomesValidationGroup;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IFormBean;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.IScreenPrivilegeBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.data.PreJudgeForCodesParameter;
import com.gnomes.system.entity.MstrMessageDefine;
import com.gnomes.system.logic.BLSecurity;
import com.gnomes.system.logic.JudgePersonsLicenseOption;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

/**
 * コマンド 基底クラス
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
public abstract class BaseCommand
{

    // アノテーション定義要素
    protected static final String    RESOURCE_ID    = "resourceId";
    protected static final String    MESSAGE_PARAMS = "messageParams";

    protected static final String    operationStr   = "OPE.";

    protected static final String    TRUE           = "true";

    //ページトークンチェックフラグ
    protected boolean                tokenCheckFlg  = false;

    //ページトークン更新フラグ
    protected boolean                tokenUpdateFlg = true;

    @Inject
    protected transient Logger       logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper              logHelper;

    @Inject
    protected ContainerRequest       requestContext;

    @Inject
    protected ContainerResponse      responseContext;

    @Inject
    protected GnomesSessionBean      gnomesSessionBean;

    @Inject
    protected GnomesSystemBean       gnomesSystemBean;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    @Inject
    protected Validator              validator;

    @Inject
    protected MstrMessageDefineDao   mstrMessageDefineDao;

    @Inject
    GnomesExceptionFactory           gnomesExceptionFactory;

    @Inject
    RequestParamProducer             reqParamProducer;

    /**
     * 権限認証の判定コンテンツ処理オプション
     */
    @Inject
    JudgePersonsLicenseOption        judgePersonsLicenseOption;

    /**
     * セキュリティ ビーン
     */
    @Inject
    SystemFormBean                   systemFormBean;

    /**
     * バッチ系Bean(画面だとisEjbBatchがfalse）
     */
    @Inject
    protected GnomesEjbBean          ejbBean;

    /**
     * セキュリティ機能
     */
    @Inject
    BLSecurity                       blSecurity;

    @Inject
    protected MstrSystemDefineDao    mstrSystemDefineDao;

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory     emf;

    /** エンティティマネージャーファクトリ（保管領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE)
    private EntityManagerFactory     emfStorage;

    // リクエスト時のエラー情報
    // [key], [messageNo, messageParameter ...]
    protected Map<String, String[]>  requestErr     = new LinkedHashMap<String, String[]>();

    /**
    * デフォルトコンストラクタ
    */
    public BaseCommand()
    {
    }

    public Map<String, String[]> getRequestErr()
    {
        return requestErr;
    }

    /**
    * リクエスト時のパラメータ変換エラーを追加
    * ※業務バリデーション実行時にもこのメソッドよりエラーをPutする
    * @param key キー
    * @param messageNo メッセージNo
    * @param param メッセージ置き換えパラメータ
    */
    /*
    public void putRequestErr(String key, String messageNo, String[] param) {

        String[] p;

        if (param == null) {
            p = new String[1];
        } else {
            p = new String[param.length + 1];
            for (int i = 0; i < param.length; i++) {
                p[i + 1] = param[i];
            }
        }
        p[0] = messageNo;

        requestErr.put(key, p);
    }
    */
    /**
    * ページトークンチェックフラグをTrueに設定する。
    *
    */
    public void setCheckTokenFlgTrue()
    {
        tokenCheckFlg = true;
    }

    /**
    * ページトークン更新フラグをFalseに設定する。
    *
    */
    public void setTokenUpdateFlgFalse()
    {
        tokenUpdateFlg = false;
    }

    /**
    * ページトークンチェックフラグを取得
    * @return tokenCheckFlg
    */
    public boolean getTokenCheckFlg()
    {
        return this.tokenCheckFlg;
    }

    /**
    * ページトークン更新フラグを取得
    * @return tokenUpdateFlg
    */
    public boolean getTokenUpdateFlg()
    {
        return this.tokenUpdateFlg;
    }

    /**
    * 認証権限チェック
    * @throws Exception
    */

    public boolean judgePersonsLicenseCheck(IScreenPrivilegeBean formBean, String screenId) throws GnomesException
    {

        boolean result = true;

        EntityTransaction tran = null;
        // エンティティマネージャの取得
        EntityManager em = this.getEntityManager(RegionType.getEnum(gnomesSessionBean.getRegionType()));

        try {
            // 拠点コード
            String siteCode = null;
            // 指図工程コード
            String orderProcessCode = null;
            // 作業工程コード
            String workProcessCode = null;
            // 作業場所コード
            String workCellCode = null;
            //-----------------------------------------------------------------
            // バッチ系（SFやloginなど)は従来通りformBeanで検査する
            //-----------------------------------------------------------------
            if (this.ejbBean.isEjbBatch()) {

                // 拠点コードを取得
                List<String> siteCodeList = formBean.getSiteCode();
                if ((!Objects.isNull(siteCodeList)) && (siteCodeList.size() > 0)) {
                    siteCode = siteCodeList.get(0);
                }

                // 指図工程コードを取得
                List<String> orderProcessCodeList = formBean.getOrderProcessCode();
                if ((!Objects.isNull(orderProcessCodeList)) && (orderProcessCodeList.size() > 0)) {
                    orderProcessCode = orderProcessCodeList.get(0);
                }

                // 作業工程コードを取得
                List<String> workProcessCodeList = formBean.getWorkProcessCode();
                if ((!Objects.isNull(workProcessCodeList)) && (workProcessCodeList.size() > 0)) {
                    workProcessCode = workProcessCodeList.get(0);
                }
            }
            //-----------------------------------------------------------------
            // 非バッチ（画面系）はSessionBeanを使って検査する
            //-----------------------------------------------------------------
            else {
                // 拠点コードを取得
                siteCode = this.gnomesSessionBean.getSiteCode();
                // 指図工程コードを取得
                orderProcessCode = this.gnomesSessionBean.getOrderProcessCode();
                // 作業工程コードを取得
                workProcessCode = this.gnomesSessionBean.getWorkProcessCode();
                // 作業場所コードを取得
                workCellCode = this.gnomesSessionBean.getWorkCellCode();

                //-------------------------------------------------------------
                // コンテンツ側の条件で判定方法が変わることを考慮し
                // 差し替え技術でコンテンツのチェック処理を呼び出せるようにする
                // 差し替えなければ何も起きない
                //-------------------------------------------------------------
                PreJudgeForCodesParameter preJudgeForCodesParameter = new PreJudgeForCodesParameter(siteCode,
                        orderProcessCode, workProcessCode, workCellCode);

                //コンテンツ側を呼び出し（差し替えなければ何もしない)
                judgePersonsLicenseOption.preJudgeForCodes(preJudgeForCodesParameter);

                //渡したオブジェクトから各種コードを設定する変更されていなければ変わらない
                siteCode = preJudgeForCodesParameter.getSiteCode();
                orderProcessCode = preJudgeForCodesParameter.getOrderProcessCode();
                workProcessCode = preJudgeForCodesParameter.getWorkProcessCode();
                workCellCode = preJudgeForCodesParameter.getWorkCellCode();

            }

            // ぞれぞれ値が取れなかったら "-" が割りつく
            if (StringUtil.isNullOrEmpty(siteCode)) {
                //拠点コードを取得できない場合、マスターコードを設定
                siteCode = CommonConstants.SITE_CODE_ALL;
            }
            if (StringUtil.isNullOrEmpty(orderProcessCode)) {
                //指図工程コードを取得できない場合、マスターコードを設定
                orderProcessCode = CommonConstants.ORDER_PROCESS_CODE_ALL;
            }
            if (StringUtil.isNullOrEmpty(workProcessCode)) {
                //作業工程コードを取得できない場合、マスターコードを設定
                workProcessCode = CommonConstants.WORK_PROCESS_CODE_ALL;
            }
            if (StringUtil.isNullOrEmpty(workCellCode)) {
                //作業場所コードを取得できない場合、マスターコードを設定
                workCellCode = CommonConstants.WORK_CELL_CODE_ALL;
            }

            //-----------------------------------------------------------------
            // 拠点、指図工程、作業工程を使ってダブルチェックの実施
            //-----------------------------------------------------------------
            if (systemFormBean.getIsDoubleCheck() != null && !systemFormBean.getIsDoubleCheck().isEmpty()) {
            	if(em == null) {
            		throw new IllegalStateException("EntityManager is null because RegionType is unknown, Failed to certification of double check");
            	}

                // トランザクション開始
                tran = em.getTransaction();
                tran.begin();
                em.setFlushMode(FlushModeType.AUTO);

                // ボタン名の設定
                for (PartsPrivilegeResultInfo dataPartsPrivilegeResultInfo : formBean.getPartsPrivilegeResultInfo()) {
                    if (dataPartsPrivilegeResultInfo.getButtonId().equals(systemFormBean.getButtonId())) {
                        systemFormBean.setButtonName(dataPartsPrivilegeResultInfo.getButtonName());
                    }
                }

                // 認証区分
                CertificateType certType;
                // ダブル認証か否か
                if (systemFormBean.getIsDoubleCheck().equals(TRUE)) {
                    certType = CertificateType.DOUBLE_PRIVILEGE_CHECK;
                }
                else {
                    certType = CertificateType.PRIVILEGE_CHECK;
                }

                result = blSecurity.judgePersonsLicenseCertCheck(systemFormBean.getLoginUserId(),
                        systemFormBean.getLoginUserPassword(), systemFormBean.getCertUserId(),
                        systemFormBean.getCertUserPassword(), screenId, systemFormBean.getButtonId(), siteCode,
                        orderProcessCode, workProcessCode, workCellCode, certType,
                        formBean.getPartsPrivilegeResultInfo(), em);

                tran.commit();

            }

            //ダブルチェック出ない場合、パーツ権限結果情報を取得して
            //操作権限チェックを実施する
            else if (formBean.getPartsPrivilegeResultInfo() != null && formBean.getPartsPrivilegeResultInfo().size() > 0) {

                result = blSecurity.judgePersonsLicenseCheck(requestContext.getUserId(), screenId,
                        systemFormBean.getButtonId(), formBean.getPartsPrivilegeResultInfo(), siteCode,
                        orderProcessCode, workProcessCode, workCellCode);

            }

        }
        catch (GnomesException e) {
            if (!Objects.isNull(tran)) {
                // ロールバック
                tran.rollback();
            }
            throw e;
        }
        catch (Exception e) {
            if (!Objects.isNull(tran)) {
                // ロールバック
                tran.rollback();
            }
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = gnomesExceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            throw ex;
        }
        finally {
            if (!Objects.isNull(em)) {
                em.close();
            }
            em = null;
        }
        return result;
    }

    /**
     * エンティティマネージャ取得（参照領域指定）
     * @param str 判定文字
     * @return 判定結果
     */
    private EntityManager getEntityManager(RegionType regionType)
    {

        if (RegionType.NORMAL.equals(regionType)) {
            return this.emf.createEntityManager();
        }
        else if (RegionType.STORAGE.equals(regionType)) {
            return this.emfStorage.createEntityManager();
        }

        return null;

    }

    /**
    * リクエストパラメータのエラーをエラー情報に設定
    * @param checkList エラーチェック対象リスト
    * @param convertarErrorList リクエストエラー
    * @return true:エラーあり,false:エラーなし
    */
    protected boolean setRequestParamError(Map<Integer, String> checkList, Map<String, String[]> convertarErrorList)
    {

        boolean isError = false;

        if (Objects.isNull(checkList) || checkList.size() == 0)
            return false;

        // コンバータによるエラーチェック
        if (!Objects.isNull(convertarErrorList) && convertarErrorList.size() > 0) {

            try {
                Iterator<Map.Entry<Integer, String>> itr = checkList.entrySet().iterator();
                // 検査対象リスト分比較を繰り返し
                while (itr.hasNext()) {
                    Map.Entry<Integer, String> check = itr.next();
                    for (Entry<String, String[]> e : convertarErrorList.entrySet()) {
                        // メッセージ出力対象チェック
                        String errKey = null;
                        // テーブルインディクス有無
                        int sepIndex = e.getKey().indexOf(CommonConstants.REQUEST_ERR_KEY_SEPARATOR);
                        if (sepIndex > 0) {
                            errKey = e.getKey().substring(0, sepIndex);
                        }
                        else {
                            errKey = e.getKey();
                        }

                        if (check.getValue().equals(errKey)) {
                            requestErr.put(e.getKey(), e.getValue());
                            isError = true;
                        }
                    }
                }
            }
            catch (Exception e) {
                logHelper.severe(logger, null, null, "An error occurred in validating the screen input item.");
                logHelper.severe(logger, null, null,
                        "- Please check the definition of the validation item of FormBean definition.");
                // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
                GnomesException ex = gnomesExceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
                throw ex;
            }
        }
        return isError;
    }

    /**
     * formBeanのバリデータによるチェック
     * @param checkList エラーチェック対象リスト
     * @param formBean チェックするフォームビーン
     * @return true:エラーあり,false:エラーなし
     */
    protected boolean validateFormBean(Map<Integer, String> checkList, IFormBean formBean)
    {

        boolean isError = false;

        if (Objects.isNull(checkList) || checkList.size() == 0)
            return false;

        try {
            // fromBeanのインポートファイル以外のチェック
            Set<ConstraintViolation<Object>> validaterErrorList = validator.validate(formBean);

            // ドメイン参照時のバリデーションチェックエラー情報の取得
            Map<String, Map<String, String[]>> validationDomainError = requestContext.getValidationDomainError();

            // バリデータによるチェック
            if ((!Objects.isNull(validaterErrorList) && validaterErrorList.size() != 0) || ((!Objects.isNull(
                    validationDomainError) && validationDomainError.size() != 0))) {

                Iterator<Map.Entry<Integer, String>> itr = formBean.getCheckList().entrySet().iterator();
                // 検査対象リスト分比較を繰り返し
                while (itr.hasNext()) {
                    Map.Entry<Integer, String> check = itr.next();

                    // 対象がリスト以外の場合、リソースIDを対象とする
                    // 対象がリストの場合は、フィールド名を対象とする
                    String listFieldName = null;
                    int indexList = check.getValue().indexOf(
                            CommonConstants.COMMAND_INPUT_CHECK_LIST_FIELDNAME_SEPARATOR);
                    if (indexList == 0) {
                        listFieldName = check.getValue().substring(
                                CommonConstants.COMMAND_INPUT_CHECK_LIST_FIELDNAME_SEPARATOR.length(),
                                check.getValue().length());
                    }
                    
                    // 複数エラー時の出力メッセージ統一のため並び替え
                    ArrayList<ConstraintViolation<Object>> sortedValidaterErrorList = new ArrayList<ConstraintViolation<Object>>(validaterErrorList);
                    Collections.sort(sortedValidaterErrorList, new Comparator<ConstraintViolation<Object>>() {
                        public int compare(ConstraintViolation<Object> obj1, ConstraintViolation<Object> obj2) {
                        	int obj1Order = GnomesValidationGroup.GnomesValidationOrder.getOrder(obj1.getMessage().replace(".", "_"));
                        	int obj2Order = GnomesValidationGroup.GnomesValidationOrder.getOrder(obj2.getMessage().replace(".", "_")); 
                        	
                        	// エラー表示はList内の末尾の内容で表示するため、優先順の逆順でソート                        	
                        	if(obj1Order > obj2Order) {
                                return -1;
                            } else if(obj1Order < obj2Order) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    });                    

                    Iterator<ConstraintViolation<Object>> iterator = sortedValidaterErrorList.iterator();

                    while (iterator.hasNext()) {
                        ConstraintViolation<Object> violation = iterator.next();
                        Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();

                        // リスト行数
                        String strTableIndex = null;

                        String resourceId = "";
                        // アノテーションのリソースIDから項目名取得
                        if (attributes.containsKey(RESOURCE_ID)) {
                            resourceId = attributes.get(RESOURCE_ID).toString();
                        }

                        // エラー出力判定フラグ
                        boolean isOutPutErr = false;
                        if (listFieldName == null) {
                            // リスト項目でない場合は、
                            // リスト項目のエラーでないかつ
                            // リソースIDでメッセージ出力対象チェック
                            int count = 0;
                            Iterator<Node> iteratorNode = violation.getPropertyPath().iterator();
                            while (iteratorNode.hasNext()) {
                                iteratorNode.next();
                                count++;
                            }
                            if (count == 1) {
                                isOutPutErr = check.getValue().equals(resourceId);
                            }
                            else {
                                // リストのエラーはチェックなし
                                isOutPutErr = false;
                            }
                        }
                        else {
                            // リスト項目の場合は、Listのフィールド名単位で対象チェック
                            Node node = findFirstIndexNode(listFieldName, violation.getPropertyPath());

                            if (node != null) {
                                isOutPutErr = true;
                                strTableIndex = String.valueOf(node.getIndex() + 1);
                            }
                        }

                        // メッセージ出力対象チェック
                        if (isOutPutErr) {
                            List<String> objs = new ArrayList<String>();

                            objs.add(violation.getMessage());

                            if (attributes.containsKey(MESSAGE_PARAMS)) {
                                // 出力パラメータ対象取得
                                String[] messageParams = (String[]) attributes.get(MESSAGE_PARAMS);

                                for (String messageParam : messageParams) {
                                    if (attributes.containsKey(messageParam)) {
                                        if (messageParam.equals(RESOURCE_ID)) {

                                            // 項目名
                                            String strLine = "";
                                            String strRes = ResourcesHandler.getString(attributes.get(
                                                    messageParam).toString(), requestContext.getUserLocale());

                                            if (strTableIndex != null) {
                                                strLine = MessagesHandler.getString(GnomesLogMessageConstants.MV01_0026,
                                                        requestContext.getUserLocale(), strTableIndex);
                                            }
                                            objs.add(strRes + strLine);
                                        }
                                        else {
                                            objs.add(attributes.get(messageParam).toString());
                                        }
                                    }
                                }
                            }

                            String requestErrKey = null;
                            if (strTableIndex == null) {
                                requestErrKey = resourceId;
                            }
                            else {
                                requestErrKey = resourceId + CommonConstants.REQUEST_ERR_KEY_SEPARATOR + strTableIndex;
                            }

                            requestErr.put(requestErrKey, //resourceId,
                                    (String[]) objs.toArray(new String[0]));
                            isError = true;
                        }
                    }

                    // ドメイン参照時のバリデーションチェックエラー情報の参照
                    for (Entry<String, Map<String, String[]>> err : validationDomainError.entrySet()) {
                        if (check.getValue().equals(err.getKey())) {
                            requestErr.putAll(err.getValue());
                        }
                    }
                }
            }

        }
        catch (ValidationException e) {

            if (e.getCause() instanceof GnomesException) {

                GnomesException ex = (GnomesException) e.getCause();

                List<Object> param = new ArrayList<>();
                param.add(ex.getMessageNo());
                param.addAll(Arrays.asList(ex.getMessageParams()));
                requestErr.put(ex.getMessageNo(), param.toArray(new String[param.size()]));

                isError = true;
            }

        }
        return isError;
    }

    /**
     * 最初のindexがnullでないNodeを取得
     * @param path
     * @return IndexがnullでないNode
     */
    private Node findFirstIndexNode(String listFieldName, Path path)
    {

        // node 1階層目 listのフィールド名
        //      2階層目 list内のフィールド名とlist内でのindex
        boolean isFoundFieldName = false;
        for (Iterator<Node> it = path.iterator(); it.hasNext();) {

            Node node = it.next();
            if (!isFoundFieldName) {
                if (node.getName().equals(listFieldName)) {
                    isFoundFieldName = true;
                }
                else {
                    return null;
                }
            }
            else {
                if (node.getIndex() != null && node.getName() != null && node.getName().length() > 0) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * リクエストエラー情報のソート
     *  リスト分をソートする
     *  "XXXX,tableIndex=?"の"?"はリストの行数を表す
     *  リストは"XXXX"、"?"でソートする（出力順はリソースID、行数の順序)
     */
    protected void sortRequestErr()
    {
        Map<String, String[]> sortedRequestErr = new LinkedHashMap<String, String[]>();

        List<Integer> checkTableIndexList = new ArrayList<>();
        List<String> checkTableResouceIdList = new ArrayList<>();

        // ",tableIndex="がついてないものは、そのまま追加
        // ",tableIndex="がついているものから"=?"の?(行数)を収集
        for (Map.Entry<String, String[]> entry : requestErr.entrySet()) {
            int sepIndex = entry.getKey().indexOf(CommonConstants.REQUEST_ERR_KEY_SEPARATOR);

            if (sepIndex == -1) {
                // リスト内のエラーでない場合
                // 追加
                sortedRequestErr.put(entry.getKey(), entry.getValue());
            }
            else {
                // リスト内のエラーの場合
                // リスト内のIndexを取得
                Integer tebleIndex = Integer.valueOf(entry.getKey().substring(
                        sepIndex + CommonConstants.REQUEST_ERR_KEY_SEPARATOR.length(), entry.getKey().length()));
                if (!checkTableIndexList.contains(tebleIndex)) {
                    checkTableIndexList.add(tebleIndex);
                }

                // リソースIDを取得
                String resouceId = entry.getKey().substring(0, sepIndex);
                if (!checkTableResouceIdList.contains(resouceId)) {
                    checkTableResouceIdList.add(resouceId);
                }
            }
        }

        // 昇順でソートする
        Collections.sort(checkTableIndexList);
        Collections.sort(checkTableResouceIdList);

        // ",tableIndex="がついているtableIndexList順で追加
        for (Integer checkTableIndex : checkTableIndexList) {
            for (String checkResouceId : checkTableResouceIdList) {

                for (Map.Entry<String, String[]> entry : requestErr.entrySet()) {
                    int sepIndex = entry.getKey().indexOf(CommonConstants.REQUEST_ERR_KEY_SEPARATOR);
                    if (sepIndex > 0) {

                        Integer tebleIndex = Integer.valueOf(entry.getKey().substring(
                                sepIndex + CommonConstants.REQUEST_ERR_KEY_SEPARATOR.length(),
                                entry.getKey().length()));

                        String resouceId = entry.getKey().substring(0, sepIndex);

                        if (checkTableIndex.equals(tebleIndex) && checkResouceId.equals(resouceId)) {
                            sortedRequestErr.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }

        requestErr.clear();
        requestErr.putAll(sortedRequestErr);
    }

    /**
    * エラーメッセージを取得
    * @param e エラー情報
    * @return エラーメッセージ
    */
    protected String getErrorMessage(Entry<String, String[]> e)
    {

        String[] convertarError = e.getValue();

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

        String message = MessagesHandler.getString(messageNo, requestContext.getUserLocale(), params);

        return message;
    }

    /**
     * メッセージデータ取得
     * @param e エラー情報
     * @return メッセージデータ
     */
    protected MessageData getErrorMessageData(Entry<String, String[]> e)
    {

        String[] convertarError = e.getValue();
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

        MessageData messageData = new MessageData(messageNo, params);
        return messageData;
    }

    /**
     * cid begin
     * @param formBean formmBean
     * @param functionBean functionBean
     * @throws GnomesAppException
     */
    protected void beginFunctionBean(BaseFormBean formBean, BaseFunctionBean functionBean) throws GnomesAppException
    {

        if (functionBean.begin()) {
            // 成功時
            this.addCidMap(functionBean.getCid(), functionBean.getClass().getName(), formBean.getWindowId());
        }

    }

    /**
     * cid end
     * @param formBean formBean
     * @param functionBean functionBean
     */
    protected void endFunctionBean(BaseFormBean formBean, BaseFunctionBean functionBean)
    {
        String endCid = functionBean.end();
        if (endCid != null) {
            this.deleteCidMapFromCid(endCid);
        }
    }

    /**
     * cidマップ登録
     * @param cid cid
     * @param functionBeanClassName functionBeanクラス名
     * @param windowId windowId
     * @throws GnomesAppException
     */
    protected void addCidMap(String cid, String functionBeanClassName, String windowId) throws GnomesAppException
    {

        synchronized (this.gnomesSessionBean) {
            List<CidMap> cidMapList = gnomesSessionBean.getCidMapList();

            for (CidMap item : cidMapList) {

                if (item.getCid().equals(cid)) {
                    // ME01.0148:「cidマップ登録でcid重複エラーが発生しました。cid：{0}、functionBeanClassName：{1}」
                    throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0148, cid,
                            functionBeanClassName);
                }
            }
            CidMap addItem = new CidMap();
            addItem.setCid(cid);
            addItem.setFunctionBeanClassName(functionBeanClassName);
            addItem.setWindowId(windowId);
            cidMapList.add(addItem);

            gnomesSessionBean.setCidMapList(cidMapList);
        }
    }

    /**
     * cidマップ削除
     * @param windowId 削除するwindowId
     */
    protected void deleteCidMap(String windowId)
    {

        synchronized (this.gnomesSessionBean) {

            List<CidMap> removes = new ArrayList<>();
            List<CidMap> cidMapList = gnomesSessionBean.getCidMapList();

            for (CidMap item : cidMapList) {

                if (item.getWindowId().equals(windowId)) {
                    removes.add(item);
                }
            }

            cidMapList.removeAll(removes);
            gnomesSessionBean.setCidMapList(cidMapList);
        }
    }

    /**
     * cidマップ削除
     * @param cid 削除するcid
     */
    protected void deleteCidMapFromCid(String cid)
    {

        synchronized (this.gnomesSessionBean) {
            List<CidMap> removes = new ArrayList<>();
            List<CidMap> cidMapList = gnomesSessionBean.getCidMapList();

            for (CidMap item : cidMapList) {

                if (item.getCid().equals(cid)) {
                    removes.add(item);
                }
            }

            cidMapList.removeAll(removes);
            gnomesSessionBean.setCidMapList(cidMapList);
        }
    }

    /**
     * windowIdの作成
     * @return windowId
     */
    protected String makeWindowId()
    {
        String newWindowId = UUID.randomUUID().toString();
        List<String> windowIdList = gnomesSessionBean.getWindowIdList();
        windowIdList.add(newWindowId);
        gnomesSessionBean.setWindowIdList(windowIdList);

        return newWindowId;
    }

    /**
     * windowIdの削除
     * @param windowId windowId
     */
    protected void deleteWindowId(String windowId)
    {
        List<String> removes = new ArrayList<>();
        List<String> windowIdList = gnomesSessionBean.getWindowIdList();

        for (String item : windowIdList) {
            if (item.equals(windowId)) {
                removes.add(item);
            }
        }

        windowIdList.removeAll(removes);
        gnomesSessionBean.setWindowIdList(windowIdList);

    }

}
