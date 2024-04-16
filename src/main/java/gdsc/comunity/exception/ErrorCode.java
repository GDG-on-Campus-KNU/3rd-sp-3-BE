package gdsc.comunity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    LOGIN_REQUIRED(40100, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
