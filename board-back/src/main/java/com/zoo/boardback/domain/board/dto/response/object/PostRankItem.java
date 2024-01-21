package com.zoo.boardback.domain.board.dto.response.object;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRankItem {

  private Long boardNumber;
  private String title;
  private String content;
  private String boardTitleImage;
  private Integer favoriteCount;
  private Integer commentCount;
  private Integer viewCount;
  private String writerNickname;
  private String writerCreatedAt;
  private String writerProfileImage;

  @Builder
  public PostRankItem(Long boardNumber, String title, String content, String boardTitleImage,
      Integer favoriteCount, Integer commentCount, Integer viewCount, String writerNickname,
      LocalDateTime writerCreatedAt, String writerProfileImage) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    this.boardNumber = boardNumber;
    this.title = title;
    this.content = content;
    this.boardTitleImage = boardTitleImage;
    this.favoriteCount = favoriteCount;
    this.commentCount = commentCount;
    this.viewCount = viewCount;
    this.writerNickname = writerNickname;
    this.writerCreatedAt = writerCreatedAt.format(formatter);
    this.writerProfileImage = writerProfileImage;
  }
}
