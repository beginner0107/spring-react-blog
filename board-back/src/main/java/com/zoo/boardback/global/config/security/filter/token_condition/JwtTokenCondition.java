package com.zoo.boardback.global.config.security.filter.token_condition;

import static com.zoo.boardback.global.config.security.data.JwtValidationType.EXPIRED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.VALID;

import com.zoo.boardback.global.config.security.data.TokenValidationResultDto;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

public interface JwtTokenCondition {

  boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto,
      TokenValidationResultDto refreshTokenDto,
      HttpServletRequest httpRequest);

  void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
      HttpServletRequest request, HttpServletResponse response);

  default void setAuthentication(JwtProvider jwtTokenProvider,
      TokenValidationResultDto jwtTokenDto) {
    SecurityContextHolder.getContext()
        .setAuthentication(jwtTokenProvider.getAuthentication(jwtTokenDto.getToken()));
  }

  default boolean isTokenValid(TokenValidationResultDto jwtTokenDto) {
    return jwtTokenDto.getResultType() == VALID;
  }

  default boolean isTokenExpired(TokenValidationResultDto jwtTokenDto) {
    return jwtTokenDto.getResultType() == EXPIRED;
  }

}