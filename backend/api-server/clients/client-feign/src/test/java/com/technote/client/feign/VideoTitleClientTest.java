package com.technote.client.feign;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.technote.client.feign.model.NoteTitleResult;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VideoTitleClientTest {

    @Autowired
    private VideoTitleClient videoTitleClient;

    @Test
    public void testGetNoteTitle() {
        String videoId = "h30k7YixrMo";
        String expectedTitle = "2. What Makes Redis Special? | Redis Internals";

        NoteTitleResult result = videoTitleClient.getNoteTitle(videoId);

        Assertions.assertThat(result.noteTitle()).isEqualTo(expectedTitle);
    }

}