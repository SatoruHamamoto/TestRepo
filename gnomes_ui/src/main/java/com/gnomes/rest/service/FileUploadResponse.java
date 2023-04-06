package com.gnomes.rest.service;

import java.util.List;

import com.gnomes.common.util.StringUtils;

/**
 * JSON データ用のクラス定義(ファイルアップロード レスポンス)
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/26 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

/**
 * JSON データ用のクラス定義（ファイルアップロード レスポンス）
 */
public class FileUploadResponse {

    /**
     * 処理成功有無
     */
    private Boolean isSuccess;

    /**
     * 上書き確認チェック必要　有無
     */
    private Boolean isCheckOverwrite;

    /**
     * ファイル名一覧
     */
    private List<String> filenames;


    /**
     * メッセージ情報
     */
    private String message;


    /**
     * コマンド
     */
    private String command;


    /**
     * コンストラクター
     */
    public FileUploadResponse() {
        this.isSuccess = false;
        this.isCheckOverwrite = false;
    }

    /**
     * @return isSuccess
     */
    public Boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * @param isSuccess セットする isSuccess
     */
    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * @return isCheckOverwrite
     */
    public Boolean getIsCheckOverwrite() {
        return isCheckOverwrite;
    }

    /**
     * @param isCheckOverwrite セットする isCheckOverwrite
     */
    public void setIsCheckOverwrite(Boolean isCheckOverwrite) {
        this.isCheckOverwrite = isCheckOverwrite;
    }

    /**
     * @return filenames
     */
    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * @param filenames セットする filenames
     */
    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message セットする message
     */
    public void setMessage(String message) {

        String[] s = null;
        if (message != null) {
            s = message.split(StringUtils.LIEN_SEPARATOR_RESOURCE);
        }

        if (s != null) {
            this.message = s[0];
        } else {
            this.message = message;
        }
    }

    /**
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command セットする command
     */
    public void setCommand(String command) {
        this.command = command;
    }







}
