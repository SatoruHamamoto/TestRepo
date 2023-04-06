package com.gnomes.common.dto;

import java.util.List;

import com.gnomes.common.constants.CommonEnums.DisplayType;
import com.gnomes.common.exception.GnomesAppException;

/**
 *
 * 共通プルダウン情報取得 インターフェイス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/21 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface ICommonPullDown {

    /**
     * 言語選択プルダウン取得
     * @return 言語選択プルダウン
     * @throws GnomesAppException
     */
    public List<PullDownDto> getPD0004() throws GnomesAppException;

    /**
     * 端末情報より端末選択プルダウンを取得
     * @return 端末選択プルダウン
     */
    public List<PullDownDto> getPD0006(DisplayType displayType) throws GnomesAppException;

    /**
     * サイト選択プルダウン取得
     * @return サイト選択プルダウン
     */
    public List<PullDownDto> getPD0007() throws GnomesAppException;

    /**
     * システム定義選択プルダウン取得
     * @return システム定義選択プルダウン
     */
    public List<PullDownDto> getPD0008(String systemDefineType) throws GnomesAppException;
}
