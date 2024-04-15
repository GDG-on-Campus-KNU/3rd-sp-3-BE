package gdsc.comunity.controller;

import gdsc.comunity.dto.JwtTokensDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import gdsc.comunity.service.OAuthService;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @GetMapping("/login/google/callback")
    public ResponseEntity<?> loginByGoogleCallback(
            @RequestParam("code") String code
    ) {
        //1. 해당 코드로 구글에게 토큰을 요청
        String tokenByGoogle = oAuthService.getAccessToken(code);
        log.info("tokenByGoogle : {}", tokenByGoogle);

        //2. 토큰을 받아서 유저 정보를 가져옴
        Map<String, String> userInfo = oAuthService.getUserInfoByAccessToken(tokenByGoogle);
        log.info("userInfo : {}", userInfo);

        //3. 유저 정보를 토대로 회원가입 or 로그인
        JwtTokensDto jwtDto = oAuthService.loginOrRegister(userInfo);
        return new ResponseEntity<>(jwtDto, HttpStatus.OK);
    }
}
