package com.technote.client.kafka.event;

import lombok.Builder;

@Builder
public record CreateNoteContentEvent(
        String videoId,
        long startTime,
        String content
) {
}
