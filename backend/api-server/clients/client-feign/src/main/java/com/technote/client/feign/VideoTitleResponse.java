package com.technote.client.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.technote.client.feign.model.NoteTitleResult;


@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoTitleResponse(
        String title
) {
    NoteTitleResult toResult() {
        return new NoteTitleResult(title);
    }
}
