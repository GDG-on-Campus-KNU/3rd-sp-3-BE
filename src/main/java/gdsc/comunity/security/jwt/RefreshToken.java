package gdsc.comunity.security.jwt;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@RedisHash(value = "refreshToken", timeToLive = JwtVO.REFRESH_TOKEN_EXPIRATION_TIME / 1000)
public class RefreshToken {
    @Id
    private String refreshToken;

    @Indexed
    private Long userId;

    @Builder
    private RefreshToken(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
