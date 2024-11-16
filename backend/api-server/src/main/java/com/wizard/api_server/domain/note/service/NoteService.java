package com.wizard.api_server.domain.note.service;

import com.wizard.api_server.domain.note.event.CreateNoteEvent;
import com.wizard.api_server.domain.note.event.NoteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteEventPublisher eventPublisher;

    public void sendVideoLink(String videoId, String userLevel) {
        log.info("Publishing video link for videoId: {}, user level: {}", videoId, userLevel);
        CreateNoteEvent event = new CreateNoteEvent(videoId, userLevel);
        eventPublisher.publish(event);
    }
}
