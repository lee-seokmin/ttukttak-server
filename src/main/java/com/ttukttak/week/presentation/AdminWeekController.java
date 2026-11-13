package com.ttukttak.week.presentation;

import com.ttukttak.common.exception.ApiError;
import com.ttukttak.week.presentation.request.ImportWeeksRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Swagger 문서만 모아둔 곳이에요. 매핑과 구현은 AdminWeekControllerImpl에 있어요. */
@Tag(name = "Admin", description = "운영자 전용 (role: admin)")
public interface AdminWeekController {

    @Operation(summary = "주차 가져오기", description = "weeks.json 형식을 그대로 받아요. id가 같으면 덮어쓰고, 하나라도 틀리면 모두 취소돼요.")
    @ApiResponse(responseCode = "204", description = "넣었어요")
    @ApiResponse(responseCode = "403", description = "forbidden",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "conflict (회차가 겹침)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "validation_failed",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    void importWeeks(ImportWeeksRequest request);
}
