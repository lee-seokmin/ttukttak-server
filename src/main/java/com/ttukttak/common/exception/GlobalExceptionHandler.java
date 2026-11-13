package com.ttukttak.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiError> handleBusiness(BusinessException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiError.of(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ApiError.of("validation_failed", "입력한 내용을 다시 확인해 주세요.", fields));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiError.of("invalid_request", "요청 형식이 올바르지 않아요."));
    }

    /** 동시에 같은 값으로 가입하는 등 DB 고유 제약에 걸렸을 때 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException cve
                && cve.getKind() == ConstraintViolationException.ConstraintKind.UNIQUE) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("conflict", "이미 있는 값이에요."));
        }
        // 길이 초과, NOT NULL 같은 위반은 요청 검사가 놓친 서버 버그예요.
        log.error("Data integrity violation", e);
        return ResponseEntity.internalServerError().body(ApiError.of("internal", "잠시 후 다시 시도해 주세요."));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnknown(Exception e) {
        // 없는 경로, 지원하지 않는 메서드 같은 Spring MVC 예외는 원래 상태 코드를 살려요.
        if (e instanceof ErrorResponse spring && spring.getStatusCode().is4xxClientError()) {
            boolean notFound = spring.getStatusCode().value() == 404;
            return ResponseEntity.status(spring.getStatusCode()).body(notFound
                    ? ApiError.of("not_found", "찾을 수 없어요.")
                    : ApiError.of("invalid_request", "요청 형식이 올바르지 않아요."));
        }
        log.error("Unhandled exception", e);
        return ResponseEntity.internalServerError().body(ApiError.of("internal", "잠시 후 다시 시도해 주세요."));
    }
}
