package gdsc.comunity.security.filter;

import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.security.info.UserPrincipal;
import gdsc.comunity.security.jwt.JwtProvider;
import gdsc.comunity.security.jwt.JwtVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader(JwtVO.HEADER);

        if (bearerToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!bearerToken.startsWith(JwtVO.TOKEN_PREFIX)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_PREFIX_ERROR);
        }

        String accessToken = bearerToken.substring(7);
        Claims claims;
        try {
            claims = jwtProvider.validateToken(accessToken);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN_ERROR);
        }

        if (claims.get("isAccessToken", Boolean.class)) {
            //Claim은 String, Integer, Boolean만 저장 가능하다.
            String userIdStr = claims.get("userId", String.class);
            Long userId = Long.parseLong(userIdStr);

            UserPrincipal userPrincipal = new UserPrincipal(userId);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, null); // 권한은 없음

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }
}
