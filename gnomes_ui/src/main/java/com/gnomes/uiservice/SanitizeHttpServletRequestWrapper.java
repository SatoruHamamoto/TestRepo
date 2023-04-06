package com.gnomes.uiservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.gnomes.common.constants.StringSanitizeConstants;
/**
 * リクエストラッピングクラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/02/21 YJP/T.Kamizuru              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class SanitizeHttpServletRequestWrapper extends HttpServletRequestWrapper {
    /**
     * デフォルト・コンストラクタ
     * @param request
     */
    public SanitizeHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * パラメータ取得（サニタイズ処理）
     * @param name パラメータ名
     * @return String パラメータ名(サニタイズ後)
     */
    public String getParameter(String name) {
        return sanitize(getRequest().getParameter(name));
    }

    /**
     * すべてのパラメータ名、値を取得（サニタイズ処理）
     * @return Map<String, String[]> 取得値配列
     */
    public Map<String, String[]> getParameterMap() {
        return getSanitizeMap(getRequest().getParameterMap());
    }

    /**
     * すべてのパラメータ値を取得（サニタイズ処理）
     * @return String[] 取得値配列
     */
    public String[] getParameterValues(String name) {
        String[] values = getRequest().getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = sanitize(values[i]);
            }
        }
        return values;
    }

    /**
     * サニタイズ処理（Map返却)
     * @param originalMap 変換前文字列
     * @return Map<String, String[]> 変換後文字列
     */
    private Map<String, String[]> getSanitizeMap(Map<String, String[]> originalMap) {
        if (originalMap == null) {
            return (new HashMap<String, String[]>());
        }
        HashMap<String, String[]> dest = new HashMap<String, String[]>();
        Iterator<String> keys = originalMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = sanitize((String) keys.next());
            dest.put(key, originalMap.get(key));
        }
        return dest;
    }

    /**
     * サニタイズ処理(String返却)
     * @param original 変換前文字列
     * @return String 変換後文字列
     */
    private String sanitize(String original) {
        String result = original;

        if (result != null && result.length() > 0) {
            for (int i = 0; i < StringSanitizeConstants.getSanitizeString().length; i++) {
                result = Pattern.compile(StringSanitizeConstants.getSanitizeString()[i][0]).matcher(
                        result).replaceAll(StringSanitizeConstants.getSanitizeString()[i][1]);
          }
        }
        return result;
      }
}