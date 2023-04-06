/**
 * フレームワーク共通処理スクリプト チェックボックス付きテーブルカスタムタグ定義の単一選択を強制するスクリプト
 * テーブルを指定するのでtableタグに独自にクラス名を定義すること
 * JSPやHTMLファイルに本jsをincludeし、setTableSingleSelect(target)を初回コールすることで動作する
 *
 * @version 0.01
 * @author 03501213 S.Hamamoto
 *
 * ============================ MODIFICATION HISTORY ============================
 *  Release Date Comment
 * ------------------------------------------------------------------------------
 * 0.0 2019/01/25 初版
 *
 * [END OF MODIFICATION HISTORY]
 * ==============================================================================
 */
$(document).ready(function() {

	setTableSingleSelect = function(target) {
		if (target.length == 0)
			return;
		// var clickHandler = target[0].onclick; // イベントを退避
		// 全行のクリックイベントをリセット
		var saveEvent = [];
		for (var i = 0; i < target.length; i++) {
			saveEvent.push(target[i].onclick);
			target[i].onclick = null; // クリックイベントをリセット
		}

		target.click(function() {
			var checked = $(this).prop('checked');
			$(target).prop('checked', false);
			$(this).prop('checked', checked);
		});
		// 差し込み
		// target.click(clickHandler); // その後にオリジナルをしまう
		// 全行のクリックイベントを復元
		for (var i = 0; i < target.length; i++) {
			target[i].onclick = saveEvent[i]; // クリックイベントを復元
		}
	};

});