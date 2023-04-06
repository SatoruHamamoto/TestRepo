package com.gnomes.common.constants;

/**
 * クエリ 定数クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * R0.01.01 2019/07/31 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesQueryConstants {

    /** ユーザアカウントセキュリティポリシー取得 */
    public static final String QUERY_NAME_BLSECURITY_MSTR_PERSON_SEC_POLICY = "BLSecurity.MstrPersonSecPolicy";

    /** ユーザ取得 */
    public static final String QUERY_NAME_BLSECURITY_MSTR_PERSON = "BLSecurity.MstrPerson";

    /** パスワード禁止文字取得 */
    public static final String QUERY_NAME_BLSECURITY_MSTR_INVALID_PASSWD = "BLSecurity.MstrInvalidPasswd";

    /** アップロードファイル管理取得 */
    public static final String QUERY_NAME_BLFILEMANAGET_UPLOAD_FILE = "BLFileManager.UploadFile";

    /** アップロードファイル管理(条件 システム名)取得 */
    public static final String QUERY_NAME_BLFILEMANAGET_UPLOAD_FILE_SYS = "BLFileManager.UploadFileSys";

    /** 外部I/F送信状態キュー取得 */
    public static final String QUERY_NAME_QUEUE_EXTERNAL_IF_SEND_STATUS = "BLFileTransfer.QueueExternalIfSendStatus";

    /** 外部I/F送信データ詳細取得 */
    public static final String QUERY_NAME_EXTERNAL_IF_SEND_DATA_DETAILIL = "BLFileTransfer.ExternalIfSendDataDetail";

    /** 外部I/F送信ファイル連番管理取得 */
    public static final String QUERY_NAME_EXTERNAL_IF_SEND_FILE_SEQ_NO = "BLFileTransfer.ExternalIfSendFileSeqNo";

    /** 外部I/Fデータファイル送受信実績詳細取得 */
    public static final String QUERY_NAME_GET_EXT_IF_DATA_SR_ACTUAL_DETAIL = "BLFileTransfer.GetExtIfDataSrActualDetail";

    /** 外部I/F受信キュー取得 */
    public static final String QUERY_NAME_QUEUE_EXTERNAL_IF_RECV = "BLFileTransfer.QueueExternalIfRecv";

    /** ブックマーク管理取得(Screen) */
    public static final String QUERY_NAME_BLBOOKMARK_BOOKMARK_USER_SCREEN = "BLBookMark.BookMarkUserScreen";
    
    /** ブックマーク管理取得(Parameter) */
    public static final String QUERY_NAME_BLBOOKMARK_BOOKMARK_USER_PARAMETER = "BLBookMark.BookMarkUserParameter";

    /** ブックマーク管理取得 */
    public static final String QUERY_NAME_BLBOOKMARK_BOOKMARK_USER = "BLBookMark.BookMarkUser";

    /** テーブル検索条件管理取得 */
    public static final String QUERY_NAME_TABLE_SEARCH_SETTING_USER_TABLE_TYPE = "TableSearchSetting.UserTableType";

    /** パトランプリスト */
    public static final String QUERY_NAME_GET_MSTR_PATLAMP_LIST = "GetMstrPatlampList";

    /** パトランプモデルリスト */
    public static final String QUERY_NAME_GET_MSTR_PATLAMP_MODEL_LIST = "GetMstrPatlampModelList";

    /** 端末情報取得 */
    public static final String QUERY_NAME_GET_INFO_COMPUTER = "GetInfoComputer";

    /** ユーザ情報取得 */
    public static final String QUERY_NAME_BLSECURITY_INFO_USER = "BLSecurity.InfoUser";

    /** 端末工程作業場所選択情報取得 */
    public static final String QUERY_NAME_BLSECURITY_INFO_COMPUTER_PROC_WORKCELL = "BLSecurity.InfoComputerProcWorkcell";

    /** パスワード変更履歴取得 */
    public static final String QUERY_NAME_BLSECURITY_HIST_CHANGE_PASSWORD = "BLSecurity.HistChangePassword";

    /** メッセージ一覧取得 */
    public static final String QUERY_NAME_GET_MESSAGE_LIST = "GetMessageList";

    /** メッセージ情報発生件数取得 */
    public static final String QUERY_NAME_GET_MESSAGE_OCCUR_CNT = "GetMessageOccurCnt";

    /** ポップアップメッセージ取得 */
    public static final String QUERY_NAME_GET_POPUP_MESSAGE_LIST = "GetPopupMessageList";

    /** メッセージ件数取得 */
    public static final String QUERY_NAME_GET_MESSAGE_CNT = "GetMessageCnt";

	/** メール送信件数取得 */
    public static final String QUERY_NAME_GET_SEND_MAIL_COUNT = "GetSendMailCount";

    /** メール通知キューリスト取得 */
    public static final String QUERY_NAME_GET_TMP_QUEUE_MAIL_NOTICE_LIST = "GetTmpQueueMailNoticeList";

    /** 権限基本チェック */
    public static final String QUERY_NAME_JUDGE_PERSON_LICENSE_COUNT = "JudgePersonLicenseCount";

    /** 代替ユーザを含む権限チェック */
    public static final String QUERY_NAME_JUDGE_PERSON_LICENSE_SUBSTITUTE_COUNT = "JudgePersonLicenseSubstituteCount";

    /** ダブル認証の権限チェック */
    public static final String QUERY_NAME_JUDGE_PERSON_LICENSE_DOUBLE_CHECK_COUNT = "JudgePersonLicenseDoubleCheckCount";

    /** 権限基本チェック(作業場所含む) */
    public static final String QUERY_NAME_JUDGE_PERSON_LICENSE_COUNT_ADD_WORK_CELL = "JudgePersonLicenseCountAddWorkCell";

    /** 代替ユーザを含む権限チェック(作業場所含む) */
    public static final String QUERY_NAME_JUDGE_PERSON_LICENSE_SUBSTITUTE_COUNT_ADD_WORK_CELL = "JudgePersonLicenseSubstituteCountAddWorkCell";

    /** ダブル認証の権限チェック (作業場所含む)*/
    public static final String QUERY_NAME_JUDGE_PERSON_LICENSE_DOUBLE_CHECK_COUNT_ADD_WORK_CELL = "JudgePersonLicenseDoubleCheckCountAddWorkCell";

    /** 帳票印字結果状態実行中タイムアウト時エラー変更 */
    public static final String QUERY_NAME_UPDATE_PRINTOUT_STATUS_TIMEOUT_ERROR = "UpdatePrintoutStatusTimeoutError";

    /** メッセージ監視機能用Key一覧取得 */
    public static final String QUERY_NAME_GET_MESSAGE_WATCHER_SEARCH_LIST = "GetMessageWatcherSearchList";

    /** 画面に権限があるボタンリスト取得 */
    public static final String QUERY_NAME_GET_SCREEN_ENABLE_BUTTON_LIST = "GetScreenEnableButtonList";

}
