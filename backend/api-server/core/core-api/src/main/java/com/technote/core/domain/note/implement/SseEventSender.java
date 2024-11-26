package com.technote.core.domain.note.implement;

import com.technote.core.enums.SseName;
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

    public void sendEvent(String noteId, String sessionId, SseName eventName) {
        sendEvent(noteId, sessionId, eventName, "");
    }

    public void sendEvent(String noteId, String sessionId, SseName eventName, String data) {
        try {
            var emitter = sseEmitterManager.getEmitter(noteId, sessionId);
            if (emitter != null) {
                var event = SseEmitter.event().name(eventName.getValue()).data(data);
                emitter.send(event);
                log.info("Event '{}' sent to sessionId: {}", eventName, sessionId);
            } else {
                log.warn("Emitter not found for noteId: {}, sessionId: {}", noteId, sessionId);
            }
        } catch (IOException e) {
            log.error("Failed to send event '{}' to sessionId: {}", eventName, sessionId, e);
        }
    }

    public void broadcastEvent(String noteId, SseName eventName) {
        broadcastEvent(noteId, eventName, "");
    }

    public void broadcastEvent(String noteId, SseName eventName, String data) {
        var noteEmitters = sseEmitterManager.getEmittersByNoteId(noteId);
        if (noteEmitters != null) {
            for (String sessionId : noteEmitters.keySet()) {
                sendEvent(noteId, sessionId, eventName, data);
            }
        } else {
            log.warn("No emitters found for noteId: {}", noteId);
        }
    }
}

