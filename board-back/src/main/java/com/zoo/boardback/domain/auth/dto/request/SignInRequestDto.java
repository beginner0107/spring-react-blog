package com.zoo.boardback.domain.auth.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequestDto {
  @Email
  private String email;
  @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?\\\\d).{8,20}$", message = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.")
  @JsonProperty("password")
  private String password;

  @Builder
  public SignInRequestDto(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
