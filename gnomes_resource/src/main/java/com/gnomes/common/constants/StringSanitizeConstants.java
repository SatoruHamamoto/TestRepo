package com.gnomes.common.constants;

/**
 * サニタイズ文字列  定数クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class StringSanitizeConstants {

    /** サニタイズ文字列 */
    private static final String[][] SANITIZE_STRING =
        {
                //変換前→変換後
                { "&",  "&amp;" },
                { "<",  "&lt;" },
                { ">",  "&gt;" },
                { "\"", "&quot;" },
                { "'",  "&#39;" }
        };

    public static String[][] getSanitizeString(){
    	return SANITIZE_STRING;
    }


}
