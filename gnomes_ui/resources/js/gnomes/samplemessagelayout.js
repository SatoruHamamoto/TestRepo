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
// ���b�Z�[�W��� - �\�T�C�Y�ύX
function changeSize(){
//
	// �w�b�_�[���̕\���̈��height���擾
	var headerHeight = $('#headerToolBox').height() + $('#headerBox').height();
	// �����݌ɕ\�̃w�b�_�[����height���擾
	var messageTableHeaderHeight = $('#TABLE_MESSAGE_LIST thead').height();
//	// /�����݌ɕ\�̘g��+���s��������
	var messageTableBorderHeight = parseInt(parseFloat($('#TABLE_MESSAGE_LIST').css('border-top-width'), 10) +
							parseFloat($('#TABLE_MESSAGE_LIST').css('border-bottom-width'), 10)) + 5;
//
//	//alert(headerHeight  + '\n' +  slotHeight  + '\n' + stockTextHeight  + '\n' + stockTableHeaderHeight + '\n' +  stockTableBorderHeight);
//
//	// �����ݒ�
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
			$(this).css("cursor","pointer"); //---�J�[�\�����w��
		},
		function(){
			$(this).css("cursor","default"); //---�J�[�\����߂�
		}
	);
});

//�_�C�A���O���̃t�B�[���h�J��
function toggleField(field, area){

	//�t�B�[���h�J��
	$(area).slideToggle();

	//�A�C�R���ύX
	var right = $(field).find('.glyphicon-menu-right');
	var down = $(field).find('.glyphicon-menu-down');

	right.removeClass("glyphicon-menu-right");
	right.addClass("glyphicon-menu-down");

	down.removeClass("glyphicon-menu-down");
	down.addClass("glyphicon-menu-right");

}


