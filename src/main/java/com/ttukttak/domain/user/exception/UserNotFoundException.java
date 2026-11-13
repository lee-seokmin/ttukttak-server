package com.ttukttak.domain.user.exception;

import com.ttukttak.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "not_found", "사용자를 찾을 수 없어요.");
    }
}
