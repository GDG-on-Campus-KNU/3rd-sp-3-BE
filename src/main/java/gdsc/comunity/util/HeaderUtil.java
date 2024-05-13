package gdsc.comunity.util;

import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.security.jwt.JwtVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public class HeaderUtil {
    public static String extractToken(HttpServletRequest request, String header, String tokenPrefix) {
        String bearerToken = request.getHeader(JwtVO.HEADER);
        if (bearerToken == null) {
            return null;
        }

        if (!bearerToken.startsWith(JwtVO.TOKEN_PREFIX)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_PREFIX_ERROR);
        }

        return bearerToken.substring(7);
    }

    public static String extractToken(StompHeaderAccessor request, String header, String tokenPrefix) {
        String bearerToken = request.getFirstNativeHeader(header);

        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(tokenPrefix)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_PREFIX_ERROR);
        }

        return bearerToken.substring(tokenPrefix.length());
    }
}
