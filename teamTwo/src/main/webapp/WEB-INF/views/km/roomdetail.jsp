<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<style>
[v-cloak] {
	display: none;
}
</style>
</head>
<body>
	<div style="height: 70px; margin: 0 0 15px 0;">
		<jsp:include page="/WEB-INF/views/teamtwo/header.jsp">
			<jsp:param name="user_id_email" value="${user_id_email}" />
			<jsp:param name="user_nickname" value="${user_nickname}" />
		</jsp:include>
	</div>

	<input type="hidden" name="user_id_email" value="${user_id_email}">
	<input type="hidden" name="user_nickname" value="${user_nickname}">
	<input type="hidden" name="user_image" value="${user_image}">
	
	
	<div class="container" id="app" v-cloak>
		<div class="row">
			<div class="col-md-6">
				<h3>
					{{roomName}}<span class="badge badge-info badge-pill">{{userCount}}</span>
				</h3>
			</div>
			<div class="col-md-6 text-right">
				<a class="btn btn-info btn-sm" href="/chat/room">채팅방 나가기</a>
			</div>
		</div>
		<div class="input-group">
			<div class="input-group-prepend">
				<label class="input-group-text">내용</label>
			</div>
			<input type="text" class="form-control" v-model="message"
				v-on:keypress.enter="sendMessage('TALK')">
			<div class="input-group-append">
				<button class="btn btn-primary" type="button"
					@click="sendMessage('TALK')">보내기</button>
			</div>
		</div>
		<ul class="list-group">
			<li class="list-group-item" v-for="message in messages">
				{{message.sender}} - {{message.message}}</li>
		</ul>
	</div>
	<!-- JavaScript -->
	<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
	<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
	<script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
	<script src="/webjars/stomp-websocket/2.3.3/stomp.min.js"></script>
	<script>
        //alert(document.title);
        // websocket & stomp initialize
        var sock = new SockJS("/km-websocket");
        var ws = Stomp.over(sock);
        var reconnect = 0;
        // vue.js
        var vm = new Vue({
            el: '#app',
            data: {
                roomId: '',
                roomName: '',
                sender: '',
                message: '',
                messages: [],
                userCount: 0
            },
            created() {
                this.roomId = localStorage.getItem('wschat.roomId');
                this.roomName = localStorage.getItem('wschat.roomName');
                this.sender = localStorage.getItem('wschat.sender');
                this.findRoom();
                var _this = this;
                ws.connect({}, function(frame) {
                    ws.subscribe("/sub/chat/room/"+_this.roomId, function(message) {
                        var recv = JSON.parse(message.body);
                        _this.recvMessage(recv);
                    });
             
                }, function(error) {
                	alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
                    location.href="/chat/room";
                });
            },
            methods: {
                findRoom: function() {
                    axios.get('/chat/room/'+this.roomId).then(response => { this.room = response.data; });
                },
                sendMessage: function() {
                    ws.send("/pub/chat/message", {}, JSON.stringify({type:'TALK', roomId:this.roomId, sender:this.sender, message:this.message}));
                    this.message = '';
                },
                recvMessage: function(recv) {
                	this.userCount = recv.userCount;
                    this.messages.unshift({"type":recv.type,"sender":recv.sender,"message":recv.message})
                }
            }
        });

   
    </script>
</body>
</html>