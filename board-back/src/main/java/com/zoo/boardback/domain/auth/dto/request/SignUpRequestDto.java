package com.zoo.boardback.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

  @Email(message = "이메일 형식을 맞춰주세요")
  private String email;
  @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?\\d).{8,20}$", message = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.")
  @JsonProperty("password")
  private String password;
  @NotBlank
  @Size(max = 20, message = "닉네임은 20자 이하입니다.")
  private String nickname;
  @NotBlank @Pattern(regexp = "^[0-9]{11,13}$", message = "전화번호는 11자에서 13자 사이의 숫자만 가능합니다.")
  private String telNumber;
  @NotBlank
  private String address;
  private String addressDetail;

  @Builder
  public SignUpRequestDto(String email, String password, String nickname, String telNumber,
      String address, String addressDetail) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.telNumber = telNumber;
    this.address = address;
    this.addressDetail = addressDetail;
  }

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