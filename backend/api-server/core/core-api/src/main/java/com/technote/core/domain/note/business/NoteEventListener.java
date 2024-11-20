package com.technote.core.domain.note.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technote.core.domain.note.implement.SseEventSender;
import com.technote.core.support.error.CustomException;
import com.technote.core.support.error.ErrorType;
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
            log.error("CreateNoteContentEvent 직렬화 과정에서 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                    ErrorType.IO_ERROR,
                    event.toString()
            );
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
            log.error("CreateNoteIndexEvent 직렬화 과정에서 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                    ErrorType.IO_ERROR,
                    event.toString()
            );
        }
    }
}