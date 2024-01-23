package com.zoo.boardback.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

  @DisplayName("게시글의 조회수를 증가시킨다.")
  @Test
  void increaseViewCount() {
    // given
    int viewCount = 0;
    Post post = createPost(viewCount, 0);

    // when
    post.increaseFavoriteCount();

    // then
    assertThat(post.getFavoriteCount()).isEqualTo(viewCount + 1);
  }

  @DisplayName("게시글의 추천수를 증가시킨다.")
  @Test
  void increaseFavoriteCount() {
    // given
    int favoriteCount = 0;
    Post post = createPost(0, favoriteCount);

    // when
    post.increaseFavoriteCount();

    // then
    assertThat(post.getFavoriteCount()).isEqualTo(favoriteCount + 1);
  }

  @DisplayName("게시글의 추천수를 감소시킨다.")
  @Test
  void decreaseFavoriteCount() {
    // given
    int favoriteCount = 1;
    Post post = createPost(0, favoriteCount);

    // when
    post.decreaseFavoriteCount();

    // then
    assertThat(post.getFavoriteCount()).isEqualTo(favoriteCount - 1);
  }

  private static Post createPost(int viewCount, int favoriteCount) {
    return Post.builder()
        .title("글의 제목")
        .content("글의 컨텐츠")
        .favoriteCount(favoriteCount)
        .viewCount(viewCount)
        .build();
  }
}