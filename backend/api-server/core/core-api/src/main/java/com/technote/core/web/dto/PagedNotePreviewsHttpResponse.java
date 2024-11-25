package com.technote.core.web.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PagedNotePreviewsHttpResponse(
        List<NotePreviewHttpResponse> notePreviews,
        String nextCursor,
        boolean hasMore
) {
    @Builder
    public record NotePreviewHttpResponse (
            String noteId,
            String videoId,
            String userLevel,
            String title
    ){
    }
}
