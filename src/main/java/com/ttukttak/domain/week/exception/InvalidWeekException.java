package com.ttukttak.domain.week.exception;

import com.ttukttak.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidWeekException extends BusinessException {
    public InvalidWeekException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "validation_failed", message);
    }
}
