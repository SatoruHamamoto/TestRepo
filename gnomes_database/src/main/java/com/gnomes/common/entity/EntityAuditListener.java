package com.gnomes.common.entity;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * エンティティ コールバック処理
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 *  */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/13 YJP/H.Gojo              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class EntityAuditListener {

    /** ログヘルパー */
    protected LogHelper logHelper = new LogHelper();

    @PrePersist
    public void prePersist(Object entity) throws ParseException {

        if (entity instanceof BaseEntity) {
            if (((BaseEntity)entity).getReq() == null) {
                //logHelper.fine(null, null, "prePersist: " + entity.getClass().toString() + ", NULL");
            } else {
                Date nowDate = new Date();
                //logHelper.fine(null, null,  "prePersist: " + entity.getClass().toString() + "," + ((BaseEntity)entity).getReq().getClass().toString());

                // 登録情報、更新情報を設定
                IContainerRequest req = ((BaseEntity)entity).getReq();
                // 登録イベントID
                ((BaseEntity)entity).setFirst_regist_event_id(req.getEventId());
                // 登録従業員No
                ((BaseEntity)entity).setFirst_regist_user_number(req.getUserId());
                // 登録従業員名
                ((BaseEntity)entity).setFirst_regist_user_name(req.getUserName());
                // 登録日時
                ((BaseEntity)entity).setFirst_regist_datetime(nowDate);
                // 更新イベントID
                ((BaseEntity)entity).setLast_regist_event_id(req.getEventId());
                // 更新従業員No
                ((BaseEntity)entity).setLast_regist_user_number(req.getUserId());
                // 更新従業員名
                ((BaseEntity)entity).setLast_regist_user_name(req.getUserName());
                // 更新日時
                ((BaseEntity)entity).setLast_regist_datetime(nowDate);
            }

        }

    }

    @PreUpdate
    public void preUpdate(Object entity) throws ParseException {

        if (entity instanceof BaseEntity) {
            if (((BaseEntity)entity).getReq() == null) {
                //logHelper.fine(null, null,  "preUpdate: " + entity.getClass().toString() + ", NULL");
            } else {
                Date nowDate = new Date();

                //logHelper.fine(null, null,  "preUpdate: " + entity.getClass().toString() + "," + ((BaseEntity)entity).getReq().getClass().toString());

                // 更新情報を設定
                IContainerRequest req = ((BaseEntity)entity).getReq();
                // 更新イベントID
                ((BaseEntity)entity).setLast_regist_event_id(req.getEventId());
                // 更新従業員No
                ((BaseEntity)entity).setLast_regist_user_number(req.getUserId());
                // 更新従業員名
                ((BaseEntity)entity).setLast_regist_user_name(req.getUserName());
                // 更新日時
                ((BaseEntity)entity).setLast_regist_datetime(nowDate);

            }

        }

    }

    @PreRemove
    public void preRemove(Object entity) {
        if (entity instanceof BaseEntity) {
            if(((BaseEntity)entity).getReq() == null) {
                //logHelper.fine(null, null, "preRemove: " + entity.getClass().toString() + ", NULL");
            } else {
                //logHelper.fine(null, null, "preRemove: " + entity.getClass().toString() + "," + ((BaseEntity)entity).getReq().getClass().toString());

                Date nowDate = new Date();

                // 更新情報を設定
                IContainerRequest req = ((BaseEntity)entity).getReq();
                // 更新イベントID
                ((BaseEntity)entity).setLast_regist_event_id(req.getEventId());
                // 更新従業員No
                ((BaseEntity)entity).setLast_regist_user_number(req.getUserId());
                // 更新従業員名
                ((BaseEntity)entity).setLast_regist_user_name(req.getUserName());
                // 更新日時
                ((BaseEntity)entity).setLast_regist_datetime(nowDate);

            }
        }
    }

    @PostRemove
    public void postRemove(Object entity) {
        if (entity instanceof BaseEntity) {
            if (((BaseEntity)entity).getReq() != null) {
                //((BaseEntity)entity).getReq().addAuditTrailInfo((BaseEntity)entity, GnomesResourcesConstants.YY01_0034);
            }
        }
    }

    /**
     * ログヘルパー
     */
    public class LogHelper {

        /** コロン */
        private static final String COLON = ":";

        /** 区切り文字 */
        private static final String DELIMITER = ", ";

        /**
         * 詳細ログ出力.
         * <pre>
         * 詳細ログ出力処理を行う。
         * </pre>
         * @param className 呼び出し元クラス名
         * @param methodName 呼び出し元メソッド名
         * @param message メッセージ
         */
        protected void fine(String className, String methodName, String message) {

            Logger logger = Logger.getLogger("UI" + ": " + this.getClass().getName());
            String msg = this.editMessage(className, methodName, message);
            logger.fine(msg);

        }

        /**
         * メッセージ編集.
         * @param className 呼び出し元クラス名
         * @param methodName 呼び出し元メソッド名
         * @param message メッセージ
         * @return 編集後メッセージ
         */
        private String editMessage(String className, String methodName, String message) {

            String classNameLog = className;
            String methodNameLog = methodName;

            StackTraceElement[] traces = new Throwable().getStackTrace();
            if (traces.length >= 3) {
                if (isEmpty(classNameLog)) {
                    classNameLog = traces[2].getClassName();
                }
                if (isEmpty(methodNameLog))
                    methodNameLog = traces[2].getMethodName();
            }

            StringBuilder messages = new StringBuilder();

            // クラス名
            messages.append(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0052));
            messages.append(COLON).append(classNameLog).append(DELIMITER);
            // メソッド名
            messages.append(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0053));
            messages.append(COLON).append(methodNameLog).append(DELIMITER);
            // メッセージ
            messages.append(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0006));
            messages.append(COLON).append(message);

            return messages.toString();

        }

    }

    private boolean isEmpty(String value) {

        if (value == null || "".equals(value)) {
            return true;
        }

        return false;

    }

}
