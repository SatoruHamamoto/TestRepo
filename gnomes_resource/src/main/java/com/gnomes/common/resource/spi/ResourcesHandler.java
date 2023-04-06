package com.gnomes.common.resource.spi;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.gnomes.common.resource.InheritedBundleManager;

/**
 * ここにクラス概要を入力してください。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class ResourcesHandler {

    //private static transient Logger logger = null;

    /**
     * デフォルト・コンストラクタ
     */
    private ResourcesHandler() {
    }

    /**
     * リソース取得
     * @param    リソースKey
     * @return   文字列
     */
    public static String getString(String key) {



        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[2],  Locale.getDefault());
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[1],  Locale.getDefault());
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[0],  Locale.getDefault());
                try {
                    return bundle.getString(key);
                } catch (MissingResourceException e3) {
                    e3.printStackTrace();

                }
            }
        }
        return key;
    }

    /**
     * リソース取得
     * @param    リソースKey
     * @param    ロケール
     * @return   文字列
     */
    public static String getString(String key, Locale locale) {
        // Jobリソースから取得
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[2], locale);
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Contentsリソースから取得
            bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[1], locale);
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e2) {
                // Gnomesリソースから取得
                bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[0],  locale);
                try {
                    return bundle.getString(key);
                } catch (MissingResourceException e3) {
                    // 指定のロケールで該当リソースKey の文字列が取得できない場合は、システムデフォルトロケールより取得する。
                    bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.getGnomesResourcesList()[0], Locale.getDefault());
                    try {
                        return bundle.getString(key);
                    } catch (MissingResourceException e4) {
                        e4.printStackTrace();
                    }
                }
            }

        }
        return key;
    }

}
