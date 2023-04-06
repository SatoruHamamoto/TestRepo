package com.gnomes.system.dto;

import java.util.Date;

/**
*
* メッセージ情報DTO インターフェイス
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/01/17 KCC/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public interface IMessageBaseDto {

    /**
     * 発生日を取得する
     * @return 発生日
     */
    public Date getOccur_date();

    /**
     * メッセージNoを取得する
     * @return メッセージNo
     */
    public String getMessage_no();

    /**
     * 種別を取得する
     * @return 種別
     */
    public Integer getCategory();

    /**
     * メッセージ重要度を取得する
     * @return メッセージ重要度
     */
    public Integer getMessage_level();

    /**
     * リソースidを取得する
     * @return リソースid
     */
    public String getResource_id();

    /**
     * メッセージパラメータ１を取得する
     * @return メッセージパラメータ１
     */
    public String getMessage_param1();

    /**
     * メッセージパラメータ２を取得する
     * @return メッセージパラメータ２
     */
    public String getMessage_param2();

    /**
     * メッセージパラメータ３を取得する
     * @return メッセージパラメータ３
     */
    public String getMessage_param3();

    /**
     * メッセージパラメータ４を取得する
     * @return メッセージパラメータ４
     */
    public String getMessage_param4();

    /**
     * メッセージパラメータ５を取得する
     * @return メッセージパラメータ５
     */
    public String getMessage_param5();

    /**
     * メッセージパラメータ６を取得する
     * @return メッセージパラメータ６
     */
    public String getMessage_param6();

    /**
     * メッセージパラメータ７を取得する
     * @return メッセージパラメータ７
     */
    public String getMessage_param7();

    /**
     * メッセージパラメータ８を取得する
     * @return メッセージパラメータ８
     */
    public String getMessage_param8();

    /**
     * メッセージパラメータ９を取得する
     * @return メッセージパラメータ９
     */
    public String getMessage_param9();

    /**
     * メッセージパラメータ１０を取得する
     * @return メッセージパラメータ１０
     */
    public String getMessage_param10();

}
