<%@ page contentType = "text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/gnomes_tag" prefix="gnomes" %>
<%@ page import="com.gnomes.common.constants.CommonConstants" %>
<!-- ================================================================== -->
<!DOCTYPE html>
<html lang="ja" lang="ja" xmlns="http://www.w3.org/1999/xhtml">
	<!-- ========== MODIFICATION HISTORY ========================== -->
	<!-- Release  Date       ID/Name                 Comment        -->
	<!-- R0.01.01 2016/11/22 YJP/Y.Oota              original       -->
	<!-- [END OF MODIFICATION HISTORY]                              -->
	<!-- ========================================================== -->
	<!-- ========== HEADER ======================================== -->
	<head>
		<meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
		<meta content="0" http-equiv="Expires">
		<meta content="No-cache" http-equiv="Pragma">
		<meta content="private" http-equiv="Cache-Control">
		<title>${A01001FormBean.screenTitle}</title>
		<!-- ========================================================== -->
		<!-- ========== SCRIPT ======================================== -->
		<script src="./js/jquery-2.1.4.min.js">
		</script>
		<script src="./js/navigation.js">
		</script>
		<!-- ========================================================== -->
		<!-- ========== CSS =========================================== -->
		<link href="./css/gnomes/common_gnomes.css" rel="stylesheet" type="text/css"/>
		<link href="./css/gnomes/bootstrap.min.css" rel="stylesheet"/>
		<link href="./css/gnomes/bootstrap-expo.css" rel="stylesheet"/>
		<link href="./css/gnomes/bootstrap-select.css" rel="stylesheet"/>

		
		<script src="./js/jquery.min.js"></script>
		<script src="./js/bootstrap-select.js"></script>
		<script src="./js/jquery-2.1.4.min.js"></script>
				
		<!-- 5. jQueryの読み込み-->
		<script src="./js/jquery.min.js"></script>
		<!-- 6. Bootstrapで使うJavaScriptの読み込み-->
		<script src="./js/bootstrap.min.js"></script>
		<script src="./js/gnomes/gnomes_common.js"></script>
		<!-- ========================================================== -->
		<!-- ========== MENU ========================================== -->
		<!-- ========================================================== -->
		<!-- ========== INPUT CHECK SCRIPT ============================ -->
		<!-- ========================================================== -->
		<!-- ========== USER SCRIPT =================================== -->
		<!-- ========================================================== -->
	</head>
	<!-- ========== CUSTOMIZE ===================================== -->
	<body class="MENU_BODY" onkeydown="gbgf_bodyonkeydown();" >
		<form name="main" action="<%= CommonConstants.REDIRECT_URL %>" method="post">
			<input type="hidden" name="command" value="A01001C002">
			<input type="hidden" name="jsonSaveSearchInfos" value="">
			
			<!-- ロケール設定 -->
			<fmt:setLocale value="${A01001FormBean.systemLocale}"/>
			<input type="hidden" name="userId" value="<fmt:message bundle="${GnomesResources}" key="DI01.0008"/>">
			<input type="hidden" name="login" value="<fmt:message bundle="${GnomesResources}" key="DI01.0010"/>">
			<input type="hidden" name="passwordChange" value="<fmt:message bundle="${GnomesResources}" key="DI01.0011"/>">
			<input type="hidden" name="oldPassword" value="<fmt:message bundle="${GnomesResources}" key="DI01.0012"/>">
			<input type="hidden" name="newPassword" value="<fmt:message bundle="${GnomesResources}" key="DI01.0013"/>">
			<input type="hidden" name="newPasswordConfirm" value="<fmt:message bundle="${GnomesResources}" key="DI01.0014"/>">
			<input type="hidden" name="errMessageNoSession" value="${A01001FormBean.errMessageNoSession}">
			<input type="hidden" name="dateFormat" value="<fmt:message bundle="${GnomesResources}" key="YY01.0045"/>" >
		
			<input type="hidden" name="computerName" value=""/>
			<input type="hidden" name="computerList" value="${A01001FormBean.computerList}"/>
			<input type="hidden" name="computerInfoMap" value='<gnomes:GnomesCTagOutJson bean="${A01001FormBean}" paramName="computerInfoMap" />' />
			<input type="hidden" name="siteList" value='<gnomes:GnomesCTagOutJson bean="${A01001FormBean}" paramName="siteList" />'/>
		
		    <!-- ページトークン -->
			<gnomes:GnomesCTagPageToken bean="${BaseFormBean}" />
			
			<table>
				<tr>
					<td id="root">
					</td>
				</tr>
				<tr>
					<td>
						<div id="errorLocation" style="font-size: small;color: Gray;"/></div>
					</td>
				</tr>
			</table>
		
			<!-- BEGIN # BOOTSNIP INFO -->
			<div class="container">
				<div class="row">
					<h1 class="text-center"><fmt:message bundle="${GnomesResources}" key="DI01.0019"/></h1>
					<p class="text-center"><a href="#" id="loginbtn" class="btn btn-primary btn-lg" role="button" data-toggle="modal" data-target="#login-modal"><fmt:message bundle="${GnomesResources}" key="DI01.0010"/></a></p>
				</div>
			</div>
			<!-- END # BOOTSNIP INFO -->
		
			<!-- BEGIN # MODAL LOGIN -->
			<div class="modal" id="login-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-show="true" data-keyboard="false" data-backdrop="static" style="display: inherit;">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header" align="center">
							<h1  class="text-center"><fmt:message bundle="${GnomesResources}" key="DI01.0020"/></h1>
						</div>
		
						<!-- Begin # DIV Form -->
						<div id="div-forms">
							<div class="modal-body">
								<div id="div-login-msg">
									<div id="icon-login-msg" class="glyphicon glyphicon-chevron-right"></div>
									<span id="text-login-msg"><fmt:message bundle="${GnomesResources}" key="DI01.0021"/></span>
								</div>
								<!-- ユーザID -->
								<input id="login_username" class="form-control" type="text" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0008"/>" value="" autofocus>
								<!-- パスワード -->
								<input id="login_password" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0009"/>" value="${A01001FormBean.password}">
	
								<fieldset id="loginOptionToggle" onclick="$('#loginOption').slideToggle() ">
									<h5 style="border-top: 1px solid #DDDDDD; margin:10px 0 0; text-align:left; padding:0; height:10px; ">
										<span style="position: relative; top: -9px; padding-right:3px; background:white;">
											<span class="glyphicon glyphicon-menu-down"></span>
											<fmt:message bundle="${GnomesResources}" key="DI01.0016"/>
											</span>
									</h5>
								</fieldset>
								<!-- オプション -->
								<div id="loginOption" class="collapse">
									<!-- 言語選択 -->
								    <span id="text-login-msg"><fmt:message bundle="${GnomesResources}" key="DI01.0017"/></span><br>
									  <select id="locale" class="selectpicker form-control">
										<c:forEach var="localeList" items="${A01001FormBean.localeList}" begin="0">
											<option <c:if test="${ localeList.name == A01001FormBean.systemLocale }"> selected</c:if> value="${localeList.value}">${localeList.name}</option>
										</c:forEach>
									  </select>
									  <br>
									<!-- 拠点選択 -->
	  							    <span id="text-login-msg"><fmt:message bundle="${GnomesResources}" key="DI01.0018"/></span><br>
									  <select id="site" class="selectpicker form-control">
										<c:forEach var="siteList" items="${A01001FormBean.siteList}" begin="0">
											<option <c:if test="${ siteList.name == A01001FormBean.siteCode }"> selected</c:if> value="${siteList.value}">${siteList.name}</option>
										</c:forEach>
									  </select>
                                    <!-- 領域区分選択 -->
                                    <span id="text-login-msg">領域区分</span><br>
                                    <select id="regionType" class="selectpicker form-control">
                                        <option value="1" selected="selected">通常領域</option>
                                        <option value="2">保管領域</option>
                                    </select>
								</div>
							</div>
							<div class="modal-footer">
								<!-- ログイン -->
								<div>
									<button id="certify-button" type="button" class="btn btn-primary btn-lg btn-block" ><fmt:message bundle="${GnomesResources}" key="DI01.0010"/></button>
								</div>
							</div>
						</div>
						<!-- End # DIV Form -->
					</div>
				</div>
			</div>
			
			<!-- パスワード変更ダイアログ -->
		    <div class="modal" id="change-pass-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-show="true" data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header" align="center">
							
							<button type="button" class="close" onclick="MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0015"/>','<fmt:message bundle="${GnomesMessages}" key="MC01.0001"/>','<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','','isSelectedComputer($(\'#login_username\').val());');" data-toggle="modal" data-target="#modal-example">
								<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
							</button>
							<h1  class="text-center"><fmt:message bundle="${GnomesResources}" key="DI01.0011"/></h1>
						</div>
						<div id="div-forms">
							<div class="modal-body">
								<div style="float:left; width:20%; padding-top:17px;"><fmt:message bundle="${GnomesResources}" key="DI01.0008"/></div>
								<input id="change_user_id" class="form-control" type="text" style="width:80%;" readonly="readonly" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0008"/>" value="">
								<input id="login_old_password" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0012"/>" value="${A01001FormBean.password}">
								<input id="login_new_password" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0013"/>" value="${A01001FormBean.newPassword}">
								<input id="login_new_password_confirm" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0014"/>" value="${A01001FormBean.newConfirmPassword}">
							</div>
							<div class="modal-footer">
								<div>
									<button id="password-change-button" type="button" class="btn btn-primary btn-lg btn-block" onclick="openWindowToMrgJsp('PRODINSLIST');"><fmt:message bundle="${GnomesResources}" key="DI01.0010"/></button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- END # MODAL CHANGE PASSWORD -->
			
			<!-- 端末選択ダイアログ -->
		    <div class="modal" id="select-pulldown-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-show="true" data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<h1  class="text-center"><fmt:message bundle="${GnomesResources}" key="DI01.0078"/></h1>
						</div>
						<div id="div-forms">
							<div class="modal-body">
								<div id="div-login-msg">
									<div id="icon-login-msg" class="glyphicon glyphicon-chevron-right"></div>
									<span id="text-login-msg"><fmt:message bundle="${GnomesMessages}" key="ME01.0036"/></span>
								</div>
								<table style="width: 100%;">
									<tbody>
										<!-- ユーザID -->
										<tr>
											<td>
												<div style="float:left; margin-top: 10px;"><fmt:message bundle="${GnomesResources}" key="DI01.0008"/></div>
											</td>
											<td>
												<input id="pulldown_userid" class="form-control" type="text" readonly="readonly" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0008"/>" value="${gnomesSessionBean.userId}">
											</td>
										</tr>
											<!-- プルダウン -->
										<tr>
											<td>
												<div id="text-pulldown-item" style="float:left; margin-top: 10px;"><fmt:message bundle="${GnomesResources}" key="DI01.0079"/></div>
											</td>
											<td style="padding-top: 12px;">
												<select id="computer" class="selectpicker form-control">
													<c:forEach var="computerList" items="${A01001FormBean.computerList}" begin="0">
														<option>${computerList.value}</option>
													</c:forEach>
												</select>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-primary" onclick="saveUserComputer();">決定</button>
						</div>
					</div>
				</div>
			</div>
					
			<!-- メッセージダイアログ -->
			<div class="modal" id="myModal" tabindex="-1" role="dialog" aria-labelledby="staticModalLabel" aria-hidden="true" data-show="true" data-keyboard="false" data-backdrop="static">
			</div> <!-- /.modal -->
			<div class="modal" id="myAnyModal" tabindex="-1" role="dialog" aria-labelledby="staticModalLabel" aria-hidden="true" data-show="true" data-keyboard="false" data-backdrop="static">
			</div> <!-- /.modal -->
			
			<!-- ダイアログ 文字 -->
			<div style="display: none;">
				<!-- 中止 -->
				<p id="msgBoxBtnNameAbort"><fmt:message bundle="${GnomesResources}" key="YY01.0016"/></p>
				<!-- 再試行 -->
				<p id="msgBoxBtnNameRetry"><fmt:message bundle="${GnomesResources}" key="YY01.0017"/></p>
				<!-- 無視 -->
				<p id="msgBoxBtnNameIgnore"><fmt:message bundle="${GnomesResources}" key="YY01.0018"/></p>
				<!-- OK -->
				<p id="msgBoxBtnNameOk"><fmt:message bundle="${GnomesResources}" key="YY01.0019"/></p>
				<!-- Cancel -->
				<p id="msgBoxBtnNameCancel"><fmt:message bundle="${GnomesResources}" key="YY01.0020"/></p>
				<!-- Yes -->
				<p id="msgBoxBtnNameYes"><fmt:message bundle="${GnomesResources}" key="YY01.0021"/></p>
				<!-- No -->
				<p id="msgBoxBtnNameNo"><fmt:message bundle="${GnomesResources}" key="YY01.0022"/></p>
				<!-- 発生者： -->
				<p id="msgBoxLabelOccrUserName"><fmt:message bundle="${GnomesResources}" key="DI01.0042"/></p>
				<!-- 発生場所： -->
				<p id="msgBoxLabelOccrHost"><fmt:message bundle="${GnomesResources}" key="DI01.0043"/></p>
				<!-- メッセージ詳細： -->
				<p id="msgBoxLabelmessageDetal"><fmt:message bundle="${GnomesResources}" key="DI01.0044"/></p>
				<!-- 閉じる -->
				<p id="msgBoxBtnClose"><fmt:message bundle="${GnomesResources}" key="YY01.0038"/></p>
				<!-- タイトル：エラー -->
				<p id="msgBoxTitleError"><fmt:message bundle="${GnomesResources}" key="YY01.0007"/></p>
				<!-- タイトル：確認 -->
				<p id="msgBoxTitleConfirm"><fmt:message bundle="${GnomesResources}" key="YY01.0015"/></p>
				<!-- タイトル：警告 -->
				<p id="msgBoxTitleWarning"><fmt:message bundle="${GnomesResources}" key="YY01.0036"/></p>
				<!-- タイトル：情報 -->
				<p id="msgBoxTitleInfo"><fmt:message bundle="${GnomesResources}" key="YY01.0037"/></p>
				<!-- タイトル：メッセージ -->
				<p id="msgBoxTitleMessage"><fmt:message bundle="${GnomesResources}" key="YY01.0006"/></p>
			</div>
			
			<!-- ========================================================== -->
			<!-- ========== FOOTER ======================================== -->
			<script>
				CreateFooter();
				$("#loginbtn").click();
				
			<!-- ========================================================== -->
			<!-- ========== LOGIN ========================================= -->
				// add<R1.01.01>----------------------------------------
				// ログイン POST メソッド
                $("#certify-button").click(function() {
                    $.ajax({
                        type: 'POST',
                        contentType: 'application/json',
                        url: "rest/A01001S000/setSessionBean",
                        async: true,
                        dataType: "json",
                        data: formToCertifyJSON(),
                        success: function(data, textStatus, jqXHR) {
                            if(data.isSuccess == 1) {
                                $.ajax({
                                    type: 'POST',
                                    contentType: 'application/json',
                                    url: "rest/A01001S001/Certify",
                                    async: true,
                                    dataType: "json",
                                    data: formToCertifyJSON(),
                                    success: function(data, textStatus, jqXHR){
                                        if(data.isSuccess == 1){
                                            if(data.isChangePassword == 1){
                                                // パスワード変更画面表示
                                                MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0036"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','');
                                                $('#myModal').on('hidden.bs.modal', function () {
                                                    $('#login-modal').modal('hide');
                                                    $('#change_user_id').val($('#login_username').val());
                                                    $('#change-pass-modal').modal('show');
                                                    $('#change-pass-modal').on('shown.bs.modal', function () {
                                                        // 初期表示フォーカス設定
                                                        $('#login_old_password').focus();
                                                    }).modal();
                                                }).modal();
                                                return;
                                            }
                                            else {
                                                // LDAP認証時、パスワード変更画面を表示しない
                                                if(data.message){
                                                    MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0036"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                                                }
                                            }
                                            
                                            // 端末情報未登録時、端末選択ダイアログ表示
                                            isSelectedComputer(data.userId);
                                        }
                                        else {
                                            if(data.message == $('[name=errMessageNoSession]').val()){
                                                window.location.reload();
                                            }
                                            else{
                                                MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','','', data.linkInfo);
                                            }
                                        }
                                    },
                                    error: function(jqXHR, textStatus, errorThrown){
                                        MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                                    }
                                });
                            }
                        
                        }
                    });
                });
				
				// ログイン情報をJSONデータに格納
				function formToCertifyJSON() {
				    return JSON.stringify({
				        "userId": $('#login_username').val(),
				        "password": $('#login_password').val(),
				        "localeId": $('#locale').val(),
				        "siteCode": $('#site').val(),
				        "regionType": $('#regionType').val(),
				        // 端末ID仮設定
				        "computerId": "CPC035-01213-10"
				    });
				}

			<!-- ========================================================== -->
			<!-- ========== CHANGE PASSWORD =============================== -->
				// パスワード変更 POST メソッド
				$("#password-change-button").click(function() {
					$.ajax({
						type: 'POST',
						contentType: 'application/json',
						url: "rest/A01001S001/ChangePassword",
						async: true,
						dataType: "json",
						data: formToChangePasswordJSON(),
						success: function(data, textStatus, jqXHR){
							if(data.isSuccessChange == false){
								MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','','', data.linkInfo);
							}
							else {
								// 端末情報未登録時、端末選択ダイアログ表示
								isSelectedComputer(data.userId);
								
							}
						},
						error: function(jqXHR, textStatus, errorThrown){
							MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
						}
					});
				});
				
				// パスワード変更情報をJSONデータに格納
				function formToChangePasswordJSON() {
				    return JSON.stringify({
				        "userId": $('#login_username').val(),
				        "password": $('#login_old_password').val(),
				        "newPassword": $('#login_new_password').val(),
				        "newPasswordConfirm": $('#login_new_password_confirm').val(),
				        "isInitPassword":0
				    });
				}
				
				// ----------------------------------------add<R1.01.01>
			<!-- ========================================================== -->
				
				if(${gnomesSessionBean.userId!=null}){
					$('#login-modal').modal('hide');
					$('#select-pulldown-modal').modal('show');
					$('#pulldown_user_id').val('${gnomesSessionBean.userId}');
					
				}
				
				function isSelectedComputer(userId){
					// 端末情報未登録時、端末選択ダイアログ表示
					if(getLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>')==null){
						document.main.command.value="A01001C003";
						document.main.jsonSaveSearchInfos.value = getJsonSaveSearchInfos(userId);
						document.main.submit();
					}
					else{
						$('[name=computerName]').val(getLocalStorage( '<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>'));
						document.main.jsonSaveSearchInfos.value = getJsonSaveSearchInfos(userId);
						document.main.command.value=$('[name=command]').val();
						document.main.submit();
					}
				}

				
				function saveUserComputer(){
					if($('#computer').val() != null && $('#computer').val() != ""){
						saveLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>', $('#computer').val());
						$('[name=computerName]').val($('#computer').val());
						document.main.jsonSaveSearchInfos.value = getJsonSaveSearchInfos($('#pulldown_user_id').val());
						document.main.command.value=$('[name=command]').val();
						document.main.submit();
					}
					else{
						MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0036"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
					}
				}

				//===================================================================
				/**
				 * [機能]	localStorageに保存している検索情報を取得
				 * [引数]	userId		ユーザID
				 * [戻値]	結果JSON
				 */
				//===================================================================
				function getJsonSaveSearchInfos(userId) {
					var key = makeLocalStrageKey(userId, '<fmt:message bundle="${GnomesResources}" key="YY01.0048"/>');
					var datas = getLocalStorages(key);
					var ret = JSON.stringify(datas);
					return ret;
				}

				// 初期表示時、拠点プルダウンの選択状態を端末情報を元に設定する。
				$(document).ready(function(){
					// ローカルストレージ.端末名取得
					var computerName = getLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>');
					var siteCode = '';
					// ローカルストレージに端末が保存されている場合
					if (computerName != null && computerName != '') {
			
						// 端末リスト
						if ($('[name=computerInfoMap]').val()!=null){
			
							// 対象の端末情報から拠点コードを取得
							var computerInfoMap = JSON.parse($('[name=computerInfoMap]').val());
							siteCode = computerInfoMap[computerName];
							var str = "";
			
							var siteList = JSON.parse($('[name=siteList]').val());
							
							for(var i=0; i<siteList.length; i++){
								// 端末情報の拠点コードと一致するデータがある場合
								if(siteList[i].name == siteCode){
									str += "<option selected value=\""+siteList[i].value+"\">"+ siteList[i].name +"</option>";
								}
								else{
									str += "<option value=\""+siteList[i].value+"\">"+ siteList[i].name +"</option>";
								}
							}
							$("#site").html(str);
						}
					}
				});	

			</script>
			<input type="hidden" name="screenId" value="${A01001FormBean.screenId}" />
			<input type="hidden" name="screenName" value="${A01001FormBean.screenName}" />
		</form>
	</body>
	<!-- ========================================================== -->
</html>
