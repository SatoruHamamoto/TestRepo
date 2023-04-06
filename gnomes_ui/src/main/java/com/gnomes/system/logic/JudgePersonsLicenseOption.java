package com.gnomes.system.logic;
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/09/03 09:36 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

import javax.enterprise.context.Dependent;

import com.gnomes.system.data.PreJudgeForCodesParameter;

/**
 * 認証判定のオプション処理クラス
 * 認証判定や認証判定する前のデータ設定（準備）などのタイミングで
 * コンテンツ側の情報を元に判定を変えたり確認したり、準備をしたりする
 * オプションの処理を行う。このクラスは通常基盤では何もせず、
 * 上位で差し替えて使われる
 * @author 03501213
 *
 */
@Dependent
public class JudgePersonsLicenseOption
{

    /**
     *
     * 認証判定前の拠点コード、指図工程コード、作業工程コード、作業場所コードの内容を
     * コンテンツやJOB側の情報に応じてフィルタ（変更）をする場合のロジックを差し替える
     * JOB,コンテンツ側は本クラスを親にする子クラスを作り@Specializesを指定し
     * 本関数を@Overrideする
     *
     * @param preJudgeForCodesParameter 判定の変更を兼ね必要な各種パラメータ
     */
    public void preJudgeForCodes(PreJudgeForCodesParameter preJudgeForCodesParameter)
    {
        //なにもしない
        return;
    }

}
