package com.gnomes.system.data;

/**
 * ブックマークサービス サービスレスポンスビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                     -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class BookMarkServiceResponseBean {

    /** ブックマーク区分 */
    private String bookMarkKbn;

    /**
     * ブックマーク区分を取得
     * @return bookMarkKbn
     */
    public String getBookMarkKbn() {
        return this.bookMarkKbn;
    }

    /**
     * ブックマーク区分を設定
     * @param bookMarkKbn ブックマーク区分
     */
    public void setBookMarkKbn(String bookMarkKbn) {
        this.bookMarkKbn = bookMarkKbn;
    }

}
