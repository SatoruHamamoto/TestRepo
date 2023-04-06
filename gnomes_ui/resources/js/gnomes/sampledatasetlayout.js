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
//SOP�n��ʍ����T�C�Y�ύX
function changeSizeLeft(){

	//�w�b�_�[���̕\���̈��height���擾
	var headerHeight = $('#headerDataBox').height() + $('#headerToolBox').height() + $('#headerBox').height();
	//�����e�v�f�̋󔒕��E�g������height���擾
	var marginLeft = parseInt($('#mainBoxLeft').css('padding-top'), 10) +
				parseInt($('#mainBoxLeft').css('padding-bottom'), 10);

	//�����X�N���[���{�^���i�㉺�j��height���擾
	var sopButtonHeight = 0;
	$('.sopTableScrollButton').each(function(index, element){
		sopButtonHeight += $(element).outerHeight();
	});
	//alert(headerHeight +" "+ marginLeft + " " + sopAreaMargineHeight);
	//�����e�[�u���\���̈�ȊO�̕\���̈��height���v�Z
	var sopAreaMargineHeight = headerHeight + marginLeft + sopButtonHeight;
	//�����e�[�u���w�b�_�[��height���擾
	var sopTableHeaderHeight = $('#TABLE_SOP_LIST thead').height();
	//�����e�[�u���̘g��
	var sopTableBorderHeight = parseInt(parseFloat($('#TABLE_SOP_LIST').css('border-top-width'), 10) +
							parseFloat($('#TABLE_SOP_LIST').css('border-bottom-width'), 10)) + 1;

	//�����ݒ� ��
	$('#mainBoxLeft').css({'cssText':'height: calc(100vh - ' + headerHeight + 'px) !important;'});
	$('#mainBoxLeftTable').css({'cssText':'height: calc(100vh - ' + sopAreaMargineHeight + 'px) !important;'});
	$('#TABLE_SOP_LIST tbody').css({'cssText':'height: calc(100vh - ' + (sopAreaMargineHeight + sopTableHeaderHeight + sopTableBorderHeight) + 'px) !important;'});
	$('.openButton').css({'cssText':'height: calc(100vh - ' + (headerHeight + marginLeft) + 'px) !important;'});

}

//SOP�n��ʉE���T�C�Y�ύX
function changeSizeRight(){
	//�w�b�_�[���̕\���̈��height���擾
	var headerHeight = $('#headerDataBox').height() + $('#headerToolBox').height() + $('#headerBox').height();

	//�E���e�v�f�̋󔒕��E�g������height���擾
	var marginRight = parseInt($('#mainBoxRightMargin').css('margin-top'), 10) +
				parseInt($('#mainBoxRightMargin').css('margin-bottom'), 10) +
				parseInt($('#mainBoxRightBorder').css('border-top-width'), 10) +
				parseInt($('#mainBoxRightBorder').css('border-bottom-width'), 10)  +
				parseInt($('#mainBoxRight').css('padding-bottom'), 10) +
				parseInt($('#mainBoxRight').css('padding-top'), 10);
	//�E���X�N���[���{�^���i�㉺�j��height���擾
	var mainButtonHeight = 0;
	$('.mainTableScrollButton').each(function(index, element){
		mainButtonHeight += $(element).outerHeight();
	});

	//�E���e�[�u���\���̈�ȊO�̕\���̈��height���v�Z
	var mainAreaMargineHeight = headerHeight + marginRight + mainButtonHeight;
	//�E���e�[�u���w�b�_�[��height���擾
	var mainTableHeaderHeight = $('#TABLE_DATA_SET_LIST3 thead').height();


	//�����ݒ� �E
	$('#mainBoxRight').css({'cssText':'height: calc(100vh - ' + headerHeight + 'px) !important;'});
	$('#mainBoxRightTable').css({'cssText':'height: calc(100vh - ' + mainAreaMargineHeight + 'px) !important;'});
	$('#TABLE_DATA_SET_LIST3 tbody').css({'cssText':'height: calc(100vh - ' + (mainAreaMargineHeight + mainTableHeaderHeight) + 'px) !important;'});
}

