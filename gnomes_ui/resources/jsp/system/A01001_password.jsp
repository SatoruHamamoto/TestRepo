<%@ page contentType = "text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/gnomes_tag" prefix="gnomes" %>
<!-- ================================================================== -->
<!DOCTYPE html>
<html lang="ja" lang="ja" xmlns="http://www.w3.org/1999/xhtml">
	<!-- ========== MODIFICATION HISTORY =================================== -->
	<!-- Release  Date       ID/Name                 Comment        		 -->
	<!-- R0.01.01 2016/11/22 YJP/Y.Oota              original				 -->
	<!-- R0.01.01 2016/12/19 YJP/A.Oomori            パスワード変更処理追加	 -->
	<!-- [END OF MODIFICATION HISTORY]                             			 -->
	<!-- =================================================================== -->
	<!-- ========== HEADER ================================================= -->
	<head>
		<meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
		<meta content="0" http-equiv="Expires">
		<meta content="No-cache" http-equiv="Pragma">
		<meta content="private" http-equiv="Cache-Control">
		<!-- ========================================================== -->
		<!-- ========== SCRIPT ======================================== -->
		<script src="./js/jquery-2.1.4.min.js">
		</script>
		<script src="./js/buttonDef.js">
		</script>
		<script src="./js/navigation.js">
		</script>
		<script src="./js/objectManipulation.js">
		</script>
		<script src="./js/dataCheck.js">
		</script>
		<script src="./js/dataUtil.js">
		</script>
		<script src="./js/timeManipulation.js">
		</script>
		<script src="./js/svg.js">
		</script>
		<script src="./js/common.js">
		</script>
		<script src="./js/gb_gf_ezg.js">
		</script>
		<script src="./js/gb_gf_ezg_xaml.js">
		</script>
		<script src="./js/xaml.js">
		</script>
		<script src="./js/login.js">
		</script>
		<!-- ========================================================== -->
		<!-- ========== CSS =========================================== -->
		<link href="./css/gnomes/common_gnomes.css" rel="stylesheet" type="text/css"/>
		<link href="./css/gnomes/bootstrap.min.css" rel="stylesheet"/>
		<link href="./css/gnomes/bootstrap-expo.css" rel="stylesheet"/>
		<link href="./css/gnomes/bootstrap-select.css" rel="stylesheet"/>

		<script src="./js/jquery.min.js"></script>
		<script src="./js/bootstrap-modal.js"></script>
		<script src="./js/bootstrap-transition.js"></script>
		<script src="./js/bootstrap-select.js"></script>
		<script src="./js/jquery-2.1.4.min.js"></script>
				
		<!-- 5. jQueryの読み込み-->
		<script src="./js/jquery.min.js"></script>
		<!-- 6. Bootstrapで使うJavaScriptの読み込み-->
		<script src="./js/bootstrap.min.js"></script>
		<script src="./js/gnomes/gnomes_common.js">
		<title>${A01001FormBean.screenTitle}</title>
		<!-- ========================================================== -->
		<!-- ========== MENU ========================================== -->
		<!-- ========================================================== -->
		<!-- ========== INPUT CHECK SCRIPT ============================ -->
		<!-- ========================================================== -->
		<!-- ========== USER SCRIPT =================================== -->
		<!-- ========================================================== -->
		<title>dummytitle</title>
	</head>
	<!-- ========== CUSTOMIZE ===================================== -->
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
			<h1 class="text-center">GNOMESへようこそ</h1>
			<p class="text-center"><a href="#" id="loginbtn" class="btn btn-primary btn-lg" role="button" data-toggle="modal" data-target="#login-modal">ログイン</a></p>
		</div>
	</div>
	<!-- END # BOOTSNIP INFO -->

	<!-- BEGIN # MODAL LOGIN -->
	<div class="modal fade" id="login-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: inherit;">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">
					
					<button type="button" class="close" onclick="GnomesSendBTN('<fmt:message bundle="${GnomesResources}" key="YY01.0015"/>','<fmt:message bundle="${GnomesMessages}" key="MC01.0001"/>','<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','');" data-toggle="modal" data-target="#modal-example">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
					</button>
					<h1  class="text-center"><fmt:message bundle="${GnomesResources}" key="DI01.0011"/></h1>
				</div>

				<!-- Begin # DIV Form -->
				<div id="div-forms">

					<!-- Begin # Login Form -->
					<form id="login-form">
						<div class="modal-body">
							<div style="float:left; width:20%; padding-top:17px;"><fmt:message bundle="${GnomesResources}" key="DI01.0008"/></div>
							<input id="login_username" class="form-control" type="text" style="width:80%;" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0011"/>" value="${A01001FormBean.userId}">
							<input id="login_old_password" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0012"/>" value="${A01001FormBean.password}">
							<input id="login_new_password" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0013"/>" value="${A01001FormBean.newPassword}">
							<input id="login_new_password_confirm" class="form-control" type="password" placeholder="<fmt:message bundle="${GnomesResources}" key="DI01.0014"/>" value="${A01001FormBean.newConfirmPassword}">
						</div>
						<div class="modal-footer">
							<div>
								<button id="button" type="button" class="btn btn-primary btn-lg btn-block" onclick="openWindowToMrgJsp('PRODINSLIST');"><fmt:message bundle="${GnomesResources}" key="DI01.0010"/></button>
							</div>
						</div>
					</form>
				</div>
				<!-- End # DIV Form -->
			</div>
		</div>
	</div>
	<!-- END # MODAL CHANGE PASSWORD -->

	<!-- Modal メッセージダイアログ -->
	<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	</div>
	<!-- モーダルダイアログ -->
	<div class="modal" id="myAnyModal" tabindex="-1" role="dialog" aria-labelledby="staticModalLabel" aria-hidden="true" data-show="true" data-keyboard="false" data-backdrop="static">
	</div> <!-- /.modal -->
	
	<!-- ========================================================== -->
	<!-- ========== FOOTER ======================================== -->
	<script>
		CreateFooter();
		$("#loginbtn").click();
		
	</script>
	<!-- ========================================================== -->
	<!-- ========== CHANGE PASSWORD =============================== -->
	<script>
		// add<R1.01.01>----------------------------------------
		// パスワード変更 POST メソッド
		$("#button").click(function() {
		  $.ajax({
		      type: 'POST',
		      contentType: 'application/json',
		      url: "rest/A01001S001/ChangePassword",
		      async: true,
		      dataType: "json",
		      data: formToChangePasswordJSON(),
		      success: function(data, textStatus, jqXHR){
		          if(data.isSuccessChange == false){
		              MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
		      	  }
		      	  else {
		              MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0006"/>', '変更しました。', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
		              $('#login-modal').modal('hide');
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
		        });
		}
		// ----------------------------------------add<R1.01.01>

	</script>
	<!-- ========================================================== -->
</html>
