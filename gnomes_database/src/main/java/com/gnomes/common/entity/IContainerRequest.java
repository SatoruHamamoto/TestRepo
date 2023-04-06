package com.gnomes.common.entity;

public interface IContainerRequest {

    public void addAuditTrailInfo(BaseEntity entity, String mes);


    /** イベントIDを取得 */
    public String getEventId();

    /** 操作ユーザID */
    public String getUserId();

    /** 操作ユーザ名 */
    public String getUserName();

}
