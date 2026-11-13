package com.ttukttak.auth.presentation;

import com.ttukttak.auth.presentation.response.UserResponse;
import com.ttukttak.common.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Swagger 문서만 모아둔 곳이에요. 매핑과 구현은 UserControllerImpl에 있어요. */
@Tag(name = "User", description = "사용자 프로필")
public interface UserController {

    @Operation(summary = "프로필 조회")
    @ApiResponse(responseCode = "200", description = "찾았어요")
    @ApiResponse(responseCode = "404", description = "not_found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    UserResponse get(@Parameter(description = "@ 없는 아이디", example = "jh.park") String handle);
}
