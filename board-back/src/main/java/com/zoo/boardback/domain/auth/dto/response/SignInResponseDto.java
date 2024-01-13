package com.zoo.boardback.domain.auth.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class SignInResponseDto {

  private String token;
  private int expirationTime;

  public static SignInResponseDto of(String token, int expirationTime) {
    return new SignInResponseDto(token, expirationTime);
  }

  @Builder
  public SignInResponseDto(String token, int expirationTime) {
    this.token = token;
    this.expirationTime = expirationTime;
  }
}