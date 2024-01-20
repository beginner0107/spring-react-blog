package com.zoo.boardback.domain.comment.dto.response;

import static lombok.AccessLevel.PRIVATE;

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

  public static CommentListResponseDto from(Page<CommentQueryDto> comments) {
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
    Long totalElements = comments.getTotalElements();
    return new CommentListResponseDto(commentListResponse, totalElements);
  }
}
