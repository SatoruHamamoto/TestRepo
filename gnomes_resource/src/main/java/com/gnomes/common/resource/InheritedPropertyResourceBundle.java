package com.gnomes.common.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * ここにクラス概要を入力してください。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/12 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class InheritedPropertyResourceBundle extends ResourceBundle {

//    // 継承関係のバンドル
//    private List<InheritedPropertyResourceBundle> relationships = null;

    // リソースバンドルのインスタンス
    private ResourceBundle instance = null;

//    // ベース名
//    private String baseName = null;

    /**
     * コンストラクタ
     */
    public InheritedPropertyResourceBundle(String baseName, Locale locale) {
//        this.baseName = baseName;

        // リソースバンドル読み込み
        this.instance = ResourceBundle.getBundle(baseName, locale,
                new ResourceBundleControl());

//        // 継承関係初期化
//        initRelationships();

    }
//
//    /**
//     * 継承関係のバンドルを初期化します。
//     */
//    protected void initRelationships() {
//        //
//        relationships = Collections.synchronizedList(
//                new ArrayList<InheritedPropertyResourceBundle>());
//
//        RelationshipLoader loader = new RelationshipLoader(this.baseName);
//
//        loader.createRelationships(this);
//    }
//
//    public void addRelationship(InheritedPropertyResourceBundle bundle) {
//        relationships.add(bundle);
//    }
//
    /* (非 Javadoc)
     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
     */
    @Override
    protected Object handleGetObject(String key) {
        Object retVal = null;

//        // MES-2 移行の要件検討
//        // 言語順、継承関係順に取得したい
//        // 正）継承関係のバンドル 日本語 ー＞ 本インスタンス 日本語 －＞ 継承関係のバンドル デフォルト・ロケール ー＞ 本インスタンス デフォルト・ロケール
//        // 現状は、継承関係順、言語順 に取得してしまう
//        // 誤）継承関係のバンドル 日本語 ー＞ 継承関係のバンドル デフォルト・ロケール ー＞ 本インスタンス 日本語 －＞ 本インスタンス デフォルト・ロケール
//
//        // 継承関係のバンドルからリソースを取得します
//        //if (retVal == null && relationships != null) {
//        if (relationships != null) {
//            for (InheritedPropertyResourceBundle bundle : relationships) {
//                retVal = bundle.handleGetObject(key);
//                if (retVal != null) {
//                    return retVal;
//                }
//            }
//        }

        // 本インスタンスからリソースを取得します
        try {
            retVal = instance.getObject(key);
        } catch (MissingResourceException e) {
            //nothing
        }

        return retVal;
    }

    /* (非 Javadoc)
     * @see java.util.ResourceBundle#getKeys()
     */
    @Override
    public Enumeration<String> getKeys() {
        //
        Vector<String> temp = new Vector<String>();
        for (Enumeration<String> e = instance.getKeys(); e.hasMoreElements();) {
            temp.add(e.nextElement());
        }

//        // 継承関係のバンドルが存在する場合、本インスタンスに存在しないキーを追加します
//        if (relationships != null) {
//            for (InheritedPropertyResourceBundle bundle : relationships) {
//                for (Enumeration<String> e = bundle.getKeys(); e
//                        .hasMoreElements();) {
//                    String o = e.nextElement();
//                    if (!temp.contains(o)) {
//                        temp.add(o);
//                    }
//                }
//            }
//        }
        return temp.elements();
    }

    /**
     * ここにクラス概要を入力してください。
     * <!-- TYPE DESCRIPTION --><pre>
     * </pre>
     */
    /* ========================== MODIFICATION HISTORY ==========================
     * Release  Date       ID/Name                   Comment
     * --------------------------------------------------------------------------
     * R0.01.01 2016/07/12 YJP/K.Gotanda              初版
     * [END OF MODIFICATION HISTORY]
     * ==========================================================================
     */
    private static class ResourceBundleControl extends ResourceBundle.Control {
        /*
         * UTF-8 エンコーディングされたプロパティファイルを読み込む。
         * @see java.util.ResourceBundle.Control#newBundle(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
         */
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale,
                String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException,
                IOException {

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");

            ResourceBundle bundle = null;

            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(
                            new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
