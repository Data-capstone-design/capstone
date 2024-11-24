package com.technote.core.domain.note.implement;

import com.technote.core.domain.note.implement.NoteVO.CommentaryVO;
import com.technote.core.domain.note.implement.NoteVO.SegmentVO;
import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import com.technote.core.support.error.CustomException;
import com.technote.core.support.error.ErrorType;
import com.technote.storage.mongo.core.Note;
import com.technote.storage.mongo.core.Note.Commentary;
import com.technote.storage.mongo.core.Note.Segment;
import com.technote.storage.mongo.core.NoteRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteStorageHandler {
    private final NoteRepository noteRepository;

    public String saveNote(String videoId, UserLevel userLevel) {
        boolean isExist = noteRepository.existsByVideoIdAndUserLevel(videoId, userLevel);
        if (isExist) {
            throw new CustomException(ErrorType.NOTE_ALREADY_EXIST);
        }

        Note newNote = Note.builder()
                .videoId(videoId)
                .userLevel(userLevel)
                .status(NoteStatus.IN_PROGRESS)
                .outline(new ArrayList<>())
                .commentaries(new ArrayList<>())
                .build();
        Note savedNote = noteRepository.save(newNote);
        return savedNote.getId();
    }

    public NoteStatus getNoteStatus(String videoId, UserLevel userLevel) {
        return noteRepository.findByVideoIdAndUserLevel(videoId, userLevel)
                .map(Note::getStatus)
                .orElseGet(() -> NoteStatus.NOT_EXIST);
    }

    public NoteVO getNote(String videoId, UserLevel userLevel) {
        Note note = noteRepository.findByVideoIdAndUserLevel(videoId, userLevel)
                .orElseThrow(() -> new CustomException(ErrorType.NOTE_NOT_FOUND));

        return NoteVO.builder()
                .id(note.getId())
                .videoId(note.getVideoId())
                .title(note.getTitle())
                .status(note.getStatus())
                .userLevel(note.getUserLevel())
                .commentaries(note.getCommentaries()
                        .stream()
                        .map(commentary -> CommentaryVO.builder()
                                .startTime(commentary.getStartTime())
                                .content(commentary.getContent())
                                .build()
                        )
                        .toList()
                )
                .outline(note.getOutline()
                        .stream()
                        .map(segment -> SegmentVO.builder()
                                .startTime(segment.getStartTime())
                                .summary(segment.getSummary())
                                .title(segment.getTitle())
                                .build()
                        )
                        .toList()
                )
                .build();
    }

    public void setStatusToCompleted(String noteId) {
        noteRepository.setStatusToComplete(noteId);
    }

    public void setNoteCommentariesBasedOnOutline(String noteId, List<SegmentVO> segments) {
        List<Commentary> commentaries = segments.stream()
                .map((segment -> Commentary.builder()
                        .startTime(segment.startTime())
                        .build()
                ))
                .toList();
        noteRepository.setCommentaries(noteId, commentaries);
    }

    public void updateCommentaryByOrder(String noteId, int orderIndex,  String content) {
        noteRepository.updateCommentaryContentByOrder(noteId, orderIndex, content);
    }

    public void setNoteOutline(String noteId, List<SegmentVO> segments) {
        List<Segment> outline = segments.stream().map(segment -> Segment.builder()
                .startTime(segment.startTime())
                .title(segment.title())
                .summary(segment.summary())
                .build()
        ).toList();
        noteRepository.setOutline(noteId, outline);
    }
}
