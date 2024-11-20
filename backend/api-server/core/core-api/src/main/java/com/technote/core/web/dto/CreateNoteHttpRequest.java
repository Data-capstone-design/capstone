package com.technote.core.web.dto;

public record CreateNoteHttpRequest(
        String videoId,
        String userLevel
) {
}
