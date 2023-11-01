package com.zoo.boardback.domain.auth.dto.response;

import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignResponseDto {

  private String email;

  @Builder.Default
  private List<Authority> roles = new ArrayList<>();

  private String token;

  public SignResponseDto(User user) {
    this.email = user.getEmail();
    this.roles = user.getRoles();
  }
}