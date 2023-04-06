package com.gnomes.common.resource;

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
public class RelationshipLoader {

//    private String baseName = null;
//
//    private List<InheritedPropertyResourceBundle> relationships = null;

    public static final String FILE_EXTENSION = ".relationships";

    /**
     * デフォルト・コンストラクタ
     */
    public RelationshipLoader(String baseName) {

//        this.baseName = baseName;

        initRelationships();
    }

    protected void initRelationships() {

        //
//        relationships = Collections.synchronizedList(
//                new ArrayList<InheritedPropertyResourceBundle>());
//
//        InheritedBundleManager manager = InheritedBundleManager.getInstance();

//        //
//        // コンテンツの開発まで、リソースファイルの継承関係は持たせない。
//        //
//        // 設定ファイルに定義します
//        List<String> relationshipNames = new ArrayList<String>();
//        if (baseName.equals("GnomesResources")) {
//            relationshipNames.add("GnomesIryoResources");
//        }
//        if (baseName.equals("GnomesMessages")) {
//            relationshipNames.add("GnomesIryoMessages");
//        }
//        for (String relationshipName : relationshipNames) {
//
//            relationships.add(manager.getBundle(relationshipName));
//        }
    }

//    protected void createRelationships(InheritedPropertyResourceBundle source) {
//        //
//        for (InheritedPropertyResourceBundle bundle : relationships) {
//            source.addRelationship(bundle);
//        }
//    }

}
