package com.gnomes.rest.service;

/** リモート接続データインプット. */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/23 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class RemoteConnectDataInput {

    /** コマンドID. */
    private String command;

    /** クラス名. */
    private String className;

    /** メソッド名. */
    private String methodName;

    /** stataicメソッドフラグ. */
    private boolean staticMethodFlag;

    /** リクエストパラメータ(Json). */
    private String[] requestParams;

    /** リクエストパラメータクラス. */
    private Class<?>[] requestParamsType;

    /**
     * コマンドID取得.
     * @return コマンドID
     */
    public String getCommand() {
        return command;
    }

    /**
     * コマンドID設定.
     * @param command コマンドID
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * クラス名取得.
     * @return クラス名設定
     */
    public String getClassName() {
        return className;
    }

    /**
     * クラス名設定.
     * @param className クラス名
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * メソッド名取得.
     * @return メソッド名
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * メソッド名設定.
     * @param methodName メソッド名
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    /**
     * stataicメソッドフラグ取得.
     * @return stataicメソッドフラグ
     */
    public boolean isStaticMethodFlag() {
        return staticMethodFlag;
    }

    /**
     * stataicメソッドフラグ設定.
     * @param staticMethodFlag stataicメソッドフラグ
     */
    public void setStaticMethodFlag(boolean staticMethodFlag) {
        this.staticMethodFlag = staticMethodFlag;
    }

    /**
     * リクエストパラメータ取得.
     * @return リクエストパラメータ
     */
    public String[] getRequestParams() {
        return requestParams;
    }

    /**
     * リクエストパラメータ設定.
     * @param requestParams リクエストパラメータ
     */
    public void setRequestParams(String[] requestParams) {
        this.requestParams = requestParams;
    }

    /**
     * リクエストパラメータ型取得.
     * @return リクエストパラメータ型
     */
    public Class<?>[] getRequestParamsType() {
        return requestParamsType;
    }

    /**
     * リクエストパラメータ型設定.
     * @param requestParamsType リクエストパラメータ型
     */
    public void setRequestParamsType(Class<?>[] requestParamsType) {
        this.requestParamsType = requestParamsType;
    }

}
