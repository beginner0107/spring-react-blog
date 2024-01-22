package com.zoo.boardback.global.converter.search;

import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import org.springframework.core.convert.converter.Converter;

public class SearchConverter implements Converter<String, SearchType> {


  @Override
  public SearchType convert(String source) {
    return SearchType.fromCode(source.toUpperCase());
  }
}
