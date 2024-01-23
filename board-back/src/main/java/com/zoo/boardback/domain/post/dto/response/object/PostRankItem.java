package com.zoo.boardback.domain.post.dto.response.object;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRankItem {

  private Long postId;
  private String title;
  private String content;
  private String postTitleImage;
  private Integer favoriteCount;
  private Integer commentCount;
  private Integer viewCount;
  private String writerNickname;
  private String writerCreatedAt;
  private String writerProfileImage;

  @Builder
  public PostRankItem(Long postId, String title, String content, String postTitleImage,
      Integer favoriteCount, Integer commentCount, Integer viewCount, String writerNickname,
      LocalDateTime writerCreatedAt, String writerProfileImage) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    this.postId = postId;
    this.title = title;
    this.content = content;
    this.postTitleImage = postTitleImage;
    this.favoriteCount = favoriteCount;
    this.commentCount = commentCount;
    this.viewCount = viewCount;
    this.writerNickname = writerNickname;
    this.writerCreatedAt = writerCreatedAt.format(formatter);
    this.writerProfileImage = writerProfileImage;
  }
}
