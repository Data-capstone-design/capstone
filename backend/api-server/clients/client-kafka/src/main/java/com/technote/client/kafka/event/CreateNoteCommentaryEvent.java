package com.technote.client.kafka.event;

import lombok.Builder;

@Builder
public record CreateNoteCommentaryEvent(
        String noteId,
        int startTime,
        int orderIndex,
        String content
) {
}
