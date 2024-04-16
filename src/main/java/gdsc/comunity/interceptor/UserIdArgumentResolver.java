package gdsc.comunity.interceptor;

import gdsc.comunity.annotation.UserId;
import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    //parameter객체의 타입을 확인하는 메소드
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Object userIdObj = webRequest.getAttribute("userId", NativeWebRequest.SCOPE_REQUEST);
        if (userIdObj == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }
        return Long.parseLong((String) userIdObj);
    }
}
