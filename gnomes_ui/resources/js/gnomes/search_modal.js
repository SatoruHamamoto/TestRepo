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

var searchMasterInfo;

/** 表示タイプ */
var searchDispType;

/** 最大表示可能件数 */
var searchMaxDispCount;

/** 現在のページ */
var searchNowPage;

/** １ページ表示件数 */
var searchOnePageDispCount;

/** 全件件数 */
var searchAllDataCount;

$(document).ready(function() {

	// 各画面で定義が必要 (jsonSearchMasterInfo)
	searchMasterInfo = JSON.parse(jsonSearchMasterInfo);
	// 各画面で定義が必要 (jsonSearchInfo)
	var json = $('input[name=jsonSearchInfo]').val();
	var searchSettingInfo = JSON.parse(json);
	
	// 保持
	// 表示タイプ
	searchDispType = searchSettingInfo.dispType;
	// 最大表示可能件数
	searchMaxDispCount = searchSettingInfo.maxDispCount;
	// 現在のページ
	searchNowPage = searchSettingInfo.nowPage;
	// １ページ表示件数
	searchOnePageDispCount = searchSettingInfo.onePageDispCount;
	// 全件件数
	searchAllDataCount = searchSettingInfo.allDataCount;
	
	makeShearchDialog(searchMasterInfo, searchSettingInfo);
	makeFilterSelectBox(searchMasterInfo);
});
	

//フィルタのセレクトボックス作成
function makeFilterSelectBox(mstInfo) {
	var mstConditions = mstInfo.mstConditions;

	for(var i = 0; i < mstConditions.length; i++){
		$('[name=filter]').append('<option value="' + i + '">' + mstConditions[i].text + '</option>');
	}
}

//フィルタのセレクトボックス押下時の処理
function addFilter() {
	var mstConditions = searchMasterInfo.mstConditions;

	var index = $("[name=filter]").val();

	if (index != "" ){

		var i = $("#filterTable [type=checkbox]").length;

		//カラム名
		var columnName = mstConditions[index]["columnName"];
		//表示カラム名
		var text = mstConditions[index]["text"];
		//タイプ
		var type = mstConditions[index]["type"];
		//選択肢
		var patterns = mstConditions[index]["patterns"];

		if($("[name=" + index + "_enable]").size() == 0){
			//列追加処理
			//チェックボックス列
			var firstTd = '<div class="col-xs-4">';
			firstTd += '<input type="checkbox" name="' + index + '_enable" class="enableCheckBox pull-left" value="1">';
			firstTd += '<input type="hidden" name="' + index + '_type" value="' + type + '">';
			firstTd += '<div class="pull-left"><label id="' + index + '_text" class="control-label text-left">' + text + '</label></div>';
			firstTd += '</div>';

			//検索条件列
			var secondTd = '<div class="col-xs-8">' + type + '</div>';
			//type=0:文字入力
			switch(type){
			case 0:
				secondTd = '<div class="col-xs-5">';
				secondTd += '<input type="text" class="form-control" name="'+ index +'_value" value="">';
				secondTd += '</div><div class="col-xs-3">';
				secondTd += '<select name="' + index +'_condition" class="form-control">';
				secondTd += '<option></option>';
				for(var listKey in patterns){
					secondTd += '<option value="' + listKey + '">' + patterns[listKey] + '</option>';
				}
				secondTd += '</select>';
				secondTd += '</div>';
				break;

			//type=1:文字 プルダウン
			case 1:
				secondTd = '<div class="col-xs-8">';
				secondTd += '<select name="' + index +'_value" class="form-control">';
				secondTd += '<option></option>';
				for(var listKey in patterns){
					secondTd += '<option value="' + listKey + '">' + patterns[listKey] + '</option>';
				}
				secondTd += '</select>';
				secondTd += '</div>';
				break;
			//type=2:数値 プルダウン
			case 2:
				secondTd = '<div class="col-xs-8">';
				secondTd += '<select name="' + index +'_value" class="form-control">'
				secondTd += '<option></option>';
				for(var listKey in patterns){
					secondTd += '<option value="' + listKey + '">' + patterns[listKey] + '</option>';
				}
				secondTd += '</select>';
				secondTd += '</div>';
				break;
			//type=3:数値入力
			case 3:
				secondTd = '<div class="col-xs-8">';
				secondTd += '<div class="row">';
				secondTd += '<div class="col-xs-5 pull-left">';
				secondTd += '<input type="text" class="form-control numeric" name="' + index + '_from" value="0">';
				secondTd += '</div>';
				secondTd += '<div class="pull-left">';
				secondTd += '&nbsp;-&nbsp;';
				secondTd += '</div>';
				secondTd += '<div class="col-xs-5 pull-left">';
				secondTd += '<input type="text" class="form-control numeric" name="' + index + '_to" value="">';
				secondTd += '</div>';
				secondTd += '</div></div>';
				break;
			//type=4:日付入力
			case 4:
				secondTd = '<div class="col-xs-8">';
				secondTd += '<div class="row">';
				secondTd += '<div class="col-xs-5" id="' + index + '_fromArea"><div class="input-group date" id="' + index + '_from" style="float:left;">';
				secondTd += '<input class="form-control" name="' + index + '_from" type="text" placeholder="" value="">';
				secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
				secondTd += '</div></div>';
				secondTd += '<div class="pull-left">';
				secondTd += '&nbsp;-&nbsp;';
				secondTd += '</div>';
				secondTd += '<div class="col-xs-5" id="' + index + '_toArea"><div class="input-group date" id="' + index + '_to" style="float:left;">';
				secondTd += '<input class="form-control" name="' + index + '_to" type="text" placeholder="" value="">';
				secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
				secondTd += '</div></div>';
				secondTd += '</div></div>';
				break;
			//type=5:日時入力(分まで）
			case 5:
				secondTd = '<div class="col-xs-8">';
				secondTd += '<div class="row">';
				secondTd += '<div class="col-xs-5" id="' + index + '_fromArea"><div class="input-group datetime" id="' + index + '_from" style="float:left;">';
				secondTd += '<input class="form-control" name="' + index + '_from" type="text" placeholder="" value="">';
				secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
				secondTd += '</div></div>';
				secondTd += '<div class="pull-left">';
				secondTd += '&nbsp;-&nbsp;';
				secondTd += '</div>';
				secondTd += '<div class="col-xs-5" id="' + index + '_toArea"><div class="input-group datetime" id="' + index + '_to" style="float:left;">';
				secondTd += '<input class="form-control" name="' + index + '_to" type="text" placeholder="" value="">';
				secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
				secondTd += '</div></div>';
				secondTd += '</div></div>';
				break;
			//type=6:日時入力(秒まで）
			case 6:
				secondTd = '<div class="col-xs-8">';
				secondTd += '<div class="row">';
				secondTd += '<div class="col-xs-5" id="' + index + '_fromArea"><div class="input-group datetime-ss" id="' + index + '_from" style="float:left;">';
				secondTd += '<input class="form-control" name="' + index + '_from" type="text" placeholder="" value="">';
				secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
				secondTd += '</div></div>';
				secondTd += '<div class="pull-left">';
				secondTd += '&nbsp;-&nbsp;';
				secondTd += '</div>';
				secondTd += '<div class="col-xs-5" id="' + index + '_toArea"><div class="input-group datetime-ss" id="' + index + '_to" style="float:left;">';
				secondTd += '<input class="form-control" name="' + index + '_to" type="text" placeholder="" value="">';
				secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
				secondTd += '</div></div>';
				secondTd += '</div></div>';
				break;
			default:
				break;
			}

			var tr = '<div id="' + index + '" class="tr row form-group">' + firstTd + secondTd + '</div>';
			$('#filterTable').append(tr);
			
			$('[name=' + index + '_enable]').val([1]);
		}
		setInput();
		setHeight();
	}
}

function setInput(){
	 $('.datetime-ss').datetimepicker({
	 format : 'YYYY/MM/DD HH:mm:ss',
	 showClose : true

		 });

	 $('.datetime').datetimepicker({
	 format : 'YYYY/MM/DD HH:mm',
	 showClose : true

		 });

	 $('.date').datetimepicker({
	 format : 'YYYY/MM/DD',
	 showClose : true

		 });

	$(".numeric").numeric();
};

$(function () {
	$('#searchModal').on('shown.bs.modal', function (event) {
		setHeight();
	});
});

function setHeight(){
	if($("#filterTable [type=checkbox]").length > 5){
		$('#filterArea').height("25vh");
	}
	if($("#sortTable [type=checkbox]").length > 10){
		$('#sortArea').height("30vh");
	}
};



// 検索ダイアログの作成
//
// mstInfo		:マスター情報
// searchInfo	:検索情報
//
function makeShearchDialog(mstInfo, searchInfo) {
	
	var sortColumnNum = 0;
	for(var i = 0; i < mstInfo["mstOrdering"].length; i++){
		if (mstInfo["mstOrdering"][i]["columnName"] != null && mstInfo["mstOrdering"][i]["columnName"].length > 0) {
			sortColumnNum++;
		}
	}
	
	$("#filterTable").html('<div id="filterTable"></div>');
	$("#sortTable").html('<table id="sortTable"  class="table table-striped table-hover" ></table>');
	//フィルタ部分
	for(var i = 0; i < searchInfo.conditionInfos.length; i++){

		var index = searchInfo.conditionInfos[i].index;

		//カラム名
		var columnName = mstInfo["mstConditions"][index]["columnName"];
		//表示カラム名
		var text = mstInfo["mstConditions"][index]["text"];
		//タイプ
		var type = mstInfo["mstConditions"][index]["type"];
		//選択肢
		var patterns = mstInfo["mstConditions"][index]["patterns"];

		//列追加処理
		//チェックボックス列
		var firstTd = '<div class="col-xs-4">';
		firstTd += '<input type="checkbox" name="' + index + '_enable" class="enableCheckBox pull-left" value="1">';
		firstTd += '<input type="hidden" name="' + index + '_type" value="' + type + '">';
		firstTd += '<div class="pull-left"><label id="' + index + '_text" class="control-label text-left">' + text + '</label></div>';
		firstTd += '</div>';

		//検索条件列
		var secondTd = '<div class="col-xs-8">' + type + '</div>';
		//type=0:文字入力
		switch(type){
		case 0:
			secondTd = '<div class="col-xs-5">';
			secondTd += '<input type="text" class="form-control" name="'+ index +'_value" value="">';
			secondTd += '</div><div class="col-xs-3">';
			secondTd += '<select name="' + index +'_condition" class="form-control">';
			secondTd += '<option></option>';
			for(var listKey in patterns){
				secondTd += '<option value="' + listKey + '">' + patterns[listKey] + '</option>';
			}
			secondTd += '</select>';
			secondTd += '</div>';
			break;

		//type=1:文字 プルダウン
		case 1:
			secondTd = '<div class="col-xs-8">';
			secondTd += '<select name="' + index +'_value" class="form-control">';
			secondTd += '<option></option>';
			for(var listKey in patterns){
				secondTd += '<option value="' + listKey + '">' + patterns[listKey] + '</option>';
			}
			secondTd += '</select>';
			secondTd += '</div>';
			break;
		//type=2:数値 プルダウン
		case 2:
			secondTd = '<div class="col-xs-8">';
			secondTd += '<select name="' + index +'_value" class="form-control">'
			secondTd += '<option></option>';
			for(var listKey in patterns){
				secondTd += '<option value="' + listKey + '">' + patterns[listKey] + '</option>';
			}
			secondTd += '</select>';
			secondTd += '</div>';
			break;
		//type=3:数値入力
		case 3:
			secondTd = '<div class="col-xs-8">';
			secondTd += '<div class="row">';
			secondTd += '<div class="col-xs-5 pull-left">';
			secondTd += '<input type="text" class="form-control numeric" name="' + index + '_from" value="0">';
			secondTd += '</div>';
			secondTd += '<div class="pull-left">';
			secondTd += '&nbsp;-&nbsp;';
			secondTd += '</div>';
			secondTd += '<div class="col-xs-5 pull-left">';
			secondTd += '<input type="text" class="form-control numeric" name="' + index + '_to" value="">';
			secondTd += '</div>';
			secondTd += '<div class="col-xs-5 pull-left">';
			break;
		//type=4:日付入力
		case 4:
			secondTd = '<div class="col-xs-8">';
			secondTd += '<div class="row">';
			secondTd += '<div class="col-xs-5" id="' + index + '_fromArea"><div class="input-group date" id="' + index + '_from" style="float:left;">';
			secondTd += '<input class="form-control" name="' + index + '_from" type="text" placeholder="" value="">';
			secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
			secondTd += '</div></div>';
			secondTd += '<div class="pull-left">';
			secondTd += '&nbsp;-&nbsp;';
			secondTd += '</div>';
			secondTd += '<div class="col-xs-5" id="' + index + '_toArea"><div class="input-group date" id="' + index + '_to" style="float:left;">';
			secondTd += '<input class="form-control" name="' + index + '_to" type="text" placeholder="" value="">';
			secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
			secondTd += '</div></div>';
			secondTd += '</div></div>';
			break;
		//type=5:日付入力
		case 5:
			secondTd = '<div class="col-xs-8">';
			secondTd += '<div class="row">';
			secondTd += '<div class="col-xs-5" id="' + index + '_fromArea"><div class="input-group datetime" id="' + index + '_from" style="float:left;">';
			secondTd += '<input class="form-control" name="' + index + '_from" type="text" placeholder="" value="">';
			secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
			secondTd += '</div></div>';
			secondTd += '<div class="pull-left">';
			secondTd += '&nbsp;-&nbsp;';
			secondTd += '</div>';
			secondTd += '<div class="col-xs-5" id="' + index + '_toArea"><div class="input-group datetime" id="' + index + '_to" style="float:left;">';
			secondTd += '<input class="form-control" name="' + index + '_to" type="text" placeholder="" value="">';
			secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
			secondTd += '</div></div>';
			secondTd += '</div></div>';
			break;
		//type=6:日付入力
		case 6:
			secondTd = '<div class="col-xs-8">';
			secondTd += '<div class="row">';
			secondTd += '<div class="col-xs-5" id="' + index + '_fromArea"><div class="input-group datetime-ss" id="' + index + '_from" style="float:left;">';
			secondTd += '<input class="form-control" name="' + index + '_from" type="text" placeholder="" value="">';
			secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
			secondTd += '</div></div>';
			secondTd += '<div class="pull-left">';
			secondTd += '&nbsp;-&nbsp;';
			secondTd += '</div>';
			secondTd += '<div class="col-xs-5" id="' + index + '_toArea"><div class="input-group datetime-ss" id="' + index + '_to" style="float:left;">';
			secondTd += '<input class="form-control" name="' + index + '_to" type="text" placeholder="" value="">';
			secondTd += '<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>';
			secondTd += '</div></div>';
			secondTd += '</div></div>';
			break;
		default:
			break;
		}

		var tr = '<div id="' + index + '" class="tr row form-group">' + firstTd + secondTd + '</div>';
		$('#filterTable').append(tr);

		//値の入力
		//チェック有無
		if(searchInfo.conditionInfos[i].enable){
			$('[name=' + index + '_enable]').val([1]);
		} else {
			$('[name=' + index + '_enable]').val([]);
		}
		switch(type){
		//type=0:文字入力
		case 0:
			$('[name=' + index + '_condition]').val(searchInfo.conditionInfos[i].patternKeys[0]);
			$('[name=' + index + '_value]').val(searchInfo.conditionInfos[i].parameters[0]);
			break;
		//type=1:文字 プルダウン
		case 1:
			$('[name=' + index + '_value]').val(searchInfo.conditionInfos[i].patternKeys[0]);
			break;
		//type=2:数値 プルダウン
		case 2:
			$('[name=' + index + '_value]').val(searchInfo.conditionInfos[i].patternKeys[0]);
			break;
		//type=3:数値入力
		case 3:
			$('[name=' + index + '_from]').val(searchInfo.conditionInfos[i].parameters[0]);
			$('[name=' + index + '_to]').val(searchInfo.conditionInfos[i].parameters[1]);
			break;
		//type=4:日付入力
		case 4:
			$('[name=' + index + '_pattern]:eq('+ searchInfo.conditionInfos[i].patternKeys[0] +')').prop('checked', true);
			$('[name=' + index + '_from]').val(searchInfo.conditionInfos[i].parameters[0]);
			$('[name=' + index + '_to]').val(searchInfo.conditionInfos[i].parameters[1]);
			break;
		//type=5:日時入力
		case 5:
			$('[name=' + index + '_pattern]:eq('+ searchInfo.conditionInfos[i].patternKeys[0] +')').prop('checked', true);
			$('[name=' + index + '_from]').val(searchInfo.conditionInfos[i].parameters[0]);
			$('[name=' + index + '_to]').val(searchInfo.conditionInfos[i].parameters[1]);
			break;
		//type=6:日時入力
		case 6:
			$('[name=' + index + '_pattern]:eq('+ searchInfo.conditionInfos[i].patternKeys[0] +')').prop('checked', true);
			$('[name=' + index + '_from]').val(searchInfo.conditionInfos[i].parameters[0]);
			$('[name=' + index + '_to]').val(searchInfo.conditionInfos[i].parameters[1]);
			break;
		default:
			break;
		}
	}

	//ソート部分
	for(var i = 0; i < searchInfo.orderingInfos.length; i++){

		var index = searchInfo.orderingInfos[i].index;

		//カラム名
		var columnName = mstInfo["mstOrdering"][index]["columnName"];
		//表示カラム名
		var text = mstInfo["mstOrdering"][index]["text"];
		//リソースID
		var type = mstInfo["mstOrdering"][index]["resourceId"];

		//列追加処理
		//列宣言
		var buttonTd ='<td></td>';
		var checkboxTd ='<td></td>';
		var colnameTd ='<td></td>';
		var orderTd ='<td></td>';
		var ascTd ='<td></td>';

		//表示順列
		buttonTd = '<td class="buttonTd"><span class="glyphicon glyphicon-circle-arrow-up orderFont" onclick="moveUp(' + "'" + index + "'" + ');"></span></td>';
		buttonTd +='<td class="buttonTd"><span class="glyphicon glyphicon-circle-arrow-down orderFont" onclick="moveDown(' + "'" + index + "'" + ');"></span></td>';

		//チェックボックス列
		checkboxTd = '<td class="buttonTd">';
		checkboxTd += '<input type="checkbox" name="' +  index + '_disp" value="1" style="margin: 0px 8px 0px 8px;"></td>';

		//カラム名列
		colnameTd = '<td style="border: 1px #cccccc solid;">';
		colnameTd += '<span style="margin-left:10px;" >' + text + '</span></td>';

		//ソート順序
		orderTd = '<td style="border: 1px #cccccc solid;">';
		if(columnName != null && columnName.length > 0){
			orderTd += '<select class="form-control" name="' + index + '_orderNum">';
			orderTd += '<option></option>';
			//選択肢（カラム数分）
			for(var j = 1; j <= sortColumnNum; j++){
				orderTd += '<option value="' + (j) + '">' + j + '</option>';
			}
			orderTd += '</select>';
		}
		orderTd += '</td>';

		//ソート方式
		ascTd = '<td style="border: 1px #cccccc solid;">';
		if(columnName != null && columnName.length > 0){
			ascTd += '<input type="radio" name="' + index  + '_orderType" value="0" style="margin-bottom:5px; margin-left:10px; margin-right:10px;">';
			ascTd += '<span style=""><span class="glyphicon glyphicon-sort-by-alphabet"></span></span>';
			ascTd += '<input type="radio" name="' + index  + '_orderType" value="1" style="margin-bottom:5px; margin-left:10px; margin-right:10px;">';
			ascTd += '<span style=" margin-right:10px;""><span class="glyphicon glyphicon-sort-by-alphabet-alt"></span></span>';
		}
		ascTd += '</td>';

		var tr = '<tr id="' + index + '">' + buttonTd + checkboxTd + colnameTd + orderTd + ascTd + '</tr>';
		$('#sortTable').append(tr);

		//}

		//値の入力
		//チェック有無
		if(!searchInfo.orderingInfos[i].hiddenTable){
			$('[name=' + index + '_disp]').val([1]);
		} else {
			$('[name=' + index + '_disp]').val([]);
		}
		//ソート順序
		$('[name=' + index + '_orderNum]').val(searchInfo.orderingInfos[i].orderNum);
		//ソート方式 0:降順 1:昇順
		$('[name=' + index + '_orderType]:eq('+ searchInfo.orderingInfos[i].orderType +')').prop('checked', true);
	}
	setInput();
	setHeight();
}


//searchSettingを表示済みのHTMLから作成
function makeSearchJSON(){
	var searchSetting = {};

	//filter部
	var conditionInfos = [];
	var i = 0;
	$("#filterTable .tr").each(function(){
		//各行のID取得
		var index = $(this).attr("id");

		if($("[name=" + index + "_enable]").prop('checked')){
			//typeを取得・分岐
			var type = $("[name=" + index + "_type]").val();
			//1行分のJSON
			var columnJson = {};

			//JSONを作成
			columnJson["index"] = Number(index);

			switch(type){
				//type=0:文字入力
				case "0":
					columnJson["patternKeys"] = [$("[name=" + index + "_condition]").val()];
					columnJson["parameters"] = [$("[name=" + index + "_value]").val()];
					break;
				//type=1:文字 プルダウン
				case "1":
					columnJson["patternKeys"] = [$("[name=" + index + "_value]").val()];
					columnJson["parameters"] = [];
					break;
				//type=2:数値 プルダウン
				case "2":
					columnJson["patternKeys"] = [$("[name=" + index + "_value]").val()];
					columnJson["parameters"] = [];
					break;
				//type=3:数値入力
				case "3":
					columnJson["patternKeys"] = [];
					columnJson["parameters"] = [$("[name=" + index + "_from]").val(), $("[name=" + index + "_to]").val()];
					break;
				//type=4:日付入力
				case "4":
					columnJson["patternKeys"] =  [];
					columnJson["parameters"] = [$("[name=" + index + "_from]").val(), $("[name=" + index + "_to]").val()];
					break;
				//type=5:日時入力
				case "5":
					columnJson["patternKeys"] =  [];
					columnJson["parameters"] = [$("[name=" + index + "_from]").val(), $("[name=" + index + "_to]").val()];
					break;
				//type=6:日時入力
				case "6":
					columnJson["patternKeys"] =  [];
					columnJson["parameters"] = [$("[name=" + index + "_from]").val(), $("[name=" + index + "_to]").val()];
					break;
				default:
					break;
			}
			columnJson["enable"] = $("[name=" + index + "_enable]").prop('checked');

			//1行分のJSONを配列に追加
			conditionInfos.push(columnJson);
		}
		i++;
	});

	//sort部
	var orderingInfos = [];

	i = 0;
	$("#sortTable tr").each(function(){
		var index = $(this).attr("id");
		//1行分のJSON
		var sortJson = {};

		//JSONを作成
		sortJson["index"] = Number(index);

		if($("[name=" + index + "_orderNum]").val() != "" && $("[name=" + index + "_orderNum]").val() != null){
			sortJson["orderNum"] = Number($("[name=" + index + "_orderNum]").val());
		}

		if($("[name=" + index + "_orderType]:checked").val() != null){
			sortJson["orderType"] = Number($("[name=" + index + "_orderType]:checked").val());
		} else {
			sortJson["orderType"] = -1;
		}
		sortJson["hiddenTable"] = !$("[name=" + index + "_disp]").prop('checked');

		orderingInfos.push(sortJson);
		i++;
	});
	//配列をJSONに追加
	searchSetting["conditionInfos"] = conditionInfos;
	searchSetting["orderingInfos"] = orderingInfos;
	
	// 復元
	// 表示タイプ
	searchSetting["dispType"] = searchDispType;
	// 最大表示可能件数
	searchSetting["maxDispCount"] = searchMaxDispCount;
	// 現在のページ
	searchSetting["nowPage"] = searchNowPage;
	// １ページ表示件数
	searchSetting["onePageDispCount"] = searchOnePageDispCount;
	// 全件件数
	searchSetting["allDataCount"] = searchAllDataCount;

	return searchSetting;
};


function moveUp(index){
	if($('#sortTable #' + index).prev().html() != null){
		var changeIndex = $('#sortTable #' + index).prev().prop('id');
		moveTr(index, changeIndex);
	}
}


function moveDown(index){
	if($('#sortTable #' + index).next().html() != null){
		var changeIndex = $('#sortTable #' + index).next().prop('id');
		moveTr(index, changeIndex);
	}
}


function moveTr(index, changeIndex){

	//行の取得
	var tr1 =  $('#sortTable #' + index);
	var tr2 =  $('#sortTable #' + changeIndex);

	//行の値の取得
	var tr1HTML = $('#sortTable #' + index).prop('outerHTML');
	var tr2HTML = $('#sortTable #' + changeIndex).prop('outerHTML');

	var tr1Check =  $('#sortTable [name=' + index + '_disp]').prop('checked');
	var tr2Check =  $('#sortTable [name=' + changeIndex + '_disp]').prop('checked');

	var tr1Select =  $('#sortTable [name=' + index + '_orderNum]').val();
	var tr2Select =  $('#sortTable [name=' + changeIndex + '_orderNum]').val();

	var tr1Type =  $('#sortTable [name=' + index + '_orderType]:checked').val();
	var tr2Type =  $('#sortTable [name=' + changeIndex + '_orderType]:checked').val();

	//h行の入れ替え
	tr1.prop('outerHTML', tr2HTML);
	tr2.prop('outerHTML', tr1HTML);

	$('#sortTable [name=' + index + '_disp]').prop('checked', tr1Check);
	$('#sortTable [name=' + changeIndex + '_disp]').prop('checked',tr2Check);

	$('#sortTable [name=' + index + '_orderNum]').val(tr1Select);
	$('#sortTable [name=' + changeIndex + '_orderNum]').val(tr2Select);

	$('#sortTable [name=' + index + '_orderType]').val([tr1Type]);
	$('#sortTable [name=' + changeIndex + '_orderType]').val([tr2Type]);

}



function setDefaultSearchSetting() {

	// 各画面で定義が必要 (jsonSearchMasterInfo)
	//var searchMasterInfo = JSON.parse(jsonSearchMasterInfo);
	
	makeShearchDialog(searchMasterInfo, searchMasterInfo.defaultSearchSetting);
}


(function($) {

  $.searchOpenDialog = function() {

  var deferred = $.Deferred();

    var $element = $("#searchModal");
    $element
        .data("resolve", false) // resolve するかどうかのフラグ
        //「検索」ボタンのクリックイベント
        .on("click", ".btn-primary", function() {

			//searchSettingを表示済みのHTMLから作成
			var jsonObject = makeSearchJSON();
			var jsonString = JSON.stringify(jsonObject);

			//hiddenパラメータ書き換え
        	$('input[name=jsonSearchInfo]').val(jsonString);

			// モーダル閉じない
            deferred.resolve();

            // resolve フラグを立てて、モーダルを閉じる
//            $element
//                .data("resolve", true)
//                .modal("hide");
        })
        // モーダルの非表示イベント
        .one("hidden.bs.modal", function() {
            $(this).off("click", ".btn-primary");

            deferred.reject();

            // resolveフラグをみて resolve か reject
//            if($(this).data("resolve")) {
//                deferred.resolve();
//            } else {
//                deferred.reject();
//            }
        })
        .modal({ show: true });

    return deferred.promise();
	};
})(jQuery);




