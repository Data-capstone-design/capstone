package com.technote.storage.mongo.core;

import com.technote.core.enums.NoteStatus;
import com.technote.storage.mongo.core.Note.Commentary;
import com.technote.storage.mongo.core.Note.Segment;
import java.util.List;

public interface NoteCustomRepository {
    void setCommentaries(String noteId, List<Commentary> commentaries);
    void updateCommentaryContentByOrder(String noteId, int orderIndex, String content);
    void setStatus(String noteId, NoteStatus status);
    void setOutline(String noteId, List<Segment> segments);
    List<Note> getPagedNotes(String lastId, int pageSize);
    boolean hasMoreNotes(String lastId);
}
