//===================================================================
/**
 * [機能]	工程選択時に対応する
 * 			サブ工程のデータを取得する
 * [引数]	key			キー
 * [戻値]	結果		データ
 */
//===================================================================
function getSubProsess(obj, command){

    var idx = obj.selectedIndex;
    var value = obj.options[idx].value; // 値
    var text  = obj.options[idx].text;  // 表示テキスト

	var process = value;

	var formData = new FormData();
	formData.append("process", process);

	// コールバック
	var callbacks = {
		'successfinal'  : function(data){

			if (data.isSuccess == true){
				var src = "";
				src = src + '<option value=""></option>';

				for (var i=0 ; i<data.commandResponse.subProcess.length ; i++){
					src = src + '<option value="' + (i + 1) + '">' + data.commandResponse.subProcess[i].value + '</option>';
				}
				$('select[name=selectSubProcess]').html(src);
			}
		}
	};

	ajax_submit_command('rest/AJAXTEST/testAjax', command, formData, callbacks);
}
