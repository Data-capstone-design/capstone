package com.technote.client.kafka.event;

import java.util.List;
import lombok.Builder;

@Builder
public record CreateNoteIndexEvent (
        String videoId,
        List<CreateNoteIndexEventContent> contents
) {
    @Builder
    public record CreateNoteIndexEventContent (
            String startTime,
            String title,
            String summary
    ) {
    }
}
