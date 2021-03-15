<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/teamtwo/jstl_jquery_ajax.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>할수있다람쥐</title>
<style type="text/css">
h2{
   font-family: 'JalnanOTF';
   text-align: center;
}

table{
   width:1800px;
   margin: 0 auto;
   border-collapse: collapse;
}

th {
    background-color: palegoldenrod;
    font-size: 16px;
    font-family: 'JalnanOTF';
    color: #515543;
    text-align: center;
    height: 60px;
}

td {
    background-color: #FBF5EC;
    font-size: 14px;
    font-family: 'ChosunGu';
    color: #515543;
    font-weight: bold;
    text-align: center;
    line-height: 50px;
}

#stop{
   background-color: peachpuff;
    border: none;
    font-family: 'ChosunGu';
    font-weight: bold;
    height: 20px;
    border-radius: 5px;
    cursor: pointer;
}

#go{
   background-color: palegoldenrod;
    border: none;
    font-family: 'ChosunGu';
    font-weight: bold;
    height: 20px;
    border-radius: 5px;
    cursor: pointer;

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

   <h2>챌린지 목록</h2>
<div id="ch">
<table>
   <tr><th style="border-radius: 30px 0 0 0;">챌린지 등록번호</th><th>대분류번호</th><th>중분류번호</th><th>소분류번호</th><th>챌주 아이디</th><th>챌주 닉네임</th>
   <th>챌린지 등록일</th><th>챌린지 시작일</th><th>챌린지 종료일</th><th>참여정원 수</th><th>챌린지제목</th>
   <th>챌린지 추천수</th><th>챌린지 상태</th><th>현재 참여인원 수</th><th style="border-radius: 0 30px 0 0;">관리자권한</th></tr>
   <c:forEach var="ch" items="${listCh }">
      <tr>
         <td>${ch.chg_num }</td>
         <td>${ch.lc_num }</td>
         <td>${ch.mc_num }</td>
         <td>${ch.sc_num }</td>
         <td>${ch.chg_master }</td>
         <td>${ch.master_nickname }</td>
         <td>${ch.chg_regdate }</td>
         <td>${ch.chg_startdate }</td>
         <td>${ch.chg_enddate }</td>
         <td>${ch.chg_capacity }</td>
         <td>${ch.chg_title }</td>
         
         <td>${ch.chg_like_count }</td>
         <td>${ch.chg_state }</td>
         
         
         <td>${ch.chg_now_parti }</td>
         <td><input type="button" id="stop" value="챌린지중단" onclick="location.href='updateCh?chg_num=${ch.chg_num}'">
         <input type="button" id="go" value="포인트회수" onclick="location.href=''">
         </tr>
   </c:forEach>
</table>
</div>

<div>
<%@ include file="/WEB-INF/views/teamtwo/footer.jsp" %>
</div>
</body>
</html>