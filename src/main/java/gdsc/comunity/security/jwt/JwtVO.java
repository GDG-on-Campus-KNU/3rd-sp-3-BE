package gdsc.comunity.security.jwt;

public interface JwtVO {
    public static final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1hour
    public static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 1week
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

}
