package com.zoo.boardback.global.config.security.filter;

import com.zoo.boardback.global.config.security.data.TokenValidationResultDto;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  public JwtAuthenticationFilter(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    TokenValidationResultDto tokenValidationResultDto = jwtProvider.tryCheckTokenValid(request);
    if (tokenValidationResultDto.isValid()) {
      // check access token
      String token = tokenValidationResultDto.getToken().split(" ")[1].trim();
      Authentication auth = jwtProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else {
      log.info(tokenValidationResultDto.getResultType().getMsg());
    }

    filterChain.doFilter(request, response);
  }
}
