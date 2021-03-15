<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Websocket Chat</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<style>
@font-face {
	font-family: 'yg-jalnan';
	src:
		url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_four@1.2/JalnanOTF00.woff')
		format('woff');
	font-weight: normal;
	font-style: normal;
}

@font-face {
	font-family: 'ChosunGu';
	src:
		url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_20-04@1.0/ChosunGu.woff')
		format('woff');
	font-weight: normal;
	font-style: normal;
}

[v-cloak] {
	display: none;
}

.roomItem {
	font-family: 'ChosunGu';
	font-size: 15px;
	line-height: 50px;
	background-color: #fcf0c9;
	border-radius: 5px;
	display: inline-block;
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
			<div class="col-md-12">
				<h3>채팅방 리스트</h3>
			</div>
		</div>
		<div class="input-group">
			<div class="input-group-prepend">
				<label class="input-group-text">방제목</label>
			</div>

			<input type="text" class="form-control" v-model="room_name"
				v-on:keyup.enter="createRoom">
			<div class="input-group-append">
				<button class="btn btn-primary" type="button" @click="createRoom">채팅방
					개설</button>
			</div>

		</div>
		<ul class="list-group">
			<li class="list-group-item list-group-item-action"
				v-for="item in chatrooms" v-bind:key="item.roomId"
				v-on:click="enterRoom(item.roomId, item.name)">
				<div class="roomItem">
					{{item.name}}<span class="badge badge-info badge-pill">{{item.userCount}}</span>
				</div>
			</li>
		</ul>
	</div>
	<!-- JavaScript -->
	<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
	<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
	<script>
        var vm = new Vue({
            el: '#app',
            data: {
                room_name : '',
                chatrooms: []
            },
            created() {
                this.findAllRoom();
            },
            methods: {
                findAllRoom: function() {
                    axios.get('/chat/rooms').then(response => { this.chatrooms = response.data; });
                },
                createRoom: function() {
                    if("" === this.room_name) {
                        alert("방 제목을 입력해 주십시요.");
                        return;
                    } else {
                        var params = new URLSearchParams();
                        params.append("name",this.room_name);
                        params.append("nickname",'${user_nickname}');
                        axios.post('/chat/room', params)
                        .then(
                            response => {
                                alert(response.data.name+"방 개설에 성공하였습니다.")
                                var roomName = this.room_name;
                                this.room_name = '';
                                this.findAllRoom();
                                var sender = '${user_nickname}';
                                var roomId = response.data.roomId;
                        		
                                if(sender != "") {
                                    localStorage.setItem('wschat.sender',sender);
                                    localStorage.setItem('wschat.roomId',roomId);
                                    localStorage.setItem('wschat.roomName',roomName);
                                    location.href="/chat/room/enter/"+roomId;
                                }
                            }
                        )
                        .catch( response => { alert("채팅방 개설에 실패하였습니다."); } );
                    }
                },
                enterRoom: function(roomId, roomName) {
                	alert(roomName);
                    var sender = '${user_nickname}';
                    if(sender != "") {
                        localStorage.setItem('wschat.sender',sender);
                        localStorage.setItem('wschat.roomId',roomId);
                        localStorage.setItem('wschat.roomName',roomName);
                        location.href="/chat/room/enter/"+roomId;
                    }
                }
            }
        });
    </script>
</body>
</html>