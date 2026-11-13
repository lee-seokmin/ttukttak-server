package com.ttukttak.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/** docs/api/README.md의 오류 형식: { "error": { "code", "message", "fields"? } } */
public record ApiError(Error error) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Error(String code, String message, Map<String, String> fields) {
    }

    public static ApiError of(String code, String message) {
        return of(code, message, null);
    }

    public static ApiError of(String code, String message, Map<String, String> fields) {
        return new ApiError(new Error(code, message, fields));
    }
}
