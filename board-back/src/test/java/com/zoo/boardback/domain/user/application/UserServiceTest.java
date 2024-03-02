package com.zoo.boardback.domain.user.application;

import static com.zoo.boardback.domain.user.entity.role.UserRole.GENERAL_USER;
import static com.zoo.boardback.global.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.dao.AuthRepository;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.dto.response.SignUserResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthRepository authRepository;

    @AfterEach
    void tearDown() {
        authRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("이메일로 회원의 정보를 조회한다.")
    @Test
    void getSignUser() {
        // given
        String email = "test12@naver.com";
        User user = createUser(email, "testpassword123"
            , "01022222222", "개구리왕눈이");
        userRepository.save(user);

        // when
        SignUserResponseDto userResponseDto = userService.getUser(email);

        // then
        assertThat(userResponseDto.getEmail()).isEqualTo(email);
        assertThat(userResponseDto.getNickname()).isEqualTo("개구리왕눈이");
        assertThat(userResponseDto.getProfileImage()).isNull();
    }

    @DisplayName("존재하지 않는 이메일로 회원의 정보를 조회하면 예외 메시지를 반환한다.")
    @Test
    void getSignUserNotExistEmail() {
        // given

        // when & then
        assertThatThrownBy(() -> userService.getUser("test12@naver.com"))
            .isInstanceOf(BusinessException.class)
            .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @DisplayName("회원의 닉네임을 변경한다.")
    @Test
    void updateNickname() {
        // given
        String email = "test12@naver.com";
        User user = createUser(email, "testpassword123"
            , "01022222222", "개구리왕눈이");
        userRepository.save(user);
        String changeNickname = "김봉석";

        // when
        userService.updateNickname(email, changeNickname);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.get(0).getNickname()).isEqualTo(changeNickname);
    }

    @DisplayName("회원의 닉네임은 중복될 수 없다.")
    @Test
    void updateNicknameDuplicated() {
        // given
        String email = "test12@naver.com";
        String email2 = "test14@naver.com";
        User user1 = createUser(email, "testpassword123"
            , "01022222222", "개구리왕눈이");
        User user2 = createUser(email2, "testpassword123"
            , "01022222322", "개구리왕눈이1");
        userRepository.saveAll(List.of(user1, user2));
        String changeNickname = "개구리왕눈이1";

        // when
        assertThatThrownBy(() ->
            userService.updateNickname(email, changeNickname))
            .isInstanceOf(BusinessException.class)
            .hasMessage(USER_LOGIN_ID_DUPLICATE.getMessage());
    }

    @DisplayName("회원의 닉네임은 중복될 수 없다.")
    @Test
    void updateProfileImage() {
        String email = "test12@naver.com";
        User user = createUser(email, "testpassword123"
            , "01022222222", "개구리왕눈이");
        userRepository.save(user);
        String changeProfileImage = "http://localhost:1233/cImage.png";

        // when
        userService.updateProfileImage(email, changeProfileImage);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.get(0).getProfileImage()).isEqualTo(changeProfileImage);
    }

    private User createUser(String email, String password, String telNumber, String nickname) {
        return User.builder()
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