package com.technote.core.domain.note.vo;

import com.technote.core.enums.UserLevel;
import lombok.Builder;

@Builder
public record NotePreviewVO (
        String id,
        String videoId,
        UserLevel userLevel,
        String title
) {
}
