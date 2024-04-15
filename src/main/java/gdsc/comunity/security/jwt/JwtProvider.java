package gdsc.comunity.security.jwt;

import gdsc.comunity.dto.JwtTokensDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public JwtTokensDto generateTokens(Long userId) {
        String accessToken = generateToken(userId, true);
        String refreshToken = generateToken(userId, false);
        return JwtTokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateToken(Long userId, boolean isAccessToken){
        long expireTime = isAccessToken ? JwtVO.ACCESS_TOKEN_EXPIRATION_TIME : JwtVO.REFRESH_TOKEN_EXPIRATION_TIME;
        Date expireDate = new Date(System.currentTimeMillis() + expireTime);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .claim("userId",userId.toString())
                .claim("isAccessToken", isAccessToken)
                .setExpiration(expireDate)
                .compact();
    }

    public Claims validateToken(String token) {
        return null; //TODO: token 검증
    }
}
