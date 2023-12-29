package com.zoo.boardback.docs.auth;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.docs.RestDocsSupport;
import com.zoo.boardback.domain.auth.api.AuthController;
import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class AuthControllerDocsTest extends RestDocsSupport {

  private final AuthService authService = mock(AuthService.class);

  @Override
  protected Object initController() {
    return new AuthController(authService);
  }

  @DisplayName("필요한 정보들을 입력하면 회원가입을 할 수 있다.")
  @Test
  void signUp() throws Exception {
    SignUpRequestDto request = createSignUpRequest("test22@naver.com", "testpassword33"
        ,"testNickname34", "01022222222");

    mockMvc.perform(
            post("/api/v1/auth/sign-up")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andExpect(status().isOk())
        .andDo(document("auth-sign-up",
            preprocessRequest(prettyPrint()),
            requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING)
                    .description("비밀번호"),
                fieldWithPath("nickname").type(JsonFieldType.STRING)
                    .description("닉네임"),
                fieldWithPath("telNumber").type(JsonFieldType.STRING)
                    .description("전화번호"),
                fieldWithPath("address").type(JsonFieldType.STRING)
                    .description("주소"),
                fieldWithPath("addressDetail").type(JsonFieldType.STRING)
                    .optional()
                    .description("상세주소")
            )
        ));
  }

  private SignUpRequestDto createSignUpRequest(String userEmail, String password, String nickname, String telNumber) {
    return SignUpRequestDto.builder()
        .email(userEmail)
        .password(password)
        .nickname(nickname)
        .telNumber(telNumber)
        .address("경기도 용인시 기흥구")
        .addressDetail(null)
        .build();
  }
}
