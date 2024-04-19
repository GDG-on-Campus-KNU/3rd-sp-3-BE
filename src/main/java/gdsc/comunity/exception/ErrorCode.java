package gdsc.comunity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    LOGIN_REQUIRED(40100, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN_ERROR(40101, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    OAUTH_SERVER_ERROR(40300, HttpStatus.BAD_GATEWAY, "OAuth 서버 에러입니다. ");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
