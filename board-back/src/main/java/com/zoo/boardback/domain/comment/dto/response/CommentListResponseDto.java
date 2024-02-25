package com.zoo.boardback.domain.comment.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.comment.dto.query.ChildCommentQueryDto;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class CommentListResponseDto {

  private List<CommentResponse> commentListResponse;
  private Long totalElements;

  @Builder
  public CommentListResponseDto(List<CommentResponse> commentListResponse, Long totalElements) {
    this.commentListResponse = commentListResponse;
    this.totalElements = totalElements;
  }

  public CommentListResponseDto(List<CommentResponse> commentListResponse) {
    this.commentListResponse = commentListResponse;
  }

  public static CommentListResponseDto from(Page<CommentQueryDto> comments) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    List<CommentResponse> commentsResponse = comments.stream()
        .map(comment -> CommentResponse.builder()
            .commentId(comment.getCommentId())
            .nickname(comment.getNickname())
            .profileImage(comment.getProfileImage())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt().format(formatter))
            .updatedAt(comment.getUpdatedAt().format(formatter))
            .childCount(comment.getChildCount())
            .delYn(comment.getDelYn())
            .build())
        .collect(Collectors.toList());
    Long totalElements = comments.getTotalElements();
    return new CommentListResponseDto(commentsResponse, totalElements);
  }

  public static CommentListResponseDto of(List<ChildCommentQueryDto> comments) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    List<CommentResponse> commentsResponse = comments.stream()
        .map(comment -> CommentResponse.builder()
            .commentId(comment.getCommentId())
            .nickname(comment.getNickname())
            .profileImage(comment.getProfileImage())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt().format(formatter))
            .updatedAt(comment.getUpdatedAt().format(formatter))
            .childCount(comment.getChildCount())
            .delYn(comment.getDelYn())
            .build())
        .collect(Collectors.toList());
    return new CommentListResponseDto(commentsResponse);
  }
}
