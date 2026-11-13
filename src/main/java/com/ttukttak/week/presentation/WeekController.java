package com.ttukttak.week.presentation;

import com.ttukttak.common.exception.ApiError;
import com.ttukttak.week.presentation.request.PressRequest;
import com.ttukttak.week.presentation.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;

/** Swagger 문서만 모아둔 곳이에요. 매핑과 구현은 WeekControllerImpl에 있어요. */
@Tag(name = "Week", description = "주차")
public interface WeekController {

    @Operation(summary = "이번 주", description = "진행 중인 주차가 없으면 week가 null이에요.")
    @ApiResponse(responseCode = "200", description = "찾았어요")
    CurrentWeekResponse current();

    @Operation(summary = "지난 주제", description = "결과가 나온 주제 주만, 최신순. 휴식 주는 빠져요.")
    @ApiResponse(responseCode = "200", description = "찾았어요")
    WeekListResponse archive(@Parameter(description = "이전 응답의 nextCursor") String cursor,
                             @Parameter(description = "기본 12, 최대 50") int limit);

    @Operation(summary = "주차 결과")
    @ApiResponse(responseCode = "200", description = "찾았어요")
    @ApiResponse(responseCode = "404", description = "not_found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    WeekDetailResponse get(@Parameter(description = "주차 id", example = "2026-w47") String weekId);

    @Operation(summary = "테마 버튼 누르기", description = "1초 동안 모아서 보내요.")
    @ApiResponse(responseCode = "200", description = "더했어요")
    @ApiResponse(responseCode = "404", description = "not_found (진행 중인 주차 없음)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    PressResponse press(PressRequest request);

    @Operation(summary = "이번 주 참가하기", description = "여러 번 불러도 한 번만 세요. 제작 기간에만 돼요.")
    @ApiResponse(responseCode = "200", description = "참가했어요")
    @ApiResponse(responseCode = "409", description = "break_week, submission_closed",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ParticipationResponse join(Principal principal);

    @Operation(summary = "참가 취소", description = "제작 기간에만 돼요.")
    @ApiResponse(responseCode = "200", description = "취소했어요")
    @ApiResponse(responseCode = "409", description = "break_week, submission_closed",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ParticipationResponse leave(Principal principal);
}
