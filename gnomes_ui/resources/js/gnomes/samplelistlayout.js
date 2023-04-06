/**
 *
 *
 * @version 0.01
 * @author  03501213 S.Hamamoto
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
function drawDetail_hoge(btnObj){
	var data = [];
	var objHeader = [];
	var tr = $(btnObj).parents("tr");
	for( var i=0,l=tr.length;i<l;i++ ){
		var cells = tr.eq(i).children();
		for( var j=0,m=cells.length;j<m;j++ ){
			if( typeof data[i] == "undefined" ){
				data[i] = [];
			}
			data[i][j] = cells.eq(j).find("input").val();
		}
	}
	var th = $("#TABLE_PROD_INST_LIST").find("th");
	for( var i=0,l=th.length;i<l;i++ ){
		objHeader[i] = th[i].textContent;
	}
  var $cur_td = $(this)[0]; 
  var $cur_tr = $(this).parent()[0]; 
}

function statusIconSet(){

	$("#TABLE_PROD_INST_LIST").find("input").each( function() {
		//selvalue.push($(this).val());
		var objName = $(this).attr("name");
		if(objName.match(/NMLTXT_PRODUCTION_STATUS/i)){
			//if($(this).attr("name")=="NMLTXT_PRODUCTION_STATUS"
			//console.log($(this).attr("name") + " = " + $(this).val());
			var iconfile = "signs";
			var status = $(this).val();
			switch(status){
				case '�w�}�쐬':
					break;
				case '�m�F��':
					iconfile="approval-symbol-in-badge";
					break;
				case '���s��':
					iconfile="running";
					break;
				case '���F��':
					iconfile="thumb-up-sign";
					break;
			}
			
			$(this).replaceWith("<img src='images/icons/" + iconfile + ".png'></img>");
		}
		//$(div).appendChild(this.childNodes.length - 1);

	});
}

function clearDetailArea(){
	$(".form-horizontal").find("input").each( function() {
		$(this).val("");
	});
	$(".form-horizontal").find("label").each( function() {
		$(this)[0].innerText="";
	});
}

function drawDetail(btnObj){

	var id;
	var parentTr = $(btnObj).parents("tr");
	var selIndex = parentTr.index();
	var idObj = parentTr.eq(0).find("input[name='NMLTXT_PRODUCTION_KEY_R_" + selIndex + "']");
	id = idObj.val();


	$.ajax({
        type: "GET",
        async: false,
		cache : false,
        url: "/gnomes/ui/proInfo/getProdInstDetail?id=" + id,
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: function (data) {
			var domString="";
			for (var i = 0; i < data.dtList.length; i++) {
				var contentsObj = data.dtList[i];
				domString += "<div class='" + contentsObj.divMainClass + "'>";
				domString += "<label class='" + contentsObj.labelClass + "' for='" + contentsObj.labelFor + "'>" + contentsObj.labelText + "</label>";
				domString += "<div class='" + contentsObj.divColClass + "'>";
				domString += "<input type='text' class='" + contentsObj.txtClass + "' name='" + contentsObj.txtIdName + "' placeholder='" + contentsObj.placeFolder+"' ";
				domString += "value='" + contentsObj.txtValue + "'";
				if(contentsObj.txtIdName.match(/Key/i)){
					domString += " readOnly='true' ";
				}
				domString += "></input>";
				domString += "</div></div>";
			}
			$('#detail_form').html(domString);
		}
		,
        error: function (e) {
            alert("�����ݒ���̎擾�Ɏ��s���܂����B<br />[Web Error]");
        }
    });

}

function CreateDialogOpen(){

	$.ajax({
        type: "GET",
        async: false,
		cache : false,
        url: "/gnomes/ui/proInfo/getNewProdInstDetail",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: function (data) {
			var domString="";
			for (var i = 0; i < data.dtList.length; i++) {
				var contentsObj =  data.dtList[i];
				domString += "<div class='" + contentsObj.divMainClass + "'>";
				domString += "<label class='" + contentsObj.labelClass + "' for='" + contentsObj.labelFor + "'>" + contentsObj.labelText + "</label>";
				domString += "<div class='" + contentsObj.divColClass + "'>";
				domString += "<input type='text' class='" + contentsObj.txtClass + "' name='" + contentsObj.txtIdName + "' placeholder='" + contentsObj.placeFolder+"' ";
				domString += "value='" + contentsObj.txtValue + "'></input>";
				domString += "</div></div>";
			}
			$('#neweditarea_form').html(domString);
		}
		,
        error: function (e) {
            alert("�����ݒ���̎擾�Ɏ��s���܂����B<br />[Web Error]");
        }
    });

}

$( document ).ready(function() {

	$('.collapse').on('shown.bs.collapse', function() {
		$(this).addClass('in');
		recalcDisplaySubWindow();
	});
	$('.collapse ').on('hidden.bs.collapse', function() {
		recalcHiddenSubWindow();
		$(this).removeClass('in');
	});
});

function recalcDisplaySubWindow()
{
	var sW = window.innerWidth;
	var sH = window.innerHeight;
	console.log(sW + "x" + sH);
	var top = $('.collapse').css("top");
	$('#main_area').css('height',((sH/2)-50) + 'px');
	console.log('height',((sH/2)-20) + 'px');
	$('.span1').css('height',((sH/2)-20) + 'px');
}
function recalcHiddenSubWindow(){
		
		$('#main_area').css('height','100%');
		$('.span1').css('height','');
}

//�E�B���h�E�T�C�Y�ύX���ɍX�V
window.onresize = window_load;

//�T�C�Y�̕\��
function window_load() {
	if($(".collapse.in").attr("id") != undefined){
		recalcDisplaySubWindow();
	}
}

/*
(function($){ 
    
    $(function(){
        // �X�N���[���������������ɏ������s��
        $(window).scroll(function () {
 
            var $table = $(".table");          // �e�[�u���̗v�f���擾
            var $thead = $table.children("thead");  // thead�擾
            var toffset = $table.offset();          // �e�[�u���̈ʒu���擾
            // �e�[�u���ʒu+�e�[�u���c�� < �X�N���[���ʒu < �e�[�u���ʒu
            if(toffset.top + $table.height()< $(window).scrollTop() 
              || toffset.top > $(window).scrollTop()){
                // �N���[���e�[�u�������݂���ꍇ�͏���
                var $clone = $("#clonetable");
                if($clone.length > 0){
                    $clone.css("display", "none");
                }
            }
            // �e�[�u���ʒu < �X�N���[���ʒu < �e�[�u���ʒu+�e�[�u���c��
            else if(toffset.top < $(window).scrollTop()){
                // �N���[���e�[�u�������݂��邩�m�F
                var $clone = $("#clonetable");
                if($clone.length == 0){
                    // ���݂��Ȃ��ꍇ�́Athead�̃N���[�����쐬
                    $clone= $thead.clone(true);
                    // id��clonetable�Ƃ���
                    $clone.attr("id", "clonetable");
                    // body���ɗv�f��ǉ�
                    $clone.appendTo("body");
                    // thead��CSS���R�s�[����
                    StyleCopy($clone, $thead);
                    // thead�̎q�v�f(tr)�����[�v������
                    for(var i = 0; i < $thead.children("tr").length; i++)
                    {
                        // i�Ԗڂ�tr���擾
                        var $theadtr = $thead.children("tr").eq(i);
                        var $clonetr = $clone.children("tr").eq(i);
                        // tr�̎q�v�f(th)�����[�v������
                        for (var j = 0; j < $theadtr.eq(i).children("th").length; j++){
                            // j�Ԗڂ�th���擾
                            var $theadth = $theadtr.eq(i).children("th").eq(j);
                            var $cloneth = $clonetr.eq(i).children("th").eq(j);
                            // th��CSS���R�s�[����
                            StyleCopy($cloneth, $theadth);
                        }
                    }
                }
 
                // �R�s�[����thead�̕\���`����table�ɕύX
                $clone.css("display", "table");         
                // position���u���E�U�ɑ΂���Βl�Ƃ���
                $clone.css("position", "fixed");        
                $clone.css("border-collapse", "collapse");
                // position�̈ʒu��ݒ�(left = ���̃e�[�u����left�Ƃ���)
                $clone.css("left", toffset.left - $(window).scrollLeft());
                // position�̈ʒu��ݒ�(top���u���E�U�̈�ԏ�Ƃ���)
                $clone.css("top", "100px");
                // �\�����Ԃ���ԗD�悳����
                $clone.css("z-index", 99);

                $clone.css("table-layout", "fixed");
            }
        });
 
        // CSS�̃R�s�[
        function StyleCopy($copyTo, $copyFrom){
            $copyTo.css("width", 
                        $copyFrom.css("width"));
            $copyTo.css("height", 
                        $copyFrom.css("height"));
 
            $copyTo.css("padding-top", 
                        $copyFrom.css("padding-top"));
            $copyTo.css("padding-left", 
                        $copyFrom.css("padding-left"));
            $copyTo.css("padding-bottom", 
                        $copyFrom.css("padding-bottom"));
            $copyTo.css("padding-right", 
                        $copyFrom.css("padding-right"));
 
            $copyTo.css("background", 
                        $copyFrom.css("background"));
            $copyTo.css("background-color", 
                        $copyFrom.css("background-color"));
            $copyTo.css("vertical-align", 
                        $copyFrom.css("vertical-align"));
 
            $copyTo.css("border-top-width", 
                        $copyFrom.css("border-top-width"));
            $copyTo.css("border-top-color", 
                        $copyFrom.css("border-top-color"));
            $copyTo.css("border-top-style", 
                        $copyFrom.css("border-top-style"));
 
            $copyTo.css("border-left-width", 
                        $copyFrom.css("border-left-width"));
            $copyTo.css("border-left-color", 
                        $copyFrom.css("border-left-color"));
            $copyTo.css("border-left-style", 
                        $copyFrom.css("border-left-style"));
 
            $copyTo.css("border-right-width", 
                        $copyFrom.css("border-right-width"));
            $copyTo.css("border-right-color", 
                        $copyFrom.css("border-right-color"));
            $copyTo.css("border-right-style", 
                        $copyFrom.css("border-right-style"));
 
            $copyTo.css("border-bottom-width", 
                        $copyFrom.css("border-bottom-width"));
            $copyTo.css("border-bottom-color", 
                        $copyFrom.css("border-bottom-color"));
            $copyTo.css("border-bottom-style", 
                        $copyFrom.css("border-bottom-style"));
        }
    });
})(jQuery);
*/