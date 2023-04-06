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
import com.gnomes.equipment.entity.MstrEquipment;

/**
 * 設備マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrEquipmentDao extends BaseDao implements Serializable {

    /** コンストラクタ. */
    public MstrEquipmentDao() {

    }

    /**
     * 設備マスタ情報取得.
     * <pre>
     * 設備IDをもとに設備マスタの取得を行う。
     * </pre>
     * @param equipmentId 設備ID
     * @return 設備マスタ情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrEquipment getMstrEquipment(String equipmentId) throws GnomesAppException {

        // パラメータチェック
        // 設備ID
        if (StringUtil.isNullOrEmpty(equipmentId)) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrEquipment.COLUMN_NAME_EQUIPMENT_ID, equipmentId)});

        }

        List<MstrEquipment> result = super.gnomesSystemModel.getMstrEquipmentList()
                .stream().filter(item -> item.getEquipment_id().equals(equipmentId))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }

}
