package com.zoo.boardback.domain.auth.api;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthControllerTest extends ControllerTestSupport {

  @DisplayName("필요한 정보들을 입력하면 회원가입을 할 수 있다.")
  @Test
  //@WithMockUser
  void signUp() throws Exception {
    // given
    SignUpRequestDto request = createSignUpRequest("test22@naver.com", "testpassword33"
    ,"testNickname34", "01022222222");

    // when & then
    mockMvc.perform(
        post("/api/v1/auth/sign-up")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
    )
        .andDo(print())
        .andExpect(status().isOk());
  }

  private SignUpRequestDto createSignUpRequest(String userEmail, String password, String nickname, String telNumber) {
    return SignUpRequestDto.builder()
        .email(userEmail)
        .password(password)
        .nickname(nickname)
        .telNumber(telNumber)
        .address("경기도 용인시 기흥구")
        .build();
  }
}