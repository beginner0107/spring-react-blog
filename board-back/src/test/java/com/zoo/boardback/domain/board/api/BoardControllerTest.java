package com.zoo.boardback.domain.board.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.favorite.dto.object.FavoriteListItem;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class BoardControllerTest extends ControllerTestSupport {

  @DisplayName("게시글에 필요한 정보를 입력 후 등록을 하면 게시글이 저장된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createBoard() throws Exception {
    // given
    String title = "게시글제목";
    String content = "게시글내용";
    PostCreateRequestDto request = createPostRequest(title, content);

    // when & then
    mockMvc.perform(post("/api/v1/board")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("게시글의 제목이 빈 값이라면 게시글을 저장할 수 없다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createBoardEmptyTitle() throws Exception {
    // given
    String title = "";
    String content = "게시글내용";
    PostCreateRequestDto request = createPostRequest(title, content);

    // when & then
    mockMvc.perform(post("/api/v1/board")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("게시글 제목을 입력해주세요."))
        .andExpect(jsonPath("$.field").value("title"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("게시글의 내용이 빈 값이라면 게시글을 저장할 수 없다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createBoardEmptyContent() throws Exception {
    // given
    String title = "게시글제목";
    String content = "";
    PostCreateRequestDto request = createPostRequest(title, content);

    // when & then
    mockMvc.perform(post("/api/v1/board")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("must not be blank"))
        .andExpect(jsonPath("$.field").value("content"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("게시글 번호를 넘기면 게시글 상세 내용을 볼 수 있다.")
  @Test
  void getPost() throws Exception {
    // given
    final int boardNumber = 1;
    String imageUrl = "https://testImage1.png";
    List<String> imageUrls = List.of(imageUrl);
    PostDetailResponseDto response = createPostDetailResponse(boardNumber, imageUrls
    , "테스트1", "테스트내용1", "test123@naver.com", "개구리왕눈이");

    given(boardService.find(any(Integer.class))).willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/board/" + boardNumber))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data.boardNumber").value(boardNumber))
        .andExpect(jsonPath("$.data.title").value("테스트1"))
        .andExpect(jsonPath("$.data.content").value("테스트내용1"))
        .andExpect(jsonPath("$.data.boardImageList[0]").value(imageUrl))
        .andExpect(jsonPath("$.data.writerEmail").value("test123@naver.com"))
        .andExpect(jsonPath("$.data.writerNickname").value("개구리왕눈이"));
  }

  @DisplayName("상세 게시글 페이지에서 좋아요(△, ▽) 버튼을 누를 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void putFavorite() throws Exception {
    // given
    final int boardNumber = 1;

    // when & then
    mockMvc.perform(put("/api/v1/board/" + boardNumber + "/favorite"))
        .andExpect(status().isOk());
  }

  @DisplayName("하나의 게시물에 좋아요를 눌러준 사람들의 목록을 가지고온다.")
  @Test
  void getFavoriteList() throws Exception {
    // given
    final int boardNumber = 1;
    FavoriteListResponseDto response = FavoriteListResponseDto.builder().favoriteList(
        List.of(FavoriteListItem.builder().email("test123@naver.com")
            .nickname("개구리왕눈이")
            .profileImage("https://profileImage.png")
            .build())
    ).isEmpty(false).build();
    given(favoriteService.getFavoriteList(any(Integer.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/board/" + boardNumber + "/favorite-list"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data.favoriteList").hasJsonPath())
        .andExpect(jsonPath("$.data.favoriteList[0].email").value("test123@naver.com"))
        .andExpect(jsonPath("$.data.favoriteList[0].nickname").value("개구리왕눈이"))
        .andExpect(jsonPath("$.data.favoriteList[0].profileImage").value("https://profileImage.png"));
  }

  private static PostDetailResponseDto createPostDetailResponse(int boardNumber, List<String> imageUrls
  , String title, String content, String email, String nickname) {
    return PostDetailResponseDto.builder()
        .boardNumber(boardNumber)
        .title(title)
        .content(content)
        .boardImageList(imageUrls)
        .writerEmail(email)
        .writerNickname(nickname)
        .build();
  }

  private static PostCreateRequestDto createPostRequest(String title, String content) {
    return PostCreateRequestDto.builder()
        .title(title)
        .content(content)
        .boardImageList(List.of("https://testImage.png"))
        .build();
  }
}