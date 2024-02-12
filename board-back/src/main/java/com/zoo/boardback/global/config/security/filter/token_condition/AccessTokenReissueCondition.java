package com.zoo.boardback.global.config.security.filter.token_condition;

import static org.springframework.http.HttpHeaders.USER_AGENT;

import com.zoo.boardback.domain.auth.application.AuthCookieService;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.global.config.security.data.TokenValidationResultDto;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import com.zoo.boardback.global.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenReissueCondition implements JwtTokenCondition {

  private final AuthCookieService authCookieService;
  private final JwtProvider jwtTokenProvider;
  private final RedisUtil redisUtil;

  @Override
  public boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto,
      TokenValidationResultDto refreshTokenDto,
      HttpServletRequest httpRequest) {
    return isTokenExpired(accessTokenDto) &&
        isTokenValid(refreshTokenDto) &&
        isTokenInRedis(refreshTokenDto, httpRequest.getHeader(USER_AGENT));
  }

  @Override
  public void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    setAuthentication(jwtTokenProvider, refreshTokenDto);

    String userId = String.valueOf(jwtTokenProvider.getUserId(refreshTokenDto.getToken()));
    List<Authority> roles = jwtTokenProvider.getRoles(refreshTokenDto.getToken());
    authCookieService.setNewCookieInResponse(userId, roles, httpRequest.getHeader(USER_AGENT), httpResponse);
  }

  private boolean isTokenInRedis(TokenValidationResultDto refreshTokenDto, String userAgent) {
    long authId = jwtTokenProvider.getUserId(refreshTokenDto.getToken());
    String refreshTokenKey = JwtProvider.getRefreshTokenKeyForRedis(String.valueOf(authId), userAgent);
    Optional<String> tokenInRedis = redisUtil.getData(refreshTokenKey, String.class);
    return tokenInRedis.isPresent() && tokenInRedis.get().equals(refreshTokenDto.getToken());
  }
}