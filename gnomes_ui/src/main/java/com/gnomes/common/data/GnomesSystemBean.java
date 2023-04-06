package com.gnomes.common.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.system.data.PartsPrivilegeInfo;

import biz.grandsight.ex.rs.CGenReportMeta;

/**
 * アプリケーションビーン
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
public class GnomesSystemBean implements Serializable {


    @Inject
    private GnomesSystemModel gnomesSystemModel;

    /** 帳票印刷情報 */
    private CGenReportMeta cGenReportMeta;

    /** 帳票印刷情報（多重） */
    private biz.grandsight.ex.rs_multiple.CGenReportMeta cGenReportMultipleMeta;

    /** 帳票印刷情報（多重棚卸一覧） */
    private biz.grandsight.ex.rs_multiple21.CGenReportMeta cGenReportMultipleInventoryMeta;

    /** 帳票印刷情報（多段） */
    private biz.grandsight.ex.rs_multistage.CGenReportMeta cGenReportMultiStageMeta;

    /** 帳票印刷情報（多段改ページ無し） */
    private biz.grandsight.ex.rs_multistage41.CGenReportMeta cGenReportMultiStageNoNewPageMeta;

    /** 帳票印刷情報（多重多段） */
    private biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta cGenReportMultipleMultiStageMeta;

    //-------------------------------------------------------------------------
    //  保管領域用
    //-------------------------------------------------------------------------
    /** 帳票印刷情報 保管領域*/
    private CGenReportMeta cGenReportMetaStorage;

    /** 帳票印刷情報（多重）保管領域 */
    private biz.grandsight.ex.rs_multiple.CGenReportMeta cGenReportMultipleMetaStorage;

    /** 帳票印刷情報（多重棚卸一覧）保管領域 */
    private biz.grandsight.ex.rs_multiple21.CGenReportMeta cGenReportMultipleInventoryMetaStorage;

    /** 帳票印刷情報（多段）保管領域 */
    private biz.grandsight.ex.rs_multistage.CGenReportMeta cGenReportMultiStageMetaStorage;

    /** 帳票印刷情報（多段改ページ無し）保管領域 */
    private biz.grandsight.ex.rs_multistage41.CGenReportMeta cGenReportMultiStageNoNewPageMetaStorage;

    /** 帳票印刷情報（多重多段）保管領域 */
    private biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta cGenReportMultipleMultiStageMetaStorage;


    /** CONVERSION_TIME_OUT **/
    private long conversionTimeOut = CommonConstants.CONVERSATION_TIME_OUT_DEFAULT;

    /** 帳票印字処理で使用する定義ファイル名 */
    private String reportDefinitionXMLFileName;

    /** トレースログ要否 デフォルト値:FINEST */
    private boolean isTraceLog = true;

    /** パーツ権限情報 */
    private List<PartsPrivilegeInfo> partsPrivilegeInfoList = new ArrayList<PartsPrivilegeInfo>();

    /** モジュールオプション情報 */
    private Hashtable<String, String> moduleOptionInfo = null;

    /** ログイン認証用モジュールの種類 */
    private String loginModuleType = null;

    /** 共通コマンドデータ */
    private CommandDatas commandDatas;

    /** 入力項目ドメイン定義 */
    private GnomesInputDomain gnomesInputDomain;

    /** 秤量器IFクローズまでのタイムアウト時間  **/
    private long weighCloseTimeout;

    /** 秤量インジケータの表示間隔(ms) */
    private long cyclicWeighIntervalMiliSecond;
    
    /** 同期モード */
    private int isCyclicWeighSyncAccessMode;

    /**
     * トレースログ要否取得
     * @return トレースログ要否
     */
    public boolean isTraceLog() {
        return isTraceLog;
    }

    /**
     * トレースログ要否設定
     * <pre>
     * <code>true</code>を設定した場合、ロガーのログレベル：FINEST
     * </pre>
     * @param isTraceLog トレースログ要否
     */
    public void setTraceLog(boolean isTraceLog) {
        this.isTraceLog = isTraceLog;
    }

    /**
     * パーツ権限情報を取得
     * @return partsPrivilegeInfoList
     */
    public List<PartsPrivilegeInfo> getPartsPrivilegeInfo() {
        return partsPrivilegeInfoList;
    }

    /**
     * パーツ権限情報を設定
     * @param partsPrivilegeInfoList パーツ権限情報
     */
    public void setPartsPrivilegeInfo(List<PartsPrivilegeInfo> partsPrivilegeInfoList) {
        this.partsPrivilegeInfoList = partsPrivilegeInfoList;
    }

    /**
     * パーツ権限情報を追加
     * @param partsPrivilegeInfoList パーツ権限情報
     */
    public void addPartsPrivilegeInfo(List<PartsPrivilegeInfo> partsPrivilegeInfoList) {
        if (this.partsPrivilegeInfoList == null){
            this.partsPrivilegeInfoList = new  ArrayList<PartsPrivilegeInfo>();
        }

        for(PartsPrivilegeInfo partsPrivilegeInfo : partsPrivilegeInfoList){
            this.partsPrivilegeInfoList.add(partsPrivilegeInfo);
        }
    }

    /**
     * モジュールオプション情報を取得
     * @return moduleOptionInfoList
     */
    public Hashtable<String, String> getModuleOptionInfo() {
        return moduleOptionInfo;
    }

    /**
     * モジュールオプション情報を設定
     * @param moduleOptionInfoList モジュールオプション情報
     */
    public void setModuleOptionInfo(Hashtable<String, String> moduleOptionInfo) {
        this.moduleOptionInfo = moduleOptionInfo;
    }

    /**
     * ログイン認証用モジュールの種類
     * @return loginModuleType
     */
    public String getLoginModuleType() {
        return loginModuleType;
    }

    /**
     * ログイン認証用モジュールの種類を設定
     * @param loginModuleType ログイン認証用モジュールの種類
     */
    public void setLoginModuleType(String loginModuleType) {
        this.loginModuleType = loginModuleType;
    }

    /**
     * 共通コマンドデータの取得
     * @return commandDatas
     */
    public CommandDatas getCommandDatas() {
        return commandDatas;
    }

    /**
     * 共通コマンドデータの設定
     * @param commandDatas 共通コマンドデータ
     */
    public void setCommandDatas(CommandDatas commandDatas) {
        this.commandDatas = commandDatas;
    }

	/**
	 * 入力項目ドメイン定義を取得
	 * @return gnomesInputDomain
	 */
	public GnomesInputDomain getGnomesInputDomain() {
		return gnomesInputDomain;
	}

	/**
	 * 入力項目ドメイン定義を設定
	 * @param gnomesInputDomain 入力項目ドメイン定義
	 */
	public void setGnomesInputDomain(GnomesInputDomain gnomesInputDomain) {
		this.gnomesInputDomain = gnomesInputDomain;
	}

    /**
     * ドメインIDよりドメイン情報を取得
     * @param inputDomainId
     * @return
     */
    public InputDomain getInputDomain(String inputDomainId) {

    	InputDomain resultInputDomain = null;
		String[] values = inputDomainId.split(":");
		if (values.length == 2 && !StringUtil.isNullOrEmpty(values[1])) {
			String domainId = values[1];
			List<InputDomain> inputDomainList = this.gnomesInputDomain.getInputDomainList();
			for (InputDomain inputDomain: inputDomainList){
				if (inputDomain.getId().equals(domainId)) {
					resultInputDomain = inputDomain;
					break;
				}

			}

		}
		return resultInputDomain;

    }

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
    }

    public GnomesSystemModel getGnomesSystemModel() {
        return gnomesSystemModel;
    }

    public void setGnomesSystemModel(GnomesSystemModel gnomesSystemModel) {
        this.gnomesSystemModel = gnomesSystemModel;
    }

    /**
     * 帳票印刷情報を取得
     * @return 帳票印刷情報
     */
    public CGenReportMeta getcGenReportMeta() {
        return cGenReportMeta;
    }

    /**
     * 帳票印刷情報を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMeta(CGenReportMeta cGenReportMeta) {
        this.cGenReportMeta = cGenReportMeta;
    }

    /**
     * 帳票印刷情報（多重）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multiple.CGenReportMeta getcGenReportMultipleMeta() {
        return cGenReportMultipleMeta;
    }

    /**
     * 帳票印刷情報（多重）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultipleMeta(biz.grandsight.ex.rs_multiple.CGenReportMeta cGenReportMultipleMeta) {
        this.cGenReportMultipleMeta = cGenReportMultipleMeta;
    }

    /**
     * 帳票印刷情報（多段）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multistage.CGenReportMeta getcGenReportMultiStageMeta() {
        return cGenReportMultiStageMeta;
    }

    /**
     * 帳票印刷情報（多段）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultiStageMeta(biz.grandsight.ex.rs_multistage.CGenReportMeta cGenReportMultiStageMeta) {
        this.cGenReportMultiStageMeta = cGenReportMultiStageMeta;
    }

    /**
     * 帳票印刷情報（多段改ページ無し）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multistage41.CGenReportMeta getcGenReportMultiStageNoNewPageMeta() {
        return cGenReportMultiStageNoNewPageMeta;
    }

    /**
     * 帳票印刷情報（多段改ページ無し）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultiStageNoNewPageMeta(biz.grandsight.ex.rs_multistage41.CGenReportMeta cGenReportMultiStageNoNewPageMeta) {
        this.cGenReportMultiStageNoNewPageMeta = cGenReportMultiStageNoNewPageMeta;
    }

    /**
     * 帳票印刷情報（多重多段）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta getcGenReportMultipleMultiStageMeta() {
        return cGenReportMultipleMultiStageMeta;
    }

    /**
     * 帳票印刷情報（多重多段）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultipleMultiStageMeta(biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta cGenReportMultipleMultiStageMeta) {
        this.cGenReportMultipleMultiStageMeta = cGenReportMultipleMultiStageMeta;
    }

    /**
     * 帳票印刷情報（多重棚卸一覧）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multiple21.CGenReportMeta getcGenReportMultipleInventoryMeta() {
        return cGenReportMultipleInventoryMeta;
    }

    /**
     * 帳票印刷情報（多重棚卸一覧）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultipleInventoryMeta(biz.grandsight.ex.rs_multiple21.CGenReportMeta cGenReportMultipleInventoryMeta) {
        this.cGenReportMultipleInventoryMeta = cGenReportMultipleInventoryMeta;
    }

    //-------------------------------------------------------------------------
    // 保管領域用
    //-------------------------------------------------------------------------
    /**
     * 帳票印刷情報（保管領域用）を取得
     * @return 帳票印刷情報
     */
    public CGenReportMeta getcGenReportMetaStorage() {
        return cGenReportMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMetaStorage(CGenReportMeta cGenReportMetaStorage) {
        this.cGenReportMetaStorage = cGenReportMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多重）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multiple.CGenReportMeta getcGenReportMultipleMetaStorage() {
        return cGenReportMultipleMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多重）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultipleMetaStorage(biz.grandsight.ex.rs_multiple.CGenReportMeta cGenReportMultipleMetaStorage) {
        this.cGenReportMultipleMetaStorage = cGenReportMultipleMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多段）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multistage.CGenReportMeta getcGenReportMultiStageMetaStorage() {
        return cGenReportMultiStageMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多段）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultiStageMetaStorage(biz.grandsight.ex.rs_multistage.CGenReportMeta cGenReportMultiStageMetaStorage) {
        this.cGenReportMultiStageMetaStorage = cGenReportMultiStageMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多段改ページ無し）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multistage41.CGenReportMeta getcGenReportMultiStageNoNewPageMetaStorage() {
        return cGenReportMultiStageNoNewPageMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多段改ページ無し）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultiStageNoNewPageMetaStorage(biz.grandsight.ex.rs_multistage41.CGenReportMeta cGenReportMultiStageNoNewPageMetaStorage) {
        this.cGenReportMultiStageNoNewPageMetaStorage = cGenReportMultiStageNoNewPageMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多重多段）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta getcGenReportMultipleMultiStageMetaStorage() {
        return cGenReportMultipleMultiStageMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多重多段）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultipleMultiStageMetaStorage(biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta cGenReportMultipleMultiStageMetaStorage) {
        this.cGenReportMultipleMultiStageMetaStorage = cGenReportMultipleMultiStageMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多重棚卸一覧）を取得
     * @return 帳票印刷情報
     */
    public biz.grandsight.ex.rs_multiple21.CGenReportMeta getcGenReportMultipleInventoryMetaStorage() {
        return cGenReportMultipleInventoryMetaStorage;
    }

    /**
     * 帳票印刷情報（保管領域用）（多重棚卸一覧）を設定
     * @param cGenReportMeta 帳票印刷情報
     */
    public void setcGenReportMultipleInventoryMetaStorage(biz.grandsight.ex.rs_multiple21.CGenReportMeta cGenReportMultipleInventoryMetaStorage) {
        this.cGenReportMultipleInventoryMetaStorage = cGenReportMultipleInventoryMetaStorage;
    }

    /**
     * 帳票印字処理で使用する定義ファイル名を取得
     * @return 帳票印字処理で使用する定義ファイル名
     */
    public String getReportDefinitionXMLFileName() {
        return reportDefinitionXMLFileName;
    }

    /**
     * 帳票印字処理で使用する定義ファイル名を設定
     * @param reportDefinitionXMLFileName 帳票印字処理で使用する定義ファイル名
     */
    public void setReportDefinitionXMLFileName(String reportDefinitionXMLFileName) {
        this.reportDefinitionXMLFileName = reportDefinitionXMLFileName;
    }

    /**
     *
     * ConversionScopeのオブジェクトのタイムアウト値を取得
     *
     * @return デフォルトは60*60（１時間）
     */
    public long getConversionTimeOut() {
        return conversionTimeOut;
    }

    /**
     * ConversionScopeのオブジェクトのタイムアウト値を設定
     *
     * @param conversionTimeOut
     */
    public void setConversionTimeOut(long conversionTimeOut) {
        this.conversionTimeOut = conversionTimeOut;
    }

    /**
     *
     * 秤量器IFクローズまでのタイムアウト時間を取得
     *
     * @return weighCloseTimeout
     */
    public long getWeighCloseTimeout() {
    	return weighCloseTimeout;
    }

    /**
     * 秤量器IFクローズまでのタイムアウト時間を設定
     *
     * @param weighCloseTimeout
     */
    public void setWeighCloseTimeout(long weighCloseTimeout) {
    	this.weighCloseTimeout = weighCloseTimeout;
    }
    /**
     * 秤量インジケータの表示間隔(ms)
     * @return cyclicWeighIntervalMiliSecond
     */
    public long getCyclicWeighIntervalMiliSecond()
    {
        return cyclicWeighIntervalMiliSecond;
    }

    /**
     * 秤量インジケータの表示間隔(ms)
     * @param 秤量インジケータの表示間隔(ms) セットする cyclicWeighIntervalMiliSecond
     */
    public void setCyclicWeighIntervalMiliSecond(long cyclicWeighIntervalMiliSecond)
    {
        this.cyclicWeighIntervalMiliSecond = cyclicWeighIntervalMiliSecond;
    }

    /**
     * 同期モードを取得
     * @return 同期モード
     */
    public int getIsCyclicWeighSyncAccessMode() {
        return isCyclicWeighSyncAccessMode;
    }

    /**
     * 同期モードを設定
     * @param isCyclicWeighSyncAccessMode 同期モード
     */
    public void setIsCyclicWeighSyncAccessMode(int isCyclicWeighSyncAccessMode) {
        this.isCyclicWeighSyncAccessMode = isCyclicWeighSyncAccessMode;
    }

}
