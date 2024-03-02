package com.zoo.boardback.domain.user.application;

import static com.zoo.boardback.global.error.ErrorCode.USER_LOGIN_ID_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;

import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.dto.response.SignUserResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public SignUserResponseDto getUser(String email) {
        User user = findUserByEmail(email);
        return SignUserResponseDto.from(user);
    }

    @Transactional
    public void updateNickname(String email, String nickname) {
        User user = findUserByEmail(email);
        checkIsDuplicationNickname(nickname);
        user.changeNickname(nickname);
    }

    private void checkIsDuplicationNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new BusinessException(nickname, "nickname", USER_LOGIN_ID_DUPLICATE);
        }
    }

    @Transactional
    public void updateProfileImage(String email, String profileImage) {
        User user = findUserByEmail(email);
        user.changeProfileImage(profileImage);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(email, "email", USER_NOT_FOUND));
    }
}
