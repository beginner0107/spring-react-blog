package com.zoo.boardback.domain.comment.dto.query;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CommentQueryDto {
  private int commentNumber;
  private String nickname;
  private String profileImage;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public CommentQueryDto(int commentNumber, String nickname, String profileImage, String content
      , LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    this.commentNumber = commentNumber;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}