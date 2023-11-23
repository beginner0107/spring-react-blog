package com.zoo.boardback.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseDto {

  private String token;
  private int expirationTime;

  public static SignInResponseDto of(String token) {
    return new SignInResponseDto(token, 3600);
  }
}