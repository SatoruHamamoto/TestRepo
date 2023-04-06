package com.gnomes.common.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.GnomesEntitiy;
import com.gnomes.common.entity.IContainerRequest;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.uiservice.ContainerRequest;

/**
 * EntityにカラムDAOの共通関数やユーティリティ群
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/09/04 10:08 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
@Dependent
public class EntityDaoUtils
{

    /** ロガー */
    @Inject
    protected transient Logger    logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** ContainerRequest */
    @Inject
    protected ContainerRequest    req;

    /**
     * ロガーに渡すクラス名（定数の方が速い）
     */
    private static final String   CLASS_NAME = "EntityDaoUtils";
    /**
     * バルクインサート用に共通フィールドを設定する
     * 通常PersistならばInterceptするのだが、こちらは特別に組み込む必要があるため。
     *
     * @param entity
     * @throws ParseException
     */
    public void prePersist(Object entity)
    {

        if (entity instanceof BaseEntity)
        {
            if (((BaseEntity) entity).getReq() == null)
            {
                //logHelper.fine(null, null, "prePersist: " + entity.getClass().toString() + ", NULL");
            }
            else
            {
                Date nowDate = new Date();
                //logHelper.fine(null, null,  "prePersist: " + entity.getClass().toString() + "," + ((BaseEntity)entity).getReq().getClass().toString());

                // 登録情報、更新情報を設定
                IContainerRequest conatainerRequest = ((BaseEntity) entity)
                        .getReq();
                // 登録イベントID
                ((BaseEntity) entity).setFirst_regist_event_id(
                        conatainerRequest.getEventId());
                // 登録従業員No
                ((BaseEntity) entity).setFirst_regist_user_number(
                        conatainerRequest.getUserId());
                // 登録従業員名
                ((BaseEntity) entity).setFirst_regist_user_name(
                        conatainerRequest.getUserName());
                // 登録日時
                ((BaseEntity) entity).setFirst_regist_datetime(nowDate);
                // 更新イベントID
                ((BaseEntity) entity).setLast_regist_event_id(
                        conatainerRequest.getEventId());
                // 更新従業員No
                ((BaseEntity) entity).setLast_regist_user_number(
                        conatainerRequest.getUserId());
                // 更新従業員名
                ((BaseEntity) entity).setLast_regist_user_name(
                        conatainerRequest.getUserName());
                // 更新日時
                ((BaseEntity) entity).setLast_regist_datetime(nowDate);
            }
        }
    }
    // 20181004 add start ----
    /**
     * ContainerRequestを再帰的に設定
     * @param entity 起点Entity
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public <T extends GnomesEntitiy> void setRequest(T entity)
    {

        final String methodName = "setRequest";

        entity.setReq(req);
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());

            PropertyDescriptor[] propertyDescriptors = beanInfo
                    .getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors)
            {

                Method getter = pd.getReadMethod();
                if (getter == null)
                {
                    continue;
                }
                if (getter.isAnnotationPresent(OneToMany.class))
                {
                    Set<GnomesEntitiy> collections = (Set<GnomesEntitiy>) getter
                            .invoke(entity, (Object[]) null);

                    if (collections != null)
                    {
                        for (GnomesEntitiy item : collections)
                        {
                            this.setRequest(item);
                        }
                    }
                }
                else if (getter.isAnnotationPresent(OneToOne.class))
                {
                    GnomesEntitiy item = (GnomesEntitiy) getter.invoke(entity,
                            (Object[]) null);
                    if (item != null)
                    {
                        this.setRequest(item);
                    }
                }
            }
        } catch (IntrospectionException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e)
        {
            logHelper.severe(logger, CLASS_NAME, methodName,
                    "setRequest Error Please check the Entity. "
                            + e.getMessage());
            throw new GnomesException(e);
        }
    }
    // 20181004 add end ----

    /**
     * 履歴登録対象チェック.
     * @param entity エンティティ
     * @return 履歴登録対象の場合、<code>true</code>を返却
     */
    public boolean historyRegisterCheck(BaseEntity entity)
    {

        Annotation[] annotations = entity.getClass().getAnnotations();

        for (int i = 0; i < annotations.length; i++)
        {

            // 履歴登録対象チェック
            if (annotations[i] instanceof Audited)
            {
                return true;

            }

        }

        return false;

    }
    /**
     * エンティティクラスの中のテーブル名を探して返す
     *
     * 例）public static final String TABLE_NAME = "hist_certification";
     *
     * @param entity    対象のエンティティ
     * @return
     */
    public <T extends GnomesEntitiy> String getTableNameFromEntity(T entity)
    {
        //エンティティからテーブル名を取得
        Table[] anoTables = entity.getClass()
                .getAnnotationsByType(javax.persistence.Table.class);
        String tablename = null;

        //************************************************************
        //  テーブル名を取得
        //************************************************************
        for (Table anoTable : anoTables)
        {
            if (anoTable instanceof Table)
            {
                tablename = anoTable.name();
                break;
            }
        }

        return tablename;
    }
}
