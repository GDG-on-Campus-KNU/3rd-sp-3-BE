package gdsc.comunity.service;

import gdsc.comunity.dto.GoogleUserInfo;
import gdsc.comunity.dto.JwtTokensDto;
import gdsc.comunity.entity.user.Provider;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.repository.user.UserRepository;
import gdsc.comunity.security.jwt.JwtProvider;
import gdsc.comunity.security.jwt.RefreshToken;
import gdsc.comunity.security.jwt.RefreshTokenRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthGoogleService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;
    private final JwtProvider jwtProvider;
    private final WebClient webClientForGetAccessToken = WebClient.builder()
            .baseUrl("https://oauth2.googleapis.com")
            .build();
    private final WebClient webClientForGetUserInfo = WebClient.builder()
            .baseUrl("https://www.googleapis.com/userinfo/v2/me")
            .build();
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String getAccessToken(String code) {
        Map<String, String> responseBody;

        try {
            responseBody = webClientForGetAccessToken.post()
                    .uri("token")
                    .headers(httpHeaders -> {
                        httpHeaders.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    })
                    .body(BodyInserters.fromFormData("code", code)
                            .with("client_id", CLIENT_ID)
                            .with("client_secret", CLIENT_SECRET)
                            .with("redirect_uri", REDIRECT_URI)
                            .with("grant_type", "authorization_code"))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, String>>() {
                    })
                    .block().getBody();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH_SERVER_ERROR);
        }

        return responseBody.get("access_token");
    }

    public Map<String, String> getUserInfoByAccessToken(String accessTokenByOAuth) {
        Map<String, String> responseBody;

        try {
            responseBody = webClientForGetUserInfo.get()
                    .uri("")
                    .headers(httpHeaders -> {
                        httpHeaders.set("Authorization", "Bearer " + accessTokenByOAuth);
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, String>>() {
                    })
                    .block().getBody();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH_SERVER_ERROR);
        }

        return responseBody;
    }

    @Transactional
    public JwtTokensDto loginOrRegister(Map<String, String> userInfo) {
        // 유저가 존재하는지 확인
        String email = userInfo.get("email");
        Optional<User> userOP = userRepository.findByEmail(email);

        if (userOP.isEmpty()) {
            // 유저가 존재하지 않으면 회원가입
            User user = User.builder()
                    .providerId(userInfo.get("id"))
                    .profileImageUrl(userInfo.get("picture"))
                    .provider(Provider.GOOGLE)
                    .email(email)
                    .build();
            User savedUser = userRepository.save(user);
            JwtTokensDto jwtTokensDto = jwtProvider.generateTokens(savedUser.getId());
            saveRefreshToken(savedUser.getId(), jwtTokensDto.refreshToken());
            return jwtTokensDto;
        }

        JwtTokensDto jwtTokensDto = jwtProvider.generateTokens(userOP.get().getId());
        saveRefreshToken(userOP.get().getId(), jwtTokensDto.refreshToken());
        return jwtTokensDto;
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build());
    }
}
