package com.gnomes.common.constants;

import java.math.BigDecimal;

/**
 * 共通  定数クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class CommonConstants {

    /** アプリケーション名 */
    public static final String APP_NAME = "java:app/AppName";

    /** Persistence ユニット名（通常領域） */
    public static final String PERSISTENCE_UNIT_NAME_NON_JTA = "gnomes-ui_nonjta";

    /** Persistence ユニット名（保管領域） */
    public static final String PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE = "gnomes-ui_nonjta-storage";

    /** ログイン画面表示用コマンド名 */
    public static final String LOGIN_COMMAND_NAME = "A01001C001";

    /** エラーページ */
    public static final String PATH_ERR_PAGE = "/jsp/gnomes_err.jsp";

    /** 作業依頼キー */
    public static final String OPERATIONS_REQUEST_KEY = "operations_request_key";

    /** 作業依頼ID */
    public static final String OPERATIONS_REQUEST_ID = "operations_request_id";

    /** 作業タイプ */
    public static final String OPERATIONS_TYPE = "operations_type";

    /** 作業定義キー */
    public static final String OPERATIONS_DEFINITION_KEY = "operations_definition_key";

    /** 作業定義ID */
    public static final String OPERATIONS_DEFINITION_ID = "operations_definition_id";

    /** 品目キー */
    public static final String ARTICLE_KEY = "article_key";

    /** ブロードキャスト先コンピュータ名 */
    public static final String BROADCASTCOMPUTERNAME = "broadcastcomputername";

    /** ロケール文字列の区切り文字 */
    public static final String SPLIT_CHAR = "_";

    /** ユーザID */
    public static final String USER_ID = "userid";

    /** エリアID */
    public static final String AREA_ID = "area_id";

    /** エリア名 */
    public static final String AREA_NAME = "area_name";

    /** 発生日の表示フォーマットリソース */
    public static final String RES_OCCURDATE_FORMAT = "YY01.0001";

    /** エラーメッセージ：セッション取得時 */
    public static final String ERR_MESSAGE_NO_SESSION = "UT010033: No session";

    /** ブロードキャスト有無 */
    public static final String ISBROADCASTMESSAGE = "isbroadcastmessage";

    /** ipアドレス */
    public static final String IP_ADDRESS = "ipaddress";

    /** サイトキー */
    public static final String SITE_KEY = "site_key";

    /** 区分 */
    public static final String SYSTEM_CONSTANT_CLASS = "system_constant_class";

    /** コード */
    public static final String DEFINITION_CODE = "definitioncode";

    /** JDBC */
    public static final String JDBC = "JDBC";

    /** LDAP認証 */
    public static final String LDAP = "LDAP";

    /** 工程コード全て */
    public static final String PROCESS_CODE_ALL = "-";

    /** タイムゾーン:協定世界時(GMT) */
    public static final String ZONEID_GMT = "GMT";

    /** タイムゾーン:協定世界時(UTC) */
    public static final String ZONEID_UTC = "UTC";

    /** ConversationScopedのタイムアウト(デフォルト１０分) */
    public static final long CONVERSATION_TIME_OUT_DEFAULT = 600000;//1728000000;

    /** 監査証跡を行うか否か */
    public static final Boolean IS_AUDIT_TRAIL = true;

    /** @Alternativeによる優先度：gnomes */
    public static final int GNOMESINTERCEPTOR_PLATFORM = 1000;

    /** @Alternativeによる優先度：contents */
    public static final int GNOMESINTERCEPTOR_CONTENTS = 2000;

    /** @Alternativeによる優先度：job */
    public static final int GNOMESINTERCEPTOR_JOBCUSTOMIZE = 3000;

    /** RSクライアントプロパティ：タイムアウト */
    public static final String RSCLIENT_PROPERTY_TIMEOUT = "com.ibm.ws.jaxrs.client.timeout";

    /** RSクライアントプロパティ：HTTPSプロトコル */
    public static final String RSCLIENT_HTTP_PROTOCOLS = "https.protocols";

	/** RSクライアントプロパティ：セキュア・ソケット・プロトコル TLSv1.2 (Java 8) */
    public static final String RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS = "TLSv1.2";

    /** RSクライアントプロパティ：セキュア・ソケット・プロトコル SSL (Java 6) */
    public static final String RSCLIENT_SECURE_SOCKET_PROTOCOL_SSL = "SSL";

    /** WebSocket状態：接続 */
    public static final String WEB_SOCKET_STATUS_CONNECT = "CONNECT";

    /** WebSocket状態：受信 */
    public static final String WEB_SOCKET_STATUS_RECEIVE = "RECEIVE";

    /** WebSocket状態：接続停止 */
    public static final String WEB_SOCKET_STATUS_STOP = "STOP";

    /** デフォルト処理待機時間(ミリ秒) */
    public static final int DEFAULT_SLEEP_TIME = 100;

    /** リモート接続フラグON */
    public static final String REMOTE_FLAG_ON = "1";

    /** 作成日時 */
    public static final String OCCURRENCE_DATE = "occurrence_date";

    /** ファイル種別 */
    public static final String DATA_TYPE = "data_type";

    /** 送信状態 */
    public static final String SEND_STATE = "send_state";

    /** 受信状態 */
    public static final String RECV_STATE = "recv_state";

    /** 送信状態キー */
    public static final String SEND_STATE_KEY = "send_state_key";

    /** 外部I/F受信キューキー */
    public static final String EXTERNAL_IF_RECV_QUE_KEY = "external_if_recv_que_key";

    /** 送受信区分 */
    public static final String SEND_RECV_TYPE = "send_recv_type";

    /** ステータス */
    public static final String STATE = "state";

    /** 行ステータス */
    public static final String LINE_STATE = "line_state";

    /** 外部I/Fデータキー */
    public static final String EXTERNAL_IF_DATA_KEY = "external_if_data_key";

    /** 外部I/F対象システムコード */
    public static final String EXTERNAL_TARGET_CODE = "external_target_code";

    /** 定数：小数点以下有効桁数デフォルト値 */
    public static final int DEFAULT_SCALE = 5;

    /** 定数：丸め方法デフォルト値(四捨五入) */
    public static final int DEFAULT_ROUNDINGMODE = BigDecimal.ROUND_HALF_UP;

    /** メッセージテーブル メッセージ補足情報 最大項目数 */
    public static final int MESSAGE_PARAMETER_MAX_COLUMN = 10;

    /** メッセージテーブル メッセージ補足情報 最大桁数 */
    public static final int MESSAGE_PARAMETER_MAX_VALUE = 2000;

    /** 受信要求JOB */
    public static final String RECV_REQUEST_JOBS = "recv_request_jobs";

    /** 受信定周期処理JOB */
    public static final String RECV_PROC_JOBS = "recv_proc_jobs";

    /** 受信定周期処理後ファイル移動JOB */
    public static final String RECV_PROC_JOBS_MOVE_FILE = "recv_proc_jobs_move_file";

    /** 受信状態変更JOB */
    public static final String RECV_CHANGESTATE_JOBS = "recv_changestate_jobs";

    /** 送信要求JOB */
    public static final String SEND_REQUEST_JOBS = "send_request_jobs";

    /** ファイル送信処理JOB */
    public static final String SEND_PROC_JOBS = "send_proc_jobs";

    /** 送信結果JOB */
    public static final String SEND_RESULT_JOBS_MOVE_FILE = "send_result_jobs_move_file";

    /** 送信状態変更JOB */
    public static final String SEND_CHANGESTATE_JOBS_MOVE_FILE = "send_changestate_jobs_move_file";

    /** 受信要求エラーコメント */
    public static final String RUN_RECV_REQUEST_JOBS = "runRecvRequestJobs";

    /** 受信定周期処理エラーコメント */
    public static final String RUN_RECV_PROC_JOBS = "runRecvProcJobs";

    /** 受信状態変更エラーコメント */
    public static final String RUN_RECV_CHANGE_STATE_JOBS = "runRecvChangeStateJobs";

    /** 送信要求エラーコメント */
    public static final String RUN_SEND_REQUEST_JOBS = "runSendRequestJobs";

    /** ファイル送信処理エラーコメント */
    public static final String RUN_SEND_PROC_JOBS = "runSendProcJobs";

    /** 送信エラー処理コメント */
    public static final String RUN_SEND_ERR_JOBS = "runSendErrorJobs";

    /** 送信結果エラーコメント */
    public static final String RUN_SEND_RESULT_JOBS = "runSendResultJobs";

    /** 送信状態変更エラーコメント */
    public static final String RUN_SEND_CHANGE_STATE_JOBS = "runSendChangeStateJobs";

    /** 受信データマッピングクラス名 */
    public static final String SEND_DATA_MAPPING_JOB = "SendDataMappingJob";

    /** 受信データマッピングクラスのメソッド名 */
    public static final String PROCESS_NAME = "process";

    /** 送信データヘッダ作成クラス名 */
    public static final String SEND_DATA_CREATE_HEADER = "SendDataCreateHeader";

    /** ContainerRequest */
    public static final String CONTAINER_REQUEST = "req";

    /** 送信データ詳細テーブル */
    public static final String SEND_DATA_DETAILS = "sendDataDetails";

    /** リダイレクトURL */
    public static final String REDIRECT_URL = "/UI/gnomes";

    /** 定数：ピリオド */
    public static final String PERIOD = ".";

    /** 定数：カンマ */
    public static final String COMMA = ",";

    /** 定数：コロン */
    public static final String COLON = "：";

    /** 定数：テーブル名 */
    public static final String TABLE_NAME = "table_name：";

    /** 定数：カラム名 */
    public static final String COLUMN_NAME = "column_name：";

    /** 定数：値 */
    public static final String VALUE = "value：";

    /** 定数：リクエストエラーキー セパレーター */
    public static final String REQUEST_ERR_KEY_SEPARATOR = ",tableIndex=";

    /** 定数：CheckBox checked */
    public static final Integer DTO_CHECK_BOX_CHECKED_VALUE = 1;

    /** 定数：CheckBox no checked */
    public static final Integer DTO_CHECK_BOX_NO_CHECKED_VALUE = 0;

    /** 定数:コマンドの入力チェック対象項目のリストフィールド名セパレータ */
    public static final String COMMAND_INPUT_CHECK_LIST_FIELDNAME_SEPARATOR = "ListFieldName:";

    /** 定数：画面共通コマンド */
    public static final String SCREEN_COMMON_COMMAND = "ScreenCommonCommand";

    /** 定数：サービス共通コマンド */
    public static final String SERVICE_COMMON_COMMAND = "ServiceCommonCommand";

    /** 拠点コード全て */
    public static final String SITE_CODE_ALL = "-";

    /** 指図工程コード全て */
    public static final String ORDER_PROCESS_CODE_ALL = "-";

    /** 作業工程コード全て */
    public static final String WORK_PROCESS_CODE_ALL = "-";

    /** 作業場所コード全て */
    public static final String WORK_CELL_CODE_ALL = "-";

    /** 権限ID全て */
    public static final String PRIVILEGE_ID_ALL = "-";

    /** 全件取得 */
    public static final String FIND_ALL = ".findAll";

    /** パトランプID */
    public static final String PATLAMP_ID = "patlamp_id";

    /** パトランプ機種ID */
    public static final String PATLAMP_MODEL_ID = "patlamp_model_id";

    /** ログイン画面(管理端末)表示用URL */
    public static final String LOGIN_CLIENT_URL = "/UI/gnomes-manage";

    /** ログイン画面(工程端末)表示用URL */
    public static final String LOGIN_PANECON_URL = "/UI/gnomes-operate";

    /** 送信処理要求 */
    public static final String SEND_PROC = "sendProc";

    /** 送信処理要求 */
    public static final Integer SR_ACTUAL_DETAIL_COMMENT_MAX_LENGTH = 1000;

    /** システム定数マスタに定義されていない場合における外部I/F受信テキストのデフォルト文字コード*/
    public static final String RECVFILE_DEFAULT_CHARSET = "MS932";

    /** 時刻のデフォルト値 (時分）*/
    public static final String DEFAULT_TIMESTRING = "00:00";

    /**
     * バッチ処理や内部処理で動くユーザIDの固定文字
     */
    public static final String USERID_SYSTEM = "SYSTEM";

    /**
     * バッチ処理や内部処理で動くユーザ名の固定文字
     */
    public static final String USERNAME_SYSTEM  ="SYSTEM";

    /**
     * ローカルホストのIPアドレス（固定）
     */
    public static final String IPADDRESS_LOCALHOST = "127.0.0.1";

    /**
     * ローカルホストのコンピュータID(SF)
     */
    public static final String COMPUTERID_LOCALHOST_SF = "localhost(SF)";

    /**
     * ローカルホストのコンピュータID
     */
    public static final String COMPUTERID_LOCALHOST = "localhost";

    /**
     * Talendコンテキストパラメータ用接頭文字列
     */
    public static final String TALEND_CONTEXT_PARAM_HEADER = "--context_param ";

    /**
     * 外部I/Fデフォルト改行コード
     */
    public static final String FILE_DEFAULT_DELIMITER = "\r\n";

    /**
     * 、SYSTEM_BATCH
     */
    public static final String USERNAME_BATCH= "SYSTEM_BATCH";

    /**
     * 秤量インジケータの表示間隔(ms)デフォルト2秒
     */
    public static final Long CYCLIC_WEIGH_INTERVAL_MILISECOND=(long)2000;

    /**
     * 選択したロケールでファイルを切り替える為の拡張子との区切り文字
     */
    public static final String SPLIT_EXTENSION_LOCALE = "\\.";

    /**
     * 選択したロケールでファイルを切り替えのマジックナンバー
     */
    public static final String REPLACE_LOCALE = "LOCALE";

    /**
     * 選択したロケールでファイルを切り替えの結合記号
     */
    public static final String JOIN_LOCALE = "-";

    /**
     * テーブルカスタムタグ検索条件指定専用固定日付時刻フォーマット文字列(年月）)
     */
    public static final String TABLE_SEARCH_DATE_YM = "yyyy/MM";

    /**
     * テーブルカスタムタグ検索条件指定専用固定日付時刻フォーマット文字列(年月日）)
     */
    public static final String TABLE_SEARCH_DATE = "yyyy/MM/dd";

    /**
     * テーブルカスタムタグ検索条件指定専用固定日付時刻フォーマット文字列(年月日時間（分まで）)
     */
    public static final String TABLE_SEARCH_DATE_TIME = "yyyy/MM/dd HH:mm";

    /**
     * テーブルカスタムタグ検索条件指定専用固定日付時刻フォーマット文字列(年月日時間（秒まで）)
     */
    public static final String TABLE_SEARCH_DATE_TIME_SS = "yyyy/MM/dd HH:mm:ss";
    
    /**
     * テーブルカスタムタグ検索条件指定専用固定日付時刻フォーマット文字列(年月日時間（分まで、分は00固定）)
     */
    public static final String TABLE_SEARCH_DATE_TIME_MM00 = "yyyy/MM/dd HH:00";
    /**
     * テーブルカスタムタグ検索条件指定専用固定日付時刻フォーマット文字列(年月日時間（秒まで、秒は00固定）)
     */
    public static final String TABLE_SEARCH_DATE_TIME_SS00 = "yyyy/MM/dd HH:mm:00";

    /** ノーブレークスペース */
    public static final char NON_BREAKING_SPACE= '\u00a0';
    
    /**
     * 受信ファイルBOM判定用文字列
     */
    public static final String BOM = "\uFEFF";

    /**
     * 送受信時作成ファイル文字コードBOM判定用文字列
     */
    public static final String CREATE_FILE_ENCODE_BOM = "_B";
}
