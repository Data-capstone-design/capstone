package com.technote.client.kafka.mapper;

import com.technote.client.kafka.consumer.NoteCommentaryDto;
import com.technote.client.kafka.consumer.NoteOutlineDto;
import com.technote.client.kafka.event.CreateNoteCommentaryEvent;
import com.technote.client.kafka.event.CreateNoteOutlineEvent;
import com.technote.client.kafka.event.CreateNoteOutlineEvent.CreateNoteOutlineEventSegment;

public class NoteEventMapper {
    public static CreateNoteCommentaryEvent toCreateNoteCommentaryEvent(NoteCommentaryDto noteCommentaryDto) {
        return CreateNoteCommentaryEvent.builder()
                .noteId(noteCommentaryDto.noteId())
                .startTime(noteCommentaryDto.startTime())
                .content(noteCommentaryDto.content())
                .build();
    }

    public static CreateNoteOutlineEvent toCreateNoteOutlineEvent(NoteOutlineDto noteOutlineDto) {
        return CreateNoteOutlineEvent.builder()
                .noteId(noteOutlineDto.noteId())
                .segments(
                        noteOutlineDto.segments()
                                .stream()
                                .map(content -> CreateNoteOutlineEventSegment.builder()
                                        .title(content.title())
                                        .startTime(content.startTime())
                                        .summary(content.summary())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }
}
