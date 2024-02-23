package com.zoo.boardback.global.config.security.jwt;

import static com.zoo.boardback.domain.user.entity.role.UserRole.GENERAL_USER;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.EMPTY;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.EXPIRED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.MALFORMED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.NOT_EXIST_BEARER;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.UNKNOWN;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.UNSUPPORTED;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.VALID;
import static com.zoo.boardback.global.config.security.data.JwtValidationType.WRONG_SIGNATURE;

import com.zoo.boardback.global.config.security.data.JwtUserDetails;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.global.config.security.data.JwtType;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private static final String ROLES = "roles";
  private static final String SEPARATOR = ",";

  final Key secretKey;

  public JwtProvider(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public static String getRefreshTokenKeyForRedis(String userId, String userAgent) {
    String encodedUserRole = Base64.getEncoder().encodeToString((userAgent == null ? "" : userAgent).getBytes());
    return "refreshToken:" + userId + ":" + encodedUserRole;
  }

  public long getUserId(String token) {
    return Long.parseLong(getClaim(token).getSubject());
  }

  private Claims getClaim(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }


  // 토큰 생성
  public String createAccessToken(JwtType jwtType, String userPk, List<Authority> roles) {
    Claims claims = Jwts.claims().setSubject(userPk);
    setRoles(claims, roles);
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + jwtType.getExpiredMillis()))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  private static void setRoles(Claims claims, List<Authority> authorities) {
    String roles = authorities.stream().map(Authority::getRoleName)
            .collect(Collectors.joining(SEPARATOR));
    claims.put(ROLES, String.join(SEPARATOR, roles));
  }

  // 권한정보 획득
  // Spring Security 인증과정에서 권한확인을 위한 기능
  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    List<String> roles = getRolesBy(claims);
    UserDetails userDetails = new JwtUserDetails(claims.getSubject(), roles);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // 토큰에 담겨있는 유저 account 획득
  public String getEmail(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
        .getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req, JwtType jwtType) {
    Optional<Cookie> accessToken = Arrays.stream(req.getCookies())
        .filter(cookie -> cookie.getName().equals(jwtType.getTokenName()))
        .findFirst();
    if (accessToken.isEmpty()) {
      throw new EmptyJwtException();
    }
    return accessToken.get().getValue();
  }

  public TokenValidationResultDto tryCheckTokenValid(HttpServletRequest req, JwtType jwtType) {
    try {
      String token = resolveToken(req, jwtType);
      getUserId(token);
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

  public List<Authority> getRoles(String token) {
    Claims claims = getClaim(token);
    List<String> roles = getRolesBy(claims);
    List<Authority> authorityRoles = new ArrayList<>();
    for (String role : roles) {
      authorityRoles.add(Authority.builder().role(GENERAL_USER).build());
    }
    return authorityRoles;
  }

  private static List<String> getRolesBy(Claims claims) {
    return List.of(claims.get(ROLES)
        .toString()
        .split(SEPARATOR));
  }
}

