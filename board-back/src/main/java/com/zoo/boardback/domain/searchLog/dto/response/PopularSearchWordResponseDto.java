package com.zoo.boardback.domain.searchLog.dto.response;

import com.zoo.boardback.domain.searchLog.dto.query.PopularSearchWordDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PopularSearchWordResponseDto {
  List<PopularSearchWordDto> searchWords;

  private PopularSearchWordResponseDto(List<PopularSearchWordDto> searchWords) {
    this.searchWords = searchWords;
  }

  public static PopularSearchWordResponseDto withSearchWords(List<PopularSearchWordDto> searchWords) {
    return new PopularSearchWordResponseDto(searchWords);
  }
}
