package com.technote.client.kafka.event;

import lombok.Builder;

@Builder
public record CreateNoteCommentaryEvent(
        String noteId,
        long startTime,
        String content
) {
}
