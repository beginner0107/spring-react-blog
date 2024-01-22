package com.zoo.boardback.domain.searchLog.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PopularSearchWordDto {
  private String searchWord;
  private Long count;

  @Builder
  public PopularSearchWordDto(String searchWord, Long count) {
    this.searchWord = searchWord;
    this.count = count;
  }
}
