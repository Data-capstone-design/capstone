package com.technote.storage.mongo.core;

import com.technote.core.enums.UserLevel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String> {
    Optional<Note> findByVideoIdAndUserLevel(String videoId, UserLevel userLevel);
    boolean existsByVideoIdAndUserLevel(String videoId, UserLevel userLevel);
}