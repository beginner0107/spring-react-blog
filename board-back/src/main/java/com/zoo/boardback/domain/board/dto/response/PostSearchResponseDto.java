package com.zoo.boardback.domain.board.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchResponseDto {
  private String profileImage;
  private String nickname;
  private LocalDateTime createdAt;
  private String title;
  private String content;
  private Integer viewCount;
  private Integer favoriteCount;
  private Integer commentCount;
  private String boardTitleImage;
}
