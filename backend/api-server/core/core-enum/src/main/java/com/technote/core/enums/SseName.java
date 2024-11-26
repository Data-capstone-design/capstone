package com.technote.core.enums;

import lombok.Getter;

@Getter
public enum SseName {
    CONNECT("connect"),
    OUTLINE("outline"),
    COMMENTARY("commentary"),
    EXPLANATION_END("explanation-end"),
    FEEDBACK_END("feedback-end"),
    COMPLETE("complete");

    private final String value;

    SseName(String value) {
        this.value = value;
    }
}
