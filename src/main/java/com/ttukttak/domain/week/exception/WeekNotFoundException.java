package com.ttukttak.domain.week.exception;

import com.ttukttak.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WeekNotFoundException extends BusinessException {
    public WeekNotFoundException() {
        super(HttpStatus.NOT_FOUND, "not_found", "주차를 찾을 수 없어요.");
    }
}
