package com.zoo.boardback.domain.searchLog.dto.response;

import com.zoo.boardback.domain.searchLog.dto.query.PopularSearchWordDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PopularSearchWordResponseDto {
  List<PopularSearchWordDto> searchWords;

  @Builder
  public PopularSearchWordResponseDto(List<PopularSearchWordDto> searchWords) {
    this.searchWords = searchWords;
  }
}
