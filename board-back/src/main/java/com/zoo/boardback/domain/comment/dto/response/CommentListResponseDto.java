package com.zoo.boardback.domain.comment.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CommentListResponseDto {

  private List<CommentResponse> commentListResponse;

  public CommentListResponseDto(List<CommentResponse> commentListResponse) {
    this.commentListResponse = commentListResponse;
  }

  public static CommentListResponseDto from(List<CommentResponse> comments) {
    List<CommentResponse> commentListResponse = comments.stream()
        .map(comment -> CommentResponse.builder()
            .commentNumber(comment.getCommentNumber())
            .nickname(comment.getNickname())
            .profileImage(comment.getProfileImage())
            .content(comment.getContent())
            .build())
        .collect(Collectors.toList());
    return new CommentListResponseDto(commentListResponse);
  }
}
