package com.zoo.boardback.domain.post.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.domain.post.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.post.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.post.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostsTop3ResponseDto;
import com.zoo.boardback.domain.post.dto.response.object.PostRankItem;
import com.zoo.boardback.domain.favorite.dto.object.FavoriteListItem;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

class PostControllerTest extends ControllerTestSupport {

  @DisplayName("게시글에 필요한 정보를 입력 후 등록을 하면 게시글이 저장된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createPost() throws Exception {
    // given
    String title = "게시글제목";
    String content = "게시글내용";
    PostCreateRequestDto request = createPostRequest(title, content);

    // when & then
    mockMvc.perform(post("/api/v1/post")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("게시글의 제목이 빈 값이라면 게시글을 저장할 수 없다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createPostEmptyTitle() throws Exception {
    // given
    String title = "";
    String content = "게시글내용";
    PostCreateRequestDto request = createPostRequest(title, content);

    // when & then
    mockMvc.perform(post("/api/v1/post")
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
  void createPostEmptyContent() throws Exception {
    // given
    String title = "게시글제목";
    String content = "";
    PostCreateRequestDto request = createPostRequest(title, content);

    // when & then
    mockMvc.perform(post("/api/v1/post")
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
        .titleOrContent("제목+내용")
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
        .postTitleImage("http://localhost:3939/titleImage.png")
        .build();
    Page<PostSearchResponseDto> response = new PageImpl<>(List.of(searchResponse));

    given(postService.searchPosts(condition, Pageable.ofSize(5)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/post")
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
    final Long postId = 1L;
    String imageUrl = "https://testImage1.png";
    List<String> imageUrls = List.of(imageUrl);
    PostDetailResponseDto response = createPostDetailResponse(postId, imageUrls
    , "테스트1", "테스트내용1", "test123@naver.com", "개구리왕눈이");

    given(postService.find(any(Long.class))).willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/post/" + postId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data.postId").value(postId))
        .andExpect(jsonPath("$.data.title").value("테스트1"))
        .andExpect(jsonPath("$.data.content").value("테스트내용1"))
        .andExpect(jsonPath("$.data.postImageList[0]").value(imageUrl))
        .andExpect(jsonPath("$.data.writerEmail").value("test123@naver.com"))
        .andExpect(jsonPath("$.data.writerNickname").value("개구리왕눈이"));
  }

  @DisplayName("상세 게시글 페이지에서 좋아요(△, ▽) 버튼을 누를 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void putFavorite() throws Exception {
    // given
    final int postId = 1;

    // when & then
    mockMvc.perform(put("/api/v1/post/" + postId + "/favorite"))
        .andExpect(status().isOk());
  }

  @DisplayName("하나의 게시물에 좋아요를 눌러준 사람들의 목록을 가지고온다.")
  @Test
  void getFavoriteList() throws Exception {
    // given
    final int postId = 1;
    FavoriteListResponseDto response = FavoriteListResponseDto.builder().favoriteList(
        List.of(FavoriteListItem.builder().email("test123@naver.com")
            .nickname("개구리왕눈이")
            .profileImage("https://profileImage.png")
            .build())
    ).isEmpty(false).build();
    given(favoriteService.getFavoriteList(any(Long.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/post/" + postId + "/favorite-list"))
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
    final int postId = 1;
    String editTitle = "테스트 수정 글의 제목";
    String editContent = "테스트 수정 글의 내용";
    PostUpdateRequestDto request = createPostUpdateRequest(editTitle, editContent);

    // when & then
    mockMvc.perform(put("/api/v1/post/{postId}", postId)
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
    final int postId = 1;

    // when & then
    mockMvc.perform(delete("/api/v1/post/{postId}", postId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(204))
        .andExpect(jsonPath("$.status").value("NO_CONTENT"))
        .andExpect(jsonPath("$.message").value("NO_CONTENT"));
  }

  @DisplayName("회원은 상위 3개의 게시물을 볼 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void getPostsTop3() throws Exception {
    // given
    PostsTop3ResponseDto response = PostsTop3ResponseDto.builder()
        .top3List(
            List.of(
                createPostRankItem("제목3", "내용3", 3),
                createPostRankItem("제목2", "내용2", 2),
                createPostRankItem("제목1", "내용1", 1)
            )
        ).build();

    given(postService.getTop3Posts(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/v1/post/top3"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.field").isEmpty())
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.top3List[0].title").value("제목3"))
        .andExpect(jsonPath("$.data.top3List[0].content").value("내용3"))
        .andExpect(jsonPath("$.data.top3List[0].favoriteCount").value(3));
  }

  private PostRankItem createPostRankItem(
      String title, String content,
      Integer favoriteCount
  ) {
    return PostRankItem
        .builder()
        .postId(3L)
        .title(title)
        .content(content)
        .postTitleImage("http://localhost:3939/titleImage.png")
        .favoriteCount(favoriteCount)
        .commentCount(0)
        .viewCount(0)
        .writerNickname("닉네임3")
        .writerCreatedAt(LocalDateTime.now())
        .writerProfileImage("http://localhost:3939/profileImage.png")
        .build();
  }

  private static PostDetailResponseDto createPostDetailResponse(Long postId, List<String> imageUrls
  , String title, String content, String email, String nickname) {
    return PostDetailResponseDto.builder()
        .postId(postId)
        .title(title)
        .content(content)
        .postImageList(imageUrls)
        .writerEmail(email)
        .writerNickname(nickname)
        .build();
  }

  private static PostCreateRequestDto createPostRequest(String title, String content) {
    return PostCreateRequestDto.builder()
        .title(title)
        .content(content)
        .postImageList(List.of("https://testImage.png"))
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