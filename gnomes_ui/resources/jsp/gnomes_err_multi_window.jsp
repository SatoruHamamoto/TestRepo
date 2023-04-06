<%@ page contentType = "text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- ================================================================== -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<!-- ================================================================== -->
<!-- ========== SCRIPT ================================================ -->
<script src="./js/jquery-2.1.4.min.js"></script>
<!-- ================================================================== -->
<!-- ========== CSS =================================================== -->
<link href="../css/bootstrap.min.css" rel="stylesheet"></link>
<link href="../css/bootstrap-expo.css" rel="stylesheet"></link>
<link href="../css/common_gnomes.css" rel="stylesheet"></link>
<script src="../js/bootstrap.min.js"></script>
<script src="../js/jquery-2.1.4.min.js"></script>
<!-- ================================================================== -->

<!-- == ヘッダー ========================================================== -->
<!-- ================================================================== -->
<title>GNOMES システム エラー</title>
</head>
<!-- ================================================================== -->
<div>GNOMES システム エラー</div>
<p><fmt:message bundle="${GnomesMessages}" key="ME01.0147"/></p><br>
 <input type="button" onClick='window.close();' value="<fmt:message bundle="${GnomesResources}" key="YY01.0038"/>">
</html>
