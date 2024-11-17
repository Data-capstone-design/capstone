package com.technote.client.kafka.mapper;

import com.technote.client.kafka.dto.NoteContentDto;
import com.technote.client.kafka.dto.NoteIndexDto;
import com.technote.client.kafka.event.CreateNoteContentEvent;
import com.technote.client.kafka.event.CreateNoteIndexEvent;
import com.technote.client.kafka.event.CreateNoteIndexEvent.CreateNoteIndexEventContent;

public class NoteEventMapper {
    public static CreateNoteContentEvent toCreateNoteContentEvent(NoteContentDto noteContentDto) {
        return CreateNoteContentEvent.builder()
                .videoId(noteContentDto.videoId())
                .startTime(noteContentDto.startTime())
                .content(noteContentDto.content())
                .build();

    }

    public static CreateNoteIndexEvent toCreateNoteIndexEvent(NoteIndexDto noteIndexDto) {
        return CreateNoteIndexEvent.builder()
                .videoId(noteIndexDto.videoId())
                .contents(
                        noteIndexDto.contents()
                                .stream()
                                .map(content -> CreateNoteIndexEventContent.builder()
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
