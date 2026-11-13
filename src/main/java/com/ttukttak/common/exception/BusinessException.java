package com.ttukttak.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** API 오류 형식의 code와 message를 그대로 담아요. message는 화면에 바로 보여줘도 되는 문장이에요. */
@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
