package com.zoo.boardback.domain.favorite.dto.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteListItem {
  private String email;
  private String nickname;
  private String profileImage;
}
