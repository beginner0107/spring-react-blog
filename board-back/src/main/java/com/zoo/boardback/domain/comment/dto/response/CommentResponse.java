package com.zoo.boardback.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CommentResponse {
  private int commentNumber;
  private String nickname;
  private String profileImage;
  private String content;

  public CommentResponse(int commentNumber, String nickname, String profileImage, String content) {
    this.commentNumber = commentNumber;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.content = content;
  }
}
