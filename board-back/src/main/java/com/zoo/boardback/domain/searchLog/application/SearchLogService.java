package com.zoo.boardback.domain.searchLog.application;

import com.zoo.boardback.domain.searchLog.dao.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchLogService {

  private final SearchLogRepository searchLogRepository;
}
