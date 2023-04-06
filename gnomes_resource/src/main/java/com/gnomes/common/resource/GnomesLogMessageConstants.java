package com.gnomes.common.resource;

/**
 * ログファイルに直接出力用メッセージリソース 限定子
 *
 */
/**
 * ここにクラス概要を入力してください。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/26 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesLogMessageConstants {

    /** アプリケーションエラーが発生しました。 詳細についてはログを確認してください。 */
    public static final String ME01_0001 = "ME01.0001";

    /** 製造品目キー[{0}]に該当する、払出品が存在しません。 */
    public static final String ME02_0016 = "ME02.0016";

    /** 投入品.投入品キー[{0}]の要求量算出タイプが未設定です。 */
    public static final String ME02_0017 = "ME02.0017";

    /** 投入品.投入品キー[{0}]の要求量算出タイプに未知の値[{1}]が設定されています */
    public static final String ME02_0018 = "ME02.0018";

    /** 投入品.投入品キー[{0}]の要求量算出タイプ有効小数点が未設定です。 */
    public static final String ME02_0019 = "ME02.0019";

    /** 払出品.払出品キー[{0}]の要求量算出タイプが未設定です。 */
    public static final String ME02_0020 = "ME02.0020";

    /** 払出品.払出品キー[{0}]の要求量算出タイプに未知の値[{1}]が設定されています。 */
    public static final String ME02_0021 = "ME02.0021";

    /** 払出品.払出品キー[{0}]の払出品の要求量有効少数点桁数が未設定です。 */
    public static final String ME02_0022 = "ME02.0022";

    /** 操作エラーです。 */
    public static final String YY01_0013 = "YY01.0013";

    /** クラス名[{0}]にフィールド名[{1}]のアクセス用メソッドが存在しません。 */
    public static final String ME01_0005 = "ME01.0005";

    /** クラス[{0}]のフィールド名[{1}]より値取得に失敗しました。 */
    public static final String ME01_0006 = "ME01.0006";

    /** {0}から{1}に変換時、変換エラーが発生しました。ID=[{2}], param_name=[{3}], value=[{4}] */
    public static final String ME01_0008 = "ME01.0008";

    /** {0}から{1}に変換時、変換エラーが発生しました。ID=[{2}], param_name=[{3}] value=[{4}], format=[{5}] */
    public static final String ME01_0009 = "ME01.0009";

    /** ユーザアカウントはロックアウトされています。システム管理者に相談してください。 */
    public static final String ME01_0010 = "ME01.0010";

    /** パスワードが有効ではありません。システム管理者に相談してください。 */
    public static final String ME01_0012 = "ME01.0012";

    /** 変更するパスワードが有効ではありません。 */
    public static final String ME01_0013 = "ME01.0013";

    /** ログインユーザのみロック解除可能です。 */
    public static final String ME01_0014 = "ME01.0014";

    /** ユーザー有効期限が切れています。 */
    public static final String ME01_0015 = "ME01.0015";

    /** パスワード有効期限が切れています。 */
    public static final String ME01_0016 = "ME01.0016";

    /** 最近使用したパスワードを再度使用することはできません。 */
    public static final String ME01_0017 = "ME01.0017";

    /** ユーザーID、パスワードを入力してください。 */
    public static final String ME01_0018 = "ME01.0018";

    /** パスワードは {0}桁以上入力してください。 */
    public static final String ME01_0019 = "ME01.0019";

    /** 全て同じ文字または数字パスワードを使用することはできません。 */
    public static final String ME01_0020 = "ME01.0020";

    /** ユーザーIDと同じパスワードを使用することはできません。 */
    public static final String ME01_0021 = "ME01.0021";

    /** 特定文字列 {0} をパスワードに使用することはできません。 */
    public static final String ME01_0022 = "ME01.0022";

    /** 英字または数字のみのパスワードを使用することはできません。 */
    public static final String ME01_0023 = "ME01.0023";

    /** 現パスワードが正しくありません。 */
    public static final String ME01_0024 = "ME01.0024";

    /** データがみつかりません。（{0}） */
    public static final String ME01_0026 = "ME01.0026";

    /** 使用する拠点が設定されていません。*/
    public static final String ME01_0027 = "ME01.0027";

    /** ロックアウトしたユーザーのパスワードは変更できません。システム管理者に相談してください。 */
    public static final String ME01_0028 = "ME01.0028";

    /** 不正なアクセスです。ログイン画面からログインしてください。 */
    public static final String ME01_0030 = "ME01.0030";

    /** 認証者の権限を確認してください。 */
    public static final String ME01_0034 = "ME01.0034";

    /** 残り {0}日でユーザアカウントは無効になります。システム管理者に相談してください。 */
    public static final String MA01_0002 = "MA01.0002";

    /** 残り {0}日でパスワードが無効になります。パスワードを変更してください。 */
    public static final String MA01_0003 = "MA01.0003";

    /** 残り {0}日でパスワードが無効になります。システム管理者に相談してください。 */
    public static final String MA01_0004 = "MA01.0004";

    /** 入力された内容を取消します。よろしいですか？ */
    public static final String MC01_0004 = "MC01.0004";

    /** 認証処理成功  (ユーザーID： {0}) */
    public static final String MG01_0002 = "MG01.0002";

    /** ダブル認証処理成功  (ユーザーID： {0}、認証者： {1}) */
    public static final String MG01_0003 = "MG01.0003";

    /** ログイン認証成功  (ユーザーID： {0}) */
    public static final String MG01_0004 = "MG01.0004";

    /** 設定する端末を指定してください。 */
    public static final String ME01_0036 = "ME01.0036";

    /** 設定するエリアを指定してください。 */
    public static final String ME01_0037 = "ME01.0037";

    /** 端末の設定に失敗しました。 */
    public static final String ME01_0038 = "ME01.0038";

    /** 端末情報の削除に失敗しました。 */
    public static final String ME01_0039 = "ME01.0039";

    /** セッション情報が見つかりません。もう一度ログインしてください。 */
    public static final String ME01_0040 = "ME01.0040";

    /** ファイルをアップロードしました。 */
    public static final String MG01_0005 = "MG01.0005";

    /** {0}採番処理を実行しました。採番値=[{1}],テーブル=[{2}],キー=[{3}] */
    public static final String MG01_0008 = "MG01.0008";

    /** {0}採番削除処理を実行しました。テーブル=[{1}],キー=[{2}] */
    public static final String MG01_0009 = "MG01.0009";

    /** アップロードファイル名登録処理を実行しました。登録内容=[{0}] */
    public static final String MG01_0010 = "MG01.0010";

    /** アップロードファイル登録処理を実行しました。登録内容=[{0}] */
    public static final String MG01_0011 = "MG01.0011";

    /** アップロードファイル削除処理を実行しました。削除内容=[{0}] */
    public static final String MG01_0012 = "MG01.0012";

    /** ファイル参照処理を実行しました。参照内容=[{0}] */
    public static final String MG01_0013 = "MG01.0013";

    /** シート「{0}」： */
    public static final String ME01_0088 = "ME01.0088";

    /** {0}は{1}を入力してください。 */
    public static final String MV01_0001 = "MV01.0001";

    /** {0}は{1}（"{2}"形式)を入力してください。 */
    public static final String MV01_0016 = "MV01.0016";

    /** {0}行{1},列{2}：{3} */
    public static final String MV01_0017 = "MV01.0017";

    /** 行{0}：データを{1}件入力してください。データ件数：{3} */
    public static final String MV01_0018 = "MV01.0018";

    /** {0}ヘッダの列{1}:{2} */
    public static final String MV01_0019 = "MV01.0019";

    /** セルは文字列形式で作成してください。 */
    public static final String ME01_0096 = "ME01.0096";

    /** 項目名は「{0}」を入力してください。項目名：{1} */
    public static final String ME01_0091 = "ME01.0091";

    /** データが不正です。 詳細はエラーメッセージを確認してください。\n{0} */
    public static final String ME01_0106 = "ME01.0106";

    /** X102:上位I/Fファイル構成定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分:{1} */
    public static final String ME01_0107 = "ME01.0107";

    /** 同一ファイルが存在するため、処理終了します。ファイル名：{0} */
    public static final String ME01_0113 = "ME01.0113";

    /** HULFTへの送信要求に失敗しました。詳細はエラーメッセージを確認してください。\n送信ファイル名:{0} */
    public static final String ME01_0119 = "ME01.0119";

    /** 対象の伝文グループは使用不可能です。対象システムコード:{0} */
    public static final String ME01_0129 = "ME01.0129";

    /** 電子ファイル名または電子ファイルが設定されていません。 */
    public static final String ME01_0133 = "ME01.0133";

    /** 受信データ処理の呼出しでエラーが発生しました。詳細についてはログを確認してください。 */
    public static final String ME01_0149 = "ME01.0149";

    /** {0}は必須入力です。 */
    public static final String MV01_0002 = "MV01.0002";

    /** ({0}行目) */
    public static final String MV01_0026 = "MV01.0026";

    /** ログイン認証失敗  (ユーザーID： {0}) */
    public static final String MG01_0018 = "MG01.0018";

    /** データ不正です。項目名=[{1}] 値=[{0}] */
    public static final String ME01_0171 = "ME01.0171";

    /** 数値の文字列変換で、エラーが発生しました。値=[{0}] フィールド名=[{1}] */
    public static final String ME01_0099 = "ME01.0099";

    /** 日付の文字列変換で、エラーが発生しました。値=[{0}], フォーマット=[{1}], フィールド名=[{2}] */
    public static final String ME01_0100 = "ME01.0100";

    /** 設定する拠点を指定してください。 */
    public static final String ME01_0172 = "ME01.0172";

    /** コマンドIDが見つかりません。commandId={0} Bean定義シートと定数シートを確認してください \n commandIdがnullの場合、Bean定義シートのコマンドIDリソースIDが、定数定義のコマンドID定義に存在しません。 */
    public static final String ME01_0178 = "ME01.0178";

    /** 画面IDが見つかりません。screenId={0} Bean定義シートと定数シートを確認してください\n screenIdがnullの場合、Bean定義シートの画面IDリソースIdが定数定義の画面ID定義に存在しません。 */
    public static final String ME01_0179 = "ME01.0179";

    /**JSPで指定したカスタムタグのIDが画面アイテム定義のシート内に存在しません。種類={0} カスタムタグキー={1} */
    public static final String ME01_0180 = "ME01.0180";

    /** パスワード変更が完了しました。（ユーザーID： {0}、新しいパスワード：{1}） */
    public static final String MG01_0020 = "MG01.0020";

    /** 認証処理成功（処理：{0}、ユーザーID：{1}） */
    public static final String MG01_0021 = "MG01.0021";

    /** 認証処理失敗（処理：{0}、ユーザーID：{1}） */
    public static final String MG01_0022 = "MG01.0022";

    /** ダブル認証処理失敗  (ユーザーID： {0}、認証者： {1}) */
    public static final String MG01_0023 = "MG01.0023";

    /** パスワード変更成功  (ユーザーID： {0}) */
    public static final String MG01_0024 = "MG01.0024";

    /** パスワード変更失敗  (ユーザーID： {0}) */
    public static final String MG01_0025 = "MG01.0025";

    /** パスワード初期化成功 (ユーザーID： {0}) */
    public static final String MG01_0026 = "MG01.0026";

    /** パスワード初期化失敗 (ユーザーID： {0}) */
    public static final String MG01_0027 = "MG01.0027";

    /** メール通知を行いませんでした。（メッセージNo：{0}, 発生日時：{1}, 原因：{2}） */
    public static final String ME01_0141 = "ME01.0141";

    /** メール通知対象ユーザが設定されていません。 （{0}） */
    public static final String ME01_0142 = "ME01.0142";

    /** メール通知対象ユーザのメールアドレスが設定されていません。 （{0}） */
    public static final String ME01_0143 = "ME01.0143";

    /** メールサーバー情報の取得に失敗しました。（{0}） */
    public static final String ME01_0144 = "ME01.0144";

    /** メール通知に失敗しました。 （メッセージNo：{0}, 発生日時：{1}, 原因：{2}） */
    public static final String ME01_0145 = "ME01.0145";

    /** Talend（ジョブ名：{0}、コンポーネント：{1}）にてエラーが発生しました。エラーの詳細については、メッセージ履歴を確認してください。 */
    public static final String ME01_0146 = "ME01.0146";

    /** ユーザーID：{0}はロックアウトされました。 */
    public static final String ME01_0228 = "ME01.0228";

    /** 全てのキーはパスワード禁止文字に設定されています。 */
    public static final String ME01_0230 = "ME01.0230";

    /** パスワードの初期化ができませんでした。英字または数字のみのパスワードは使用できない設定になっていますが、パスワード禁止文字に全ての数字を登録しています。 */
    public static final String ME01_0232 = "ME01.0232";

    /** パスワードの初期化ができませんでした。英字または数字のみのパスワードは使用できない設定になっていますが、パスワード禁止文字に全ての英字を登録しています。 */
    public static final String ME01_0233 = "ME01.0233";

    /** 指定した端末がありません。（{0}） */
    public static final String ME01_0250 = "ME01.0250";

    /** Talend（ジョブ名：{0}、コンポーネント：{1}）にてエラーが発生しました。エラーの詳細については、メッセージ履歴を確認してください。{2}。*/
    public static final String ME01_0253 = "ME01.0253";
}
