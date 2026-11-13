package com.ttukttak.auth.presentation;

import com.ttukttak.auth.presentation.request.CreateUserRequest;
import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.UserResponse;
import com.ttukttak.common.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Swagger 문서만 모아둔 곳이에요. 매핑과 구현은 UserControllerImpl에 있어요. */
@Tag(name = "User", description = "사용자 (로그인 전 임시 CRUD, 쓰기는 local 프로필에서만)")
public interface UserController {

    @Operation(summary = "사용자 만들기")
    @ApiResponse(responseCode = "201", description = "만들었어요")
    @ApiResponse(responseCode = "409", description = "handle_taken, github_account_taken",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "validation_failed",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    UserResponse create(CreateUserRequest request);

    @Operation(summary = "프로필 조회")
    @ApiResponse(responseCode = "200", description = "찾았어요")
    @ApiResponse(responseCode = "404", description = "not_found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    UserResponse get(@Parameter(description = "@ 없는 아이디", example = "jh.park") String handle);

    @Operation(summary = "프로필 수정", description = "보낸 필드만 바뀌어요. 링크에 빈 문자열을 보내면 지워요.")
    @ApiResponse(responseCode = "200", description = "고쳤어요")
    @ApiResponse(responseCode = "404", description = "not_found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "validation_failed",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    UserResponse update(@Parameter(description = "@ 없는 아이디", example = "jh.park") String handle,
                        UpdateUserRequest request);

    @Operation(summary = "사용자 삭제", description = "행을 지우지 않고 deleted_at을 채워요.")
    @ApiResponse(responseCode = "204", description = "지웠어요")
    @ApiResponse(responseCode = "404", description = "not_found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    void delete(@Parameter(description = "@ 없는 아이디", example = "jh.park") String handle);
}
