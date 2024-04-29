package gdsc.comunity.security.config;

import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import gdsc.comunity.security.filter.JwtAuthenticationFilter;
import gdsc.comunity.security.filter.JwtExceptionFilter;
import gdsc.comunity.security.info.UserPrincipal;
import gdsc.comunity.security.jwt.JwtProvider;
import gdsc.comunity.security.jwt.RefreshToken;
import gdsc.comunity.security.jwt.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers("/api/login", "/api/auth/register").permitAll()
                                .requestMatchers("/oauth/**").permitAll()
                                .requestMatchers("/api/auth/refresh").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            //1. authentication에서 userId 추출
                            Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();

                            //2. 해당 userId로 redis에서 refresh token 조회 후 삭제
                            Optional<RefreshToken> refreshTokenOP = refreshTokenRepository.findByUserId(userId);
                            if (refreshTokenOP.isEmpty()) {
                                throw new CustomException(ErrorCode.EMPTY_REFRESH_TOKEN_ERROR);
                            }
                            refreshTokenRepository.delete(refreshTokenOP.get());
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.addHeader("message", "logout success");
                            }
                        )
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        LogoutFilter.class
                )
                .addFilterBefore(
                        new JwtExceptionFilter(),
                        JwtAuthenticationFilter.class
                );

        return http.build();
    }
}
