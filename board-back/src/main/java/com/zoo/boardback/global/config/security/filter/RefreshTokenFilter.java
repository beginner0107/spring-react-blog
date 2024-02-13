package com.zoo.boardback.global.config.security.filter;

import static com.zoo.boardback.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.zoo.boardback.global.config.security.data.JwtType.REFRESH_TOKEN;

import com.zoo.boardback.domain.auth.application.AuthCookieService;
import com.zoo.boardback.global.config.security.filter.token_condition.JwtTokenCondition;
import com.zoo.boardback.global.config.security.filter.token_condition.JwtTokenConditionFactory;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final JwtProvider jwtTokenProvider;
  private final JwtTokenConditionFactory jwtTokenConditionFactory;
  private final AuthCookieService authCookieService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final var accessTokenDto = jwtTokenProvider.tryCheckTokenValid(request, ACCESS_TOKEN);
    final var refreshTokenDto = jwtTokenProvider.tryCheckTokenValid(request, REFRESH_TOKEN);

    List<JwtTokenCondition> jwtTokenConditions = jwtTokenConditionFactory.createJwtTokenConditions();
    jwtTokenConditions.stream()
        .filter(jwtTokenCondition -> jwtTokenCondition.isSatisfiedBy(accessTokenDto, refreshTokenDto, request))
        .findFirst()
        .ifPresentOrElse(jwtTokenCondition -> jwtTokenCondition.setJwtToken(accessTokenDto, refreshTokenDto, request, response),
            () -> authCookieService.setCookieExpired(response));

    filterChain.doFilter(request, response);
  }
}