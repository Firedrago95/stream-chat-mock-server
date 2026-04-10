package io.stream.stream_chat_mock_server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class MockChzzkHandler extends TextWebSocketHandler {

    private final AtomicLong messageCount = new AtomicLong(0);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Mock 치지직] 엔진 연결 성공 Session ID: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("[Mock 치지직] 엔진으로부터 수신된 메시지: {}", payload);

        if (payload.contains("\"cmd\":10000") || payload.contains("subscribe")) {
            String ackResponse = "{\"cmd\":10000, \"retCode\":0, \"retMsg\":\"SUCCESS\"}";
            session.sendMessage(new TextMessage(ackResponse));
            log.info("[Mock 치지직] 구독 성공 응답 발송 완료. 채팅 데이터 전송을 시작합니다.");

            Thread.startVirtualThread(() -> pumpChatData(session));
        }
    }

    private void pumpChatData(WebSocketSession session) {
        try {
            while (session.isOpen()) {
                long count = messageCount.incrementAndGet();
                long currentTime = System.currentTimeMillis();

                String chatJson = """
                    {
                      "cmd": 93101,
                      "svcid": "game",
                      "cid": "mock_channel_123",
                      "tid": "1",
                      "sid": "mock_session_id",
                      "bdy": [
                        {
                          "profile": "{\\\\"userIdHash\\\\":\\\\"hash_%d\\\\",\\\\"nickname\\\\":\\\\"더미시청자%d\\\\",\\\\"profileImageUrl\\\\":\\\\"\\\\",\\\\"badge\\\\":{},\\\\"title\\\\":{}}",
                          "extras": "{}",
                          "msg": "테스트 화력 펌핑 %d",
                          "msgTypeCode": 1,
                          "msgTime": %d
                        }
                      ]
                    }
                    """.formatted(count, count, count, currentTime);

                session.sendMessage(new TextMessage(chatJson));

                Thread.sleep(1);
            }
        } catch (IOException e) {
            log.warn("[Mock 치지직] 엔진과의 연결이 끊어졌습니다. 전송 중단.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[Mock 치지직] 엔진 연결 종료. Status: {}", status);
    }
}
