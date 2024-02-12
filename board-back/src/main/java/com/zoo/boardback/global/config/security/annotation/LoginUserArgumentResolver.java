package com.zoo.boardback.global.config.security.annotation;

import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.global.error.BusinessException;
import com.zoo.boardback.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

  private final UserRepository userRepository;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoginUser.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    long loginUserId = Long.parseLong(authentication.getName());
    return userRepository.findById(loginUserId)
        .orElseThrow(() -> new BusinessException(loginUserId, "JWT", ErrorCode.USER_NOT_FOUND));
  }
}