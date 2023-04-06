package com.gnomes.common.ptlamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.SoundPatternData;
import com.gnomes.common.data.TalendJobInfoBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.ptlamp.data.PatlampData;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.JobBase;
import com.gnomes.common.util.StringUtils;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.system.dao.MstrPatlampDao;
import com.gnomes.system.dao.MstrPatlampModelDao;
import com.gnomes.system.entity.MstrPatlamp;
import com.gnomes.system.entity.MstrPatlampModel;

/**
 * パトランプ制御APIクラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/03 YJP/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class SendRequestPatlamp {

	/** コンテンツパラメータ（IPアドレス） */
	private static final String CONTENT_IP_ADDRESS = "ip_address=";

	/** コンテンツパラメータ（コマンドタイプ） */
	private static final String CONTENT_PARAM_NAME = "command_type=";

	/** コンテンツパラメータ（点灯パターン区分） */
	private static final String CONTENT_LIGHTS_DATA = "lights_data=";

	/** コンテンツパラメータ（サウンドパターン区分） */
	private static final String CONTENT_SOUND_DATA = "sound_data=";

	/** talendJob名(head部) */
	private static final String TALEND_JOB_NAME_HEAD = "gnomes_project.";

	/** talendJob名(尾) */
	private static final String TALEND_JOB_NAME_TAIL = "_0_1.";

	/** パトランプ機種マスタDao */
	@Inject
	protected MstrPatlampModelDao mstrPatlampModelDao;

	/** パトランプマスタDao */
	@Inject
	protected MstrPatlampDao mstrPatlampDao;

    /** Talendジョブ情報Bean */
    @Inject
    protected TalendJobInfoBean talendJobInfoBean;

    /** GnomesExceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory exceptionFactory;

	/**
	 * 点灯制御
	 * @param patliteId     パトランプID
	 * @param ptdata  点灯パターン区分
	 * @param soundptr  サウンドパターン区分
	 * @throws Exception
	 */
    @TraceMonitor
    @ErrorHandling
	public void lightOn(String patliteId, Map<String, Integer> ptdata, SoundPatternData soundptr) throws Exception {

		// パトランプIDからデータを取得
		PatlampData patlampData = this.getPatliteData(patliteId);

		// 点灯パターンをJSONに変換
		String jsonLightPtr = ConverterUtils.getJson(ptdata);

		// サウンドパターンを文字列に変換
		String sound = soundptr.getValue();

		// コンテンツパラメータ
		String talendContextParam = CONTENT_IP_ADDRESS + patlampData.getIp_address()
						+ "," + CONTENT_SOUND_DATA + sound
						+ "," + CONTENT_PARAM_NAME + CommonEnums.CommandParam.ALERT.getValue();

		// talend処理実行
		this.runTalendJob(patlampData.getTalendJoobName(), talendContextParam, jsonLightPtr);

	}

    /**
     * 点灯制御(パラメータ無し)
     * @param patliteId     パトランプID
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void lightOn(String patliteId) throws Exception {

    	// パトランプIDからデータを取得
    	PatlampData patlampData = this.getPatliteData(patliteId);

    	// コンテンツパラメータ作成
    	List<String> contextParamList = new ArrayList<String>();
    	//IPアドレス
    	String talendContextParam = "--context_param " + CONTENT_IP_ADDRESS + patlampData.getIp_address();
    	contextParamList.add(talendContextParam);

    	//点灯鳴動パラメーター文字列01～05
    	if(patlampData.getLightSoundParameterStringList() != null && patlampData.getLightSoundParameterStringList().size() > 0){
    		contextParamList.addAll(patlampData.getLightSoundParameterStringList());
    	}

    	String[] contextParamArray = contextParamList.toArray(new String[contextParamList.size()]);

    	// talend処理実行
    	this.runTalendJob(patlampData.getTalendJoobName(), contextParamArray);

    }

	/**
	 * 消灯制御
	 * @param patliteId     パトランプID
	 * @throws Exception
	 */
    @TraceMonitor
    @ErrorHandling
	public void lightOff(String patliteId) throws Exception {

		// パトランプIDからデータを取得
		PatlampData patlampData = this.getPatliteData(patliteId);

		// 消灯用データの取得
		Map<String, Integer> lightPattern = this.getLightOffData();

		// 点灯パターンをJSONに変換
		String jsonLightPtr = ConverterUtils.getJson(lightPattern);

		// サウンドパターンを文字列に変換(消音)
		String sound = CommonEnums.SoundPatternData.OFF.getValue();

		// コンテンツパラメータ
		String talendContextParam = CONTENT_IP_ADDRESS + patlampData.getIp_address()
						+ "," + CONTENT_SOUND_DATA + sound
						+ "," + CONTENT_PARAM_NAME + CommonEnums.CommandParam.CLEAR.getValue();

		// talend処理実行
		this.runTalendJob(patlampData.getTalendJoobName(), talendContextParam, jsonLightPtr);

	}

    /**
     * Talendジョブ実行
     * @param talendJobName ジョブ名
     * @param talendContextParam コンテキストパラメータ
     * @param jsonLightPtr 点灯用データ
     * @throws Exception
     */
    private void runTalendJob(String talendJobName, String talendContextParam, String jsonLightPtr) throws Exception {

        // Talend呼出ジョブ名が設定されている場合
        if (!StringUtil.isNullOrEmpty(talendJobName)) {
            // コンテキストパラメータ
            String[] contextParamTemp = null;
            String[] contextParam = null;

            // Talendコンテキストパラメータから引数に設定するコンテキストパラメータを取得
            if (!StringUtil.isNullOrEmpty(talendContextParam)) {
                // Talendコンテキストパラメータをカンマ区切りで配列に変換
                contextParamTemp = JobBase.convertContext(StringUtils.splitLineWithComma(talendContextParam));

                // コンテキストパラメータに点灯用データを追加
                contextParam = new String[contextParamTemp.length+1];
                System.arraycopy(contextParamTemp, 0, contextParam, 0, contextParamTemp.length);
                contextParam[contextParamTemp.length] = "--context_param " + CONTENT_LIGHTS_DATA + jsonLightPtr;
            }

            TalendJobRun.runJob(talendJobName, contextParam, false);
            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(this.talendJobInfoBean.getErrorJobName())) {
                throw this.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0146,
                        talendJobInfoBean.getErrorJobName(),
                        talendJobInfoBean.getErrorComponentName());
            }
        }
    }
    /**
     * Talendジョブ実行
     * @param talendJobName ジョブ名
     * @param talendContextParam コンテキストパラメータ
     * @param jsonLightPtr 点灯用データ
     * @throws Exception
     */
    private void runTalendJob(String talendJobName, String[] contextParam) throws Exception {

    	// Talend呼出ジョブ名が設定されている場合
    	if (!StringUtil.isNullOrEmpty(talendJobName)) {

    		//System.out.println(contextParam);
    		TalendJobRun.runJob(talendJobName, contextParam, false);
    		// エラーが出力された場合
    		if (!StringUtil.isNullOrEmpty(this.talendJobInfoBean.getErrorJobName())) {
    			throw this.exceptionFactory.createGnomesAppException(
    					null, GnomesMessagesConstants.ME01_0146,
    					talendJobInfoBean.getErrorJobName(),
    					talendJobInfoBean.getErrorComponentName());
    		}
    	}
    }

	/**
	 * 消灯用データの設定
	 * @return 消灯用データ
	 */
	private Map<String, Integer> getLightOffData() {
		Map <String, Integer> map = new HashMap<String, Integer>();
		map.put("clear", 1);
		return map;
	}

	/**
	 * パトランプIDからIPアドレス、talendジョブ名を取得
	 * @param patliteId パトランプID
	 * @return パトライトデータ（IPアドレス、Talendジョブ名）
	 * @throws GnomesAppException
	 */
	private PatlampData getPatliteData(String patliteId) throws GnomesAppException {

		PatlampData data = new PatlampData();

		// パトランプマスタ取得
		MstrPatlamp mstrPtlamp = mstrPatlampDao.getMstrPatlamp(patliteId);

        if (Objects.isNull(mstrPtlamp)) {
            // ME01.0185:「該当するパトランプ機種の登録がありません。\nパトランプ機種の登録を確認してください。」
            throw this.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0185);
		}

		// パトランプ機種マスタ取得
		MstrPatlampModel mstrPtlampModel = mstrPatlampModelDao.getMstrPatlampModel(mstrPtlamp.getPatlamp_model_id());

        if (Objects.isNull(mstrPtlampModel)) {
            // ME01.0186:「実行するジョブ名の指定がありません。\n実行するジョブ名を設定してください。」
            throw this.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0186);
		}

		// IPアドレス、talendジョブ名を取得
		String talendJobName = mstrPtlampModel.getTalend_job_name();

		// IPアドレス、talendジョブ名を設定
		data.setIp_address(mstrPtlamp.getIp_address());
		data.setTalendJoobName(TALEND_JOB_NAME_HEAD + talendJobName.toLowerCase() + TALEND_JOB_NAME_TAIL + talendJobName);

		//点灯鳴動パラメーター文字列を取得
		List<String> LightSoundParameterStringList = new ArrayList<String>();
		//点灯鳴動パラメーター文字列01
		if(mstrPtlamp.getLight_sound_parameter_string_01() != null && !mstrPtlamp.getLight_sound_parameter_string_01().isEmpty()){
			LightSoundParameterStringList.add("--context_param " + mstrPtlamp.getLight_sound_parameter_string_01());
		}
		//点灯鳴動パラメーター文字列02
		if(mstrPtlamp.getLight_sound_parameter_string_02() != null && !mstrPtlamp.getLight_sound_parameter_string_02().isEmpty()){
			LightSoundParameterStringList.add("--context_param " + mstrPtlamp.getLight_sound_parameter_string_02());
		}
		//点灯鳴動パラメーター文字列03
		if(mstrPtlamp.getLight_sound_parameter_string_03() != null && !mstrPtlamp.getLight_sound_parameter_string_03().isEmpty()){
			LightSoundParameterStringList.add("--context_param " + mstrPtlamp.getLight_sound_parameter_string_03());
		}
		//点灯鳴動パラメーター文字列04
		if(mstrPtlamp.getLight_sound_parameter_string_04() != null && !mstrPtlamp.getLight_sound_parameter_string_04().isEmpty()){
			LightSoundParameterStringList.add("--context_param " + mstrPtlamp.getLight_sound_parameter_string_04());
		}
		//点灯鳴動パラメーター文字列05
		if(mstrPtlamp.getLight_sound_parameter_string_05() != null && !mstrPtlamp.getLight_sound_parameter_string_05().isEmpty()){
			LightSoundParameterStringList.add("--context_param " + mstrPtlamp.getLight_sound_parameter_string_05());
		}
		data.setLightSoundParameterStringList(LightSoundParameterStringList);

		return data;
	}

}
