package gdsc.comunity.configuration;

import gdsc.comunity.interceptor.UserIdArgumentResolver;
import gdsc.comunity.interceptor.UserIdInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserIdArgumentResolver userIdArgumentResolver;
    private final UserIdInterceptor userIdInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(this.userIdArgumentResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(userIdInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login",
                        "/api/auth/register",
                        "/oauth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**");
    }
}
