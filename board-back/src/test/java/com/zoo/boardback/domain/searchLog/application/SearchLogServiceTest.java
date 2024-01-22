package com.zoo.boardback.domain.searchLog.application;

import static com.zoo.boardback.domain.searchLog.entity.type.SearchType.POST_WRITER_TITLE;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.searchLog.dao.SearchLogRepository;
import com.zoo.boardback.domain.searchLog.dto.response.PopularSearchWordResponseDto;
import com.zoo.boardback.domain.searchLog.entity.SearchLog;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SearchLogServiceTest extends IntegrationTestSupport {

  @Autowired
  private SearchLogRepository searchLogRepository;
  @Autowired
  private SearchLogService searchLogService;

  @DisplayName("검색 타입을 받아 인기 검색어 10개 목록을 조회할 수 있다.")
  @Test
  void getPopularSearchWords() {
    // given
    saveSearchLogs(
        "페이커", "페이커", "페이커",
        "구마유스", "구마유스", "구마유스", "구마유스",
        "제우스", "제우스",
        "비디디", "데프트", "표식", "베릴",
        "살라", "누녜즈", "아놀드", "로버트슨"
    );

    // when
    PopularSearchWordResponseDto searchWords = searchLogService.getPopularSearchWords(POST_WRITER_TITLE);

    // then
    assertThat(searchWords.getSearchWords()).hasSize(10);
    assertThat(searchWords.getSearchWords().get(0).getSearchWord()).isEqualTo("구마유스");
    assertThat(searchWords.getSearchWords().get(1).getSearchWord()).isEqualTo("페이커");
    assertThat(searchWords.getSearchWords().get(2).getSearchWord()).isEqualTo("제우스");
  }

  private void saveSearchLogs(String... searchWords) {
    List<SearchLog> searchLogs = Arrays.stream(searchWords)
        .map(this::createSearchLog)
        .collect(Collectors.toList());

    searchLogRepository.saveAll(searchLogs);
  }

  private SearchLog createSearchLog(String searchWord) {
    return SearchLog.builder()
        .searchType(POST_WRITER_TITLE)
        .searchWord(searchWord)
        .build();
  }
}