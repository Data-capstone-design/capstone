package com.wizard.api_server.web.note.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizard.api_server.common.event.CreateNoteEvent;
import com.wizard.api_server.common.event.CreateNoteIndexEvent;
import com.wizard.api_server.external.kafka.note.dto.NoteIndex.NoteIndexContent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteEventListener {
    private final ObjectMapper objectMapper;
    private final SseEmitters sseEmitters;

    @EventListener
    public void handleCreateNoteContentEvent(CreateNoteEvent event) {
        try {
            log.info("Received create note content event: {}", objectMapper.writeValueAsString(event));
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("startTime", event.getStartTime());
            eventData.put("content", event.getContent());
            String jsonData = objectMapper.writeValueAsString(eventData);
            sseEmitters.sendCommentaryEvent(event.getVideoId(),jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void handleCreateNoteIndexEvent(CreateNoteIndexEvent event) {
        try {
            log.info("Received create note content event: {}", objectMapper.writeValueAsString(event));
            Map<String, Object> eventData = new HashMap<>();
            String videoId = event.getNoteIndex().videoId();
            List<NoteIndexContent> indices = event.getNoteIndex().indices();
            eventData.put("indices", indices);
            String jsonDate = objectMapper.writeValueAsString(eventData);
            sseEmitters.sendIndexEvent(videoId, jsonDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
