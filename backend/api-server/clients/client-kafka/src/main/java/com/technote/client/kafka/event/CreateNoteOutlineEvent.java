package com.technote.client.kafka.event;

import java.util.List;
import lombok.Builder;

@Builder
public record CreateNoteOutlineEvent(
        String noteId,
        List<CreateNoteOutlineEventSegment> segments
) {
    @Builder
    public record CreateNoteOutlineEventSegment (
            int startTime,
            String title,
            String summary
    ) {
    }
}
