package com.wizard.api_server.common.event;

import com.wizard.api_server.external.kafka.note.dto.NoteIndex;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateNoteIndexEvent extends ApplicationEvent{
    public NoteIndex noteIndex;

    public CreateNoteIndexEvent(Object source, NoteIndex noteIndex) {
        super(source);
        this.noteIndex = noteIndex;
    }
}