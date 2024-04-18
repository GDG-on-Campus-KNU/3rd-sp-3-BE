package gdsc.comunity.security.jwt;

import gdsc.comunity.dto.JwtTokensDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public JwtTokensDto generateTokens(Long userId) {
        String accessToken = generateAccessToken(userId);
        String refreshToken = generateRefreshToken();
        return JwtTokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateRefreshToken() {
        Claims claims = Jwts.claims();
        claims.put("isAccessToken", false);

        Date expireDate = new Date(System.currentTimeMillis() + JwtVO.REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .setClaims(claims)
                .setExpiration(expireDate)
                .compact();
    }

    private String generateAccessToken(Long userId){
        Claims claims = Jwts.claims();
        claims.put("userId", userId.toString());
        claims.put("isAccessToken", true);

        Date expireDate = new Date(System.currentTimeMillis() + JwtVO.ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .setClaims(claims)
                .setExpiration(expireDate)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
