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
//===================================================================
//SOP系画面左側サイズ変更
function changeSizeLeft(){

	//ヘッダー部の表示領域のheightを取得
	var headerHeight = $('#headerDataBox').height() + $('#headerToolBox').height() + $('#headerBox').height();
	//左側各要素の空白部・枠線部のheightを取得
	var marginLeft = parseInt($('#mainBoxLeft').css('padding-top'), 10) +
				parseInt($('#mainBoxLeft').css('padding-bottom'), 10);

	//左側スクロールボタン（上下）のheightを取得
	var sopButtonHeight = 0;
	$('.sopTableScrollButton').each(function(index, element){
		sopButtonHeight += $(element).outerHeight();
	});
	//alert(headerHeight +" "+ marginLeft + " " + sopAreaMargineHeight);
	//左側テーブル表示領域以外の表示領域のheightを計算
	var sopAreaMargineHeight = headerHeight + marginLeft + sopButtonHeight;
	//左側テーブルヘッダーのheightを取得
	var sopTableHeaderHeight = $('#TABLE_SOP_LIST thead').height();
	//左側テーブルの枠線
	var sopTableBorderHeight = parseInt(parseFloat($('#TABLE_SOP_LIST').css('border-top-width'), 10) +
							parseFloat($('#TABLE_SOP_LIST').css('border-bottom-width'), 10)) + 1;

	//高さ設定 左
	$('#mainBoxLeft').css({'cssText':'height: calc(100vh - ' + headerHeight + 'px) !important;'});
	$('#mainBoxLeftTable').css({'cssText':'height: calc(100vh - ' + sopAreaMargineHeight + 'px) !important;'});
	$('#TABLE_SOP_LIST tbody').css({'cssText':'height: calc(100vh - ' + (sopAreaMargineHeight + sopTableHeaderHeight + sopTableBorderHeight) + 'px) !important;'});
	$('.openButton').css({'cssText':'height: calc(100vh - ' + (headerHeight + marginLeft) + 'px) !important;'});

}

//SOP系画面右側サイズ変更
function changeSizeRight(){
	//ヘッダー部の表示領域のheightを取得
	var headerHeight = $('#headerDataBox').height() + $('#headerToolBox').height() + $('#headerBox').height();

	//右側各要素の空白部・枠線部のheightを取得
	var marginRight = parseInt($('#mainBoxRightMargin').css('margin-top'), 10) +
				parseInt($('#mainBoxRightMargin').css('margin-bottom'), 10) +
				parseInt($('#mainBoxRightBorder').css('border-top-width'), 10) +
				parseInt($('#mainBoxRightBorder').css('border-bottom-width'), 10)  +
				parseInt($('#mainBoxRight').css('padding-bottom'), 10) +
				parseInt($('#mainBoxRight').css('padding-top'), 10);
	//右側スクロールボタン（上下）のheightを取得
	var mainButtonHeight = 0;
	$('.mainTableScrollButton').each(function(index, element){
		mainButtonHeight += $(element).outerHeight();
	});

	//右側テーブル表示領域以外の表示領域のheightを計算
	var mainAreaMargineHeight = headerHeight + marginRight + mainButtonHeight;
	//右側テーブルヘッダーのheightを取得
	var mainTableHeaderHeight = $('#TABLE_DATA_SET_LIST3 thead').height();


	//高さ設定 右
	$('#mainBoxRight').css({'cssText':'height: calc(100vh - ' + headerHeight + 'px) !important;'});
	$('#mainBoxRightTable').css({'cssText':'height: calc(100vh - ' + mainAreaMargineHeight + 'px) !important;'});
	$('#TABLE_DATA_SET_LIST3 tbody').css({'cssText':'height: calc(100vh - ' + (mainAreaMargineHeight + mainTableHeaderHeight) + 'px) !important;'});
}

