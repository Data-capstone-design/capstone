package com.technote.storage.mongo.core;

import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "note")
public class Note {
    @Id
    private String id;

    private String videoId;

    private String title;

    private UserLevel userLevel;

    private NoteStatus status;

    private List<Segment> outline;

    private List<Commentary> commentaries;

    @Builder
    private Note(String videoId, String title, UserLevel userLevel, NoteStatus status, List<Segment> outline, List<Commentary> commentaries) {
        this.videoId = videoId;
        this.title = title;
        this.userLevel = userLevel;
        this.status = status;
        this.outline = outline;
        this.commentaries = commentaries;
    }

    @Getter
    @Builder
    public static class Commentary {
        private int startTime;
        private String content;
    }

    @Getter
    @Builder
    public static class Segment {
        private int startTime;
        private String title;
        private String summary;
    }
}
