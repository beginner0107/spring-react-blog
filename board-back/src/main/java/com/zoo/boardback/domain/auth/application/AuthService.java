package com.zoo.boardback.domain.auth.application;

import static com.zoo.boardback.global.error.ErrorCode.USER_EMAIL_DUPLICATE;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_WRONG_PASSWORD;

import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import com.zoo.boardback.global.error.BusinessException;
import jakarta.transaction.Transactional;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Transactional
  public void signUp(SignUpRequestDto request) {
    
    checkIsDuplicationEmail(request.getEmail());
    checkIsDuplicationNickname(request.getNickname());
    checkIsDuplicationTelNumber(request.getTelNumber());

    User user = request.toEntity(passwordEncoder);
    user.addRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

    userRepository.save(user);
  }

  @Transactional
  public SignInResponseDto signIn(SignInRequestDto request) {
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
        new BusinessException(request.getEmail(), "email", USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException(null, "password", USER_WRONG_PASSWORD);
    }

    return SignInResponseDto.of(jwtProvider.createToken(user.getEmail(), user.getRoles()));
  }

  private void checkIsDuplicationTelNumber(String telNumber) {
    if (userRepository.existsByTelNumber(telNumber)) {
      throw new BusinessException(telNumber, "telNumber", USER_EMAIL_DUPLICATE);
    }
  }

  private void checkIsDuplicationNickname(String nickname) {
    if (userRepository.existsByNickname(nickname)) {
      throw new BusinessException(nickname, "nickname", USER_EMAIL_DUPLICATE);
    }
  }

  private void checkIsDuplicationEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessException(email, "email", USER_EMAIL_DUPLICATE);
    }
  }
}
