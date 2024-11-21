package com.technote.client.kafka.dto;

public record NoteInfoDto(
        String videoId,
        String userLevel,
        String noteId
) {
}
