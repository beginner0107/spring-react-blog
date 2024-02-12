package com.zoo.boardback.domain.user.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.domain.user.dto.request.NicknameUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.response.GetSignUserResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class UserControllerTest extends ControllerTestSupport {

  @DisplayName("회원은 자신의 정보를 조회할 수 있다.")
  @WithAuthUser(userId = "1", role = "ROLE_USER")
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
    given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
        .id(1L)
        .email(email)
        .nickname(nickname)
        .profileImage(null)
        .build()));

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
        .andExpect(jsonPath("$.data.profileImage").isEmpty());
  }

  @DisplayName("회원은 자신의 닉네임을 변경할 수 있다.")
  @WithAuthUser(userId = "1", role = "ROLE_USER")
  @Test
  void updateNickname() throws Exception {
    // given
    String email = "test123@naver.com";
    String nickname = "개구리왕눈이123";
    NicknameUpdateRequestDto request = new NicknameUpdateRequestDto("마동석");
    given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
        .id(1L)
        .email(email)
        .nickname(nickname)
        .profileImage(null)
        .build()));

    // when & then
    mockMvc.perform(patch("/api/v1/user/nickname")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.field").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("닉네임은 20자 초과로 입력하면 예외가 발생한다.")
  @WithAuthUser(userId = "1", role = "ROLE_USER")
  @Test
  void updateNicknameShouldThrowExceptionWhenExceedsMaxLength() throws Exception {
    // given
    String email = "test123@naver.com";
    String nickname = "개구리왕눈이123";
    NicknameUpdateRequestDto request = new NicknameUpdateRequestDto(
        "마동석sdfjsdkfjlksdjflksdjlkfjslkfjsdk"
    );
    given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
        .id(1L)
        .email(email)
        .nickname(nickname)
        .profileImage(null)
        .build()));

    // when & then
    mockMvc.perform(patch("/api/v1/user/nickname")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("닉네임은 20자 이하입니다."))
        .andExpect(jsonPath("$.field").value("nickname"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("변경할 이미지의 경로를 입력하면 프로필 이미지를 변경할 수 있다.")
  @WithAuthUser(userId = "1", role = "ROLE_USER")
  @Test
  void updateProfileImage() throws Exception {
    // given
    String email = "test123@naver.com";
    String nickname = "개구리왕눈이123";
    UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto(
        "http://localhost:2344/cProfileImage.png"
    );
    given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
        .id(1L)
        .email(email)
        .nickname(nickname)
        .profileImage(null)
        .build()));

    // when & then
    mockMvc.perform(patch("/api/v1/user/profileImage")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.field").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }
}