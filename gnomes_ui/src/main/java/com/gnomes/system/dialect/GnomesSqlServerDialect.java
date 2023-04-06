package com.gnomes.system.dialect;

import java.sql.Types;

import javax.enterprise.context.Dependent;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.dialect.pagination.LegacyLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;

/**
 * GnomesSqlServerDialect.
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/15 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class GnomesSqlServerDialect extends SQLServer2008Dialect /* SQLServer2012Dialect */ {

    /** ロックオプション. */
    private LockOptions lockOptions;

    /** コンストラクタ. */
    public GnomesSqlServerDialect() {
        registerColumnType(Types.NCLOB, "nvarchar(max)");
    }

    /**
     * リミッドハンドラ
     * SQLServer製を使用するとバインド変数が使われ
     * 動的検索に影響があるので、バインド変数を使用しない処理に置き換え
     */
    @Override
    public LimitHandler getLimitHandler() {
        return new LegacyLimitHandler(this);
    }


    @Override
    public String appendLockHint(LockOptions lockOptions, String tableName) {

        this.lockOptions = lockOptions;
        final LockMode mode = lockOptions.getLockMode();

        switch ( mode ) {
            case UPGRADE_NOWAIT:
                return tableName + " with (updlock, rowlock, nowait)";
            case PESSIMISTIC_WRITE:
            case WRITE:
            case PESSIMISTIC_READ:
                return tableName + " with (updlock, rowlock)";
            case UPGRADE_SKIPLOCKED:
                return tableName + " with (updlock, rowlock, readpast)";
            default:
                return tableName;
        }

    }

    @Override
    public String transformSelectString(String select) {

        final LockMode mode = this.lockOptions.getLockMode();

        switch ( mode ) {
            case PESSIMISTIC_WRITE:
            case WRITE:
            case PESSIMISTIC_READ:
                return "set lock_timeout " + this.lockOptions.getTimeOut() + " " + select;
            default:
                return select;
        }

    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    public String getLimitString(String querySelect, int offset, int limit) {

        return new StringBuilder(querySelect)
                .append(" offset ").append(offset).append(" rows")
                .append(" fetch next ").append(limit - offset).append(" rows only").toString();

    }

}
