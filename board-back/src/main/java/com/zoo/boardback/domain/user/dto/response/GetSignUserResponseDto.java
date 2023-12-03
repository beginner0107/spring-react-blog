package com.zoo.boardback.domain.user.dto.response;

import com.zoo.boardback.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetSignUserResponseDto {

  private String email;
  private String nickname;
  private String profileImage;

  public static GetSignUserResponseDto from(User user) {
    return GetSignUserResponseDto.builder()
        .email(user.getEmail())
        .nickname(user.getNickname())
        .profileImage(user.getProfileImage())
        .build();
  }
}
