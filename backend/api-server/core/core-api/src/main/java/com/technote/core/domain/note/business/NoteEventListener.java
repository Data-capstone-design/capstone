package com.technote.core.domain.note.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technote.core.domain.note.event.CreateNoteContentEvent;
import com.technote.core.domain.note.event.CreateNoteIndexEvent;
import com.technote.core.domain.note.implement.SseEventSender;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteEventListener {
    private final ObjectMapper objectMapper;
    private final SseEventSender sseEventSender;

    @EventListener
    public void handleCreateNoteContentEvent(CreateNoteContentEvent event) {
        try {
            log.info("Received create note content event: {}", event.toString());
            Map<String, Object> eventData = new HashMap<>();
            String jsonData = objectMapper.writeValueAsString(eventData);
            sseEventSender.sendNoteContentEvent(event.videoId(),jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void handleCreateNoteIndexEvent(CreateNoteIndexEvent event) {
        try {
            log.info("Received create note index event: {}", event.toString());
            Map<String, Object> eventData = new HashMap<>();
            String videoId = event.videoId();
            eventData.put("contents", event.contents());
            String jsonDate = objectMapper.writeValueAsString(eventData);
            sseEventSender.sendNoteIndexEvent(videoId, jsonDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}