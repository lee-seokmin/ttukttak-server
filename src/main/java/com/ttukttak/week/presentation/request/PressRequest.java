package com.ttukttak.week.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 프론트가 1초 동안 모아서 보내요. */
public record PressRequest(
        @NotNull(message = "횟수를 입력해 주세요.")
        @Min(value = 1, message = "1부터 20까지예요.") @Max(value = 20, message = "1부터 20까지예요.")
        Integer count
) {
}
