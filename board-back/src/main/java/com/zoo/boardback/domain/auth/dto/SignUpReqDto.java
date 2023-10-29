package com.zoo.boardback.domain.auth.dto;

import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.auth.entity.enums.Role;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpReqDto {

  private String email;
  private String password;
  private String nickname;
  private String telNumber;
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
