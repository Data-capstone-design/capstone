package com.technote.core.domain.note.event;

import lombok.Builder;

@Builder
public record CreateNoteContentEvent(
        String videoId,
        long startTime,
        String content
) {
}
