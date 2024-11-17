package com.technote.core.enums;

import lombok.Getter;

@Getter
public enum UserLevel {
    BEGINNER("BEGINNER"),
    INTERMEDIATE("INTERMEDIATE"),
    EXPERT("EXPERT");

    private final String value;

    UserLevel(String value) {
        this.value = value;
    }
}
