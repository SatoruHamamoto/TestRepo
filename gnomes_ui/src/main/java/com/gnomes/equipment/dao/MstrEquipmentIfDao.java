package com.gnomes.equipment.dao;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.equipment.entity.MstrEquipmentIf;

/**
 * 設備I/Fマスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrEquipmentIfDao extends BaseDao implements Serializable {

    /** コンストラクタ. */
    public MstrEquipmentIfDao() {

    }

    /**
     * 設備I/Fマスタ情報取得.
     * <pre>
     * 対象設備キーをもとに設備I/Fマスタ情報の取得を行う。
     * </pre>
     * @param equipmentKey 対象設備キー
     * @return 設備I/Fマスタ情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrEquipmentIf getMstrEquipmentIf(String equipmentKey) throws GnomesAppException {

        // パラメータチェック
        // 対象設備キー
        if (StringUtil.isNullOrEmpty(equipmentKey)) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipmentIf.COLUMN_NAME_EQUIPMENT_KEY, equipmentKey)});

        }

        List<MstrEquipmentIf> result = super.gnomesSystemModel.getMstrEquipmentIfList()
                .stream().filter(item -> item.getEquipment_key().equals(equipmentKey))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }

}
