package com.technote.core.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum UserLevel {
    BASIC,
    INTERMEDIATE,
    ADVANCED;

    public static UserLevel fromValue(String value) {
        return Arrays.stream(UserLevel.values())
                .filter(v -> v.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid user level: " + value));
    }
}
