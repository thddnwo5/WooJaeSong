<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/teamtwo/jstl_jquery_ajax.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>할수있다람쥐</title>
<style type="text/css">
.endPageBody {
	width: 100%
	display: inline-block;
	margin: auto;
	vertical-align: top;
	text-align: center;
}

.makeChalTitleFont {
	display: inline-block;
	vertical-align: baseline;
	font-size: 30px;
	font-family: 'JalnanOTF';
}


</style>
</head>
<body>
		<div style="height: 70px; margin: 0 0 15px 0;">
	<jsp:include page="/WEB-INF/views/teamtwo/header.jsp">
		<jsp:param name="user_id_email" value="${user_id_email}"/>
		<jsp:param name="user_nickname" value="${user_nickname}"/>
	</jsp:include>
	</div>
	<div class="endPageBody">
		<b class="makeChalTitleFont">${message }</b><br /><br>
		<img src="ban/img/daram1.png" width="400px">

	</div>


<div style="position: absolute; bottom: 0px; width: 99%;">
<%@ include file="/WEB-INF/views/teamtwo/footer.jsp" %>
</div>
</body>
</html>