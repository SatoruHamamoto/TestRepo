package com.gnomes.rest.service;

/**
*
* インポートファイル コマンド 実行結果 基底クラス
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public abstract class BaseImportFileCommandResponse {


    /**
     * インポートファイルのバリデーションエラー有無フラグ
     * 画面のエラーダウンロードリンクを表示するため
     */
    private Boolean isImportValidateError = false;


    public Boolean isImportValidateError() {
        return isImportValidateError;
    }


    public void setImportValidateError(Boolean isImportValidateError) {
        this.isImportValidateError = isImportValidateError;
    }



}
