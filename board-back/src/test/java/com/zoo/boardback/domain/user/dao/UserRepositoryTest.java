package com.zoo.boardback.domain.user.dao;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.*;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserRepositoryTest extends IntegrationTestSupport {

  @Autowired
  UserRepository userRepository;

  @DisplayName("회원의 이메일이 존재하면 유저정보를 조회한다.")
  @Test
  void findByEmail() {
    // given
    String email = "test12@naver.com";
    User user = createUser(email, "testpassword123"
        , "01022222222", "개구리왕눈이");
    userRepository.save(user);

    // when
    User user1 = userRepository.findByEmail(email).orElseThrow();

    // then
    assertThat(user1).isNotNull();
    assertThat(user1.getEmail()).isEqualTo(email);
    assertThat(user1.getNickname()).isEqualTo("개구리왕눈이");
    assertThat(user1.getTelNumber()).isEqualTo("01022222222");
  }

  @DisplayName("회원의 이메일이 존재하는지 확인한다.")
  @Test
  void existsByEmail() {
    // given
    String email = "test12@naver.com";
    User user = createUser(email, "testpassword123"
        , "01022222222", "개구리왕눈이");
    userRepository.save(user);

    // when
    boolean existsByEmail = userRepository.existsByEmail(email);

    // then
    assertThat(existsByEmail).isTrue();
  }

  @DisplayName("회원의 닉네임이 존재하는지 확인한다.")
  @Test
  void existsByNickname() {
    // given
    String nickname = "test12@naver.com";
    User user = createUser("email", "testpassword123"
        , "01022222222", nickname);
    userRepository.save(user);

    // when
    boolean existsByNickname = userRepository.existsByNickname(nickname);

    // then
    assertThat(existsByNickname).isTrue();
  }

  @DisplayName("회원의 전화번호가 존재하는지 확인한다.")
  @Test
  void existsByTelNumber() {
    // given
    String telNumber = "01022222222";
    User user = createUser("email", "testpassword123"
        , telNumber, "개구리왕눈이");
    userRepository.save(user);

    // when
    boolean existsByTelNumber = userRepository.existsByTelNumber(telNumber);

    // then
    assertThat(existsByTelNumber).isTrue();
  }
  
  private User createUser(String email, String password, String telNumber, String nickname) {
    return User.builder()
        .id(1L)
        .email(email)
        .password(password)
        .telNumber(telNumber)
        .nickname(nickname)
        .address("용인시 기흥구 보정로")
        .roles(initRole())
        .build();
  }

  private List<Authority> initRole() {
    return Collections.singletonList(Authority.builder().role(GENERAL_USER).build());
  }

}