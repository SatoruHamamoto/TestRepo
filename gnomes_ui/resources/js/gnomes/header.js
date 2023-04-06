/**
 * フレームワーク共通処理スクリプト
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
 * 共通ヘッダ作成
 */
//===================================================================
function drawHeader(){

var screenId = $('input[name=SCREEN_ID]').val();

$.ajax({
        type: "GET",
        async: false,
		cache : false,
        url: "/gnomes/ui/header/headerInfo",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: function (data) {
			var domString="";

			domString += "<table style='width: 100%; white-space: nowrap;'>";

			for (var i = 0; i < data.contentsList.length; i++) {
				var contentsObj = data.contentsList[i];

				//セル幅固定かどうか
				if ( contentsObj.tdStyle.length > 0){
					domString += "<td valign='center' style='" + contentsObj.tdStyle + "'>";
				}
				else {
					domString += "<td valign='center'>"
				}

				domString += "<div class='ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset'>";


				//コンテンツの種類ごとの設定
				switch (contentsObj.contentsType) {
				case 'IconText':
					domString += "<button class='" + contentsObj.className +"' type='reset' data-toggle='modal' data-target='#modal-example' ";
					domString += "title='" + contentsObj.toolTip + "'>";
					domString += "<IMG src='"+ contentsObj.image_source + "' alt='button' /></button>";
					domString += "<input style='font-weight: bold;margin-top: 10px;' class='" + contentsObj.textClassName + "' maxlength='" + contentsObj.textMaxLength + "' ";
					domString += "name='" + contentsObj.textName + "' size='" + contentsObj.textSize + "' ";
					domString += "value='" + contentsObj.textString + " ' readonly/>";
					break;
				case 'Icon':
					domString += "<button class='" + contentsObj.className + "' type='reset' data-toggle='" + contentsObj.data_toggle + "' data-target='" + contentsObj.data_target + "' ";
					domString += "title='" + contentsObj.toolTip ;
					if(contentsObj.commandName.length > 0){
						if(contentsObj.objStyle="dialog"){
							domString +="onclick='"+ contentsObj.commandName[0] + "'";
						}
						else {
							domString +="onclick='document.main.COMMAND_NAME.value='" + contentsObj.commandName[0] + "';SendBTN('"+ contentsObj.commandName[1] + "');'";
						}
					}
					domString += "'>";
					domString += "<IMG src='"+ contentsObj.image_source + "' alt='button' /></button>";
					break;
				case 'DropDown':
					domString += "<div class='dropdown'>";
					domString += "<button class='btn btn-default dropdown-toggle' type='button' id='" + contentsObj.contentsIdName + "' data-toggle='dropdown'>";
					domString += contentsObj.message;
					domString += "<span class='caret'></span></button>";
					domString += "<ul class='dropdown-menu' role='menu' aria-labelledby='" + contentsObj.contentsIdName + "'>";
					for(var j=0;j<contentsObj.menuItem.length;j++){
						var menuItem = contentsObj.menuItem[j];
						domString += "<li role='presentation' class='" + menuItem.className + "'>";
						domString += "<a role='menuitem' tabindex='-1' href='#'><IMG src='images/warning.png' />";
						domString += menuItem.message + "</a></li>";
					}
					domString +="</ui></div>";
					break;
				case 'DropDownItem':
					break;
				case 'glyph':
					domString += "<button type='button' class='" + contentsObj.className + "'";
					domString += "aria-label='Left Align'>";
					domString += "<span class='" + contentsObj.glyphicon + "' aria-hidden='true'></span></button>";
					break;
				//その他
				default:
					break;
				}

				domString +="</div></td>";
			}
			//alert(data.name);
			$('#headerBox').html(domString);
		},
        error: function (e) {
            alert("初期設定情報の取得に失敗しました。<br />[Web Error]");
        }
    });
$.ajax({
        type: "GET",
		cache : false,
        async: false,
        url: "/gnomes/ui/header/headerToolBoxInfo?screen=" + screenId,
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: function (data) {
			var domString="";

			domString += "<table>";

			for (var i = 0; i < data.contentsList.length; i++) {
				var contentsObj = data.contentsList[i];
				if ( contentsObj.tdStyle.length > 0){
					domString += "<td valign='center' style='" + contentsObj.tdStyle + "'>";
				}
				else {
					domString += "<td valign='center'>"
				}
				domString += "<div class='ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset'>";
				domString += "<button class='" + contentsObj.className +"' type='reset' data-toggle='" + contentsObj.data_toggle + "' data-target='" + contentsObj.data_target + "' ";
				domString += "title='" + contentsObj.toolTip + "' ";
				if(contentsObj.commandName.length > 0){

					if(contentsObj.objStyle=="dialog"){
						domString +="onclick='"+ contentsObj.commandName[0] + "'";
					}
					else {
						domString += "onclick=\"document.main.COMMAND_NAME.value='" + contentsObj.commandName[0] + "';";
						domString +="GnomesSendBTN('確認',";
						domString +="'製造指図の" + contentsObj.toolTip + "を実施しますか？',";
						domString +="'" + contentsObj.toolTip + "',";
						domString +="'"+ contentsObj.commandName[0] + "',";
						domString +="'"+ contentsObj.commandName[1] + "');\"";
					}
					//onclick="document.main.COMMAND_NAME.value='Apploval';SendBTN(this.name);"
				}
				domString += ">";
				domString += "<IMG src='"+ contentsObj.image_source + "' alt='button' /></button>";
				domString +="</div></td>";
			}
			$('#headerToolBox').html(domString);
		},
        error: function (e) {
            alert("初期設定情報の取得に失敗しました。<br />[Web Error]");
        }
    });

	// $('#headerBox').html('<table>'
		// + '<td><button class="headerBtn" type="reset" data-toggle="modal" data-target="#modal-example"> '
		// +	'<IMG src="images/home.png" alt="button"/></button></td>'
		// + '<td><div class="ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset">'
		// +	'<button class="headerBtn" type="reset" data-toggle="modal" data-target="#modal-example">'
		// +	'<IMG src="images/people.png" alt="button"/></button>'
		// +	'<input style="font-weight: bold;" class="NOINPUT_TEXT" maxlength="25" name="NMLTXT_USERNAME" size="12" value="浅野"/></div></td>'
		// + '<td><div class="ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset">'
		// +	'<button class="headerBtn" type="reset" data-toggle="modal" data-target="#modal-example"><IMG src="images/factory.png" alt="button"/></button>'
		// +	'<input style="font-weight: bold;" class="NOINPUT_TEXT" maxlength="25" name="NMLTXT_FACTORY" size="16" value="Ａ工場　Ｂ秤量室"/>'
		// + '<td><div class="dropdown">'
		// + '<button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">'
		// + '2016/04/22 13:55 リンゴジュースの製造が完了しました。'
		// + '<span class="caret"></span></button>'
		// + '<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">'
		// + '<li role="presentation"><a role="menuitem" tabindex="-1" href="#" ><IMG src="images/warning.png" />2016/04/22 13:56 ビタミンC入りリンゴジュースの製造が滞っています</a></li>'
		// + '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"><IMG src="images/danger.png" />2016/04/22 13:50 添加釜2が故障しました</a></li>'
		// + '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"><IMG src="images/warning.png" />2016/04/22 13:00 岩崎さんが工程室１に入りました</a></li>'
		// + '<li role="presentation" class="divider"></li>'
		// + '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"><IMG src="images/warning.png" />2016/04/22 01:56 砂糖水の在庫が残り少なくなっています。</a></li>'
		// + '</ul></div></td>'
		// + '<td>'
		// + '<button type="button" class="btn btn-default" aria-label="Left Align"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></button>'
		// + '</td>'
		// + '<td>'
		// + '<button type="button" class="btn btn-default" aria-label="Left Align"><span class="glyphicon glyphicon-cog" aria-hidden="true"></span></button>'
		// + '</td>'
		// + '</table>');

}

$( document ).ready(function() {

	drawHeader();

});

