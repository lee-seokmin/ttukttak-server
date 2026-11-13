package com.ttukttak.domain.user.exception;

import com.ttukttak.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateUserException extends BusinessException {
    private DuplicateUserException(String code, String message) {
        super(HttpStatus.CONFLICT, code, message);
    }

    public static DuplicateUserException handle() {
        return new DuplicateUserException("handle_taken", "이미 쓰고 있는 아이디예요.");
    }

    public static DuplicateUserException githubAccount() {
        return new DuplicateUserException("github_account_taken", "이미 가입한 GitHub 계정이에요.");
    }
}
