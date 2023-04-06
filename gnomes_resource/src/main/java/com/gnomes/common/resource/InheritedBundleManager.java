package com.gnomes.common.resource;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Priority;

import com.gnomes.common.constants.CommonConstants;



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
// @ApplicationScoped
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class InheritedBundleManager {

    private static InheritedBundleManager instance = null;

    private static final String[] GNOMES_RESOURCES_LIST = {"GnomesResources", "GnomesResources_Contents", "GnomesResources_Contents_Job"};

    private static final String[] GNOMES_MESSAGES_LIST = {"GnomesMessages", "GnomesMessages_Contents", "GnomesMessages_Contents_Job"};

    public static final String GNOMES_RESOURCES = "GnomesResources";

    public static final String GNOMES_RESOURCES_CONTENTS = "GnomesResources_Contents";

    public static final String GNOMES_RESOURCES_CONTENTS_JOB = "GnomesResources_Contents_Job";

    public static final String GNOMES_MESSAGES = "GnomesMessages";

    public static final String GNOMES_MESSAGES_CONTENTS = "GnomesMessages_Contents";

    public static final String GNOMES_MESSAGES_CONTENTS_JOB = "GnomesMessages_Contents_Job";

    // キャッシュ
    private ConcurrentMap<InheritedBundleKey, InheritedPropertyResourceBundle> bundles = null;

    /**
     * デフォルト・コンストラクタ
     */
    private InheritedBundleManager() {
        bundles = new ConcurrentHashMap<>();
/*
        // ファイル検索
        File file = new File("C:/workspace3/gnomes_common/src/com/gnomes/common/resource");
        String[] fileNameList = file.list();
        boolean isChangeResource = false;
        boolean isChangeMessage = false;

        for(String fileName: fileNameList){
            if(!isChangeResource){
                // JOBリソース有無
                if(fileName.matches("GnomesResources_+.*_+.*Constants.java")){
                    GNOMES_RESOURCES_LIST[2] = fileName.substring(0, fileName.indexOf("Constants.java"));
                    isChangeResource = true;
                }
                // コンテンツリソース有無
                else if(fileName.matches("GnomesResources_+.*Constants.java")){
                    GNOMES_RESOURCES_LIST[1] = fileName.substring(0, fileName.indexOf("Constants.java"));
                }
            }

            if(!isChangeMessage){
                // JOBリソース有無
                if(fileName.matches("GnomesMessages_+.*_+.*Constants.java")){
                    GNOMES_MESSAGES_LIST[2] = fileName.substring(0, fileName.indexOf("Constants.java"));
                    isChangeMessage = true;
                }
                // コンテンツリソース有無
                else if(fileName.matches("GnomesMessages_+.*Constants.java")){
                    GNOMES_MESSAGES_LIST[1] = fileName.substring(0, fileName.indexOf("Constants.java"));
                }
            }
        }
*/
    }

    public static  String[] getGnomesResourcesList(){
    	return GNOMES_RESOURCES_LIST;
    }

    public static  String[] getGnomesMessagesList(){
    	return GNOMES_MESSAGES_LIST;
    }


//    /**
//     * リソースバンドル取得 プロデューサー
//     * @return
//     */
////    @GnomesResources

////    @Produces
//    public ResourceBundle resourceBundleProducer() {
//        return InheritedBundleManager.getInstance().getBundle("GnomesResources");
//    }
//
//    /**
//     * メッセージリソースバンドル取得 プロデューサー
//     * @return
//     */
////    @GnomesMessages
////    @Produces
//    public ResourceBundle messageResourceBundleProducer() {
//        return InheritedBundleManager.getInstance().getBundle("GnomesMessages");
//    }
//
//    /**
//     * JSTL用 リソースバンドル取得 プロデューサー
//     * @return
//     */
////    @Named(value = "GnomesResources")
////    @Produces
//    public LocalizationContext localizationContextProducer() {
//        // リソース読込み
//        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle("GnomesResources");
//        // LocalizationContext
//        return (bundle == null ? null : new LocalizationContext(bundle));
//    }
//
//    /**
//     * JSTL用 メッセージリソースバンドル取得 プロデューサー
//     * @return
//     */
////    @Named(value = "GnomesMessages")
////    @Produces
//    public LocalizationContext messageLocalizationContextProducer() {
//        // リソース読込み
//        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle("GnomesMessages");
//        // LocalizationContext
//        return (bundle == null ? null : new LocalizationContext(bundle));
//    }

//    /**
//     * 初期化処理
//     */
//    @PostConstruct
//    public void init() {
//        bundles = new ConcurrentHashMap<>();
//    }

    /**
     * バンドル取得 (ロケール指定）
     * @param baseName
     * @param locale
     * @return
     */
    public InheritedPropertyResourceBundle getBundle(String baseName, Locale locale) {

        // リソースバンドル保持キー
        InheritedBundleKey key = new InheritedBundleKey(baseName, locale);

        // リソースバンドル取得
        InheritedPropertyResourceBundle bundle = bundles.get(key);

        if (bundle == null) {
            bundle = new InheritedPropertyResourceBundle(baseName, locale);
            bundles.put(key, bundle);
        }

        return bundle;
    }

    /**
     * インスタンス取得
     * @return
     */
    public static InheritedBundleManager getInstance() {
        if (instance == null) {
           instance = new InheritedBundleManager();
        }
        return instance;
     }

}

/**
 * リソースバンドルを保持するためのキークラス。
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

class InheritedBundleKey {

    private String baseName = null;

    private Locale locale = null;

    /**
     * コンストラクタ
     * @param baseName バンドルのベース名
     * @param locale ロケール
     */
    InheritedBundleKey(String baseName, Locale locale) {
        this.baseName = baseName;
        this.locale = locale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InheritedBundleKey) {
            InheritedBundleKey other = (InheritedBundleKey) obj;
            return (baseName.equals(other.baseName)&&locale.equals(other.locale));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int retVal = 0;
        retVal = retVal | baseName.hashCode();
        return retVal;
    }
}
