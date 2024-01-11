package com.zoo.boardback.domain.comment.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class CommentUpdateRequestDto {

  public static final int MAX_REQUEST_COMMENT_LENGTH = 300;

  @NotNull(message = "게시글 번호를 입력해주세요")
  private int boardNumber;

  @NotBlank(message = "댓글 내용을 입력해주세요")
  @Size(max = MAX_REQUEST_COMMENT_LENGTH, message = "댓글 내용은 300자 이하로 입력해주세요.")
  private String content;

  @Builder
  public CommentUpdateRequestDto(int boardNumber, String content) {
    this.boardNumber = boardNumber;
    this.content = content;
  }

  public Comment toEntity(User user, Board board) {
    return Comment.builder()
        .board(board)
        .user(user)
        .content(content)
        .build();
  }
}
