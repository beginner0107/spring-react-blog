package com.zoo.boardback.domain.post.application;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.global.util.redis.RedisUtil;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PostCacheServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private RedisUtil redisUtil;

  @InjectMocks
  private PostCacheService postCacheService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("조회수 기록이 존재하지 않을 때 게시글의 조회수를 조회하고 레디스에 조회수를 저장한다.")
  void addViewCntToRedis() {
    // given
    Long postId = 1L;
    String viewCntKey = "postViewCount::" + postId;

    given(redisUtil.getData(viewCntKey)).willReturn(null);
    given(postRepository.findViewCount(postId)).willReturn(5L);

    // when
    postCacheService.addViewCntToRedis(postId);

    // then
    verify(redisUtil).setData(eq(viewCntKey), eq("6"), eq(Duration.ofMinutes(3)));
  }

  @Test
  @DisplayName("증가한 조회수를 확인하고 증가시킨다.")
  void applyViewCountToRDB() {
    // given
    String viewCntKey = "postViewCount::1";
    Long postId = 1L;
    Long viewCount = 10L;

    when(redisUtil.keys("postViewCount*")).thenReturn(Set.of(viewCntKey));
    when(redisUtil.getData(viewCntKey)).thenReturn(String.valueOf(viewCount));

    // when
    postCacheService.applyViewCountToRDB();

    // then
    verify(postRepository).applyViewCntToRDB(postId, viewCount);
    verify(redisUtil).deleteData(viewCntKey);
  }
}