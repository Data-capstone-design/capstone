package com.technote.core.support.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // common
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "예상치 못한 에러가 발생했습니다.",
            LogLevel.ERROR),
    NETWORK_ERROR(HttpStatus.BAD_GATEWAY, ErrorCode.E502, "네트워크 오류가 발생했습니다.", LogLevel.ERROR),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 요청입니다.", LogLevel.WARN),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "입출력 오류가 발생했습니다.", LogLevel.ERROR),

    // note
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.N001, "노트를 찾을 수 없습니다.", LogLevel.WARN),
    NOTE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, ErrorCode.N002, "이미 존재하는 노트입니다.", LogLevel.WARN);

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;
}
