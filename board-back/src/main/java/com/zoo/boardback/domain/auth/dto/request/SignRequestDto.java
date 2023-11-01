package com.zoo.boardback.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignRequestDto {

  private String email;
  private String password;
  private String nickname;
  private String telNumber;
  private String address;
  private String addressDetail;
}