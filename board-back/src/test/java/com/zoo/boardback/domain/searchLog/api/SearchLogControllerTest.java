package com.zoo.boardback.domain.searchLog.api;

import static com.zoo.boardback.domain.searchLog.entity.type.SearchType.POST_WRITER_TITLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.domain.searchLog.dto.response.PopularSearchWordResponseDto;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchLogControllerTest extends ControllerTestSupport {
  @DisplayName("검색 타입을 받아 인기 검색어 10개 목록을 조회할 수 있다.")
  @Test
  void getPopularSearchWords() throws Exception {
    // given
    PopularSearchWordResponseDto response = PopularSearchWordResponseDto.builder()
        .searchWords(List.of()).build();
    given(searchLogService.getPopularSearchWords(any(SearchType.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/search")
            .queryParam("searchType", POST_WRITER_TITLE.getSearchType())
        )
        .andDo(print())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(status().isOk());
  }
}