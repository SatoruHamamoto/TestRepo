package com.gnomes.common.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Parameter;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.envers.Audited;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.GnomesSystemModel;
import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.GnomesEntitiy;
import com.gnomes.common.entity.GnomesRevisionEntity;
import com.gnomes.common.entity.IContainerRequest;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.HistCertification;
import com.gnomes.uiservice.ContainerRequest;

/**
 * Dao 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/01 YJP/A.Oomori              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class BaseDao
{

    /** ロガー */
    @Inject
    protected transient Logger       logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper    logHelper;

    /** ContainerRequest */
    @Inject
    protected ContainerRequest       req;

    /** セッションビーン */
    @Inject
    protected GnomesSessionBean      gnomesSessionBean;

    /** システムビーン */
    @Inject
    protected GnomesSystemBean       gnomesSystemBean;

    /** exceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /** システムモデル */
    @Inject
    protected GnomesSystemModel      gnomesSystemModel;

    /**
     * DAOの中で共通なエンティティの操作や設定のユーティリティ関数群
     */
    @Inject
    protected EntityDaoUtils         entityDaoUtils;

    /**
     * デフォルトコンストラクタ
     */
    public BaseDao()
    {
    }

    /**
     * 登録.
     * @param em エンティティマネージャー
     * @param entity 登録対象エンティティ
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws GnomesAppException
     */
    protected void persist(EntityManager em, BaseEntity... entity)
    {

        if (Objects.nonNull(entity) && entity.length > 0)
        {

            for (int i = 0; i < entity.length; i++)
            {
                if (Objects.nonNull(entity[i]))
                {
                    // 登録者情報設定
                    // 20181004 upd start ----
                    //entity[i].setReq(this.req);
                    this.entityDaoUtils.setRequest(entity[i]);
                    // 20181004 upd end ----
                    em.persist(entity[i]);
                }

            }
            em.flush();

        }

    }

    /**
     * 更新.
     * @param em エンティティマネージャ
     * @param entity 更新対象エンティティ
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws GnomesAppException
     */
    protected void update(EntityManager em, BaseEntity... entity)
    {

        if (Objects.nonNull(entity) && entity.length > 0)
        {

            for (int i = 0; i < entity.length; i++)
            {
                if (Objects.nonNull(entity[i]))
                {
                    // 更新者情報設定
                    // 20181004 upd start ----
                    //entity[i].setReq(this.req);
                    this.entityDaoUtils.setRequest(entity[i]);
                    // 20181004 upd end ----
                }
            }
            em.flush();

        }

    }

    /**
     * エンティティの削除を行う。
     * 　引数の対象エンティティは複数渡せるが、親子関係を指定したい場合
     * 　必ず先頭に履歴テーブルが対応されているものを指定する。
     * 　複数指定しているエンティティの子の中に削除対象がある場合は
     * 　自動的に更新者情報を埋めてくれないので、事前に呼び出し元が設定するか
     * 　この関数を別々に呼ぶこと
     *
     * なお、履歴対象では一度updateをかけremoveするため、この関数のみflush()を自動実施する
     *
     * @param em エンティティマネージャ
     * @param entity 削除対象エンティティ
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws GnomesAppException
     */
    protected void remove(EntityManager em, BaseEntity... entity)
    {

        if (Objects.nonNull(entity) && entity.length > 0)
        {

            if (Objects.nonNull(entity[0]))
            {
                // 履歴登録対象の場合
                if (this.entityDaoUtils.historyRegisterCheck(entity[0]))
                {

                    for (int i = 0; i < entity.length; i++)
                    {
                        if (Objects.nonNull(entity[i]))
                        {
                            // 更新者情報を更新
                            entity[i].setLast_regist_event_id(
                                    this.req.getEventId());
                            // 20181004 upd start ----
                            //entity[i].setReq(this.req);
                            this.entityDaoUtils.setRequest(entity[i]);
                            // 20181004 upd end ----
                        }

                    }
                    em.flush();

                }

            }

            for (int i = 0; i < entity.length; i++)
            {
                if (Objects.nonNull(entity[i]))
                {
                    // 削除
                    em.remove(entity[i]);
                }
            }
            em.flush();

        }

    }

    /**
     * メッセージパラメータ作成(必須チェック)
     * @param columnName カラム名
     * @param value 値
     * @return メッセージパラメータ
     */
    protected String createMessageParamsRequired(String columnName,
            Object value)
    {

        StringBuilder messageParams = new StringBuilder();
        messageParams.append(columnName);
        messageParams.append(CommonConstants.COLON);
        messageParams.append(value);
        return messageParams.toString();

    }

    /**
     * クエリのパラメータ名、値を取得
     * @param query 取得するクエリ
     * @param nameList パラメータ名格納先
     * @param valueList 値格納先
     */
    private void getQueryParameterMessage(TypedQuery<?> query,
            List<String> nameList, List<String> valueList)
    {
        Set<Parameter<?>> parameters = query.getParameters();
        if (parameters != null)
        {
            for (Parameter<?> param : parameters)
            {
                nameList.add(param.getName());
                valueList.add(String.valueOf(query.getParameterValue(param)));
            }
        }
    }

    /**
     * 取得データの0件チェック
     * @param table 取得対象テーブル名<br>
     *                   テーブル[{0}]       例)[〇〇マスターテーブル（Mstr〇〇）, ××情報テーブル（Info××）]
     * @param queryName クエリ名
     * @param query クエリ
     * @param checkDatas 取得データ
     * @throws GnomesAppException 取得データが0件の場合
     */
    protected <T> void checkExistDataList(String table, String queryName,
            TypedQuery<T> query, List<T> checkDatas) throws GnomesAppException
    {

        if (checkDatas.size() == 0)
        {
            // データが取得できませんでした。
            // テーブル[{0}]       --〇〇マスターテーブル（Mstr〇〇）, ××情報テーブル（Info××）]
            // クエリ名[{1}]       --Xxxxxx]
            // パラメーター項目[{2}]      -- aaa, bbb, ccc, ddd]
            // パラメーター値[{3}]        --111, 222, 333, 444]

            List<String> pname = new ArrayList<>();
            List<String> pvalue = new ArrayList<>();

            this.getQueryParameterMessage(query, pname, pvalue);

            String paramNames = String.join(CommonConstants.COMMA, pname);
            String paramValues = String.join(CommonConstants.COMMA, pvalue);

            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0204, table, queryName,
                    paramNames, paramValues);
            throw ex;

        }
    }

    /**
     * 取得データの0件チェック（SingleのEntity用）
     * @param table 取得対象テーブル名<br>
     *                   テーブル[{0}]       例)[〇〇マスターテーブル（Mstr〇〇）, ××情報テーブル（Info××）]
     * @param queryName クエリ名
     * @param query クエリ
     * @param checkEntity 取得データ
     * @throws GnomesAppException 取得データが0件の場合
     */
    protected <T> void checkExistSingleData(String table, String queryName,
            TypedQuery<T> query, BaseEntity checkEntity)
            throws GnomesAppException
    {

        if (checkEntity == null)
        {
            this.checkExistDataList(table, queryName, query, new ArrayList<>());
        }
    }
}
