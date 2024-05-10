package gdsc.comunity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //401
    LOGIN_REQUIRED(40100, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN_ERROR(40101, HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_ACCESS_TOKEN_ERROR(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다."),
    EMPTY_REFRESH_TOKEN_ERROR(40103, HttpStatus.UNAUTHORIZED, "해당 유저의 리프레시 토큰이 존재하지 않습니다."),
    INVALID_TOKEN_PREFIX_ERROR(40104, HttpStatus.UNAUTHORIZED, "토큰의 prefix가 올바르지 않습니다"),

    //403
    OAUTH_SERVER_ERROR(40300, HttpStatus.BAD_GATEWAY, "OAuth 서버 에러입니다."),

    //500
    FILE_UPLOAD_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 에러가 발생했습니다."),
    FILE_DOWNLOAD_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 중 에러가 발생했습니다."),
    FILE_TRANSFORM_ERROR(50002, HttpStatus.INTERNAL_SERVER_ERROR, "파일 변환 중 에러가 발생했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
