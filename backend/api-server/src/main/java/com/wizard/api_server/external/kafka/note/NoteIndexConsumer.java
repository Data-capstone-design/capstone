package com.wizard.api_server.external.kafka.note;

import com.wizard.api_server.common.event.CreateNoteIndexEvent;
import com.wizard.api_server.external.kafka.note.dto.NoteIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteIndexConsumer {

    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = "llm-index-events",
            groupId = "group_1",
            containerFactory = "kafkaIndexListenerContainerFactory"
    )
    public void listenToNoteContent(NoteIndex noteIndex) {
        log.info("received note index");
        log.info("note index: {}", noteIndex);
        CreateNoteIndexEvent event = new CreateNoteIndexEvent(this, noteIndex);
        eventPublisher.publishEvent(event);
    }
}
