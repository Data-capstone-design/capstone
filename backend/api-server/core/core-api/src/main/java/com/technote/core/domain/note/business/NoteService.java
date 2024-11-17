package com.technote.core.domain.note.business;

import com.technote.core.domain.note.implement.NoteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteEventPublisher noteEventPublisher;

    public void createNote(String videoId, String userLevel) {
        log.info("Publishing video link for videoId: {}, user level: {}", videoId, userLevel);
        noteEventPublisher.publishCreateNoteEvent(videoId, userLevel);
    }
}
