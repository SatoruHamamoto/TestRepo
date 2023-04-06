package com.gnomes.system.data;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.logging.LogHelper;

/**
 * ファンクションビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                     -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@ConversationScoped
public class BookMarkFunctionBean extends BaseFunctionBean {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** 画面ID */
    private String bookmarkScreenId;

    /** ブックマーク区分 */
    private String bookMarkKbn;

    /**
     * 画面IDを取得
     * @return 画面ID
     */
    public String getBookmarkScreenId() {
        return this.bookmarkScreenId;
    }

    /**
     * 画面IDを設定
     * @param bookmarkScreenId 画面ID
     */
    public void setBookmarkScreenId(String bookmarkScreenId) {
        this.bookmarkScreenId = bookmarkScreenId;
    }

    /**
     * ブックマーク区分を取得
     * @return ブックマーク区分
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

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

    /**
     * クリア処理
     */
    @Override
    public void clear() {
        super.clear();
        this.bookmarkScreenId = null;
        this.bookMarkKbn = null;
    }

}
