package com.zoo.boardback.domain.board.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardTest {

  @DisplayName("게시글의 조회수를 증가시킨다.")
  @Test
  void increaseViewCount() {
    // given
    int viewCount = 0;
    Board board = createBoard(viewCount, 0);

    // when
    board.increaseFavoriteCount();

    // then
    assertThat(board.getFavoriteCount()).isEqualTo(viewCount + 1);
  }

  @DisplayName("게시글의 추천수를 증가시킨다.")
  @Test
  void increaseFavoriteCount() {
    // given
    int favoriteCount = 0;
    Board board = createBoard(0, favoriteCount);

    // when
    board.increaseFavoriteCount();

    // then
    assertThat(board.getFavoriteCount()).isEqualTo(favoriteCount + 1);
  }

  @DisplayName("게시글의 추천수를 감소시킨다.")
  @Test
  void decreaseFavoriteCount() {
    // given
    int favoriteCount = 1;
    Board board = createBoard(0, favoriteCount);

    // when
    board.decreaseFavoriteCount();

    // then
    assertThat(board.getFavoriteCount()).isEqualTo(favoriteCount - 1);
  }

  private static Board createBoard(int viewCount, int favoriteCount) {
    return Board.builder()
        .boardNumber(1L)
        .title("글의 제목")
        .content("글의 컨텐츠")
        .favoriteCount(favoriteCount)
        .viewCount(viewCount)
        .build();
  }
}