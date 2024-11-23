package com.technote.client.kafka.consumer;

import java.util.List;

public record NoteOutlineDto(
      String noteId,
      List<NoteOutlineContent> segments
) {
    public record NoteOutlineContent (
            int startTime,
            String title,
            String summary
    ) {
    }
}
