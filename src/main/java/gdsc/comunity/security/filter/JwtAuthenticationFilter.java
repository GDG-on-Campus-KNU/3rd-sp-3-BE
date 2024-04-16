package gdsc.comunity.security.filter;

import gdsc.comunity.security.info.UserPrincipal;
import gdsc.comunity.security.jwt.JwtProvider;
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
        log.info("JwtAuthenticationFilter is running...");

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            log.info("BearerToken is null or does not start with 'Bearer '");
            log.info("bearerToken : {}", bearerToken);
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = bearerToken.substring(7);

        Claims claims = jwtProvider.validateToken(accessToken);
        log.info("claims: {}", claims);
        if (claims.get("isAccessToken", Boolean.class)) {
            log.info("isAccessToken: {}", claims.get("isAccessToken", Boolean.class));
            log.info("userId: {}", claims.get("userId", String.class));

            //Claim은 String, Integer, Boolean 저장 가능?
            String userIdStr = claims.get("userId", String.class);
            Long userId = Long.parseLong(userIdStr);

            UserPrincipal userPrincipal = new UserPrincipal(userId);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, null); // 권한은 없음

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);
            log.info("UserPrincipal: {}", userPrincipal);
        }

        filterChain.doFilter(request, response);
    }
}
