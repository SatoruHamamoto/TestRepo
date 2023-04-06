package com.gnomes.common.util;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.StringSanitizeConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * 文字列変換処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/08/19 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class StringUtils {

    public static final String EMPTY = "";

    /** html 改行文字 */
    public static final String BR = "<br>";

    /** 改行コードの正規表現 */
    public static final String LIEN_SEPARATOR_REGEX = "[\\n|\\r]";

    /** htmlの空白 */
    public static final String HTML_SPACE = "&nbsp;";

    /** 区切り文字 */
    public static final String SEPARATOR = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0035);

    /** リソースの改行コード */
    public static final String LIEN_SEPARATOR_RESOURCE = "\n";

    /** 後ろに偶数個の「"」が現れる「,」にマッチする正規表現 */
    public static final String REGEX_CSV_COMMA = ",(?=(([^\"]*\"){2})*[^\"]*$)";

    /** 最初と最後の「"」にマッチする正規表現*/
    public static final String REGEX_SURROUND_DOUBLEQUATATION = "(^\"|\"$)";

    /** 「""」にマッチする正規表現 */
    public static final String REGEX_DOUBLEQUOATATION = "\"\"";

    /** 非入力データ置換文字 */
    private static final String[][] NOT_INPUT_VALUE_REPLACE =
        {
                //変換前→変換後
                { " ",  "&nbsp;" },
                { "\r\n",  "<br>" },
                { "\r",  "<br>" },
                { "\n",  "<br>" }
        };

    /**
     * 文字幅を超えたときに途中で切って繋ぎがあることを示す文字とその文字数
     */
    private static final String STR_MSG_EOM = "...";
    private static final int eomLength = STR_MSG_EOM.length();


    /**
     * デフォルト・コンストラクタ
     */
    private StringUtils() {
    }

    /**
     * 大文字に変換する
     * @param text
     * @return 大文字となった文字列
     */
    public static String reverseUpperCase(String text) {
        if (text == null || text.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return text.toUpperCase();
    }

    /**
     * 文字列の先頭を大文字に変換.
     * <pre>
     * <code>null</code> が指定された場合、 <code>null</code> を返します。
     * </pre>
     * @param text 先頭を大文字にする文字列
     * @return 先頭が大文字となった文字列
     */
    public static String apitalise(String text) {

        if (StringUtil.isNullOrEmpty(text)) {
            return null;
        }

        return text.substring(0,1).toUpperCase() + text.substring(1);

    }


    /**
     * 文字列結合
     * @param オブジェクト配列
     * @return 結合文字列
     */
    public static String joinString(Object[] items) {
        if(Objects.isNull(items)){
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        for(Object item : items) {
            if(!Objects.equals(item, null)){
                builder.append(item.toString()).append(SEPARATOR);
            }
        }
        if(builder.length() > SEPARATOR.length()){
            return builder.substring(0, builder.length() - SEPARATOR.length());

        }else{
            return StringUtils.EMPTY;
        }
    }

    /**
     * 文字列結合
     * @param コレクション
     * @return 結合文字列
     */
    public static String joinString(List<?> items) {
        if(Objects.isNull(items)){
            return StringUtils.EMPTY;
        }

        StringBuilder builder = new StringBuilder();
        for(Object item : items) {
            if(!Objects.equals(item, null)){
                builder.append(item.toString()).append(SEPARATOR);
            }
        }
        return builder.substring(0, builder.length() - SEPARATOR.length());
    }

    /**
     * ファイル名から拡張子を返します。
     * @param fileName ファイル名
     * @return ファイルの拡張子
     */
    public static String getSuffix(String fileName) {

        String suffix = StringUtils.EMPTY;

        //引数のファイル名が空白、nullでなければ処理する
        if (!StringUtil.isNullOrEmpty(fileName)) {
            int point = fileName.lastIndexOf(".");
            if (point != -1) {
                suffix = fileName.substring(point + 1);
            }
        }
        return suffix;
    }
    /**
     * htmlエスケープ
     * @param str 対象文字列
     * @return 変換後文字列
     * @throws Exception
     */
    public static String getStringEscapeHtml(String str){
    	String strEscapeHtml = str;

    	if (strEscapeHtml != null && strEscapeHtml.length() > 0) {
        	for (int i = 0; i < StringSanitizeConstants.getSanitizeString().length; i++) {
        		strEscapeHtml = Pattern.compile(StringSanitizeConstants.getSanitizeString()[i][0]).matcher(
        				strEscapeHtml).replaceAll(StringSanitizeConstants.getSanitizeString()[i][1]);
        	}

        }
    	return strEscapeHtml;
    }

    /**
     * htmlエスケープ
     * @param inStr 対象文字列
     * @param isValue 入力データか否か
     * @return 変換後文字列
     */
    public static String getStringEscapeHtml(String inStr, boolean isValue) {

        String result = inStr;

        if (result != null && result.length() > 0) {

        	result = getStringEscapeHtml(inStr);

            if (isValue == false) {
        	    for (int i = 0; i < NOT_INPUT_VALUE_REPLACE.length; i++) {
                    result = Pattern.compile(NOT_INPUT_VALUE_REPLACE[i][0]).matcher(
                    		result).replaceAll(NOT_INPUT_VALUE_REPLACE[i][1]);
        	    }
            }

        }
        return result;

    }

   /**
    * カンマ区切りで行を分割し、文字列配列を返す。
    * 値にカンマ(,)を含む場合は,値の前後をダブルクオート(")で囲う
    * ダブルクオート(")は，2つのダブルクオートに置換する("")
    * @param str 対象文字列
    * @return 変換後文字配列
    */
    public static String[] splitLineWithComma(String line) {
       // 分割後の文字列配列
       String[] arr = null;

       // １、「"」で囲まれていない「,」で行を分割する。
       Pattern cPattern = Pattern.compile(REGEX_CSV_COMMA);
       String[] cols = cPattern.split(line, -1);

       arr = new String[cols.length];
       for (int i = 0, len = cols.length; i < len; i++) {
           String col = cols[i].trim();

           // ２、最初と最後に「"」があれば削除する。
           Pattern sdqPattern =
               Pattern.compile(REGEX_SURROUND_DOUBLEQUATATION);
           Matcher matcher = sdqPattern.matcher(col);
           col = matcher.replaceAll("");

           // ３、エスケープされた「"」を戻す。
           Pattern dqPattern =
               Pattern.compile(REGEX_DOUBLEQUOATATION);
           matcher = dqPattern.matcher(col);
           col = matcher.replaceAll("\"");

           arr[i] = col;
       }
       return arr;
   }

    /**
     * 指定文字が指定した長さを超えてたら、指定長さに切って"..."を作る
     * 例
     *      CutStringAndSetEOM("ABCDEFFG",5) = "AB..."
     * @param src       元の文字
     * @param length    残す文字の長さ
     * @return          元の文字を長さで切って末端に"..."を埋める
     */
    public static String CutStringAndSetEOM(String src,int length)
    {

        /**
         * 文字の長さが length 以下なら何もしない
         * 例）
         *      src = "ABCDE" length = 5 return = "ABCDE"
         *      src = "ABCDE" length > 6 return = "ABCDE"
         *      src = "A" length = 1 return = "A"
         *
         */
        if(src.length() <= length){
            return src;
        }
        /**
         * 文字の長さが lengthを超えている場合は、srcにlengthのELMマークの長さ分削る
         * ただし、EOMマークの長さに満たない場合はEOMマークはくっつかない
         * 例）
         *      src = "ABCDEFG" length = 2 return = "AB"
         *      src = "ABCDEFG" length = 3 return = "ABC"
         *      src = "ABCDEFG" length = 4 return = "A..."
         *      src = "ABCDEFG" length = 5 return = "AB..."
         *      src = "ABCDEFG" length = 6 return = "ABC..."
        */
        if(length <= eomLength){
            return src.substring(1, length);
        }
        /**
         * 文字加工を行う
         * srcの文字にEOMの文字数分削り、EOMをくっつける
         */
        return src.substring(1,length-eomLength) + STR_MSG_EOM;
    }

}
