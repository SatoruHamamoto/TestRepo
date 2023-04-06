<%@ page contentType = "text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/gnomes_tag" prefix="gnomes" %>
<!DOCTYPE html>
<html lang="ja" lang="ja" xmlns="http://www.w3.org/1999/xhtml">

	<!-- ========== MODIFICATION HISTORY ========================== -->
	<!-- Release  Date       ID/Name                 Comment        -->
	<!-- R0.01.01 2017/05/09 30022467/K.Fujiwara     original       -->
	<!-- [END OF MODIFICATION HISTORY]                              -->
	<!-- ========================================================== -->
	<!-- ========== HEADER ======================================== -->
	<head>
		<meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
		<meta content="width=device-width, initial-scale=1" name="viewport">
		<meta content="0" http-equiv="Expires">
		<meta content="No-cache" http-equiv="Pragma">
		<meta content="private" http-equiv="Cache-Control">
		<title><fmt:message bundle="${GnomesResources}" key="DI01.0098"/></title>
		<!-- ========================================================== -->
		<!-- ========== SCRIPT ======================================== -->
		<script src="./js/jquery-2.1.4.min.js">
		</script>
		<script src="./js/bootstrap.min.js">
		</script>
		<!-- ========================================================== -->
		<!-- ========== CSS =========================================== -->
		<link href="./css/gnomes/common_gnomes.css" rel="stylesheet" type="text/css"/>
		<link href="./css/gnomes/bootstrap.min.css" rel="stylesheet">
		<link href="./css/gnomes/bootstrap-expo.css" rel="stylesheet">
<!-- ========================================================== -->
<!-- ========== MENU ========================================== -->
		 <script type="text/javascript">
		 </script>
		<style type="text/css">
		.auto-style1 {
			text-align: right;
		}
		</style>
	</head>
	<body class="MENU_BODY" >
		<div><fmt:message bundle="${GnomesResources}" key="DI01.0098"/></div>
		<p>${messageBean.message}</p><br>
		<p style="white-space: pre;">${messageBean.messageDetail}</p><br>
		<p>${messageBean.linkInfo[0]}</p>
		<a href="${messageBean.linkInfo[1]}" target="_blank">${messageBean.linkInfo[2]}</a><br>
		
 		<input type="button" onClick='window.close();' value="<fmt:message bundle="${GnomesResources}" key="YY01.0038"/>">
	</body>
<!-- ========================================================== -->
</html>
