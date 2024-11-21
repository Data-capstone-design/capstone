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

    public void sendConnectEvent(String noteId, String sessionId) {
        sendEvent(noteId, sessionId, "connect", "send connect event");
    }

    public void broadcastNoteCommentaryEvent(String noteId, String data) {
        broadcastEvent(noteId, "commentary", data);
    }

    public void broadcastNoteOutlineEvent(String noteId, String data) {
        broadcastEvent(noteId, "outline", data);
    }

    private void sendEvent(String noteId, String sessionId, String eventName, String data) {
        try {
            var emitter = sseEmitterManager.getEmitter(noteId, sessionId);
            if (emitter != null) {
                var event = SseEmitter.event().name(eventName).data(data);
                emitter.send(event);
                log.info("Event '{}' sent to sessionId: {}", eventName, sessionId);
            } else {
                log.warn("Emitter not found for noteId: {}, sessionId: {}", noteId, sessionId);
            }
        } catch (IOException e) {
            log.error("Failed to send event '{}' to sessionId: {}", eventName, sessionId, e);
        }
    }

    private void broadcastEvent(String noteId, String eventName, String data) {
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

