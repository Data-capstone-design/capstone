package com.technote.client.kafka.dto;

public record NoteContentDto(
        String videoId,
        long startTime,
        String content) {
}
