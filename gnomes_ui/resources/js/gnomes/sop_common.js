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
	var th = $("#TABLE_SOP_LIST").find("th");
	for( var i=0,l=th.length;i<l;i++ ){
		objHeader[i] = th[i].textContent;
	}
  var $cur_td = $(this)[0];
  var $cur_tr = $(this).parent()[0];
}

function statusIconSet(){

	var count = 0;
	$("#TABLE_SOP_LIST").find("input[name^=NMLTXT_OPERATING_STATUS]").each( function() {
		//selvalue.push($(this).val());
		var objName = $(this).attr("name");
		//if(objName.match(/NMLTXT_OPERATING_STATUS/i)){
			//if($(this).attr("name")=="NMLTXT_PRODUCTION_STATUS"
			//console.log($(this).attr("name") + " = " + $(this).val());
			var iconfile = "success";
			var status = $(this).val();
			switch(status){
				case '5':
					$(this).replaceWith("<img src='images/icons/" + iconfile + ".png'></img>");
					break;
				default:
					$(this).replaceWith();
					break;
			}


		//}
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
            alert("初期設定情報の取得に失敗しました。<br />[Web Error]");
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
            alert("初期設定情報の取得に失敗しました。<br />[Web Error]");
        }
    });

}

function BackSOPOpen(){

	var id = $("#BackSOPKey").val();
	if(id != null && id != "" ){
		openAjax(id);
	}
}

function sopOpen(btnObj){
	var id;
	var parentTr = $(btnObj).parents("tr");
	var selIndex = parentTr.index();
	var idObj = parentTr.eq(0).find("input[name='NMLTXT_OPERATING_PROCEDURE_KEY_R_" + selIndex + "']");
	id = idObj.val();

	openAjax(id);

}


function sopConfirm(id, status){
	$.ajax({
        type: "GET",
        async: false,
		cache : false,
        url: "/gnomes/ui/sopOperation/sopConfirm?id=" + id + "&status=" + status,
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: function (data) {
        	var domString= '';
        	domString += '<div class="sopDetailBox" id="mainBoxRight">';
			domString += '<div class="sopDetail" id="sopDetail" >';
			domString += '結果：' + data.result + '<br />';
			domString += id + '</div>';
			domString += '</div>';
			$('#mainBoxRight').replaceWith(domString);
		}
		,
        error: function (e) {
        	var domString="";
            alert("初期設定情報の取得に失敗しました。<br />[Web Error]");
            $('#sopDetail').replaceWith(domString);
        }
    });

}

//上下スクロール
function upScroll(str){
	var obj = document.getElementById(str);
	obj.scrollTop = obj.scrollTop - 50;

}
function downScroll(str){
	var obj = document.getElementById(str);
	obj.scrollTop = obj.scrollTop + 50;

}

//サイドバー開閉
function toggleSidebar(){
	$('#sideMenuClose').collapse('toggle');
	$('#sideMenu').collapse('toggle');
	//changeSizeLeft、changeSizeRightの中身は各SOP画面ごとのJSに記載
	changeSizeLeft();
	changeSizeRight();
}
//ヘッダー開閉
function toggleHeader(){
	//開閉処理（トグル）
	$('#headerText').collapse('toggle');
	$('#headerOnlyButton').collapse('toggle');
	//changeSizeLeft、changeSizeRightの中身は各SOP画面ごとのJSに記載
	changeSizeLeft();
	changeSizeRight();
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

$(window).load(function() {
	//alert($("input[name='NMLTXT_STATUS_R_0']").val());
	$("#TABLE_SOP_LIST td").each(function(index) {
		if($("input[name='NMLTXT_STATUS_R_0']") == 5){
			$(this).html("<span class='glyphicon glyphicon-ok' style=\"font-family: 'Glyphicons Halflings' !important; color:#000000 !important; overflow:hidden;\" ></span>");
		}
	});
	//changeSizeLeft、changeSizeRightの中身は各SOP画面ごとのJSに記載
	changeSizeLeft();
	changeSizeRight();
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

//ウィンドウサイズ変更時に更新
window.onresize = window_load;

//サイズの表示;
function window_load() {
	if($(".collapse.in").attr("id") != undefined){
		recalcDisplaySubWindow();
	}
}
