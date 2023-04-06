package com.gnomes.common.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.uiservice.ContainerRequest;

/**
 * ログヘルパー
 */
@Dependent
public class LogHelper {

    @Inject
    protected ContainerRequest request;

    protected transient Logger logger;

    /** コロン */
    private static final String COLON = ":";

    /** 区切り文字 */
    private static final String DELIMITER = ", ";

    /**
     * INFOログ出力.
     * <pre>
     * 情報ログ出力処理を行う。
     * </pre>
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     */
    public void info(Logger logger, String className, String methodName, String message) {

        this.logger = logger;
        String msg = this.editMessage(className, methodName, message,null);
        this.logger.info(msg);

    }

    /**
     * WARNINGログ出力.
     * <pre>
     * 警告ログ出力処理を行う。
     * </pre>
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     */
    public void warning(Logger logger, String className, String methodName, String message) {

        this.logger = logger;
        String msg = this.editMessage(className, methodName, message,null);
        this.logger.warning(msg);

    }

    /**
     * デバッグログ出力.
     * <pre>
     * デバッグログ出力処理を行う。
     * </pre>
     * @param logger ロガー
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     */
    public void debug(Logger logger, String className, String methodName, String message) {
        this.logger = logger;
        String msg = this.editMessage(className, methodName, "[DEBUG] " + message, null);
        this.logger.finest(msg);
    }

    /**
     * 詳細ログ出力.
     * <pre>
     * 詳細ログ出力処理を行う。
     * </pre>
     * @param logger ロガー
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     */
    public void fine(Logger logger, String className, String methodName, String message) {

        this.logger = logger;
        String msg = this.editMessage(className, methodName, message, null);
        this.logger.fine(msg);

    }

    /**
     * 重大ログ出力.
     * <pre>
     * 重大ログ出力処理を行う。
     * </pre>
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     */
    public void severe(Logger logger, String className, String methodName, String message) {

        this.logger = logger;
        String msg = this.editMessage(className, methodName, message, null);
        this.logger.severe(msg);

    }

    /**
     * 重大ログ出力.
     * <pre>
     * 重大ログ出力処理を行う。
     * </pre>
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     * @param messageId メッセージID
     */
    public void severe(Logger logger, String className, String methodName, String message, String messageId) {

        this.logger = logger;
        String msg = this.editMessage(className, methodName, message, messageId);
        this.logger.severe(msg);

    }


    /**
     * 重大ログ出力.
     * <pre>
     * 重大ログ出力処理を行う。
     * </pre>
     * @param logger
     * @param msgResourceID メッセージリソースID
     * @param params メッセージ
     */
    public void severeOfResourceID(Logger logger,String msgResourceID,Object ... params){
    	//String msg = ResourcesHandler.getString(msgResourceID,params);
    	//this.messageNo = messageNo;

    	Object[] messageParams = null;

    	if (params != null) {
            Object[] paramNullForStrings = new Object[params.length];
            for(int i=0; i<params.length; i++) {
                if(params[i] == null){
                    paramNullForStrings[i] = "null";
                } else {
                    paramNullForStrings[i] = params[i];
                }
            }
            messageParams = paramNullForStrings;
        }
    	String message = MessagesHandler.getString(msgResourceID, messageParams);

    	this.severe(logger, null,null,message);
    }

    /**
     * 重大ログ出力.
     * <pre>
     * 重大ログ出力処理を行う。
     * </pre>
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     * @param thrown エラー情報
     */
    public void severe(Logger logger, String className, String methodName, String message, Throwable thrown) {

        this.logger = logger;
        String messageId = null;
        if (thrown != null) {
            if (thrown instanceof GnomesException) {
            	messageId = ((GnomesException)thrown).getMessageNo();
            } else if (thrown instanceof GnomesAppException) {
            	messageId = ((GnomesAppException)thrown).getMessageNo();
            }
        }

        String msg = this.editMessage(className, methodName, message, messageId);


        this.logger.log(Level.SEVERE, msg, thrown);

    }

    /**
     * メッセージ編集.
     * @param className 呼び出し元クラス名
     * @param methodName 呼び出し元メソッド名
     * @param message メッセージ
     * @param messageId メッセージID
     * @return 編集後メッセージ
     */
    private String editMessage(String className, String methodName, String message, String messageId) {

        String classNameLog = className;
        String methodNameLog = methodName;

        if (StringUtils.isEmpty(classNameLog) || StringUtils.isEmpty(methodNameLog) ) {
	        //StackTraceElement[] traces = new Throwable().getStackTrace();
	        //if (traces.length >= 3) {
	            if (StringUtils.isEmpty(classNameLog)) {
	                //classNameLog = traces[2].getClassName();
	            	classNameLog = "none";
	            }
	            if (StringUtils.isEmpty(methodNameLog))
	                //methodNameLog = traces[2].getMethodName();
	            	methodNameLog = "none";
	        //}
        }

        StringBuilder messages = new StringBuilder();

        try {
            // イベントID
            if (!StringUtils.isEmpty(request.getEventId())) {
                messages.append("EventID");
                messages.append(COLON).append(request.getEventId()).append(DELIMITER);
            }
            // ユーザID
            if (!StringUtils.isEmpty(request.getUserId())) {
                messages.append("UserID");
                messages.append(COLON).append(request.getUserId()).append(DELIMITER);
            }
            // コンピュータ名
            if (!StringUtils.isEmpty(request.getComputerName())) {
                messages.append("ComputerName");
                messages.append(COLON).append(request.getComputerName()).append(DELIMITER);
            }
            // 画面ID
            if (!StringUtils.isEmpty(request.getScreenId())) {
                messages.append("ScreenID");
                messages.append(COLON).append(request.getScreenId()).append(DELIMITER);
            }
            // コマンドID
            if (!StringUtils.isEmpty(request.getCommandId())) {
                messages.append("CommandID");
                messages.append(COLON).append(request.getCommandId()).append(DELIMITER);
            }
            // メッセージID
            if (!StringUtils.isEmpty(messageId)) {
                messages.append("MessageID");
                messages.append(COLON).append(messageId).append(DELIMITER);
            }

            // クラス名
            messages.append("ClassName");
            messages.append(COLON).append(classNameLog).append(DELIMITER);
            // メソッド名
            messages.append("MethodName");
            messages.append(COLON).append(methodNameLog).append(DELIMITER);
            // メッセージ
            messages.append("Message");
            messages.append(COLON).append(message);
        } catch (Exception e) {
            // クラス名
            messages.append("ClassName");
            messages.append(COLON).append(classNameLog).append(DELIMITER);
            // メソッド名
            messages.append("MethodName");
            messages.append(COLON).append(methodNameLog).append(DELIMITER);
            // メッセージ
            messages.append("Message");
            messages.append(COLON).append(message);
        }

        return messages.toString();

    }

}
