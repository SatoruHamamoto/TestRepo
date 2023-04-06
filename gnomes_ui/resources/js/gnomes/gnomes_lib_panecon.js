// 上下展開の処理
$(document).ready(function() {
  $('.common-separator-horizon').on('click', function() {
      $('.common-main-area').toggleClass('common-head-none');
  });
});

// 工程端末画面 キーボード表示
$(document).ready(function() {
  $('.common-keyboard-input').keyboard();
});

// 数値入力ダイアログ
$(document).ready(function() {
  $('.common-keyboard-number').keyboard();
});

// 工程端末画面 デイトピッカー(期間）
$(document).ready(function() {
  $('.deliveryDate-to,.deliveryDate-from').datepicker({
      "dateFormat" : "yy/mm/dd"
  });
});

// 工程端末画面 デイトピッカー(日付）
$(document).ready(function() {
  $('.deliveryDate').datepicker({
      "dateFormat" : "yy/mm/dd"
  });
});

// 時刻入力ダイアログ
$(document).ready(function() {
  $('.deliveryTime').datepicker({
      "dateFormat" : "HH:mm"
  });
});

// 日時入力ダイアログ
$(document).ready(function() {
  $('.deliveryDateTime').datepicker({
      "dateFormat" : "yy/mm/dd HH:mm"
  });
});

// 選択ダイアログ
$(document).ready(function() {
  $('.common-select-dialog-input').keyboard();
});

// 一覧選択ダイアログ
$(document).ready(function() {
  $('.common-table-dialog-input').keyboard();
});

// 表示されているチェックボックスにチェックをつける
function checkAll(chkboxName, checked, syncMode) {
  var chkList = document.getElementsByName(chkboxName);
  for (var i = 0; i < chkList.length; i++) {
      var chkbox = chkList[i];
      chkbox.checked = checked;
  }
}

// スクロールバー制御
$(function() {
  // ボタンクリック時の移動量
  var scroll_step = 0;

  // 画面IDで移動量を設定
  if ($('.p99001-data-table').length) {
      scroll_step = 80;
  }
  else if ($('#panecon-treeview').length) {
      scroll_step = 30;
  }
  else {
      scroll_step = 64;
  }

  // スクロールの倍率
  var magnification = 5;

  // trタグの高さ取得
  var table_tr_height_out = $('.common-scroll-box table tbody tr').outerHeight();

  // スクロールコンテンツの高さ取得
  var scroll_contents_height = $('.common-data-table-t').innerHeight();

  // テーブルのtr要素数取得
  var tr_length = $('.common-scroll-box table tr').length;

  // 最大行数
  $('.common-scroll-all').text(tr_length - 1);

  $('.common-scroll-box:not(.fixed_header_display_none_at_print)').each(function(index, element){
    scroll_pos(element);
     // テーブルのtr要素数取得
    var tr_length = $(element).find('tr').length;
    //$(element).find('.common-scroll-all').text(tr_length - 1);
    $(element).closest('.common-data-scroll-area').children('.common-scroll-button-area').find('.common-scroll-all').text(tr_length - 1);
  });

  // 現在値の初期化
  //scroll_pos();

  // スクロールの現在値
  function scroll_pos(my) {
    var pos_now = $(my).scrollTop();
    var tr_length = $(my).children('.common-data-table-t').find('tr').length
    var scroll_contents_height = $(my).children('.common-data-table-t').innerHeight();
    //var pos_now = $('.common-scroll-box').scrollTop();
    var pos_propotion = pos_now / scroll_contents_height;
    var pos_line = tr_length * pos_propotion;

    // var teble_pos_now = Math.round(pos_now / table_tr_height_out);
    var teble_pos_now = Math.round(pos_line);

     if(tr_length == 1) {
        //$('.common-scroll-pos').text(teble_pos_now);
        $(my).closest('.common-data-scroll-area').children('.common-scroll-button-area').find('.common-scroll-pos').text(teble_pos_now);
    } else {
        //$('.common-scroll-pos').text(teble_pos_now + 1);
        $(my).closest('.common-data-scroll-area').children('.common-scroll-button-area').find('.common-scroll-pos').text(teble_pos_now + 1);
    }
  }

  // スクロールイベント検知
  $('.common-scroll-box').scroll(function() {
      scroll_pos($(this));
  });

  // HOMEボタンクリック
  $('.common-scroll-button-home').on('click', function() {

      $('.common-scroll-box').scrollTop(0);

  });

  // 上上ボタンクリック
  $('.common-scroll-button-upper').on('click', function() {

    var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
    var pos = $(scrboxobj).scrollTop();
    // $(scrboxobj).scrollTop(pos - (scroll_step * magnification));
    if (scroll_step!=64) {
    	$(scrboxobj).scrollTop(pos - (scroll_step * magnification));
    } else {
    	$(scrboxobj).scrollTop(pos - (table_tr_height_out * magnification));
    }


  });

  // 上ボタンクリック
  $('.common-scroll-button-up').on('click', function() {

    var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
    var pos = $(scrboxobj).scrollTop();
    // $(scrboxobj).scrollTop(pos - scroll_step);
    if (scroll_step!=64) {
    	$(scrboxobj).scrollTop(pos - scroll_step);
    } else {
    	$(scrboxobj).scrollTop(pos - table_tr_height_out);
    }

  });

  // 下ボタンクリック
  $('.common-scroll-button-down').on('click', function() {

    var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
    var pos = $(scrboxobj).scrollTop();
    // $(scrboxobj).scrollTop(pos + scroll_step);
    if (scroll_step!=64) {
    	$(scrboxobj).scrollTop(pos + scroll_step);
    } else {
    	$(scrboxobj).scrollTop(pos + table_tr_height_out);
    }
  });

  // 下下ボタンクリック
  $('.common-scroll-button-under').on('click', function() {

      var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
      var pos = $(scrboxobj).scrollTop();
      // $(scrboxobj).scrollTop(pos + (scroll_step * magnification));
      if (scroll_step!=64) {
      	$(scrboxobj).scrollTop(pos + (scroll_step * magnification));
      } else {
      	$(scrboxobj).scrollTop(pos + (table_tr_height_out * magnification));
      }

  });

});

//スクロールバー制御
function scrollButtonStep(scrollStepPixel) {
  // ボタンクリック時の移動量
  var scroll_step = 0;

  // 画面IDで移動量を設定
  if (scrollStepPixel != null && scrollStepPixel != undefined && scrollStepPixel != "") {
   scroll_step = scrollStepPixel;
  }

  if (scroll_step == 0) {
   scroll_step = 64;
  }

  // スクロールの倍率
  var magnification = 5;

  // trタグの高さ取得
  var table_tr_height_out = $('.common-scroll-box table tbody tr').outerHeight();

  // スクロールコンテンツの高さ取得
  var scroll_contents_height = $('.common-data-table-t').innerHeight();

  // テーブルのtr要素数取得
  var tr_length = $('.common-scroll-box table tr').length;

  // 最大行数
  $('.common-scroll-all').text(tr_length - 1);

  $('.common-scroll-box:not(.fixed_header_display_none_at_print)').each(function(index, element){
   scroll_pos(element);
    // テーブルのtr要素数取得
   var tr_length = $(element).find('tr').length;
   //$(element).find('.common-scroll-all').text(tr_length - 1);
   $(element).closest('.common-data-scroll-area').children('.common-scroll-button-area').find('.common-scroll-all').text(tr_length - 1);
  });

  // 現在値の初期化
  //scroll_pos();

  // スクロールの現在値
  function scroll_pos(my) {
   var pos_now = $(my).scrollTop();
   var tr_length = $(my).children('.common-data-table-t').find('tr').length
   var scroll_contents_height = $(my).children('.common-data-table-t').innerHeight();
   //var pos_now = $('.common-scroll-box').scrollTop();
   var pos_propotion = pos_now / scroll_contents_height;
   var pos_line = tr_length * pos_propotion;

   // var teble_pos_now = Math.round(pos_now / table_tr_height_out);
   var teble_pos_now = Math.round(pos_line);

   //$('.common-scroll-pos').text(teble_pos_now + 1);
   $(my).closest('.common-data-scroll-area').children('.common-scroll-button-area').find('.common-scroll-pos').text(teble_pos_now + 1);
  }

  // スクロールイベント検知
  $('.common-scroll-box').scroll(function() {
     scroll_pos($(this));
  });

  $('.common-scroll-button-home').unbind('click');
  // HOMEボタンクリック
  $('.common-scroll-button-home').on('click', function() {

     $('.common-scroll-box').scrollTop(0);

  });

  $('.common-scroll-button-upper').unbind('click');
  // 上上ボタンクリック
  $('.common-scroll-button-upper').on('click', function() {

   var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
   var pos = $(scrboxobj).scrollTop();
   $(scrboxobj).scrollTop(pos - (scroll_step * magnification));

  });

  $('.common-scroll-button-up').unbind('click');
  // 上ボタンクリック
  $('.common-scroll-button-up').on('click', function() {

   var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
   var pos = $(scrboxobj).scrollTop();
   $(scrboxobj).scrollTop(pos - scroll_step);

  });

  $('.common-scroll-button-down').unbind('click');
  // 下ボタンクリック
  $('.common-scroll-button-down').on('click', function() {

   var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
   var pos = $(scrboxobj).scrollTop();
   $(scrboxobj).scrollTop(pos + scroll_step);

  });

  $('.common-scroll-button-under').unbind('click');
  // 下下ボタンクリック
  $('.common-scroll-button-under').on('click', function() {

   var scrboxobj = $(this).closest('.common-data-scroll-area').find('.common-scroll-box');
   var pos = $(scrboxobj).scrollTop();
   $(scrboxobj).scrollTop(pos + (scroll_step * magnification));

  });

}
