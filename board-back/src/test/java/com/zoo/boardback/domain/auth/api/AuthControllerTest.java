package com.zoo.boardback.domain.auth.api;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

class AuthControllerTest extends ControllerTestSupport {

  final static String EMAIL = "test123@naver.com";
  final static String NICKNAME = "개구리왕눈이123";

  @DisplayName("필요한 정보들을 입력하면 회원가입을 할 수 있다.")
  @Test
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

  @DisplayName("이메일 형식이 틀리면 회원가입을 할 수 없다.")
  @Test
  void signUpFailEmailFormatFalse() throws Exception {
    // given
    String email = "test22@.com";
    SignUpRequestDto request = createSignUpRequest(email, "testpassword33"
        ,"testNickname34", "01022222222");

    // when & then
    mockMvc.perform(
            post("/api/v1/auth/sign-up")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("이메일 형식을 맞춰주세요"))
        .andExpect(jsonPath("$.field").value("email"));
  }

  private static Stream<Arguments> providerPassword() {
    return Stream.of(
        Arguments.of("012345E"),
        Arguments.of("01234567"),
        Arguments.of("012345F78901234567890"),
        Arguments.of("ABCDEFGH"),
        Arguments.of("ABCDEFGHIJKLNMOPQRSTUWXY1")
    );
  }

  @DisplayName("비밀번호의 형식이 틀리면 회원가입을 할 수 없다.")
  @MethodSource("providerPassword")
  @ParameterizedTest
  void signUpFailPasswordFormatFalse(String password) throws Exception {
    // given
    SignUpRequestDto request = createSignUpRequest("test22@naver.com", password
        ,"testNickname34", "01022222222");

    // when & then
    mockMvc.perform(
            post("/api/v1/auth/sign-up")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."))
        .andExpect(jsonPath("$.field").value("password"));
  }

  @DisplayName("닉네임의 형식이 틀리면 회원가입을 할 수 없다.")
  @Test
  void signUpFailNicknameFormatFalse() throws Exception {
    // given
    String nickname = "testNickname34111111111111111";
    SignUpRequestDto request = createSignUpRequest("test22@naver.com", "testpassword33"
        , nickname, "01022222222");

    // when & then
    mockMvc.perform(
            post("/api/v1/auth/sign-up")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value( "닉네임은 20자 이하입니다."))
        .andExpect(jsonPath("$.field").value("nickname"));
  }

  private static Stream<Arguments> providerTelNumber() {
    return Stream.of(
        Arguments.of("01234567890123"), // 14자리
        Arguments.of("0123456789"), // 10자리
        Arguments.of("ABCDEFGDFJDKF"),
        Arguments.of("101-2323-2332"),
        Arguments.of("123012312m32mm3")
    );
  }

  @DisplayName("전화번호의 형식이 틀리면 회원가입을 할 수 없다.")
  @MethodSource("providerTelNumber")
  @ParameterizedTest
  void signUpFailTelNumberFormatFalse(String telNumber) throws Exception {
    // given
    SignUpRequestDto request = createSignUpRequest("test22@naver.com", "testpassword33"
        , "testNickname34", telNumber);

    // when & then
    mockMvc.perform(
            post("/api/v1/auth/sign-up")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value( "전화번호는 11자에서 13자 사이의 숫자만 가능합니다."))
        .andExpect(jsonPath("$.field").value("telNumber"));
  }

  @DisplayName("이메일과 비밀번호를 입력시 로그인에 성공한다.")
  @Test
  void signIn() throws Exception{
    // given
    SignInRequestDto signInRequest = createSignInRequest("test123@naver.com", "test12324dpass");
    given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
        .id(1L)
        .email(EMAIL)
        .nickname(NICKNAME)
        .profileImage(null)
        .build()));

    // when & then
    mockMvc.perform(
        post("/api/v1/auth/sign-in")
            .content(objectMapper.writeValueAsString(signInRequest))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("200"))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"));
  }

  @DisplayName("이메일 형식이 틀리면 회원가입을 할 수 없다.")
  @Test
  void signInFailEmailFormatFalse() throws Exception{
    // given
    String email = "test123";
    SignInRequestDto signInRequest = createSignInRequest(email, "testpassworrd123");

    // when & then
    mockMvc.perform(
            post("/api/v1/auth/sign-in")
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value( "이메일 형식을 맞춰주세요"))
        .andExpect(jsonPath("$.field").value("email"));
  }

  @DisplayName("비밀번호 형식이 틀리면 회원가입을 할 수 없다.")
  @MethodSource("providerPassword")
  @ParameterizedTest
  void signInFailPasswordFormatFalse(String password) throws Exception{
    // given
    SignInRequestDto signInRequest = createSignInRequest("test@naver.com", password);

    // when & then
    mockMvc.perform(
            post("/api/v1/auth/sign-in")
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value( "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."))
        .andExpect(jsonPath("$.field").value("password"));
  }

  private SignInRequestDto createSignInRequest(String email, String password) {
    return SignInRequestDto.builder()
        .email(email)
        .password(password)
        .build();
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