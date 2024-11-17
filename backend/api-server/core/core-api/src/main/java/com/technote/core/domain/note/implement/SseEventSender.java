package com.technote.core.domain.note.implement;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEventSender {
    private final SseEmitterManager sseEmitterManager;

    public void sendConnectEvent(String connectId)   {
        try {
            SseEmitter emitter = sseEmitterManager.getEmitter(connectId);
            var event = SseEmitter.event()
                    .name("connect")
                    .data("send connect event");
            emitter.send(event);
            log.info("emitter sent first event connectId: {}", connectId);
        } catch (IOException e) {
            log.error("SseEmitter error", e);
        }
    }

    public void sendNoteContentEvent(String connectId, String data)   {
        try {
            SseEmitter emitter = sseEmitterManager.getEmitter(connectId);
            if (emitter != null) {
                var event = SseEmitter.event()
                        .name("commentary")
                        .data(data);
                emitter.send(event);
                log.info("emitter sent comment event");
            }
        } catch (IOException e) {
            log.error("SseEmitter error", e);
        }
    }

    public void sendNoteIndexEvent(String connectId, String data) throws IOException {
        try {
            SseEmitter emitter = sseEmitterManager.getEmitter(connectId);

            if (emitter != null) {
                var event = SseEmitter.event()
                        .name("index")
                        .data(data);
                emitter.send(event);
                log.info("emitter sent index event");
            } else {
                log.warn("emitter not found");
            }
        } catch (IOException e) {
            log.error("SseEmitter error", e);
        }
    }
}
