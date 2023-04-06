package com.gnomes.common.data;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.logging.LogHelper;

/**
 * TalendJob情報Bean
 */
@RequestScoped
public class TalendJobInfoBean implements Serializable {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** 実行Job名 */
    private String jobName;

    /** コンテキストパラメータ */
    private String[] contextParam;

    /** エラー発生ジョブ名 */
    private String errorJobName;

    /** エラー発生コンポーネント名 */
    private String errorComponentName;

    /** エラーコメント */
    private String errorComment;


    /**
     * TalendJobInfoBeanコンストラクタ
     */
    public TalendJobInfoBean() {
    }


	/**
	 * 実行Job名の取得
	 * @return 実行Job名
	 */
	public String getJobName() {
		return jobName;
	}


	/**
	 * 実行Job名の設定
	 * @param jobName 実行Job名
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	/**
	 * コンテキストパラメータの取得
	 * @return コンテキストパラメータ
	 */
	public String[] getContextParam() {
		return contextParam;
	}


	/**
	 * コンテキストパラメータの設定
	 * @param contextParam コンテキストパラメータ
	 */
	public void setContextParam(String[] contextParam) {
		this.contextParam = contextParam;
	}


	/**
	 * エラー発生ジョブ名の取得
	 * @return エラー発生ジョブ名
	 */
	public String getErrorJobName() {
		return errorJobName;
	}


	/**
	 * エラー発生ジョブ名の設定
	 * @param errorJobName エラー発生ジョブ名
	 */
	public void setErrorJobName(String errorJobName) {
		this.errorJobName = errorJobName;
	}


	/**
	 * エラー発生コンポーネント名の取得
	 * @return エラー発生コンポーネント名
	 */
	public String getErrorComponentName() {
		return errorComponentName;
	}


	/**
	 * エラー発生コンポーネント名の設定
	 * @param errorComponentName エラー発生コンポーネント名
	 */
	public void setErrorComponentName(String errorComponentName) {
		this.errorComponentName = errorComponentName;
	}


	/**
	 * エラーコメントの取得
	 * @return エラーコメント
	 */
	public String getErrorComment() {
		return errorComment;
	}


	/**
	 * エラーコメントの設定
	 * @param errorComment エラーコメント
	 */
	public void setErrorComment(String errorComment) {
		this.errorComment = errorComment;
	}

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }



}
