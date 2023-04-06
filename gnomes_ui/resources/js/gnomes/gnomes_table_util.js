/**
 * フレームワーク共通処理スクリプト
 *  チェックボックス付きテーブルカスタムタグ定義の単一選択を強制するスクリプト
 *      テーブルを指定するのでtableタグに独自にクラス名を定義すること
 *      JSPやHTMLファイルに本jsをincludeし、setTableSingleSelect(target)を初回コールすることで動作する
 *
 * @version 0.01
 * @author  03501213 S.Hamamoto
 *
 * ============================ MODIFICATION HISTORY ============================
 * Release  Date       Comment
 * ------------------------------------------------------------------------------
 * 0.0      2019/01/25  初版
 *
 * [END OF MODIFICATION HISTORY]
 * ==============================================================================
 */
$(document).ready(function () {

  setTableSingleSelect = function (target) {
    var clickHandler = target[0].onclick; // イベントを退避
    // 全行のクリックイベントをリセット
    for (var i = 0; i < target.length; i++) {
      target[i].onclick = null;         // クリックイベントをリセット
    }

    target.click(function () {
      var checked = $(this).prop('checked');
      $(target).prop('checked', false);
      $(this).prop('checked', checked);
    });           // 差し込み
    target.click(clickHandler);       // その後にオリジナルをしまう
  };
});

/**
  * フレームワーク共通処理スクリプト
  *  チェックボックス付きテーブルカスタムタグ定義の未選択、複数選択を確認するスクリプト
  *      テーブルを指定するのでtableタグに独自にクラス名を定義すること
  *      JSPやHTMLファイルに本jsをincludeし、onClickイベントに設定することで動作する
  */

//===================================================================
/**
 * [機能]    チェックボックスの選択状態の確認
 * [引数]    target       対象テーブルの対象チェックボックス群(セレクタ)
 *          dialogid     表示するダイアログID
 *          evt          表示時に実行する処理
 *          type         確認内容（0：複数選択不可、1：複数選択可）
 * [戻値]    結果        html
 */
//===================================================================
function tableSelectedCheck(target, dialogid, evt, type) {
  var checkeboxSelected = 0;
  var nonSelectMsg = $('input:hidden[name="nonSelectedMessage"]').val();
  var multipleSelectMsg = $('input:hidden[name="multipleSelectedMessage"]').val();

  for (var i = 0; i < target.length; i++) {
    if (target[i].checked) { // 選択行の有無
      checkeboxSelected++;
    }
  }

  //選択数により処理を分岐y
  switch (checkeboxSelected) {
    case 0:
      //エラーメッセージ（未選択）
      MakeModalMessage($('#msgBoxTitleError').text(), nonSelectMsg, $('#msgBoxBtnNameOk'));
      return false;
    case 1:
      //処理を実行
      dialogid.on('shown.bs.modal', evt).modal();
      break;
    default:
      if (type == 0) {
        //エラーメッセージ（複数選択不可）
        MakeModalMessage($('#msgBoxTitleError').text(), multipleSelectMsg, $('#msgBoxBtnNameOk'));
        return false;
      } else {
        //処理を実行
        dialogid.on('shown.bs.modal', evt).modal();
      }
  }
  return true;
}
