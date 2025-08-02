package greencity.config;

import greencity.annotations.CurrentUser;
import greencity.dto.user.UserVO;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.bind.support.WebDataBinderFactory;

public class MockCurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserVO user;

    public MockCurrentUserArgumentResolver(UserVO user) {
        this.user = user;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && parameter.getParameterType().equals(UserVO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        return user;
    }
}

