package com.zoo.boardback.domain.post.application;

import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.global.util.redis.RedisUtil;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCacheService {

  private final PostRepository postRepository;
  private final RedisUtil redisUtil;

  public void addViewCntToRedis(Long postId) {
    String viewCntKey = createViewCntCacheKey(postId);
    if (redisUtil.getData(viewCntKey) != null) {
      redisUtil.increment(viewCntKey);
      return;
    }

    redisUtil.setData(
        viewCntKey,
        String.valueOf(postRepository.findViewCount(postId) + 1),
        Duration.ofMinutes(3)
    );
  }

  /**
   * 3분마다 캐시 데이터를 RDB 반영 후 삭제한다
   */
  @Scheduled(cron = "0 0/3 * * * ?")
  public void applyViewCountToRDB() {
    Set<String> viewCntKeys = redisUtil.keys("postViewCount*");
    if(Objects.requireNonNull(viewCntKeys).isEmpty()) return;

    for (String viewCntKey : viewCntKeys) {
      Long postId = extractPostIdFromKey(viewCntKey);
      Long viewCount = Long.parseLong(redisUtil.getData(viewCntKey));

      postRepository.applyViewCntToRDB(postId, viewCount);
      redisUtil.deleteData(viewCntKey);
    }
  }

  public Long extractPostIdFromKey(String key) {
    return Long.parseLong(key.split("::")[1]);
  }

  public String createViewCntCacheKey(Long id) {
    return createCacheKey("postViewCount", id);
  }

  public String createCacheKey(String cacheType, Long id) {
    return cacheType + "::" + id;
  }
}