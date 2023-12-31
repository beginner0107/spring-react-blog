package com.zoo.boardback.domain.favorite.dto.response;

import static java.util.stream.Collectors.toList;

import com.zoo.boardback.domain.favorite.dto.object.FavoriteListItem;
import com.zoo.boardback.domain.favorite.dto.query.FavoriteQueryDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteListResponseDto {
  private List<FavoriteListItem> favoriteList;
  private boolean isEmpty;

  @Builder
  public FavoriteListResponseDto(List<FavoriteListItem> favoriteList, boolean isEmpty) {
    this.favoriteList = favoriteList;
    this.isEmpty = isEmpty;
  }

  public static FavoriteListResponseDto from(List<FavoriteQueryDto> favorites) {
    List<FavoriteListItem> favoriteList = null;
    boolean isEmpty = true;
    if (!favorites.isEmpty()) {
      isEmpty = false;
      favoriteList = favorites.stream().map(favorite ->
          FavoriteListItem.builder()
              .email(favorite.getEmail())
              .nickname(favorite.getNickname())
              .profileImage(favorite.getProfileImage())
              .build()
      ).collect(toList());
    }
    return new FavoriteListResponseDto(favoriteList, isEmpty);
  }
}
