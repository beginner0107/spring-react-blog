package com.zoo.boardback.domain.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

  @DisplayName("댓글을 수정한다.")
  @Test
  void increaseViewCount() {
    // given
    Board board = createBoard();
    Comment comment = createComment("댓글을 답니다.!", board);
    String updateContent = "댓글 수정입니다. ㅎㅎ";
    CommentUpdateRequestDto updateRequestDto = createUpdateRequestDto(updateContent);

    // when
    comment.editComment(updateRequestDto);

    // then
    assertThat(comment.getContent()).isEqualTo(updateContent);
  }

  private Board createBoard() {
    return Board.builder()
        .boardNumber(1L)
        .title("글의 제목")
        .content("글의 컨텐츠")
        .favoriteCount(0)
        .viewCount(0)
        .build();
  }

  private Comment createComment(String content, Board board) {
    return Comment.builder()
        .commentNumber(1L)
        .content(content)
        .board(board)
        .build();
  }

  private CommentUpdateRequestDto createUpdateRequestDto(String content) {
    return CommentUpdateRequestDto.builder()
        .boardNumber(1)
        .content(content)
        .build();
  }
}