package com.gnomes.equipment.logic;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/10/28 19:06 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * 設備I/Fアクセスで特殊処理をする際のロジックを記述するクラス。
 * AbstractEquipmentから切り離す
 * 差し替えを前提とする
 * @author 03501213
 *
 */
@Dependent
public class EqupmentAccessSpecialLogic
{
    /** ロガー */
    @Inject
    protected transient Logger  logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper         logHelper;

    /** GnomesExceptionファクトリ. */
    @Inject
    protected GnomesExceptionFactory      gnomesExceptionFactory;

    private static final String className = "EqupmentAccessSpecialLogic";
    /**
     * 特殊変換処理区分
     *  Zm113設備I/Fパラメータマスタ（特殊変換処理区分）
     *  mstr_equipment_if_parameter(special_convert_div) で使用
     *  共通にしないのは差し替えを行うため
     * @author 03501213
     *
     */
    public enum SpecialConvertDiv
    {
        /** 初期値、変換無し */
        OFF(0),
        /** 1:INT32の16ビット反転 */
        INT32_REVERCE_INT16(1);

        private int value;

        private SpecialConvertDiv(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }
    }

    /**
     * 特殊変換処理
     * @param equipmentId 設備ID
     * @param specialConvertDiv 特殊変換処理区分
     * @param tagData 入力データ
     * @return 変換後のタグデータ
     * @throws GnomesAppException
     */
    public String itemSpecialConvert(String equipmentId,Integer specialConvertDiv, String tagItem,String tagValue) throws GnomesAppException
    {
        final String methodName = "itemSpecialConvert";
        try {
            //1:INT32の16ビット反転の対応
            if (specialConvertDiv.equals(SpecialConvertDiv.INT32_REVERCE_INT16.getValue())) {
                //INT32整数にコンバートする
                int source = Integer.parseInt(tagValue);

                //16ビットずつ上下に入れ替える
                // src=AAAABBBBCCCCDDDDEEEEFFFFGGGGHHHH
                //1.16左シフト EEEEFFFFGGGGHHHH0000000000000000
                //2 16右シフト 0000000000000000AAAABBBBCCCCDDDD
                //3 1,2の or   EEEEFFFFGGGGHHHHAAAABBBBCCCCDDDD
                int dest = ( source << 16 ) | ( source >>> 16);

                //文字に戻す
                return String.valueOf(dest);
            }
        }
        // Int32に移せない場合はExceptionにてスローする
        catch (Exception ex) {

            this.logHelper.severe(this.logger, className, methodName,
                    "readSubsystemTagData ReadError ItegValue is 32bit Value",ex);

            StringBuilder sb = new StringBuilder();
            sb.append("Bad Value TagItem = \n");
            sb.append(" [" + tagItem + "] = [" + tagValue + "]\n");
            this.logHelper.severe(this.logger, className, methodName, sb.toString());

            //PLCからのデータが一部取得できませんでした。アイテム名定義および設備を確認してください。（設備ID：{0}）\n{1}
            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0235,
                    equipmentId, sb.toString());
        }
        //それ以外は何もしない
        return tagValue;
    }

}
