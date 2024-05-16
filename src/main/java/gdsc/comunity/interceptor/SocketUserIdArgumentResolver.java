package gdsc.comunity.interceptor;

import gdsc.comunity.annotation.UserId;
import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.security.jwt.JwtProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            Message<?> message
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Object userIdObj = accessor.getSessionAttributes().get("userId");
        return Long.parseLong(userIdObj.toString());
    }
}
