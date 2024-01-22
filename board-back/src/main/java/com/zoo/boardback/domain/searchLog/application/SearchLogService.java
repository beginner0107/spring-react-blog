package com.zoo.boardback.domain.searchLog.application;

import com.zoo.boardback.domain.searchLog.dao.SearchLogRepository;
import com.zoo.boardback.domain.searchLog.dto.response.PopularSearchWordResponseDto;
import com.zoo.boardback.domain.searchLog.dto.query.PopularSearchWordDto;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchLogService {

  private final SearchLogRepository searchLogRepository;

  public PopularSearchWordResponseDto getPopularSearchWords(SearchType searchType) {
    List<PopularSearchWordDto> searchWords = searchLogRepository.getPopularSearchWords(searchType);
    return PopularSearchWordResponseDto.builder().searchWords(searchWords).build();
  }
}
