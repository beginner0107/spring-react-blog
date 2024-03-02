package com.zoo.boardback.docs.search;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.docs.RestDocsSupport;
import com.zoo.boardback.domain.searchLog.api.SearchLogController;
import com.zoo.boardback.domain.searchLog.application.SearchLogService;
import com.zoo.boardback.domain.searchLog.dto.query.PopularSearchWordDto;
import com.zoo.boardback.domain.searchLog.dto.response.PopularSearchWordResponseDto;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

public class SearchLogControllerDocsTest extends RestDocsSupport {

  private final SearchLogService searchLogService = mock(SearchLogService.class);

  @Override
  protected Object initController() {
    return new SearchLogController(searchLogService);
  }

  @DisplayName("검색 타입을 받아 인기 검색어 10개 목록을 조회할 수 있다.")
  @Test
  void getPopularSearchWords() throws Exception {
    // given
    PopularSearchWordResponseDto response = PopularSearchWordResponseDto.withSearchWords(List.of(
            createPopularSearchWordDto()
        ));
    given(searchLogService.getPopularSearchWords(any(SearchType.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/search")
            .param("searchType", "PT")
        )
        .andExpect(status().isOk())
        .andDo(document("search-popularWords",
            preprocessResponse(prettyPrint()),
            queryParameters(
                parameterWithName("searchType").description("검색 타입")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER)
                    .description("코드"),
                fieldWithPath("status").type(JsonFieldType.STRING)
                    .description("상태"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("메시지"),
                fieldWithPath("field").type(JsonFieldType.STRING)
                    .optional()
                    .description("에러 발생 필드명"),
                fieldWithPath("data.searchWords").type(JsonFieldType.ARRAY)
                    .optional()
                    .description("인기 검색어 목록"),
                fieldWithPath("data.searchWords[].searchWord").type(JsonFieldType.STRING)
                    .optional()
                    .description("인기 검색어 이름"),
                fieldWithPath("data.searchWords[].count").type(JsonFieldType.NUMBER)
                    .optional()
                    .description("검색 회수")
            )
        ));
  }

  private PopularSearchWordDto createPopularSearchWordDto() {
      return new PopularSearchWordDto("검색", 3L);
  }
}
