package com.technote.storage.mongo.core;

import com.technote.core.enums.UserLevel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Note, String>, NoteCustomRepository {

    Optional<Note> findByVideoIdAndUserLevel(String videoId, UserLevel userLevel);

    boolean existsByVideoIdAndUserLevel(String videoId, UserLevel userLevel);
}