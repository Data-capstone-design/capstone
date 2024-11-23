package com.technote.storage.mongo.core;

import com.technote.storage.mongo.core.Note.Commentary;
import java.util.List;

public interface NoteCustomRepository {
    void setCommentaries(String noteId, List<Commentary> commentaries);
    void updateCommentaryContentByOrder(String noteId, int orderIndex, String content);
}
