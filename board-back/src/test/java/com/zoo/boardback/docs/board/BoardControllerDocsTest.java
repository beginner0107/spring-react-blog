package com.zoo.boardback.docs.board;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.docs.RestDocsSecuritySupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.favorite.dto.object.FavoriteListItem;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class BoardControllerDocsTest extends RestDocsSecuritySupport {

  @DisplayName("게시글에 필요한 정보를 입력 후 등록을 하면 게시글이 저장된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createBoard() throws Exception {
    String title = "게시글제목";
    String content = "게시글내용";
    PostCreateRequestDto request = createPostRequest(title, content);


    mockMvc.perform(post("/api/v1/board")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            )
        .andExpect(status().isOk())
        .andDo(document("board-create",
            preprocessRequest(prettyPrint()),
            requestFields(
                fieldWithPath("title").type(JsonFieldType.STRING)
                    .description("게시글 제목"),
                fieldWithPath("content").type(JsonFieldType.STRING)
                    .description("게시글 내용"),
                fieldWithPath("boardTitleImage").type(JsonFieldType.STRING).optional()
                    .description("게시글 대표 이미지"),
                fieldWithPath("boardImageList").type(JsonFieldType.ARRAY)
                    .description("게시글 이미지 목록(URL 경로 리스트[String])")
            )
            ));
  }

  @DisplayName("게시글 번호를 넘기면 게시글 상세 내용을 볼 수 있다.")
  @Test
  void getPost() throws Exception {
    final Long boardNumber = 1L;
    String imageUrl = "https://testImage1.png";
    List<String> imageUrls = List.of(imageUrl);
    PostDetailResponseDto response = createPostDetailResponse(boardNumber, imageUrls
        , "테스트1", "테스트내용1", "test123@naver.com", "개구리왕눈이");

    given(boardService.find(any(Long.class))).willReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/board/{boardNumber}", boardNumber))
        .andExpect(status().isOk())
        .andDo(document("board-postDetail",
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("boardNumber").description("Board Id")
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
                fieldWithPath("data.boardNumber").type(JsonFieldType.NUMBER)
                    .description("Board Id"),
                fieldWithPath("data.title").type(JsonFieldType.STRING)
                    .description("글 제목"),
                fieldWithPath("data.content").type(JsonFieldType.STRING)
                    .description("글 내용"),
                fieldWithPath("data.boardImageList").type(JsonFieldType.ARRAY)
                    .description("이미지 경로 목록[String]"),
                fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
                    .description("글 작성일자"),
                fieldWithPath("data.updatedAt").type(JsonFieldType.STRING)
                    .description("글 수정일자"),
                fieldWithPath("data.writerEmail").type(JsonFieldType.STRING)
                    .description("작성자 이메일"),
                fieldWithPath("data.writerNickname").type(JsonFieldType.STRING)
                    .description("작성자 닉네임"),
                fieldWithPath("data.writerProfileImage").type(JsonFieldType.STRING)
                    .description("작성자 프로필 이미지 경로[String]")
            )
            ))
    ;
  }

  @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 목록을 반환한다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void getPosts() throws Exception {
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
    mockMvc.perform(get("/api/v1/board?page=0&size=5" + 
            "&title=제목&content=내용&nickname=닉네임&commentCont=댓글내용" + 
            "&titleAndContent=제목과내용"))
        .andExpect(status().isOk())
        .andDo(document("board-getPosts",
            preprocessResponse(prettyPrint()),
            queryParameters(
                parameterWithName("page").optional().description("페이지 번호"),
                parameterWithName("size").optional().description("페이지 크기"),
                parameterWithName("title").optional().description("글 제목"),
                parameterWithName("content").optional().description("글 내용"),
                parameterWithName("nickname").optional().description("작성자닉네임"),
                parameterWithName("commentCont").optional().description("댓글내용"),
                parameterWithName("titleAndContent").optional().description("제목과내용")
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
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .optional()
                    .description("게시글 목록"),
                fieldWithPath("data.content[].profileImage").type(JsonFieldType.STRING)
                    .optional().description("작성자 프로필 이미지"),
                fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING)
                    .description("작성자 닉네임"),
                fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING)
                    .description("게시글 작성일"),
                fieldWithPath("data.content[].title").type(JsonFieldType.STRING)
                    .description("게시글 제목"),
                fieldWithPath("data.content[].content").type(JsonFieldType.STRING)
                    .description("게시글 내용"),
                fieldWithPath("data.content[].viewCount").type(JsonFieldType.NUMBER)
                    .description("조회수"),
                fieldWithPath("data.content[].favoriteCount").type(JsonFieldType.NUMBER)
                    .description("좋아요 개수"),
                fieldWithPath("data.content[].commentCount").type(JsonFieldType.NUMBER)
                    .description("댓글 개수"),
                fieldWithPath("data.content[].boardTitleImage").type(JsonFieldType.STRING)
                    .optional().description("게시글 대표 이미지")
            )
        ));
  }

  @DisplayName("상세 게시글 페이지에서 좋아요(△, ▽) 버튼을 누를 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void putFavorite() throws Exception {
    final int boardNumber = 1;

    mockMvc.perform(put("/api/v1/board/{boardNumber}/favorite", boardNumber))
        .andExpect(status().isOk())
        .andDo(document("board-favorite-button-click",
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("boardNumber").description("Board Id")
            )
        ));
  }

  @DisplayName("하나의 게시물에 좋아요를 눌러준 사람들의 목록을 가지고온다.")
  @Test
  void getFavoriteList() throws Exception {
    final int boardNumber = 1;
    FavoriteListResponseDto response = FavoriteListResponseDto.builder().favoriteList(
        List.of(FavoriteListItem.builder().email("test123@naver.com")
            .nickname("개구리왕눈이")
            .profileImage("http://profileImage.png")
            .build())
    ).isEmpty(false).build();
    given(favoriteService.getFavoriteList(any(Long.class)))
        .willReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/board/{boardNumber}/favorite-list", boardNumber))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("board-favoriteList",
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("boardNumber").description("Board Id")
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
                fieldWithPath("data.favoriteList").type(JsonFieldType.ARRAY)
                    .description("좋아요 누른 회원의 목록"),
                fieldWithPath("data.favoriteList[].email").type(JsonFieldType.STRING)
                    .description("좋아요 누른 회원의 이메일"),
                fieldWithPath("data.favoriteList[].nickname").type(JsonFieldType.STRING)
                    .description("좋아요 누른 회원의 닉네임"),
                fieldWithPath("data.favoriteList[].profileImage").type(JsonFieldType.STRING)
                    .description("좋아요 누른 회원의 프로필 이미지"),
                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                    .description("빈 값 여부")
            )
        ));
  }

  @DisplayName("회원은 게시글을 수정할 수 있습니다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void editPost() throws Exception {
    final int boardNumber = 1;
    final String editTitle = "테스트 수정 글의 제목";
    final String editContent = "테스트 수정 글의 내용";
    PostUpdateRequestDto request = createPostUpdateRequest(editTitle, editContent);

    mockMvc.perform(put("/api/v1/board/{boardNumber}", boardNumber)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("board-editPost",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("boardNumber").description("Board Id")
            ),
            requestFields(
                fieldWithPath("title").type(JsonFieldType.STRING)
                    .description("게시글 제목"),
                fieldWithPath("content").type(JsonFieldType.STRING)
                    .description("게시글 내용"),
                fieldWithPath("boardImageList").type(JsonFieldType.ARRAY)
                    .description("게시글 이미지 목록(URL 경로 목록)")
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
                fieldWithPath("data").type(JsonFieldType.NULL)
                    .optional()
                    .description("빈 값")
            )
        ));
  }

  @DisplayName("회원은 게시글을 삭제할 수 있습니다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void deletePost() throws Exception {
    final int boardNumber = 1;

    mockMvc.perform(delete("/api/v1/board/{boardNumber}", boardNumber))
        .andExpect(status().isOk())
        .andDo(document("board-deletePost",
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("boardNumber").description("Board Id")
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
                fieldWithPath("data").type(JsonFieldType.NULL)
                    .optional()
                    .description("빈 값")
            )
        ));
  }

  private PostCreateRequestDto createPostRequest(String title, String content) {
    return PostCreateRequestDto.builder()
        .title(title)
        .content(content)
        .boardImageList(List.of("https://testImage.png"))
        .build();
  }

  private User createUser(String email, String password, String telNumber, String nickname) {
    return User.builder()
        .email(email)
        .password(password)
        .telNumber(telNumber)
        .nickname(nickname)
        .address("용인시 기흥구 보정로")
        .roles(initRole())
        .build();
  }

  private List<Authority> initRole() {
    return Collections.singletonList(Authority.builder().name("ROLE_USER").build());
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
        .writerProfileImage("http://writerprofileImage.png")
        .createdAt("2023-12-22")
        .updatedAt("2023-12-23")
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
