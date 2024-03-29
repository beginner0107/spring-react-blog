package com.zoo.boardback.domain.auth.application;

import static com.zoo.boardback.domain.user.entity.role.UserRole.GENERAL_USER;
import static com.zoo.boardback.global.error.ErrorCode.USER_EMAIL_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_LOGIN_ID_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_LOGIN_TEL_NUMBER_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_WRONG_PASSWORD;

import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthCookieService authCookieService;

    @Transactional
    public void signUp(SignUpRequestDto request) {

        checkIsDuplicationEmail(request.getEmail());
        checkIsDuplicationNickname(request.getNickname());
        checkIsDuplicationTelNumber(request.getTelNumber());

        User user = request.toEntity(passwordEncoder);
        user.addRoles(Collections.singletonList(Authority.builder().role(GENERAL_USER).build()));

        userRepository.save(user);
    }

    @Transactional
    public SignInResponseDto signIn(SignInRequestDto request
        , HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = findUserByEmail(request.getEmail());

        checkPasswordMatch(request.getPassword(), user.getPassword());

        authCookieService.setNewCookieInResponse(String.valueOf(user.getId()), user.getRoles(),
            httpRequest.getHeader(HttpHeaders.USER_AGENT), httpResponse);

        List<String> userRoles = mapAuthoritiesToRoleNames(user.getRoles());
        return SignInResponseDto.of(user, userRoles);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new BusinessException(email, "email", USER_NOT_FOUND));
    }

    private void checkIsDuplicationTelNumber(String telNumber) {
        if (userRepository.existsByTelNumber(telNumber)) {
            throw new BusinessException(telNumber, "telNumber", USER_LOGIN_TEL_NUMBER_DUPLICATE);
        }
    }

    private void checkIsDuplicationNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new BusinessException(nickname, "nickname", USER_LOGIN_ID_DUPLICATE);
        }
    }

    private void checkIsDuplicationEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(email, "email", USER_EMAIL_DUPLICATE);
        }
    }

    private void checkPasswordMatch(String enteredPassword, String storedPassword) {
        if (!passwordEncoder.matches(enteredPassword, storedPassword)) {
            throw new BusinessException(null, "password", USER_WRONG_PASSWORD);
        }
    }

    private List<String> mapAuthoritiesToRoleNames(List<Authority> authorities) {
        return authorities.stream()
            .map(Authority::getRoleName)
            .collect(Collectors.toList());
    }
}
