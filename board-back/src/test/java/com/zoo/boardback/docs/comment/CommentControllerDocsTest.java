package com.zoo.boardback.docs.comment;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.docs.RestDocsSecuritySupport;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class CommentControllerDocsTest extends RestDocsSecuritySupport {

  @DisplayName("게시글에 필요한 정보를 입력 후 등록을 하면 게시글이 저장된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void createComment() throws Exception {
    int boardNumber = 1;
    String content = "댓글내용입니당.";
    CommentCreateRequestDto request = createComment(boardNumber, content);


    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/comments")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("comments-create",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("boardNumber").type(JsonFieldType.NUMBER)
                    .description("게시글 내용"),
                fieldWithPath("content").type(JsonFieldType.STRING)
                    .description("게시글 내용")
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

  @DisplayName("게시글의 댓글의 목록은 누구나 조회할 수 있다.")
  @Test
  void getComments() throws Exception {
    int boardNumber = 1;
    CommentListResponseDto responseDto = CommentListResponseDto.from(
        List.of(
            CommentQueryDto.builder()
                .commentNumber(2)
                .content("댓글 작성2")
                .nickname("닉네임2")
                .profileImage("http://localhost:8080/image2.png")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        )
    );
    given(commentService.getComments(boardNumber)).willReturn(responseDto);

    mockMvc.perform(get("/api/v1/comments/board/{boardNumber}", boardNumber))
        .andExpect(status().isOk())
        .andDo(document("comments-getComments",
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
                fieldWithPath("data.commentListResponse").type(JsonFieldType.ARRAY)
                    .description("댓글 목록"),
                fieldWithPath("data.commentListResponse[0].commentNumber").type(JsonFieldType.NUMBER)
                    .description("댓글 번호"),
                fieldWithPath("data.commentListResponse[0].content").type(JsonFieldType.STRING)
                    .description("댓글 내용"),
                fieldWithPath("data.commentListResponse[0].nickname").type(JsonFieldType.STRING)
                    .description("댓글 작성자 닉네임"),
                fieldWithPath("data.commentListResponse[0].profileImage").type(JsonFieldType.STRING)
                    .description("댓글 작성자 프로필 이미지 URL"),
                fieldWithPath("data.commentListResponse[0].createdAt").type(JsonFieldType.STRING)
                    .description("댓글 생성일자"),
                fieldWithPath("data.commentListResponse[0].updatedAt").type(JsonFieldType.STRING)
                    .description("댓글 수정일자")
            )
        ));
  }

  @DisplayName("댓글의 내용을 변경 하면 댓글이 수정된다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void editComment() throws Exception {
    String content = "댓글수정입니다.";
    int commentNumber = 1;
    CommentUpdateRequestDto requestDto = CommentUpdateRequestDto.builder()
        .boardNumber(1)
        .content(content).build();

    mockMvc.perform(put("/api/v1/comments/{commentNumber}", commentNumber)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("comments-editComment",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("commentNumber").description("Comment Id")
            ),
            requestFields(
                fieldWithPath("boardNumber").type(JsonFieldType.NUMBER)
                    .description("게시글 내용"),
                fieldWithPath("content").type(JsonFieldType.STRING)
                    .description("게시글 내용")
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

  @DisplayName("회원은 댓글을 삭제할 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void deleteComment() throws Exception {
    // given
    int commentNumber = 1;
    int boardNumber = 1;

    // when & then
    mockMvc.perform(delete("/api/v1/comments/{commentNumber}"
            , commentNumber, boardNumber)
        )
        .andExpect(status().isOk())
        .andDo(document("comments-deleteComment",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("commentNumber").description("Comment Id")
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

  private CommentCreateRequestDto createComment(Integer boardNumber, String content) {
    return CommentCreateRequestDto.builder()
        .boardNumber(boardNumber)
        .content(content)
        .build();
  }
}