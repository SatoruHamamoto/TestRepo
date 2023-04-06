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
//テーブルの横スクロール位置をテーブルヘッダーに同期する
$(function () {
	$('#tableAreaTable').scroll(function() {
		$('#tableAreaHeader').scrollLeft($('#tableAreaTable').scrollLeft());
	});
});

$(function () {
	$('#topTableAreaTable').scroll(function() {
		$('#topTableAreaHeader').scrollLeft($('#topTableAreaTable').scrollLeft());
	});
});

function copyDate(itemID, copyItemID){
	$(copyItemID).val($(itemID).val());
}


