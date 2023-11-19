package com.zoo.boardback.global.config.security.data;

import lombok.Getter;

@Getter
public class TokenValidationResultDto {

  private final boolean isValid;
  private final JwtValidationType resultType;
  private final String token;

  public TokenValidationResultDto(boolean isValid, JwtValidationType resultType, String token) {
    this.isValid = isValid;
    this.resultType = resultType;
    this.token = token;
  }

  public static TokenValidationResultDto of(boolean isValid, JwtValidationType tokenType, String token) {
    return new TokenValidationResultDto(isValid, tokenType, token);
  }

  public static TokenValidationResultDto of(boolean isValid, JwtValidationType tokenType) {
    return new TokenValidationResultDto(isValid, tokenType, null);
  }
}
