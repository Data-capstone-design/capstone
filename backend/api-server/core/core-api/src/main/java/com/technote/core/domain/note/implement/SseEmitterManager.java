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
    private final Map<String, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter addEmiter(String noteId, String sessionId) {
        emitters.putIfAbsent(noteId, new ConcurrentHashMap<>());
        Map<String, SseEmitter> noteEmitters = emitters.get(noteId);
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);
        noteEmitters.put(sessionId, emitter);

        log.info("Added emitter {}", sessionId);
        log.info("emitter list size: {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("Emitter completed for sessionId: {}", sessionId);
            removeEmitter(noteId, sessionId);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter timed out for sessionId: {}", sessionId);
            removeEmitter(noteId, sessionId);
        });

        emitter.onError((error) -> {
            log.error("Error occurred for sessionId: {}", sessionId, error);
            removeEmitter(noteId, sessionId);
        });
        return emitter;
    }

    public Map<String, SseEmitter> getEmittersByNoteId(String noteId) {
        return emitters.get(noteId);
    }

    public SseEmitter getEmitter(String noteId, String sessionId) {
        Map<String, SseEmitter> noteEmitters = emitters.get(noteId);
        return noteEmitters.get(sessionId);
    }

    public void removeEmitter(String noteId, String sessionId) {
        Map<String, SseEmitter> noteEmitters = emitters.get(noteId);
        if (noteEmitters != null) {
            noteEmitters.remove(sessionId);
            log.info("Removed emitter {}", sessionId);
            if(noteEmitters.isEmpty()) {
                emitters.remove(noteId);
            }
        }
    }
}
