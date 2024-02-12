package com.zoo.boardback.global.config.security.filter.token_condition;

import com.zoo.boardback.global.config.security.data.TokenValidationResultDto;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidCondition implements JwtTokenCondition {

  private final JwtProvider jwtTokenProvider;

  @Override
  public boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
      HttpServletRequest httpRequest) {
    return accessTokenDto.isValid();
  }

  @Override
  public void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
      HttpServletRequest request, HttpServletResponse response) {
    setAuthentication(jwtTokenProvider, accessTokenDto);
  }
}
