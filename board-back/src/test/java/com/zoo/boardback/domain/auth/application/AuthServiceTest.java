package com.zoo.boardback.domain.auth.application;

import static com.zoo.boardback.global.error.ErrorCode.USER_EMAIL_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_LOGIN_ID_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_LOGIN_TEL_NUMBER_DUPLICATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.dao.AuthRepository;
import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest extends IntegrationTestSupport {

  @Autowired
  private AuthService authService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private AuthRepository authRepository;

  @AfterEach
  void tearDown() {
    authRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }


  @DisplayName("회원가입 화면에서 필요한 정보를 입력하면 회원가입이 성공한다.")
  @Test
  void signUp() {
    // given
    SignUpRequestDto signUpRequestDto = createSignUpRequest("test18@naver.com", "password123"
    , "nickname12", "01022222222");

    // when
    authService.signUp(signUpRequestDto);

    // then
    List<User> users = userRepository.findAll();

    assertThat(users).hasSize(1)
        .extracting("email", "nickname", "telNumber", "address")
        .containsExactlyInAnyOrder(
            tuple(signUpRequestDto.getEmail(),
                signUpRequestDto.getNickname(),
                signUpRequestDto.getTelNumber(),
                signUpRequestDto.getAddress())
        );
  }

  @DisplayName("회원가입을 수행할 때 이메일이 중복되면 예외가 발생한다.")
  @Test
  void signUpDuplicatedEmail() {
    // given
    User user = createUser("test18@naver.com", "nickname13", "01022222222");
    userRepository.save(user);
    SignUpRequestDto signUpRequestDto = createSignUpRequest("test18@naver.com", "password123"
        , "nickname14", "01022222222");

    // when & then
    assertThatThrownBy(() -> authService.signUp(signUpRequestDto))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(USER_EMAIL_DUPLICATE.getMessage());
  }

  @DisplayName("회원가입을 수행할 때 닉네임이 중복되면 예외가 발생한다.")
  @Test
  void signUpDuplicatedNickname() {
    // given
    User user = createUser("test1@naver.com", "nickname13", "01022222222");
    userRepository.save(user);
    SignUpRequestDto signUpRequestDto = createSignUpRequest("test2@naver.com", "password123"
        , "nickname13", "01022222222");

    // when & then
    assertThatThrownBy(() -> authService.signUp(signUpRequestDto))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(USER_LOGIN_ID_DUPLICATE.getMessage());
  }

  @DisplayName("회원가입을 수행할 때 전화번호가 중복되면 예외가 발생한다.")
  @Test
  void signUpDuplicatedTelNumber() {
    // given
    User user = createUser("test1@naver.com", "nickname13", "01022222222");
    userRepository.save(user);
    SignUpRequestDto signUpRequestDto = createSignUpRequest("test2@naver.com", "password123"
        , "nickname14", "01022222222");

    // when & then
    assertThatThrownBy(() -> authService.signUp(signUpRequestDto))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(USER_LOGIN_TEL_NUMBER_DUPLICATE.getMessage());
  }

  @DisplayName("이메일과 비밀번호를 입력하면 로그인에 성공한다.")
  @Test
  void signIn() {
    // given
    User user = createUser("test1@naver.com", "nickname13", "01022222222");
    user.addRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));
    userRepository.save(user);
    SignInRequestDto signInRequestDto = SignInRequestDto.builder()
        .email("test1@naver.com")
        .password("testpassword123").build();

    // when
    SignInResponseDto signInResponse = authService.signIn(signInRequestDto);

    // then
    assertThat(signInResponse)
        .hasFieldOrProperty("token")
        .hasFieldOrProperty("expirationTime");
  }

  private SignUpRequestDto createSignUpRequest(String email, String password
  , String nickname, String telNumber) {
    return SignUpRequestDto.builder()
        .email(email)
        .password(password)
        .nickname(nickname)
        .telNumber(telNumber)
        .address("경기도 용인시 기흥구")
        .build();
  }

  private User createUser(String userEmail, String nickname, String telNumber) {
    return User.builder()
        .email(userEmail)
        .password(passwordEncoder.encode("testpassword123"))
        .nickname(nickname)
        .telNumber(telNumber)
        .address("경기도 용인시 기흥구")
        .build();
  }
}