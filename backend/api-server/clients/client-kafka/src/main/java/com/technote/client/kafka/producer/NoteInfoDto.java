package com.technote.client.kafka.producer;

public record NoteInfoDto(
        String videoId,
        String userLevel,
        String noteId
) {
}
