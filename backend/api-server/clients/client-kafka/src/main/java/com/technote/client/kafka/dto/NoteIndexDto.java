package com.technote.client.kafka.dto;

import java.util.List;

public record NoteIndexDto(
      String videoId,
      List<NoteIndexContent> contents
) {
    public record NoteIndexContent (
            String startTime,
            String title,
            String summary
    ) {
    }
}
