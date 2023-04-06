package com.gnomes.common.entity;

/**
 * Gnomesエンティティ 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/20 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesEntitiy {

    /** コンテナリクエスト, */
    protected IContainerRequest req;

    /**
     * ContainerRequest を設定
     * @param req
     */
    public void setReq( IContainerRequest req) {
        this.req = req;
    }

    /**
     * ContainerRequest を取得
     * @return req
     */
    public IContainerRequest getReq() {
        return this.req;
    }

}
