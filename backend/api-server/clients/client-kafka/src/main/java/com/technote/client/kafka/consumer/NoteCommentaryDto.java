package com.technote.client.kafka.consumer;

public record NoteCommentaryDto(
        String noteId,
        int startTime,
        int commentaryOrder,
        String content
) {
}
