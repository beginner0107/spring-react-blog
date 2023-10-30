package com.zoo.boardback.domain.auth.dto.response;

import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResDto {

  private String email;

  private List<Authority> roles = new ArrayList<>();

  private String token;

  public SignInResDto(User user) {
    this.email = user.getEmail();
    this.roles = user.getRoles();
  }
}
