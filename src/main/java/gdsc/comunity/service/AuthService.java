package gdsc.comunity.service;

import gdsc.comunity.dto.JwtTokensDto;
import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.security.jwt.JwtProvider;
import gdsc.comunity.security.jwt.RefreshToken;
import gdsc.comunity.security.jwt.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    public void validateRefreshToken(String refreshToken) {
        log.info("validateRefreshToken method start, refreshToken : {}", refreshToken);
        Claims claims = jwtProvider.validateToken(refreshToken);
        boolean isAccessToken = claims.get("isAccessToken", Boolean.class);
        if (isAccessToken) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_ERROR);
        }
        log.info("validateRefreshToken method end");
    }

    public JwtTokensDto refreshNewTokens(String refreshToken) {
        log.info("refreshNewTokens method start, refreshToken : {}", refreshToken);
        //1. redis에서 해당 refresh token 조회하고 userId 얻기
        RefreshToken refreshTokenFromRepo = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_ERROR));
        Long userId = refreshTokenFromRepo.getUserId();

        //2. 해당 토큰 삭제
        refreshTokenRepository.delete(refreshTokenFromRepo);

        //3. 새롭게 생성하고 반환
        JwtTokensDto jwtTokensDto = jwtProvider.generateTokens(userId);
        saveRefreshToken(userId, jwtTokensDto.refreshToken());
        return jwtTokensDto;
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build());
    }
}
