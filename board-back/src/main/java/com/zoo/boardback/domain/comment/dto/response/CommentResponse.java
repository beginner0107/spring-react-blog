package com.zoo.boardback.domain.comment.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@Builder
public class CommentResponse {
  private Long commentId;
  private String nickname;
  private String profileImage;
  private String content;
  private String createdAt;
  private String updatedAt;
  private Long childCount;
  private Boolean delYn;

  @Builder
  public CommentResponse(Long commentId, String nickname, String profileImage, String content
      , String createdAt, String updatedAt, Long childCount, Boolean delYn
  ) {
    this.commentId = commentId;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.childCount = childCount;
    this.delYn = delYn;
  }
}
