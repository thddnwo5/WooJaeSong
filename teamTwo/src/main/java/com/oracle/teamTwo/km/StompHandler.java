package com.oracle.teamTwo.km;

import java.security.Principal;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.oracle.teamTwo.km.dao.ChatRoomRepository;
import com.oracle.teamTwo.km.model.ChatMessage;
import com.oracle.teamTwo.km.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatService chatService;

	private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOpsEnterInfo;

	// websocket을 통해 들어온 요청이 처리 되기전 실행된다.
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청

		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
			// header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
			String roomId = chatService.getRoomId(
					Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
			// 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
			String sessionId = (String) message.getHeaders().get("simpSessionId");
			chatRoomRepository.setUserEnterInfo(sessionId, roomId);
			// 채팅방의 인원수를 +1한다.
			chatRoomRepository.plusUserCount(roomId);
			// 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
			String nickname = hashOpsEnterInfo.get(CHAT_ROOMS, "nickname");
			chatService.sendChatMessage(
					ChatMessage.builder().type(ChatMessage.MessageType.ENTER).roomId(roomId).sender(nickname).build());
			log.info("SUBSCRIBED {}, {}",sessionId, nickname, roomId);
		
		} else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
			// 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
			String sessionId = (String) message.getHeaders().get("simpSessionId");
			String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
			// 채팅방의 인원수를 -1한다.
			chatRoomRepository.minusUserCount(roomId);
			// 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
			String nickname = hashOpsEnterInfo.get(CHAT_ROOMS, "nickname");
			chatService.sendChatMessage(
					ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(nickname).build());
			// 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
			chatRoomRepository.removeUserEnterInfo(sessionId);
			log.info("DISCONNECTED {}, {}",sessionId, nickname, roomId);
		}
		return message;
	}
}
