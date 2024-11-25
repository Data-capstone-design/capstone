package com.technote.client.feign;

import com.technote.client.feign.model.NoteTitleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoTitleClient {
    private static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";

    private final VideoTitleApi videoTitleApi;

    public NoteTitleResult getNoteTitle(String videoId) {
        String url = YOUTUBE_VIDEO_URL + videoId;
        return videoTitleApi.getVideoTitle(url).toResult();
    }
}