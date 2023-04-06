package com.gnomes.common.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSystemBean;

/**
 * Logger ファクトリ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class LoggerFactory {

    @Resource(lookup = CommonConstants.APP_NAME)
    String appName;

    @Inject
    protected GnomesSystemBean gnomesSystemBean;

    public LoggerFactory() {
    }

    @Produces
    public Logger getLogger(InjectionPoint ip) {
        String loggerName = (ip.getBean() == null)
                ? ip.getMember().getDeclaringClass().getName()
                : ip.getBean().getBeanClass().getName();
        Logger mylogger = Logger.getLogger(appName + ": " + loggerName);

        setTraceLog(mylogger);

        return mylogger;
    }

    /**
     * ログレベル設定.
     * <pre>
     * GnomesSystemBeanに設定されているトレースログ要否をもとに、
     * ログレベルの変更を行う。
     * <code>true</code>の場合、ログレベル：FINEST
     * <code>false</code>の場合、ログレベル：INFO
     * </pre>
     * @param mylogger ロガー
     */
    private void setTraceLog(Logger mylogger) {

        Logger parent = mylogger.getParent();

        while (true) {

            if (parent == null) {
                break;
            }

            Handler[] handlers = parent.getHandlers();

            for (int i = 0; i < handlers.length; i++) {

                if ("org.jboss.logmanager.handlers.AsyncHandler".equals(handlers[i].getClass().getName())) {

                    if (this.gnomesSystemBean.isTraceLog()) {
                        if (!Level.FINEST.equals(handlers[i].getLevel())) {
                            handlers[i].setLevel(Level.FINEST);
                        }
                    } else {
                        if (!Level.INFO.equals(handlers[i].getLevel())) {
                            handlers[i].setLevel(Level.INFO);
                        }
                    }
                    break;

                }

            }

            parent = parent.getParent();

        }

    }

}
