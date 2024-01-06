package com.zoo.boardback.docs.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.docs.RestDocsSupport;
import com.zoo.boardback.domain.auth.api.AuthController;
import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
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

  @DisplayName("회원의 이메일과 비밀번호를 입력하면 로그인을 할 수 있다.")
  @Test
  void signIn() throws Exception {
    SignInRequestDto signInRequest = createSignInRequest("test123@naver.com", "test12324dpass");
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikpv";
    given(authService.signIn(any(SignInRequestDto.class))).willReturn(
        SignInResponseDto.builder()
            .token(token)
            .expirationTime(3600)
            .build());

    mockMvc.perform(
            post("/api/v1/auth/sign-in")
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andExpect(status().isOk())
        .andDo(document("auth-sign-in",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING)
                    .description("비밀번호")
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
                fieldWithPath("data.token").type(JsonFieldType.STRING)
                    .description("엑세스 토큰"),
                fieldWithPath("data.expirationTime").type(JsonFieldType.NUMBER)
                    .description("토큰 만료시간")
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

  private SignInRequestDto createSignInRequest(String email, String password) {
    return SignInRequestDto.builder()
        .email(email)
        .password(password)
        .build();
  }
}
