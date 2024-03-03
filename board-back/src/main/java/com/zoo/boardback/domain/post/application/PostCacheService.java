package com.zoo.boardback.domain.post.application;

import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.global.util.redis.RedisUtil;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostCacheService {

    private final PostRepository postRepository;
    private final RedisUtil redisUtil;
    private static final String CACHE_REFRESH_CRON_EXPRESSION = "0 0/3 * * * ?";
    private static final String POST_VIEW_COUNT_CACHE_PREFIX = "postViewCount";
    private static final String KEY_SEPARATOR = "::";
    private static final String MATCH_ANY = "*";
    public static final long CACHE_EXPIRATION_TIME_MINUTES = 3L;


    public void addViewCntToRedis(Long postId) {
        String viewCntKey = createViewCntCacheKey(postId);
        if (redisUtil.getData(viewCntKey, Long.class).isPresent()) {
            redisUtil.increment(viewCntKey);
            return;
        }

        redisUtil.setDataExpire(
            viewCntKey,
            String.valueOf(postRepository.findViewCount(postId) + 1),
            CACHE_EXPIRATION_TIME_MINUTES
        );
    }

    /**
     * 3분마다 캐시 데이터를 RDB 반영 후 삭제한다
     */
    @Transactional
    @Scheduled(cron = CACHE_REFRESH_CRON_EXPRESSION)
    public void applyViewCountToRDB() {
        Set<String> viewCntKeys = redisUtil.keys(POST_VIEW_COUNT_CACHE_PREFIX + MATCH_ANY);
        if (Objects.requireNonNull(viewCntKeys).isEmpty()) {
            return;
        }

        for (String viewCntKey : viewCntKeys) {
            Long postId = extractPostIdFromKey(viewCntKey);
            Long viewCount = redisUtil.getData(viewCntKey, Long.class)
                .orElseThrow(IllegalArgumentException::new);

            postRepository.applyViewCntToRDB(postId, viewCount);
            redisUtil.deleteData(viewCntKey);
        }
    }

    private Long extractPostIdFromKey(String key) {
        return Long.parseLong(key.split(KEY_SEPARATOR)[1]);
    }

    private String createViewCntCacheKey(Long id) {
        return createCacheKey(POST_VIEW_COUNT_CACHE_PREFIX, id);
    }

    private String createCacheKey(String cacheType, Long id) {
        return cacheType + KEY_SEPARATOR + id;
    }
}