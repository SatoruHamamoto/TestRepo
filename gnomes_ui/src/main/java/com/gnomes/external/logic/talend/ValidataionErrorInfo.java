package com.gnomes.external.logic.talend;
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/12/26 15:34 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

import java.util.List;

/**
 * 受信データ検証で発生したチェックエラー情報を格納するクラス
 * @author 03501213
 *
 */
public class ValidataionErrorInfo
{
    // ({0}行目、{1}番目）エラーメッセージ

    /**
     * 定義の行番号
     */
    private int defineLineNumber;

    /**
     * 対象の定義しているフィールドの名前
     */
    private String fieldName;


    /**
     * 定義している要素の位置
     */
    private Integer columnNo;


    /**
     * このエラーのメッセージリソースNo
     */
    private String messagResourceNo;


    /**
     * チェックの詳細を示す文字
     */
    private List<String> messageParams;

    /**
     * @return defineLineNumber
     */
    public int getDefineLineNumber()
    {
        return defineLineNumber;
    }


    /**
     * @param defineLineNumber セットする defineLineNumber
     */
    public void setDefineLineNumber(int defineLineNumber)
    {
        this.defineLineNumber = defineLineNumber;
    }


    /**
     * @return fieldName
     */
    public String getFieldName()
    {
        return fieldName;
    }


    /**
     * @param fieldName セットする fieldName
     */
    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }


    /**
     * @return columnNo
     */
    public Integer getColumnNo()
    {
        return columnNo;
    }


    /**
     * @param columnNo セットする columnNo
     */
    public void setColumnNo(Integer columnNo)
    {
        this.columnNo = columnNo;
    }


    /**
     * @return messagResourceNo
     */
    public String getMessagResourceNo()
    {
        return messagResourceNo;
    }


    /**
     * @param messagResourceNo セットする messagResourceNo
     */
    public void setMessagResourceNo(String messagResourceNo)
    {
        this.messagResourceNo = messagResourceNo;
    }


    /**
     * @return messageParams
     */
    public List<String> getMessageParams()
    {
        return messageParams;
    }


    /**
     * @param messageParams セットする messageParams
     */
    public void setMessageParams(List<String> messageParams)
    {
        this.messageParams = messageParams;
    }

}
