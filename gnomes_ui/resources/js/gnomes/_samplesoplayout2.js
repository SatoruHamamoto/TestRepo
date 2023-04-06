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

function openAjax(id){

	$.ajax({
        type: "GET",
        async: false,
		cache : false,
        url: "/gnomes/ui/sopOperation/openSOPDetailInfo?id=" + id,
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: function (data) {
        	var domString= '';
        	domString += '<div class="sopDetailBox" id="mainBoxRight">';
			domString += '<div class="sopDetail" id="sopDetail" >';
			domString += '親作業手順キー：' + data.sop.parentOperatingProcedureKey + '<br />';
			domString += '作業指図ID：' + data.sop.productionInstructionID + '<br />';
			domString += '作業順序：' + data.sop.operationOrder + '<br />';
			domString += '作業手順キー：' + data.sop.operatingProcedureKey + '<br />';
			domString += '作業手順タイプ：' + data.sop.operatingProcedureType + '<br />';
			domString += '作業名：' + data.sop.operatingProcedureName + '<br />';
			domString += '作業属性：' + data.sop.operatingProcedureAttribute + '<br />';
			domString += '作業指示：' + data.sop.operatingInstruction + '<br />';
			domString += '備考：' + data.sop.remark + '<br />';
			domString += '作業ステータス：' + data.sop.operatingStatus + '<br />';
			domString += '<input name="SOP_KEY" value="' + data.sop.operatingProcedureKey + '" type="hidden" />';
			domString += '<input name="PROD_KEY" value="' + data.sop.productionInstructionID + '" type="hidden" />';

			var disabledConfirm = '';
			if (data.sop.operatingStatus == "5"){
				disabledConfirm = 'disabled="diasbled" style="color:gray;"';
			}

			domString += '<input type="button" id="confirm" value="確認(JAX-RS)" ' + disabledConfirm + ' onclick="sopConfirm(\'' + id + '\',5);" />';
			domString += '<input type="button" id="confirm" value="戻す(JAX-RS)" onclick="sopConfirm(\'' + id + '\',0);" />';
			//domString += '<input type="button" id="confirm" value="確認(FIDES)" onclick="document.main.COMMAND_NAME.value=\'Confirm\';GnomesSendBTN(\'確認\',\'確認しますか？\',\'確認\',\'Confirm\',\'BTNCMD_CONFIRM\',\'#main\');" />';
			domString += '<br /><input type="button" id="confirm" value="確認(FIDES)" onclick="document.main.COMMAND_NAME.value=\'ConfirmDirect\';SendBTN(\'ConfirmDirect\');" />';
			domString += '<input type="button" id="confirm" value="確認(ENGINE)" onclick="document.main.COMMAND_NAME.value=\'Confirm\';SendBTN(\'Confirm\');" />';

			domString += '</div>';
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

//===================================================================
/**
 * [機能]	上スクロール(1行)処理
 * [引数]	strId		親要素のID
 *	
 * [戻値]	なし
 */
//===================================================================
function upScroll(strId){
	var sh = $('#' + strId).scrollTop();
	var nowIndex = getIndexInScroll(strId, sh, 0);

	if (nowIndex == 0) {
		return;
	}

	var pos = getScrollPos(strId, nowIndex - 1);

	$('#' + strId).animate({
        scrollTop: pos
    },"fast", "swing");
}

//===================================================================
/**
 * [機能]	下スクロール(1行)処理
 * [引数]	strId		親要素のID
 *	
 * [戻値]	なし
 */
//===================================================================
function downScroll(strId){
	var bh = $('#' + strId).outerHeight();
	var sh = $('#' + strId).scrollTop();

	var nowIndex = getIndexInScroll(strId, sh, 1);

	// tr全ての高さ
	var h = 0;
	$.each($('#' + strId).children(), function() {
    	h = h + $(this).outerHeight();
	});

	if (bh >= h - sh) {
		return;
	}

//	pos = (nowIndex + 1) * th;
	var pos = getScrollPos(strId, nowIndex + 1);
	$('#' + strId).animate({
        scrollTop: pos
    },"fast", "swing");
}

//===================================================================
/**
 * [機能]	上スクロール(1ページ)処理
 * [引数]	strId		親要素のID
 *	
 * [戻値]	なし
 */
//===================================================================
function upScrollPage(strId){
	var bh = $('#' + strId).outerHeight();
	var sh = $('#' + strId).scrollTop();

	var upIndex = 0;
	var nowIndex = getIndexInScroll(strId, sh, 0);

	if (nowIndex == 0) {
		return;
	}
	if (nowIndex > 1) {
		var nowIndexPos = getScrollPos(strId, nowIndex);
		var limit = bh;		// - (nowIndexPos - sh);
		for (var i=nowIndex - 1; i>0; i--) {
			limit = limit - $('#' + strId).children().eq(i).outerHeight();

			if (limit == 0) {
				upIndex = i;
				break;
			}
			if (limit < 0) {
				upIndex = i + 1;
				break;
			}
		}
	}

	var pos = getScrollPos(strId, upIndex);
	$('#' + strId).animate({
        scrollTop: pos
    },"fast", "swing");


}

//===================================================================
/**
 * [機能]	下スクロール(1ページ)処理
 * [引数]	strId		親要素のID
 *
 * [戻値]	なし
 */
//===================================================================
function downScrollPage(strId){
	var bh = $('#' + strId).outerHeight();
	var sh = $('#' + strId).scrollTop();

	// tr全ての高さ
	var h = 0;
	$.each($('#' + strId).children(), function() {
    	h = h + $(this).outerHeight();
	});


	if (bh >= h - sh) {
		return;
	}

	var nowIndex = getIndexInScroll(strId, sh, 1);
	var nowIndexPos = getScrollPos(strId, nowIndex);
	

	
	var downIndex = 0;
	var h = 0;
	var limit = bh - (nowIndexPos - sh);
//	for (var i=0; i<$("#TABLE_DATA_SET_LIST_BODY tr").length; i++) {
	for (var i=0; i<$('#' + strId).children().length; i++) {
		if (i >= nowIndex) {
//			limit = limit - $("#TABLE_DATA_SET_LIST_BODY tr").eq(i).outerHeight();
			limit = limit - $('#' + strId).children().eq(i).outerHeight();
			if (limit <= 0) {
				downIndex = i;
				break;
			}
		}
		
	}

	var pos = getScrollPos(strId, downIndex);
	$('#' + strId).animate({
        scrollTop: pos
    },"fast", "swing");


}



//===================================================================
/**
 * [機能]	スクロール位置から先頭子要素のIndexを取得
 * [引数]	strId		親要素のID
 *			sh			縦スクロール位置
 *			dir			0:上スクロール, 1:下スクロール
 * [戻値]	結果		先頭子要素のIndex
 */
//===================================================================
function getIndexInScroll(strId, sh, dir) {

	var index = 0;
	var h = 0;
	for (var i=0; i<$('#' + strId).children().length; i++) {
		h = h + $('#' + strId).children().eq(i).outerHeight();
	
//	for (var i=0; i<$("#TABLE_DATA_SET_LIST_BODY tr").length; i++) {
//		h = h + $("#TABLE_DATA_SET_LIST_BODY tr").eq(i).outerHeight();

		// up 
		if (dir == 0) {
			if (sh <= h) {
				// (切り上げ)
				index = i + 1;
				break;
			}
		}
		// down
		if (dir == 1 && sh < h) {
			index = i;
			break;
		}

	}
	return index;
}

//===================================================================
/**
 * [機能]	先頭子要素Indexを先頭にするスクロール位置取得
 * [引数]	strId		親要素のID
 *			index		先頭子要素Index
 *
 * [戻値]	結果		スクロール縦位置
 */
//===================================================================
function getScrollPos(strId, index) {

	var pos = 0;
//	for (var i=0; i<$("#TABLE_DATA_SET_LIST_BODY tr").length; i++) {
	for (var i=0; i<$('#' + strId).children().length; i++) {
		if (i == index) {
			break;
		}
//		pos = pos + $("#TABLE_DATA_SET_LIST_BODY tr").eq(i).outerHeight();
		pos = pos + $('#' + strId).children().eq(i).outerHeight();
	}
	return pos;
}

//サイドバー開閉
function toggleSidebar(){
	$('#sideMenuClose').collapse('toggle');
	$('#sideMenu').collapse('toggle');
	changeSizeLeft();
	changeSizeRight();
}
//ヘッダー開閉
function toggleHeader(){
	//開閉処理（トグル）
	$('#headerText').collapse('toggle');
	$('#headerOnlyButton').collapse('toggle');
	changeSizeLeft();
	changeSizeRight();
}

//SOP系画面右側サイズ変更
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
	$('#mainBoxLeft').css({'cssText':'height: calc(100vh - ' + headerHeight + 'px) !important; width: 300px;'});
	$('#mainBoxLeftTable').css({'cssText':'height: calc(100vh - ' + sopAreaMargineHeight + 'px) !important;'});
	$('#TABLE_SOP_LIST tbody').css({'cssText':'height: calc(100vh - ' + (sopAreaMargineHeight + sopTableHeaderHeight + sopTableBorderHeight) + 'px) !important;'});
	$('.openButton').css({'cssText':'height: calc(100vh - ' + (headerHeight + marginLeft) + 'px) !important;'});

}

//SOP系画面左側サイズ変更
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



//追加 
//サイドバー開閉(右側のスライドバー)
function toggleRightSidebar(){
	$('#sideMenuClose').collapse('toggle');
	$('#sideMenu').collapse('toggle');
	changeLeftSize();
	changeRightSize();
}
//ヘッダー開閉
function toggleRightHeader(){
	//開閉処理（トグル）
	$('#headerText').collapse('toggle');
	$('#headerOnlyButton').collapse('toggle');
	changeLeftSize();
	changeRightSize();
}

//SOP系画面右側サイズ変更
function changeRightSize(){

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
	$('#mainBoxLeft').css({'cssText':'height: calc(100vh - ' + headerHeight + 'px) !important; width: 300px; float:right; margin-right: 5px;'});
	$('#mainBoxLeftTable').css({'cssText':'height: calc(100vh - ' + sopAreaMargineHeight + 'px) !important;'});
	$('#TABLE_SOP_LIST tbody').css({'cssText':'height: calc(100vh - ' + (sopAreaMargineHeight + sopTableHeaderHeight + sopTableBorderHeight) + 'px) !important;'});
	$('.openButton').css({'cssText':'height: calc(100vh - ' + (headerHeight + marginLeft) + 'px) !important;'});

}

//SOP系画面左側サイズ変更
function changeLeftSize(){
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


// 右側スライド
$(window).load(function() {
	//alert($("input[name='NMLTXT_STATUS_R_0']").val());
	$("#TABLE_SOP_LIST td").each(function(index) {
		if($("input[name='NMLTXT_STATUS_R_0']") == 5){
			$(this).html("<span class='glyphicon glyphicon-ok' style=\"font-family: 'Glyphicons Halflings' !important; color:#000000 !important; overflow:hidden;\" ></span>");
		}
	});
	changeLeftSize();
	changeRightSize();
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


/*(function($){

    $(function(){
        // スクロールが発生した時に処理を行う
        $(window).scroll(function () {

            var $table = $(".table");          // テーブルの要素を取得
            var $thead = $table.children("thead");  // thead取得
            var toffset = $table.offset();          // テーブルの位置情報取得
            // テーブル位置+テーブル縦幅 < スクロール位置 < テーブル位置
            if(toffset.top + $table.height()< $(window).scrollTop()
              || toffset.top > $(window).scrollTop()){
                // クローンテーブルが存在する場合は消す
                var $clone = $("#clonetable");
                if($clone.length > 0){
                    $clone.css("display", "none");
                }
            }
            // テーブル位置 < スクロール位置 < テーブル位置+テーブル縦幅
            else if(toffset.top < $(window).scrollTop()){
                // クローンテーブルが存在するか確認
                var $clone = $("#clonetable");
                if($clone.length == 0){
                    // 存在しない場合は、theadのクローンを作成
                    $clone= $thead.clone(true);
                    // idをclonetableとする
                    $clone.attr("id", "clonetable");
                    // body部に要素を追加
                    $clone.appendTo("body");
                    // theadのCSSをコピーする
                    StyleCopy($clone, $thead);
                    // theadの子要素(tr)分ループさせる
                    for(var i = 0; i < $thead.children("tr").length; i++)
                    {
                        // i番目のtrを取得
                        var $theadtr = $thead.children("tr").eq(i);
                        var $clonetr = $clone.children("tr").eq(i);
                        // trの子要素(th)分ループさせる
                        for (var j = 0; j < $theadtr.eq(i).children("th").length; j++){
                            // j番目のthを取得
                            var $theadth = $theadtr.eq(i).children("th").eq(j);
                            var $cloneth = $clonetr.eq(i).children("th").eq(j);
                            // thのCSSをコピーする
                            StyleCopy($cloneth, $theadth);
                        }
                    }
                }

                // コピーしたtheadの表示形式をtableに変更
                $clone.css("display", "table");
                // positionをブラウザに対し絶対値とする
                $clone.css("position", "fixed");
                $clone.css("border-collapse", "collapse");
                // positionの位置を設定(left = 元のテーブルのleftとする)
                $clone.css("left", toffset.left - $(window).scrollLeft());
                // positionの位置を設定(topをブラウザの一番上とする)
                $clone.css("top", "100px");
                // 表示順番を一番優先させる
                $clone.css("z-index", 99);
            }
        });

        // CSSのコピー
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
})(jQuery);*/
