package com.gnomes.common.util;

import java.sql.Timestamp;

/**
 * システム日時取得共通処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/01 YJP/K.Gotanda             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class CurrentTimeStamp {

    /**
     * システム日時取得.
     * <pre>
     * システム日付の取得を行う。
     * </pre>
     * @return システム日時
     */
    public static Timestamp getSystemCurrentTimeStamp() {
        // 以下の処理で1ミリ秒スリープして「ずれ」を作ります。
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // 以下の処理はナノ秒の設定に不備がある為、コメントアウトにします。
        // currentTimeMillis…1970/1/1 0:00:00から何ミリ秒経ったか取得できる（どのPCでも同じ値） 直接時刻に変更できる
        // nanoTime…JavaVM上のシステム時間が持つシステム時間をナノ秒で取得できる(実行環境で違う値、負の値もあり得る)
        // CurrentTimeStampクラスでは、currentTimeMillisのミリ秒以下の値をnanoTimeで取得したミリ秒～マイクロ秒の部分までの値に変更している。（秒以上は切り捨て）
        // currentTimeMillisとnanoTimeでは基準となるシステム時間の開始タイミングにずれがあるので、
        // 「currentTimeMillisのミリ秒以下だけの値」と「nanoTimeのミリ秒以下だけの値」は常にどちらかが大きいわけではなく、
        // currentTimeMillisの繰り上がり（秒の桁）が起こってからnanoTimeの繰上り（秒の桁）が起こるまでの間は逆転してしまう。

//        // nano秒の取得
//        long nanos = System.nanoTime();
//        // ナノからマイクロへの変換
//        long micro = TimeUnit.NANOSECONDS.toMicros(nanos);
//        // ナノから秒への変換(マイクロと比較するために10^6をかける)
//        long seconds = TimeUnit.NANOSECONDS.toSeconds(nanos) * 1000000;
//        // マイクロ秒－秒をとりマイクロ秒だけを切り出す。
//        long mirco2 = micro - seconds;
//        timestamp.setNanos((int)mirco2 * 1000);

        return timestamp;
    }

}

