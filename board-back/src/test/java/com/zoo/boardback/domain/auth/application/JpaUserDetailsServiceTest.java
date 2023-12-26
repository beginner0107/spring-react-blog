package com.zoo.boardback.domain.auth.application;

import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

class JpaUserDetailsServiceTest extends IntegrationTestSupport {

  @Autowired
  private JpaUserDetailsService jpaUserDetailsService;
  @Autowired
  private UserRepository userRepository;

  @AfterEach
  void tearDown() {
    userRepository.deleteAllInBatch();
  }

  @DisplayName("회원의 이메일이 존재하는지 확인한다.")
  @Test
  void loadUserByUsername() {
    // given
    final String userEmail = "test99@naver.com";
    User user = createUser(userEmail);
    userRepository.save(user);

    // when
    UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(userEmail);

    // then
    assertThat(userDetails.getUsername()).isEqualTo(userEmail);
  }

  @DisplayName("회원의 이메일이 존재하지 않는다면 로그인에 실패한다.")
  @Test
  void loadUserByUsernameNotExist() {
    // given
    final String userEmail = "test99@naver.com";

    // when & then
    assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername(userEmail))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(USER_NOT_FOUND.getMessage());
  }

  private static User createUser(String userEmail) {
    return User.builder()
        .email(userEmail)
        .password("testpassword123")
        .nickname("test12")
        .telNumber("010-1111-1111")
        .address("경기도 용인시 기흥구")
        .build();
  }
}