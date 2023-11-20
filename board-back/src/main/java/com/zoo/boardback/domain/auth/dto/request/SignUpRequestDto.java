package com.zoo.boardback.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

  @Email
  private String email;
  @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?\\\\d).{8,20}$", message = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.")
  @JsonProperty("password")
  private String password;
  @NotBlank(message = "닉네임은 20자 이하입니다.")
  private String nickname;
  @NotBlank @Pattern(regexp = "^[0-9]{11,13}$", message = "전화번호는 11자에서 13자 사이의 숫자만 가능합니다.")
  private String telNumber;
  @NotBlank
  private String address;
  private String addressDetail;

  public User toEntity(PasswordEncoder passwordEncoder) {
    return User.builder()
        .email(email)
        .password(passwordEncoder.encode(password))
        .nickname(nickname)
        .telNumber(telNumber)
        .address(address)
        .addressDetail(addressDetail)
        .build();
  }
}