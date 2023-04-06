package com.gnomes.external.command;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.external.logic.WeighCycle;
import com.gnomes.uiservice.ContainerRequest;

/**
 * WeighWebSocket通信クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/07/05 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@ServerEndpoint("/weighWebSocket")
public class WeighWebSocket {

    static final String className = "WeighWebSocket";

    static final String logmsg_clientClosed = "WebSocet: Client Close Recieved: WeighID = ";
    static final String logmsg_clientOpened = "WebSocet: Client Open Recieved: URL = ";

    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected ContainerRequest req;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    @Inject
    protected WeighCycle weighCycle;

    /** webSocketSession. */
    protected Session webSocketSession;

    /** 秤量器ID. */
    protected String weighApparatusId;

    /** WebSocket通信中リスト. */
    protected static CopyOnWriteArrayList<String> weighWebSocketList;

    /**
     * static初期化.
     */
    static {
        weighWebSocketList = new CopyOnWriteArrayList<>();
    }

    /**
     * （定周期）クライアントからの接続時に呼び出されるメソッド.
     * <pre>
     * サーバー側の処理を実装し、クライアント側へ処理結果を返却する。
     * </pre>
     *
     * @param session クライアントの接続情報
     * @param ec 設定情報
     */
    @OnOpen
    protected void onOpen(Session session, EndpointConfig ec) {

        final String methodName="onOpen";
        this.webSocketSession = session;
        this.weighApparatusId = "";

        try {
            this.logHelper.fine(this.logger, className, methodName, "クライアントからOnOpenが来ました");

            logHelper.info(this.logger, className, methodName, logmsg_clientOpened + session.getRequestURI().toString());

            // 接続状態フラグに true を設定
            this.weighCycle.setConnectionState(this.weighApparatusId, true);

            // メッセージをクライアント側に送信
            session.getBasicRemote().sendText(CommonConstants.WEB_SOCKET_STATUS_CONNECT);

        } catch (IOException e) {
            // (ME01.0082) WebSocketの通信に失敗しました。
            GnomesException ex = this.exceptionFactory.createGnomesException(
                    e, GnomesMessagesConstants.ME01_0082);
            // ログ出力
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, ex));

        }

    }

    /**
     * （定周期）クライアント側からメッセージ受信時の処理.
     * <pre>
     * クライアント側からメッセージの受信が行われた際に、呼び出される。
     * 接続状態フラグにtrueを設定する。
     * </pre>
     * @param msg クライアントから送信されたテキスト
     */
    @OnMessage
    protected void receiveMessage(String msg) {
        final String methodName = "receiveMessage";

        try {
            this.logHelper.fine(this.logger, className, methodName, "クライアントからreceiveMessage(" + msg + ")が来ました");


            // 受信完了時
            if (CommonConstants.WEB_SOCKET_STATUS_RECEIVE.equals(msg)) {

                this.logHelper.fine(this.logger, className, methodName, "RECEIVEが来たので、通信状態フラグをtrueにします。");

                // 接続状態フラグに true を設定
                this.weighCycle.setConnectionState(this.weighApparatusId, true);

                this.logHelper.fine(this.logger, className, methodName, "RECEIVERECEIVEが来たので、スレッド停止要求も解除します。");

                this.weighCycle.resetThreadStopRequest(this.weighApparatusId);

                String receiveData =
                        this.weighCycle.getWeighCycleReceiveData(this.weighApparatusId);

                this.logHelper.fine(this.logger, className, methodName, "現在値は[" + receiveData + "]です。クライアントに返送します");

                this.sendMessage(receiveData);

                return;

            } else if (CommonConstants.WEB_SOCKET_STATUS_STOP.equals(msg)) {

                this.logHelper.fine(this.logger, className, methodName, "STOPが来たので、通信状態フラグをfalaseにします。");

                // WebSocket接続状態設定に false を設定
                this.weighCycle.setConnectionState(this.weighApparatusId, false);

            } else {

                // 初回受信時

                // 秤量器IDを設定
                this.weighApparatusId = msg;

                this.logHelper.fine(this.logger, className, methodName, "その他が来たので、weighWebSocketListから秤量器ID(" + msg +")を探します");

                if (weighWebSocketList.contains(msg)) {

                    this.logHelper.fine(this.logger, className, methodName, "weighWebSocketListに見つかったので、接続状態フラグに true を設定。");

                    // 接続状態フラグに true を設定
                    this.weighCycle.setConnectionState(this.weighApparatusId, true);

                    //使用中でもオンラインで設定して続行出来るようにする
                    String receiveData =
                            this.weighCycle.getWeighCycleReceiveData(this.weighApparatusId);

                    this.logHelper.fine(this.logger, className, methodName, "現在値は[" + receiveData + "]です。クライアントに返送します");

                    this.sendMessage(receiveData);

                    return;

                } else {
                    this.logHelper.fine(this.logger, className, methodName, "weighWebSocketListの中に存在しなかったのでWebSocket通信中リスト追加");
                    // WebSocket通信中リスト追加
                    weighWebSocketList.add(this.weighApparatusId);
                }

            }

        } catch (Exception e) {

            this.logHelper.fine(this.logger, className, methodName, "Exceptionが来ました。" + e.getMessage());

            // 接続状態フラグをOFFにする。クライアントを閉じたり開きなおす度に呼ばれる
            // 所なので、この場でOFFにして定周期側がクライアント切断を検知する
            this.weighCycle.setConnectionState(this.weighApparatusId, false);


            // 定周期処理スレッド強制停止要求
            this.logHelper.fine(this.logger, className, methodName, "周期処理スレッド強制停止要求 : " + this.weighApparatusId);
            this.weighCycle.setThreadStopRequest(this.weighApparatusId);

            // (ME01.0082) WebSocketの通信に失敗しました。
            GnomesException ex = this.exceptionFactory.createGnomesException(
                    e, GnomesMessagesConstants.ME01_0082);
            // ログ出力
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, ex));
        }

    }

    /**
     * （定周期）クライアント切断時の処理.
     * <pre>
     * クライアントが切断された際の処理を実装する。
     * 接続状態フラグにfalseを設定する。
     * </pre>
     * @param session クライアントの接続情報
     * @param reason 切断理由
     */
    @OnClose
    protected void onClose(Session session, CloseReason reason) {

        final String methodName = "onClose";
        this.logHelper.fine(this.logger, className, methodName, "クライアントから切断されました");

        try {
            logHelper.info(this.logger, className, methodName, logmsg_clientClosed + this.weighApparatusId);
            // 接続状態フラグ
            this.logHelper.fine(this.logger, className, methodName, "通信状態フラグをfalseにします");
            this.weighCycle.setConnectionState(this.weighApparatusId, false);

        } catch (Exception e) {
            GnomesException ex = this.exceptionFactory.createGnomesException(
                    e, GnomesMessagesConstants.ME01_0082);
            // ログ出力
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, ex));
        } finally {
            this.logHelper.fine(this.logger, className, methodName, "weighWebSocketListから " + this.weighApparatusId + " のエントリを外します");
            weighWebSocketList.remove(this.weighApparatusId);
        }

    }

    /**
     * （定周期）エラー発生時の処理.
     * <pre>
     * エラー発生時の処理を実装する。
     * 接続状態フラグにfalseを設定する。
     * </pre>
     */
    @OnError
    protected void onError(Throwable t) {

        GnomesException ex = this.exceptionFactory.createGnomesException(
                t, GnomesMessagesConstants.ME01_0082);
        // ログ出力
        this.logHelper.severe(this.logger, null, null,
                MessagesHandler.getExceptionMessage(this.req, ex), t);

        // 接続状態フラグ
        try {
            this.weighCycle.setConnectionState(this.weighApparatusId, false);
        } catch (Exception e) {
            ex = this.exceptionFactory.createGnomesException(
                    e, GnomesMessagesConstants.ME01_0082);
            // ログ出力
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, ex), e);
        } finally {
            weighWebSocketList.remove(this.weighApparatusId);
        }

    }

    /**
     * 受信データをクライアントに送信.
     * @param message メッセージ
     */
    protected void sendMessage(String message) {

        try {
            // 受信データをクライアントに送信
            this.webSocketSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            // (ME01.0082) WebSocketの通信に失敗しました。
            GnomesException ex = this.exceptionFactory.createGnomesException(
                    e, GnomesMessagesConstants.ME01_0082);
            this.logHelper.severe(this.logger, null, null,
                    MessagesHandler.getExceptionMessage(this.req, ex));

        }
    }

}
