package io.stream.stream_chat_mock_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MockApiController {

    // 엔진이 방송 목록을 동기화할 때 (404 방지)
    @PostMapping("/sync")
    public ResponseEntity<Void> sync() {
        log.info("[Mock API] 엔진으로부터 /sync 요청 수신 -> 200 OK 응답");
        return ResponseEntity.ok().build();
    }

    // 엔진이 하이라이트 신호를 보낼 때 (404 방지)
    @PostMapping("/signal")
    public ResponseEntity<Void> signal() {
        log.info("[Mock API] 엔진으로부터 /signal 요청 수신 -> 200 OK 응답");
        return ResponseEntity.ok().build();
    }
}
