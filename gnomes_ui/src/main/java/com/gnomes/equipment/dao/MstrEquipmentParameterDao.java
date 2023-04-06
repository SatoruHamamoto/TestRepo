package com.gnomes.equipment.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.equipment.entity.MstrEquipmentParameter;

/**
 * 設備パラメータマスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrEquipmentParameterDao extends BaseDao implements Serializable {

    /** コンストラクタ. */
    public MstrEquipmentParameterDao() {

    }

    /**
     * 設備パラメータマスタ情報リスト取得.
     * <pre>
     * 設備キー、設備パラメータ項目IDリストをもとに設備パラメータマスタ情報の取得を行う。
     * </pre>
     * @param equipmentKey 設備キー
     * @param parameterItemIdArray 設備パラメータ項目ID配列
     * @return 設備パラメータマスタ情報リスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrEquipmentParameter> getMstrEquipmentParameterList(
            String equipmentKey, String[] parameterItemIdArray) throws GnomesAppException {

        // パラメータチェック
        // 設備キー
        if (StringUtil.isNullOrEmpty(equipmentKey)) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipmentParameter.COLUMN_NAME_EQUIPMENT_KEY, equipmentKey)});

        }
        // 設備パラメータ項目ID配列
        if (parameterItemIdArray == null) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipmentParameter.COLUMN_NAME_PARAMETER_ITEM_ID, parameterItemIdArray)});

        } else if (parameterItemIdArray.length == 0) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipmentParameter.COLUMN_NAME_PARAMETER_ITEM_ID, "")});

        }

        List<MstrEquipmentParameter> result =
                super.gnomesSystemModel.getMstrEquipmentParameterList().stream()
                .filter(item -> item.getEquipment_key().equals(equipmentKey))
                .filter(item -> Arrays.asList(parameterItemIdArray).contains(item.getParameter_item_id()))
                .collect(Collectors.toList());

        Collections.sort(result, Comparator.comparing(MstrEquipmentParameter::getEquipment_parameter_key));

        return result;

    }

}
