package com.technote.core.domain.note.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technote.client.kafka.event.CreateNoteCommentaryEvent;
import com.technote.client.kafka.event.CreateNoteOutlineEvent;
import com.technote.core.domain.note.implement.SseEventSender;
import com.technote.core.support.error.CustomException;
import com.technote.core.support.error.ErrorType;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        log.info("NoteEventListener Bean 등록됨");
    }

    @EventListener
    public void handleCreateNoteCommentaryEvent(CreateNoteCommentaryEvent event) {
        try {
            log.info("Received create note commentary event: {}", event.toString());
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("startTime", event.startTime());
            eventData.put("content", event.content());
            String jsonData = objectMapper.writeValueAsString(eventData);
            sseEventSender.broadcastNoteCommentaryEvent(event.noteId(),jsonData);
        } catch (IOException e) {
            log.error("CreateNoteCommentaryEvent 직렬화 과정에서 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                    ErrorType.IO_ERROR,
                    event.toString()
            );
        }
    }

    @EventListener
    public void handleCreateNoteOutlineEvent(CreateNoteOutlineEvent event) {
        try {
            log.info("Received create note outline event: {}", event.toString());
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("segments", event.segments());
            String jsonData = objectMapper.writeValueAsString(eventData);
            sseEventSender.broadcastNoteOutlineEvent(event.noteId(), jsonData);
        } catch (IOException e) {
            log.error("CreateNoteOutlineEvent 직렬화 과정에서 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                    ErrorType.IO_ERROR,
                    event.toString()
            );
        }
    }
}