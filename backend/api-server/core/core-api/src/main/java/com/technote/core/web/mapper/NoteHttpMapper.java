package com.technote.core.web.mapper;

import com.technote.core.domain.note.dto.PagedNotePreviewsDto;
import com.technote.core.domain.note.vo.NoteVO;
import com.technote.core.enums.NoteStatus;
import com.technote.core.web.dto.CreateNoteHttpResponse;
import com.technote.core.web.dto.NoteHttpResponse;
import com.technote.core.web.dto.NoteHttpResponse.CommentaryResponse;
import com.technote.core.web.dto.NoteHttpResponse.SegmentResponse;
import com.technote.core.web.dto.NoteStatusHttpResponse;
import com.technote.core.web.dto.PagedNotePreviewsHttpResponse;
import com.technote.core.web.dto.PagedNotePreviewsHttpResponse.NotePreviewHttpResponse;

public class NoteHttpMapper {
    public static NoteStatusHttpResponse toGetNoteStatusHttpResponse(NoteStatus noteStatus) {
        return new NoteStatusHttpResponse(noteStatus.name());
    }

    public static NoteHttpResponse toNoteHttpResponse(NoteVO noteVO) {
        return new NoteHttpResponse(
                noteVO.id(),
                noteVO.videoId(),
                noteVO.title(),
                noteVO.userLevel().toString(),
                noteVO.status().toString(),
                noteVO.outline().stream()
                        .map(segment -> new SegmentResponse(segment.startTime(), segment.title(), segment.summary()))
                        .toList(),
                noteVO.commentaries().stream()
                        .map(commentary -> new CommentaryResponse(commentary.startTime(), commentary.content()))
                        .toList()
        );
    }

    public static CreateNoteHttpResponse toCreateNoteHttpResponse(String noteId) {
        return new CreateNoteHttpResponse(noteId);
    }

    public static PagedNotePreviewsHttpResponse toPagedNotePreviewsHttpResponse(PagedNotePreviewsDto pagedNotePreviewsDto) {
        return PagedNotePreviewsHttpResponse.builder()
                .notePreviews(pagedNotePreviewsDto.notePreviews()
                        .stream().map(notePreview -> NotePreviewHttpResponse.builder()
                                .noteId(notePreview.id())
                                .videoId(notePreview.videoId())
                                .userLevel(notePreview.userLevel().name())
                                .title(notePreview.title())
                                .build()
                        ).toList()
                )
                .nextCursor(pagedNotePreviewsDto.nextCursor())
                .hasMore(pagedNotePreviewsDto.hasMore())
                .build();

    }
}
