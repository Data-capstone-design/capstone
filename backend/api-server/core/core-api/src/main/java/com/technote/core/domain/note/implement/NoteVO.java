package com.technote.core.domain.note.implement;

import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import java.util.List;
import lombok.Builder;

@Builder
public record NoteVO (
        String id,
        String videoId,
        String title,
        UserLevel userLevel,
        NoteStatus status,
        List<SegmentVO> outline,
        List<CommentaryVO> commentaries
){
    @Builder
    public record CommentaryVO(
            int startTime,
            String content
    ) {}

    @Builder
    public record SegmentVO(
            int startTime,
            String title,
            String summary
    ) {}
}
