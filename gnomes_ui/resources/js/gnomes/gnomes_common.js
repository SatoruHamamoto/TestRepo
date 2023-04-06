//===================================================================
/**
 * ボタン送信処理 ＧＮＯＭＥＳ事前チェック版
 * サーブレットに渡すHIDDEN項目の値を押されたボタンの名前にする
 * @param obj valueにセットする値
 */
//===================================================================

function GnomesSendBTN(title,message,okBtnLabel,commandName,objName,idName)
{

	MakeModalAlert(title,message,okBtnLabel,commandName,objName,idName);

}

function MakeModalAlert(title,message,okBtnLabel,commandName,objName,idName)
{
	// モーダルダイアログのヘッダとボディの作成
	var str = MakeModalHeaderBody(title,message);

	str += "<div class=\"modal-footer\">";

	if (commandName != null && commandName != '' ) {
		// 閉じる
		str += "<button type=\"button\" id=\"myAnyClose\" class=\"btn btn-default\" data-dismiss=\"modal\">" + $('#msgBoxBtnClose').text() + "</button>";
		str += "<button type=\"button\" class=\"btn btn-primary\"";

		str += "onclick=\"$('#myAnyModal').modal('hide');$('#myLoadingModal').modal();document.main.command.value='" + commandName + "';document.main.submit();\"";
	} else {
		str += "<button type=\"button\" class=\"btn btn-primary\"";
		str += "onclick=\"$('#myAnyModal').modal('hide');\"";
	}
	str += ">" + okBtnLabel + "</button>";
	str += "</div>";
	str += "</div>";
	str += "</div>";
	$('#myAnyModal').html(str);
	$('#myAnyModal').on('shown.bs.modal', function () {
		if (commandName != null && commandName != '' ) {
   			 $('#myAnyClose').focus();
   		} else {
   			 $('.btn','#myAnyModal').focus();
   		}
	}).modal();
}

//===================================================================
/**
 * [機能]	モーダルダイアログのヘッダとボディの作成
 * [引数]	title		タイトル文言
 *			message		ダイアログメッセージ
 * [戻値]	結果		html
 */
//===================================================================
function MakeModalHeaderBody(title,message)
{
	var format = $('[name=dateFormat]').val() + ":ss";
	var dateFormat = comDateFormat(new Date(), format);

	var str = "<div class=\"modal-dialog\"> <div class=\"modal-content\"> <div class=\"modal-header\">";
	str += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><span ariea-hidden=\"true\"></span></button>";
	str += "<h4 class=\"modal-title\" id=\"modal-label\">" + title + "</h4></div>";
	str += "<div class=\"modal-body\">";
	str += "<table>";
	str += "<tr>";

	// タイトルからアイコンを設定
	// 情報
	if (title==$('#msgBoxTitleInfo').text()) {
		str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/mes.png\"/></td>";
	// 警告
	} else if (title==$('#msgBoxTitleWarning').text()) {
		str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/minor-process-alert.png\"/></td>";
	// 確認
	} else if (title==$('#msgBoxTitleConfirm').text()) {
		str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/kaku.png\"/></td>";
	// メッセージ
	} else if (title==$('#msgBoxTitleMessage').text()) {
		str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/mes.png\"/></td>";
	} else {
		str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/batu.png\"/></td>";
	}
	str += "<td style=\"font-size: 8pt; height:20px;\">" + dateFormat + "</td>";
	str += "</tr>";
	str += "<tr>";
	str += "<td>" + message + "</td>";
	str += "</tr>";
	str += "</table>";
	str += "</div>";

	return str;
}

//===================================================================
/**
 * [機能]	モーダルダイアログのガイダンス部の作成
 * [引数]	guindanceMessage		ガイダンスメッセージ
 *			linkURL		URL
 *			linkName	リンク名
 * [戻値]	結果		html
 */
//===================================================================
function MakeModalGuidance(guidanceMessage, linkURL, linkName)
{
	var str = "<div class=\"modal-guidance\">";
	str += "<div style=\"word-break: break-all;\">" + guidanceMessage + "</div>";
	str += "<a href=\"" + linkURL + "\" target=\"_blank\" style=\"word-break: break-all;\">"+ linkName + "</a><br>";
	str += "</div>";

	return str;
}

function MakeModalMessage(title,message,okBtnLabel,okCommand,onclick,linkInfo)
{
	if(okCommand == undefined){
		okCommand = null;
	}

	// モーダルダイアログのヘッダとボディの作成
	var str = MakeModalHeaderBody(title,message);

	if (linkInfo && linkInfo[0] != null && linkInfo[0] != '' ) {
		// モーダルダイアログのガイダンス部の作成
		str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
	}

	str += "<div class=\"modal-footer\">";
	if (okCommand != null && okCommand != '' ) {
		str += "<button type=\"button\" class=\"btn btn-primary\"";
		str += "onclick=\"$('#myModal').modal('hide');$('#myLoadingModal').modal();document.main.command.value='" + okCommand + "';document.main.submit();\"";
		// 閉じる
		str += ">" + $('#msgBoxBtnClose').text() + "</button>";
	} else if(onclick != null && onclick != ''){
		// 閉じる
		str += "<button type=\"button\" id=\"myAnyClose\" class=\"btn btn-default\" data-dismiss=\"modal\">" + $('#msgBoxBtnClose').text() + "</button>";
		str += "<button type=\"button\" class=\"btn btn-primary\"";
		str += "onclick=\"$('#myModal').modal('hide');" + onclick + "\"";
		str += ">" + okBtnLabel + "</button>";
	} else {
		str += "<button type=\"button\" id=\"myAnyClose\" class=\"btn btn-primary\" data-dismiss=\"modal\">" + okBtnLabel + "</button>";
	}
	str += "</div>";
	str += "</div>";
	str += "</div>";
	$('#myModal').html(str);
	$('#myModal').on('shown.bs.modal', function () {
   		 $('#myAnyClose').focus();
	}).modal();

}

//メッセージ詳細Modal
function MakeMessageModal(time,id,message,name,place,comment){
	var str = "<div class=\"modal-dialog\" role=\"document\"> <div class=\"modal-content\">";
	str += "<div class=\"modal-body\"><div class=\"row\"><div class=\"col-xs-1\">";
	str += "<img alt=\"button\" src=\"./images/gnomes/icons/dialog-success.png\">";
	str += "</div><div class=\"col-xs-11\">";
	str += "<div>" + time + "<br />";
	str += "<span style=\"font-size: 24px;\">" + message + "</span>";
	str += "</div></div></div>";
	str += "<fieldset id=\"detailField\" onclick=\"toggleField('#detailField' ,'#detailArea') \">";
	str += "<h5 style=\"border-top: 1px solid #DDDDDD; margin: 10px 0 0; text-align: left; padding: 0; height: 10px;\">";
	str += "<span style=\"position: relative; top: -9px; padding-right: 3px; background: white;\">";
	str += "<span class=\"glyphicon glyphicon-menu-down\"></span>" + document.getElementById("DETAIL").value +"</span>";
	str += "</h5></fieldset>";
	str += "<div id=\"detailArea\" class=\"row\" style=\"padding-top: 5px;\"><div class=\"col-xs-1\"></div><div class=\"col-xs-11\">";
	str += "</div><div class=\"col-xs-11\">";
	str += "<div>" + document.getElementById("DETAIL").value + "</div>";
	str += "<div style=\"padding-left: 10px\">" + comment + "</div>";
	str += "</div></div></div>";
	str += "<div class=\"modal-footer\">";
	str += "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">" + document.getElementById("OK").value + "</button>";
	str += "</div></div></div>";

	$('#messageModal').html(str);
	$('#messageModal').on('shown.bs.modal', function () {
		 $('#myAnyClose').focus();
	}).modal();
}

// メッセージボタンモード
const MesBtnMode_AbortRetryIgnore = '0';	// [中止]、[再試行]、および [無視] の各ボタン
const MesBtnMode_OK = '1';				// [OK] ボタン
const MesBtnMode_OKCancel = '2';			// [OK] ボタンと [キャンセル] ボタン
const MesBtnMode_RetryCancel = '3';		// [再試行] ボタンと [キャンセル] ボタン
const MesBtnMode_YesNo = '4';				// [はい] ボタンと [いいえ] ボタン
const MesBtnMode_YesNoCancel = '5';		// [はい]、[いいえ]、および [キャンセル] の各ボタン

// メッセージデフォルトボタンモード
const MesDefaultBtn_Button1 = '0';		// 最初のボタン
const MesDefaultBtn_Button2 = '1';		// 2 番目のボタン
const MesDefaultBtn_Button3 = '2';		// 3 番目のボタン


//メッセージ情報表示 基本
function makeMessageModalBase( title, iconName, occurDate, occrUserName, occrHost, message, messageDetail, msgBtnMode, defaultBtn, commands, linkInfo,isOpenDetail) {

	if (isOpenDetail == undefined) {
		isOpenDetail = true;
	}

	var str = "<div class=\"modal-dialog\"> <div class=\"modal-content\"> <div class=\"modal-header\">";
	str += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><span ariea-hidden=\"true\"></span></button>";
	str += "<h4 class=\"modal-title\" id=\"modal-label\">" + title + "</h4></div>";
	str += "<div class=\"modal-body\">";
	str += "<table>";
	str += "<tr>";
	str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/" + iconName + "\"/></td>";
	str += "<td style=\"font-size: 8pt; height:20px;\">" + occurDate + "</td>";
	str += "</tr>";
	str += "<tr>";
	str += "<td style=\"word-break: break-all;\">" + message + "</td>";
	str += "</tr>";
	str += "</table>";

	// 詳細
	str += "<fieldset id=\"detailField\" onclick=\"toggleField('#detailField' ,'#detailArea') \">";
	str += "<h5 style=\"border-top: 1px solid #DDDDDD; margin: 10px 0 0; text-align: left; padding: 0; height: 10px;\">";
	str += "<span style=\"position: relative; top: -9px; padding-right: 3px; background: white;\">";
	if (isOpenDetail == true) {
		str += "<span class=\"glyphicon glyphicon-menu-down\"></span>詳細</span>";
	} else {
		str += "<span class=\"glyphicon glyphicon-menu-right\"></span>詳細</span>";
	}
	str += "</h5></fieldset>";

	if (isOpenDetail == true) {
		str += "<div id=\"detailArea\" class=\"row\" style=\"padding-top: 5px;\">";
	} else {
		str += "<div id=\"detailArea\" class=\"row\" style=\"padding-top: 5px; display:none;\">";
	}

	str += "<div class=\"col-xs-1\"></div>";
	str += "<div class=\"col-xs-11\">";
	// 発生者
	str += "<div style=\"word-break: break-all;\">" + $('#msgBoxLabelOccrUserName').text() + occrUserName + "</div>";
	// 発生場所
	str += "<div style=\"word-break: break-all;\">" + $('#msgBoxLabelOccrHost').text() + occrHost + "</div>";

	// メッセージ詳細
	if (messageDetail != null && messageDetail.length > 0) {
		str += "<div style=\"word-break: break-all;\">" + $('#msgBoxLabelmessageDetal').text() + "</div>";
		str += "<div style=\"padding-left: 10px;overflow: scroll;height: 200px;white-space: pre;\">" + messageDetail + "</div>";
	}
	str += "</div>";
	str += "</div>";
	str += "</div>";

	if (linkInfo && linkInfo[0] != null && linkInfo[0] != '' ) {
		// モーダルダイアログのガイダンス部の作成
		str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
	}

	// フッダー
	str += "<div class=\"modal-footer\">";

	// ボタンモード
	var btnNames = [];

	switch (msgBtnMode) {
		case MesBtnMode_AbortRetryIgnore:
			// 中止
			btnNames.push($('#msgBoxBtnNameAbort').text());
			// 再試行
			btnNames.push($('#msgBoxBtnNameRetry').text());
			// 無視
			btnNames.push($('#msgBoxBtnNameIgnore').text());
			break;

		case MesBtnMode_OK:
			// OK
			btnNames.push($('#msgBoxBtnNameOk').text());
			break;

		case MesBtnMode_OKCancel:
			// OK
			btnNames.push($('#msgBoxBtnNameOk').text());
			// Cancel
			btnNames.push($('#msgBoxBtnNameCancel').text());
			break;

		case MesBtnMode_RetryCancel:
			// Retry
			btnNames.push($('#msgBoxBtnNameRetry').text());
			// Cancel
			btnNames.push($('#msgBoxBtnNameCancel').text());
			break;

		case MesBtnMode_YesNo:
			// Yes
			btnNames.push($('#msgBoxBtnNameYes').text());
			// No
			btnNames.push($('#msgBoxBtnNameNo').text());
			break;

		case MesBtnMode_YesNoCancel:
			// Yes
			btnNames.push($('#msgBoxBtnNameYes').text());
			// No
			btnNames.push($('#msgBoxBtnNameNo').text());
			// Cancel
			btnNames.push($('#msgBoxBtnNameCancel').text());
			break;

		default:
			// OK
			btnNames.push($('#msgBoxBtnNameOk').text());
			break;
	}

	var defBtnId = null;

	for (var i=0; i<btnNames.length; i++) {

		var btnId = "myAnyBtn_" + i;

		if (i == defaultBtn) {
			defBtnId = btnId;
		}

		str += "<button type=\"button\" id=\"" + btnId + "\" class=\"btn btn-default\" ";

		// コマンドあり
		if (commands != null && commands.length > i && commands[i] != null && commands[i].length > 0) {

			str += "onclick=\"$('#myAnyModal').modal('hide');$('#myLoadingModal').modal();document.main.command.value='" + commands + "';document.main.submit();\">";

		// コマンドなし
		} else {
			str += "data-dismiss=\"modal\">";

		}
		str += btnNames[i] + "</button>";
	}

	str += "</div>";
	str += "</div>";
	str += "</div>";
	$('#myAnyModal').html(str);
	$('#myAnyModal').on('shown.bs.modal', function () {
		if (defBtnId != null) {
			$('#' + defBtnId).focus();
		}
	}).modal();

}

//メッセージ情報表示 基本
function makeMessageModalBase( title, iconName, occurDate, occrUserName, occrHost, message, messageDetail, msgBtnMode, defaultBtn, commands, resourceid, linkInfo,isOpenDetail) {

	if (isOpenDetail == undefined) {
		isOpenDetail = true;
	}

	var str = "<div class=\"modal-dialog\"> <div class=\"modal-content\"> <div class=\"modal-header\">";
	str += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><span ariea-hidden=\"true\"></span></button>";
	str += "<h4 class=\"modal-title\" id=\"modal-label\">" + title + "</h4></div>";
	str += "<div class=\"modal-body\">";
	str += "<table>";
	str += "<tr>";
	str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/" + iconName + "\"/></td>";
	str += "<td style=\"font-size: 8pt; height:20px;\">" + occurDate + "</td>";
	str += "</tr>";
	str += "<tr>";
	str += "<td style=\"word-break: break-all;\">" + message + "</td>";
	str += "</tr>";
	str += "</table>";

	// 詳細
	str += "<fieldset id=\"detailField\" onclick=\"toggleField('#detailField' ,'#detailArea') \">";
	str += "<h5 style=\"border-top: 1px solid #DDDDDD; margin: 10px 0 0; text-align: left; padding: 0; height: 10px;\">";
	str += "<span style=\"position: relative; top: -9px; padding-right: 3px; background: white;\">";
	if (isOpenDetail == true) {
		str += "<span class=\"glyphicon glyphicon-menu-down\"></span>詳細</span>";
	} else {
		str += "<span class=\"glyphicon glyphicon-menu-right\"></span>詳細</span>";
	}
	str += "</h5></fieldset>";

	if (isOpenDetail == true) {
		str += "<div id=\"detailArea\" class=\"row\" style=\"padding-top: 5px;\">";
	} else {
		str += "<div id=\"detailArea\" class=\"row\" style=\"padding-top: 5px; display:none;\">";
	}

	str += "<div class=\"col-xs-1\"></div>";
	str += "<div class=\"col-xs-11\">";
	// 発生者
	str += "<div style=\"word-break: break-all;\">" + $('#msgBoxLabelOccrUserName').text() + occrUserName + "</div>";
	// 発生場所
	str += "<div style=\"word-break: break-all;\">" + $('#msgBoxLabelOccrHost').text() + occrHost + "</div>";

	// メッセージ詳細
	if (messageDetail != null && messageDetail.length > 0) {
		str += "<div style=\"word-break: break-all;\">" + $('#msgBoxLabelmessageDetal').text() + "</div>";
		str += "<div style=\"padding-left: 10px;overflow: scroll;height: 200px;white-space: pre;\">" + messageDetail + "</div>";
	}
	str += "</div>";
	str += "</div>";
	str += "</div>";

	if (linkInfo && linkInfo[0] != null && linkInfo[0] != '' ) {
		// モーダルダイアログのガイダンス部の作成
		str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
	}

	// フッダー
	str += "<div class=\"modal-footer\">";

	// ボタンモード
	var btnNames = [];

	switch (msgBtnMode) {
		case MesBtnMode_AbortRetryIgnore:
			// 中止
			btnNames.push($('#msgBoxBtnNameAbort').text());
			// 再試行
			btnNames.push($('#msgBoxBtnNameRetry').text());
			// 無視
			btnNames.push($('#msgBoxBtnNameIgnore').text());
			break;

		case MesBtnMode_OK:
			// OK
			btnNames.push($('#msgBoxBtnNameOk').text());
			break;

		case MesBtnMode_OKCancel:
			// OK
			btnNames.push($('#msgBoxBtnNameOk').text());
			// Cancel
			btnNames.push($('#msgBoxBtnNameCancel').text());
			break;

		case MesBtnMode_RetryCancel:
			// Retry
			btnNames.push($('#msgBoxBtnNameRetry').text());
			// Cancel
			btnNames.push($('#msgBoxBtnNameCancel').text());
			break;

		case MesBtnMode_YesNo:
			// Yes
			btnNames.push($('#msgBoxBtnNameYes').text());
			// No
			btnNames.push($('#msgBoxBtnNameNo').text());
			break;

		case MesBtnMode_YesNoCancel:
			// Yes
			btnNames.push($('#msgBoxBtnNameYes').text());
			// No
			btnNames.push($('#msgBoxBtnNameNo').text());
			// Cancel
			btnNames.push($('#msgBoxBtnNameCancel').text());
			break;

		default:
			// OK
			btnNames.push($('#msgBoxBtnNameOk').text());
			break;
	}

	var defBtnId = null;

	for (var i=0; i<btnNames.length; i++) {

		var btnId = "myAnyBtn_" + i;

		if (i == defaultBtn) {
			defBtnId = btnId;
		}

		str += "<button type=\"button\" id=\"" + btnId + "\" class=\"btn btn-default\" ";
		if (msgBtnMode == MesBtnMode_OKCancel && i == 0) {
			str += "onclick=\"$('#myAnyModal').modal('hide');$('#myLoadingModal').modal(); buttonProcess('" + resourceid + "');\">";
		} else {
			str += "data-dismiss=\"modal\">";

		}

		str += btnNames[i] + "</button>";
	}

	str += "</div>";
	str += "</div>";
	str += "</div>";
	$('#myAnyModal').html(str);
	$('#myAnyModal').on('shown.bs.modal', function () {
		if (defBtnId != null) {
			$('#' + defBtnId).focus();
		}
	}).modal();

}


function buttonProcess(resourceid) {

	// リソースID毎に処理を記述
	//alert(resourceid);
}

// ユーザ認証の有無
const PrivilegeIsnecessaryPassword_NONE = 0;		// 何もしない
const PrivilegeIsnecessaryPassword_SINGLE = 1;		// 認証ダイアログを表示する
const PrivilegeIsnecessaryPassword_DOUBLE = 2;		// ダブル認証ダイアログを表示する


// 権限確認メッセージ
// onclick でjavascriptを指定時は、PrivilegeIsnecessaryPassword_NONEとすること、commandは無視する
function GnomesConfirmBTN(title, message, okBtnLabel, isnecessarypassword, commandScript, onclick, loginUserId, privilegeId)
{
	var strFlagChange = "";

	// 2重チェック対象時
	if(isCheckDoubleSubmit){
		if(submitFlag){
			MakeModalMessage( '警告', '只今処理中につき、しばらくお待ちください...<br>処理を中断する場合は、再表示または遷移元の画面に戻ってください。', 'OK');
			return;
		}
		strFlagChange = "submitFlag=true;";
	}

	var escCommandScript = commandScript.split('\'').join('\\\'');

	var format = $('[name=dateFormat]').val() + ":ss";
	var dateFormat = comDateFormat(new Date(), format);

	var str = "<div class=\"modal-dialog\"> <div class=\"modal-content\"> <div class=\"modal-header\">";
	str += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><span ariea-hidden=\"true\"></span></button>";
	str += "<h4 class=\"modal-title\" id=\"modal-label\">" + title + "</h4></div>";
	str += "<div class=\"modal-body\">";

	str += "<table>";
	str += "<tr>";
	str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/kaku.png\"/></td>";
	str += "<td style=\"font-size: 8pt; height:20px;\">" + dateFormat + "</td>";
	str += "</tr>";
	str += "<tr>";
	str += "<td style=\"word-break: break-all;\">" + message + "</td>";
	str += "</tr>";
	str += "</table>";

	str += "</div>";
	str += "<div class=\"modal-footer\">";
	str += "<button type=\"button\" id=\"myAnyClose\" class=\"btn btn-default\" data-dismiss=\"modal\">" + $('#msgBoxBtnClose').text() + "</button>";
	str += "<button type=\"button\" class=\"btn btn-primary\"";

	if (commandScript != null && commandScript != '' ) {
		switch (isnecessarypassword) {
			case PrivilegeIsnecessaryPassword_NONE:
				if(onclick == undefined || onclick == ''){
					str += "onclick=\"$('#myAnyModal').modal('hide');$('#myLoadingModal').modal();"  + strFlagChange + "isCheckDoubleSubmit=false;" + commandScript + "\"";

				} else {
					str += "onclick=\"" + onclick + "\"";
				}
				break;
			case PrivilegeIsnecessaryPassword_SINGLE:
				str += "onclick=\"$('#myAnyModal').modal('hide');IsnecessaryPassword('" + title + "','" + loginUserId + "','"  + escCommandScript + "','" + privilegeId + "', false);\"";
				break;

			case PrivilegeIsnecessaryPassword_DOUBLE:
				str += "onclick=\"$('#myAnyModal').modal('hide');IsnecessaryPassword('" + title + "','" + loginUserId + "','"  + escCommandScript + "','" + privilegeId + "', true);\"";
				break;
		}
	}

	else {
		if(onclick == undefined || onclick == null || onclick == ''){
			str += "onclick=\"$('#myAnyModal').modal('hide');\"";

		} else {
			str += "onclick=\"$('#myAnyModal').modal('hide');" + onclick + "\"";
		}
	}

	str += ">" + okBtnLabel + "</button>";
	str += "</div>";
	str += "</div>";
	str += "</div>";
	$('#myAnyModal').html(str);
	$('#myAnyModal').on('shown.bs.modal', function () {
   		 $('#myAnyClose').focus();
	}).modal();

}

// 認証ダイアログ(ダブル認証ダイアログ)を表示する
function IsnecessaryPassword(title, loginUserId, commandScript, privilegeId, isDoubleCheck)
{

	var strFlagSubmit = "";

	// 2重サブミットチェック対象時
	if(isCheckDoubleSubmit){
		if(submitFlag){
			MakeModalMessage( '警告', '只今処理中につき、しばらくお待ちください...<br>処理を中断する場合は、再表示または遷移元の画面に戻ってください。', 'OK');
			return;
		}
		strFlagChange = "submitFlag=true;";
	}

	var certUserId = $('[name=certUserName]').val();

	var str ="<div class=\"modal-dialog\">";
	str +="	<div class=\"modal-content\">";
	str +="		<div class=\"modal-header\" align=\"center\">";
	str +="			<button type=\"button\" class=\"close\" onclick=\"$('#license-check-modal').modal('hide');$('#license-check-modal').remove();\" data-dismiss=\"modal\" aria-label=\"Close\">";
	str +="				<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
	str +="			</button>";
	str +="			<h1  class=\"text-center\">" + title + "</h1>";
	str +="		</div>";
	str +="		<div id=\"div-forms\">";
	str +="			<div class=\"modal-body\">";
	str +="				<div id=\"div-login-msg\">";
	str +="					<div id=\"icon-login-msg\" class=\"glyphicon glyphicon-chevron-right\"></div>";
	str +="					<span id=\"text-login-msg\">ログインユーザ名とパスワードを入力ください</span>";
	str +="				</div>";
	str +="				<div style=\"float:left; width:20%; padding-top:17px;\">ユーザID</div>";
	str +="				<input name=\"loginUserId\" id=\"loginUserId\" class=\"form-control\" type=\"text\" style=\"width:80%;\"  placeholder=\"ログインユーザ名\" value=\"" + loginUserId + "\" readonly>";
	str +="				<input name=\"loginUserPassword\" id=\"loginUserPassword\" class=\"form-control\" type=\"password\" placeholder=\"パスワード\">";

	if(isDoubleCheck){
		str +="				<div id=\"div-login-msg\" style=\"margin-top:10px;\">";
		str +="					<div id=\"icon-login-msg\" class=\"glyphicon glyphicon-chevron-right\"></div>";
		str +="					<span id=\"text-login-msg\">認証ユーザ名とパスワードを入力ください</span>";
		str +="				</div>";
		str +="				<input name=\"certUserId\" id=\"certUserId\" class=\"form-control\" type=\"text\" placeholder=\"認証ユーザ名\" value=\"" + certUserId + "\">";
		str +="				<input name=\"certUserPassword\" id=\"certUserPassword\" class=\"form-control\" type=\"password\" placeholder=\"パスワード\">";
	}
	else{
		str +="				<input type=\"hidden\" name=\"certUserId\" value=\"\">";
		str +="				<input type=\"hidden\" name=\"certUserPassword\" value=\"\">";
	}

	str +="				<input type=\"hidden\" name=\"privilegeId\" value=\""+ privilegeId +"\"/>";
	str +="				<input type=\"hidden\" name=\"isDoubleCheck\" value=\""+ isDoubleCheck +"\"/>";
	str +="			</div>";
	str +="			<div class=\"modal-footer\">";
	// OKボタン
	str += "			<input type=\"button\" class=\"btn btn-primary btn-lg\" value=\"OK\"";
	str += "				onclick=\"$('#license-check-modal').modal('hide');"  + strFlagChange + "isCheckDoubleSubmit=false;" + commandScript + "\">";

	// キャンセルボタン
	str += "			<input type=\"button\" class=\"btn btn-primary btn-lg\" value=\"キャンセル\"";
	str += "				onclick=\"$('#license-check-modal').modal('hide');$('#license-check-modal').remove();\">";

	str +="			</div>";
	str +="		</div>";
	str +="	</div>";
	str +="</div>";

	$('#license-check-modal').html(str);
	$('#license-check-modal').on('shown.bs.modal', function () {
   		 $('#license-check-modal').focus();
	}).modal();

}


//ダイアログ中のフィールド開閉
function toggleField(field, area){

	//フィールド開閉
	$(area).slideToggle();

	//アイコン変更
	var right = $(field).find('.glyphicon-menu-right');
	var down = $(field).find('.glyphicon-menu-down');

	right.removeClass("glyphicon-menu-right");
	right.addClass("glyphicon-menu-down");

	down.removeClass("glyphicon-menu-down");
	down.addClass("glyphicon-menu-right");

}

//===================================================================
/**
 * 戻るボタンの無効化
 */
//===================================================================

if( window.history.pushState ){
  history.pushState( "nohb", null, "" );
  $(window).on( "popstate", function(event){
    if( !event.originalEvent.state ){
      history.pushState( "nohb", null, "" );
      return;
    }
  });
}

//===================================================================
/**
 * F5キー、ESCキーの無効化
 *
 * キーマップは URL http://www.programming-magic.com/file/20080205232140/keycode_table.html を参照
 *
 */
//===================================================================

function keydown() {
  if(event.keyCode == 116){
    event.keyCode = 0;
    return false;
  }
}

//===================================================================
/**
 * [機能]	日付オブジェクトから文字列に変換します
 * [引数]	date	対象の日付オブジェクト
 * 			format	フォーマット
 * [戻値]	フォーマット後の文字列
 */
//===================================================================

function comDateFormat(date, format){

	var result = format;

	var f;
	var rep;

	var yobi = new Array('日', '月', '火', '水', '木', '金', '土');

	f = 'YYYY';
	if ( result.indexOf(f) > -1 ) {
		rep = date.getFullYear();
		result = result.replace(/YYYY/, rep);
	}

	f = 'MM';
	if ( result.indexOf(f) > -1 ) {
		rep = comPadZero(date.getMonth() + 1, 2);
		result = result.replace(/MM/, rep);
	}

	f = 'ddd';
	if ( result.indexOf(f) > -1 ) {
		rep = yobi[date.getDay()];
		result = result.replace(/ddd/, rep);
	}

	f = 'DD';
	if ( result.indexOf(f) > -1 ) {
		rep = comPadZero(date.getDate(), 2);
		result = result.replace(/DD/, rep);
	}

	f = 'HH';
	if ( result.indexOf(f) > -1 ) {
		rep = comPadZero(date.getHours(), 2);
		result = result.replace(/HH/, rep);
	}

	f = 'mm';
	if ( result.indexOf(f) > -1 ) {
		rep = comPadZero(date.getMinutes(), 2);
		result = result.replace(/mm/, rep);
	}

	f = 'ss';
	if ( result.indexOf(f) > -1 ) {
		rep = comPadZero(date.getSeconds(), 2);
		result = result.replace(/ss/, rep);
	}

	f = 'fff';
	if ( result.indexOf(f) > -1 ) {
		rep = comPadZero(date.getMilliseconds(), 3);
		result = result.replace(/fff/, rep);
	}

	return result;

}

//===================================================================
/**
 * [機能]	文字列から日付オブジェクトに変換します
 * [引数]	date	日付を表す文字列
 * 			format	フォーマット
 * [戻値]	変換後の日付オブジェクト
 */
//===================================================================

function comDateParse(date, format){

	var year = 1990;
	var month = 01;
	var day = 01;
	var hour = 00;
	var minute = 00;
	var second = 00;
	var millisecond = 000;

	var f;
	var idx;

	f = 'YYYY';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		year = date.substr(idx, f.length);
	}

	f = 'MM';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		month = parseInt(date.substr(idx, f.length), 10) - 1;
	}

	f = 'DD';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		day = date.substr(idx, f.length);
	}

	f = 'HH';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		hour = date.substr(idx, f.length);
	}

	f = 'mm';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		minute = date.substr(idx, f.length);
	}

	f = 'ss';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		second = date.substr(idx, f.length);
	}

	f = 'fff';
	idx = format.indexOf(f);
	if ( idx > -1 ) {
		millisecond = date.substr(idx, f.length);
	}

	var result = new Date(year, month, day, hour, minute, second, millisecond);

	return result;

}

//===================================================================
/**
 * [機能]	ゼロパディングを行います
 * [引数]	value	対象の文字列
 * 			length	長さ
 * [戻値]	結果文字列
 */
//===================================================================
function comPadZero(value, length){
    return new Array(length - ('' + value).length + 1).join('0') + value;
}


//===================================================================
/**
 * [機能]	localStorageのキー作成
 * [引数]	userId		ユーザID
 * 			kindName	データ種類名
 *			param1		パラメータ1
 * [戻値]	結果 key
 */
//===================================================================
function makeLocalStrageKey(userId, kindName, param1) {

	var key = userId + "_" + kindName;
	if (param1 != undefined) {
		key = key + "_" + param1;
	}
	return key;
}

//===================================================================
/**
 * [機能]	localStorageへ保存
 * [引数]	key		キー
 * 			vakue	値
 * [戻値]	なし
 */
//===================================================================
function saveLocalStorage(key, value) {
	localStorage.setItem(key, value);
}

//===================================================================
/**
 * [機能]	localStorageから削除
 * [引数]	key		キー
 * [戻値]	なし
 */
//===================================================================
function deleteLocalStrage(key) {
	localStorage.removeItem(key);
}

//===================================================================
/**
 * [機能]	localStorageからkeyで
 * 			１つのデータを取得する
 * [引数]	key			キー
 * [戻値]	結果		データ
 */
//===================================================================
function getLocalStorage(key){

	return localStorage.getItem(key);

}

//===================================================================
/**
 * [機能]	localStorageからkeyで
 *       	前方一致する情報をまとめて連想配列を返す
 * [引数]	key			キー
 * 			kindName	データ種類名
 * [戻値]	結果		連想配列
 */
//===================================================================
function getLocalStorages(key) {

	var datas = new Object();

	for (var i=0; i<localStorage.length; i++){

		var k = localStorage.key(i);
		var cmp = ' ' + k;

		// 前方一致
		if (cmp.indexOf(" " + key) !== -1) {
			datas[k] = localStorage.getItem(k);
		}
	}

	return datas;
}

//===================================================================
/**
 * [機能]	userIdを取得する
 * [引数]	なし
 * [戻値]	usetId
 */
//===================================================================
function getUserId() {
	return $('#userId').text();
}

//===================================================================
/**
 * [機能]	screenIdを取得する
 * [引数]	なし
 * [戻値]	screenId
 */
//===================================================================
function getScreenId() {
	return $('#screenId').text();
}

//===================================================================
/**
 * [機能]	screenNameを取得する
 * [引数]	なし
 * [戻値]	screenName
 */
//===================================================================
function getScreenName() {
	return $('#screenName').text();
}



//===================================================================
/**
 * [機能]	モーダルダイアログ閉じる共通処理
 *          下にモーダルがある場合、下のモーダルにwindowスクロールを行えるように処理する
 * [引数]	userId		ユーザID
 * [戻値]	結果JSON
 */
//===================================================================
function doCommonHiddenModal(hiddenModal) {

	$(hiddenModal).prevAll(".modal").each(function(i, elem) {
		if ( $(elem).css('display') == 'block'){
			$('body').addClass('modal-open');
			return false;
		}
	});

}

//===================================================================
/**
 * [機能]	ページングコマンド発行
 * [引数]	command			コマンド
 *			pagingCommand	ページングコマンド
 * [戻値]	なし
 */
//===================================================================
function funcPaging(command, pagingCommand) {
	document.main.command.value = command;
	document.main.pagingCommand.value = pagingCommand;
	document.main.submit();
}

//===================================================================
/**
 * [機能]	ページング1ページ表示数入力
 * [引数]	command			コマンド
 *			pagingCommand	ページングコマンド
 * [戻値]	なし
 */
//===================================================================
function keyDownPagingOnePageCount(command, pagingCommand) {

	//EnterキーならSubmit
	if(window.event.keyCode==13) {
		document.main.command.value = command;
		document.main.pagingCommand.value = pagingCommand;
		document.main.submit();
	}
}

//===================================================================
/**
 * [機能]	インポートファイルサービス実行
 * [引数]	inputId			ファイルInputのタグID
 *			errLinkId		エラーリンクのタグID
 *			command			実行コマンド
 * [戻値]	なし
 */
//===================================================================
function doImportFileService(inputId, errLinkId, command, inCallbacks) {
	var formData = new FormData();
	$.each($('#'+inputId)[0].files, function(i, file) {
		formDataAppendFile(formData, file);
	});

	var windowId = $('#windowId').val();
	formData.append("windowId", windowId);

	var callbacks = {
	   'begin'    : function() {
						$('#'+errLinkId).hide();
						$("button[id$=ErrLnk]").hide();
	   				},
	   'cmdsuccess'  : function(commandResponse){
						if (commandResponse.importValidateError) {
							$('#'+errLinkId).show();
						} else {
							$('#'+errLinkId).hide();
						}

						if (inCallbacks != undefined && 'cmdsuccess' in inCallbacks) {
							inCallbacks['cmdsuccess'](commandResponse);
						}
	   				}
	};

	ajax_submit_command(
		'rest/Y99002S001/importDataFile',
		command,
		formData,
		callbacks
	);
}


//===================================================================
/**
 * [機能]	アップロードサービス実行
 * [引数]	inputId			ファイルInputのタグID
 *			command			実行コマンド
 * [戻値]	なし
 */
//===================================================================
function doUpLoadService(inputId, command) {

	var formData = new FormData();

	var windowId = $('#windowId').val();
	formData.append("windowId", windowId);

	// ファイルの設定
	$.each($('#'+inputId)[0].files, function(i, file) {
		formDataAppendFile(formData, file);
	});

	// コールバック設定
	var callbacks = {
	   'successfinal' : function(data){
							if (data.commandResponse != null && data.commandResponse.isCheckOverwrite == true) {

								// 暫定処置
								// メッセージビーンのメッセージも表示され
								// 上書き確認ができないので
								// nullに設定して表示しないようにする
								data.messageBean.message = null;

								// 上書き確認
								$.confirmDialog(
										data.commandResponse.message,
										$('#msgBoxBtnNameOk').text(),	// OK
										$('#msgBoxBtnNameCancel').text()	// Cancel
									)
									.done(function() {
										// OK
										doUpLoadCompulsion(data.commandResponse.command, '1');
			    					})
			    					.fail(function() {
			    						// Cancel
										doUpLoadCompulsion(data.commandResponse.command, '0');
			    					});
							}
						}
	};

	ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);

}
//===================================================================
/**
 * [機能]	強制アップロードサービス実行
 * [引数]	inputId			ファイルInputのタグID
 *			command			実行コマンド
 * [戻値]	なし
 */
//===================================================================
function doUpLoadCompulsion(command, isOverwrite) {

	var formData = new FormData();
	formData.append("isOverwrite", isOverwrite);

	var windowId = $('#windowId').val();
	formData.append("windowId", windowId);

	// コールバック
	var callbacks = {
	   'cmdsuccess'  : function(commandResponse){
			      		$('#myAnyModal').modal('hide');
   				}
	};

	ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);
}



//===================================================================
/**
 * [機能]	アップロードサービス(後から登録処理）用の非表示Inputタグで
 *			ファイル選択を行う
 * [引数]	inputId			非表示InputタグID
 *			cntId			ファイル選択件数を表示するタグID
 *			filetableId		ファイル情報を表示するTableのタグID
 * [戻値]	なし
 */
//===================================================================
function doHideUpLoadFileSelect(inputId, cntId, filetableId) {

	// ファイル情報表示を削除
	$('#' + filetableId).find("tr").remove();

	$('#'+inputId).change(function() {
		var files = $(this).prop('files');
		var num = files.length;
		// ファイル選択件数を表示
		$('#'+cntId).text(num + "ファイル");
	});

	$('#'+inputId).click();
}



//===================================================================
/**
 * [機能]	アップロードサービス実行(後から登録処理）
 * [引数]	inputId			ファイルInputのタグID
 *			filetableId		ファイル情報を表示するTableのタグID
 *			command			実行コマンド
 * [戻値]	なし
 */
//===================================================================
function doUpLoadServiceAfterSubmit(inputId, filetableId, command) {

	var formData = new FormData();

	var windowId = $('#windowId').val();
	formData.append("windowId", windowId);

	// ファイルの設定
	$.each($('#'+inputId)[0].files, function(i, file) {
		formDataAppendFile(formData, file);
	});

	// コールバック
	var callbacks = {
	   'cmdsuccess'  : function(commandResponse){
	   					if (commandResponse.isSuccess == true) {
							// ファイル情報表示を削除
							$('#' + filetableId).find("tr").remove();

							// ファイル情報を表示
							for (var i = 0; i < commandResponse.filenames.length; i++) {
								$('#' + filetableId).append( '<tr><td>' + commandResponse.filenames[i] + '</td></tr>' );
							}
						}
	   				}
	};

	ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);

}

//===================================================================
/**
 * [機能]	windowのcloseサービス実行
 * [引数]	command			実行コマンド
 *
 * [戻値]	なし
 */
//===================================================================
function doCloseWindow(command) {

	var windowId = $('#windowId').val();

	var formData = new FormData();
	formData.append("windowId", windowId);

	// コールバック
	var callbacks = {
	   'successfinal'  : function(data){
			      		$('#myAnyModal').modal('hide');
			      		window.close();
   				}
	};

	ajax_submit_command('rest/Y99002S001/closeWindow', command, formData, callbacks, false);
}

//===================================================================
/**
 * [機能]	フォームデータにファイルを追加
 * [引数]	formData	フォームデータ
 *			file		追加ファイル
 * [戻値]	なし
 */
//===================================================================
function formDataAppendFile(formData, file) {
	formData.append('file', file);
}

//===================================================================
/**
 * [機能]	ajaxでコマンド実行
 * [引数]	action		url
 *			command		実行コマンド
 *			formData	送信フォームデータ
 *			callbacks	コールバック
 *			isCheckSessionError		セッションエラー判定フラグ
 * [戻値]	なし
 */
//===================================================================
function ajax_submit_command( action, command, formData, callbacks, isCheckSessionError ) {


	if(isCheckSessionError === undefined) isCheckSessionError = true;


	// コールバックのデフォルト値を設定
	var submit_defaults = {
	   'begin'    : function(){
	   					// ロックタイマーのクリア
						clearLockTimer();

						if ('begin' in callbacks) {
							callbacks['begin']();
						}
	   				},
	   'success'  : function(data){
	   					// 次回ダブル認証表示時の承認者IDを設定
						$(':hidden[name="certUserName"]').val(data.certUserId);

						if ('success' in callbacks) {
							callbacks['success']();
						}
	   				},
	   'cmdsuccess'  : function(commandResponse){
						if ('cmdsuccess' in callbacks) {
							callbacks['cmdsuccess'](commandResponse);
						}
	   				},
	   'cmderror'  : function(commandResponse){
						if ('cmderror' in callbacks) {
							callbacks['cmderror'](commandResponse);
						}
	   				},
	   'successfinal'  : function(data){
							if ('successfinal' in callbacks) {
								callbacks['successfinal'](data);
							}
							if (data.messageBean.message != null) {
								// メッセージダイアログを表示
								var linkInfo = ['', '', ''];

								if (data.messageBean.linkInfo != null) {
									linkInfo = [data.messageBean.linkInfo[0],data.messageBean.linkInfo[1],data.messageBean.linkInfo[2]];
								}

								makeMessageModalBase( data.messageBean.categoryName,
									  data.messageBean.iconName,
									  data.messageBean.occurDate,
									  data.messageBean.occrUserName,
									  data.messageBean.occrHost,
									  data.messageBean.message,
									  data.messageBean.messageDetail,
									  data.messageBean.msgBtnMode,
									  data.messageBean.defaultBtn,
									  data.messageBean.command,
									  linkInfo,
									  false);
					        }

					        // セッションエラー判定
					        if (isCheckSessionError == true && data.isSessionError == true) {
								MakeModalAlert( $('#msgBoxTitleError').text(),$('#msgServiceSessionErrMessage').text(), $('#msgBoxBtnNameOk').text());
					        }

	   				},
	   'error'    : function(){
				   		   MakeModalAlert( $('#msgBoxTitleError').text(),$('#msgServiceErrMessage').text(), $('#msgBoxBtnNameOk').text());
	   				},
	   'complete' : function(){
       					 	// ロックタイマー再開
							// ロックタイマー設定
							setLockTimer();
	   				},
	};

	var actionWithParam = action;


	// cid
	var cid = $('#cid').val();
	if (cid != undefined) {
		actionWithParam = actionWithParam + "?cid=" + cid;
	}

	if (formData == null) {
		formData = new FormData();
	}

	// 基本情報の設定
	var paramArray = get_common_command_base(command);
	for(var key in paramArray) {
		formData.append(key, paramArray[key]);
	}

	ajax_submit(actionWithParam, formData, submit_defaults);
}


//===================================================================
/**
 * [機能]	ajax通信共通処理
 * [引数]	action		url
 *			formData	送信フォームデータ
 *			callbacks	コールバック
 * [戻値]	なし
 */
//===================================================================
function ajax_submit( action, formData, callbacks ) {

	// コールバックのデフォルト値を設定
	var defaults = {
	   'begin'    : function(){},
	   'success'  : function(){},
	   'cmdsuccess'  : function(){},
	   'cmderror'  : function(){},
	   'successfinal'  : function(){},
	   'error'    : function(){},
	   'complete' : function(){},
	};
    // デフォルト値とマージ
    var callbacks = $.extend( defaults, callbacks );
    // 開始コールバックを起動
    callbacks['begin']();

    // 非同期通信
	$.ajax({
        url: action,
        type: 'POST',
        data:  formData,
        contentType: false,
        cache: false,
        processData:false,
        dataType: 'json',
        success: function(data, textStatus, jqXHR) {

	       	callbacks['success'](data);

        	if (data.isSuccess == true) {
	           // 成功コールバックを起動
	           callbacks['cmdsuccess'](data.commandResponse);
	        } else {
               // 失敗コールバックを起動
               callbacks['cmderror'](data.commandResponse);

	        }

	       	callbacks['successfinal'](data);



        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
           // 失敗コールバックを起動
           callbacks['error'](XMLHttpRequest, textStatus, errorThrown);
        },
        complete : function( result ){
           // 完了コールバックを起動
           callbacks['complete'](result);
        }
    });
}

//===================================================================
/**
 * [機能]	共通パラメータを取得
 * [引数]	command	フォームデータ
 *			inArray		追加データ
 * [戻値]	Array		キー：パラメータ名 値：パラメータ値
 */
//===================================================================
function get_common_command_base(command, inArray) {

	var result = new Array();

	if (inArray != undefined && inArray != null) {
		for(var key in inArray) {
			result[key] = inArray[key];
		}
	}

	result['command'] = command;

	if($(':hidden[name="pageToken"]').length){
		var pageToken = $(':hidden[name="pageToken"]').val();
		result['pageToken'] = pageToken;
	}

	if($('#loginUserId').length){
		var loginUserId = $('#loginUserId').val();
		result['loginUserId'] = loginUserId;
	}

	if($('#loginUserPassword').length){
		var loginUserPassword = $('#loginUserPassword').val();
		result['loginUserPassword'] = loginUserPassword;
	}

	if($('#certUserId').length){
		var certUserId = $('#certUserId').val();
		result['certUserId'] = certUserId;
	}

	if($('#certUserPassword').length){
		var certUserPassword = $('#certUserPassword').val();
		result['certUserPassword'] = certUserPassword;
	}

	if($(':hidden[name="privilegeId"]').length){
		var privilegeId = $(':hidden[name="privilegeId"]').val();
		result['privilegeId'] = privilegeId;
	}

	if($(':hidden[name="isDoubleCheck"]').length){
		var isDoubleCheck = $(':hidden[name="isDoubleCheck"]').val();
		result['isDoubleCheck'] = isDoubleCheck;
	}

	return result;
}


//===================================================================
/**
 * [機能]	別Windowでコマンド実行処理
 * [引数]	command		実行コマンド
 *			inArray		送信パラメータ
 * [戻値]	なし
 */
//===================================================================
function open_window_submit_command(command, inArray) {

		var paramArray = get_common_command_base(command, inArray);
		var cid = $('#cid').val();

		var target = '_blank';
		var paramStr = '';

		for(var key in paramArray) {
			paramStr = paramStr + "&" + key + "=" + decodeURIComponent(paramArray[key]);
		}

		window.open(location.href + "gnomes?command=" + command + "&cid=" + cid + paramStr, target) ;


/* POSTだと閉じない
		// about:blankとしてOpen
		var target = 'GNOMESDOWNLOAD';
		window.open("", target) ;


		// formを生成
		var form = document.createElement("form");
		form.action = location.href + "gnomes?cid=" + cid;
		form.target = target;
		form.method = 'post';

		// input-hidden生成と設定
		for(var key in paramArray) {
			var input = document.createElement("input");
			input.setAttribute("type", "hidden");
			input.setAttribute("name", key);
			input.setAttribute("value", paramArray[key]);
			form.appendChild(input);
		}

		// formをbodyに追加して、サブミットする。その後、formを削除
		var body = document.getElementsByTagName("body")[0];
		body.appendChild(form);
		form.submit();
		body.removeChild(form);
*/
}

//===================================================================
/**
 * [機能]	別Windowでコマンド実行処理(post)
 * [引数]	command		実行コマンド
 *			inArray		送信パラメータ
 *			isPostCid	cid送信フラグ
 * [戻値]	なし
 */
//===================================================================
function open_window_submit_command_post(command, inArray, isPostCid) {

		if(isPostCid === undefined) isPostCid = false;

		var paramArray = get_common_command_base(command, inArray);
		var cid = $('#cid').val();

		// about:blankとしてOpen
		var target = 'GNOMES' + (new Date()).getTime();
		window.open("", target) ;

		// formを生成
		var form = document.createElement("form");
		form.action = location.href + "gnomes";
		form.target = target;

		form.method = 'post';

		if (isPostCid == true) {
			paramArray['cid'] = cid;
		}

		// input-hidden生成と設定
		for(var key in paramArray) {
			var input = document.createElement("input");
			input.setAttribute("type", "hidden");
			input.setAttribute("name", key);
			input.setAttribute("value", paramArray[key]);
			form.appendChild(input);
		}

		// formをbodyに追加して、サブミットする。その後、formを削除
		var body = document.getElementsByTagName("body")[0];
		body.appendChild(form);
		form.submit();
		body.removeChild(form);
}


//===================================================================
/**
 * [機能]	一覧データエクスポート
 * [引数]	なし
 *
 * [戻値]	なし
 */
//===================================================================
function exportTable() {

		var params = {};
		var windowId = $('#windowId').val();

		params['screenId'] = getScreenId();
		params['screenName'] = getScreenName();
		params['windowId'] = windowId;

		open_window_submit_command('Y99002C002', params);

}

//===================================================================
/**
 * [機能]	インポートエラーダウンロード
 * [引数]	なし
 *
 * [戻値]	なし
 */
//===================================================================
function importErrorDownload() {

		var params = {};
		var windowId = $('#windowId').val();

		params['screenId'] = getScreenId();
		params['screenName'] = getScreenName();
		params['windowId'] = windowId;

		open_window_submit_command('Y99002C001', params);

}

//===================================================================
/**
 * [機能]	ajaxでブックマークサービスコマンドを実行
 * [引数]	command		実行コマンド
 * [戻値]	なし
 */
//===================================================================
function ajax_bookmark_command( command ) {

	var formData = new FormData();
	formData.append("bookmarkScreenId", getScreenId());

	// コールバック
	var callbacks = {
// カスタムタグの出力内容にあわせる
// メニューの内容を変更する
//	   'successfinal'  : function(data){
//			      		$('#myAnyModal').modal('hide');
//			      		window.close();
//   				}
	};
	ajax_submit_command('rest/BookMarkService', command, formData, callbacks);
}


	// 確認ダイアログ
	(function($) {
		$.confirmDialog = function(message, yesLabel, noLabel) {

			var str = MakeModalHeaderBody($('#msgBoxTitleConfirm').text(), message);
			str += "<div class=\"modal-footer\">";

			str += "<button type=\"button\" class=\"btn btn-default\"";
			str += ">" + noLabel + "</button>";
			str += "<button type=\"button\" class=\"btn btn-primary\"";
			str += ">" + yesLabel + "</button>";
			str += "</div>";
			str += "</div>";
			str += "</div>";
			$('#myAnyModal').html(str);

			var deferred = $.Deferred();

			var $element = $("#myAnyModal");
    		$element
        		//「YES」ボタンのクリックイベント
        		.on("click", ".btn-primary", function() {
					// モーダル閉じない
            		deferred.resolve();
		        })
        		//「NO」ボタンのクリックイベント
        		.on("click", ".btn-default", function() {
					// モーダル閉じない
            		deferred.reject();
		        })
		        // 表示時処理
		        .on('shown.bs.modal', function () {
		        	$('.btn-default','#myAnyModal').focus();
		        })
        	.modal({ show: true });

			return deferred.promise();
		};
	})(jQuery);
