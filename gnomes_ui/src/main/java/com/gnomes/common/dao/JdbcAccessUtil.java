package com.gnomes.common.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;

/**
 * JDBCアクセスに必要なユーティリティ関数群
 *  JDBCアクセスに関するデータや定義解析や共通処理を行う
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/09/04 09:58 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
@Dependent
public class JdbcAccessUtil
{

    /**
     * クラス変数の解析をするため自動生成されたエンティティの必ず備わる
     * カラム定数を取得するためのキーになる文字列
     */
    private static final String COLUMN_NAME_SUFFIX = "COLUMN_NAME";
    private static final String FIELD_NAME_SUFFIX  = "COLUMN_NAME_";

    /**
     * エンティティ定義のカラム情報をリフレクションを使って取得
     * カラムの情報を出力する
     * @param clazz         検査対象のクラス
     * @param columnList    出力されたカラム名リスト
     * @param fieldList     出力されたFieldクラスのリスト
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    public void getEntityColumnNameInfoList(Class clazz,
            List<String> columnList, List<Field> fieldList)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        //クラスのフィールド一覧をループで回す
        for (Field field : clazz.getDeclaredFields()) {

            //public static final のフィールドを取得
            int midifiers = field.getModifiers();

            if (Modifier.isFinal(midifiers) && Modifier.isStatic(midifiers)
                    && Modifier.isPublic(midifiers)) {

                //フィールドの名前を取得（例）COLUMN_NAME_USER_NUMBER
                String fieldName = field.getName();

                //フィールド名に"COLUMN_NAME"が含まれているもののみ対処
                if (fieldName.contains(COLUMN_NAME_SUFFIX)) {

                    //COLUMN_NAMEの設定値をカラム名にする
                    String columnName = (String)field.get(null);

                    //出来上がったカラム名のget/setができるものを格納
                    columnList.add(columnName);

                    //このカラム名が収まっているフィールドを入手して格納
                    Field dataField = clazz.getDeclaredField(columnName);
                    dataField.setAccessible(true);
                    fieldList.add(dataField);
                }
            }
        }
    }
    /**
     * Insert文の文字列を生成
     * @param tablename     insert into テーブル名
     * @param columnList    カラム名のリスト
     * @return
     */
    public String makeInsertSQLString(String tablename, List<String> columnList)
    {

        StringBuilder columnSb = new StringBuilder(" (");
        StringBuilder paramSb = new StringBuilder(" (");
        boolean first = true;
        for (String columnName : columnList) {
            if (first) {
                first = false;
                columnSb.append(columnName);
                paramSb.append("?");
            }
            else {
                columnSb.append("," + columnName);
                paramSb.append(",?");
            }
        }
        columnSb.append(" ) ");
        paramSb.append(" ) ");

        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(tablename);
        sb.append(columnSb.toString());
        sb.append(" values ");
        sb.append(paramSb.toString());

        return (sb.toString());
    }

    /**
     * delete文の文字列を生成
     * @param tableName         削除するテーブル名
     * @param keyColumnName     キーになるカラム名
     * @return
     */
    public String makeDeleteSQLString(String tableName, String keyColumnName)
    {
        return ("delete from " + tableName + " where " + keyColumnName
                + " = ?");
    }
}
