package com.zoo.boardback.domain.searchLog.dao;

import com.zoo.boardback.domain.searchLog.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Integer> {

}
