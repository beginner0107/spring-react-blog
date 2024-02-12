package com.zoo.boardback.domain.auth.application;

import static com.zoo.boardback.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.zoo.boardback.global.config.security.data.JwtType.REFRESH_TOKEN;

import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import com.zoo.boardback.global.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthCookieService {

  private final JwtProvider jwtProvider;
  private final RedisUtil redisUtil;

  public void setNewCookieInResponse(String userId, List<Authority> roles, String userAgent, HttpServletResponse response) {
    String newRefreshToken = jwtProvider.createAccessToken(REFRESH_TOKEN, userId, roles);
    setTokenInCookie(response, newRefreshToken, (int) REFRESH_TOKEN.getExpiredMillis() / 1000,
        REFRESH_TOKEN.getTokenName());
    String newAccessToken = jwtProvider.createAccessToken(ACCESS_TOKEN, userId, roles);
    setTokenInCookie(response, newAccessToken, (int) REFRESH_TOKEN.getExpiredMillis() / 1000,
        ACCESS_TOKEN.getTokenName());
    redisUtil.setDataExpire(JwtProvider.getRefreshTokenKeyForRedis(userId, userAgent), newRefreshToken, REFRESH_TOKEN.getExpiredMillis());
  }

  private void setTokenInCookie(HttpServletResponse httpResponse, String token, int expiredSeconds, String cookieName) {
    ResponseCookie cookie = ResponseCookie.from(cookieName, token)
        .path("/")
        .sameSite("None")
        .httpOnly(true)
        .maxAge(expiredSeconds)
        .secure(true)
        .build();
    httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public void setCookieExpiredWithRedis(String authId, HttpServletResponse response) {
    setCookieExpired(response);
    redisUtil.deleteData(authId);
  }

  public void setCookieExpired(HttpServletResponse response) {
    setTokenInCookie(response, "", 0, REFRESH_TOKEN.getTokenName());
    setTokenInCookie(response, "", 0, ACCESS_TOKEN.getTokenName());
  }
}