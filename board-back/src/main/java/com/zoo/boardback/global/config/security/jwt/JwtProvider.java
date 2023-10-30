package com.zoo.boardback.global.config.security.jwt;

import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.auth.userdetail.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

  @Value("${jwt.secret}")
  private String salt;

  private Key secretKey;
  private final long exp = 1000L * 60 * 60;

  private final AuthService authService;

  @PostConstruct
  protected void init() { // HMAC 알고리즘 사용하여 JWT의 시크릿 키를 생성
    secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
  }

  // 토큰 생성
  public String createToken(String email, List<Authority> roles) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("roles", roles);
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + exp))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  // 권한정보 획득
  // Spring Security 인증과정에서 권한확인을 위한 기능
  public Authentication getAuthentication(String token) {
    CustomUserDetails userDetails = (CustomUserDetails) authService.loadUserByUsername(this.getEmail(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // 토큰에 담겨있는 유저 email 획득
  public String getEmail(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
  }

  // Authorization Header를 통해 인증을 한다.
  public String resolveToken(HttpServletRequest request) {
    return request.getHeader("Authorization");
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      // Bearer 검증
      if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
        return false;
      } else {
        token = token.split(" ")[1].trim();
      }
      Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
      // 만료되었을 시 false
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}
