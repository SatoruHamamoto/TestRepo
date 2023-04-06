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

//サイズの表示
function window_load() {
	if($(".collapse.in").attr("id") != undefined){
		recalcDisplaySubWindow();
	}
}


(function($){

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
})(jQuery);
