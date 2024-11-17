package com.technote.core.web.dto;

public record CreateNoteRequest (
        String videoId,
        String userLevel
) {
}
