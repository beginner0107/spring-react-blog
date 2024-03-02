package com.zoo.boardback.domain.searchLog.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopularSearchWordDto {

    private String searchWord;
    private Long count;
}
