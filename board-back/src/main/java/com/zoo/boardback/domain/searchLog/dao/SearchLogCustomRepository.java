package com.zoo.boardback.domain.searchLog.dao;

import com.zoo.boardback.domain.searchLog.dto.query.PopularSearchWordDto;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import java.util.List;

public interface SearchLogCustomRepository {
  List<PopularSearchWordDto> getPopularSearchWords(SearchType searchType);
}
