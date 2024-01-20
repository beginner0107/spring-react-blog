package com.zoo.boardback.domain.board.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.favorite.dto.object.FavoriteListItem;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

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

  @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 목록을 볼 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void getPosts() throws Exception {
    // given
    PostSearchCondition condition = PostSearchCondition.builder()
        .title("제목")
        .content("내용")
        .commentCont("댓글내용")
        .nickname("개구리왕눈이")
        .titleAndContent("제목+내용")
        .build();

    PostSearchResponseDto searchResponse = PostSearchResponseDto.builder()
        .profileImage("http://localhost:3939/profileImage.png")
        .nickname("개구리왕눈이")
        .createdAt(LocalDateTime.now())
        .title("제목")
        .content("내용")
        .viewCount(0)
        .favoriteCount(0)
        .commentCount(0)
        .boardTitleImage("http://localhost:3939/titleImage.png")
        .build();
    Page<PostSearchResponseDto> response = new PageImpl<>(List.of(searchResponse));

    given(boardService.searchPosts(condition, Pageable.ofSize(5)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/board")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("title", "제목")
            .queryParam("nickname", "개구리왕눈이")
            .queryParam("content", "내용")
            .queryParam("commentCont", "댓글내용")
            .queryParam("titleAndContent", "제목+내용")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"));
  }

  @DisplayName("게시글 번호를 넘기면 게시글 상세 내용을 볼 수 있다.")
  @Test
  void getPost() throws Exception {
    // given
    final Long boardNumber = 1L;
    String imageUrl = "https://testImage1.png";
    List<String> imageUrls = List.of(imageUrl);
    PostDetailResponseDto response = createPostDetailResponse(boardNumber, imageUrls
    , "테스트1", "테스트내용1", "test123@naver.com", "개구리왕눈이");

    given(boardService.find(any(Long.class))).willReturn(response);

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
    given(favoriteService.getFavoriteList(any(Long.class)))
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

  @DisplayName("회원은 게시글을 수정할 수 있습니다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void editPost() throws Exception {
    // given
    final int boardNumber = 1;
    String editTitle = "테스트 수정 글의 제목";
    String editContent = "테스트 수정 글의 내용";
    PostUpdateRequestDto request = createPostUpdateRequest(editTitle, editContent);

    // when & then
    mockMvc.perform(put("/api/v1/board/{boardNumber}", boardNumber)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.field").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("회원은 게시글을 삭제할 수 있습니다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void deletePost() throws Exception {
    // given
    final int boardNumber = 1;

    // when & then
    mockMvc.perform(delete("/api/v1/board/{boardNumber}", boardNumber))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(204))
        .andExpect(jsonPath("$.status").value("NO_CONTENT"))
        .andExpect(jsonPath("$.message").value("NO_CONTENT"));
  }

  private static PostDetailResponseDto createPostDetailResponse(Long boardNumber, List<String> imageUrls
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

  private PostUpdateRequestDto createPostUpdateRequest(String title, String content) {
    return PostUpdateRequestDto.builder()
        .title(title)
        .content(content)
        .boardImageList(List.of("https://updateImage1.png",
            "https://updateImage2.png"))
        .build();
  }

}