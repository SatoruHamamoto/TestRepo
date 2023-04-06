package com.gnomes.equipment.logic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.equipment.dao.MstrEquipmentDao;
import com.gnomes.equipment.entity.MstrEquipment;
import com.gnomes.equipment.spi.AbstractEquipment;
import com.gnomes.equipment.spi.RemoteLoopOnExApi;

/**
 * 設備IF機能(設備アクセスカセットAPI生成ファクトリ)
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/05 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class EquipmentControllerFactory {

    /** 設備マスタDao. */
    @Inject
    protected MstrEquipmentDao mstrEquipmentDao;

    /** GnomesExceptionファクトリ. */
    @Inject
    protected GnomesExceptionFactory gnomesExceptionFactory;

    /** 設備IF機能(LoopOnEx). */
    @Inject
    protected RemoteLoopOnExApi remoteLoopOnExApi;

    /** 定数：半角スペース. */
    private static final String HALF_SPACE = " ";

    /**
     * 設備アクセスカセットAPIインスタンス生成.
     * <pre>
     * 設備アクセスカセットAPIのインスタンス生成を行う。
     * 現状、「LoopOnEx」のみのため、「LoopOnEx」のインスタンス生成を行う。
     * </pre>
     * @param equipmentId 設備ID
     * @return 設備アクセスカセットAPIインスタンス
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public AbstractEquipment createEquipmentContoroller(String equipmentId) throws GnomesAppException {

        // 設備情報取得
        MstrEquipment equipment = this.getEquipment(equipmentId);

        AbstractEquipment result;

        switch (equipment.getEquipment_name()) {

            default:
                result = this.remoteLoopOnExApi;
                break;

        }

        return result;

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
    private MstrEquipment getEquipment(String equipmentId) throws GnomesAppException {

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

            throw this.gnomesExceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0026, new Object[]{argument.toString()});

        }

        return result;

    }

}
