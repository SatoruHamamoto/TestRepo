package com.gnomes.equipment.dao;

import java.io.Serializable;
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
import com.gnomes.equipment.entity.MstrEquipmentIfParameter;

/**
 * 設備I/Fパラメータマスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrEquipmentIfParameterDao extends BaseDao implements Serializable {

    /** コンストラクタ. */
    public MstrEquipmentIfParameterDao() {

    }

    /**
     * 設備IFパラメータ情報リスト取得.
     * @param ifParamKey 設備IFキー
     * @param equipmentParameterKeyList 設備パラメータキーリスト
     * @return 設備IFパラメータ情報リスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrEquipmentIfParameter> getMstrEquipmentIfParameterList(
            String ifParamKey, List<String> equipmentParameterKeyList) throws GnomesAppException {

        // パラメータチェック
        // 設備IFキー
        if (StringUtil.isNullOrEmpty(ifParamKey)) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipmentIfParameter.COLUMN_NAME_EQUIPMENT_IF_KEY, ifParamKey)});

        }
        // 設備パラメータキーリスト
        if (equipmentParameterKeyList == null || equipmentParameterKeyList.isEmpty()) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipmentIfParameter.COLUMN_NAME_EQUIPMENT_PARAMETER_KEY, equipmentParameterKeyList)});


        }

        List<MstrEquipmentIfParameter> result =
                super.gnomesSystemModel.getMstrEquipmentIfParameterList().stream()
                .filter(item -> item.getEquipment_if_key().equals(ifParamKey))
                .filter(item -> equipmentParameterKeyList.contains(item.getEquipment_parameter_key()))
                .collect(Collectors.toList());

        Collections.sort(result, Comparator.comparing(MstrEquipmentIfParameter::getEquipment_if_key));

        return result;

    }

}
