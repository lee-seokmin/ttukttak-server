package com.ttukttak.auth.presentation;

import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.MeResponse;
import com.ttukttak.common.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;

/** Swagger 문서만 모아둔 곳이에요. 매핑과 구현은 MeControllerImpl에 있어요. */
@Tag(name = "Me", description = "내 정보. 로그인은 GET /api/v1/auth/github, 로그아웃은 POST /api/v1/auth/logout")
@ApiResponse(responseCode = "401", description = "unauthenticated",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
public interface MeController {

    @Operation(summary = "내 정보")
    @ApiResponse(responseCode = "200", description = "찾았어요")
    MeResponse get(Principal principal);

    @Operation(summary = "프로필 수정", description = "보낸 필드만 바뀌어요. 링크에 빈 문자열을 보내면 지워요.")
    @ApiResponse(responseCode = "200", description = "고쳤어요")
    @ApiResponse(responseCode = "422", description = "validation_failed",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    MeResponse update(Principal principal, UpdateUserRequest request);

    @Operation(summary = "탈퇴", description = "행을 지우지 않고 deleted_at을 채우고 로그아웃해요.")
    @ApiResponse(responseCode = "204", description = "탈퇴했어요")
    void delete(Principal principal, HttpSession session);
}
