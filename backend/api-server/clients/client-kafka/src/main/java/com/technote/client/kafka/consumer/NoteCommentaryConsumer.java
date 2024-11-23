package com.technote.client.kafka.consumer;

import com.technote.client.kafka.event.CreateNoteCommentaryEvent;
import com.technote.client.kafka.mapper.NoteEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteCommentaryConsumer {

    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = "note-commentary-created-events",
            groupId = "group_1",
            containerFactory = "kafkaCommentaryListenerContainerFactory"
    )
    public void listenToNoteContent(NoteCommentaryDto noteCommentaryDto) {
        CreateNoteCommentaryEvent event = NoteEventMapper.toCreateNoteCommentaryEvent(noteCommentaryDto);
        System.out.println(event);
        eventPublisher.publishEvent(event);
    }
}
