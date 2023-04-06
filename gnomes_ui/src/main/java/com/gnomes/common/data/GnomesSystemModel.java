package com.gnomes.common.data;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.MstrDataCacheGroup;
import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.equipment.entity.MstrEquipment;
import com.gnomes.equipment.entity.MstrEquipmentIf;
import com.gnomes.equipment.entity.MstrEquipmentIfParameter;
import com.gnomes.equipment.entity.MstrEquipmentParameter;
import com.gnomes.external.entity.MstrExternalIfDataDefine;
import com.gnomes.external.entity.MstrExternalIfFileDefine;
import com.gnomes.external.entity.MstrExternalIfFormatDefine;
import com.gnomes.external.entity.MstrExternalIfSystemDefine;
import com.gnomes.system.entity.MstrComputer;
import com.gnomes.system.entity.MstrInvalidPasswd;
import com.gnomes.system.entity.MstrLink;
import com.gnomes.system.entity.MstrMessageDefine;
import com.gnomes.system.entity.MstrMessageGroup;
import com.gnomes.system.entity.MstrPatlamp;
import com.gnomes.system.entity.MstrPatlampModel;
import com.gnomes.system.entity.MstrPersonSecPolicy;
import com.gnomes.system.entity.MstrPrinter;
import com.gnomes.system.entity.MstrScreenButton;
import com.gnomes.system.entity.MstrScreenTableTag;
import com.gnomes.system.entity.MstrScreenTransition;
import com.gnomes.system.entity.MstrSite;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * アプリケーションシステムモデル
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/20 YJP/H.Gojo                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named
@ApplicationScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class GnomesSystemModel implements Serializable
{

    /** エンティティマネージャー(通常領域固定) */
    @PersistenceContext(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    protected transient EntityManager                                            em;

    /** ロガー */
    @Inject
    protected Logger                                                             logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper                                                          logHelper;

    /** exceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory                                             exceptionFactory;

    /** キャッシュグループマップ */
    protected Map<String, List<String>>                                          cashGroupInfoMap   = null;

    /** マスタ定義グループエンティティマップ */
    protected ConcurrentHashMap<String, Map<String, List<? extends BaseEntity>>> cashGroupEntityMap = null;

    /**
     * 全マスタ定義情報読込み
     * @throws GnomesAppException
     */
    public void readAllMstrEntity() throws GnomesAppException
    {
        // キャッシュグループマップ作成
        this.createCashGroupMap();
        // 全マスタ定義情報読込み
        this.readAllCashInfo();

    }

    /**
     * キャッシュグループリセット.
     * マスタデータキャッシュグループが空欄、NULLの場合は全リセットします。
     * <pre>
     * キャッシュグループに属するマスタ定義情報の初期化を行う。
     * </pre>
     * @param cacheGroup マスタデータキャッシュグループ(空欄、NULLの場合は全リセット)
     * @throws GnomesAppException
     */
    public void resetCashGroup(String cacheGroup) throws GnomesAppException {

        //キャッシュグループがnullか空文字だったら全部リセットする
        if(StringUtil.isNullOrEmpty(cacheGroup)){
            this.cashGroupEntityMap.clear();
            return;
        }

        // キャッシュグループ存在チェック
        if (this.cashGroupEntityMap.containsKey(cacheGroup)) {
            this.cashGroupEntityMap.compute(cacheGroup, (k, v) -> null);
        } else {
            // キャッシュグループが存在しません。（値：{0}）
            throw this.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0175, cacheGroup);
        }
    }

    /**
     * サイトマスタを取得
     * @return mstrSiteList
     * @throws GnomesAppException
     */
    public List<MstrSite> getMstrSiteList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrSite.class);
    }

    /**
     * 端末定義を取得
     * @return mstrComputerList
     * @throws GnomesAppException
     */
    public List<MstrComputer> getMstrComputerList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrComputer.class);
    }

    /**
     * パスワード禁止文字を取得
     * @return mstrInvalidPasswdList
     * @throws GnomesAppException
     */
    public List<MstrInvalidPasswd> getMstrInvalidPasswdList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrInvalidPasswd.class);
    }

    /**
     * システム定義を取得
     * @return mstrSystemConstantList
     * @throws GnomesAppException
     */
    public List<MstrSystemDefine> getMstrSystemDefineList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrSystemDefine.class);
    }

    /**
     * ユーザアカウントセキュリティポリシーを取得
     * @return mstrPersonSecPolicyList
     * @throws GnomesAppException
     */
    public List<MstrPersonSecPolicy> getMstrPersonSecPolicyList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrPersonSecPolicy.class);
    }

    /**
     * メッセージ定義を取得
     * @return mstrMessageDefineList
     * @throws GnomesAppException
     */
    public List<MstrMessageDefine> getMstrMessageDefineList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrMessageDefine.class);
    }

    /**
     * リンク情報を取得
     * @return mstrLinkList
     * @throws GnomesAppException
     */
    public List<MstrLink> getMstrLinkList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrLink.class);
    }

    /**
     * 外部I/Fシステム定義マスタを取得
     * @return externalIfDefineList
     * @throws GnomesAppException
     */
    public List<MstrExternalIfSystemDefine> getMstrExternalIfSystemDefineList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrExternalIfSystemDefine.class);
    }

    /**
     * 外部I/Fファイル構成定義マスタを取得
     * @return mstrExternalIfFileDefine
     * @throws GnomesAppException
     */
    public List<MstrExternalIfFileDefine> getMstrExternalIfFileDefineList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrExternalIfFileDefine.class);
    }

    /**
     * 外部I/Fデータ項目定義マスタを取得
     * @return mstrExternalIfDataDefineList
     * @throws GnomesAppException
     */
    public List<MstrExternalIfDataDefine> getMstrExternalIfDataDefineList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrExternalIfDataDefine.class);
    }

    /**
     * 外部I/Fフォーマット定義マスタを取得
     * @return 外部I/Fフォーマット定義マスタ
     * @throws GnomesAppException
     */
    public List<MstrExternalIfFormatDefine> getMstrExternalIfFormatDefineList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrExternalIfFormatDefine.class);
    }

    /**
     * プリンタマスタを取得
     * @return mstrPrinterList
     * @throws GnomesAppException
     */
    public List<MstrPrinter> getMstrPrinterList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrPrinter.class);
    }

    /**
     * メッセージグループマスタリスト取得
     * @return メッセージグループマスタリスト
     * @throws GnomesAppException
     */
    public List<MstrMessageGroup> getMstrMessageGroupList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrMessageGroup.class);
    }

    /**
     * 画面遷移情報マスタリスト取得
     * @return 画面遷移情報マスタリストリスト
     * @throws GnomesAppException
     */
    public List<MstrScreenTransition> getMstrScreenTransitionList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrScreenTransition.class);
    }

    /**
     * 設備マスタ エンティティリストを取得
     * @return 設備マスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrEquipment> getMstrEquipmentList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrEquipment.class);
    }

    /**
     * 設備I/Fマスタ エンティティリストを取得
     * @return 設備I/Fマスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrEquipmentIf> getMstrEquipmentIfList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrEquipmentIf.class);
    }

    /**
     * 設備パラメータマスタ エンティティリストを取得
     * @return 設備パラメータマスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrEquipmentParameter> getMstrEquipmentParameterList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrEquipmentParameter.class);
    }

    /**
     * 設備I/Fパラメータマスタ エンティティリストを設定
     * @return 設備I/Fパラメータマスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrEquipmentIfParameter> getMstrEquipmentIfParameterList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrEquipmentIfParameter.class);
    }

    /**
     * パトランプマスタ エンティティリストを設定
     * @return パトランプマスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrPatlamp> getMstrPatlampList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrPatlamp.class);
    }

    /**
     * パトランプ設備マスタ エンティティリストを設定
     * @return パトランプ設備マスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrPatlampModel> getMstrPatlampModelList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrPatlampModel.class);
    }

    /**
     * 画面ボタンマスタ エンティティリストを設定
     * @return 画面ボタンマスタ エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrScreenButton> getMstrScreenButtonList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrScreenButton.class);
    }

    /**
     * 画面テーブル設定マスタ  エンティティリストを設定
     * @return 画面テーブル設定マスタ  エンティティリスト
     * @throws GnomesAppException
     */
    public List<MstrScreenTableTag> getMstrScreenTableTagList() throws GnomesAppException
    {
        return this.getCashGroupEntityList(MstrScreenTableTag.class);
    }

    /**
     * キャッシュグループマップ作成.
     * <pre>
     * キャッシュグループマップ情報の作成を行う。
     * </pre>
     */
    protected void createCashGroupMap()
    {

        if (Objects.isNull(this.cashGroupInfoMap)) {
            this.cashGroupInfoMap = new HashMap<String, List<String>>();
        }

        for (MstrDataCacheGroup cacheGroup : MstrDataCacheGroup.values()) {

            switch (cacheGroup) {

                case PERSON_INFO :
                    // ユーザ情報
                    this.cashGroupInfoMap.put(MstrDataCacheGroup.PERSON_INFO.getValue(), this.getPersonGroupInfo());
                    break;
                case EXTERNALIF_INFO :
                    // 外部IF情報
                    this.cashGroupInfoMap.put(MstrDataCacheGroup.EXTERNALIF_INFO.getValue(),
                            this.getExternalIfGroupInfo());
                    break;
                case OTHER :
                    // その他
                    this.cashGroupInfoMap.put(MstrDataCacheGroup.OTHER.getValue(), this.getOtherGroupInfo());
                    break;
                default :
                    break;
            }

        }

    }

    /**
     * 全マスタ定義情報読込み.
     * @throws GnomesAppException
     */
    protected void readAllCashInfo() throws GnomesAppException
    {

        if (Objects.isNull(this.cashGroupEntityMap)) {
            this.cashGroupEntityMap = new ConcurrentHashMap<>();
        }

        for (MstrDataCacheGroup cacheGroup : MstrDataCacheGroup.values()) {

            if (this.cashGroupInfoMap.containsKey(cacheGroup.getValue())) {

                this.cashGroupEntityMap.put(cacheGroup.getValue(), this.createCashGroupEntity(cacheGroup.getValue()));

            }
            else {
                // キャッシュグループ情報が作成されていません。（キャッシュグループ：{0}）
                throw this.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0173,
                        cacheGroup.getValue());
            }

        }

    }

    /**
     * キャッシュグループエンティティ情報取得.
     * <pre>
     * キャッシュグループに属するエンティティ情報の取得を行う。
     * </pre>
     * @param cacheGroup キャッシュグループ
     * @return キャッシュグループエンティティ情報マップ
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    protected Map<String, List<? extends BaseEntity>> createCashGroupEntity(String cacheGroup)
    {

        Map<String, List<? extends BaseEntity>> newGroupEntityMap = new HashMap<>();

        for (String entityName : this.cashGroupInfoMap.get(cacheGroup)) {

            try {
                Class<?> c = Class.forName(entityName);

                List<? extends BaseEntity> entityList = (List<? extends BaseEntity>) this.em.createNamedQuery(
                        c.getSimpleName() + CommonConstants.FIND_ALL, c).getResultList();

                newGroupEntityMap.put(entityName, entityList);

                this.outputLog(c, entityList.size());

            }
            catch (ClassNotFoundException e) {
                this.logHelper.severe(this.logger, null, null, e.toString());
            }

        }
        return newGroupEntityMap;

    }

    /**
     * エンティティリスト取得.
     * <pre>
     * 取得対象エンティティクラスをもとに、エンティティリストの取得を行う。
     * 取得対象エンティティに属するグループより取得を行い、データが存在しない場合、
     * グループに含まれるエンティティリストを取得する。
     * </pre>
     * @param entityClass 取得対象エンティティクラス
     * @return エンティティリスト
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity> List<T> getCashGroupEntityList(Class<T> entityClass) throws GnomesAppException
    {

        // キャッシュグループ取得
        String cacheGroup = this.getCacheGroup(entityClass);

        // グループが未キャシュの場合は、キャッシュする
        Map<String, List<? extends BaseEntity>> groupEntitys = this.cashGroupEntityMap.computeIfAbsent(cacheGroup, (
                key) -> this.createCashGroupEntity(cacheGroup));

        return (List<T>) groupEntitys.get(entityClass.getName());

    }

    /**
     * キャッシュグループ取得.
     * <pre>
     * 指定されたクラスのキャッシュグループを取得する。
     * </pre>
     * @param clazz クラス
     * @return キャッシュグループ
     * @throws GnomesAppException
     */
    private String getCacheGroup(Class<?> entityClass) throws GnomesAppException
    {

        for (Map.Entry<String, List<String>> entry : this.cashGroupInfoMap.entrySet()) {

            List<String> cashGroupList = entry.getValue();

            if (!Objects.isNull(cashGroupList)) {
                if (cashGroupList.contains(entityClass.getName())) {
                    return entry.getKey();
                }

            }

        }
        // キャッシュグループにクラスが存在しません。（クラス名：{0}）
        throw this.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0174,
                entityClass.getName());

    }

    /**
     * ユーザグループ情報取得
     * @return ユーザグループ情報
     */
    private List<String> getPersonGroupInfo()
    {

        List<String> list = new ArrayList<>();
        // ユーザアカウントセキュリティポリシーマスタ
        list.add(MstrPersonSecPolicy.class.getName());

        return list;

    }

    /**
     * 外部IFグループ情報取得
     * @return 外部IFグループ情報
     */
    private List<String> getExternalIfGroupInfo()
    {

        List<String> list = new ArrayList<>();
        // 外部I/Fデータ項目定義マスタ
        list.add(MstrExternalIfDataDefine.class.getName());
        // 外部I/Fファイル構成定義マスタ
        list.add(MstrExternalIfFileDefine.class.getName());
        // 外部I/Fフォーマット定義マスタ
        list.add(MstrExternalIfFormatDefine.class.getName());
        // 外部I/Fシステム定義マスタ
        list.add(MstrExternalIfSystemDefine.class.getName());

        return list;

    }

    /**
     * その他グループ情報取得
     * @return その他グループ情報
     */
    private List<String> getOtherGroupInfo()
    {

        List<String> list = new ArrayList<>();
        // 端末定義
        list.add(MstrComputer.class.getName());
        // 設備マスタ
        list.add(MstrEquipment.class.getName());
        // 設備I/Fマスタ
        list.add(MstrEquipmentIf.class.getName());
        // 設備I/Fパラメータマスタ
        list.add(MstrEquipmentIfParameter.class.getName());
        // 設備パラメータマスタ
        list.add(MstrEquipmentParameter.class.getName());
        // パスワード禁止文字
        list.add(MstrInvalidPasswd.class.getName());
        // リンク情報
        list.add(MstrLink.class.getName());
        // メッセージ定義
        list.add(MstrMessageDefine.class.getName());
        // メッセージグループマスタ
        list.add(MstrMessageGroup.class.getName());
        // プリンタマスタ
        list.add(MstrPrinter.class.getName());
        // 画面遷移情報マスタ
        list.add(MstrScreenTransition.class.getName());
        // サイトマスタ
        list.add(MstrSite.class.getName());
        // システム定義
        list.add(MstrSystemDefine.class.getName());
        // パトランプマスタ
        list.add(MstrPatlamp.class.getName());
        // パトランプ設備マスタ
        list.add(MstrPatlampModel.class.getName());
        // 画面ボタンマスタ
        list.add(MstrScreenButton.class.getName());
        // 画面テーブル設定マスタ
        list.add(MstrScreenTableTag.class.getName());

        return list;

    }

    /**
     * マスタデータ件数ログ出力
     * @param entityClass エンティティクラス
     * @param size データ件数
     */
    private void outputLog(Class<?> entityClass, int size)
    {

        Annotation[] annotations = entityClass.getAnnotations();

        String tableName = "";

        for (int i = 0; i < annotations.length; i++) {
            // テーブル名取得
            if (annotations[i] instanceof Table) {
                tableName = ((Table) annotations[i]).name();
            }
        }

        StringBuilder message = new StringBuilder();
        message.append(" table name");
        message.append(CommonConstants.COLON);
        message.append(tableName);
        message.append(" record count");
        message.append(CommonConstants.COLON);
        message.append(size);
        this.logHelper.debug(this.logger, null, null, message.toString());

    }

    /**
     * 初期化処理
     * @throws GnomesAppException
     */
    public void initializeProc() throws GnomesAppException
    {
        // 処理無し

    }

}
