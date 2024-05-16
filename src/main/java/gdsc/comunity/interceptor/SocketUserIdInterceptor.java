package gdsc.comunity.interceptor;

import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.security.info.UserPrincipal;
import gdsc.comunity.security.jwt.JwtProvider;
import gdsc.comunity.security.jwt.JwtVO;
import gdsc.comunity.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketUserIdInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // STOMP Header에서 토큰 추출
            String accessToken = HeaderUtil.extractToken(accessor, JwtVO.HEADER, JwtVO.TOKEN_PREFIX);
            log.info("accessToken: " + accessToken);

            // 토큰 검증
            Claims claims = jwtProvider.validateToken(accessToken);
            Long userId = null;

            if (claims.get("isAccessToken", Boolean.class)) {
                //Claim은 String, Integer, Boolean만 저장 가능하다.
                String userIdStr = claims.get("userId", String.class);

                userId = Long.parseLong(userIdStr);
                accessor.getSessionAttributes().put("userId", userId);
            }
            else {
                throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN_ERROR);
            }
        }
        // 메시지 반환
        return message;
    }
}
