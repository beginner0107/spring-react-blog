package com.zoo.boardback.domain.comment.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@Builder
public class CommentResponse {
  private int commentNumber;
  private String nickname;
  private String profileImage;
  private String content;
  private String createdAt;
  private String updatedAt;

  public CommentResponse(int commentNumber, String nickname, String profileImage, String content
      , String createdAt, String updatedAt
  ) {
    this.commentNumber = commentNumber;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
