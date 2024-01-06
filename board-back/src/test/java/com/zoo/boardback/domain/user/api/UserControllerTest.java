package com.zoo.boardback.domain.user.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.domain.user.dto.response.GetSignUserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest extends ControllerTestSupport {

  @DisplayName("회원은 자신의 정보를 조회할 수 있다.")
  @WithAuthUser(email = "test123@naver.com", role = "ROLE_USER")
  @Test
  void getSignInUser() throws Exception {
    // given
    String email = "test123@naver.com";
    String nickname = "개구리왕눈이123";
    given(userService.getSignUser(email))
        .willReturn(GetSignUserResponseDto.builder()
            .email(email)
            .nickname(nickname)
            .build()
    );
    
    // when & then
    mockMvc.perform(get("/api/v1/user"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.field").isEmpty())
        .andExpect(jsonPath("$.data").hasJsonPath())
        .andExpect(jsonPath("$.data.email").value(email))
        .andExpect(jsonPath("$.data.nickname").value(nickname))
        .andExpect(jsonPath("$.data.profileImage").isEmpty())
    ;
  }
}