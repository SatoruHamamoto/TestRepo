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
//�e�[�u���̉��X�N���[���ʒu���e�[�u���w�b�_�[�ɓ�������
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


