package com.technote.core.domain.note.implement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class SseEmitterManager {
    private static final long EMITTER_TIMEOUT = 5*60*1000L;
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
            removeEmitter(connectId);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter timed out for connectId: {}", connectId);
            removeEmitter(connectId);
        });

        emitter.onError((error) -> {
            log.error("Error occurred for connectId: {}", connectId, error);
            removeEmitter(connectId);
        });
        return emitter;
    }

    public SseEmitter getEmitter(String connectId) {
        return emitters.get(connectId);
    }

    public void removeEmitter(String connectId) {
        emitters.remove(connectId);
        log.info("Removed emitter {}", connectId);
    }
}
