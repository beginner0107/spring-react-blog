package com.zoo.boardback.global.config.security.jwt;

import static com.zoo.boardback.global.config.security.data.JwtValidationType.EMPTY;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.EXPIRED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.MALFORMED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.NOT_EXIST_BEARER;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.UNKNOWN;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.UNSUPPORTED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.VALID;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.WRONG_SIGNATURE;

import com.zoo.boardback.domain.auth.application.JpaUserDetailsService;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.global.config.security.data.TokenValidationResultDto;
import com.zoo.boardback.global.config.security.exception.BearerTokenMissingException;
import com.zoo.boardback.global.config.security.exception.EmptyJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

  @Value("${jwt.secret}")
  private String salt;

  private Key secretKey;

  // 만료시간 : 1Hour
  private final long exp = 1000L * 60 * 60;

  private final JpaUserDetailsService userDetailsService;

  @PostConstruct
  protected void init() {
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
    UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // 토큰에 담겨있는 유저 account 획득
  public String getEmail(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
  }

  // Authorization Header를 통해 인증을 한다.
  public String resolveToken(HttpServletRequest request) {
    return request.getHeader("Authorization");
  }

  // 토큰 검증
  public void validateToken(String token) {
      // Bearer 검증
      if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
        throw new BearerTokenMissingException();
      } else {
        token = token.split(" ")[1].trim();
      }
      Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
  }

  public TokenValidationResultDto tryCheckTokenValid(HttpServletRequest req) {
    try {
      String token = resolveToken(req);
      if (token == null) {
        throw new EmptyJwtException();
      }
      validateToken(token);
      return TokenValidationResultDto.of(true, VALID, token);
    } catch (MalformedJwtException e) {
      return TokenValidationResultDto.of(false, MALFORMED);
    } catch (ExpiredJwtException e) {
      return TokenValidationResultDto.of(false, EXPIRED);
    } catch (UnsupportedJwtException e) {
      return TokenValidationResultDto.of(false, UNSUPPORTED);
    } catch (SignatureException e) {
      return TokenValidationResultDto.of(false, WRONG_SIGNATURE);
    } catch (EmptyJwtException e) {
      return TokenValidationResultDto.of(false, EMPTY);
    } catch (BearerTokenMissingException e) {
      return TokenValidationResultDto.of(false, NOT_EXIST_BEARER);
    } catch (Exception e) {
      return TokenValidationResultDto.of(false, UNKNOWN);
    }
  }
}

