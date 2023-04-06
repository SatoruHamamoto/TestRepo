package com.gnomes.system.data;

import java.util.Map;

import javax.enterprise.context.Dependent;

/**
 * JSON データ用のクラス定義(パスワードのチェック)
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R5.02.10 2023/02/10 YJP/A.Okada               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class CheckPasswordInfo{
	
	/*
	 * パスワード有効桁数
	 */
	private Integer minimumPasswordSize;

	/*
	 * パスワード有効桁数(エラーメッセージ)
	 */
	private String msgMinimumPasswordSize;
	
	/*
	 * 英字または数字のみのパスワード使用制限(0:制限なし、1:制限あり)
	 */
	private Integer isAlphabetAndNumeralOnly;
	
	/*
	 * 英字または数字のみのパスワード使用制限(エラーメッセージ)
	 */
	private String msgIsAlphabetAndNumeralOnly;

	/*
	 * 全て同じ文字または数字の使用禁止制限(0:制限なし、1:制限あり)
	 */
	private Integer isOneCharacterPasswordProhibit;
	
	/*
	 * 全て同じ文字または数字の使用禁止制限(エラーメッセージ)
	 */
	private String msgIsOneCharacterPasswordProhibit;
	
	/*
	 * ユーザIDと同じパスワードの使用禁止制限(0:制限なし、1:制限あり)
	 */
	private Integer isUserIdPasswordProhibit;
	
	/*
	 * ユーザIDと同じパスワードの使用禁止制限(エラーメッセージ)
	 */
	private String msgIsUserIdPasswordProhibit;	

	/*
	 * 禁止文字情報 (禁止文字, 禁止文字に対応するエラーメッセージ(ME01.0022))
	 */
    private Map<String, String> invalidPasswd;


    /**
     * パスワード有効桁数を取得
     * @return パスワード有効桁数
     */
    public Integer getMinimumPasswordSize() {
        return minimumPasswordSize;
    }

    /**
     * パスワード有効桁数を設定
     * @param minimumPasswordSize パスワード有効桁数
     */
    public void setMinimumPasswordSize(Integer minimumPasswordSize) {
        this.minimumPasswordSize = minimumPasswordSize;
    }
    
    /**
     * パスワード有効桁数(エラーメッセージ)を取得
     * @return パスワード有効桁数(エラーメッセージ)
     */
    public String getMsgMinimumPasswordSize() {
        return msgMinimumPasswordSize;
    }

    /**
     * パスワード有効桁数(エラーメッセージ)を設定
     * @param msgMinimumPasswordSize パスワード有効桁数(エラーメッセージ)
     */
    public void setMsgMinimumPasswordSize(String msgMinimumPasswordSize) {
        this.msgMinimumPasswordSize = msgMinimumPasswordSize;
    }

    /**
     * 英字または数字のみのパスワード使用制限を取得
     * @return 0:制限なし、1:制限あり
     */
    public Integer getIsAlphabetAndNumeralOnly() {
        return isAlphabetAndNumeralOnly;
    }

    /**
     * 英字または数字のみのパスワード使用制限を設定
     * @param isAlphabetAndNumeralOnly 0:制限なし、1:制限あり
     */
    public void setIsAlphabetAndNumeralOnly(Integer isAlphabetAndNumeralOnly) {
        this.isAlphabetAndNumeralOnly = isAlphabetAndNumeralOnly;
    }
    
    /**
     * 英字または数字のみのパスワード使用制限(エラーメッセージ)を取得
     * @return 英字または数字のみのパスワード使用制限(エラーメッセージ)
     */
    public String getMsgIsAlphabetAndNumeralOnly() {
        return msgIsAlphabetAndNumeralOnly;
    }

    /**
     * 英字または数字のみのパスワード使用制限(エラーメッセージ)を設定
     * @param msgIsAlphabetAndNumeralOnly 英字または数字のみのパスワード使用制限(エラーメッセージ)
     */
    public void setMsgIsAlphabetAndNumeralOnly(String msgIsAlphabetAndNumeralOnly) {
        this.msgIsAlphabetAndNumeralOnly = msgIsAlphabetAndNumeralOnly;
    }

    /**
     * 全て同じ文字または数字の使用禁止制限を取得
     * @return 0:制限なし、1:制限あり
     */
    public Integer getIsOneCharacterPasswordProhibit() {
        return isOneCharacterPasswordProhibit;
    }

    /**
     * 全て同じ文字または数字の使用禁止制限を設定
     * @param isOneCharacterPasswordProhibit 0:制限なし、1:制限あり
     */
    public void setIsOneCharacterPasswordProhibit(Integer isOneCharacterPasswordProhibit) {
        this.isOneCharacterPasswordProhibit = isOneCharacterPasswordProhibit;
    }
    
    /**
     * 全て同じ文字または数字の使用禁止制限(エラーメッセージ)を取得
     * @return 全て同じ文字または数字の使用禁止制限(エラーメッセージ)
     */
    public String getMsgIsOneCharacterPasswordProhibit() {
        return msgIsOneCharacterPasswordProhibit;
    }

    /**
     * 全て同じ文字または数字の使用禁止制限(エラーメッセージ)を設定
     * @param msgIsOneCharacterPasswordProhibit 全て同じ文字または数字の使用禁止制限(エラーメッセージ)
     */
    public void setMsgIsOneCharacterPasswordProhibit(String msgIsOneCharacterPasswordProhibit) {
        this.msgIsOneCharacterPasswordProhibit = msgIsOneCharacterPasswordProhibit;
    }
    
    /**
     * ユーザIDと同じパスワードの使用禁止制限を取得
     * @return 0:制限なし、1:制限あり
     */
    public Integer getIsUserIdPasswordProhibit() {
        return isUserIdPasswordProhibit;
    }

    /**
     * ユーザIDと同じパスワードの使用禁止制限を設定
     * @param isUserIdPasswordProhibit 0:制限なし、1:制限あり
     */
    public void setIsUserIdPasswordProhibit(Integer isUserIdPasswordProhibit) {
        this.isUserIdPasswordProhibit = isUserIdPasswordProhibit;
    }
    
    /**
     * ユーザIDと同じパスワードの使用禁止制限(エラーメッセージ)を取得
     * @return ユーザIDと同じパスワードの使用禁止制限(エラーメッセージ)
     */
    public String getMsgIsUserIdPasswordProhibit() {
        return msgIsUserIdPasswordProhibit;
    }

    /**
     * ユーザIDと同じパスワードの使用禁止制限(エラーメッセージ)を設定
     * @param msgIsUserIdPasswordProhibit ユーザIDと同じパスワードの使用禁止制限(エラーメッセージ)
     */
    public void setMsgIsUserIdPasswordProhibit(String msgIsUserIdPasswordProhibit) {
        this.msgIsUserIdPasswordProhibit = msgIsUserIdPasswordProhibit;
    }

    /**
     * 禁止文字情報を取得
     * @return 禁止文字情報 (禁止文字, 禁止文字に対応するエラーメッセージ(ME01.0022))
     */
    public Map<String, String> getInvalidPasswd() {
        return invalidPasswd;
    }

    /**
     * 禁止文字情報を設定
     * @param invalidPasswd 禁止文字情報 (禁止文字, 禁止文字に対応するエラーメッセージ(ME01.0022))
     */
    public void setInvalidPasswd(Map<String, String> invalidPasswd) {
        this.invalidPasswd = invalidPasswd;
    }
    
}