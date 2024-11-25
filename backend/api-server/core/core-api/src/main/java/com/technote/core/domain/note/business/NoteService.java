package com.technote.core.domain.note.business;

import com.technote.core.domain.note.dto.PagedNotePreviewsDto;
import com.technote.core.domain.note.implement.NoteEventPublisher;
import com.technote.core.domain.note.implement.NoteStorageHandler;
import com.technote.core.domain.note.vo.NotePreviewVO;
import com.technote.core.domain.note.vo.NoteVO;
import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteEventPublisher noteEventPublisher;
    private final NoteStorageHandler noteStorageHandler;

    public String createNote(String videoId, UserLevel userLevel) {
        String noteId = noteStorageHandler.saveNote(videoId, userLevel);
        noteEventPublisher.publishCreateNoteEvent(videoId, userLevel, noteId);
        return noteId;
    }

    public NoteStatus getNoteStatus(String videoId, UserLevel userLevel) {
        return noteStorageHandler.getNoteStatus(videoId, userLevel);
    }

    public NoteVO getNote(String videoId, UserLevel userLevel) {
        return noteStorageHandler.getNote(videoId, userLevel);
    }

    public PagedNotePreviewsDto getMainPageNotes(String lastId, int pageSize) {
        List<NotePreviewVO> notePreviews = noteStorageHandler.getPagedNotes(lastId, pageSize);
        boolean isEmpty = notePreviews.isEmpty();
        String nextCursor = isEmpty ? null : notePreviews.get(notePreviews.size() -1).id();
        boolean hasMore = !isEmpty && noteStorageHandler.hasMoreNotes(nextCursor);
        return new PagedNotePreviewsDto(notePreviews, nextCursor, hasMore);
    }
}
