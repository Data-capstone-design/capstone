package com.technote.client.kafka.consumer;

import com.technote.client.kafka.dto.NoteOutlineDto;
import com.technote.client.kafka.event.CreateNoteOutlineEvent;
import com.technote.client.kafka.mapper.NoteEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteOutlineConsumer {

    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = "note-outline-created-events",
            groupId = "group_1",
            containerFactory = "kafkaOutlineListenerContainerFactory"
    )
    public void listenToNoteContent(NoteOutlineDto noteOutline) {
        CreateNoteOutlineEvent event = NoteEventMapper.toCreateNoteOutlineEvent(noteOutline);
        System.out.println(event);
        eventPublisher.publishEvent(event);
    }
}
