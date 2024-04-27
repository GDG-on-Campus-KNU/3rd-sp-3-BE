package gdsc.comunity.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.comunity.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            //fixme : 예외처리 공통 Response 사용해서 처리하기
            ObjectMapper om = new ObjectMapper();
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(e.getErrorCode().getStatus().value());
            String responseBody = om.writeValueAsString(e.getErrorCode().getMessage());
            response.getWriter().println(responseBody);

            log.error("CustomException occurred : {}", e.getMessage());
            log.error("CustomException occurred : {}", e.getStackTrace());

        } catch (Exception e) {
            //fixme : 예외처리 공통 Response 사용해서 처리하기
            log.error("Exception occurred : {}", e.getMessage());
            log.error("Exception occurred : {}", e.getStackTrace());
        }
    }
}
