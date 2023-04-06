// テーブルの見出し設定
$(document).ready(function(){
  window.addEventListener("load", function() {
    FixedMidashi.create();

    // 初回表示時のみダブルクリックイベントは自動で追加されるので省略
  }, false);
});

// テーブルがリサイズされた時の見出し再設定
$(window).resize(function () {
  FixedMidashi.create();
  
  // GnomesCTagSortDialog側で対応のためここでのダブルクリック処理は省略
});

// 検索ウィンドウを操作した時の見出し設定
$( function () {
  $('.common-separator-vartical').on('click', function () {
	FixedMidashi.create();
	  
	// 固定ヘッダにダブルクリックでのイベントを追加
    setDblClickEvent();
  });
});

// マウスホバー判定用クラスの追加
$( function () {
  $('.common-table-ttl-fix tr').each(function(i){
    $(this).attr('onmouseover', 'mouseover(' + i + ');');
    $(this).attr('onmouseout', 'mouseout(' + i + ');');
    $(this).addClass('common-table-tr-' + i);
  });
});

// テーブル表示件数 一覧表示処理
$( function () {
  $('.common-pageNum-current').on('click', function () {
    $(this).next('.common-pageNum-select').toggleClass('common-pageNum-select-show');
  });
});

// テーブル表示件数 選択処理
function selectPageNum(selectTag) {

	// 選択値取得
    var pageNum = selectTag.value;

    // 数値選択
    $(".common-pageNum-option", selectTag).each(function() {
        if ($(this).val() == pageNum) {

            var tableId = $(this).parent().parent().data('tableid');
            // テーブルの1ページ表示件数を設定
            setTableOnePageDispCount(tableId, pageNum);

            // 数値表示
            var pageNumTag = $(this).parent().parent().parent();
            $('.common-pageNum-current', pageNumTag).text(pageNum);

            if(!$(this).hasClass( "common-pageNum-option-selected" )){
                $(this).addClass( "common-pageNum-option-selected" );
            }

        } else {
            if($(this).hasClass( "common-pageNum-option-selected" )){
                $(this).removeClass( "common-pageNum-option-selected" );
            }
        }
    });

}



// マウスのテーブルホバー時の色変更
function mouseover( index ){
  $('.common-table-tr-' + index).addClass('common-table-hover');
}

// マウスがホバーが外れた時の設定
function mouseout( index ){
  $('.common-table-tr-' + index).removeClass('common-table-hover');
}

// 見出しのチェックボックスの連動
function checkAll( chkboxName, checked, syncMode ){
	var chkList = document.getElementsByName(chkboxName);
	for (var i = 0; i < chkList.length; i++) {
		var chkbox = chkList[i];
		chkbox.checked = checked;
		if (syncMode) FixedMidashi.syncValue(chkbox);
	}
}

// 詳細系(タイプ3)で使用するタブ
//
$( function () {
  function recreate_midashi(){
	  FixedMidashi.create();
	  
      // 固定ヘッダにダブルクリックでのイベントを追加
      setDblClickEvent();
  }
  $('.p0004-tab-label1').on('click', function () {
    setTimeout(function(){
      recreate_midashi();
    }, 100);
  });

  $('.p0004-tab-label2').on('click', function () {
    setTimeout(function(){
      recreate_midashi();
    }, 100);
  });

  $('.p0004-tab-label3').on('click', function () {
    setTimeout(function(){
      recreate_midashi();
    }, 100);
  });
});

//===================================================================
/**
 * [機能]	ページングコマンド発行
 * [引数]	command			コマンド
 *			pagingCommand	ページングコマンド
 * [戻値]	なし
 */
//===================================================================
function funcPaging(command, pagingCommand, pageingParam) {

	var exit = false;
	// 検索ボタンを押す場合、禁則文字をチェック
	$(".common-flexMenu-search-box.common-flexMenu-size").each(function() {
        var valid = checkInvalidValSearchCondition();
        if(valid == false) {
        	var format = $('[name=dateFormat]').val() + ":ss";
            var dateFormat = comDateFormat(new Date(), format);
            makeMessageModalBase(
					$('[name=mesgCategoryName]').val(),   //メッセージタイトル
            		$('[name=mesgIconName]').val(),       //アイコン名
                    dateFormat,                           //発生日時
                    $(".common-header-userName").html(),  //操作ユーザ名
                    $(".common-header-deviceName").html(),//操作ホスト
                    $('[name=mesgBodyName]').val(),       //メッセージ本文
                    null,                                 //メッセージ詳細
                    null,                                 //ボタンモード
                    null,                                 //デフォルトボタン
                    null,                                 //コマンド
                    null,                                 //リンク情報
                    false,                                //オープン時詳細
                    null,                                 //OKボタン押下時呼び出し関数
                    null,                                 //Cancelボタン押下呼び出し関数
                    null,                                 //ボタンID
                    ""                                    //DB領域
            );
            exit = true;
        }
    });
	if (exit) {
		return;
	}
	makeSearchMenuMap(true);

    // 処理中にダイアログを表示
    processingDialog();

	document.main.command.value = command;
	document.main.pagingCommand.value = pagingCommand;
	document.main.pagingParam.value = pageingParam;
	document.main.submit();
}

