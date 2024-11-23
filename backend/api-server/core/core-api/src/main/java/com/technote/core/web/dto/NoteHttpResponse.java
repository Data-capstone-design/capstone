package com.technote.core.web.dto;

import java.util.List;

public record NoteHttpResponse(
        String noteId,
        String videoId,
        String title,
        String userLevel,
        String status,
        List<SegmentResponse> outline,
        List<CommentaryResponse> commentaries
) {
    public record SegmentResponse(
            double startTime,
            String title,
            String summary
    ) {}

    public record CommentaryResponse(
            double startTime,
            String content
    ) {}
}

