package com.technote.client.kafka.consumer;

import com.technote.client.kafka.dto.NoteContentDto;
import com.technote.client.kafka.event.CreateNoteContentEvent;
import com.technote.client.kafka.mapper.NoteEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteContentConsumer {

    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = "llm-commentary-events",
            groupId = "group_1",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenToNoteContent(NoteContentDto noteContentDto) {
        CreateNoteContentEvent event = NoteEventMapper.toCreateNoteContentEvent(noteContentDto);
        eventPublisher.publishEvent(event);
    }
}
