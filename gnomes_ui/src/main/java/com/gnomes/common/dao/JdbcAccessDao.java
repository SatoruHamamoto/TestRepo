package com.gnomes.common.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.gnomes.common.data.MessageData;
import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.GnomesEntitiy;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.uiservice.ContainerRequest;

/**
 * JDBCアクセス専用DAO
 * @Injectして使用する
 *
 * Insert/Deleteをサポートしている（Updateは将来）
 * BaseDaoのPersistやRemoveの代わりにコールする
 * トランザクションはBaseと同じ
 *
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/09/04 09:49 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * @author 03501213
 *
 */
@Dependent
public class JdbcAccessDao
{
    /** ロガー */
    @Inject
    protected transient Logger       logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper    logHelper;

    /** exceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /** ContainerRequest */
    @Inject
    protected ContainerRequest       req;

    /**
     * JDBCアクセスに関するデータや定義解析や共通処理を行う
     */
    @Inject
    protected JdbcAccessUtil         jdbcAccessUtil;

    /**
     * DAOの中で共通なエンティティの操作や設定のユーティリティ関数群
     */
    @Inject
    protected EntityDaoUtils         entityDaoUtils;

    //履歴区分 【システム項目】履歴テーブルに記録される変更の種類。0:登録, 1:更新, 2:削除
    public enum RevTypeOfHistorycalTable
    {
        ENTRY(0), UPDATE(1), DELETE(2);
        private int value;
        private RevTypeOfHistorycalTable(int n)
        {
            this.value = n;
        }
    }

    //履歴テーブルに関する定数
    private static final String HISTORY_REVTYPE_COLUMNNAME  = "revType";
    private static final String HISTORY_REV_COLUMNNAME      = "rev";
    private static final String SUFFIX_HISTORICAL_TABLENAME = "h_";
    //レブ情報の発番をするクエリ文字列
    private static final String SQLSTR_INSERT_REVINFO       = "insert into revinfo(revtstmp) values (?)";
    //レブ情報の最後の値を取り出すクエリ文字列
    private static final String SQLSTR_REVMAX_VALUEGET      = "select MAX(rev) from revinfo";

    //エラーログフォーマット定数
    private static final String ERRLOG_FORMAT_FIELD         = "field = ";
    private static final String ERRLOG_FORMAT_VALUE         = " value =";

    /**
     * ロガーに渡すクラス名（定数の方が速い）
     */
    private static final String CLASS_NAME                  = "JdbcAccessDao";
    /**
     * JDBCを使ってバルクインサートを行う
     *
     * @param emm エンティティマネージャ
     * @param entityList エンティティのオブジェクトリスト（同一エンティティのリスト）
     * @throws GnomesAppException
     */
    @ErrorHandling
    public <T extends GnomesEntitiy> void bulkPersist(EntityManager emm,
            List<T> entityDataList) throws GnomesAppException
    {

        //エンティティの数が０の場合は何もしない
        if (entityDataList == null || entityDataList.isEmpty())
        {
            return;
        }

        //エンティティを管理から除外する
        // これにより、エンティティはJPAの管理下にならなくなるので
        // 使いまわしてはいけない
        for(T entity : entityDataList){
            emm.detach(entity);
        }

        //型解析するために先頭のデータを利用する
        T entityFirst = entityDataList.get(0);

        //エンティティ定義アクセスのための諸情報を用意
        String tableName = entityDaoUtils.getTableNameFromEntity(entityFirst);
        List<String> columnList = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>();

        //************************************************************
        //  現クラスのカラム情報を取得
        //************************************************************
        try
        {

            //エンティティ定義の情報を取得
            jdbcAccessUtil.getEntityColumnNameInfoList(entityFirst.getClass(),
                    columnList, fieldList);

            //合わせてスーパークラスのカラム情報を取得
            jdbcAccessUtil.getEntityColumnNameInfoList(
                    entityFirst.getClass().getSuperclass(), columnList,
                    fieldList);

        }
        catch (Exception e)
        {
            /** 一括インサートのエンティティオブジェクトの解析処理に失敗しました。エンティティクラス={0} インサートする対象のテーブル名={1} */
            GnomesAppException ex = CreteAppExceptionFromLocal(e,
                    GnomesMessagesConstants.ME01_0218,
                    GnomesMessagesConstants.ME01_0224, entityFirst, tableName);
            throw ex;
        }

        //************************************************************
        //  JDBCよりバルクインサートを実行(本体)
        //************************************************************
        Session session = emm.unwrap(Session.class);

        try
        {
            session.doWork(connection -> doBulkInsert(connection, tableName,
                    fieldList, columnList, entityDataList));
        }
        catch (Exception e)
        {
            //一括削除（Remove)の一括実行に失敗しました。エンティティクラス={0} 削除（Remove)する対象のテーブル名={1}
            GnomesAppException ex = CreteAppExceptionFromLocal(e,
                    GnomesMessagesConstants.ME01_0219,
                    GnomesMessagesConstants.ME01_0224, entityFirst, tableName);
            throw ex;
        }

        //************************************************************
        //  JDBCよりバルクインサートを実行(履歴)
        //************************************************************
        if (entityDaoUtils.historyRegisterCheck((BaseEntity) entityFirst))
        {
            try
            {
                //履歴を格納する
                session.doWork(connection -> doBulkInsertHistory(connection,
                        SUFFIX_HISTORICAL_TABLENAME + tableName,
                        RevTypeOfHistorycalTable.ENTRY, fieldList, columnList,
                        entityDataList));

            }
            catch (Exception e)
            {
                //一括削除（Remove)の一括実行に失敗しました。エンティティクラス={0} 削除（Remove)する対象のテーブル名={1}
                GnomesAppException ex = CreteAppExceptionFromLocal(e,
                        GnomesMessagesConstants.ME01_0219,
                        GnomesMessagesConstants.ME01_0224, entityFirst,
                        SUFFIX_HISTORICAL_TABLENAME + tableName);
                throw ex;
            }

        }

    }

    /**
     *
     * JDBCを使ってdeleteを行う
     *
     * @param emm               エンティティマネージャ
     * @param keyColumnName     削除で唯一のユニークキーとなるサロゲートキーのカラム名を指定
     * @param entityDataList    削除対象のエンティティリスト
     * @throws GnomesAppException
     */
    @ErrorHandling
    public <T extends GnomesEntitiy> void bulkRemove(EntityManager emm,
            String keyColumnName, List<T> entityDataList)
            throws GnomesAppException
    {
        //エンティティの数が０の場合は何もしない
        if (entityDataList == null || entityDataList.isEmpty())
        {
            return;
        }

        //エンティティを管理から除外する
        // これにより、エンティティはJPAの管理下にならなくなるので
        // 使いまわしてはいけない
        for(T entity : entityDataList){
            emm.detach(entity);
        }

        //型解析するために先頭のデータを利用する
        T entityFirst = entityDataList.get(0);

        //エンティティ定義アクセスのための諸情報を用意
        String tableName = this.entityDaoUtils
                .getTableNameFromEntity(entityFirst);
        Field keyColumnField;

        //************************************************************
        // キーカラムのフィールドを取得
        //************************************************************
        try
        {

            keyColumnField = entityFirst.getClass()
                    .getDeclaredField(keyColumnName);
            keyColumnField.setAccessible(true);

        }
        catch (Exception e)
        {
            /** 一括削除（Remove)のエンティティオブジェクトの解析処理に失敗しました。エンティティクラス={0} 削除（Remove)する対象のテーブル名={1} */
            GnomesAppException ex = CreteAppExceptionFromLocal(e,
                    GnomesMessagesConstants.ME01_0221,
                    GnomesMessagesConstants.ME01_0224, entityFirst, tableName);
            throw ex;
        }

        //************************************************************
        //  JDBCよりバルク削除を実行(本体)
        //************************************************************
        Session session = emm.unwrap(Session.class);

        try
        {
            session.doWork(connection -> doBulkDelete(connection, tableName,
                    keyColumnName, keyColumnField, entityDataList));
        }
        catch (Exception e)
        {
            //一括削除（Remove)の一括実行に失敗しました。エンティティクラス={0} 削除（Remove)する対象のテーブル名={1}
            GnomesAppException ex = CreteAppExceptionFromLocal(e,
                    GnomesMessagesConstants.ME01_0222,
                    GnomesMessagesConstants.ME01_0224, entityFirst, tableName);
            throw ex;
        }

        //************************************************************
        //  JDBCよりバルク削除を実行(履歴)
        //************************************************************
        if (this.entityDaoUtils.historyRegisterCheck((BaseEntity) entityFirst))
        {
            List<String> columnList = new ArrayList<>();
            List<Field> fieldList = new ArrayList<>();

            //************************************************************
            //  現クラスのカラム情報を取得
            //************************************************************
            try
            {

                //エンティティ定義の情報を取得
                jdbcAccessUtil.getEntityColumnNameInfoList(
                        entityFirst.getClass(), columnList, fieldList);

                //合わせてスーパークラスのカラム情報を取得
                jdbcAccessUtil.getEntityColumnNameInfoList(
                        entityFirst.getClass().getSuperclass(), columnList,
                        fieldList);

            }
            catch (Exception e)
            {
                /** 一括削除（Remove)のエンティティオブジェクトの解析処理に失敗しました。エンティティクラス={0} 削除（Remove)する対象のテーブル名={1} */
                GnomesAppException ex = CreteAppExceptionFromLocal(e,
                        GnomesMessagesConstants.ME01_0221,
                        GnomesMessagesConstants.ME01_0224, entityFirst,
                        tableName);
                throw ex;
            }

            try
            {
                //履歴を格納する
                session.doWork(connection -> doBulkInsertHistory(connection,
                        SUFFIX_HISTORICAL_TABLENAME + tableName,
                        RevTypeOfHistorycalTable.DELETE, fieldList, columnList,
                        entityDataList));

            }
            catch (Exception e)
            {
                //一括インサートの一括実行に失敗しました。エンティティクラス={0} インサートする対象のテーブル名={1}
                GnomesAppException ex = CreteAppExceptionFromLocal(e,
                        GnomesMessagesConstants.ME01_0219,
                        GnomesMessagesConstants.ME01_0224, entityFirst,
                        SUFFIX_HISTORICAL_TABLENAME + tableName);
                throw ex;
            }
        }

    }
    /**
     *  JDBCをじかに使って複数のエンティティオブジェクト（同じクラス）のデータを一括して
     *  Insertする
     *
     * @param connection    コネクションオブジェクト
     * @param tablename     テーブル名
     * @param fieldList     エンティティオブジェクトデータのフィールドオブジェクト
     * @param columnList    カラム名のリスト
     * @param entityDataList    エンティティオブジェクトのリスト
     * @return 将来用（今はnull）
     * @throws SQLException
     */
    @ErrorHandling
    private <T extends GnomesEntitiy> Object doBulkInsert(Connection connection,
            String tablename, List<Field> fieldList, List<String> columnList,
            List<T> entityDataList) throws SQLException
    {

        final String methodName = "doBulkInsert";

        PreparedStatement pstmt = null;
        long startTime = System.currentTimeMillis();

        //別関数でInsertのSQL文を作る
        String sql = this.jdbcAccessUtil.makeInsertSQLString(tablename,
                columnList);

        logHelper.fine(this.logger, CLASS_NAME, methodName, sql);

        //バルクインサートをするステートメントを準備
        pstmt = connection.prepareStatement(sql);
        Field field = null;
        Object objData = null;

        try
        {
            //************************************************************
            //エンティティオブジェクトを回してエンティティオブジェクトのインサートを実行
            //************************************************************

            for (T entityData : entityDataList)
            {

                //共通フィールドに値を入れる
                entityData.setReq(req);
                entityDaoUtils.prePersist(entityData);

                try
                {

                    //カラムリストからフィールドのデータを取得して
                    //ステートメントのパラメータ値にセットする
                    for (int i = 0; i < fieldList.size(); i++)
                    {
                        field = fieldList.get(i);
                        objData = field.get(entityData);
                        pstmt.setObject(i + 1, objData);
                    }
                    //1件分のレコードを登録
                    pstmt.addBatch();

                }
                catch (Exception e)
                {
                    String message = e.getMessage() + ERRLOG_FORMAT_FIELD
                            + (field != null ? field.toString() : "")
                            + ERRLOG_FORMAT_VALUE
                            + (objData != null ? objData.toString() : "");
                    logHelper.severe(this.logger, CLASS_NAME, methodName,
                            message);
                    throw new HibernateException(e);
                }
            }

            //************************************************************
            //  一括してSQL実行
            //************************************************************
            pstmt.executeBatch();

            long endTime = System.currentTimeMillis();

            logHelper.fine(this.logger, CLASS_NAME, methodName,
                    "Exec Bulk Insert table = " + tablename + " exectime = "
                            + (endTime - startTime) + " ms");

        }
        catch (Exception e)
        {
            logHelper.severe(this.logger, CLASS_NAME, methodName,
                    e.getMessage());
            throw new HibernateException(e);
        }
        finally
        {
            pstmt.close();
        }

        return null;
    }
    /**
     *  JDBCをじかに使って複数のエンティティオブジェクト（同じクラス）のデータを一括して
     *  Deleteする
     *
     * @param connection    コネクションオブジェクト
     * @param tablename     テーブル名
     * @param keyColumnName 削除で唯一のユニークキーとなるサロゲートキーのカラム名を指定
     * @param keyColumnField     keyColumnNameの対象フィールドオブジェクト
     * @param columnList    カラム名のリスト
     * @param entityDataList    エンティティオブジェクトのリスト
     * @return 将来用（今はnull）
     * @throws SQLException
     */
    @ErrorHandling
    private <T extends GnomesEntitiy> Object doBulkDelete(Connection connection,
            String tablename, String keyColumnName, Field keyColumnField,
            List<T> entityDataList) throws SQLException
    {

        PreparedStatement pstmt = null;
        long startTime = System.currentTimeMillis();

        final String methodName = "doBulkDelete";

        //別関数でInsertのSQL文を作る
        String sql = this.jdbcAccessUtil.makeDeleteSQLString(tablename,
                keyColumnName);

        logHelper.fine(this.logger, CLASS_NAME, methodName, sql);

        //バルクインサートをするステートメントを準備
        pstmt = connection.prepareStatement(sql);

        try
        {
            //************************************************************
            //エンティティオブジェクトを回してエンティティオブジェクトのDeleteを実行
            //************************************************************

            for (T entityData : entityDataList)
            {

                Object objData = null;

                try
                {
                    //カラムリストからフィールドのデータを取得して
                    //ステートメントのパラメータ値にセットする
                    objData = keyColumnField.get(entityData);
                    pstmt.setObject(1, objData);
                    //1件分のレコードを登録
                    pstmt.addBatch();

                }
                catch (Exception e)
                {
                    String message = e.getMessage() + ERRLOG_FORMAT_FIELD
                    		// 現状の呼び出しではnullだと事前に例外になるので常にtrueになる条件式だが、異なる呼び出しをされた場合に備えてこのままnullチェックの判定を残す
                            + (keyColumnField != null
                                    ? keyColumnField.toString()
                                    : "")
                            + ERRLOG_FORMAT_VALUE
                            + (objData != null ? objData.toString() : "");
                    logHelper.severe(this.logger, CLASS_NAME, methodName,
                            message);
                    throw new HibernateException(e);
                }
            }

            //************************************************************
            //  一括してSQL実行
            //************************************************************
            pstmt.executeBatch();

            long endTime = System.currentTimeMillis();

            logHelper.fine(this.logger, CLASS_NAME, methodName,
                    "Exec Bulk delete table = " + tablename + " exectime = "
                            + (endTime - startTime) + " ms");
        }
        catch (Exception e)
        {
            logHelper.severe(this.logger, CLASS_NAME, methodName,
                    e.getMessage());
            throw new HibernateException(e);
        }
        finally
        {
            pstmt.close();
        }
        return null;
    }
    /**
     * バルクインサートやデリーとされたテーブルの履歴を格納する
     *
     * @param connection    コネクションオブジェクト
     * @param tablename     テーブル名
     * @param revInfoList   レブ情報のエンティティのリスト
     * @param revType       履歴区分 【システム項目】履歴テーブルに記録される変更の種類。0:登録, 1:更新, 2:削除
     * @param fieldList     エンティティオブジェクトデータのフィールドオブジェクト
     * @param columnList    カラム名のリスト
     * @param entityDataList    エンティティオブジェクトのリスト
     * @return
     * @throws SQLException
     */
    private <T extends GnomesEntitiy> Object doBulkInsertHistory(
            Connection connection, String tablename,
            RevTypeOfHistorycalTable revType, List<Field> fieldList,
            List<String> columnList, List<T> entityDataList) throws SQLException
    {

        final String methodName = "doBulkInsertHistory";

        //レブ情報の発番をする insert into revinfo(revtstmp) values (?)
        ExecuteSimpleJDBCQueryNoResult(connection, SQLSTR_INSERT_REVINFO,
                System.currentTimeMillis());

        //レブ情報の最後の値を取り出す select MAX(rev) from revinfo
        ResultSet lastRev = ExecuteSimpleJDBCQuery(connection,
                SQLSTR_REVMAX_VALUEGET);

        long lastRevVal = -1;
        //登録されたRevの値をとる
        if (lastRev.next())
        {
            lastRevVal = lastRev.getLong(1);
        }

        lastRev.close();

        PreparedStatement pstmt = null;
        long startTime = System.currentTimeMillis();

        //カラム名にrevとrevTypeを付与する
        columnList.add(0, HISTORY_REVTYPE_COLUMNNAME); //revType
        columnList.add(0, HISTORY_REV_COLUMNNAME); //rev

        //別関数でInsertのSQL文を作る
        String sql = this.jdbcAccessUtil.makeInsertSQLString(tablename,
                columnList);

        logHelper.fine(this.logger, CLASS_NAME, methodName, sql);

        //バルクインサートをするステートメントを準備
        pstmt = connection.prepareStatement(sql);

        try
        {
            //************************************************************
            //エンティティオブジェクトを回してエンティティオブジェクトのインサートを実行
            //************************************************************

            for (T entityData : entityDataList)
            {

                //共通フィールドに値を入れる
                entityData.setReq(req);
                this.entityDaoUtils.prePersist(entityData);

                Field field = null;
                Object objData = null;

                try
                {

                    //カラムリストからフィールドのデータを取得して
                    //ステートメントのパラメータ値にセットする
                    int index = 1;

                    //revとrevtypeの値を追加
                    pstmt.setObject(index++, lastRevVal);
                    pstmt.setObject(index++, revType.value);

                    for (int i = 0; i < fieldList.size(); i++)
                    {
                        field = fieldList.get(i);
                        objData = field.get(entityData);
                        pstmt.setObject(index++, objData);
                    }
                    //1件分のレコードを登録
                    pstmt.addBatch();


                }
                catch (Exception e)
                {
                    String message = e.getMessage() + ERRLOG_FORMAT_FIELD
                            + (field != null ? field.toString() : "")
                            + ERRLOG_FORMAT_VALUE
                            + (objData != null ? objData.toString() : "");
                    logHelper.severe(this.logger, CLASS_NAME, methodName,
                            message);
                    throw new HibernateException(e);
                }
            }

            //************************************************************
            //  一括してSQL実行
            //************************************************************
            pstmt.executeBatch();

            long endTime = System.currentTimeMillis();

            logHelper.fine(this.logger, CLASS_NAME, methodName,
                    "Exec Bulk Insert historical table = " + tablename
                            + " exectime = " + (endTime - startTime) + " ms");
        }
        catch (Exception e)
        {
            logHelper.severe(this.logger, CLASS_NAME, methodName,
                    e.getMessage());
            throw new HibernateException(e);
        }
        finally
        {
            pstmt.close();
        }

        return null;

    }
    /**
     * シンプルなクエリの実行(Result付き)
     *
     * @param connection コネクション
     * @param sql       クエリ文字列
     * @param params    パラメータ
     * @return
     * @throws SQLException
     */
    private ResultSet ExecuteSimpleJDBCQuery(Connection connection, String sql,
            Object... params) throws SQLException
    {

        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        try
        {
            stmt = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                stmt.setObject(i, params[i]);
            }

            resultSet = stmt.executeQuery();
        }
        catch (Exception e)
        {
            this.logHelper.severe(this.logger, CLASS_NAME,
                    "ExecuteSimpleJDBCQuery", e.getMessage());
            throw e;
        }
        finally
        {
            if (!Objects.isNull(stmt))
            {
                stmt.close();
            }
        }

        return resultSet;
    }
    /**
     * シンプルなクエリの実行(返り値なし)
     *
     * @param connection コネクション
     * @param sql       クエリ文字列
     * @param params    パラメータ
     * @return
     * @throws SQLException
     */
    private void ExecuteSimpleJDBCQueryNoResult(Connection connection,
            String sql, Object... params) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            stmt = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();

        }
        catch (Exception e)
        {
            this.logHelper.severe(this.logger, CLASS_NAME,
                    "ExecuteSimpleJDBCQueryNoResult", e.getMessage());
            throw e;
        }
        finally
        {
            if (!Objects.isNull(stmt))
            {
                stmt.close();
            }
        }

        return;
    }
    /**
     *
     * 本クラスのローカルで共通な例外メッセージの情報を集めてGnomesAppExceptionを作成
     *
     * @param e                 元になる例外ハンドラ
     * @param messageNo         代表メッセージNo
     * @param childMessageNo    詳細メッセージNo
     * @param entityObj         情報の基になるエンティティ
     * @param tableName         情報の基になるクラス名
     * @return
     */
    private <T extends GnomesEntitiy> GnomesAppException CreteAppExceptionFromLocal(
            Throwable e, String messageNo, String childMessageNo, T entityObj,
            String tableName)
    {
        GnomesAppException ex = this.exceptionFactory
                .createGnomesAppException(e);
        ex.setMessageNo(messageNo);
        Object[] errParam = {entityObj.getClass().toString(), tableName};
        ex.setMessageParams(errParam);

        String causeMessage = "";
        if (e.getCause() != null)
        {
            causeMessage = e.getCause().getMessage();
        }

        Object[] childParam = {e.getMessage(), causeMessage, "", "", ""};
        List<MessageData> childMessageDatas = new ArrayList<>();
        childMessageDatas.add(new MessageData(childMessageNo, childParam));
        ex.setChildMessageDatas(childMessageDatas);
        return ex;
    }

}
