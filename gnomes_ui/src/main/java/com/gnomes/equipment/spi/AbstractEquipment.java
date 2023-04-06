package com.gnomes.equipment.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.logic.RsClient;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.equipment.dao.MstrEquipmentDao;
import com.gnomes.equipment.dao.MstrEquipmentIfDao;
import com.gnomes.equipment.dao.MstrEquipmentIfParameterDao;
import com.gnomes.equipment.dao.MstrEquipmentParameterDao;
import com.gnomes.equipment.data.EquipmentIfParamInfo;
import com.gnomes.equipment.data.EquipmentIfReadData;
import com.gnomes.equipment.entity.MstrEquipment;
import com.gnomes.equipment.entity.MstrEquipmentIf;
import com.gnomes.equipment.entity.MstrEquipmentIfParameter;
import com.gnomes.equipment.entity.MstrEquipmentParameter;
import com.gnomes.equipment.logic.EqupmentAccessSpecialLogic;
import com.gnomes.uiservice.ContainerRequest;

import biz.grandsight.looponex.LoopOnExException;
import biz.grandsight.looponex.user.api.ITagDataManager;
import biz.grandsight.looponex.user.api.TagDataManager;
import biz.grandsight.looponex.user.api.model.TagData;
import biz.grandsight.looponex.user.api.model.TagValue;

/**
 * 設備IF機能共通処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class AbstractEquipment
{

    /** ロガー */
    @Inject
    protected transient Logger            logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper                   logHelper;

    /** 設備マスタDao. */
    @Inject
    protected MstrEquipmentDao            mstrEquipmentDao;

    /** 設備パラメータマスタDao. */
    @Inject
    protected MstrEquipmentParameterDao   mstrEquipmentParameterDao;

    /** X003:設備I/FマスタDao. */
    @Inject
    protected MstrEquipmentIfDao          mstrEquipmentIfDao;

    /** 設備I/FパラメータマスタDao. */
    @Inject
    protected MstrEquipmentIfParameterDao mstrEquipmentIfParameterDao;

    /** GnomesExceptionファクトリ. */
    @Inject
    protected GnomesExceptionFactory      gnomesExceptionFactory;

    /** JAX-RS サーバアクセスクラス. */
    @Inject
    protected RsClient                    rsClient;

    /** ContainerRequest. */
    @Inject
    protected ContainerRequest            req;

    /** 設備I/Fアクセスデータ変換等特殊ロジック */
    @Inject
    protected EqupmentAccessSpecialLogic  equipmentSpecalLogic;

    //    /** リモート接続先パス(仮). */
    //    private static final String REMOTE_SERVICE_PATH = "http://localhost/UI/";

    /** 定数：半角スペース. */
    private static final String           HALF_SPACE         = " ";

    /** 定数：NaN */
    private static final String           LoopOnEx_NaN_VALUE = "NaN";

    private static final String           className          = "AbstractEquipment";

    /**
     * 設備I/Fパラメータ情報取得.
     * <pre>
     * 設備I/Fパラメータ情報の取得を行う。
     * </pre>
     * @param equipmentId 設備ID
     * @param parameterItemIdArray 設備パラメータ項目ID配列
     * @return 設備I/Fパラメータ情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public EquipmentIfParamInfo getInterfaceParameter(String equipmentId, String[] parameterItemIdArray)
            throws GnomesAppException
    {

        // 設備IDに該当する設備情報を取得
        MstrEquipment equipment = this.getEquipment(equipmentId);

        // 設備情報の設備キーに該当する設備I/F情報を取得（1対１の関係）
        MstrEquipmentIf equipmentIf = this.getEquipmentIf(equipment.getEquipment_key());

        // 引数の設備パラメータ項目ID配列に該当する設備パラメータ情報リスト取得
        // 引数のparameterItemIdArrayで指定されたparameterItemIdにマッチするものを取得
        List<MstrEquipmentParameter> equipmentParameterList = this.getEquipmentParameterList(
                equipment.getEquipment_key(), parameterItemIdArray);

        // 設備情報に該当する設備I/Fパラメータ情報リストを設備I/F情報の設備IFキーに該当し、かつ
        // parameterItemIdにマッチするものを
        List<MstrEquipmentIfParameter> equipmentIfParameterList = this.getEquipmentIfParameter(
                equipmentIf.getEquipment_if_key(), equipmentParameterList);

        // 設備I/Fパラメータ情報の編集
        return this.editEquipmentIfParamInfo(equipmentIf.getEquipment_if_sub_type(), parameterItemIdArray,
                equipmentParameterList, equipmentIfParameterList);

    }

    /**
     * 設備情報読込み.
     * <pre>
     * 設備情報の読込みを行う。
     * </pre>
     * @param equipmentId 設備ID
     * @param parameterItemIdArray 設備パラメータ項目ID配列
     * @return 取得データリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<TagData> getSubSystemTagData(String equipmentId, String[] parameterItemIdArray)
            throws GnomesAppException
    {

        final String methodName = "getSubSystemTagData";
        /*
        // 設備I/Fパラメータ情報取得
        EquipmentIfParamInfo paramInfo = this.getInterfaceParameter(equipmentId, parameterItemIdArray);

        LoopOnExReadSubsystemTagParam readTagParam = new LoopOnExReadSubsystemTagParam();
        readTagParam.setSubSystemId(paramInfo.getSubSystemId());
        readTagParam.setItemNameList(paramInfo.getItemNameList());

        // 接続先サービスURL
        String servicePath = this.getServicePath("readSubsystemTagData");

        List<TagData> tagDataList = new ArrayList<>();

        try {
            // LoopOnExのサブシステムタグデータ取得
            tagDataList = this.rsClient.post(servicePath, Entity.json(readTagParam), List.class);

        } catch (GnomesAppException e) {
            throw e;
        } catch (GnomesException e) {
            throw e;
        } catch (Exception e) {

        }
        */

        // 設備I/Fパラメータ情報取得
        EquipmentIfParamInfo paramInfo = this.getInterfaceParameter(equipmentId, parameterItemIdArray);

        List<TagData> tagDataList = new ArrayList<TagData>();

        try {
            ITagDataManager tagDataManager = new TagDataManager();

            // サブシステムタグデータ取得
            tagDataList = tagDataManager.readSubsystemTagData(paramInfo.getSubSystemId(), paramInfo.getItemNameList());


            // データチェック　１つでもN/Aがあった場合はエラーにする
            tagDataListValidation(tagDataList, paramInfo, equipmentId);

            //特殊変換処理区分を元にデータコンバート
            tagDataListSpecialConvert(tagDataList, paramInfo, equipmentId);


        }
        catch (LoopOnExException e) {

            this.logHelper.severe(this.logger, className, methodName, e.toString());

            StringBuilder sb = new StringBuilder();
            sb.append("** Read Tag Data Error SubsystemId = " + paramInfo.getSubSystemId());
            sb.append(" TagItem = ");
            for (String itemName : paramInfo.getItemNameList()) {
                sb.append("[" + itemName + "]");
            }
            this.logHelper.severe(this.logger, className, methodName, sb.toString());

            // LoopOnEx処理でエラーが発生しました。 GRANSIGHT-EX LoopOnEx処理のログを確認してください。（設備ID：{0}）(ME01.0176）
            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0176,
                    equipmentId);

        }

        return tagDataList;
    }

    /**
     * PLCタグの内容を検証する
     *
     * @param tagDataList LoopOnExから取得したタグデータのリスト
     * @throws GnomesAppException
     */
    private void tagDataListValidation(List<TagData> tagDataList, EquipmentIfParamInfo paramInfo, String equipmentId)
            throws GnomesAppException
    {
        final String methodName = "tagDataListValidation";
        List<String> errorTagItem = new ArrayList<>();

        for (TagData data : tagDataList) {
            if (data.getTagValue().equals(LoopOnEx_NaN_VALUE)) {
                //NaNが１つでもあったらエラーにする
                errorTagItem.add(data.getTagItemName());
            }
        }
        //1件でもエラーになったらスローする
        if (!errorTagItem.isEmpty()) {
            this.logHelper.severe(this.logger, className, methodName,
                    "readSubsystemTagData ReadError ItegValue is NaN");

            StringBuilder sb = new StringBuilder();
            sb.append("Bad Value TagItem = \n");
            for (String tagItemName : errorTagItem) {
                sb.append(" [" + tagItemName + "] = [" + LoopOnEx_NaN_VALUE + "]\n");
            }
            this.logHelper.severe(this.logger, className, methodName, sb.toString());

            //PLCからのデータが一部取得できませんでした。アイテム名定義および設備を確認してください。（設備ID：{0}）\n{1}
            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0235,
                    equipmentId, sb.toString());
        }
    }

    /**
     * PLCタグの内容を特殊変換する
     *
     * @param tagDataList
     * @param paramInfo
     * @param equipmentId
     * @throws GnomesAppException
     */
    private void tagDataListSpecialConvert(List<TagData> tagDataList, EquipmentIfParamInfo paramInfo,
            String equipmentId) throws GnomesAppException
    {
        List<Integer> specialConvertDivList = paramInfo.getSpecialConvertDivList();
        List<String> itemNameList = paramInfo.getItemNameList();

        //タグデータの大きなループを配列インデックス形式で回す
        //タグリストの要素数が一致していることが前提（チェックはする）
        for (int i = 0; i < tagDataList.size(); i++) {
            //一致していることを前提とする
            TagData tagData = tagDataList.get(i);
            if (tagData.getTagItemName().equals(itemNameList.get(i))) {

                //特殊変換処理区分を見て0以外だったら特殊変換に進む
                if (!(specialConvertDivList.get(i).equals(0))) {
                    tagData.setTagValue(
                            equipmentSpecalLogic.itemSpecialConvert(
                                    equipmentId,
                                    specialConvertDivList.get(i),
                                    tagData.getTagItemName(),
                                    tagData.getTagValue()));
                }
            }
        }

    }

    /**
     * 設備情報読込み.
     * <pre>
     * 設備情報の読込みを行う。
     * </pre>
     * @param equipmentId 設備ID
     * @param parameterItemIdArray 設備パラメータ項目ID配列
     * @return 取得データリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<EquipmentIfReadData> readSubSystemTagData(String equipmentId, String[] parameterItemIdArray)
            throws GnomesAppException
    {

        List<TagData> tagDataList = this.getSubSystemTagData(equipmentId, parameterItemIdArray);

        List<EquipmentIfReadData> readDataList = new ArrayList<EquipmentIfReadData>();

        // タグデータリストの値を設備I/F読取データ情報リストに移す
        tagDataList.forEach(tagdata -> {
            EquipmentIfReadData buf = new EquipmentIfReadData();
            buf.setParameterItemId(tagdata.getTagItemName());
            buf.setValue(tagdata.getTagValue());
            buf.setIsGoodQuality(tagdata.isGoodQuality());
            readDataList.add(buf);
        });

        return readDataList;
    }

    /**
     * 設備情報書込み.
     * <pre>
     * 設備情報の書込みを行う。
     * </pre>
     * @param equipmentId 設備ID
     * @param parameterItemIdArray 設備パラメータ項目ID配列
     * @param inputDataArray 入力データ値配列
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<Integer> writeSubSystemTagData(String equipmentId, String[] parameterItemIdArray,
            String[] inputDataArray) throws GnomesAppException
    {

        try {
            // 設備I/Fパラメータ情報取得
            EquipmentIfParamInfo paramInfo = this.getInterfaceParameter(equipmentId, parameterItemIdArray);

            // 書込タグ情報作成
            List<TagValue> paramList = this.setWriteSubsystemTagParam(equipmentId, paramInfo, inputDataArray);

            ITagDataManager tagDataManager = new TagDataManager();
            // サブシステムタグデータ書込み
            List<Integer> statusList = tagDataManager.writeSubsystemTagData(paramInfo.getSubSystemId(), paramList);



            return statusList;

        }
        catch (LoopOnExException e) {

            this.logHelper.severe(this.logger, null, null, e.toString());

            // LoopOnEx処理でエラーが発生しました。 GRANSIGHT-EX LoopOnEx処理のログを確認してください。（設備ID：{0}）(ME01.0176）
            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0176,
                    equipmentId);
        }


    }

    /**
     * 設備情報取得.
     * <pre>
     * 設備マスタより、設備情報の取得を行う。
     * </pre>
     * @param equipmentId 設備ID
     * @return 設備マスタ情報
     * @throws GnomesAppException
     */
    private MstrEquipment getEquipment(String equipmentId) throws GnomesAppException
    {

        MstrEquipment result = this.mstrEquipmentDao.getMstrEquipment(equipmentId);
        // 設備情報が取得できなかった場合
        if (result == null) {
            // データがみつかりません。（{0}）(ME01.0026）
            StringBuilder argument = new StringBuilder();
            argument.append(CommonConstants.TABLE_NAME).append(MstrEquipment.TABLE_NAME);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(MstrEquipment.COLUMN_NAME_EQUIPMENT_ID);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(equipmentId);

            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0026,
                    new Object[]{argument.toString()});

        }

        return result;

    }

    /**
     * 設備パラメータ情報取得.
     * <pre>
     * 設備パラメータマスタより設備パラメータ情報の取得を行う。
     * </pre>
     * @param equipmentKey 設備キー
     * @param parameterItemIdArray パラメータ項目ID配列
     * @return 設備パラメータ情報
     * @throws GnomesAppException
     */
    private List<MstrEquipmentParameter> getEquipmentParameterList(String equipmentKey, String[] parameterItemIdArray)
            throws GnomesAppException
    {

        List<MstrEquipmentParameter> result = this.mstrEquipmentParameterDao.getMstrEquipmentParameterList(equipmentKey,
                parameterItemIdArray);

        // 設備パラメータ情報リスト存在チェック
        this.isExisteEquipmentParameter(equipmentKey, parameterItemIdArray, result);

        return result;

    }

    /**
     * 設備パラメータ情報リスト存在チェック.
     * @param equipmentKey 設備キー
     * @param parameterItemIdArray パラメータ項目ID配列
     * @param result 設備パラメータ情報リスト取得結果
     * @throws GnomesAppException
     */
    private void isExisteEquipmentParameter(String equipmentKey, String[] parameterItemIdArray,
            List<MstrEquipmentParameter> result) throws GnomesAppException
    {

        List<String> isExisteList = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            isExisteList.add(result.get(i).getParameter_item_id());
        }

        StringBuilder parameterItemId = new StringBuilder();

        for (int i = 0; i < parameterItemIdArray.length; i++) {

            if (!isExisteList.contains(parameterItemIdArray[i])) {

                if (parameterItemId.length() > 0) {
                    parameterItemId.append(CommonConstants.COMMA).append(HALF_SPACE);
                }
                parameterItemId.append(parameterItemIdArray[i]);

            }

        }

        if (parameterItemId.length() > 0) {

            // データがみつかりません。（{0}）(ME01.0026）
            StringBuilder argument = new StringBuilder();
            argument.append(CommonConstants.TABLE_NAME).append(MstrEquipmentParameter.TABLE_NAME);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(MstrEquipmentParameter.COLUMN_NAME_EQUIPMENT_KEY);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(equipmentKey);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(MstrEquipmentParameter.COLUMN_NAME_PARAMETER_ITEM_ID);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(parameterItemId.toString());

            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0026,
                    new Object[]{argument.toString()});

        }

    }

    /**
     * 設備I/F情報取得.
     * <pre>
     * 設備I/Fマスタより、設備I/F情報の取得を行う。
     * </pre>
     * @param equipmentKey 対象設備キー
     * @return 設備I/F情報
     * @throws GnomesAppException
     */
    private MstrEquipmentIf getEquipmentIf(String equipmentKey) throws GnomesAppException
    {

        MstrEquipmentIf result = this.mstrEquipmentIfDao.getMstrEquipmentIf(equipmentKey);

        if (result == null) {
            // データがみつかりません。（{0}）(ME01.0026）
            StringBuilder argument = new StringBuilder();
            argument.append(CommonConstants.TABLE_NAME).append(MstrEquipmentIf.TABLE_NAME);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(MstrEquipmentIf.COLUMN_NAME_EQUIPMENT_KEY);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(equipmentKey);

            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0026,
                    new Object[]{argument.toString()});

        }

        return result;

    }

    /**
     * 設備I/Fパラメータ情報リスト取得.
     * <pre>
     * 設備I/Fパラメータマスタより、設備I/Fパラメータ情報リストの取得を行う。
     * </pre>
     * @param ifParamKey 設備IFキー
     * @param equipmentParameterList 設備パラメータ情報リスト
     * @return 設備I/Fパラメータ情報リスト
     * @throws GnomesAppException
     */
    private List<MstrEquipmentIfParameter> getEquipmentIfParameter(String ifParamKey,
            List<MstrEquipmentParameter> equipmentParameterList) throws GnomesAppException
    {

        // 設備パラメータキーリスト作成
        List<String> equipmentParameterKeyList = new ArrayList<>();

        for (int i = 0; i < equipmentParameterList.size(); i++) {

            equipmentParameterKeyList.add(equipmentParameterList.get(i).getEquipment_parameter_key());

        }

        List<MstrEquipmentIfParameter> result = this.mstrEquipmentIfParameterDao.getMstrEquipmentIfParameterList(
                ifParamKey, equipmentParameterKeyList);

        // 設備I/Fパラメータ情報リスト存在チェック
        this.isExisteEquipmentIfParameter(ifParamKey, equipmentParameterKeyList, result);

        return result;

    }

    /**
     * 設備I/Fパラメータ情報リスト存在チェック.
     * @param ifParamKey 設備IFキー
     * @param equipmentParameterKeyList 設備パラメータ情報リスト
     * @param result 設備I/Fパラメータ情報リスト取得結果
     * @throws GnomesAppException
     */
    private void isExisteEquipmentIfParameter(String ifParamKey, List<String> equipmentParameterKeyList,
            List<MstrEquipmentIfParameter> result) throws GnomesAppException
    {

        List<String> isExisteList = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            isExisteList.add(result.get(i).getEquipment_parameter_key());
        }

        StringBuilder parameterKey = new StringBuilder();

        for (int i = 0; i < equipmentParameterKeyList.size(); i++) {

            if (!isExisteList.contains(equipmentParameterKeyList.get(i))) {

                if (parameterKey.length() > 0) {
                    parameterKey.append(CommonConstants.COMMA).append(HALF_SPACE);
                }
                parameterKey.append(equipmentParameterKeyList.get(i));

            }

        }

        if (parameterKey.length() > 0) {
            // データがみつかりません。（{0}）(ME01.0026）
            StringBuilder argument = new StringBuilder();
            argument.append(CommonConstants.TABLE_NAME).append(MstrEquipmentIfParameter.TABLE_NAME);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(MstrEquipmentIfParameter.COLUMN_NAME_EQUIPMENT_IF_KEY);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(ifParamKey);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.COLUMN_NAME).append(
                    MstrEquipmentIfParameter.COLUMN_NAME_EQUIPMENT_PARAMETER_KEY);
            argument.append(CommonConstants.COMMA).append(HALF_SPACE);
            argument.append(CommonConstants.VALUE).append(parameterKey.toString());

            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0026,
                    new Object[]{argument.toString()});

        }

    }

    /**
     * 設備I/Fパラメータ情報編集.
     * <pre>
     * 設備I/Fパラメータ情報の編集を行う。
     * </pre>
     * @param subSystemId サブシステムID
     * @param parameterItemIdArray パラメータIDのリスト
     * @param equipmentParameterList 設備パラメータ情報リスト（パラメータIDに該当するリスト）
     * @param equipmentInterfaceParameterList 設備I/Fパラメータ情報リスト（パラメータIDに該当するリスト）
     * @return 設備I/Fパラメータ情報
     */
    private EquipmentIfParamInfo editEquipmentIfParamInfo(String subSystemId, String[] parameterItemIdArray,
            List<MstrEquipmentParameter> equipmentParameterList,
            List<MstrEquipmentIfParameter> equipmentInterfaceParameterList)
    {

        //設備パラメータ情報や設備IFパラメータ情報を探して見つけたデータアイテムIDの保管マップ
        Map<String, String> dataItemAddressMap = new HashMap<>();

        //設備パラメータ情報や設備IFパラメータ情報を探して見つけた特殊変換処理区分の保管マップ
        Map<String, Integer> specialConvertDivMap = new HashMap<>();

        // アイテム名リスト
        List<String> itemNameList = new ArrayList<>();

        // 特殊変換処理区分リスト
        List<Integer> specialConvertDivList = new ArrayList<>();

        //---------------------------------------------------------------------
        //  設備パラメータ情報リストを回す
        //---------------------------------------------------------------------
        for (int i = 0; i < equipmentParameterList.size(); i++) {

            // パラメータ項目ID
            String parameterItemId = equipmentParameterList.get(i).getParameter_item_id();
            // 設備パラメータキー
            String parameterKey = equipmentParameterList.get(i).getEquipment_parameter_key();
            // データアイテム
            String dataItemAddress = "";
            // 特殊変換処理区分
            int specialConvertDiv = 0;

            //----------------------------------------------------------------
            //  さらに設備I/Fパラメータ情報リストを回して
            //  設備パラメータキーに一致するアイテムIDを探す
            //----------------------------------------------------------------
            for (MstrEquipmentIfParameter ifparam : equipmentInterfaceParameterList) {

                //設備I/Fパラメータ情報の１つと設備パラメータ情報リストの１つが一致したら
                if (ifparam.getEquipment_parameter_key().equals(parameterKey)) {

                    //-------------------------------------------------------
                    //これがLoopOnExに渡すタグ名なので、dataItemAddressに保管
                    //-------------------------------------------------------
                    dataItemAddress = ifparam.getData_item_address();
                    specialConvertDiv = ifparam.getSpecial_convert_div();

                    //-------------------------------------------------------
                    //パフォーマンスのため、一度判定したデータは消す
                    //-------------------------------------------------------
                    equipmentInterfaceParameterList.remove(ifparam);
                    break;

                }

            }

            //同じアイテムがあったら登録しない
            if (!dataItemAddressMap.containsKey(parameterItemId)) {
                dataItemAddressMap.put(parameterItemId, dataItemAddress);
                specialConvertDivMap.put(parameterItemId, specialConvertDiv);
            }

        }

        EquipmentIfParamInfo result = new EquipmentIfParamInfo();
        // サブシステムID
        result.setSubSystemId(subSystemId);

        //同じアイテムがあったら同じデータを流用するので、一度
        //パラメータIDのリストを回して、補完マップからデータを取る
        for (String parameterItemId : parameterItemIdArray) {
            // データアイテムを設定
            itemNameList.add(dataItemAddressMap.get(parameterItemId));
            specialConvertDivList.add(specialConvertDivMap.get(parameterItemId));

        }

        //集めたPLCタグのリストを設定
        result.setItemNameList(itemNameList);
        //集めた特殊変換処理区分のリストを設定
        result.setSpecialConvertDivList(specialConvertDivList);

        return result;

    }

    //    /**
    //     * リモート接続 サービスパス取得
    //     * @param methodPath 呼び出しメソッドパス
    //     * @return サービスパス
    //     */
    //    private String getServicePath(String methodPath) {
    //
    //        return String.format("%s%s/%s/%s",
    //                REMOTE_SERVICE_PATH, "rest", "TagDataManagerService", methodPath);
    //
    //    }

    //    /**
    //     * 書込タグ情報作成
    //     * @param paramInfo 設備I/Fパラメータ情報
    //     * @param inputDataArray 入力データ値配列
    //     * @return サブシステムタグデータ書き込みパラメータ
    //     */
    //    private LoopOnExWriteSubsystemTagParam setWriteSubsystemTagParam(
    //            EquipmentIfParamInfo paramInfo, String[] inputDataArray) {
    //
    //        LoopOnExWriteSubsystemTagParam writeParam = new LoopOnExWriteSubsystemTagParam();
    //
    //        writeParam.setReq(this.req);
    //
    //        writeParam.setTagValueList(new ArrayList<>());
    //
    //        writeParam.setSubSystemId(paramInfo.getSubSystemId());
    //
    //        for (int i = 0; i < paramInfo.getItemNameList().size(); i++) {
    //
    //            TagValue tagValue = new TagValue();
    //            tagValue.setTagNo(paramInfo.getItemNameList().get(i));
    //            tagValue.setValue(inputDataArray[i]);
    //            writeParam.getTagValueList().add(tagValue);
    //
    //        }
    //
    //        return writeParam;
    //
    //    }

    /**
     * 書込タグ情報作成
     * @param paramInfo 設備I/Fパラメータ情報
     * @param inputDataArray 入力データ値配列
     * @return
     * @throws GnomesAppException
     */
    private List<TagValue> setWriteSubsystemTagParam(String equipmentId, EquipmentIfParamInfo paramInfo,
            String[] inputDataArray) throws GnomesAppException
    {

        List<TagValue> paramList = new ArrayList<>();
        List<Integer> specialConvertDivList = paramInfo.getSpecialConvertDivList();
        List<String> itemNameList = paramInfo.getItemNameList();

        int itemNameListsize = itemNameList.size();

        //データの値の個数がparamInfoのアイテムの個数より小さい場合は
        //データの値の個数に合わせる
        if (inputDataArray.length < itemNameListsize) {
            itemNameListsize = inputDataArray.length;
        }

        for (int i = 0; i < itemNameListsize; i++) {

            TagValue tagValue = new TagValue();
            tagValue.setTagItemName(itemNameList.get(i));
            //特殊変換処理区分が0でない（何か変換がある場合）は特殊変換処理を行う
            if (!specialConvertDivList.get(i).equals(0)) {

                String convertedValue = this.equipmentSpecalLogic.itemSpecialConvert(equipmentId, specialConvertDivList.get(i),
                        itemNameList.get(i), inputDataArray[i]);
                tagValue.setValue(convertedValue);
            }
            else {
                tagValue.setValue(inputDataArray[i]);

            }

            paramList.add(tagValue);

        }

        return paramList;

    }

}
