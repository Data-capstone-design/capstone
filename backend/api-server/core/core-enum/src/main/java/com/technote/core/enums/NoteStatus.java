package com.technote.core.enums;

import java.util.Arrays;

public enum NoteStatus {
    COMPLETED,
    IN_PROGRESS,
    NOT_EXIST;

    public static NoteStatus fromValue(String value) {
        return Arrays.stream(NoteStatus.values())
                .filter(v -> v.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
