package com.gnomes.common.dto;
/*
*
* ツールによって自動生成されたDTOです。
* 生成日時：2016/10/17 9:43:21
*
*/

import java.math.BigInteger;

public class CountDto {

    /** 件数 */
    private BigInteger cnt;

    /**
     * CountDto・コンストラクタ
     */
    public CountDto() {
    }

    /**
     * CountDto・コンストラクタ
     * @param cnt 件数
     */
    public CountDto(BigInteger cnt) {
        super();
        this.cnt = cnt;
    }

    /**
     * 件数を取得
     * @return 件数
     */
    public BigInteger getCnt() {
        return cnt;
    }

    /**
     * 件数を設定
     * @param cnt 件数
     */
    public void setCnt(BigInteger cnt) {
        this.cnt = cnt;
    }

}
