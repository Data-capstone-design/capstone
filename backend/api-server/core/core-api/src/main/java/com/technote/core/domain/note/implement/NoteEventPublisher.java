package com.technote.core.domain.note.implement;

import com.technote.client.kafka.dto.NoteInfoDto;
import com.technote.client.kafka.producer.NoteInfoProducer;
import com.technote.core.enums.UserLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteEventPublisher {
    private final NoteInfoProducer noteInfoProducer;

    public void publishCreateNoteEvent(String videoId, UserLevel userLevel, String noteId) {
        noteInfoProducer.produce(new NoteInfoDto(videoId, userLevel.name(), noteId));
    }
}
