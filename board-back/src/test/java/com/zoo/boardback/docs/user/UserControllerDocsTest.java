package com.zoo.boardback.docs.user;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.docs.RestDocsSecuritySupport;
import com.zoo.boardback.domain.user.dto.response.GetSignUserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

public class UserControllerDocsTest extends RestDocsSecuritySupport {

  @DisplayName("회원은 자신의 정보를 조회할 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void getSignInUser() throws Exception {
    String email = "test123@naver.com";
    String nickname = "개구리왕눈이123";
    given(userService.getSignUser(email))
        .willReturn(GetSignUserResponseDto.builder()
            .email(email)
            .nickname(nickname)
            .profileImage("http://image.png")
            .build()
    );


    mockMvc.perform(get("/api/v1/user")
        )
        .andExpect(status().isOk())
        .andDo(document("user-info",
            preprocessResponse(prettyPrint()),
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
                fieldWithPath("data.email").type(JsonFieldType.STRING)
                    .description("회원 이메일"),
                fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                    .description("회원 닉네임"),
                fieldWithPath("data.profileImage").type(JsonFieldType.STRING)
                    .optional()
                    .description("회원 프로필 이미지")
            )
        ));
  }
}
