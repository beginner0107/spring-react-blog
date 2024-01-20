package com.zoo.boardback.domain.board.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

  @Builder
  public PostSearchResponseDto(String profileImage, String nickname, LocalDateTime createdAt,
      String title, String content, Integer viewCount, Integer favoriteCount, Integer commentCount,
      String boardTitleImage) {
    this.profileImage = profileImage;
    this.nickname = nickname;
    this.createdAt = createdAt;
    this.title = title;
    this.content = content;
    this.viewCount = viewCount;
    this.favoriteCount = favoriteCount;
    this.commentCount = commentCount;
    this.boardTitleImage = boardTitleImage;
  }
}
