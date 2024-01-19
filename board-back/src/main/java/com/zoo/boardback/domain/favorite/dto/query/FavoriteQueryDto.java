package com.zoo.boardback.domain.favorite.dto.query;

import com.zoo.boardback.domain.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteQueryDto {
  private String email;
  private String nickname;
  private String profileImage;

  @Builder
  public FavoriteQueryDto(String email, String nickname, String profileImage) {
    this.email = email;
    this.nickname = nickname;
    this.profileImage = profileImage;
  }

  public static FavoriteQueryDto from(Favorite favorite) {
    return FavoriteQueryDto.builder()
        .email(favorite.getFavoritePk().getUser().getEmail())
        .nickname(favorite.getFavoritePk().getUser().getNickname())
        .profileImage(favorite.getFavoritePk().getUser().getProfileImage())
        .build();
  }
}
