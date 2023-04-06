package com.gnomes.common.constants;

import com.gnomes.common.constants.CommonEnums.MessageLevel;

/**
 * 共通 A101:システム定数 区分・コード用 定数クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                      -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class SystemDefConstants {

    /** 区分：MESSAGE */
    public static final String MESSAGE = "MESSAGE";

    /** 区分：SECRITY */
    public static final String SECURITY = "SECURITY";

    /** 区分：UPLOAD */
    public static final String UPLOAD = "UPLOAD";

    /** 区分：IMPORT_EXPORT */
    public static final String IMPORT_EXPORT = "IMPORT_EXPORT";

    /** 区分：UPLOAD_SUB_PATH */
    public static final String UPLOAD_SUB_PATH = "UPLOAD_SUB_PATH";

    /** 区分：MESSAGE コード：MAX_LIST_DISPLAY_COUNT */
    public static final String MESSAGE_MAX_LIST_DISPLAY_COUNT = "MAX_LIST_DISPLAY_COUNT";

    /** 区分：MAX_LIST_DISPLAY_COUNT */
    public static final String MAX_LIST_DISPLAY_COUNT = "MAX_LIST_DISPLAY_COUNT";

    /** 区分：ONE_PAGE_LIST_DISPLAY_COUNT */
    public static final String ONE_PAGE_LIST_DISPLAY_COUNT = "ONE_PAGE_LIST_DISPLAY_COUNT";

    /** 区分：MESSAGE コード：POPUP_DISPLAY_COUNT */
    public static final String MESSAGE_POPUP_DISPLAY_COUNT = "POPUP_DISPLAY_COUNT";

    /** 区分：MESSAGE コード：WATCH_PERIOD_FOR_POPUP */
    public static final String MESSAGE_WATCH_PERIOD_FOR_POPUP = "WATCH_PERIOD_FOR_POPUP";

    /** 区分：SECURITY コード：LOGIN_MODULE_TYPE */
    public static final String SECURITY_LOGIN_MODULE_TYPE = "LOGIN_MODULE_TYPE";

    /** 区分：UPLOAD コード：ROOT_PATH */
    public static final String UPLOAD_ROOT_PATH = "ROOT_PATH";

    /** 区分：UPLOAD コード：MAX_SIZE */
    public static final String UPLOAD_MAX_SIZE = "MAX_SIZE";

    /** 区分：IMPORT_EXPORT コード：CSV_SEPARATOR */
    public static final String IMPORT_EXPORT_CSV_SEPARATOR = "CSV_SEPARATOR";

    /** 区分：IMPORT_EXPORT コード：CSV_QUOTECHAR */
    public static final String IMPORT_EXPORT_CSV_QUOTECHAR = "CSV_QUOTECHAR";

    /** 区分：IMPORT_EXPORT コード：CSV_END_LINE */
    public static final String IMPORT_EXPORT_CSV_END_LINE = "CSV_END_LINE";

    /** 区分：IMPORT_EXPORT コード：CSV_CHARSET_NAME */
    public static final String IMPORT_EXPORT_CSV_CHARSET_NAME = "CSV_CHARSET_NAME";

    /** 区分：IMPORT_EXPORT コード：IMPORT_ERR_FILENAME_FORMAT */
    public static final String IMPORT_EXPORT_IMPORT_ERR_FILENAME_FORMAT = "IMPORT_ERR_FILENAME_FORMAT";

    /** 区分：IMPORT_EXPORT コード：EXCEL_FONT */
    public static final String IMPORT_EXPORT_EXCEL_FONT = "EXCEL_FONT";

    /** 区分：IMPORT_EXPORT コード：EXCEL97_HEADER_FILLFOREGROUND_COLORINDEX */
    public static final String IMPORT_EXPORT_EXCEL97_HEADER_FILLFOREGROUND_COLORINDEX = "EXCEL97_HEADER_FILLFOREGROUND_COLORINDEX";

    /** 区分：IMPORT_EXPORT コード：EXCEL_HEADER_FILLFOREGROUND_COLOR */
    public static final String IMPORT_EXPORT_EXCEL_HEADER_FILLFOREGROUND_COLOR = "EXCEL_HEADER_FILLFOREGROUND_COLOR";

    /** システム定義区分：外部I/F */
    public static final String TYPE_EXTERNAL_IF = "EXTERNAL_IF";

    /** システム定義コード：HULFTコマンド */
    public static final String CODE_HULFT_COMMAND = "HULFT_COMMAND";

    /**
     * システム定義コード：受信ファイルのデフォルトの文字コード
     */
    public static final String CODE_RECVFILE_DEFAULT_CHARSET = "RECVFILE_DEFAULT_CHARSET";

    /** システム定義区分：メール通知 */
    public static final String TYPE_MAIL_SERVER_INFO = "MAIL_SERVER_INFO";

    /** システム定義コード：差出人 */
    public static final String CODE_MAIL_ADDRESS_FROM = "MAIL_ADDRESS_FROM";

    /** システム定義コード：認証 ユーザID */
    public static final String CODE_AUTHENTICATED_USERID = "AUTHENTICATED_USERID";

    /** システム定義コード：認証 パスワード */
    public static final String CODE_AUTHENTICATED_PASSWORD = "AUTHENTICATED_PASSWORD";

    /** システム定義コード：エンコード */
    public static final String CODE_ENCODE = "ENCODE";

    /** システム定義コード：パスワード変更時メール通知フラグ */
    public static final String PASSWORD_CHANGE_MAIL_FLAG = "PASSWORD_CHANGE_MAIL_FLAG";

    /** システム定義コード：認証 ユーザID（パスワード変更用） */
    public static final String CODE_AUTHENTICATED_USERID_CHANGE_PASSWORD = "AUTHENTICATED_USERID_CHANGE_PASSWORD";

    /** システム定義コード：認証 パスワード（パスワード変更用） */
    public static final String CODE_AUTHENTICATED_PASSWORD_CHANGE_PASSWORD = "AUTHENTICATED_PASSWORD_CHANGE_PASSWORD";

    /** システム定義コード：メールアドレス FROM（パスワード変更用） */
    public static final String CODE_MAIL_ADDRESS_FROM_CHANGE_PASSWORD = "MAIL_ADDRESS_FROM_CHANGE_PASSWORD";

    /** システム定義区分：帳票ラベル印字履歴ファイル */
    public static final String TYPE_HISTORY_PRINTOUT_FILE = "HISTORY_PRINTOUT_FILE";

    /** システム定義コード：登録フラグ */
    public static final String CODE_HISTORY_PRINTOUT_FILE_INSERT_FLAG = "INSERT_FLAG";

    /** システム定数区分：参照先選択プルダウン */
    public static final String SYSTEM_DEFINE_TYPE_SELECT_REGION_TYPE = "SELECT_REGION_TYPE";

    /** システム定数区分：参照先選択プルダウン */
    public static final String TYPE_CTAG_TABLE_DEFAULT = "CTAG_TABLE_DEFAULT";

    /** システム定義コード：表示件数プルダウンの選択候補値 */
    public static final String CODE_DISPLAY_COUNT_PULLDOWN_ITEM = "DISPLAY_COUNT_PULLDOWN_ITEM";

    /** システム定義コード：ログイン時の拠点選択設定 */
    public static final String CODE_SITE_SELECTABLE_SETTING = "SITE_SELECTABLE_SETTING";

    /** システム定義コード：バリデーション */
    public static final String VALIDATOR = "VALIDATOR";

    /** システム定義コード：禁則文字列 */
    public static final String PROHIBITION_STRING = "PROHIBITION_STRING";

    /** システム定義コード：工程端末ログオン時の端末ID入力方法 */
    public static final String OPERATE_COMPUTER_INPUT_FLAG = "OPERATE_COMPUTER_INPUT_FLAG";

    /** システム定義コード：端末ID入力分類 フラグ */
    public static final String COMPUTER_INPUT_FLAG = "COMPUTER_INPUT_FLAG";

    /** システム定義コード：ソフトウェアのバージョン*/
    public static final String SOFTWARE_VERSION = "SOFTWARE_VERSION";

    /** システム定義コード：ソフトウェアのバージョン番号*/
    public static final String VERSION_NUMBER = "VERSION_NUMBER";

    /** システム定義コード：メールサーバーホスト名*/
    public static final String MAIL_SERVER_HOST = "MAIL_SERVER_HOST";

    /** システム定義コード：メールサーバーポート番号*/
    public static final String MAIL_SERVER_PORT = "MAIL_SERVER_PORT";

    /** システム定義コード：ロケール選択プルダウンの選択候補値 */
    public static final String SYSTEM_LOCALE = "SYSTEM_LOCALE";

    /** システム定義コード：ロケール選択プルダウンの選択候補値 */
    public static final String CODE_LOCALE_TYPE = "LOCALE_TYPE";

    /** システム定数コード：メールサーバ処理種別 mailSendDiv*/
    public static final String MAIL_SEND_DIV = "MAIL_SEND_DIV";

    /** システム定数コード：送受信ファイルの改行コード区分 */
    public static final String CODE_FILE_DEFAULT_DELIMITER = "FILE_DEFAULT_DELIMITER";

    /** システム定義コード：検索条件必須項目の詳細検索削除ボタン表示有無 */
    public static final String CONDITION_REQUIRED = "CONDITION_REQUIRED";

    /** システム定義コード：検索条件必須項目の詳細検索削除ボタン表示無し */
    public static final String CODE_DISP_DELETE_BUTTON = "DISP_DELETE_BUTTON";

    /**
     * 区分：MESSAGE コード一覧
     */
    public enum MessageDefinitionCode {

        MAX_LIST_DISPLAY_COUNT,

        POPUP_DISPLAY_COUNT,

        WATCH_PERIOD_FOR_POPUP;

        public static MessageLevel getEnum(int num) {
            // enum型全てを取得します。
            MessageLevel[] enumArray = MessageLevel.values();

            // 取得出来たenum型分ループします。
            for(MessageLevel enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 区分：SECURITY コード一覧
     */
    public enum SecurityDefinitionCode {

        LOGIN_MODULE_TYPE;

        public static MessageLevel getEnum(int num) {
            // enum型全てを取得します。
            MessageLevel[] enumArray = MessageLevel.values();

            // 取得出来たenum型分ループします。
            for(MessageLevel enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }
}
