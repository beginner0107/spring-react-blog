package com.zoo.boardback.domain.comment.dto.response;

import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CommentListResponseDto {

  private List<CommentResponse> commentListResponse;

  public CommentListResponseDto(List<CommentResponse> commentListResponse) {
    this.commentListResponse = commentListResponse;
  }

  public static CommentListResponseDto from(List<CommentQueryDto> comments) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    List<CommentResponse> commentListResponse = comments.stream()
        .map(comment -> CommentResponse.builder()
            .commentNumber(comment.getCommentNumber())
            .nickname(comment.getNickname())
            .profileImage(comment.getProfileImage())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt().format(formatter))
            .updatedAt(comment.getUpdatedAt().format(formatter))
            .build())
        .collect(Collectors.toList());
    return new CommentListResponseDto(commentListResponse);
  }
}
