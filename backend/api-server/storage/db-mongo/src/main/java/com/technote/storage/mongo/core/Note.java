package com.technote.storage.mongo.core;

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

    private List<Segment> outline;

    private List<Commentary> commentaries;

    @Builder
    private Note(String videoId, String title, UserLevel userLevel, List<Segment> outline, List<Commentary> commentaries) {
        this.videoId = videoId;
        this.title = title;
        this.userLevel = userLevel;
        this.outline = outline;
        this.commentaries = commentaries;
    }

    @Getter
    @Builder
    public static class Commentary {
        private double startTime;
        private String content;
    }

    @Getter
    @Builder
    public static class Segment {
        private double startTime;
        private String title;
        private String summary;
    }
}
