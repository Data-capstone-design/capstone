package com.technote.client.kafka.dto;

import java.util.List;

public record NoteOutlineDto(
      String noteId,
      List<NoteOutlineContent> segments
) {
    public record NoteOutlineContent (
            String startTime,
            String title,
            String summary
    ) {
    }
}
