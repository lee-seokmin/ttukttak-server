package com.ttukttak.domain.week.exception;

import com.ttukttak.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/** 지금 단계에서 할 수 없는 일 */
public class WeekPhaseException extends BusinessException {
    private WeekPhaseException(String code, String message) {
        super(HttpStatus.CONFLICT, code, message);
    }

    public static WeekPhaseException breakWeek() {
        return new WeekPhaseException("break_week", "이번 주는 쉬는 주예요.");
    }

    public static WeekPhaseException submissionClosed() {
        return new WeekPhaseException("submission_closed", "제출 기간이 아니에요.");
    }
}
