package com.technote.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "video-title-api", url = "https://noembed.com",
    configuration = FeignConfig.class
)
public interface VideoTitleApi {
    @GetMapping("/embed")
    VideoTitleResponse getVideoTitle(@RequestParam("url") String url);
}
