package com.zoo.boardback.domain.searchLog.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.searchLog.application.SearchLogService;
import com.zoo.boardback.domain.searchLog.dto.response.PopularSearchWordResponseDto;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchLogController {

  private final SearchLogService searchLogService;

  @GetMapping
  public ApiResponse<PopularSearchWordResponseDto> getPopularSearchWords(
      @RequestParam("searchType") String searchType
  ) {
    PopularSearchWordResponseDto searchWords = searchLogService.getPopularSearchWords(
        SearchType.fromCode(searchType));
    return ApiResponse.ok(searchWords);
  }
}
