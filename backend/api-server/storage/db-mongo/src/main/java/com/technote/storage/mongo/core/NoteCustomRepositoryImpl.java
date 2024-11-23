package com.technote.storage.mongo.core;

import com.technote.storage.mongo.core.Note.Commentary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public class NoteCustomRepositoryImpl implements NoteCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void setCommentaries(String noteId, List<Commentary> commentaries) {
        Query query = new Query(Criteria.where("_id").is(noteId));
        Update update = new Update().set("commentaries", commentaries);
        mongoTemplate.updateFirst(query, update, Note.class);
    }

    @Override
    public void updateCommentaryContentByOrder(String noteId, int orderIndex, String content) {
        Query query = new Query(Criteria.where("_id").is(noteId));
        Update update = new Update().set("commentaries." + orderIndex + ".content", content);
        mongoTemplate.updateFirst(query, update, Note.class);
    }
}

