package com.wizard.api_server.web.note.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class SseEmitters {
    private final long EMITTER_TIMEOUT = 5*60*1000L;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter addEmiter(String connectId) {
        if(emitters.containsKey(connectId)) {
            log.info("Emitter already exist for connectId {}", connectId);
            return emitters.get(connectId);
        }
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);
        emitters.put(connectId, emitter);

        log.info("Added emitter {}", connectId);
        log.info("emitter list size: {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("Emitter completed for connectId: {}", connectId);
            emitters.remove(connectId);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter timed out for connectId: {}", connectId);
            emitters.remove(connectId);
        });

        emitter.onError((error) -> {
            log.error("Error occurred for connectId: {}", connectId, error);
            emitters.remove(connectId);
        });
        return emitter;
    }

    public void sendConnectEvent(String connectId) throws IOException {
        SseEmitter emitter = emitters.get(connectId);
        var event = SseEmitter.event()
                .name("connect")
                .data("send connect event");
            emitter.send(event);
        log.info("emitter sent first event connectId: {}", connectId);
    }

    public void sendCommentaryEvent(String connectId, String data) throws IOException {
        SseEmitter emitter = emitters.get(connectId);
        if (emitter != null) {
            var event = SseEmitter.event()
                    .name("commentary")
                    .data(data);

            emitter.send(event);
            log.info("emitter sent comment event");
        } else {
            log.warn("emitter not found");
        }
    }

    public void sendIndexEvent(String connectId, String data) throws IOException {
        SseEmitter emitter = emitters.get(connectId);
        if (emitter != null) {
            var event = SseEmitter.event()
                    .name("index")
                    .data(data);
            emitter.send(event);
            log.info("emitter sent index event");
        } else {
            log.warn("emitter not found");
        }
    }
}
