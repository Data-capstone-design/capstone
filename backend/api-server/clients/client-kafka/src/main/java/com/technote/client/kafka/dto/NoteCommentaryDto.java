package com.technote.client.kafka.dto;

public record NoteCommentaryDto(
        String noteId,
        long startTime,
        String content) {
}
