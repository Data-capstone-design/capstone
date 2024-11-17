package com.technote.core.domain.note.business;

import com.technote.core.domain.note.implement.SseEmitterManager;
import com.technote.core.domain.note.implement.SseEventSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NoteSseService {
    private final SseEmitterManager sseEmitterManager;
    private final SseEventSender sseEventSender;

    public SseEmitter connect(String connectId) {
        SseEmitter emitter = sseEmitterManager.addEmiter(connectId);
        sseEventSender.sendConnectEvent(connectId);
        return emitter;
    }
}
