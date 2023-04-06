/**
 *
 *
 * @version 0.01
 * @author  30018232 S.Hosokawa
 *
 * ============================ MODIFICATION HISTORY ============================
 * Release  Date       Comment
 * ------------------------------------------------------------------------------
 *
 * [END OF MODIFICATION HISTORY]
 * ==============================================================================
 */

//===================================================================
/**
 *
 */
// ===================================================================
// メッセージ画面 - 表サイズ変更
function changeSize(){
//
	// ヘッダー部の表示領域のheightを取得
	var headerHeight = $('#headerToolBox').height() + $('#headerBox').height();
	// 引当在庫表のヘッダー部のheightを取得
	var messageTableHeaderHeight = $('#TABLE_MESSAGE_LIST thead').height();
//	// /引当在庫表の枠線+改行時増加分
	var messageTableBorderHeight = parseInt(parseFloat($('#TABLE_MESSAGE_LIST').css('border-top-width'), 10) +
							parseFloat($('#TABLE_MESSAGE_LIST').css('border-bottom-width'), 10)) + 5;
//
//	//alert(headerHeight  + '\n' +  slotHeight  + '\n' + stockTextHeight  + '\n' + stockTableHeaderHeight + '\n' +  stockTableBorderHeight);
//
//	// 高さ設定
	$('#messageArea').css({'cssText':'height: calc(100vh - ' + (headerHeight) + 'px) !important;'});
	$('#messageAreaTable').css({'cssText':'height: calc(100vh - ' + (headerHeight) + 'px) !important;'});
	$('#TABLE_MESSAGE_LIST tbody').css({'cssText':'height: calc(100vh - ' + (headerHeight + messageTableHeaderHeight + messageTableBorderHeight) + 'px) !important;'});
//
}
//
$(window).load(function() {
	changeSize();
});
//
//
//var timer = false;
//$(window).resize(function() {
//    if (timer !== false) {
//        clearTimeout(timer);
//    }
//    timer = setTimeout(function() {
//    	changeSize();
//    }, 200);
//});


$(function(){
	$(".divButton").hover(
		function(){
			$(this).css("cursor","pointer"); //---カーソルを指に
		},
		function(){
			$(this).css("cursor","default"); //---カーソルを戻す
		}
	);
});

//ダイアログ中のフィールド開閉
function toggleField(field, area){

	//フィールド開閉
	$(area).slideToggle();

	//アイコン変更
	var right = $(field).find('.glyphicon-menu-right');
	var down = $(field).find('.glyphicon-menu-down');

	right.removeClass("glyphicon-menu-right");
	right.addClass("glyphicon-menu-down");

	down.removeClass("glyphicon-menu-down");
	down.addClass("glyphicon-menu-right");

}


