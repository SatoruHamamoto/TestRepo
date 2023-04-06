package com.gnomes.common.constants;

import java.util.HashMap;

import com.gnomes.common.resource.GnomesResourcesConstants;

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
public class CommonEnums {

//    システム定義マスターで必要なロケールを制御する為コメントアウト
//    ロケールの増減で値を変更しないといけないので不整合になる可能性がある。
//    public enum GnomesLocale{
//        zh_CN("zh_CN"),  // ロケール：中国語 - 簡体字
//        zh_TW("zh_TW"),  // ロケール：中国語 - 繁体字
//        en_US("en_US"),  // ロケール：英語
//        fr_FR("fr_FR"),  // ロケール：フランス語
//        de_DE("de_DE"),  // ロケール：ドイツ語
//        it_IT("it_IT"),  // ロケール：イタリア語
//        ja_JP("ja_JP"),  // ロケール：日本語
//        ko_KR("ko_KR"),  // ロケール：韓国語
//        pt_BR("pt_BR"),  // ロケール：ポルトガル語 - ブラジル
//        es_ES("es_ES"),; // ロケール：スペイン語
//
//        private final String text;
//
//        private GnomesLocale(final String text) {
//          this.text = text;
//        }
//
//        /**
//         * 設定された値を返す
//         */
//        @Override
//        public String toString() {
//          return this.text;
//        }
//
//        /**
//         * 大文字に変換する
//         */
//        public String toReverseUpperCase() {
//            return reverseUpperCase(this.text);
//        }
//    }

    /**
     * メッセージ分類
     */
    public enum MessageType {
        /** 操業メッセージ */
        MessageType_Process(1),
        /** 操作メッセージ */
        MessageType_Operation(2),
        /** 確認メッセージ */
        MessageType_Confirm(3);

        private int value;

        private MessageType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageType getEnum(int num) {
            // enum型全てを取得します。
            MessageType[] enumArray = MessageType.values();

            // 取得出来たenum型分ループします。
            for(MessageType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * メッセージソース
     */
    public enum MessageSource {
        /** クライアント */
        MessageSource_Client(1),
        /** パネコン */
        MessageSource_Panecom(2),
        /** システム内部 */
        MessageSource_SystemIn(3),
        /** システム外部 */
        MessageSource_SystemOut(4),
        /** その他 */
        MessageSource_Other(99);

        private int value;

        private MessageSource(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageSource getEnum(int num) {
            // enum型全てを取得します。
            MessageSource[] enumArray = MessageSource.values();

            // 取得出来たenum型分ループします。
            for(MessageSource enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * メッセージ種別
     */
    public enum MessageCategory {
        /** エラー */
        MessageCategory_Error(1),
        /** 警告 */
        MessageCategory_Alarm(2),
        /** 情報 */
        MessageCategory_OperationGuide(3),
        /** 確認 */
        MessageCategory_Confirm(4),
        /** デバッグ */
        MessageCategory_Debug(9);

        private int value;

        private MessageCategory(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageCategory getEnum(int num) {
            // enum型全てを取得します。
            MessageCategory[] enumArray = MessageCategory.values();

            // 取得出来たenum型分ループします。
            for(MessageCategory enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * メッセージ重要度
     */
    public enum MessageLevel {
        /** 軽 */
        MessageLevel_Light(0),
        /** 中 */
        MessageLevel_Middle(5),
        /** 重 */
        MessageLevel_Heavy(10);

        private int value;

        private MessageLevel(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

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
     * ブロードキャスト有無
     */
    public enum MessageIsBroadcastMessage {
        /** ブロードキャストなし */
        IsBroadcastMessage_NO(0),
        /** ブロードキャストあり */
        IsBroadcastMessage_YES(1);

        private int value;

        private MessageIsBroadcastMessage(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageIsBroadcastMessage getEnum(int num) {
            // enum型全てを取得します。
            MessageIsBroadcastMessage[] enumArray = MessageIsBroadcastMessage.values();

            // 取得出来たenum型分ループします。
            for(MessageIsBroadcastMessage enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }


    /**
     * 権限
     * 作業権限による制限を行なうか否か
     */
    public enum PrivilegeIsRestricted {
        /** 行わない */
        PrivilegeIsRestricted_False(0),
        /** 行う */
        PrivilegeIsRestricted_True(1);

        private int value;

        private PrivilegeIsRestricted(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrivilegeIsRestricted getEnum(int num) {
            // enum型全てを取得します。
            PrivilegeIsRestricted[] enumArray = PrivilegeIsRestricted.values();

            // 取得出来たenum型分ループします。
            for(PrivilegeIsRestricted enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 権限
     * 確認ダイアログの表示有無
     */
    public enum PrivilegeDisplayConfirmFlag {
        /** 表示しない */
    	PrivilegeDisplayConfirmFlag_OFF(0),
        /** 確認ダイアログを表示する */
    	PrivilegeDisplayConfirmFlag_ON(1);

        private int value;

        private PrivilegeDisplayConfirmFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrivilegeDisplayConfirmFlag getEnum(int num) {
            // enum型全てを取得します。
            PrivilegeDisplayConfirmFlag[] enumArray = PrivilegeDisplayConfirmFlag.values();

            // 取得出来たenum型分ループします。
            for(PrivilegeDisplayConfirmFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 権限
     * 完了ダイアログの表示有無
     */
    public enum PrivilegeDisplayFinishFlag {
        /** 表示しない */
        PrivilegeDisplayFinishFlag_OFF(0),
        /** 完了ダイアログを表示する */
        PrivilegeDisplayFinishFlag_ON(1);

        private int value;

        private PrivilegeDisplayFinishFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrivilegeDisplayFinishFlag getEnum(int num) {
            // enum型全てを取得します。
            PrivilegeDisplayFinishFlag[] enumArray = PrivilegeDisplayFinishFlag.values();

            // 取得出来たenum型分ループします。
            for(PrivilegeDisplayFinishFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 権限
     * データ入力時確認ダイアログの表示有無
     */
    public enum PrivilegeDisplayDiscardChangeFlag {
        /** 表示しない */
    	PrivilegeDisplayDiscardChangeFlag_OFF(0),
        /** データ入力時確認ダイアログを表示する */
    	PrivilegeDisplayDiscardChangeFlag_ON(1);

        private int value;

        private PrivilegeDisplayDiscardChangeFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrivilegeDisplayDiscardChangeFlag getEnum(int num) {
            // enum型全てを取得します。
            PrivilegeDisplayDiscardChangeFlag[] enumArray = PrivilegeDisplayDiscardChangeFlag.values();

            // 取得出来たenum型分ループします。
            for(PrivilegeDisplayDiscardChangeFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 権限
     * ユーザ認証の有無
     */
    public enum PrivilegeIsnecessaryPassword {
        /** 何もしない */
        PrivilegeIsnecessaryPassword_NONE(0),
        /** 認証ダイアログを表示する */
        PrivilegeIsnecessaryPassword_SINGLE(1),
        /** ダブル認証ダイアログを表示する */
        PrivilegeIsnecessaryPassword_DOUBLE(2);

        private int value;

        private PrivilegeIsnecessaryPassword(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrivilegeIsnecessaryPassword getEnum(int num) {
            // enum型全てを取得します。
            PrivilegeIsnecessaryPassword[] enumArray = PrivilegeIsnecessaryPassword.values();

            // 取得出来たenum型分ループします。
            for(PrivilegeIsnecessaryPassword enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 権限
     * 2重サブミットチェックの有無
     */
    public enum PrivilegeIsCheckDoubleSubmit {
        /** 行わない */
        PrivilegeIsCheckDoubleSubmit_False(0),
        /** 行う */
        PrivilegeIsCheckDoubleSubmit_True(1);

        private int value;

        private PrivilegeIsCheckDoubleSubmit(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrivilegeIsCheckDoubleSubmit getEnum(int num) {
            // enum型全てを取得します。
            PrivilegeIsCheckDoubleSubmit[] enumArray = PrivilegeIsCheckDoubleSubmit.values();

            // 取得出来たenum型分ループします。
            for(PrivilegeIsCheckDoubleSubmit enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * トレースモニタ書き込みタイミング
     * 真偽でタイミングを変更 trueは１つだけ設定する
     */
    public enum TraceMonitorWrite {
        /** 行わない */
        None(false),
        /** 処理ごとに随時書き込み */
        AnyTime(true),
        /** 最後に書き込み */
        End(false);

        private Boolean value;

        private TraceMonitorWrite(Boolean n) {
            this.value = n;
        }

        public Boolean getValue() {
            return this.value;
        }

        public static TraceMonitorWrite getEnum(Boolean num) {
            // enum型全てを取得します。
            TraceMonitorWrite[] enumArray = TraceMonitorWrite.values();

            // 取得出来たenum型分ループします。
            for(TraceMonitorWrite enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num.equals(enumInt.getValue())){
                    return enumInt;
                }
            }
            return null;
        }
    }


    /**
     * 丸め方法.
     */
    public enum RoundingMode {
        /** 四捨五入 */
        RoundHalfUp(0),
        /** 切り上げ */
        RoundUp(1),
        /** 切り捨て */
        RoundDown(2);

        private int value;

        private RoundingMode(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static RoundingMode getEnum(int num) {
            // enum型全てを取得します。
            RoundingMode[] enumArray = RoundingMode.values();

            // 取得出来たenum型分ループします。
            for(RoundingMode enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 進捗ステータス
     */
    public enum  OperationsRequestStatus {
        /** 新規作成 */
        OperationsRequestStatus_StstusNew("0"),
        /** 製造指図確定 */
        OperationsRequestStatus_StatusFixed("1"),
        /** 製造指図承認 */
        OperationsRequestStatus_StatusApploval("2"),
        /** 製造指図通知 */
        OperationsRequestStatus_StatusNotification("3"),
        /** 実行中 */
        OperationsRequestStatus_StatusRunning("4");

        private String value;

        private OperationsRequestStatus(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static OperationsRequestStatus getEnum(String num) {
            // enum型全てを取得します。
            OperationsRequestStatus[] enumArray = OperationsRequestStatus.values();

            // 取得出来たenum型分ループします。
            for(OperationsRequestStatus enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num.equals(enumInt.getValue())){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 大文字に変換する
     * @param text
     * @return
     */
    private static String reverseUpperCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.toUpperCase();
    }

    /**
     * 秤量処理区分.
     */
    public enum WeighProcessType {
        /** 秤量値 */
        Weigh(1),
        /** 風袋値 */
        Tare(2),
        /** 風袋引き */
        TareWeigh(3),
        /** ゼロリセット */
        ZeroReset(4),
        /** 定周期 */
        Cycle(5);

        private int value;

        private WeighProcessType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static WeighProcessType getEnum(int num) {
            // enum型全てを取得します。
            WeighProcessType[] enumArray = WeighProcessType.values();

            // 取得出来たenum型分ループします。
            for(WeighProcessType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 秤量I/F 接続開始完了判定実行フラグ */
    public enum OpenFinishJudgeFlag {

        /** 実行しない */
        OFF(0),
        /** 実行する */
        ON(1);

        private int value;

        private OpenFinishJudgeFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static OpenFinishJudgeFlag getEnum(int num) {

            // enum型全てを取得します。
        	OpenFinishJudgeFlag[] enumArray = OpenFinishJudgeFlag.values();

            // 取得出来たenum型分ループします。
            for (OpenFinishJudgeFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 外部装置区分.
     */
    public enum ExternalDevicePattern {
        /** 秤量器 */
        WeighMachine(1),
        /** PLC */
        PLC(2),
        /** 搬送 */
        FA(3),
        /** SCADA */
        SCADA(4);

        private int value;

        private ExternalDevicePattern(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalDevicePattern getEnum(int num) {
            // enum型全てを取得します。
            ExternalDevicePattern[] enumArray = ExternalDevicePattern.values();

            // 取得出来たenum型分ループします。
            for(ExternalDevicePattern enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 秤量処理結果区分.
     */
    public enum WeighProcessResultType {
        /** 秤量結果成功 */
        Success(1),
        /** 秤量結果失敗 */
        Failure(2),
        /** 定周期開始 */
        CycleProcessStart(3);

        private int value;

        private WeighProcessResultType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static WeighProcessResultType getEnum(int num) {
            // enum型全てを取得します。
            WeighProcessResultType[] enumArray = WeighProcessResultType.values();

            // 取得出来たenum型分ループします。
            for(WeighProcessResultType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 送受信状態
     */
    public enum SendRecvStateType {
    	/** 待機中 */
    	Waiting(-1),
    	/** 要求中 */
        Request(0),
        /** 実行中 */
        Running(1),
        /** OK */
        OK(2),
        /** NG */
        NG(3),
        /** 要求失敗 */
        Failed(4),
        /** ファイル作成失敗 */
        FailedCreateFile(5);

        private int value;

        private SendRecvStateType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static SendRecvStateType getEnum(int num) {
            // enum型全てを取得します。
            SendRecvStateType[] sendRecvStateType = SendRecvStateType.values();

            // 取得出来たenum型分ループします。
            for(SendRecvStateType enumInt : sendRecvStateType) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 送受信区分
     */
    public enum SendRecvType {
        /** 受信 */
        Recv(0),
        /** 送信 */
        Send(1);

        private int value;

        private SendRecvType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static SendRecvType getEnum(int num) {
            // enum型全てを取得します。
            SendRecvType[] sendRecvType = SendRecvType.values();

            // 取得出来たenum型分ループします。
            for(SendRecvType enumInt : sendRecvType) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 送受信状態変更区分
     */
    public enum SendRecvChangeStateType {
        /** 再送信（再受信） */
        Retry(0),
        /** クリア */
        Clear(1);

        private int value;

        private SendRecvChangeStateType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static SendRecvChangeStateType getEnum(int num) {
            // enum型全てを取得します。
            SendRecvChangeStateType[] sendRecvChangeStateType = SendRecvChangeStateType.values();

            // 取得出来たenum型分ループします。
            for(SendRecvChangeStateType enumInt : sendRecvChangeStateType) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 受信エラー時に処理続行するか否か
     */
    public enum IsContinueRecvProc {
        /** エラー時処理中断 */
        Stop(0),
        /** エラー時処理続行 */
        Continue(1);

        private int value;

        private IsContinueRecvProc(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static IsContinueRecvProc getEnum(int num) {
            // enum型全てを取得します。
            IsContinueRecvProc[] isContinueRecvProc = IsContinueRecvProc.values();

            // 取得出来たenum型分ループします。
            for(IsContinueRecvProc enumInt : isContinueRecvProc) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * ステータス
     */
    public enum State {
        /** 未処理 */
        Not(0),
        /** 正常終了 */
        Success(1),
        /** エラー */
        Error(2);

        private int value;

        private State(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static State getEnum(int num) {
            // enum型全てを取得します。
            State[] state = State.values();

            // 取得出来たenum型分ループします。
            for(State enumInt : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }


    /**
     * ファイル形式
     */
    public enum FileFormat {
        /** 固定長 */
        FixedLength(0),
        /** CSV */
        Csv(1),
        /** TSV */
        Tsv(2),
        /** XML */
        Xml(3);


        private int value;

        private FileFormat(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static FileFormat getEnum(int num) {
            // enum型全てを取得します。
            FileFormat[] state = FileFormat.values();

            // 取得出来たenum型分ループします。
            for(FileFormat fileFormat : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == fileFormat.getValue()){
                    return fileFormat;
                }
            }
            return null;
        }

    }

    /**
     * フォーマットID
     */
    public enum FormatId {
        /** String */
        String(0),
        /** Date */
        Date(1),
        /** Integer */
        Integer(2),
        /** BigDecimal */
        BigDecimal(3),
        /** 固定値 */
        FixedValue(4);

        private int value;

        private FormatId(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static FormatId getEnum(int num) {
            // enum型全てを取得します。
            FormatId[] state = FormatId.values();

            // 取得出来たenum型分ループします。
            for(FormatId formatId : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == formatId.getValue()){
                    return formatId;
                }
            }
            return null;
        }

    }

    /**
     * ヘッダー項目種類
     */
    public enum HeaderItemType {
        /** 送信日時 */
        SendDate(0),
        /** 送信件数 */
        SendDataNum(1);

        private int value;

        private HeaderItemType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static HeaderItemType getEnum(int num) {
            // enum型全てを取得します。
        	HeaderItemType[] state = HeaderItemType.values();

            // 取得出来たenum型分ループします。
            for(HeaderItemType HeaderItemType : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == HeaderItemType.getValue()){
                    return HeaderItemType;
                }
            }
            return null;
        }

    }


    /**
     * 電子ファイルの有無
     */
    public enum ReportFile {
        /** なし */
        NOTEXSIT(0),
        /** あり */
        EXSIT(1);

        private int value;

        private ReportFile(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ReportFile getEnum(int num) {
            // enum型全てを取得します。
            ReportFile[] state = ReportFile.values();

            // 取得出来たenum型分ループします。
            for(ReportFile reportFile : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == reportFile.getValue()){
                    return reportFile;
                }
            }
            return null;
        }

    }

    /**
     * 帳票種類
     */
    public enum PrintType {
        /** ラベル */
        Label(10),
        /** 一覧 */
        List(20),
        /** 多重 */
        Multiple(21),
        /** 多段 */
        MultiStage(22),
        /** 多段（キー割れ時改頁無し） */
        MultiStageNoNewPage(23),
        /** 多重多段 */
        MultipleMultiStage(24),
        /** 集計行有り */
        MultipleInventory(25);

        private int value;

        private PrintType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrintType getEnum(int  num) {
            // enum型全てを取得します。
            PrintType[] state = PrintType.values();

            // 取得出来たenum型分ループします。
            for (PrintType printType : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == printType.getValue()) {
                    return printType;
                }
            }
            return null;
        }

    }

    /**
     * 再印刷マーク出力有無
     */
    public enum RePrintMark {
        /** 再印字マーク出力しない */
        NOTREPRINTMARK(0),
        /** 再印字マーク出力する */
        REPRINTMARK(1);

        private int value;

        private RePrintMark(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static RePrintMark getEnum(int num) {
            // enum型全てを取得します。
            RePrintMark[] state = RePrintMark.values();

            // 取得出来たenum型分ループします。
            for(RePrintMark rePrintMark : state) {
                // 引数intとenum型のvalueを比較します。
                if (num == rePrintMark.getValue()){
                    return rePrintMark;
                }
            }
            return null;
        }

    }

    /** 印刷要求区分 */
    public enum PrintRequestCategory {
        PrintRequest(1),            // 印字要求
        GroupTempDeleteRequest(2),  // 帳票用情報グループテンポラリデータ削除要求
        TracePrintRequest(3),       // トレース印字要求
        TraceGroupTempDeleteRequest(4); // 帳票用情報グループテンポラリデータ削除要求（トレース）

        private int value;

        private PrintRequestCategory(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrintRequestCategory getEnum(int num) {

            // enum型全てを取得します。
            PrintRequestCategory[] enumArray = PrintRequestCategory.values();

            // 取得出来たenum型分ループします。
            for (PrintRequestCategory enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** プリンタ種別 */
    public enum PrintTypeNum {
        /** 通常 */
        Normal(1),
        /** ラベル */
        Label(2);

        private int value;

        private PrintTypeNum(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrintTypeNum getEnum(int num) {

            // enum型全てを取得します。
            PrintTypeNum[] enumArray = PrintTypeNum.values();

            // 取得出来たenum型分ループします。
            for (PrintTypeNum enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 電子ファイル作成区分 */
    public enum ElectronicFileCreateType {
        Print(1), // 印刷
        PrintAndSave(2), // 印刷と電子保存
        Save(3); // 電子保存

        private int value;

        private ElectronicFileCreateType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ElectronicFileCreateType getEnum(int num) {

            // enum型全てを取得します。
            ElectronicFileCreateType[] enumArray = ElectronicFileCreateType
                    .values();

            // 取得出来たenum型分ループします。
            for (ElectronicFileCreateType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** メール通知状況 */
    public enum MailNoticeStatus {
        /** 未通知 */
        NotNotice(0),
        /** 通知済 */
        Notified(1),
        /** 通知失敗 */
        NoticeFailure(2);

        private int value;

        private MailNoticeStatus(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static MailNoticeStatus getEnum(int num) {

            // enum型全てを取得します。
            MailNoticeStatus[] enumArray = MailNoticeStatus.values();

            // 取得出来たenum型分ループします。
            for (MailNoticeStatus enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** メール送信区分 */
    public enum SendMailType {
        /** To */
        TO(1),
        /** CC */
        CC(2),
        /** Bcc */
        BCC(3);

        private int value;

        private SendMailType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static SendMailType getEnum(int num) {

            // enum型全てを取得します。
            SendMailType[] enumArray = SendMailType.values();

            // 取得出来たenum型分ループします。
            for (SendMailType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 受信エラー時に処理続行するか否か */
    public enum  ContinueRecvProc{
        /** エラー時処理中断 */
        NOTCONTINUE(0),
        /** エラー時処理続行 */
        CONTINUE(1);

        private int value;

        private ContinueRecvProc(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static SendMailType getEnum(int num) {

            // enum型全てを取得します。
            SendMailType[] enumArray = SendMailType.values();

            // 取得出来たenum型分ループします。
            for (SendMailType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** プッシュ通知フラグ */
    public enum PushNoticeFlag {
        /** OFF */
        OFF(0),
        /** ON */
        ON(1);

        private int value;

        private PushNoticeFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PushNoticeFlag getEnum(int num) {

            // enum型全てを取得します。
            PushNoticeFlag[] enumArray = PushNoticeFlag.values();

            // 取得出来たenum型分ループします。
            for (PushNoticeFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** メッセージ履歴記録可否 */
    public enum MessageHistoryRec {
        /** 残さない */
        FALSE(0),
        /** 残す(デフォルト) */
        TRUE(1);

        private int value;

        private MessageHistoryRec(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageHistoryRec getEnum(int num) {

            // enum型全てを取得します。
            MessageHistoryRec[] enumArray = MessageHistoryRec.values();

            // 取得出来たenum型分ループします。
            for (MessageHistoryRec enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** ログ有無 */
    public enum IsLogging {
        /** ログなし */
        FALSE(0),
        /** ログあり */
        TRUE(1);

        private int value;

        private IsLogging(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static IsLogging getEnum(int num) {

            // enum型全てを取得します。
            IsLogging[] enumArray = IsLogging.values();

            // 取得出来たenum型分ループします。
            for (IsLogging enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 秤量通信種別 */
    public enum WeighTransmissionType {
        /** RS-232C通信 */
        RS232C(1),
        /** Ethernet通信 */
        ETHERNET(2);

        private int value;

        private WeighTransmissionType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static WeighTransmissionType getEnum(int num) {

            // enum型全てを取得します。
            WeighTransmissionType[] enumArray = WeighTransmissionType.values();

            // 取得出来たenum型分ループします。
            for (WeighTransmissionType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 秤量 応答有無 */
    public enum WeighIsResponse {
        /** 応答なし */
        FALSE(0),
        /** 応答あり */
        TRUE(1);

        private int value;

        private WeighIsResponse(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static WeighIsResponse getEnum(int num) {

            // enum型全てを取得します。
            WeighIsResponse[] enumArray = WeighIsResponse.values();

            // 取得出来たenum型分ループします。
            for (WeighIsResponse enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 秤量 応答区分 */
    public enum WeighResponseType {
        /** コマンドに対して安定応答が返ってきた（応答値は返ってくる) */
        STABLE(1),
        /** コマンドに対して不安定応答が返ってきた（応答値は返ってくる) */
        UNSTABLE(2),
        /** コマンドに対して成功応答が返ってきた（応答値を返さないゼロリセットなど） */
        SUCCESS(3),
        /** コマンドに対して失敗応答が返ってきた(応答値を返さないゼロリセットなど) */
        FAILED(4);

        private int value;

        private WeighResponseType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static WeighResponseType getEnum(int num) {

            // enum型全てを取得します。
            WeighResponseType[] enumArray = WeighResponseType.values();

            // 取得出来たenum型分ループします。
            for (WeighResponseType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** ブックマーク区分 */
    public enum BookMarkType {
        /** ブックマーク対象外 */
        NONE("0"),
        /** ブックマーク済 */
        MARKED("1"),
        /** ブックマーク未 */
        NO_MARK("2");


        private String value;

        private BookMarkType(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static BookMarkType getEnum(String type) {

            // enum型全てを取得します。
            BookMarkType[] enumArray = BookMarkType.values();

            // 取得出来たenum型分ループします。
            for (BookMarkType enumType : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (type.equals(enumType.getValue())) {
                    return enumType;
                }
            }
            return null;
        }
    }

    /** ブックマーク登録可否 */
    public enum BookMarkRegistration {
        /** 否 */
        NO(0),
        /** 可 */
        YES(1);

        private int value;

        private BookMarkRegistration(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static BookMarkRegistration getEnum(int num) {

            // enum型全てを取得します。
            BookMarkRegistration[] enumArray = BookMarkRegistration.values();

            // 取得出来たenum型分ループします。
            for (BookMarkRegistration enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }


    /** テーブル検索条件管理の設定種類 */
    public enum TableSearchSettingType {
        /** 検索設定 */
        SEARCH(0),
        /** 表示設定 */
        DISPLAY(1);

        private int value;

        private TableSearchSettingType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static TableSearchSettingType getEnum(int num) {

            // enum型全てを取得します。
            TableSearchSettingType[] enumArray = TableSearchSettingType.values();

            // 取得出来たenum型分ループします。
            for (TableSearchSettingType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** 表示画面区分 */
    public enum DisplayType {
        CLIENT("CLIENT"),     // 管理端末画面
        PANECON("PANECON"),    // 工程端末画面
        MAINMENU("MAINMENU"),   // メインメニュー
        PANECON_MAINMENU("PANECON_MAINMENU");   // 工程端末メインメニュー

        private String value;

        private DisplayType(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static DisplayType getEnum(String type) {
            // enum型全てを取得します。
            DisplayType[] enumArray = DisplayType.values();

            // 取得出来たenum型分ループします。
            for(DisplayType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (type.equals(enumInt.getValue())){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** 表示件数プルダウン */
    public enum PageingPulldown {
        /** 10 */
        TEN(1, "10"),
        /** 20 */
        TWENTY(2, "20"),
        /** 30 */
        THIRTY(3, "30"),
        /** 40 */
        FORTY(4, "40"),
        /** 50 */
        FIFTY(5, "50");

        private int num;
        private String text;

        private PageingPulldown(int num, String text) {
            this.num = num;
            this.text = text;
        }

        /** プルダウンの要素数 */
        public static int getPageingPulldownLength() {
            return PageingPulldown.values().length;
        }

        /**
         * 設定されたnumを返す
         * @return
         */
        public int getNum() {
            return this.num;
        }

        /**
         * 設定された値を返す
         */
        public String getValue() {
          return this.text;
        }

        public static String getEnum(int num) {
            // enum型全てを取得します。
            PageingPulldown[] enumArray = PageingPulldown.values();

            // 取得出来たenum型分ループします。
            for(PageingPulldown enumMap : enumArray) {
                if (num == enumMap.getNum()) {
                    return enumMap.getValue();
                }
            }
            return null;
        }
    }

    /** 外部I/F 送信伝文まとめ可否 */
    public enum ExternalIfSendIsSummary {

        /** 不可 */
        NO(0),
        /** まとめ可 */
        YES(1);

        private int value;

        private ExternalIfSendIsSummary(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfSendIsSummary getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfSendIsSummary[] enumArray = ExternalIfSendIsSummary.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfSendIsSummary enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F データ項目識別ID有効/無効 */
    public enum ExternalIfIsDataItemId {

        /** 有効 */
        VALID(0),
        /** 無効 */
        INVALID(1);

        private int value;

        private ExternalIfIsDataItemId(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfIsDataItemId getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfIsDataItemId[] enumArray = ExternalIfIsDataItemId.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfIsDataItemId enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F 送受信クリアフラグ */
    public enum ExternalIfSendRecvClearFlag {

        /** クリアしない */
        OFF(0),
        /** クリアする */
        ON(1);

        private int value;

        private ExternalIfSendRecvClearFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfSendRecvClearFlag getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfSendRecvClearFlag[] enumArray = ExternalIfSendRecvClearFlag.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfSendRecvClearFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F 再処理フラグ */
    public enum ExternalIfSendRecvRetryFlag {

        /** 再処理しない */
        OFF(0),
        /** 再処理する */
        ON(1);

        private int value;

        private ExternalIfSendRecvRetryFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfSendRecvRetryFlag getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfSendRecvRetryFlag[] enumArray = ExternalIfSendRecvRetryFlag.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfSendRecvRetryFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F 送信結果 */
    public enum ExternalIfSendResult {

        /** 成功 */
        OK(0),
        /** 失敗 */
        NG(1);

        private int value;

        private ExternalIfSendResult(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfSendResult getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfSendResult[] enumArray = ExternalIfSendResult.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfSendResult enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F 送受信モード */
    public enum ExternalIfSendRecvMode {

        /** ファイル種別単位 */
        FILE_TYPE(0),
        /** グループ単位 */
        GROUP(1);

        private int value;

        private ExternalIfSendRecvMode(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfSendRecvMode getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfSendRecvMode[] enumArray = ExternalIfSendRecvMode.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfSendRecvMode enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F プロトコル種別 */
    public enum ExternalIfProtocolType {

        /** HULFT */
        HULFT(0),
        /** HULFT以外 */
        NOT_HULFT(1);

        private int value;

        private ExternalIfProtocolType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfProtocolType getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfProtocolType[] enumArray = ExternalIfProtocolType.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfProtocolType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F エラー時に処理続行するか否か */
    public enum ExternalIfSendRecvIsContinueError {

        /** 処理を中断する。 */
        STOP(0),
        /** 処理を続行する。 */
        CONTINUE(1);

        private int value;

        private ExternalIfSendRecvIsContinueError(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfSendRecvIsContinueError getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfSendRecvIsContinueError[] enumArray = ExternalIfSendRecvIsContinueError.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfSendRecvIsContinueError enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 外部I/F 電文使用可否 */
    public enum ExternalIfTelegramIsUsable {

        /** 使用不可 */
        NO(0),
        /** 使用可 */
        YES(1);

        private int value;

        private ExternalIfTelegramIsUsable(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ExternalIfTelegramIsUsable getEnum(int num) {

            // enum型全てを取得します。
            ExternalIfTelegramIsUsable[] enumArray = ExternalIfTelegramIsUsable.values();

            // 取得出来たenum型分ループします。
            for (ExternalIfTelegramIsUsable enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 印字状態 */
    public enum PrintoutStatus {
        /** 未処理 */
        UNTREATED(0),
        /** 処理中 */
        PROCESSING(1),
        /** 正常処理 */
        NORMAL(2),
        /** エラー */
        ERROR(3);

        private int value;

        private PrintoutStatus(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static PrintoutStatus getEnum(int num) {

            // enum型全てを取得します。
            PrintoutStatus[] enumArray = PrintoutStatus.values();

            // 取得出来たenum型分ループします。
            for (PrintoutStatus enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 帳票印刷プレビュー状態 */
    public enum PrintPreviewStatus
    {
        /** プレビュー未処理 */
        PREVIEW_UNTREATED(0),
        /** プレビュー処理中 */
        PREVIEW_PROCESSING(1),
        /** プレビュー処理済 */
        PREVIEW_END(2),
        /** 印刷未処理 */
        PRINT_UNTREATED(10),
        /** 印刷処理中 */
        PRINT_PROCESSING(11),
        /** 印刷処理済 */
        PRINT_END(12),
        /** 正常処理 */
        NORMAL(3),
        /** エラー */
        ERROR(4);

        private int value;

        private PrintPreviewStatus(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static PrintPreviewStatus getEnum(int num)
        {

            // enum型全てを取得します。
            PrintPreviewStatus[] enumArray = PrintPreviewStatus.values();

            // 取得出来たenum型分ループします。
            for (PrintPreviewStatus enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** プレビュー画面表示状態 */
    public enum PreviewDisplayStatus
    {
        /** 画面が表示中 */
        DISPLAYING(1),
        /** 画面が閉じられた */
        CLOSED(2);

        private int value;

        private PreviewDisplayStatus(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static PreviewDisplayStatus getEnum(int num)
        {

            // enum型全てを取得します。
            PreviewDisplayStatus[] enumArray = PreviewDisplayStatus.values();

            // 取得出来たenum型分ループします。
            for (PreviewDisplayStatus enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * マスタデータキャッシュグループ
     */
    public enum MstrDataCacheGroup {
        /** ユーザ情報 */
        PERSON_INFO("01"),
        /** 外部IF情報 */
        EXTERNALIF_INFO("02"),
        /** その他 */
        OTHER("99");

        private String value;

        private MstrDataCacheGroup(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static MstrDataCacheGroup getEnum(String num) {
            // enum型全てを取得します。
            MstrDataCacheGroup[] enumArray = MstrDataCacheGroup.values();

            // 取得出来たenum型分ループします。
            for (MstrDataCacheGroup enumStr : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (enumStr.getValue().equals(num)) {
                    return enumStr;
                }
            }
            return null;
        }

    }

    /**
     * メッセージ定義内Talendパラメータ名
     */
    public enum TalendParameterName {

    	/** パトランプID */
    	KIND("kind"),
    	/** パトランプID */
    	PATLAMP_ID("patlamp_id"),
    	/** IPアドレス */
    	IP_ADDRESS("ip_address"),
    	/** サウンドパターン区分 */
    	SOUND_DATA("sound_data"),
    	/** パトライトパラメータ名(点灯消灯) */
    	COMMAND_TYPE("command_type"),
    	/** 点灯状態 */
    	LIGHTS_DATA("lights_data");


    	private String value;

    	private TalendParameterName(String str) {
    		this.value = str;
    	}

    	public String getValue() {
    		return this.value;
    	}

    	public static String getEnum(String num) {
    		// enum型全てを取得します。
    		TalendParameterName[] enumArray = TalendParameterName.values();

    		// 取得出来たenum型分ループします。
    		for(TalendParameterName enumStr : enumArray) {
    			// 引数intとenum型のvalueを比較します。
    			if (enumStr.getValue().equals(num)) {
    				return enumStr.getValue();
    			}
    		}
    		return null;
    	}
    }

    /**
     * Talendジョブ種類
     */
    public enum TalendJobKind {

        /** パトランプ */
        PATLAMP("patlamp");

        private String value;

        private TalendJobKind(String str) {
            this.value = str;
        }

        public String getValue() {
            return this.value;
        }

        public static String getEnum(String num) {
            // enum型全てを取得します。
        	TalendJobKind[] enumArray = TalendJobKind.values();

            // 取得出来たenum型分ループします。
            for(TalendJobKind enumStr : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (enumStr.getValue().equals(num)) {
                    return enumStr.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 点灯状態
     */
    public enum lightPatternData {
        /** 消灯 */
        OFF(0),
        /** 点灯 */
        ON(1),
        /** 点滅パターン1 */
        FLASH1(2),
        /** 点滅パターン2 */
        FLASH2(3),
        /** 現状維持 */
        KEEP(9);

        private int value;

        private lightPatternData(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static lightPatternData getEnum(int num) {
            // enum型全てを取得します。
            lightPatternData[] enumArray = lightPatternData.values();

            // 取得出来たenum型分ループします。
            for(lightPatternData enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }


    /**
     * サウンドパターン区分
     */
    public enum SoundPatternData {
        /** 消音 */
        OFF("0"),
        /** ブザーパターン1 */
        PATTERN1("1"),
        /** ブザーパターン2 */
        PATTERN2("2"),
        /** ブザーパターン3 */
        PATTERN3("3"),
        /** ブザーパターン4 */
        PATTERN4("4"),
        /** 現状維持 */
        KEEP("9");

        private String value;

        private SoundPatternData(String str) {
            this.value = str;
        }

        public String getValue() {
            return this.value;
        }

        public static String getEnum(String num) {
            // enum型全てを取得します。
            SoundPatternData[] enumArray = SoundPatternData.values();

            // 取得出来たenum型分ループします。
            for(SoundPatternData enumStr : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (enumStr.getValue().equals(num)) {
                    return enumStr.getValue();
                }
            }
            return null;
        }
    }

    /**
     * パトランプ色
     */
    public enum PatlampColor {
        /** 赤 */
        RED(1, "#ff0000"),
        /** 黄 */
        YELLOW(2, "#ffff00"),
        /** 緑 */
        GREEN(3, "#008000"),
        /** 青 */
        BLUE(4, "#0000ff"),
        /** 白 */
        WHITE(5, "#ffffff");

        private int num;
        private String value;

        private PatlampColor(int num, String str) {
            this.num = num;
            this.value = str;
        }

        public int getNum() {
            return this.num;
        }

        public String getValue() {
            return this.value;
        }

        public static String getEnum(int num) {
            // enum型全てを取得します。
            PatlampColor[] enumArray = PatlampColor.values();

            // 取得出来たenum型分ループします。
            for(PatlampColor enumMap : enumArray) {
                if (num == enumMap.getNum()) {
                    return enumMap.getValue();
                }
            }
            return null;
        }
    }

    /**
     * パトライトパラメータ名
     */
    public enum CommandParam {
        /** 点灯 */
        ALERT(1,"alert"),
        /** 消灯 */
        CLEAR(2, "clear");

        private int num;
        private String value;

        private CommandParam(int num, String str) {
            this.num = num;
            this.value = str;
        }

        public int getNum() {
            return this.num;
        }

        public String getValue() {
            return this.value;
        }

        public static String getEnum(int num) {
            // enum型全てを取得します。
            CommandParam[] enumArray = CommandParam.values();

            // 取得出来たenum型分ループします。
            for(CommandParam enumMap : enumArray) {
                if (num == enumMap.getNum()) {
                    return enumMap.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 領域区分
     */
    public enum RegionType {
        /** 通常領域 */
        NORMAL("1"),
        /** 保管領域 */
        STORAGE("2");

        private String value;

        private RegionType(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public int getIntValue() {
            return Integer.valueOf(this.value);
        }

        public static RegionType getEnum(String num) {
            // enum型全てを取得します。
            RegionType[] enumArray = RegionType.values();

            // 取得出来たenum型分ループします。
            for (RegionType enumStr : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (enumStr.getValue().equals(num)) {
                    return enumStr;
                }
            }
            return null;
        }

    }

    /**
     * 端末選択プルダウン選択可能フラグ
     */
    public enum ComputerSelectableFlag {

        /** 選択不可 */
        OFF(0),
        /** 選択可 */
        ON(1);

        private int value;

        private ComputerSelectableFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ComputerSelectableFlag getEnum(int num) {

            // enum型全てを取得します。
            ComputerSelectableFlag[] enumArray = ComputerSelectableFlag.values();

            // 取得出来たenum型分ループします。
            for (ComputerSelectableFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 再印字フラグ */
    public enum ReprintFlag {
        /** OFF */
        OFF(0),
        /** ON */
        ON(1);

        private int value;

        private ReprintFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ReprintFlag getEnum(int num) {

            // enum型全てを取得します。
            ReprintFlag[] enumArray = ReprintFlag.values();

            // 取得出来たenum型分ループします。
            for (ReprintFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 認証区分
     */
    public enum CertificateType {
        /** ログイン認証 */
        LOGIN("DI01.0010"),
        /** ロック解除 */
        LOCK("DI01.0007"),
        /** 権限認証入力 */
        PRIVILEGE_CHECK("DI01.0117"),
        /** ダブル権限認証入力 */
        DOUBLE_PRIVILEGE_CHECK("DI01.0118"),
        /** 承認権限確認 */
    	APPROVE_PRIVILEGE_CHECK("DI01.0135"),
    	/** 代替承認権限確認 */
    	ALTERNATE_APPROVE_PRIVILEGE_CHECK("DI01.0136");

        private String value;

        private CertificateType(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static CertificateType getEnum(String num) {
            // enum型全てを取得します。
            CertificateType[] enumArray = CertificateType.values();

            // 取得出来たenum型分ループします。
            for (CertificateType enumStr : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (enumStr.getValue().equals(num)) {
                    return enumStr;
                }
            }
            return null;
        }

    }

    /**
     * パスワード変更区分
     */
    public enum ChangePasswordType {
        /** パスワード変更 */
        CHANGE_PASSWORD(0),
        /** パスワード初期化 */
        INIT_PASSWORD(1);

        private int value;

        private ChangePasswordType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ChangePasswordType getEnum(int num) {
            // enum型全てを取得します。
            ChangePasswordType[] enumArray = ChangePasswordType.values();

            // 取得出来たenum型分ループします。
            for(ChangePasswordType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 履歴テーブルNGフラグ */
    public enum HistNgFlag {
        /** OFF */
        OFF(0),
        /** ON */
        ON(1);

        private int value;

        private HistNgFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static HistNgFlag getEnum(int num) {

            // enum型全てを取得します。
            HistNgFlag[] enumArray = HistNgFlag.values();

            // 取得出来たenum型分ループします。
            for (HistNgFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** パスワード変更時メール通知フラグ */
    public enum ChangePasswordMailNoticeFlag {
        /** OFF */
        OFF(0),
        /** ON */
        ON(1);

        private int value;

        private ChangePasswordMailNoticeFlag(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static ChangePasswordMailNoticeFlag getEnum(int num) {

            // enum型全てを取得します。
            ChangePasswordMailNoticeFlag[] enumArray = ChangePasswordMailNoticeFlag.values();

            // 取得出来たenum型分ループします。
            for (ChangePasswordMailNoticeFlag enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /**
     * 共通日付時刻フォーマット<br>
     * ３種類をサポート<br>
     * 1.yyyy/MM/dd<br>
     * 2.yyyy/MM/dd hh:mm<br>
     * 3.yyyy/mm/dd hh;mm:ss
     *
     */
    public enum GnomesDateTimeFormat {
            YMDHMS(GnomesResourcesConstants.YY01_0001),//yyyy/MM/dd hh:mm:ss
            YMDHM(GnomesResourcesConstants.YY01_0002), //yyyy/MM/dd hh:mm
            YMD(GnomesResourcesConstants.YY01_0003),   //yyyy/MM/dd
            YM(GnomesResourcesConstants.YY01_0094);   //yyyy/MM

        /**
         * 共通日付時刻フォーマット
         */
        private final String format;

        /**
         * コンストラクタ
         * @param format
         */
        private GnomesDateTimeFormat(final String format) {
            this.format = format;
        }

        /* (非 Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString(){
            return this.format;
        }
        /**
         * 共通日付時刻フォーマットの取得
         * @return
         */
        public String getValue() {
            return this.format;
        }

    }

    /**
     * カスタムタグの非表示区分
     */
    public enum TagHiddenKind {
        HIDDEN,
        NONE
    }

    /**
     * 丸め計算方式区分
     */
    public enum RoundCalculateDiv {
        /** 四捨五入 */
        RoundCalculateDiv_RoundHalfUp(1),
        /** 切り捨て */
        RoundCalculateDiv_RoundDown(2),
        /** 切り上げ */
        RoundCalculateDiv_RoundUp(3),
        /** 丸めなし */
        RoundCalculateDiv_RoundNone(4),
        /** JIS丸め */
        RoundCalculateDiv_RoundJIS(5);

        private int value;

        private RoundCalculateDiv(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static RoundCalculateDiv getEnum(int num) {
            // enum型全てを取得します。
            RoundCalculateDiv[] enumArray = RoundCalculateDiv.values();

            // 取得出来たenum型分ループします。
            for(RoundCalculateDiv enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 実行クラスメソッド区分
     * @author 03530078
     *
     */
    public enum DoingClassMethodDiv {
        /** Modelメソッド */
        MODEL(1),
        /** BLメソッド */
        BUSINESS_LOGIC(2),
        /** 表示データ取得Modelメソッド */
        GET_DISP_DATA(3),
        /** その他 */
        OTHER(0);
        private int value;

        private DoingClassMethodDiv(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static DoingClassMethodDiv getEnum(int num) {
            // enum型全てを取得します。
            DoingClassMethodDiv[] enumArray = DoingClassMethodDiv.values();

            // 取得出来たenum型分ループします。
            for(DoingClassMethodDiv enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** 保管モード  */
    public enum UploadfileSaveMode {
        NoOverlap(0),       // 重複不可能
        Overlap(1);         // 重複可能

        private int value;

        private UploadfileSaveMode(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static UploadfileSaveMode getEnum(int num) {
            // enum型全てを取得します。
            UploadfileSaveMode[] enumArray = UploadfileSaveMode.values();

            // 取得出来たenum型分ループします。
            for(UploadfileSaveMode enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 改行コード
     */
    public enum FileDefaultDelimiter {
        /** CR */
        CR(1,"\r"),
        /** CRLF */
        CRLF(2, "\r\n"),
    	/** LF */
    	LF(3, "\n");

        private int num;
        private String value;

        private FileDefaultDelimiter(int num, String str) {
            this.num = num;
            this.value = str;
        }

        public int getNum() {
            return this.num;
        }

        public String getValue() {
            return this.value;
        }

        public static String getEnum(int num) {
            // enum型全てを取得します。
        	FileDefaultDelimiter[] enumArray = FileDefaultDelimiter.values();

            // 取得出来たenum型分ループします。
            for(FileDefaultDelimiter enumMap : enumArray) {
                if (num == enumMap.getNum()) {
                    return enumMap.getValue();
                }
            }
            return null;
        }
    }

    /**
     * nullを許可するリクエストパラメータ
     */
    public enum  RequestParamAllowNull {

    	/** windowId */
    	WindowId("windowId"),
    	/** 検索条件マップ */
    	SearchSettingMap("searchSettingMap"),
    	/** ローカルストレージ */
    	LocalStorage("localStorage"),
    	/** 動的画面ID */
    	DynamicScreenId("dynamicScreenId"),
    	/** 呼出画面Parametar */
    	ScreenTransitionMode("screenTransitionMode"),
    	/** 動的画面名ID */
    	DynamicScreenNameId("dynamicScreenNameId"),
    	/** 動的タイトルID */
    	DynamicTitleId("dynamicTitleId"),
    	/** 編集有フラグ */
    	InputChangeFlag("inputChangeFlag"),
    	/** ログインユーザID */
    	LoginUserId("loginUserId"),
    	/** ログインユーザパスワード */
    	LoginUserPassword("loginUserPassword"),
    	/** 認証者ユーザID */
    	CertUserId("certUserId"),
    	/** 認証者ユーザパスワード */
    	CertUserPassword("certUserPassword"),
    	/** 代替者ユーザID */
    	SubstituteUserId("substituteUserId"),
    	/** 代替者ユーザパスワード */
    	SubstituteUserPassword("substituteUserPassword"),
    	/** ボタンID */
    	ButtonId("buttonId"),
    	/** 代替フラグ */
    	SubstituteFlag("substituteFlag"),
    	/** ダブル認証か否か */
    	IsDoubleCheck("isDoubleCheck");


        private String value;

        private RequestParamAllowNull(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static RequestParamAllowNull getEnum(String str) {
            // enum型全てを取得します。
        	RequestParamAllowNull[] enumArray = RequestParamAllowNull.values();

            // 取得出来たenum型分ループします。
            for(RequestParamAllowNull enumInt : enumArray) {
                // 引数strとenum型のvalueを比較します。
                if (str.equals(enumInt.getValue())){
                    return enumInt;
                }
            }
            return null;
        }

        public static HashMap<String, String> getAllEnum(){
        	HashMap<String, String> map = new HashMap<String, String>();

            // enum型全てを取得します。
        	RequestParamAllowNull[] enumArray = RequestParamAllowNull.values();

            // 取得出来たenum型分ループします。
            for(RequestParamAllowNull enumInt : enumArray) {
                map.put(enumInt.value, enumInt.value);
            }

            return map;

        }

    }

    /** 同期モード */
    public enum CyclicWeighSyncAccessMode
    {
        /** ONとOFF以外 */
        NONE(0),
        /** ON */
        ON(1),
        /** OFF */
        OFF(2);

        private int value;

        private CyclicWeighSyncAccessMode(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static CyclicWeighSyncAccessMode getEnum(Integer num)
        {
            CyclicWeighSyncAccessMode mode = CyclicWeighSyncAccessMode.NONE;

            if (num != null) {
                if (CyclicWeighSyncAccessMode.ON.getValue() == num.intValue()) {
                    mode = CyclicWeighSyncAccessMode.ON;
                }
                else if (CyclicWeighSyncAccessMode.OFF.getValue() == num.intValue()) {
                    mode = CyclicWeighSyncAccessMode.OFF;
                }
            }

            return mode;
        }
    }

    /** 通信方式 */
    public enum StreamMode
    {
        /** コマンド方式 */
        COMMAND(0),
        /** ストリーミング方式 */
        STREAMING(1);

        private int value;

        private StreamMode(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static StreamMode getEnum(Integer num)
        {
            StreamMode mode = StreamMode.COMMAND;

            if (num != null) {
                if (StreamMode.STREAMING.getValue() == num.intValue()) {
                    mode = StreamMode.STREAMING;
                }
            }

            return mode;
        }
    }

    /** ボタン表示区分 */
    public enum DisplayDivision
    {
        /** エラーを表示（従来の動き） */
        NORMAL(0),
        /** 非表示 */
        HIDE(1),
        /** 非活性 */
        DISABLE(2);

        private int value;

        private DisplayDivision(int num)
        {
            this.value = num;
        }

        public int getValue()
        {
            return this.value;
        }

        public static DisplayDivision getEnum(Integer num)
        {
            DisplayDivision mode = DisplayDivision.NORMAL;

            if (num != null) {
                if (DisplayDivision.HIDE.getValue() == num.intValue()) {
                    mode = DisplayDivision.HIDE;
                }
                else if (DisplayDivision.DISABLE.getValue() == num.intValue()) {
                    mode = DisplayDivision.DISABLE;
                }
                else {
                    mode = DisplayDivision.NORMAL;
                }
            }

            return mode;
        }
    }
}
