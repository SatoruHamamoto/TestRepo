package com.gnomes.equipment.logic;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.equipment.data.EquipmentIfReadData;
import com.gnomes.equipment.spi.AbstractEquipment;

import biz.grandsight.looponex.user.api.model.TagData;

/**
 * 設備IF機能
 */
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2018/02/06 YJP/H.Yamada              初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
@Dependent
public class EquipmentAccessClient extends BaseLogic {

    /** 設備IF機能(設備アクセスカセットAPI生成ファクトリ) */
    @Inject
    protected EquipmentControllerFactory factory;

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
    public List<EquipmentIfReadData> readMultiFromEquipment(
            String equipmentId, String[] parameterItemIdArray) throws GnomesAppException {

        // 設備アクセスカセットAPIインスタンス生成
        AbstractEquipment abstractEquipment = this.factory.createEquipmentContoroller(equipmentId);

        // 設備情報読込み
        List<EquipmentIfReadData> result = abstractEquipment.readSubSystemTagData(equipmentId, parameterItemIdArray);

        return result;

    }

    /**
     * 設備情報読込み（非推奨 ― デモ画面用互換性維持関数）
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
    public List<TagData> readMultiFromEquipment_old(
            String equipmentId, String[] parameterItemIdArray) throws GnomesAppException {

        // 設備アクセスカセットAPIインスタンス生成
        AbstractEquipment abstractEquipment = this.factory.createEquipmentContoroller(equipmentId);

        // 設備情報読込み
        List<TagData> result = abstractEquipment.getSubSystemTagData(equipmentId, parameterItemIdArray);

        return result;

    }

    /**
     * 設備情報書込み.
     * <pre>
     * 設備情報の書込みを行う。
     * </pre>
     * @param equipmentId 設備ID
     * @param parameterItemIdArray 設備パラメータ項目ID配列
     * @param inputDataArray 入力データ値配列
     * @return ステータスのリスト（0:正常 マイナス値:書き込みできなかった）
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void writeMultiToEquipment(String equipmentId,
            String[] parameterItemIdArray, String[] inputDataArray) throws GnomesAppException {

        // 設備アクセスカセットAPIインスタンス生成
        AbstractEquipment abstractEquipment = this.factory.createEquipmentContoroller(equipmentId);

        // 設備情報書込み
        List<Integer> statusList = abstractEquipment.writeSubSystemTagData(equipmentId, parameterItemIdArray, inputDataArray);

        // 内容をチェックして1件でもエラーがあったらスローする
        boolean bError=false;
        for(Integer status : statusList){
            //マイナス値が入った瞬間にスローする
            if(status.intValue() < 0){
                bError = true;
                break;
            }
        }
        if(bError){
            //設備への書き込みに失敗しました。（設備ID：{0}）
            GnomesAppException ex = this.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0240, equipmentId);

            int i=0;
            for(Integer status : statusList){
                String detailMsg = String.format("Item[%s] Val(%s) Status<%d>", parameterItemIdArray[i],inputDataArray[i],status.intValue());
                ex.addChildMessageData(new MessageData(GnomesMessagesConstants.ME01_0238, new Object[]{detailMsg}));
                i++;
            }
            throw ex;
        }

    }

}
