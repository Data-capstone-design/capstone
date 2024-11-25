package com.technote.core.domain.note.dto;

import com.technote.core.domain.note.vo.NotePreviewVO;
import java.util.List;

public record PagedNotePreviewsDto(
        List<NotePreviewVO> notePreviews,
        String nextCursor,
        boolean hasMore
)  {
}
