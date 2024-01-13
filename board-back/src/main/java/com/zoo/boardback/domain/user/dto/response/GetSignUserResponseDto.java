package com.zoo.boardback.domain.user.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
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
