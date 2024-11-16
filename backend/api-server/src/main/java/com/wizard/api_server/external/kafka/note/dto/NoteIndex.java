package com.wizard.api_server.external.kafka.note.dto;

import java.util.List;

public record NoteIndex(
      String videoId,
      List<NoteIndexContent> indices
) {
    public record NoteIndexContent (
            String startTime,
            String index,
            String summary
    ) {
    }
}
