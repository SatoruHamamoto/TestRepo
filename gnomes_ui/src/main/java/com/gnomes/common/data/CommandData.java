package com.gnomes.common.data;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class CommandData {

    /** コマンドID */
    @XmlAttribute
    private String commandId;

    /** コマンド 限定子 */
    @XmlAttribute
    private String commandQualifier;

    /** 画面ID */
    private String screenId;

    /** トランザクション有無 */
    private boolean isTransactional = true;

    /** FunctionBeanの初期化実行有無 */
    private String isFunctionBeanBegin;

    /** FunctionBeanの終了実行有無 */
    private String isFunctionBeanEnd;

    /** デフォルトフォワード先 */
    private String defaultForward;

    /** ページトークンチェックフラグ */
    private boolean tokenCheckFlg = true;

    /** ページトークン更新フラグ */
    private boolean tokenUpdateFlg = true;

    /** 編集有フラグを0に戻さないフラグ*/
    private boolean editFlag = true;

    /** フォワード先コマンド */
    private String forwardCommandId;

    /** 画面遷移タイプ 0:フォワード 1:リダイレクト（cidなし）FormBeanのプロパティ名:リダイレクト（cidあり cidは設定されているFormBeanのプロパティ名より取得） */
    private String screenTransitionType;

    /** 引継パラメータマッピング情報 */
    @XmlElementWrapper
    private Map<String, String> mappingTransitionParameter;

    /** モデル実行クラス.メソッド名 */
    private String doModelClassMethod;

    /** businessBeanクラス名 */
    private String businessBeanName;

    /** スクロール位置保持有無 */
    private boolean keepScrollPosition = false;

    /** マッピング FunctionBean→businessBean */
    @XmlElementWrapper
    private Map<String, String> mappingBusinessBeanFromFunction;

    /** マッピング businessBean→FunctionBean */
    @XmlElementWrapper
    private Map<String, String> mappingFunctionBeanFromBusiness;

    /** ビジネスロジック実行クラス.メソッド名 */
    private String doBusinessClassMethod;

    /** 表示データ取得実行クラス.メソッド名 */
    private String doGetDispDataClassMethod;

    /** 検証対象のリソースIDリスト */
    @XmlElementWrapper
    @XmlElement(name = "resouceId")
    private List<String> checkList;

    /** 初期表示フラグ */
    private boolean initCommandFlag = false;

    /** FormBeanリストアマッピング情報 FunctionBean→FormBean */
    @XmlElementWrapper
    private Map<String, String> mappingFormBeanRestore;

    /** マッピング FormBean→FunctionBean */
    @XmlElementWrapper
    private Map<String, String> mappingFunctionBeanFromForm;

    /** マッピング FunctionBean→Bean */
    @XmlElementWrapper
    private Map<String, String> mappingBeanFromFunction;


    /** サービスformBeanクラス名 */
    private String serviceFormBeanName;

    /** サービスfunctionBeanクラス名 */
    private String serviceFunctionBeanName;

    /** サービスresponseBeanクラス名 */
    private String serviceResponseBeanName;

    /** FunctionBean受渡テーブルIDリスト */
    @XmlElementWrapper
    @XmlElement(name = "tableId")
    private List<String> tableIdList;

    /**
     * @return commandId
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * @param commandId セットする commandId
     */
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    /**
     * @return defaultForward
     */
    public String getDefaultForward() {
        return defaultForward;
    }

    /**
     * @param defaultForward セットする defaultForward
     */
    public void setDefaultForward(String defaultForward) {
        this.defaultForward = defaultForward;
    }

    /**
     * @return checkList
     */
    public List<String> getCheckList() {
        return checkList;
    }

    /**
     * @param checkList セットする checkList
     */
    public void setCheckList(List<String> checkList) {
        this.checkList = checkList;
    }

    /**
     * @return commandQualifier
     */
    public String getCommandQualifier() {
        return commandQualifier;
    }

    /**
     * @param commandQualifiere セットする commandQualifier
     */
    public void setCommandQualifier(String commandQualifier) {
        this.commandQualifier = commandQualifier;
    }

    /**
	 * @return isFunctionBeanBegin
	 */
	public String isFunctionBeanBegin() {
		return isFunctionBeanBegin;
	}

	/**
	 * @param isFunctionBeanBegin セットする isFunctionBeanBegin
	 */
	public void setFunctionBeanBegin(String isFunctionBeanBegin) {
		this.isFunctionBeanBegin = isFunctionBeanBegin;
	}

	/**
     * @return mappingFunctionBeanFromForm
     */
    public Map<String, String> getMappingFunctionBeanFromForm() {
        return mappingFunctionBeanFromForm;
    }

    /**
     * @param mappingFunctionBeanFromForm セットする mappingFunctionBeanFromForm
     */
    public void setMappingFunctionBeanFromForm(
            Map<String, String> mappingFunctionBeanFromForm) {
        this.mappingFunctionBeanFromForm = mappingFunctionBeanFromForm;
    }


    /**
     * @return mappingBeanFromFunction
     */
    public Map<String, String> getMappingBeanFromFunction() {
        return mappingBeanFromFunction;
    }

    /**
     * @param mappingBeanFromFunction セットする mappingBeanFromFunction
     */
    public void setMappingBeanFromFunction(
            Map<String, String> mappingBeanFromFunction) {
        this.mappingBeanFromFunction = mappingBeanFromFunction;
    }

    /**
     * @return forwardCommandId
     */
    public String getForwardCommandId() {
        return forwardCommandId;
    }

    /**
     * @param forwardCommandId セットする forwardCommandId
     */
    public void setForwardCommandId(String forwardCommandId) {
        this.forwardCommandId = forwardCommandId;
    }

	/**
	 * @return isFunctionBeanEnd
	 */
	public String isFunctionBeanEnd() {
		return isFunctionBeanEnd;
	}

	/**
	 * @param isFunctionBeanEnd セットする isFunctionBeanEnd
	 */
	public void setFunctionBeanEnd(String isFunctionBeanEnd) {
		this.isFunctionBeanEnd = isFunctionBeanEnd;
	}

	/**
	 * @return screenTransitionType
	 */
	public String getScreenTransitionType() {
		return screenTransitionType;
	}

	/**
	 * @param screenTransitionType セットする screenTransitionType
	 */
	public void setScreenTransitionType(String screenTransitionType) {
		this.screenTransitionType = screenTransitionType;
	}

	/**
	 * @return isTransactional
	 */
	public boolean isTransactional() {
		return isTransactional;
	}

	/**
	 * @param isTransactional セットする isTransactional
	 */
	public void setTransactional(boolean isTransactional) {
		this.isTransactional = isTransactional;
	}

	/**
	 * @return screenId
	 */
	public String getScreenId() {
		return screenId;
	}

	/**
	 * @param screenId セットする screenId
	 */
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	/**
	 * @return mappingTransitionParameter
	 */
	public Map<String, String> getMappingTransitionParameter() {
		return mappingTransitionParameter;
	}

	/**
	 * @param mappingTransitionParameter セットする mappingTransitionParameter
	 */
	public void setMappingTransitionParameter(Map<String, String> mappingTransitionParameter) {
		this.mappingTransitionParameter = mappingTransitionParameter;
	}

    /**
     * @return serviceFormBeanName
     */
    public String getServiceFormBeanName() {
        return serviceFormBeanName;
    }

    /**
     * @param serviceFormBeanName セットする serviceFormBeanName
     */
    public void setServiceFormBeanName(String serviceFormBeanName) {
        this.serviceFormBeanName = serviceFormBeanName;
    }

    /**
     * @return serviceFunctionBeanName
     */
    public String getServiceFunctionBeanName() {
        return serviceFunctionBeanName;
    }

    /**
     * @param serviceFunctionBeanName セットする serviceFunctionBeanName
     */
    public void setServiceFunctionBeanName(String serviceFunctionBeanName) {
        this.serviceFunctionBeanName = serviceFunctionBeanName;
    }

    /**
     * @return serviceResponseBeanName
     */
    public String getServiceResponseBeanName() {
        return serviceResponseBeanName;
    }

    /**
     * @param serviceResponseBeanName セットする serviceResponseBeanName
     */
    public void setServiceResponseBeanName(String serviceResponseBeanName) {
        this.serviceResponseBeanName = serviceResponseBeanName;
    }

    /**
     * @return mappingFormBeanRestore
     */
    public Map<String, String> getMappingFormBeanRestore() {
        return mappingFormBeanRestore;
    }

    /**
     * @param mappingFormBeanRestore セットする mappingFormBeanRestore
     */
    public void setMappingFormBeanRestore(
            Map<String, String> mappingFormBeanRestore) {
        this.mappingFormBeanRestore = mappingFormBeanRestore;
    }

    /**
     * @return tokenCheckFlg
     */
    public boolean isTokenCheckFlg() {
        return tokenCheckFlg;
    }

    /**
     * @param tokenCheckFlg セットする tokenCheckFlg
     */
    public void setTokenCheckFlg(boolean tokenCheckFlg) {
        this.tokenCheckFlg = tokenCheckFlg;
    }

    /**
     * @return tokenUpdateFlg
     */
    public boolean isTokenUpdateFlg() {
        return tokenUpdateFlg;
    }

    /**
     * @param tokenUpdateFlg セットする tokenUpdateFlg
     */
    public void setTokenUpdateFlg(boolean tokenUpdateFlg) {
        this.tokenUpdateFlg = tokenUpdateFlg;
    }

    /**
     * 編集有フラグを0に戻さないフラグを取得
     * @return editFlag 編集有フラグを0に戻さないフラグ
     */
    public boolean isEditFlag() {
        return editFlag;
    }


    /**
     * 編集有フラグを0に戻さないフラグセットする
     * @param editFlag 編編集有フラグを0に戻さないフラグ
     */
    public void setEditFlag(boolean editFlag) {
        this.editFlag = editFlag;
    }

    /**
     * @return doModelClassMethod
     */
    public String getDoModelClassMethod() {
        return doModelClassMethod;
    }

    /**
     * @param doModelClassMethod セットする doModelClassMethod
     */
    public void setDoModelClassMethod(String doModelClassMethod) {
        this.doModelClassMethod = doModelClassMethod;
    }

    /**
     * @return doBusinessClassMethod
     */
    public String getDoBusinessClassMethod() {
        return doBusinessClassMethod;
    }

    /**
     * @param doBusinessClassMethod セットする doBusinessClassMethod
     */
    public void setDoBusinessClassMethod(String doBusinessClassMethod) {
        this.doBusinessClassMethod = doBusinessClassMethod;
    }

    /**
     * @return doGetDispDataClassMethod
     */
    public String getDoGetDispDataClassMethod() {
        return doGetDispDataClassMethod;
    }

    /**
     * @param doGetDispDataClassMethod セットする doGetDispDataClassMethod
     */
    public void setDoGetDispDataClassMethod(String doGetDispDataClassMethod) {
        this.doGetDispDataClassMethod = doGetDispDataClassMethod;
    }

    /**
     * @return initCommandFlag
     */
    public boolean isInitCommandFlag() {
        return initCommandFlag;
    }

    /**
     * @param initCommandFlag セットする initCommandFlag
     */
    public void setInitCommandFlag(boolean initCommandFlag) {
        this.initCommandFlag = initCommandFlag;
    }

    /**
     * @return businessBeanName
     */
    public String getBusinessBeanName() {
        return businessBeanName;
    }

    /**
     * @param businessBeanName セットする businessBeanName
     */
    public void setBusinessBeanName(String businessBeanName) {
        this.businessBeanName = businessBeanName;
    }

    /**
     * @return keepScrollPosition
     */
    public boolean isKeepScrollPosition() {
        return keepScrollPosition;
    }

    /**
     * @param keepScrollPosition セットする keepScrollPosition
     */
    public void setKeepScrollPosition(boolean keepScrollPosition) {
        this.keepScrollPosition = keepScrollPosition;
    }

    /**
     * @return mappingBusinessBeanFromFunction
     */
    public Map<String, String> getMappingBusinessBeanFromFunction() {
        return mappingBusinessBeanFromFunction;
    }

    /**
     * @param mappingBusinessBeanFromFunction セットする mappingBusinessBeanFromFunction
     */
    public void setMappingBusinessBeanFromFunction(
            Map<String, String> mappingBusinessBeanFromFunction) {
        this.mappingBusinessBeanFromFunction = mappingBusinessBeanFromFunction;
    }

    /**
     * @return mappingFunctionBeanFromBusiness
     */
    public Map<String, String> getMappingFunctionBeanFromBusiness() {
        return mappingFunctionBeanFromBusiness;
    }

    /**
     * @param mappingFunctionBeanFromBusiness セットする mappingFunctionBeanFromBusiness
     */
    public void setMappingFunctionBeanFromBusiness(
            Map<String, String> mappingFunctionBeanFromBusiness) {
        this.mappingFunctionBeanFromBusiness = mappingFunctionBeanFromBusiness;
    }

    /**
     * @return tableIdList
     */
    public List<String> getTableIdList() {
        return tableIdList;
    }

    /**
     * @param tableIdList セットする tableIdList
     */
    public void setTableIdList(List<String> tableIdList) {
        this.tableIdList = tableIdList;
    }


}
