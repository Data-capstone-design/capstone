package com.technote.client.kafka.consumer;

import com.technote.client.kafka.dto.NoteIndexDto;
import com.technote.client.kafka.event.CreateNoteIndexEvent;
import com.technote.client.kafka.mapper.NoteEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteIndexConsumer {

    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = "llm-index-events",
            groupId = "group_1",
            containerFactory = "kafkaIndexListenerContainerFactory"
    )
    public void listenToNoteContent(NoteIndexDto noteIndex) {
        CreateNoteIndexEvent event = NoteEventMapper.toCreateNoteIndexEvent(noteIndex);
        eventPublisher.publishEvent(event);
    }
}
