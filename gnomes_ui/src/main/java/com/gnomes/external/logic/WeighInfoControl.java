/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/04/26 20:45 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
package com.gnomes.external.logic;

import java.util.concurrent.ConcurrentHashMap;

import com.gnomes.external.data.WeighIfParam;
import com.gnomes.external.dto.WeighResultDto;
import com.gnomes.external.spi.WeighCycleThread;

/**
 * 秤量定周期やワンショット秤量のアクセス情報を保持するグローバル情報
 * @author 03501213
 *
 */
public class WeighInfoControl
{
    /** 定周期秤量処理スレッドMap.
     *      秤量器のデータ収集側のスレッドが起動したタイミングでBLからスレッドオブジェクト
     *      を受けて保持する。
     *      インジケータ画面の初期表示や秤量器オンライン・オフライン切替でコールされる
     *      画面処理で秤量のマスターから得た秤量のパラメータを定周期収集で使う
     *      する。定周期収集が終わったら破棄される
     */
    public static final ConcurrentHashMap<String, WeighCycleThread> weighCycleThreadMap       = new ConcurrentHashMap<>();

    /** 秤量器IFパラメータMap.
     *      秤量器のデータ収集側のスレッドが起動したタイミングで保持されるIFパラメータ
     *      インジケータ画面の初期表示や秤量器オンライン・オフライン切替でコールされる
     *      画面処理で秤量のマスターから得た秤量のパラメータを定周期収集で使うパラメータに
     *      する。定周期収集が終わったら破棄される
     */
    public static final ConcurrentHashMap<String, WeighIfParam>     weighIfParamMap           = new ConcurrentHashMap<>();

    /** 定周期秤量処理強制停止Map.
     *      クライアントのインジケータ画面が切断（画面展開または閉じる）ことにより
     *      WebSocket通信で切断を検知するとONにする
     *      ONにすると、秤量器のデータ収集側のスレッドは一定期間ONが続いていることを検知して
     *      定周期を止める判断をする
     */
    public static final ConcurrentHashMap<String, Boolean>          threadStopStateMap        = new ConcurrentHashMap<>();

    /** WebSocket接続状態Map.
     *      クライアントのインジケータから定周期で接続し、データを要求する。
     *      切断するとステータスがOFF(false)になる
     *      秤量器のデータ収集側のスレッドはこのステータスを見て収集する必要性を知り
     *      OFFにしたりする。画面展開が頻繁するとON/OFFが入れ替わるので、秤量器側のスレッドは
     *      不感帯を設けて一定期間OFFを検知して終了する
     */
    public static final ConcurrentHashMap<String, Boolean>          connectionStateMap        = new ConcurrentHashMap<>();

    /** 秤量結果(定周期)Map.
     *      定周期で秤量器の値を収集した内容を保持する
     *      収集のタイミングとクライアントからのデータ要求のタイミングが異なるため
     *      データ値をこちらに保管し毎回書き換える
     *      クライアントがいなくなったら定周期で秤量も終了する時、このデータはクリアされる
     */
    public static final ConcurrentHashMap<String, WeighResultDto>   weighCycleResultMap       = new ConcurrentHashMap<>();

    /**
     * 定周期秤量処理スレッド破棄.
     * <pre>
     * 定周期秤量処理スレッド破棄を行う。
     * </pre>
     *
     * @param weighApparatusId 秤量器ID
     */
    public static synchronized void removeWeighCycleThread(String weighApparatusId)
    {
        // 定周期秤量処理スレッド破棄
        WeighInfoControl.weighCycleThreadMap.remove(weighApparatusId);
        // 秤量器IFパラメータ破棄
        WeighInfoControl.weighIfParamMap.remove(weighApparatusId);
        // 定周期処理強制停止破棄
        WeighInfoControl.threadStopStateMap.remove(weighApparatusId);
        // WebSocket接続状態破棄
        WeighInfoControl.connectionStateMap.remove(weighApparatusId);
        // 秤量結果(定周期)破棄
        WeighInfoControl.weighCycleResultMap.remove(weighApparatusId);
    }

}
