package com.zoo.boardback.domain.comment.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.dto.response.CommentResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

class CommentControllerTest extends ControllerTestSupport {

  @DisplayName("댓글에 필요한 정보를 입력 후 등록을 하면 댓글이 저장된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createCommentEmptyBoardNumber() throws Exception {
    // given
    String content = "댓글내용입니다.";
    Long boardNumber = 1L;
    CommentCreateRequestDto request = createComment(boardNumber, content);

    // when & then
    mockMvc.perform(post("/api/v1/comments")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(status().isOk());
    then(commentService).should(times(1)).create(any(), any());
  }

  @DisplayName("댓글 내용을 입력하지 않으면 댓글을 작성할 수 없습니다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createComment() throws Exception {
    // given
    String content = "";
    Long boardNumber = 1L;
    CommentCreateRequestDto request = createComment(boardNumber, content);

    // when & then
    mockMvc.perform(post("/api/v1/comments")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("댓글 내용을 입력해주세요"))
        .andExpect(jsonPath("$.field").value("content"))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(status().isBadRequest());
  }

  @DisplayName("게시글의 댓글의 목록은 누구나 조회할 수 있다.")
  @Test
  void getComments() throws Exception {
    // given
    Long boardNumber = 1L;
    CommentListResponseDto comments = CommentListResponseDto.builder()
            .commentListResponse(
                List.of(
                  CommentResponse.builder()
                      .commentNumber(2L)
                      .content("댓글 작성2")
                      .nickname("닉네임2")
                      .profileImage("http://localhost:8080/image2.png")
                      .createdAt("2024-01-20 22:52:59")
                      .updatedAt("2024-01-20 22:52:59")
                      .build(),
                  CommentResponse.builder()
                      .commentNumber(1L)
                      .content("댓글 작성1")
                      .nickname("닉네임1")
                      .profileImage("http://localhost:8080/image1.png")
                      .createdAt("2024-01-20 23:52:59")
                      .updatedAt("2024-01-20 23:52:59")
                      .build()
                )
          )
        .totalElements(2L)
        .build();
    given(commentService.getComments(anyLong(), any(Pageable.class))).willReturn(comments);

    // when & then
    mockMvc.perform(get("/api/v1/comments/post/{postId}", boardNumber))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data.totalElements").value(2))
        .andExpect(jsonPath("$.data.commentListResponse").hasJsonPath())
        .andExpect(jsonPath("$.data.commentListResponse[0].commentNumber").value(2))
        .andExpect(jsonPath("$.data.commentListResponse[0].content").value("댓글 작성2"))
        .andExpect(jsonPath("$.data.commentListResponse[0].nickname").value("닉네임2"))
        .andExpect(jsonPath("$.data.commentListResponse[0].profileImage").value("http://localhost:8080/image2.png"))
        .andExpect(jsonPath("$.data.commentListResponse[1].commentNumber").value(1))
        .andExpect(jsonPath("$.data.commentListResponse[1].content").value("댓글 작성1"))
        .andExpect(jsonPath("$.data.commentListResponse[1].nickname").value("닉네임1"))
        .andExpect(jsonPath("$.data.commentListResponse[1].profileImage").value("http://localhost:8080/image1.png"));
  }

  @DisplayName("댓글의 내용을 변경 하면 댓글이 수정된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void editComment() throws Exception {
    // given
    String content = "댓글수정입니다.";
    Long commentId = 1L;
    CommentUpdateRequestDto requestDto = CommentUpdateRequestDto.builder()
        .postId(1L)
        .content(content).build();

    // when & then
    mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.field").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
    then(commentService).should(times(1)).editComment(anyString(), anyLong(), any());
  }

  @DisplayName("댓글의 내용을 입력하지 않으면 댓글이 수정되지 않는다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void editCommentEmptyContent() throws Exception {
    // given
    String content = "";
    int commentId = 1;
    CommentUpdateRequestDto requestDto = CommentUpdateRequestDto.builder()
        .postId(1L)
        .content(content).build();

    // when & then
    mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("댓글 내용을 입력해주세요"))
        .andExpect(jsonPath("$.field").value("content"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("회원은 댓글을 삭제할 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void deleteComment() throws Exception {
    // given
    int commentId = 1;

    // when & then
    mockMvc.perform(delete("/api/v1/comments/{commentId}"
            , commentId)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(204))
        .andExpect(jsonPath("$.status").value("NO_CONTENT"))
        .andExpect(jsonPath("$.message").value("NO_CONTENT"));
    then(commentService).should(times(1)).deleteComment(anyLong(), anyString());
  }

  private CommentCreateRequestDto createComment(Long postId, String content) {
    return CommentCreateRequestDto.builder()
        .postId(postId)
        .content(content)
        .build();
  }
}